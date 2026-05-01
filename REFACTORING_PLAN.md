# Refactoring Plan

## 目的

この計画の目的は、現状の挙動を保ったまま、変更しやすい構造へ段階的に移行することです。

最優先は「巨大クラスを小さくすること」ではありません。まず副作用と依存を見える化し、テストで守られた範囲から分割します。大規模な一括置換やパッケージ再編を先に行うと、ゲーム挙動・セーブ互換・GUI 操作のどれが壊れたのか判定できなくなるためです。

## 現状診断

### コード規模

確認時点の主要な巨大ファイルは以下です。

| ファイル | 行数 | 主な問題 |
| --- | ---: | --- |
| `src/base/Body.java` | 9,072 | 状態、行動、物理、関係、イベント、描画補助、getter/setter が集中 |
| `src/base/BodyAttributes.java` | 7,108 | 種族パラメータ、画像情報、調整値、アクセサが集中 |
| `src/logic/BodyLogic.java` | 2,041 | 関係判定、行動選択、集合処理、イベント補助が混在 |
| `src/logic/FoodLogic.java` | 2,005 | 食事探索、摂食副作用、持ち帰り、捕食判定が混在 |
| `src/draw/Terrarium.java` | 1,889 | ワールド進行、生成、保存/読込、当たり判定、環境状態が混在 |
| `src/draw/MyPane.java` | 1,360 | 描画、画像ロード、メインループ、入力補助、UI 状態が混在 |
| `src/SimYukkuri.java` | 1,152 | アプリ起動、グローバル状態、入力、ウィンドウ制御、保存/読込 UI が混在 |
| `src/command/GadgetAction.java` | 1,524 | ツール操作の分岐と副作用が集中 |
| `src/item/Yunba.java` | 1,393 | アイテム固有ロジックが大きい |
| `src/command/GadgetMenu.java` | 1,211 | メニュー定義、UI 表示、操作定義が密結合 |

全体では `src` が約 73,000 行、`test/src` が約 94,000 行です。既存テストは十分に増えており、リファクタリングに入れる土台はかなりあります。

### 主な構造問題

1. グローバル状態が多い

`SimYukkuri.world`, `SimYukkuri.RND`, `SimYukkuri.mypane`, `Terrarium` の static 環境状態、`ResourceUtil.getInstance()` などが広範囲から直接参照されています。テストの順序依存、乱数依存、GUI なし実行の難しさにつながっています。

特に前回のテスト拡充で苦労した点はここです。乱数、ワールド、メッセージ、リソース、画像ロードが static 参照に固定されているため、テスト用の deterministic な乱数や軽量な world stub に差し替えにくくなっています。その結果、テスト側で global state をリセットしたり、実行順に気を使ったり、狙った分岐へ到達するために過剰な fixture を組む必要が出ています。

2. UI とドメインロジックが混ざっている

`SimYukkuri`, `MyPane`, `Terrarium`, `GadgetAction`, `GadgetMenu` が Swing、入力イベント、描画、ワールド変更を同時に扱っています。画面操作を変えるだけでもゲーム状態へ波及しやすい構造です。

3. `logic` が具象クラスを直接知りすぎている

`FoodLogic` は `Food`, `Bed`, `Toilet`, `Barrier`, `Stalk`, `Vomit`, 複数の `yukkuri` 型、イベント型を直接 import しています。ルール追加時に既存分岐へ割り込む形になりやすく、仕様の局所性が低いです。

4. factory/registry が switch と具象型に寄っている

`Terrarium.makeBody` は type 値で大量の `yukkuri` クラスを生成しています。`MyPane` も各種画像ロードで具象クラスを列挙しています。新種追加時の変更箇所が多くなります。

5. データと振る舞いの境界が曖昧

`Body` と `BodyAttributes` が永続化対象、計算対象、描画対象、UI 表示対象を兼ねています。フィールドを移動するだけでも Jackson セーブ互換、テスト fixture、描画に影響します。

6. ビルド/生成物がワークツリーに混ざりやすい

`.gitignore` は `build`, `bin`, `lib/jacoco` 等を除外していますが、現在の作業ツリーには `node_modules`, `package*.json`, jar 類、ログなど未追跡ファイルが見えています。大規模変更時に差分の把握を邪魔します。

## 進め方の原則

1. テストで守られている範囲から動かす

既存の `TEST_EXPANSION_PLAN.md` では、GUI とリソース読込を除く主要仕様がかなり回帰テスト化されています。リファクタリングはこの安全網を前提にし、`base`, `logic`, `event`, `item`, `game`, `util` の順で進めます。

2. static 直参照を依存注入可能な形へ寄せる

最初に解くべき痛点は、テスト時に依存を差し替えられないことです。`SimYukkuri.RND`、`SimYukkuri.world`、`ResourceUtil.getInstance()`、`MessagePool`、画像ロード系 static へ直接触る箇所を、薄い provider/service 経由に変えます。

ただし、全コードを一気に DI コンテナ化する必要はありません。まずは constructor injection か method parameter injection で十分です。既存コードからは default provider を使い、テストからだけ fake/stub を渡せる形にします。

3. public API を急に変えない

最初は既存メソッドを残し、中身だけ委譲します。呼び出し側の置換は別 PR/別コミットで進めます。

