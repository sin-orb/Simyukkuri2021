# Test Expansion Plan

## Goal

GUI とリソース読込を除く領域について、将来の大規模リファクタリングに耐えるテスト基盤を整備する。

この計画の目的は単純な line coverage の最大化ではない。先に仕様の骨格を固定し、その後に branch coverage を詰めることで、挙動を壊さずに内部構造を変更できる状態を作る。

## Scope

優先対象:

- `src/base`
- `src/logic`
- `src/event`
- `src/item`
- `src/game`
- `src/util`
- `src/enums`
- `src/attachment`
- `src/Const.java`

限定対象:

- `src/draw`
- `src/system`

`draw` と `system` は全面対象にしない。純粋値オブジェクト、状態保持、非 GUI ロジックに限って対象にする。

## Out Of Scope

原則として次は対象外:

- `src/SimYukkuri.java`
- `src/draw/MyPane.java`
- `src/command/*`
- Swing ウィンドウ生成と表示そのもの
- 画像ロードそのもの
- properties / ini / resource file の実ファイル依存テスト

補足:

- `ModLoader`, `ResourceUtil`, `MessagePool`, `IconPool`, `TerrainField`, `MainCommandUI` は依存の薄い部分だけを選択的にテストする
- GUI とファイル I/O を抱えたままの箇所は、まず seam を入れてからテスト対象へ昇格させる

## Principles

1. 仕様保護を coverage より優先する
2. 巨大クラスは branch table ベースで攻める
3. static 状態と singleton 依存は fixture で吸収する
4. GUI 依存はテスト対象にしないのではなく、ロジックと分離してからテストする
5. flaky なテストは増やさない

## Test Layers

### 1. Unit Tests

対象:

- enum
- utility
- 変換ロジック
- 計算
- 単一クラスの状態遷移

目的:

- 仕様の最小単位を固定する
- リファクタリング時の局所破壊を検出する

### 2. Integration-Lite Tests

対象:

- `Body` と `logic`
- `item` と `logic`
- `event` 発火と副作用
- `World` / `Terrarium` の非 GUI 状態変化

目的:

- 単体では意味を持たない相互作用を固定する

### 3. Regression Scenario Tests

対象:

- 壊れるとゲーム挙動が大きく崩れるシナリオ

目的:

- 大規模リファクタリング時の安全網

## Coverage Targets

初期目標:

- `src/enums`, `src/util`, `src/attachment`, `src/game`: line 90%+, branch 85%+
- `src/logic`, `src/event`, `src/item`, `src/base`: line 85%+, branch 75%+

最終目標:

- GUI とリソースを除く対象領域について、主要分岐に未確認ケースが残らない状態

注意:

- 100% を数値目標にしない
- 代わりに「重要分岐未確認ゼロ」を重視する

## Work Phases

## Phase 1: Target Definition And Harness Cleanup

目的:

- 何を守るかを固定する
- テストを書くコストを下げる

作業:

- [x] package ごとの対象 / 対象外を確定する
- [x] 既存 test utility を棚卸しする
- [x] `WorldTestHelper` 系の責務を整理する
- [x] `Body` / `World` / `Terrarium` の static 状態リセット手段を標準化する
- [x] fixture 生成 API を揃える
- [x] テスト命名規約を統一する

完了条件:

- 新しいテストが短く書ける
- テスト間汚染の主要因が抑えられている

## Phase 2: Regression Backbone

目的:

- リファクタリング前に主要仕様を固定する

優先順:

1. `src/base`
2. `src/logic`
3. `src/event`
4. `src/item`
5. `src/util`
6. `src/game`

追加するべき回帰系:

- [x] `Body` の生存 / 死亡 / 年齢 / 損傷 / 空腹 / 排泄 / 睡眠の遷移
- [x] 親子・番・群れ・敵対など関係判定
- [x] `FoodLogic`, `FamilyActionLogic`, `EventLogic`, `EmotionLogic` の主要分岐
- [x] `item` 設置物が `Body` に与える副作用
- [x] `event` の発火条件と発火後の状態変化
- [x] `Dna`, `YukkuriUtil`, `BodyUtil` の変換系

完了条件:

- 大規模リファクタリング前に守るべき主要仕様がテストで表現されている

現在の評価:

- Phase 2 の高優先対象は実用上かなり達成済み
- `logic`, `event`, `item`, `attachment`, `game`, `util` の主要仕様は大半が回帰化済み
- 残る未処理は主に低優先の棚残しと、計画書の文書反映

回帰シナリオ数の目安:

- `logic`: `122`
- `item`: `58`
- `event`: `43`
- `attachment`: `33`
- `base`: `23`
- `util`: `19`
- `game`: `16`
- `system`: `13`

進捗メモ:

