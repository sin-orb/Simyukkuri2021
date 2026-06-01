# TEST_EXPANTION_PLAN

カバレッジ計測日: 2026-06-01  
テスト数: 5789 passed / 1 skipped  
対象: GUI・描画・リソースローダーを除いた非GUIロジック

---

## 現状サマリ

| 対象 | Instruction | Branch | miss (inst) | miss (br) |
|---|---|---|---|---|
| **非GUIクラス合計** | **66.2%** | **31.3%** | **73,233** | **11,292** |
| GUI/描画除外分 | — | — | 11,072 | 1,003 |

Branch カバレッジが全体的に低い。Instruction は 66% あるが、Branch が 31% に留まっており条件分岐の大部分が未検証。

---

## パッケージ別カバレッジ

| パッケージ | Inst% | Branch% | Inst miss | Branch miss | 備考 |
|---|---|---|---|---|---|
| `enums` | 98.6% | 54.2% | 43 | 33 | ほぼ完了 |
| `entity (base)` | 92.1% | 62.4% | 7,103 | 2,024 | LivingEntity/SocialEntity 層は良好。残りは Body.java の複合条件 |
| `attachment` | 81.4% | 78.9% | 2,192 | 81 | 良好 |
| `system` | 68.9% | 49.1% | 3,152 | 275 | WorldState 等は高い。LoggerYukkuri が低い |
| `util` | 59.1% | 40.4% | 2,509 | 311 | YukkuriUtil は改善中 |
| `yukkuri` | 67.2% | 18.9% | 10,539 | 1,077 | 各キャラクタクラスの killTime/draw 等が未カバー |
| `event` ⚠ | 37.4% | 11.9% | 8,068 | 1,506 | イベント execute/start ロジックがほぼ未テスト |
| `org.simyukkuri` (root) ⚠ | 25.9% | 3.2% | 2,631 | 273 | SimYukkuri 本体・マウス処理が headless 不可 |
| `world.mobile` ⚠ | 35.8% | 10.2% | 2,106 | 203 | Shit/Stalk/Vomit の clockTick ロジック |
| `logic` ⚠ | 28.7% | 14.5% | 10,969 | 2,590 | FoodLogic・BodyLogic が巨大かつ低カバレッジ |
| `command` ⚠ | 11.4% | 2.2% | 4,184 | 785 | GadgetAction は描画依存で headless 困難 |
| `world.item` ⚠ | 17.1% | 1.1% | 19,231 | 2,128 | Branch ほぼ 0%。アイテムロジックが大量未テスト |
| `effect` ⚠ | 0.0% | 0.0% | 506 | 6 | GarbageStation など未着手 |

---

## クラス別ワーストリスト（Inst miss 上位）

| クラス | Inst% | Branch% | Inst miss | Branch miss |
|---|---|---|---|---|
| `entity.Body` | 69.7% | 58.1% | 6,045 | 1,696 |
| `logic.FoodLogic` | 8.6% | 6.0% | 4,441 | 968 |
| `logic.BodyLogic` | 4.7% | 3.9% | 3,866 | 999 |
| `world.item.Yunba` | 2.6% | 0% | 3,669 | 512 |
| `command.GadgetAction` | 0% | 0% | 3,135 | 714 |
| `yukkuri.Marisa` | 19.5% | 4.3% | 1,833 | 289 |
| `util.BodyUtil` | 0% | 0% | 1,722 | 84 |
| `system.LoggerYukkuri` | 12.8% | 13% | 1,608 | 114 |
| `world.item.BeltconveyorObj` | 5.6% | 0% | 1,594 | 171 |
| `yukkuri.Reimu` | 25.4% | 11.5% | 1,530 | 246 |
| `world.item.Beltconveyor` | 3.0% | 0% | 1,221 | 82 |
| `world.item.ProcesserPlate` | 3.6% | 0% | 1,144 | 213 |
| `world.item.Pool` | 2.2% | 0% | 1,079 | 167 |
| `event.SuperEatingTimeEvent` | 9.3% | 4.9% | 852 | 154 |
| `world.item.Farm` | 1.5% | 0% | 905 | 119 |
| `event.ProposeEvent` | 7.2% | 8.6% | 834 | 117 |
| `world.item.FoodMaker` | 49.7% | 0% | 828 | 116 |
| `world.item.BreedingPool` | 9.8% | 0% | 822 | 160 |
| `event.FuneralEvent` | 10.6% | 5.5% | 781 | 155 |
| `event.ShitExercisesEvent` | 9.9% | 3.8% | 776 | 153 |

---

## 拡張計画

### 優先度 A — ロジック中核・miss 量最大