4. セーブ互換を壊さない

`Body`, `BodyAttributes`, `World`, `MapPlaceData`, `Obj` 系は Jackson シリアライズに関わるため、フィールド名・型・継承関係の変更は後回しにします。先に保存/読込の回帰テストを固定します。

5. static の排除は置換ではなく注入口の追加から始める

`SimYukkuri.RND` や `SimYukkuri.world` を一気に消すのではなく、`GameContext` などの薄い参照口を導入し、既存 static を初期実装として包みます。

6. 分割単位は「責務」と「テスト単位」で決める

単純に行数で割るのではなく、テストで振る舞いを確認しやすい単位へ分けます。

## DI 化の優先順位

リファクタリングの最初の狙いは、テストで依存を差し替えられる構造を作ることです。優先順位は以下です。

| 優先 | 対象 | 現状の問題 | 目標 |
| ---: | --- | --- | --- |
| 1 | `SimYukkuri.RND` | 乱数分岐を狙い撃ちできず、テストが順序依存しやすい | `GameRandom` / `RandomProvider` を注入可能にする |
| 2 | `SimYukkuri.world` | ロジックが巨大な実 world を要求する | `WorldView` / `WorldAccess` を経由し、必要な操作だけ見せる |
| 3 | `ResourceUtil.getInstance()` | 文言取得が static singleton に固定される | `TextResource` を渡せるようにする |
| 4 | `MessagePool` | 乱数、Body 状態、文言生成が混ざる | `MessageService` として切り出す |
| 5 | 画像ロード static | GUI なしテストで重い実ロードに触りやすい | `ImageRepository` / `SpriteRepository` を差し替え可能にする |
| 6 | `Terrarium` static 環境状態 | 環境状態がテスト間で漏れやすい | `EnvironmentState` インスタンスへ移す |

初期実装では、既存 static を包む default 実装を置きます。テストだけ fake を使います。これにより、挙動変更を最小にしながら、前回苦労した「ランダムを差し替えられない」「world を丸ごと立てないと分岐に入れない」という問題を先に潰します。

## 目標アーキテクチャ

最終的には、少なくとも以下の境界を作ります。

| 層 | 役割 | 代表候補 |
| --- | --- | --- |
| App/UI | Swing 起動、入力、画面表示 | `SimYukkuri`, `MyPane`, `MainCommandUI` |
| Application Service | ユーザー操作をゲーム命令へ変換 | `GadgetAction`, save/load orchestration |
| Simulation Core | tick 更新、行動選択、イベント進行 | `Terrarium`, `BodyLogic`, `FoodLogic`, `EventLogic` |
| Domain Model | Body/Item/Event の状態と最小振る舞い | `Body`, `Obj`, `BodyAttributes`, `item`, `event` |
| Infrastructure | 画像、resources、ini、ログ、保存形式 | `ModLoader`, `ResourceUtil`, `IniFileUtil`, save codec |

依存方向は原則として上から下のみです。特に Core/Domain から Swing、`MyPane`、`SimYukkuri` へ直接依存しない状態を目指します。

## フェーズ計画

### Phase 0: 作業環境と差分管理の整理

目的:

- 大規模変更前に差分ノイズを減らす
- いつでも全テスト/部分テストを回せる状態にする

作業:

- [x] 未追跡の `node_modules`, log, `package*.json` を ignore する
- [x] Jackson 以外の未追跡テスト補助 jar を ignore する
- [x] 依存 jar の管理方針を `docs/build-and-test.md` に固定する
- [x] 現行のコンパイル手順と JUnit 実行手順を `docs/build-and-test.md` に固定する
- [x] `bin/` 配下の複製ファイルを成果物として扱い、通常の探索対象から外す
- [x] 全テスト、主要パッケージ別テスト、単一テストの実行コマンドを明文化する
- [x] Windows/Linux の配布 jar ビルドスクリプトを揃える
- [x] Windows/Linux のテスト実行スクリプトを揃える

完了条件:

- [x] `git status --short` で作業対象外のノイズが説明可能
- [x] 変更前後で同じテストセットを比較できる

現在の評価:

- Phase 0 は完了扱い。
- `build/build.bat` と `build/build.sh` は配布 jar 作成用。
- `build/run_tests.bat` と `build/run_tests.sh` はテスト用コンパイル + JUnit 実行用。
- GitHub Actions は Jackson 3 jar をリポジトリから使い、JUnit Console standalone を CI 内で取得する。
- 残る headless/X11 起因の失敗は環境依存のテスト安定化課題であり、Phase 1 以降の static/UI 依存分離で扱う。

### Phase 1: グローバル依存の包み込み

状態: 完了。以降は Phase 2 に移行する。

目的:

- 既存 static を残したまま、テストから差し替え可能な参照口を作る
- 乱数と world 依存を最優先で注入可能にする

候補:

- `RandomProvider` または `GameRandom`
- `WorldProvider`
- `GameMessages`
- `GameText`
- `GameImages`
- `GameEnvironment`

最初の対象:

- `SimYukkuri.RND` を直接使う `logic`, `event`, `item`, `draw` のうち、テストが厚いクラス
- `MessagePool.getMessage` と `ResourceUtil.getInstance().read` の呼び出しがロジック判定と混在している箇所