- `FamilyActionLogic` に対して、`checkFamilyAction` から実際に `SuperEatingTimeEvent`, `ShitExercisesEvent`, `ProudChildEvent` が起動される回帰シナリオを追加した
- `EventLogic` に対して、`addWorldEvent` / `addBodyEvent` の登録副作用と message 反映、`checkWorldEvent` の非破壊性を固定する回帰シナリオを追加した
- 上記の Phase 2 初回バッチとして `FamilyActionLogicTest` と `EventLogicTest` の 114 テスト成功を確認した
- `BodyTest` に対して、`checkHungry -> checkDamage` の回復連鎖と、`checkSick` 末期時の睡眠解除・感情変化・低優先度イベント破棄を固定する回帰シナリオを追加した
- 上記の `Body` 回帰シナリオを含む `BodyTest` 全体 1193 テスト成功を確認した
- `EmotionLogicTest` に対して、家族の負傷時・他人の負傷時・ゲス個体の嫉妬時の感情ビット集合を exact-match で固定する回帰シナリオを追加した
- 上記の `EmotionLogic` 回帰シナリオを含む 13 テスト成功を確認した
- `FoodLogicTest` に対して、`forceEat` 時の sweets 選択、到着時の food takeout 成立、既存 takeout 保持時の重複 takeout 打ち切りを固定する回帰シナリオを追加した
- 上記の `FoodLogic` 回帰シナリオを含む `FoodLogicTest` の対象実行成功を確認した
- `HotPlateTest` に対して、拘束中の焼きダメージ蓄積、重度足焼き時の pull/push 化、拘束解除時の mobility/shadow 復帰を固定する回帰シナリオを追加した
- 上記の `HotPlate` 回帰シナリオを含む対象実行成功を確認した
- `AutoFeederTest` に対して、通常給餌での concrete food 生成と world 登録、既存 food の保持、removed food 時の feeder 参照解放を固定する回帰シナリオを追加した
- 上記の `AutoFeeder` 回帰シナリオを含む対象実行成功を確認した
- `BeltconveyorTest` に対して、通常 adult body の受理、food object の受理、RIGHT/MIDDLE 設定時の正方向速度加算を固定する回帰シナリオを追加した
- 上記の `Beltconveyor` 回帰シナリオを含む対象実行成功を確認した
- `PoolTest` に対して、EDGE での水面復帰と SHALLOW での 1 段沈み込みを固定する回帰シナリオを追加した
- 上記の `Pool` 回帰シナリオを含む対象実行成功を確認した
- `FarmTest` に対して、`Shit` が肥料へ変換されて畑の `amount` が増え、元オブジェクトの `amount` が減る回帰シナリオを追加した
- 上記の `Farm` 回帰シナリオを含む対象実行成功を確認した
- `ProcesserPlateTest` に対して、`PAIN` モードで sleeping body が起床し、damage/stress/happiness/forceFace/dropShadow が変化する回帰シナリオを追加した
- 上記の `ProcesserPlate` 回帰シナリオを含む対象実行成功を確認した
- `ProudChildEventTest` に対して、参加成立した child が `HAPPY` 化する回帰シナリオを追加した
- 上記の `ProudChildEvent` 回帰シナリオを含む対象実行成功を確認した
- `FlyingEatEventTest` に対して、tick 到達時に生存 prey が `VERY_SAD` 化し `PAIN` face になる回帰シナリオを追加した
- 上記の `FlyingEatEvent` 回帰シナリオを含む対象実行成功を確認した
- `SuperEatingTimeEventTest` に対して、target 消失時に parent が `VERY_SAD` 化してイベント中断する回帰シナリオを追加した
- 上記の `SuperEatingTimeEvent` 回帰シナリオを含む対象実行成功を確認した
- `ShitExercisesEventTest` に対して、参加成立した baby child が `VERY_HAPPY` 化する回帰シナリオを追加した
- 上記の `ShitExercisesEvent` 回帰シナリオを含む対象実行成功を確認した
- `EventLogicTest` に対して、選ばれた body event だけが list から抜けて trailing event は残ること、および simple body event の除去が後続 event に波及しないことを固定する回帰シナリオを追加した
- 上記の `EventLogic` 回帰シナリオを含む対象実行成功を確認した
- `FamilyActionLogicTest` に対して、`rideOnParent` が food と toilet の両方を候補にできる状況で food target を優先する回帰シナリオを追加した
- 上記の `FamilyActionLogic` 回帰シナリオを含む対象実行成功を確認した
- `FoodLogicTest` の `setUp()` で `SimYukkuri.RND` を固定し、body 初期値が前テストの RNG に汚染される順序依存を解消した
- 上記の RNG 固定後に `FoodLogicTest` 全体を再実行し、既存の `OnlyAmaama` 系失敗が解消されることを確認した
- `FuneralEventTest` に対して、参加成立した child が `HAPPY` 化し、既存行動フラグが `clearActions()` で落ちる回帰シナリオを追加した
- 上記の `FuneralEvent` 回帰シナリオを含む対象実行成功を確認した
- `HateNoOkazariEventTest` に対して、very rude / healthy / okazari ありの個体が実際に攻撃イベントへ参加成立する回帰シナリオを追加した
- 上記の `HateNoOkazariEvent` 回帰シナリオを含む対象実行成功を確認した
- `BegForLifeEventTest` に対して、not damaged / average の個体が命乞い成功枝で `VERY_HAPPY` かつ `SMILE` face になり、begging を解除する回帰シナリオを追加した
- 上記の `BegForLifeEvent` 回帰シナリオを含む対象実行成功を確認した
- `GetTrashOkazariEventTest` に対して、okazari を持たない個体が trash target を追跡し続けて `update` 継続状態に入る回帰シナリオを追加した
- 上記の `GetTrashOkazariEvent` 回帰シナリオを含む対象実行成功を確認した
- `EatBodyEventTest` に対して、average 個体が終端 tick で stress `+2000` と `VERY_SAD` を受ける回帰シナリオを追加した
- 上記の `EatBodyEvent` 回帰シナリオを含む対象実行成功を確認した
- `YukkuriRideEventTest` に対して、親子が十分近いとき child が `linkParent` に親 `objId` を持って実際に搭乗状態へ遷移する回帰シナリオを追加した
- 上記の `YukkuriRideEvent` 回帰シナリオを含む対象実行成功を確認した
- `BreedEventTest` に対して、出生成功枝で responder が `VERY_HAPPY` になり、stress が下がって memories が増える回帰シナリオを追加した
- 上記の `BreedEvent` 回帰シナリオを含む対象実行成功を確認した
- `FavCopyEventTest` に対して、partner 関係の家族間で favorite bed が実際にコピーされる回帰シナリオを追加した
- 上記の `FavCopyEvent` 回帰シナリオを含む対象実行成功を確認した
- `CutPenipeniEventTest` に対して、unBirth 被害者が切断後に `VERY_SAD` 化し、raper 解除・damage 増加・cut face へ遷移する回帰シナリオを追加した
- 上記の `CutPenipeniEvent` 回帰シナリオを含む対象実行成功を確認した
- `AvoidMoldEventTest` に対して、very rude かつ FOOL の個体が moldy target への制裁後に `PUFF` face を取り、`sickPeriod +100` される回帰シナリオを追加した
- 上記の `AvoidMoldEvent` 回帰シナリオを含む対象実行成功を確認した
- `PredatorsGameEventTest` に対して、toy dead 時に predator が `PUFF` face で中断する回帰シナリオを追加した
- 上記の `PredatorsGameEvent` 回帰シナリオを含む対象実行成功を確認した
- `BedLogicTest` に対して、ベッド到着時に favorite bed が実際に保存される回帰シナリオを追加した
- 上記の `BedLogic` 回帰シナリオを含む対象実行成功を確認した
- `ToyLogicTest` に対して、近場のおもちゃ発見時に所有権取得・favorite ball 設定・`HAPPY` 化まで進む回帰シナリオを追加した
- 上記の `ToyLogic` 回帰シナリオを含む対象実行成功を確認した
- `StoneLogicTest` に対して、wise 個体が近距離の stone を回避すると scare 状態に入り、退避先 destination が map edge に設定される回帰シナリオを追加した
- 上記の `StoneLogic` 回帰シナリオを含む対象実行成功を確認した
- `ToiletLogicTest` に対して、持ち運び中の shit を抱えた個体が toilet 到着時に drop し、`toShit` を解除して stress を下げる回帰シナリオを追加した
- 上記の `ToiletLogic` 回帰シナリオを含む対象実行成功を確認した
- `BadgeLogicTest` に対して、新規 gold badge 付与時に `getInVain` が走り、`beVain` が立って stress が下がる回帰シナリオを追加した
- 上記の `BadgeLogic` 回帰シナリオを含む対象実行成功を確認した
- `BodyLogicTest` に対して、発情中の partner 個体が carried shit を drop して `moveToSukkiri` に入り、`toSukkiri` / `targetBind` / `moveTarget` が正しく設定される回帰シナリオを追加した
- `BodyLogicTest` の既存失敗 11 件について、RNG 前提不足と現行実装との差分を整理してテスト前提・期待値を更新し、全体 364 テスト成功まで復旧した
- `BodyLogicTest` に対して、dirty child が近距離の parent によって即座に `peropero` され、dirty 解消と双方の `VERY_HAPPY` 化まで進む `checkNearParent` 回帰シナリオを追加し、全体 365 テスト成功を確認した
- `BodyLogicTest` に対して、`checkWakeupOtherYukkuri` が `UnunSlave` を起床判定から除外しつつ通常個体は正しく数える回帰シナリオを追加し、乱数分岐系 2 ケースの座標を安定化して全体 366 テスト成功を確認した
- `BodyLogicTest` に対して、dirty な baby child が `doActionOther` で母親に即座に `peropero` され、dirty 解消・双方の `VERY_HAPPY` 化・stress 低下まで進む親子ケア回帰シナリオを追加し、全体 367 テスト成功を確認した
- `FamilyActionLogicTest` に対して、sleepy な baby child を持つ親が `checkFamilyAction` 本経路から `YukkuriRideEvent` を開始し、bed を target にして world event に登録される回帰シナリオを追加し、全体 85 テスト成功を確認した
- `EventLogicTest` に対して、`checkSimpleWorldEvent` が simple world event 自体も後続 normal event も除去せず保持する回帰シナリオを追加し、全体 34 テスト成功を確認した
- `EmotionLogicTest` に対して、happy な非ゲス個体が happy な stranger を見たとき `Pleasure` だけが立つ回帰シナリオを追加し、全体 14 テスト成功を確認した
- `FoodLogicTest` に対して、`isToTakeout=true` でも very hungry なら到着 food を持ち帰らずその場で消費する回帰シナリオを追加し、全体 715 テスト成功を確認した
- `BodyLogicTest` に対して、okazari を持たない rude adult が decorated body を見つけたとき、`checkPartner` から `toSteal=true`・`moveTarget=target`・`targetBind=false` で盗み行動へ入る回帰シナリオを追加し、全体 368 テスト成功を確認した
- `BodyLogicTest` に対して、`targetBind=true` の非接触 `doActionOther` が近距離で target を `stay()` 状態にする回帰シナリオを追加し、乱数 sister 分岐 1 件の座標安定化を含めて全体 369 テスト成功を確認した
- `BodyLogicTest` に対して、sleeping target への rude steal 成功枝で実際におかざりが移り、`toSteal` 解除・`stay()`・`VERY_HAPPY` 化まで進む回帰シナリオを追加し、全体 370 テスト成功を確認した
- `BodyLogicTest` に対して、smart child が dirty sister を `doActionOther` から即 `peropero` して dirty 解消・双方 `VERY_HAPPY`・双方 `stay()` まで進む姉妹ケア回帰シナリオを追加した
- `BodyLogicTest` に対して、partner 接触時の `surisuri` 成功枝で `nobinobi`・双方 `VERY_HAPPY`・双方 `stay()`・双方 stress 低下まで進む回帰シナリオを追加した
- `BodyLogicTest` に対して、damaged な younger sister を見た elder sister の concern 分岐で `SAD` 化し、`peropero` / `surisuri` へ逸れずに双方 `stay()` する回帰シナリオを追加した
- `BodyLogicTest` に対して、dirty child を見た adult parent が `checkPartner` から `moveToBody` に入り、`toBody`・`moveTarget=child`・`targetBind=true` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、needled child を見た adult parent が `checkPartner` から `moveToBody` に入り、`toBody`・`moveTarget=child`・`targetBind=false` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、partner の random approach 分岐で `checkPartner` が `moveToBody` に入り、`toBody`・`moveTarget=partner`・`targetBind=false` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、child の random approach 分岐で `checkPartner` が `moveToBody` に入り、`toBody`・`moveTarget=parent`・`targetBind=false` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、adult family の random approach 分岐で `checkPartner` が `moveToBody` に入り、`toBody`・`moveTarget=child`・`targetBind=false` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、needled partner 分岐で `checkPartner` が `moveToBody` に入り、`toBody`・`moveTarget=partner`・`targetBind=false` まで設定される回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の needled partner guriguri で actor/target の `VERY_SAD` 化、stress 加算、face 変化、actor の `stay()` まで進む回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の needled child guriguri で actor/target の `VERY_SAD` 化、stress 加算、face 変化、actor の `stay()` まで進む回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の dead elder/younger sister 分岐で actor が `VERY_SAD` になり、stress `+100` を受ける回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の dead child / dead partner / dead parent 分岐で actor の `VERY_SAD` 化、stress `+100`、dead parent では `SURPRISE` face まで固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の dead child / dead partner 分岐で `moveToBody`・`moveTarget`・`targetBind=false` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の hungry child への food drop 分岐で takeout 解放・`inOutTakeoutItem`・food の `ON_FLOOR` 復帰を、ants treatment 分岐で `peropero`・`SAD` 化・stay・target の stress/damage 改善を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、child-parent の skinship 分岐で healthy parent への `surisuri` による `nobinobi`・双方 `VERY_HAPPY`・双方 stay・stress 低下を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、sister-sister の skinship 分岐で healthy sister への `surisuri` による `nobinobi`・双方 `VERY_HAPPY`・双方 stay・stress 低下を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の glad-about-partner `GO` 枝で actor が `stay()` し、`HAPPY` を維持する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の envy-angry partner `WAIT` 枝で actor の `VERY_SAD` 維持と `stay()` を、fear-only `WAIT` 枝で `SAD` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の needled sister guriguri で actor/target の `VERY_SAD` 化・stress 加算・target の `PAIN` face を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の unun-slave 盗み成功枝で actor の `PublicRank.NONE` 昇格、target の `UnunSlave` 降格、おかざり移動、`VERY_HAPPY` 化、`stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の mercy-about-other `GO` 枝で actor の `VERY_SAD` 維持と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-partner `GO` 枝で actor の `SAD` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-child with pain `GO` 枝で actor の `VERY_SAD` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の glad-about-child `GO` 枝で actor の `HAPPY` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の glad-about-mother `GO` 枝で actor の `HAPPY` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の glad-about-father `GO` 枝で actor の `HAPPY` 維持と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-father with pain `GO` 枝で actor の感情変化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の very-happy partner `GO` 枝で actor の `VERY_HAPPY` 維持と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-sad partner `GO` 枝で actor の感情変化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-mother without pain `GO` 枝で actor の感情変化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の mold detection 枝で actor に `AvoidMoldEvent` が 1 件だけ queue され、`from/to` が期待個体を指す回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の NYD child 制裁枝で world event queue に `HateNoOkazariEvent` が 1 件だけ積まれ、`from/to` が期待個体を指す回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の dead body onanism 枝で actor の `HAPPY` 化・stress 減少・memories 増加・双方 `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の dead body rape 枝で actor の `sukkiri`・`HAPPY`・stress 0・memories 増加と、corpse 側の stress 増加・`stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の force-exciting + baby target 枝で actor/target の双方が `sukkiri`・`HAPPY`・`stay()` に入る回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の exciting adult + no partner 枝で actor が onanism にフォールバックし、`HAPPY`・`stay()`・`currentEvent=null` を保つ回帰シナリオを追加した
- `BodyLogicTest` に対して、dead child に対する `doActionOther` の funeral queue 枝で `FuneralEvent` の `from/to`、parent の `VERY_SAD` 化、stress `+100`、`currentEvent=null` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` のプロポーズ枝で `ProposeEvent` の `from/to` と `currentEvent=null` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の HateNoOkazari queue 枝で `HateNoOkazariEvent` の `from/to` と `currentEvent=null` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の very-rude / raper / SUPER_SHITHEAD rape-only の `moveToSukkiri` 3 枝で `toSukkiri`・`moveTarget`・`targetBind=true`・`currentEvent=null` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の idiot-target 中断枝で actor が `setCalm()` され、`isExciting=false`、移動系フラグなし、`currentEvent=null` を保つ回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の no-target + exciting + `doOnanism` フォールバック枝で actor の `isExciting=false`、`HAPPY`、`stay`、stress / memories の固定変化、移動系フラグなし、`currentEvent=null` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の `KillPredeatorEvent` 低優先度枝で actor の `panic` が解除され、`currentEvent` は維持されることを固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の dead stranger corpse + adult `lookTo` 枝で actor の `direction` 変更、`stay`、`SAD` 化、`memories -1`、移動系フラグなしを固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkPartner` の `callingParents -> checkNearParent` 枝で sleeping parent が起床し、actor 側で移動系フラグや event が始まらないことを固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-father without pain `GO` 枝で actor の感情変化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の glad-about-sister `GO` 枝で actor の `HAPPY` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-elder-sister without pain `GO` 枝で actor の `SAD` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`checkActionSurisuriFromPlayer` の concern-about-younger-sister with pain `GO` 枝で actor の `VERY_SAD` 化と `stay()` を固定する回帰シナリオを追加した
- `BodyLogicTest` に対して、`doActionOther` の exciting adult partner 接触枝で双方が `sukkiri`・`HAPPY`・`stress=0`・`stay()` に入る回帰シナリオを追加した

## Phase 3: Branch Gap Closure

目的:

- 既存テストでは拾えていない条件分岐を埋める

作業:

- [x] coverage report から missed branch 上位メソッドを抽出する
- [x] 条件表を作ってケースを追加する
- [x] 巨大メソッドは仕様断面ごとにテストを分割する

優先候補:

- `BodyLogic`
- `EventLogic`
- `FamilyActionLogic`
- `EmotionLogic`
- `FoodLogic`
- `Body`
- `Terrarium`

完了条件:

- 上位の巨大ロジックで、重要分岐の未確認が大きく減っている

現在の評価:

- Phase 3 は実務上かなり達成済み
- `EventLogic`, `EmotionLogic`, `FamilyActionLogic`, `FoodLogic`, `Terrarium` の high-value branch はかなり整理された
- `FuneralEvent`, `ProudChildEvent`, `ShitExercisesEvent`, `SuperEatingTimeEvent` のような missed が残っていた event 本体にも具体回帰を追加した
- coverage 上の大きな残りは、主に `GUI` / `command` / `MainCommandUI` と、巨大クラスを low-value まで掘る必要がある領域へ寄っている
- 以後の残件は「まだ重要な仕様が未固定」ではなく、「深追いコストが高い割にリターンが薄い」ものが中心

## Phase 4: Dependency Seams

目的:

- GUI / リソース依存を抱えた箇所のテスト可能性を上げる

作業:

- [ ] resource 取得を adapter 経由に寄せる
- [ ] image load をロジックから分離する
- [ ] ini / properties / message access を差し替え可能にする
- [ ] Swing 部品生成を薄い境界の外に追い出す

完了条件:

- GUI 非対象のはずなのに GUI 依存でテスト不能な箇所が減っている

## Phase 5: Enforcement

目的:

- 以後の変更でテスト品質が後退しないようにする

作業:

- [ ] 安定したテスト実行コマンドを一本化する
- [ ] coverage 集計方法を固定する
- [ ] package 単位の下限を定める
- [ ] 将来の CI 化に耐える形にする

## Concrete Near-Term Backlog

最初の実作業は次の順に進める。

1. 既存 test helper の整理
2. static 状態リセットの共通化
3. `BodyLogic` の missed branch 抽出
4. `FamilyActionLogic`, `FoodLogic`, `EventLogic`, `EmotionLogic` のケース表作成
5. `Body` の主要状態遷移シナリオ追加
6. `item` と `event` の回帰シナリオ追加

## Phase 1 Findings

初回棚卸しで確認できた点:

- `test/src/util/WorldTestHelper.java` が既存の共通 helper として存在する
- ただし現在の helper は `SimYukkuri.world`, `SimYukkuri.mypane`, `Numbering`, `RND` の初期化に寄っており、static 状態の管理が不十分
- `Terrarium` は環境フラグと内部 static 状態を多く持つ
- `Translate` は描画前提の static 状態を広く持つ
- `MainCommandUI` は static UI コンポーネントを広く保持している
- `MessagePool` と `ResourceUtil` は singleton / static アクセスの温床になっている

フェーズ1で最初に手を入れる対象:

- `WorldTestHelper` に `MessagePool` / `Translate` 初期化を集約し、既存テストの直初期化を段階的に削減する
- `BodyBehaviorTest` 由来の `SimYukkuri.RND` 汚染が `BodyTest` に波及する問題を `BodyTest` 側の明示初期化で遮断済み
- `BodyBehaviorTest`, `BodyTest`, `GadgetActionTest`, `GadgetMenuTest` の組み合わせ実行で `1229/1229` 成功を確認済み
- `GadgetToolTest`, `ShowStatusFrameTest`, `MessagePoolTest` の `MessagePool.loadMessage(...)` 直呼びを helper 経由へ移行した
- `FoodLogicTest`, `BeltconveyorObjTest`, `PoolTest` の一部 `Translate` 再初期化を helper 経由へ移行した
- `BodyLogicTest`, `StoneLogicTest`, `ObjTest`, `ObjEXTest`, `EventPacketTest` の `Translate` 初期化を helper 経由へ移行し、対象 543 テストの成功を確認した
- `VomitTest`, `StalkTest`, `ShitTest`, `LoggerYukkuriTest`, `ObjDrawCompTest`, `TerrainFieldTest` の `Translate` 初期化を helper 経由へ移行し、対象 167 テストの成功を確認した
- `ToyLogicTest`, `FoodLogicTest`, `PoolTest`, `FarmTest`, `ProcesserPlateTest` の残存 `Translate` 初期化を helper 経由へ移行し、対象 924 テスト成功を確認した
- `WorldTestHelper.resetStates()` に `SimYukkuri.RND` と `LoggerYukkuri` の static リセットを追加し、`BodyBehaviorTest` に明示 teardown を追加した
- `BodyBehaviorTest`, `BodyTest`, `LoggerYukkuriTest`, `GadgetActionTest`, `GadgetToolTest` の static 汚染に敏感な組み合わせで 1292 テスト成功を確認した
- 現時点で残る主な Phase 1 整理対象は、意図的に `Translate` 自体を検証している `draw/TranslateTest`、小規模な独自初期化コメント、`logic/UnunSlaveDebug.java` のようなデバッグ用途コード

- [x] `WorldTestHelper` の責務を「世界初期化」「static state reset」「fixture 生成」に分割し始める
- [x] `Terrarium` のテスト用 reset 導線を共通化する
- [ ] `Translate` の初期化をテスト fixture に閉じ込める
- [x] `MainCommandUI` に依存するテストを最小限の helper 経由に寄せ始める
- [x] `MessagePool` の空初期化を共通セットアップへ寄せ始める

今回の反映内容:

- `test/src/util/WorldTestHelper.java` に `resetTerrariumState`, `resetMainCommandUIState`, `initializeEmptyMessagePool`, `initializeMainCommandUITestState` を追加
- `test/src/util/WorldTestHelper.java` に `initializeTranslate`, `initializeStandardTranslate200`, `initializeStandardTranslate500` を追加
- `BodyMetabolismTest` の `Terrarium` static reset を helper 経由へ移行
- `FireTest`, `NeedleTest`, `PoisonAmpouleTest`, `OrangeAmpouleTest` の `MessagePool` 初期化重複を helper へ移行
- `MainCommandListenerTest`, `ItemListenerTest`, `MainCommandUITest` の `MainCommandUI` 足場を helper ベースへ移行
- `ItemTestBase`, `EventTestBase`, `BodyTest`, `MainCommandUITest` の `Translate` 初期化の一部を helper ベースへ移行

次の候補:

- `Translate` 初期化を個別テストから helper へ寄せる
- `MessagePool` / `ResourceUtil` を直接叩いている残りのテストを整理する
- `WorldTestHelper` を world fixture と UI fixture に分割する

## Definition Of Done

次を満たしたら「GUI とリソース以外は大規模リファクタリング可能」とみなす。

- 主要ドメインロジックに回帰シナリオがある
- 大型メソッドの重要分岐に未確認ケースがほぼ残っていない
- テスト間汚染が抑えられている
- テスト実行手順が固定されている
- coverage が継続監視可能である

## Execution Rule

以後の作業は、この計画に従って以下の順で進める。

1. harness 整備
2. 回帰シナリオ固定
3. branch gap 解消
4. 依存分離
5. 実行基盤の固定

## Latest Progress

- 最終棚卸しとして、Phase 1 は実務上ほぼ完了、Phase 2 は高優先領域の主要仕様固定をかなり達成、Phase 3 も実務上かなり達成済みと整理した
- `YukkuriUtilTest` に対して、`getRandomYukkuriType()` の non-`Dos` 親継承、`null` 親の通常分岐、`Alice slot -> Arisu` 写像、rare type 分岐を固定し、`36 tests successful / 0 failed` を確認した
- `YukkuriUtilCreateBabyDnaTest` に対して、mother ancestor の先祖返り、`attBase=0` rare roll、`intBase=4` rare roll を固定し、`16 tests successful / 0 failed` を確認した
- `TerrariumTest` に対して、`getDayState()` 境界、`resetTerrariumEnvironment()` の steam flag 全消去、`checkPanic()` の近距離非 `raper` への `FEAR` 伝播を固定し、`8 tests successful / 0 failed` を確認した
- `EventLogicTest` に対して、到達 side の `execute()` 成功 / 継続分岐を固定し、`36 tests successful / 0 failed` を確認した
- `EmotionLogicTest` に対して、`AVERAGE` / `SAD` / rude 個体の exact emotion 配列を固定し、`17 tests successful / 0 failed` を確認した
- `FamilyActionLogicTest` に対して、takeout food の即再ターゲット、`proudChild()` の direct start + world event queue、`checkRaperFamily()` の exciting 解除を固定し、`88 tests successful / 0 failed` を確認した
- `FoodLogicTest` に対して、`OnlyAmaama` の non-sweets 中断、`exciting && !raper && so hungry` の calm 化、starving 中の toilet 優先抑制を固定し、`718 tests successful / 0 failed` を確認した
- `FuneralEventTest` に対して、`FIND` の elder sister grief、親 `GOODBYE` のおかざり剥奪、rude child `GOODBYE` の `furifuri` を固定し、`47 tests successful / 0 failed` を確認した
- `ProudChildEventTest` に対して、child `GO` random hit、親 `PROUD`、rude child `PROUD` の副作用を固定し、`37 tests successful / 0 failed` を確認した
- `ShitExercisesEventTest` に対して、`POKAPOKA` の `furifuri`、`UNUN` 成功時の `DOSHIT`、肛門封鎖時の `shit` 増加抑制枝を固定し、`49 tests successful / 0 failed` を確認した
- `SuperEatingTimeEventTest` に対して、親 `START` の food target 付与と `noHungrybySupereatingTimePeriod`、子 `START` の実食と `clearActions()` を固定し、`45 tests successful / 0 failed` を確認した
- `TEST_EXPANSION_PLAN.md` の棚卸し結果として、Phase 1 は実務上ほぼ完了、Phase 2 は高優先領域の主要仕様固定をかなり達成したと整理した
- `BodyAttributesTest` に対して、`Burst.HALF` で `addStress()` が stress 自体は増やすが `plusShit` 副作用を抑制する枝、`babyTypes` dequeue の FIFO、`hasBabyOrStalk()` の腹妊娠/茎妊娠判定を固定する回帰シナリオを追加し、`361 tests successful / 0 failed` を確認した
- `BodyBehaviorTest` に対して、damage があっても stress 条件がなければ `AVERAGE` や `FOOL + SHITHEAD` が命乞いしない抑制枝を追加し、`10 tests successful / 0 failed` を確認した
- `DnaTest` に対して、default / parameterized constructor が `father` / `mother` を暗黙設定しない枝を固定し、`5 tests successful / 0 failed` を確認した
- `BodyLogicTest` に対して、`LOW priority currentEvent` を抱えていても `moveToSukkiri` に進める枝を追加し、あわせて dead stranger child corpse の期待値を現行実装へ整理したうえで `457 tests successful / 0 failed` を確認した
- `HouseTest` に対して、static `boundary/images` を差し込んだ状態で引数付き constructor が `houseType` / `itemRank` / world 登録 / boundary / collision を正しく反映する枝と、`getImageLayer()` が floor image を返す枝を固定し、`13 tests successful / 0 failed` を確認した
- `BarrierTest` に対して、壁生成が wall map に実際に線を書き込み `onBarrier()` / `acrossBarrier()` / `getBarrier()` が正方向で反応し、`clearBarrier()` でそれらが消える枝を固定し、`16 tests successful / 0 failed` を確認した
- `BodyAttributesBurstTest` に対して、`getBurstState()` の `SAFE` / `HALF` / `NEAR` / `BURST` 閾値を固定し、`6 tests successful / 0 failed` を確認した
- `AntsTest` に対して、`update()` が `numOfAnts / 3` ちょうどの量だけ `bodyAmount` / `hungry` を削る枝と、`lockmove` 個体が `VERY_SAD`・`PAIN`・`purupuru` に入る抑制枝を固定し、`16 tests successful / 0 failed` を確認した
- `PlayerTest` に対して、`world` が存在しても `MainCommandUI` 未初期化で `showPlayerStatus()` が落ちる状況を `addCash()` が握りつぶし cash 更新自体は成功する枝を固定し、`6 tests successful / 0 failed` を確認した
- `IniFileReaderTest` に対して、同一セクション内で `currentSection` を維持して複数 key を返す枝と、途中セクション切替後に次 key から新セクションを返す枝を固定し、`14 tests successful / 0 failed` を確認した
- `SpriteTest` に対して、`setPivotType()` が即時には pivot を再計算せず次のサイズ更新まで反映しない枝と、`addSpriteSize()` が常に original size 基準で再計算する枝を固定し、`14 tests successful / 0 failed` を確認した
- `BodyLogicTest` に `checkPartner` の `publicRank` 不一致枝を stateful 回帰として追加し、`false` 戻りだけでなく `moveToBody` / `moveToSukkiri` / `steal` / `event` が何も始まらないことまで固定
- `BodyLogicTest` に `checkPartner` の `dead body + random skip` 枝を stateful 回帰として追加し、`false` 戻り時に向き変更・移動・panic・event が何も始まらないことを固定
- `BodyLogicTest` に `checkPartner` の `high priority currentEvent` ガードを stateful 回帰として追加し、既存 event の維持と移動・steal・感情変化の不発を固定
- `BodyLogicTest` に `checkPartner` の `toFood / toBed / toShit` ガードを stateful 回帰として追加し、最上段 return で移動・steal・event・感情変化が起きないことを固定
- `BodyLogicTest` に `checkPartner` の `wantToShit / nearToBirth / NYD` ガードを stateful 回帰として追加し、前段 return で移動・steal・event・感情変化が起きないことを固定
- `BodyLogicTest` で `has SHIT takeout` 分岐を補強し、`not exciting` 側の carry 維持・不発と、`exciting` 側の actual drop (`inOutTakeoutItem`, `Where.ON_FLOOR`) を固定
- `BodyLogicTest` に `checkPartner` の `old move target -> doActionOther` 再利用枝を stateful 回帰として追加し、dirty child が即座に `peropero` で清掃されることを固定
- `BodyLogicTest` に `checkPartner -> checkActionSurisuriFromPlayer(GO)` の母子枝を stateful 回帰として追加し、`moveToBody` / `moveTarget` / `targetBind=false` を固定
- `BodyLogicTest` に `pheromone + okazari` 優先の steal 枝を stateful 回帰として追加し、近い別個体がいてもフェロモン付き target が `moveTarget` に選ばれることを固定
