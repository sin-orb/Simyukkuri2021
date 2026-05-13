# Simyukkuri2021 アーキテクチャ設計計画

---

## TL;DR

このドキュメントは「現状の何が問題か」と「どう直すか」の設計方針をまとめたもの。
**どこから手をつけるか** → [第3部: 実装計画](#第3部-実装計画) を見ること。

**実装の現在地** → [実装進捗](#実装進捗) を見ること。

**現状の主要問題（深刻度順）**

| # | 問題 | 深刻度 |
|---|-----|--------|
| 1 | 旧 `enums.YukkuriType` ↔ `yukkuri.*` 依存設計 | 最高 |
| 2 | 8組のパッケージ間循環依存 | 最高 |
| 3 | `Yukkuri.java` 9396行（`BodyAttributes` は削除済み。責務は LivingEntity=1865行・SocialEntity=474行に分散済み） | 高 |
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
（旧）src/base/BodyAttributes.java  7,133行  → 削除済み（Step 6-5 完了）
（旧）src/base/Body.java            7,850行  → Yukkuri.java にリネーム

（現在）
src/entity/living/LivingEntity.java   1,865行  生物一般の状態・行動
src/entity/living/SocialEntity.java     474行  社会的関係・感情
src/yukkuri/Yukkuri.java              9,396行  ゆっくり固有（さらなる分割が次の課題）
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
| `ObjEX` | 世界上の runtime object の共通基底。`ObjEXType` は実装レジストリ | `src.entity.world` か `src.entity` 直下 |
| `BodyAttributes` | ゆっくり属性（7133行） | `src.entity.living` |
| `Body` | ゆっくり行動（7850行） | `src.yukkuri` |
| `Effect` | エフェクト基底 | `src.effect`（**具象クラスはすでにここにある！**） |
| `Attachment` | アタッチメント基底 | `src.attachment`（**具象クラスはすでにここにある！**） |
| `EventPacket` | イベントパケット基底 | `src.event`（**具象クラスはすでにここにある！**） |
| `Okazari` | Body に従属するおかざり実体 | `src.entity.world.bodylinked` |
| `BodyXxxProfile` × 5 | 値オブジェクト群 | `src.entity.living.profile` |

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
Shit, Vomit       → world runtime object として再配置
Stalk             → Body 由来の world runtime object として再配置
Dna               → src.entity.living.profile か src.data へ移動
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

### 3-2. YukkuriType 周りの旧依存設計（整理済み/整理対象）

```java
// 旧設計では、enum と具象クラスが相互に名前/型を参照していた
// 現設計の YukkuriType は文字列と typeID を持つだけで、具象クラスを直接参照しない
```

旧設計では `yukkuri.*` と `YukkuriType` が相互に型情報を持っていたため、
依存の向きが崩れていた。現設計では `YukkuriType` は enum のみで完結させる。

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
| **略語命名** | `ObjEX` / `ObjEXType`（意味が不明） | `WorldEntity` / `WorldEntityKind` へ改名 |

---

# 第2部: 目指す設計

## 5. 新しいパッケージ設計

```
src.entity
  ├── Entity.java              （旧 Obj）
  ├── WorldEntity.java         （旧 ObjEX）
  └── kind
       └── WorldEntityKind.java（旧 ObjEXType）

src.entity.living
  ├── LivingEntity.java
  ├── SocialEntity.java
  ├── state
  │    ├── VitalState.java
  │    ├── NeedState.java
  │    ├── GrowthState.java
  │    ├── MindState.java
  │    ├── RelationState.java
  │    ├── ReproductionState.java
  │    ├── ActionState.java
  │    ├── WorldContactState.java
  │    └── PresentationState.java
  └── profile
       ├── BodyStatProfile.java
       ├── BodyTimingProfile.java
       ├── BodyBehaviorProfile.java
       ├── BodyNameSet.java
       ├── BodySpriteSet.java
       └── Dna.java           （src.game から移動）

src.yukkuri
  ├── Yukkuri.java             （旧 Body: ゆっくり抽象クラス）
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

src.entity.world
  ├── static
  │    ├── Bed.java, Toilet.java, House.java, BreedingPool.java, ...
  │    ├── GarbageChute.java, MachinePress.java, FoodMaker.java, ...
  │    └── StickyPlate.java, HotPlate.java, ProcesserPlate.java, ...
  ├── mobile
  │    ├── Food.java, Shit.java, Vomit.java, Toy.java, Stone.java
  │    └── ...
  ├── bodylinked
  │    ├── Stalk.java          （Body 由来の world runtime object）
  │    └── Okazari.java        （Body に従属するおかざり実体）

src.field
  ├── FieldShape.java          （旧 FieldShapeBase の基底）
  └── impl
       ├── Barrier.java
       ├── Farm.java
       ├── Pool.java
       └── Beltconveyor.java

src.engine
  ├── GameLoop.java, Terrarium.java, World.java, TerrariumXxx.java ...
  ├── transform
  │    ├── TransformationService.java
  │    └── TransformationPolicy.java
  └── factory
       ├── BodyFactory.java, TerrariumObjectFactory.java, TerrariumEffectFactory.java

src.render
  ├── Renderer.java, MyPane.java, ObjDrawComp.java, Translate.java
  ├── BodyLayer.java, Sprite.java
  └── geom
       ├── Color4y.java, Point4y.java, Rectangle4y.java, Dimension4y.java
       └── BasicStrokeEX.java

src.visual
  └── TerrainBillboard.java

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

src.meta
  ├── Player.java
  └── Cash.java, FrameRate.java

src.game
  ├── MapPlaceData.java
  └── message
       ├── MessageMap.java, MessagePool.java

src.logic     変更なし
src.command   変更なし
src.enums     変更なし（ただし WorldEntityKind / YukkuriType の責務を整理）
src.util      変更なし
```

**あるべき依存方向（一方通行）**

```
entity
       ↓
  entity.living
       ↓
  yukkuri
       ↓
  logic / event        （item / attachment も参照）
       ↓
  item / attachment / effect / field
       ↓
  engine / command     （上位調整層: 全てに依存してよい）
       ↓
  ui / io / render / visual / meta
```

---

## 6. 新しいクラス階層

```
Entity（旧 Obj）
├── WorldEntity                ← 旧 ObjEX。世界上の runtime object の共通基底
│   ├── StaticEntity           ← 据え置き設備
│   ├── MobileEntity           ← 可動・消耗物
│   └── BodyLinkedEntity       ← Stalk のような Body 由来の独立実体
└── LivingEntity               ← 【新設】生物一般の層
    └── SocialEntity           ← 【新設】社会的行動・関係の層
        └── Yukkuri            ← 旧 Body（ゆっくり固有の実装）
            ├── Marisa
            ├── Reimu
            └── ...

継承関係の要約:
`Entity` -> `LivingEntity` -> `SocialEntity` -> `Yukkuri`

`WorldEntity` は `Entity` 直下の別枝で、`Yukkuri` の親ではない。

Entity階層の外
├── Attachment
├── Effect
├── FieldShape
├── VisualObject
├── GameState
└── Service / Data
```

| 外側の系統 | 具体例 | 役割 |
|----------|------|------|
| `VisualObject` | `TerrainBillboard` | 背景部品・描画補助。世界のシミュレーション主体ではない |
| `GameState` | `Player` | 所持金・所持品などのプレイヤー状態 |
| `Service / Data` | `SaveDataCodec`, `MessagePool`, `Dna` など | 処理・定義・設定 |

`src.field` は「フィールド上に配置される図形/区域」の系統で、
`FieldShape` を基底に `Barrier` / `Farm` / `Pool` / `Beltconveyor` を `impl` に置く。
現行の `src.item` 配下に散っているのは移行前の配置で、設計上はここへ寄せる。

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

### WorldEntity（世界実体層）旧 ObjEX

`ObjEX` は「非生物可動オブジェクト」ではなく、**世界上で tick される runtime object の共通基底**として再定義する。
`Barrier` や `TerrainBillboard` のようなフィールド編集・背景部品はここに入れない。

```java
public abstract class WorldEntity extends Entity {
    int option;
    int interval = 1;
    boolean enabled = true;
    int collisionWidth, collisionHeight;
    int linkParent = -1;
    int looks = 0;
    WorldEntityKind kind;
}
```

### LivingEntity（生物一般層）新設

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

**原則**: LivingEntity は「種族を知らなくていい」。
`Yukkuri` も「全状態を抱え込まない」。

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
`Yukkuri` はゆっくり固有の統合層に留める。
`BodyAttributes` 相当の巨大状態は `state` / `profile` / `inventory`
`relation` のような独立オブジェクトへ分割する前提。
ここでいう `state` 群は「`Yukkuri` だけのフィールド」ではなく、
`LivingEntity` / `SocialEntity` / `Yukkuri` の各層がそれぞれ持つ
composition である。

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

### BodyAttributes の再分配表

`Yukkuri` に残さず、状態の束を切り分ける。  
この表の「保持先」が実際の格納先で、`Yukkuri` はそれらを束ねるだけにする。

| 属性束 | 主なフィールド例 | 保持先 |
|---|---|---|
| 物理・移動 | `x/y/z`, `destX/Y/Z`, `countX/Y/Z`, `dirX/Y/Z`, `direction`, `speed`, `option`, `interval`, `enabled`, `collisionWidth/Height`, `linkParent`, `looks` | `Entity` / `WorldEntity` |
| 生命・損傷 | `damage`, `dead`, `crushed`, `burned`, `melt`, `pealed`, `packed`, `blind`, `criticalDamege`, `bodyBake`, `footBake`, `cantDiePeriod`, `noDamagePeriod`, `falldownDamage` | `VitalState` |
| 欲求・疲労 | `hungry`, `shit`, `sleeping`, `sleepingPeriod`, `wakeUpTime`, `noHungryPeriod`, `noHungrybySupereatingTimePeriod`, `purposeOfMoving` の一部 | `NeedState` |
| 成長・年齢 | `birthAge`, `ageState`, `babyLimit`, `childLimit`, `bFirstGround`, `bFirstEatStalk` | `GrowthState` |
| 感情・精神 | `happiness`, `stress`, `panicType`, `pain`, `trauma`, `memories`, `angry/scare/sad` 系の期間とフラグ | `MindState` |
| 社会関係 | `partner`, `parents`, `childrenList`, `elderSisterList`, `sisterList`, `ancestorList`, `fatherRaper`, `bodyRank`, `publicRank`, `lovePlayer`, `intelligence`, `attitude` | `RelationState` / `SocialEntity` |
| 生殖 | `hasBaby`, `hasStalk`, `pregnantPeriod`, `pregnantPeriodOrg`, `pregnantPeriodBoost`, `babyTypes`, `stalkBabyTypes`, `stalks`, `bindStalk`, `birth`, `unBirth`, `forceBirthMessage` | `ReproductionState` |
| 行動・イベント | `staying`, `shitting`, `furifuri`, `strike`, `eating`, `peropero`, `sukkiri`, `scare`, `eatingShit`, `silent`, `shutmouth`, `nobinobi`, `beVain`, `pikopiko`, `purupuru`, `callingParents`, `yunnyaa`, `begging`, `playing`, `playingLimit`, `staycount`, `stayTime`, `moveTarget`, `targetPosOfsX/Y`, `targetBind`, `currentEvent`, `eventList`, `eventResultAction` | `ActionState` |
| 表現・表示 | `bodyNameSet`, `bodySpriteSet`, `forceFace`, `msgType`, `shitType`, `pin`, `dropShadow`, `messageLineColor`, `messageBoxColor`, `messageTextColor`, `messageWindowStroke`, `messageTextSize`, `messageBuf`, `messageCount`, `canTalk`, `bImageNagasiMode`, `uniqueID` | `PresentationState` |
| 所持品・付属物 | `attach`, `favItem`, `takeoutItem`, `bodyAmount`, `Ycost`, `saleValue`, `numOfAnts` | `InventoryState` |
| 環境リンク・接触 | `bOnDontMoveBeltconveyor`, `bNoDamageNextFall`, `bNeedled`, `fixBack`, `lockmove`, `lockmovePeriod`, `pullAndPush`, `extForce`, `baryState`, `taken`, `bSurisuriFromPlayer`, `bPurupuru` | `WorldContactState` |
| 種族判定・タグ | `rareType`, `likeBitterFood`, `likeHotFood`, `likeWater`, `flyingType`, `braidType`, `predatorType`, `rapist`, `superRapist`, `canFly`, `isRude` 系判定 | `Yukkuri` 固有の種族ヘルパー / `Profile` |

### Yukkuri.java に残す責務

`Yukkuri` が持つのは、次の3つに絞る。

| 責務 | 中身 |
|---|---|
| ゆっくり固有の統合 | `state` オブジェクト同士をつなぐ、優先順位を決める、相互作用を調停する |
| 見た目の差分 | スプライト、名前、変身、種族判定、描画時のゆっくり固有フック |
| ゆっくり固有のAI差分 | `killTime()` 相当の固有行動、捕食・主従・ハイブリッド判定 |

この分離ができると、`Yukkuri.java` は「全状態を抱えた神クラス」ではなく、
**state を束ねる薄いオーケストレータ** になる。

### state の配置ルール

| 層 | 持つ state の例 |
|---|---|
| `LivingEntity` | `VitalState`, `NeedState`, `GrowthState`, `MindState`, `WorldContactState` |
| `SocialEntity` | `RelationState`, `ReproductionState`, `ActionState` の社会寄り部分 |
| `Yukkuri` | `PresentationState` と、ゆっくり固有の見た目・変身・種族フック |

要点は、`〜State` は「1個の巨大な `Yukkuri` フィールド群」ではなく、
**各継承層に分散した、まとまりごとのフィールド** だということ。

### 参照方向の原則

各 state は、**自分か自分より上の層だけ** を参照してよい。
下位層を直接参照すると、責務が逆流して再び神クラス化する。

| 層 | 参照してよいもの |
|---|---|
| `LivingEntity` / `state` | `Entity`、`WorldEntity`、`LivingEntity` 自身の state |
| `SocialEntity` / `state` | `LivingEntity` 以上、`SocialEntity` 自身の state |
| `Yukkuri` / `state` | `SocialEntity` 以上、`Yukkuri` 自身の state |

逆方向は禁止する。
たとえば `NeedState` が `Yukkuri` 固有の名前やスプライトを見ない、
`SocialEntity` が `PresentationState` の描画情報に依存しない、という形にする。

### 変身の責務分離

`Yukkuri` は変身を**実行しない**。持つのは「変身できるか」「どの型へ変わるか」の判定だけ。
実際の置換は `src.engine.transform.TransformationService` が担当する。
このサービスは既に実装済みで、`Marisa` と `Reimu` の `execTransform()` は
ここへ委譲している。
`Yukkuri` は `Marisa`, `DosMarisa`, `Reimu`, `Deibu` のような具象クラス名を
参照しない。変身先は `YukkuriType` か、その派生する transform target ID で表現する。

| 責務 | 担当 |
|---|---|
| 変身可能判定 | `Yukkuri` / 具象クラス |
| 変身先IDの決定 | `Yukkuri` / `YukkuriType` |
| 新インスタンス生成 | `BodyFactory` |
| state の移送 | `TransformationPolicy` |
| world registry 差し替え | `TransformationService` |
| UI・選択中参照の更新 | `TransformationService` |

`Yukkuri` から `BodyFactory` や `World` を直接触らない。
これで変身ロジックの逆参照と循環依存を避ける。

`TransformationPolicy` は変身に関する細かなルールをまとめる薄い層で、
現在は予約判定・保存名解決・選択中ボディ判定を持つ。

---

## 8. メソッド配置の原則

> **メソッドは、主として依存するフィールドを持つ最も基底の層に置く。**

| 依存フィールドの所在 | メソッドを置く層 |
|---------------------|----------------|
| Entity のフィールドのみ | Entity |
| WorldEntity のフィールドを含む | WorldEntity |
| LivingEntity / state のフィールドを含む | LivingEntity か state |
| SocialEntity / state のフィールドを含む | SocialEntity か state |
| Yukkuri 固有のフィールドを含む | Yukkuri |

**複数層をまたぐ計算は「結果を下位層フィールドに書き込む」で解決する。**

具体例：`getCollisionX()` の問題

```java
// 現状: Yukkuri層（スプライト）+ LivingEntity層（ageState）を合成
// → 結果が Entity / WorldEntity から参照できない

// 解決策: Yukkuri がスプライト変更・ageState変更時に Entity を更新する
// Yukkuri層:
super.setCollisionSize(
    bodySpr[ageState.ordinal()].getImageW() + getExpandSizeW(),
    bodySpr[ageState.ordinal()].getImageH() + getExpandSizeH()
);
// Entity/WorldEntity層: collisionWidth/Height フィールドを参照するだけでよくなる
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

## 実装進捗

この節は、設計上の理想ではなく「今のコードがどこまで進んだか」を示す。

| 項目 | 状態 | 補足 |
|---|---|---|
| Step 1: 種族判定メソッドの追加 | 完了 | `isPredator()` / `isHybrid()` / `isServantOf()` を導入し、`FoodPredatorCandidatePolicy` / `BodyPartnerSearchRule` / `BodyStealRule` 側の参照を整理済み |
| Step 2: `YukkuriType` 循環参照修正 | 完了 | `YukkuriType` は具象 `yukkuri.*` を直接参照しない。型ID・表示名・正規化ロジックを enum 側へ寄せ済み |
| Step 2.5: 互換ラッパー・Hungarian回収 | 進行中 | `gete...` / `sete...` は大半を回収済み。`b...` / `e...` / `n...` / `s...` / `f...` / `d...` / `an...` も、正規名へ寄せ切ったものから順次削除する |
| `YukkuriUtil` の縮小 | 完了 | 実体の `YukkuriUtil.java` は撤去済み。出生・変身・世界参照の残りは `BodyFactory` / `TransformationService` / `BodyRegistry` / `BabyDnaFactory` 側へ移動済み |
| `Body` の直接依存整理 | 進行中 | `BodyRegistry` 直参照を削り、`BodyRelations` などへ寄せ始めた。まだ `Body` 側に残る直接依存と旧名回収がある |
| `BodyAttributes` の状態分解 | 進行中 | `BodyNameSet` / `BodySpriteSet` / `BodyStatProfile` / `BodyTimingProfile` / `BodyBehaviorProfile` は大幅に分離済み。残りの生の状態フィールドを順に切る |
| Step 3: `src.base` 解体 | **完了** | Effect→src.effect, Attachment→src.attachment, EventPacket→src.event, BodyXxxProfile/BodyNameSet/BodySpriteSet→src.entity.living.profile, Okazari→src.entity.world.bodylinked, FieldShapeBase→src.field.FieldShape, Barrier/Farm/Pool/Beltconveyor→src.field.impl, TerrainBillboard→src.visual, Player→src.meta, ObjEXType→WorldEntityKind 全移動完了。テスト 6988/6988 通過 |
| Step 4: 新継承階層の導入 | 未着手 | `Entity` / `LivingEntity` / `SocialEntity` / `Yukkuri` の実体化はまだ |
| Step 5: パッケージ名変更 | 未着手 | `src.*` の全面置換は最後に回す |

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

### Step 2.5: 互換ラッパー・Hungarian回収（小〜中規模）

**リスク**: 低〜中（呼び出し側の置換漏れがあるとコンパイルエラーになる）  
**効果**: 意味の薄い旧名・互換メソッドが残らず、設計の意図がコードに反映される

Step 2 までで導入した正規名を基準に、旧名の残骸を消す。

1. `gete...` / `sete...` を `get...` / `set...` に統一する
2. `b...` プレフィックスを持つ boolean / state フィールドを `...` に改名する
3. `e...` プレフィックスを持つ enum / state フィールドを `...State` / `...Type` などに改名する
4. `n...` / `s...` / `f...` / `d...` / `an...` など、型や用途を表しているだけで意味を運ばない接頭辞を削る
5. 旧名ラッパーは全呼び出しが消えた時点で削除する
6. テストも旧名呼び出しを残さない

この段階では「設計上の正規名」と「実装上の互換名」を並立させない。  
残すのは移行中の一時的な足場だけで、足場が不要になったら即座に撤去する。

---

### Step 3: src.base / field / visual / meta 解体（中規模）

**リスク**: 中（パッケージ移動で全 import 文が変わる）
**効果**: 循環依存の `base ↔ logic` 等が解消される

1. `Effect`→`src.effect`, `Attachment`→`src.attachment`, `EventPacket`→`src.event` に移動
2. `Okazari`→`src.entity.world.bodylinked`, `BodyXxxProfile`→`src.entity.living.profile` に移動
3. `FieldShapeBase`→`src.field.FieldShape` に改名し、`Barrier` / `Farm` / `Pool` / `Beltconveyor` を `src.field.impl` に移す
4. `TerrainBillboard`→`src.visual`、`Player`→`src.meta` に移す
5. `src.base` が `Obj`/`BodyAttributes`/`Body` だけになる
6. `ObjEXType` は `WorldEntityKind` へ改名し、`classPack` の保持先を factory / registry に寄せる

---

### Step 4: 継承階層の新設（大規模）

**リスク**: 高（全ファイルに影響）  
**効果**: 根本的な設計改善

**基本原則**
- 外部からは `body.getHoge()` という API 呼び出しがほとんど。内部継承関係が変わっても呼び出し側は変更不要
- `BodyAttributes` のフィールド+getter/setter を `SocialEntity` / `LivingEntity` に直接移す（委譲コードを `BodyAttributes` に残さない）
- 移すたびに `BodyAttributes` が減る。全部終われば `BodyAttributes` は空になり削除できる
- `Yukkuri`（旧 Body）は `BodyAttributes` 消滅後に `extends SocialEntity` に直す
- `Yukkuri` 自身のメソッドも、中身を見て `SocialEntity` / `LivingEntity` に振り分けられるものは移す

**フィールド分配の進め方（反省を踏まえたルール）**
- POJO（YukkuriRelation 等）＋BodyAttributes に委譲コードを残すパターンは NG：委譲コードがいつまでも残り、BodyAttributes が消えない
- フィールドは直接 `SocialEntity` / `LivingEntity` に定義し、getter/setter もそこに置く
- `BodyAttributes` にはその層固有のコードだけを残す（移行期の一時的な滞在は許容）

**現在の進め方（`YukkuriRelation` / `YukkuriInventory` POJO がある場合）**
- `yukkuriRelation` フィールド + 関係 getter/setter → `SocialEntity` に移動
- `yukkuriInventory` フィールド + 在庫 getter/setter → `LivingEntity` に移動
- `BodyAttributes` の委譲メソッドを削除（継承で解決されるため呼び出し側変更なし）
- `YukkuriRelation.java` / `YukkuriInventory.java` は削除

**完了条件**
1. `LivingEntity`, `SocialEntity` クラスを `src.entity.living` に新設 ✅
2. `Obj` → `Entity`、`ObjEX` → `WorldEntity`、`Body` → `Yukkuri` リネーム ✅
3. `BodyAttributes` のフィールドを `SocialEntity` / `LivingEntity` に移しきる（YukkuriRelation/YukkuriInventory 含む）
4. `BodyAttributes` が空になり削除、`Yukkuri extends SocialEntity` に変更
5. `WorldEntity` 配下を `StaticEntity` / `MobileEntity` / `BodyLinkedEntity` に分割する
6. `changeBody()` をリフレクション廃止に書き直し
7. `collisionWidth/Height` を Entity か WorldEntity のどちらに置くべきか再検討し、`getCollisionX/Y()` を廃止

---

### Step 6: BodyAttributes 削除（大規模）

**リスク**: 中（メソッド移動のみで呼び出し側変更不要。static フィールドのみ43ファイル参照変更が必要）
**効果**: 4000行の神クラスが消え、継承ツリーが `Entity→LivingEntity→SocialEntity→Yukkuri` に整理される

**方針**
- メソッドは「主に依存するフィールドを持つ最も基底の層」へ移動（§8 原則）
- 移動後、BodyAttributes の当該メソッドを削除するだけ。継承で解決されるため呼び出し側変更不要
- Rule 呼び出しメソッド（isDead/isHasBraid 等）はゆっくり固有処理を含むため Yukkuri に移動
- @JsonIgnore / @JsonProperty アノテーションは移動先で維持

**Step 6-1: LivingEntity へのメソッド移動（~40メソッド）**

| メソッド群 | 根拠 |
|----------|------|
| isFull / isHungry / addHungry / isTooHungry 等 hungry 系 | hungry フィールドが LivingEntity |
| isAdult / isChild / isBaby | ageState フィールドが LivingEntity |
| addStress / isStressful / getStressLimit 等 stress 系 | stress フィールドが LivingEntity |
| getDamageLimit | damage フィールドが LivingEntity |
| isSick / isSickHeavily / forceSetSick 等 sick 系 | sickPeriod フィールドが LivingEntity |
| isNYD / isNotNYD | coreAnkoState フィールドが LivingEntity |
| addTang / getTangType | tang フィールドが LivingEntity |
| getBodyBakeLevel / isGotBurned / addBodyBakePeriod 等 bake 系 | bodyBakePeriod が LivingEntity |
| addFootBakePeriod / getFootBakeLevel | footBakePeriod が LivingEntity |
| addAttachment / removeAttachment / getAttachmentSize / resetAttachmentBoundary | attach が LivingEntity |
| getFavoriteItem / setFavoriteItem / removeFavoriteItem | favoriteItems が LivingEntity |
| getCarryItem / removeCarryItem | carryItems が LivingEntity |
| isOld / isTalking / getEatAmount | age / messageCount / eatAmount が LivingEntity |

**Step 6-2: SocialEntity へのメソッド移動（~20メソッド）**

| メソッド群 | 根拠 |
|----------|------|
| setHappiness（内部ロジックあり） | happiness フィールドが SocialEntity |
| addLovePlayer | lovePlayer フィールドが SocialEntity |
| addMemories / hasTrauma | memories フィールドが SocialEntity |
| addAmaamaDiscipline / isStubbornlyDirty | amaamaDiscipline が SocialEntity |
| addChildrenList / removeChildrenList 等リスト操作系 | childrenList 等が SocialEntity |

**Step 6-3: Yukkuri へのメソッド移動（~130メソッド）**

- abstract メソッド 31個（getType / getNameJ / getImage / tuneParameters 等）
- copyBodyNameSetFrom / copyBodySpriteSetFrom
- Rule 呼び出しメソッド ~40個（isDead / isHasBraid / isRelax 等）
- ゆっくり固有ビジネスロジック（isPealed / isPacked / isFurifuri / isVain 等）
- スプライト系（getCollisionX/Y / getBodyBaseSpr / getBraidSprite 等）
- @JsonTypeInfo クラスアノテーション
- @JsonProperty override（getDamage / isSleeping / setDamage 等）
- equals / hashCode / compareTo

**Step 6-4: static フィールドを Renderer へ移動**

- `shadowImages[]`, `shadowImgW/H[]`, `shadowPivX/Y[]` → `Renderer` へ移動
- `UNYOSTRENGTH[]` → `Yukkuri` 定数として残す
- 43ファイルの `BodyAttributes.shadowImages` 等参照を `Renderer.getShadowImages()` 等に書き換え

**Step 6-5: BodyAttributes 削除・継承変更**

1. `Yukkuri.java`: `extends BodyAttributes` → `extends SocialEntity` に変更
2. `BodyAttributes.java` を削除
3. `StubBodyAttributes` / `PlainBodyAttributes` 等のテストスタブを `Yukkuri` か `SocialEntity` を継承するよう修正
4. コンパイル・テスト（6986 passed / 0 failed）を確認

---

### Step 6.5: Rule クラス・Raw getter 整理（完了）

**経緯**
Step 6 完了後、Yukkuri.java には Rule クラスへ処理を委譲するだけのオーバーライドが大量に残っていた。
これらは「Yukkuri → Rule → Raw getter → フィールド」という3段間接参照を形成しており、
Rule クラス側のメソッドをインライン化することで Rule クラスが死コード化した。

**削除した Rule クラス（34ファイル）**
- 第1陣（6）: `BodyBurstRule`, `BodyDamageRule`, `BodyControlRule`, `BodyPresentationRule`, `BodyExpressionRule`, `BodyDisplayRule`
- 第2陣（28）: `BodyActionStateRule`, `BodyActivityRule`, `BodyAgeCategoryRule`, `BodyAgeRule`, `BodyAnimalRule`, `BodyAppearanceRule`, `BodyAttitudeRule`, `BodyBehaviorRule`, `BodyBirthRule`, `BodyBurnRule`, `BodyConditionRule`, `BodyDependencyRule`, `BodyFallRule`, `BodyFlagRule`, `BodyHungerRule`, `BodyHungerStateRule`, `BodyMoodRule`, `BodyMovementGoalRule`, `BodyPreferenceRule`, `BodySpecialTypeRule`, `BodySpeechRule`, `BodyStatRule`, `BodyStructureRule`, `BodyStyleRule`, `BodyTimingRule`, `BodyTraitRule` + 2（一時削除後に復元: `BodySelectionRule`, `BodySurisuriRule`）

**残存 Rule クラス（21ファイル）**
実際のロジックを持つもののみ残した:
`BodyApproachRule`, `BodyContactEffectRule`, `BodyContactRule`, `BodyCoreStateRule`, `BodyDeadActionRule`, `BodyDeadSearchRule`, `BodyExcitementRule`, `BodyExcretionRule`, `BodyFoundAffinityRule`, `BodyGatheringRule`, `BodyIllnessRule`, `BodyNeedleRule`, `BodyParentRule`, `BodyPartnerActionRule`, `BodyPartnerEntryRule`, `BodyPartnerSearchRule`, `BodySelectionRule`, `BodySkinshipRule`, `BodyStealRule`, `BodyStressRule`, `BodyWakeupRule`

**削除した Raw getter（74本）**
`isXxxRaw()` / `getXxxRaw()` 形式の bare-field accessor で、呼び出し元の Rule クラスが削除されたため不要になったもの。
残存 Raw getter（12本）: `getDamageRaw`, `isCanTalkRaw`, `isDirtyRaw`, `isDropShadowRaw`, `isImageNagasiModeRaw`, `isNewbornRaw`, `isOnDontMoveBeltconveyorRaw`, `isPinRaw`, `isShakePhaseRaw`, `isSleepingRaw`, `isSurisuriFromPlayerRaw`, `isTargetBindRaw`

**Yukkuri.java でのインライン化（~30メソッド）**
Rule 委譲 override を直接式に書き換えた例:
```java
// Before: return BodyBurstRule.isBurst(this);
// After:
public boolean isBurst() { return getBurstState() == Burst.BURST; }
```

---

### Step 5: パッケージ名変更（長期）

**リスク**: 最高（全ファイルの package 宣言と import 文）
**効果**: Java 標準命名規則への準拠

`src.*` → `com.example.simyukkuri.*`（または適切なドメイン名）

一括置換で機械的にできるが、テストを含む全ファイルが対象。

---

## 設計上の完了状況

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
| **Step 3**: src.base 解体 | **完了** |
| **Step 4**: 継承階層の新設 | **進行中** |
| └ LivingEntity / SocialEntity を src.entity.living に新設・BodyAttributesの親をSocialEntityに変更 | 完了 |
| └ ObjEX→WorldEntity リネーム・getObjEXType→getWorldEntityType | 完了 |
| └ Obj→Entity リネーム（src/base/Entity.java）・全ファイル置換 | 完了 |
| └ BodyAttributes のフィールド分配（SocialEntity/LivingEntity への直接移動） | **完了** |
| └ &nbsp;&nbsp;YukkuriRelation → SocialEntity 移動・委譲コード削除 | 完了 |
| └ &nbsp;&nbsp;YukkuriInventory → LivingEntity 移動・委譲コード削除 | 完了 |
| └ &nbsp;&nbsp;全フィールドを SocialEntity / LivingEntity に移動、5プロファイルクラス削除 | 完了 |
| └ &nbsp;&nbsp;copyStateTo チェーン実装（Entity→LivingEntity→SocialEntity→BodyAttributes→Yukkuri） | 完了 |
| └ &nbsp;&nbsp;changeBody() リフレクション廃止 → copyStateTo チェーンに置換 | 完了 |
| └ &nbsp;&nbsp;BodyAttributes 削除・Yukkuri extends SocialEntity に変更 | **→ Step 6** |
| └ WorldEntity配下を StaticEntity/MobileEntity/BodyLinkedEntity に分割 | **未着手** |
| └ Body→Yukkuri リネーム (371ファイル影響) | 完了 |
| **Step 5**: パッケージ名変更 | **未着手** |
| **Step 6**: BodyAttributes 削除 | **完了** |
| └ Step 6-1: LivingEntity へのメソッド移動（hungry/stress/damage/sick/bake/attachment 系 ~40メソッド） | **完了** |
| └ Step 6-2: SocialEntity へのメソッド移動（happiness/lovePlayer/memories/childrenList 系 ~20メソッド） | **完了** |
| └ Step 6-3: Yukkuri へのメソッド移動（abstract 31個 + Rule 系 ~40 + ゆっくり固有 ~60） | **完了** |
| └ Step 6-4: static フィールド（shadowImages 等）を Renderer へ移動、43ファイル参照書き換え | **完了** |
| └ Step 6-5: BodyAttributes.java 削除・Yukkuri extends SocialEntity に変更・テストスタブ修正 | **完了** |
| **Step 6.5**: Rule クラス・Raw getter 整理 | **完了** |
| └ 死コード Rule クラス 34ファイル削除（BodyActionStateRule 等。src/logic/ に残存 21クラス） | **完了** |
| └ Raw getter 74本削除（`isXxxRaw()` 形式。呼び出し元の Rule が消えたため不要に） | **完了** |
| └ Yukkuri.java の Rule-delegate override を直接式にインライン化（~30メソッド） | **完了** |
| └ 37テストファイル削除（削除した Rule クラスに対応するテスト） | **完了** |
| └ テスト基準: 6988 → 6935 successful / 0 failed | **完了** |

---

## Step 7: LivingEntity の責務純化（現在の課題）

Delegate パターン導入によって `Yukkuri.java` は 9,396行 → 3,649行 に削減された。
しかし `LivingEntity.java`（5,068行）に「ゆっくり固有」の概念が多数混入している。

### 発見した問題一覧

| # | 問題 | 深刻度 | 対処 |
|---|-----|--------|------|
| 7-1 | LivingEntity に種族固有フィールド混入 | 高 | Yukkuri へ移動 |
| 7-2 | LivingEntity に ゆっくり固有 abstract メソッド混入 | 高 | Yukkuri / SocialEntity へ移動 |
| 7-3 | `exciting` / `forceExciting` が LivingEntity にある | 中 | SocialEntity へ移動 |
| 7-4 | `setMessage` 系の3段間接呼び出し | 低 | YukkuriMessage の要否を再検討 |
| 7-5 | Delegate lazy init にスレッドセーフティなし | 低 | シングルスレッド前提をコメント明記 |

---

### Step 7-1: 種族固有フィールドを LivingEntity から Yukkuri へ移動

**対象フィールド（LivingEntity → Yukkuri）**

| フィールド | 理由 |
|-----------|------|
| `braidType` | 「お下げ・羽・尻尾を持つ種族か」→ ゆっくり固有の見た目 |
| `flyingType` | 「空を飛ぶ種族か」→ ゆっくり固有の種族特性 |
| `predatorType` | 捕食者タイプ → ゆっくり固有 |
| `rareType` | 希少種か → ゆっくり固有 |
| `likeBitterFood` | 食の好み → ゆっくり固有の嗜好 |
| `likeHotFood` | 同上 |
| `likeWater` | 同上 |
| `hasPants` | パンツ → ゆっくり固有の装備状態 |
| `analClose` | ゆっくり固有の身体的状態 |
| `bodyCastration` | 去勢 → ゆっくり固有 |
| `stalkCastration` | 同上 |
| `penipeniCutted` | ゆっくり固有の身体的特徴 |

**完了条件**: `grep "braidType\|flyingType\|predatorType" LivingEntity.java` が 0件になること

---

### Step 7-2: ゆっくり固有 abstract メソッドを LivingEntity から追い出す

**setMessage 系 13個 → Yukkuri 直接実装（abstract 廃止）**

`setMessage(String)` 等は「ゆっくりがしゃべる」UI 処理で、生物一般には不要。
現在は `LivingEntity(abstract) → Yukkuri(override) → messageDelegate → BodyEventState` の4段。
`LivingEntity` の abstract 宣言を削除し、`Yukkuri` に直接 `public` 実装を持てばよい。
`LivingEntity` 内の呼び出し箇所は、呼び出す理由が正当か再検討すること。

**その他の abstract メソッド（配置が間違っている）**

| メソッド | 正しい層 | 理由 |
|---------|---------|------|
| `getPublicRank()` | SocialEntity | `publicRank` フィールドが SocialEntity にある |
| `isRaper()` | SocialEntity | `rapist` フィールドが SocialEntity にある |
| `setForceFace(int)` | Yukkuri | スプライト制御 = ゆっくり固有 |
| `makeDirty(boolean)` | Yukkuri | ゆっくり固有の外見変化 |
| `setPeropero(boolean)` | Yukkuri | ゆっくり固有の行動フラグ |
| `willingFurifuri()` | Yukkuri | ゆっくり固有の感情表現 |
| `changeUnyo(int,int,int)` | Yukkuri | うんうん = ゆっくり固有 |
| `changeReUnyo()` | Yukkuri | 同上 |
| `isUnyoActionAll()` | Yukkuri | 同上 |

---

### Step 7-3: exciting / forceExciting を SocialEntity へ移動

```java
// 現状 LivingEntity にある
protected boolean exciting = false;
protected boolean forceExciting = false;
```

「性的興奮」は他者との社会的関係・刺激から生じる状態。`SocialEntity` に属する。

---

### Step 7-4: YukkuriMessage の中間層を評価する

```
現状: Yukkuri → YukkuriMessage → BodyEventState
```

`YukkuriMessage` が `BodyEventState` への単純転送しかしていないなら、
`Yukkuri.setMessage()` が直接 `BodyEventState` を呼べばよく、`YukkuriMessage` は削除できる。
ただしカラー設定などの付加ロジックがある場合は残す。

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