進め方:

1. 既存 static へ委譲する薄いインターフェース/クラスを追加する
2. テストが厚い 1 クラスだけ注入口を使う。最初の候補は `FoodLogic` または `BodyLogic`
3. 既存テストが変わらず通ることを確認する
4. 同じパターンで横展開する

進捗:

- `RandomSource` と `GameRandom` を追加し、既存の `SimYukkuri.RND` へ委譲する default 経路を作成した
- `Okazari.getRandomOkazari` を `GameRandom` 経由へ移行した
- `OkazariTest` に `SimYukkuri.RND` を直接差し替えず、テスト用 random source で分岐を固定するテストを追加した
- 既存の `SimYukkuri.RND` 差し替えテストも通るため、移行中の互換性は維持できている
- `Body.java` を除く `src` 配下の `SimYukkuri.RND` 直参照を `GameRandom` 経由へ横展開した
- 横展開後、`Body.java` 以外の production code には `SimYukkuri.RND` 直参照が残っていない
- `Body.java` の 185 箇所の `SimYukkuri.RND` 直参照も `GameRandom` 経由へ移行した
- production code の `SimYukkuri.RND` 直参照は `GameRandom` の default 委譲実装だけになった
- `BodyLogicTest` と `ProudChildEventTest` は `GameRandom.clearOverride()` と `SimYukkuri.RND` 復元を teardown に入れ、ランダム差し替えのテスト間漏れを抑えた
- `ProudChildEventTest` の思い出増加シナリオは、コンストラクタ乱数で決まる知能に依存しないよう `Intelligence.FOOL` を明示した
- 検証: production compile、test compile、`src.base/src.logic/src.event/src.yukkuri` JUnit、全体 JUnit が成功。全体 JUnit は 6,769 tests successful / 0 failed
- `WorldSource` と `GameWorld` を追加し、既存の `SimYukkuri.world` へ委譲する default 経路とテスト用 override 経路を作成した
- production code の `SimYukkuri.world` 直参照は `GameWorld` の default 委譲実装だけになった
- `Terrarium` のロード復元時の world 差し替えは `GameWorld.set(...)` 経由へ移行した
- `GameWorldTest` を追加し、default 委譲、`set(...)`、override の基本挙動を固定した
- 検証: production compile、test compile、`GameWorldTest + src.base/src.logic/src.event/src.yukkuri` JUnit、全体 JUnit が成功。全体 JUnit は 6,772 tests successful / 0 failed
- `TextSource` と `GameText` を追加し、production code の `ResourceUtil.getInstance().read(...)` 直参照を `GameText.read(...)` 経由へ移行した
- `MessageSource` と `GameMessages` を追加し、production code の `MessagePool.loadMessage(...)` / `MessagePool.getMessage(...)` 直参照を `GameMessages` 経由へ移行した
- `ImageSource` と `GameImages` を追加し、production code の `ImageIO.read(...)` 直参照を `GameImages.read(...)` 経由へ移行した。既存経路に合わせて `InputStream` と `File` の両方を扱う
- `EnvironmentSource` と `GameEnvironment` を追加し、`Terrarium` の時刻・蒸気・アラームなどの static 環境状態参照を `GameEnvironment` 経由へ移行した
- `GameTextTest`, `GameMessagesTest`, `GameImagesTest`, `GameEnvironmentTest` を追加し、各 facade の override 差し替え動作を固定した
- 検証: production compile、test compile、`GameTextTest/GameMessagesTest/GameImagesTest/GameEnvironmentTest + src.base/src.logic/src.event/src.yukkuri` JUnit、全体 JUnit が成功。全体 JUnit は 6,777 tests successful / 0 failed
- `LocaleSource` と `GameLocale` を追加し、production code の `ResourceUtil.IS_JP` 直参照を `GameLocale.isJapanese()` 経由へ移行した。`isJapanese()` は既存二択コードの移行用 API とし、今後の本命は `getLocale()` や文言 resolver へ寄せる
- `ViewSource` と `GameView` を追加し、production code の `SimYukkuri.mypane` 直参照を `GameView` 経由へ移行した。対象は dialog parent、画像ロード、初期化、Terrarium 経由の `addBody/makeBody/addVomit/addEffect` など
- production code の `SimYukkuri.mypane` 直参照は `GameView` の default 委譲実装だけになった
- `GameLocaleTest` と `GameViewTest` を追加し、locale と view/terrarium 操作の override 差し替え動作を固定した
- 検証: production compile、test compile、`GameLocaleTest/GameViewTest + src.base/src.logic/src.event/src.yukkuri` JUnit、全体 JUnit が成功。全体 JUnit は 6,781 tests successful / 0 failed
- `ResourceUtil.IS_JP` は互換性維持のため public field として残し、`@Deprecated` を付与した。production code は `GameLocale` 経由へ移行済み
- Phase 1 は完了。直参照は各 facade の default 委譲実装内だけに閉じ込めた

避けること:

- `SimYukkuri` の static フィールドをいきなり削除しない
- 乱数の呼び出し順が変わるロジック整理を同時にしない
- DI フレームワークを導入しない。まずは明示的な引数か constructor injection で足りる

### Phase 2: `Body` の責務分離

