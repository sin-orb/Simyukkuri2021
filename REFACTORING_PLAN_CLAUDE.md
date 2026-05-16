# Claude によるリファクタリング優先順位プラン

優先度の基準: バグを産みやすいか > 変更コストが高いか > テスタビリティへの影響か。
既存の REFACTORING_PLAN_PACKAGE.md / REFACTORING_ENUM_REVIEW.md / REFACTORING_API_REVIEW.md
との重複は最小限にし、それらが未言及か補足が必要な箇所に絞る。

---

## 優先度 1 — バグの根因・設計上の地雷

### ✅ 1-A. `objId` と `uniqueID` の二重 ID 問題を解消する

**現状の問題**

| 識別子 | 定義場所 | カウンタ | 用途 |
|--------|----------|----------|------|
| `objId` | `Entity.java:147` | `Numbering.numberingObjId()` | 全 WorldState マップのキー |
| `uniqueID` | `LivingEntity.java:2842` | `Numbering.numberingYukkuriID()` | `yukkuriRegistry` のキー・家族リスト |

この二重 ID が混乱を生む。具体的な害：
- `yukkuriRegistry` は `uniqueID` でキー付けされている
- `takeMappedObj(i)` は Yukkuri の検索だけ `b.objId == i` の linear scan（L708）
- 本来 `objId` 渡し用の `parentLinkId` に `uniqueID` が入ると movement ガードが効かない
  → 今セッションで修正した `Stalk.upDate()` バグはこの混乱から生まれた

**修正方針**

選択肢 A（推奨）: `yukkuriRegistry` のキーを `uniqueID` から `objId` に変更し、
`LivingEntity.uniqueID` を廃止して `objId` に一本化。
`getUniqueID()` は `getObjId()` の alias として一時残留 → 呼び出し側を順次置き換えて削除。

選択肢 B: 全マップを `uniqueID` に統一（変更箇所が多い）。

影響ファイル: `LivingEntity.java`, `Yukkuri.java`（registry 登録），
`YukkuriFactory.java`, `TerrariumYukkuriLookup.java`, `TransformationService.java`,
全 Event クラス（`getFrom()` / `getTo()` の解釈）。

---

### ✅ 1-B. `Entity.takeMappedObj()` を統合エンティティレジストリに置き換える

**現状の問題**

`Entity.java` L614–713（100行）: WorldState の 29 マップを if-else チェーンで線形走査し、
Yukkuri だけさらに linear scan するという二段構えの O(n) 実装。
新しいエンティティ種別が追加されるたびに if ブロックが増える構造。

```java
// 現状: 29連 if-else + Yukkuri linear scan
if (m.getAutoFeeders().containsKey(i)) return m.getAutoFeeders().get(i);
if (m.getBeds().containsKey(i))        return m.getBeds().get(i);
// ... 27 個続く ...
for (Map.Entry<Integer, Yukkuri> entry : m.getYukkuriRegistry().entrySet()) {
    if (b.objId == i) return b;  // ← キー不一致のため常に全件走査
}
```

**修正方針**

`WorldState` に `Map<Integer, Entity> entityIndex` を追加。
各エンティティが WorldState に登録される時点（現状: コンストラクタか Factory）で
同時に `entityIndex.put(objId, this)` し、`takeMappedObj` を
`return entityIndex.get(i)` の1行に置き換える。

1-A の ID 統一が完了してからこちらに着手すると衝突が少ない。

---

### ✅ 1-C. コンストラクタによる GlobalState 自己登録を廃止する

**現状の問題**

18+ のエンティティクラス（Stalk, Toy, Sui, Stone, Yunba, Trampoline 等）が
コンストラクタ内で `GameWorld.get().getCurrentWorldState().getXxx().put(objId, this)` を
直接呼び出している。

害:
- テストごとに完全な WorldState 初期化が必須になる（`WorldTestHelper.resetWorld()` が必要な理由）
- コンストラクタ内で副作用が起きるためモック不可
- デシリアライズ時にも同じ副作用が走り二重登録のリスクがある

**修正方針**

