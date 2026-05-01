# Simyukkuri2021 アーキテクチャ設計計画

---

## TL;DR

このドキュメントは「現状の何が問題か」と「どう直すか」の設計方針をまとめたもの。
**どこから手をつけるか** → [第3部: 実装計画](#第3部-実装計画) を見ること。

**現状の主要問題（深刻度順）**

| # | 問題 | 深刻度 |
|---|-----|--------|
| 1 | `enums.YukkuriType` ↔ `yukkuri.*` 循環参照 | 最高 |
| 2 | 8組のパッケージ間循環依存 | 最高 |
| 3 | `BodyAttributes.java` 7133行・`Body.java` 7850行 | 最高 |
| 4 | `SimYukkuri.world` / `RND` がグローバル可変状態 | 最高 |
| 5 | `src.base` が「基底クラス置き場」（責任でまとめていない） | 高 |
| 6 | `changeBody()` がリフレクション依存 | 高 |
| 7 | 汎用ロジックが具象ゆっくりクラスを直接参照 | 高 |
| 8 | `takeMappedObj()` が Entity に実装（Repository責任の漏れ） | 高 |
| 9 | パッケージ名が `src.*`（逆ドメイン名でない） | 中 |
| 10 | Hungarian記法・Raw型・定数God Object 等 | 低〜中 |

---

# 第1部: 現状の負債

## 1. クラス設計の問題

### 1-1. 巨大クラス（神クラス）

```
src/base/BodyAttributes.java  7,133行  物理・生物・社会・ゆっくり固有が全混在
src/base/Body.java            7,850行  ゆっくり固有メソッドが全混在
```

`Obj.java` のコメント「private変数は使わずprotectedを使用してください」が
サブクラスが protected フィールドに直接アクセスする慣習を生んだ根源。

`super.getHoge()` が「どのsuperか」不明なほど継承が深く、意味的な層分けがない。

### 1-2. changeBody() がリフレクション依存

```java
// YukkuriUtil.java - 継承階層の深さをハードコード
fromField = from.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields();
```

継承階層を変えると `.getSuperclass()` の数がズレてバグになる。

### 1-3. getCollisionX() の層違反

```java
// BodyAttributes.java
public int getCollisionX() {
    return (bodySpr[getBodyAgeState().ordinal()].getImageW() + getExpandSizeW()) >> 1;
    //      ↑ Yukkuri層（スプライト）              ↑ LivingEntity層（成長段階）
}
```

「衝突判定サイズ」は物理的概念（Entity層の責任）なのに、
計算に Yukkuri 固有データが必要なため Entity から参照できない構造になっている。

---

## 2. パッケージ設計の問題

### 原則違反：「基底クラス置き場」としての src.base

> パッケージは「構造的な理由（基底だから base）」ではなく
> **「責任領域」** でまとめる。
> 基底クラスはその具象クラスと同じパッケージに置く。

**src.base の実態**

| クラス | 実際の役割 | あるべき場所 |
|-------|-----------|------------|
| `Obj` | 全オブジェクトの物理基底 | `src.entity` |
| `ObjEX` | 非生物可動オブジェクト基底 | `src.entity` |
| `BodyAttributes` | ゆっくり属性（7133行） | `src.yukkuri.core` |
| `Body` | ゆっくり行動（7850行） | `src.yukkuri.core` |
| `Effect` | エフェクト基底 | `src.effect`（**具象クラスはすでにここにある！**） |
| `Attachment` | アタッチメント基底 | `src.attachment`（**具象クラスはすでにここにある！**） |
| `EventPacket` | イベントパケット基底 | `src.event`（**具象クラスはすでにここにある！**） |
| `Okazari` | おかざり（アイテム） | `src.item` |
| `BodyXxxProfile` × 5 | 値オブジェクト群 | `src.yukkuri.core` |

正しいパターン（業界標準）：

```
src.effect
  ├── Effect.java        ← 基底（パッケージ直下）
  └── impl
       ├── BakeSmoke.java ← 具象
       └── Hit.java

現状（最悪）：
  src.base.Effect        ← 基底
  src.effect.BakeSmoke   ← 具象（別パッケージ！）
```

**src.draw の実態**（「描画」のはずがエンジン全体）

| クラス群 | 実際の役割 |
|---------|-----------|
| `Terrarium`, `TerrariumXxx` × 10 | ゲームエンジン・世界管理 |
| `BodyFactory`, `TerrariumObjectFactory` 等 | 生成ファクトリ |
| `SaveDataCodec` | セーブデータIO |
| `GameLoop` | ゲームループ |
| `MyPane` | UIパネル |
| `Renderer`, `Translate`, `ObjDrawComp` | **これだけが本来の draw** |

**src.system の実態**（「システム」という名の何でも屋）

UI / 入力制御 / IO / ログ / 描画 / ゲーム管理 が全混在。

**src.game の実態**

```
Shit, Stalk, Vomit → src.item でいい
Dna               → src.yukkuri.core でいい
```

### パッケージ名が src.* （Java 命名規則の根本違反）

```java
// 現状（完全に間違い）
package src.yukkuri;   // src はディレクトリ名であってパッケージ名ではない
```

Java の標準は**逆ドメイン名**：`com.example.simyukkuri.yukkuri`

Maven/Gradle では `src/main/java/` 以下がパッケージルートであり、
`src/` 自体はパッケージ名に含まれない。

---

## 3. 依存関係の問題

### 3-1. yukkuri 具象クラスへの逆依存（全パッケージ）

具象ゆっくりクラス（`yukkuri.*`）は他のパッケージに依存されてはならないが、
現実は逆方向の依存が蔓延している。

```
【あるべき方向】             【現実の汚染】
yukkuri.Marisa              enums.YukkuriType    → yukkuri.* 全32クラス  ← 最悪
  → logic.BodyLogic         util.YukkuriUtil     → yukkuri.* 12クラス
  → enums.YukkuriType       item.BreedingPool    → yukkuri.* 10クラス
  → util.YukkuriUtil        draw.Terrarium       → yukkuri.* 25クラス
                            draw.MyPane          → yukkuri.* 25クラス
                            logic.FoodPredatorCandidatePolicy → yukkuri.Fran/Meirin/Remirya/Sakuya
                            logic.BodyPartnerSearchRule       → yukkuri.Fran/HybridYukkuri 等
                            logic.BodyStealRule               → yukkuri.HybridYukkuri
                            system.LoggerYukkuri              → yukkuri.Deibu/Kimeemaru 等
                            game.Dna                          → yukkuri.Tarinai
                            command.GadgetTool                → yukkuri.Reimu
                            draw.BodyFactory                  → yukkuri.* （唯一許容される場所）
```

### 3-2. YukkuriType enum の循環参照（根源）

```java
// enums/YukkuriType.java
MARISA("Marisa", ..., Marisa.nameJ, Marisa.type),  // 具象クラスの定数を参照
```

- `yukkuri.Marisa` → `enums.YukkuriType` を使いたい
- `enums.YukkuriType` → `yukkuri.Marisa` を参照している

→ **循環参照**。コンパイルが通っているのは初期化順がたまたま噛み合っているだけ。

### 3-3. 汎用ロジックへの種族知識の漏れ出し

```java
// YukkuriUtil.java（遺伝計算）
if (babyType == Kimeemaru.type) babyType = Ayaya.type;
// → 新キャラ追加のたびにここを修正する羽目になる

// FoodPredatorCandidatePolicy.java（捕食者判定）
if (d.getType() == Sakuya.type && b.getType() == Remirya.type) { ... }
// → 「サクヤはレミリアに仕える」という種族固有知識がロジッククラスにある

// BodyPartnerSearchRule.java（パートナー探索）
b.getType() != HybridYukkuri.type
// → HybridYukkuri を直接参照
```

### 3-4. 8組のパッケージ間循環依存（実測値）

| 循環ペア | 件数(A→B) | 件数(B→A) | 深刻度 |
|---------|----------|----------|--------|
| `base ↔ logic` | 10 | 87 | **最高** |
| `base ↔ event` | 12 | 66 | **最高** |
| `draw ↔ item` | 86 | 71 | **最高** |
| `enums ↔ yukkuri` | 32 | 146 | **最高** |
| `base ↔ item` | 10 | 70 | 高 |
| `base ↔ attachment` | 10 | 24 | 高 |
| `system ↔ item` | 32 | 49 | 高 |
| `draw ↔ command` | 8 | 8 | 中 |

---

## 4. Java 一般規則からの逸脱

| 問題 | 現状 | あるべき姿 |
|-----|------|-----------|
| **グローバル可変状態** | `SimYukkuri.world`, `RND` が `public static` | DI or シングルトン。テストの `WorldTestHelper.resetWorld()` が必要な根本原因 |
| **Hungarian記法** | `bFallingUnderGround`, `nMostDepth`, `eCoreAnkoState`, `anBabyName` | `fallingUnderGround`, `mostDepth`, `coreAnkoState`, `babyNames` |
| **Raw型 Comparable** | `implements Comparable` 型パラメータなし + `compareTo()` が常に0 | `Comparable<Entity>` に修正するか、使っていないなら削除 |
| **@Transient 2種混在** | `java.beans.Transient`（JavaBeans用）と `@JsonIgnore`（Jackson用）が混在 | Jackson に統一するなら `@JsonIgnore` のみ |
| **God Object 定数クラス** | `Const.java` に無関係な定数が集中 | 定数は使う場所の近くに置く |
| **Entity にRepository責任** | `Obj.takeMappedObj()` が全マップを線形探索 | `MapObjectRepository.findById()` を別クラスに |
| **二重シリアライズ** | `java.io.Serializable` と Jackson の両方を使用 | Jackson のみに統一（または理由をコメントに明記） |
| **略語命名** | `ObjEX`（EX が何の略か不明） | `EntityEx` か `ExtendedEntity` |

---

# 第2部: 目指す設計

## 5. 新しいパッケージ設計

```
src.entity
  ├── Entity.java              （旧 Obj）
  └── EntityEx.java            （旧 ObjEX）

src.yukkuri
  ├── Yukkuri.java             （旧 Body: ゆっくり抽象クラス）
  ├── core
  │    ├── LivingEntity.java
  │    ├── SocialEntity.java
  │    └── profile
  │         ├── BodyStatProfile.java
  │         ├── BodyTimingProfile.java
  │         ├── BodyBehaviorProfile.java
  │         ├── BodyNameSet.java
  │         ├── BodySpriteSet.java
  │         └── Dna.java       （src.game から移動）
  └── impl
       ├── Marisa.java
       ├── Reimu.java
       └── ...（全具象ゆっくり）

src.effect
  ├── Effect.java              （src.base から移動）
  └── impl
       ├── BakeSmoke.java, Hit.java, Mix.java, Steam.java

src.attachment
  ├── Attachment.java          （src.base から移動）
  └── impl
       ├── Ants.java, Badge.java, Fire.java, Needle.java, （各アンプル類）

src.event
  ├── EventPacket.java         （src.base から移動）
  └── impl
       ├── FuneralEvent.java, ProposeEvent.java, ...

src.item
  └── impl
       ├── Bed.java, Food.java, Toilet.java, ...
       ├── Okazari.java        （src.base から移動）
       └── Shit.java, Stalk.java, Vomit.java  （src.game から移動）

src.engine
  ├── GameLoop.java, Terrarium.java, World.java, TerrariumXxx.java ...
  └── factory
       ├── BodyFactory.java, TerrariumObjectFactory.java, TerrariumEffectFactory.java

src.render
  ├── Renderer.java, MyPane.java, ObjDrawComp.java, Translate.java
  ├── TerrainBillboard.java, BodyLayer.java, Sprite.java
  └── geom
       ├── Color4y.java, Point4y.java, Rectangle4y.java, Dimension4y.java
       └── BasicStrokeEX.java

src.io
  ├── SaveDataCodec.java, IniFileReader.java, ResourceUtil.java
  ├── ImageLoadService.java, ModLoader.java
  └── log
       ├── LoggerYukkuri.java, CustomLogFormatter.java

src.ui
  ├── MainCommandUI.java, ItemWindow.java, LoadWindow.java, MapWindow.java
  ├── YukkuriFilterPanel.java, IconPool.java
  └── listener
       ├── ButtonListener.java, InputController.java, MouseInputController.java, ...

src.game
  ├── Player.java, Cash.java, FrameRate.java, MapPlaceData.java
  └── message
       ├── MessageMap.java, MessagePool.java

src.logic     変更なし
src.command   変更なし
src.enums     変更なし（ただし YukkuriType の循環参照を修正）
src.util      変更なし
```

**あるべき依存方向（一方通行）**

```
  enums / entity
       ↓
  yukkuri.core
       ↓
  yukkuri.impl
       ↓
  logic / event        （item / attachment も参照）
       ↓
  item / attachment / effect
       ↓
  engine / command     （上位調整層: 全てに依存してよい）
       ↓
  ui / io / render
```

---

## 6. 新しいクラス階層

```
Entity（旧 Obj）
├── EntityEx             ← 既存の非生物可動オブジェクト
├── Food, Toilet, Bed... ← 既存のアイテム群
└── LivingEntity         ← 【新設】生物的生存の層
    └── SocialEntity     ← 【新設】社会的行動・関係の層
        └── Yukkuri      ← 旧 Body
            ├── Marisa
            ├── Reimu
            └── ...
```

`BodyAttributes` は**廃止**し、その責任を各層に分散する。

---

## 7. 各層の責任とフィールド

### Entity（物理層）旧 Obj

全ゲームオブジェクトの物理的存在。ゆっくりも食べ物も同じ基底。

```java
public abstract class Entity {
    int objId; Type objType; long age;          // 識別
    int x, y, z;                                // 座標
    int vx, vy, vz;                             // 外力ベクトル
    int bx, by, bz;                             // 拘束ベクトル（コンベア等）
    boolean removed, canGrab, grabbed;
    boolean enableWall, fallingUnderGround, inPool;
    int mostDepth; Where where;
    int value, cost, bindObj;                   // 経済・バインド
    int imgW, imgH, pivX, pivY, ofsX, ofsY;    // 描画
    Point4y screenPivot; Rectangle4y screenRect;
    int collisionWidth, collisionHeight;         // ★新規: 衝突サイズ（後述）
}
```

### LivingEntity（生物層）新設

「他者の意思を前提としない」行動。食べる・眠る・排泄・成長・病気・生殖。

```java
public abstract class LivingEntity extends Entity {
    // Vitality（体力）
    int[] damage; boolean dead, pealed, melt, packed, burned;
    BodyBake bodyBake; FootBake footBake;

    // Fatigue（疲労）
    int fatigue; boolean sleeping, wantToSleep; int sleepCount;

    // Appetite（食欲）
    int hungry; TangType tangType;

    // Excretion（排泄欲求）
    int shit; boolean hasShit;

    // Health（健康）
    int sickPeriod; boolean sick;

    // Growth（成長）
    AgeState ageState; long babyLimit, childLimit;

    // Emotions（感情）
    Happiness happiness; PanicType panicType; int stress; Pain pain;

    // 移動意図（生物的なもの: FOOD / SHIT / SLEEP のみ）
    PurposeOfMoving purposeOfMoving;

    // 生殖（生物的な妊娠・出産）
    boolean hasBaby; int pregnantPeriod, pregnantPeriodOrg;
}
```

**原則**: LivingEntity は「他のゆっくりの存在を知らなくていい」。

### SocialEntity（社会層）新設

「他者との相互関係を前提とする」行動。
家族を持つ・パートナーを求める・コミュニケーションする。

```java
public abstract class SocialEntity extends LivingEntity {
    // 家族関係
    int partner; int[] parents;
    List<Integer> childrenList, elderSisterList, sisterList;

    // 社会的感情
    LovePlayer lovePlayer; Trauma trauma; Attitude attitude;

    // コミュニケーション
    String currentMessage; int messageCount; EventPacket currentEvent;

    // 社会的地位
    BodyRank bodyRank; PublicRank publicRank;

    // 知性
    Intelligence intelligence;

    // 移動意図（社会的なもの: PARTNER / YUKKURI 等）を LivingEntity に追加
}
```

**SUKKIRI の3分類**（現状は `PurposeOfMoving.SUKKIRI` が区別なし）

| パターン | 層 | 意味 |
|---------|---|------|
| オナニー | 生物層 | 単独完結の生物的衝動 |
| レイプ | 社会層 | 一方的・相手の存在が必要 |
| パートナースキンシップ | 社会層 | 相互同意・パートナー関係が前提 |

将来的に `SukkiriType { MASTURBATION, RAPE, SKINSHIP }` enum を追加。

### Yukkuri（ゆっくり固有層）旧 Body

おかざり・変身・スプライト・名前・種族固有パラメータ。

```java
public abstract class Yukkuri extends SocialEntity {
    BodyNameSet bodyNameSet;
    BodySpriteSet bodySpriteSet;
    Okazari okazari;
    boolean transformed; YukkuriType transformedFrom;
    // 種族固有パラメータ → BodyStatProfile 等のヘルパーに委譲済み

    abstract void tuneParameters();
    abstract int getImage(...);
}
```

---

## 8. メソッド配置の原則

> **メソッドは、主として依存するフィールドを持つ最も基底の層に置く。**

| 依存フィールドの所在 | メソッドを置く層 |
|---------------------|----------------|
| Entity のフィールドのみ | Entity |
| LivingEntity のフィールドを含む | LivingEntity |
| SocialEntity のフィールドを含む | SocialEntity |
| Yukkuri 固有のフィールドを含む | Yukkuri |

**複数層をまたぐ計算は「結果を下位層フィールドに書き込む」で解決する。**

具体例：`getCollisionX()` の問題

```java
// 現状: Yukkuri層（スプライト）+ LivingEntity層（ageState）を合成
// → 結果が Entity から参照できない

// 解決策: Yukkuri がスプライト変更・ageState変更時に Entity を更新する
// Yukkuri層:
super.setCollisionSize(
    bodySpr[ageState.ordinal()].getImageW() + getExpandSizeW(),
    bodySpr[ageState.ordinal()].getImageH() + getExpandSizeH()
);
// Entity層: collisionWidth/Height フィールドを参照するだけでよくなる
```

**メソッド振り分け早見表**

| メソッド例 | 振り先 |
|-----------|--------|
| `setCalcX()`, `kick()` | Entity |
| `getCollisionX/Y()` | **廃止** → `collisionWidth/Height` フィールドで代替 |
| `isTooHungry()` | LivingEntity |
| `canAction()` | LivingEntity / SocialEntity |
| `isFamily()` | SocialEntity |
| `checkFood()`, `checkToilet()` | Yukkuri（ゲーム固有） |

---

## 9. 依存関係の修正方針

### YukkuriType の循環参照を断ち切る

type ID と名称の権威を `YukkuriType` 側に移す。

```java
// 新設計: enum 内に全情報を集約
public enum YukkuriType {
    MARISA(1000, "Marisa", "まりさ", "Marisa"),
    REIMU (1001, "Reimu",  "れいむ", "Reimu"),
    ;
    private final int typeID;
    private final String nameJ;
    // ...
}

// 各具象クラスは enum を参照する（依存方向が正しくなる）
public class Marisa extends Yukkuri {
    public static final int type = YukkuriType.MARISA.getTypeID();
    public int getType() { return YukkuriType.MARISA.getTypeID(); }
}
```

### 種族固有の知識を enum / 具象クラスに戻す

```java
// 遺伝変異ルール → YukkuriType に持たせる
public enum YukkuriType {
    KIMEEMARU(...) {
        @Override public YukkuriType mutate() { return AYAYA; }
    },
    AYAYA(...) {
        @Override public YukkuriType mutate() {
            return GameRandom.nextInt(20) == 0 ? KIMEEMARU : AYAYA;
        }
    };
    public YukkuriType mutate() { return this; }   // デフォルト: 変異なし
}
// YukkuriUtil は babyType.mutate() を呼ぶだけで Ayaya/Kimeemaru を知らなくていい

// 主従関係 → YukkuriType に持たせる
SAKUYA(...) {
    @Override public boolean isServantOf(YukkuriType master) {
        return master == REMIRYA || master == FRAN;
    }
},

// ハイブリッド・捕食者判定 → Yukkuri 抽象クラスにメソッド追加
public boolean isHybrid()   { return false; }  // HybridYukkuri だけ true
public boolean isPredator() { return false; }  // Remirya/Fran だけ true
```

### インスタンス生成を BodyFactory に集約

```java
// BodyFactory のみが new Marisa() 等を行う（唯一 yukkuri.impl を知っていい場所）
Body b = BodyFactory.create(YukkuriType.MARISA);
// Terrarium / MyPane 等は YukkuriType だけ知っていればよくなる
```

### changeBody() のリフレクション廃止

各層が自層のフィールドコピーを責任持つ。

```java
// 各層に copyXxxFrom() を定義
Entity:      void copyEntityFrom(Entity from) { this.x = from.x; ... }
LivingEntity: void copyLivingFrom(LivingEntity from) { super.copyEntityFrom(from); ... }
SocialEntity: void copySocialFrom(SocialEntity from) { super.copyLivingFrom(from); ... }
Yukkuri:      void copyYukkuriFrom(Yukkuri from)    { super.copySocialFrom(from); ... }

// changeBody() はこれを呼ぶだけ
to.copyYukkuriFrom(from);
```

NOCOPY_FIELD 相当は `copyYukkuriFrom()` 内で skip するか、
別メソッド `copyIdentityFrom()` に分ける。

---

# 第3部: 実装計画

## 優先順位と手順

```
リスク低・効果高 ─────────────────────────────── リスク高・効果高
      ↓                                                   ↓
   Step 1                Step 2              Step 3      Step 4+
isPredator()等       YukkuriType        src.base 解体   継承階層
メソッド追加         循環参照修正       パッケージ整理   全面再設計
```

---

### Step 1: 種族判定メソッドの追加（今すぐ着手可能）

**リスク**: ほぼゼロ（後方互換、メソッド追加のみ）
**効果**: Logic クラスから yukkuri 具象への直接参照が消える

```java
// Yukkuri（旧 Body）に追加
public boolean isPredator()                      { return false; }
public boolean isHybrid()                        { return false; }
public boolean isServantOf(int masterType)       { return false; }

// 各具象クラスでオーバーライド
// Remirya.java
@Override public boolean isPredator() { return true; }

// Sakuya.java
@Override public boolean isServantOf(int masterType) {
    return masterType == Remirya.type || masterType == Fran.type;
}
```

修正対象：
- `logic.FoodPredatorCandidatePolicy` → `b.isPredator()` に置換
- `logic.BodyPartnerSearchRule` → `b.isHybrid()`, `b.isServantOf()` に置換
- `logic.BodyStealRule` → `b.isHybrid()` に置換

---

### Step 2: YukkuriType 循環参照の修正（中規模）

**リスク**: 低〜中（全クラスの type 定数参照が変わる）
**効果**: 依存関係の根源が断ち切れる。Step 3 以降が整理しやすくなる

1. `YukkuriType` に typeID・nameJ・nameE を集約
2. 各具象クラスの `type` を `YukkuriType.XXX.getTypeID()` に変更
3. `YukkuriType` から `import src.yukkuri.*` を全削除

合わせて `YukkuriType.mutate()`, `breedWith()` を追加し、
`YukkuriUtil`, `BreedingPool` の具象参照を置換する。

---

### Step 3: src.base 解体（中規模）

**リスク**: 中（パッケージ移動で全 import 文が変わる）
**効果**: 循環依存の `base ↔ logic` 等が解消される

1. `Effect`→`src.effect`, `Attachment`→`src.attachment`, `EventPacket`→`src.event` に移動
2. `Okazari`→`src.item`, `BodyXxxProfile`→`src.yukkuri.core` に移動
3. `src.base` が `Obj`/`ObjEX`/`BodyAttributes`/`Body` だけになる

---

### Step 4: 継承階層の新設（大規模）

**リスク**: 高（全ファイルに影響）
**効果**: 根本的な設計改善

1. `LivingEntity`, `SocialEntity` クラスを新設
2. `BodyAttributes` のフィールドを各層に分配
3. `Obj` → `Entity` 改名、`Body` → `Yukkuri` 改名
4. `changeBody()` をリフレクション廃止に書き直し
5. `collisionWidth/Height` を Entity に追加し `getCollisionX/Y()` を廃止

---

### Step 5: パッケージ名変更（長期）

**リスク**: 最高（全ファイルの package 宣言と import 文）
**効果**: Java 標準命名規則への準拠

`src.*` → `com.example.simyukkuri.*`（または適切なドメイン名）

一括置換で機械的にできるが、テストを含む全ファイルが対象。

---

## 現状の完了状況

| フェーズ | 状態 |
|---------|------|
| Phase 3: BodyAttributes リファクタリング | **完了** |
| └ BodyNameSet getter 委譲化・alias フィールド削除 | 完了（29サブクラスの直接参照も getter 経由に変換） |
| └ BodySpriteSet 完全委譲化 | 完了（alias 3本削除・syncSpriteAliases 削除） |
| └ BodyStatProfile / BodyTimingProfile / BodyBehaviorProfile 統合 | 完了 |
| **Step 1**: 種族判定メソッドの追加 | **完了** |
| └ `isPredator()` / `isHybrid()` / `isServant()` を Body に追加 | 完了 |
| └ Remirya・Fran・Sakuya・Meirin・HybridYukkuri 等でオーバーライド | 完了 |
| └ FoodPredatorCandidatePolicy・BodyPartnerSearchRule・BodyStealRule 等で使用 | 完了 |
| **Step 2**: YukkuriType 循環参照の修正 | **完了** |
| └ typeID・nameJ・nameE を enum 内に直接埋め込み、yukkuri クラスへの import を全削除 | 完了 |
| └ `getNameJ()` は後方互換でロケール依存の表示名を返す。`getJapaneseName()`/`getEnglishName()` を追加 | 完了 |
| **Step 3**: src.base 解体 | 未着手 |
| **Step 4**: 継承階層の新設 | 未着手 |
| **Step 5**: パッケージ名変更 | 未着手 |

---

## 制約事項・注意事項

- **Java 8**: 多重継承不可、interface default method 不可
  → 単一継承チェーンで意味的な層分けをする
- **テストが `src.*` パッケージ前提**: パッケージ名変更（Step 5）は最後
- **セーブデータ互換**: **非互換でよい**。過去バージョンが Java 標準 ObjectSerializer を使用していたため、Jackson 移行時点で既に非互換になっている。JSONプロパティ名の変更・フィールドの移動は気にせず実施してよい。
- **protected フィールド直接アクセス**: サブクラスが `body.bodySpr[i]` で直接アクセスしているため、段階的に getter 経由に移行してから private 化する

---

# 付録: 実測パッケージ間依存数

```
attachment → base(24) draw(12) enums(54) system(15) util(30)
base       → attachment(10) draw(19) enums(74) event(12) game(6) item(10) logic(10) system(17) util(18)
command    → attachment(20) base(22) draw(8) enums(10) event(2) game(6) item(50) logic(8) system(11) util(17) yukkuri(1)
draw       → attachment(26) base(34) command(8) effect(16) enums(28) game(18) item(86) logic(14) system(36) util(35) yukkuri(64)
effect     → base(4) draw(4)
enums      → system(3) util(4) yukkuri(32)
event      → attachment(2) base(66) draw(14) enums(70) item(18) logic(21) system(41) util(90) yukkuri(4)
game       → base(8) draw(7) enums(16) item(3) system(8) util(9) yukkuri(1)
item       → attachment(1) base(70) command(5) draw(71) enums(86) game(13) system(49) util(79) yukkuri(11)
logic      → attachment(7) base(87) draw(28) enums(112) event(21) game(22) item(49) system(20) util(84) yukkuri(10)
system     → base(18) command(14) draw(20) enums(11) game(8) item(32) util(28) yukkuri(5)
util       → attachment(1) base(13) draw(13) enums(12) game(6) system(7) yukkuri(12)
yukkuri    → base(36) command(1) draw(109) enums(146) event(2) item(1) logic(6) system(37) util(87)
```