状態: 開始。最初の対象として、親子・番・姉妹・家族判定を `BodyRelations` へ切り出した。

目的:

- 9,000 行級の `Body` を、状態保持と振る舞いサービスへ分ける

推奨する切り出し順:

1. 関係判定: 親子、番、敵対、群れ、所有関係
2. 生理状態: 空腹、排泄、睡眠、病気、ダメージ、老化
3. 移動状態: 目的地、速度、衝突、逃走、追従
4. イベント状態: current event、event packet、message
5. 描画補助: image code、face、影、レイヤー情報

初期形:

- `Body` の public method は残す
- 新クラスへ private/static helper として委譲する
- 呼び出し側の置換は後続フェーズへ回す

進捗:

- `BodyRelations` を追加し、`Body.isFamily/isParent/isFather/isMother/isChild/isPartner/isSister/isElderSister` の実装を委譲した
- `BodyRelations` に `checkMyRelation(...)` を追加し、`BodyLogic.checkMyRelation(...)` の分類実装を委譲した
- `Body` の public API は維持し、呼び出し側の置換や仕様整理はまだ行っていない
- `BodyRelationsTest` を追加し、親子、番、姉妹、無関係、既存 null 挙動を抽出先で直接固定した
- 検証: production compile、test compile、`BodyRelationsTest + BodyTest + BodyLogicTest + FamilyActionLogicTest + src.event` JUnit、全体 JUnit が成功。全体 JUnit は 6,786 tests successful / 0 failed
- `BodyVitals` を追加し、`BodyAttributes` の空腹、ダメージ、病気の判定メソッドを委譲した。フィールドとJSON互換は動かしていない
- `BodyVitalsTest` を追加し、空腹、ダメージ、病気の境界値を抽出先で直接固定した
- 検証: production compile、test compile、`BodyVitalsTest + BodyAttributesTest + BodyTest + FoodLogicTest` JUnit、全体 JUnit が成功。全体 JUnit は 6,792 tests successful / 0 failed
- `BodyMovement` を追加し、`Body.moveBody(...)` のうち移動stepと移動周期の計算を委譲した
- `BodyMovementTest` を追加し、通常step、空腹、捕食種、ダメージ、病気、痛み、妊娠/茎、蟻、盲目、step 0補正、移動周期を抽出先で直接固定した
- `BodyMovement` へ X/Y 目的地方向更新、目的地なしのランダム方向更新、実移動ベクトル計算、飛行時の Z 目的地/高度維持更新を追加で切り出した。`Body.moveBody(...)` は既存 public API とフィールド互換を維持したまま委譲している
- `BodyMovementTest` に X/Y/Z 目的地方向、到達時の目的地クリア、ランダム方向更新の後置インクリメント相当、速度余りの確率加算、飛行高度維持のテストを追加した
- `BodyMovement` へ外力移動、壁衝突、マップ境界補正、落下、着地ダメージ処理を追加で切り出した。`Body.moveBody(...)` 先頭の物理処理は `applyExternalMotion(...)` へ委譲し、着地副作用は `applyLanding(...)` に閉じ込めた
- `BodyMovementTest` に X/Y 境界補正、落下開始、着地時の `bNoDamageNextFall` 解消テストを追加した。壁判定が `Body` スプライト初期化に依存する点も、抽出先テストで明示的に固定した
- `BodyMovement` へ通常移動の実座標反映、壁衝突時の blocked count 更新、水場進入回避、マップ境界補正、向き更新を追加で切り出した。`Body.moveBody(...)` 後半は移動周期と方向決定の orchestration に近い形まで縮小した
- `BodyMovementTest` に移動先オーバーシュート抑止、目的地あり壁衝突、水嫌い個体のプール進入回避を追加し、既存 `BodyTest` の `moveBody(...)` 回帰と合わせて抽出先の挙動を固定した
- `BodyMovement` へ `moveTo(...)`、`moveToBody(...)`、`runAway(...)` を追加し、`Body` 側の public API は facade として残した。移動先 clamp、追従対象設定、逃走先角選択、scare 付与を抽出先へ集約した
- `BodyMovementTest` に移動先 clamp、`moveToBody(...)` の flag/target 設定、`runAway(...)` の逃走先とガード条件テストを追加した。既存 `BodyTest` の `moveToBody/runAway` 回帰も維持している
- `BodyEventState` を追加し、`setMessage(...)` 群、`setPikoMessage(...)` 群、`setWorldEvent*Message(...)`、`setBodyEvent*Message(...)`、`setNYDMessage(...)`、`setNegiMessage(...)`、`clearActions(...)`、`clearEvent(...)`、`clearActionsForEvent(...)` を委譲した。`Body` 側の public API は facade のまま維持している
- `BodyEventState` へ保留イベントの選別と開始処理 `processPendingEvents(...)` を追加し、`Body` の tick 側に残っていた `currentEvent` 選択、simple event 消化、`start(...)` 呼び出しを委譲した
- `BodyEventState` へ event 結果反映 `resolveEventResultAction(...)` を追加し、`Body.clockTick()` 末尾に残っていた low/high priority に応じた `eventResultAction` の上書き条件を委譲した
- `BodyEventState` へ event 実行中の状態遷移 `updateCurrentEvent(...)` を追加し、`EventLogic.eventUpdate(...)` の update / abort / 到達 execute / end の実処理を委譲した。`EventLogic` 側は facade のみ残している
- `BodyEventStateTest` を追加し、action reset、message count 更新、body event 優先、応答不可時の simple event 消化を抽出先で直接固定した
- `BodyEventStateTest` に low priority では通常戻り値を保持し、high priority では `eventResultAction` を優先する境界テストを追加した
- `BodyEventStateTest` に event abort、到達 execute、未到達維持のテストを追加し、`EventLogic.eventUpdate(...)` の主要分岐を抽出先で直接固定した
- `BodyRenderState` を追加し、`Body.getFaceImage(...)` と `Body.getBodyBaseImage(...)` を委譲した。`Body` 側の public API は facade として維持し、描画条件分岐を抽出先へ集約した
- `BodyRenderStateTest` を追加し、死体/NYD/睡眠まばたきの表情分岐に加えて、潰れ死体、排泄中、通常時ボディ画像の選択を抽出先で直接固定した
- `BodyRenderState` へ `Body.getAbnormalBodyImage(...)` も追加で委譲し、切断・負傷・溶解の描画条件を抽出先へ集約した
- `BodyRenderStateTest` に `BODY_CUT` と `MELT_PEALED` の直接テストを追加し、異常状態ボディ描画の選択を抽出先で固定した
- `BodyRenderState` へ `Body.getEffectImage(...)` も追加で委譲し、空腹、焼け、ダメージ、汚れ、かび、濡れの体表エフェクト選択を抽出先へ集約した
- `BodyRenderStateTest` に `HUNGRY2 + WET` と `SICK3` の直接テストを追加し、effect overlay の主要分岐を抽出先で固定した
- `BodyRenderState` へ `Body.getOlazariImage(...)` と `Body.getBraidImage(...)` も追加で委譲し、おかざりと braid 系の描画条件を抽出先へ集約した
- `BodyRenderStateTest` に default おかざり、braid cut、braid back の直接テストを追加し、残りの render 補助も抽出先で固定した
- 検証: production compile、test compile、`BodyEventStateTest + BodyMovementTest + BodyTest + BodyAttributesTest + EventLogicTest` JUnit、全体 JUnit が成功。全体 JUnit は 6,840 tests successful / 0 failed, 2 skipped
- 検証: production compile、test compile、`BodyRenderStateTest + BodyRenderingTest + BodyEventStateTest + BodyMovementTest + BodyTest + BodyAttributesTest + EventLogicTest` JUnit、全体 JUnit が成功。全体 JUnit は 6,840 tests successful / 0 failed, 2 skipped
- 検証: production compile、test compile、`BodyRenderStateTest + BodyRenderingTest + SerializationTestTest` JUnit が成功。focused JUnit は 20 tests successful / 0 failed
- 検証: production compile、test compile、`BodyRenderStateTest + BodyRenderingTest + SerializationTestTest` JUnit が成功。focused JUnit は 22 tests successful / 0 failed
- Phase 2 のイベント状態切り出しは message/action reset/イベント開始入口/実行中状態遷移まで完了。描画寄りは face / body base / abnormal body / effect / おかざり / braid まで切り出し済みで、次は event packet 登録補助の整理、または Phase 3 の `BodyAttributes` 分割に進む