Factory パターンへ移行:
`TerrariumObjectFactory` や専用の `EntityFactory` が生成と登録を両方担う。
コンストラクタは純粋な初期化のみ（副作用なし）。
デシリアライズ用の `@JsonCreator` パスは登録を行わず、
ロード後に `SaveDataCodec` 側で一括登録する。

---

## 優先度 2 — 肥大クラスの責務分割

### ✅ 2-A. `LivingEntity.java`（4403行・751 public/protected メンバ）のさらなる分割

**現状の問題**

delegate は6つ（BodyDamage, Sleep, Panic, Hunger, BodyCondition, Action）が存在するが、
本体には依然として大量のフィールドと getter/setter が残っている。
フィールドだけで約 80 個、うちカテゴリが混在している:
- ダメージ・バーン系（damage, footBakePeriod, bodyBakePeriod ...）
- 生理系（hungry, shit, tang, pregnantPeriod ...）
- 外見状態（dirty, wet, pealed, packed, blind ...）
- タイマー（angryPeriod, scarePeriod, sadPeriod, nonYukkuriDiseasePeriod ...）

**修正方針**

Step 7 として `LivingEntity` → `Yukkuri` への大規模フィールド移行:
LivingEntity は本来「生き物一般」の抽象基底であるべき。
現状の多くのフィールドはゆっくり固有（ゆっくり病、妊娠、茎など）なので
`Yukkuri` または対応する delegate に移譲するのが適切。

具体的な移動候補:
- `unBirth`, `pregnantPeriod`, `bindStalk`, `stalkList` → `YukkuriStalkDelegate`
- `nonYukkuriDiseasePeriod`, `coreAnkoState` → `YukkuriNydDelegate`
- `exciting`, `excitingPeriod`, `forceExciting` → `YukkuriSexualDelegate`（現は SocialEntity）

---

### ✅ 2-B. `Yukkuri.java`（3929行）の `clockTick()` 分割

**現状の問題**

`clockTick()` が L2091–L2416 の **326行**。
17 個の delegate があるにもかかわらず、clockTick 本体に以下が直書きされている:
- アタッチメントの tick 処理ループ
- 死亡時の腐敗カウント
- 爆発処理
- アンプル効果のカウント
- ゆ虐神拳カウント (`plusGodHand()`)
- 経時加算（age, deadPeriod 等）

**修正方針**

clockTick を「tick のオーケストレーター」に徹させる。
各ブロックを対応する delegate メソッドに移動:
- attachment tick → `attachmentDelegate.tick()`
- 死亡処理 → `stateDelegate.tickDead()` or `damageDelegate.tickDecay()`
- アンプル処理 → 新設 `YukkuriAmpouleDelegate`

clockTick の目標行数: 50行以下。

---

### ✅ 2-C. `SocialEntity.java`（1440行）と `LivingEntity` の境界明確化

**現状の問題**

`SocialEntity` にはしつけ系フラグ（`shittingDiscipline`, `furifuriDiscipline` 等）と
社会属性（`attitude`, `intelligence`, `rapist` 等）が混在している。
一方 `LivingEntity` にも一部の感情・パニック系フラグが残っており、
「どちらに何があるか」が直感的でない。

**修正方針**

役割を再定義:
- `LivingEntity`: 生理的状態（damage, hungry, sick, sleep）のみ
- `SocialEntity`: 社会属性 + しつけ + 家族関係
- 現在 `LivingEntity` に残っている感情系（panicType, trauma）は `SocialEntity` または
  専用の `EmotionState` データクラスへ移動

---

## 優先度 3 — 依存関係の整理

### 3-A. `GameWorld.get().getCurrentWorldState()` の散在を解消する

**現状の問題**

`entity/`, `event/`, `logic/` 全体で 226+ 箇所の同一 static アクセスパターン。
`logic` クラスが GameWorld に直依存しているためテストに WorldState セットアップが必須。

**修正方針**

段階的アプローチ:
1. `logic/` クラスのメソッドシグネチャに `WorldState` を引数として追加するか、
   コンストラクタインジェクションに変更する
2. `event/impl/` は `EventPacket` の `execute(body, worldState)` に拡張する
3. 最終的に GameWorld への static アクセスを Engine 層（`Terrarium`）だけに限定する

