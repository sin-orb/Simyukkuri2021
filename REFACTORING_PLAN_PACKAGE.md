# Package Refactoring Plan

この文書は、現状のパッケージ構成を「役割」と「実装の置き場所」で整理するための案です。
互換性維持を目的にせず、意味の通る名前と責務の分離を優先します。

## 現在の大枠

- `org.simyukkuri.entity.core`
  - ゲーム内実体の基底と、その派生
- `org.simyukkuri.field`
  - フィールド形状、地形、設置物
- `org.simyukkuri.logic`
  - 判定、ルール、ポリシー
- `org.simyukkuri.event`
  - イベント本体
- `org.simyukkuri.command`
  - コマンド定義とコマンド実行
- `org.simyukkuri.draw`
  - 描画、座標変換、描画補助
- `org.simyukkuri.system`
  - 入力、UI 部品、共通設定、メッセージ、ログ、保存周辺の基盤
- `org.simyukkuri.visual`
  - 背景や装飾の視覚部品
- `org.simyukkuri.enums`
  - 列挙型、定数
- `org.simyukkuri.util`
  - 汎用補助

## 役割定義

### `org.simyukkuri.command`

役割:
- ユーザー操作をコマンドとして解釈する
- コマンドメニューを構築する
- コマンドの実行先を振り分ける

残すべきもの:
- `GadgetAction`
- `GadgetAmpouleAction`
- `GadgetBodyAction`
- `GadgetCleanupAction`
- `GadgetDebugAction`
- `GadgetFieldPlacementAction`
- `GadgetItemSetupAction`
- `GadgetMenu`
- `GadgetMenuPopup`
- `GadgetMenuPopupAction`
- `GadgetTool`
- `GadgetToolAction`
- `YukkuriMethodDispatcher`

ズレているもの:
- `ShowStatusFrame`
  - 表示用 JFrame であり、コマンド実行ではない

移動先案:
- `org.simyukkuri.ui`

### `org.simyukkuri.draw`

役割:
- 描画順序
- 描画座標変換
- 描画そのもの

残すべきもの:
- `MyPane`
- `ObjDrawComp`
- `Renderer`
- `Translate`
- `TerrainField`
- `Color4y`
- `Dimension4y`
- `Point4y`
- `Rectangle4y`

ズレているもの:
- `GameLoop`
- `ImageLoadService`
- `ModLoader`
- `SaveDataCodec`
- `World`
- `YukkuriFactory`
- `YukkuriTickProcessor`
- `Terrarium*`

移動先案:
- `org.simyukkuri.engine`

### `org.simyukkuri.system`

役割:
- 入力イベント
- ローカル設定
- メッセージ
- ログ
- 共通 UI 補助
- 共通状態

残すべきもの:
- `IniFileReader`
- `ResourceUtil`
- `LoggerYukkuri`
- `MessagePool`
- `MessageMap`
- `Cash`
- `FrameRate`
- `IconPool`
- `BasicStrokeEX`
- `CustomLogFormatter`
- `MapPlaceData`
- `Sprite`
- `YukkuriLayer`

ズレているもの:
- `MapPlaceData`
  - 保存データ兼ワールド状態であり、UI 基盤ではない
- `Sprite`
  - 描画情報であり、`draw` か専用描画補助向け
- `YukkuriLayer`
  - 描画補助寄りで、`draw` か専用補助向け
- `ButtonListener`
- `GameSpeedComboBoxListener`
- `MainItemComboBoxListener`
- `SubItemComboBoxListener`
- `OptionMenuListener`
- `OptionPopupListener`
- `InputController`
- `ItemGetMenuAction`
- `ItemPopupNoopAction`
- `ItemPopupSpeedAction`
- `ItemShapeMenuAction`
- `ItemUseMenuAction`
- `ItemWindow`
- `LoadWindow`
- `MainCommandUI`
- `MainCommandSelection`
- `MapWindow`
- `MouseInputController`
- `YukkuriFilterPanel`

移動先案:
- `org.simyukkuri.ui`
- `org.simyukkuri.ui.listener`

### `org.simyukkuri.visual`

役割:
- 背景・装飾・視覚部品

残すべきもの:
- `TerrainBillboard`

補足:
- `TerrainBillboard` は `visual` に置くのは妥当だが、`Entity` 継承は実装都合が強い

### `org.simyukkuri.entity.core`

役割:
- ゲーム内実体の基底

残すべきもの:
- `Entity`
- `WorldEntity`
- `LivingEntity`
- `SocialEntity`
- `Yukkuri` 系
- `Attachment` 系
- `Effect` 系
- `meta`
- `world`
- `living`
- `bodylinked`

評価:
- 大枠は妥当
- delegate 分割後も、責務境界は概ね自然

### `org.simyukkuri.field`

役割:
- 地形、境界、設置形状

残すべきもの:
- `FieldShape`
- `Barrier`
- `Farm`
- `Pool`
- `Beltconveyor`
- `FieldShapeBase` 相当

評価:
- 現行の責務は概ね妥当

### `org.simyukkuri.logic`

役割:
- 判定
- ルール
- ポリシー

残すべきもの:
- `Body*Rule`
- `Food*Policy`
- `FamilyActionLogic`
- `ToiletLogic`
- `BedLogic`
- `BadgeLogic`
- `EventLogic`
- `YukkuriLogic`

評価:
- ここは妥当
- 純ロジックが増えるほど価値が出る

### `org.simyukkuri.event`

役割:
- イベント本体

残すべきもの:
- `*Event`

評価:
- イベントが増える前提なら、この粒度でよい

### `org.simyukkuri.enums`

役割:
- 列挙型
- 定数

残すべきもの:
- 各種 enum / 定数

評価:
- 妥当

### `org.simyukkuri.util`

役割:
- 汎用補助

残すべきもの:
- `GameRandom`
- `GameText`
- `GameView`
- `GameWorld`
- `GameEnvironment`
- `GameImages`
- `IniFileUtil`
- `ListUtil`

評価:
- ここは本当に汎用のものだけに縮める

## 切り分け優先度

### 優先度 A

- `command` から `ShowStatusFrame` を外す
- `draw` から `World` / `SaveDataCodec` / `GameLoop` / `Terrarium*` を外す
- `system` から `ItemWindow` / `LoadWindow` / `MapWindow` / `MainCommandUI` / `MouseInputController` を外す

### 優先度 B

- `system.Sprite` と `system.YukkuriLayer` の置き場所を再検討する
- `visual` の `TerrainBillboard` の `Entity` 継承を再検討する

### 優先度 C

- `util` を本当に汎用補助だけに縮める

## 方針

- クラス名や公開 API の意味を優先する
- UI フレームとコマンド実行は分ける
- 描画とゲーム進行は分ける
- 互換ラッパーは増やさない
- 役割に合わないパッケージは、名前ではなく実態で移す