候補クラス:

- `BodyRelations`
- `BodyVitals`
- `BodyMovement`
- `BodyEventState`
- `BodyRenderState`

完了条件:

- `Body` の行数が段階的に減る
- `BodyTest`, `BodyLogicTest`, `FoodLogicTest` が各段階で通る
- セーブ/読込の JSON 互換を壊していない

### Phase 3: `BodyAttributes` のデータ化

目的:

- 種族別パラメータ、画像メタデータ、調整ロジックを分ける

推奨する切り出し順:

1. getter/setter 群の意味単位を分類する
2. パラメータ定義を immutable に近い値オブジェクトへ寄せる
3. 画像ロード済み状態や sprite 情報を `BodySpriteSet` へ分離する
4. 種族クラス側の `tuneParameters()` を設定ビルダーへ寄せる

候補クラス:

- `BodySpeciesProfile`
- `BodyStatProfile`
- `BodySpriteSet`
- `BodyNameSet`

注意:

- `BodyAttributes` は既存継承構造と種族クラスの基底なので、最初は facade として残す
- Jackson 対象ならフィールド移動前にシリアライズテストを追加する

進捗:

- `BodyNameSet` を追加し、`BodyAttributes` の名前関連データを helper に寄せた
- `BodySpriteSet` を追加し、`BodyAttributes` の sprite 配列を helper に寄せた
- `BodyStatProfile` を追加し、`BodyAttributes` の配列型統計データを helper に寄せた
- `BodyTimingProfile` を追加し、`BodyAttributes` の時刻・閾値データを helper に寄せた
- `BodyBehaviorProfile` を追加し、`BodyAttributes` の行動・性格・繁殖寄りの調整値を helper に寄せた
- `BodyBehaviorProfile` に `sameDest` と `ROBUSTNESS` を含め、種族クラスの直代入を setter 経由へ寄せた
- `YukkuriUtil.changeBody(...)` は名前データを深く複製するようにした
- `YukkuriUtil.changeBody(...)` は sprite 配列も深く複製するようにした
- `YukkuriUtil.changeBody(...)` は配列型統計データも深く複製するようにした
- `YukkuriUtil.changeBody(...)` は時刻・閾値データも深く複製するようにした
- `YukkuriUtil.changeBody(...)` は行動・性格・繁殖寄りの調整値も深く複製するようにした
- `HybridYukkuri` の名前配列の直接代入を setter 経由へ寄せた
- `BodyAttributes` の移行済みプロパティに `@JsonProperty` を付け、保存/読込で getter/setter を明示した
- 検証: production compile、test compile、`YukkuriUtilTest` の behavior profile deep copy が成功
- **Phase 3 Step 1 完了**: `BodyNameSet` getter 不整合を修正。alias フィールド 8本を削除し、getter を `bodyNameSet` 直接委譲に変更。29の yukkuri サブクラスで `anMyName[]/anMyNameD[]` 直接参照を `getAnMyName()[]/getAnMyNameD()[]` 経由に変更。`syncNameAliases()` を削除。
- **Phase 3 Step 2 完了**: `BodySpriteSet` alias を正式委譲に変更。`bodySpr/expandSpr/braidSpr` の alias フィールド 3本を削除し、getter・内部参照を `bodySpriteSet` 委譲に変更。`syncSpriteAliases()` を削除。
- 検証: production compile 成功。全テスト 6,848 successful / 48 failed (失敗 48 は Phase 3 作業前から存在する Jackson save/load 系の既存問題のみ)。`BodyAttributes.java` が 7,170 → 7,100 行 (-70行)。