#### `logic.FoodLogic` (miss=4,441, 8.6%)
- テスト数は 711 あるが、クラスが極めて巨大（checkFood L1〜L2000+）
- 未カバーの主な領域:
  - `checkFood` の中盤〜後半分岐（L400-L600 帯）
  - predator 系の複合条件（Fran/Remirya との相互作用）
  - stalk の plantYukkuri 関連フロー
  - `eatFood` の各種フードタイプ処理
- アプローチ: checkFood の残り missed branch を JaCoCo HTML で個別確認し、条件を絞った短いテストを追加

#### `logic.BodyLogic` (miss=3,866, 4.7%)
- テスト数は 455 だが、クラスが 10,000 行超
- 未カバーの主な領域:
  - `checkPartner` の後半（L300-L600 帯）
  - `doActionOther` の各種 reaction branch
  - `gatheringYukkuri*` の複数体インタラクション
- アプローチ: BedLogicTest パターンを参考に、具体的な状態変化を assert するテストを追加

#### `entity.Body` (miss=6,045, 69.7%)
- カバレッジは 70% と高く見えるが、miss 量が最大
- 残りは複合的な状態フラグ組み合わせ（`canAction`, `isDontMove` の境界条件）
- BodyLogicTest の checkPartner 等でカバーが進む見込み

---

### 優先度 B — world.item 群（Branch ≒ 0%）

アイテムクラスの Branch が壊滅的（0〜5%）。clockTick/checkHitObj/objHitProcess の条件分岐がほぼ未テスト。

| クラス | 主な未カバー領域 |
|---|---|
| `Yunba` (miss=3,669) | clockTick 全域、飛行・落下ロジック |
| `BeltconveyorObj` (miss=1,594) | ベルト搬送の方向・速度分岐 |
| `Beltconveyor` (miss=1,221) | フィールド判定、搬送先選択 |
| `ProcesserPlate` (miss=1,144) | 加工種別ロジック |
| `Pool` (miss=1,079) | 水位・ゆっくり入水判定 |
| `Farm` (miss=905) | 収穫条件、肥料吸収 |
| `BreedingPool` (miss=822) | 繁殖条件分岐 |
| `FoodMaker` (miss=828) | 食料生成ロジック（Branch 0%） |
| `Toilet` (miss=517) | しぶき着地判定 |

- アプローチ: 各アイテムの `clockTick` を WorldTestHelper でセットアップしてから呼び出し、戻り値と状態変化を assert する。headless 環境でも動作可能な部分から開始。

---

### 優先度 C — event パッケージ（37.4% / 11.9%）

イベントの `execute` / `start` / `checkEventResponse` の内部ロジックがほぼ未テスト。

| クラス | miss | 主な未カバー |
|---|---|---|
| `SuperEatingTimeEvent` | 852 | execute の満腹・空腹分岐 |
| `ProposeEvent` | 834 | 求愛成否・拒否分岐 |
| `FuneralEvent` | 781 | 喪失反応の種別分岐 |
| `ShitExercisesEvent` | 776 | 排泄練習の成否 |
| `PredatorsGameEvent` | 683 | 捕食ゲームの各フェーズ |
| `RaperReactionEvent` | 595 | 強制交配の反応 |
| `ProudChildEvent` | 589 | 子育て誇示の各種条件 |
| `AvoidMoldEvent` | 536 | カビ回避ロジック |

- アプローチ: EventLogicTest に `execute()` を呼び出して状態変化を assert するシナリオテストを追加。EventPacket の匿名サブクラス実装パターンを活用。

---

### 優先度 D — headless 困難・対応方針を要検討

| クラス / パッケージ | 理由 |
|---|---|
| `command.GadgetAction` (0%) | 画面操作・座標変換依存 |
| `util.BodyUtil` (0%) | リフレクション依存の変換処理 |
| `SimYukkuri.MyMouseListener` (0%) | AWT イベント依存 |
| `system.LoggerYukkuri` (12.8%) | ファイル I/O + GUI ロガー |
| `effect.*` (0%) | エフェクト描画依存 |

- これらは headless 環境でのテストが根本的に困難。インターフェース抽出や純粋ロジックの分離リファクタリングが前提となる。

---

## 数値目標（参考）

| フェーズ | 目標 Inst% | 目標 Branch% | 主な対象 |
|---|---|---|---|
| 現状 | 66.2% | 31.3% | — |
| Phase 1 | 72% | 38% | FoodLogic + BodyLogic の残り missed branch |
| Phase 2 | 78% | 45% | world.item 群の clockTick |
| Phase 3 | 82% | 50% | event 群の execute/start |
| 目標上限 | ~85% | ~55% | headless 困難クラスを除いたほぼ全域 |

headless 不可クラス（GadgetAction 等）を除くと、実質的な上限は Inst ~88% / Branch ~60% 程度と推定。