---

### 3-B. `WorldState.java`（734行・34 マップ）の責務分割

**現状の問題**

34 個の `Map<Integer, XxxEntity>` を1クラスが保持する God Object。
マップが増えるたびに `takeMappedObj` の if-else も増える連鎖。

**修正方針**

カテゴリ別の State Container に分割:
```
WorldState
├── YukkuriRegistry    // yukkuriRegistry のみ
├── ItemRepository     // Toy, Stone, Sui, Bed, Trampoline 等
├── FoodRepository     // Food, Shit, Vomit, TakenOutFood 等
├── MachineRepository  // AutoFeeder, FoodMaker, Mixer, Diffuser 等
├── FieldRepository    // Barrier, Pool, Farm, wallMap, fieldMap
└── EffectRepository   // sortEffect, frontEffect, event list
```
`WorldState` 自体はこれらの集約コンテナとして残す。

---

### ✅ 3-C. `EventPacket` 実装クラスの共通ロジック集約

**現状の問題**

22 の `EventPacket` 実装に以下の同一パターンが重複:
- `checkEventResponse` 冒頭の `priority = EventPriority.HIGH` セット
- `update` の `sourceBody == null → searchNextTarget → re-fetch → ABORT` パターン
- `execute` の null チェック後の移動処理

今回修正した `RaperReactionEvent.update()` のバグも、このパターンが各クラスに
手書きされていたことで検出が遅れた。

**修正方針**

`EventPacket` に protected ユーティリティメソッドを追加:
```java
protected Yukkuri requireSourceOrAbort() // null/dead なら ABORT シグナルを投げる
protected void setHighPriority()
protected Yukkuri resolveOrSearchNext(UpdateState[] outResult) // re-fetch ロジック共通化
```
または Template Method パターンで `updateInternal(Yukkuri body, Yukkuri source)` を
抽象メソッドとして定義し、null チェック・re-fetch は基底クラスが担う。

---

## 優先度 4 — パッケージ整理（REFACTORING_PLAN_PACKAGE.md 実行）

REFACTORING_PLAN_PACKAGE.md の優先度 A は未完了。以下の順で実施:

1. `draw` → `engine` への移動:
   `GameLoop`, `ImageLoadService`, `ModLoader`, `SaveDataCodec`,
   `World`, `YukkuriFactory`, `YukkuriTickProcessor`

2. `system` → `ui` への移動:
   `ItemWindow`, `LoadWindow`, `MapWindow`, `MainCommandUI`,
   `MouseInputController`, `ButtonListener` 系, `ItemPopup*Action` 系

3. `command.ShowStatusFrame` → `ui`

4. 優先度 B: `system.Sprite` / `system.YukkuriLayer` の配置再検討

---

## 優先度 5 — 命名残件（REFACTORING_ENUM_REVIEW.md 実行）

REFACTORING_ENUM_REVIEW.md の未完了項目:

- `ImageCode.EXCITING_raper` → `EXCITING_RAPER`（mixed-case 修正）
- `ImageCode` ファミリーのナンバリング規則統一（`YUNYAA1/2/3` 等）
- `GadgetMenu.GadgetList` → `GadgetItem` or `GadgetCommand`（REFACTORING_API_REVIEW.md）
- `MessageMap` → `MessageCatalog` or `MessageTable`
- `YukkuriFamilyDelegate.checkRemovedFamilyList()` → `refreshRemovedFamilyMembers()`

---

## 着手順のまとめ

```
1-A → 1-B → 1-C  （ID統一 → registry集約 → コンストラクタ副作用除去）
   ↓
2-A + 2-B 並行  （LivingEntity分割 + clockTick分割）
   ↓
2-C + 3-A 並行  （境界明確化 + 依存整理）
   ↓
3-B + 3-C 並行  （WorldState分割 + Event共通化）
   ↓
4 → 5  （パッケージ整理 → 命名）
```

1-A の ID 統一が全体の前提になる。ここを後回しにすると以降の作業でも
同種のバグ（lookup キー不一致）が繰り返し発生する。