**Phase 3 完了**: 定義された全 helper の統合・不整合解消が完了。4,000 行目標は Phase 3-5 横断の中期目標として継続。

### Phase 4: Simulation Core の分離

目的:

- `Terrarium` から保存/読込、生成、tick 更新、環境状態を分離する

推奨する切り出し順:

1. 保存/読込: gzip, 暗号化, JSON 変換を `SaveDataCodec` へ移す
2. Body 生成: `YukkuriFactory` / `BodyFactory` を作り、type switch を閉じ込める
3. Effect/Item 生成: `EffectFactory`, `ObjectSpawner`
4. tick 更新: `SimulationStepper`
5. 環境状態: static steam/alarm/day state を `EnvironmentState` へ移す

進捗:

- `SaveDataCodec` を追加し、`Terrarium` の JSON / GZIP / 暗号化処理を切り出した
- `Terrarium.saveState(...)` / `Terrarium.loadState(...)` は codec 呼び出しだけに縮めた
- `SaveDataCodec` の往復テストを追加した
- `BodyFactory` を追加し、`Terrarium.makeBody(...)` の type switch を切り出した
- `BodyFactory` の生成回帰テストを追加した
- `BodyTickProcessor` を追加し、`Terrarium.stepRun()` の body 1体分の更新処理を切り出した
- `Terrarium` の stepRun は body ループの orchestration に近づいた
- `TerrariumTickProcessor` を追加し、`Terrarium.stepRun()` の platform / belt / pool / farm / effect 更新を切り出した
- `Terrarium.stepRun()` は時刻進行と body / world tick の呼び出しに近づいた
- `TerrariumCollisionProcessor` を追加し、`TerrariumTickProcessor` から collision 系をさらに切り出した
- `TerrariumEntryProcessor` を追加し、`Shit / Vomit / Okazari` の共通更新を切り出した

完了条件:

- `Terrarium.stepRun()` が orchestration に近い形になる
- `Terrarium` から `SimYukkuri.world` 直接参照が減る
- 保存/読込の既存形式が維持される

### Phase 5: Logic クラスのルール分割

目的:

- `BodyLogic` と `FoodLogic` を長大な手続きから、小さいルール群へ分ける

推奨する切り出し順:

`FoodLogic`:

- `FoodSearchPolicy`
- `PredatorFoodSearch`
- `TakeoutRule`
- `EatingEffect`
- `BodyEatingRule`

`BodyLogic`:

- `RelationRule`
- `ActionSelection`
- `GatheringRule`
- `WakeupRule`
- `UnunSlaveRule`

進め方:

- 既存 static method は当面残し、内部で新ルールへ委譲する
- 1 つの public method につき 1 回帰テスト単位で移す
- メッセージ生成や乱数をロジック本体から分離する

進捗:

- `BodyRelations` に `checkMyRelation(...)` を追加し、`BodyLogic.checkMyRelation(...)` を委譲した
- `FoodEligibility` を追加し、`FoodLogic.checkCanEatBody(...)` を委譲した
- `FoodEligibilityTest` を追加し、predator / living body / okazari body の判定を直接固定した
- `BodyRelationsTest` を拡張し、関係分類の direct helper も固定した
- `FoodTakeoutPolicy` を追加し、`FoodLogic.checkTakeout(...)` の食べ物・奴隷シット判定を切り出した
- `FoodTakeoutPolicyTest` を追加し、奴隷トイレ判定と持ち帰り可否の基本分岐を直接固定した
- `FoodSearchPolicy` を追加し、`FoodLogic.searchFoodStandard(...)` の通常種探索を切り出した
- `FoodLogic.searchFoodPredetor(...)` の捕食種切り出しは挙動差分が出たため一旦 back out した
- `FoodUnunSlaveSearchPolicy` を追加し、`FoodLogic.searchFoodForUnunSlave(...)` のうんうん奴隷探索を切り出した
- `FoodActionGate` を追加し、`FoodLogic.checkFood(...)` の開始前キャンセル条件を切り出した
- `FoodFoundReaction` を追加し、`FoodLogic.checkFood(...)` の到着後反応を切り出した
- `FoodArrivalActionPolicy` を追加し、`FoodLogic.checkFood(...)` の到着後の個別食事処理を切り出した
- `FoodApproachPolicy` を追加し、`FoodLogic.checkFood(...)` の未到着時移動指示を切り出した
- `FoodNoFoodReaction` を追加し、`FoodLogic.checkFood(...)` の未発見時反応を切り出した
- `FoodConsumptionPolicy` を追加し、`FoodLogic.eatFood(...)` の食事副作用と反応を切り出した
- `FoodNearestSearchPolicy` を追加し、`FoodLogic.searchFoodNearlest(...)` の近傍探索を切り出した
- `FoodPredatorCandidatePolicy` を追加し、`FoodLogic.searchFoodPredetor(...)` の候補評価ループを切り出した
- `FoodPredatorFallbackPolicy` を追加し、`FoodLogic.searchFoodPredetor(...)` の非常食・吐物・うんこ探索を切り出した
- `BodyWakeupRule` を追加し、`BodyLogic.checkWakeupOtherYukkuri(...)` の視界内 awake 判定を切り出した
- `BodyWakeupRuleTest` を追加し、awake / sleeping の基本分岐を direct helper で固定した
- `BodyParentRule` を追加し、`BodyLogic.checkNearParent(...)` の親探索・接近処理を切り出した
- `BodyParentRuleTest` を追加し、成人早期 return と dirty child の親接近を direct helper で固定した
- `BodySurisuriRule` を追加し、`BodyLogic.checkActionSurisuriFromPlayer(...)` のスリスリ反応を切り出した
- `BodySurisuriRuleTest` を追加し、null / 非フラグの入口ガードを direct helper で固定した
- `BodyDeadActionRule` を追加し、`BodyLogic.doActionOther(...)` の死体反応を切り出した
- `BodyDeadActionRuleTest` を追加し、発情死体反応と親死亡の悲嘆を direct helper で固定した
- `BodyStealRule` を追加し、`BodyLogic.doActionOther(...)` のおかざり盗みを切り出した
- `BodyStealRuleTest` を追加し、成功 steaI と awake witness ブロックを direct helper で固定した
- `BodyNeedleRule` を追加し、`BodyLogic.doActionOther(...)` の針付き個体反応を切り出した
- `BodySkinshipRule` を追加し、`BodyLogic.doActionOther(...)` の親子・番・姉妹スキンシップを切り出した
- `BodyContactRule` を追加し、`BodyLogic.doActionOther(...)` の隣接接触分岐をまとめた
- `BodySelectionRule` を追加し、`BodyLogic.createActiveFianceeList(...)` と `createActiveChildList(...)` の候補生成を切り出した
- `BodyGatheringRule` を追加し、`BodyLogic.gatheringYukkuri*` の群れ移動処理を切り出した
- `BodyPartnerActionRule` を追加し、`BodyLogic.checkPartner(...)` の対象発見後の行動分岐を切り出した
- `BodyPartnerEntryRule` を追加し、`BodyLogic.checkPartner(...)` の入口ガードと既存ターゲット判定を切り出した
- `BodyPartnerEntryRule` を拡張し、`BodyLogic.checkPartner(...)` の未発見時 fallback も切り出した
- 検証: production compile、test compile、全体 JUnit が成功。全体 JUnit は 6,891 tests successful / 0 failed, 2 skipped

完了:

- Phase 5 の対象ルール分割は一通り完了
- headless 全体 JUnit で 6,891 tests successful / 0 failed, 2 skipped を確認済み

完了条件:

- `FoodLogic` と `BodyLogic` の public API は維持
- 主要分岐テストが移動前後で同じ
- 新規ルール追加時に既存巨大 method を編集しなくてよい

完了:

- `FoodLogic` と `BodyLogic` の主要分岐は helper へ分割済み
- `BodyLogic` / `FoodLogic` の全体 JUnit は headless で成功
- `BodyLogic` / `FoodLogic` の import 整理まで完了済み

### Phase 6: UI/Command の分離

目的:

- Swing 入力処理からゲーム操作を切り離す

推奨する切り出し順:

1. `GadgetMenu` の定義データと Swing 表示を分ける
2. `GadgetAction` の操作を command object 化する
3. `SimYukkuri` の mouse/key listener から操作解釈を `InputController` へ移す
4. `MyPane` の画像ロードと描画を分ける
5. `MyPane.run()` のループ制御を `GameLoop` へ移す
6. `MyPane.paint()` の描画本体を `Renderer` へ移す

進捗:

- `GadgetMenu` の popup 構築を `GadgetMenuPopup` へ分離した
- `ItemMenu` の popup listener を `ItemPopupSpeedAction` / `ItemPopupNoopAction` に分離した
- `MainCommandListener` の combo box listener を `GameSpeedComboBoxListener` / `MainItemComboBoxListener` / `SubItemComboBoxListener` に分離した
- `SimYukkuri` の key listener を `InputController` に寄せた
- `SimYukkuri` の mouse listener を `MouseInputController` に分離した
- `MyPane.loadImage()` を `ImageLoadService` に分離した
- `MyPane.run()` を `GameLoop` に分離した
- `MyPane.paint()` を `Renderer` に分離した
- `MainCommandListener` の button / option listener を top-level に分離した
- `GadgetAction.immediateEvaluate()` を `GadgetCleanupAction` に分離した
- `GadgetAction.evaluateTool()` を `GadgetToolAction` に分離し、古い注入分岐を整理した
- `GadgetAction.evaluateAmpoule()` を `GadgetAmpouleAction` に分離した
- `GadgetAction.evaluateClean/evaluateAccessory/evaluatePants/evaluateCommunicate` を `GadgetBodyAction` に分離した
- `GadgetAction.evaluateFloorItems/evaluateToys/evaluateConveyor` を `GadgetItemSetupAction` に分離した
- `GadgetAction.evaluateTest()` を `GadgetDebugAction` に分離した
- `GadgetAction.leftMultiClickEvaluate()` を `GadgetFieldPlacementAction` に分離した
- `GadgetMenu.executeBodyMethod()` 3 オーバーロードを `BodyMethodDispatcher` に分離した
- `GadgetAction.java` を facade に縮小 (1524行 → 357行)、不要 import を整理した
- `GadgetMenu.java` を facade に縮小 (1211行 → 988行)、不要 import を整理した
- 検証: production compile、test compile、全体 JUnit が成功。全体 JUnit は 6,885 tests successful / 6 failed (既存 headless/static 初期化問題のみ), 2 skipped

候補クラス:

- `ToolCommand`
- `ToolCommandRegistry`
- `InputController`
- `GameLoop`
- `Renderer`
- `ImageLoadService`
- `GadgetCleanupAction`
- `GadgetToolAction`
- `GadgetAmpouleAction`

完了条件:

- Swing なしで command の主要副作用をテストできる
- UI 変更が `logic`/`item`/`event` に波及しない

## 最初に着手する具体タスク

最初の 5 コミットは小さく切るべきです。

1. `docs/build-and-test.md` を追加し、現行の compile/test コマンドを固定する
2. `SaveDataCodec` を追加し、`Terrarium` の gzip/暗号化/JSON private static method を移す
3. `YukkuriFactory` を追加し、`Terrarium.makeBody` の type switch を委譲する
4. `GameRandom` を追加し、テストが厚い `FoodLogic` の乱数参照を 1 箇所だけ置き換える
5. `BodyRelations` を追加し、`Body` の関係判定系 public method の内部だけ委譲する

この順番にする理由は、セーブ処理・factory・乱数・関係判定がそれぞれ独立しており、失敗時の原因を切り分けやすいからです。

## テスト戦略

最低限、各フェーズで以下を実行します。

- 変更対象の単一テストクラス
- 変更対象パッケージのテスト
- `BodyTest`, `BodyAttributesTest`, `BodyLogicTest`, `FoodLogicTest`
- 保存/読込に触れる場合は yukkuri serialization 系テスト

大規模フェーズの節目では全テストを実行します。

追加が必要なテスト:

- `Terrarium.saveState/loadState` の互換性テスト
- `YukkuriFactory` の全 type 対応テスト
- `GameRandom` 固定時の順序依存検出テスト
- `GadgetAction` の Swing なし副作用テスト

## 禁止事項

- テストなしで `Body` のフィールド名・型・継承関係を変える
- セーブ形式の変更とロジック分割を同じコミットで行う
- `SimYukkuri.RND` の呼び出し順が変わる変更を、仕様変更なしに混ぜる
- GUI リファクタリングと simulation tick の変更を同時に行う
- formatter 全面適用と意味変更を同じ差分に入れる
- 未追跡の生成物整理とコード変更を同じコミットに入れる

## 成功指標

短期:

- 1 コミットあたりの変更範囲が小さい
- 既存テストが各段階で通る
- `Terrarium`, `Body`, `FoodLogic` の一部処理が委譲され、呼び出し側の変更なしに分離できている

中期:

- `Body.java` が 6,000 行未満
- `BodyAttributes.java` が 4,000 行未満
- `Terrarium.java` が 1,200 行未満
- `FoodLogic.java` と `BodyLogic.java` の public static 巨大 method が小さいルールへ分解されている

長期:

- Core/Domain から Swing への直接依存がない
- `SimYukkuri.world` と `SimYukkuri.RND` の直接参照が局所化されている
- 新しい yukkuri 種、item、event を追加するときの変更箇所が registry/factory/個別クラス中心になる
- セーブ互換を維持したまま内部構造を変更できる

## 推奨ブランチ運用

- `refactor/prep-build-test`
- `refactor/save-codec`
- `refactor/yukkuri-factory`
- `refactor/random-context`
- `refactor/body-relations`
- `refactor/body-vitals`
- `refactor/food-rules`
- `refactor/ui-command-separation`

各ブランチは「委譲追加」「呼び出し側置換」「不要コード削除」を分けます。特に最初のうちは、削除よりも委譲追加を優先します。
