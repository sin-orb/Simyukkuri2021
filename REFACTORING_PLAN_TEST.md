# REFACTORING_PLAN_TEST

テストを**メソッド粒度**で整理した。
カバレッジではなく、**各テストメソッドがどんな回帰を防ぐか** を基準に見直している。

- 対象ファイル数: 231
- 対象テストメソッド数: 6185
- main ソースには触れていない

## まず結論
- `残す候補`: `良い`
- `要修正候補`: `不足` と `ダメ`
- `ハーネス/fixture`: テスト本体ではなく補助なので別枠

## 見方
- `何を保証するか`: そのメソッドが壊れたときに止めたい回帰
- `足りているか`: その保証を本当に担えているか
- `良い`: 具体的な回帰を止めていて、残す価値がある
- `不足`: 目的はあるが、assert/条件/対象範囲が足りない
- `ダメ`: smoke/no-throw/getter-setter確認に寄りすぎていて、回帰保証として弱い
- `ハーネス/fixture`: テスト本体ではなく補助
- `値オブジェクト往復確認`: `Color4y` / `Point4y` のような純データの全成分往復を保証するテスト
- `状態保存復元確認`: `Terrarium` / `Yukkuri` / `SaveDataCodec` のような複合状態の永続化・復元を保証するテスト

## ハーネス/fixture
- `org.simyukkuri.logic.UnunSlaveDebug`

## `org.simyukkuri`
### `ConstTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 座標変換や幾何値が壊れない
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDirections` | directions | 基礎回帰 / directions | 良い | - | - |
| `testWindowColors` | window / colors | 基礎回帰 / window / colors | 良い | - | - |
| `testNegiWindowColor` | negi / window / color | 基礎回帰 / negi / window / color | 良い | - | - |
| `testDamageValues` | ダメージ / values | 基礎回帰 / ダメージ / values | 良い | - | - |
| `testTimeConstants` | time / constants | 基礎回帰 / time / constants | 良い | - | - |
| `testBodySize` | 本体 / size | 基礎回帰 / 本体 / size | 良い | - | - |
| `testStalkOffsets` | stalk / offsets | 基礎回帰 / stalk / offsets | 良い | - | - |
| `testLimits` | limits | 基礎回帰 / limits | 良い | - | - |
| `testIndices` | indices | 基礎回帰 / indices | 良い | - | - |
| `testWindowStroke` | window / stroke | 基礎回帰 / window / stroke | 良い | - | - |

## `org.simyukkuri.command`
### `GadgetActionTest`
- 状態: 完了 (29/29 良い)
- クラス要約: `コマンド/メニュー/道具実行回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 座標変換や幾何値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testYuCleanSetsCleaningOnLiveBodies` | yu / clean / sets / cleaning / on / live / bodies | コマンド/メニュー/道具実行回帰 / yu / clean / sets / cleaning / on / live / bodies | 良い | - | - |
| `testYuCleanDoesNotRemoveDeadBodies` | yu / clean / does / 非 / 除去 / 死亡 / bodies | コマンド/メニュー/道具実行回帰 / yu / clean / does / 非 / 除去 / 死亡 / bodies | 良い | - | - |
| `testBodyRemovesDeadBodies` | 本体 / removes / 死亡 / bodies | コマンド/メニュー/道具実行回帰 / 本体 / removes / 死亡 / bodies | 良い | - | - |
| `testShitRemovesShitAndVomit` | shit / removes / shit / and / vomit | コマンド/メニュー/道具実行回帰 / shit / removes / shit / and / vomit | 良い | - | - |
| `testAllRemovesDeadAndShitButNotLive` | all / removes / 死亡 / and / shit / but / 非 / live | コマンド/メニュー/道具実行回帰 / all / removes / 死亡 / and / shit / but / 非 / live | 良い | - | - |
| `testEtcRemovesEmptyFood` | etc / removes / empty / food | コマンド/メニュー/道具実行回帰 / etc / removes / empty / food | 良い | - | - |
| `testPunishStrikesBody` | punish / strikes / 本体 | コマンド/メニュー/道具実行回帰 / punish / strikes / 本体 | 良い | - | - |
| `testSnappingKicksBody` | snapping / kicks / 本体 | コマンド/メニュー/道具実行回帰 / snapping / kicks / 本体 | 良い | - | - |
| `testVibratorExcitesBody` | vibrator / excites / 本体 | コマンド/メニュー/道具実行回帰 / vibrator / excites / 本体 | 良い | - | - |
| `testOrangeAmpouleAddsAttachment` | orange / ampoule / adds / attachment | コマンド/メニュー/道具実行回帰 / orange / ampoule / adds / attachment | 良い | - | - |
| `testPoisonAmpouleAddsAttachment` | poison / ampoule / adds / attachment | コマンド/メニュー/道具実行回帰 / poison / ampoule / adds / attachment | 良い | - | - |
| `testTakeItEasySetsMessage` | take / it / easy / sets / メッセージ | コマンド/メニュー/道具実行回帰 / take / it / easy / sets / メッセージ | 良い | - | - |
| `testIndividualRemovesDeadBody` | individual / removes / 死亡 / 本体 | コマンド/メニュー/道具実行回帰 / individual / removes / 死亡 / 本体 | 良い | - | - |
| `testIndividualSetsCleaningOnLiveBody` | individual / sets / cleaning / on / live / 本体 | コマンド/メニュー/道具実行回帰 / individual / sets / cleaning / on / live / 本体 | 良い | - | - |
| `testIndividualRemovesNonBodyObj` | individual / removes / non / 本体 / obj | コマンド/メニュー/道具実行回帰 / individual / removes / non / 本体 / obj | 良い | - | - |
| `testNormalClickTakeOkazariWhenHasOkazari` | normal / click / take / okazari / when / 有無 / okazari | コマンド/メニュー/道具実行回帰 / normal / click / take / okazari / when / 有無 / okazari | 良い | - | - |
| `testNormalClickGiveOkazariWhenNoOkazari` | normal / click / give / okazari / when / なし / okazari | コマンド/メニュー/道具実行回帰 / normal / click / give / okazari / when / なし / okazari | 良い | - | - |
| `testShiftAppliesBasedOnTarget` | shift / applies / based / on / target | コマンド/メニュー/道具実行回帰 / shift / applies / based / on / target | 良い | - | - |
| `testCtrlInvertsAll` | ctrl / inverts / all | コマンド/メニュー/道具実行回帰 / ctrl / inverts / all | 良い | - | - |
| `testNormalClickGivePantsWhenNoPants` | normal / click / give / pants / when / なし / pants | コマンド/メニュー/道具実行回帰 / normal / click / give / pants / when / なし / pants | 良い | - | - |
| `testNormalClickTakePantsWhenHasPants` | normal / click / take / pants / when / 有無 / pants | コマンド/メニュー/道具実行回帰 / normal / click / take / pants / when / 有無 / pants | 良い | - | - |
| `testCtrlInvertsAllPants` | ctrl / inverts / all / pants | コマンド/メニュー/道具実行回帰 / ctrl / inverts / all / pants | 良い | - | - |
| `testRankSetTogglesKaiyuToNorayu` | rank / 設定 / toggles / kaiyu / to / norayu | コマンド/メニュー/道具実行回帰 / rank / 設定 / toggles / kaiyu / to / norayu | 良い | - | - |
| `testRankSetTogglesNorayuToKaiyu` | rank / 設定 / toggles / norayu / to / kaiyu | コマンド/メニュー/道具実行回帰 / rank / 設定 / toggles / norayu / to / kaiyu | 良い | - | - |
| `testSetVainCallsGetInVain` | 設定 / vain / calls / 取得 / in / vain | コマンド/メニュー/道具実行回帰 / 設定 / vain / calls / 取得 / in / vain | 良い | - | - |
| `testFeedCallsFeedOnLiveBody` | feed / calls / feed / on / live / 本体 | コマンド/メニュー/道具実行回帰 / feed / calls / feed / on / live / 本体 | 良い | - | - |
| `testFeedDoesNothingOnDeadBody` | feed / does / nothing / on / 死亡 / 本体 | コマンド/メニュー/道具実行回帰 / feed / does / nothing / on / 死亡 / 本体 | 良い | - | - |
| `testInviteAntsIgnoresShiftAndCtrl` | invite / ants / ignores / shift / and / ctrl | コマンド/メニュー/道具実行回帰 / invite / ants / ignores / shift / and / ctrl | 良い | - | - |
| `testInviteAntsIgnoresCtrl` | invite / ants / ignores / ctrl | コマンド/メニュー/道具実行回帰 / invite / ants / ignores / ctrl | 良い | - | - |

### `GadgetMenuChoiceTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `コマンド/メニュー/道具実行回帰`
- 回帰目的:
  - メニュー構成とカテゴリ順が壊れない
  - 主要項目の group / target / class / option 定義が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMainCategoryMatchesExpectedOrder` | main / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / main / category / matches / expected / order | 良い | - | - |
| `testToolCategoryMatchesExpectedOrder` | tool / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / tool / category / matches / expected / order | 良い | - | - |
| `testToolCategory2MatchesExpectedOrder` | tool2 / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / tool2 / category / matches / expected / order | 良い | - | - |
| `testAmpouleCategoryMatchesExpectedOrder` | ampoule / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / ampoule / category / matches / expected / order | 良い | - | - |
| `testFoodCategoryMatchesExpectedOrder` | food / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / food / category / matches / expected / order | 良い | - | - |
| `testCleanCategoryMatchesExpectedOrder` | clean / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / clean / category / matches / expected / order | 良い | - | - |
| `testBarrierCategoryMatchesExpectedOrder` | barrier / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / barrier / category / matches / expected / order | 良い | - | - |
| `testToysCategoryMatchesExpectedOrder` | toys / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / toys / category / matches / expected / order | 良い | - | - |
| `testConveyorCategoryMatchesExpectedOrder` | conveyor / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / conveyor / category / matches / expected / order | 良い | - | - |
| `testVoiceCategoryMatchesExpectedOrder` | voice / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / voice / category / matches / expected / order | 良い | - | - |
| `testTestCategoryMatchesExpectedOrder` | test / category / matches / expected / order | コマンド/メニュー/道具実行回帰 / test / category / matches / expected / order | 良い | - | - |
| `testImportantEntriesHaveExpectedDefinitions` | important / entries / have / expected / definitions | コマンド/メニュー/道具実行回帰 / important / entries / have / expected / definitions | 良い | - | - |

### `GadgetMenuTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `コマンド/メニュー/道具実行回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - メニュー構成と選択状態が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testActionTargetFlags` | action / target / flags | コマンド/メニュー/道具実行回帰 / action / target / flags | 良い | - | - |
| `testHelpContextToStringMatchesLoadedText` | help / context / to / string / matches / loaded / text | コマンド/メニュー/道具実行回帰 / help / context / to / string / matches / loaded / text | 良い | - | - |
| `testHelpIconProperties` | help / icon / properties | コマンド/メニュー/道具実行回帰 / help / icon / properties | 良い | - | - |
| `testCategoryArraysContainExpectedItems` | category / arrays / contain / expected / items | コマンド/メニュー/道具実行回帰 / category / arrays / contain / expected / items | 良い | - | - |
| `testPopupMenuBuildsCategories` | popup / menu / builds / categories | コマンド/メニュー/道具実行回帰 / popup / menu / builds / categories | 良い | - | - |

### `GadgetToolTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `コマンド/メニュー/道具実行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDoGodHandDoesNothingWhenDead` | do / god / hand / does / nothing / when / 死亡 | コマンド/メニュー/道具実行回帰 / do / god / hand / does / nothing / when / 死亡 | 良い | - | - |
| `testCase0RapistToggleWhenCannotTransform` | case0 / rapist / 切替 / when / cannot / transform | コマンド/メニュー/道具実行回帰 / case0 / rapist / 切替 / when / cannot / transform | 良い | - | - |
| `testCase1BodyCut` | case1 / 本体 / cut | コマンド/メニュー/道具実行回帰 / case1 / 本体 / cut | 良い | - | - |
| `testCase2Stretch` | case2 / stretch | コマンド/メニュー/道具実行回帰 / case2 / stretch | 良い | - | - |
| `testCase2StretchFromCompress` | case2 / stretch / from / compress | コマンド/メニュー/道具実行回帰 / case2 / stretch / from / compress | 良い | - | - |
| `testCase3Compress` | case3 / compress | コマンド/メニュー/道具実行回帰 / case3 / compress | 良い | - | - |
| `testCase3CompressFromStretch` | case3 / compress / from / stretch | コマンド/メニュー/道具実行回帰 / case3 / compress / from / stretch | 良い | - | - |
| `testCase4Heal` | case4 / heal | コマンド/メニュー/道具実行回帰 / case4 / heal | 良い | - | - |
| `testCase5LanguageBreakReimu` | case5 / language / break / reimu | コマンド/メニュー/道具実行回帰 / case5 / language / break / reimu | 良い | - | - |
| `testDefaultKickAndInflate` | default / kick / and / inflate | コマンド/メニュー/道具実行回帰 / default / kick / and / inflate | 良い | - | - |
| `testDefaultSecondTimeExplosion` | default / second / time / explosion | コマンド/メニュー/道具実行回帰 / default / second / time / explosion | 良い | - | - |

## `org.simyukkuri.draw`
### `Color4yTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `値オブジェクト往復回帰`
- 回帰目的:
  - RGBA 全成分の往復が壊れない
  - 0/255 の境界とクランプが壊れない
  - toString と serialization が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | デフォルトコンストラクタで全成分が0になる | 値オブジェクト往復回帰 / default / constructor | 良い | - | displayNameあり |
| `testParameterizedConstructor` | パラメータ付きコンストラクタでRGBA順に値がセットされる | 値オブジェクト往復回帰 / parameterized / constructor | 良い | - | displayNameあり |
| `testSetRed` | setRedでred成分だけを変更できる | 値オブジェクト往復回帰 / 設定 / red | 良い | - | displayNameあり |
| `testSetGreen` | setGreenでgreen成分だけを変更できる | 値オブジェクト往復回帰 / 設定 / green | 良い | - | displayNameあり |
| `testSetBlue` | setBlueでblue成分だけを変更できる | 値オブジェクト往復回帰 / 設定 / blue | 良い | - | displayNameあり |
| `testSetAlpha` | setAlphaでalpha成分だけを変更できる | 値オブジェクト往復回帰 / 設定 / alpha | 良い | - | displayNameあり |
| `testToString` | toStringが正しいフォーマットを返す | 値オブジェクト往復回帰 / to / string | 良い | - | displayNameあり |
| `testSerialization` | シリアライズ・デシリアライズできる | 値オブジェクト往復回帰 / serialization | 良い | - | displayNameあり |
| `testClampsOutOfRangeValues` | コンストラクタでもsetterでも範囲外値はクランプされる | 値オブジェクト往復回帰 / clamp / out / range | 良い | - | displayNameあり |
| `testTypicalColors` | 赤・緑・青・白・黒の各成分が正確にセットされる | 値オブジェクト往復回帰 / typical / colors | 良い | - | - |
| `testAlphaRange` | alpha 0/128/255 の境界値が正確にセットされる | 値オブジェクト往復回帰 / alpha / range | 良い | - | - |
| `testValuesOver255Clamped` | コンストラクタで 255 超えはクランプされる | 値オブジェクト往復回帰 / clamp / over / 255 | 良い | - | - |
| `testNegativeValuesClamped` | コンストラクタで負値は 0 にクランプされる | 値オブジェクト往復回帰 / clamp / negative | 良い | - | - |
| `testSetterClampsOver255` | setter 経由で 255 超えはクランプされる | 値オブジェクト往復回帰 / setter / clamp / over / 255 | 良い | - | - |
| `testSetterClampsNegative` | setter 経由で負値は 0 にクランプされる | 値オブジェクト往復回帰 / setter / clamp / negative | 良い | - | - |
| `testBoundaryValues` | 境界値 0 と 255 が正確に保持される | 値オブジェクト往復回帰 / boundary / values | 良い | - | - |

### `Dimension4yTest`
- 状態: 完了 (9/9 良い)
- クラス要約: `値オブジェクト往復回帰`
- 回帰目的:
  - width / height の往復が壊れない
  - toString と serialization が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | デフォルトコンストラクタでwidth=0, height=0になる | 値オブジェクト往復回帰 / default / constructor | 良い | - | displayNameあり |
| `testParameterizedConstructor` | パラメータ付きコンストラクタで指定値がセットされる | 値オブジェクト往復回帰 / parameterized / constructor | 良い | - | displayNameあり |
| `testNegativeValues` | 負の値もセットできる（通常は使わないが） | 値オブジェクト往復回帰 / negative / values | 良い | - | displayNameあり |
| `testSetWidth` | setWidthでwidthを変更できる | 値オブジェクト往復回帰 / 設定 / width | 良い | - | displayNameあり |
| `testSetHeight` | setHeightでheightを変更できる | 値オブジェクト往復回帰 / 設定 / height | 良い | - | displayNameあり |
| `testSetters` | setterメソッドを使用して値を変更できる | 値オブジェクト往復回帰 / setters | 良い | - | displayNameあり |
| `testToString` | toStringが正しいフォーマットを返す | 値オブジェクト往復回帰 / to / string | 良い | - | displayNameあり |
| `testSerialization` | シリアライズ・デシリアライズできる | 値オブジェクト往復回帰 / serialization | 良い | - | displayNameあり |
| `testExtremeValues` | Integer.MAX_VALUEも扱える | 値オブジェクト往復回帰 / extreme / values | 良い | - | displayNameあり |

### `ModLoaderTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `設定/テーマ/メッセージファイル回帰`
- 回帰目的:
  - jar path / theme / default dir の設定と参照が壊れない
  - メッセージファイルのオープン経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSetAndGetJarPath` | 設定 / and / 取得 / jar / path | 設定/テーマ/メッセージファイル回帰 / jar / path | 良い | - | - |
| `testGetDefaultDirs` | 取得 / default / dirs | 設定/テーマ/メッセージファイル回帰 / default / dirs | 良い | - | - |
| `testThemePaths` | theme / paths | 設定/テーマ/メッセージファイル回帰 / theme / paths | 良い | - | - |
| `testGetThemeList` | 取得 / theme / list | 設定/テーマ/メッセージファイル回帰 / theme / list | 良い | - | - |
| `testOpenMessageFileNonExistent` | open / メッセージ / file / non / existent | 設定/テーマ/メッセージファイル回帰 / open / メッセージ / file / non / existent | 良い | - | - |
| `testOpenMessageFileDevelopmentMode` | open / メッセージ / file / development / mode | 設定/テーマ/メッセージファイル回帰 / open / メッセージ / file / development / mode | 良い | - | - |

### `MyPaneDialogTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `ダイアログ応答回帰`
- 回帰目的:
  - ダイアログ応答が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `shouldProceedAfterAddDialog_onlyAcceptsOk` | should / proceed / after / 追加 / dialog / only / accepts / ok | ダイアログ応答回帰 | 良い | - | - |

### `Point4yTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `値オブジェクト往復回帰`
- 回帰目的:
  - x / y 座標の往復が壊れない
  - toString と serialization が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | デフォルトコンストラクタでx=0, y=0になる | 値オブジェクト往復回帰 / default / constructor | 良い | - | displayNameあり |
| `testParameterizedConstructor` | パラメータ付きコンストラクタで指定値がセットされる | 値オブジェクト往復回帰 / parameterized / constructor | 良い | - | displayNameあり |
| `testNegativeValues` | 負の値もセットできる | 値オブジェクト往復回帰 / negative / values | 良い | - | displayNameあり |
| `testSetX` | setXでx座標を変更できる | 値オブジェクト往復回帰 / 設定 / x | 良い | - | displayNameあり |
| `testSetY` | setYでy座標を変更できる | 値オブジェクト往復回帰 / 設定 / y | 良い | - | displayNameあり |
| `testSettersUpdateCoordinatesIndependently` | setXとsetYでそれぞれの座標だけが変わる | 値オブジェクト往復回帰 / direct / field / access | 良い | - | displayNameあり |
| `testToString` | toStringが正しいフォーマットを返す | 値オブジェクト往復回帰 / to / string | 良い | - | displayNameあり |
| `testToStringNegative` | toStringで負の値も正しく表示される | 値オブジェクト往復回帰 / to / string / negative | 良い | - | displayNameあり |
| `testSerialization` | シリアライズ・デシリアライズできる | 値オブジェクト往復回帰 / serialization | 良い | - | displayNameあり |
| `testExtremeValues` | Integer.MAX_VALUEやMIN_VALUEも扱える | 値オブジェクト往復回帰 / extreme / values | 良い | - | displayNameあり |

### `Rectangle4yTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `値オブジェクト往復回帰`
- 回帰目的:
  - 位置とサイズの往復が壊れない
  - toString と serialization が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | デフォルトコンストラクタで全フィールドが0になる | 値オブジェクト往復回帰 / default / constructor | 良い | - | displayNameあり |
| `testParameterizedConstructor` | パラメータ付きコンストラクタで指定値がセットされる | 値オブジェクト往復回帰 / parameterized / constructor | 良い | - | displayNameあり |
| `testNegativePosition` | 負の座標値もセットできる | 値オブジェクト往復回帰 / negative / position | 良い | - | displayNameあり |
| `testSetX` | setXでx座標を変更できる | 値オブジェクト往復回帰 / 設定 / x | 良い | - | displayNameあり |
| `testSetY` | setYでy座標を変更できる | 値オブジェクト往復回帰 / 設定 / y | 良い | - | displayNameあり |
| `testSetWidth` | setWidthでwidthを変更できる | 値オブジェクト往復回帰 / 設定 / width | 良い | - | displayNameあり |
| `testSetHeight` | setHeightでheightを変更できる | 値オブジェクト往復回帰 / 設定 / height | 良い | - | displayNameあり |
| `testSetters` | setterメソッドを使用して値を変更できる | 値オブジェクト往復回帰 / setters | 良い | - | displayNameあり |
| `testToString` | toStringが正しいフォーマットを返す | 値オブジェクト往復回帰 / to / string | 良い | - | displayNameあり |
| `testSerialization` | シリアライズ・デシリアライズできる | 値オブジェクト往復回帰 / serialization | 良い | - | displayNameあり |
| `testTypicalGameDimensions` | 典型的なゲーム画面サイズで使える | 値オブジェクト往復回帰 / typical / game / dimensions | 良い | - | displayNameあり |

### `RenderOrderComparatorTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `描画順比較回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 比較順序が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetInstanceNonNull` | 取得 / instance / non / null | 描画順比較回帰 / singleton / instance | 良い | - | - |
| `testGetInstanceSingleton` | 取得 / instance / singleton | 描画順比較回帰 / singleton / instance | 良い | - | - |
| `testCompareO1YlessThanO2YreturnsNegative` | compare / o1 / yless / than / o2 / yreturns / negative | 描画順比較回帰 / y / ascending | 良い | - | - |
| `testCompareO1YgreaterThanO2YreturnsPositive` | compare / o1 / ygreater / than / o2 / yreturns / positive | 描画順比較回帰 / y / descending | 良い | - | - |
| `testCompareSameYbothNonBodyReturnsZero` | compare / same / yboth / non / 本体 / 戻り / zero | 描画順比較回帰 / same / y / non / body | 良い | - | - |
| `testCompareSameYbabyVsAdult` | compare / same / ybaby / vs / adult | 描画順比較回帰 / same / y / baby / adult | 良い | - | - |
| `testCompareSameYadultVsBaby` | compare / same / yadult / vs / baby | 描画順比較回帰 / same / y / adult / baby | 良い | - | - |
| `testCompareSameYbodyVsObj` | compare / same / ybody / vs / obj | 描画順比較回帰 / same / y / body / object | 良い | - | - |
| `testCompareSameYobjVsBody` | compare / same / yobj / vs / 本体 | 描画順比較回帰 / same / y / object / body | 良い | - | - |
| `testCompareSameYbabyBodyVsObj` | compare / same / ybaby / 本体 / vs / obj | 描画順比較回帰 / same / y / baby / object | 良い | - | - |

### `TerrainFieldTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `地形ロード回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 世界状態の保存/復元と進行が壊れない
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testLoadTerrainOldFormat` | ロード / terrain / old / format | 値オブジェクト往復確認 / 状態保存復元確認 | 良い | - | - |
| `testLoadTerrainNewFormat` | ロード / terrain / new / format | 値オブジェクト往復確認 / 状態保存復元確認 | 良い | - | - |
| `testLoadTerrainFailureRestoresPreviousStaticState` | ロード / terrain / failure / restores / previous / static / state | 値オブジェクト往復確認 / 状態保存復元確認 | 良い | - | - |

### `TerrariumTest`
- 状態: 完了 (50/50 良い)
- クラス要約: `世界状態保存復元回帰`
- 回帰目的:
  - 世界状態の保存/復元が壊れない
  - 個体・装置・イベント・装備・関係の状態保持が壊れない
  - ロード失敗時に world を壊さない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAddBodySuccess` | 追加 / 本体 / success | 値オブジェクト往復確認 / 追加 / 本体 / success | 良い | - | - |
| `testCheckPanicPanicNearShit` | 判定 / 恐慌 / 恐慌 / near / shit | 値オブジェクト往復確認 / 判定 / 恐慌 / 恐慌 / near / shit | 良い | - | - |
| `testSaveLoadStateBasic` | 保存 / ロード / state / basic | 値オブジェクト往復確認 / 状態保存復元確認 | 良い | - | - |
| `testScenarioGetDayStateUsesExpectedBoundaryBuckets` | シナリオ / 取得 / day / state / uses / expected / boundary / buckets | 値オブジェクト往復確認 / シナリオ / 取得 / day / state / uses / expected / boundary / buckets | 良い | - | - |
| `testScenarioResetTerrariumEnvironmentClearsAllSteamFlags` | シナリオ / reset / terrarium / environment / clears / all / steam / flags | 値オブジェクト往復確認 / シナリオ / reset / terrarium / environment / clears / all / steam / flags | 良い | - | - |
| `testScenarioCheckPanicBurnPropagatesFearOnlyToNearbyNonRaperBodies` | シナリオ / 判定 / 恐慌 / burn / propagates / fear / only / to / nearby / non / raper / bodies | 値オブジェクト往復確認 / シナリオ / 判定 / 恐慌 / burn / propagates / fear / only / to / nearby / non / raper / bodies | 良い | - | - |
| `testScenarioSaveLoadRestoresPickedUpBodyInPlayerInventory` | シナリオ / 保存 / ロード / restores / picked / up / 本体 / in / player / inventory | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / restores / picked / up / 本体 / in / player / inventory | 良い | - | - |
| `testScenarioSaveLoadRestoresBodyTakeoutFoodReference` | シナリオ / 保存 / ロード / restores / 本体 / takeout / food / reference | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / restores / 本体 / takeout / food / reference | 良い | - | - |
| `testScenarioSaveLoadPreservesFoodCarriedOnHeadState` | シナリオ / 保存 / ロード / preserves / food / carried / on / head / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / food / carried / on / head / state | 良い | - | - |
| `testScenarioSaveLoadPreservesParentChildAndPartnerRelations` | シナリオ / 保存 / ロード / preserves / 親 / 子 / and / 相手 / relations | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / 親 / 子 / and / 相手 / relations | 良い | - | - |
| `testScenarioSaveLoadPreservesAnimalPregnancyAndFamilyRelations` | シナリオ / 保存 / ロード / preserves / animal / pregnancy / and / 家族 / relations | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / animal / pregnancy / and / 家族 / relations | 良い | - | - |
| `testScenarioSaveLoadRestoresStalkPregnancyBindings` | シナリオ / 保存 / ロード / restores / stalk / pregnancy / bindings | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / restores / stalk / pregnancy / bindings | 良い | - | - |
| `testScenarioSaveLoadPreservesTransformedReimuWithPregnancyAndFamilyRelations` | シナリオ / 保存 / ロード / preserves / transformed / reimu / with / pregnancy / and / 家族 / relations | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / transformed / reimu / with / pregnancy / and / 家族 / relations | 良い | - | - |
| `testScenarioSaveLoadPreservesTransformedMarisaWithStalkPregnancyAndFamilyRelations` | シナリオ / 保存 / ロード / preserves / transformed / marisa / with / stalk / pregnancy / and / 家族 / relations | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / transformed / marisa / with / stalk / pregnancy / and / 家族 / relations | 良い | - | - |
| `testScenarioSaveLoadRestoresFavoriteBedReference` | シナリオ / 保存 / ロード / restores / favorite / bed / reference | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / restores / favorite / bed / reference | 良い | - | - |
| `testScenarioSaveLoadPreservesSleepingBodyPositionOnBed` | シナリオ / 保存 / ロード / preserves / sleeping / 本体 / position / on / bed | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / sleeping / 本体 / position / on / bed | 良い | - | - |
| `testScenarioSaveLoadPreservesRepresentativeItemProperties` | シナリオ / 保存 / ロード / preserves / representative / item / properties | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / representative / item / properties | 良い | - | - |
| `testScenarioSaveLoadPreservesDiffuserProperties` | シナリオ / 保存 / ロード / preserves / diffuser / properties | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / diffuser / properties | 良い | - | - |
| `testScenarioSaveLoadPreservesActiveDiffuserSteamEmission` | シナリオ / 保存 / ロード / preserves / active / diffuser / steam / emission | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / active / diffuser / steam / emission | 良い | - | - |
| `testScenarioSaveLoadPreservesActiveHotPlateFootBakeProgress` | シナリオ / 保存 / ロード / preserves / active / hot / plate / foot / bake / progress | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / active / hot / plate / foot / bake / progress | 良い | - | - |
| `testScenarioSaveLoadPreservesActiveStickyPlateBinding` | シナリオ / 保存 / ロード / preserves / active / sticky / plate / binding | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / active / sticky / plate / binding | 良い | - | - |
| `testScenarioSaveLoadPreservesActiveMixerProcessing` | シナリオ / 保存 / ロード / preserves / active / mixer / processing | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / active / mixer / processing | 良い | - | - |
| `testScenarioSaveLoadPreservesGarbageChuteBoundObjects` | シナリオ / 保存 / ロード / preserves / garbage / chute / bound / objects | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / garbage / chute / bound / objects | 良い | - | - |
| `testScenarioSaveLoadPreservesGarbageStationFoodSlots` | シナリオ / 保存 / ロード / preserves / garbage / station / food / slots | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / garbage / station / food / slots | 良い | - | - |
| `testScenarioSaveLoadPreservesAutoFeederBlockedByLivingSpawnedBody` | シナリオ / 保存 / ロード / preserves / auto / feeder / blocked / by / living / spawned / 本体 | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / auto / feeder / blocked / by / living / spawned / 本体 | 良い | - | - |
| `testScenarioSaveLoadPreservesYunbaInstructionsAndContinuesWorking` | シナリオ / 保存 / ロード / preserves / yunba / instructions / and / continues / working | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / yunba / instructions / and / continues / working | 良い | - | - |
| `testScenarioSaveLoadPreservesEquippedOkazariTypeOnNormalBody` | シナリオ / 保存 / ロード / preserves / equipped / okazari / type / on / normal / 本体 | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / equipped / okazari / type / on / normal / 本体 | 良い | - | - |
| `testScenarioSaveLoadPreservesNoOkazariStateOnNormalBody` | シナリオ / 保存 / ロード / preserves / なし / okazari / state / on / normal / 本体 | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / なし / okazari / state / on / normal / 本体 | 良い | - | - |
| `testScenarioSaveLoadPreservesOkazariStateOnMarisaReimu` | シナリオ / 保存 / ロード / preserves / okazari / state / on / marisa / reimu | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / okazari / state / on / marisa / reimu | 良い | - | - |
| `testScenarioSaveLoadPreservesOkazariStateOnReimuMarisa` | シナリオ / 保存 / ロード / preserves / okazari / state / on / reimu / marisa | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / okazari / state / on / reimu / marisa | 良い | - | - |
| `testScenarioSaveLoadPreservesPreBurnBodyState` | シナリオ / 保存 / ロード / preserves / pre / burn / 本体 / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / pre / burn / 本体 / state | 良い | - | - |
| `testScenarioSaveLoadPreservesBurningBodyState` | シナリオ / 保存 / ロード / preserves / burning / 本体 / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / burning / 本体 / state | 良い | - | - |
| `testScenarioSaveLoadPreservesBurnedCorpseState` | シナリオ / 保存 / ロード / preserves / burned / corpse / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / burned / corpse / state | 良い | - | - |
| `testScenarioSaveLoadPreservesAntsAttachmentState` | シナリオ / 保存 / ロード / preserves / ants / attachment / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / ants / attachment / state | 良い | - | - |
| `testScenarioSaveLoadPreservesBadgeAttachmentRank` | シナリオ / 保存 / ロード / preserves / badge / attachment / rank | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / badge / attachment / rank | 良い | - | - |
| `testScenarioSaveLoadPreservesRepresentativeAmpouleAttachments` | シナリオ / 保存 / ロード / preserves / representative / ampoule / attachments | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / representative / ampoule / attachments | 良い | - | - |
| `testScenarioSaveLoadPreservesBodyRidingOnSuiState` | シナリオ / 保存 / ロード / preserves / 本体 / riding / on / sui / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / 本体 / riding / on / sui / state | 良い | - | - |
| `testScenarioSaveLoadPreservesChildRidingOnParentsHeadState` | シナリオ / 保存 / ロード / preserves / 子 / riding / on / parents / head / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / 子 / riding / on / parents / head / state | 良い | - | - |
| `testScenarioSaveLoadPreservesFarmBuriedBodyState` | シナリオ / 保存 / ロード / preserves / farm / buried / 本体 / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / farm / buried / 本体 / state | 良い | - | - |
| `testScenarioSaveLoadPreservesGodHandExpandedBodyState` | シナリオ / 保存 / ロード / preserves / god / hand / expanded / 本体 / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / god / hand / expanded / 本体 / state | 良い | - | - |
| `testScenarioSaveLoadPreservesBodyOnBeltconveyorState` | シナリオ / 保存 / ロード / preserves / 本体 / on / beltconveyor / state | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / 本体 / on / beltconveyor / state | 良い | - | - |
| `testScenarioLoadStateRejectsCorruptedSaveWithoutReplacingWorld` | シナリオ / ロード / state / rejects / corrupted / 保存 / without / replacing / world | 値オブジェクト往復確認 / シナリオ / ロード / state / rejects / corrupted / 保存 / without / replacing / world | 良い | - | - |
| `testScenarioSaveLoadPreservesFuneralEventStateAcrossParticipants` | シナリオ / 保存 / ロード / preserves / funeral / イベント / state / across / participants | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / funeral / イベント / state / across / participants | 良い | - | reflection |
| `testScenarioSaveLoadPreservesSuperEatingTimeEventStateAndTarget` | シナリオ / 保存 / ロード / preserves / super / eating / time / イベント / state / and / target | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / super / eating / time / イベント / state / and / target | 良い | - | - |
| `testScenarioSaveLoadPreservesProposeEventProgress` | シナリオ / 保存 / ロード / preserves / propose / イベント / progress | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / propose / イベント / progress | 良い | - | - |
| `testScenarioSaveLoadPreservesProudChildEventProgressFields` | シナリオ / 保存 / ロード / preserves / proud / 子 / イベント / progress / fields | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / proud / 子 / イベント / progress / fields | 良い | - | reflection |
| `testScenarioSaveLoadPreservesShitExercisesEventProgressFields` | シナリオ / 保存 / ロード / preserves / shit / exercises / イベント / progress / fields | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / shit / exercises / イベント / progress / fields | 良い | - | reflection |
| `testScenarioSaveLoadPreservesRaperReactionEventProgressFields` | シナリオ / 保存 / ロード / preserves / raper / reaction / イベント / progress / fields | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / raper / reaction / イベント / progress / fields | 良い | - | - |
| `testScenarioSaveLoadPreservesBegForLifeEventProgressFields` | シナリオ / 保存 / ロード / preserves / beg / for / life / イベント / progress / fields | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / beg / for / life / イベント / progress / fields | 良い | - | - |
| `testScenarioSaveLoadPreservesPredatorsGameEventProgressFields` | シナリオ / 保存 / ロード / preserves / predators / game / イベント / progress / fields | 値オブジェクト往復確認 / シナリオ / 保存 / ロード / preserves / predators / game / イベント / progress / fields | 良い | - | - |

### `TranslateTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `座標変換回帰`
- 回帰目的:
  - 距離・角度・反転・ズームなどの座標変換が壊れない.
  - 表示領域と buffer / field size の整合が壊れない.

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSetCanvasSizeDerivesFieldAndBufferDimensions` | canvas / size / derives / field / and / buffer / dimensions | 座標変換回帰 / canvas / size / derives / field / and / buffer / dimensions | 良い | - | - |
| `testDistanceAndRealDistanceKnownValues` | distance / and / real / distance / known / values | 座標変換回帰 / distance / and / real / distance / known / values | 良い | - | - |
| `testGetRadianAndPointByDistAndRadRoundTrip` | 取得 / radian / and / point / by / dist / and / rad / round / trip | 座標変換回帰 / 取得 / radian / and / point / by / dist / and / rad / round / trip | 良い | - | - |
| `testTransSizeReturnsSameValue` | trans / size / 戻り / same / value | 座標変換回帰 / trans / size / 戻り / same / value | 良い | - | - |
| `testTranslateRoundTripAndOrigin` | translate / round / trip / and / origin | 座標変換回帰 / translate / round / trip / and / origin | 良い | - | - |
| `testVerticalTransformFormulas` | vertical / transform / formulas | 座標変換回帰 / vertical / transform / formulas | 良い | - | - |
| `testInvertRejectsInvalidPositions` | invert / rejects / invalid / positions | 座標変換回帰 / invert / rejects / invalid / positions | 良い | - | - |
| `testInvertLimitClampsCoordinates` | invert / limit / clamps / coordinates | 座標変換回帰 / invert / limit / clamps / coordinates | 良い | - | - |
| `testInInvertLimitDistinguishesWallAndFloor` | in / invert / limit / distinguishes / wall / and / floor | 座標変換回帰 / in / invert / limit / distinguishes / wall / and / floor | 良い | - | - |
| `testZoomRateClampsAndBufferZoomResizesDisplayArea` | zoom / rate / clamps / and / buffer / zoom / resizes / display / area | 座標変換回帰 / zoom / rate / clamps / and / buffer / zoom / resizes / display / area | 良い | - | - |
| `testBufferPositionMethodsClampDisplayArea` | buffer / position / methods / clamp / display / area | 座標変換回帰 / buffer / position / methods / clamp / display / area | 良い | - | - |

### `WorldTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `ワールド切替回帰`
- 回帰目的:
  - 初期化で現在ワールド状態と次ワールド状態が壊れない
  - ワールドサイズ再計算と状態切替が壊れない
  - current と next の切替動作が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructorInitializesWorldStates` | default / constructor / initializes / world / states | ワールド切替回帰 | 良い | - | - |
| `testParameterizedConstructorRecalculatesWorldSize` | parameterized / constructor / recalculates / world / size | ワールド切替回帰 | 良い | - | - |
| `testSetCurrentWorldStateIndexChangesCurrentStateImmediately` | current / world / state / changes / current / state / immediately | ワールド切替回帰 | 良い | - | - |
| `testNextWorldStateIndexQueuesAndChangeWorldStateAppliesIt` | next / world / state / queues / and / change / world / state / applies / it | ワールド切替回帰 | 良い | - | - |

## `org.simyukkuri.effect`
### `EffectCoverageTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `エフェクト表示回帰`
- 回帰目的:
  - エフェクト画像の種類とフレーム選択が壊れない
  - 向きごとの画像選択が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBakeSmoke` | bake / smoke | エフェクト表示回帰 / bake / smoke | 良い | - | - |
| `testSteam` | steam | エフェクト表示回帰 / steam | 良い | - | - |
| `testMix` | mix | エフェクト表示回帰 / mix | 良い | - | - |
| `testHit` | hit | エフェクト表示回帰 / hit | 良い | - | - |
| `testGetImages` | 取得 / images | エフェクト表示回帰 / 取得 / images | 良い | - | - |

## `org.simyukkuri.engine`
### `SaveDataCodecTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `世界状態保存復元回帰`
- 回帰目的:
  - 世界全体の保存/復元が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSaveAndLoadRoundTripPreservesCoreWorldFields` | 保存 / and / ロード / round / trip / preserves / core / world / fields | 世界状態保存復元回帰 | 良い | - | - |

### `YukkuriFactoryTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `生成結果回帰`
- 回帰目的:
  - 種別ごとの生成結果と画像選択が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCreateReimuLoadsExpectedImageAndReturnsBody` | 生成 / reimu / loads / expected / image / and / 戻り / 本体 | 基礎回帰 / 生成 / reimu / loads / expected / image / and / 戻り / 本体 | 良い | - | - |
| `testCreateDosMarisaFallsBackWhenDosMakerReturnsFalse` | 生成 / dos / marisa / falls / back / when / dos / maker / 戻り / false | 基礎回帰 / 生成 / dos / marisa / falls / back / when / dos / maker / 戻り / false | 良い | - | - |

## `org.simyukkuri.engine.birth`
### `BabyDnaFactoryTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `出生DNA/親子伝搬回帰`
- 回帰目的:
  - 親子 ID と種別継承の保存/生成規則が壊れない
  - 強制生成・例外条件・ハイブリッド判定が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCreateBabyDnaWithNullMother` | 生成 / baby / dna / with / null / 母 | 出生DNA/親子伝搬回帰 / 生成 / baby / dna / with / null / 母 | 良い | - | - |
| `testCreateBabyDnaWithNullFatherDoesNotThrow` | 生成 / baby / dna / with / null / 父 / does / 非 / 例外 | 出生DNA/親子伝搬回帰 / 生成 / baby / dna / with / null / 父 / does / 非 / 例外 | 良い | - | - |
| `testCreateBabyDnaForceCreate` | 生成 / baby / dna / force / 生成 | 出生DNA/親子伝搬回帰 / 生成 / baby / dna / force / 生成 | 良い | - | - |
| `testCreateBabyDnaNoHybridWhenDosMarisaParent` | 生成 / baby / dna / なし / hybrid / when / dos / marisa / 親 | 出生DNA/親子伝搬回帰 / 生成 / baby / dna / なし / hybrid / when / dos / marisa / 親 | 良い | - | - |
| `testCreateBabyDnaFailsWhenRandomZeroAndForceDisabled` | 生成 / baby / dna / fails / when / random / zero / and / force / disabled | 出生DNA/親子伝搬回帰 / 生成 / baby / dna / fails / when / random / zero / and / force / disabled | 良い | - | - |
| `testScenarioFatherDamageDegradesBabyToTarinaiAndCopiesParentIds` | シナリオ / 父 / ダメージ / degrades / baby / to / tarinai / and / copies / 親 / ids | 出生DNA/親子伝搬回帰 / シナリオ / 父 / ダメージ / degrades / baby / to / tarinai / and / copies / 親 / ids | 良い | - | - |
| `testScenarioOverPregnantReimuParentsCanProduceTarinaiReimu` | シナリオ / over / pregnant / reimu / parents / 可否 / produce / tarinai / reimu | 出生DNA/親子伝搬回帰 / シナリオ / over / pregnant / reimu / parents / 可否 / produce / tarinai / reimu | 良い | - | - |
| `testScenarioDifferentParentsCanProduceConcreteHybridType` | シナリオ / different / parents / 可否 / produce / concrete / hybrid / type | 出生DNA/親子伝搬回帰 / シナリオ / different / parents / 可否 / produce / concrete / hybrid / type | 良い | - | - |
| `testScenarioHybridSteamForcesHybridTypeForDifferentParents` | シナリオ / hybrid / steam / forces / hybrid / type / for / different / parents | 出生DNA/親子伝搬回帰 / シナリオ / hybrid / steam / forces / hybrid / type / for / different / parents | 良い | - | - |
| `testScenarioDosMarisaSelectedTypeFallsBackToMarisa` | シナリオ / dos / marisa / selected / type / falls / back / to / marisa | 出生DNA/親子伝搬回帰 / シナリオ / dos / marisa / selected / type / falls / back / to / marisa | 良い | - | - |
| `testScenarioDeibuSelectedTypeFallsBackToReimu` | シナリオ / deibu / selected / type / falls / back / to / reimu | 出生DNA/親子伝搬回帰 / シナリオ / deibu / selected / type / falls / back / to / reimu | 良い | - | - |
| `testScenarioChangelingOverrideCanReplaceParentTypeWithRareType` | シナリオ / changeling / override / 可否 / replace / 親 / type / with / rare / type | 出生DNA/親子伝搬回帰 / シナリオ / changeling / override / 可否 / replace / 親 / type / with / rare / type | 良い | - | - |
| `testScenarioReimuTypeCanMutateToWasaReimu` | シナリオ / reimu / type / 可否 / mutate / to / wasa / reimu | 出生DNA/親子伝搬回帰 / シナリオ / reimu / type / 可否 / mutate / to / wasa / reimu | 良い | - | - |
| `testScenarioMarisaTypeCanMutateToTsumuriSubtype` | シナリオ / marisa / type / 可否 / mutate / to / tsumuri / subtype | 出生DNA/親子伝搬回帰 / シナリオ / marisa / type / 可否 / mutate / to / tsumuri / subtype | 良い | - | - |
| `testScenarioMotherAncestorAtavismCanOverrideBabyType` | シナリオ / 母 / ancestor / atavism / 可否 / override / baby / type | 出生DNA/親子伝搬回帰 / シナリオ / 母 / ancestor / atavism / 可否 / override / baby / type | 良い | - | - |
| `testScenarioAttitudeBaseZeroRareRollCanProduceShithead` | シナリオ / attitude / base / zero / rare / roll / 可否 / produce / shithead | 出生DNA/親子伝搬回帰 / シナリオ / attitude / base / zero / rare / roll / 可否 / produce / shithead | 良い | - | - |
| `testScenarioIntelligenceBaseFourRareRollCanProduceWise` | シナリオ / intelligence / base / four / rare / roll / 可否 / produce / wise | 出生DNA/親子伝搬回帰 / シナリオ / intelligence / base / four / rare / roll / 可否 / produce / wise | 良い | - | - |

## `org.simyukkuri.engine.transform`
### `TransformationPolicyTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `変身/変換回帰`
- 回帰目的:
  - 変身の基底ファイル名と年齢変換規則が壊れない
  - 選択中個体の判定が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testNeedsDosReservation` | needs / dos / reservation | 変身/変換回帰 / needs / dos / reservation | 良い | - | - |
| `testResolveBaseBodyFileName` | resolve / base / 本体 / file / name | 変身/変換回帰 / resolve / base / 本体 / file / name | 良い | - | - |
| `testIsSelectedYukkuri` | selected / yukkuri | 変身/変換回帰 / selected / yukkuri | 良い | - | - |
| `testNormalizeTransformedAge` | normalize / transformed / age | 変身/変換回帰 / normalize / transformed / age | 良い | - | - |

### `TransformationServiceTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `変身/変換回帰`
- 回帰目的:
  - 変身時に同一 unique ID を維持して差し替える規則が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testTransformReplacesBodyAtSameUniqueId` | transform / replaces / 本体 / at / same / unique / id | 変身/変換回帰 / transform / replaces / 本体 / at / same / unique / id | 良い | - | 選択状態も新個体へ移ることを追加確認 |

## `org.simyukkuri.entity.core`
### `ObjTest`
- 状態: 完了 (69/69 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 比較順序が壊れない
  - メニュー構成と選択状態が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAge` | age | Entity/世界実体の基盤回帰 / age | 良い | - | - |
| `testCoordinates` | coordinates | Entity/世界実体の基盤回帰 / coordinates | 良い | - | - |
| `testSetCalcXNormal` | 設定 / calc / xnormal | Entity/世界実体の基盤回帰 / 設定 / calc / xnormal | 良い | - | - |
| `testSetCalcXMin` | 設定 / calc / xmin | Entity/世界実体の基盤回帰 / 設定 / calc / xmin | 良い | - | - |
| `testSetCalcXMax` | 設定 / calc / xmax | Entity/世界実体の基盤回帰 / 設定 / calc / xmax | 良い | - | - |
| `testSetCalcXNoWall` | 設定 / calc / xno / 壁 | Entity/世界実体の基盤回帰 / 設定 / calc / xno / 壁 | 良い | - | - |
| `testSetCalcYNormal` | 設定 / calc / ynormal | Entity/世界実体の基盤回帰 / 設定 / calc / ynormal | 良い | - | - |
| `testSetCalcYMin` | 設定 / calc / ymin | Entity/世界実体の基盤回帰 / 設定 / calc / ymin | 良い | - | - |
| `testSetCalcYMax` | 設定 / calc / ymax | Entity/世界実体の基盤回帰 / 設定 / calc / ymax | 良い | - | - |
| `testSetCalcYNoWall` | 設定 / calc / yno / 壁 | Entity/世界実体の基盤回帰 / 設定 / calc / yno / 壁 | 良い | - | - |
| `testSetCalcZNormal` | 設定 / calc / znormal | Entity/世界実体の基盤回帰 / 設定 / calc / znormal | 良い | - | - |
| `testSetCalcZAboveMax` | 設定 / calc / zabove / max | Entity/世界実体の基盤回帰 / 設定 / calc / zabove / max | 良い | - | - |
| `testSetCalcZBelowMostDepthNotFalling` | 設定 / calc / zbelow / most / depth / 非 / falling | Entity/世界実体の基盤回帰 / 設定 / calc / zbelow / most / depth / 非 / falling | 良い | - | - |
| `testSetCalcZBelowMostDepthFalling` | 設定 / calc / zbelow / most / depth / falling | Entity/世界実体の基盤回帰 / 設定 / calc / zbelow / most / depth / falling | 良い | - | - |
| `testSetCalcZNoWall` | 設定 / calc / zno / 壁 | Entity/世界実体の基盤回帰 / 設定 / calc / zno / 壁 | 良い | - | - |
| `testVectors` | vectors | Entity/世界実体の基盤回帰 / vectors | 良い | - | - |
| `testSetBxyz` | 設定 / bxyz | Entity/世界実体の基盤回帰 / 設定 / bxyz | 良い | - | - |
| `testAddBxyz` | 追加 / bxyz | Entity/世界実体の基盤回帰 / 追加 / bxyz | 良い | - | - |
| `testResetBPos` | reset / bpos | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testOfsXY` | ofs / xy | Entity/世界実体の基盤回帰 / ofs / xy | 良い | - | - |
| `testSetBoundaryDirect` | 設定 / boundary / direct | Entity/世界実体の基盤回帰 / 設定 / boundary / direct | 良い | - | - |
| `testGetBoundaryShape` | 取得 / boundary / shape | Entity/世界実体の基盤回帰 / 取得 / boundary / shape | 良い | - | - |
| `testScreenPivotDirect` | screen / pivot / direct | Entity/世界実体の基盤回帰 / screen / pivot / direct | 良い | - | - |
| `testScreenPivotPoint` | screen / pivot / point | Entity/世界実体の基盤回帰 / screen / pivot / point | 良い | - | - |
| `testScreenRectDirect` | screen / rect / direct | Entity/世界実体の基盤回帰 / screen / rect / direct | 良い | - | - |
| `testScreenRectWithRect` | screen / rect / with / rect | Entity/世界実体の基盤回帰 / screen / rect / with / rect | 良い | - | - |
| `testGrabRelease` | grab / release | Entity/世界実体の基盤回帰 / grab / release | 良い | - | - |
| `testRemove` | 除去 | Entity/世界実体の基盤回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjType` | obj / type | Entity/世界実体の基盤回帰 / obj / type | 良い | - | - |
| `testValueCost` | value / cost | Entity/世界実体の基盤回帰 / value / cost | 良い | - | - |
| `testWhere` | where | Entity/世界実体の基盤回帰 / where | 良い | - | - |
| `testFallingUnderGround` | falling / under / ground | Entity/世界実体の基盤回帰 / falling / under / ground | 良い | - | - |
| `testInPool` | in / pool | Entity/世界実体の基盤回帰 / in / pool | 良い | - | - |
| `testMostDepth` | most / depth | Entity/世界実体の基盤回帰 / most / depth | 良い | - | - |
| `testBindObj` | bind / obj | Entity/世界実体の基盤回帰 / bind / obj | 良い | - | - |
| `testCompareTo` | compare / to | Entity/世界実体の基盤回帰 / compare / to | 良い | - | - |
| `testHasGetPopup` | 有無 / 取得 / popup | Entity/世界実体の基盤回帰 / 有無 / 取得 / popup | 良い | - | - |
| `testHasUsePopup` | 有無 / use / popup | Entity/世界実体の基盤回帰 / 有無 / use / popup | 良い | - | - |
| `testForceXy` | force / xy | Entity/世界実体の基盤回帰 / force / xy | 良い | - | - |
| `testKickNoArgs` | kick / なし / args | Entity/世界実体の基盤回帰 / kick / なし / args | 良い | - | assert:0 |
| `testCalcPosClampXLow` | calc / pos / 範囲補正 / xlow | Entity/世界実体の基盤回帰 / calc / pos / 範囲補正 / xlow | 良い | - | - |
| `testCalcPosClampYLow` | calc / pos / 範囲補正 / ylow | Entity/世界実体の基盤回帰 / calc / pos / 範囲補正 / ylow | 良い | - | - |
| `testCalcPosClampXHigh` | calc / pos / 範囲補正 / xhigh | Entity/世界実体の基盤回帰 / calc / pos / 範囲補正 / xhigh | 良い | - | - |
| `testCalcPosClampYHigh` | calc / pos / 範囲補正 / yhigh | Entity/世界実体の基盤回帰 / calc / pos / 範囲補正 / yhigh | 良い | - | - |
| `testCalcPosNoClamp` | calc / pos / なし / 範囲補正 | Entity/世界実体の基盤回帰 / calc / pos / なし / 範囲補正 | 良い | - | - |
| `testClockTickRemovedReturnsRemoved` | clock / tick / removed / 戻り / removed | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGrabbedNoMovement` | clock / tick / grabbed / なし / movement | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMoveX` | clock / tick / 移動 / x | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXBoundsLow` | clock / tick / xbounds / low | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXBoundsHigh` | clock / tick / xbounds / high | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMoveY` | clock / tick / 移動 / y | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYBoundsLow` | clock / tick / ybounds / low | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYBoundsHigh` | clock / tick / ybounds / high | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZGravity` | clock / tick / zgravity | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZLandsAtMostDepth` | clock / tick / zlands / at / most / depth | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZFallingUnderGround` | clock / tick / zfalling / under / ground | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXBarrierHit` | clock / tick / xbarrier / hit | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYBarrierHit` | clock / tick / ybarrier / hit | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNoMovement` | clock / tick / なし / movement | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNegativeXYClamp` | clock / tick / negative / xyclamp | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testImgWH` | img / wh | Entity/世界実体の基盤回帰 / img / wh | 良い | - | - |
| `testPivXY` | piv / xy | Entity/世界実体の基盤回帰 / piv / xy | 良い | - | - |
| `testOfsXYGetSet` | ofs / xyget / 設定 | Entity/世界実体の基盤回帰 / ofs / xyget / 設定 | 良い | - | - |
| `testObjId` | obj / id | Entity/世界実体の基盤回帰 / obj / id | 良い | - | - |
| `testTakeMappedObjReturnsNull` | take / mapped / obj / 戻り / null | Entity/世界実体の基盤回帰 / take / mapped / obj / 戻り / null | 良い | - | - |
| `testTakeMappedObjFromFrontEffect` | take / mapped / obj / from / front / effect | Entity/世界実体の基盤回帰 / take / mapped / obj / from / front / effect | 良い | - | - |
| `testTakeMappedObjFromBodyMap` | take / mapped / obj / from / 本体 / map | Entity/世界実体の基盤回帰 / take / mapped / obj / from / 本体 / map | 良い | - | - |
| `testScenarioClockTickAppliesVelocityAndKnockbackThenResetsBVector` | シナリオ / clock / tick / applies / velocity / and / knockback / then / resets / bvector | Entity/世界実体の基盤回帰 / シナリオ / clock / tick / applies / velocity / and / knockback / then / resets / bvector | 良い | - | - |
| `testScenarioFallingUnderGroundKeepsNegativeZButStillZeroesXYVelocity` | シナリオ / falling / under / ground / 維持 / negative / zbut / still / zeroes / xyvelocity | Entity/世界実体の基盤回帰 / シナリオ / falling / under / ground / 維持 / negative / zbut / still / zeroes / xyvelocity | 良い | - | - |

## `org.simyukkuri.entity.core.attachment`
### `AttachmentTest`
- 状態: 完了 (30/30 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorWithBody` | constructor / with / 本体 | Entity/世界実体の基盤回帰 / constructor / with / 本体 | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testParentGetterSetter` | 親 / getter / setter | Entity/世界実体の基盤回帰 / 親 / getter / setter | 良い | - | - |
| `testAnimateGetterSetter` | animate / getter / setter | Entity/世界実体の基盤回帰 / animate / getter / setter | 良い | - | - |
| `testAnimeFrameGetterSetter` | anime / frame / getter / setter | Entity/世界実体の基盤回帰 / anime / frame / getter / setter | 良い | - | - |
| `testAnimeIntervalGetterSetter` | anime / interval / getter / setter | Entity/世界実体の基盤回帰 / anime / interval / getter / setter | 良い | - | - |
| `testAnimeLoopGetterSetter` | anime / loop / getter / setter | Entity/世界実体の基盤回帰 / anime / loop / getter / setter | 良い | - | - |
| `testAttachPropertyGetterSetter` | attach / property / getter / setter | Entity/世界実体の基盤回帰 / attach / property / getter / setter | 良い | - | - |
| `testProcessIntervalGetterSetter` | process / interval / getter / setter | Entity/世界実体の基盤回帰 / process / interval / getter / setter | 良い | - | - |
| `testPosOfsGetterSetter` | pos / ofs / getter / setter | Entity/世界実体の基盤回帰 / pos / ofs / getter / setter | 良い | - | - |
| `testSetAttachPropertyWithNullParent` | 設定 / attach / property / with / null / 親 | Entity/世界実体の基盤回帰 / 設定 / attach / property / with / null / 親 | 良い | - | assert:0 |
| `testSetAttachPropertySetsAnimateTrue` | 設定 / attach / property / sets / animate / true | Entity/世界実体の基盤回帰 / 設定 / attach / property / sets / animate / true | 良い | - | - |
| `testSetAttachPropertySetsAnimateFalse` | 設定 / attach / property / sets / animate / false | Entity/世界実体の基盤回帰 / 設定 / attach / property / sets / animate / false | 良い | - | - |
| `testClockTickIncrementsAge` | clock / tick / increments / age | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickCallsUpdateAtProcessInterval` | clock / tick / calls / 更新 / at / process / interval | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickDoesNotCallUpdateBetweenIntervals` | clock / tick / does / 非 / call / 更新 / between / intervals | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickReturnsUpdateResult` | clock / tick / 戻り / 更新 / result | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickReturnsDoNothingWhenNotAtInterval` | clock / tick / 戻り / do / nothing / when / 非 / at / interval | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAdvancesAnimationFrame` | clock / tick / advances / animation / frame | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickWrapsAnimationFrame` | clock / tick / wraps / animation / frame | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickDecreasesAnimeLoop` | clock / tick / decreases / anime / loop | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickStopsAnimationWhenLoopReachesZero` | clock / tick / stops / animation / when / loop / reaches / zero | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNoAnimationWhenDisabled` | clock / tick / なし / animation / when / disabled | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testGetOfsXReturnsNegativeOneWhenParentNull` | 取得 / ofs / xreturns / negative / one / when / 親 / null | Entity/世界実体の基盤回帰 / 取得 / ofs / xreturns / negative / one / when / 親 / null | 良い | - | - |
| `testGetOfsYReturnsNegativeOneWhenParentNull` | 取得 / ofs / yreturns / negative / one / when / 親 / null | Entity/世界実体の基盤回帰 / 取得 / ofs / yreturns / negative / one / when / 親 / null | 良い | - | - |
| `testGetOfsXReturnsCorrectValue` | 取得 / ofs / xreturns / correct / value | Entity/世界実体の基盤回帰 / 取得 / ofs / xreturns / correct / value | 良い | - | - |
| `testGetOfsYReturnsCorrectValue` | 取得 / ofs / yreturns / correct / value | Entity/世界実体の基盤回帰 / 取得 / ofs / yreturns / correct / value | 良い | - | - |
| `testGetParentOrigin` | 取得 / 親 / origin | Entity/世界実体の基盤回帰 / 取得 / 親 / origin | 良い | - | - |
| `testScenarioUpdateAndAnimationAdvanceOnSameTick` | シナリオ / 更新 / and / animation / advance / on / same / tick | Entity/世界実体の基盤回帰 / シナリオ / 更新 / and / animation / advance / on / same / tick | 良い | - | - |
| `testScenarioFinalAnimationLoopStopsExactlyWhenFrameWraps` | シナリオ / final / animation / loop / stops / exactly / when / frame / wraps | Entity/世界実体の基盤回帰 / シナリオ / final / animation / loop / stops / exactly / when / frame / wraps | 良い | - | - |

## `org.simyukkuri.entity.core.attachment.impl`
### `AccelAmpouleTest`
- 状態: 完了 (16/16 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateIncreasesAgeForNonAdult` | 更新 / increases / age / for / non / adult | Entity/世界実体の基盤回帰 / 更新 / increases / age / for / non / adult | 良い | - | - |
| `testGetImageUsesDirectionAndAge` | 取得 / image / uses / direction / and / age | Entity/世界実体の基盤回帰 / 取得 / image / uses / direction / and / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdateReturnsNullWhenParentNotInWorld` | 更新 / 戻り / null / when / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / 更新 / 戻り / null / when / 親 / 非 / in / world | 良い | - | - |
| `testUpdateSkipsAgeForDeadParent` | 更新 / skips / age / for / 死亡 / 親 | Entity/世界実体の基盤回帰 / 更新 / skips / age / for / 死亡 / 親 | 良い | - | - |
| `testUpdateSkipsAgeForAdultParent` | 更新 / skips / age / for / adult / 親 | Entity/世界実体の基盤回帰 / 更新 / skips / age / for / adult / 親 | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInWorld` | 取得 / image / 戻り / null / when / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / world | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInWorld` | reset / boundary / does / nothing / when / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioChildBodyGetsLargeAgeAccelerationButStaysAlive` | シナリオ / 子 / 本体 / gets / large / age / acceleration / but / stays / alive | Entity/世界実体の基盤回帰 / シナリオ / 子 / 本体 / gets / large / age / acceleration / but / stays / alive | 良い | - | - |
| `testScenarioAdultBodyDoesNotAgeEvenWhenAmpouleUpdates` | シナリオ / adult / 本体 / does / 非 / age / even / when / ampoule / updates | Entity/世界実体の基盤回帰 / シナリオ / adult / 本体 / does / 非 / age / even / when / ampoule / updates | 良い | - | - |

### `AntsTest`
- 状態: 完了 (16/16 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testConstructorSetsNumOfAntsTo50` | constructor / sets / num / of / ants / to50 | Entity/世界実体の基盤回帰 / constructor / sets / num / of / ants / to50 | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateCallsBeEaten` | 更新 / calls / be / eaten | Entity/世界実体の基盤回帰 / 更新 / calls / be / eaten | 良い | - | - |
| `testGetImageReturnsImage0WhenAntsLow` | 取得 / image / 戻り / image0 / when / ants / low | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / image0 / when / ants / low | 良い | - | - |
| `testGetImageReturnsImage1WhenAntsMedium` | 取得 / image / 戻り / image1 / when / ants / medium | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / image1 / when / ants / medium | 良い | - | - |
| `testGetImageReturnsImage2WhenAntsHigh` | 取得 / image / 戻り / image2 / when / ants / high | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / image2 / when / ants / high | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioUpdateConsumesExactlyNumOfAntsDividedByThreeFromBodyAndHungry` | シナリオ / 更新 / consumes / exactly / num / of / ants / divided / by / three / from / 本体 / and / 空腹 | Entity/世界実体の基盤回帰 / シナリオ / 更新 / consumes / exactly / num / of / ants / divided / by / three / from / 本体 / and / 空腹 | 良い | - | - |
| `testScenarioLockmoveYukkuriHitByAntsEntersPainPurupuruBranchWithoutReducingAntCount` | シナリオ / lockmove / yukkuri / hit / by / ants / enters / pain / purupuru / branch / without / reducing / ant / count | Entity/世界実体の基盤回帰 / シナリオ / lockmove / yukkuri / hit / by / ants / enters / pain / purupuru / branch / without / reducing / ant / count | 良い | - | - |

### `AnydAmpouleTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothing` | 更新 / 戻り / do / nothing | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |

### `BadgeTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBadgeRankEnum` | badge / rank / enum | Entity/世界実体の基盤回帰 / badge / rank / enum | 良い | - | - |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorWithBronzeBadge` | constructor / with / bronze / badge | Entity/世界実体の基盤回帰 / constructor / with / bronze / badge | 良い | - | - |
| `testConstructorWithGoldBadge` | constructor / with / gold / badge | Entity/世界実体の基盤回帰 / constructor / with / gold / badge | 良い | - | - |
| `testUpdateReturnsDoNothing` | 更新 / 戻り / do / nothing | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsCorrectImageForRank` | 取得 / image / 戻り / correct / image / for / rank | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / rank | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testSetEbadgeRankChangesRank` | 設定 / ebadge / rank / changes / rank | Entity/世界実体の基盤回帰 / 設定 / ebadge / rank / changes / rank | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioChangingBadgeRankSwitchesRenderedImage` | シナリオ / changing / badge / rank / switches / rendered / image | Entity/世界実体の基盤回帰 / シナリオ / changing / badge / rank / switches / rendered / image | 良い | - | - |
| `testScenarioAdultBadgeUsesAdultBoundaryAndSelectedRankImage` | シナリオ / adult / badge / uses / adult / boundary / and / selected / rank / image | Entity/世界実体の基盤回帰 / シナリオ / adult / badge / uses / adult / boundary / and / selected / rank / image | 良い | - | - |

### `BreedingAmpouleTest`
- 状態: 完了 (19/19 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsDead` | 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsBurned` | 更新 / 戻り / do / nothing / when / 親 / 状態 / burned | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / burned | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsCrushed` | 更新 / 戻り / do / nothing / when / 親 / 状態 / crushed | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / crushed | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdateBreedsWhenParentIsAliveAndNotDisabled` | 更新 / breeds / when / 親 / 状態 / alive / and / 非 / disabled | Entity/世界実体の基盤回帰 / 更新 / breeds / when / 親 / 状態 / alive / and / 非 / disabled | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsBodyCastrated` | 更新 / 戻り / do / nothing / when / 親 / 状態 / 本体 / castrated | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / 本体 / castrated | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioLiveBodyGetsFedHealedAndPregnantWithNewBabyDna` | シナリオ / live / 本体 / gets / fed / healed / and / pregnant / with / new / baby / dna | Entity/世界実体の基盤回帰 / シナリオ / live / 本体 / gets / fed / healed / and / pregnant / with / new / baby / dna | 良い | - | - |
| `testScenarioBodyCastrationBlocksPregnancyAndHealingSideEffects` | シナリオ / 本体 / castration / blocks / pregnancy / and / healing / side / effects | Entity/世界実体の基盤回帰 / シナリオ / 本体 / castration / blocks / pregnancy / and / healing / side / effects | 良い | - | - |

### `FireTest`
- 状態: 完了 (35/35 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testBurnPeriodGetterSetter` | burn / period / getter / setter | Entity/世界実体の基盤回帰 / burn / period / getter / setter | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsCorrectAnimeFrame` | 取得 / image / 戻り / correct / anime / frame | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / anime / frame | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdateReturnsRemovedWhenDeadAndBurned` | 更新 / 戻り / removed / when / 死亡 / and / burned | Entity/世界実体の基盤回帰 / 更新 / 戻り / removed / when / 死亡 / and / burned | 良い | - | - |
| `testUpdateDoesNotReturnRemovedWhenDeadButNotBurned` | 更新 / does / 非 / 戻り / removed / when / 死亡 / but / 非 / burned | Entity/世界実体の基盤回帰 / 更新 / does / 非 / 戻り / removed / when / 死亡 / but / 非 / burned | 良い | - | - |
| `testUpdateIncreasesBurnPeriodWhenDead` | 更新 / increases / burn / period / when / 死亡 | Entity/世界実体の基盤回帰 / 更新 / increases / burn / period / when / 死亡 | 良い | - | - |
| `testUpdateTakesOkazariWhenBurnPeriodHighAndHasOkazari` | 更新 / takes / okazari / when / burn / period / high / and / 有無 / okazari | Entity/世界実体の基盤回帰 / 更新 / takes / okazari / when / burn / period / high / and / 有無 / okazari | 良い | - | - |
| `testUpdatePicksHairWhenBurnPeriodHighAndNotBald` | 更新 / picks / 毛 / when / burn / period / high / and / 非 / bald | Entity/世界実体の基盤回帰 / 更新 / picks / 毛 / when / burn / period / high / and / 非 / bald | 良い | - | assert:0 |
| `testUpdateSetsBurnedWhenBurnPeriodVeryHighAndDead` | 更新 / sets / burned / when / burn / period / very / high / and / 死亡 | Entity/世界実体の基盤回帰 / 更新 / sets / burned / when / burn / period / very / high / and / 死亡 | 良い | - | - |
| `testUpdateAliveParentAddsDamageAndStress` | 更新 / alive / 親 / adds / ダメージ / and / ストレス | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / adds / ダメージ / and / ストレス | 良い | - | - |
| `testUpdateAliveParentNotNydtalking` | 更新 / alive / 親 / 非 / nydtalking | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / nydtalking | 良い | - | assert:0 |
| `testUpdateAliveParentFixBackNotNeedledFurifuri` | 更新 / alive / 親 / fix / back / 非 / needled / furifuri | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / fix / back / 非 / needled / furifuri | 良い | - | - |
| `testUpdateAliveParentFixBackNotNeedledNoFurifuri` | 更新 / alive / 親 / fix / back / 非 / needled / なし / furifuri | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / fix / back / 非 / needled / なし / furifuri | 良い | - | assert:0 |
| `testUpdateAliveParentFixBackNeedled` | 更新 / alive / 親 / fix / back / needled | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / fix / back / needled | 良い | - | assert:0 |
| `testUpdateAliveParentLockmoveNobinobi` | 更新 / alive / 親 / lockmove / nobinobi | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / lockmove / nobinobi | 良い | - | - |
| `testUpdateAliveParentLockmoveNoNobinobi` | 更新 / alive / 親 / lockmove / なし / nobinobi | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / lockmove / なし / nobinobi | 良い | - | assert:0 |
| `testUpdateAliveParentNotLockmove` | 更新 / alive / 親 / 非 / lockmove | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / lockmove | 良い | - | - |
| `testUpdateAliveParentNydstate` | 更新 / alive / 親 / nydstate | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / nydstate | 良い | - | - |
| `testUpdateAliveParentBurnPeriodIncreasesWhenAlive` | 更新 / alive / 親 / burn / period / increases / when / alive | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / burn / period / increases / when / alive | 良い | - | - |
| `testStalkMotherReactsWhenRndHits` | stalk / 母 / reacts / when / rnd / hits | Entity/世界実体の基盤回帰 / stalk / 母 / reacts / when / rnd / hits | 良い | - | - |
| `testStalkMotherNoReactionWhenRndMisses` | stalk / 母 / なし / reaction / when / rnd / misses | Entity/世界実体の基盤回帰 / stalk / 母 / なし / reaction / when / rnd / misses | 良い | - | - |
| `testStalkMotherNoReactionWhenMotherNyd` | stalk / 母 / なし / reaction / when / 母 / 非ゆっくり症 | Entity/世界実体の基盤回帰 / stalk / 母 / なし / reaction / when / 母 / 非ゆっくり症 | 良い | - | - |
| `testStalkMotherNoReactionWhenNoStalk` | stalk / 母 / なし / reaction / when / なし / stalk | Entity/世界実体の基盤回帰 / stalk / 母 / なし / reaction / when / なし / stalk | 良い | - | - |
| `testScenarioLiveBodyWithOkazariBurnsDamageStressAndLosesDecoration` | シナリオ / live / 本体 / with / okazari / burns / ダメージ / ストレス / and / loses / decoration | Entity/世界実体の基盤回帰 / シナリオ / live / 本体 / with / okazari / burns / ダメージ / ストレス / and / loses / decoration | 良い | - | - |
| `testScenarioDeadBaldBodyCrossesFinalBurnThresholdAndIsRemoved` | シナリオ / 死亡 / bald / 本体 / crosses / final / burn / threshold / and / 状態 / removed | Entity/世界実体の基盤回帰 / 死亡時ガード回帰 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |

### `HungryAmpouleTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReducesHungryWhenNotEating` | 更新 / reduces / 空腹 / when / 非 / eating | Entity/世界実体の基盤回帰 / 更新 / reduces / 空腹 / when / 非 / eating | 良い | - | - |
| `testUpdateDoesNotReduceHungryWhenEating` | 更新 / does / 非 / reduce / 空腹 / when / eating | Entity/世界実体の基盤回帰 / 更新 / does / 非 / reduce / 空腹 / when / eating | 良い | - | - |
| `testUpdateClampsHungryToZero` | 更新 / clamps / 空腹 / to / zero | Entity/世界実体の基盤回帰 / 更新 / clamps / 空腹 / to / zero | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioNonEatingBodyLosesExactlyOneTickOfHungry` | シナリオ / non / eating / 本体 / loses / exactly / one / tick / of / 空腹 | Entity/世界実体の基盤回帰 / シナリオ / non / eating / 本体 / loses / exactly / one / tick / of / 空腹 | 良い | - | - |
| `testScenarioEatingBodyPreservesHungryEvenNearClampBoundary` | シナリオ / eating / 本体 / preserves / 空腹 / even / near / 範囲補正 / boundary | Entity/世界実体の基盤回帰 / シナリオ / eating / 本体 / preserves / 空腹 / even / near / 範囲補正 / boundary | 良い | - | - |

### `NeedleTest`
- 状態: 完了 (32/32 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testConstructorSetsFixBackWhenFurifuri` | constructor / sets / fix / back / when / furifuri | Entity/世界実体の基盤回帰 / constructor / sets / fix / back / when / furifuri | 良い | - | - |
| `testConstructorSetsFixBackWhenShitting` | constructor / sets / fix / back / when / shitting | Entity/世界実体の基盤回帰 / constructor / sets / fix / back / when / shitting | 良い | - | - |
| `testConstructorSetsFixBackWhenBirth` | constructor / sets / fix / back / when / birth | Entity/世界実体の基盤回帰 / constructor / sets / fix / back / when / birth | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateClearsFixBackWhenParentIsDead` | 更新 / clears / fix / back / when / 親 / 状態 / 死亡 | Entity/世界実体の基盤回帰 / 更新 / clears / fix / back / when / 親 / 状態 / 死亡 | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdateAliveParentAddsDamageAndStress` | 更新 / alive / 親 / adds / ダメージ / and / ストレス | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / adds / ダメージ / and / ストレス | 良い | - | - |
| `testUpdateAliveParentWakesUpWhenSleeping` | 更新 / alive / 親 / wakes / up / when / sleeping | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / wakes / up / when / sleeping | 良い | - | - |
| `testUpdateAliveParentNotSleeping` | 更新 / alive / 親 / 非 / sleeping | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / sleeping | 良い | - | assert:0 |
| `testUpdateAliveParentFixBackSetsDirectionLeft` | 更新 / alive / 親 / fix / back / sets / direction / left | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / fix / back / sets / direction / left | 良い | - | - |
| `testUpdateAliveParentNotFixBack` | 更新 / alive / 親 / 非 / fix / back | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / fix / back | 良い | - | assert:0 |
| `testUpdateAliveParentNotNydfixBackTalking` | 更新 / alive / 親 / 非 / nydfix / back / talking | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / nydfix / back / talking | 良い | - | assert:0 |
| `testUpdateAliveParentNotNydnotFixBackTalking` | 更新 / alive / 親 / 非 / nydnot / fix / back / talking | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / 非 / nydnot / fix / back / talking | 良い | - | assert:0 |
| `testUpdateAliveParentNydstate` | 更新 / alive / 親 / nydstate | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / nydstate | 良い | - | - |
| `testUpdateAliveParentPurupuru` | 更新 / alive / 親 / purupuru | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / purupuru | 良い | - | - |
| `testUpdateAliveParentNoPurupuru` | 更新 / alive / 親 / なし / purupuru | Entity/世界実体の基盤回帰 / 更新 / alive / 親 / なし / purupuru | 良い | - | assert:0 |
| `testStalkMotherReactsWhenRndHits` | stalk / 母 / reacts / when / rnd / hits | Entity/世界実体の基盤回帰 / stalk / 母 / reacts / when / rnd / hits | 良い | - | - |
| `testStalkMotherNoReactionWhenRndMisses` | stalk / 母 / なし / reaction / when / rnd / misses | Entity/世界実体の基盤回帰 / stalk / 母 / なし / reaction / when / rnd / misses | 良い | - | - |
| `testStalkMotherNoReactionWhenNoStalk` | stalk / 母 / なし / reaction / when / なし / stalk | Entity/世界実体の基盤回帰 / stalk / 母 / なし / reaction / when / なし / stalk | 良い | - | - |
| `testScenarioFixBackNeedleUpdateWakesBodyFacesPainAndCanTriggerPurupuru` | シナリオ / fix / back / 針 / 更新 / wakes / 本体 / faces / pain / and / 可否 / trigger / purupuru | Entity/世界実体の基盤回帰 / シナリオ / fix / back / 針 / 更新 / wakes / 本体 / faces / pain / and / 可否 / trigger / purupuru | 良い | - | - |
| `testScenarioUnbirthChildNeedleCanTriggerStalkMotherReaction` | シナリオ / unbirth / 子 / 針 / 可否 / trigger / stalk / 母 / reaction | Entity/世界実体の基盤回帰 / シナリオ / unbirth / 子 / 針 / 可否 / trigger / stalk / 母 / reaction | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |

### `OrangeAmpouleTest`
- 状態: 完了 (20/20 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReducesDamage` | 更新 / reduces / ダメージ | Entity/世界実体の基盤回帰 / 更新 / reduces / ダメージ | 良い | - | - |
| `testUpdateDoesNotReviveWhenCrushed` | 更新 / does / 非 / revive / when / crushed | Entity/世界実体の基盤回帰 / 更新 / does / 非 / revive / when / crushed | 良い | - | - |
| `testUpdateDoesNotReviveWhenBurned` | 更新 / does / 非 / revive / when / burned | Entity/世界実体の基盤回帰 / 更新 / does / 非 / revive / when / burned | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdateRevivesWhenDeadNotCrushedNotBurned` | 更新 / revives / when / 死亡 / 非 / crushed / 非 / burned | Entity/世界実体の基盤回帰 / 更新 / revives / when / 死亡 / 非 / crushed / 非 / burned | 良い | - | - |
| `testUpdateReducesDamageWhenAlive` | 更新 / reduces / ダメージ / when / alive | Entity/世界実体の基盤回帰 / 更新 / reduces / ダメージ / when / alive | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioDeadNonBurnedBodyRevivesAndRecoversDamage` | シナリオ / 死亡 / non / burned / 本体 / revives / and / recovers / ダメージ | Entity/世界実体の基盤回帰 / 死亡時ガード回帰 | 良い | - | - |
| `testScenarioBurnedCorpseDoesNotReviveOrRecoverDamage` | シナリオ / burned / corpse / does / 非 / revive / or / recover / ダメージ | Entity/世界実体の基盤回帰 / シナリオ / burned / corpse / does / 非 / revive / or / recover / ダメージ | 良い | - | - |

### `PoisonAmpouleTest`
- 状態: 完了 (21/21 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsDead` | 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | 良い | - | - |
| `testUpdateIncreasesShitWhenAlive` | 更新 / increases / shit / when / alive | Entity/世界実体の基盤回帰 / 更新 / increases / shit / when / alive | 良い | - | - |
| `testUpdateSetsHappinessToSad` | 更新 / sets / 幸福 / to / sad | Entity/世界実体の基盤回帰 / 更新 / sets / 幸福 / to / sad | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testUpdatePoisonDamageWhenRndHits` | 更新 / poison / ダメージ / when / rnd / hits | Entity/世界実体の基盤回帰 / 更新 / poison / ダメージ / when / rnd / hits | 良い | - | - |
| `testUpdateNoPoisonDamageWhenRndMisses` | 更新 / なし / poison / ダメージ / when / rnd / misses | Entity/世界実体の基盤回帰 / 更新 / なし / poison / ダメージ / when / rnd / misses | 良い | - | - |
| `testUpdateDoesNotAddShitWhenCut` | 更新 / does / 非 / 追加 / shit / when / cut | Entity/世界実体の基盤回帰 / 更新 / does / 非 / 追加 / shit / when / cut | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioLivePoisonAmpouleHitWakesBodyAddsShitAndAppliesPoisonDamage` | シナリオ / live / poison / ampoule / hit / wakes / 本体 / adds / shit / and / applies / poison / ダメージ | Entity/世界実体の基盤回帰 / シナリオ / live / poison / ampoule / hit / wakes / 本体 / adds / shit / and / applies / poison / ダメージ | 良い | - | - |
| `testScenarioCutBodyDoesNotWakeOrGainShitWhenPoisonDoesNotProc` | シナリオ / cut / 本体 / does / 非 / wake / or / gain / shit / when / poison / does / 非 / proc | Entity/世界実体の基盤回帰 / シナリオ / cut / 本体 / does / 非 / wake / or / gain / shit / when / poison / does / 非 / proc | 良い | - | - |

### `StopAmpouleTest`
- 状態: 完了 (18/18 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReducesAgeWhenNotAdult` | 更新 / reduces / age / when / 非 / adult | Entity/世界実体の基盤回帰 / 更新 / reduces / age / when / 非 / adult | 良い | - | - |
| `testUpdateReducesAgeWhenBaby` | 更新 / reduces / age / when / baby | Entity/世界実体の基盤回帰 / 更新 / reduces / age / when / baby | 良い | - | - |
| `testUpdateDoesNotReduceAgeWhenAdult` | 更新 / does / 非 / reduce / age / when / adult | Entity/世界実体の基盤回帰 / 更新 / does / 非 / reduce / age / when / adult | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioChildBodyLosesExactlyOneStopTickOfAge` | シナリオ / 子 / 本体 / loses / exactly / one / stop / tick / of / age | Entity/世界実体の基盤回帰 / シナリオ / 子 / 本体 / loses / exactly / one / stop / tick / of / age | 良い | - | - |
| `testScenarioAdultBodyAgeRemainsStable` | シナリオ / adult / 本体 / age / remains / stable | Entity/世界実体の基盤回帰 / シナリオ / adult / 本体 / age / remains / stable | 良い | - | - |

### `VeryShitAmpouleTest`
- 状態: 完了 (18/18 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStaticAccessors` | static / accessors | Entity/世界実体の基盤回帰 / static / accessors | 良い | - | - |
| `testConstructorDefaultsAndBoundary` | constructor / defaults / and / boundary | Entity/世界実体の基盤回帰 / constructor / defaults / and / boundary | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsNull` | 更新 / 戻り / do / nothing / when / 親 / 状態 / null | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / null | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsDead` | 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / 死亡 | 良い | - | - |
| `testUpdateReturnsDoNothingWhenParentIsCut` | 更新 / 戻り / do / nothing / when / 親 / 状態 / cut | Entity/世界実体の基盤回帰 / 更新 / 戻り / do / nothing / when / 親 / 状態 / cut | 良い | - | - |
| `testUpdateSetsShitWhenAlive` | 更新 / sets / shit / when / alive | Entity/世界実体の基盤回帰 / 更新 / sets / shit / when / alive | 良い | - | - |
| `testGetImageReturnsNullWhenParentNotInMap` | 取得 / image / 戻り / null / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / null / when / 親 / 非 / in / map | 良い | - | - |
| `testGetImageReturnsLeftImageWhenDirectionLeft` | 取得 / image / 戻り / left / image / when / direction / left | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / left / image / when / direction / left | 良い | - | - |
| `testGetImageReturnsRightImageWhenDirectionRight` | 取得 / image / 戻り / right / image / when / direction / right | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / right / image / when / direction / right | 良い | - | - |
| `testGetImageReturnsCorrectImageForAge` | 取得 / image / 戻り / correct / image / for / age | Entity/世界実体の基盤回帰 / 取得 / image / 戻り / correct / image / for / age | 良い | - | - |
| `testResetBoundaryUsesParentAge` | reset / boundary / uses / 親 / age | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testResetBoundaryDoesNothingWhenParentNotInMap` | reset / boundary / does / nothing / when / 親 / 非 / in / map | Entity/世界実体の基盤回帰 / 復活/再生回帰 | 良い | - | - |
| `testToStringUsesResourceUtil` | to / string / uses / resource / util | Entity/世界実体の基盤回帰 / to / string / uses / resource / util | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithParentNotInWorld` | constructor / with / 親 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / 親 / 非 / in / world | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioLiveBodyWakesUpAndSetsShitNearLimit` | シナリオ / live / 本体 / wakes / up / and / sets / shit / near / limit | Entity/世界実体の基盤回帰 / シナリオ / live / 本体 / wakes / up / and / sets / shit / near / limit | 良い | - | - |
| `testScenarioCutBodyKeepsSleepingAndDoesNotRaiseShit` | シナリオ / cut / 本体 / 維持 / sleeping / and / does / 非 / raise / shit | Entity/世界実体の基盤回帰 / シナリオ / cut / 本体 / 維持 / sleeping / and / does / 非 / raise / shit | 良い | - | - |

## `org.simyukkuri.entity.core.effect`
### `EffectTest`
- 状態: 完了 (14/14 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorRegistersFrontEffectAndInitializesFields` | constructor / registers / front / effect / and / initializes / fields | Entity/世界実体の基盤回帰 / constructor / registers / front / effect / and / initializes / fields | 良い | - | - |
| `testConstructorRegistersSortEffectWhenBack` | constructor / registers / sort / effect / when / back | Entity/世界実体の基盤回帰 / constructor / registers / sort / effect / when / back | 良い | - | - |
| `testClockTickRemovesWhenLifetimeExpired` | clock / tick / removes / when / lifetime / expired | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAnimateLoopEndsStopsAnimation` | clock / tick / animate / loop / ends / stops / animation | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAnimateEndRemovesOnComplete` | clock / tick / animate / end / removes / on / complete | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGravityAffectsZ` | clock / tick / gravity / affects / z | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testSetDirectionFiresProbe` | 設定 / direction / fires / probe | Entity/世界実体の基盤回帰 / 設定 / direction / fires / probe | 良い | - | - |
| `testGetIntervalFiresProbe` | 取得 / interval / fires / probe | Entity/世界実体の基盤回帰 / 取得 / interval / fires / probe | 良い | - | - |
| `testGetFramesFiresProbe` | 取得 / frames / fires / probe | Entity/世界実体の基盤回帰 / 取得 / frames / fires / probe | 良い | - | - |
| `testSetLifeTimeFiresProbe` | 設定 / life / time / fires / probe | Entity/世界実体の基盤回帰 / 設定 / life / time / fires / probe | 良い | - | - |
| `testGetAnimeIntervalFiresProbe` | 取得 / anime / interval / fires / probe | Entity/世界実体の基盤回帰 / 取得 / anime / interval / fires / probe | 良い | - | - |
| `testGetAnimeLoopFiresProbe` | 取得 / anime / loop / fires / probe | Entity/世界実体の基盤回帰 / 取得 / anime / loop / fires / probe | 良い | - | - |
| `testIsAnimeEndFiresProbe` | 状態 / anime / end / fires / probe | Entity/世界実体の基盤回帰 / 状態 / anime / end / fires / probe | 良い | - | - |
| `testSetEnableGravityFiresProbe` | 設定 / enable / gravity / fires / probe | Entity/世界実体の基盤回帰 / 設定 / enable / gravity / fires / probe | 良い | - | - |

## `org.simyukkuri.entity.core.living.yukkuri`
### `BodyAttributesBurstTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 画像・描画用データが壊れない
  - 世界状態の保存/復元と進行が壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBurstStateWithNullSprites` | 破裂 / state / with / null / sprites | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / with / null / sprites | 良い | - | - |
| `testBurstStateWithZeroOriginSize` | 破裂 / state / with / zero / origin / size | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / with / zero / origin / size | 良い | - | - |
| `testScenarioExpandWidthAtFiveQuarterThresholdIsSafe` | シナリオ / expand / width / at / five / quarter / threshold / 状態 / safe | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / expand / width / at / five / quarter / threshold / 状態 / safe | 良い | - | - |
| `testScenarioExpandWidthAtSixQuarterThresholdIsHalf` | シナリオ / expand / width / at / six / quarter / threshold / 状態 / half | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / expand / width / at / six / quarter / threshold / 状態 / half | 良い | - | - |
| `testScenarioExpandWidthAtSevenQuarterThresholdIsNear` | シナリオ / expand / width / at / seven / quarter / threshold / 状態 / near | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / expand / width / at / seven / quarter / threshold / 状態 / near | 良い | - | - |
| `testScenarioExpandWidthAtDoubleWidthThresholdIsBurst` | シナリオ / expand / width / at / double / width / threshold / 状態 / 破裂 | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / expand / width / at / double / width / threshold / 状態 / 破裂 | 良い | - | - |

### `BodyAttributesTest`
- 状態: 完了 (361/361 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testScenarioAddStressAtBurstHalfDoesNotIncreaseShitButStillRaisesStress` | シナリオ / 追加 / ストレス / at / 破裂 / half / does / 非 / increase / shit / but / still / raises / ストレス | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / 追加 / ストレス / at / 破裂 / half / does / 非 / increase / shit / but / still / raises / ストレス | 良い | - | - |
| `testScenarioBabyTypesDequeuePreservesInsertionOrderAcrossMultipleEntries` | シナリオ / baby / types / dequeue / preserves / insertion / order / across / multiple / entries | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / baby / types / dequeue / preserves / insertion / order / across / multiple / entries | 良い | - | - |
| `testScenarioHasBabyOrStalkReflectsEitherPregnancyState` | シナリオ / 有無 / baby / or / stalk / reflects / either / pregnancy / state | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / 有無 / baby / or / stalk / reflects / either / pregnancy / state | 良い | - | - |
| `testInitialValues` | initial / values | ゆっくり本体の状態/行動/イベント回帰 / initial / values | 良い | - | - |
| `testSettersGetters` | setters / getters | ゆっくり本体の状態/行動/イベント回帰 / setters / getters | 良い | - | - |
| `testNameArraysSetGet` | name / arrays / 設定 / 取得 | ゆっくり本体の状態/行動/イベント回帰 / name / arrays / 設定 / 取得 | 良い | - | - |
| `testShadowImageStatics` | shadow / image / statics | ゆっくり本体の状態/行動/イベント回帰 / shadow / image / statics | 良い | - | - |
| `testSpriteAccessors` | sprite / accessors | ゆっくり本体の状態/行動/イベント回帰 / sprite / accessors | 良い | - | - |
| `testBaseArraysAndLimitsAccessors` | base / arrays / and / limits / accessors | ゆっくり本体の状態/行動/イベント回帰 / base / arrays / and / limits / accessors | 良い | - | - |
| `testBasePeriodsAndLimitsAccessors` | base / periods / and / limits / accessors | ゆっくり本体の状態/行動/イベント回帰 / base / periods / and / limits / accessors | 良い | - | - |
| `testAccidentProbabilitiesAccessors` | accident / probabilities / accessors | ゆっくり本体の状態/行動/イベント回帰 / accident / probabilities / accessors | 良い | - | - |
| `testProtectedFieldAccess` | protected / field / access | ゆっくり本体の状態/行動/イベント回帰 / protected / field / access | 良い | - | - |
| `testSetAgeStateBaby` | 設定 / age / state / baby | ゆっくり本体の状態/行動/イベント回帰 / 設定 / age / state / baby | 良い | - | - |
| `testSetAgeStateChild` | 設定 / age / state / 子 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / age / state / 子 | 良い | - | - |
| `testSetAgeStateAdult` | 設定 / age / state / adult | ゆっくり本体の状態/行動/イベント回帰 / 設定 / age / state / adult | 良い | - | - |
| `testGetBodyAgeStateBoundary` | 取得 / 本体 / age / state / boundary | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / age / state / boundary | 良い | - | - |
| `testIsAdultChildBaby` | 状態 / adult / 子 / baby | ゆっくり本体の状態/行動/イベント回帰 / 状態 / adult / 子 / baby | 良い | - | - |
| `testGetDamageStateNone` | 取得 / ダメージ / state / none | ゆっくり本体の状態/行動/イベント回帰 / 取得 / ダメージ / state / none | 良い | - | - |
| `testGetDamageStateVery` | 取得 / ダメージ / state / very | ゆっくり本体の状態/行動/イベント回帰 / 取得 / ダメージ / state / very | 良い | - | - |
| `testGetDamageStateToomuch` | 取得 / ダメージ / state / toomuch | ゆっくり本体の状態/行動/イベント回帰 / 取得 / ダメージ / state / toomuch | 良い | - | - |
| `testGetDamageLimit` | 取得 / ダメージ / limit | ゆっくり本体の状態/行動/イベント回帰 / 取得 / ダメージ / limit | 良い | - | - |
| `testPainNoneByDefault` | pain / none / by / default | ゆっくり本体の状態/行動/イベント回帰 / pain / none / by / default | 良い | - | - |
| `testBurstStateNone` | 破裂 / state / none | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / none | 良い | - | - |
| `testBurstStateBurst` | 破裂 / state / 破裂 | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / 破裂 | 良い | - | assert:0 |
| `testPainVeryWhenNeedled` | pain / very / when / needled | ゆっくり本体の状態/行動/イベント回帰 / pain / very / when / needled | 良い | - | - |
| `testPainSomeWhenCriticalDamage` | pain / some / when / critical / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / pain / some / when / critical / ダメージ | 良い | - | - |
| `testIsVeryRude` | 状態 / very / rude | ゆっくり本体の状態/行動/イベント回帰 / 状態 / very / rude | 良い | - | - |
| `testIsRude` | 状態 / rude | ゆっくり本体の状態/行動/イベント回帰 / 状態 / rude | 良い | - | - |
| `testIsNormal` | 状態 / normal | ゆっくり本体の状態/行動/イベント回帰 / 状態 / normal | 良い | - | - |
| `testIsSmartNice` | 状態 / smart / nice | ゆっくり本体の状態/行動/イベント回帰 / 状態 / smart / nice | 良い | - | - |
| `testIsSmartVeryNice` | 状態 / smart / very / nice | ゆっくり本体の状態/行動/イベント回帰 / 状態 / smart / very / nice | 良い | - | - |
| `testAddHungry` | 追加 / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 空腹 | 良い | - | - |
| `testAddHungryNegative` | 追加 / 空腹 / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 空腹 / negative | 良い | - | - |
| `testGetHungryLimit` | 取得 / 空腹 / limit | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 空腹 / limit | 良い | - | - |
| `testIsVeryHungry` | 状態 / very / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / very / 空腹 | 良い | - | - |
| `testIsVeryHungryDeadReturnsFalse` | 状態 / very / 空腹 / 死亡 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / very / 空腹 / 死亡 / 戻り / false | 良い | - | - |
| `testIsStarving` | 状態 / starving | ゆっくり本体の状態/行動/イベント回帰 / 状態 / starving | 良い | - | - |
| `testIsStarvingNotToomuch` | 状態 / starving / 非 / toomuch | ゆっくり本体の状態/行動/イベント回帰 / 状態 / starving / 非 / toomuch | 良い | - | - |
| `testGetEatAmount` | 取得 / eat / amount | ゆっくり本体の状態/行動/イベント回帰 / 取得 / eat / amount | 良い | - | - |
| `testSetStressPositive` | 設定 / ストレス / positive | ゆっくり本体の状態/行動/イベント回帰 / 設定 / ストレス / positive | 良い | - | - |
| `testSetStressZeroIgnored` | 設定 / ストレス / zero / ignored | ゆっくり本体の状態/行動/イベント回帰 / 設定 / ストレス / zero / ignored | 良い | - | - |
| `testSetStressNegativeIgnored` | 設定 / ストレス / negative / ignored | ゆっくり本体の状態/行動/イベント回帰 / 設定 / ストレス / negative / ignored | 良い | - | - |
| `testAddStressDeadIgnored` | 追加 / ストレス / 死亡 / ignored | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / 死亡 / ignored | 良い | - | - |
| `testAddStressAlive` | 追加 / ストレス / alive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / alive | 良い | - | - |
| `testAddStressAlsoIncreasesShit` | 追加 / ストレス / also / increases / shit | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / also / increases / shit | 良い | - | - |
| `testGetStressLimit` | 取得 / ストレス / limit | ゆっくり本体の状態/行動/イベント回帰 / 取得 / ストレス / limit | 良い | - | - |
| `testPlusShit` | plus / shit | ゆっくり本体の状態/行動/イベント回帰 / plus / shit | 良い | - | - |
| `testPlusShitZeroBaseIgnored` | plus / shit / zero / base / ignored | ゆっくり本体の状態/行動/イベント回帰 / plus / shit / zero / base / ignored | 良い | - | - |
| `testPlusShitNegativeIgnored` | plus / shit / negative / ignored | ゆっくり本体の状態/行動/イベント回帰 / plus / shit / negative / ignored | 良い | - | - |
| `testSetShitDirect` | 設定 / shit / direct | ゆっくり本体の状態/行動/イベント回帰 / 設定 / shit / direct | 良い | - | - |
| `testSetShitWhileShittingIgnored` | 設定 / shit / while / shitting / ignored | ゆっくり本体の状態/行動/イベント回帰 / 設定 / shit / while / shitting / ignored | 良い | - | - |
| `testSetShitVeryShit` | 設定 / shit / very / shit | ゆっくり本体の状態/行動/イベント回帰 / 設定 / shit / very / shit | 良い | - | - |
| `testGetShitLimit` | 取得 / shit / limit | ゆっくり本体の状態/行動/イベント回帰 / 取得 / shit / limit | 良い | - | - |
| `testIsBeggingForLifeAlive` | 状態 / begging / for / life / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / begging / for / life / alive | 良い | - | - |
| `testIsBeggingForLifeDead` | 状態 / begging / for / life / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / begging / for / life / 死亡 | 良い | - | - |
| `testSetBeggingBlockedByBuryState` | 設定 / begging / blocked / by / bury / state | ゆっくり本体の状態/行動/イベント回帰 / 設定 / begging / blocked / by / bury / state | 良い | - | - |
| `testIsStrikeAliveAndDead` | 状態 / 打撃 / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 打撃 / alive / and / 死亡 | 良い | - | - |
| `testIsBirthAliveAndDead` | 状態 / birth / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / birth / alive / and / 死亡 | 良い | - | - |
| `testIsEatingAliveAndDead` | 状態 / eating / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / eating / alive / and / 死亡 | 良い | - | - |
| `testIsSukkiriAliveAndDead` | 状態 / sukkiri / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sukkiri / alive / and / 死亡 | 良い | - | - |
| `testIsNeedledAliveAndDead` | 状態 / needled / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / needled / alive / and / 死亡 | 良い | - | - |
| `testIsStubbornlyDirtyAliveAndDead` | 状態 / stubbornly / dirty / alive / and / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / stubbornly / dirty / alive / and / 死亡 | 良い | - | - |
| `testGetFatherMother` | 取得 / 父 / 母 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 父 / 母 | 良い | - | - |
| `testSisterListSize` | 姉妹 / list / size | ゆっくり本体の状態/行動/イベント回帰 / 姉妹 / list / size | 良い | - | - |
| `testElderSisterListSize` | elder / 姉妹 / list / size | ゆっくり本体の状態/行動/イベント回帰 / elder / 姉妹 / list / size | 良い | - | - |
| `testChildrenListSize` | children / list / size | ゆっくり本体の状態/行動/イベント回帰 / children / list / size | 良い | - | - |
| `testInitAmount` | init / amount | ゆっくり本体の状態/行動/イベント回帰 / init / amount | 良い | - | - |
| `testAddAmountReturnsFalseWhenPositive` | 追加 / amount / 戻り / false / when / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / 戻り / false / when / positive | 良い | - | - |
| `testAddAmountReturnsTrueWhenZero` | 追加 / amount / 戻り / true / when / zero | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / 戻り / true / when / zero | 良い | - | - |
| `testAddAmountReturnsTrueWhenNegative` | 追加 / amount / 戻り / true / when / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / 戻り / true / when / negative | 良い | - | - |
| `testIsSickFalseWhenBelowIncubation` | 状態 / 病気 / false / when / below / incubation | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsSickTrueWhenAboveIncubation` | 状態 / 病気 / true / when / above / incubation | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsSickHeavily` | 状態 / 病気 / heavily | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsSickTooHeavily` | 状態 / 病気 / too / heavily | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsSickTooHeavilyNoDamage` | 状態 / 病気 / too / heavily / なし / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testForceSetSick` | force / 設定 / 病気 | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 病気 | 良い | - | - |
| `testFindSickWise` | find / 病気 / wise | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / wise | 良い | - | - |
| `testFindSickFoolOnlyDetectsHeavy` | find / 病気 / fool / only / detects / heavy | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / fool / only / detects / heavy | 良い | - | - |
| `testIsNyddefault` | 状態 / nyddefault | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nyddefault | 良い | - | - |
| `testIsNydnear` | 状態 / nydnear | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nydnear | 良い | - | - |
| `testIsNyddisease` | 状態 / nyddisease | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nyddisease | 良い | - | - |
| `testIsOldFalse` | 状態 / old / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / old / false | 良い | - | - |
| `testIsOldTrue` | 状態 / old / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / old / true | 良い | - | - |
| `testIsSleepyFalseWhenSleeping` | 状態 / sleepy / false / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sleepy / false / when / sleeping | 良い | - | - |
| `testIsSleepyFalseWhenNotEnoughTime` | 状態 / sleepy / false / when / 非 / enough / time | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sleepy / false / when / 非 / enough / time | 良い | - | - |
| `testIsSleepyTrueWhenEnoughTime` | 状態 / sleepy / true / when / enough / time | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sleepy / true / when / enough / time | 良い | - | - |
| `testSetExcitingTrue` | 設定 / exciting / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / exciting / true | 良い | - | - |
| `testSetExcitingDead` | 設定 / exciting / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / exciting / 死亡 | 良い | - | - |
| `testSetCalm` | 設定 / calm | ゆっくり本体の状態/行動/イベント回帰 / 設定 / calm | 良い | - | - |
| `testAddAndCountAttachment` | 追加 / and / count / attachment | ゆっくり本体の状態/行動/イベント回帰 / 追加 / and / count / attachment | 良い | - | - |
| `testRemoveAttachment` | 除去 / attachment | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testFootBakeLevelNone` | foot / bake / level / none | ゆっくり本体の状態/行動/イベント回帰 / foot / bake / level / none | 良い | - | - |
| `testFootBakeLevelMidium` | foot / bake / level / midium | ゆっくり本体の状態/行動/イベント回帰 / foot / bake / level / midium | 良い | - | - |
| `testFootBakeLevelCritical` | foot / bake / level / critical | ゆっくり本体の状態/行動/イベント回帰 / foot / bake / level / critical | 良い | - | - |
| `testFootBakeNegativeClamped` | foot / bake / negative / clamped | ゆっくり本体の状態/行動/イベント回帰 / foot / bake / negative / clamped | 良い | - | - |
| `testBodyBakeLevelNone` | 本体 / bake / level / none | ゆっくり本体の状態/行動/イベント回帰 / 本体 / bake / level / none | 良い | - | - |
| `testBodyBakeLevelMidium` | 本体 / bake / level / midium | ゆっくり本体の状態/行動/イベント回帰 / 本体 / bake / level / midium | 良い | - | - |
| `testBodyBakeLevelCritical` | 本体 / bake / level / critical | ゆっくり本体の状態/行動/イベント回帰 / 本体 / bake / level / critical | 良い | - | - |
| `testIsGotBurnedFalse` | 状態 / got / burned / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / false | 良い | - | - |
| `testIsGotBurnedTrue` | 状態 / got / burned / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / true | 良い | - | - |
| `testAddBodyBakePeriod` | 追加 / 本体 / bake / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 本体 / bake / period | 良い | - | - |
| `testAddFootBakePeriod` | 追加 / foot / bake / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / foot / bake / period | 良い | - | - |
| `testGetTangTypePoor` | 取得 / tang / type / poor | ゆっくり本体の状態/行動/イベント回帰 / 取得 / tang / type / poor | 良い | - | - |
| `testGetTangTypeNormal` | 取得 / tang / type / normal | ゆっくり本体の状態/行動/イベント回帰 / 取得 / tang / type / normal | 良い | - | - |
| `testGetTangTypeGourmet` | 取得 / tang / type / gourmet | ゆっくり本体の状態/行動/イベント回帰 / 取得 / tang / type / gourmet | 良い | - | - |
| `testAddTang` | 追加 / tang | ゆっくり本体の状態/行動/イベント回帰 / 追加 / tang | 良い | - | - |
| `testAddAmaamaDiscipline` | 追加 / amaama / discipline | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline | 良い | - | - |
| `testAddAmaamaDisciplineUpperClamp` | 追加 / amaama / discipline / upper / 範囲補正 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline / upper / 範囲補正 | 良い | - | - |
| `testAddAmaamaDisciplineLowerClamp` | 追加 / amaama / discipline / lower / 範囲補正 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline / lower / 範囲補正 | 良い | - | - |
| `testAddLovePlayerPositive` | 追加 / love / player / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / positive | 良い | - | - |
| `testAddLovePlayerUpperClamp` | 追加 / love / player / upper / 範囲補正 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / upper / 範囲補正 | 良い | - | - |
| `testAddLovePlayerLowerClamp` | 追加 / love / player / lower / 範囲補正 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / lower / 範囲補正 | 良い | - | - |
| `testAddLovePlayerNydforcesHate` | 追加 / love / player / nydforces / hate | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / nydforces / hate | 良い | - | - |
| `testAddMemoriesWise` | 追加 / memories / wise | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / wise | 良い | - | - |
| `testAddMemoriesFoolPositive` | 追加 / memories / fool / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / fool / positive | 良い | - | - |
| `testAddMemoriesFoolNegative` | 追加 / memories / fool / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / fool / negative | 良い | - | - |
| `testAddMemoriesAveragePositive` | 追加 / memories / average / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / average / positive | 良い | - | - |
| `testAddMemoriesAverageNegative` | 追加 / memories / average / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / average / negative | 良い | - | - |
| `testSetHappinessDeadForcesAverage` | 設定 / 幸福 / 死亡 / forces / average | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / 死亡 / forces / average | 良い | - | - |
| `testSetHappinessNydforcesVerySad` | 設定 / 幸福 / nydforces / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / nydforces / very / sad | 良い | - | - |
| `testSetHappinessHappyClearsScareAndAngry` | 設定 / 幸福 / happy / clears / scare / and / angry | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / happy / clears / scare / and / angry | 良い | - | - |
| `testSetHappinessSadDoesNotOverrideVerySad` | 設定 / 幸福 / sad / does / 非 / override / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / sad / does / 非 / override / very / sad | 良い | - | - |
| `testSetHappinessHappyDoesNotOverrideVeryHappy` | 設定 / 幸福 / happy / does / 非 / override / very / happy | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / happy / does / 非 / override / very / happy | 良い | - | - |
| `testCanFurifuriTrue` | 可否 / furifuri / true | ゆっくり本体の状態/行動/イベント回帰 / 可否 / furifuri / true | 良い | - | - |
| `testCanFurifuriFalseWhenBurnedCritical` | 可否 / furifuri / false / when / burned / critical | ゆっくり本体の状態/行動/イベント回帰 / 可否 / furifuri / false / when / burned / critical | 良い | - | - |
| `testCanFurifuriFalseWhenNyd` | 可否 / furifuri / false / when / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 可否 / furifuri / false / when / 非ゆっくり症 | 良い | - | - |
| `testGetBabyTypesDequeueEmpty` | 取得 / baby / types / dequeue / empty | ゆっくり本体の状態/行動/イベント回帰 / 取得 / baby / types / dequeue / empty | 良い | - | - |
| `testGetBabyTypesDequeueWithItem` | 取得 / baby / types / dequeue / with / item | ゆっくり本体の状態/行動/イベント回帰 / 取得 / baby / types / dequeue / with / item | 良い | - | - |
| `testGetStalksDequeueEmpty` | 取得 / stalks / dequeue / empty | ゆっくり本体の状態/行動/イベント回帰 / 取得 / stalks / dequeue / empty | 良い | - | - |
| `testGetStalksDequeueWithItem` | 取得 / stalks / dequeue / with / item | ゆっくり本体の状態/行動/イベント回帰 / 取得 / stalks / dequeue / with / item | 良い | - | - |
| `testEqualsNull` | equals / null | ゆっくり本体の状態/行動/イベント回帰 / equals / null | 良い | - | - |
| `testEqualsWrongType` | equals / wrong / type | ゆっくり本体の状態/行動/イベント回帰 / equals / wrong / type | 良い | - | - |
| `testEqualsSameUniqueId` | equals / same / unique / id | ゆっくり本体の状態/行動/イベント回帰 / equals / same / unique / id | 良い | - | - |
| `testEqualsDifferentUniqueId` | equals / different / unique / id | ゆっくり本体の状態/行動/イベント回帰 / equals / different / unique / id | 良い | - | - |
| `testHashCode` | hash / code | ゆっくり本体の状態/行動/イベント回帰 / hash / code | 良い | - | - |
| `testCompareToNull` | compare / to / null | ゆっくり本体の状態/行動/イベント回帰 / compare / to / null | 良い | - | - |
| `testCompareToWrongType` | compare / to / wrong / type | ゆっくり本体の状態/行動/イベント回帰 / compare / to / wrong / type | 良い | - | - |
| `testCompareToSame` | compare / to / same | ゆっくり本体の状態/行動/イベント回帰 / compare / to / same | 良い | - | - |
| `testCompareToSmaller` | compare / to / smaller | ゆっくり本体の状態/行動/イベント回帰 / compare / to / smaller | 良い | - | - |
| `testSetCantDie` | 設定 / cant / die | ゆっくり本体の状態/行動/イベント回帰 / 設定 / cant / die | 良い | - | - |
| `testHasTraumaFalse` | 有無 / trauma / false | ゆっくり本体の状態/行動/イベント回帰 / 有無 / trauma / false | 良い | - | - |
| `testHasTraumaTrue` | 有無 / trauma / true | ゆっくり本体の状態/行動/イベント回帰 / 有無 / trauma / true | 良い | - | - |
| `testCutHair` | cut / 毛 | ゆっくり本体の状態/行動/イベント回帰 / cut / 毛 | 良い | - | - |
| `testBurstStateBurst` | 破裂 / state / 破裂 | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / 破裂 | 良い | - | - |
| `testBurstStateNear` | 破裂 / state / near | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / near | 良い | - | - |
| `testBurstStateHalf` | 破裂 / state / half | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / half | 良い | - | - |
| `testBurstStateSafe` | 破裂 / state / safe | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / safe | 良い | - | - |
| `testBurstStateNone` | 破裂 / state / none | ゆっくり本体の状態/行動/イベント回帰 / 破裂 / state / none | 良い | - | - |
| `testDamageStateNone` | ダメージ / state / none | ゆっくり本体の状態/行動/イベント回帰 / ダメージ / state / none | 良い | - | - |
| `testDamageStateVery` | ダメージ / state / very | ゆっくり本体の状態/行動/イベント回帰 / ダメージ / state / very | 良い | - | - |
| `testDamageStateToomuch` | ダメージ / state / toomuch | ゆっくり本体の状態/行動/イベント回帰 / ダメージ / state / toomuch | 良い | - | - |
| `testDamageStateTriggersDeath` | ダメージ / state / triggers / death | ゆっくり本体の状態/行動/イベント回帰 / ダメージ / state / triggers / death | 良い | - | - |
| `testTangTypePoor` | tang / type / poor | ゆっくり本体の状態/行動/イベント回帰 / tang / type / poor | 良い | - | - |
| `testTangTypeNormal` | tang / type / normal | ゆっくり本体の状態/行動/イベント回帰 / tang / type / normal | 良い | - | - |
| `testTangTypeGourmet` | tang / type / gourmet | ゆっくり本体の状態/行動/イベント回帰 / tang / type / gourmet | 良い | - | - |
| `testAddLovePlayerNyd` | 追加 / love / player / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / 非ゆっくり症 | 良い | - | - |
| `testAddLovePlayerNormal` | 追加 / love / player / normal | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / normal | 良い | - | - |
| `testAddLovePlayerUpperLimit` | 追加 / love / player / upper / limit | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / upper / limit | 良い | - | - |
| `testAddLovePlayerLowerLimit` | 追加 / love / player / lower / limit | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / lower / limit | 良い | - | - |
| `testAddMemoriesWise` | 追加 / memories / wise | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / wise | 良い | - | - |
| `testAddMemoriesFoolPositive` | 追加 / memories / fool / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / fool / positive | 良い | - | - |
| `testAddMemoriesFoolNegative` | 追加 / memories / fool / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / fool / negative | 良い | - | - |
| `testAddMemoriesAveragePositive` | 追加 / memories / average / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / average / positive | 良い | - | - |
| `testAddMemoriesAverageNegative` | 追加 / memories / average / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / memories / average / negative | 良い | - | - |
| `testFindSickWiseDetectsLightSick` | find / 病気 / wise / detects / light / 病気 | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / wise / detects / light / 病気 | 良い | - | - |
| `testFindSickAverageDetectsLightSick` | find / 病気 / average / detects / light / 病気 | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / average / detects / light / 病気 | 良い | - | - |
| `testFindSickFoolDoesNotDetectLightSick` | find / 病気 / fool / does / 非 / detect / light / 病気 | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / fool / does / 非 / detect / light / 病気 | 良い | - | - |
| `testFindSickFoolDetectsHeavySick` | find / 病気 / fool / detects / heavy / 病気 | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / fool / detects / heavy / 病気 | 良い | - | - |
| `testFindSickNoSick` | find / 病気 / なし / 病気 | ゆっくり本体の状態/行動/イベント回帰 / find / 病気 / なし / 病気 | 良い | - | - |
| `testSetHappinessDeadReturnsAverage` | 設定 / 幸福 / 死亡 / 戻り / average | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / 死亡 / 戻り / average | 良い | - | - |
| `testSetHappinessNydreturnVerySad` | 設定 / 幸福 / nydreturn / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / nydreturn / very / sad | 良い | - | - |
| `testSetHappinessSadFromNonVerySad` | 設定 / 幸福 / sad / from / non / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / sad / from / non / very / sad | 良い | - | - |
| `testSetHappinessSadFromVerySadNoChange` | 設定 / 幸福 / sad / from / very / sad / なし / change | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / sad / from / very / sad / なし / change | 良い | - | - |
| `testSetHappinessHappyFromNonVeryHappy` | 設定 / 幸福 / happy / from / non / very / happy | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / happy / from / non / very / happy | 良い | - | - |
| `testSetHappinessHappyFromVeryHappyNoChange` | 設定 / 幸福 / happy / from / very / happy / なし / change | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / happy / from / very / happy / なし / change | 良い | - | - |
| `testSetHappinessVerySad` | 設定 / 幸福 / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / very / sad | 良い | - | - |
| `testSetHappinessVeryHappy` | 設定 / 幸福 / very / happy | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / very / happy | 良い | - | - |
| `testSetToFoodTrue` | 設定 / to / food / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / food / true | 良い | - | - |
| `testSetToFoodFalseClearsPurpose` | 設定 / to / food / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / food / false / clears / purpose | 良い | - | - |
| `testSetToFoodFalseIgnoresIfNotFood` | 設定 / to / food / false / ignores / if / 非 / food | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / food / false / ignores / if / 非 / food | 良い | - | - |
| `testSetToSukkiriTrue` | 設定 / to / sukkiri / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / sukkiri / true | 良い | - | - |
| `testSetToSukkiriFalseClearsPurpose` | 設定 / to / sukkiri / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / sukkiri / false / clears / purpose | 良い | - | - |
| `testSetToShitTrue` | 設定 / to / shit / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / shit / true | 良い | - | - |
| `testSetToShitFalseClearsPurpose` | 設定 / to / shit / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / shit / false / clears / purpose | 良い | - | - |
| `testSetToBedTrue` | 設定 / to / bed / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / bed / true | 良い | - | - |
| `testSetToBedFalseClearsPurpose` | 設定 / to / bed / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / bed / false / clears / purpose | 良い | - | - |
| `testSetToBodyTrue` | 設定 / to / 本体 / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / 本体 / true | 良い | - | - |
| `testSetToBodyFalseClearsPurpose` | 設定 / to / 本体 / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / 本体 / false / clears / purpose | 良い | - | - |
| `testSetToStealTrue` | 設定 / to / steal / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / steal / true | 良い | - | - |
| `testSetToStealFalseClearsPurpose` | 設定 / to / steal / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / steal / false / clears / purpose | 良い | - | - |
| `testSetToTakeoutTrue` | 設定 / to / takeout / true | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / takeout / true | 良い | - | - |
| `testSetToTakeoutFalseClearsPurpose` | 設定 / to / takeout / false / clears / purpose | ゆっくり本体の状態/行動/イベント回帰 / 設定 / to / takeout / false / clears / purpose | 良い | - | - |
| `testIsVainAlive` | 状態 / vain / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / vain / alive | 良い | - | - |
| `testIsVainDead` | 状態 / vain / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / vain / 死亡 | 良い | - | - |
| `testIsNobinobiAlive` | 状態 / nobinobi / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nobinobi / alive | 良い | - | - |
| `testIsNobinobiDead` | 状態 / nobinobi / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nobinobi / 死亡 | 良い | - | - |
| `testIsFurifuriAlive` | 状態 / furifuri / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / furifuri / alive | 良い | - | - |
| `testIsFurifuriDead` | 状態 / furifuri / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / furifuri / 死亡 | 良い | - | - |
| `testIsEatingShitAlive` | 状態 / eating / shit / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / eating / shit / alive | 良い | - | - |
| `testIsEatingShitDead` | 状態 / eating / shit / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / eating / shit / 死亡 | 良い | - | - |
| `testIsPeroperoAlive` | 状態 / peropero / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / peropero / alive | 良い | - | - |
| `testIsPeroPeroDead` | 状態 / pero / pero / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pero / pero / 死亡 | 良い | - | - |
| `testIsYunnyaaAlive` | 状態 / yunnyaa / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / yunnyaa / alive | 良い | - | - |
| `testIsYunnyaaDead` | 状態 / yunnyaa / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / yunnyaa / 死亡 | 良い | - | - |
| `testIsPikopikoTrue` | 状態 / pikopiko / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pikopiko / true | 良い | - | - |
| `testIsPikopikoFalse` | 状態 / pikopiko / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pikopiko / false | 良い | - | - |
| `testIsPurupuruTrue` | 状態 / purupuru / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / purupuru / true | 良い | - | - |
| `testIsPurupuruFalse` | 状態 / purupuru / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / purupuru / false | 良い | - | - |
| `testIsCallingParentsAlive` | 状態 / calling / parents / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / calling / parents / alive | 良い | - | - |
| `testIsCallingParentsDead` | 状態 / calling / parents / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / calling / parents / 死亡 | 良い | - | - |
| `testGetSellingPriceKaiyu` | 取得 / selling / price / kaiyu | ゆっくり本体の状態/行動/イベント回帰 / 取得 / selling / price / kaiyu | 良い | - | - |
| `testGetSellingPriceProcessed` | 取得 / selling / price / processed | ゆっくり本体の状態/行動/イベント回帰 / 取得 / selling / price / processed | 良い | - | - |
| `testGetWeight` | 取得 / weight | ゆっくり本体の状態/行動/イベント回帰 / 取得 / weight | 良い | - | - |
| `testGetStep` | 取得 / step | ゆっくり本体の状態/行動/イベント回帰 / 取得 / step | 良い | - | - |
| `testGetStepDist` | 取得 / step / dist | ゆっくり本体の状態/行動/イベント回帰 / 取得 / step / dist | 良い | - | - |
| `testGetCollisionX` | 取得 / 衝突 / x | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 衝突 / x | 良い | - | - |
| `testGetCollisionY` | 取得 / 衝突 / y | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 衝突 / y | 良い | - | - |
| `testGetW` | 取得 / w | ゆっくり本体の状態/行動/イベント回帰 / 取得 / w | 良い | - | - |
| `testGetH` | 取得 / h | ゆっくり本体の状態/行動/イベント回帰 / 取得 / h | 良い | - | - |
| `testGetBodyBaseSpr` | 取得 / 本体 / base / spr | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / base / spr | 良い | - | - |
| `testGetBodyExpandSpr` | 取得 / 本体 / expand / spr | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / expand / spr | 良い | - | - |
| `testGetBraidSprite` | 取得 / おさげ / sprite | ゆっくり本体の状態/行動/イベント回帰 / 取得 / おさげ / sprite | 良い | - | - |
| `testIsStressfulFalse` | 状態 / stressful / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / stressful / false | 良い | - | - |
| `testIsStressfulTrue` | 状態 / stressful / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / stressful / true | 良い | - | - |
| `testIsVeryStressfulFalse` | 状態 / very / stressful / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / very / stressful / false | 良い | - | - |
| `testIsVeryStressfulTrue` | 状態 / very / stressful / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / very / stressful / true | 良い | - | - |
| `testHasBabyFalse` | 有無 / baby / false | ゆっくり本体の状態/行動/イベント回帰 / 有無 / baby / false | 良い | - | - |
| `testHasBabyTrue` | 有無 / baby / true | ゆっくり本体の状態/行動/イベント回帰 / 有無 / baby / true | 良い | - | - |
| `testHasStalkFalse` | 有無 / stalk / false | ゆっくり本体の状態/行動/イベント回帰 / 有無 / stalk / false | 良い | - | - |
| `testHasStalkTrue` | 有無 / stalk / true | ゆっくり本体の状態/行動/イベント回帰 / 有無 / stalk / true | 良い | - | - |
| `testSetNumOfAnts` | 設定 / num / of / ants | ゆっくり本体の状態/行動/イベント回帰 / 設定 / num / of / ants | 良い | - | - |
| `testSetNumOfAntsNegativeClamped` | 設定 / num / of / ants / negative / clamped | ゆっくり本体の状態/行動/イベント回帰 / 設定 / num / of / ants / negative / clamped | 良い | - | - |
| `testSubstractNumOfAnts` | substract / num / of / ants | ゆっくり本体の状態/行動/イベント回帰 / substract / num / of / ants | 良い | - | - |
| `testSubstractNumOfAntsNegativeClamped` | substract / num / of / ants / negative / clamped | ゆっくり本体の状態/行動/イベント回帰 / substract / num / of / ants / negative / clamped | 良い | - | - |
| `testIsGotBurnedByFootBake` | 状態 / got / burned / by / foot / bake | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / by / foot / bake | 良い | - | - |
| `testIsGotBurnedByBodyBake` | 状態 / got / burned / by / 本体 / bake | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / by / 本体 / bake | 良い | - | - |
| `testIsGotBurnedHeavilyFalse` | 状態 / got / burned / heavily / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / heavily / false | 良い | - | - |
| `testIsGotBurnedHeavilyByFootBakeCritical` | 状態 / got / burned / heavily / by / foot / bake / critical | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / heavily / by / foot / bake / critical | 良い | - | - |
| `testIsGotBurnedHeavilyByBodyBakeCritical` | 状態 / got / burned / heavily / by / 本体 / bake / critical | ゆっくり本体の状態/行動/イベント回帰 / 状態 / got / burned / heavily / by / 本体 / bake / critical | 良い | - | - |
| `testIsDirtyAliveAndDirty` | 状態 / dirty / alive / and / dirty | ゆっくり本体の状態/行動/イベント回帰 / 状態 / dirty / alive / and / dirty | 良い | - | - |
| `testIsDirtyAliveAndStubbornlyDirty` | 状態 / dirty / alive / and / stubbornly / dirty | ゆっくり本体の状態/行動/イベント回帰 / 状態 / dirty / alive / and / stubbornly / dirty | 良い | - | - |
| `testIsDirtyDeadReturnsFalse` | 状態 / dirty / 死亡 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / dirty / 死亡 / 戻り / false | 良い | - | - |
| `testIsDirtyClean` | 状態 / dirty / clean | ゆっくり本体の状態/行動/イベント回帰 / 状態 / dirty / clean | 良い | - | - |
| `testIsNormalDirtyTrue` | 状態 / normal / dirty / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / normal / dirty / true | 良い | - | - |
| `testIsNormalDirtyDeadReturnsFalse` | 状態 / normal / dirty / 死亡 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / normal / dirty / 死亡 / 戻り / false | 良い | - | - |
| `testAddDirtyPeriod` | 追加 / dirty / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / dirty / period | 良い | - | - |
| `testIsShittingAlive` | 状態 / shitting / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / shitting / alive | 良い | - | - |
| `testIsShittingDead` | 状態 / shitting / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / shitting / 死亡 | 良い | - | - |
| `testIsSleepingAlive` | 状態 / sleeping / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sleeping / alive | 良い | - | - |
| `testIsSleepingDead` | 状態 / sleeping / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / sleeping / 死亡 | 良い | - | - |
| `testIsForceExcitingTrue` | 状態 / force / exciting / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / force / exciting / true | 良い | - | - |
| `testIsForceExcitingDeadReturnsFalse` | 状態 / force / exciting / 死亡 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / force / exciting / 死亡 / 戻り / false | 良い | - | - |
| `testIsForceExcitingNotExciting` | 状態 / force / exciting / 非 / exciting | ゆっくり本体の状態/行動/イベント回帰 / 状態 / force / exciting / 非 / exciting | 良い | - | - |
| `testIsForceExcitingNotForced` | 状態 / force / exciting / 非 / forced | ゆっくり本体の状態/行動/イベント回帰 / 状態 / force / exciting / 非 / forced | 良い | - | - |
| `testIsEatenByAnimalsFalse` | 状態 / eaten / by / animals / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / eaten / by / animals / false | 良い | - | - |
| `testRemoveAnts` | 除去 / ants | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testResetAttachmentBoundaryEmpty` | reset / attachment / boundary / empty | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | assert:0 |
| `testResetAttachmentBoundaryWithAttachment` | reset / attachment / boundary / with / attachment | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | assert:0 |
| `testAddChildrenListNull` | 追加 / children / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / children / list / null | 良い | - | - |
| `testAddChildrenListWithBody` | 追加 / children / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / children / list / with / 本体 | 良い | - | - |
| `testRemoveChildrenListNull` | 除去 / children / list / null | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | assert:0 |
| `testRemoveChildrenListWithBody` | 除去 / children / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveChildrenListNotInList` | 除去 / children / list / 非 / in / list | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testAddElderSisterListNull` | 追加 / elder / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / elder / 姉妹 / list / null | 良い | - | - |
| `testAddElderSisterListWithBody` | 追加 / elder / 姉妹 / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / elder / 姉妹 / list / with / 本体 | 良い | - | - |
| `testRemoveElderSisterListNull` | 除去 / elder / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | assert:0 |
| `testRemoveElderSisterListWithBody` | 除去 / elder / 姉妹 / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testAddSisterListNull` | 追加 / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 姉妹 / list / null | 良い | - | - |
| `testAddSisterListWithBody` | 追加 / 姉妹 / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 姉妹 / list / with / 本体 | 良い | - | - |
| `testRemoveSisterListNull` | 除去 / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | assert:0 |
| `testRemoveSisterListWithBody` | 除去 / 姉妹 / list / with / 本体 | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetDiarrheaKaiyuAlwaysTrue` | 取得 / diarrhea / kaiyu / always / true | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / kaiyu / always / true | 良い | - | - |
| `testGetDiarrheaNonKaiyuWithSickDoublesProbability` | 取得 / diarrhea / non / kaiyu / with / 病気 / doubles / probability | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / non / kaiyu / with / 病気 / doubles / probability | 良い | - | assert:0 |
| `testGetDiarrheaNonKaiyuWithDamageDoublesProbability` | 取得 / diarrhea / non / kaiyu / with / ダメージ / doubles / probability | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / non / kaiyu / with / ダメージ / doubles / probability | 良い | - | assert:0 |
| `testGetDiarrheaWithControlledRnd` | 取得 / diarrhea / with / controlled / rnd | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / with / controlled / rnd | 良い | - | - |
| `testWillingFurifuriNotRudeReturnsFalse` | willing / furifuri / 非 / rude / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / 非 / rude / 戻り / false | 良い | - | - |
| `testWillingFurifuriRudeWithHighDisciplineReturnsFalse` | willing / furifuri / rude / with / high / discipline / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / rude / with / high / discipline / 戻り / false | 良い | - | - |
| `testWillingFurifuriRudeWithZeroDiscipline` | willing / furifuri / rude / with / zero / discipline | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / rude / with / zero / discipline | 良い | - | - |
| `testWillingFurifuriCannotFurifuriReturnsFalse` | willing / furifuri / cannot / furifuri / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / cannot / furifuri / 戻り / false | 良い | - | - |
| `testGetTakeoutItemNullMap` | 取得 / takeout / item / null / map | ゆっくり本体の状態/行動/イベント回帰 / 取得 / takeout / item / null / map | 良い | - | - |
| `testGetTakeoutItemKeyNotFound` | 取得 / takeout / item / key / 非 / found | ゆっくり本体の状態/行動/イベント回帰 / 取得 / takeout / item / key / 非 / found | 良い | - | - |
| `testGetTakeoutItemFromTakenOutShit` | 取得 / takeout / item / from / taken / out / shit | ゆっくり本体の状態/行動/イベント回帰 / 取得 / takeout / item / from / taken / out / shit | 良い | - | - |
| `testGetTakeoutItemNotInFoodOrShitMaps` | 取得 / takeout / item / 非 / in / food / or / shit / maps | ゆっくり本体の状態/行動/イベント回帰 / 取得 / takeout / item / 非 / in / food / or / shit / maps | 良い | - | - |
| `testRemoveTakeoutItem` | 除去 / takeout / item | ゆっくり本体の状態/行動/イベント回帰 / 除去フラグ回帰 | 良い | - | - |
| `testIsAngryAlive` | 状態 / angry / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / angry / alive | 良い | - | - |
| `testIsAngryDead` | 状態 / angry / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / angry / 死亡 | 良い | - | - |
| `testIsScareAlive` | 状態 / scare / alive | ゆっくり本体の状態/行動/イベント回帰 / 状態 / scare / alive | 良い | - | - |
| `testIsScareDead` | 状態 / scare / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / scare / 死亡 | 良い | - | - |
| `testIsWet` | 状態 / 水濡れ | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 水濡れ | 良い | - | - |
| `testIsMelt` | 状態 / melt | ゆっくり本体の状態/行動/イベント回帰 / 状態 / melt | 良い | - | - |
| `testIsPealed` | 状態 / pealed | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pealed | 良い | - | - |
| `testIsPacked` | 状態 / packed | ゆっくり本体の状態/行動/イベント回帰 / 状態 / packed | 良い | - | - |
| `testIsBlind` | 状態 / blind | ゆっくり本体の状態/行動/イベント回帰 / 状態 / blind | 良い | - | - |
| `testIsRelax` | 状態 / relax | ゆっくり本体の状態/行動/イベント回帰 / 状態 / relax | 良い | - | - |
| `testIsRapist` | 状態 / rapist | ゆっくり本体の状態/行動/イベント回帰 / 状態 / rapist | 良い | - | - |
| `testIsSuperRapist` | 状態 / super / rapist | ゆっくり本体の状態/行動/イベント回帰 / 状態 / super / rapist | 良い | - | - |
| `testIsHybrid` | 状態 / hybrid | ゆっくり本体の状態/行動/イベント回帰 / 状態 / hybrid | 良い | - | - |
| `testIsNotChangeCharacter` | 状態 / 非 / change / character | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 非 / change / character | 良い | - | - |
| `testIsRealPregnantLimit` | 状態 / real / pregnant / limit | ゆっくり本体の状態/行動/イベント回帰 / 状態 / real / pregnant / limit | 良い | - | - |
| `testIsHasBraid` | 状態 / 有無 / おさげ | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 有無 / おさげ | 良い | - | - |
| `testIsHasPants` | 状態 / 有無 / pants | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 有無 / pants | 良い | - | - |
| `testIsAnalClose` | 状態 / anal / close | ゆっくり本体の状態/行動/イベント回帰 / 状態 / anal / close | 良い | - | - |
| `testIsBodyCastration` | 状態 / 本体 / castration | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 本体 / castration | 良い | - | - |
| `testIsStalkCastration` | 状態 / stalk / castration | ゆっくり本体の状態/行動/イベント回帰 / 状態 / stalk / castration | 良い | - | - |
| `testIsCrushed` | 状態 / crushed | ゆっくり本体の状態/行動/イベント回帰 / 状態 / crushed | 良い | - | - |
| `testIsBurned` | 状態 / burned | ゆっくり本体の状態/行動/イベント回帰 / 状態 / burned | 良い | - | - |
| `testIsNightmare` | 状態 / nightmare | ゆっくり本体の状態/行動/イベント回帰 / 状態 / nightmare | 良い | - | - |
| `testIsFatherRaper` | 状態 / 父 / raper | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 父 / raper | 良い | - | - |
| `testIsRareType` | 状態 / rare / type | ゆっくり本体の状態/行動/イベント回帰 / 状態 / rare / type | 良い | - | - |
| `testIsLikeBitterFood` | 状態 / like / bitter / food | ゆっくり本体の状態/行動/イベント回帰 / 状態 / like / bitter / food | 良い | - | - |
| `testIsLikeHotFood` | 状態 / like / hot / food | ゆっくり本体の状態/行動/イベント回帰 / 状態 / like / hot / food | 良い | - | - |
| `testIsLikeWater` | 状態 / like / 水 | ゆっくり本体の状態/行動/イベント回帰 / 状態 / like / 水 | 良い | - | - |
| `testIsFlyingType` | 状態 / flying / type | ゆっくり本体の状態/行動/イベント回帰 / 状態 / flying / type | 良い | - | - |
| `testIsBraidType` | 状態 / おさげ / type | ゆっくり本体の状態/行動/イベント回帰 / 状態 / おさげ / type | 良い | - | - |
| `testIsLockmove` | 状態 / lockmove | ゆっくり本体の状態/行動/イベント回帰 / 状態 / lockmove | 良い | - | - |
| `testIsPullAndPush` | 状態 / pull / and / push | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pull / and / push | 良い | - | - |
| `testIsFixBack` | 状態 / fix / back | ゆっくり本体の状態/行動/イベント回帰 / 状態 / fix / back | 良い | - | - |
| `testIsTargetBind` | 状態 / target / bind | ゆっくり本体の状態/行動/イベント回帰 / 状態 / target / bind | 良い | - | - |
| `testIsInOutTakeoutItem` | 状態 / in / out / takeout / item | ゆっくり本体の状態/行動/イベント回帰 / 状態 / in / out / takeout / item | 良い | - | - |
| `testIsStaying` | 状態 / staying | ゆっくり本体の状態/行動/イベント回帰 / 状態 / staying | 良い | - | - |
| `testIsSilent` | 状態 / silent | ゆっくり本体の状態/行動/イベント回帰 / 状態 / silent | 良い | - | - |
| `testIsShutmouth` | 状態 / shutmouth | ゆっくり本体の状態/行動/イベント回帰 / 状態 / shutmouth | 良い | - | - |
| `testIsUnBirth` | 状態 / un / birth | ゆっくり本体の状態/行動/イベント回帰 / 状態 / un / birth | 良い | - | - |
| `testIsCanTalk` | 状態 / 可否 / talk | ゆっくり本体の状態/行動/イベント回帰 / 状態 / 可否 / talk | 良い | - | - |
| `testIsForceBirthMessage` | 状態 / force / birth / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 状態 / force / birth / メッセージ | 良い | - | - |
| `testIsPin` | 状態 / pin | ゆっくり本体の状態/行動/イベント回帰 / 状態 / pin | 良い | - | - |
| `testIsDropShadow` | 状態 / drop / shadow | ゆっくり本体の状態/行動/イベント回帰 / 状態 / drop / shadow | 良い | - | - |
| `testIsTaken` | 状態 / taken | ゆっくり本体の状態/行動/イベント回帰 / 状態 / taken | 良い | - | - |
| `testIsbPheromone` | isb / pheromone | ゆっくり本体の状態/行動/イベント回帰 / isb / pheromone | 良い | - | - |
| `testIsbNoDamageNextFall` | isb / なし / ダメージ / next / 落下 | ゆっくり本体の状態/行動/イベント回帰 / isb / なし / ダメージ / next / 落下 | 良い | - | - |
| `testIsbSurisuriFromPlayer` | isb / surisuri / from / player | ゆっくり本体の状態/行動/イベント回帰 / isb / surisuri / from / player | 良い | - | - |
| `testIsbPurupuru` | isb / purupuru | ゆっくり本体の状態/行動/イベント回帰 / isb / purupuru | 良い | - | - |
| `testIsbOnDontMoveBeltconveyor` | isb / on / dont / 移動 / beltconveyor | ゆっくり本体の状態/行動/イベント回帰 / isb / on / dont / 移動 / beltconveyor | 良い | - | - |
| `testIsbNoticeNoOkazari` | isb / notice / なし / okazari | ゆっくり本体の状態/行動/イベント回帰 / isb / notice / なし / okazari | 良い | - | - |
| `testIsbPenipeniCutted` | isb / penipeni / cutted | ゆっくり本体の状態/行動/イベント回帰 / isb / penipeni / cutted | 良い | - | - |
| `testIsbFirstEatStalk` | isb / first / eat / stalk | ゆっくり本体の状態/行動/イベント回帰 / isb / first / eat / stalk | 良い | - | - |
| `testIsbImageNagasiMode` | isb / image / nagasi / mode | ゆっくり本体の状態/行動/イベント回帰 / isb / image / nagasi / mode | 良い | - | - |
| `testAddStressDeadNoEffect` | 追加 / ストレス / 死亡 / なし / effect | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / 死亡 / なし / effect | 良い | - | - |
| `testAddStressPositiveAddsShit` | 追加 / ストレス / positive / adds / shit | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / positive / adds / shit | 良い | - | - |
| `testAddStressNegativeNoShit` | 追加 / ストレス / negative / なし / shit | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / negative / なし / shit | 良い | - | - |
| `testAddStressClampToZero` | 追加 / ストレス / 範囲補正 / to / zero | ゆっくり本体の状態/行動/イベント回帰 / 追加 / ストレス / 範囲補正 / to / zero | 良い | - | - |
| `testVerySadSadPeriodWithRndZero` | very / sad / sad / period / with / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / very / sad / sad / period / with / rnd / zero | 良い | - | - |
| `testVerySadSadPeriodWithRndMax` | very / sad / sad / period / with / rnd / max | ゆっくり本体の状態/行動/イベント回帰 / very / sad / sad / period / with / rnd / max | 良い | - | - |
| `testReturnsFalseWhenNotRealPregnantLimit` | 戻り / false / when / 非 / real / pregnant / limit | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsTrueWhenLimitZeroAndRndNonZero` | 戻り / true / when / limit / zero / and / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsFalseWhenLimitZeroAndRndZero` | 戻り / false / when / limit / zero / and / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsFalseWhenLimitHigh` | 戻り / false / when / limit / high | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsTrueWhenLimitHighAndRndZero` | 戻り / true / when / limit / high / and / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsTrueWhenLimitModerateAndRndZero` | 戻り / true / when / limit / moderate / and / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testReturnsFalseWhenNotRealPregnantLimitZero` | 戻り / false / when / 非 / real / pregnant / limit / zero | ゆっくり本体の状態/行動/イベント回帰 / 復活/再生回帰 | 良い | - | - |
| `testDiarrheaReturnsTrueWhenKaiyu` | diarrhea / 戻り / true / when / kaiyu | ゆっくり本体の状態/行動/イベント回帰 / diarrhea / 戻り / true / when / kaiyu | 良い | - | - |
| `testDiarrheaReturnsTrueWhenRndHits` | diarrhea / 戻り / true / when / rnd / hits | ゆっくり本体の状態/行動/イベント回帰 / diarrhea / 戻り / true / when / rnd / hits | 良い | - | - |
| `testDiarrheaReturnsFalseWhenRndMisses` | diarrhea / 戻り / false / when / rnd / misses | ゆっくり本体の状態/行動/イベント回帰 / diarrhea / 戻り / false / when / rnd / misses | 良い | - | - |
| `testDiarrheaSickDoublesChance` | diarrhea / 病気 / doubles / chance | ゆっくり本体の状態/行動/イベント回帰 / diarrhea / 病気 / doubles / chance | 良い | - | - |
| `testDiarrheaDamagedDoublesChance` | diarrhea / damaged / doubles / chance | ゆっくり本体の状態/行動/イベント回帰 / diarrhea / damaged / doubles / chance | 良い | - | - |
| `testWillingFurifuriRudeZeroDisciplineRndHits` | willing / furifuri / rude / zero / discipline / rnd / hits | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / rude / zero / discipline / rnd / hits | 良い | - | - |
| `testWillingFurifuriRudeModerateDisciplineRndHits` | willing / furifuri / rude / moderate / discipline / rnd / hits | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / rude / moderate / discipline / rnd / hits | 良い | - | - |
| `testWillingFurifuriRudeModerateDisciplineRndMisses` | willing / furifuri / rude / moderate / discipline / rnd / misses | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / rude / moderate / discipline / rnd / misses | 良い | - | - |
| `testSetHappinessNydsetsSadPeriod` | 設定 / 幸福 / nydsets / sad / period | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / nydsets / sad / period | 良い | - | - |
| `testSetHappinessNydminSadPeriod` | 設定 / 幸福 / nydmin / sad / period | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / nydmin / sad / period | 良い | - | - |
| `testSetHappinessNydmaxSadPeriod` | 設定 / 幸福 / nydmax / sad / period | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 幸福 / nydmax / sad / period | 良い | - | - |

### `BodyBehaviorTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 境界値とクランプが壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBegForLifeVeryNice` | beg / for / life / very / nice | ゆっくり本体の状態/行動/イベント回帰 / beg / for / life / very / nice | 良い | - | - |
| `testBegForLifeSuperShithead` | beg / for / life / super / shithead | ゆっくり本体の状態/行動/イベント回帰 / beg / for / life / super / shithead | 良い | - | - |
| `testCheckSickTerminal` | 判定 / 病気 / terminal | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckPanicPropagation` | 判定 / 恐慌 / propagation | ゆっくり本体の状態/行動/イベント回帰 / 判定 / 恐慌 / propagation | 良い | - | - |
| `testCheckFirePropagation` | 判定 / 火 / propagation | ゆっくり本体の状態/行動/イベント回帰 / 判定 / 火 / propagation | 良い | - | reflection |
| `testScenarioForcedBegForLifeStartsEventEvenWithoutRandomHit` | シナリオ / forced / beg / for / life / starts / イベント / even / without / random / hit | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / forced / beg / for / life / starts / イベント / even / without / random / hit | 良い | - | - |
| `testScenarioPanicDoesNotPropagateToRaperBody` | シナリオ / 恐慌 / does / 非 / propagate / to / raper / 本体 | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / 恐慌 / does / 非 / propagate / to / raper / 本体 | 良い | - | - |
| `testScenarioFireDoesNotPropagateToDistantBody` | シナリオ / 火 / does / 非 / propagate / to / distant / 本体 | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / 火 / does / 非 / propagate / to / distant / 本体 | 良い | - | reflection |
| `testScenarioAverageBodyDoesNotBegForLifeWithoutStressEvenWhenDamaged` | シナリオ / average / 本体 / does / 非 / beg / for / life / without / ストレス / even / when / damaged | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / average / 本体 / does / 非 / beg / for / life / without / ストレス / even / when / damaged | 良い | - | - |
| `testScenarioFoolShitheadDoesNotBegForLifeWithoutStressTrigger` | シナリオ / fool / shithead / does / 非 / beg / for / life / without / ストレス / trigger | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / fool / shithead / does / 非 / beg / for / life / without / ストレス / trigger | 良い | - | - |

### `BodyLogicTest`
- 状態: 完了 (455/455 良い)
- クラス要約: `移動/代謝/ダメージ回帰`
- 回帰目的:
  - 捕食者による食べ行動と死体処理が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testEatBody_DeadBodyCrush` | eat / 本体 / 死亡 / 本体 / crush | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testEatBody_DeadBodyRemove` | eat / 本体 / 死亡 / 本体 / 除去 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testEatBody_LiveBodyDamage` | eat / 本体 / live / 本体 / ダメージ | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testEatBody_LiveBodyDeath` | eat / 本体 / live / 本体 / death | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testPruneRemovedFamilyMembers` | prune / removed / 家族 / members | 家族関係/家族行動回帰 | 良い | - | reflection |

### `BodyMetabolismTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckHungryComplex` | 判定 / 空腹 / complex | ゆっくり本体の状態/行動/イベント回帰 / 判定 / 空腹 / complex | 良い | - | - |
| `testCheckSickAdvanced` | 判定 / 病気 / advanced | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckDamageAdvanced` | 判定 / ダメージ / advanced | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / advanced | 良い | - | - |
| `testScenarioUnbirthBodyWithStalkAndBabyStillLosesLargeHungryTick` | シナリオ / unbirth / 本体 / with / stalk / and / baby / still / loses / large / 空腹 / tick | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / unbirth / 本体 / with / stalk / and / baby / still / loses / large / 空腹 / tick | 良い | - | - |
| `testScenarioPoisonSteamTurnsNaturalHealingIntoNetDamageGain` | シナリオ / poison / steam / turns / natural / healing / into / net / ダメージ / gain | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / poison / steam / turns / natural / healing / into / net / ダメージ / gain | 良い | - | - |
| `testScenarioHumidWetDamagedBodyGetsDirtyThenTerminalSickAddsStress` | シナリオ / humid / 水濡れ / damaged / 本体 / gets / dirty / then / terminal / 病気 / adds / ストレス | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / humid / 水濡れ / damaged / 本体 / gets / dirty / then / terminal / 病気 / adds / ストレス | 良い | - | - |

### `BodyRenderingTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetFaceImageDeadAndPealed` | 取得 / 表情 / image / 死亡 / and / pealed | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / 死亡 / and / pealed | 良い | - | - |
| `testGetFaceImageDeadNotPealed` | 取得 / 表情 / image / 死亡 / 非 / pealed | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / 死亡 / 非 / pealed | 良い | - | - |
| `testGetFaceImageNyd` | 取得 / 表情 / image / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / 非ゆっくり症 | 良い | - | - |
| `testGetFaceImageExciting` | 取得 / 表情 / image / exciting | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / exciting | 良い | - | - |
| `testGetFaceImagePain` | 取得 / 表情 / image / pain | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / pain | 良い | - | - |
| `testGetBodyBaseImageCrushed` | 取得 / 本体 / base / image / crushed | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / base / image / crushed | 良い | - | - |
| `testGetBodyBaseImageShitting` | 取得 / 本体 / base / image / shitting | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / base / image / shitting | 良い | - | - |
| `testGetEffectImageHungryAndWet` | 取得 / effect / image / 空腹 / and / 水濡れ | ゆっくり本体の状態/行動/イベント回帰 / 取得 / effect / image / 空腹 / and / 水濡れ | 良い | - | - |
| `testGetEffectImageSickProgression` | 取得 / effect / image / 病気 / progression | ゆっくり本体の状態/行動/イベント回帰 / 取得 / effect / image / 病気 / progression | 良い | - | - |
| `testGetAbnormalBodyImageMelt` | 取得 / abnormal / 本体 / image / melt | ゆっくり本体の状態/行動/イベント回帰 / 取得 / abnormal / 本体 / image / melt | 良い | - | - |
| `testGetFaceImageBlinkingUnyo` | 取得 / 表情 / image / blinking / unyo | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 表情 / image / blinking / unyo | 良い | - | - |

### `BodyTest`
- 状態: 完了 (1194/1194 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testIsParentTrue` | 状態 / 親 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsParentFalse` | 状態 / 親 / false | 家族関係判定回帰 | 良い | - | - |
| `testIsParentNullSafe` | 状態 / 親 / null / safe | 家族関係判定回帰 | 良い | - | - |
| `testIsFatherTrue` | 状態 / 父 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsMotherTrue` | 状態 / 母 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsFatherNullSafe` | 状態 / 父 / null / safe | 家族関係判定回帰 | 良い | - | - |
| `testIsMotherNullSafe` | 状態 / 母 / null / safe | 家族関係判定回帰 | 良い | - | - |
| `testIsChildTrue` | 状態 / 子 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsChildFalse` | 状態 / 子 / false | 家族関係判定回帰 | 良い | - | - |
| `testIsChildNullSafe` | 状態 / 子 / null / safe | 家族関係判定回帰 | 良い | - | - |
| `testIsPartnerTrue` | 状態 / 相手 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsPartnerFalse` | 状態 / 相手 / false | 家族関係判定回帰 | 良い | - | - |
| `testIsPartnerNullSafe` | 状態 / 相手 / null / safe | 家族関係判定回帰 | 良い | - | - |
| `testIsSisterTrueSameMother` | 状態 / 姉妹 / true / same / 母 | 家族関係判定回帰 | 良い | - | - |
| `testIsSisterTrueSameFather` | 状態 / 姉妹 / true / same / 父 | 家族関係判定回帰 | 良い | - | - |
| `testIsSisterFalseNoSharedParent` | 状態 / 姉妹 / false / なし / shared / 親 | 家族関係判定回帰 | 良い | - | - |
| `testIsElderSisterTrue` | 状態 / elder / 姉妹 / true | 家族関係判定回帰 | 良い | - | - |
| `testIsFamilyAsParent` | 状態 / 家族 / as / 親 | 家族関係判定回帰 | 良い | - | - |
| `testIsFamilyAsPartner` | 状態 / 家族 / as / 相手 | 家族関係判定回帰 | 良い | - | - |
| `testIsFamilyAsSister` | 状態 / 家族 / as / 姉妹 | 家族関係判定回帰 | 良い | - | - |
| `testIsFamilyFalseUnrelated` | 状態 / 家族 / false / unrelated | 家族関係判定回帰 | 良い | - | - |
| `testAddDamageAlive` | 追加 / ダメージ / alive | 生存/行動可否回帰 | 良い | - | - |
| `testAddDamageIgnoredWhenDead` | 追加 / ダメージ / ignored / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testAddDamageNegativeHeals` | 追加 / ダメージ / negative / heals | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeIncreasesDamage` | 打撃 / increases / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeAddsStress` | 打撃 / adds / ストレス | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeSetsFlags` | 打撃 / sets / flags | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeWakesUp` | 打撃 / wakes / up | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeIgnoredWhenDead` | 打撃 / ignored / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeFurifuriWhenFixBack` | 打撃 / furifuri / when / fix / back | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeNoFurifuriWhenNeedled` | 打撃 / なし / furifuri / when / needled | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionTrueByDefault` | 可否 / action / true / by / default | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenDead` | 可否 / action / false / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenSleeping` | 可否 / action / false / when / sleeping | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenNeedled` | 可否 / action / false / when / needled | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenPealed` | 可否 / action / false / when / pealed | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenCut` | 可否 / action / false / when / cut | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenNyd` | 可否 / action / false / when / 非ゆっくり症 | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenBuried` | 可否 / action / false / when / buried | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionFalseWhenPacked` | 可否 / action / false / when / packed | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveDefaultFalse` | 状態 / dont / 移動 / default / false | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenDead` | 状態 / dont / 移動 / true / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenSleeping` | 状態 / dont / 移動 / true / when / sleeping | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenNeedled` | 状態 / dont / 移動 / true / when / needled | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenCriticalFootBake` | 状態 / dont / 移動 / true / when / critical / foot / bake | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenLockmove` | 状態 / dont / 移動 / true / when / lockmove | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenGrabbed` | 状態 / dont / 移動 / true / when / grabbed | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenNyd` | 状態 / dont / 移動 / true / when / 非ゆっくり症 | 生存/行動可否回帰 | 良い | - | - |
| `testIsDontMoveTrueWhenBlind` | 状態 / dont / 移動 / true / when / blind | 生存/行動可否回帰 | 良い | - | - |
| `testIsNotAllrightDefaultFalse` | 状態 / 非 / allright / default / false | 生存/行動可否回帰 | 良い | - | - |
| `testIsNotAllrightTrueWhenDead` | 状態 / 非 / allright / true / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testIsNotAllrightTrueWhenMelt` | 状態 / 非 / allright / true / when / melt | 生存/行動可否回帰 | 良い | - | - |
| `testCanflyCheckFalseByDefault` | canfly / 判定 / false / by / default | 生存/行動可否回帰 | 良い | - | - |
| `testCanflyCheckFalseWhenDead` | canfly / 判定 / false / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testCanEventResponseTrueByDefault` | 可否 / イベント / response / true / by / default | 生存/行動可否回帰 | 良い | - | - |
| `testCanEventResponseFalseWhenDead` | 可否 / イベント / response / false / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testCanEventResponseFalseWhenLockmove` | 可否 / イベント / response / false / when / lockmove | 生存/行動可否回帰 | 良い | - | - |
| `testHasDisorderFalseCleanState` | 有無 / disorder / false / clean / state | 生存/行動可否回帰 | 良い | - | - |
| `testHasDisorderTrueWhenNyd` | 有無 / disorder / true / when / 非ゆっくり症 | 生存/行動可否回帰 | 良い | - | - |
| `testHasDisorderTrueWhenBlind` | 有無 / disorder / true / when / blind | 生存/行動可否回帰 | 良い | - | - |
| `testHasDisorderTrueWhenCut` | 有無 / disorder / true / when / cut | 生存/行動可否回帰 | 良い | - | - |
| `testHasDisorderTrueWhenNoOkazari` | 有無 / disorder / true / when / なし / okazari | 生存/行動可否回帰 | 良い | - | - |
| `testHasBraidCheckDelegates` | 有無 / おさげ / 判定 / delegates | 生存/行動可否回帰 | 良い | - | - |
| `testCheckHungryNormalDecrease` | 判定 / 空腹 / normal / decrease | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryPealedExtraOnAge7` | 判定 / 空腹 / pealed / extra / on / age7 | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryPealedNormalRateOddAge` | 判定 / 空腹 / pealed / normal / rate / odd / age | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryUnbirthFastDecrease` | 判定 / 空腹 / unbirth / fast / decrease | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungrySleepingHalfRate` | 判定 / 空腹 / sleeping / half / rate | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungrySleepingNoDecreaseOddAge` | 判定 / 空腹 / sleeping / なし / decrease / odd / age | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryZeroCausesDamage` | 判定 / 空腹 / zero / causes / ダメージ | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryNoHungryPeriodIncreases` | 判定 / 空腹 / なし / 空腹 / period / increases | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryNoHungryPeriodResetsWhenHungry` | 判定 / 空腹 / なし / 空腹 / period / resets / when / 空腹 | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryWithStalkExtraDrain` | 判定 / 空腹 / with / stalk / extra / drain | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryNoHungryBySupereatingTime` | 判定 / 空腹 / なし / 空腹 / by / supereating / time | 代謝/状態フラグ回帰 | 良い | - | - |
| `testScenarioHungryProgressIncreasesDamageAndClampsHungry` | シナリオ / 空腹 / progress / increases / ダメージ / and / clamps / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / 空腹進行でダメージ増加と clamp を守る回帰 | 良い | - | - |
| `testScenarioStressIncreasesShitWhenAlive` | シナリオ / ストレス / increases / shit / when / alive | ゆっくり本体の状態/行動/イベント回帰 / ストレス増加が排泄に反映される回帰 | 良い | - | - |
| `testScenarioWetNotLikeWaterCausesMeltAndDamage` | シナリオ / 水濡れ / 非 / like / 水 / causes / melt / and / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 水濡れ/溶解/損傷回帰 | 良い | - | - |
| `testScenarioSickProgressAddsExtraDamageWithRnd` | シナリオ / 病気 / progress / adds / extra / ダメージ / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / 病気進行と追加ダメージ回帰 | 良い | - | - |
| `testScenarioBurstStateChangesByExpandSize` | シナリオ / 破裂 / state / changes / by / expand / size | ゆっくり本体の状態/行動/イベント回帰 / 肥大/破裂状態遷移回帰 | 良い | - | - |
| `testScenarioHappinessClearsAngryAndScare` | シナリオ / 幸福 / clears / angry / and / scare | ゆっくり本体の状態/行動/イベント回帰 / 幸福/怒り/恐怖の感情遷移回帰 | 良い | - | - |
| `testScenarioNydForcesVerySadAndHate` | シナリオ / 非ゆっくり症 / forces / very / sad / and / hate | ゆっくり本体の状態/行動/イベント回帰 / 非ゆっくり症の強制遷移回帰 | 良い | - | - |
| `testScenarioDeadAndBaryStateGuardsActions` | シナリオ / 死亡 / and / bary / state / guards / actions | ゆっくり本体の状態/行動/イベント回帰 / 死亡時ガード回帰 | 良い | - | - |
| `testScenarioParentChildConsistency` | シナリオ / 親 / 子 / consistency | ゆっくり本体の状態/行動/イベント回帰 / 親子整合性回帰 | 良い | - | - |
| `testScenarioRndBranchChangesForceFaceWhenRude` | シナリオ / rnd / branch / changes / force / 表情 / when / rude | ゆっくり本体の状態/行動/イベント回帰 / 乱数分岐の表情/反応回帰 | 良い | - | - |
| `testTickHungryDamageAccumulatesOverTime` | tick / 空腹 / ダメージ / accumulates / over / time | 代謝/状態フラグ回帰 | 良い | - | - |
| `testTickStressIncreasesShitOverTime` | tick / ストレス / increases / shit / over / time | ゆっくり本体の状態/行動/イベント回帰 / tick 単位のストレス/排泄進行回帰 | 良い | - | - |
| `testTickWetAddsStressOnInterval` | tick / 水濡れ / adds / ストレス / on / interval | ゆっくり本体の状態/行動/イベント回帰 / tick 単位の水濡れ進行回帰 | 良い | - | - |
| `testTickSleepyAfterActivePeriod` | tick / sleepy / after / active / period | ゆっくり本体の状態/行動/イベント回帰 / tick 単位の睡眠状態進行回帰 | 良い | - | - |
| `testWantToShitFalseWhenFarFromLimit` | want / to / shit / false / when / far / from / limit | 代謝/状態フラグ回帰 | 良い | - | - |
| `testWantToShitTrueWhenCloseToLimit` | want / to / shit / true / when / close / to / limit | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckStressClampsNegativeToZero` | 判定 / ストレス / clamps / negative / to / zero | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckStressPositiveUnchanged` | 判定 / ストレス / positive / unchanged | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetDryAndNotMeltNoOp` | 判定 / 水濡れ / dry / and / 非 / melt / なし / op | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetIncreasesWetPeriod` | 判定 / 水濡れ / increases / 水濡れ / period | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetResetsAfter300` | 判定 / 水濡れ / resets / after300 | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetNotLikeWaterCausesDamage` | 判定 / 水濡れ / 非 / like / 水 / causes / ダメージ | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetNotLikeWaterDamagedCausesMelt` | 判定 / 水濡れ / 非 / like / 水 / damaged / causes / melt | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetNotLikeWaterPealedCausesMelt` | 判定 / 水濡れ / 非 / like / 水 / pealed / causes / melt | 代謝/状態フラグ回帰 | 良い | - | - |
| `testWakeupResetsState` | wakeup / resets / state | 代謝/状態フラグ回帰 | 良い | - | - |
| `testStaySetsStaying` | stay / sets / staying | 代謝/状態フラグ回帰 | 良い | - | - |
| `testClearActionsResetsFlags` | 解除 / actions / resets / flags | 代謝/状態フラグ回帰 | 良い | - | - |
| `testClearActionsResetsMoveTarget` | 解除 / actions / resets / 移動 / target | 代謝/状態フラグ回帰 | 良い | - | - |
| `testClearActionsResetsForceFace` | 解除 / actions / resets / force / 表情 | 代謝/状態フラグ回帰 | 良い | - | - |
| `testGrabSetsFlag` | grab / sets / flag | 代謝/状態フラグ回帰 | 良い | - | - |
| `testPlusAttitudeIncreases` | plus / attitude / increases | 代謝/状態フラグ回帰 | 良い | - | - |
| `testPlusAttitudeDecreases` | plus / attitude / decreases | 代謝/状態フラグ回帰 | 良い | - | - |
| `testPlusAttitudeIgnoredWhenLocked` | plus / attitude / ignored / when / locked | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckLovePlayerStateGood` | 判定 / love / player / state / good | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckLovePlayerStateBad` | 判定 / love / player / state / bad | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckLovePlayerStateNone` | 判定 / love / player / state / none | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckLovePlayerStateBorderNone` | 判定 / love / player / state / border / none | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetDnaReturnsCorrectType` | 取得 / dna / 戻り / correct / type | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetDnaReturnsCorrectAttitude` | 取得 / dna / 戻り / correct / attitude | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetDnaReturnsCorrectIntelligence` | 取得 / dna / 戻り / correct / intelligence | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetDnaSetsFather` | 取得 / dna / sets / 父 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetStrengthAdult` | 取得 / strength / adult | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetStrengthBaby` | 取得 / strength / baby | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testGetStrengthChild` | 取得 / strength / 子 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRevivalResurrectsDead` | revival / resurrects / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRevivalDoesNothingWhenAlive` | revival / does / nothing / when / alive | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testSetAngryWhenNoDamage` | 設定 / angry / when / なし / ダメージ | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testSetAngryIgnoredWhenDead` | 設定 / angry / ignored / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | assert:0 |
| `testSetAngryIgnoredWhenNyd` | 設定 / angry / ignored / when / 非ゆっくり症 | 感情/復活/所持/拘束回帰 | 良い | - | assert:0 |
| `testSetAngryIgnoredWhenSleeping` | 設定 / angry / ignored / when / sleeping | 感情/復活/所持/拘束回帰 | 良い | - | assert:0 |
| `testSetAngryClearsExciting` | 設定 / angry / clears / exciting | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testSetAngryResetsRelaxNobinobi` | 設定 / angry / resets / relax / nobinobi | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCutPenipeniRestore` | cut / penipeni / restore | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckDisciplineRudeFoolSetsZero` | 判定 / discipline / rude / fool / sets / zero | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckDisciplineDecaysAtPeriod` | 判定 / discipline / decays / at / period | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckDisciplineNoDecayOffPeriod` | 判定 / discipline / なし / decay / off / period | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckDisciplineClampsToZero` | 判定 / discipline / clamps / to / zero | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckDisciplineClampsToTwenty` | 判定 / discipline / clamps / to / twenty | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testDiscliplineWhenExciting` | disclipline / when / exciting | ゆっくり本体の状態/行動/イベント回帰 / しつけ値/態度値の回帰 | 良い | - | - |
| `testDiscliplineWhenShitting` | disclipline / when / shitting | ゆっくり本体の状態/行動/イベント回帰 / しつけ値/態度値の回帰 | 良い | - | - |
| `testDiscliplineWhenFurifuri` | disclipline / when / furifuri | ゆっくり本体の状態/行動/イベント回帰 / しつけ値/態度値の回帰 | 良い | - | - |
| `testMakeDirtyTrueSetsFlag` | make / dirty / true / sets / flag | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testMakeDirtyTrueSetsSadHappiness` | make / dirty / true / sets / sad / 幸福 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testMakeDirtyTrueAddsStress` | make / dirty / true / adds / ストレス | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testMakeDirtyFalseWhenNotStubbornly` | make / dirty / false / when / 非 / stubbornly | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testMakeDirtyFalseWhenStubbornlyStillDirty` | make / dirty / false / when / stubbornly / still / dirty | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testMakeDirtyDeadSkipsEffects` | make / dirty / 死亡 / skips / effects | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldIgnoredWhenDead` | 拘束 / ignored / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldSetsLockmoveAndPullPush` | 拘束 / sets / lockmove / and / pull / push | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldToggleOff` | 拘束 / 切替 / off | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveSetsRemovedFlag` | 除去 / sets / removed / flag | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveRemovesFromWorld` | 除去 / removes / from / world | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveClearsPartner` | 除去 / clears / 相手 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveClearsParents` | 除去 / clears / parents | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveClearsChildrenList` | 除去 / clears / children / list | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testClearRelationRemovesDeadParent` | 解除 / relation / removes / 死亡 / 親 | ゆっくり本体の状態/行動/イベント回帰 / 家族/関係リストのクリーンアップ回帰 | 良い | - | - |
| `testClearRelationKeepsLivingParent` | 解除 / relation / 維持 / living / 親 | ゆっくり本体の状態/行動/イベント回帰 / 家族/関係リストのクリーンアップ回帰 | 良い | - | - |
| `testClearRelationRemovesRemovedPartner` | 解除 / relation / removes / removed / 相手 | ゆっくり本体の状態/行動/イベント回帰 / 家族/関係リストのクリーンアップ回帰 | 良い | - | - |
| `testClearRelationKeepsLivingPartner` | 解除 / relation / 維持 / living / 相手 | ゆっくり本体の状態/行動/イベント回帰 / 家族/関係リストのクリーンアップ回帰 | 良い | - | - |
| `testPealToggleOff` | 剥皮 / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 剥皮/外形改変回帰 | 良い | - | - |
| `testPealIgnoredWhenDead` | 剥皮 / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 剥皮/外形改変回帰 | 良い | - | - |
| `testPackToggleOff` | 梱包 / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 梱包/拘束回帰 | 良い | - | - |
| `testPackIgnoredWhenDead` | 梱包 / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 梱包/拘束回帰 | 良い | - | - |
| `testGiveJuiceHeals` | give / juice / heals | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testGiveJuiceClearsInjury` | give / juice / clears / injury | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testGiveJuiceClearsBodyBake` | give / juice / clears / 本体 / bake | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testGiveJuiceClearsAnger` | give / juice / clears / anger | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testGiveJuiceIgnoredWhenDead` | give / juice / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testGiveJuiceAddsLovePlayer` | give / juice / adds / love / player | ゆっくり本体の状態/行動/イベント回帰 / ジュース付与による回復・感情変化回帰 | 良い | - | - |
| `testBabyCanAction` | baby / 可否 / action | ゆっくり本体の状態/行動/イベント回帰 / 赤ゆの行動可否回帰 | 良い | - | - |
| `testChildCheckHungry` | 子 / 判定 / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / 子ゆっくりの空腹代謝回帰 | 良い | - | - |
| `testBabyGetStrength` | baby / 取得 / strength | ゆっくり本体の状態/行動/イベント回帰 / 赤ゆの強さ算出回帰 | 良い | - | - |
| `testCanTransformDeadReturnsFalse` | 可否 / transform / 死亡 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformWithStressReturnsFalse` | 可否 / transform / with / ストレス / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformPoorTangReturnsFalse` | 可否 / transform / poor / tang / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformDamagedReturnsFalse` | 可否 / transform / damaged / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformFeelPainReturnsFalse` | 可否 / transform / feel / pain / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformUnBirthReturnsFalse` | 可否 / transform / un / birth / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformUnunSlaveReturnsFalse` | 可否 / transform / unun / slave / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformNydreturnsFalse` | 可否 / transform / nydreturns / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformBlindReturnsFalse` | 可否 / transform / blind / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformPealedReturnsFalse` | 可否 / transform / pealed / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformPackedReturnsFalse` | 可否 / transform / packed / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformShutmouthReturnsFalse` | 可否 / transform / shutmouth / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformBaldheadReturnsFalse` | 可否 / transform / baldhead / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testCanTransformAllConditionsMetReturnsTrue` | 可否 / transform / all / conditions / met / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / 変身条件回帰 | 良い | - | - |
| `testWakeupResetsSleepState` | wakeup / resets / 睡眠 / state | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckCantDieDecreasesPeriod` | 判定 / cant / die / decreases / period | ゆっくり本体の状態/行動/イベント回帰 / 不死/死亡不可時間回帰 | 良い | - | - |
| `testCheckCantDieStaysAtZero` | 判定 / cant / die / stays / at / zero | ゆっくり本体の状態/行動/イベント回帰 / 不死/死亡不可時間回帰 | 良い | - | - |
| `testCheckHungrySupereatingTime` | 判定 / 空腹 / supereating / time | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryPealed` | 判定 / 空腹 / pealed | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungrySleeping` | 判定 / 空腹 / sleeping | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryNormal` | 判定 / 空腹 / normal | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryWithStalk` | 判定 / 空腹 / with / stalk | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryWithBaby` | 判定 / 空腹 / with / baby | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryBelowZeroCausesDamage` | 判定 / 空腹 / below / zero / causes / ダメージ | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckHungryNoHungryPeriodIncrements` | 判定 / 空腹 / なし / 空腹 / period / increments | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckAntsCrushedRemovesAnts` | 判定 / ants / crushed / removes / ants | ゆっくり本体の状態/行動/イベント回帰 / アリ付着/除去回帰 | 良い | - | - |
| `testCheckAntsIndoorsDoesNothing` | 判定 / ants / indoors / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / アリ付着/除去回帰 | 良い | - | - |
| `testDoSurisuriByPlayerNotSurisuri` | do / surisuri / by / player / 非 / surisuri | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / by / player / 非 / surisuri | 良い | - | - |
| `testCheckSleepNotSleepy` | 判定 / 睡眠 / 非 / sleepy | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepAlreadySleeping` | 判定 / 睡眠 / already / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testSubtractPregnantLimitDecreases` | subtract / pregnant / limit / decreases | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限の減算回帰 | 良い | - | - |
| `testSubtractPregnantLimitAtZeroStaysZero` | subtract / pregnant / limit / at / zero / stays / zero | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限の減算回帰 | 良い | - | - |
| `testSubtractPregnantLimitNegativeStaysZero` | subtract / pregnant / limit / negative / stays / zero | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限の減算回帰 | 良い | - | - |
| `testIsOverPregnantLimitNotRealAndPositive` | 状態 / over / pregnant / limit / 非 / real / and / positive | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限超過判定回帰 | 良い | - | - |
| `testIsOverPregnantLimitNotRealAndZero` | 状態 / over / pregnant / limit / 非 / real / and / zero | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限超過判定回帰 | 良い | - | - |
| `testIsOverPregnantLimitNotRealAndNegative` | 状態 / over / pregnant / limit / 非 / real / and / negative | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限超過判定回帰 | 良い | - | - |
| `testIsOverPregnantLimitRealHighLimit` | 状態 / over / pregnant / limit / real / high / limit | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限超過判定回帰 | 良い | - | - |
| `testIsOverPregnantLimitRealZeroLimit` | 状態 / over / pregnant / limit / real / zero / limit | ゆっくり本体の状態/行動/イベント回帰 / 妊娠上限超過判定回帰 | 良い | - | - |
| `testNearToBirthNoPregnancy` | near / to / birth / なし / pregnancy | ゆっくり本体の状態/行動/イベント回帰 / 出産接近判定回帰 | 良い | - | - |
| `testNearToBirthWithStalkFarFromBirth` | near / to / birth / with / stalk / far / from / birth | ゆっくり本体の状態/行動/イベント回帰 / 出産接近判定回帰 | 良い | - | - |
| `testNearToBirthWithBabyNearLimit` | near / to / birth / with / baby / near / limit | ゆっくり本体の状態/行動/イベント回帰 / 出産接近判定回帰 | 良い | - | - |
| `testForceToSleepWhenDead` | force / to / 睡眠 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 強制睡眠回帰 | 良い | - | - |
| `testForceToSleepWhenAlive` | force / to / 睡眠 / when / alive | ゆっくり本体の状態/行動/イベント回帰 / 強制睡眠回帰 | 良い | - | - |
| `testForceToRaperExciteWhenDead` | force / to / raper / excite / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 強制興奮/性行為回帰 | 良い | - | - |
| `testForceToRaperExciteWhenAlreadyExciting` | force / to / raper / excite / when / already / exciting | ゆっくり本体の状態/行動/イベント回帰 / 強制興奮/性行為回帰 | 良い | - | - |
| `testForceToRaperExciteWhenPenipeniCut` | force / to / raper / excite / when / penipeni / cut | ゆっくり本体の状態/行動/イベント回帰 / 強制興奮/性行為回帰 | 良い | - | - |
| `testForceToRaperExciteSuccess` | force / to / raper / excite / success | ゆっくり本体の状態/行動/イベント回帰 / 強制興奮/性行為回帰 | 良い | - | - |
| `testForceToExciteWhenDead` | force / to / excite / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 興奮付与回帰 | 良い | - | - |
| `testForceToExciteSuccess` | force / to / excite / success | ゆっくり本体の状態/行動/イベント回帰 / 興奮付与回帰 | 良い | - | - |
| `testCheckEmotionBlindWhenNotBlind` | 判定 / emotion / blind / when / 非 / blind | ゆっくり本体の状態/行動/イベント回帰 / 盲目時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionBlindWhenBlind` | 判定 / emotion / blind / when / blind | ゆっくり本体の状態/行動/イベント回帰 / 盲目時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionCantSpeakWhenNotShutmouth` | 判定 / emotion / cant / speak / when / 非 / shutmouth | ゆっくり本体の状態/行動/イベント回帰 / 口封じ時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionCantSpeakWhenShutmouth` | 判定 / emotion / cant / speak / when / shutmouth | ゆっくり本体の状態/行動/イベント回帰 / 口封じ時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionLockmoveWhenCanMove` | 判定 / emotion / lockmove / when / 可否 / 移動 | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionLockmoveWhenSukkiri` | 判定 / emotion / lockmove / when / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionLockmoveWhenSleeping` | 判定 / emotion / lockmove / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionLockmoveWhenGrabbed` | 判定 / emotion / lockmove / when / grabbed | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testStrikeByPunishWhenDead` | 打撃 / by / punish / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByPunishWhenAlive` | 打撃 / by / punish / when / alive | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByHammerWhenDead` | 打撃 / by / hammer / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByHammerWhenAlive` | 打撃 / by / hammer / when / alive | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByPressWhenAlive` | 打撃 / by / press / when / alive | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByPunchWhenDead` | 打撃 / by / punch / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByPunchWhenAlive` | 打撃 / by / punch / when / alive | 生存/行動可否回帰 | 良い | - | - |
| `testBodyBurstWhenCrushed` | 本体 / 破裂 / when / crushed | ゆっくり本体の状態/行動/イベント回帰 / 破裂状態への遷移回帰 | 良い | - | - |
| `testBodyBurstNotCrushedStrikeCalled` | 本体 / 破裂 / 非 / crushed / 打撃 / called | ゆっくり本体の状態/行動/イベント回帰 / 破裂状態への遷移回帰 | 良い | - | - |
| `testCheckShitWhenDead` | 判定 / shit / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitWhenNotNeedShit` | 判定 / shit / when / 非 / need / shit | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitWhenShitting` | 判定 / shit / when / shitting | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckUnyoWhenDead` | 判定 / unyo / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / うんうん処理回帰 | 良い | - | - |
| `testCheckUnyoWhenAlive` | 判定 / unyo / when / alive | ゆっくり本体の状態/行動/イベント回帰 / うんうん処理回帰 | 良い | - | - |
| `testIsUnyoActionAllDefault` | 状態 / unyo / action / all / default | ゆっくり本体の状態/行動/イベント回帰 / うんうん系全体の画像/状態回帰 | 良い | - | - |
| `testCheckSickWhenNotSick` | 判定 / 病気 / when / 非 / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckSickDirtyPeriodTriggersSick` | 判定 / 病気 / dirty / period / triggers / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckSickResetsDirtyPeriodWhenClean` | 判定 / 病気 / resets / dirty / period / when / clean | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckSickWhenSick` | 判定 / 病気 / when / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckSickSevereSymptomsBranch` | 判定 / 病気 / severe / symptoms / branch | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testCheckSickSetsMoldyMessageWhenSick` | 判定 / 病気 / sets / moldy / メッセージ / when / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 病気/汚れ進行回帰 | 良い | - | - |
| `testAddSickPeriod` | 追加 / 病気 / period | ゆっくり本体の状態/行動/イベント回帰 / 病気期間増加回帰 | 良い | - | - |
| `testCheckWetWhenNotWet` | 判定 / 水濡れ / when / 非 / 水濡れ | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckWetWhenWet` | 判定 / 水濡れ / when / 水濡れ | 代謝/状態フラグ回帰 | 良い | - | - |
| `testEatBodyDeadBodyDecreases` | eat / 本体 / 死亡 / 本体 / decreases | ゆっくり本体の状態/行動/イベント回帰 / 捕食/摂食回帰 | 良い | - | - |
| `testEatBodyAliveAddsHungry` | eat / 本体 / alive / adds / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / 捕食/摂食回帰 | 良い | - | - |
| `testEatBodyWithEaterDeadReturnsEarly` | eat / 本体 / with / eater / 死亡 / 戻り / early | ゆっくり本体の状態/行動/イベント回帰 / 捕食/摂食回帰 | 良い | - | - |
| `testBeEatenWhenDead` | be / eaten / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 被食/損傷回帰 | 良い | - | - |
| `testBeEatenAliveNoVomit` | be / eaten / alive / なし / vomit | ゆっくり本体の状態/行動/イベント回帰 / 被食/損傷回帰 | 良い | - | - |
| `testBeEatenByAntsTriggersReactionBranches` | be / eaten / by / ants / triggers / reaction / branches | ゆっくり本体の状態/行動/イベント回帰 / 被食/損傷回帰 | 良い | - | - |
| `testRapidPregnantPeriodWithBaby` | rapid / pregnant / period / with / baby | ゆっくり本体の状態/行動/イベント回帰 / 妊娠進行速度回帰 | 良い | - | - |
| `testRapidPregnantPeriodNoBaby` | rapid / pregnant / period / なし / baby | ゆっくり本体の状態/行動/イベント回帰 / 妊娠進行速度回帰 | 良い | - | - |
| `testRapidShit` | rapid / shit | ゆっくり本体の状態/行動/イベント回帰 / 排泄進行速度回帰 | 良い | - | - |
| `testDisPlantStalksWithStalks` | dis / plant / stalks / with / stalks | ゆっくり本体の状態/行動/イベント回帰 / 茎の植え替え/解除回帰 | 良い | - | - |
| `testDisPlantStalksNoStalks` | dis / plant / stalks / なし / stalks | ゆっくり本体の状態/行動/イベント回帰 / 茎の植え替え/解除回帰 | 良い | - | - |
| `testCastrateStalkWhenDead` | castrate / stalk / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 茎の去勢回帰 | 良い | - | - |
| `testCastrateStalkEnable` | castrate / stalk / enable | ゆっくり本体の状態/行動/イベント回帰 / 茎の去勢回帰 | 良い | - | - |
| `testCastrateStalkDisable` | castrate / stalk / disable | ゆっくり本体の状態/行動/イベント回帰 / 茎の去勢回帰 | 良い | - | - |
| `testCastrateBodyWhenDead` | castrate / 本体 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 本体の去勢回帰 | 良い | - | - |
| `testCastrateBodyEnable` | castrate / 本体 / enable | ゆっくり本体の状態/行動/イベント回帰 / 本体の去勢回帰 | 良い | - | - |
| `testCastrateBodyDisable` | castrate / 本体 / disable | ゆっくり本体の状態/行動/イベント回帰 / 本体の去勢回帰 | 良い | - | - |
| `testGiveFireWhenBurned` | give / 火 / when / burned | ゆっくり本体の状態/行動/イベント回帰 / 火傷/着火回帰 | 良い | - | - |
| `testGiveFireWhenCrushed` | give / 火 / when / crushed | ゆっくり本体の状態/行動/イベント回帰 / 火傷/着火回帰 | 良い | - | - |
| `testGiveFireSuccess` | give / 火 / success | ゆっくり本体の状態/行動/イベント回帰 / 火傷/着火回帰 | 良い | - | - |
| `testGiveWaterWhenDead` | give / 水 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 給水/消火/嫌悪回帰 | 良い | - | - |
| `testGiveWaterAliveLikeWater` | give / 水 / alive / like / 水 | ゆっくり本体の状態/行動/イベント回帰 / 給水/消火/嫌悪回帰 | 良い | - | - |
| `testGiveWaterAliveHateWater` | give / 水 / alive / hate / 水 | ゆっくり本体の状態/行動/イベント回帰 / 給水/消火/嫌悪回帰 | 良い | - | - |
| `testGiveWaterExtinguishesFire` | give / 水 / extinguishes / 火 | ゆっくり本体の状態/行動/イベント回帰 / 給水/消火/嫌悪回帰 | 良い | - | - |
| `testRaperToggleOn` | raper / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / 強姦者状態の切替回帰 | 良い | - | - |
| `testRaperToggleOff` | raper / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 強姦者状態の切替回帰 | 良い | - | - |
| `testMoldToggleOnWhenNotSick` | mold / 切替 / on / when / 非 / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 非ゆっくり症/カビ状態切替回帰 | 良い | - | - |
| `testMoldToggleOffWhenSick` | mold / 切替 / off / when / 病気 | ゆっくり本体の状態/行動/イベント回帰 / 非ゆっくり症/カビ状態切替回帰 | 良い | - | - |
| `testClearActionsForEvent` | 解除 / actions / for / イベント | 代謝/状態フラグ回帰 | 良い | - | - |
| `testRunAwayWhenDead` | run / away / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayWhenAlive` | run / away / when / alive | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testSetPanicOn` | 設定 / 恐慌 / on | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicBurn` | 設定 / 恐慌 / burn | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicOff` | 設定 / 恐慌 / off | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testBaryInUnderGroundAlreadyBuried` | bary / in / under / ground / already / buried | ゆっくり本体の状態/行動/イベント回帰 / 地中埋没状態回帰 | 良い | - | - |
| `testBaryInUnderGroundFromNone` | bary / in / under / ground / from / none | ゆっくり本体の状態/行動/イベント回帰 / 地中埋没状態回帰 | 良い | - | - |
| `testVoiceReactionWhenDead` | voice / reaction / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 音声反応回帰 | 良い | - | - |
| `testVoiceReactionWhenAlive` | voice / reaction / when / alive | ゆっくり本体の状態/行動/イベント回帰 / 音声反応回帰 | 良い | - | - |
| `testCheckChildbirthWhenDead` | 判定 / childbirth / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 出産成立回帰 | 良い | - | - |
| `testCheckChildbirthNotPregnant` | 判定 / childbirth / 非 / pregnant | ゆっくり本体の状態/行動/イベント回帰 / 出産成立回帰 | 良い | - | - |
| `testCheckChildbirthAlreadyGivingBirth` | 判定 / childbirth / already / giving / birth | ゆっくり本体の状態/行動/イベント回帰 / 出産成立回帰 | 良い | - | - |
| `testPlusGodHandWhenDead` | plus / god / hand / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 神の手/強制操作回帰 | 良い | - | - |
| `testPlusGodHandWhenAlive` | plus / god / hand / when / alive | ゆっくり本体の状態/行動/イベント回帰 / 神の手/強制操作回帰 | 良い | - | - |
| `testHoldWhenDead` | 拘束 / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldWhenAlive` | 拘束 / when / alive | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testBreakeyesWhenDead` | breakeyes / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 目つぶし/視界喪失回帰 | 良い | - | - |
| `testBreakeyesToggleOn` | breakeyes / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / 目つぶし/視界喪失回帰 | 良い | - | - |
| `testBreakeyesToggleOff` | breakeyes / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 目つぶし/視界喪失回帰 | 良い | - | - |
| `testBreakeyesWakesUpSleeping` | breakeyes / wakes / up / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 目つぶし/視界喪失回帰 | 良い | - | - |
| `testBreakeyesClearsActions` | breakeyes / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 目つぶし/視界喪失回帰 | 良い | - | - |
| `testshutMouthWhenDead` | shut / 口 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 口封じ回帰 | 良い | - | - |
| `testshutMouthToggleOn` | shut / 口 / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / 口封じ回帰 | 良い | - | - |
| `testshutMouthToggleOff` | shut / 口 / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 口封じ回帰 | 良い | - | - |
| `testshutMouthWakesUpSleeping` | shut / 口 / wakes / up / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 口封じ回帰 | 良い | - | - |
| `testshutMouthClearsActions` | shut / 口 / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 口封じ回帰 | 良い | - | - |
| `testPickHairWhenDead` | pick / 毛 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairDefaultToBrindled1` | pick / 毛 / default / to / brindled1 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBrindled1ToBrindled2` | pick / 毛 / brindled1 / to / brindled2 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBrindled2ToBaldhead` | pick / 毛 / brindled2 / to / baldhead | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBaldheadRestores` | pick / 毛 / baldhead / restores | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairAddsStress` | pick / 毛 / adds / ストレス | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairDecreasesLovePlayer` | pick / 毛 / decreases / love / player | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testIsDontJumpWhenDead` | 状態 / dont / jump / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testIsDontJumpWhenSleeping` | 状態 / dont / jump / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testIsDontJumpWhenCriticalDamage` | 状態 / dont / jump / when / critical / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testIsDontJumpWhenNyd` | 状態 / dont / jump / when / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testIsDontJumpWhenHasBaby` | 状態 / dont / jump / when / 有無 / baby | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testIsDontJumpFalseWhenHealthy` | 状態 / dont / jump / false / when / healthy | ゆっくり本体の状態/行動/イベント回帰 / 跳躍禁止回帰 | 良い | - | - |
| `testDoSukkiriWhenDead` | do / sukkiri / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriWhenNyd` | do / sukkiri / when / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriReducesStress` | do / sukkiri / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriAddsMemories` | do / sukkiri / adds / memories | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriWithPantsDoesNotPregnant` | do / sukkiri / with / pants / does / 非 / pregnant | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriPartnerWithPantsDoesNotPregnant` | do / sukkiri / 相手 / with / pants / does / 非 / pregnant | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriSetsHappyState` | do / sukkiri / sets / happy / state | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriClearsActions` | do / sukkiri / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriPartnerDeadNoPregnancy` | do / sukkiri / 相手 / 死亡 / なし / pregnancy | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSukkiriReducesHungry` | do / sukkiri / reduces / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / すっきり/孕ませ回帰 | 良い | - | - |
| `testDoSurisuriWhenDead` | do / surisuri / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / 死亡 | 良い | - | - |
| `testDoSurisuriWhenPartnerDead` | do / surisuri / when / 相手 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / 相手 / 死亡 | 良い | - | - |
| `testDoSurisuriWhenVeryHungry` | do / surisuri / when / very / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / very / 空腹 | 良い | - | - |
| `testDoSurisuriWhenPeropero` | do / surisuri / when / peropero | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / peropero | 良い | - | - |
| `testDoSurisuriReducesStress` | do / surisuri / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / reduces / ストレス | 良い | - | - |
| `testDoSurisuriSetsNobinobi` | do / surisuri / sets / nobinobi | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / sets / nobinobi | 良い | - | - |
| `testDoSurisuriSetsHappiness` | do / surisuri / sets / 幸福 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / sets / 幸福 | 良い | - | - |
| `testDoPeroperoWhenDead` | do / peropero / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoPeroperoWhenPartnerDead` | do / peropero / when / 相手 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoPeroperoWhenNobinobi` | do / peropero / when / nobinobi | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | assert:0 |
| `testDoPeroperoWhenShutmouth` | do / peropero / when / shutmouth | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | assert:0 |
| `testDoPeroperoWhenSleeping` | do / peropero / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | assert:0 |
| `testDoPeroperoReducesPartnerStress` | do / peropero / reduces / 相手 / ストレス | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoRapeWhenDead` | do / rape / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoRapeWhenSukkiri` | do / rape / when / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | assert:0 |
| `testDoRapePartnerIsRaper` | do / rape / 相手 / 状態 / raper | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoRapeReducesStress` | do / rape / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoRapeSetsHappy` | do / rape / sets / happy | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoRapePartnerAddsStress` | do / rape / 相手 / adds / ストレス | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoRapeWithPantsNoPregnancy` | do / rape / with / pants / なし / pregnancy | ゆっくり本体の状態/行動/イベント回帰 / 強姦/妊娠回帰 | 良い | - | - |
| `testDoOnanismWhenDead` | do / onanism / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / オナニ/自発行動回帰 | 良い | - | - |
| `testDoOnanismWhenNyd` | do / onanism / when / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / オナニ/自発行動回帰 | 良い | - | assert:0 |
| `testDoOnanismReducesStress` | do / onanism / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / オナニ/自発行動回帰 | 良い | - | - |
| `testRapidExcitingDisciplineDecreases` | rapid / exciting / discipline / decreases | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testRapidExcitingDisciplineAtZeroStaysZero` | rapid / exciting / discipline / at / zero / stays / zero | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testRapidExcitingDisciplineNegativeStaysNegative` | rapid / exciting / discipline / negative / stays / negative | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testInvStalkCastrationToggleOn` | inv / stalk / castration / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / 茎去勢状態の反転回帰 | 良い | - | - |
| `testInvStalkCastrationToggleOff` | inv / stalk / castration / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / 茎去勢状態の反転回帰 | 良い | - | - |
| `testSetForceAnalCloseTrue` | 設定 / force / anal / close / true | ゆっくり本体の状態/行動/イベント回帰 / 肛門封鎖状態回帰 | 良い | - | - |
| `testSetForceAnalCloseFalse` | 設定 / force / anal / close / false | ゆっくり本体の状態/行動/イベント回帰 / 肛門封鎖状態回帰 | 良い | - | - |
| `testTakeBraidWhenDead` | take / おさげ / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / おさげ抜去回帰 | 良い | - | - |
| `testTakeBraidToggleOff` | take / おさげ / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / おさげ抜去回帰 | 良い | - | - |
| `testTakeBraidToggleOn` | take / おさげ / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / おさげ抜去回帰 | 良い | - | - |
| `testTakeBraidSetsHappinessWhenRemoved` | take / おさげ / sets / 幸福 / when / removed | ゆっくり本体の状態/行動/イベント回帰 / おさげ抜去回帰 | 良い | - | - |
| `testRemoveAllStalksWithStalks` | 除去 / all / stalks / with / stalks | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveAllStalksNoStalks` | 除去 / all / stalks / なし / stalks | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testIsHungryWhenZero` | 状態 / 空腹 / when / zero | ゆっくり本体の状態/行動/イベント回帰 / 空腹判定回帰 | 良い | - | - |
| `testIsHungryWhenFull` | 状態 / 空腹 / when / full | ゆっくり本体の状態/行動/イベント回帰 / 空腹判定回帰 | 良い | - | - |
| `testIsFullWhenAtLimit` | 状態 / full / when / at / limit | ゆっくり本体の状態/行動/イベント回帰 / 満腹判定回帰 | 良い | - | - |
| `testIsFullWhenBelow` | 状態 / full / when / below | ゆっくり本体の状態/行動/イベント回帰 / 満腹判定回帰 | 良い | - | - |
| `testIsSickWhenAboveIncubation` | 状態 / 病気 / when / above / incubation | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsSickWhenBelowIncubation` | 状態 / 病気 / when / below / incubation | ゆっくり本体の状態/行動/イベント回帰 / 病気判定回帰 | 良い | - | - |
| `testIsDamagedWhenHeavy` | 状態 / damaged / when / heavy | ゆっくり本体の状態/行動/イベント回帰 / 状態 / damaged / when / heavy | 良い | - | - |
| `testIsNoDamagedWhenZero` | 状態 / なし / damaged / when / zero | ゆっくり本体の状態/行動/イベント回帰 / 状態 / なし / damaged / when / zero | 良い | - | - |
| `testIsStarvingWhenVeryLow` | 状態 / starving / when / very / low | ゆっくり本体の状態/行動/イベント回帰 / 状態 / starving / when / very / low | 良い | - | - |
| `testEatFoodIncreasesHungry` | eat / food / increases / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / eat / food / increases / 空腹 | 良い | - | - |
| `testEatFoodAddsShit` | eat / food / adds / shit | ゆっくり本体の状態/行動/イベント回帰 / eat / food / adds / shit | 良い | - | - |
| `testEatFoodClampsNegativeHungry` | eat / food / clamps / negative / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / eat / food / clamps / negative / 空腹 | 良い | - | - |
| `testEatFoodClearsAngryAndScare` | eat / food / clears / angry / and / scare | ゆっくり本体の状態/行動/イベント回帰 / eat / food / clears / angry / and / scare | 良い | - | - |
| `testEatFoodSetsEating` | eat / food / sets / eating | ゆっくり本体の状態/行動/イベント回帰 / eat / food / sets / eating | 良い | - | - |
| `testEatFoodSetsStaying` | eat / food / sets / staying | ゆっくり本体の状態/行動/イベント回帰 / eat / food / sets / staying | 良い | - | - |
| `testBodyCutSetsCriticalDamage` | 本体 / cut / sets / critical / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 本体 / cut / sets / critical / ダメージ | 良い | - | - |
| `testBodyCutClearsActions` | 本体 / cut / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 本体 / cut / clears / actions | 良い | - | - |
| `testBodyInjureIgnoredWhenCut` | 本体 / injure / ignored / when / cut | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / ignored / when / cut | 良い | - | - |
| `testBodyInjureSetsInjured` | 本体 / injure / sets / injured | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / sets / injured | 良い | - | - |
| `testBodyInjureSetsVerySad` | 本体 / injure / sets / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / sets / very / sad | 良い | - | - |
| `testBodyInjureClearsActions` | 本体 / injure / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / clears / actions | 良い | - | - |
| `testKickCallsStrikeByPunish` | kick / calls / 打撃 / by / punish | ゆっくり本体の状態/行動/イベント回帰 / kick / calls / 打撃 / by / punish | 良い | - | - |
| `testKickFromBuriedState` | kick / from / buried / state | ゆっくり本体の状態/行動/イベント回帰 / kick / from / buried / state | 良い | - | - |
| `testNoticeNoOkazariWhenDead` | notice / なし / okazari / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / when / 死亡 | 良い | - | - |
| `testNoticeNoOkazariWhenRemoved` | notice / なし / okazari / when / removed | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / when / removed | 良い | - | - |
| `testNoticeNoOkazariWhenHasOkazari` | notice / なし / okazari / when / 有無 / okazari | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / when / 有無 / okazari | 良い | - | - |
| `testNoticeNoOkazariAlreadyNoticed` | notice / なし / okazari / already / noticed | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / already / noticed | 良い | - | - |
| `testNoticeNoOkazariWhenSleeping` | notice / なし / okazari / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / when / sleeping | 良い | - | - |
| `testNoticeNoOkazariWhenAwake` | notice / なし / okazari / when / awake | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / when / awake | 良い | - | - |
| `testCleaningItselfSetsStaying` | cleaning / itself / sets / staying | ゆっくり本体の状態/行動/イベント回帰 / cleaning / itself / sets / staying | 良い | - | - |
| `testCleaningItselfBabyDoesNotClean` | cleaning / itself / baby / does / 非 / clean | ゆっくり本体の状態/行動/イベント回帰 / cleaning / itself / baby / does / 非 / clean | 良い | - | - |
| `testCleaningItselfAdultCleansWithOkazari` | cleaning / itself / adult / cleans / with / okazari | ゆっくり本体の状態/行動/イベント回帰 / cleaning / itself / adult / cleans / with / okazari | 良い | - | - |
| `testCleaningItselfWithoutOkazariStillCleans` | cleaning / itself / without / okazari / still / cleans | ゆっくり本体の状態/行動/イベント回帰 / cleaning / itself / without / okazari / still / cleans | 良い | - | - |
| `testTeachMannerCallsDisclipline` | teach / manner / calls / disclipline | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / calls / disclipline | 良い | - | - |
| `testTeachMannerPlusAttitude` | teach / manner / plus / attitude | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / plus / attitude | 良い | - | - |
| `testCheckAttitudeNydignored` | 判定 / attitude / nydignored | ゆっくり本体の状態/行動/イベント回帰 / 判定 / attitude / nydignored | 良い | - | - |
| `testCheckAttitudeAverageWithHighPositivePoints` | 判定 / attitude / average / with / high / positive / points | ゆっくり本体の状態/行動/イベント回帰 / 判定 / attitude / average / with / high / positive / points | 良い | - | - |
| `testCheckAttitudeRudeIsCorrectedToAverage` | 判定 / attitude / rude / 状態 / corrected / to / average | ゆっくり本体の状態/行動/イベント回帰 / 判定 / attitude / rude / 状態 / corrected / to / average | 良い | - | - |
| `testCheckAttitudeSuperShitheadOnlyStepsDownToShithead` | 判定 / attitude / super / shithead / only / steps / down / to / shithead | ゆっくり本体の状態/行動/イベント回帰 / 判定 / attitude / super / shithead / only / steps / down / to / shithead | 良い | - | - |
| `testCheckAttitudeNiceDoesNotDegrade` | 判定 / attitude / nice / does / 非 / degrade | ゆっくり本体の状態/行動/イベント回帰 / 判定 / attitude / nice / does / 非 / degrade | 良い | - | - |
| `testDoSurisuriWhenDead` | do / surisuri / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / 死亡 | 良い | - | - |
| `testDoSurisuriPartnerDead` | do / surisuri / 相手 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / 相手 / 死亡 | 良い | - | - |
| `testDoSurisuriWhenVeryHungry` | do / surisuri / when / very / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / very / 空腹 | 良い | - | - |
| `testDoSurisuriWhenPeropero` | do / surisuri / when / peropero | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / when / peropero | 良い | - | - |
| `testDoSurisuriReducesStress` | do / surisuri / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / reduces / ストレス | 良い | - | - |
| `testDoSurisuriSetsVeryHappy` | do / surisuri / sets / very / happy | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / sets / very / happy | 良い | - | - |
| `testDoPeroperoWhenDead` | do / peropero / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoPeroperoTargetDead` | do / peropero / target / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoPeroperoSuccess` | do / peropero / success | ゆっくり本体の状態/行動/イベント回帰 / ぺろぺろ/スキンシップ回帰 | 良い | - | - |
| `testDoGuriguriWhenDead` | do / guriguri / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / guriguri / when / 死亡 | 良い | - | - |
| `testDoGuriguriChildDead` | do / guriguri / 子 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / guriguri / 子 / 死亡 | 良い | - | - |
| `testDoGuriguriSuccess` | do / guriguri / success | ゆっくり本体の状態/行動/イベント回帰 / do / guriguri / success | 良い | - | - |
| `testDoGuriguriAddsChildStress` | do / guriguri / adds / 子 / ストレス | ゆっくり本体の状態/行動/イベント回帰 / do / guriguri / adds / 子 / ストレス | 良い | - | - |
| `testInjectJuiceWhenDead` | inject / juice / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / when / 死亡 | 良い | - | - |
| `testInjectJuiceHeals` | inject / juice / heals | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / heals | 良い | - | - |
| `testInjectJuiceFillsHungry` | inject / juice / fills / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / fills / 空腹 | 良い | - | - |
| `testInjectJuiceClearsInjured` | inject / juice / clears / injured | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / clears / injured | 良い | - | - |
| `testInjectJuiceDoesNotClearCut` | inject / juice / does / 非 / 解除 / cut | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / does / 非 / 解除 / cut | 良い | - | - |
| `testInjectJuiceClearsMelt` | inject / juice / clears / melt | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / clears / melt | 良い | - | - |
| `testInjectJuiceSetsVeryHappy` | inject / juice / sets / very / happy | ゆっくり本体の状態/行動/イベント回帰 / inject / juice / sets / very / happy | 良い | - | - |
| `testSetNeedleOnAddsNeedle` | 設定 / 針 / on / adds / 針 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 針 / on / adds / 針 | 良い | - | - |
| `testSetNeedleOffRemovesNeedle` | 設定 / 針 / off / removes / 針 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 針 / off / removes / 針 | 良い | - | - |
| `testSetNeedleOnSetsNeedledFlag` | 設定 / 針 / on / sets / needled / flag | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 針 / on / sets / needled / flag | 良い | - | - |
| `testSetNeedleOnClearsNeedledFlag` | 設定 / 針 / on / clears / needled / flag | ゆっくり本体の状態/行動/イベント回帰 / 設定 / 針 / on / clears / needled / flag | 良い | - | - |
| `testInWaterWhenDead` | in / 水 / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / in / 水 / when / 死亡 | 良い | - | - |
| `testInWaterWakesUp` | in / 水 / wakes / up | ゆっくり本体の状態/行動/イベント回帰 / in / 水 / wakes / up | 良い | - | - |
| `testInWaterShallowLikeWaterHappy` | in / 水 / shallow / like / 水 / happy | ゆっくり本体の状態/行動/イベント回帰 / in / 水 / shallow / like / 水 / happy | 良い | - | - |
| `testInWaterShallowHateWaterSad` | in / 水 / shallow / hate / 水 / sad | ゆっくり本体の状態/行動/イベント回帰 / in / 水 / shallow / hate / 水 / sad | 良い | - | - |
| `testCheckDamageDeadBodyDoesNotHeal` | 判定 / ダメージ / 死亡 / 本体 / does / 非 / heal | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 死亡 / 本体 / does / 非 / heal | 良い | - | - |
| `testCheckDamageAliveWithHighDamageSetsTooMuch` | 判定 / ダメージ / alive / with / high / ダメージ / sets / too / much | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / alive / with / high / ダメージ / sets / too / much | 良い | - | - |
| `testCheckDamageLowDamageDoesNotKill` | 判定 / ダメージ / low / ダメージ / does / 非 / kill | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / low / ダメージ / does / 非 / kill | 良い | - | - |
| `testCheckDamageCantDieProtects` | 判定 / ダメージ / cant / die / protects | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / cant / die / protects | 良い | - | - |
| `testCheckDamageOrangeSteamHealsWhenUnbirthConnected` | 判定 / ダメージ / orange / steam / heals / when / unbirth / connected | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / orange / steam / heals / when / unbirth / connected | 良い | - | - |
| `testCheckDamageOrangeSteamNoHealWhenUnbirthDisconnected` | 判定 / ダメージ / orange / steam / なし / heal / when / unbirth / disconnected | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / orange / steam / なし / heal / when / unbirth / disconnected | 良い | - | - |
| `testCheckDamagePoisonSteamSetsVerySadAndMessage` | 判定 / ダメージ / poison / steam / sets / very / sad / and / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / poison / steam / sets / very / sad / and / メッセージ | 良い | - | - |
| `testCheckDamagePoisonSteamWithDamageStateUsesNegiMessage` | 判定 / ダメージ / poison / steam / with / ダメージ / state / uses / negi / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / poison / steam / with / ダメージ / state / uses / negi / メッセージ | 良い | - | - |
| `testCheckDamagePealedAddsDamageAndClearsPeropero` | 判定 / ダメージ / pealed / adds / ダメージ / and / clears / peropero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / pealed / adds / ダメージ / and / clears / peropero | 良い | - | - |
| `testCheckDamageSugarSteamHealsWhenHighDamage` | 判定 / ダメージ / sugar / steam / heals / when / high / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / sugar / steam / heals / when / high / ダメージ | 良い | - | - |
| `testCheckDamageSugarSteamNoHealWhenLowDamage` | 判定 / ダメージ / sugar / steam / なし / heal / when / low / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / sugar / steam / なし / heal / when / low / ダメージ | 良い | - | - |
| `testCheckDamageSugarSteamNoHealWhenUnbirthDisconnected` | 判定 / ダメージ / sugar / steam / なし / heal / when / unbirth / disconnected | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / sugar / steam / なし / heal / when / unbirth / disconnected | 良い | - | - |
| `testCheckDamageHungryZeroAddsDamage` | 判定 / ダメージ / 空腹 / zero / adds / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 空腹 / zero / adds / ダメージ | 良い | - | - |
| `testCheckDamageNotHungryHeals` | 判定 / ダメージ / 非 / 空腹 / heals | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 非 / 空腹 / heals | 良い | - | - |
| `testCheckDamageInjuredNoVomitWhenRndNonZero` | 判定 / ダメージ / injured / なし / vomit / when / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / injured / なし / vomit / when / rnd / non / zero | 良い | - | - |
| `testCheckDamageInjuredHealsWhenFullNoDamage` | 判定 / ダメージ / injured / heals / when / full / なし / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / injured / heals / when / full / なし / ダメージ | 良い | - | - |
| `testCheckDamageInjuredHealsWhenNotHeavy` | 判定 / ダメージ / injured / heals / when / 非 / heavy | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / injured / heals / when / 非 / heavy | 良い | - | - |
| `testCheckDamageCutWakesUpAndTalks` | 判定 / ダメージ / cut / wakes / up / and / talks | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / cut / wakes / up / and / talks | 良い | - | - |
| `testCheckDamageNoDamagePeriodIncrements` | 判定 / ダメージ / なし / ダメージ / period / increments | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / なし / ダメージ / period / increments | 良い | - | - |
| `testCheckDamageSickMidStageAddsDoubleDamage` | 判定 / ダメージ / 病気 / mid / stage / adds / double / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 病気 / mid / stage / adds / double / ダメージ | 良い | - | - |
| `testCheckDamageSickLateStageAddsTripleDamage` | 判定 / ダメージ / 病気 / late / stage / adds / triple / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 病気 / late / stage / adds / triple / ダメージ | 良い | - | - |
| `testCheckDamageSickEarlyStageAddsSingleDamage` | 判定 / ダメージ / 病気 / early / stage / adds / single / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / 病気 / early / stage / adds / single / ダメージ | 良い | - | - |
| `testCheckDamageUnbirthNoHealWithoutStalk` | 判定 / ダメージ / unbirth / なし / heal / without / stalk | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / unbirth / なし / heal / without / stalk | 良い | - | - |
| `testCheckDamageRoadPressStrikeOnMapIndex2` | 判定 / ダメージ / road / press / 打撃 / on / map / index2 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / road / press / 打撃 / on / map / index2 | 良い | - | - |
| `testCheckDamageTooMuchClearsLowPriorityEvent` | 判定 / ダメージ / too / much / clears / low / priority / イベント | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / too / much / clears / low / priority / イベント | 良い | - | - |
| `testCheckDamagePoisonSteamNydForcesVerySad` | 判定 / ダメージ / poison / steam / 非ゆっくり症 / forces / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 判定 / ダメージ / poison / steam / 非ゆっくり症 / forces / very / sad | 良い | - | - |
| `testGiveOkazariWhenDead` | give / okazari / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / give / okazari / when / 死亡 | 良い | - | - |
| `testGiveOkazariSuccess` | give / okazari / success | ゆっくり本体の状態/行動/イベント回帰 / give / okazari / success | 良い | - | - |
| `testTakeOkazariWhenDead` | take / okazari / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / take / okazari / when / 死亡 | 良い | - | - |
| `testTakeOkazariSuccess` | take / okazari / success | ゆっくり本体の状態/行動/イベント回帰 / take / okazari / success | 良い | - | - |
| `testDropOkazariWhenNoOkazari` | drop / okazari / when / なし / okazari | ゆっくり本体の状態/行動/イベント回帰 / drop / okazari / when / なし / okazari | 良い | - | - |
| `testDropOkazariSuccess` | drop / okazari / success | ゆっくり本体の状態/行動/イベント回帰 / drop / okazari / success | 良い | - | - |
| `testGivePantsWhenDead` | give / pants / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / give / pants / when / 死亡 | 良い | - | - |
| `testGivePantsToggleOn` | give / pants / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / give / pants / 切替 / on | 良い | - | - |
| `testGivePantsToggleOff` | give / pants / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / give / pants / 切替 / off | 良い | - | - |
| `testCheckMessageDecrementsCount` | 判定 / メッセージ / decrements / count | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / decrements / count | 良い | - | - |
| `testCheckMessageAtZeroStaysZero` | 判定 / メッセージ / at / zero / stays / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / at / zero / stays / zero | 良い | - | - |
| `testCheckMessageResetsForceFace` | 判定 / メッセージ / resets / force / 表情 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / resets / force / 表情 | 良い | - | - |
| `testCheckMessageClearsBufferWhenNearEnd` | 判定 / メッセージ / clears / buffer / when / near / end | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / clears / buffer / when / near / end | 良い | - | - |
| `testCheckMessageResetsFlagsAtZero` | 判定 / メッセージ / resets / flags / at / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / resets / flags / at / zero | 良い | - | - |
| `testCheckMessageDeadSetsSilent` | 判定 / メッセージ / 死亡 / sets / silent | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / 死亡 / sets / silent | 良い | - | - |
| `testCheckMessageSleepingNightmareMessage` | 判定 / メッセージ / sleeping / nightmare / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / sleeping / nightmare / メッセージ | 良い | - | - |
| `testCheckMessageForceBirthMessage` | 判定 / メッセージ / force / birth / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / force / birth / メッセージ | 良い | - | - |
| `testCheckMessageFlyingBranch` | 判定 / メッセージ / flying / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / flying / branch | 良い | - | - |
| `testCheckMessageGrabbedStressfulBranch` | 判定 / メッセージ / grabbed / stressful / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / メッセージ / grabbed / stressful / branch | 良い | - | - |
| `testSetBoundaryInitializesSprites` | 設定 / boundary / initializes / sprites | ゆっくり本体の状態/行動/イベント回帰 / 設定 / boundary / initializes / sprites | 良い | - | - |
| `testGetExpandShapeAppliesUnyoForce` | 取得 / expand / shape / applies / unyo / force | ゆっくり本体の状態/行動/イベント回帰 / 取得 / expand / shape / applies / unyo / force | 良い | - | - |
| `testSetNegiMessageWhenCannotTalkClears` | 設定 / negi / メッセージ / when / cannot / talk / clears | ゆっくり本体の状態/行動/イベント回帰 / 設定 / negi / メッセージ / when / cannot / talk / clears | 良い | - | - |
| `testSetNegiMessageResetsActions` | 設定 / negi / メッセージ / resets / actions | ゆっくり本体の状態/行動/イベント回帰 / 設定 / negi / メッセージ / resets / actions | 良い | - | - |
| `testCheckWaitReturnsFalseThenTrue` | 判定 / wait / 戻り / false / then / true | ゆっくり本体の状態/行動/イベント回帰 / 判定 / wait / 戻り / false / then / true | 良い | - | - |
| `testIsCutPeniDetectsEvent` | 状態 / cut / peni / detects / イベント | ゆっくり本体の状態/行動/イベント回帰 / 状態 / cut / peni / detects / イベント | 良い | - | - |
| `testInvNeedleToggles` | inv / 針 / toggles | ゆっくり本体の状態/行動/イベント回帰 / inv / 針 / toggles | 良い | - | - |
| `testGetDiarrheaKaiyuAlwaysTrue` | 取得 / diarrhea / kaiyu / always / true | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / kaiyu / always / true | 良い | - | - |
| `testGetDiarrheaProbability` | 取得 / diarrhea / probability | ゆっくり本体の状態/行動/イベント回帰 / 取得 / diarrhea / probability | 良い | - | - |
| `testSetShitWithVeryShit` | 設定 / shit / with / very / shit | ゆっくり本体の状態/行動/イベント回帰 / 設定 / shit / with / very / shit | 良い | - | - |
| `testSetShitIgnoredWhenShitting` | 設定 / shit / ignored / when / shitting | ゆっくり本体の状態/行動/イベント回帰 / 設定 / shit / ignored / when / shitting | 良い | - | - |
| `testRemoveChildrenList` | 除去 / children / list | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveElderSisterList` | 除去 / elder / 姉妹 / list | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveSisterList` | 除去 / 姉妹 / list | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testResetAttachmentBoundaryCallsReset` | reset / attachment / boundary / calls / reset | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testOverEatingAndTooFull` | over / eating / and / too / full | ゆっくり本体の状態/行動/イベント回帰 / over / eating / and / too / full | 良い | - | - |
| `testDamagedLightlyTrueWhenVery` | damaged / lightly / true / when / very | ゆっくり本体の状態/行動/イベント回帰 / damaged / lightly / true / when / very | 良い | - | - |
| `testHappyUnhappyAndNormal` | happy / unhappy / and / normal | ゆっくり本体の状態/行動/イベント回帰 / happy / unhappy / and / normal | 良い | - | - |
| `testOld` | old | ゆっくり本体の状態/行動/イベント回帰 / old | 良い | - | - |
| `testForceExciting` | force / exciting | ゆっくり本体の状態/行動/イベント回帰 / force / exciting | 良い | - | - |
| `testTakeoutItemFromBodyObjId` | takeout / item / from / 本体 / obj / id | ゆっくり本体の状態/行動/イベント回帰 / takeout / item / from / 本体 / obj / id | 良い | - | - |
| `testGetSisterAndElderSister` | 取得 / 姉妹 / and / elder / 姉妹 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 姉妹 / and / elder / 姉妹 | 良い | - | - |
| `testAboutToBurstAndInflation` | about / to / 破裂 / and / inflation | ゆっくり本体の状態/行動/イベント回帰 / about / to / 破裂 / and / inflation | 良い | - | - |
| `testEatenByAnimals` | eaten / by / animals | ゆっくり本体の状態/行動/イベント回帰 / eaten / by / animals | 良い | - | - |
| `testBabyTypesDequeueAndStalksDequeue` | baby / types / dequeue / and / stalks / dequeue | ゆっくり本体の状態/行動/イベント回帰 / baby / types / dequeue / and / stalks / dequeue | 良い | - | - |
| `testCollisionPivotAndBraidSize` | 衝突 / pivot / and / おさげ / size | ゆっくり本体の状態/行動/イベント回帰 / 衝突 / pivot / and / おさげ / size | 良い | - | - |
| `testSetFavItemNull` | 設定 / fav / item / null | ゆっくり本体の状態/行動/イベント回帰 / 設定 / fav / item / null | 良い | - | - |
| `testSetExcitingSetsForceFace` | 設定 / exciting / sets / force / 表情 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / exciting / sets / force / 表情 | 良い | - | reflection |
| `testIsToTakeout` | 状態 / to / takeout | ゆっくり本体の状態/行動/イベント回帰 / 状態 / to / takeout | 良い | - | - |
| `testCheckSleepNoSleepSteamResetsSleeping` | 判定 / 睡眠 / なし / 睡眠 / steam / resets / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepNoSleepSteamUnbirthPlantDoesNotReset` | 判定 / 睡眠 / なし / 睡眠 / steam / unbirth / plant / does / 非 / reset | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepFlyingSleepyReturnsFalseWhenZnonZero` | 判定 / 睡眠 / flying / sleepy / 戻り / false / when / znon / zero | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepSleepingStressfulSetsNightmare` | 判定 / 睡眠 / sleeping / stressful / sets / nightmare | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepSleepingNotStressfulClearsNightmare` | 判定 / 睡眠 / sleeping / 非 / stressful / clears / nightmare | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepElseBranchNightDecrementsWakeUpTime` | 判定 / 睡眠 / else / branch / night / decrements / wake / up / time | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testCheckSleepStarvingWakesUp` | 判定 / 睡眠 / starving / wakes / up | ゆっくり本体の状態/行動/イベント回帰 / 睡眠状態遷移回帰 | 良い | - | - |
| `testDoSurisuriEarlyReturnWhenVeryHungry` | do / surisuri / early / 戻り / when / very / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / early / 戻り / when / very / 空腹 | 良い | - | - |
| `testDoSurisuriSickPartnerSetsSad` | do / surisuri / sick / 相手 / sets / sad | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / sick / 相手 / sets / sad | 良い | - | - |
| `testDoSurisuriAntsTransfer` | do / surisuri / ants / transfer | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / ants / transfer | 良い | - | - |
| `testDoSurisuriAccidentTriggersSukkiri` | do / surisuri / accident / triggers / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / accident / triggers / sukkiri | 良い | - | - |
| `testDoSurisuriSickTransfersSickPeriod` | do / surisuri / 病気 / transfers / 病気 / period | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / 病気 / transfers / 病気 / period | 良い | - | - |
| `testDoSurisuriPartnerSickTransfersToSelf` | do / surisuri / 相手 / 病気 / transfers / to / self | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / 相手 / 病気 / transfers / to / self | 良い | - | - |
| `testCheckEmotionAngryPeriodExpires` | 判定 / emotion / angry / period / expires | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / angry / period / expires | 良い | - | - |
| `testCheckEmotionScarePeriodExpires` | 判定 / emotion / scare / period / expires | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / scare / period / expires | 良い | - | - |
| `testCheckEmotionSadPeriodExpires` | 判定 / emotion / sad / period / expires | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / sad / period / expires | 良い | - | - |
| `testCheckEmotionPlayingStopsOnLimit` | 判定 / emotion / playing / stops / on / limit | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / playing / stops / on / limit | 良い | - | - |
| `testCheckEmotionYunnyaaBranch` | 判定 / emotion / yunnyaa / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / yunnyaa / branch | 良い | - | - |
| `testCheckEmotionProcessingBeltBegForLifeBranch` | 判定 / emotion / processing / belt / beg / for / life / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / processing / belt / beg / for / life / branch | 良い | - | - |
| `testCheckEmotionUnunSlaveExcitingClears` | 判定 / emotion / unun / slave / exciting / clears | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / unun / slave / exciting / clears | 良い | - | - |
| `testCheckEmotionDirtyAdultCleans` | 判定 / emotion / dirty / adult / cleans | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / dirty / adult / cleans | 良い | - | - |
| `testCheckEmotionHungryMessage` | 判定 / emotion / 空腹 / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / 空腹 / メッセージ | 良い | - | - |
| `testCheckEmotionReturnsEarlyWhenEventActive` | 判定 / emotion / 戻り / early / when / イベント / active | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / 戻り / early / when / イベント / active | 良い | - | - |
| `testCheckEmotionSurisuriByPlayerShortCircuit` | 判定 / emotion / surisuri / by / player / short / circuit | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / surisuri / by / player / short / circuit | 良い | - | - |
| `testCheckEmotionProcessingBeltYunnyaaBranch` | 判定 / emotion / processing / belt / yunnyaa / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / processing / belt / yunnyaa / branch | 良い | - | - |
| `testCheckEmotionProcessingBeltKilledInFactoryMessageBranch` | 判定 / emotion / processing / belt / killed / in / factory / メッセージ / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / processing / belt / killed / in / factory / メッセージ / branch | 良い | - | - |
| `testCheckEmotionDirtyChildCallsParent` | 判定 / emotion / dirty / 子 / calls / 親 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / dirty / 子 / calls / 親 | 良い | - | - |
| `testCheckShitLimitLockmoveLeaks` | 判定 / shit / limit / lockmove / leaks | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitPreparesShittingOnAgeBoundary` | 判定 / shit / prepares / shitting / on / age / boundary | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitRaperExcitingSkipsAndClears` | 判定 / shit / raper / exciting / skips / and / clears | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitEventPriorityBlocks` | 判定 / shit / イベント / priority / blocks | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitUnbirthWithVeryShitAmpouleAddsShit` | 判定 / shit / unbirth / with / very / shit / ampoule / adds / shit | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitSleepingBelowLimitReturnsFalse` | 判定 / shit / sleeping / below / limit / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitOverLimitAnalCloseInflates` | 判定 / shit / over / limit / anal / close / inflates | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testCheckShitBabyOverLimitMakesDirty` | 判定 / shit / baby / over / limit / makes / dirty | ゆっくり本体の状態/行動/イベント回帰 / 排泄回避/実行回帰 | 良い | - | - |
| `testSetWorldEventResMessageSetsBuffer` | 設定 / world / イベント / res / メッセージ / sets / buffer | ゆっくり本体の状態/行動/イベント回帰 / 設定 / world / イベント / res / メッセージ / sets / buffer | 良い | - | - |
| `testJudgeCanTransForGodHandDefaultFalse` | judge / 可否 / trans / for / god / hand / default / false | ゆっくり本体の状態/行動/イベント回帰 / judge / 可否 / trans / for / god / hand / default / false | 良い | - | - |
| `testExecTransformNoop` | exec / transform / noop | ゆっくり本体の状態/行動/イベント回帰 / exec / transform / noop | 良い | - | - |
| `testGetHybridTypeReturnsSelfType` | 取得 / hybrid / type / 戻り / self / type | ゆっくり本体の状態/行動/イベント回帰 / 取得 / hybrid / type / 戻り / self / type | 良い | - | - |
| `testCheckTransformDefaultNull` | 判定 / transform / default / null | ゆっくり本体の状態/行動/イベント回帰 / 判定 / transform / default / null | 良い | - | - |
| `testTakeScreenRectCopiesRect` | take / screen / rect / copies / rect | ゆっくり本体の状態/行動/イベント回帰 / take / screen / rect / copies / rect | 良い | - | reflection |
| `testTakeScreenRectUsesBodyScreenRect` | take / screen / rect / uses / 本体 / screen / rect | ゆっくり本体の状態/行動/イベント回帰 / take / screen / rect / uses / 本体 / screen / rect | 良い | - | reflection |
| `testSetTargetMoveOffsetSetsOffsets` | 設定 / target / 移動 / offset / sets / offsets | ゆっくり本体の状態/行動/イベント回帰 / 設定 / target / 移動 / offset / sets / offsets | 良い | - | - |
| `testMoveToBedSetsFlagsAndTarget` | 移動 / to / bed / sets / flags / and / target | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / bed / sets / flags / and / target | 良い | - | - |
| `testSetNegiMessageWhenCannotTalkClears` | 設定 / negi / メッセージ / when / cannot / talk / clears | ゆっくり本体の状態/行動/イベント回帰 / 設定 / negi / メッセージ / when / cannot / talk / clears | 良い | - | - |
| `testSetNegiMessageSetsMessageAndPiko` | 設定 / negi / メッセージ / sets / メッセージ / and / piko | ゆっくり本体の状態/行動/イベント回帰 / 設定 / negi / メッセージ / sets / メッセージ / and / piko | 良い | - | - |
| `testSetDirtyFlagDelegates` | 設定 / dirty / flag / delegates | ゆっくり本体の状態/行動/イベント回帰 / 設定 / dirty / flag / delegates | 良い | - | - |
| `testBodyEventSendMessageSetsWindowColors` | 本体 / イベント / send / メッセージ / sets / window / colors | ゆっくり本体の状態/行動/イベント回帰 / 本体 / イベント / send / メッセージ / sets / window / colors | 良い | - | - |
| `testBodyEventResMessageSetsWindowColors` | 本体 / イベント / res / メッセージ / sets / window / colors | ゆっくり本体の状態/行動/イベント回帰 / 本体 / イベント / res / メッセージ / sets / window / colors | 良い | - | - |
| `testGetBodyCastration` | 取得 / 本体 / castration | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 本体 / castration | 良い | - | - |
| `testGetStalkCastrationReturnsField` | 取得 / stalk / castration / 戻り / field | ゆっくり本体の状態/行動/イベント回帰 / 取得 / stalk / castration / 戻り / field | 良い | - | - |
| `testGetNeedleReturnsField` | 取得 / 針 / 戻り / field | ゆっくり本体の状態/行動/イベント回帰 / 取得 / 針 / 戻り / field | 良い | - | - |
| `testIsAliceRaperDefaultFalse` | 状態 / alice / raper / default / false | ゆっくり本体の状態/行動/イベント回帰 / 状態 / alice / raper / default / false | 良い | - | reflection |
| `testHasGetPopupAndUsePopup` | 有無 / 取得 / popup / and / use / popup | ゆっくり本体の状態/行動/イベント回帰 / 有無 / 取得 / popup / and / use / popup | 良い | - | - |
| `testIsbindStalk` | isbind / stalk | ゆっくり本体の状態/行動/イベント回帰 / isbind / stalk | 良い | - | - |
| `testHasTrauma` | 有無 / trauma | ゆっくり本体の状態/行動/イベント回帰 / 有無 / trauma | 良い | - | - |
| `testGetEatAmountUsesBaseArray` | 取得 / eat / amount / uses / base / array | ゆっくり本体の状態/行動/イベント回帰 / 取得 / eat / amount / uses / base / array | 良い | - | - |
| `testSetLastActionTimeUpdatesValue` | 設定 / last / action / time / updates / value | ゆっくり本体の状態/行動/イベント回帰 / 設定 / last / action / time / updates / value | 良い | - | - |
| `testGetSellingPrice` | 取得 / selling / price | ゆっくり本体の状態/行動/イベント回帰 / 取得 / selling / price | 良い | - | - |
| `testGetMaxHaveBaby` | 取得 / max / have / baby | ゆっくり本体の状態/行動/イベント回帰 / 取得 / max / have / baby | 良い | - | - |
| `testAnNameSettersAndGetters` | an / name / setters / and / getters | ゆっくり本体の状態/行動/イベント回帰 / an / name / setters / and / getters | 良い | - | - |
| `testBaseArraySetters` | base / array / setters | ゆっくり本体の状態/行動/イベント回帰 / base / array / setters | 良い | - | - |
| `testPeriodAndLimitSetters` | period / and / limit / setters | ゆっくり本体の状態/行動/イベント回帰 / period / and / limit / setters | 良い | - | - |
| `testSetLovePlayerLimitAndState` | 設定 / love / player / limit / and / state | ゆっくり本体の状態/行動/イベント回帰 / 設定 / love / player / limit / and / state | 良い | - | - |
| `testSetCountZandGetCountZ` | 設定 / count / zand / 取得 / count / z | ゆっくり本体の状態/行動/イベント回帰 / 設定 / count / zand / 取得 / count / z | 良い | - | - |
| `testSetCantDiePeriod` | 設定 / cant / die / period | ゆっくり本体の状態/行動/イベント回帰 / 設定 / cant / die / period | 良い | - | - |
| `testMoreSimpleSettersAndGetters` | more / simple / setters / and / getters | ゆっくり本体の状態/行動/イベント回帰 / more / simple / setters / and / getters | 良い | - | - |
| `testMoreBodyAttributesAccessors` | more / 本体 / attributes / accessors | ゆっくり本体の状態/行動/イベント回帰 / more / 本体 / attributes / accessors | 良い | - | - |
| `testRemainingSimpleAccessors` | remaining / simple / accessors | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testBodyAttributesRemainingNonGuiAccessors` | 本体 / attributes / remaining / non / gui / accessors | ゆっくり本体の状態/行動/イベント回帰 / 本体 / attributes / remaining / non / gui / accessors | 良い | - | - |
| `testMultipleStrikesAccumulateDamage` | multiple / strikes / accumulate / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / multiple / strikes / accumulate / ダメージ | 良い | - | - |
| `testAddDamageWithNegativeAmount` | 追加 / ダメージ / with / negative / amount | 生存/行動可否回帰 | 良い | - | - |
| `testAddDamageClampToZero` | 追加 / ダメージ / 範囲補正 / to / zero | 生存/行動可否回帰 | 良い | - | - |
| `testStressClampToZero` | ストレス / 範囲補正 / to / zero | ゆっくり本体の状態/行動/イベント回帰 / ストレス / 範囲補正 / to / zero | 良い | - | - |
| `testHungryCanExceedLimit` | 空腹 / 可否 / exceed / limit | ゆっくり本体の状態/行動/イベント回帰 / 空腹 / 可否 / exceed / limit | 良い | - | - |
| `testPregnantPeriodBoostAccumulates` | pregnant / period / boost / accumulates | ゆっくり本体の状態/行動/イベント回帰 / pregnant / period / boost / accumulates | 良い | - | - |
| `testMoveToNormalCoordinates` | 移動 / to / normal / coordinates | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / normal / coordinates | 良い | - | - |
| `testMoveToIgnoredWhenDead` | 移動 / to / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / ignored / when / 死亡 | 良い | - | - |
| `testMoveToIgnoredWhenBlocked` | 移動 / to / ignored / when / blocked | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / ignored / when / blocked | 良い | - | - |
| `testMoveToWithZ` | 移動 / to / with / z | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / with / z | 良い | - | - |
| `testMoveToClampNegativeValues` | 移動 / to / 範囲補正 / negative / values | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / 範囲補正 / negative / values | 良い | - | - |
| `testMoveToClampExceedingValues` | 移動 / to / 範囲補正 / exceeding / values | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / 範囲補正 / exceeding / values | 良い | - | - |
| `testMoveToZnormal` | 移動 / to / znormal | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / znormal | 良い | - | - |
| `testMoveToZignoredWhenDead` | 移動 / to / zignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / zignored / when / 死亡 | 良い | - | - |
| `testMoveToFoodSetsFlags` | 移動 / to / food / sets / flags | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / food / sets / flags | 良い | - | - |
| `testMoveToSukkiriSetsFlags` | 移動 / to / sukkiri / sets / flags | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / sukkiri / sets / flags | 良い | - | - |
| `testMoveToToiletSetsFlags` | 移動 / to / toilet / sets / flags | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / toilet / sets / flags | 良い | - | - |
| `testMoveToBodySetsFlags` | 移動 / to / 本体 / sets / flags | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / 本体 / sets / flags | 良い | - | - |
| `testMoveToFoodClearsActions` | 移動 / to / food / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / food / clears / actions | 良い | - | - |
| `testMoveToSukkiriClearsActions` | 移動 / to / sukkiri / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / sukkiri / clears / actions | 良い | - | - |
| `testMoveToToiletClearsActions` | 移動 / to / toilet / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / toilet / clears / actions | 良い | - | - |
| `testMoveToEventSetsCoordinates` | 移動 / to / イベント / sets / coordinates | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / イベント / sets / coordinates | 良い | - | - |
| `testMoveToEventIgnoredWhenDead` | 移動 / to / イベント / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / イベント / ignored / when / 死亡 | 良い | - | - |
| `testFeedWhenHungryIncreasesHappiness` | feed / when / 空腹 / increases / 幸福 | ゆっくり本体の状態/行動/イベント回帰 / feed / when / 空腹 / increases / 幸福 | 良い | - | - |
| `testFeedWhenFullDecreasesHappiness` | feed / when / full / decreases / 幸福 | ゆっくり本体の状態/行動/イベント回帰 / feed / when / full / decreases / 幸福 | 良い | - | - |
| `testFeedAdds1500Food` | feed / adds1500 / food | ゆっくり本体の状態/行動/イベント回帰 / feed / adds1500 / food | 良い | - | - |
| `testAddLovePlayerPositive` | 追加 / love / player / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / positive | 良い | - | - |
| `testAddLovePlayerNegative` | 追加 / love / player / negative | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / negative | 良い | - | - |
| `testAddLovePlayerClampUpper` | 追加 / love / player / 範囲補正 / upper | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / 範囲補正 / upper | 良い | - | - |
| `testAddLovePlayerClampLower` | 追加 / love / player / 範囲補正 / lower | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / 範囲補正 / lower | 良い | - | - |
| `testAddLovePlayerNydalwaysMin` | 追加 / love / player / nydalways / min | ゆっくり本体の状態/行動/イベント回帰 / 追加 / love / player / nydalways / min | 良い | - | - |
| `testSetPanicTrueSetsTypeAndClearsFlags` | 設定 / 恐慌 / true / sets / type / and / clears / flags | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicFalseClearsType` | 設定 / 恐慌 / false / clears / type | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicIgnoredWhenDead` | 設定 / 恐慌 / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicIgnoredWhenSleeping` | 設定 / 恐慌 / ignored / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicIgnoredWhenUnbirth` | 設定 / 恐慌 / ignored / when / unbirth | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicAlreadyPanicResetsCounter` | 設定 / 恐慌 / already / 恐慌 / resets / counter | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testSetPanicIgnoredWhenRaperAndExciting` | 設定 / 恐慌 / ignored / when / raper / and / exciting | ゆっくり本体の状態/行動/イベント回帰 / 恐慌状態回帰 | 良い | - | - |
| `testHoldFirstTimePicksUp` | 拘束 / first / time / picks / up | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldSecondTimeReleases` | 拘束 / second / time / releases | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldIgnoredWhenDead` | 拘束 / ignored / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldSetsHappinessSad` | 拘束 / sets / 幸福 / sad | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testHoldResetsZwhenAboveGround` | 拘束 / resets / zwhen / above / ground | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRunAwayToUpperRight` | run / away / to / upper / right | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayToLowerLeft` | run / away / to / lower / left | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayIgnoredWhenCannotAction` | run / away / ignored / when / cannot / action | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayIgnoredWhenExciting` | run / away / ignored / when / exciting | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayIgnoredWhenAngry` | run / away / ignored / when / angry | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwaySetsScare` | run / away / sets / scare | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testRunAwayIgnoredWhenUnbirth` | run / away / ignored / when / unbirth | ゆっくり本体の状態/行動/イベント回帰 / 逃走行動回帰 | 良い | - | - |
| `testSetCleaningClearsDirty` | 設定 / cleaning / clears / dirty | ゆっくり本体の状態/行動/イベント回帰 / 設定 / cleaning / clears / dirty | 良い | - | - |
| `testSetCleaningClearsWet` | 設定 / cleaning / clears / 水濡れ | ゆっくり本体の状態/行動/イベント回帰 / 設定 / cleaning / clears / 水濡れ | 良い | - | - |
| `testSetCleaningResetsWetPeriod` | 設定 / cleaning / resets / 水濡れ / period | ゆっくり本体の状態/行動/イベント回帰 / 設定 / cleaning / resets / 水濡れ / period | 良い | - | - |
| `testPickHairDefaultToBrindled1` | pick / 毛 / default / to / brindled1 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBrindled1ToBrindled2` | pick / 毛 / brindled1 / to / brindled2 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBrindled2ToBaldhead` | pick / 毛 / brindled2 / to / baldhead | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairBaldheadToDefault` | pick / 毛 / baldhead / to / default | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testPickHairIgnoredWhenDead` | pick / 毛 / ignored / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 毛むしり/ハゲ進行回帰 | 良い | - | - |
| `testDoYunnyaaTrueSetsYunnyaaAndStay` | do / yunnyaa / true / sets / yunnyaa / and / stay | ゆっくり本体の状態/行動/イベント回帰 / do / yunnyaa / true / sets / yunnyaa / and / stay | 良い | - | - |
| `testDoYunnyaaFalseSetsYunnyaa` | do / yunnyaa / false / sets / yunnyaa | ゆっくり本体の状態/行動/イベント回帰 / do / yunnyaa / false / sets / yunnyaa | 良い | - | - |
| `testDoYunnyaaIgnoredWhenCannotAction` | do / yunnyaa / ignored / when / cannot / action | ゆっくり本体の状態/行動/イベント回帰 / do / yunnyaa / ignored / when / cannot / action | 良い | - | - |
| `testTeachMannerFurifuriAndExcitingAddsAttitude` | teach / manner / furifuri / and / exciting / adds / attitude | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / furifuri / and / exciting / adds / attitude | 良い | - | - |
| `testTeachMannerSukkiriNonRaperAddsAttitude` | teach / manner / sukkiri / non / raper / adds / attitude | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / sukkiri / non / raper / adds / attitude | 良い | - | - |
| `testTeachMannerRudeTalkingAddsAttitude` | teach / manner / rude / talking / adds / attitude | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / rude / talking / adds / attitude | 良い | - | - |
| `testTeachMannerNoConditionNoAttitudeChange` | teach / manner / なし / condition / なし / attitude / change | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / なし / condition / なし / attitude / change | 良い | - | - |
| `testTeachMannerDisciplineAlwaysApplied` | teach / manner / discipline / always / applied | ゆっくり本体の状態/行動/イベント回帰 / teach / manner / discipline / always / applied | 良い | - | - |
| `testNoticeNoOkazariSetsStress` | notice / なし / okazari / sets / ストレス | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / sets / ストレス | 良い | - | - |
| `testNoticeNoOkazariWithOkazariDoesNothing` | notice / なし / okazari / with / okazari / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / with / okazari / does / nothing | 良い | - | - |
| `testNoticeNoOkazariAlreadyNoticedDoesNothing` | notice / なし / okazari / already / noticed / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / already / noticed / does / nothing | 良い | - | - |
| `testNoticeNoOkazariSleepingDoesNothing` | notice / なし / okazari / sleeping / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / sleeping / does / nothing | 良い | - | - |
| `testNoticeNoOkazariDeadDoesNothing` | notice / なし / okazari / 死亡 / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / notice / なし / okazari / 死亡 / does / nothing | 良い | - | - |
| `testRapidShitAddsBoost` | rapid / shit / adds / boost | ゆっくり本体の状態/行動/イベント回帰 / 排泄進行速度回帰 | 良い | - | - |
| `testRapidShitAccumulates` | rapid / shit / accumulates | ゆっくり本体の状態/行動/イベント回帰 / 排泄進行速度回帰 | 良い | - | - |
| `testRapidExcitingDisciplineCountdown` | rapid / exciting / discipline / countdown | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testRapidExcitingDisciplineZeroNoChange` | rapid / exciting / discipline / zero / なし / change | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testRapidExcitingDisciplineNegativeNoChange` | rapid / exciting / discipline / negative / なし / change | ゆっくり本体の状態/行動/イベント回帰 / 興奮/しつけの早期減衰回帰 | 良い | - | - |
| `testBodyCutSetsCriticalDamage` | 本体 / cut / sets / critical / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 本体 / cut / sets / critical / ダメージ | 良い | - | - |
| `testBodyCutClearsActions` | 本体 / cut / clears / actions | ゆっくり本体の状態/行動/イベント回帰 / 本体 / cut / clears / actions | 良い | - | - |
| `testBodyInjureSetsInjured` | 本体 / injure / sets / injured | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / sets / injured | 良い | - | - |
| `testBodyInjureSkipsWhenAlreadyCut` | 本体 / injure / skips / when / already / cut | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / skips / when / already / cut | 良い | - | - |
| `testBodyInjureSetsVerySad` | 本体 / injure / sets / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / sets / very / sad | 良い | - | - |
| `testLockSetZforceZeroDoesNothing` | lock / 設定 / zforce / zero / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / zforce / zero / does / nothing | 良い | - | - |
| `testLockSetZdeadOnlySetsForce` | lock / 設定 / zdead / only / sets / force | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / zdead / only / sets / force | 良い | - | assert:0 |
| `testReleaseLockNobinobiExtForceZeroDoesNothing` | release / lock / nobinobi / ext / force / zero / does / nothing | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReleaseLockNobinobiNegativeResetsForce` | release / lock / nobinobi / negative / resets / force | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testLockSetZnegativeCrushCausesDeath` | lock / 設定 / znegative / crush / causes / death | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / znegative / crush / causes / death | 良い | - | - |
| `testLockSetZnegativeLimitMessageNoVomit` | lock / 設定 / znegative / limit / メッセージ / なし / vomit | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / znegative / limit / メッセージ / なし / vomit | 良い | - | - |
| `testLockSetZpositiveCut` | lock / 設定 / zpositive / cut | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / zpositive / cut | 良い | - | - |
| `testLockSetZpositiveLimitMessage` | lock / 設定 / zpositive / limit / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / lock / 設定 / zpositive / limit / メッセージ | 良い | - | - |
| `testToStringContainsName` | to / string / contains / name | ゆっくり本体の状態/行動/イベント回帰 / to / string / contains / name | 良い | - | - |
| `testToStringContainsAgeState` | to / string / contains / age / state | ゆっくり本体の状態/行動/イベント回帰 / to / string / contains / age / state | 良い | - | - |
| `testDecideDirectionTargetIsRight` | decide / direction / target / 状態 / right | ゆっくり本体の状態/行動/イベント回帰 / decide / direction / target / 状態 / right | 良い | - | - |
| `testDecideDirectionTargetIsLeft` | decide / direction / target / 状態 / left | ゆっくり本体の状態/行動/イベント回帰 / decide / direction / target / 状態 / left | 良い | - | - |
| `testDecideDirectionWithinRange` | decide / direction / within / range | ゆっくり本体の状態/行動/イベント回帰 / decide / direction / within / range | 良い | - | - |
| `testDecideDirectionExactlyAtRange` | decide / direction / exactly / at / range | ゆっくり本体の状態/行動/イベント回帰 / decide / direction / exactly / at / range | 良い | - | - |
| `testLookToRight` | look / to / right | ゆっくり本体の状態/行動/イベント回帰 / look / to / right | 良い | - | - |
| `testLookToLeft` | look / to / left | ゆっくり本体の状態/行動/イベント回帰 / look / to / left | 良い | - | - |
| `testLookToSamePosition` | look / to / same / position | ゆっくり本体の状態/行動/イベント回帰 / look / to / same / position | 良い | - | - |
| `testLookToWhenDead` | look / to / when / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / look / to / when / 死亡 | 良い | - | - |
| `testLookToWhenSleeping` | look / to / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / look / to / when / sleeping | 良い | - | - |
| `testDoPurupuruFirstCall` | do / purupuru / first / call | ゆっくり本体の状態/行動/イベント回帰 / do / purupuru / first / call | 良い | - | - |
| `testDoPurupuruSecondCallToggleOff` | do / purupuru / second / call / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / do / purupuru / second / call / 切替 / off | 良い | - | - |
| `testIsSuperRaperNormal` | 状態 / super / raper / normal | ゆっくり本体の状態/行動/イベント回帰 / 状態 / super / raper / normal | 良い | - | - |
| `testIsSuperRaperWhenUnbirth` | 状態 / super / raper / when / unbirth | ゆっくり本体の状態/行動/イベント回帰 / 状態 / super / raper / when / unbirth | 良い | - | - |
| `testIsSuperRaperWhenPenipeniCutted` | 状態 / super / raper / when / penipeni / cutted | ゆっくり本体の状態/行動/イベント回帰 / 状態 / super / raper / when / penipeni / cutted | 良い | - | - |
| `testSetSuperRaperNormal` | 設定 / super / raper / normal | ゆっくり本体の状態/行動/イベント回帰 / 設定 / super / raper / normal | 良い | - | - |
| `testSetSuperRaperWhenPenipeniCutted` | 設定 / super / raper / when / penipeni / cutted | ゆっくり本体の状態/行動/イベント回帰 / 設定 / super / raper / when / penipeni / cutted | 良い | - | - |
| `testInvPheromoneToggleOn` | inv / pheromone / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / inv / pheromone / 切替 / on | 良い | - | - |
| `testInvPheromoneToggleOff` | inv / pheromone / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / inv / pheromone / 切替 / off | 良い | - | - |
| `testCanActionForEventNormal` | 可否 / action / for / イベント / normal | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenDead` | 可否 / action / for / イベント / when / 死亡 | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenCut` | 可否 / action / for / イベント / when / cut | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenPealed` | 可否 / action / for / イベント / when / pealed | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenPacked` | 可否 / action / for / イベント / when / packed | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenSleeping` | 可否 / action / for / イベント / when / sleeping | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenShitting` | 可否 / action / for / イベント / when / shitting | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenNyd` | 可否 / action / for / イベント / when / 非ゆっくり症 | 生存/行動可否回帰 | 良い | - | - |
| `testCanActionForEventWhenBaryNotNone` | 可否 / action / for / イベント / when / bary / 非 / none | 生存/行動可否回帰 | 良い | - | - |
| `testClearEventWhenNoEvent` | 解除 / イベント / when / なし / イベント | ゆっくり本体の状態/行動/イベント回帰 / 解除 / イベント / when / なし / イベント | 良い | - | - |
| `testClearEventResetsForceFace` | 解除 / イベント / resets / force / 表情 | ゆっくり本体の状態/行動/イベント回帰 / 解除 / イベント / resets / force / 表情 | 良い | - | - |
| `testRemoveStalkSetsVerySadWhenAlive` | 除去 / stalk / sets / very / sad / when / alive | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveStalkWhenDead` | 除去 / stalk / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveStalkRemovesFromList` | 除去 / stalk / removes / from / list | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRemoveStalkClearsParentTrackingButKeepsChildBoundToStalk` | 除去 / stalk / clears / 親 / tracking / but / 維持 / 子 / bound / to / stalk | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testCheckTangNegativeClampedToZero` | 判定 / tang / negative / clamped / to / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / tang / negative / clamped / to / zero | 良い | - | - |
| `testCheckTangOverMaxClampedToMax` | 判定 / tang / over / max / clamped / to / max | ゆっくり本体の状態/行動/イベント回帰 / 判定 / tang / over / max / clamped / to / max | 良い | - | - |
| `testCheckTangWithinRangeUnchanged` | 判定 / tang / within / range / unchanged | ゆっくり本体の状態/行動/イベント回帰 / 判定 / tang / within / range / unchanged | 良い | - | - |
| `testToleranceReturnValue` | tolerance / 戻り / value | ゆっくり本体の状態/行動/イベント回帰 / tolerance / 戻り / value | 良い | - | - |
| `testToleranceFoolBranch` | tolerance / fool / branch | ゆっくり本体の状態/行動/イベント回帰 / tolerance / fool / branch | 良い | - | - |
| `testToleranceWiseBranch` | tolerance / wise / branch | ゆっくり本体の状態/行動/イベント回帰 / tolerance / wise / branch | 良い | - | - |
| `testToleranceBabyBranch` | tolerance / baby / branch | ゆっくり本体の状態/行動/イベント回帰 / tolerance / baby / branch | 良い | - | - |
| `testToleranceChildBranch` | tolerance / 子 / branch | ゆっくり本体の状態/行動/イベント回帰 / tolerance / 子 / branch | 良い | - | - |
| `testGetExpandSizeHdefault` | 取得 / expand / size / hdefault | ゆっくり本体の状態/行動/イベント回帰 / 取得 / expand / size / hdefault | 良い | - | - |
| `testResetUnyo` | reset / unyo | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testResetUnyoAlreadyZero` | reset / unyo / already / zero | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testConstraintDirectionAlignSame` | constraint / direction / align / same | ゆっくり本体の状態/行動/イベント回帰 / constraint / direction / align / same | 良い | - | - |
| `testConstraintDirectionFaceEachOtherBodyOnLeft` | constraint / direction / 表情 / each / other / 本体 / on / left | ゆっくり本体の状態/行動/イベント回帰 / constraint / direction / 表情 / each / other / 本体 / on / left | 良い | - | - |
| `testConstraintDirectionFaceEachOtherBodyOnRight` | constraint / direction / 表情 / each / other / 本体 / on / right | ゆっくり本体の状態/行動/イベント回帰 / constraint / direction / 表情 / each / other / 本体 / on / right | 良い | - | - |
| `testGetInVainSetsBeVain` | 取得 / in / vain / sets / be / vain | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / sets / be / vain | 良い | - | - |
| `testGetInVainReducesStress` | 取得 / in / vain / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / reduces / ストレス | 良い | - | - |
| `testGetInVainWithMessage` | 取得 / in / vain / with / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / with / メッセージ | 良い | - | - |
| `testTouchStalkSetsSad` | touch / stalk / sets / sad | ゆっくり本体の状態/行動/イベント回帰 / touch / stalk / sets / sad | 良い | - | - |
| `testSetNydforceFaceWhenNotNyd` | 設定 / nydforce / 表情 / when / 非 / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 設定 / nydforce / 表情 / when / 非 / 非ゆっくり症 | 良い | - | - |
| `testSetNydforceFaceWhenPealed` | 設定 / nydforce / 表情 / when / pealed | ゆっくり本体の状態/行動/イベント回帰 / 設定 / nydforce / 表情 / when / pealed | 良い | - | - |
| `testSetNydforceFaceWhenNydandNotPealed` | 設定 / nydforce / 表情 / when / nydand / 非 / pealed | ゆっくり本体の状態/行動/イベント回帰 / 設定 / nydforce / 表情 / when / nydand / 非 / pealed | 良い | - | - |
| `testInvBodyCastrationToggleOn` | inv / 本体 / castration / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / inv / 本体 / castration / 切替 / on | 良い | - | - |
| `testInvBodyCastrationToggleOff` | inv / 本体 / castration / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / inv / 本体 / castration / 切替 / off | 良い | - | - |
| `testInvAnalCloseToggleOn` | inv / anal / close / 切替 / on | ゆっくり本体の状態/行動/イベント回帰 / inv / anal / close / 切替 / on | 良い | - | - |
| `testInvAnalCloseToggleOff` | inv / anal / close / 切替 / off | ゆっくり本体の状態/行動/イベント回帰 / inv / anal / close / 切替 / off | 良い | - | - |
| `testCalcMoveTargetNullTarget` | calc / 移動 / target / null / target | ゆっくり本体の状態/行動/イベント回帰 / calc / 移動 / target / null / target | 良い | - | assert:0 |
| `testMoveToBed` | 移動 / to / bed | ゆっくり本体の状態/行動/イベント回帰 / 移動 / to / bed | 良い | - | - |
| `testCheckEmotionFootbakeNone` | 判定 / emotion / footbake / none | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / none | 良い | - | - |
| `testCheckEmotionFootbakeSukkiri` | 判定 / emotion / footbake / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / sukkiri | 良い | - | - |
| `testCheckEmotionFootbakeSleeping` | 判定 / emotion / footbake / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / sleeping | 良い | - | - |
| `testCheckEmotionFootbakeMidium` | 判定 / emotion / footbake / midium | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / midium | 良い | - | - |
| `testCheckEmotionFootbakeCritical` | 判定 / emotion / footbake / critical | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / critical | 良い | - | - |
| `testCheckEmotionNoOkazariPikopikoHasBoth` | 判定 / emotion / なし / okazari / pikopiko / 有無 / both | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / なし / okazari / pikopiko / 有無 / both | 良い | - | - |
| `testCheckEmotionNoOkazariPikopikoSukkiri` | 判定 / emotion / なし / okazari / pikopiko / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / なし / okazari / pikopiko / sukkiri | 良い | - | - |
| `testCheckEmotionNoOkazariPikopikoSleeping` | 判定 / emotion / なし / okazari / pikopiko / sleeping | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / なし / okazari / pikopiko / sleeping | 良い | - | - |
| `testCheckEmotionNoOkazariPikopikoMissing` | 判定 / emotion / なし / okazari / pikopiko / missing | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / なし / okazari / pikopiko / missing | 良い | - | - |
| `testCheckFearNyd` | 判定 / fear / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / fear / 非ゆっくり症 | 良い | - | - |
| `testCheckFearNormal` | 判定 / fear / normal | ゆっくり本体の状態/行動/イベント回帰 / 判定 / fear / normal | 良い | - | - |
| `testCheckFearExceedsPeriod` | 判定 / fear / exceeds / period | ゆっくり本体の状態/行動/イベント回帰 / 判定 / fear / exceeds / period | 良い | - | - |
| `testGetBoundaryShape` | 取得 / boundary / shape | ゆっくり本体の状態/行動/イベント回帰 / 取得 / boundary / shape | 良い | - | - |
| `testUpdateSpriteSize` | 更新 / sprite / size | ゆっくり本体の状態/行動/イベント回帰 / 更新 / sprite / size | 良い | - | - |
| `testCheckStressNegative` | 判定 / ストレス / negative | 代謝/状態フラグ回帰 | 良い | - | - |
| `testCheckStressPositiveUnchanged` | 判定 / ストレス / positive / unchanged | 代謝/状態フラグ回帰 | 良い | - | - |
| `testAddSickPeriod` | 追加 / 病気 / period | ゆっくり本体の状態/行動/イベント回帰 / 病気期間増加回帰 | 良い | - | - |
| `testEqualsSameUniqueId` | equals / same / unique / id | ゆっくり本体の状態/行動/イベント回帰 / equals / same / unique / id | 良い | - | - |
| `testEqualsNull` | equals / null | ゆっくり本体の状態/行動/イベント回帰 / equals / null | 良い | - | - |
| `testEqualsNonBody` | equals / non / 本体 | ゆっくり本体の状態/行動/イベント回帰 / equals / non / 本体 | 良い | - | - |
| `testEqualsDifferentBody` | equals / different / 本体 | ゆっくり本体の状態/行動/イベント回帰 / equals / different / 本体 | 良い | - | - |
| `testCompareToNull` | compare / to / null | ゆっくり本体の状態/行動/イベント回帰 / compare / to / null | 良い | - | - |
| `testCompareToNonBody` | compare / to / non / 本体 | ゆっくり本体の状態/行動/イベント回帰 / compare / to / non / 本体 | 良い | - | - |
| `testCompareToDifferentBody` | compare / to / different / 本体 | ゆっくり本体の状態/行動/イベント回帰 / compare / to / different / 本体 | 良い | - | - |
| `testAddAmaamaDisciplineNormal` | 追加 / amaama / discipline / normal | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline / normal | 良い | - | - |
| `testAddAmaamaDisciplineClampUpper` | 追加 / amaama / discipline / 範囲補正 / upper | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline / 範囲補正 / upper | 良い | - | - |
| `testAddAmaamaDisciplineClampLower` | 追加 / amaama / discipline / 範囲補正 / lower | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amaama / discipline / 範囲補正 / lower | 良い | - | - |
| `testAddAmountPositive` | 追加 / amount / positive | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / positive | 良い | - | - |
| `testAddAmountDepleted` | 追加 / amount / depleted | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / depleted | 良い | - | - |
| `testAddAmountIncrease` | 追加 / amount / increase | ゆっくり本体の状態/行動/イベント回帰 / 追加 / amount / increase | 良い | - | - |
| `testAddChildrenListLazyInit` | 追加 / children / list / lazy / init | ゆっくり本体の状態/行動/イベント回帰 / 追加 / children / list / lazy / init | 良い | - | - |
| `testAddChildrenListNull` | 追加 / children / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / children / list / null | 良い | - | assert:0 |
| `testAddElderSisterList` | 追加 / elder / 姉妹 / list | ゆっくり本体の状態/行動/イベント回帰 / 追加 / elder / 姉妹 / list | 良い | - | - |
| `testAddElderSisterListNull` | 追加 / elder / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / elder / 姉妹 / list / null | 良い | - | - |
| `testAddSisterList` | 追加 / 姉妹 / list | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 姉妹 / list | 良い | - | - |
| `testAddSisterListNull` | 追加 / 姉妹 / list / null | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 姉妹 / list / null | 良い | - | - |
| `testWillingFurifuriNotRude` | willing / furifuri / 非 / rude | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / 非 / rude | 良い | - | - |
| `testWillingFurifuriCriticalFoot` | willing / furifuri / critical / foot | ゆっくり本体の状態/行動/イベント回帰 / willing / furifuri / critical / foot | 良い | - | - |
| `testInitAmountAdult` | init / amount / adult | ゆっくり本体の状態/行動/イベント回帰 / init / amount / adult | 良い | - | - |
| `testInitAmountBaby` | init / amount / baby | ゆっくり本体の状態/行動/イベント回帰 / init / amount / baby | 良い | - | - |
| `testAddBodyBakePeriod` | 追加 / 本体 / bake / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / 本体 / bake / period | 良い | - | - |
| `testAddDirtyPeriod` | 追加 / dirty / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / dirty / period | 良い | - | - |
| `testAddFootBakePeriod` | 追加 / foot / bake / period | ゆっくり本体の状態/行動/イベント回帰 / 追加 / foot / bake / period | 良い | - | - |
| `testAddTang` | 追加 / tang | ゆっくり本体の状態/行動/イベント回帰 / 追加 / tang | 良い | - | - |
| `testClearTargets` | 解除 / targets | ゆっくり本体の状態/行動/イベント回帰 / 解除 / targets | 良い | - | - |
| `testStopStaying` | stop / staying | ゆっくり本体の状態/行動/イベント回帰 / stop / staying | 良い | - | - |
| `testCutHair` | cut / 毛 | ゆっくり本体の状態/行動/イベント回帰 / cut / 毛 | 良い | - | - |
| `testTakePants` | take / pants | ゆっくり本体の状態/行動/イベント回帰 / take / pants | 良い | - | - |
| `testRemoveTakeoutItem` | 除去 / takeout / item | 感情/復活/所持/拘束回帰 | 良い | - | assert:0 |
| `testRemoveFavItem` | 除去 / fav / item | 感情/復活/所持/拘束回帰 | 良い | - | assert:0 |
| `testBaseline` | baseline | ゆっくり本体の状態/行動/イベント回帰 / baseline | 良い | - | - |
| `testUnunSlaveBonus` | unun / slave / bonus | ゆっくり本体の状態/行動/イベント回帰 / unun / slave / bonus | 良い | - | - |
| `testIntelligenceWiseBonus` | intelligence / wise / bonus | ゆっくり本体の状態/行動/イベント回帰 / intelligence / wise / bonus | 良い | - | - |
| `testIntelligenceFoolBonus` | intelligence / fool / bonus | ゆっくり本体の状態/行動/イベント回帰 / intelligence / fool / bonus | 良い | - | - |
| `testAttitudeVeryNiceBonus` | attitude / very / nice / bonus | ゆっくり本体の状態/行動/イベント回帰 / attitude / very / nice / bonus | 良い | - | - |
| `testAttitudeSuperShitheadBonus` | attitude / super / shithead / bonus | ゆっくり本体の状態/行動/イベント回帰 / attitude / super / shithead / bonus | 良い | - | - |
| `testRapistBonus` | rapist / bonus | ゆっくり本体の状態/行動/イベント回帰 / rapist / bonus | 良い | - | - |
| `testChildAgeBonus` | 子 / age / bonus | ゆっくり本体の状態/行動/イベント回帰 / 子 / age / bonus | 良い | - | - |
| `testBabyAgeBonus` | baby / age / bonus | ゆっくり本体の状態/行動/イベント回帰 / baby / age / bonus | 良い | - | - |
| `testSickPenalty` | 病気 / penalty | ゆっくり本体の状態/行動/イベント回帰 / 病気 / penalty | 良い | - | - |
| `testNoOkazariPenalty` | なし / okazari / penalty | ゆっくり本体の状態/行動/イベント回帰 / なし / okazari / penalty | 良い | - | - |
| `testNoBraidPenalty` | なし / おさげ / penalty | ゆっくり本体の状態/行動/イベント回帰 / なし / おさげ / penalty | 良い | - | - |
| `testBlindPenalty` | blind / penalty | ゆっくり本体の状態/行動/イベント回帰 / blind / penalty | 良い | - | - |
| `testShutmouthPenalty` | shutmouth / penalty | ゆっくり本体の状態/行動/イベント回帰 / shutmouth / penalty | 良い | - | - |
| `testInjuredPenalty` | injured / penalty | ゆっくり本体の状態/行動/イベント回帰 / injured / penalty | 良い | - | - |
| `testDirtyPenalty` | dirty / penalty | ゆっくり本体の状態/行動/イベント回帰 / dirty / penalty | 良い | - | - |
| `testLockmovePenalty` | lockmove / penalty | ゆっくり本体の状態/行動/イベント回帰 / lockmove / penalty | 良い | - | - |
| `testPenipeniCuttedPenalty` | penipeni / cutted / penalty | ゆっくり本体の状態/行動/イベント回帰 / penipeni / cutted / penalty | 良い | - | - |
| `testMemoriesAdded` | memories / added | ゆっくり本体の状態/行動/イベント回帰 / memories / added | 良い | - | - |
| `testMinimumClamp` | minimum / 範囲補正 | ゆっくり本体の状態/行動/イベント回帰 / minimum / 範囲補正 | 良い | - | - |
| `testMultiplePenalties` | multiple / penalties | ゆっくり本体の状態/行動/イベント回帰 / multiple / penalties | 良い | - | - |
| `testChildAliveBonus` | 子 / alive / bonus | ゆっくり本体の状態/行動/イベント回帰 / 子 / alive / bonus | 良い | - | - |
| `testChildDeadPenalty` | 子 / 死亡 / penalty | ゆっくり本体の状態/行動/イベント回帰 / 子 / 死亡 / penalty | 良い | - | - |
| `testReturnsEarlyWhenDead` | 戻り / early / when / 死亡 | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsEarlyWhenPanicking` | 戻り / early / when / panicking | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsEarlyWhenCantAction` | 戻り / early / when / cant / action | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testType0SetsRelax` | type0 / sets / relax | ゆっくり本体の状態/行動/イベント回帰 / type0 / sets / relax | 良い | - | - |
| `testType0ReducesStress` | type0 / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / type0 / reduces / ストレス | 良い | - | - |
| `testType0AddsLovePlayer` | type0 / adds / love / player | ゆっくり本体の状態/行動/イベント回帰 / type0 / adds / love / player | 良い | - | - |
| `testType1SetsAngry` | type1 / sets / angry | ゆっくり本体の状態/行動/イベント回帰 / type1 / sets / angry | 良い | - | - |
| `testType1AddsStress` | type1 / adds / ストレス | ゆっくり本体の状態/行動/イベント回帰 / type1 / adds / ストレス | 良い | - | - |
| `testType2SetsFurifuri` | type2 / sets / furifuri | ゆっくり本体の状態/行動/イベント回帰 / type2 / sets / furifuri | 良い | - | - |
| `testType2ReducesStress` | type2 / reduces / ストレス | ゆっくり本体の状態/行動/イベント回帰 / type2 / reduces / ストレス | 良い | - | - |
| `testReturnsFalseWhenNotLockmove` | 戻り / false / when / 非 / lockmove | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenSukkiri` | 戻り / false / when / sukkiri | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenSleeping` | 戻り / false / when / sleeping | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenHasCurrentEvent` | 戻り / false / when / 有無 / current / イベント | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testIncrementsPeriodWhenLockmove` | increments / period / when / lockmove | ゆっくり本体の状態/行動/イベント回帰 / increments / period / when / lockmove | 良い | - | - |
| `testReturnsFalseWhenTalking` | 戻り / false / when / talking | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenFootBakeCritical` | 戻り / false / when / foot / bake / critical | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenSukkiri` | 戻り / false / when / sukkiri | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenEating` | 戻り / false / when / eating | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenPeropero` | 戻り / false / when / peropero | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenPacked` | 戻り / false / when / packed | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenRapistExciting` | 戻り / false / when / rapist / exciting | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenHighPriorityEvent` | 戻り / false / when / high / priority / イベント | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnsFalseWhenMiddlePriorityEvent` | 戻り / false / when / middle / priority / イベント | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testShitAccumulatesWhenRndZero` | shit / accumulates / when / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / shit / accumulates / when / rnd / zero | 良い | - | - |
| `testShitAccumulatesFasterWhenFull` | shit / accumulates / faster / when / full | ゆっくり本体の状態/行動/イベント回帰 / shit / accumulates / faster / when / full | 良い | - | - |
| `testSleepingHoldsShitBelowLimit` | sleeping / holds / shit / below / limit | ゆっくり本体の状態/行動/イベント回帰 / sleeping / holds / shit / below / limit | 良い | - | - |
| `testLockmoveLeaksWhenShitNearLimit` | lockmove / leaks / when / shit / near / limit | ゆっくり本体の状態/行動/イベント回帰 / lockmove / leaks / when / shit / near / limit | 良い | - | - |
| `testUnbirthWithoutAmpouleReturnsTrue` | unbirth / without / ampoule / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / unbirth / without / ampoule / 戻り / true | 良い | - | - |
| `testUnbirthAmpouleAnalClosedAddsShitWhenNotNearBurst` | unbirth / ampoule / anal / closed / adds / shit / when / 非 / near / 破裂 | ゆっくり本体の状態/行動/イベント回帰 / unbirth / ampoule / anal / closed / adds / shit / when / 非 / near / 破裂 | 良い | - | - |
| `testUnbirthAmpouleAnalClosedDoesNotAddWhenNearBurst` | unbirth / ampoule / anal / closed / does / 非 / 追加 / when / near / 破裂 | ゆっくり本体の状態/行動/イベント回帰 / unbirth / ampoule / anal / closed / does / 非 / 追加 / when / near / 破裂 | 良い | - | - |
| `testUnbirthAmpouleNoAnalCloseHasPants` | unbirth / ampoule / なし / anal / close / 有無 / pants | ゆっくり本体の状態/行動/イベント回帰 / unbirth / ampoule / なし / anal / close / 有無 / pants | 良い | - | - |
| `testUnbirthAmpouleNoAnalCloseNoPants` | unbirth / ampoule / なし / anal / close / なし / pants | ゆっくり本体の状態/行動/イベント回帰 / unbirth / ampoule / なし / anal / close / なし / pants | 良い | - | - |
| `testUnbirthAmpouleBelowLimitAccumulates` | unbirth / ampoule / below / limit / accumulates | ゆっくり本体の状態/行動/イベント回帰 / unbirth / ampoule / below / limit / accumulates | 良い | - | - |
| `testUnunSlaveUsesHigherAccumulationRate` | unun / slave / uses / higher / accumulation / rate | ゆっくり本体の状態/行動/イベント回帰 / unun / slave / uses / higher / accumulation / rate | 良い | - | - |
| `testNearLimitSetsShittingWhenAnalOpen` | near / limit / sets / shitting / when / anal / open | ゆっくり本体の状態/行動/イベント回帰 / near / limit / sets / shitting / when / anal / open | 良い | - | - |
| `testOverLimitAnalClosedIncreasesShit` | over / limit / anal / closed / increases / shit | ゆっくり本体の状態/行動/イベント回帰 / over / limit / anal / closed / increases / shit | 良い | - | - |
| `testOverLimitBabyAnalOpenMakesDirty` | over / limit / baby / anal / open / makes / dirty | ゆっくり本体の状態/行動/イベント回帰 / over / limit / baby / anal / open / makes / dirty | 良い | - | - |
| `testToiletArrivalSetsShitToMinimum` | toilet / arrival / sets / shit / to / minimum | ゆっくり本体の状態/行動/イベント回帰 / toilet / arrival / sets / shit / to / minimum | 良い | - | - |
| `testBedWithToiletNotHitHoldsShit` | bed / with / toilet / 非 / hit / holds / shit | ゆっくり本体の状態/行動/イベント回帰 / bed / with / toilet / 非 / hit / holds / shit | 良い | - | - |
| `testKindAdultHoldsShitWhenToiletNotReached` | kind / adult / holds / shit / when / toilet / 非 / reached | ゆっくり本体の状態/行動/イベント回帰 / kind / adult / holds / shit / when / toilet / 非 / reached | 良い | - | - |
| `testOverLimitAnalClosedBurstNearSetsMessage` | over / limit / anal / closed / 破裂 / near / sets / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / over / limit / anal / closed / 破裂 / near / sets / メッセージ | 良い | - | - |
| `testOverLimitAnalClosedNotNearSetsMessage` | over / limit / anal / closed / 非 / near / sets / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / over / limit / anal / closed / 非 / near / sets / メッセージ | 良い | - | - |
| `testOverLimitWithPantsOrNydmakesDirty` | over / limit / with / pants / or / nydmakes / dirty | ゆっくり本体の状態/行動/イベント回帰 / over / limit / with / pants / or / nydmakes / dirty | 良い | - | - |
| `testOverLimitAnalOpenTriggersFurifuriAndStressDecrease` | over / limit / anal / open / triggers / furifuri / and / ストレス / decrease | ゆっくり本体の状態/行動/イベント回帰 / over / limit / anal / open / triggers / furifuri / and / ストレス / decrease | 良い | - | - |
| `testOverLimitShitBoostDecrements` | over / limit / shit / boost / decrements | ゆっくり本体の状態/行動/イベント回帰 / over / limit / shit / boost / decrements | 良い | - | - |
| `testAngryPeriodExpires` | angry / period / expires | ゆっくり本体の状態/行動/イベント回帰 / angry / period / expires | 良い | - | - |
| `testAngryPeriodNotYetExpired` | angry / period / 非 / yet / expired | ゆっくり本体の状態/行動/イベント回帰 / angry / period / 非 / yet / expired | 良い | - | - |
| `testScarePeriodExpires` | scare / period / expires | ゆっくり本体の状態/行動/イベント回帰 / scare / period / expires | 良い | - | - |
| `testCheckEmotionBlindBranch` | 判定 / emotion / blind / branch | ゆっくり本体の状態/行動/イベント回帰 / 盲目時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionCantSpeakBranch` | 判定 / emotion / cant / speak / branch | ゆっくり本体の状態/行動/イベント回帰 / 口封じ時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionLockmoveBranch` | 判定 / emotion / lockmove / branch | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionFootbakeBranch` | 判定 / emotion / footbake / branch | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / branch | 良い | - | - |
| `testVerySadSadPeriodResets` | very / sad / sad / period / resets | ゆっくり本体の状態/行動/イベント回帰 / very / sad / sad / period / resets | 良い | - | - |
| `testVerySadStays` | very / sad / stays | ゆっくり本体の状態/行動/イベント回帰 / very / sad / stays | 良い | - | - |
| `testPlayingStopsWhenSleeping` | playing / stops / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / playing / stops / when / sleeping | 良い | - | - |
| `testPlayingStopsWhenLimitNegativeSui` | playing / stops / when / limit / negative / sui | ゆっくり本体の状態/行動/イベント回帰 / playing / stops / when / limit / negative / sui | 良い | - | - |
| `testPlayingStopsWhenLimitNegativeTrampoline` | playing / stops / when / limit / negative / trampoline | ゆっくり本体の状態/行動/イベント回帰 / playing / stops / when / limit / negative / trampoline | 良い | - | - |
| `testCheckEmotionReturnsWhenNonYukkuriDisease` | 判定 / emotion / 戻り / when / non / yukkuri / disease | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / 戻り / when / non / yukkuri / disease | 良い | - | - |
| `testCheckEmotionReturnsWhenEventActive` | 判定 / emotion / 戻り / when / イベント / active | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / 戻り / when / イベント / active | 良い | - | - |
| `testCheckEmotionReturnsWhenSurisuriByPlayer` | 判定 / emotion / 戻り / when / surisuri / by / player | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / 戻り / when / surisuri / by / player | 良い | - | - |
| `testYunnyaaSetsVerySadAndStays` | yunnyaa / sets / very / sad / and / stays | ゆっくり本体の状態/行動/イベント回帰 / yunnyaa / sets / very / sad / and / stays | 良い | - | - |
| `testDamagedOnBeltConveyorBegForLifeAddsEvent` | damaged / on / belt / conveyor / beg / for / life / adds / イベント | ゆっくり本体の状態/行動/イベント回帰 / damaged / on / belt / conveyor / beg / for / life / adds / イベント | 良い | - | - |
| `testDamagedOnBeltConveyorYunnyaaBranch` | damaged / on / belt / conveyor / yunnyaa / branch | ゆっくり本体の状態/行動/イベント回帰 / damaged / on / belt / conveyor / yunnyaa / branch | 良い | - | - |
| `testDamagedOnBeltConveyorKilledInFactoryMessage` | damaged / on / belt / conveyor / killed / in / factory / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / damaged / on / belt / conveyor / killed / in / factory / メッセージ | 良い | - | - |
| `testHungryTriggersStayWhenOkazariPresent` | 空腹 / triggers / stay / when / okazari / present | ゆっくり本体の状態/行動/イベント回帰 / 空腹 / triggers / stay / when / okazari / present | 良い | - | - |
| `testDirtyChildSetsScreamPeriod` | dirty / 子 / sets / scream / period | ゆっくり本体の状態/行動/イベント回帰 / dirty / 子 / sets / scream / period | 良い | - | - |
| `testReturnWhenCriticalCut` | 戻り / when / critical / cut | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testReturnWhenExciting` | 戻り / when / exciting | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testUnunSlaveReaction` | unun / slave / reaction | ゆっくり本体の状態/行動/イベント回帰 / unun / slave / reaction | 良い | - | - |
| `testDirtyAdultCleansItself` | dirty / adult / cleans / itself | ゆっくり本体の状態/行動/イベント回帰 / dirty / adult / cleans / itself | 良い | - | - |
| `testDirtyChildCallsParentWhenScreamPeriodSet` | dirty / 子 / calls / 親 / when / scream / period / 設定 | ゆっくり本体の状態/行動/イベント回帰 / dirty / 子 / calls / 親 / when / scream / period / 設定 | 良い | - | - |
| `testHungrySoHungrySetsSad` | 空腹 / so / 空腹 / sets / sad | ゆっくり本体の状態/行動/イベント回帰 / 空腹 / so / 空腹 / sets / sad | 良い | - | - |
| `testRelaxBranchNoPartnerSetsWantPartnerMessage` | relax / branch / なし / 相手 / sets / want / 相手 / メッセージ | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testNotDirtyResetsCallingParentsAndScreamPeriod` | 非 / dirty / resets / calling / parents / and / scream / period | ゆっくり本体の状態/行動/イベント回帰 / 非 / dirty / resets / calling / parents / and / scream / period | 良い | - | - |
| `testRelaxBranchPartnerExistsSetsExciting` | relax / branch / 相手 / exists / sets / exciting | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRelaxBranchWiseTooManyChildrenDoesNotExcite` | relax / branch / wise / too / many / children / does / 非 / excite | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRelaxBranchWiseThreeChildrenExcites` | relax / branch / wise / three / children / excites | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRelaxBranchAverageTooManyChildrenDoesNotExcite` | relax / branch / average / too / many / children / does / 非 / excite | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRelaxBranchAverageTenChildrenExcites` | relax / branch / average / ten / children / excites | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testRelaxBranchFoolAlwaysExcitesWithChildren` | relax / branch / fool / always / excites / with / children | 感情/復活/所持/拘束回帰 | 良い | - | - |
| `testDirtyChildSmartCleansItself` | dirty / 子 / smart / cleans / itself | ゆっくり本体の状態/行動/イベント回帰 / dirty / 子 / smart / cleans / itself | 良い | - | - |
| `testSurisuriByPlayerDefaultBranch` | surisuri / by / player / default / branch | ゆっくり本体の状態/行動/イベント回帰 / surisuri / by / player / default / branch | 良い | - | - |
| `testCheckEmotionLockmoveBranchTriggersAngry` | 判定 / emotion / lockmove / branch / triggers / angry | ゆっくり本体の状態/行動/イベント回帰 / 移動拘束時の感情/反応回帰 | 良い | - | - |
| `testCheckEmotionFootbakeCriticalBranchTriggersSad` | 判定 / emotion / footbake / critical / branch / triggers / sad | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / footbake / critical / branch / triggers / sad | 良い | - | - |
| `testCheckEmotionNoOkazariPikopikoBranchTriggersSad` | 判定 / emotion / なし / okazari / pikopiko / branch / triggers / sad | ゆっくり本体の状態/行動/イベント回帰 / 判定 / emotion / なし / okazari / pikopiko / branch / triggers / sad | 良い | - | - |
| `testGrabbedEarlyReturn` | grabbed / early / 戻り | 代謝/状態フラグ回帰 | 良い | - | - |
| `testDontMoveStopsMovement` | dont / 移動 / stops / movement | ゆっくり本体の状態/行動/イベント回帰 / dont / 移動 / stops / movement | 良い | - | - |
| `testLockmoveStopsMovement` | lockmove / stops / movement | ゆっくり本体の状態/行動/イベント回帰 / lockmove / stops / movement | 良い | - | - |
| `testPositionClampedToMapBoundsX` | position / clamped / to / map / bounds / x | ゆっくり本体の状態/行動/イベント回帰 / position / clamped / to / map / bounds / x | 良い | - | - |
| `testPositionClampedToMapBoundsY` | position / clamped / to / map / bounds / y | ゆっくり本体の状態/行動/イベント回帰 / position / clamped / to / map / bounds / y | 良い | - | - |
| `testFallBranchDecreasesZwhenAboveGround` | 落下 / branch / decreases / zwhen / above / ground | ゆっくり本体の状態/行動/イベント回帰 / 落下 / branch / decreases / zwhen / above / ground | 良い | - | - |
| `testLandingResetsVelocitiesAtMostDepth` | landing / resets / velocities / at / most / depth | ゆっくり本体の状態/行動/イベント回帰 / landing / resets / velocities / at / most / depth | 良い | - | - |
| `testExternalForceStopsMovementWhenBxByNonZero` | external / force / stops / movement / when / bx / by / non / zero | ゆっくり本体の状態/行動/イベント回帰 / external / force / stops / movement / when / bx / by / non / zero | 良い | - | - |
| `testStepFrequencySkipsMovementWhenAgeNotMultiple` | step / frequency / skips / movement / when / age / 非 / multiple | ゆっくり本体の状態/行動/イベント回帰 / step / frequency / skips / movement / when / age / 非 / multiple | 良い | - | - |
| `testDestXequalCurrentClearsDestX` | dest / xequal / current / clears / dest / x | ゆっくり本体の状態/行動/イベント回帰 / dest / xequal / current / clears / dest / x | 良い | - | - |
| `testRandomDirectionWhenNoDestAndCountThreshold` | random / direction / when / なし / dest / and / count / threshold | ゆっくり本体の状態/行動/イベント回帰 / random / direction / when / なし / dest / and / count / threshold | 良い | - | - |
| `testSpeedRemainderAddsExtraStep` | speed / remainder / adds / extra / step | ゆっくり本体の状態/行動/イベント回帰 / speed / remainder / adds / extra / step | 良い | - | - |
| `testRaperExcitingMovesFaster` | raper / exciting / moves / faster | ゆっくり本体の状態/行動/イベント回帰 / raper / exciting / moves / faster | 良い | - | - |
| `testBarrierCollisionStopsXmovement` | barrier / 衝突 / stops / xmovement | ゆっくり本体の状態/行動/イベント回帰 / barrier / 衝突 / stops / xmovement | 良い | - | - |
| `testBlockedCountResetsDestWhenWallHit` | blocked / count / resets / dest / when / 壁 / hit | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / resets / dest / when / 壁 / hit | 良い | - | - |
| `testPoolEntryAvoidedWhenNotLikeWater` | pool / entry / avoided / when / 非 / like / 水 | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / avoided / when / 非 / like / 水 | 良い | - | - |
| `testDestXpositiveNoOvershootMovesOne` | dest / xpositive / なし / overshoot / moves / one | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / なし / overshoot / moves / one | 良い | - | - |
| `testDestXpositiveOvershootClampsToDestExplicit` | dest / xpositive / overshoot / clamps / to / dest / explicit | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / overshoot / clamps / to / dest / explicit | 良い | - | - |
| `testDestYpositiveNoOvershootMovesOne` | dest / ypositive / なし / overshoot / moves / one | ゆっくり本体の状態/行動/イベント回帰 / dest / ypositive / なし / overshoot / moves / one | 良い | - | - |
| `testDestYpositiveOvershootClampsToDestExplicit` | dest / ypositive / overshoot / clamps / to / dest / explicit | ゆっくり本体の状態/行動/イベント回帰 / dest / ypositive / overshoot / clamps / to / dest / explicit | 良い | - | - |
| `testFallWhenMzZeroButDepthDiffers` | 落下 / when / mz / zero / but / depth / differs | ゆっくり本体の状態/行動/イベント回帰 / 落下 / when / mz / zero / but / depth / differs | 良い | - | - |
| `testFallWhenMzNonZeroEvenIfCanFly` | 落下 / when / mz / non / zero / even / if / 可否 / fly | ゆっくり本体の状態/行動/イベント回帰 / 落下 / when / mz / non / zero / even / if / 可否 / fly | 良い | - | - |
| `testNoDamageNextFallClearsWithVzAndVy` | なし / ダメージ / next / 落下 / clears / with / vz / and / vy | ゆっくり本体の状態/行動/イベント回帰 / なし / ダメージ / next / 落下 / clears / with / vz / and / vy | 良い | - | - |
| `testNoDamageNextFallNotClearedWhenDamageZero` | なし / ダメージ / next / 落下 / 非 / cleared / when / ダメージ / zero | ゆっくり本体の状態/行動/イベント回帰 / なし / ダメージ / next / 落下 / 非 / cleared / when / ダメージ / zero | 良い | - | - |
| `testStepNotHalvedMovesOnEvenAge` | step / 非 / halved / moves / on / even / age | ゆっくり本体の状態/行動/イベント回帰 / step / 非 / halved / moves / on / even / age | 良い | - | - |
| `testWallCollisionFoolPanicSetsMessage` | 壁 / 衝突 / fool / 恐慌 / sets / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / 壁 / 衝突 / fool / 恐慌 / sets / メッセージ | 良い | - | - |
| `testPoolEntryNotAvoidedWhenRandomZero` | pool / entry / 非 / avoided / when / random / zero | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / 非 / avoided / when / random / zero | 良い | - | - |
| `testMoveBodyXunderflowClampsAndAddsFallDamage` | 移動 / 本体 / xunderflow / clamps / and / adds / 落下 / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 移動 / 本体 / xunderflow / clamps / and / adds / 落下 / ダメージ | 良い | - | - |
| `testMoveBodyXoverflowClampsAndAddsFallDamage` | 移動 / 本体 / xoverflow / clamps / and / adds / 落下 / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 移動 / 本体 / xoverflow / clamps / and / adds / 落下 / ダメージ | 良い | - | - |
| `testMoveBodyYunderflowSetsDirYpositive` | 移動 / 本体 / yunderflow / sets / dir / ypositive | ゆっくり本体の状態/行動/イベント回帰 / 移動 / 本体 / yunderflow / sets / dir / ypositive | 良い | - | - |
| `testMoveBodyYoverflowSetsDirYnegative` | 移動 / 本体 / yoverflow / sets / dir / ynegative | ゆっくり本体の状態/行動/イベント回帰 / 移動 / 本体 / yoverflow / sets / dir / ynegative | 良い | - | - |
| `testFallLandingClearsNoDamageNextFall` | 落下 / landing / clears / なし / ダメージ / next / 落下 | ゆっくり本体の状態/行動/イベント回帰 / 落下 / landing / clears / なし / ダメージ / next / 落下 | 良い | - | - |
| `testFallLandingResetsFirstGroundFlag` | 落下 / landing / resets / first / ground / flag | ゆっくり本体の状態/行動/イベント回帰 / 落下 / landing / resets / first / ground / flag | 良い | - | - |
| `testFallLandingPealedBecomesDead` | 落下 / landing / pealed / becomes / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / 落下 / landing / pealed / becomes / 死亡 | 良い | - | - |
| `testCanFlySetsDestZtoFlyHeightWhenNoTarget` | 可否 / fly / sets / dest / zto / fly / height / when / なし / target | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / sets / dest / zto / fly / height / when / なし / target | 良い | - | - |
| `testDestXnegativeOvershootClampsToDest` | dest / xnegative / overshoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / dest / xnegative / overshoot / clamps / to / dest | 良い | - | - |
| `testBlockedCountHalfLimitSetsSadWhenFool` | blocked / count / half / limit / sets / sad / when / fool | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / half / limit / sets / sad / when / fool | 良い | - | - |
| `testPoolEntryAllowedWhenRndZero` | pool / entry / allowed / when / rnd / zero | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / allowed / when / rnd / zero | 良い | - | - |
| `testDirectionUpdatedFromDirX` | direction / updated / from / dir / x | ゆっくり本体の状態/行動/イベント回帰 / direction / updated / from / dir / x | 良い | - | - |
| `testSpeedRemainderDoesNotAddExtraStep` | speed / remainder / does / 非 / 追加 / extra / step | ゆっくり本体の状態/行動/イベント回帰 / speed / remainder / does / 非 / 追加 / extra / step | 良い | - | - |
| `testNoAccessoryMessageTriggeredOnRandomDirection` | なし / accessory / メッセージ / triggered / on / random / direction | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / triggered / on / random / direction | 良い | - | - |
| `testBlockedCountOverLimitUsesClearActionsForEvent` | blocked / count / over / limit / uses / 解除 / actions / for / イベント | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / uses / 解除 / actions / for / イベント | 良い | - | - |
| `testBlockedCountOverLimitNotFoolDoesNotSetVerySad` | blocked / count / over / limit / 非 / fool / does / 非 / 設定 / very / sad | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / 非 / fool / does / 非 / 設定 / very / sad | 良い | - | - |
| `testBlockedCountOverLimitFoolNoPanicDoesNotSetVerySad` | blocked / count / over / limit / fool / なし / 恐慌 / does / 非 / 設定 / very / sad | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / fool / なし / 恐慌 / does / 非 / 設定 / very / sad | 良い | - | - |
| `testBlockedCountOverLimitNotFoolDoesNotSetMessage` | blocked / count / over / limit / 非 / fool / does / 非 / 設定 / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / 非 / fool / does / 非 / 設定 / メッセージ | 良い | - | - |
| `testBlockedCountHalfLimitNoPanicDoesNotSetAngryOrSad` | blocked / count / half / limit / なし / 恐慌 / does / 非 / 設定 / angry / or / sad | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / half / limit / なし / 恐慌 / does / 非 / 設定 / angry / or / sad | 良い | - | - |
| `testBlockedCountHalfLimitAtThresholdDoesNothing` | blocked / count / half / limit / at / threshold / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / half / limit / at / threshold / does / nothing | 良い | - | - |
| `testBlockedCountHalfLimitNotFoolSkipsAngrySad` | blocked / count / half / limit / 非 / fool / skips / angry / sad | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / half / limit / 非 / fool / skips / angry / sad | 良い | - | - |
| `testBlockedCountHalfLimitRudeBecomesAngry` | blocked / count / half / limit / rude / becomes / angry | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / half / limit / rude / becomes / angry | 良い | - | - |
| `testFlyDestZovershootClampsToDest` | fly / dest / zovershoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / fly / dest / zovershoot / clamps / to / dest | 良い | - | - |
| `testFlyDestZupperClampsToDest` | fly / dest / zupper / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / fly / dest / zupper / clamps / to / dest | 良い | - | - |
| `testFallOnBedBabySkipsStrike` | 落下 / on / bed / baby / skips / 打撃 | ゆっくり本体の状態/行動/イベント回帰 / 落下 / on / bed / baby / skips / 打撃 | 良い | - | - |
| `testFlyingTypeWithoutBraidFalls` | flying / type / without / おさげ / falls | ゆっくり本体の状態/行動/イベント回帰 / flying / type / without / おさげ / falls | 良い | - | - |
| `testDestYequalCurrentClearsDestY` | dest / yequal / current / clears / dest / y | ゆっくり本体の状態/行動/イベント回帰 / dest / yequal / current / clears / dest / y | 良い | - | - |
| `testDestYovershootClampsToDest` | dest / yovershoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / dest / yovershoot / clamps / to / dest | 良い | - | - |
| `testWallHitWithoutDestRandomizesDirection` | 壁 / hit / without / dest / randomizes / direction | ゆっくり本体の状態/行動/イベント回帰 / 壁 / hit / without / dest / randomizes / direction | 良い | - | - |
| `testWallHitWithNoDestDoesNotSetBlockedCount` | 壁 / hit / with / なし / dest / does / 非 / 設定 / blocked / count | ゆっくり本体の状態/行動/イベント回帰 / 壁 / hit / with / なし / dest / does / 非 / 設定 / blocked / count | 良い | - | - |
| `testFallOnTrampolineCutsDamage` | 落下 / on / trampoline / cuts / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 落下 / on / trampoline / cuts / ダメージ | 良い | - | - |
| `testFlyDestZequalCurrentClearsDestZ` | fly / dest / zequal / current / clears / dest / z | ゆっくり本体の状態/行動/イベント回帰 / fly / dest / zequal / current / clears / dest / z | 良い | - | - |
| `testLinkParentPreventsMovement` | link / 親 / prevents / movement | ゆっくり本体の状態/行動/イベント回帰 / link / 親 / prevents / movement | 良い | - | - |
| `testYbarrierCollisionStopsYmovement` | ybarrier / 衝突 / stops / ymovement | ゆっくり本体の状態/行動/イベント回帰 / ybarrier / 衝突 / stops / ymovement | 良い | - | - |
| `testDestYpositiveOvershootClampsToDest` | dest / ypositive / overshoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / dest / ypositive / overshoot / clamps / to / dest | 良い | - | - |
| `testPoolEntryAvoidedWhenWiseAndRndNonZero` | pool / entry / avoided / when / wise / and / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / avoided / when / wise / and / rnd / non / zero | 良い | - | - |
| `testPoolEntryAvoidedWhenFoolAndRndNonZero` | pool / entry / avoided / when / fool / and / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / avoided / when / fool / and / rnd / non / zero | 良い | - | - |
| `testFallingUnderGroundSkipsFall` | falling / under / ground / skips / 落下 | ゆっくり本体の状態/行動/イベント回帰 / falling / under / ground / skips / 落下 | 良い | - | - |
| `testBindStalkPreventsFallWhenAboveGround` | bind / stalk / prevents / 落下 / when / above / ground | ゆっくり本体の状態/行動/イベント回帰 / bind / stalk / prevents / 落下 / when / above / ground | 良い | - | - |
| `testCanFlyAboveGroundDoesNotFallWhenDontMove` | 可否 / fly / above / ground / does / 非 / 落下 / when / dont / 移動 | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / above / ground / does / 非 / 落下 / when / dont / 移動 | 良い | - | - |
| `testBlockedCountOverLimitRandomizesYdirection` | blocked / count / over / limit / randomizes / ydirection | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / randomizes / ydirection | 良い | - | - |
| `testNoDamageNextFallNotClearedWhenFlagFalse` | なし / ダメージ / next / 落下 / 非 / cleared / when / flag / false | ゆっくり本体の状態/行動/イベント回帰 / なし / ダメージ / next / 落下 / 非 / cleared / when / flag / false | 良い | - | - |
| `testWallHitWithDestZincrementsBlockedCount` | 壁 / hit / with / dest / zincrements / blocked / count | ゆっくり本体の状態/行動/イベント回帰 / 壁 / hit / with / dest / zincrements / blocked / count | 良い | - | - |
| `testWallHitWithDestYincrementsBlockedCount` | 壁 / hit / with / dest / yincrements / blocked / count | ゆっくり本体の状態/行動/イベント回帰 / 壁 / hit / with / dest / yincrements / blocked / count | 良い | - | - |
| `testNoAccessoryMessageSuppressedByDiscipline` | なし / accessory / メッセージ / suppressed / by / discipline | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / suppressed / by / discipline | 良い | - | - |
| `testNoAccessoryMessageTriggeredOnYdirection` | なし / accessory / メッセージ / triggered / on / ydirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / triggered / on / ydirection | 良い | - | - |
| `testNoAccessoryMessageTriggersTalking` | なし / accessory / メッセージ / triggers / talking | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / triggers / talking | 良い | - | - |
| `testPoolEntryAllowedWhenLikeWater` | pool / entry / allowed / when / like / 水 | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / allowed / when / like / 水 | 良い | - | - |
| `testPoolEntryConditionFalseWhenAlreadyInPool` | pool / entry / condition / false / when / already / in / pool | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / condition / false / when / already / in / pool | 良い | - | - |
| `testStepHalvedWhenSick` | step / halved / when / 病気 | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / 病気 | 良い | - | - |
| `testStepHalvedWhenBlind` | step / halved / when / blind | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / blind | 良い | - | - |
| `testStepHalvedWhenHasBaby` | step / halved / when / 有無 / baby | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / 有無 / baby | 良い | - | - |
| `testStepHalvedWhenHasStalk` | step / halved / when / 有無 / stalk | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / 有無 / stalk | 良い | - | - |
| `testStepHalvedWhenDamaged` | step / halved / when / damaged | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / damaged | 良い | - | - |
| `testStepHalvedWhenSoHungryNotPredator` | step / halved / when / so / 空腹 / 非 / predator | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / so / 空腹 / 非 / predator | 良い | - | - |
| `testStepNotHalvedWhenPredatorAndHungry` | step / 非 / halved / when / predator / and / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / step / 非 / halved / when / predator / and / 空腹 | 良い | - | - |
| `testStepHalvedWhenFlyingCantFly` | step / halved / when / flying / cant / fly | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / flying / cant / fly | 良い | - | - |
| `testStepHalvedWhenFeelPain` | step / halved / when / feel / pain | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / feel / pain | 良い | - | - |
| `testStepHalvedWhenBurnedHeavily` | step / halved / when / burned / heavily | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / burned / heavily | 良い | - | - |
| `testStepHalvedWhenBurnedHeavilyNoOtherConditions` | step / halved / when / burned / heavily / なし / other / conditions | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / burned / heavily / なし / other / conditions | 良い | - | - |
| `testBurnedHeavilyCanFlyDoesNotHalveStepWhenNoOtherConditions` | burned / heavily / 可否 / fly / does / 非 / halve / step / when / なし / other / conditions | ゆっくり本体の状態/行動/イベント回帰 / burned / heavily / 可否 / fly / does / 非 / halve / step / when / なし / other / conditions | 良い | - | - |
| `testStepNotHalvedWhenFlyingAndCanFly` | step / 非 / halved / when / flying / and / 可否 / fly | ゆっくり本体の状態/行動/イベント回帰 / step / 非 / halved / when / flying / and / 可否 / fly | 良い | - | - |
| `testStepHalvedWhenAntsAttached` | step / halved / when / ants / attached | ゆっくり本体の状態/行動/イベント回帰 / step / halved / when / ants / attached | 良い | - | - |
| `testBurnedHeavilyDoesNotHalveWhenCanFly` | burned / heavily / does / 非 / halve / when / 可否 / fly | ゆっくり本体の状態/行動/イベント回帰 / burned / heavily / does / 非 / halve / when / 可否 / fly | 良い | - | - |
| `testEventLowestStepUsedForFrequency` | イベント / lowest / step / used / for / frequency | ゆっくり本体の状態/行動/イベント回帰 / イベント / lowest / step / used / for / frequency | 良い | - | - |
| `testCanFlyDestZequalCurrentClearsDestZ` | 可否 / fly / dest / zequal / current / clears / dest / z | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / dest / zequal / current / clears / dest / z | 良い | - | - |
| `testCanFlyDestZovershootClampsToDest` | 可否 / fly / dest / zovershoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / dest / zovershoot / clamps / to / dest | 良い | - | - |
| `testExternalForceBzStopsMovement` | external / force / bz / stops / movement | ゆっくり本体の状態/行動/イベント回帰 / external / force / bz / stops / movement | 良い | - | - |
| `testConveyorExternalForceStopsMovement` | conveyor / external / force / stops / movement | ゆっくり本体の状態/行動/イベント回帰 / conveyor / external / force / stops / movement | 良い | - | - |
| `testFallUnyoNoDamageNextFallAndTrampoline` | 落下 / unyo / なし / ダメージ / next / 落下 / and / trampoline | ゆっくり本体の状態/行動/イベント回帰 / 落下 / unyo / なし / ダメージ / next / 落下 / and / trampoline | 良い | - | - |
| `testFallHitsNoDamageNextFallAndTrampolineCheckFalse` | 落下 / hits / なし / ダメージ / next / 落下 / and / trampoline / 判定 / false | ゆっくり本体の状態/行動/イベント回帰 / 落下 / hits / なし / ダメージ / next / 落下 / and / trampoline / 判定 / false | 良い | - | - |
| `testNoAccessoryMessageOnXdirection` | なし / accessory / メッセージ / on / xdirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / on / xdirection | 良い | - | - |
| `testNoAccessoryMessageOnYdirection` | なし / accessory / メッセージ / on / ydirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / on / ydirection | 良い | - | - |
| `testNoAccessoryMessageNotTriggeredOnXdirection` | なし / accessory / メッセージ / 非 / triggered / on / xdirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / 非 / triggered / on / xdirection | 良い | - | - |
| `testNoAccessoryMessageNotTriggeredOnYdirection` | なし / accessory / メッセージ / 非 / triggered / on / ydirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / 非 / triggered / on / ydirection | 良い | - | - |
| `testNoAccessoryMessageSkippedWhenNotSad` | なし / accessory / メッセージ / skipped / when / 非 / sad | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / skipped / when / 非 / sad | 良い | - | - |
| `testFallDamageNoDamageNextFallAdultNotOnBed` | 落下 / ダメージ / なし / ダメージ / next / 落下 / adult / 非 / on / bed | ゆっくり本体の状態/行動/イベント回帰 / 落下 / ダメージ / なし / ダメージ / next / 落下 / adult / 非 / on / bed | 良い | - | - |
| `testStepNotHalvedWhenAllConditionsFalse` | step / 非 / halved / when / all / conditions / false | ゆっくり本体の状態/行動/イベント回帰 / step / 非 / halved / when / all / conditions / false | 良い | - | - |
| `testNoAccessoryMessageVerySadXdirection` | なし / accessory / メッセージ / very / sad / xdirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / very / sad / xdirection | 良い | - | - |
| `testNoAccessoryMessageVerySadYdirection` | なし / accessory / メッセージ / very / sad / ydirection | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / very / sad / ydirection | 良い | - | - |
| `testCanFlySetsDestZwhenNoTargetAndNoEvent` | 可否 / fly / sets / dest / zwhen / なし / target / and / なし / イベント | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / sets / dest / zwhen / なし / target / and / なし / イベント | 良い | - | - |
| `testCanFlyDoesNotAutoSetDestZwhenEventActive` | 可否 / fly / does / 非 / auto / 設定 / dest / zwhen / イベント / active | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / does / 非 / auto / 設定 / dest / zwhen / イベント / active | 良い | - | - |
| `testFallWithoutExternalForceTriggersFallBranch` | 落下 / without / external / force / triggers / 落下 / branch | ゆっくり本体の状態/行動/イベント回帰 / 落下 / without / external / force / triggers / 落下 / branch | 良い | - | - |
| `testDestXpositiveOvershootClampsToDest` | dest / xpositive / overshoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / overshoot / clamps / to / dest | 良い | - | - |
| `testDestXpositiveNoOvershootBranch` | dest / xpositive / なし / overshoot / branch | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / なし / overshoot / branch | 良い | - | - |
| `testDestYnegativeOvershootClampsToDest` | dest / ynegative / overshoot / clamps / to / dest | ゆっくり本体の状態/行動/イベント回帰 / dest / ynegative / overshoot / clamps / to / dest | 良い | - | - |
| `testDestYnegativeNoOvershootBranch` | dest / ynegative / なし / overshoot / branch | ゆっくり本体の状態/行動/イベント回帰 / dest / ynegative / なし / overshoot / branch | 良い | - | - |
| `testFallUnyoNoDamageNextFallOnBedBabySkipsStrike` | 落下 / unyo / なし / ダメージ / next / 落下 / on / bed / baby / skips / 打撃 | ゆっくり本体の状態/行動/イベント回帰 / 落下 / unyo / なし / ダメージ / next / 落下 / on / bed / baby / skips / 打撃 | 良い | - | - |
| `testFallUnyoTriggersChangeUnyo` | 落下 / unyo / triggers / change / unyo | ゆっくり本体の状態/行動/イベント回帰 / 落下 / unyo / triggers / change / unyo | 良い | - | - |
| `testDestXpositiveOvershootClamps` | dest / xpositive / overshoot / clamps | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / overshoot / clamps | 良い | - | - |
| `testDestXpositiveNoOvershoot` | dest / xpositive / なし / overshoot | ゆっくり本体の状態/行動/イベント回帰 / dest / xpositive / なし / overshoot | 良い | - | - |
| `testCanFlyKeepsHeightWhenNoTarget` | 可否 / fly / 維持 / height / when / なし / target | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / 維持 / height / when / なし / target | 良い | - | - |
| `testCanFlyDestZnegativeNoOvershoot` | 可否 / fly / dest / znegative / なし / overshoot | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / dest / znegative / なし / overshoot | 良い | - | - |
| `testSetMessageEmptyDoesNothing` | 設定 / メッセージ / empty / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / 設定 / メッセージ / empty / does / nothing | 良い | - | - |
| `testSetPikoMessageWithCount` | 設定 / piko / メッセージ / with / count | ゆっくり本体の状態/行動/イベント回帰 / 設定 / piko / メッセージ / with / count | 良い | - | - |
| `testRaperExcitingStepTwo` | raper / exciting / step / two | ゆっくり本体の状態/行動/イベント回帰 / raper / exciting / step / two | 良い | - | - |
| `testDestYpositiveNoOvershoot` | dest / ypositive / なし / overshoot | ゆっくり本体の状態/行動/イベント回帰 / dest / ypositive / なし / overshoot | 良い | - | - |
| `testDestYnegativeNoOvershoot` | dest / ynegative / なし / overshoot | ゆっくり本体の状態/行動/イベント回帰 / dest / ynegative / なし / overshoot | 良い | - | - |
| `testCanFlyDestZnegativeOvershootClamps` | 可否 / fly / dest / znegative / overshoot / clamps | ゆっくり本体の状態/行動/イベント回帰 / 可否 / fly / dest / znegative / overshoot / clamps | 良い | - | - |
| `testZcanGoBelowZeroWhenNotFlying` | zcan / go / below / zero / when / 非 / flying | ゆっくり本体の状態/行動/イベント回帰 / zcan / go / below / zero / when / 非 / flying | 良い | - | - |
| `testFallWithUnyoEnabledChangesUnyo` | 落下 / with / unyo / enabled / changes / unyo | ゆっくり本体の状態/行動/イベント回帰 / 落下 / with / unyo / enabled / changes / unyo | 良い | - | - |
| `testPoolEntryAvoidedWhenAverageAndRndNonZero` | pool / entry / avoided / when / average / and / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / pool / entry / avoided / when / average / and / rnd / non / zero | 良い | - | - |
| `testYoverflowAfterMovementClamped` | yoverflow / after / movement / clamped | ゆっくり本体の状態/行動/イベント回帰 / yoverflow / after / movement / clamped | 良い | - | - |
| `testFallDamageStrikeWhenNotOnBedAdult` | 落下 / ダメージ / 打撃 / when / 非 / on / bed / adult | ゆっくり本体の状態/行動/イベント回帰 / 落下 / ダメージ / 打撃 / when / 非 / on / bed / adult | 良い | - | - |
| `testFallDamageNoExtraWhenVzNegative` | 落下 / ダメージ / なし / extra / when / vz / negative | ゆっくり本体の状態/行動/イベント回帰 / 落下 / ダメージ / なし / extra / when / vz / negative | 良い | - | - |
| `testBlockedCountOverLimitFoolSetsVerySadAndMessage` | blocked / count / over / limit / fool / sets / very / sad / and / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / blocked / count / over / limit / fool / sets / very / sad / and / メッセージ | 良い | - | - |
| `testXoverflowSetsDirXnegative` | xoverflow / sets / dir / xnegative | ゆっくり本体の状態/行動/イベント回帰 / xoverflow / sets / dir / xnegative | 良い | - | - |
| `testYoverflowSetsDirYnegative` | yoverflow / sets / dir / ynegative | ゆっくり本体の状態/行動/イベント回帰 / yoverflow / sets / dir / ynegative | 良い | - | - |
| `testZoverflowClampsToMapZ` | zoverflow / clamps / to / map / z | ゆっくり本体の状態/行動/イベント回帰 / zoverflow / clamps / to / map / z | 良い | - | - |
| `testFallBranchUnyoAndNoDamageNextFallClearsDamage` | 落下 / branch / unyo / and / なし / ダメージ / next / 落下 / clears / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 落下 / branch / unyo / and / なし / ダメージ / next / 落下 / clears / ダメージ | 良い | - | - |
| `testFallWhenMostDepthDiffersWithoutVz` | 落下 / when / most / depth / differs / without / vz | ゆっくり本体の状態/行動/イベント回帰 / 落下 / when / most / depth / differs / without / vz | 良い | - | - |
| `testBurnedHeavilyHalvesStepSkipsMoveOnOddAge` | burned / heavily / halves / step / skips / 移動 / on / odd / age | ゆっくり本体の状態/行動/イベント回帰 / burned / heavily / halves / step / skips / 移動 / on / odd / age | 良い | - | - |
| `testRaperExcitingOvershootsDestXclamp` | raper / exciting / overshoots / dest / xclamp | ゆっくり本体の状態/行動/イベント回帰 / raper / exciting / overshoots / dest / xclamp | 良い | - | - |
| `testRaperExcitingOvershootsDestYclamp` | raper / exciting / overshoots / dest / yclamp | ゆっくり本体の状態/行動/イベント回帰 / raper / exciting / overshoots / dest / yclamp | 良い | - | - |
| `testBlockedByWallExceedLimitSetsVerySad` | blocked / by / 壁 / exceed / limit / sets / very / sad | ゆっくり本体の状態/行動/イベント回帰 / blocked / by / 壁 / exceed / limit / sets / very / sad | 良い | - | - |
| `testBlockedByWallHalfLimitFoolCalms` | blocked / by / 壁 / half / limit / fool / calms | ゆっくり本体の状態/行動/イベント回帰 / blocked / by / 壁 / half / limit / fool / calms | 良い | - | - |
| `testWorldEventSendMessageSetsBuffer` | world / イベント / send / メッセージ / sets / buffer | ゆっくり本体の状態/行動/イベント回帰 / world / イベント / send / メッセージ / sets / buffer | 良い | - | - |
| `testNoFallingWhenFlyingStable` | なし / falling / when / flying / stable | ゆっくり本体の状態/行動/イベント回帰 / なし / falling / when / flying / stable | 良い | - | - |
| `testFallBranchWhenUnyoDisabled` | 落下 / branch / when / unyo / disabled | ゆっくり本体の状態/行動/イベント回帰 / 落下 / branch / when / unyo / disabled | 良い | - | - |
| `testFallWithNoDamageNextFallFalse` | 落下 / with / なし / ダメージ / next / 落下 / false | ゆっくり本体の状態/行動/イベント回帰 / 落下 / with / なし / ダメージ / next / 落下 / false | 良い | - | - |
| `testFallAdultNotOnBedStrikes` | 落下 / adult / 非 / on / bed / strikes | ゆっくり本体の状態/行動/イベント回帰 / 落下 / adult / 非 / on / bed / strikes | 良い | - | - |
| `testSoHungryHalvesStepSkipsMoveOnOddAge` | so / 空腹 / halves / step / skips / 移動 / on / odd / age | ゆっくり本体の状態/行動/イベント回帰 / so / 空腹 / halves / step / skips / 移動 / on / odd / age | 良い | - | - |
| `testFlyWithEventDoesNotSetDestZ` | fly / with / イベント / does / 非 / 設定 / dest / z | ゆっくり本体の状態/行動/イベント回帰 / fly / with / イベント / does / 非 / 設定 / dest / z | 良い | - | - |
| `testRaperNotExcitingMovesOneStep` | raper / 非 / exciting / moves / one / step | ゆっくり本体の状態/行動/イベント回帰 / raper / 非 / exciting / moves / one / step | 良い | - | - |
| `testWallCollisionWithoutTargetsRandomizesDirection` | 壁 / 衝突 / without / targets / randomizes / direction | ゆっくり本体の状態/行動/イベント回帰 / 壁 / 衝突 / without / targets / randomizes / direction | 良い | - | - |
| `testFallBranchSkippedWhenFallingUnderGround` | 落下 / branch / skipped / when / falling / under / ground | ゆっくり本体の状態/行動/イベント回帰 / 落下 / branch / skipped / when / falling / under / ground | 良い | - | - |
| `testFallPathWithTrampolineNoDamage` | 落下 / path / with / trampoline / なし / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 落下 / path / with / trampoline / なし / ダメージ | 良い | - | - |
| `testFallBabyOnBedSkipsStrike` | 落下 / baby / on / bed / skips / 打撃 | ゆっくり本体の状態/行動/イベント回帰 / 落下 / baby / on / bed / skips / 打撃 | 良い | - | - |
| `testFallPealedTriggersDying` | 落下 / pealed / triggers / dying | ゆっくり本体の状態/行動/イベント回帰 / 落下 / pealed / triggers / dying | 良い | - | - |
| `testHasBabyHalvesStepSkipsMoveOnOddAge` | 有無 / baby / halves / step / skips / 移動 / on / odd / age | ゆっくり本体の状態/行動/イベント回帰 / 有無 / baby / halves / step / skips / 移動 / on / odd / age | 良い | - | - |
| `testNoAccessoryMessageOnXwhenVerySad` | なし / accessory / メッセージ / on / xwhen / very / sad | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / on / xwhen / very / sad | 良い | - | - |
| `testNoAccessoryMessageOnYwhenSad` | なし / accessory / メッセージ / on / ywhen / sad | ゆっくり本体の状態/行動/イベント回帰 / なし / accessory / メッセージ / on / ywhen / sad | 良い | - | - |
| `testFlyKeepsDestZwhenMoveTargetExists` | fly / 維持 / dest / zwhen / 移動 / target / exists | ゆっくり本体の状態/行動/イベント回帰 / fly / 維持 / dest / zwhen / 移動 / target / exists | 良い | - | - |
| `testWallCollisionBlockedCountHalfRandomDirection` | 壁 / 衝突 / blocked / count / half / random / direction | ゆっくり本体の状態/行動/イベント回帰 / 壁 / 衝突 / blocked / count / half / random / direction | 良い | - | - |
| `testFallOnBedAdultStillStrikes` | 落下 / on / bed / adult / still / strikes | ゆっくり本体の状態/行動/イベント回帰 / 落下 / on / bed / adult / still / strikes | 良い | - | - |
| `testHasStalkHalvesStepSkipsMoveOnOddAge` | 有無 / stalk / halves / step / skips / 移動 / on / odd / age | ゆっくり本体の状態/行動/イベント回帰 / 有無 / stalk / halves / step / skips / 移動 / on / odd / age | 良い | - | - |
| `testFlyingTypeWithoutBraidHalvesStepSkipsMoveOnOddAge` | flying / type / without / おさげ / halves / step / skips / 移動 / on / odd / age | ゆっくり本体の状態/行動/イベント回帰 / flying / type / without / おさげ / halves / step / skips / 移動 / on / odd / age | 良い | - | - |
| `testWallCollisionBlockedCountHalfRudeSetsAngry` | 壁 / 衝突 / blocked / count / half / rude / sets / angry | ゆっくり本体の状態/行動/イベント回帰 / 壁 / 衝突 / blocked / count / half / rude / sets / angry | 良い | - | - |
| `testWallCollisionBlockedCountLimitRandomXbranch` | 壁 / 衝突 / blocked / count / limit / random / xbranch | ゆっくり本体の状態/行動/イベント回帰 / 壁 / 衝突 / blocked / count / limit / random / xbranch | 良い | - | - |
| `testBlindCantSeeMessage` | blind / cant / see / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / blind / cant / see / メッセージ | 良い | - | - |
| `testBlindLamentNoYukkuriMessage` | blind / lament / なし / yukkuri / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / blind / lament / なし / yukkuri / メッセージ | 良い | - | - |
| `testBlindNoMessage` | blind / なし / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / blind / なし / メッセージ | 良い | - | - |
| `testShutmouthWithMessageTrigger` | shutmouth / with / メッセージ / trigger | ゆっくり本体の状態/行動/イベント回帰 / shutmouth / with / メッセージ / trigger | 良い | - | - |
| `testShutmouthNoMessageWhenRndNonZero` | shutmouth / なし / メッセージ / when / rnd / non / zero | ゆっくり本体の状態/行動/イベント回帰 / shutmouth / なし / メッセージ / when / rnd / non / zero | 良い | - | - |
| `testShutmouthNoMessageWhenSleeping` | shutmouth / なし / メッセージ / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / shutmouth / なし / メッセージ / when / sleeping | 良い | - | - |
| `testLockmoveEarlyPeriodBuriedMessage` | lockmove / early / period / buried / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / buried / メッセージ | 良い | - | - |
| `testLockmoveEarlyPeriodNearlyAllBuried` | lockmove / early / period / nearly / all / buried | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / nearly / all / buried | 良い | - | - |
| `testLockmoveEarlyPeriodCantMoveWithNobinobi` | lockmove / early / period / cant / 移動 / with / nobinobi | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / cant / 移動 / with / nobinobi | 良い | - | - |
| `testLockmoveEarlyPeriodCantMoveNoNobinobi` | lockmove / early / period / cant / 移動 / なし / nobinobi | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / cant / 移動 / なし / nobinobi | 良い | - | - |
| `testLockmoveEarlyPeriodHungryMessage` | lockmove / early / period / 空腹 / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / 空腹 / メッセージ | 良い | - | - |
| `testLockmoveEarlyPeriodSecondCantMoveBuried` | lockmove / early / period / second / cant / 移動 / buried | ゆっくり本体の状態/行動/イベント回帰 / lockmove / early / period / second / cant / 移動 / buried | 良い | - | - |
| `testMidiumLamentMessage` | midium / lament / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / midium / lament / メッセージ | 良い | - | - |
| `testMidiumNoLamentMessage` | midium / なし / lament / メッセージ | ゆっくり本体の状態/行動/イベント回帰 / midium / なし / lament / メッセージ | 良い | - | - |
| `testMidiumHungry` | midium / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / midium / 空腹 | 良い | - | - |
| `testCriticalEarlyLamentLowYukkuri` | critical / early / lament / low / yukkuri | ゆっくり本体の状態/行動/イベント回帰 / critical / early / lament / low / yukkuri | 良い | - | - |
| `testCriticalEarlyCantMove` | critical / early / cant / 移動 | ゆっくり本体の状態/行動/イベント回帰 / critical / early / cant / 移動 | 良い | - | - |
| `testCriticalLateCantMove2` | critical / late / cant / move2 | ゆっくり本体の状態/行動/イベント回帰 / critical / late / cant / move2 | 良い | - | - |
| `testCriticalLateLamentNoYukkuri` | critical / late / lament / なし / yukkuri | ゆっくり本体の状態/行動/イベント回帰 / critical / late / lament / なし / yukkuri | 良い | - | - |
| `testCriticalEarlyHungry` | critical / early / 空腹 | ゆっくり本体の状態/行動/イベント回帰 / critical / early / 空腹 | 良い | - | - |
| `testNoOkazariLament` | なし / okazari / lament | ゆっくり本体の状態/行動/イベント回帰 / なし / okazari / lament | 良い | - | - |
| `testNoOkazariNoLament` | なし / okazari / なし / lament | ゆっくり本体の状態/行動/イベント回帰 / なし / okazari / なし / lament | 良い | - | - |
| `testGetInVainRudeWithRndTrue` | 取得 / in / vain / rude / with / rnd / true | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / rude / with / rnd / true | 良い | - | - |
| `testGetInVainRudeWithRndFalse` | 取得 / in / vain / rude / with / rnd / false | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / rude / with / rnd / false | 良い | - | - |
| `testGetInVainNotRude` | 取得 / in / vain / 非 / rude | ゆっくり本体の状態/行動/イベント回帰 / 取得 / in / vain / 非 / rude | 良い | - | - |
| `testGetRandomAttitudeBranches` | 取得 / random / attitude / branches | ゆっくり本体の状態/行動/イベント回帰 / 取得 / random / attitude / branches | 良い | - | reflection |
| `testSetTakeoutItemMovesShitToTakenOut` | 設定 / takeout / item / moves / shit / to / taken / out | ゆっくり本体の状態/行動/イベント回帰 / 設定 / takeout / item / moves / shit / to / taken / out | 良い | - | - |
| `testSetTakeoutItemMovesFoodToTakenOut` | 設定 / takeout / item / moves / food / to / taken / out | ゆっくり本体の状態/行動/イベント回帰 / 設定 / takeout / item / moves / food / to / taken / out | 良い | - | - |
| `testDropTakeoutItemShitPlacesOnFloor` | drop / takeout / item / shit / places / on / floor | ゆっくり本体の状態/行動/イベント回帰 / drop / takeout / item / shit / places / on / floor | 良い | - | - |
| `testDropTakeoutItemFoodPlacesOnFloor` | drop / takeout / item / food / places / on / floor | ゆっくり本体の状態/行動/イベント回帰 / drop / takeout / item / food / places / on / floor | 良い | - | - |
| `testDropTakeoutItemReturnsNullWhenMissing` | drop / takeout / item / 戻り / null / when / missing | ゆっくり本体の状態/行動/イベント回帰 / drop / takeout / item / 戻り / null / when / missing | 良い | - | - |
| `testInjectIntoDeadDoesNothing` | inject / into / 死亡 / does / nothing | ゆっくり本体の状態/行動/イベント回帰 / inject / into / 死亡 / does / nothing | 良い | - | - |
| `testInjectIntoNullDnaNoBaby` | inject / into / null / dna / なし / baby | ゆっくり本体の状態/行動/イベント回帰 / inject / into / null / dna / なし / baby | 良い | - | - |
| `testInjectIntoCreatesBaby` | inject / into / creates / baby | ゆっくり本体の状態/行動/イベント回帰 / inject / into / creates / baby | 良い | - | - |
| `testInjectIntoBodyCastrationNoBaby` | inject / into / 本体 / castration / なし / baby | ゆっくり本体の状態/行動/イベント回帰 / inject / into / 本体 / castration / なし / baby | 良い | - | - |
| `testDripSpermNullDnaNoStalk` | drip / sperm / null / dna / なし / stalk | ゆっくり本体の状態/行動/イベント回帰 / drip / sperm / null / dna / なし / stalk | 良い | - | - |
| `testDripSpermCreatesStalkBabies` | drip / sperm / creates / stalk / babies | ゆっくり本体の状態/行動/イベント回帰 / drip / sperm / creates / stalk / babies | 良い | - | - |
| `testDripSpermCreatesNullEntriesWhenRndFalse` | drip / sperm / creates / null / entries / when / rnd / false | ゆっくり本体の状態/行動/イベント回帰 / drip / sperm / creates / null / entries / when / rnd / false | 良い | - | - |
| `testStrikeByObjectDeadEarlyReturn` | 打撃 / by / object / 死亡 / early / 戻り | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByObjectAllowanceCapsDamage` | 打撃 / by / object / allowance / caps / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByObjectMeltIncreasesDamage` | 打撃 / by / object / melt / increases / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByObjectWetIncreasesDamage` | 打撃 / by / object / 水濡れ / increases / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByObjectHasPantsReducesDamage` | 打撃 / by / object / 有無 / pants / reduces / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriDeadEarlyReturn` | 打撃 / by / yukkuri / 死亡 / early / 戻り | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriAllowanceCapsDamage` | 打撃 / by / yukkuri / allowance / caps / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriBreaksBraidWhenRndZero` | 打撃 / by / yukkuri / breaks / おさげ / when / rnd / zero | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriDefaultEventMakesAngryWhenNotVeryNice` | 打撃 / by / yukkuri / default / イベント / makes / angry / when / 非 / very / nice | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriKilledSetsCrushed` | 打撃 / by / yukkuri / killed / sets / crushed | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriHateNoOkazariEventMakesAngry` | 打撃 / by / yukkuri / hate / なし / okazari / イベント / makes / angry | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriPredatorsGameEventSetsScare` | 打撃 / by / yukkuri / predators / game / イベント / sets / scare | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriRaperReactionMovesToSukkiri` | 打撃 / by / yukkuri / raper / reaction / moves / to / sukkiri | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriAvoidMoldEventFoolGetsAngry` | 打撃 / by / yukkuri / avoid / mold / イベント / fool / gets / angry | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriUnyoBranchChangesUnyo` | 打撃 / by / yukkuri / unyo / branch / changes / unyo | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriPredatorVictimTakesLess` | 打撃 / by / yukkuri / predator / victim / takes / less | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriPredatorAttackerDealsMore` | 打撃 / by / yukkuri / predator / attacker / deals / more | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriMeltMultiplierIncreasesDamage` | 打撃 / by / yukkuri / melt / multiplier / increases / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriWetMultiplierIncreasesDamage` | 打撃 / by / yukkuri / 水濡れ / multiplier / increases / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriPantsReducesDamage` | 打撃 / by / yukkuri / pants / reduces / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriExcitingReducesDamage` | 打撃 / by / yukkuri / exciting / reduces / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriAllowanceCapsToFourFifths` | 打撃 / by / yukkuri / allowance / caps / to / four / fifths | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriAllowanceNoIncreaseWhenAlreadyOverFourFifths` | 打撃 / by / yukkuri / allowance / なし / increase / when / already / over / four / fifths | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriDefaultEventDoesNotAngryWhenVeryNice` | 打撃 / by / yukkuri / default / イベント / does / 非 / angry / when / very / nice | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriHateNoOkazariVeryNiceNotAngry` | 打撃 / by / yukkuri / hate / なし / okazari / very / nice / 非 / angry | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriHateNoOkazariUnunSlaveNotAngry` | 打撃 / by / yukkuri / hate / なし / okazari / unun / slave / 非 / angry | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriAvoidMoldEventBabyNotAngry` | 打撃 / by / yukkuri / avoid / mold / イベント / baby / 非 / angry | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriUnbirthSkipsReaction` | 打撃 / by / yukkuri / unbirth / skips / reaction | 生存/行動可否回帰 | 良い | - | - |
| `testStrikeByYukkuriEnemyDamagedReducesDamage` | 打撃 / by / yukkuri / enemy / damaged / reduces / ダメージ | 生存/行動可否回帰 | 良い | - | - |
| `testKillTimeReturnsWhenEventOrPlaying` | kill / time / 戻り / when / イベント / or / playing | ゆっくり本体の状態/行動/イベント回帰 / kill / time / 戻り / when / イベント / or / playing | 良い | - | assert:0 |
| `testKillTimeGetInVainBranch` | kill / time / 取得 / in / vain / branch | ゆっくり本体の状態/行動/イベント回帰 / kill / time / 取得 / in / vain / branch | 良い | - | - |
| `testKillTimeNobinobiBranch` | kill / time / nobinobi / branch | ゆっくり本体の状態/行動/イベント回帰 / kill / time / nobinobi / branch | 良い | - | - |
| `testKillTimeFurifuriBranch` | kill / time / furifuri / branch | ゆっくり本体の状態/行動/イベント回帰 / kill / time / furifuri / branch | 良い | - | - |
| `testKillTimeHungryBranch` | kill / time / 空腹 / branch | ゆっくり本体の状態/行動/イベント回帰 / kill / time / 空腹 / branch | 良い | - | - |
| `testKillTimeRelaxOkurumiBranch` | kill / time / relax / okurumi / branch | ゆっくり本体の状態/行動/イベント回帰 / kill / time / relax / okurumi / branch | 良い | - | - |
| `testOnlyAmaamaFootBakeCriticalReturnsFalse` | only / amaama / foot / bake / critical / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / only / amaama / foot / bake / critical / 戻り / false | 良い | - | - |
| `testOnlyAmaamaWiseNoDamageDiscipline40` | only / amaama / wise / なし / ダメージ / discipline40 | ゆっくり本体の状態/行動/イベント回帰 / only / amaama / wise / なし / ダメージ / discipline40 | 良い | - | - |
| `testOnlyAmaamaAverageDamagedDiscipline70` | only / amaama / average / damaged / discipline70 | ゆっくり本体の状態/行動/イベント回帰 / only / amaama / average / damaged / discipline70 | 良い | - | - |
| `testOnlyAmaamaFoolHeavilyDamagedDiscipline50` | only / amaama / fool / heavily / damaged / discipline50 | ゆっくり本体の状態/行動/イベント回帰 / only / amaama / fool / heavily / damaged / discipline50 | 良い | - | - |
| `testCallParentCannotActionResetsFlags` | call / 親 / cannot / action / resets / flags | ゆっくり本体の状態/行動/イベント回帰 / call / 親 / cannot / action / resets / flags | 良い | - | - |
| `testCallParentAntsSetsCalling` | call / 親 / ants / sets / calling | ゆっくり本体の状態/行動/イベント回帰 / call / 親 / ants / sets / calling | 良い | - | - |
| `testCallParentDirtyKusogakiCallsParents` | call / 親 / dirty / kusogaki / calls / parents | ゆっくり本体の状態/行動/イベント回帰 / call / 親 / dirty / kusogaki / calls / parents | 良い | - | - |
| `testUpDateSetsStalkZzeroWhenFullyBuried` | up / date / sets / stalk / zzero / when / fully / buried | ゆっくり本体の状態/行動/イベント回帰 / up / date / sets / stalk / zzero / when / fully / buried | 良い | - | - |
| `testUpDateSetsStalkZaboveZeroWhenNotBuried` | up / date / sets / stalk / zabove / zero / when / 非 / buried | ゆっくり本体の状態/行動/イベント回帰 / up / date / sets / stalk / zabove / zero / when / 非 / buried | 良い | - | - |
| `testConstructorSetsFirstGroundByZ` | constructor / sets / first / ground / by / z | ゆっくり本体の状態/行動/イベント回帰 / constructor / sets / first / ground / by / z | 良い | - | - |
| `testConstructorAttitudeFromPapaWhenRndTrue` | constructor / attitude / from / papa / when / rnd / true | ゆっくり本体の状態/行動/イベント回帰 / constructor / attitude / from / papa / when / rnd / true | 良い | - | - |
| `testConstructorAttitudeRandomWhenParentsNull` | constructor / attitude / random / when / parents / null | ゆっくり本体の状態/行動/イベント回帰 / constructor / attitude / random / when / parents / null | 良い | - | - |
| `testConstructorIntelligenceOverrideFromFoolParents` | constructor / intelligence / override / from / fool / parents | ゆっくり本体の状態/行動/イベント回帰 / constructor / intelligence / override / from / fool / parents | 良い | - | - |
| `testConstructorPublicRankInheritsUnunSlaveFromMama` | constructor / public / rank / inherits / unun / slave / from / mama | ゆっくり本体の状態/行動/イベント回帰 / constructor / public / rank / inherits / unun / slave / from / mama | 良い | - | - |
| `testConstructorMapIndexYaseiyuWhenNoMama` | constructor / map / index / yaseiyu / when / なし / mama | ゆっくり本体の状態/行動/イベント回帰 / constructor / map / index / yaseiyu / when / なし / mama | 良い | - | - |
| `testDoSurisuriReturnsFalseWhenNotSurisuri` | do / surisuri / 戻り / false / when / 非 / surisuri | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / 戻り / false / when / 非 / surisuri | 良い | - | - |
| `testDoSurisuriExcitingSukkiriBranch` | do / surisuri / exciting / sukkiri / branch | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / exciting / sukkiri / branch | 良い | - | - |
| `testDoSurisuriPainBranchWhenCut` | do / surisuri / pain / branch / when / cut | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / pain / branch / when / cut | 良い | - | - |
| `testDoSurisuriNeedledBranch` | do / surisuri / needled / branch | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / needled / branch | 良い | - | - |
| `testDoSurisuriDefaultBranchSetsSmileFace` | do / surisuri / default / branch / sets / smile / 表情 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / default / branch / sets / smile / 表情 | 良い | - | - |
| `testCheckNonYukkuriDiseaseAntiSteamResetsState` | 判定 / non / yukkuri / disease / anti / steam / resets / state | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / anti / steam / resets / state | 良い | - | - |
| `testCheckNonYukkuriDiseaseAnydAmpouleResetsState` | 判定 / non / yukkuri / disease / anyd / ampoule / resets / state | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / anyd / ampoule / resets / state | 良い | - | - |
| `testCheckNonYukkuriDiseaseStressTriggersNear` | 判定 / non / yukkuri / disease / ストレス / triggers / near | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / ストレス / triggers / near | 良い | - | - |
| `testCheckNonYukkuriDiseaseStressTriggersNyd` | 判定 / non / yukkuri / disease / ストレス / triggers / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / ストレス / triggers / 非ゆっくり症 | 良い | - | - |
| `testCheckNonYukkuriDiseaseRecoveryResetsState` | 判定 / non / yukkuri / disease / recovery / resets / state | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / recovery / resets / state | 良い | - | - |
| `testCheckNonYukkuriDiseaseUnbirthReturnsTrue` | 判定 / non / yukkuri / disease / unbirth / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / unbirth / 戻り / true | 良い | - | - |
| `testCheckNonYukkuriDiseaseNearPeriodProgression` | 判定 / non / yukkuri / disease / near / period / progression | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / near / period / progression | 良い | - | - |
| `testCheckNonYukkuriDiseasePeriodProgression` | 判定 / non / yukkuri / disease / period / progression | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / period / progression | 良い | - | - |
| `testCheckNonYukkuriDiseaseNearPeriodCase3To4` | 判定 / non / yukkuri / disease / near / period / case3 / to4 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / near / period / case3 / to4 | 良い | - | - |
| `testCheckNonYukkuriDiseasePeriodCase4To5NoReset` | 判定 / non / yukkuri / disease / period / case4 / to5 / なし / reset | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / period / case4 / to5 / なし / reset | 良い | - | - |
| `testCheckNonYukkuriDiseasePeriodCase0To4` | 判定 / non / yukkuri / disease / period / case0 / to4 | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / period / case0 / to4 | 良い | - | - |
| `testSickHeavyDamageExtraDamageWithRnd` | 病気 / heavy / ダメージ / extra / ダメージ / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / 病気 / heavy / ダメージ / extra / ダメージ / with / rnd | 良い | - | - |
| `testSickHeavyDamageNoExtraDamageWithRnd` | 病気 / heavy / ダメージ / なし / extra / ダメージ / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / 病気 / heavy / ダメージ / なし / extra / ダメージ / with / rnd | 良い | - | - |
| `testPackedPurupuruWithRnd` | packed / purupuru / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / 梱包/拘束回帰 | 良い | - | - |
| `testPackedNoPurupuruWithRnd` | packed / なし / purupuru / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / 梱包/拘束回帰 | 良い | - | - |
| `testBodyInjureWithBodyCut` | 本体 / injure / with / 本体 / cut | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / with / 本体 / cut | 良い | - | - |
| `testBodyInjureWithoutBodyCut` | 本体 / injure / without / 本体 / cut | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / without / 本体 / cut | 良い | - | - |
| `testBodyInjureAlreadyInjuredNoCut` | 本体 / injure / already / injured / なし / cut | ゆっくり本体の状態/行動/イベント回帰 / 本体 / injure / already / injured / なし / cut | 良い | - | - |
| `testCutDyingMessageWithRnd` | cut / dying / メッセージ / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / cut / dying / メッセージ / with / rnd | 良い | - | - |
| `testCutNoDyingMessageWithRnd` | cut / なし / dying / メッセージ / with / rnd | ゆっくり本体の状態/行動/イベント回帰 / cut / なし / dying / メッセージ / with / rnd | 良い | - | - |
| `testCutDyingMessageNydnear` | cut / dying / メッセージ / nydnear | ゆっくり本体の状態/行動/イベント回帰 / cut / dying / メッセージ / nydnear | 良い | - | assert:0 |
| `testClockTickRemovedReturnsRemoved` | clock / tick / removed / 戻り / removed | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickDeadReturnsDead` | clock / tick / 死亡 / 戻り / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickDeadCrushedFirstTime` | clock / tick / 死亡 / crushed / first / time | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickBurstDeadPath` | clock / tick / 破裂 / 死亡 / path | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAgeBoostSteamIncreasesAge` | clock / tick / age / boost / steam / increases / age | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAgeStopSteamDecreasesAge` | clock / tick / age / stop / steam / decreases / age | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickPanicBranchSetsVerySad` | clock / tick / 恐慌 / branch / sets / very / sad | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAttachmentRemoved` | clock / tick / attachment / removed | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickBirthBabyEvent` | clock / tick / birth / baby / イベント | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickEventResultAppliedWhenPriorityHigh` | clock / tick / イベント / result / applied / when / priority / high | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickCanEventResponseFalseBranch` | clock / tick / 可否 / イベント / response / false / branch | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNonYukkuriDiseaseNearMoveTrue` | clock / tick / non / yukkuri / disease / near / 移動 / true | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNonYukkuriDiseaseNearMoveDontMove` | clock / tick / non / yukkuri / disease / near / 移動 / dont / 移動 | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickBirthFailureClearsBabies` | clock / tick / birth / failure / clears / babies | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickLowPriorityEventDoesNotOverrideRetval` | clock / tick / low / priority / イベント / does / 非 / override / retval | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickSleepSetsNightmare` | clock / tick / 睡眠 / sets / nightmare | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickSleepWakesUpWhenPeriodExceeds` | clock / tick / 睡眠 / wakes / up / when / period / exceeds | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickOperationTimeTriggersFamilyCheck` | clock / tick / operation / time / triggers / 家族 / 判定 | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickDeadResetsUnyoWhenEnabled` | clock / tick / 死亡 / resets / unyo / when / enabled | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAgeLimitCausesDeath` | clock / tick / age / limit / causes / death | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickEndlessFurifuriWhenCanFurifuri` | clock / tick / endless / furifuri / when / 可否 / furifuri | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickEndlessFurifuriWhenCantFurifuriNotNyd` | clock / tick / endless / furifuri / when / cant / furifuri / 非 / 非ゆっくり症 | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickRapidPregnantSteamIncreasesBoost` | clock / tick / rapid / pregnant / steam / increases / boost | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickRandomFaceReset` | clock / tick / random / 表情 / reset | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickLowPriorityEventAppliesWhenDoNothing` | clock / tick / low / priority / イベント / applies / when / do / nothing | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickEndlessFurifuriNydbranch` | clock / tick / endless / furifuri / nydbranch | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickShitEventDoshitWhenNotBlocked` | clock / tick / shit / イベント / doshit / when / 非 / blocked | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickShitEventCrushedWhenSleeping` | clock / tick / shit / イベント / crushed / when / sleeping | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickPanicDontMoveBranch` | clock / tick / 恐慌 / dont / 移動 / branch | ゆっくり本体の状態/行動/イベント回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testSuperEatingRecoveryChainKeepsBodyFedAndHealsDamage` | super / eating / recovery / chain / 維持 / 本体 / fed / and / heals / ダメージ | ゆっくり本体の状態/行動/イベント回帰 / 大食い回復連鎖回帰 | 良い | - | - |
| `testTerminalSickChainWakesBodyClearsLowPriorityEventAndMakesVerySad` | terminal / 病気 / chain / wakes / 本体 / clears / low / priority / イベント / and / makes / very / sad | ゆっくり本体の状態/行動/イベント回帰 / 病気末期連鎖回帰 | 良い | - | - |

### `DnaTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | ゆっくり本体の状態/行動/イベント回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | ゆっくり本体の状態/行動/イベント回帰 / parameterized / constructor | 良い | - | - |
| `testGettersAndSetters` | getters / and / setters | ゆっくり本体の状態/行動/イベント回帰 / getters / and / setters | 良い | - | - |
| `testScenarioDefaultConstructorLeavesParentIdsUnset` | シナリオ / default / constructor / leaves / 親 / ids / unset | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / default / constructor / leaves / 親 / ids / unset | 良い | - | - |
| `testScenarioParameterizedConstructorDoesNotImplyAnyParentIds` | シナリオ / parameterized / constructor / does / 非 / imply / any / 親 / ids | ゆっくり本体の状態/行動/イベント回帰 / シナリオ / parameterized / constructor / does / 非 / imply / any / 親 / ids | 良い | - | - |

### `NonYukkuriDiseaseEstrusTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - ゆっくり本体の状態/行動/イベント回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testNyddoesNotEnterEstrus` | nyddoes / 非 / enter / estrus | ゆっくり本体の状態/行動/イベント回帰 / nyddoes / 非 / enter / estrus | 良い | - | - |
| `testEstrusClearedWhenNyddevelops` | estrus / cleared / when / nyddevelops | ゆっくり本体の状態/行動/イベント回帰 / estrus / cleared / when / nyddevelops | 良い | - | - |

### `PlainBodyAttributesTest`
- 状態: 完了 (22/22 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 座標変換や幾何値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testInstantiation` | instantiation | ゆっくり本体の状態/行動/イベント回帰 / instantiation | 良い | - | - |
| `testGetTypeReturnsZero` | 取得 / type / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / type / 戻り / zero | 良い | - | - |
| `testForceSetHappinessVeryHappy` | force / 設定 / 幸福 / very / happy | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / very / happy | 良い | - | - |
| `testForceSetHappinessHappy` | force / 設定 / 幸福 / happy | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / happy | 良い | - | - |
| `testForceSetHappinessAverage` | force / 設定 / 幸福 / average | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / average | 良い | - | - |
| `testForceSetHappinessSad` | force / 設定 / 幸福 / sad | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / sad | 良い | - | - |
| `testForceSetHappinessVerySad` | force / 設定 / 幸福 / very / sad | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / very / sad | 良い | - | - |
| `testGetNameJ` | 取得 / name / j | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j | 良い | - | - |
| `testGetNameE` | 取得 / name / e | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e | 良い | - | - |
| `testGetNameJ2` | 取得 / name / j2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j2 | 良い | - | - |
| `testGetNameE2` | 取得 / name / e2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e2 | 良い | - | - |
| `testGetMyName` | 取得 / my / name | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name | 良い | - | - |
| `testGetMyNameD` | 取得 / my / name / d | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name / d | 良い | - | - |
| `testGetImageReturnsZero` | 取得 / image / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / 戻り / zero | 良い | - | - |
| `testGetImageWithVariousArgs` | 取得 / image / with / various / args | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / with / various / args | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | ゆっくり本体の状態/行動/イベント回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testIsImageLoadedReturnsTrue` | 状態 / image / loaded / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / image / loaded / 戻り / true | 良い | - | - |
| `testGetMountPointReturnsNull` | 取得 / mount / point / 戻り / null | ゆっくり本体の状態/行動/イベント回帰 / 取得 / mount / point / 戻り / null | 良い | - | - |
| `testGetMountPointWithEmptyKey` | 取得 / mount / point / with / empty / key | ゆっくり本体の状態/行動/イベント回帰 / 取得 / mount / point / with / empty / key | 良い | - | - |
| `testCheckNonYukkuriDiseaseToleranceReturnsZero` | 判定 / non / yukkuri / disease / tolerance / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / tolerance / 戻り / zero | 良い | - | - |
| `testBodyAttributesIntegration` | 本体 / attributes / integration | ゆっくり本体の状態/行動/イベント回帰 / 本体 / attributes / integration | 良い | - | - |
| `testForceSetHappinessAllValues` | force / 設定 / 幸福 / all / values | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / all / values | 良い | - | - |

### `StubBodyAttributesTest`
- 状態: 完了 (20/20 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 座標変換や幾何値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetTypeReturnsZero` | 取得 / type / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / type / 戻り / zero | 良い | - | - |
| `testGetNameJ` | 取得 / name / j | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j | 良い | - | - |
| `testGetNameE` | 取得 / name / e | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e | 良い | - | - |
| `testGetNameJ2` | 取得 / name / j2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j2 | 良い | - | - |
| `testGetNameE2` | 取得 / name / e2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e2 | 良い | - | - |
| `testGetMyName` | 取得 / my / name | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name | 良い | - | - |
| `testGetMyNameD` | 取得 / my / name / d | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name / d | 良い | - | - |
| `testGetImageReturnsZero` | 取得 / image / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / 戻り / zero | 良い | - | - |
| `testGetImageWithVariousArgs` | 取得 / image / with / various / args | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / with / various / args | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | ゆっくり本体の状態/行動/イベント回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testIsImageLoadedReturnsTrue` | 状態 / image / loaded / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / image / loaded / 戻り / true | 良い | - | - |
| `testGetMountPointReturnsNull` | 取得 / mount / point / 戻り / null | ゆっくり本体の状態/行動/イベント回帰 / 取得 / mount / point / 戻り / null | 良い | - | - |
| `testGetMountPointWithEmptyKey` | 取得 / mount / point / with / empty / key | ゆっくり本体の状態/行動/イベント回帰 / 取得 / mount / point / with / empty / key | 良い | - | - |
| `testForceSetHappinessVeryHappy` | force / 設定 / 幸福 / very / happy | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / very / happy | 良い | - | - |
| `testForceSetHappinessHappy` | force / 設定 / 幸福 / happy | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / happy | 良い | - | - |
| `testForceSetHappinessAverage` | force / 設定 / 幸福 / average | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / average | 良い | - | - |
| `testForceSetHappinessSad` | force / 設定 / 幸福 / sad | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / sad | 良い | - | - |
| `testForceSetHappinessVerySad` | force / 設定 / 幸福 / very / sad | ゆっくり本体の状態/行動/イベント回帰 / force / 設定 / 幸福 / very / sad | 良い | - | - |
| `testSetExpandSizeW` | 設定 / expand / size / w | ゆっくり本体の状態/行動/イベント回帰 / 設定 / expand / size / w | 良い | - | - |
| `testAllMethodsCovered` | all / methods / covered | ゆっくり本体の状態/行動/イベント回帰 / all / methods / covered | 良い | - | - |

### `StubBodyTest`
- 状態: 完了 (18/18 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | ゆっくり本体の状態/行動/イベント回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | ゆっくり本体の状態/行動/イベント回帰 / parameterized / constructor | 良い | - | - |
| `testGetTypeReturnsZero` | 取得 / type / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / type / 戻り / zero | 良い | - | - |
| `testGetNameJ` | 取得 / name / j | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j | 良い | - | - |
| `testGetNameE` | 取得 / name / e | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e | 良い | - | - |
| `testGetNameJ2` | 取得 / name / j2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / j2 | 良い | - | - |
| `testGetNameE2` | 取得 / name / e2 | ゆっくり本体の状態/行動/イベント回帰 / 取得 / name / e2 | 良い | - | - |
| `testGetMyName` | 取得 / my / name | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name | 良い | - | - |
| `testGetMyNameD` | 取得 / my / name / d | ゆっくり本体の状態/行動/イベント回帰 / 取得 / my / name / d | 良い | - | - |
| `testGetImageReturnsZero` | 取得 / image / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / 戻り / zero | 良い | - | - |
| `testGetImageWithVariousArgs` | 取得 / image / with / various / args | ゆっくり本体の状態/行動/イベント回帰 / 取得 / image / with / various / args | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | ゆっくり本体の状態/行動/イベント回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testIsImageLoadedReturnsTrue` | 状態 / image / loaded / 戻り / true | ゆっくり本体の状態/行動/イベント回帰 / 状態 / image / loaded / 戻り / true | 良い | - | - |
| `testGetMountPointReturnsNull` | 取得 / mount / point / 戻り / null | ゆっくり本体の状態/行動/イベント回帰 / 取得 / mount / point / 戻り / null | 良い | - | - |
| `testGetPivotXreturnsZero` | 取得 / pivot / xreturns / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / pivot / xreturns / zero | 良い | - | - |
| `testGetPivotYreturnsZero` | 取得 / pivot / yreturns / zero | ゆっくり本体の状態/行動/イベント回帰 / 取得 / pivot / yreturns / zero | 良い | - | - |
| `testCheckNonYukkuriDiseaseToleranceReturnsZero` | 判定 / non / yukkuri / disease / tolerance / 戻り / zero | ゆっくり本体の状態/行動/イベント回帰 / 判定 / non / yukkuri / disease / tolerance / 戻り / zero | 良い | - | - |
| `testAllMethodsCovered` | all / methods / covered | ゆっくり本体の状態/行動/イベント回帰 / all / methods / covered | 良い | - | - |

### `YukkuriAbuseDelegateTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `takeBraid_turnsOnBraidWhenBraidType` | take / おさげ / turns / on / おさげ / when / おさげ / type | ゆっくり本体の状態/行動/イベント回帰 / take / おさげ / turns / on / おさげ / when / おさげ / type | 良い | - | - |
| `peal_turnsOnPealedAndBaldHead` | peal / turns / on / pealed / and / bald / head | ゆっくり本体の状態/行動/イベント回帰 / peal / turns / on / pealed / and / bald / head | 良い | - | - |
| `pack_turnsOnPackedAndDisablesTalk` | pack / turns / on / packed / and / disables / talk | ゆっくり本体の状態/行動/イベント回帰 / pack / turns / on / packed / and / disables / talk | 良い | - | - |
| `breakeyes_turnsOnBlind` | breakeyes / turns / on / blind | ゆっくり本体の状態/行動/イベント回帰 / breakeyes / turns / on / blind | 良い | - | - |
| `shutMouth_turnsOnShutmouth` | shut / 口 / turns / on / shutmouth | ゆっくり本体の状態/行動/イベント回帰 / shut / 口 / turns / on / shutmouth | 良い | - | - |
| `pickHair_fromBaldHeadRestoresDefault` | pick / 毛 / from / bald / head / restores / default | ゆっくり本体の状態/行動/イベント回帰 / pick / 毛 / from / bald / head / restores / default | 良い | - | - |

### `YukkuriEventDelegateTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `clearActionsClearsPlayingAndMoveTarget` | clear / actions / clears / playing / and / 移動 / target | ゆっくり本体の状態/行動/イベント回帰 / clear / actions / clears / playing / and / 移動 / target | 良い | - | - |
| `forceToSleepPutsBodyToSleep` | force / to / 睡眠 / puts / 本体 / to / 睡眠 | ゆっくり本体の状態/行動/イベント回帰 / force / to / 睡眠 / puts / 本体 / to / 睡眠 | 良い | - | - |
| `begForLifeForcedAddsBodyEvent` | beg / for / life / forced / adds / 本体 / イベント | ゆっくり本体の状態/行動/イベント回帰 / beg / for / life / forced / adds / 本体 / イベント | 良い | - | - |
| `canEventResponse_returnsFalseWhenBlindAndNotCutPeni` | can / イベント / response / returns / false / when / blind / and / 非 / cut / peni | ゆっくり本体の状態/行動/イベント回帰 / can / イベント / response / returns / false / when / blind / and / 非 / cut / peni | 良い | - | - |
| `canEventResponse_returnsTrueWhenBlindButCutPeniEventIsQueued` | can / イベント / response / returns / true / when / blind / but / cut / peni / イベント / 状態 / queued | ゆっくり本体の状態/行動/イベント回帰 / can / イベント / response / returns / true / when / blind / but / cut / peni / イベント / 状態 / queued | 良い | - | - |
| `isCutPeni_returnsTrueWhenFirstEventIsCutPenipeniEvent` | is / cut / peni / returns / true / when / first / イベント / 状態 / cut / penipeni / イベント | ゆっくり本体の状態/行動/イベント回帰 / is / cut / peni / returns / true / when / first / イベント / 状態 / cut / penipeni / イベント | 良い | - | - |

### `YukkuriMessageDelegateTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 座標変換や幾何値が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `setMessageDelegatesToBodyEventState` | set / メッセージ / delegates / to / 本体 / イベント / state | ゆっくり本体の状態/行動/イベント回帰 / set / メッセージ / delegates / to / 本体 / イベント / state | 良い | - | - |
| `setOrigMessageLineColorDelegatesToLivingEntityColor` | set / orig / メッセージ / line / color / delegates / to / living / entity / color | ゆっくり本体の状態/行動/イベント回帰 / set / orig / メッセージ / line / color / delegates / to / living / entity / color | 良い | - | - |

### `YukkuriMoveDelegateTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `setToFoodAndClearTargetsChangePurposeOfMoving` | set / to / food / and / 解除 / targets / change / purpose / of / moving | ゆっくり本体の状態/行動/イベント回帰 / set / to / food / and / 解除 / targets / change / purpose / of / moving | 良い | - | - |

### `YukkuriNydDelegateTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `hasNonYukkuriDisease_defaultBodyReturnsFalse` | has / non / yukkuri / disease / default / 本体 / 戻り / false | ゆっくり本体の状態/行動/イベント回帰 / has / non / yukkuri / disease / default / 本体 / 戻り / false | 良い | - | - |

### `YukkuriOtherRelationDelegateTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 家族・関係データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `doSurisuriReturnsWithoutThrowingWhenActorIsDead` | do / surisuri / 戻り / without / throwing / when / actor / 状態 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / 戻り / without / throwing / when / actor / 状態 / 死亡 | 良い | - | - |
| `doPeroperoReturnsWithoutThrowingWhenActorIsDead` | do / peropero / 戻り / without / throwing / when / actor / 状態 / 死亡 | ゆっくり本体の状態/行動/イベント回帰 / do / peropero / 戻り / without / throwing / when / actor / 状態 / 死亡 | 良い | - | - |

### `YukkuriPlayerRelationDelegateTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `doSurisuriByPlayerReturnsFalseWhenNotFlagged` | do / surisuri / by / player / 戻り / false / when / 非 / flagged | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / by / player / 戻り / false / when / 非 / flagged | 良い | - | - |
| `doSurisuriByPlayerInitialResponseUpdatesTime` | do / surisuri / by / player / initial / response / updates / time | ゆっくり本体の状態/行動/イベント回帰 / do / surisuri / by / player / initial / response / updates / time | 良い | - | - |

### `YukkuriSexualDelegateTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `forceToRaperExciteMakesBodyExcitingAndClearsPartner` | force / to / raper / excite / makes / 本体 / exciting / and / clears / 相手 | ゆっくり本体の状態/行動/イベント回帰 / force / to / raper / excite / makes / 本体 / exciting / and / clears / 相手 | 良い | - | - |
| `doSukkiriSetsBothBodiesToSukkiri` | do / sukkiri / sets / both / bodies / to / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / do / sukkiri / sets / both / bodies / to / sukkiri | 良い | - | - |
| `doRapeSetsBothBodiesToSukkiri` | do / rape / sets / both / bodies / to / sukkiri | ゆっくり本体の状態/行動/イベント回帰 / do / rape / sets / both / bodies / to / sukkiri | 良い | - | - |

### `YukkuriShitDelegateTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - ゆっくり本体の状態/行動/イベント回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `checkShitReturnsFalseForIdleBody` | check / shit / 戻り / false / for / idle / 本体 | ゆっくり本体の状態/行動/イベント回帰 / check / shit / 戻り / false / for / idle / 本体 | 良い | - | - |

### `YukkuriSpriteDelegateTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `isUnyoActionAllReturnsTrueWhenShitting` | is / unyo / action / all / 戻り / true / when / shitting | ゆっくり本体の状態/行動/イベント回帰 / is / unyo / action / all / 戻り / true / when / shitting | 良い | - | - |
| `resetUnyoClearsOffsets` | reset / unyo / clears / offsets | ゆっくり本体の状態/行動/イベント回帰 / reset / unyo / clears / offsets | 良い | - | - |

### `YukkuriStalkDelegateTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `ゆっくり本体の状態/行動/イベント回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - プロパティの更新と保持が壊れない
  - 世界状態の保存/復元と進行が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `setUnBirthDisablesTalkAndEnablesBirthFlags` | set / un / birth / disables / talk / and / enables / birth / flags | ゆっくり本体の状態/行動/イベント回帰 / set / un / birth / disables / talk / and / enables / birth / flags | 良い | - | - |
| `setUnBirthForLoadKeepsBirthState` | set / un / birth / for / ロード / 維持 / birth / state | ゆっくり本体の状態/行動/イベント回帰 / set / un / birth / for / ロード / 維持 / birth / state | 良い | - | - |
| `detachFromStalkClearsBindingAndParentLink` | detach / from / stalk / clears / binding / and / 親 / link | ゆっくり本体の状態/行動/イベント回帰 / detach / from / stalk / clears / binding / and / 親 / link | 良い | - | - |
| `getStalksDequeue_returnsFirstStalkAndRemovesIt` | get / stalks / dequeue / returns / first / stalk / and / removes / it | ゆっくり本体の状態/行動/イベント回帰 / get / stalks / dequeue / returns / first / stalk / and / removes / it | 良い | - | - |

## `org.simyukkuri.entity.core.meta`
### `PlayerTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor` | constructor | Entity/世界実体の基盤回帰 / constructor | 良い | - | - |
| `testCashOperations` | cash / operations | Entity/世界実体の基盤回帰 / cash / operations | 良い | - | - |
| `testItemList` | item / list | Entity/世界実体の基盤回帰 / item / list | 良い | - | - |
| `testHoldItem` | 拘束 / item | Entity/世界実体の基盤回帰 / 拘束/掴み回帰 | 良い | - | - |
| `testSetItemForSave` | 設定 / item / for / 保存 | Entity/世界実体の基盤回帰 / 設定 / item / for / 保存 | 良い | - | - |
| `testScenarioAddCashStillSucceedsWhenWorldExistsButPlayerStatusUiIsNotInitialized` | シナリオ / 追加 / cash / still / succeeds / when / world / exists / but / player / status / ui / 状態 / 非 / initialized | Entity/世界実体の基盤回帰 / シナリオ / 追加 / cash / still / succeeds / when / world / exists / but / player / status / ui / 状態 / 非 / initialized | 良い | - | - |

## `org.simyukkuri.entity.core.world`
### `ObjEXTest`
- 状態: 完了 (39/39 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - メニュー構成と選択状態が壊れない
  - 座標変換や幾何値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorSetsFields` | constructor / sets / fields | Entity/世界実体の基盤回帰 / constructor / sets / fields | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testClockTickRemovedCallsRemoveListData` | clock / tick / removed / calls / 除去 / list / data | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGrabbedNoMovement` | clock / tick / grabbed / なし / movement | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMoveX` | clock / tick / 移動 / x | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXboundsLow` | clock / tick / xbounds / low | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXboundsHigh` | clock / tick / xbounds / high | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMoveY` | clock / tick / 移動 / y | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYboundsLow` | clock / tick / ybounds / low | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYboundsHigh` | clock / tick / ybounds / high | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZgravity` | clock / tick / zgravity | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZlandsAtMostDepth` | clock / tick / zlands / at / most / depth | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZfallingUnderGround` | clock / tick / zfalling / under / ground | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | assert:0 |
| `testClockTickZplatformType` | clock / tick / zplatform / type | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNoMovementWhenVelocityZero` | clock / tick / なし / movement / when / velocity / zero | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickXbarrierHit` | clock / tick / xbarrier / hit | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickYbarrierHit` | clock / tick / ybarrier / hit | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNormalReturnsDoNothing` | clock / tick / normal / 戻り / do / nothing | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testCheckIntervalTrue` | 判定 / interval / true | Entity/世界実体の基盤回帰 / 判定 / interval / true | 良い | - | - |
| `testCheckIntervalFalse` | 判定 / interval / false | Entity/世界実体の基盤回帰 / 判定 / interval / false | 良い | - | - |
| `testInvertEnabled` | invert / enabled | Entity/世界実体の基盤回帰 / invert / enabled | 良い | - | - |
| `testHasSetupMenuReturnsFalse` | 有無 / setup / menu / 戻り / false | Entity/世界実体の基盤回帰 / 有無 / setup / menu / 戻り / false | 良い | - | - |
| `testEnableHitCheckReturnsTrue` | enable / hit / 判定 / 戻り / true | Entity/世界実体の基盤回帰 / enable / hit / 判定 / 戻り / true | 良い | - | - |
| `testGetHitCheckObjTypeReturnsZero` | 取得 / hit / 判定 / obj / type / 戻り / zero | Entity/世界実体の基盤回帰 / 取得 / hit / 判定 / obj / type / 戻り / zero | 良い | - | - |
| `testObjHitProcessReturnsZero` | obj / hit / process / 戻り / zero | Entity/世界実体の基盤回帰 / obj / hit / process / 戻り / zero | 良い | - | - |
| `testCheckHitObjNullReturnsFalse` | 判定 / hit / obj / null / 戻り / false | Entity/世界実体の基盤回帰 / 判定 / hit / obj / null / 戻り / false | 良い | - | - |
| `testCheckHitObjZnonZeroAndBcheckZfalse` | 判定 / hit / obj / znon / zero / and / bcheck / zfalse | Entity/世界実体の基盤回帰 / 判定 / hit / obj / znon / zero / and / bcheck / zfalse | 良い | - | - |
| `testCheckHitObjInsideReturnsTrue` | 判定 / hit / obj / inside / 戻り / true | Entity/世界実体の基盤回帰 / 判定 / hit / obj / inside / 戻り / true | 良い | - | - |
| `testCheckHitObjOutsideReturnsFalse` | 判定 / hit / obj / outside / 戻り / false | Entity/世界実体の基盤回帰 / 判定 / hit / obj / outside / 戻り / false | 良い | - | - |
| `testCheckHitObjRectNullReturnsFalse` | 判定 / hit / obj / rect / null / 戻り / false | Entity/世界実体の基盤回帰 / 判定 / hit / obj / rect / null / 戻り / false | 良い | - | - |
| `testCheckHitObjRectObjZnonZeroReturnsTrue` | 判定 / hit / obj / rect / obj / znon / zero / 戻り / true | Entity/世界実体の基盤回帰 / 判定 / hit / obj / rect / obj / znon / zero / 戻り / true | 良い | - | - |
| `testCheckHitObjRectInsideProcessesAndReturnsFalse` | 判定 / hit / obj / rect / inside / processes / and / 戻り / false | Entity/世界実体の基盤回帰 / 判定 / hit / obj / rect / inside / processes / and / 戻り / false | 良い | - | - |
| `testGetCollisionRectUsesPivotAndSize` | 取得 / 衝突 / rect / uses / pivot / and / size | Entity/世界実体の基盤回帰 / 取得 / 衝突 / rect / uses / pivot / and / size | 良い | - | - |
| `testTmpPosGetterSetter` | tmp / pos / getter / setter | Entity/世界実体の基盤回帰 / tmp / pos / getter / setter | 良い | - | - |
| `testGettersAndSetters` | getters / and / setters | Entity/世界実体の基盤回帰 / getters / and / setters | 良い | - | - |
| `testItemRankValues` | item / rank / values | Entity/世界実体の基盤回帰 / item / rank / values | 良い | - | - |
| `testConstants` | constants | Entity/世界実体の基盤回帰 / constants | 良い | - | - |
| `testScenarioClockTickAppliesVelocityAndKnockbackThenResetsBvector` | シナリオ / clock / tick / applies / velocity / and / knockback / then / resets / bvector | Entity/世界実体の基盤回帰 / シナリオ / clock / tick / applies / velocity / and / knockback / then / resets / bvector | 良い | - | - |
| `testScenarioRemovedObjReturnsRemovedWithoutRunningUpdate` | シナリオ / removed / obj / 戻り / removed / without / running / 更新 | Entity/世界実体の基盤回帰 / シナリオ / removed / obj / 戻り / removed / without / running / 更新 | 良い | - | - |

## `org.simyukkuri.entity.core.world.bodylinked`
### `OkazariTest`
- 状態: 完了 (19/19 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testOkazariTypeEnum` | okazari / type / enum | Entity/世界実体の基盤回帰 / okazari / type / enum | 良い | - | - |
| `testDefaultConstructor` | default / constructor | Entity/世界実体の基盤回帰 / default / constructor | 良い | - | - |
| `testConstructorWithDefaultType` | constructor / with / default / type | Entity/世界実体の基盤回帰 / constructor / with / default / type | 良い | - | - |
| `testConstructorWithNamedTypeAndBodyInWorld` | constructor / with / named / type / and / 本体 / in / world | Entity/世界実体の基盤回帰 / constructor / with / named / type / and / 本体 / in / world | 良い | - | - |
| `testConstructorWithNamedTypeAndBodyNotInWorld` | constructor / with / named / type / and / 本体 / 非 / in / world | Entity/世界実体の基盤回帰 / constructor / with / named / type / and / 本体 / 非 / in / world | 良い | - | - |
| `testGetRandomOkazariForBaby` | 取得 / random / okazari / for / baby | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / baby | 良い | - | - |
| `testGetRandomOkazariForChild` | 取得 / random / okazari / for / 子 | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / 子 | 良い | - | - |
| `testGetRandomOkazariForAdult` | 取得 / random / okazari / for / adult | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / adult | 良い | - | - |
| `testGetRandomOkazariForBaby2` | 取得 / random / okazari / for / baby2 | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / baby2 | 良い | - | - |
| `testGetRandomOkazariForChild1` | 取得 / random / okazari / for / child1 | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / child1 | 良い | - | - |
| `testGetRandomOkazariForAdult1` | 取得 / random / okazari / for / adult1 | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / adult1 | 良い | - | - |
| `testGetRandomOkazariForAdult2` | 取得 / random / okazari / for / adult2 | Entity/世界実体の基盤回帰 / 取得 / random / okazari / for / adult2 | 良い | - | - |
| `testGetRandomOkazariUsesInjectedRandomSource` | 取得 / random / okazari / uses / injected / random / source | Entity/世界実体の基盤回帰 / 取得 / random / okazari / uses / injected / random / source | 良い | - | - |
| `testGetOkazariImage` | 取得 / okazari / image | Entity/世界実体の基盤回帰 / 取得 / okazari / image | 良い | - | - |
| `testGetOkazariImageDirection1` | 取得 / okazari / image / direction1 | Entity/世界実体の基盤回帰 / 取得 / okazari / image / direction1 | 良い | - | - |
| `testTakeOkazariOfsPosReturnsNullWhenOffsetPosNull` | take / okazari / ofs / pos / 戻り / null / when / offset / pos / null | Entity/世界実体の基盤回帰 / take / okazari / ofs / pos / 戻り / null / when / offset / pos / null | 良い | - | - |
| `testTakeOkazariOfsPosReturnsNullWhenOwnerNotInWorld` | take / okazari / ofs / pos / 戻り / null / when / owner / 非 / in / world | Entity/世界実体の基盤回帰 / take / okazari / ofs / pos / 戻り / null / when / owner / 非 / in / world | 良い | - | - |
| `testTakeOkazariOfsPosReturnsCorrectPos` | take / okazari / ofs / pos / 戻り / correct / pos | Entity/世界実体の基盤回帰 / take / okazari / ofs / pos / 戻り / correct / pos | 良い | - | - |
| `testGettersAndSetters` | getters / and / setters | Entity/世界実体の基盤回帰 / getters / and / setters | 良い | - | - |

### `StalkTest`
- 状態: 完了 (37/37 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - メニュー構成と選択状態が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testStalkId` | stalk / id | Entity/世界実体の基盤回帰 / stalk / id | 良い | - | - |
| `testDirection` | direction | Entity/世界実体の基盤回帰 / direction | 良い | - | - |
| `testPlantYukkuri` | plant / yukkuri | Entity/世界実体の基盤回帰 / plant / yukkuri | 良い | - | - |
| `testAmount` | amount | Entity/世界実体の基盤回帰 / amount | 良い | - | - |
| `testConstructorWithCoords` | constructor / with / coords | Entity/世界実体の基盤回帰 / constructor / with / coords | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | Entity/世界実体の基盤回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testObjHitProcess` | obj / hit / process | Entity/世界実体の基盤回帰 / obj / hit / process | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | Entity/世界実体の基盤回帰 / 除去フラグ回帰 | 良い | - | - |
| `testSetPlantYukkuriWithBody` | 設定 / plant / yukkuri / with / 本体 | Entity/世界実体の基盤回帰 / 設定 / plant / yukkuri / with / 本体 | 良い | - | - |
| `testSetPlantYukkuriWithNull` | 設定 / plant / yukkuri / with / null | Entity/世界実体の基盤回帰 / 設定 / plant / yukkuri / with / null | 良い | - | - |
| `testDetachFromStalkPreventsRebindOnUpdate` | detach / from / stalk / prevents / rebind / on / 更新 | Entity/世界実体の基盤回帰 / detach / from / stalk / prevents / rebind / on / 更新 | 良い | - | - |
| `testDetachFromStalkAllowsFallAfterRelease` | detach / from / stalk / allows / 落下 / after / release | Entity/世界実体の基盤回帰 / detach / from / stalk / allows / 落下 / after / release | 良い | - | - |
| `testSetBindBabyAndGetBindBabies` | 設定 / bind / baby / and / 取得 / bind / babies | Entity/世界実体の基盤回帰 / 設定 / bind / baby / and / 取得 / bind / babies | 良い | - | - |
| `testSetBindBabies` | 設定 / bind / babies | Entity/世界実体の基盤回帰 / 設定 / bind / babies | 良い | - | - |
| `testDisBindBabys` | dis / bind / babys | Entity/世界実体の基盤回帰 / dis / bind / babys | 良い | - | - |
| `testIsPlantYukkuriNoParent` | 状態 / plant / yukkuri / なし / 親 | Entity/世界実体の基盤回帰 / 状態 / plant / yukkuri / なし / 親 | 良い | - | - |
| `testIsPlantYukkuriWithParentId` | 状態 / plant / yukkuri / with / 親 / id | Entity/世界実体の基盤回帰 / 状態 / plant / yukkuri / with / 親 / id | 良い | - | - |
| `testEatStalkReducesAmount` | eat / stalk / reduces / amount | Entity/世界実体の基盤回帰 / eat / stalk / reduces / amount | 良い | - | - |
| `testEatStalkToZeroRemoves` | eat / stalk / to / zero / removes | Entity/世界実体の基盤回帰 / eat / stalk / to / zero / removes | 良い | - | - |
| `testGrab` | grab | Entity/世界実体の基盤回帰 / grab | 良い | - | - |
| `testTakePlantYukkuriNull` | take / plant / yukkuri / null | Entity/世界実体の基盤回帰 / take / plant / yukkuri / null | 良い | - | - |
| `testUpDateWithNullBindBabies` | up / date / with / null / bind / babies | Entity/世界実体の基盤回帰 / up / date / with / null / bind / babies | 良い | - | - |
| `testUpDateWithEmptyBindBabies` | up / date / with / empty / bind / babies | Entity/世界実体の基盤回帰 / up / date / with / empty / bind / babies | 良い | - | - |
| `testClockTickRemovedState` | clock / tick / removed / state | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNormal` | clock / tick / normal | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testCalcXyz` | calc / xyz | Entity/世界実体の基盤回帰 / calc / xyz | 良い | - | - |
| `testHasGetPopup` | 有無 / 取得 / popup | Entity/世界実体の基盤回帰 / 有無 / 取得 / popup | 良い | - | - |
| `testRemove` | 除去 | Entity/世界実体の基盤回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | Entity/世界実体の基盤回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetShadowImagePlantedReturnsNull` | 取得 / shadow / image / planted / 戻り / null | Entity/世界実体の基盤回帰 / 取得 / shadow / image / planted / 戻り / null | 良い | - | - |
| `testGetImageLayerOption0DoesNotThrow` | 取得 / image / layer / option0 / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / image / layer / option0 / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerOption1DoesNotThrow` | 取得 / image / layer / option1 / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / image / layer / option1 / does / 非 / 例外 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testScenarioUpdateLinksUnbornBabyToParentAndAppliesRightFacingOffsets` | シナリオ / 更新 / links / unborn / baby / to / 親 / and / applies / right / facing / offsets | Entity/世界実体の基盤回帰 / シナリオ / 更新 / links / unborn / baby / to / 親 / and / applies / right / facing / offsets | 良い | - | - |
| `testScenarioUpdateWithLeftFacingStalkMirrorsBabyPlacement` | シナリオ / 更新 / with / left / facing / stalk / mirrors / baby / placement | Entity/世界実体の基盤回帰 / シナリオ / 更新 / with / left / facing / stalk / mirrors / baby / placement | 良い | - | - |
| `testScenarioEatStalkToZeroUnbindsBabyAndRemovesStalkFromWorld` | シナリオ / eat / stalk / to / zero / unbinds / baby / and / removes / stalk / from / world | Entity/世界実体の基盤回帰 / シナリオ / eat / stalk / to / zero / unbinds / baby / and / removes / stalk / from / world | 良い | - | - |
| `testScenarioGrabDetachesFromParentStalkListAndClearsPlantOwner` | シナリオ / grab / detaches / from / 親 / stalk / list / and / clears / plant / owner | Entity/世界実体の基盤回帰 / シナリオ / grab / detaches / from / 親 / stalk / list / and / clears / plant / owner | 良い | - | - |

## `org.simyukkuri.entity.core.world.mobile`
### `ShitTest`
- 状態: 完了 (35/35 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetShitState` | 取得 / shit / state | Entity/世界実体の基盤回帰 / 取得 / shit / state | 良い | - | - |
| `testEatShit` | eat / shit | Entity/世界実体の基盤回帰 / eat / shit | 良い | - | - |
| `testCrushShit` | crush / shit | Entity/世界実体の基盤回帰 / crush / shit | 良い | - | - |
| `testGetValue` | 取得 / value | Entity/世界実体の基盤回帰 / 取得 / value | 良い | - | - |
| `testToString` | to / string | Entity/世界実体の基盤回帰 / to / string | 良い | - | - |
| `testGettersSetters` | getters / setters | Entity/世界実体の基盤回帰 / getters / setters | 良い | - | - |
| `testGetSetAmount` | 取得 / 設定 / amount | Entity/世界実体の基盤回帰 / 取得 / 設定 / amount | 良い | - | - |
| `testAgeStateGetterSetter` | age / state / getter / setter | Entity/世界実体の基盤回帰 / age / state / getter / setter | 良い | - | - |
| `testHasGetPopup` | 有無 / 取得 / popup | Entity/世界実体の基盤回帰 / 有無 / 取得 / popup | 良い | - | - |
| `testHasUsePopup` | 有無 / use / popup | Entity/世界実体の基盤回帰 / 有無 / use / popup | 良い | - | - |
| `testKickAdultSetsVelocity` | kick / adult / sets / velocity | Entity/世界実体の基盤回帰 / kick / adult / sets / velocity | 良い | - | - |
| `testKickChildSetsVelocity` | kick / 子 / sets / velocity | Entity/世界実体の基盤回帰 / kick / 子 / sets / velocity | 良い | - | - |
| `testKickBabySetsVelocity` | kick / baby / sets / velocity | Entity/世界実体の基盤回帰 / kick / baby / sets / velocity | 良い | - | - |
| `testClockTickRemovedReturnsRemoved` | clock / tick / removed / 戻り / removed | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNotRemovedBelowLimitReturnsDonothing` | clock / tick / 非 / removed / below / limit / 戻り / donothing | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNotRemovedAgeExceedsLimitRemoves` | clock / tick / 非 / removed / age / exceeds / limit / removes | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickWithVxBoundaryCheck` | clock / tick / with / vx / boundary / 判定 | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVxBeyondMapBounces` | clock / tick / vx / beyond / map / bounces | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVxBelowZeroBounces` | clock / tick / vx / below / zero / bounces | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickWithVy` | clock / tick / with / vy | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVyBeyondMapClamps` | clock / tick / vy / beyond / map / clamps | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVyBelowZeroClamps` | clock / tick / vy / below / zero / clamps | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickFallingZdoesNotThrow` | clock / tick / falling / zdoes / 非 / 例外 | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGrabbedSkipsMovement` | clock / tick / grabbed / skips / movement | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testToStringNullOwner` | to / string / null / owner | Entity/世界実体の基盤回帰 / to / string / null / owner | 良い | - | - |
| `testGetShitStateChild` | 取得 / shit / state / 子 | Entity/世界実体の基盤回帰 / 取得 / shit / state / 子 | 良い | - | - |
| `testGetShitStateBaby` | 取得 / shit / state / baby | Entity/世界実体の基盤回帰 / 取得 / shit / state / baby | 良い | - | - |
| `testGetImageDoesNotThrow` | 取得 / image / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / image / does / 非 / 例外 | 良い | - | - |
| `testGetShadowImageDoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testGetSizeDoesNotThrow` | 取得 / size / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / size / does / 非 / 例外 | 良い | - | - |
| `testShitConstants` | shit / constants | Entity/世界実体の基盤回帰 / shit / constants | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | Entity/世界実体の基盤回帰 / constructor / with / args / headless / executes / code | 良い | - | assert:0 |
| `testScenarioClockTickAtRightEdgeBouncesAndClampsX` | シナリオ / clock / tick / at / right / edge / bounces / and / clamps / x | Entity/世界実体の基盤回帰 / シナリオ / clock / tick / at / right / edge / bounces / and / clamps / x | 良い | - | - |
| `testScenarioFallingImpactCrushesShitAndResetsMotion` | シナリオ / falling / impact / crushes / shit / and / resets / motion | Entity/世界実体の基盤回帰 / シナリオ / falling / impact / crushes / shit / and / resets / motion | 良い | - | - |

### `VomitTest`
- 状態: 完了 (37/37 良い)
- クラス要約: `Entity/世界実体の基盤回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetVomitState` | 取得 / vomit / state | Entity/世界実体の基盤回帰 / 取得 / vomit / state | 良い | - | - |
| `testEatVomit` | eat / vomit | Entity/世界実体の基盤回帰 / eat / vomit | 良い | - | - |
| `testCrushVomit` | crush / vomit | Entity/世界実体の基盤回帰 / crush / vomit | 良い | - | - |
| `testGetValue` | 取得 / value | Entity/世界実体の基盤回帰 / 取得 / value | 良い | - | - |
| `testToString` | to / string | Entity/世界実体の基盤回帰 / to / string | 良い | - | - |
| `testGettersSetters` | getters / setters | Entity/世界実体の基盤回帰 / getters / setters | 良い | - | - |
| `testGetAgeStateAdult` | 取得 / age / state / adult | Entity/世界実体の基盤回帰 / 取得 / age / state / adult | 良い | - | - |
| `testGetAgeStateBaby` | 取得 / age / state / baby | Entity/世界実体の基盤回帰 / 取得 / age / state / baby | 良い | - | - |
| `testGetAgeStateChild` | 取得 / age / state / 子 | Entity/世界実体の基盤回帰 / 取得 / age / state / 子 | 良い | - | - |
| `testHasGetPopup` | 有無 / 取得 / popup | Entity/世界実体の基盤回帰 / 有無 / 取得 / popup | 良い | - | - |
| `testHasUsePopup` | 有無 / use / popup | Entity/世界実体の基盤回帰 / 有無 / use / popup | 良い | - | - |
| `testKickAdult` | kick / adult | Entity/世界実体の基盤回帰 / kick / adult | 良い | - | - |
| `testKickChild` | kick / 子 | Entity/世界実体の基盤回帰 / kick / 子 | 良い | - | - |
| `testKickBaby` | kick / baby | Entity/世界実体の基盤回帰 / kick / baby | 良い | - | - |
| `testClockTickAlreadyRemovedReturnsRemoved` | clock / tick / already / removed / 戻り / removed | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNotRemovedAgeUnderLimitReturnsDonothing` | clock / tick / 非 / removed / age / under / limit / 戻り / donothing | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNotRemovedAgeOverLimitSetsRemoved` | clock / tick / 非 / removed / age / over / limit / sets / removed | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testVomitConstants` | vomit / constants | Entity/世界実体の基盤回帰 / vomit / constants | 良い | - | - |
| `testGetAmountDefaultZero` | 取得 / amount / default / zero | Entity/世界実体の基盤回帰 / 取得 / amount / default / zero | 良い | - | - |
| `testSetAmount` | 設定 / amount | Entity/世界実体の基盤回帰 / 設定 / amount | 良い | - | - |
| `testGetOwnerNameDefaultUnknown` | 取得 / owner / name / default / unknown | Entity/世界実体の基盤回帰 / 取得 / owner / name / default / unknown | 良い | - | - |
| `testSetOwnerName` | 設定 / owner / name | Entity/世界実体の基盤回帰 / 設定 / owner / name | 良い | - | - |
| `testGetVomitTypeDefault` | 取得 / vomit / type / default | Entity/世界実体の基盤回帰 / 取得 / vomit / type / default | 良い | - | - |
| `testGetFalldownDamageDefault` | 取得 / falldown / ダメージ / default | Entity/世界実体の基盤回帰 / 取得 / falldown / ダメージ / default | 良い | - | - |
| `testVomitStateBaby` | vomit / state / baby | Entity/世界実体の基盤回帰 / vomit / state / baby | 良い | - | - |
| `testVomitStateChild` | vomit / state / 子 | Entity/世界実体の基盤回帰 / vomit / state / 子 | 良い | - | - |
| `testGetImageDoesNotThrow` | 取得 / image / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / image / does / 非 / 例外 | 良い | - | - |
| `testGetShadowImageDoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testGetSizeDoesNotThrow` | 取得 / size / does / 非 / 例外 | Entity/世界実体の基盤回帰 / 取得 / size / does / 非 / 例外 | 良い | - | - |
| `testClockTickVxNonZeroDoesNotThrow` | clock / tick / vx / non / zero / does / 非 / 例外 | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVxNegativeClampsToZero` | clock / tick / vx / negative / clamps / to / zero | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickVyNonZeroDoesNotThrow` | clock / tick / vy / non / zero / does / 非 / 例外 | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZnonZeroDoesNotThrow` | clock / tick / znon / zero / does / 非 / 例外 | Entity/世界実体の基盤回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | Entity/世界実体の基盤回帰 / 状態保存復元確認 | 良い | - | assert:0 |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | Entity/世界実体の基盤回帰 / constructor / with / args / headless / executes / code | 良い | - | assert:0 |
| `testScenarioClockTickAtRightEdgeBouncesAndClampsX` | シナリオ / clock / tick / at / right / edge / bounces / and / clamps / x | Entity/世界実体の基盤回帰 / シナリオ / clock / tick / at / right / edge / bounces / and / clamps / x | 良い | - | - |
| `testScenarioFallingImpactCrushesVomitAndResetsMotion` | シナリオ / falling / impact / crushes / vomit / and / resets / motion | Entity/世界実体の基盤回帰 / シナリオ / falling / impact / crushes / vomit / and / resets / motion | 良い | - | - |

## `org.simyukkuri.enums`
### `AgeStateTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testEnumValues` | enum / values | enum 値対応回帰 / enum / values | 良い | - | - |

### `AttachPropertyTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - enum 値対応回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testValues` | values | enum 値対応回帰 / values | 良い | - | - |

### `AttitudeTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - enum 値対応回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAttitudeEnum` | attitude / enum | enum 値対応回帰 / attitude / enum | 良い | - | - |

### `BodyRankTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - enum 値対応回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBodyRankProperties` | 本体 / rank / properties | enum 値対応回帰 / 本体 / rank / properties | 良い | - | - |
| `testSpecificValues` | specific / values | enum 値対応回帰 / specific / values | 良い | - | - |

### `ImageCodeTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBurned` | burned | enum 値対応回帰 / burned | 良い | - | - |
| `testBody` | 本体 | enum 値対応回帰 / 本体 | 良い | - | - |
| `testBraid` | おさげ | enum 値対応回帰 / おさげ | 良い | - | - |

### `NumberingTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testNumberingObjId` | numbering / obj / id | enum 値対応回帰 / numbering / obj / id | 良い | - | - |
| `testSetAndGetObjId` | 設定 / and / 取得 / obj / id | enum 値対応回帰 / 設定 / and / 取得 / obj / id | 良い | - | - |
| `testNumberingYukkuriId` | numbering / yukkuri / id | enum 値対応回帰 / numbering / yukkuri / id | 良い | - | - |
| `testSetAndGetYukkuriId` | 設定 / and / 取得 / yukkuri / id | enum 値対応回帰 / 設定 / and / 取得 / yukkuri / id | 良い | - | - |
| `testSingletonInstance` | singleton / instance | enum 値対応回帰 / singleton / instance | 良い | - | - |

### `SimpleEnumsTest`
- 状態: 完了 (32/32 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 世界状態の保存/復元と進行が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testActionState` | action / state | enum 値対応回帰 / action / state | 良い | - | assert:0 |
| `testBurialState` | burial / state | enum 値対応回帰 / burial / state | 良い | - | assert:0 |
| `testBodyBake` | 本体 / bake | enum 値対応回帰 / 本体 / bake | 良い | - | assert:0 |
| `testBurst` | 破裂 | enum 値対応回帰 / 破裂 | 良い | - | assert:0 |
| `testCoreAnkoState` | core / anko / state | enum 値対応回帰 / core / anko / state | 良い | - | assert:0 |
| `testCriticalDamageType` | critical / ダメージ / type | enum 値対応回帰 / critical / ダメージ / type | 良い | - | assert:0 |
| `testDamage` | ダメージ | enum 値対応回帰 / ダメージ | 良い | - | assert:0 |
| `testDirection` | direction | enum 値対応回帰 / direction | 良い | - | assert:0 |
| `testEffectType` | effect / type | enum 値対応回帰 / effect / type | 良い | - | assert:0 |
| `testYukkuriRelationType` | yukkuri / relation / type | enum 値対応回帰 / yukkuri / relation / type | 良い | - | assert:0 |
| `testTickResult` | tick / result | enum 値対応回帰 / tick / result | 良い | - | assert:0 |
| `testFavItemType` | fav / item / type | enum 値対応回帰 / fav / item / type | 良い | - | assert:0 |
| `testFootBake` | foot / bake | enum 値対応回帰 / foot / bake | 良い | - | assert:0 |
| `testGatheringDirection` | gathering / direction | enum 値対応回帰 / gathering / direction | 良い | - | assert:0 |
| `testHairState` | 毛 / state | enum 値対応回帰 / 毛 / state | 良い | - | assert:0 |
| `testHappiness` | 幸福 | enum 値対応回帰 / 幸福 | 良い | - | assert:0 |
| `testIntelligence` | intelligence | enum 値対応回帰 / intelligence | 良い | - | assert:0 |
| `testLovePlayer` | love / player | enum 値対応回帰 / love / player | 良い | - | assert:0 |
| `testPain` | pain | enum 値対応回帰 / pain | 良い | - | assert:0 |
| `testPanicType` | 恐慌 / type | enum 値対応回帰 / 恐慌 / type | 良い | - | assert:0 |
| `testParent` | 親 | enum 値対応回帰 / 親 | 良い | - | assert:0 |
| `testPlayStyle` | play / style | enum 値対応回帰 / play / style | 良い | - | assert:0 |
| `testPredatorType` | predator / type | enum 値対応回帰 / predator / type | 良い | - | assert:0 |
| `testPublicRank` | public / rank | enum 値対応回帰 / public / rank | 良い | - | assert:0 |
| `testPurposeOfMoving` | purpose / of / moving | enum 値対応回帰 / purpose / of / moving | 良い | - | assert:0 |
| `testTakeoutItemType` | takeout / item / type | enum 値対応回帰 / takeout / item / type | 良い | - | assert:0 |
| `testTangType` | tang / type | enum 値対応回帰 / tang / type | 良い | - | assert:0 |
| `testTrauma` | trauma | enum 値対応回帰 / trauma | 良い | - | assert:0 |
| `testType` | type | enum 値対応回帰 / type | 良い | - | assert:0 |
| `testUnbirthBabyState` | unbirth / baby / state | enum 値対応回帰 / unbirth / baby / state | 良い | - | assert:0 |
| `testWhere` | where | enum 値対応回帰 / where | 良い | - | assert:0 |
| `testWindowType` | window / type | enum 値対応回帰 / window / type | 良い | - | assert:0 |

### `WorldEntityKindTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetClassPack` | 取得 / class / 梱包 | enum 値対応回帰 / 取得 / class / 梱包 | 良い | - | - |

### `YukkuriTypeTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `enum 値対応回帰`
- 回帰目的:
  - enum 値対応回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testEnumProperties` | enum / properties | enum 値対応回帰 / enum / properties | 良い | - | - |
| `testAllTypesHaveName` | all / types / have / name | enum 値対応回帰 / all / types / have / name | 良い | - | - |
| `testTypeIdsAreUnique` | type / ids / are / unique | enum 値対応回帰 / type / ids / are / unique | 良い | - | - |
| `testLookupByClassNameAndTypeId` | lookup / by / class / name / and / type / id | enum 値対応回帰 / lookup / by / class / name / and / type / id | 良い | - | - |
| `testNormalizeOffspringType` | normalize / offspring / type | enum 値対応回帰 / normalize / offspring / type | 良い | - | - |

## `org.simyukkuri.event`
### `EventPacketTest`
- 状態: 完了 (64/64 良い)
- クラス要約: `イベントパケット回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testConstructorWithBothBodies` | constructor / with / both / bodies | イベントパケット回帰 / constructor / with / both / bodies | 良い | - | - |
| `testConstructorWithNullTo` | constructor / with / null / to | イベントパケット回帰 / constructor / with / null / to | 良い | - | - |
| `testConstructorWithNullFrom` | constructor / with / null / from | イベントパケット回帰 / constructor / with / null / from | 良い | - | - |
| `testCountDown` | count / down | イベントパケット回帰 / count / down | 良い | - | - |
| `testSetFromWithBody` | 設定 / from / with / 本体 | イベントパケット回帰 / 設定 / from / with / 本体 | 良い | - | - |
| `testSetFromWithNull` | 設定 / from / with / null | イベントパケット回帰 / 設定 / from / with / null | 良い | - | - |
| `testSetToWithBody` | 設定 / to / with / 本体 | イベントパケット回帰 / 設定 / to / with / 本体 | 良い | - | - |
| `testSetTargetWithObj` | 設定 / target / with / obj | イベントパケット回帰 / 設定 / target / with / obj | 良い | - | - |
| `testSimpleEventActionReturnsFalse` | simple / イベント / action / 戻り / false | イベントパケット回帰 / simple / イベント / action / 戻り / false | 良い | - | - |
| `testUpdateReturnsNull` | 更新 / 戻り / null | イベントパケット回帰 / 更新 / 戻り / null | 良い | - | - |
| `testEndDoesNotThrow` | end / does / 非 / 例外 | イベントパケット回帰 / end / does / 非 / 例外 | 良い | - | assert:0 |
| `testPriorities` | priorities | イベントパケット回帰 / priorities | 良い | - | - |
| `testCoordinateSetters` | coordinate / setters | イベントパケット回帰 / coordinate / setters | 良い | - | - |
| `testIntSetters` | int / setters | イベントパケット回帰 / int / setters | 良い | - | - |
| `testEventPriorityEnum` | イベント / priority / enum | イベントパケット回帰 / イベント / priority / enum | 良い | - | - |
| `testUpdateStateEnum` | 更新 / state / enum | イベントパケット回帰 / 更新 / state / enum | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベントパケット回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityHigh` | 判定 / イベント / response / sets / priority / high | イベントパケット回帰 / 判定 / イベント / response / sets / priority / high | 良い | - | - |
| `testCheckEventResponseTrueForFromBody` | 判定 / イベント / response / true / for / from / 本体 | イベントパケット回帰 / 判定 / イベント / response / true / for / from / 本体 | 良い | - | - |
| `testCheckEventResponseFalseForOtherBody` | 判定 / イベント / response / false / for / other / 本体 | イベントパケット回帰 / 判定 / イベント / response / false / for / other / 本体 | 良い | - | - |
| `testUpdateTick0SetsPenipeniCutAndLockmove` | 更新 / tick0 / sets / penipeni / cut / and / lockmove | イベントパケット回帰 / 更新 / tick0 / sets / penipeni / cut / and / lockmove | 良い | - | - |
| `testEndSetsExpectedState` | end / sets / expected / state | イベントパケット回帰 / end / sets / expected / state | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベントパケット回帰 / execute / 戻り / true | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベントパケット回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseTrueWhenFromMatches` | 判定 / イベント / response / true / when / from / matches | イベントパケット回帰 / 判定 / イベント / response / true / when / from / matches | 良い | - | - |
| `testCheckEventResponseFalseWhenNotFrom` | 判定 / イベント / response / false / when / 非 / from | イベントパケット回帰 / 判定 / イベント / response / false / when / 非 / from | 良い | - | - |
| `testCheckEventResponseFalseWhenSuperShithead` | 判定 / イベント / response / false / when / super / shithead | イベントパケット回帰 / 判定 / イベント / response / false / when / super / shithead | 良い | - | - |
| `testCheckEventResponseFalseWhenDead` | 判定 / イベント / response / false / when / 死亡 | イベントパケット回帰 / 判定 / イベント / response / false / when / 死亡 | 良い | - | - |
| `testEndClearsLockmove` | end / clears / lockmove | イベントパケット回帰 / end / clears / lockmove | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベントパケット回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityMiddle` | 判定 / イベント / response / sets / priority / middle | イベントパケット回帰 / 判定 / イベント / response / sets / priority / middle | 良い | - | - |
| `testCheckEventResponseFalseWhenFromNull` | 判定 / イベント / response / false / when / from / null | イベントパケット回帰 / 判定 / イベント / response / false / when / from / null | 良い | - | - |
| `testCheckEventResponseFalseWhenFromEqualsB` | 判定 / イベント / response / false / when / from / equals / b | イベントパケット回帰 / 判定 / イベント / response / false / when / from / equals / b | 良い | - | - |
| `testCheckEventResponseFalseWhenDead` | 判定 / イベント / response / false / when / 死亡 | イベントパケット回帰 / 判定 / イベント / response / false / when / 死亡 | 良い | - | - |
| `testCheckEventResponseFalseWhenBaryStateNotNone` | 判定 / イベント / response / false / when / bary / state / 非 / none | イベントパケット回帰 / 判定 / イベント / response / false / when / bary / state / 非 / none | 良い | - | - |
| `testCheckEventResponseFalseWhenPublicRankMismatch` | 判定 / イベント / response / false / when / public / rank / mismatch | イベントパケット回帰 / 判定 / イベント / response / false / when / public / rank / mismatch | 良い | - | - |
| `testCheckEventResponseTrueWhenPartner` | 判定 / イベント / response / true / when / 相手 | イベントパケット回帰 / 判定 / イベント / response / true / when / 相手 | 良い | - | - |
| `testUpdateAbortWhenFromNull` | 更新 / abort / when / from / null | イベントパケット回帰 / 更新 / abort / when / from / null | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityHigh` | parameterized / constructor / sets / priority / high | イベントパケット回帰 / parameterized / constructor / sets / priority / high | 良い | - | - |
| `testCheckEventResponseTrueForFrom` | 判定 / イベント / response / true / for / from | イベントパケット回帰 / 判定 / イベント / response / true / for / from | 良い | - | - |
| `testCheckEventResponseTrueForTo` | 判定 / イベント / response / true / for / to | イベントパケット回帰 / 判定 / イベント / response / true / for / to | 良い | - | - |
| `testCheckEventResponseFalseForOther` | 判定 / イベント / response / false / for / other | イベントパケット回帰 / 判定 / イベント / response / false / for / other | 良い | - | - |
| `testAcceptProposeTrueWhenEligible` | accept / propose / true / when / eligible | イベントパケット回帰 / accept / propose / true / when / eligible | 良い | - | - |
| `testAcceptProposeFalseWhenToHasPartner` | accept / propose / false / when / to / 有無 / 相手 | イベントパケット回帰 / accept / propose / false / when / to / 有無 / 相手 | 良い | - | - |
| `testAcceptProposeFalseWhenFromHasDisorder` | accept / propose / false / when / from / 有無 / disorder | イベントパケット回帰 / accept / propose / false / when / from / 有無 / disorder | 良い | - | - |
| `testAcceptProposeFalseWhenFromHasBabyOrStalk` | accept / propose / false / when / from / 有無 / baby / or / stalk | イベントパケット回帰 / accept / propose / false / when / from / 有無 / baby / or / stalk | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testCheckEventResponseAlwaysTrue` | 判定 / イベント / response / always / true | イベントパケット回帰 / 判定 / イベント / response / always / true | 良い | - | - |
| `testCheckEventResponseSetsPriorityHigh` | 判定 / イベント / response / sets / priority / high | イベントパケット回帰 / 判定 / イベント / response / sets / priority / high | 良い | - | - |
| `testStartClearsActionFlags` | start / clears / action / flags | イベントパケット回帰 / start / clears / action / flags | 良い | - | - |
| `testUpdateAbortWhenToNull` | 更新 / abort / when / to / null | イベントパケット回帰 / 更新 / abort / when / to / null | 良い | - | - |
| `testUpdateAbortWhenToRemoved` | 更新 / abort / when / to / removed | イベントパケット回帰 / 更新 / abort / when / to / removed | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testCheckEventResponseAlwaysFalse` | 判定 / イベント / response / always / false | イベントパケット回帰 / 判定 / イベント / response / always / false | 良い | - | - |
| `testSimpleEventActionFalseWhenFromIsB` | simple / イベント / action / false / when / from / 状態 / b | イベントパケット回帰 / simple / イベント / action / false / when / from / 状態 / b | 良い | - | - |
| `testSimpleEventActionFalseWhenFromIsNull` | simple / イベント / action / false / when / from / 状態 / null | イベントパケット回帰 / simple / イベント / action / false / when / from / 状態 / null | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベントパケット回帰 / execute / 戻り / true | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベントパケット回帰 / default / constructor | 良い | - | - |
| `testCheckEventResponseTrueAndSetsPriorityMiddle` | 判定 / イベント / response / true / and / sets / priority / middle | イベントパケット回帰 / 判定 / イベント / response / true / and / sets / priority / middle | 良い | - | - |

## `org.simyukkuri.event.impl`
### `AvoidMoldEventTest`
- 状態: 完了 (43/43 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityMiddle` | 判定 / イベント / response / sets / priority / middle | イベント進行回帰 / 判定 / イベント / response / sets / priority / middle | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnunSlave` | 判定 / イベント / response / 戻り / false / for / unun / slave | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unun / slave | 良い | - | - |
| `testCheckEventResponseReturnsFalseForIdiot` | 判定 / イベント / response / 戻り / false / for / idiot | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / idiot | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenCanEventResponseIsFalse` | 判定 / イベント / response / 戻り / false / when / 可否 / イベント / response / 状態 / false | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / 可否 / イベント / response / 状態 / false | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsNull` | 更新 / 戻り / abort / when / to / 状態 / null | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / null | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsDead` | 更新 / 戻り / abort / when / to / 状態 / 死亡 | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / 死亡 | 良い | - | - |
| `testExecuteReturnsTrueWhenFromIsNull` | execute / 戻り / true / when / from / 状態 / null | イベント進行回帰 / execute / 戻り / true / when / from / 状態 / null | 良い | - | - |
| `testUpdateToRemovedReturnsAbort` | 更新 / to / removed / 戻り / abort | イベント進行回帰 / 更新 / to / removed / 戻り / abort | 良い | - | - |
| `testStartToNullDoesNotThrow` | start / to / null / does / 非 / 例外 | イベント進行回帰 / start / to / null / does / 非 / 例外 | 良い | - | - |
| `testExecuteToNullReturnsTrue` | execute / to / null / 戻り / true | イベント進行回帰 / execute / to / null / 戻り / true | 良い | - | - |
| `testUpdateToAliveReturnsNull` | 更新 / to / alive / 戻り / null | イベント進行回帰 / 更新 / to / alive / 戻り / null | 良い | - | - |
| `testStartToNotNullDoesNotThrow` | start / to / 非 / null / does / 非 / 例外 | イベント進行回帰 / start / to / 非 / null / does / 非 / 例外 | 良い | - | - |
| `testExecuteFromAdultNotFamilyDoesNotThrow` | execute / from / adult / 非 / 家族 / does / 非 / 例外 | イベント進行回帰 / execute / from / adult / 非 / 家族 / does / 非 / 例外 | 良い | - | - |
| `testSaySadMessageFromNullDoesNotThrow` | say / sad / メッセージ / from / null / does / 非 / 例外 | イベント進行回帰 / say / sad / メッセージ / from / null / does / 非 / 例外 | 良い | - | - |
| `testSaySadMessageFromExistsDoesNotThrow` | say / sad / メッセージ / from / exists / does / 非 / 例外 | イベント進行回帰 / say / sad / メッセージ / from / exists / does / 非 / 例外 | 良い | - | - |
| `testSayApologyMessageFromNullDoesNotThrow` | say / apology / メッセージ / from / null / does / 非 / 例外 | イベント進行回帰 / say / apology / メッセージ / from / null / does / 非 / 例外 | 良い | - | - |
| `testSayApologyMessageFromExistsDoesNotThrow` | say / apology / メッセージ / from / exists / does / 非 / 例外 | イベント進行回帰 / say / apology / メッセージ / from / exists / does / 非 / 例外 | 良い | - | - |
| `testExecuteFromVeryRudeDoesNotThrow` | execute / from / very / rude / does / 非 / 例外 | イベント進行回帰 / execute / from / very / rude / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultParentFoolDoesNotThrow` | execute / adult / 親 / fool / does / 非 / 例外 | イベント進行回帰 / execute / adult / 親 / fool / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultParentWiseDoesNotThrow` | execute / adult / 親 / wise / does / 非 / 例外 | イベント進行回帰 / execute / adult / 親 / wise / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultParentDefaultDoesNotThrow` | execute / adult / 親 / default / does / 非 / 例外 | イベント進行回帰 / execute / adult / 親 / default / does / 非 / 例外 | 良い | - | - |
| `testExecuteChildFromNotFamilyDoesNotThrow` | execute / 子 / from / 非 / 家族 / does / 非 / 例外 | イベント進行回帰 / execute / 子 / from / 非 / 家族 / does / 非 / 例外 | 良い | - | - |
| `testExecuteChildFromIsChildFoolDoesNotThrow` | execute / 子 / from / 状態 / 子 / fool / does / 非 / 例外 | イベント進行回帰 / execute / 子 / from / 状態 / 子 / fool / does / 非 / 例外 | 良い | - | - |
| `testUpdateToDeadReturnsAbort` | 更新 / to / 死亡 / 戻り / abort | イベント進行回帰 / 更新 / to / 死亡 / 戻り / abort | 良い | - | - |
| `testExecuteAdultFromIsTalkingReturnsTrue` | execute / adult / from / 状態 / talking / 戻り / true | イベント進行回帰 / execute / adult / from / 状態 / talking / 戻り / true | 良い | - | - |
| `testExecuteFromVeryRudeFoolDoesNotThrow` | execute / from / very / rude / fool / does / 非 / 例外 | イベント進行回帰 / execute / from / very / rude / fool / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultParentFoolSaySadMessageReturnsFalse` | execute / adult / 親 / fool / say / sad / メッセージ / 戻り / false | イベント進行回帰 / execute / adult / 親 / fool / say / sad / メッセージ / 戻り / false | 良い | - | - |
| `testExecuteAdultParentWiseReturnsFalse` | execute / adult / 親 / wise / 戻り / false | イベント進行回帰 / execute / adult / 親 / wise / 戻り / false | 良い | - | - |
| `testExecuteAdultParentDefaultTrueWhenApology` | execute / adult / 親 / default / true / when / apology | イベント進行回帰 / execute / adult / 親 / default / true / when / apology | 良い | - | - |
| `testExecuteAdultFamilyFoolDoesNotThrow` | execute / adult / 家族 / fool / does / 非 / 例外 | イベント進行回帰 / execute / adult / 家族 / fool / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultFamilyWiseDoesNotThrow` | execute / adult / 家族 / wise / does / 非 / 例外 | イベント進行回帰 / execute / adult / 家族 / wise / does / 非 / 例外 | 良い | - | - |
| `testExecuteAdultFamilyDefaultDoesNotThrow` | execute / adult / 家族 / default / does / 非 / 例外 | イベント進行回帰 / execute / adult / 家族 / default / does / 非 / 例外 | 良い | - | - |
| `testExecuteChildFromIsChildWiseDoesNotThrow` | execute / 子 / from / 状態 / 子 / wise / does / 非 / 例外 | イベント進行回帰 / execute / 子 / from / 状態 / 子 / wise / does / 非 / 例外 | 良い | - | - |
| `testExecuteChildFromIsFamilyFoolDoesNotThrow` | execute / 子 / from / 状態 / 家族 / fool / does / 非 / 例外 | イベント進行回帰 / execute / 子 / from / 状態 / 家族 / fool / does / 非 / 例外 | 良い | - | - |
| `testExecuteChildFromIsFamilyWiseDoesNotThrow` | execute / 子 / from / 状態 / 家族 / wise / does / 非 / 例外 | イベント進行回帰 / execute / 子 / from / 状態 / 家族 / wise / does / 非 / 例外 | 良い | - | - |
| `testSaySadMessageIsParentDoesNotThrow` | say / sad / メッセージ / 状態 / 親 / does / 非 / 例外 | イベント進行回帰 / say / sad / メッセージ / 状態 / 親 / does / 非 / 例外 | 良い | - | - |
| `testSaySadMessageIsPartnerDoesNotThrow` | say / sad / メッセージ / 状態 / 相手 / does / 非 / 例外 | イベント進行回帰 / say / sad / メッセージ / 状態 / 相手 / does / 非 / 例外 | 良い | - | - |
| `testSaySadMessageToIsParentDoesNotThrow` | say / sad / メッセージ / to / 状態 / 親 / does / 非 / 例外 | イベント進行回帰 / say / sad / メッセージ / to / 状態 / 親 / does / 非 / 例外 | 良い | - | - |
| `testSayApologyMessageIsParentDoesNotThrow` | say / apology / メッセージ / 状態 / 親 / does / 非 / 例外 | イベント進行回帰 / say / apology / メッセージ / 状態 / 親 / does / 非 / 例外 | 良い | - | - |
| `testScenarioVeryRudeFoolSanctionsMoldyTargetAndGetsPuffFace` | シナリオ / very / rude / fool / sanctions / moldy / target / and / gets / puff / 表情 | イベント進行回帰 / シナリオ / very / rude / fool / sanctions / moldy / target / and / gets / puff / 表情 | 良い | - | - |

### `BegForLifeEventTest`
- 状態: 完了 (18/18 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityHigh` | 判定 / イベント / response / sets / priority / high | イベント進行回帰 / 判定 / イベント / response / sets / priority / high | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenBequalsFromAndNotUnBirth` | 判定 / イベント / response / 戻り / true / when / bequals / from / and / 非 / un / birth | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / bequals / from / and / 非 / un / birth | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisNotFrom` | 判定 / イベント / response / 戻り / false / when / bis / 非 / from | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / 非 / from | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisUnBirth` | 判定 / イベント / response / 戻り / false / when / bis / un / birth | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / un / birth | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベント進行回帰 / execute / 戻り / true | 良い | - | - |
| `testEndSetsBeggingToFalse` | end / sets / begging / to / false | イベント進行回帰 / end / sets / begging / to / false | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick0DoesNotThrow` | 更新 / tick0 / does / 非 / 例外 | イベント進行回帰 / 更新 / tick0 / does / 非 / 例外 | 良い | - | - |
| `testUpdateBtalkingReturnsNull` | 更新 / btalking / 戻り / null | イベント進行回帰 / 更新 / btalking / 戻り / null | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick7RoopNotZeroSetsBegging` | 更新 / tick7 / roop / 非 / zero / sets / begging | イベント進行回帰 / 更新 / tick7 / roop / 非 / zero / sets / begging | 良い | - | - |
| `testUpdateRoop0Roop2NotZeroBegForLife` | 更新 / roop0 / roop2 / 非 / zero / beg / for / life | イベント進行回帰 / 更新 / roop0 / roop2 / 非 / zero / beg / for / life | 良い | - | - |
| `testUpdateWait30AllRoopZeroReturnsAbort` | 更新 / wait30 / all / roop / zero / 戻り / abort | イベント進行回帰 / 更新 / wait30 / all / roop / zero / 戻り / abort | 良い | - | - |
| `testUpdateWait50DamagedSetsMessage` | 更新 / wait50 / damaged / sets / メッセージ | イベント進行回帰 / 更新 / wait50 / damaged / sets / メッセージ | 良い | - | - |
| `testUpdateWait50NotDamagedThanksPath` | 更新 / wait50 / 非 / damaged / thanks / path | イベント進行回帰 / 更新 / wait50 / 非 / damaged / thanks / path | 良い | - | - |
| `testScenarioNotDamagedAverageBodyEndsBeggingVeryHappyAndSmiling` | シナリオ / 非 / damaged / average / 本体 / ends / begging / very / happy / and / smiling | イベント進行回帰 / シナリオ / 非 / damaged / average / 本体 / ends / begging / very / happy / and / smiling | 良い | - | - |

### `BreedEventTest`
- 状態: 完了 (33/33 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEventResponseParentParticipates` | 判定 / イベント / response / 親 / participates | イベント進行回帰 / 判定 / イベント / response / 親 / participates | 良い | - | - |
| `testCheckEventResponseBabyChildOfFromDoesNotParticipate` | 判定 / イベント / response / baby / 子 / of / from / does / 非 / participate | イベント進行回帰 / 判定 / イベント / response / baby / 子 / of / from / does / 非 / participate | 良い | - | - |
| `testCheckEventResponseBabyChildOfFromBirthEventBlockedDoesNotParticipate` | 判定 / イベント / response / baby / 子 / of / from / birth / イベント / blocked / does / 非 / participate | イベント進行回帰 / 判定 / イベント / response / baby / 子 / of / from / birth / イベント / blocked / does / 非 / participate | 良い | - | - |
| `testCheckEventResponseBabyChildOfFromWithoutBirthMessageParticipates` | 判定 / イベント / response / baby / 子 / of / from / without / birth / メッセージ / participates | イベント進行回帰 / 判定 / イベント / response / baby / 子 / of / from / without / birth / メッセージ / participates | 良い | - | - |
| `testCheckEventResponseStrangerDoesNotParticipate` | 判定 / イベント / response / stranger / does / 非 / participate | イベント進行回帰 / 判定 / イベント / response / stranger / does / 非 / participate | 良い | - | - |
| `testDefaultConstructorDoesNotThrow` | default / constructor / does / 非 / 例外 | イベント進行回帰 / default / constructor / does / 非 / 例外 | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testStartFromNullDoesNotThrow` | start / from / null / does / 非 / 例外 | イベント進行回帰 / start / from / null / does / 非 / 例外 | 良い | - | - |
| `testStartFromExistsDoesNotThrow` | start / from / exists / does / 非 / 例外 | イベント進行回帰 / start / from / exists / does / 非 / 例外 | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateFromExistsDoesNotThrow` | 更新 / from / exists / does / 非 / 例外 | イベント進行回帰 / 更新 / from / exists / does / 非 / 例外 | 良い | - | - |
| `testExecuteFromNullReturnsTrue` | execute / from / null / 戻り / true | イベント進行回帰 / execute / from / null / 戻り / true | 良い | - | - |
| `testExecuteFromExistsDoesNotThrow` | execute / from / exists / does / 非 / 例外 | イベント進行回帰 / execute / from / exists / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseBnearToBirthReturnsFalse` | 判定 / イベント / response / bnear / to / birth / 戻り / false | イベント進行回帰 / 判定 / イベント / response / bnear / to / birth / 戻り / false | 良い | - | - |
| `testCheckEventResponseBisUnBirthReturnsFalse` | 判定 / イベント / response / bis / un / birth / 戻り / false | イベント進行回帰 / 判定 / イベント / response / bis / un / birth / 戻り / false | 良い | - | - |
| `testCheckEventResponseBisRaperExcitingReturnsFalse` | 判定 / イベント / response / bis / raper / exciting / 戻り / false | イベント進行回帰 / 判定 / イベント / response / bis / raper / exciting / 戻り / false | 良い | - | - |
| `testCheckEventResponseDifferentPublicRankReturnsFalse` | 判定 / イベント / response / different / public / rank / 戻り / false | イベント進行回帰 / 判定 / イベント / response / different / public / rank / 戻り / false | 良い | - | - |
| `testCheckEventResponseBburiedReturnsFalse` | 判定 / イベント / response / bburied / 戻り / false | イベント進行回帰 / 判定 / イベント / response / bburied / 戻り / false | 良い | - | - |
| `testCheckEventResponseFromNoOkazariFoolBreturnsFalse` | 判定 / イベント / response / from / なし / okazari / fool / breturns / false | イベント進行回帰 / 判定 / イベント / response / from / なし / okazari / fool / breturns / false | 良い | - | - |
| `testCheckEventResponseFromIsPartnerOfBreturnsTrue` | 判定 / イベント / response / from / 状態 / 相手 / of / breturns / true | イベント進行回帰 / 判定 / イベント / response / from / 状態 / 相手 / of / breturns / true | 良い | - | - |
| `testCheckEventResponseBisParentOfFromReturnsTrue` | 判定 / イベント / response / bis / 親 / of / from / 戻り / true | イベント進行回帰 / 判定 / イベント / response / bis / 親 / of / from / 戻り / true | 良い | - | - |
| `testUpdateBnearToBirthReturnsForceExec` | 更新 / bnear / to / birth / 戻り / force / exec | イベント進行回帰 / 更新 / bnear / to / birth / 戻り / force / exec | 良い | - | - |
| `testUpdateCloseDistanceReturnsForceExec` | 更新 / close / distance / 戻り / force / exec | イベント進行回帰 / 更新 / close / distance / 戻り / force / exec | 良い | - | - |
| `testUpdateBabyChildOfFromReturnsAbort` | 更新 / baby / 子 / of / from / 戻り / abort | イベント進行回帰 / 更新 / baby / 子 / of / from / 戻り / abort | 良い | - | - |
| `testUpdateBabyChildBirthEventBlockedReturnsAbort` | 更新 / baby / 子 / birth / イベント / blocked / 戻り / abort | イベント進行回帰 / 更新 / baby / 子 / birth / イベント / blocked / 戻り / abort | 良い | - | - |
| `testUpdateFromDeadReturnsAbort` | 更新 / from / 死亡 / 戻り / abort | イベント進行回帰 / 更新 / from / 死亡 / 戻り / abort | 良い | - | - |
| `testExecuteBnearToBirthReturnsTrue` | execute / bnear / to / birth / 戻り / true | イベント進行回帰 / execute / bnear / to / birth / 戻り / true | 良い | - | - |
| `testExecuteBisNydreturnsFalse` | execute / bis / nydreturns / false | イベント進行回帰 / execute / bis / nydreturns / false | 良い | - | - |
| `testExecuteFromIsBirthReturnsTrue` | execute / from / 状態 / birth / 戻り / true | イベント進行回帰 / execute / from / 状態 / birth / 戻り / true | 良い | - | - |
| `testExecuteFromHasPantsReturnsTrue` | execute / from / 有無 / pants / 戻り / true | イベント進行回帰 / execute / from / 有無 / pants / 戻り / true | 良い | - | - |
| `testExecuteFromHasBabyOrStalkReturnsFalse` | execute / from / 有無 / baby / or / stalk / 戻り / false | イベント進行回帰 / execute / from / 有無 / baby / or / stalk / 戻り / false | 良い | - | - |
| `testScenarioBirthSuccessMakesResponderVeryHappyAndAddsGoodMemories` | シナリオ / birth / success / makes / responder / very / happy / and / adds / good / memories | イベント進行回帰 / シナリオ / birth / success / makes / responder / very / happy / and / adds / good / memories | 良い | - | - |
| `testScenarioChildResponderLeavesEventAfterGreeting` | シナリオ / 子 / responder / leaves / イベント / after / greeting | イベント進行回帰 / シナリオ / 子 / responder / leaves / イベント / after / greeting | 良い | - | - |

### `CutPenipeniEventTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEventResponseReturnsTrueForInitiator` | 判定 / イベント / response / 戻り / true / for / initiator | イベント進行回帰 / 判定 / イベント / response / 戻り / true / for / initiator | 良い | - | - |
| `testCheckEventResponseReturnsFalseForStranger` | 判定 / イベント / response / 戻り / false / for / stranger | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / stranger | 良い | - | - |
| `testDefaultConstructorDoesNotThrow` | default / constructor / does / 非 / 例外 | イベント進行回帰 / default / constructor / does / 非 / 例外 | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベント進行回帰 / execute / 戻り / true | 良い | - | - |
| `testEndDoesNotThrow` | end / does / 非 / 例外 | イベント進行回帰 / end / does / 非 / 例外 | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateUnBirthReturnsForceExec` | 更新 / un / birth / 戻り / force / exec | イベント進行回帰 / 更新 / un / birth / 戻り / force / exec | 良い | - | - |
| `testUpdateTick0NormalReturnsNull` | 更新 / tick0 / normal / 戻り / null | イベント進行回帰 / 更新 / tick0 / normal / 戻り / null | 良い | - | - |
| `testUpdateTick20Rnd0DoesNotThrow` | 更新 / tick20 / rnd0 / does / 非 / 例外 | イベント進行回帰 / 更新 / tick20 / rnd0 / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick20Rnd1DoesNotThrow` | 更新 / tick20 / rnd1 / does / 非 / 例外 | イベント進行回帰 / 更新 / tick20 / rnd1 / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick40VeryNiceDoesNotThrow` | 更新 / tick40 / very / nice / does / 非 / 例外 | イベント進行回帰 / 更新 / tick40 / very / nice / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick40NiceDoesNotThrow` | 更新 / tick40 / nice / does / 非 / 例外 | イベント進行回帰 / 更新 / tick40 / nice / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick40AverageDoesNotThrow` | 更新 / tick40 / average / does / 非 / 例外 | イベント進行回帰 / 更新 / tick40 / average / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick40ShitheadDoesNotThrow` | 更新 / tick40 / shithead / does / 非 / 例外 | イベント進行回帰 / 更新 / tick40 / shithead / does / 非 / 例外 | 良い | - | - |
| `testUpdateTick70ReturnsForceExec` | 更新 / tick70 / 戻り / force / exec | イベント進行回帰 / 更新 / tick70 / 戻り / force / exec | 良い | - | - |
| `testScenarioUnBirthVictimBecomesVerySadNonRaperAfterCut` | シナリオ / un / birth / victim / becomes / very / sad / non / raper / after / cut | イベント進行回帰 / シナリオ / un / birth / victim / becomes / very / sad / non / raper / after / cut | 良い | - | - |

### `EatBodyEventTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEventResponseReturnsTrueForEater` | 判定 / イベント / response / 戻り / true / for / eater | イベント進行回帰 / 判定 / イベント / response / 戻り / true / for / eater | 良い | - | - |
| `testCheckEventResponseReturnsFalseForNonEater` | 判定 / イベント / response / 戻り / false / for / non / eater | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / non / eater | 良い | - | - |
| `testCheckEventResponseReturnsFalseForSuperShithead` | 判定 / イベント / response / 戻り / false / for / super / shithead | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / super / shithead | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testEndResetsLockmove` | end / resets / lockmove | イベント進行回帰 / end / resets / lockmove | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testExecuteMultipleTicks` | execute / multiple / ticks | イベント進行回帰 / execute / multiple / ticks | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick120VeryNiceReturnsTrue` | execute / tick120 / very / nice / 戻り / true | イベント進行回帰 / execute / tick120 / very / nice / 戻り / true | 良い | - | - |
| `testExecuteTick120NiceReturnsTrue` | execute / tick120 / nice / 戻り / true | イベント進行回帰 / execute / tick120 / nice / 戻り / true | 良い | - | - |
| `testExecuteTick120AverageReturnsTrue` | execute / tick120 / average / 戻り / true | イベント進行回帰 / execute / tick120 / average / 戻り / true | 良い | - | - |
| `testExecuteTick120ShitheadReturnsTrue` | execute / tick120 / shithead / 戻り / true | イベント進行回帰 / execute / tick120 / shithead / 戻り / true | 良い | - | - |
| `testUpdateReturnsNull` | 更新 / 戻り / null | イベント進行回帰 / 更新 / 戻り / null | 良い | - | - |
| `testStartToNullDoesNotThrow` | start / to / null / does / 非 / 例外 | イベント進行回帰 / start / to / null / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseEaterDeadReturnsFalse` | 判定 / イベント / response / eater / 死亡 / 戻り / false | イベント進行回帰 / 判定 / イベント / response / eater / 死亡 / 戻り / false | 良い | - | - |
| `testExecuteTick10DoesNotThrow` | execute / tick10 / does / 非 / 例外 | イベント進行回帰 / execute / tick10 / does / 非 / 例外 | 良い | - | - |
| `testScenarioAverageEaterFinishesVerySadWithStressPenalty` | シナリオ / average / eater / finishes / very / sad / with / ストレス / penalty | イベント進行回帰 / シナリオ / average / eater / finishes / very / sad / with / ストレス / penalty | 良い | - | - |

### `FavCopyEventTest`
- 状態: 完了 (13/13 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSimpleEventActionReturnsTrue` | simple / イベント / action / 戻り / true | イベント進行回帰 / simple / イベント / action / 戻り / true | 良い | - | - |
| `testSimpleEventActionFromIsBreturnsFalse` | simple / イベント / action / from / 状態 / breturns / false | イベント進行回帰 / simple / イベント / action / from / 状態 / breturns / false | 良い | - | - |
| `testDefaultConstructorDoesNotThrow` | default / constructor / does / 非 / 例外 | イベント進行回帰 / default / constructor / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseReturnsFalse` | 判定 / イベント / response / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 戻り / false | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベント進行回帰 / execute / 戻り / true | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullReturnsFalse` | simple / イベント / action / from / null / 戻り / false | イベント進行回帰 / simple / イベント / action / from / null / 戻り / false | 良い | - | - |
| `testSimpleEventActionBisParentOfFromReturnsTrue` | simple / イベント / action / bis / 親 / of / from / 戻り / true | イベント進行回帰 / simple / イベント / action / bis / 親 / of / from / 戻り / true | 良い | - | - |
| `testSimpleEventActionBisPartnerOfFromReturnsTrue` | simple / イベント / action / bis / 相手 / of / from / 戻り / true | イベント進行回帰 / simple / イベント / action / bis / 相手 / of / from / 戻り / true | 良い | - | - |
| `testSimpleEventActionBothUnunSlaveReturnsTrue` | simple / イベント / action / both / unun / slave / 戻り / true | イベント進行回帰 / simple / イベント / action / both / unun / slave / 戻り / true | 良い | - | - |
| `testSimpleEventActionOneUnunSlaveReturnsTrue` | simple / イベント / action / one / unun / slave / 戻り / true | イベント進行回帰 / simple / イベント / action / one / unun / slave / 戻り / true | 良い | - | - |
| `testScenarioFamilyCopiesFavoriteBedAcrossEvent` | シナリオ / 家族 / copies / favorite / bed / across / イベント | イベント進行回帰 / シナリオ / 家族 / copies / favorite / bed / across / イベント | 良い | - | - |

### `FlyingEatEventTest`
- 状態: 完了 (23/23 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityHighAndReturnsTrue` | 判定 / イベント / response / sets / priority / high / and / 戻り / true | イベント進行回帰 / 判定 / イベント / response / sets / priority / high / and / 戻り / true | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsNull` | 更新 / 戻り / abort / when / to / 状態 / null | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / null | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsRemoved` | 更新 / 戻り / abort / when / to / 状態 / removed | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / removed | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsGrabbed` | 更新 / 戻り / abort / when / to / 状態 / grabbed | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / grabbed | 良い | - | - |
| `testStartToNullDoesNotThrow` | start / to / null / does / 非 / 例外 | イベント進行回帰 / start / to / null / does / 非 / 例外 | 良い | - | - |
| `testExecuteToNullReturnsTrue` | execute / to / null / 戻り / true | イベント進行回帰 / execute / to / null / 戻り / true | 良い | - | - |
| `testExecuteToRemovedReturnsTrue` | execute / to / removed / 戻り / true | イベント進行回帰 / execute / to / removed / 戻り / true | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testEndSetsLinkParentToMinusOne` | end / sets / link / 親 / to / minus / one | イベント進行回帰 / end / sets / link / 親 / to / minus / one | 良い | - | - |
| `testStartToNotNullDoesNotThrow` | start / to / 非 / null / does / 非 / 例外 | イベント進行回帰 / start / to / 非 / null / does / 非 / 例外 | 良い | - | - |
| `testUpdateToAliveNotAtFlyHeightReturnsNull` | 更新 / to / alive / 非 / at / fly / height / 戻り / null | イベント進行回帰 / 更新 / to / alive / 非 / at / fly / height / 戻り / null | 良い | - | - |
| `testUpdateToAliveAtFlyHeightReturnsForceExec` | 更新 / to / alive / at / fly / height / 戻り / force / exec | イベント進行回帰 / 更新 / to / alive / at / fly / height / 戻り / force / exec | 良い | - | - |
| `testExecuteToGrabbedReturnsTrue` | execute / to / grabbed / 戻り / true | イベント進行回帰 / execute / to / grabbed / 戻り / true | 良い | - | - |
| `testExecuteTickLessThan20ReturnsFalse` | execute / tick / less / than20 / 戻り / false | イベント進行回帰 / execute / tick / less / than20 / 戻り / false | 良い | - | - |
| `testExecuteMultipleCallsBelow20AlwaysReturnsFalse` | execute / multiple / calls / below20 / always / 戻り / false | イベント進行回帰 / execute / multiple / calls / below20 / always / 戻り / false | 良い | - | - |
| `testExecuteTick19ToCrushedReturnsTrue` | execute / tick19 / to / crushed / 戻り / true | イベント進行回帰 / execute / tick19 / to / crushed / 戻り / true | 良い | - | - |
| `testExecuteTick19ToDeadRudeEaterReturnsTrue` | execute / tick19 / to / 死亡 / rude / eater / 戻り / true | イベント進行回帰 / execute / tick19 / to / 死亡 / rude / eater / 戻り / true | 良い | - | - |
| `testExecuteTick19ToDeadKaiyuEaterReturnsTrue` | execute / tick19 / to / 死亡 / kaiyu / eater / 戻り / true | イベント進行回帰 / execute / tick19 / to / 死亡 / kaiyu / eater / 戻り / true | 良い | - | - |
| `testExecuteTick19ZeroEatToNotNydreturnsFalse` | execute / tick19 / zero / eat / to / 非 / nydreturns / false | イベント進行回帰 / execute / tick19 / zero / eat / to / 非 / nydreturns / false | 良い | - | - |
| `testExecuteTick19ZeroEatBfullReturnsTrue` | execute / tick19 / zero / eat / bfull / 戻り / true | イベント進行回帰 / execute / tick19 / zero / eat / bfull / 戻り / true | 良い | - | - |
| `testScenarioAlivePreyBecomesVerySadAndPainFacedAtEatTick` | シナリオ / alive / prey / becomes / very / sad / and / pain / faced / at / eat / tick | イベント進行回帰 / シナリオ / alive / prey / becomes / very / sad / and / pain / faced / at / eat / tick | 良い | - | - |

### `FuneralEventTest`
- 状態: 完了 (48/48 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityHigh` | parameterized / constructor / sets / priority / high | イベント進行回帰 / parameterized / constructor / sets / priority / high | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromIsNull` | simple / イベント / action / 戻り / true / when / from / 状態 / null | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / 状態 / null | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromEqualsB` | simple / イベント / action / 戻り / true / when / from / equals / b | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / equals / b | 良い | - | - |
| `testSimpleEventActionReturnsFalseWhenFromIsNotBandNotShutmouth` | simple / イベント / action / 戻り / false / when / from / 状態 / 非 / band / 非 / shutmouth | イベント進行回帰 / simple / イベント / action / 戻り / false / when / from / 状態 / 非 / band / 非 / shutmouth | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromUniqueIdEqualsB` | 判定 / イベント / response / 戻り / true / when / from / unique / id / equals / b | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / unique / id / equals / b | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnunSlave` | 判定 / イベント / response / 戻り / false / for / unun / slave | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unun / slave | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenSourceBodyIsAlreadyInAnotherEvent` | 判定 / イベント / response / 戻り / false / when / source / 本体 / 状態 / already / in / another / イベント | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / source / 本体 / 状態 / already / in / another / イベント | 良い | - | - |
| `testExecuteReturnsFalse` | execute / 戻り / false | イベント進行回帰 / execute / 戻り / false | 良い | - | - |
| `testEndSetsCurrentEventToNull` | end / sets / current / イベント / to / null | イベント進行回帰 / end / sets / current / イベント / to / null | 良い | - | - |
| `testGetStateDefaultIsGo` | 取得 / state / default / 状態 / go | イベント進行回帰 / 取得 / state / default / 状態 / go | 良い | - | - |
| `testSetState` | 設定 / state | イベント進行回帰 / 設定 / state | 良い | - | - |
| `testStateEnumAllValues` | state / enum / all / values | イベント進行回帰 / state / enum / all / values | 良い | - | - |
| `testStartSetsCurrentEvent` | start / sets / current / イベント | イベント進行回帰 / start / sets / current / イベント | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseNoParentsReturnsFalse` | 判定 / イベント / response / なし / parents / 戻り / false | イベント進行回帰 / 判定 / イベント / response / なし / parents / 戻り / false | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateBodyNydreturnsAbort` | 更新 / 本体 / nydreturns / abort | イベント進行回帰 / 更新 / 本体 / nydreturns / abort | 良い | - | - |
| `testUpdateFromRemovedReturnsAbort` | 更新 / from / removed / 戻り / abort | イベント進行回帰 / 更新 / from / removed / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromNoChildrenReturnsAbort` | 更新 / bequals / from / なし / children / 戻り / abort | イベント進行回帰 / 更新 / bequals / from / なし / children / 戻り / abort | 良い | - | - |
| `testUpdatePartnerOfFromStateNotGoreturnsNull` | 更新 / 相手 / of / from / state / 非 / goreturns / null | イベント進行回帰 / 更新 / 相手 / of / from / state / 非 / goreturns / null | 良い | - | - |
| `testUpdateTickNotMultipleOf30ReturnsNull` | 更新 / tick / 非 / multiple / of30 / 戻り / null | イベント進行回帰 / 更新 / tick / 非 / multiple / of30 / 戻り / null | 良い | - | - |
| `testUpdateBisPartnerOfFromStateGodoesNotThrow` | 更新 / bis / 相手 / of / from / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / bis / 相手 / of / from / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateNfromWaitCountOver2000ReturnsAbort` | 更新 / nfrom / wait / count / over2000 / 戻り / abort | イベント進行回帰 / 更新 / nfrom / wait / count / over2000 / 戻り / abort | 良い | - | - |
| `testUpdateChildBodyStateGodoesNotThrow` | 更新 / 子 / 本体 / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateFinddoesNotThrow` | 更新 / 子 / 本体 / state / finddoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / finddoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateStartbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateIntroducebactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / introducebaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / introducebaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateSingbactionFlagFalseDoesNotThrow` | 更新 / 子 / 本体 / state / singbaction / flag / false / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / singbaction / flag / false / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateTalkbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / talkbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / talkbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateGoodbyebactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / goodbyebaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / goodbyebaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateEnddoesNotThrow` | 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | 良い | - | - |
| `testUpdatePartnerOfFromStateNotGostays` | 更新 / 相手 / of / from / state / 非 / gostays | イベント進行回帰 / 更新 / 相手 / of / from / state / 非 / gostays | 良い | - | - |
| `testCheckEventResponsePartnerOfFromHasParentsReturnsTrue` | 判定 / イベント / response / 相手 / of / from / 有無 / parents / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 相手 / of / from / 有無 / parents / 戻り / true | 良い | - | - |
| `testCheckEventResponseNotChildOfFromHasParentsReturnsFalse` | 判定 / イベント / response / 非 / 子 / of / from / 有無 / parents / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 非 / 子 / of / from / 有無 / parents / 戻り / false | 良い | - | - |
| `testCheckEventResponseIsChildOfFromAdultReturnsFalse` | 判定 / イベント / response / 状態 / 子 / of / from / adult / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 状態 / 子 / of / from / adult / 戻り / false | 良い | - | - |
| `testCheckEventResponseIsChildOfFromBabyReturnsTrue` | 判定 / イベント / response / 状態 / 子 / of / from / baby / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 状態 / 子 / of / from / baby / 戻り / true | 良い | - | - |
| `testScenarioChildParticipationMarksHappyAndClearsActions` | シナリオ / 子 / participation / marks / happy / and / clears / actions | イベント進行回帰 / シナリオ / 子 / participation / marks / happy / and / clears / actions | 良い | - | - |
| `testScenarioChildFindForElderSisterSetsVerySadAndCryingFace` | シナリオ / 子 / find / for / elder / 姉妹 / sets / very / sad / and / crying / 表情 | イベント進行回帰 / シナリオ / 子 / find / for / elder / 姉妹 / sets / very / sad / and / crying / 表情 | 良い | - | - |
| `testScenarioFromGoodbyeRemovesDeceasedOkazariAndAddsMemories` | シナリオ / from / goodbye / removes / deceased / okazari / and / adds / memories | イベント進行回帰 / シナリオ / from / goodbye / removes / deceased / okazari / and / adds / memories | 良い | - | - |
| `testScenarioRudeChildGoodbyeCanEnterFurifuriPath` | シナリオ / rude / 子 / goodbye / 可否 / enter / furifuri / path | イベント進行回帰 / シナリオ / rude / 子 / goodbye / 可否 / enter / furifuri / path | 良い | - | - |
| `testUpdateChildBodyGoIsDontMoveReturnsAbort` | 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | イベント進行回帰 / 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromWithBabyChildStateGodoesNotThrow` | 更新 / bequals / from / with / baby / 子 / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / with / baby / 子 / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateFromHighZbnotFromReturnsNull` | 更新 / from / high / zbnot / from / 戻り / null | イベント進行回帰 / 更新 / from / high / zbnot / from / 戻り / null | 良い | - | - |
| `testUpdateFromHighZbequalsFromWithChildDoesNotThrow` | 更新 / from / high / zbequals / from / with / 子 / does / 非 / 例外 | イベント進行回帰 / 更新 / from / high / zbequals / from / with / 子 / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyFindWithToDoesNotThrow` | 更新 / 子 / 本体 / find / with / to / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / find / with / to / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyGoodbyeRudeDoesNotThrow` | 更新 / 子 / 本体 / goodbye / rude / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / goodbye / rude / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyEndRudeDoesNotThrow` | 更新 / 子 / 本体 / end / rude / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / end / rude / does / 非 / 例外 | 良い | - | - |

### `GetTrashOkazariEventTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEventResponseAlwaysReturnsTrue` | 判定 / イベント / response / always / 戻り / true | イベント進行回帰 / 判定 / イベント / response / always / 戻り / true | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testCheckEventResponsePriorityIsMiddle` | 判定 / イベント / response / priority / 状態 / middle | イベント進行回帰 / 判定 / イベント / response / priority / 状態 / middle | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testUpdateWithRemovedTargetAbortsEarly` | 更新 / with / removed / target / aborts / early | イベント進行回帰 / 更新 / with / removed / target / aborts / early | 良い | - | - |
| `testExecuteWithRemovedTargetReturnsTrue` | execute / with / removed / target / 戻り / true | イベント進行回帰 / execute / with / removed / target / 戻り / true | 良い | - | - |
| `testUpdateWithNonRemovedAndNoOkazari` | 更新 / with / non / removed / and / なし / okazari | イベント進行回帰 / 更新 / with / non / removed / and / なし / okazari | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateBodyHasOkazariReturnsAbort` | 更新 / 本体 / 有無 / okazari / 戻り / abort | イベント進行回帰 / 更新 / 本体 / 有無 / okazari / 戻り / abort | 良い | - | - |
| `testScenarioBareBodyKeepsChasingTrashUntilExecution` | シナリオ / bare / 本体 / 維持 / chasing / trash / until / execution | イベント進行回帰 / シナリオ / bare / 本体 / 維持 / chasing / trash / until / execution | 良い | - | - |

### `HateNoOkazariEventTest`
- 状態: 完了 (23/23 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityMiddle` | 判定 / イベント / response / sets / priority / middle | イベント進行回帰 / 判定 / イベント / response / sets / priority / middle | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnunSlave` | 判定 / イベント / response / 戻り / false / for / unun / slave | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unun / slave | 良い | - | - |
| `testCheckEventResponseReturnsFalseForSmart` | 判定 / イベント / response / 戻り / false / for / smart | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / smart | 良い | - | - |
| `testCheckEventResponseReturnsFalseForIdiot` | 判定 / イベント / response / 戻り / false / for / idiot | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / idiot | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenToIsNull` | 判定 / イベント / response / 戻り / false / when / to / 状態 / null | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / to / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenCanEventResponseIsFalse` | 判定 / イベント / response / 戻り / false / when / 可否 / イベント / response / 状態 / false | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / 可否 / イベント / response / 状態 / false | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsNull` | 更新 / 戻り / abort / when / to / 状態 / null | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / null | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsRemoved` | 更新 / 戻り / abort / when / to / 状態 / removed | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / removed | 良い | - | - |
| `testUpdateToAliveReturnsNull` | 更新 / to / alive / 戻り / null | イベント進行回帰 / 更新 / to / alive / 戻り / null | 良い | - | - |
| `testStartToNotNullDoesNotThrow` | start / to / 非 / null / does / 非 / 例外 | イベント進行回帰 / start / to / 非 / null / does / 非 / 例外 | 良い | - | - |
| `testExecuteToNullReturnsTrue` | execute / to / null / 戻り / true | イベント進行回帰 / execute / to / null / 戻り / true | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testExecuteToHighZreturnsTrue` | execute / to / high / zreturns / true | イベント進行回帰 / execute / to / high / zreturns / true | 良い | - | - |
| `testExecuteToDeadReturnsTrue` | execute / to / 死亡 / 戻り / true | イベント進行回帰 / execute / to / 死亡 / 戻り / true | 良い | - | - |
| `testCheckEventResponseBnotPredatorToIsPredatorReturnsFalse` | 判定 / イベント / response / bnot / predator / to / 状態 / predator / 戻り / false | イベント進行回帰 / 判定 / イベント / response / bnot / predator / to / 状態 / predator / 戻り / false | 良い | - | - |
| `testCheckEventResponseHasOkazariIsVeryRudeReturnsTrue` | 判定 / イベント / response / 有無 / okazari / 状態 / very / rude / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 有無 / okazari / 状態 / very / rude / 戻り / true | 良い | - | - |
| `testScenarioVeryRudeHealthyOkazariBodyActuallyJoinsAttack` | シナリオ / very / rude / healthy / okazari / 本体 / actually / joins / attack | イベント進行回帰 / シナリオ / very / rude / healthy / okazari / 本体 / actually / joins / attack | 良い | - | - |
| `testCheckEventResponseWiseToIsPartnerOfBreturnsFalse` | 判定 / イベント / response / wise / to / 状態 / 相手 / of / breturns / false | イベント進行回帰 / 判定 / イベント / response / wise / to / 状態 / 相手 / of / breturns / false | 良い | - | - |
| `testExecuteToRemovedReturnsTrue` | execute / to / removed / 戻り / true | イベント進行回帰 / execute / to / removed / 戻り / true | 良い | - | - |
| `testCheckEventResponseIsRudeNotDamagedReturnsTrue` | 判定 / イベント / response / 状態 / rude / 非 / damaged / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 状態 / rude / 非 / damaged / 戻り / true | 良い | - | - |
| `testUpdateCloseDistanceReturnsNull` | 更新 / close / distance / 戻り / null | イベント進行回帰 / 更新 / close / distance / 戻り / null | 良い | - | - |

### `KillPredeatorEventTest`
- 状態: 完了 (20/20 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityHigh` | 判定 / イベント / response / sets / priority / high | イベント進行回帰 / 判定 / イベント / response / sets / priority / high | 良い | - | - |
| `testCheckEventResponseNoPredatorReturnsFalse` | 判定 / イベント / response / なし / predator / 戻り / false | イベント進行回帰 / 判定 / イベント / response / なし / predator / 戻り / false | 良い | - | - |
| `testCheckEventResponseNyd` | 判定 / イベント / response / 非ゆっくり症 | イベント進行回帰 / 判定 / イベント / response / 非ゆっくり症 | 良い | - | - |
| `testCheckEventResponseDeadBodyReturnsTrue` | 判定 / イベント / response / 死亡 / 本体 / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 死亡 / 本体 / 戻り / true | 良い | - | - |
| `testSearchNextTargetNoPredators` | search / next / target / なし / predators | イベント進行回帰 / search / next / target / なし / predators | 良い | - | - |
| `testSearchNextTargetWithPredator` | search / next / target / with / predator | イベント進行回帰 / search / next / target / with / predator | 良い | - | - |
| `testUpdateFromDeadReturnsAbort` | 更新 / from / 死亡 / 戻り / abort | イベント進行回帰 / 更新 / from / 死亡 / 戻り / abort | 良い | - | - |
| `testExecuteFromNull` | execute / from / null | イベント進行回帰 / execute / from / null | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testStartNydbodyReturnsEarly` | start / nydbody / 戻り / early | イベント進行回帰 / start / nydbody / 戻り / early | 良い | - | - |
| `testStartNotNyddoesNotThrow` | start / 非 / nyddoes / 非 / 例外 | イベント進行回帰 / start / 非 / nyddoes / 非 / 例外 | 良い | - | - |
| `testUpdateRnd0ReturnsAbort` | 更新 / rnd0 / 戻り / abort | イベント進行回帰 / 更新 / rnd0 / 戻り / abort | 良い | - | - |
| `testExecuteFromRemovedWithPredatorReturnsFalse` | execute / from / removed / with / predator / 戻り / false | イベント進行回帰 / execute / from / removed / with / predator / 戻り / false | 良い | - | - |
| `testExecuteFromAliveHighZreturnsFalse` | execute / from / alive / high / zreturns / false | イベント進行回帰 / execute / from / alive / high / zreturns / false | 良い | - | - |
| `testCheckEventResponseWithNearbyPredatorReturnsTrue` | 判定 / イベント / response / with / nearby / predator / 戻り / true | イベント進行回帰 / 判定 / イベント / response / with / nearby / predator / 戻り / true | 良い | - | - |
| `testCheckEventResponsePredatorIsParentOfBreturnsFalse` | 判定 / イベント / response / predator / 状態 / 親 / of / breturns / false | イベント進行回帰 / 判定 / イベント / response / predator / 状態 / 親 / of / breturns / false | 良い | - | - |
| `testUpdateFromAlivePredatorAdultNonSlaveReturnsNull` | 更新 / from / alive / predator / adult / non / slave / 戻り / null | イベント進行回帰 / 更新 / from / alive / predator / adult / non / slave / 戻り / null | 良い | - | - |
| `testExecuteFromDeadNoPredatorReturnsTrue` | execute / from / 死亡 / なし / predator / 戻り / true | イベント進行回帰 / execute / from / 死亡 / なし / predator / 戻り / true | 良い | - | - |

### `PredatorsGameEventTest`
- 状態: 完了 (24/24 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityLow` | 判定 / イベント / response / sets / priority / low | イベント進行回帰 / 判定 / イベント / response / sets / priority / low | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenDead` | 判定 / イベント / response / 戻り / false / when / 死亡 | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / 死亡 | 良い | - | - |
| `testExecuteWhenToyIsNull` | execute / when / toy / 状態 / null | イベント進行回帰 / execute / when / toy / 状態 / null | 良い | - | - |
| `testUpdateFromNullToyNullReturnsAbort` | 更新 / from / null / toy / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / toy / null / 戻り / abort | 良い | - | - |
| `testUpdateBnotFromReturnsNull` | 更新 / bnot / from / 戻り / null | イベント進行回帰 / 更新 / bnot / from / 戻り / null | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testUpdateToyGrabbedReturnsAbort` | 更新 / toy / grabbed / 戻り / abort | イベント進行回帰 / 更新 / toy / grabbed / 戻り / abort | 良い | - | - |
| `testUpdateToyDeadReturnsAbort` | 更新 / toy / 死亡 / 戻り / abort | イベント進行回帰 / 更新 / toy / 死亡 / 戻り / abort | 良い | - | - |
| `testUpdateSnackTrueReturnsForceExec` | 更新 / snack / true / 戻り / force / exec | イベント進行回帰 / 更新 / snack / true / 戻り / force / exec | 良い | - | - |
| `testUpdateTickZeroBequalsFromDoesNotThrow` | 更新 / tick / zero / bequals / from / does / 非 / 例外 | イベント進行回帰 / 更新 / tick / zero / bequals / from / does / 非 / 例外 | 良い | - | - |
| `testUpdateToyRemovedReturnsAbort` | 更新 / toy / removed / 戻り / abort | イベント進行回帰 / 更新 / toy / removed / 戻り / abort | 良い | - | - |
| `testCheckEventResponsePredatorBodyScansBodies` | 判定 / イベント / response / predator / 本体 / scans / bodies | イベント進行回帰 / 判定 / イベント / response / predator / 本体 / scans / bodies | 良い | - | - |
| `testCheckEventResponseNotPredatorReturnsFalse` | 判定 / イベント / response / 非 / predator / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 非 / predator / 戻り / false | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateVeryHungryReturnsAbort` | 更新 / very / 空腹 / 戻り / abort | イベント進行回帰 / 更新 / very / 空腹 / 戻り / abort | 良い | - | - |
| `testUpdateTickNegativeNotHungryNonContactDoesNotThrow` | 更新 / tick / negative / 非 / 空腹 / non / contact / does / 非 / 例外 | イベント進行回帰 / 更新 / tick / negative / 非 / 空腹 / non / contact / does / 非 / 例外 | 良い | - | - |
| `testExecuteToyRemovedReturnsTrue` | execute / toy / removed / 戻り / true | イベント進行回帰 / execute / toy / removed / 戻り / true | 良い | - | - |
| `testExecuteToyAliveReturnsFalse` | execute / toy / alive / 戻り / false | イベント進行回帰 / execute / toy / alive / 戻り / false | 良い | - | - |
| `testExecuteToyGrabbedReturnsTrue` | execute / toy / grabbed / 戻り / true | イベント進行回帰 / execute / toy / grabbed / 戻り / true | 良い | - | - |
| `testExecuteToyDeadReturnsTrue` | execute / toy / 死亡 / 戻り / true | イベント進行回帰 / execute / toy / 死亡 / 戻り / true | 良い | - | - |
| `testEndSetsGrabbingFalse` | end / sets / grabbing / false | イベント進行回帰 / end / sets / grabbing / false | 良い | - | - |
| `testScenarioDeadToyMakesPredatorPuffAndAbort` | シナリオ / 死亡 / toy / makes / predator / puff / and / abort | イベント進行回帰 / 死亡時ガード回帰 | 良い | - | - |

### `ProposeEventTest`
- 状態: 完了 (50/50 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEventResponseParticipants` | 判定 / イベント / response / participants | イベント進行回帰 / 判定 / イベント / response / participants | 良い | - | - |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityHigh` | parameterized / constructor / sets / priority / high | イベント進行回帰 / parameterized / constructor / sets / priority / high | 良い | - | - |
| `testCheckEventResponseStranger` | 判定 / イベント / response / stranger | イベント進行回帰 / 判定 / イベント / response / stranger | 良い | - | - |
| `testCheckEventResponseFromNull` | 判定 / イベント / response / from / null | イベント進行回帰 / 判定 / イベント / response / from / null | 良い | - | - |
| `testExecuteBothNull` | execute / both / null | イベント進行回帰 / execute / both / null | 良い | - | - |
| `testExecuteToGrabbed` | execute / to / grabbed | イベント進行回帰 / execute / to / grabbed | 良い | - | - |
| `testEndBothNull` | end / both / null | イベント進行回帰 / end / both / null | 良い | - | - |
| `testEndWithBodies` | end / with / bodies | イベント進行回帰 / end / with / bodies | 良い | - | - |
| `testStartToAndFromBothNull` | start / to / and / from / both / null | イベント進行回帰 / start / to / and / from / both / null | 良い | - | - |
| `testUpdateFromNull` | 更新 / from / null | イベント進行回帰 / 更新 / from / null | 良い | - | - |
| `testUpdateToDead` | 更新 / to / 死亡 | イベント進行回帰 / 更新 / to / 死亡 | 良い | - | - |
| `testAcceptProposeAlreadyMarried` | accept / propose / already / married | イベント進行回帰 / accept / propose / already / married | 良い | - | - |
| `testAcceptProposeNoObstacles` | accept / propose / なし / obstacles | イベント進行回帰 / accept / propose / なし / obstacles | 良い | - | - |
| `testToString` | to / string | イベント進行回帰 / to / string | 良い | - | - |
| `testUpdateToGrabbedReturnsNull` | 更新 / to / grabbed / 戻り / null | イベント進行回帰 / 更新 / to / grabbed / 戻り / null | 良い | - | - |
| `testUpdateStartedReturnsForceExec` | 更新 / started / 戻り / force / exec | イベント進行回帰 / 更新 / started / 戻り / force / exec | 良い | - | - |
| `testUpdateNormalCaseReturnsNull` | 更新 / normal / case / 戻り / null | イベント進行回帰 / 更新 / normal / case / 戻り / null | 良い | - | - |
| `testUpdateToRemovedReturnsAbort` | 更新 / to / removed / 戻り / abort | イベント進行回帰 / 更新 / to / removed / 戻り / abort | 良い | - | - |
| `testUpdateToNydreturnsAbort` | 更新 / to / nydreturns / abort | イベント進行回帰 / 更新 / to / nydreturns / abort | 良い | - | - |
| `testStartWithBodiesDoesNotThrow` | start / with / bodies / does / 非 / 例外 | イベント進行回帰 / start / with / bodies / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick0SetsStarted` | execute / tick0 / sets / started | イベント進行回帰 / execute / tick0 / sets / started | 良い | - | - |
| `testExecuteTick5DoesNotThrow` | execute / tick5 / does / 非 / 例外 | イベント進行回帰 / execute / tick5 / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick20DoesNotThrow` | execute / tick20 / does / 非 / 例外 | イベント進行回帰 / execute / tick20 / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40SuccessDoesNotThrow` | execute / tick40 / success / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / success / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureAlreadyMarried` | execute / tick40 / failure / already / married | イベント進行回帰 / execute / tick40 / failure / already / married | 良い | - | - |
| `testExecuteTick60SuccessDoesNotThrow` | execute / tick60 / success / does / 非 / 例外 | イベント進行回帰 / execute / tick60 / success / does / 非 / 例外 | 良い | - | - |
| `testAcceptProposeFromHasBabyReturnsFalse` | accept / propose / from / 有無 / baby / 戻り / false | イベント進行回帰 / accept / propose / from / 有無 / baby / 戻り / false | 良い | - | - |
| `testAcceptProposeFindSickReturnsFalse` | accept / propose / find / 病気 / 戻り / false | イベント進行回帰 / accept / propose / find / 病気 / 戻り / false | 良い | - | - |
| `testAcceptProposeFromHasDisorderReturnsFalse` | accept / propose / from / 有無 / disorder / 戻り / false | イベント進行回帰 / accept / propose / from / 有無 / disorder / 戻り / false | 良い | - | - |
| `testUpdateToTakenReturnsAbort_566` | 更新 / to / taken / 戻り / abort / 566 | イベント進行回帰 / 更新 / to / taken / 戻り / abort / 566 | 良い | - | - |
| `testExecuteFromFindSickReturnsTrue` | execute / from / find / 病気 / 戻り / true | イベント進行回帰 / execute / from / find / 病気 / 戻り / true | 良い | - | - |
| `testExecuteToHasDisorderReturnsTrue` | execute / to / 有無 / disorder / 戻り / true | イベント進行回帰 / execute / to / 有無 / disorder / 戻り / true | 良い | - | - |
| `testExecuteTick0FromRudeSetsStarted` | execute / tick0 / from / rude / sets / started | イベント進行回帰 / execute / tick0 / from / rude / sets / started | 良い | - | - |
| `testExecuteTick20FromRudeDoesNotThrow` | execute / tick20 / from / rude / does / 非 / 例外 | イベント進行回帰 / execute / tick20 / from / rude / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40SuccessVeryNiceDoesNotThrow` | execute / tick40 / success / very / nice / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / success / very / nice / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40SuccessNiceDoesNotThrow` | execute / tick40 / success / nice / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / success / nice / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40SuccessShitheadDoesNotThrow` | execute / tick40 / success / shithead / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / success / shithead / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureToFindSickDoesNotThrow` | execute / tick40 / failure / to / find / 病気 / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / failure / to / find / 病気 / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureToIsRudeDoesNotThrow` | execute / tick40 / failure / to / 状態 / rude / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / failure / to / 状態 / rude / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureVeryNiceDoesNotThrow` | execute / tick40 / failure / very / nice / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / failure / very / nice / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureNiceDoesNotThrow` | execute / tick40 / failure / nice / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / failure / nice / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40FailureShitheadDoesNotThrow` | execute / tick40 / failure / shithead / does / 非 / 例外 | イベント進行回帰 / execute / tick40 / failure / shithead / does / 非 / 例外 | 良い | - | - |
| `testExecuteTick40SuccessSemantic` | execute / tick40 / success / semantic | イベント進行回帰 / execute / tick40 / success / semantic | 良い | - | - |
| `testExecuteTick40FailureSemantic` | execute / tick40 / failure / semantic | イベント進行回帰 / execute / tick40 / failure / semantic | 良い | - | - |
| `testAcceptProposeFromHasStalkReturnsFalse` | accept / propose / from / 有無 / stalk / 戻り / false | イベント進行回帰 / accept / propose / from / 有無 / stalk / 戻り / false | 良い | - | - |
| `testUpdateToTakenReturnsAbort` | 更新 / to / taken / 戻り / abort | イベント進行回帰 / 更新 / to / taken / 戻り / abort | 良い | - | - |
| `testExecuteToSickFromFindSickReturnsTrue` | execute / to / 病気 / from / find / 病気 / 戻り / true | イベント進行回帰 / execute / to / 病気 / from / find / 病気 / 戻り / true | 良い | - | - |
| `testUpdateToGrabbedResetsStarted` | 更新 / to / grabbed / resets / started | イベント進行回帰 / 更新 / to / grabbed / resets / started | 良い | - | - |
| `testExecuteTick60FailureSemantic` | execute / tick60 / failure / semantic | イベント進行回帰 / execute / tick60 / failure / semantic | 良い | - | - |

### `ProudChildEventTest`
- 状態: 完了 (45/45 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityMiddle` | parameterized / constructor / sets / priority / middle | イベント進行回帰 / parameterized / constructor / sets / priority / middle | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromIsNull` | simple / イベント / action / 戻り / true / when / from / 状態 / null | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / 状態 / null | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromEqualsB` | simple / イベント / action / 戻り / true / when / from / equals / b | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / equals / b | 良い | - | - |
| `testSimpleEventActionReturnsFalseWhenFromNotBandNotShutmouth` | simple / イベント / action / 戻り / false / when / from / 非 / band / 非 / shutmouth | イベント進行回帰 / simple / イベント / action / 戻り / false / when / from / 非 / band / 非 / shutmouth | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnunSlave` | 判定 / イベント / response / 戻り / false / for / unun / slave | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unun / slave | 良い | - | - |
| `testCheckEventResponseReturnsFalseForBirthMessageForcedBaby` | 判定 / イベント / response / 戻り / false / for / birth / メッセージ / forced / baby | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / birth / メッセージ / forced / baby | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenFromIsNull` | 判定 / イベント / response / 戻り / false / when / from / 状態 / null | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / from / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsFalseForNewbornBaby` | 判定 / イベント / response / 戻り / false / for / newborn / baby | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / newborn / baby | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhileFalling` | 判定 / イベント / response / 戻り / false / while / falling | イベント進行回帰 / 判定 / イベント / response / 戻り / false / while / falling | 良い | - | - |
| `testExecuteReturnsFalse` | execute / 戻り / false | イベント進行回帰 / execute / 戻り / false | 良い | - | - |
| `testEndSetsCurrentEventToNull` | end / sets / current / イベント / to / null | イベント進行回帰 / end / sets / current / イベント / to / null | 良い | - | - |
| `testGetStateDefaultIsGo` | 取得 / state / default / 状態 / go | イベント進行回帰 / 取得 / state / default / 状態 / go | 良い | - | - |
| `testSetState` | 設定 / state | イベント進行回帰 / 設定 / state | 良い | - | - |
| `testStartSetsCurrentEvent` | start / sets / current / イベント | イベント進行回帰 / start / sets / current / イベント | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testStateEnumAllValues` | state / enum / all / values | イベント進行回帰 / state / enum / all / values | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateBodyNydreturnsAbort` | 更新 / 本体 / nydreturns / abort | イベント進行回帰 / 更新 / 本体 / nydreturns / abort | 良い | - | - |
| `testUpdateNewbornBabyReturnsAbort` | 更新 / newborn / baby / 戻り / abort | イベント進行回帰 / 更新 / newborn / baby / 戻り / abort | 良い | - | - |
| `testCheckEventResponseBirthEventBlockedBabyReturnsFalse` | 判定 / イベント / response / birth / イベント / blocked / baby / 戻り / false | イベント進行回帰 / 判定 / イベント / response / birth / イベント / blocked / baby / 戻り / false | 良い | - | - |
| `testUpdateFromRemovedReturnsAbort` | 更新 / from / removed / 戻り / abort | イベント進行回帰 / 更新 / from / removed / 戻り / abort | 良い | - | - |
| `testUpdateFromCurrentEventNullReturnsAbort` | 更新 / from / current / イベント / null / 戻り / abort | イベント進行回帰 / 更新 / from / current / イベント / null / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromNoChildrenReturnsAbort` | 更新 / bequals / from / なし / children / 戻り / abort | イベント進行回帰 / 更新 / bequals / from / なし / children / 戻り / abort | 良い | - | - |
| `testUpdateChildBodyStateGodoesNotThrow` | 更新 / 子 / 本体 / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateWaitdoesNotThrow` | 更新 / 子 / 本体 / state / waitdoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / waitdoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateStartbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateSingbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / singbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / singbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateProudbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / proudbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / proudbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateEnddoesNotThrow` | 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | 良い | - | - |
| `testUpdateParentBodyStateEndreturnsAbort` | 更新 / 親 / 本体 / state / endreturns / abort | イベント進行回帰 / 更新 / 親 / 本体 / state / endreturns / abort | 良い | - | - |
| `testUpdateParentBodyAllSleepingChildrenReturnsAbort` | 更新 / 親 / 本体 / all / sleeping / children / 戻り / abort | イベント進行回帰 / 更新 / 親 / 本体 / all / sleeping / children / 戻り / abort | 良い | - | - |
| `testUpdatePartnerOfFromStateGodoesNotThrow` | 更新 / 相手 / of / from / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / 相手 / of / from / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdatePartnerOfFromStateNotGostays` | 更新 / 相手 / of / from / state / 非 / gostays | イベント進行回帰 / 更新 / 相手 / of / from / state / 非 / gostays | 良い | - | - |
| `testUpdatePanicTypeReturnsAbort` | 更新 / 恐慌 / type / 戻り / abort | イベント進行回帰 / 更新 / 恐慌 / type / 戻り / abort | 良い | - | - |
| `testUpdateFromUnhappyReturnsAbort` | 更新 / from / unhappy / 戻り / abort | イベント進行回帰 / 更新 / from / unhappy / 戻り / abort | 良い | - | - |
| `testUpdateChildDamagedReturnsAbort` | 更新 / 子 / damaged / 戻り / abort | イベント進行回帰 / 更新 / 子 / damaged / 戻り / abort | 良い | - | - |
| `testUpdateTick1ReturnsNull` | 更新 / tick1 / 戻り / null | イベント進行回帰 / 更新 / tick1 / 戻り / null | 良い | - | - |
| `testUpdateChildBodyGoIsDontMoveReturnsAbort` | 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | イベント進行回帰 / 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | 良い | - | - |
| `testUpdateChildBodySingBactionFlagFalseDoesNotThrow` | 更新 / 子 / 本体 / sing / baction / flag / false / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / sing / baction / flag / false / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseIsChildOfFromBabyReturnsTrue` | 判定 / イベント / response / 状態 / 子 / of / from / baby / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 状態 / 子 / of / from / baby / 戻り / true | 良い | - | - |
| `testScenarioChildParticipationBecomesHappy` | シナリオ / 子 / participation / becomes / happy | イベント進行回帰 / シナリオ / 子 / participation / becomes / happy | 良い | - | - |
| `testScenarioChildGoRandomHitMakesVeryHappyAndAddsMemory` | シナリオ / 子 / go / random / hit / makes / very / happy / and / adds / memory | イベント進行回帰 / シナリオ / 子 / go / random / hit / makes / very / happy / and / adds / memory | 良い | - | - |
| `testScenarioFromProudStateBecomesVeryHappyAndAddsMemories` | シナリオ / from / proud / state / becomes / very / happy / and / adds / memories | イベント進行回帰 / シナリオ / from / proud / state / becomes / very / happy / and / adds / memories | 良い | - | - |
| `testScenarioRudeChildProudCanEnterFurifuriPath` | シナリオ / rude / 子 / proud / 可否 / enter / furifuri / path | イベント進行回帰 / シナリオ / rude / 子 / proud / 可否 / enter / furifuri / path | 良い | - | - |

### `RaperReactionEventTest`
- 状態: 完了 (34/34 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseSetsPriorityHigh` | 判定 / イベント / response / sets / priority / high | イベント進行回帰 / 判定 / イベント / response / sets / priority / high | 良い | - | - |
| `testGetStateDefaultIsNull` | 取得 / state / default / 状態 / null | イベント進行回帰 / 取得 / state / default / 状態 / null | 良い | - | - |
| `testSetState` | 設定 / state | イベント進行回帰 / 設定 / state | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateFromDeadDoesNotThrow` | 更新 / from / 死亡 / does / 非 / 例外 | イベント進行回帰 / 更新 / from / 死亡 / does / 非 / 例外 | 良い | - | - |
| `testUpdateFromRemovedDoesNotThrow` | 更新 / from / removed / does / 非 / 例外 | イベント進行回帰 / 更新 / from / removed / does / 非 / 例外 | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseFromNullDoesNotThrow` | 判定 / イベント / response / from / null / does / 非 / 例外 | イベント進行回帰 / 判定 / イベント / response / from / null / does / 非 / 例外 | 良い | - | - |
| `testExecuteFromNullReturnsTrue` | execute / from / null / 戻り / true | イベント進行回帰 / execute / from / null / 戻り / true | 良い | - | - |
| `testEndDoesNotThrow` | end / does / 非 / 例外 | イベント進行回帰 / end / does / 非 / 例外 | 良い | - | - |
| `testSetScareWorldEventMessageDoesNotThrow` | 設定 / scare / world / イベント / メッセージ / does / 非 / 例外 | イベント進行回帰 / 設定 / scare / world / イベント / メッセージ / does / 非 / 例外 | 良い | - | - |
| `testSetCounterWorldEventMessageDoesNotThrow` | 設定 / counter / world / イベント / メッセージ / does / 非 / 例外 | イベント進行回帰 / 設定 / counter / world / イベント / メッセージ / does / 非 / 例外 | 良い | - | - |
| `testCheckConditionOfTargetFromNullReturnsFalse` | 判定 / condition / of / target / from / null / 戻り / false | イベント進行回帰 / 判定 / condition / of / target / from / null / 戻り / false | 良い | - | - |
| `testCheckConditionOfTargetFromRegisteredDoesNotThrow` | 判定 / condition / of / target / from / registered / does / 非 / 例外 | イベント進行回帰 / 判定 / condition / of / target / from / registered / does / 非 / 例外 | 良い | - | - |
| `testSearchAttackTargetEmptyWorldReturnsNull` | search / attack / target / empty / world / 戻り / null | イベント進行回帰 / search / attack / target / empty / world / 戻り / null | 良い | - | - |
| `testMoveTargetFromNullDoesNotThrow` | 移動 / target / from / null / does / 非 / 例外 | イベント進行回帰 / 移動 / target / from / null / does / 非 / 例外 | 良い | - | - |
| `testMoveTargetFromExistsDoesNotThrow` | 移動 / target / from / exists / does / 非 / 例外 | イベント進行回帰 / 移動 / target / from / exists / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseRaperNearbyNormalBodyStateEscapeReturnsTrue` | 判定 / イベント / response / raper / nearby / normal / 本体 / state / escape / 戻り / true | イベント進行回帰 / 判定 / イベント / response / raper / nearby / normal / 本体 / state / escape / 戻り / true | 良い | - | - |
| `testCheckEventResponseRaperNearbyUnunSlaveStateEscapeReturnsTrue` | 判定 / イベント / response / raper / nearby / unun / slave / state / escape / 戻り / true | イベント進行回帰 / 判定 / イベント / response / raper / nearby / unun / slave / state / escape / 戻り / true | 良い | - | - |
| `testStartNydEarlyReturn` | start / 非ゆっくり症 / early / 戻り | イベント進行回帰 / start / 非ゆっくり症 / early / 戻り | 良い | - | - |
| `testStartAttackStateDoesNotThrow` | start / attack / state / does / 非 / 例外 | イベント進行回帰 / start / attack / state / does / 非 / 例外 | 良い | - | - |
| `testStartEscapeStateDoesNotThrow` | start / escape / state / does / 非 / 例外 | イベント進行回帰 / start / escape / state / does / 非 / 例外 | 良い | - | - |
| `testUpdateStateAttackRaperFromReturnsNull` | 更新 / state / attack / raper / from / 戻り / null | イベント進行回帰 / 更新 / state / attack / raper / from / 戻り / null | 良い | - | - |
| `testUpdateStateEscapeAge1DoesNotThrow` | 更新 / state / escape / age1 / does / 非 / 例外 | イベント進行回帰 / 更新 / state / escape / age1 / does / 非 / 例外 | 良い | - | - |
| `testExecuteFromAliveStateEscapeReturnsFalse` | execute / from / alive / state / escape / 戻り / false | イベント進行回帰 / execute / from / alive / state / escape / 戻り / false | 良い | - | - |
| `testExecuteFromAliveStateAttackIsDontMoveReturnsFalse` | execute / from / alive / state / attack / 状態 / dont / 移動 / 戻り / false | イベント進行回帰 / execute / from / alive / state / attack / 状態 / dont / 移動 / 戻り / false | 良い | - | - |
| `testExecuteFromDeadNewRaperFoundReturnsFalse` | execute / from / 死亡 / new / raper / found / 戻り / false | イベント進行回帰 / execute / from / 死亡 / new / raper / found / 戻り / false | 良い | - | - |
| `testSearchNextTargetRaperExistsReturnsBody` | search / next / target / raper / exists / 戻り / 本体 | イベント進行回帰 / search / next / target / raper / exists / 戻り / 本体 | 良い | - | - |
| `testEscapeTargetFromNullDoesNotThrow` | escape / target / from / null / does / 非 / 例外 | イベント進行回帰 / escape / target / from / null / does / 非 / 例外 | 良い | - | - |
| `testEscapeTargetFromExistsDoesNotThrow` | escape / target / from / exists / does / 非 / 例外 | イベント進行回帰 / escape / target / from / exists / does / 非 / 例外 | 良い | - | - |
| `testCheckConditionOfTargetFromExcitingReturnsTrue` | 判定 / condition / of / target / from / exciting / 戻り / true | イベント進行回帰 / 判定 / condition / of / target / from / exciting / 戻り / true | 良い | - | - |

### `RaperWakeupEventTest`
- 状態: 完了 (14/14 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testSimpleEventActionReturnsFalseWhenBequalsFrom` | simple / イベント / action / 戻り / false / when / bequals / from | イベント進行回帰 / simple / イベント / action / 戻り / false / when / bequals / from | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenCanEventResponseIsFalse` | simple / イベント / action / 戻り / true / when / 可否 / イベント / response / 状態 / false | イベント進行回帰 / simple / イベント / action / 戻り / true / when / 可否 / イベント / response / 状態 / false | 良い | - | - |
| `testCheckEventResponseAlwaysReturnsFalse` | 判定 / イベント / response / always / 戻り / false | イベント進行回帰 / 判定 / イベント / response / always / 戻り / false | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベント進行回帰 / execute / 戻り / true | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionBisNydreturnsTrue` | simple / イベント / action / bis / nydreturns / true | イベント進行回帰 / simple / イベント / action / bis / nydreturns / true | 良い | - | - |
| `testSimpleEventActionBisNeedledReturnsTrue` | simple / イベント / action / bis / needled / 戻り / true | イベント進行回帰 / simple / イベント / action / bis / needled / 戻り / true | 良い | - | - |
| `testSimpleEventActionBisRaperReturnsTrue` | simple / イベント / action / bis / raper / 戻り / true | イベント進行回帰 / simple / イベント / action / bis / raper / 戻り / true | 良い | - | - |
| `testSimpleEventActionNormalBodyReturnsTrue` | simple / イベント / action / normal / 本体 / 戻り / true | イベント進行回帰 / simple / イベント / action / normal / 本体 / 戻り / true | 良い | - | - |
| `testScenarioNormalBodyQueuesSingleRaperReactionEvent` | シナリオ / normal / 本体 / queues / single / raper / reaction / イベント | イベント進行回帰 / シナリオ / normal / 本体 / queues / single / raper / reaction / イベント | 良い | - | - |
| `testScenarioRaperBodyGetsForcedExcitedAndDropsPartner` | シナリオ / raper / 本体 / gets / forced / excited / and / drops / 相手 | イベント進行回帰 / シナリオ / raper / 本体 / gets / forced / excited / and / drops / 相手 | 良い | - | - |

### `RevengeAttackEventTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseAlwaysTrueAndSetsPriorityHigh` | 判定 / イベント / response / always / true / and / sets / priority / high | イベント進行回帰 / 判定 / イベント / response / always / true / and / sets / priority / high | 良い | - | - |
| `testStartClearsActionFlags` | start / clears / action / flags | イベント進行回帰 / start / clears / action / flags | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsNull` | 更新 / 戻り / abort / when / to / 状態 / null | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / null | 良い | - | - |
| `testUpdateReturnsAbortWhenToIsRemoved` | 更新 / 戻り / abort / when / to / 状態 / removed | イベント進行回帰 / 更新 / 戻り / abort / when / to / 状態 / removed | 良い | - | - |
| `testUpdateToTakenReturnsAbort` | 更新 / to / taken / 戻り / abort | イベント進行回帰 / 更新 / to / taken / 戻り / abort | 良い | - | - |
| `testExecuteToNullReturnsTrue` | execute / to / null / 戻り / true | イベント進行回帰 / execute / to / null / 戻り / true | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateToAliveReturnsNull` | 更新 / to / alive / 戻り / null | イベント進行回帰 / 更新 / to / alive / 戻り / null | 良い | - | - |
| `testExecuteIsDontMoveReturnsTrue` | execute / 状態 / dont / 移動 / 戻り / true | イベント進行回帰 / execute / 状態 / dont / 移動 / 戻り / true | 良い | - | - |
| `testExecuteRnd0ReturnsTrue` | execute / rnd0 / 戻り / true | イベント進行回帰 / execute / rnd0 / 戻り / true | 良い | - | - |
| `testStartWithToDoesNotThrow` | start / with / to / does / 非 / 例外 | イベント進行回帰 / start / with / to / does / 非 / 例外 | 良い | - | - |
| `testUpdateCloseDistanceReturnsNull` | 更新 / close / distance / 戻り / null | イベント進行回帰 / 更新 / close / distance / 戻り / null | 良い | - | - |
| `testScenarioStartWakesSleeperClearsActionsAndTargetsVictim` | シナリオ / start / wakes / sleeper / clears / actions / and / targets / victim | イベント進行回帰 / シナリオ / start / wakes / sleeper / clears / actions / and / targets / victim | 良い | - | - |
| `testScenarioUpdateNearVictimForcesVictimToStay` | シナリオ / 更新 / near / victim / forces / victim / to / stay | イベント進行回帰 / シナリオ / 更新 / near / victim / forces / victim / to / stay | 良い | - | - |
| `testScenarioExecuteDontMoveMakesAttackerSadAndLament` | シナリオ / execute / dont / 移動 / makes / attacker / sad / and / lament | イベント進行回帰 / シナリオ / execute / dont / 移動 / makes / attacker / sad / and / lament | 良い | - | - |

### `ShitExercisesEventTest`
- 状態: 完了 (52/52 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityHigh` | parameterized / constructor / sets / priority / high | イベント進行回帰 / parameterized / constructor / sets / priority / high | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromIsNull` | simple / イベント / action / 戻り / true / when / from / 状態 / null | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenFromIsNull` | 判定 / イベント / response / 戻り / false / when / from / 状態 / null | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / from / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromEqualsB` | 判定 / イベント / response / 戻り / true / when / from / equals / b | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / equals / b | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnunSlave` | 判定 / イベント / response / 戻り / false / for / unun / slave | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unun / slave | 良い | - | - |
| `testExecuteReturnsFalse` | execute / 戻り / false | イベント進行回帰 / execute / 戻り / false | 良い | - | - |
| `testGetStateDefaultIsGo` | 取得 / state / default / 状態 / go | イベント進行回帰 / 取得 / state / default / 状態 / go | 良い | - | - |
| `testSetState` | 設定 / state | イベント進行回帰 / 設定 / state | 良い | - | - |
| `testSimpleEventActionFromNotNullNotShutmouthReturnsFalse` | simple / イベント / action / from / 非 / null / 非 / shutmouth / 戻り / false | イベント進行回帰 / simple / イベント / action / from / 非 / null / 非 / shutmouth / 戻り / false | 良い | - | - |
| `testCheckEventResponsePartnerReturnsTrue` | 判定 / イベント / response / 相手 / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 相手 / 戻り / true | 良い | - | - |
| `testCheckEventResponseChildBabyCanEventResponseDoesNotThrow` | 判定 / イベント / response / 子 / baby / 可否 / イベント / response / does / 非 / 例外 | イベント進行回帰 / 判定 / イベント / response / 子 / baby / 可否 / イベント / response / does / 非 / 例外 | 良い | - | - |
| `testStartSetsCurrentEvent` | start / sets / current / イベント | イベント進行回帰 / start / sets / current / イベント | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateBodyNydreturnsAbort` | 更新 / 本体 / nydreturns / abort | イベント進行回帰 / 更新 / 本体 / nydreturns / abort | 良い | - | - |
| `testUpdateFromRemovedReturnsAbort` | 更新 / from / removed / 戻り / abort | イベント進行回帰 / 更新 / from / removed / 戻り / abort | 良い | - | - |
| `testUpdateFromCurrentEventNullReturnsAbort` | 更新 / from / current / イベント / null / 戻り / abort | イベント進行回帰 / 更新 / from / current / イベント / null / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromNoChildrenReturnsAbort` | 更新 / bequals / from / なし / children / 戻り / abort | イベント進行回帰 / 更新 / bequals / from / なし / children / 戻り / abort | 良い | - | - |
| `testUpdateBisPartnerOfFromStateGodoesNotThrow` | 更新 / bis / 相手 / of / from / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / bis / 相手 / of / from / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateTickNotMultipleOf20ReturnsNull` | 更新 / tick / 非 / multiple / of20 / 戻り / null | イベント進行回帰 / 更新 / tick / 非 / multiple / of20 / 戻り / null | 良い | - | - |
| `testUpdateNfromWaitCountOver2000ReturnsAbort` | 更新 / nfrom / wait / count / over2000 / 戻り / abort | イベント進行回帰 / 更新 / nfrom / wait / count / over2000 / 戻り / abort | 良い | - | - |
| `testUpdateBisPartnerOfFromStateActualGodoesNotThrow` | 更新 / bis / 相手 / of / from / state / actual / godoes / 非 / 例外 | イベント進行回帰 / 更新 / bis / 相手 / of / from / state / actual / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateParentBodyStateGoatToiletEntersWaitEvenIfGatheringIncomplete` | 更新 / 親 / 本体 / state / goat / toilet / enters / wait / even / if / gathering / incomplete | イベント進行回帰 / 更新 / 親 / 本体 / state / goat / toilet / enters / wait / even / if / gathering / incomplete | 良い | - | - |
| `testUpdateChildBodyStateGodoesNotThrow` | 更新 / 子 / 本体 / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateWaitdoesNotThrow` | 更新 / 子 / 本体 / state / waitdoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / waitdoes / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateStartbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / startbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateYurayurabactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / yurayurabaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / yurayurabaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateNobinobibactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / nobinobibaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / nobinobibaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStatePokapokabactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / pokapokabaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / pokapokabaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateUnunbactionFlagTrueDoesNotThrow` | 更新 / 子 / 本体 / state / ununbaction / flag / true / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / ununbaction / flag / true / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBodyStateEnddoesNotThrow` | 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | イベント進行回帰 / 更新 / 子 / 本体 / state / enddoes / 非 / 例外 | 良い | - | - |
| `testUpdateParentBodyStateEndactionFlagTrueAborts` | 更新 / 親 / 本体 / state / endaction / flag / true / aborts | イベント進行回帰 / 更新 / 親 / 本体 / state / endaction / flag / true / aborts | 良い | - | - |
| `testCheckEventResponseCanEventResponseFalseReturnsFalse` | 判定 / イベント / response / 可否 / イベント / response / false / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 可否 / イベント / response / false / 戻り / false | 良い | - | - |
| `testCheckEventResponseNotChildOfFromReturnsFalse` | 判定 / イベント / response / 非 / 子 / of / from / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 非 / 子 / of / from / 戻り / false | 良い | - | - |
| `testCheckEventResponseIsChildNotBabyReturnsFalse` | 判定 / イベント / response / 状態 / 子 / 非 / baby / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 状態 / 子 / 非 / baby / 戻り / false | 良い | - | - |
| `testCheckEventResponseBabyChildOfFromReturnsTrue` | 判定 / イベント / response / baby / 子 / of / from / 戻り / true | イベント進行回帰 / 判定 / イベント / response / baby / 子 / of / from / 戻り / true | 良い | - | - |
| `testUpdateChildBodyGoIsDontMoveReturnsAbort` | 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | イベント進行回帰 / 更新 / 子 / 本体 / go / 状態 / dont / 移動 / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromWithBabyChildStateGodoesNotThrow` | 更新 / bequals / from / with / baby / 子 / state / godoes / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / with / baby / 子 / state / godoes / 非 / 例外 | 良い | - | - |
| `testUpdateBequalsFromWithBabyChildStateWaitdoesNotThrow` | 更新 / bequals / from / with / baby / 子 / state / waitdoes / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / with / baby / 子 / state / waitdoes / 非 / 例外 | 良い | - | - |
| `testUpdateFromHighZchildNotFromReturnsNull` | 更新 / from / high / zchild / 非 / from / 戻り / null | イベント進行回帰 / 更新 / from / high / zchild / 非 / from / 戻り / null | 良い | - | - |
| `testUpdateChildBodyUnunBactionFlagFalseTransitionToEnd` | 更新 / 子 / 本体 / unun / baction / flag / false / transition / to / end | イベント進行回帰 / 更新 / 子 / 本体 / unun / baction / flag / false / transition / to / end | 良い | - | - |
| `testUpdateParentBodyUnunActionFlagTrueTransitionsToEnd` | 更新 / 親 / 本体 / unun / action / flag / true / transitions / to / end | イベント進行回帰 / 更新 / 親 / 本体 / unun / action / flag / true / transitions / to / end | 良い | - | - |
| `testUpdateBequalsFromWithBabyStateStartbactionFlagFalseDoesNotThrow` | 更新 / bequals / from / with / baby / state / startbaction / flag / false / does / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / with / baby / state / startbaction / flag / false / does / 非 / 例外 | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testStateEnumAllValues` | state / enum / all / values | イベント進行回帰 / state / enum / all / values | 良い | - | - |
| `testUpdateHungryRelief` | 更新 / 空腹 / relief | イベント進行回帰 / 更新 / 空腹 / relief | 良い | - | - |
| `testUpdateNearToBirthReturnsAbort` | 更新 / near / to / birth / 戻り / abort | イベント進行回帰 / 更新 / near / to / birth / 戻り / abort | 良い | - | - |
| `testUpdateFromHighZnextInt0ReturnsAbort` | 更新 / from / high / znext / int0 / 戻り / abort | イベント進行回帰 / 更新 / from / high / znext / int0 / 戻り / abort | 良い | - | - |
| `testScenarioBabyParticipantBecomesVeryHappy` | シナリオ / baby / participant / becomes / very / happy | イベント進行回帰 / シナリオ / baby / participant / becomes / very / happy | 良い | - | - |
| `testScenarioChildPokapokaSetsFurifuriAndClearsUnunActionFlag` | シナリオ / 子 / pokapoka / sets / furifuri / and / clears / unun / action / flag | イベント進行回帰 / シナリオ / 子 / pokapoka / sets / furifuri / and / clears / unun / action / flag | 良い | - | - |
| `testScenarioChildUnunSuccessSetsDoShitAndClearsShitGauge` | シナリオ / 子 / unun / success / sets / do / shit / and / clears / shit / gauge | イベント進行回帰 / シナリオ / 子 / unun / success / sets / do / shit / and / clears / shit / gauge | 良い | - | - |
| `testScenarioChildUnunWithAnalCloseAddsShitInsteadOfDoShit` | シナリオ / 子 / unun / with / anal / close / adds / shit / instead / of / do / shit | イベント進行回帰 / シナリオ / 子 / unun / with / anal / close / adds / shit / instead / of / do / shit | 良い | - | - |

### `SuiRideEventTest`
- 状態: 完了 (48/48 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructorFromAndToAreNegativeOne` | default / constructor / from / and / to / are / negative / one | イベント進行回帰 / default / constructor / from / and / to / are / negative / one | 良い | - | - |
| `testDefaultConstructorPriorityIsLow` | default / constructor / priority / 状態 / low | イベント進行回帰 / default / constructor / priority / 状態 / low | 良い | - | - |
| `testParameterizedConstructorPriorityIsMiddle` | parameterized / constructor / priority / 状態 / middle | イベント進行回帰 / parameterized / constructor / priority / 状態 / middle | 良い | - | - |
| `testParameterizedConstructorFromIsSet` | parameterized / constructor / from / 状態 / 設定 | イベント進行回帰 / parameterized / constructor / from / 状態 / 設定 | 良い | - | - |
| `testParameterizedConstructorCountIsSet` | parameterized / constructor / count / 状態 / 設定 | イベント進行回帰 / parameterized / constructor / count / 状態 / 設定 | 良い | - | - |
| `testParameterizedConstructorToIsNegativeOneWhenNull` | parameterized / constructor / to / 状態 / negative / one / when / null | イベント進行回帰 / parameterized / constructor / to / 状態 / negative / one / when / null | 良い | - | - |
| `testToStringReturnsNonNull` | to / string / 戻り / non / null | イベント進行回帰 / to / string / 戻り / non / null | 良い | - | - |
| `testToStringReturnsNonEmpty` | to / string / 戻り / non / empty | イベント進行回帰 / to / string / 戻り / non / empty | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenTargetIsNullDefaultCtor` | 判定 / イベント / response / 戻り / false / when / target / 状態 / null / default / ctor | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / target / 状態 / null / default / ctor | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenTargetIsNullParamCtor` | 判定 / イベント / response / 戻り / false / when / target / 状態 / null / param / ctor | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / target / 状態 / null / param / ctor | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenFromIsNullWithValidTarget` | 判定 / イベント / response / 戻り / false / when / from / 状態 / null / with / valid / target | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / from / 状態 / null / with / valid / target | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromEqualsBwithValidTarget` | 判定 / イベント / response / 戻り / true / when / from / equals / bwith / valid / target | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / equals / bwith / valid / target | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBcannotRespondNoCurrentEvent` | 判定 / イベント / response / 戻り / false / when / bcannot / respond / なし / current / イベント | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bcannot / respond / なし / current / イベント | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBcannotEventResponseDead` | 判定 / イベント / response / 戻り / false / when / bcannot / イベント / response / 死亡 | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bcannot / イベント / response / 死亡 | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBalreadyHasEvent` | 判定 / イベント / response / 戻り / false / when / balready / 有無 / イベント | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / balready / 有無 / イベント | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisLockedMove` | 判定 / イベント / response / 戻り / false / when / bis / locked / 移動 | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / locked / 移動 | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenNoFamilyRelationship` | 判定 / イベント / response / 戻り / false / when / なし / 家族 / relationship | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / なし / 家族 / relationship | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenBisPartnerOfFrom` | 判定 / イベント / response / 戻り / true / when / bis / 相手 / of / from | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / bis / 相手 / of / from | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromIsParentOfB` | 判定 / イベント / response / 戻り / true / when / from / 状態 / 親 / of / b | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / 状態 / 親 / of / b | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisExcitingWithRelationship` | 判定 / イベント / response / 戻り / false / when / bis / exciting / with / relationship | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / exciting / with / relationship | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisScareWithRelationship` | 判定 / イベント / response / 戻り / false / when / bis / scare / with / relationship | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / scare / with / relationship | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenBisUnunSlaveWithRelationship` | 判定 / イベント / response / 戻り / false / when / bis / unun / slave / with / relationship | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / bis / unun / slave / with / relationship | 良い | - | - |
| `testStartDoesNotThrowWhenTargetIsNull` | start / does / 非 / 例外 / when / target / 状態 / null | イベント進行回帰 / start / does / 非 / 例外 / when / target / 状態 / null | 良い | - | - |
| `testStartDoesNotThrowWithValidTarget` | start / does / 非 / 例外 / with / valid / target | イベント進行回帰 / start / does / 非 / 例外 / with / valid / target | 良い | - | - |
| `testExecuteReturnsFalseWhenTargetIsNull` | execute / 戻り / false / when / target / 状態 / null | イベント進行回帰 / execute / 戻り / false / when / target / 状態 / null | 良い | - | - |
| `testExecuteReturnsFalseWhenSuiNotInWaitingState` | execute / 戻り / false / when / sui / 非 / in / waiting / state | イベント進行回帰 / execute / 戻り / false / when / sui / 非 / in / waiting / state | 良い | - | - |
| `testExecuteAlwaysReturnsFalseSuiInWaitingState` | execute / always / 戻り / false / sui / in / waiting / state | イベント進行回帰 / execute / always / 戻り / false / sui / in / waiting / state | 良い | - | - |
| `testExecuteSuiWaitingBodyRidesOnSui` | execute / sui / waiting / 本体 / rides / on / sui | イベント進行回帰 / execute / sui / waiting / 本体 / rides / on / sui | 良い | - | - |
| `testEndDoesNotThrowWhenLinkParentIsNegativeOne` | end / does / 非 / 例外 / when / link / 親 / 状態 / negative / one | イベント進行回帰 / end / does / 非 / 例外 / when / link / 親 / 状態 / negative / one | 良い | - | - |
| `testEndCallsRideOffWhenBodyIsRidingSui` | end / calls / ride / off / when / 本体 / 状態 / riding / sui | イベント進行回帰 / end / calls / ride / off / when / 本体 / 状態 / riding / sui | 良い | - | - |
| `testEndDoesNotThrowForNonRidingBody` | end / does / 非 / 例外 / for / non / riding / 本体 | イベント進行回帰 / end / does / 非 / 例外 / for / non / riding / 本体 | 良い | - | - |
| `testUpdateReturnsAbortWhenTargetIsNull` | 更新 / 戻り / abort / when / target / 状態 / null | イベント進行回帰 / 更新 / 戻り / abort / when / target / 状態 / null | 良い | - | - |
| `testUpdateNoFavSuiHasLinkParentReturnsNull` | 更新 / なし / fav / sui / 有無 / link / 親 / 戻り / null | イベント進行回帰 / 更新 / なし / fav / sui / 有無 / link / 親 / 戻り / null | 良い | - | - |
| `testUpdateNoFavSuiNoLinkParentFromIsNullReturnsAbort` | 更新 / なし / fav / sui / なし / link / 親 / from / 状態 / null / 戻り / abort | イベント進行回帰 / 更新 / なし / fav / sui / なし / link / 親 / from / 状態 / null / 戻り / abort | 良い | - | - |
| `testUpdateNoFavSuiFromEqualsBsuiCannotRideDoesNotThrow` | 更新 / なし / fav / sui / from / equals / bsui / cannot / ride / does / 非 / 例外 | イベント進行回帰 / 更新 / なし / fav / sui / from / equals / bsui / cannot / ride / does / 非 / 例外 | 良い | - | - |
| `testUpdateNoFavSuiFromNotEqualsBfromCurrentEventIsNullReturnsAbort` | 更新 / なし / fav / sui / from / 非 / equals / bfrom / current / イベント / 状態 / null / 戻り / abort | イベント進行回帰 / 更新 / なし / fav / sui / from / 非 / equals / bfrom / current / イベント / 状態 / null / 戻り / abort | 良い | - | - |
| `testUpdateNoFavSuiBisDontMoveReturnsAbort` | 更新 / なし / fav / sui / bis / dont / 移動 / 戻り / abort | イベント進行回帰 / 更新 / なし / fav / sui / bis / dont / 移動 / 戻り / abort | 良い | - | - |
| `testUpdateNoFavSuiBisExcitingReturnsAbort` | 更新 / なし / fav / sui / bis / exciting / 戻り / abort | イベント進行回帰 / 更新 / なし / fav / sui / bis / exciting / 戻り / abort | 良い | - | - |
| `testUpdateNoFavSuiBisScareReturnsAbort` | 更新 / なし / fav / sui / bis / scare / 戻り / abort | イベント進行回帰 / 更新 / なし / fav / sui / bis / scare / 戻り / abort | 良い | - | - |
| `testUpdateHasFavSuiNotRidingFromEqualsBreturnsNull` | 更新 / 有無 / fav / sui / 非 / riding / from / equals / breturns / null | イベント進行回帰 / 更新 / 有無 / fav / sui / 非 / riding / from / equals / breturns / null | 良い | - | - |
| `testUpdateHasFavSuiRidingFromIsNullReturnsAbort` | 更新 / 有無 / fav / sui / riding / from / 状態 / null / 戻り / abort | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / 状態 / null / 戻り / abort | 良い | - | - |
| `testUpdateHasFavSuiRidingFromEqualsBbindbodyUnder3TickUnder50ReturnsNull` | 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / under3 / tick / under50 / 戻り / null | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / under3 / tick / under50 / 戻り / null | 良い | - | - |
| `testUpdateHasFavSuiRidingFromEqualsBbindbodyAtLimitSuiWaiting` | 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / at / limit / sui / waiting | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / at / limit / sui / waiting | 良い | - | - |
| `testUpdateHasFavSuiRidingFromEqualsBbindbodyAtLimitSuiNotWaiting` | 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / at / limit / sui / 非 / waiting | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / equals / bbindbody / at / limit / sui / 非 / waiting | 良い | - | - |
| `testUpdateHasFavSuiRidingFromNotEqualsBfromCurrentEventIsThisSuiWaitingReturnsAbort` | 更新 / 有無 / fav / sui / riding / from / 非 / equals / bfrom / current / イベント / 状態 / this / sui / waiting / 戻り / abort | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / 非 / equals / bfrom / current / イベント / 状態 / this / sui / waiting / 戻り / abort | 良い | - | - |
| `testUpdateHasFavSuiRidingFromNotEqualsBsuiNotCondition1ReturnsNull` | 更新 / 有無 / fav / sui / riding / from / 非 / equals / bsui / 非 / condition1 / 戻り / null | イベント進行回帰 / 更新 / 有無 / fav / sui / riding / from / 非 / equals / bsui / 非 / condition1 / 戻り / null | 良い | - | - |
| `testScenarioUnunSlaveFamilyMemberBecomesVerySadAndDoesNotJoinRideEvent` | シナリオ / unun / slave / 家族 / member / becomes / very / sad / and / does / 非 / join / ride / イベント | イベント進行回帰 / シナリオ / unun / slave / 家族 / member / becomes / very / sad / and / does / 非 / join / ride / イベント | 良い | - | - |
| `testScenarioExecuteMakesFirstRiderOwnerAndRegistersSuiFavorite` | シナリオ / execute / makes / first / rider / owner / and / registers / sui / favorite | イベント進行回帰 / シナリオ / execute / makes / first / rider / owner / and / registers / sui / favorite | 良い | - | - |

### `SuiSpeakeTest`
- 状態: 完了 (37/37 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor` | parameterized / constructor | イベント進行回帰 / parameterized / constructor | 良い | - | - |
| `testCheckEventResponseAlwaysReturnsFalse` | 判定 / イベント / response / always / 戻り / false | イベント進行回帰 / 判定 / イベント / response / always / 戻り / false | 良い | - | - |
| `testExecuteReturnsTrue` | execute / 戻り / true | イベント進行回帰 / execute / 戻り / true | 良い | - | - |
| `testSimpleEventActionWithCurrentEvent` | simple / イベント / action / with / current / イベント | イベント進行回帰 / simple / イベント / action / with / current / イベント | 良い | - | - |
| `testSimpleEventActionTalking` | simple / イベント / action / talking | イベント進行回帰 / simple / イベント / action / talking | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseAlwaysFalse` | 判定 / イベント / response / always / false | イベント進行回帰 / 判定 / イベント / response / always / false | 良い | - | - |
| `testExecuteAlwaysTrue` | execute / always / true | イベント進行回帰 / execute / always / true | 良い | - | - |
| `testSimpleEventActionDeadBodyDoesNotThrow` | simple / イベント / action / 死亡 / 本体 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / 死亡 / 本体 / does / 非 / 例外 | 良い | - | - |
| `testToString` | to / string | イベント進行回帰 / to / string | 良い | - | - |
| `testSimpleEventActionFromEqualsBreturnsFalse` | simple / イベント / action / from / equals / breturns / false | イベント進行回帰 / simple / イベント / action / from / equals / breturns / false | 良い | - | - |
| `testSimpleEventActionFromNullTargetNullNextBoolTrueDoesNotThrow` | simple / イベント / action / from / null / target / null / next / bool / true / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / null / next / bool / true / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetNullNextBoolFalseDoesNotThrow` | simple / イベント / action / from / null / target / null / next / bool / false / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / null / next / bool / false / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullDifferentBodyTargetNullDoesNotThrow` | simple / イベント / action / from / 非 / null / different / 本体 / target / null / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / different / 本体 / target / null / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullBisPartnerOfFromTargetNullDoesNotThrow` | simple / イベント / action / from / 非 / null / bis / 相手 / of / from / target / null / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / bis / 相手 / of / from / target / null / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetFarNoActionDoesNotThrow` | simple / イベント / action / from / null / target / far / なし / action / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / far / なし / action / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiDbNullReturnsFalse` | simple / イベント / action / from / null / target / sui / db / null / 戻り / false | イベント進行回帰 / simple / イベント / action / from / null / target / sui / db / null / 戻り / false | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiDbIsFatherDoesNotThrow` | simple / イベント / action / from / null / target / sui / db / 状態 / 父 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / db / 状態 / 父 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiDbIsMotherDoesNotThrow` | simple / イベント / action / from / null / target / sui / db / 状態 / 母 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / db / 状態 / 母 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiBisPartnerOfDbDoesNotThrow` | simple / イベント / action / from / null / target / sui / bis / 相手 / of / db / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / bis / 相手 / of / db / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiBisParentOfDbDoesNotThrow` | simple / イベント / action / from / null / target / sui / bis / 親 / of / db / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / bis / 親 / of / db / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiDbIsElderSisterDoesNotThrow` | simple / イベント / action / from / null / target / sui / db / 状態 / elder / 姉妹 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / db / 状態 / elder / 姉妹 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiDbIsYoungerSisterDoesNotThrow` | simple / イベント / action / from / null / target / sui / db / 状態 / younger / 姉妹 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / db / 状態 / younger / 姉妹 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNullTargetSuiNoRelationshipDoesNotThrow` | simple / イベント / action / from / null / target / sui / なし / relationship / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / null / target / sui / なし / relationship / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetNullBisParentOfFromDoesNotThrow` | simple / イベント / action / from / 非 / null / target / null / bis / 親 / of / from / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / null / bis / 親 / of / from / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetNullTooFarDoesNotThrow` | simple / イベント / action / from / 非 / null / target / null / too / far / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / null / too / far / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiFromIsMotherDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / from / 状態 / 母 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / from / 状態 / 母 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiFromIsFatherDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / from / 状態 / 父 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / from / 状態 / 父 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiBisPartnerDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / bis / 相手 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / bis / 相手 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiBisParentDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / bis / 親 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / bis / 親 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiFromIsElderSisterDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / from / 状態 / elder / 姉妹 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / from / 状態 / elder / 姉妹 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiFromIsYoungerSisterDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / from / 状態 / younger / 姉妹 / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / from / 状態 / younger / 姉妹 / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiNoRelationshipDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / なし / relationship / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / なし / relationship / does / 非 / 例外 | 良い | - | - |
| `testSimpleEventActionFromNotNullTargetSuiTooFarDoesNotThrow` | simple / イベント / action / from / 非 / null / target / sui / too / far / does / 非 / 例外 | イベント進行回帰 / simple / イベント / action / from / 非 / null / target / sui / too / far / does / 非 / 例外 | 良い | - | - |
| `testScenarioRudeBodyWithoutSuiQueuesWorldSpeakEventAndSetsWantingMessage` | シナリオ / rude / 本体 / without / sui / queues / world / speak / イベント / and / sets / wanting / メッセージ | イベント進行回帰 / シナリオ / rude / 本体 / without / sui / queues / world / speak / イベント / and / sets / wanting / メッセージ | 良い | - | - |
| `testScenarioUnrelatedDriverQueuesFollowupBodySpeakEvent` | シナリオ / unrelated / driver / queues / followup / 本体 / speak / イベント | イベント進行回帰 / シナリオ / unrelated / driver / queues / followup / 本体 / speak / イベント | 良い | - | - |

### `SuperEatingTimeEventTest`
- 状態: 完了 (45/45 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityHigh` | parameterized / constructor / sets / priority / high | イベント進行回帰 / parameterized / constructor / sets / priority / high | 良い | - | - |
| `testSimpleEventActionReturnsTrueWhenFromIsNull` | simple / イベント / action / 戻り / true / when / from / 状態 / null | イベント進行回帰 / simple / イベント / action / 戻り / true / when / from / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenFromIsNull` | 判定 / イベント / response / 戻り / false / when / from / 状態 / null | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / from / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromEqualsBandNotShutmouth` | 判定 / イベント / response / 戻り / true / when / from / equals / band / 非 / shutmouth | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / equals / band / 非 / shutmouth | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenDead` | 判定 / イベント / response / 戻り / false / when / 死亡 | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / 死亡 | 良い | - | - |
| `testExecuteReturnsFalse` | execute / 戻り / false | イベント進行回帰 / execute / 戻り / false | 良い | - | - |
| `testGetStateDefaultIsWait` | 取得 / state / default / 状態 / wait | イベント進行回帰 / 取得 / state / default / 状態 / wait | 良い | - | - |
| `testSetState` | 設定 / state | イベント進行回帰 / 設定 / state | 良い | - | - |
| `testGetMinimumStepDefaultIsZero` | 取得 / minimum / step / default / 状態 / zero | イベント進行回帰 / 取得 / minimum / step / default / 状態 / zero | 良い | - | - |
| `testSimpleEventActionFromNotNullReturnsFalse` | simple / イベント / action / from / 非 / null / 戻り / false | イベント進行回帰 / simple / イベント / action / from / 非 / null / 戻り / false | 良い | - | - |
| `testCheckEventResponseDifferentPublicRankReturnsFalse` | 判定 / イベント / response / different / public / rank / 戻り / false | イベント進行回帰 / 判定 / イベント / response / different / public / rank / 戻り / false | 良い | - | - |
| `testCheckEventResponsePartnerReturnsTrue` | 判定 / イベント / response / 相手 / 戻り / true | イベント進行回帰 / 判定 / イベント / response / 相手 / 戻り / true | 良い | - | - |
| `testStartSetsCurrentEvent` | start / sets / current / イベント | イベント進行回帰 / start / sets / current / イベント | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateBodyNydreturnsAbort` | 更新 / 本体 / nydreturns / abort | イベント進行回帰 / 更新 / 本体 / nydreturns / abort | 良い | - | - |
| `testUpdateFromRemovedReturnsAbort` | 更新 / from / removed / 戻り / abort | イベント進行回帰 / 更新 / from / removed / 戻り / abort | 良い | - | - |
| `testUpdateTargetNullReturnsAbort` | 更新 / target / null / 戻り / abort | イベント進行回帰 / 更新 / target / null / 戻り / abort | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testStateEnumAllValues` | state / enum / all / values | イベント進行回帰 / state / enum / all / values | 良い | - | - |
| `testUpdateParentBranchNoChildrenReturnsAbort` | 更新 / 親 / branch / なし / children / 戻り / abort | イベント進行回帰 / 更新 / 親 / branch / なし / children / 戻り / abort | 良い | - | - |
| `testUpdateTickNotMultipleOf20ReturnsNull` | 更新 / tick / 非 / multiple / of20 / 戻り / null | イベント進行回帰 / 更新 / tick / 非 / multiple / of20 / 戻り / null | 良い | - | - |
| `testUpdateChildBranchDefaultStateReturnsNull` | 更新 / 子 / branch / default / state / 戻り / null | イベント進行回帰 / 更新 / 子 / branch / default / state / 戻り / null | 良い | - | - |
| `testUpdateChildBranchPartnerOfFromReturnsNull` | 更新 / 子 / branch / 相手 / of / from / 戻り / null | イベント進行回帰 / 更新 / 子 / branch / 相手 / of / from / 戻り / null | 良い | - | - |
| `testUpdateNfromWaitCountOver10FromNoEventReturnsAbort` | 更新 / nfrom / wait / count / over10 / from / なし / イベント / 戻り / abort | イベント進行回帰 / 更新 / nfrom / wait / count / over10 / from / なし / イベント / 戻り / abort | 良い | - | - |
| `testUpdateParentBranchWithAdultChildReturnsAbort` | 更新 / 親 / branch / with / adult / 子 / 戻り / abort | イベント進行回帰 / 更新 / 親 / branch / with / adult / 子 / 戻り / abort | 良い | - | - |
| `testUpdateChildBranchGostateReturnsNull` | 更新 / 子 / branch / gostate / 戻り / null | イベント進行回帰 / 更新 / 子 / branch / gostate / 戻り / null | 良い | - | - |
| `testUpdateChildBranchStartBeforeStateDoesNotThrow` | 更新 / 子 / branch / start / before / state / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / branch / start / before / state / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBranchStartStateDoesNotThrow` | 更新 / 子 / branch / start / state / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / branch / start / state / does / 非 / 例外 | 良い | - | - |
| `testUpdateChildBranchWaitStateDoesNotThrow` | 更新 / 子 / branch / wait / state / does / 非 / 例外 | イベント進行回帰 / 更新 / 子 / branch / wait / state / does / 非 / 例外 | 良い | - | - |
| `testUpdateParentBranchWaitStateWithBabyChildDoesNotThrow` | 更新 / 親 / branch / wait / state / with / baby / 子 / does / 非 / 例外 | イベント進行回帰 / 更新 / 親 / branch / wait / state / with / baby / 子 / does / 非 / 例外 | 良い | - | - |
| `testUpdateParentBranchGoStateWithBabyChildDoesNotThrow` | 更新 / 親 / branch / go / state / with / baby / 子 / does / 非 / 例外 | イベント進行回帰 / 更新 / 親 / branch / go / state / with / baby / 子 / does / 非 / 例外 | 良い | - | - |
| `testCheckEventResponseIsDontMoveReturnsFalse` | 判定 / イベント / response / 状態 / dont / 移動 / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 状態 / dont / 移動 / 戻り / false | 良い | - | - |
| `testCheckEventResponseIsNydreturnsFalse` | 判定 / イベント / response / 状態 / nydreturns / false | イベント進行回帰 / 判定 / イベント / response / 状態 / nydreturns / false | 良い | - | - |
| `testCheckEventResponseNotChildOfFromReturnsFalse` | 判定 / イベント / response / 非 / 子 / of / from / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 非 / 子 / of / from / 戻り / false | 良い | - | - |
| `testCheckEventResponseIsChildAdultReturnsFalse` | 判定 / イベント / response / 状態 / 子 / adult / 戻り / false | イベント進行回帰 / 判定 / イベント / response / 状態 / 子 / adult / 戻り / false | 良い | - | - |
| `testCheckEventResponseBabyChildReturnsTrue` | 判定 / イベント / response / baby / 子 / 戻り / true | イベント進行回帰 / 判定 / イベント / response / baby / 子 / 戻り / true | 良い | - | - |
| `testUpdateNfromWaitCountOver5000ReturnsAbort` | 更新 / nfrom / wait / count / over5000 / 戻り / abort | イベント進行回帰 / 更新 / nfrom / wait / count / over5000 / 戻り / abort | 良い | - | - |
| `testSimpleEventActionFromShutmouthReturnsTrue` | simple / イベント / action / from / shutmouth / 戻り / true | イベント進行回帰 / simple / イベント / action / from / shutmouth / 戻り / true | 良い | - | - |
| `testUpdateParentBranchStartBeforeWithBabyChildDoesNotThrow` | 更新 / 親 / branch / start / before / with / baby / 子 / does / 非 / 例外 | イベント進行回帰 / 更新 / 親 / branch / start / before / with / baby / 子 / does / 非 / 例外 | 良い | - | - |
| `testUpdateParentBranchStartWithBabyChildDoesNotThrow` | 更新 / 親 / branch / start / with / baby / 子 / does / 非 / 例外 | イベント進行回帰 / 更新 / 親 / branch / start / with / baby / 子 / does / 非 / 例外 | 良い | - | - |
| `testUpdateFoodEmptyReturnsAbort` | 更新 / food / empty / 戻り / abort | イベント進行回帰 / 更新 / food / empty / 戻り / abort | 良い | - | - |
| `testScenarioRemovedTargetMakesParentVerySadAndAborts` | シナリオ / removed / target / makes / 親 / very / sad / and / aborts | イベント進行回帰 / シナリオ / removed / target / makes / 親 / very / sad / and / aborts | 良い | - | - |
| `testScenarioParentStartWithSatiatedChildTargetsFoodAndGetsNoHungryPeriod` | シナリオ / 親 / start / with / satiated / 子 / targets / food / and / gets / なし / 空腹 / period | イベント進行回帰 / シナリオ / 親 / start / with / satiated / 子 / targets / food / and / gets / なし / 空腹 / period | 良い | - | - |
| `testScenarioChildStartNearFoodActuallyEatsAndClearsActions` | シナリオ / 子 / start / near / food / actually / eats / and / clears / actions | イベント進行回帰 / シナリオ / 子 / start / near / food / actually / eats / and / clears / actions | 良い | - | - |

### `YukkuriRideEventTest`
- 状態: 完了 (25/25 良い)
- クラス要約: `イベント進行回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 文字列表現が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | イベント進行回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorSetsPriorityMiddle` | parameterized / constructor / sets / priority / middle | イベント進行回帰 / parameterized / constructor / sets / priority / middle | 良い | - | - |
| `testCheckEventResponseReturnsFalseWhenToIsNull` | 判定 / イベント / response / 戻り / false / when / to / 状態 / null | イベント進行回帰 / 判定 / イベント / response / 戻り / false / when / to / 状態 / null | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenFromEqualsB` | 判定 / イベント / response / 戻り / true / when / from / equals / b | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / from / equals / b | 良い | - | - |
| `testCheckEventResponseReturnsTrueWhenToEqualsB` | 判定 / イベント / response / 戻り / true / when / to / equals / b | イベント進行回帰 / 判定 / イベント / response / 戻り / true / when / to / equals / b | 良い | - | - |
| `testCheckEventResponseReturnsFalseForUnrelatedBody` | 判定 / イベント / response / 戻り / false / for / unrelated / 本体 | イベント進行回帰 / 判定 / イベント / response / 戻り / false / for / unrelated / 本体 | 良い | - | - |
| `testExecuteReturnsFalse` | execute / 戻り / false | イベント進行回帰 / execute / 戻り / false | 良い | - | - |
| `testEndSetsLinkParentToNegativeOneOnTo` | end / sets / link / 親 / to / negative / one / on / to | イベント進行回帰 / end / sets / link / 親 / to / negative / one / on / to | 良い | - | - |
| `testUpdateFromNullReturnsAbort` | 更新 / from / null / 戻り / abort | イベント進行回帰 / 更新 / from / null / 戻り / abort | 良い | - | - |
| `testUpdateToNullReturnsAbort` | 更新 / to / null / 戻り / abort | イベント進行回帰 / 更新 / to / null / 戻り / abort | 良い | - | - |
| `testUpdateToDeadReturnsAbort` | 更新 / to / 死亡 / 戻り / abort | イベント進行回帰 / 更新 / to / 死亡 / 戻り / abort | 良い | - | - |
| `testUpdateFromCurrentEventNotThisReturnsAbort` | 更新 / from / current / イベント / 非 / this / 戻り / abort | イベント進行回帰 / 更新 / from / current / イベント / 非 / this / 戻り / abort | 良い | - | - |
| `testStartDoesNotThrow` | start / does / 非 / 例外 | イベント進行回帰 / start / does / 非 / 例外 | 良い | - | - |
| `testToStringDoesNotThrow` | to / string / does / 非 / 例外 | イベント進行回帰 / to / string / does / 非 / 例外 | 良い | - | - |
| `testUpdateBequalsFromTick10001ReturnsAbort` | 更新 / bequals / from / tick10001 / 戻り / abort | イベント進行回帰 / 更新 / bequals / from / tick10001 / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromParentLinkIdNullDoesNotThrow` | 更新 / bequals / from / 親 / link / id / null / does / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / 親 / link / id / null / does / 非 / 例外 | 良い | - | - |
| `testUpdateBequalsToNotLinkedDoesNotThrow` | 更新 / bequals / to / 非 / linked / does / 非 / 例外 | イベント進行回帰 / 更新 / bequals / to / 非 / linked / does / 非 / 例外 | 良い | - | - |
| `testUpdateFromRemovedReturnsAbort` | 更新 / from / removed / 戻り / abort | イベント進行回帰 / 更新 / from / removed / 戻り / abort | 良い | - | - |
| `testUpdateToRemovedReturnsAbort` | 更新 / to / removed / 戻り / abort | イベント進行回帰 / 更新 / to / removed / 戻り / abort | 良い | - | - |
| `testSimpleEventActionDefaultReturnsFalse` | simple / イベント / action / default / 戻り / false | イベント進行回帰 / simple / イベント / action / default / 戻り / false | 良い | - | - |
| `testUpdateToNormalDirtyReturnsAbort` | 更新 / to / normal / dirty / 戻り / abort | イベント進行回帰 / 更新 / to / normal / dirty / 戻り / abort | 良い | - | - |
| `testUpdateFindSickReturnsAbort` | 更新 / find / 病気 / 戻り / abort | イベント進行回帰 / 更新 / find / 病気 / 戻り / abort | 良い | - | - |
| `testUpdateBequalsFromToOnHeadDoesNotThrow` | 更新 / bequals / from / to / on / head / does / 非 / 例外 | イベント進行回帰 / 更新 / bequals / from / to / on / head / does / 非 / 例外 | 良い | - | - |
| `testUpdateBequalsToOnHeadDoesNotThrow` | 更新 / bequals / to / on / head / does / 非 / 例外 | イベント進行回帰 / 更新 / bequals / to / on / head / does / 非 / 例外 | 良い | - | - |
| `testScenarioCloseChildGetsLinkedOntoParent` | シナリオ / close / 子 / gets / linked / onto / 親 | イベント進行回帰 / シナリオ / close / 子 / gets / linked / onto / 親 | 良い | - | - |

## `org.simyukkuri.item`
### `AutoFeederTest`
- 状態: 完了 (36/36 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 文字列表現が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testValueAndCost` | value / and / cost | 設置物/アイテム動作回帰 / value / and / cost | 良い | - | - |
| `testFeedTypeEnum` | feed / type / enum | 設置物/アイテム動作回帰 / feed / type / enum | 良い | - | - |
| `testFeedModeEnum` | feed / mode / enum | 設置物/アイテム動作回帰 / feed / mode / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetSetType` | 取得 / 設定 / type | 設置物/アイテム動作回帰 / 取得 / 設定 / type | 良い | - | - |
| `testGetSetMode` | 取得 / 設定 / mode | 設置物/アイテム動作回帰 / 取得 / 設定 / mode | 良い | - | - |
| `testGetSetFeedingInterval` | 取得 / 設定 / feeding / interval | 設置物/アイテム動作回帰 / 取得 / 設定 / feeding / interval | 良い | - | - |
| `testGetSetFeedingP` | 取得 / 設定 / feeding / p | 設置物/アイテム動作回帰 / 取得 / 設定 / feeding / p | 良い | - | - |
| `testGetSetFood` | 取得 / 設定 / food | 設置物/アイテム動作回帰 / 取得 / 設定 / food | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDateDisabled` | up / date / disabled | 設置物/アイテム動作回帰 / up / date / disabled | 良い | - | - |
| `testUpDateAgeModulo20NotZero` | up / date / age / modulo20 / 非 / zero | 設置物/アイテム動作回帰 / up / date / age / modulo20 / 非 / zero | 良い | - | - |
| `testUpDateFoodNullModeOne` | up / date / food / null / mode / one | 設置物/アイテム動作回帰 / up / date / food / null / mode / one | 良い | - | - |
| `testUpDateFoodSetFoodRemovedClearsFood` | up / date / food / 設定 / food / removed / clears / food | 設置物/アイテム動作回帰 / up / date / food / 設定 / food / removed / clears / food | 良い | - | - |
| `testUpDateFoodSetFoodEmptyRemovesFood` | up / date / food / 設定 / food / empty / removes / food | 設置物/アイテム動作回帰 / up / date / food / 設定 / food / empty / removes / food | 良い | - | - |
| `testUpDateFoodSetFoodValidNoChange` | up / date / food / 設定 / food / valid / なし / change | 設置物/アイテム動作回帰 / up / date / food / 設定 / food / valid / なし / change | 良い | - | - |
| `testUpDateAgeNotDivisibleBy20EarlyReturn` | up / date / age / 非 / divisible / by20 / early / 戻り | 設置物/アイテム動作回帰 / up / date / age / 非 / divisible / by20 / early / 戻り | 良い | - | - |
| `testUpDateFoodNullMode0Type0` | up / date / food / null / mode0 / type0 | 設置物/アイテム動作回帰 / up / date / food / null / mode0 / type0 | 良い | - | - |
| `testReadIniFileDoesNotThrow` | read / ini / file / does / 非 / 例外 | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testFeedTypeEnumToString` | feed / type / enum / to / string | 設置物/アイテム動作回帰 / feed / type / enum / to / string | 良い | - | - |
| `testFeedModeEnumToString` | feed / mode / enum / to / string | 設置物/アイテム動作回帰 / feed / mode / enum / to / string | 良い | - | - |
| `testGetImageLayerEnabled` | 取得 / image / layer / enabled | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled | 良い | - | - |
| `testGetImageLayerDisabled` | 取得 / image / layer / disabled | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled | 良い | - | - |
| `testUpDateFoodNotInWorldMapClearsFood` | up / date / food / 非 / in / world / map / clears / food | 設置物/アイテム動作回帰 / up / date / food / 非 / in / world / map / clears / food | 良い | - | - |
| `testSetupFeederHeadlessExecutesCode` | setup / feeder / headless / executes / code | 設置物/アイテム動作回帰 / setup / feeder / headless / executes / code | 良い | - | assert:0 |
| `testSetupFeederModeHeadlessExecutesCode` | setup / feeder / mode / headless / executes / code | 設置物/アイテム動作回帰 / setup / feeder / mode / headless / executes / code | 良い | - | assert:0 |
| `testConstructorWithCoordsDoesNotThrow` | constructor / with / coords / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | assert:0 |
| `testIsTakenOutBodyHoldingFoodReturnsTrue` | 状態 / taken / out / 本体 / holding / food / 戻り / true | 設置物/アイテム動作回帰 / 状態 / taken / out / 本体 / holding / food / 戻り / true | 良い | - | - |
| `testMakeRandomTypeViaReflection` | make / random / type / via / reflection | 設置物/アイテム動作回帰 / make / random / type / via / reflection | 良い | - | reflection |
| `testScenarioNormalModeCreatesConcreteFoodAndRegistersItInWorld` | シナリオ / normal / mode / creates / concrete / food / and / registers / it / in / world | 設置物/アイテム動作回帰 / シナリオ / normal / mode / creates / concrete / food / and / registers / it / in / world | 良い | - | - |
| `testScenarioValidWorldFoodRemainsAttachedToFeeder` | シナリオ / valid / world / food / remains / attached / to / feeder | 設置物/アイテム動作回帰 / シナリオ / valid / world / food / remains / attached / to / feeder | 良い | - | - |
| `testScenarioRemovedFoodClearsFeederReference` | シナリオ / removed / food / clears / feeder / reference | 設置物/アイテム動作回帰 / シナリオ / removed / food / clears / feeder / reference | 良い | - | - |
| `testScenarioRegularModeCreatesConfiguredFoodAndConsumesCash` | シナリオ / regular / mode / creates / configured / food / and / consumes / cash | 設置物/アイテム動作回帰 / シナリオ / regular / mode / creates / configured / food / and / consumes / cash | 良い | - | - |

### `BarrierTest`
- 状態: 完了 (16/16 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetColorDefaultIsNull` | BARRIER_WALL の color が (128,128,128,255) であること | 設置物/アイテム動作回帰 / color 回帰 | 良い | - | - |
| `testGetAttributeDefaultIsZero` | WALL/GAP_MINI の attribute が正しい値に設定されること | 設置物/アイテム動作回帰 / attribute 回帰 | 良い | - | - |
| `testGetMinimumSizeReturns1` | 取得 / minimum / size / returns1 | 設置物/アイテム動作回帰 / 取得 / minimum / size / returns1 | 良い | - | - |
| `testConstructorWithArgsBarrierWallDoesNotThrow` | BARRIER_WALL のフィールド座標と color が正しく設定されること | 設置物/アイテム動作回帰 / constructor / フィールド座標 / color 回帰 | 良い | - | - |
| `testConstructorWithArgsBarrierGapMiniDoesNotThrow` | BARRIER_GAP_MINI の黄色 color と attribute が設定されること | 設置物/アイテム動作回帰 / constructor / GAP_MINI color 回帰 | 良い | - | - |
| `testDrawPreviewDoesNotThrow` | drawPreview が線上のピクセルを描画すること | 設置物/アイテム動作回帰 / 描画ピクセル回帰 | 良い | - | - |
| `testDrawShapeDoesNotThrow` | drawShape が barrier color でピクセルを描画すること | 設置物/アイテム動作回帰 / 描画ピクセル回帰 | 良い | - | - |
| `testClearBarrierDoesNotThrow` | clearBarrier 後にリストと壁マップ両方から消えること | 設置物/アイテム動作回帰 / clearBarrier 回帰 | 良い | - | - |
| `testOnBarrierNoWallsReturnsFalse` | on / barrier / なし / walls / 戻り / false | 設置物/アイテム動作回帰 / on / barrier / なし / walls / 戻り / false | 良い | - | - |
| `testGetBarrierEmptyListReturnsNull` | 空リスト・範囲外座標では null を返すこと | 設置物/アイテム動作回帰 / getBarrier null 回帰 | 良い | - | - |
| `testGetBarrierWithBarrierReturnsBarrier` | バリア線上の座標で getBarrier が当該バリアを返すこと | 設置物/アイテム動作回帰 / getBarrier 発見回帰 | 良い | - | - |
| `testAcrossBarrierNoWallsReturnsFalse` | across / barrier / なし / walls / 戻り / false | 設置物/アイテム動作回帰 / across / barrier / なし / walls / 戻り / false | 良い | - | - |
| `testScenarioConstructedWallMarksWallMapAndMakesOnBarrierTrue` | シナリオ / constructed / 壁 / marks / 壁 / map / and / makes / on / barrier / true | 設置物/アイテム動作回帰 / シナリオ / constructed / 壁 / marks / 壁 / map / and / makes / on / barrier / true | 良い | - | - |
| `testScenarioGetBarrierFindsExactBarrierOnItsLine` | シナリオ / 取得 / barrier / finds / exact / barrier / on / its / line | 設置物/アイテム動作回帰 / シナリオ / 取得 / barrier / finds / exact / barrier / on / its / line | 良い | - | - |
| `testScenarioClearBarrierRemovesWallMapPresenceAndLineBlocking` | シナリオ / 解除 / barrier / removes / 壁 / map / presence / and / line / blocking | 設置物/アイテム動作回帰 / シナリオ / 解除 / barrier / removes / 壁 / map / presence / and / line / blocking | 良い | - | - |

### `BedTest`
- 状態: 完了 (84/84 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 比較順序が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | 設置物/アイテム動作回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructor_House` | parameterized / constructor / house | 設置物/アイテム動作回帰 / parameterized / constructor / house | 良い | - | - |
| `testParameterizedConstructor_Nora` | parameterized / constructor / nora | 設置物/アイテム動作回帰 / parameterized / constructor / nora | 良い | - | - |
| `testParameterizedConstructor_Yasei` | parameterized / constructor / yasei | 設置物/アイテム動作回帰 / parameterized / constructor / yasei | 良い | - | - |
| `testConstructor_MapIndex5_HouseBecomesYasei` | constructor / map / index5 / house / becomes / yasei | 設置物/アイテム動作回帰 / constructor / map / index5 / house / becomes / yasei | 良い | - | - |
| `testConstructor_MapIndex6_HouseBecomesYasei` | constructor / map / index6 / house / becomes / yasei | 設置物/アイテム動作回帰 / constructor / map / index6 / house / becomes / yasei | 良い | - | - |
| `testConstructor_MapIndex5_NoraStaysNora` | constructor / map / index5 / nora / stays / nora | 設置物/アイテム動作回帰 / constructor / map / index5 / nora / stays / nora | 良い | - | - |
| `testConstructor_RegisteredInWorld` | constructor / registered / in / world | 設置物/アイテム動作回帰 / constructor / registered / in / world | 良い | - | - |
| `testInterval_Default` | interval / default | 設置物/アイテム動作回帰 / interval / default | 良い | - | - |
| `testSetItemRank_House` | 設定 / item / rank / house | 設置物/アイテム動作回帰 / 設定 / item / rank / house | 良い | - | - |
| `testSetItemRank_Nora` | 設定 / item / rank / nora | 設置物/アイテム動作回帰 / 設定 / item / rank / nora | 良い | - | - |
| `testSetItemRank_Yasei` | 設定 / item / rank / yasei | 設置物/アイテム動作回帰 / 設定 / item / rank / yasei | 良い | - | - |
| `testGetValue_House` | 取得 / value / house | 設置物/アイテム動作回帰 / 取得 / value / house | 良い | - | - |
| `testGetValue_Nora` | 取得 / value / nora | 設置物/アイテム動作回帰 / 取得 / value / nora | 良い | - | - |
| `testGetShadowImage_ReturnsNull` | 取得 / shadow / image / 戻り / null | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null | 良い | - | - |
| `testGetBounding_NotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testTakeScreenRect_NotNull` | take / screen / rect / 非 / null | 設置物/アイテム動作回帰 / take / screen / rect / 非 / null | 良い | - | - |
| `testTakeScreenRect_ContainsBoundaryValues` | take / screen / rect / contains / boundary / values | 設置物/アイテム動作回帰 / take / screen / rect / contains / boundary / values | 良い | - | - |
| `testRemoveListData_RemovesFromWorld` | 除去 / list / data / removes / from / world | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetObjEXType` | 取得 / obj / extype | 設置物/アイテム動作回帰 / 取得 / obj / extype | 良い | - | - |
| `testGetObjType` | 取得 / obj / type | 設置物/アイテム動作回帰 / 取得 / obj / type | 良い | - | - |
| `testCheckInterval` | 判定 / interval | 設置物/アイテム動作回帰 / 判定 / interval | 良い | - | - |
| `testGetEnabled_DefaultTrue` | 取得 / enabled / default / true | 設置物/アイテム動作回帰 / 取得 / enabled / default / true | 良い | - | - |
| `testSetEnabled` | 設定 / enabled | 設置物/アイテム動作回帰 / 設定 / enabled | 良い | - | - |
| `testInvertEnabled` | invert / enabled | 設置物/アイテム動作回帰 / invert / enabled | 良い | - | - |
| `testHasSetupMenu_DefaultFalse` | 有無 / setup / menu / default / false | 設置物/アイテム動作回帰 / 有無 / setup / menu / default / false | 良い | - | - |
| `testEnableHitCheck_DefaultTrue` | enable / hit / 判定 / default / true | 設置物/アイテム動作回帰 / enable / hit / 判定 / default / true | 良い | - | - |
| `testGetOption` | 取得 / option | 設置物/アイテム動作回帰 / 取得 / option | 良い | - | - |
| `testSetOption` | 設定 / option | 設置物/アイテム動作回帰 / 設定 / option | 良い | - | - |
| `testGetLooks_Default` | 取得 / looks / default | 設置物/アイテム動作回帰 / 取得 / looks / default | 良い | - | - |
| `testSetLooks` | 設定 / looks | 設置物/アイテム動作回帰 / 設定 / looks | 良い | - | - |
| `testGetLinkParent_Default` | 取得 / link / 親 / default | 設置物/アイテム動作回帰 / 取得 / link / 親 / default | 良い | - | - |
| `testSetLinkParent` | 設定 / link / 親 | 設置物/アイテム動作回帰 / 設定 / link / 親 | 良い | - | - |
| `testGetSetColW` | 取得 / 設定 / col / w | 設置物/アイテム動作回帰 / 取得 / 設定 / col / w | 良い | - | - |
| `testGetSetColH` | 取得 / 設定 / col / h | 設置物/アイテム動作回帰 / 取得 / 設定 / col / h | 良い | - | - |
| `testGetX_GetY` | 取得 / xget / y | 設置物/アイテム動作回帰 / 取得 / xget / y | 良い | - | - |
| `testSetX_SetY` | 設定 / xset / y | 設置物/アイテム動作回帰 / 設定 / xset / y | 良い | - | - |
| `testGetZ_Default` | 取得 / zdefault | 設置物/アイテム動作回帰 / 取得 / zdefault | 良い | - | - |
| `testIsRemoved_Default` | 状態 / removed / default | 設置物/アイテム動作回帰 / 状態 / removed / default | 良い | - | - |
| `testRemove` | 除去 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testIsCanGrab_Default` | 状態 / 可否 / grab / default | 設置物/アイテム動作回帰 / 状態 / 可否 / grab / default | 良い | - | - |
| `testGrab` | grab | 設置物/アイテム動作回帰 / grab | 良い | - | - |
| `testRelease` | release | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testGetAge_Default` | 取得 / age / default | 設置物/アイテム動作回帰 / 取得 / age / default | 良い | - | - |
| `testSetAge` | 設定 / age | 設置物/アイテム動作回帰 / 設定 / age | 良い | - | - |
| `testAddAge` | 追加 / age | 設置物/アイテム動作回帰 / 追加 / age | 良い | - | - |
| `testSetVx_Vy_Vz` | 設定 / vx / vy / vz | 設置物/アイテム動作回帰 / 設定 / vx / vy / vz | 良い | - | - |
| `testKick` | kick | 設置物/アイテム動作回帰 / kick | 良い | - | - |
| `testGetCost` | 取得 / cost | 設置物/アイテム動作回帰 / 取得 / cost | 良い | - | - |
| `testSetCost` | 設定 / cost | 設置物/アイテム動作回帰 / 設定 / cost | 良い | - | - |
| `testSetValue` | 設定 / value | 設置物/アイテム動作回帰 / 設定 / value | 良い | - | - |
| `testSetCanGrab` | 設定 / 可否 / grab | 設置物/アイテム動作回帰 / 設定 / 可否 / grab | 良い | - | - |
| `testSetGrabbed` | 設定 / grabbed | 設置物/アイテム動作回帰 / 設定 / grabbed | 良い | - | - |
| `testSetRemoved` | 設定 / removed | 設置物/アイテム動作回帰 / 設定 / removed | 良い | - | - |
| `testIsEnableWall_Default` | 状態 / enable / 壁 / default | 設置物/アイテム動作回帰 / 状態 / enable / 壁 / default | 良い | - | - |
| `testSetEnableWall` | 設定 / enable / 壁 | 設置物/アイテム動作回帰 / 設定 / enable / 壁 | 良い | - | - |
| `testGetSetOfsX_OfsY` | 取得 / 設定 / ofs / xofs / y | 設置物/アイテム動作回帰 / 取得 / 設定 / ofs / xofs / y | 良い | - | - |
| `testGetDrawOfsX_DrawOfsY` | 取得 / draw / ofs / xdraw / ofs / y | 設置物/アイテム動作回帰 / 取得 / draw / ofs / xdraw / ofs / y | 良い | - | - |
| `testGetScreenPivot_NotNull` | 取得 / screen / pivot / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / pivot / 非 / null | 良い | - | - |
| `testSetScreenPivot` | 設定 / screen / pivot | 設置物/アイテム動作回帰 / 設定 / screen / pivot | 良い | - | - |
| `testGetScreenRect_NotNull` | 取得 / screen / rect / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / rect / 非 / null | 良い | - | - |
| `testSetBxyz_ResetBPos` | 設定 / bxyz / reset / bpos | 設置物/アイテム動作回帰 / 設定 / bxyz / reset / bpos | 良い | - | - |
| `testAddBxyz` | 追加 / bxyz | 設置物/アイテム動作回帰 / 追加 / bxyz | 良い | - | - |
| `testIsFallingUnderGround_Default` | 状態 / falling / under / ground / default | 設置物/アイテム動作回帰 / 状態 / falling / under / ground / default | 良い | - | - |
| `testSetFallingUnderGround` | 設定 / falling / under / ground | 設置物/アイテム動作回帰 / 設定 / falling / under / ground | 良い | - | - |
| `testIsbInPool_Default` | isb / in / pool / default | 設置物/アイテム動作回帰 / isb / in / pool / default | 良い | - | - |
| `testSetbInPool` | setb / in / pool | 設置物/アイテム動作回帰 / setb / in / pool | 良い | - | - |
| `testGetnMostDepth_Default` | getn / most / depth / default | 設置物/アイテム動作回帰 / getn / most / depth / default | 良い | - | - |
| `testSetnMostDepth` | setn / most / depth | 設置物/アイテム動作回帰 / setn / most / depth | 良い | - | - |
| `testGetSetBindObj` | 取得 / 設定 / bind / obj | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj | 良い | - | - |
| `testCompareTo` | compare / to | 設置物/アイテム動作回帰 / compare / to | 良い | - | - |
| `testGetVxyz` | 取得 / vxyz | 設置物/アイテム動作回帰 / 取得 / vxyz | 良い | - | - |
| `testCalcPos_ClampBoundary` | calc / pos / 範囲補正 / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / boundary | 良い | - | - |
| `testCalcPos_ClampUpperBoundary` | calc / pos / 範囲補正 / upper / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / upper / boundary | 良い | - | - |
| `testGetBoundaryShape` | 取得 / boundary / shape | 設置物/アイテム動作回帰 / 取得 / boundary / shape | 良い | - | - |
| `testGetSetImgW_ImgH` | 取得 / 設定 / img / wimg / h | 設置物/アイテム動作回帰 / 取得 / 設定 / img / wimg / h | 良い | - | - |
| `testGetSetPivX_PivY` | 取得 / 設定 / piv / xpiv / y | 設置物/アイテム動作回帰 / 取得 / 設定 / piv / xpiv / y | 良い | - | - |
| `testSetInterval` | 設定 / interval | 設置物/アイテム動作回帰 / 設定 / interval | 良い | - | - |
| `testSetObjEXType` | 設定 / obj / extype | 設置物/アイテム動作回帰 / 設定 / obj / extype | 良い | - | - |
| `testSetOfsXY` | 設定 / ofs / xy | 設置物/アイテム動作回帰 / 設定 / ofs / xy | 良い | - | - |
| `testGetTmpPos_NotNull` | 取得 / tmp / pos / 非 / null | 設置物/アイテム動作回帰 / 取得 / tmp / pos / 非 / null | 良い | - | - |
| `testGetHitCheckObjType_DefaultZero` | 取得 / hit / 判定 / obj / type / default / zero | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type / default / zero | 良い | - | - |
| `testMultipleBedsHaveUniqueIds` | multiple / beds / have / unique / ids | 設置物/アイテム動作回帰 / multiple / beds / have / unique / ids | 良い | - | - |
| `testObjHitProcess_Default` | obj / hit / process / default | 設置物/アイテム動作回帰 / obj / hit / process / default | 良い | - | - |

### `BeltconveyorObjTest`
- 状態: 完了 (66/66 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testActionEnum` | action / enum | 設置物/アイテム動作回帰 / action / enum | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testCheckInterval_AlwaysTrue` | 判定 / interval / always / true | 設置物/アイテム動作回帰 / 判定 / interval / always / true | 良い | - | - |
| `testSetupBeltconveyor_AlwaysTrue` | setup / beltconveyor / always / true | 設置物/アイテム動作回帰 / setup / beltconveyor / always / true | 良い | - | - |
| `testGetSetBeltSpeed` | 取得 / 設定 / belt / speed | 設置物/アイテム動作回帰 / 取得 / 設定 / belt / speed | 良い | - | - |
| `testGetSetHou_before` | 取得 / 設定 / hou / before | 設置物/アイテム動作回帰 / 取得 / 設定 / hou / before | 良い | - | - |
| `testGetSetObj_before` | 取得 / 設定 / obj / before | 設置物/アイテム動作回帰 / 取得 / 設定 / obj / before | 良い | - | - |
| `testGetSetMove_before` | 取得 / 設定 / 移動 / before | 設置物/アイテム動作回帰 / 取得 / 設定 / 移動 / before | 良い | - | - |
| `testGetSetSpeed_before` | 取得 / 設定 / speed / before | 設置物/アイテム動作回帰 / 取得 / 設定 / speed / before | 良い | - | - |
| `testGetSetTargetType` | 取得 / 設定 / target / type | 設置物/アイテム動作回帰 / 取得 / 設定 / target / type | 良い | - | - |
| `testGetSetCantmove` | 取得 / 設定 / cantmove | 設置物/アイテム動作回帰 / 取得 / 設定 / cantmove | 良い | - | - |
| `testGetSetMoveOnce` | 取得 / 設定 / 移動 / once | 設置物/アイテム動作回帰 / 取得 / 設定 / 移動 / once | 良い | - | - |
| `testGetSetBindObjList` | 取得 / 設定 / bind / obj / list | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj / list | 良い | - | - |
| `testGetSetSelectedYukkuriType` | 取得 / 設定 / selected / yukkuri / type | 設置物/アイテム動作回帰 / 取得 / 設定 / selected / yukkuri / type | 良い | - | - |
| `testGetSetObOptionSelectionList` | 取得 / 設定 / ob / option / selection / list | 設置物/アイテム動作回帰 / 取得 / 設定 / ob / option / selection / list | 良い | - | - |
| `testGetSetFilter` | 取得 / 設定 / filter | 設置物/アイテム動作回帰 / 取得 / 設定 / filter | 良い | - | - |
| `testSetFilterMethod` | 設定 / filter / method | 設置物/アイテム動作回帰 / 設定 / filter / method | 良い | - | - |
| `testGetSetYukkuriFilter` | 取得 / 設定 / yukkuri / filter | 設置物/アイテム動作回帰 / 取得 / 設定 / yukkuri / filter | 良い | - | - |
| `testGetOptionFilter` | 取得 / option / filter | 設置物/アイテム動作回帰 / 取得 / option / filter | 良い | - | - |
| `testGetOptionResultFilter` | 取得 / option / result / filter | 設置物/アイテム動作回帰 / 取得 / option / result / filter | 良い | - | - |
| `testSetOptionResultFilter` | 設定 / option / result / filter | 設置物/アイテム動作回帰 / 設定 / option / result / filter | 良い | - | - |
| `testGetSetFieldSX` | 取得 / 設定 / field / sx | 設置物/アイテム動作回帰 / 取得 / 設定 / field / sx | 良い | - | - |
| `testGetSetFieldSY` | 取得 / 設定 / field / sy | 設置物/アイテム動作回帰 / 取得 / 設定 / field / sy | 良い | - | - |
| `testGetSetFieldEX` | 取得 / 設定 / field / ex | 設置物/アイテム動作回帰 / 取得 / 設定 / field / ex | 良い | - | - |
| `testGetSetFieldEY` | 取得 / 設定 / field / ey | 設置物/アイテム動作回帰 / 取得 / 設定 / field / ey | 良い | - | - |
| `testGetSetFirstX` | 取得 / 設定 / first / x | 設置物/アイテム動作回帰 / 取得 / 設定 / first / x | 良い | - | - |
| `testGetSetFirstY` | 取得 / 設定 / first / y | 設置物/アイテム動作回帰 / 取得 / 設定 / first / y | 良い | - | - |
| `testGetSetAnPointX` | 取得 / 設定 / an / point / x | 設置物/アイテム動作回帰 / 取得 / 設定 / an / point / x | 良い | - | - |
| `testGetSetAnPointY` | 取得 / 設定 / an / point / y | 設置物/アイテム動作回帰 / 取得 / 設定 / an / point / y | 良い | - | - |
| `testRemoveListData_EmptyBindList` | 除去 / list / data / empty / bind / list | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListData_WithBodyInBindList` | 除去 / list / data / with / 本体 / in / bind / list | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListData_WithNullInBindList` | 除去 / list / data / with / null / in / bind / list | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDate_AgeNotDivisibleBy2400` | up / date / age / 非 / divisible / by2400 | 設置物/アイテム動作回帰 / up / date / age / 非 / divisible / by2400 | 良い | - | - |
| `testUpDate_AgeDivisibleBy2400` | up / date / age / divisible / by2400 | 設置物/アイテム動作回帰 / up / date / age / divisible / by2400 | 良い | - | - |
| `testObjHitProcess_TargetType1_NonBody_ReturnsZero` | obj / hit / process / target / type1 / non / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / target / type1 / non / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcess_TargetType2_Body_ReturnsZero` | obj / hit / process / target / type2 / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / target / type2 / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcess_TargetType3_Body_ReturnsZero` | obj / hit / process / target / type3 / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / target / type3 / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcess_TargetType4_NonStalk_ReturnsZero` | obj / hit / process / target / type4 / non / stalk / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / target / type4 / non / stalk / 戻り / zero | 良い | - | - |
| `testObjHitProcess_TargetTypeDefault_Body_ReturnsZero` | obj / hit / process / target / type / default / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / target / type / default / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcess_TargetType0_Shit_MovesBeltSpeed0` | obj / hit / process / target / type0 / shit / moves / belt / speed0 | 設置物/アイテム動作回帰 / obj / hit / process / target / type0 / shit / moves / belt / speed0 | 良い | - | - |
| `testObjHitProcess_Cantmove1_Body_SetsFlag` | obj / hit / process / cantmove1 / 本体 / sets / flag | 設置物/アイテム動作回帰 / obj / hit / process / cantmove1 / 本体 / sets / flag | 良い | - | - |
| `testObjHitProcess_Option0_MovesByBeltSpeed` | obj / hit / process / option0 / moves / by / belt / speed | 設置物/アイテム動作回帰 / obj / hit / process / option0 / moves / by / belt / speed | 良い | - | - |
| `testObjHitProcess_Option1_MovesByBeltSpeedDown` | obj / hit / process / option1 / moves / by / belt / speed / down | 設置物/アイテム動作回帰 / obj / hit / process / option1 / moves / by / belt / speed / down | 良い | - | - |
| `testObjHitProcess_Option2_MovesByBeltSpeedRight` | obj / hit / process / option2 / moves / by / belt / speed / right | 設置物/アイテム動作回帰 / obj / hit / process / option2 / moves / by / belt / speed / right | 良い | - | - |
| `testObjHitProcess_Option3_MovesByBeltSpeedLeft` | obj / hit / process / option3 / moves / by / belt / speed / left | 設置物/アイテム動作回帰 / obj / hit / process / option3 / moves / by / belt / speed / left | 良い | - | - |
| `testObjHitProcess_FilterEnabled_BodyInFilter_ReturnsZero` | obj / hit / process / filter / enabled / 本体 / in / filter / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / filter / enabled / 本体 / in / filter / 戻り / zero | 良い | - | - |
| `testGetImage_returnsNull` | 取得 / image / 戻り / null | 設置物/アイテム動作回帰 / 取得 / image / 戻り / null | 良い | - | - |
| `testGetImageLayerCount_returnsZero` | 取得 / image / layer / count / 戻り / zero | 設置物/アイテム動作回帰 / 取得 / image / layer / count / 戻り / zero | 良い | - | - |
| `testGetShadowImage_returnsNull` | 取得 / shadow / image / 戻り / null | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null | 良い | - | - |
| `testCheckHitObj_LockmoveYukkuri_ReturnsFalse` | 判定 / hit / obj / lockmove / yukkuri / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / lockmove / yukkuri / 戻り / false | 良い | - | - |
| `testCheckHitObj_RemovedObj_RemovesFromList` | 判定 / hit / obj / removed / obj / removes / from / list | 設置物/アイテム動作回帰 / 判定 / hit / obj / removed / obj / removes / from / list | 良い | - | - |
| `testCheckHitObj_NotContained_ReturnsFalse` | 判定 / hit / obj / 非 / contained / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / 非 / contained / 戻り / false | 良い | - | - |
| `testCheckContain_mapCoords_doesNotThrow` | 判定 / contain / map / coords / outside / false | 設置物/アイテム動作回帰 / 判定 / contain / map / coords | 良い | - | - |
| `testCheckContain_outsideBounds_returnsFalse` | 判定 / contain / outside / bounds / 戻り / false | 設置物/アイテム動作回帰 / 判定 / contain / outside / bounds / 戻り / false | 良い | - | - |
| `testCheckContain_fieldCoords_doesNotThrow` | 判定 / contain / field / coords / outside / false | 設置物/アイテム動作回帰 / 判定 / contain / field / coords | 良い | - | - |
| `testGetImageLayer_option0_returnsOne` | 取得 / image / layer / option0 / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / option0 / 戻り / one | 良い | - | - |
| `testGetImageLayer_option1_returnsOne` | 取得 / image / layer / option1 / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / option1 / 戻り / one | 良い | - | - |
| `testGetImageLayer_option2_returnsOne` | 取得 / image / layer / option2 / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / option2 / 戻り / one | 良い | - | - |
| `testGetImageLayer_option3_returnsOne` | 取得 / image / layer / option3 / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / option3 / 戻り / one | 良い | - | - |
| `testGetImageLayer_g2_withImage_returnsOne` | 取得 / image / layer / g2 / with / image / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / g2 / with / image / 戻り / one | 良い | - | - |
| `testDrawPreview_doesNotThrow` | draw / preview / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / preview / does / 非 / 例外 | 良い | - | - |
| `testSetBeltconveyor_headless_executesCode` | 設定 / beltconveyor / headless / executes / code | 設置物/アイテム動作回帰 / 設定 / beltconveyor / headless / executes / code | 良い | - | - |
| `testConstructor_WithCoords_doesNotThrow` | constructor / with / coords / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | - |
| `testLoadImages_headless_executesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |

### `BeltconveyorTest`
- 状態: 完了 (33/33 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetAttribute_returnsFIELD_BELT` | 取得 / attribute / 戻り / fieldbelt | 設置物/アイテム動作回帰 / 取得 / attribute / 戻り / fieldbelt | 良い | - | - |
| `testGetMinimumSize_returns8` | 取得 / minimum / size / returns8 | 設置物/アイテム動作回帰 / 取得 / minimum / size / returns8 | 良い | - | - |
| `testDrawPreview_doesNotThrow` | draw / preview / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / preview / does / 非 / 例外 | 良い | - | - |
| `testLoadImages_headless_executesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetSetObjId` | 取得 / 設定 / obj / id | 設置物/アイテム動作回帰 / 取得 / 設定 / obj / id | 良い | - | - |
| `testGetSetSetting` | 取得 / 設定 / setting | 設置物/アイテム動作回帰 / 取得 / 設定 / setting | 良い | - | - |
| `testGetBeltconveyor_emptyList_returnsNull` | 取得 / beltconveyor / empty / list / 戻り / null | 設置物/アイテム動作回帰 / 取得 / beltconveyor / empty / list / 戻り / null | 良い | - | - |
| `testDeleteBelt_doesNotThrow` | delete / belt / does / 非 / 例外 | 設置物/アイテム動作回帰 / delete / belt / does / 非 / 例外 | 良い | - | - |
| `testSetupBelt_headless_executesCode` | setup / belt / headless / executes / code | 設置物/アイテム動作回帰 / setup / belt / headless / executes / code | 良い | - | - |
| `testCheckHitObj_shitType_settingFalse_returnsFalse` | 判定 / hit / obj / shit / type / setting / false / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / shit / type / setting / false / 戻り / false | 良い | - | - |
| `testCheckHitObj_vomitType_settingFalse_returnsFalse` | 判定 / hit / obj / vomit / type / setting / false / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / vomit / type / setting / false / 戻り / false | 良い | - | - |
| `testCheckHitObj_foodType_settingFalse_returnsFalse` | 判定 / hit / obj / food / type / setting / false / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / food / type / setting / false / 戻り / false | 良い | - | - |
| `testCheckHitObj_stalkType_settingFalse_returnsFalse` | 判定 / hit / obj / stalk / type / setting / false / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / stalk / type / setting / false / 戻り / false | 良い | - | - |
| `testCheckHitObj_yukkuriType_settingFalse_returnsFalse` | 判定 / hit / obj / yukkuri / type / setting / false / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / yukkuri / type / setting / false / 戻り / false | 良い | - | - |
| `testProcessHitObj_directionNull_throwsNPEOrDoesNotThrow` | process / hit / obj / direction / null / 例外 / npeor / does / 非 / 例外 | 設置物/アイテム動作回帰 / process / hit / obj / direction / null / 例外 / npeor / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopup_setup_headless_executesCode` | execute / shape / popup / setup / headless / executes / code | 設置物/アイテム動作回帰 / execute / shape / popup / setup / headless / executes / code | 良い | - | - |
| `testExecuteShapePopup_top_doesNotThrow` | execute / shape / popup / top / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / top / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopup_down_doesNotThrow` | execute / shape / popup / down / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / down / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopup_bottom_doesNotThrow` | execute / shape / popup / bottom / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / bottom / does / 非 / 例外 | 良い | - | - |
| `testConstructor_WithCoords_executesCode` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testGetBeltconveyor_withItem_foundInside` | 取得 / beltconveyor / with / item / found / inside | 設置物/アイテム動作回帰 / 取得 / beltconveyor / with / item / found / inside | 良い | - | - |
| `testStaticConstants_notNull` | static / constants / 非 / null | 設置物/アイテム動作回帰 / static / constants / 非 / null | 良い | - | - |
| `testHasShapePopup_returnsNonNull` | 有無 / shape / popup / 戻り / non / null | 設置物/アイテム動作回帰 / 有無 / shape / popup / 戻り / non / null | 良い | - | - |
| `testGetDirection_returnsNull_afterDefaultConstructor` | 取得 / direction / 戻り / null / after / default / constructor | 設置物/アイテム動作回帰 / 取得 / direction / 戻り / null / after / default / constructor | 良い | - | - |
| `testGetBeltSpeed_returnsNull_afterDefaultConstructor` | 取得 / belt / speed / 戻り / null / after / default / constructor | 設置物/アイテム動作回帰 / 取得 / belt / speed / 戻り / null / after / default / constructor | 良い | - | - |
| `testSetDirection_null_setsToNull` | 設定 / direction / null / sets / to / null | 設置物/アイテム動作回帰 / 設定 / direction / null / sets / to / null | 良い | - | - |
| `testSetBeltSpeed_null_setsToNull` | 設定 / belt / speed / null / sets / to / null | 設置物/アイテム動作回帰 / 設定 / belt / speed / null / sets / to / null | 良い | - | - |
| `testProcessHitObj_withDirection_executesCode` | process / hit / obj / with / direction / executes / code | 設置物/アイテム動作回帰 / process / hit / obj / with / direction / executes / code | 良い | - | - |
| `testDrawShape_withDirection_executesCode` | draw / shape / with / direction / executes / code | 設置物/アイテム動作回帰 / draw / shape / with / direction / executes / code | 良い | - | - |
| `testScenario_NormalAdultSettingAcceptsAdultBody` | シナリオ / normal / adult / setting / accepts / adult / 本体 | 設置物/アイテム動作回帰 / シナリオ / normal / adult / setting / accepts / adult / 本体 | 良い | - | - |
| `testScenario_FoodSettingAcceptsFoodObjects` | シナリオ / food / setting / accepts / food / objects | 設置物/アイテム動作回帰 / シナリオ / food / setting / accepts / food / objects | 良い | - | - |
| `testScenario_RightMiddleBeltAddsPositiveXVelocity` | シナリオ / right / middle / belt / adds / positive / xvelocity | 設置物/アイテム動作回帰 / シナリオ / right / middle / belt / adds / positive / xvelocity | 良い | - | - |

### `BreedingPoolTest`
- 状態: 完了 (29/29 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testPoolTypeEnum` | pool / type / enum | 設置物/アイテム動作回帰 / pool / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testGetSetHighQuality` | 取得 / 設定 / high / quality | 設置物/アイテム動作回帰 / 取得 / 設定 / high / quality | 良い | - | - |
| `testGetSetStalkPool` | 取得 / 設定 / stalk / pool | 設置物/アイテム動作回帰 / 取得 / 設定 / stalk / pool | 良い | - | - |
| `testGetSetLiquidYukkuriType` | 取得 / 設定 / liquid / yukkuri / type | 設置物/アイテム動作回帰 / 取得 / 設定 / liquid / yukkuri / type | 良い | - | - |
| `testGetSetLastSelected` | 取得 / 設定 / last / selected | 設置物/アイテム動作回帰 / 取得 / 設定 / last / selected | 良い | - | - |
| `testGetValue_AllOptions` | 取得 / value / all / options | 設置物/アイテム動作回帰 / 取得 / value / all / options | 良い | - | - |
| `testGetCost_AllOptions` | 取得 / cost / all / options | 設置物/アイテム動作回帰 / 取得 / cost / all / options | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcess_Disabled` | obj / hit / process / disabled | 設置物/アイテム動作回帰 / obj / hit / process / disabled | 良い | - | - |
| `testObjHitProcess_BodyCastration_ReturnsZero` | obj / hit / process / 本体 / castration / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / 本体 / castration / 戻り / zero | 良い | - | - |
| `testObjHitProcess_StalkCastration_ReturnsZero` | obj / hit / process / stalk / castration / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / stalk / castration / 戻り / zero | 良い | - | - |
| `testObjHitProcess_DeadCrushed_SetsLiquidType` | obj / hit / process / 死亡 / crushed / sets / liquid / type | 設置物/アイテム動作回帰 / obj / hit / process / 死亡 / crushed / sets / liquid / type | 良い | - | - |
| `testObjHitProcess_AlivePools_AddsBaby` | obj / hit / process / alive / pools / adds / baby | 設置物/アイテム動作回帰 / obj / hit / process / alive / pools / adds / baby | 良い | - | - |
| `testCry_NoBabyOrStalk` | cry / なし / baby / or / stalk | 設置物/アイテム動作回帰 / cry / なし / baby / or / stalk | 良い | - | - |
| `testCry_WithBabyOrStalk_NotNYD` | cry / with / baby / or / stalk / 非 / 非ゆっくり症 | 設置物/アイテム動作回帰 / cry / with / baby / or / stalk / 非 / 非ゆっくり症 | 良い | - | - |
| `testCry_WithBabyOrStalk_IsNYD` | cry / with / baby / or / stalk / 状態 / 非ゆっくり症 | 設置物/アイテム動作回帰 / cry / with / baby / or / stalk / 状態 / 非ゆっくり症 | 良い | - | - |
| `testGetImageLayer_Enabled_LiquidType2` | 取得 / image / layer / enabled / liquid / type2 | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / liquid / type2 | 良い | - | - |
| `testGetImageLayer_Enabled_LiquidTypeOther` | 取得 / image / layer / enabled / liquid / type / other | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / liquid / type / other | 良い | - | - |
| `testGetImageLayer_Enabled_LiquidTypeNeg1` | 取得 / image / layer / enabled / liquid / type / neg1 | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / liquid / type / neg1 | 良い | - | - |
| `testGetImageLayer_Disabled` | 取得 / image / layer / disabled | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled | 良い | - | - |
| `testSetupPool_headless_executesCode` | setup / pool / headless / executes / code | 設置物/アイテム動作回帰 / setup / pool / headless / executes / code | 良い | - | - |
| `testConstructor_WithCoords_doesNotThrow` | constructor / with / coords / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | - |
| `testLoadImages_headless_executesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testScenario_NormalPoolAddsSingleBabyAndChargesCost` | シナリオ / normal / pool / adds / single / baby / and / charges / cost | 設置物/アイテム動作回帰 / シナリオ / normal / pool / adds / single / baby / and / charges / cost | 良い | - | - |
| `testScenario_StalkPoolAddsFiveStalkBabiesAndChargesCost` | シナリオ / stalk / pool / adds / five / stalk / babies / and / charges / cost | 設置物/アイテム動作回帰 / シナリオ / stalk / pool / adds / five / stalk / babies / and / charges / cost | 良い | - | - |

### `DiffuserTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testSteamTypeEnum` | steam / type / enum | 設置物/アイテム動作回帰 / steam / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testGetShadowImage_DoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testGetSetSteamType` | 取得 / 設定 / steam / type | 設置物/アイテム動作回帰 / 取得 / 設定 / steam / type | 良い | - | - |
| `testGetSetSteamNum` | 取得 / 設定 / steam / num | 設置物/アイテム動作回帰 / 取得 / 設定 / steam / num | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDate_Disabled` | up / date / disabled | 設置物/アイテム動作回帰 / up / date / disabled | 良い | - | - |
| `testUpDate_Enabled_NoSteamTypeSet` | up / date / enabled / なし / steam / type / 設定 | 設置物/アイテム動作回帰 / up / date / enabled / なし / steam / type / 設定 | 良い | - | - |
| `testUpDate_Enabled_AgeModulo40NotZero` | up / date / enabled / age / modulo40 / 非 / zero | 設置物/アイテム動作回帰 / up / date / enabled / age / modulo40 / 非 / zero | 良い | - | - |
| `testGetImageLayer_enabled_doesNotThrow` | 取得 / image / layer / enabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayer_disabled_doesNotThrow` | 取得 / image / layer / disabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled / does / 非 / 例外 | 良い | - | - |
| `testConstructor_WithCoords_executesCode` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testSetupDiffuser_headless_executesCode` | setup / diffuser / headless / executes / code | 設置物/アイテム動作回帰 / setup / diffuser / headless / executes / code | 良い | - | - |

### `FarmTest`
- 状態: 完了 (51/51 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetAttribute` | 取得 / attribute | 設置物/アイテム動作回帰 / 取得 / attribute | 良い | - | - |
| `testGetMinimumSize` | 取得 / minimum / size | 設置物/アイテム動作回帰 / 取得 / minimum / size | 良い | - | - |
| `testHasShapePopup` | 有無 / shape / popup | 設置物/アイテム動作回帰 / 有無 / shape / popup | 良い | - | - |
| `testRemoveAndIsRemoved` | 除去 / and / 状態 / removed | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetSetAge` | 取得 / 設定 / age | 設置物/アイテム動作回帰 / 取得 / 設定 / age | 良い | - | - |
| `testClockTick_NotRemoved` | clock / tick / 非 / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTick_Removed` | clock / tick / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testMapContains_Inside` | map / contains / inside | 設置物/アイテム動作回帰 / map / contains / inside | 良い | - | - |
| `testMapContains_Outside` | map / contains / outside | 設置物/アイテム動作回帰 / map / contains / outside | 良い | - | - |
| `testFieldContains_Inside` | field / contains / inside | 設置物/アイテム動作回帰 / field / contains / inside | 良い | - | - |
| `testFieldContains_Outside` | field / contains / outside | 設置物/アイテム動作回帰 / field / contains / outside | 良い | - | - |
| `testGetSetMapW_H` | 取得 / 設定 / map / wh | 設置物/アイテム動作回帰 / 取得 / 設定 / map / wh | 良い | - | - |
| `testGetSetFieldW_H` | 取得 / 設定 / field / wh | 設置物/アイテム動作回帰 / 取得 / 設定 / field / wh | 良い | - | - |
| `testGetSetMapPos_Getters` | 取得 / 設定 / map / pos / getters | 設置物/アイテム動作回帰 / 取得 / 設定 / map / pos / getters | 良い | - | - |
| `testGetSetFieldPos_Getters` | 取得 / 設定 / field / pos / getters | 設置物/アイテム動作回帰 / 取得 / 設定 / field / pos / getters | 良い | - | - |
| `testGetSetAmount` | 取得 / 設定 / amount | 設置物/アイテム動作回帰 / 取得 / 設定 / amount | 良い | - | - |
| `testGetSetAnPointX` | 取得 / 設定 / an / point / x | 設置物/アイテム動作回帰 / 取得 / 設定 / an / point / x | 良い | - | - |
| `testGetSetAnPointY` | 取得 / 設定 / an / point / y | 設置物/アイテム動作回帰 / 取得 / 設定 / an / point / y | 良い | - | - |
| `testCheckHitObj_Null_ReturnsFalse` | 判定 / hit / obj / null / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / null / 戻り / false | 良い | - | - |
| `testObjHitProcess_Null_ReturnsZero` | obj / hit / process / null / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / null / 戻り / zero | 良い | - | - |
| `testObjHitProcess_AirborneNonBody_ReturnsOne` | obj / hit / process / airborne / non / 本体 / 戻り / one | 設置物/アイテム動作回帰 / obj / hit / process / airborne / non / 本体 / 戻り / one | 良い | - | - |
| `testGetAmountFromNull_DoesNotThrow` | 取得 / amount / from / null / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / amount / from / null / does / 非 / 例外 | 良い | - | - |
| `testGetAmountFromShit_IncreasesFarmAmount` | 取得 / amount / from / shit / increases / farm / amount | 設置物/アイテム動作回帰 / 取得 / amount / from / shit / increases / farm / amount | 良い | - | - |
| `testGetAmountFromFood_NoChange` | 取得 / amount / from / food / なし / change | 設置物/アイテム動作回帰 / 取得 / amount / from / food / なし / change | 良い | - | - |
| `testGiveAmountNull_DoesNotThrow` | give / amount / null / does / 非 / 例外 | 設置物/アイテム動作回帰 / give / amount / null / does / 非 / 例外 | 良い | - | - |
| `testGetFarm_EmptyList_ReturnsNull` | 取得 / farm / empty / list / 戻り / null | 設置物/アイテム動作回帰 / 取得 / farm / empty / list / 戻り / null | 良い | - | - |
| `testGetFarm_WithFarm_ReturnsFarm` | 取得 / farm / with / farm / 戻り / farm | 設置物/アイテム動作回帰 / 取得 / farm / with / farm / 戻り / farm | 良い | - | - |
| `testGetFarm_OutsideArea_ReturnsNull` | 取得 / farm / outside / area / 戻り / null | 設置物/アイテム動作回帰 / 取得 / farm / outside / area / 戻り / null | 良い | - | - |
| `testDeleteFarm_RemovesFromList` | delete / farm / removes / from / list | 設置物/アイテム動作回帰 / delete / farm / removes / from / list | 良い | - | - |
| `testExecuteShapePopup_SETUP_DoesNotThrow` | execute / shape / popup / setup / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / setup / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopup_HARVEST_DoesNotThrow` | execute / shape / popup / harvest / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / harvest / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopup_TOP_MovesToFront` | execute / shape / popup / top / moves / to / front | 設置物/アイテム動作回帰 / execute / shape / popup / top / moves / to / front | 良い | - | - |
| `testExecuteShapePopup_BOTTOM_MovesToEnd` | execute / shape / popup / bottom / moves / to / end | 設置物/アイテム動作回帰 / execute / shape / popup / bottom / moves / to / end | 良い | - | - |
| `testExecuteShapePopup_UP_MovesUp` | execute / shape / popup / up / moves / up | 設置物/アイテム動作回帰 / execute / shape / popup / up / moves / up | 良い | - | - |
| `testExecuteShapePopup_DOWN_MovesDown` | execute / shape / popup / down / moves / down | 設置物/アイテム動作回帰 / execute / shape / popup / down / moves / down | 良い | - | - |
| `testDrawPreview_doesNotThrow` | draw / preview / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / preview / does / 非 / 例外 | 良い | - | - |
| `testDrawShape_doesNotThrow` | draw / shape / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / shape / does / 非 / 例外 | 良い | - | - |
| `testCheckContain_mapCoord` | 判定 / contain / map / coord | 設置物/アイテム動作回帰 / 判定 / contain / map / coord | 良い | - | - |
| `testCheckContain_fieldCoord` | 判定 / contain / field / coord | 設置物/アイテム動作回帰 / 判定 / contain / field / coord | 良い | - | - |
| `testGiveAmount_withBody_doesNotThrow` | give / amount / with / 本体 / does / 非 / 例外 | 設置物/アイテム動作回帰 / give / amount / with / 本体 / does / 非 / 例外 | 良い | - | - |
| `testLoadImages_headless_executesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testExecuteShapePopup_top_doesNotThrow` | execute / shape / popup / top / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / top / does / 非 / 例外 | 良い | - | - |
| `testGetFarm_emptyList_returnsNull` | 取得 / farm / empty / list / 戻り / null | 設置物/アイテム動作回帰 / 取得 / farm / empty / list / 戻り / null | 良い | - | - |
| `testDeleteFarm_doesNotThrow` | delete / farm / does / 非 / 例外 | 設置物/アイテム動作回帰 / delete / farm / does / 非 / 例外 | 良い | - | - |
| `testGetSetAmount_newMethod` | 取得 / 設定 / amount / new / method | 設置物/アイテム動作回帰 / 取得 / 設定 / amount / new / method | 良い | - | - |
| `testCheckHitObj_yukkuriInside_doesNotThrow` | 判定 / hit / obj / yukkuri / inside / does / 非 / 例外 | 設置物/アイテム動作回帰 / 判定 / hit / obj / yukkuri / inside / does / 非 / 例外 | 良い | - | - |
| `testObjHitProcess_yukkuriInside_doesNotThrow` | obj / hit / process / yukkuri / inside / does / 非 / 例外 | 設置物/アイテム動作回帰 / obj / hit / process / yukkuri / inside / does / 非 / 例外 | 良い | - | - |
| `testGetAmount_withObj_doesNotThrow` | 取得 / amount / with / obj / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / amount / with / obj / does / 非 / 例外 | 良い | - | - |
| `testConstructor_WithCoords_executesCode` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testScenario_ShitIsConvertedIntoFertilizer` | シナリオ / shit / is / converted / into / fertilizer | 設置物/アイテム動作回帰 / シナリオ / shit / is / converted / into / fertilizer | 良い | - | - |

### `FoodMakerTest`
- 状態: 完了 (34/34 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor_Default` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetBounding_notNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetShadowImage_null` | 取得 / shadow / image / null | 設置物/アイテム動作回帰 / 取得 / shadow / image / null | 良い | - | - |
| `testGetSetProcessReady` | 取得 / 設定 / process / ready | 設置物/アイテム動作回帰 / 取得 / 設定 / process / ready | 良い | - | - |
| `testGetSetStockFood` | 取得 / 設定 / stock / food | 設置物/アイテム動作回帰 / 取得 / 設定 / stock / food | 良い | - | - |
| `testGetSetFoodAmount` | 取得 / 設定 / food / amount | 設置物/アイテム動作回帰 / 取得 / 設定 / food / amount | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDate_processReady_doesNothing` | up / date / process / ready / does / nothing | 設置物/アイテム動作回帰 / up / date / process / ready / does / nothing | 良い | - | - |
| `testUpDate_notReadyAge0_setsReady` | up / date / 非 / ready / age0 / sets / ready | 設置物/アイテム動作回帰 / up / date / 非 / ready / age0 / sets / ready | 良い | - | - |
| `testUpDate_notReadyAge1_staysNotReady` | up / date / 非 / ready / age1 / stays / 非 / ready | 設置物/アイテム動作回帰 / up / date / 非 / ready / age1 / stays / 非 / ready | 良い | - | - |
| `testObjHitProcess_notReady_returns0` | obj / hit / process / 非 / ready / returns0 | 設置物/アイテム動作回帰 / obj / hit / process / 非 / ready / returns0 | 良い | - | - |
| `testObjHitProcess_stockNegative_Shit_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / shit / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / shit / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_Vomit_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / vomit / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / vomit / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodNormal_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / normal / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / normal / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodBitter_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / bitter / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / bitter / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodLemonpop_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / lemonpop / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / lemonpop / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodHot_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / hot / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / hot / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodViyugra_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / viyugra / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / viyugra / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testObjHitProcess_stockNegative_FoodWaste_consumesInputAndLeavesStockEmpty` | obj / hit / process / stock / negative / food / waste / consumes / input / and / leaves / stock / empty | 設置物/アイテム動作回帰 / obj / hit / process / stock / negative / food / waste / consumes / input / and / leaves / stock / empty | 良い | - | - |
| `testGetImageLayer_enabled` | 取得 / image / layer / enabled | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled | 良い | - | - |
| `testGetImageLayer_disabled` | 取得 / image / layer / disabled | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled | 良い | - | - |
| `testConstructor_WithCoords_registersFoodMakerInWorld` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testLoadImages_initializesBoundingAndLayers` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testObjHitProcess_stockPositive_FoodFOOD_consumesInputAndProducesFood` | obj / hit / process / stock / positive / food / food / consumes / input / and / produces / food | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / food / food | 良い | - | - |
| `testObjHitProcess_stockPositive_FoodBITTER_executesCode` | obj / hit / process / stock / positive / food / bitter / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / food / bitter | 良い | - | - |
| `testObjHitProcess_stockPositive_FoodLEMONPOP_executesCode` | obj / hit / process / stock / positive / food / lemonpop / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / food / lemonpop | 良い | - | - |
| `testObjHitProcess_stockPositive_FoodHOT_executesCode` | obj / hit / process / stock / positive / food / hot / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / food / hot | 良い | - | - |
| `testObjHitProcess_stockPositive_Shit_consumesInputAndCreatesOutputFood` | obj / hit / process / stock / positive / shit / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / shit | 良い | - | - |
| `testObjHitProcess_stockPositive_Vomit_consumesInputAndCreatesOutputFood` | obj / hit / process / stock / positive / vomit / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / vomit | 良い | - | - |
| `testObjHitProcess_stockPositive_Body_Reimu_consumesBodyAndConsumesCash` | obj / hit / process / stock / positive / 本体 / reimu / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / stock / positive / 本体 / reimu | 良い | - | - |
| `testUpDate_processReadyFalse_setsTrue` | up / date / process / ready / false / sets / true | 設置物/アイテム動作回帰 / up / date / process / ready / false / sets / true | 良い | - | - |
| `testScenario_CrushedBabyBodyContributesFoodAmountAndIsRemoved` | シナリオ / crushed / baby / 本体 / contributes / food / amount / and / 状態 / removed | 設置物/アイテム動作回帰 / シナリオ / crushed / baby / 本体 / contributes / food / amount / and / 状態 / removed | 良い | - | - |
| `testScenario_StoredFoodProcessesInputIntoOutputFoodAndConsumesCash` | シナリオ / stored / food / processes / input / into / output / food / and / consumes / cash | 設置物/アイテム動作回帰 / シナリオ / stored / food / processes / input / into / output / food / and / consumes / cash | 良い | - | - |

### `FoodTest`
- 状態: 完了 (140/140 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testConstructorWithCoordinates` | constructor / with / coordinates | 設置物/アイテム動作回帰 / constructor / with / coordinates | 良い | - | - |
| `testConstructorRegistersInWorldFoodMap` | constructor / registers / in / world / food / map | 設置物/アイテム動作回帰 / constructor / registers / in / world / food / map | 良い | - | - |
| `testConstructorSetsObjExtypeToFood` | constructor / sets / obj / extype / to / food | 設置物/アイテム動作回帰 / constructor / sets / obj / extype / to / food | 良い | - | - |
| `testConstructorSetsRemovedFalse` | constructor / sets / removed / false | 設置物/アイテム動作回帰 / constructor / sets / removed / false | 良い | - | - |
| `testConstructorSweets1` | constructor / sweets1 | 設置物/アイテム動作回帰 / constructor / sweets1 | 良い | - | - |
| `testConstructorSweets2` | constructor / sweets2 | 設置物/アイテム動作回帰 / constructor / sweets2 | 良い | - | - |
| `testConstructorStalk` | constructor / stalk | 設置物/アイテム動作回帰 / constructor / stalk | 良い | - | - |
| `testConstructorBitter` | constructor / bitter | 設置物/アイテム動作回帰 / constructor / bitter | 良い | - | - |
| `testConstructorLemonpop` | constructor / lemonpop | 設置物/アイテム動作回帰 / constructor / lemonpop | 良い | - | - |
| `testConstructorHot` | constructor / hot | 設置物/アイテム動作回帰 / constructor / hot | 良い | - | - |
| `testConstructorViyugra` | constructor / viyugra | 設置物/アイテム動作回帰 / constructor / viyugra | 良い | - | - |
| `testConstructorWaste` | constructor / waste | 設置物/アイテム動作回帰 / constructor / waste | 良い | - | - |
| `testConstructorInitializesAmountFromFoodType` | constructor / initializes / amount / from / food / type | 設置物/アイテム動作回帰 / constructor / initializes / amount / from / food / type | 良い | - | - |
| `testFoodTypeEnum` | food / type / enum | 設置物/アイテム動作回帰 / food / type / enum | 良い | - | - |
| `testFoodTypeEnumSweets1Ordinal` | food / type / enum / sweets1 / ordinal | 設置物/アイテム動作回帰 / food / type / enum / sweets1 / ordinal | 良い | - | - |
| `testFoodTypeEnumSweets2Ordinal` | food / type / enum / sweets2 / ordinal | 設置物/アイテム動作回帰 / food / type / enum / sweets2 / ordinal | 良い | - | - |
| `testFoodTypeEnumStalkOrdinal` | food / type / enum / stalk / ordinal | 設置物/アイテム動作回帰 / food / type / enum / stalk / ordinal | 良い | - | - |
| `testFoodTypeEnumGetValueFood` | food / type / enum / 取得 / value / food | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / food | 良い | - | - |
| `testFoodTypeEnumGetValueSweets1` | food / type / enum / 取得 / value / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / sweets1 | 良い | - | - |
| `testFoodTypeEnumGetValueSweets2` | food / type / enum / 取得 / value / sweets2 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / sweets2 | 良い | - | - |
| `testFoodTypeEnumGetValueStalk` | food / type / enum / 取得 / value / stalk | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / stalk | 良い | - | - |
| `testFoodTypeEnumGetValueWaste` | food / type / enum / 取得 / value / waste | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / waste | 良い | - | - |
| `testFoodTypeEnumGetValueVomit` | food / type / enum / 取得 / value / vomit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / vomit | 良い | - | - |
| `testFoodTypeEnumGetValueShit` | food / type / enum / 取得 / value / shit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / shit | 良い | - | - |
| `testFoodTypeEnumGetValueBitter` | food / type / enum / 取得 / value / bitter | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / bitter | 良い | - | - |
| `testFoodTypeEnumGetValueLemonpop` | food / type / enum / 取得 / value / lemonpop | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / lemonpop | 良い | - | - |
| `testFoodTypeEnumGetValueHot` | food / type / enum / 取得 / value / hot | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / hot | 良い | - | - |
| `testFoodTypeEnumGetValueViyugra` | food / type / enum / 取得 / value / viyugra | 設置物/アイテム動作回帰 / food / type / enum / 取得 / value / viyugra | 良い | - | - |
| `testFoodTypeEnumGetLooksFood` | food / type / enum / 取得 / looks / food | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / food | 良い | - | - |
| `testFoodTypeEnumGetLooksSweets1` | food / type / enum / 取得 / looks / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / sweets1 | 良い | - | - |
| `testFoodTypeEnumGetLooksSweets2` | food / type / enum / 取得 / looks / sweets2 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / sweets2 | 良い | - | - |
| `testFoodTypeEnumGetLooksStalk` | food / type / enum / 取得 / looks / stalk | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / stalk | 良い | - | - |
| `testFoodTypeEnumGetLooksBitter` | food / type / enum / 取得 / looks / bitter | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / bitter | 良い | - | - |
| `testFoodTypeEnumGetLooksLemonpop` | food / type / enum / 取得 / looks / lemonpop | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / lemonpop | 良い | - | - |
| `testFoodTypeEnumGetLooksHot` | food / type / enum / 取得 / looks / hot | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / hot | 良い | - | - |
| `testFoodTypeEnumGetLooksViyugra` | food / type / enum / 取得 / looks / viyugra | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / viyugra | 良い | - | - |
| `testFoodTypeEnumGetLooksWaste` | food / type / enum / 取得 / looks / waste | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / waste | 良い | - | - |
| `testFoodTypeEnumGetLooksShit` | food / type / enum / 取得 / looks / shit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / shit | 良い | - | - |
| `testFoodTypeEnumGetLooksBody` | food / type / enum / 取得 / looks / 本体 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / 本体 | 良い | - | - |
| `testFoodTypeEnumGetLooksVomit` | food / type / enum / 取得 / looks / vomit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / looks / vomit | 良い | - | - |
| `testFoodTypeEnumGetAmountFood` | food / type / enum / 取得 / amount / food | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / food | 良い | - | - |
| `testFoodTypeEnumGetAmountSweets1` | food / type / enum / 取得 / amount / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / sweets1 | 良い | - | - |
| `testFoodTypeEnumGetAmountStalk` | food / type / enum / 取得 / amount / stalk | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / stalk | 良い | - | - |
| `testFoodTypeEnumGetAmountVomit` | food / type / enum / 取得 / amount / vomit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / vomit | 良い | - | - |
| `testFoodTypeEnumGetAmountShit` | food / type / enum / 取得 / amount / shit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / shit | 良い | - | - |
| `testFoodTypeEnumGetAmountBody` | food / type / enum / 取得 / amount / 本体 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / 本体 | 良い | - | - |
| `testFoodTypeEnumGetAmountWaste` | food / type / enum / 取得 / amount / waste | 設置物/アイテム動作回帰 / food / type / enum / 取得 / amount / waste | 良い | - | - |
| `testFoodTypeEnumGetFileNameFood` | food / type / enum / 取得 / file / name / food | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / food | 良い | - | - |
| `testFoodTypeEnumGetFileNameSweets1` | food / type / enum / 取得 / file / name / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / sweets1 | 良い | - | - |
| `testFoodTypeEnumGetFileNameSweets2` | food / type / enum / 取得 / file / name / sweets2 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / sweets2 | 良い | - | - |
| `testFoodTypeEnumGetFileNameStalk` | food / type / enum / 取得 / file / name / stalk | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / stalk | 良い | - | - |
| `testFoodTypeEnumGetFileNameVomit` | food / type / enum / 取得 / file / name / vomit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / vomit | 良い | - | - |
| `testFoodTypeEnumGetFileNameShit` | food / type / enum / 取得 / file / name / shit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / shit | 良い | - | - |
| `testFoodTypeEnumGetFileNameBody` | food / type / enum / 取得 / file / name / 本体 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / file / name / 本体 | 良い | - | - |
| `testFoodTypeEnumGetEmptyImgFood` | food / type / enum / 取得 / empty / img / food | 設置物/アイテム動作回帰 / food / type / enum / 取得 / empty / img / food | 良い | - | - |
| `testFoodTypeEnumGetEmptyImgSweets1` | food / type / enum / 取得 / empty / img / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 取得 / empty / img / sweets1 | 良い | - | - |
| `testFoodTypeEnumGetEmptyImgStalk` | food / type / enum / 取得 / empty / img / stalk | 設置物/アイテム動作回帰 / food / type / enum / 取得 / empty / img / stalk | 良い | - | - |
| `testFoodTypeEnumGetEmptyImgVomit` | food / type / enum / 取得 / empty / img / vomit | 設置物/アイテム動作回帰 / food / type / enum / 取得 / empty / img / vomit | 良い | - | - |
| `testFoodTypeEnumGetEmptyImgWaste` | food / type / enum / 取得 / empty / img / waste | 設置物/アイテム動作回帰 / food / type / enum / 取得 / empty / img / waste | 良い | - | - |
| `testFoodTypeEnumHasShadowFood` | food / type / enum / 有無 / shadow / food | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / food | 良い | - | - |
| `testFoodTypeEnumHasShadowSweets1` | food / type / enum / 有無 / shadow / sweets1 | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / sweets1 | 良い | - | - |
| `testFoodTypeEnumHasShadowStalk` | food / type / enum / 有無 / shadow / stalk | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / stalk | 良い | - | - |
| `testFoodTypeEnumHasShadowBitter` | food / type / enum / 有無 / shadow / bitter | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / bitter | 良い | - | - |
| `testFoodTypeEnumHasShadowWaste` | food / type / enum / 有無 / shadow / waste | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / waste | 良い | - | - |
| `testFoodTypeEnumHasShadowVomit` | food / type / enum / 有無 / shadow / vomit | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / vomit | 良い | - | - |
| `testFoodTypeEnumHasShadowShit` | food / type / enum / 有無 / shadow / shit | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / shit | 良い | - | - |
| `testFoodTypeEnumHasShadowBody` | food / type / enum / 有無 / shadow / 本体 | 設置物/アイテム動作回帰 / food / type / enum / 有無 / shadow / 本体 | 良い | - | - |
| `testFoodTypeEnumFoodNoraGetLooks` | food / type / enum / food / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / food / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumWasteNoraGetLooks` | food / type / enum / waste / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / waste / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumSweetsNora1HasShadow` | food / type / enum / sweets / nora1 / 有無 / shadow | 設置物/アイテム動作回帰 / food / type / enum / sweets / nora1 / 有無 / shadow | 良い | - | - |
| `testFoodTypeEnumWasteNoraHasShadow` | food / type / enum / waste / nora / 有無 / shadow | 設置物/アイテム動作回帰 / food / type / enum / waste / nora / 有無 / shadow | 良い | - | - |
| `testFoodTypeEnumBitterNoraGetLooks` | food / type / enum / bitter / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / bitter / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumLemonpopNoraGetLooks` | food / type / enum / lemonpop / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / lemonpop / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumHotNoraGetLooks` | food / type / enum / hot / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / hot / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumViyugraNoraGetLooks` | food / type / enum / viyugra / nora / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / viyugra / nora / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumFoodYaseiHasShadow` | food / type / enum / food / yasei / 有無 / shadow | 設置物/アイテム動作回帰 / food / type / enum / food / yasei / 有無 / shadow | 良い | - | - |
| `testFoodTypeEnumWasteYaseiHasShadow` | food / type / enum / waste / yasei / 有無 / shadow | 設置物/アイテム動作回帰 / food / type / enum / waste / yasei / 有無 / shadow | 良い | - | - |
| `testFoodTypeEnumBitterYaseiGetLooks` | food / type / enum / bitter / yasei / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / bitter / yasei / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumLemonpopYaseiGetLooks` | food / type / enum / lemonpop / yasei / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / lemonpop / yasei / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumHotYaseiGetLooks` | food / type / enum / hot / yasei / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / hot / yasei / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumViyugraYaseiGetLooks` | food / type / enum / viyugra / yasei / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / viyugra / yasei / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumWasteYaseiGetLooks` | food / type / enum / waste / yasei / 取得 / looks | 設置物/アイテム動作回帰 / food / type / enum / waste / yasei / 取得 / looks | 良い | - | - |
| `testFoodTypeEnumSweetsYasei1HasShadow` | food / type / enum / sweets / yasei1 / 有無 / shadow | 設置物/アイテム動作回帰 / food / type / enum / sweets / yasei1 / 有無 / shadow | 良い | - | - |
| `testEmptyImageDishFileName` | empty / image / dish / file / name | 設置物/アイテム動作回帰 / empty / image / dish / file / name | 良い | - | - |
| `testEmptyImageSweetsFileName` | empty / image / sweets / file / name | 設置物/アイテム動作回帰 / empty / image / sweets / file / name | 良い | - | - |
| `testEmptyImageWasteFileName` | empty / image / waste / file / name | 設置物/アイテム動作回帰 / empty / image / waste / file / name | 良い | - | - |
| `testEmptyImageStalkFileNameIsNull` | empty / image / stalk / file / name / 状態 / null | 設置物/アイテム動作回帰 / empty / image / stalk / file / name / 状態 / null | 良い | - | - |
| `testEmptyImageFuelFileNameIsNull` | empty / image / fuel / file / name / 状態 / null | 設置物/アイテム動作回帰 / empty / image / fuel / file / name / 状態 / null | 良い | - | - |
| `testEmptyImageDishNoraFileName` | empty / image / dish / nora / file / name | 設置物/アイテム動作回帰 / empty / image / dish / nora / file / name | 良い | - | - |
| `testEmptyImageSweetsNoraFileName` | empty / image / sweets / nora / file / name | 設置物/アイテム動作回帰 / empty / image / sweets / nora / file / name | 良い | - | - |
| `testEmptyImageWasteNoraFileName` | empty / image / waste / nora / file / name | 設置物/アイテム動作回帰 / empty / image / waste / nora / file / name | 良い | - | - |
| `testEmptyImageDishYaseiFileName` | empty / image / dish / yasei / file / name | 設置物/アイテム動作回帰 / empty / image / dish / yasei / file / name | 良い | - | - |
| `testEmptyImageSweetsYaseiFileName` | empty / image / sweets / yasei / file / name | 設置物/アイテム動作回帰 / empty / image / sweets / yasei / file / name | 良い | - | - |
| `testEmptyImageWasteYaseiFileName` | empty / image / waste / yasei / file / name | 設置物/アイテム動作回帰 / empty / image / waste / yasei / file / name | 良い | - | - |
| `testEmptyImageValuesCount` | empty / image / values / count | 設置物/アイテム動作回帰 / empty / image / values / count | 良い | - | - |
| `testGetFoodTypeDefault` | 取得 / food / type / default | 設置物/アイテム動作回帰 / 取得 / food / type / default | 良い | - | - |
| `testSetFoodTypeAllTypes` | 設定 / food / type / all / types | 設置物/アイテム動作回帰 / 設定 / food / type / all / types | 良い | - | - |
| `testGetAmountDefaultFood` | 取得 / amount / default / food | 設置物/アイテム動作回帰 / 取得 / amount / default / food | 良い | - | - |
| `testSetAmountPositive` | 設定 / amount / positive | 設置物/アイテム動作回帰 / 設定 / amount / positive | 良い | - | - |
| `testSetAmountZero` | 設定 / amount / zero | 設置物/アイテム動作回帰 / 設定 / amount / zero | 良い | - | - |
| `testSetAmountNegative` | 設定 / amount / negative | 設置物/アイテム動作回帰 / 設定 / amount / negative | 良い | - | - |
| `testGetValueMatchesFoodType` | 取得 / value / matches / food / type | 設置物/アイテム動作回帰 / 取得 / value / matches / food / type | 良い | - | - |
| `testGetValueSweets1` | 取得 / value / sweets1 | 設置物/アイテム動作回帰 / 取得 / value / sweets1 | 良い | - | - |
| `testGetValueSweets2` | 取得 / value / sweets2 | 設置物/アイテム動作回帰 / 取得 / value / sweets2 | 良い | - | - |
| `testGetLooksMatchesFoodType` | 取得 / looks / matches / food / type | 設置物/アイテム動作回帰 / 取得 / looks / matches / food / type | 良い | - | - |
| `testGetLooksSweets1` | 取得 / looks / sweets1 | 設置物/アイテム動作回帰 / 取得 / looks / sweets1 | 良い | - | - |
| `testGetLooksWaste` | 取得 / looks / waste | 設置物/アイテム動作回帰 / 取得 / looks / waste | 良い | - | - |
| `testIsEmptyWhenAmountIsZero` | 状態 / empty / when / amount / 状態 / zero | 設置物/アイテム動作回帰 / 状態 / empty / when / amount / 状態 / zero | 良い | - | - |
| `testIsEmptyWhenAmountIsPositive` | 状態 / empty / when / amount / 状態 / positive | 設置物/アイテム動作回帰 / 状態 / empty / when / amount / 状態 / positive | 良い | - | - |
| `testIsEmptyWhenNewlyCreatedNonZeroAmount` | 状態 / empty / when / newly / created / non / zero / amount | 設置物/アイテム動作回帰 / 状態 / empty / when / newly / created / non / zero / amount | 良い | - | - |
| `testEatFoodReducesAmount` | eat / food / reduces / amount | 設置物/アイテム動作回帰 / eat / food / reduces / amount | 良い | - | - |
| `testEatFoodDoesNothingWhenAlreadyEmpty` | eat / food / does / nothing / when / already / empty | 設置物/アイテム動作回帰 / eat / food / does / nothing / when / already / empty | 良い | - | - |
| `testEatFoodClampedToZeroWhenOvereaten` | eat / food / clamped / to / zero / when / overeaten | 設置物/アイテム動作回帰 / eat / food / clamped / to / zero / when / overeaten | 良い | - | - |
| `testEatFoodExactlyDepletes` | eat / food / exactly / depletes | 設置物/アイテム動作回帰 / eat / food / exactly / depletes | 良い | - | - |
| `testEatFoodMultipleEats` | eat / food / multiple / eats | 設置物/アイテム動作回帰 / eat / food / multiple / eats | 良い | - | - |
| `testEatFoodZeroEatNoChange` | eat / food / zero / eat / なし / change | 設置物/アイテム動作回帰 / eat / food / zero / eat / なし / change | 良い | - | - |
| `testKickDoesNotThrow` | kick / does / 非 / 例外 | 設置物/アイテム動作回帰 / kick / does / 非 / 例外 | 良い | - | - |
| `testGetShadowImageDoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testRemoveListDataRemovesFoodFromWorldMap` | 除去 / list / data / removes / food / from / world / map | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListDataCalledTwiceDoesNotThrow` | 除去 / list / data / called / twice / does / 非 / 例外 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetBoundingDoesNotThrow` | 取得 / bounding / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / bounding / does / 非 / 例外 | 良い | - | - |
| `testGetFoodBoundingDoesNotThrow` | 取得 / food / bounding / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / food / bounding / does / 非 / 例外 | 良い | - | - |
| `testGetFoodBoundingAllTypesNoThrow` | 取得 / food / bounding / all / types / なし / 例外 | 設置物/アイテム動作回帰 / 取得 / food / bounding / all / types / なし / 例外 | 良い | - | - |
| `testGetEnabledDefaultTrue` | 取得 / enabled / default / true | 設置物/アイテム動作回帰 / 取得 / enabled / default / true | 良い | - | - |
| `testSetEnabledFalse` | 設定 / enabled / false | 設置物/アイテム動作回帰 / 設定 / enabled / false | 良い | - | - |
| `testSetEnabledTrue` | 設定 / enabled / true | 設置物/アイテム動作回帰 / 設定 / enabled / true | 良い | - | - |
| `testIsRemovedDefaultFalse` | 状態 / removed / default / false | 設置物/アイテム動作回帰 / 状態 / removed / default / false | 良い | - | - |
| `testSetRemovedTrue` | 設定 / removed / true | 設置物/アイテム動作回帰 / 設定 / removed / true | 良い | - | - |
| `testSetRemovedFalseAgain` | 設定 / removed / false / again | 設置物/アイテム動作回帰 / 設定 / removed / false / again | 良い | - | - |
| `testObjHitProcessReturnsZero` | obj / hit / process / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / 戻り / zero | 良い | - | - |
| `testGetObjIdNonZero` | 取得 / obj / id / non / zero | 設置物/アイテム動作回帰 / 取得 / obj / id / non / zero | 良い | - | - |
| `testGetObjIdUniquePerInstance` | 取得 / obj / id / unique / per / instance | 設置物/アイテム動作回帰 / 取得 / obj / id / unique / per / instance | 良い | - | - |
| `testGetObjExtypeIsFood` | 取得 / obj / extype / 状態 / food | 設置物/アイテム動作回帰 / 取得 / obj / extype / 状態 / food | 良い | - | - |
| `testVerifyCommonProperties` | verify / common / properties | 設置物/アイテム動作回帰 / verify / common / properties | 良い | - | - |
| `testMultipleFoodsAllRegisteredInWorldMap` | multiple / foods / all / registered / in / world / map | 設置物/アイテム動作回帰 / multiple / foods / all / registered / in / world / map | 良い | - | - |
| `testFoodTypeEnumValuesNonEmpty` | food / type / enum / values / non / empty | 設置物/アイテム動作回帰 / food / type / enum / values / non / empty | 良い | - | - |
| `testEatFoodBecomeEmptyThenDoNothing` | eat / food / become / empty / then / do / nothing | 設置物/アイテム動作回帰 / eat / food / become / empty / then / do / nothing | 良い | - | - |
| `testGetImageLayerNotEmptyDoesNotThrow` | 取得 / image / layer / 非 / empty / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / 非 / empty / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerEmptyDoesNotThrow` | 取得 / image / layer / empty / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / empty / does / 非 / 例外 | 良い | - | - |

### `GarbageChuteTest`
- 状態: 完了 (24/24 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testConstructorHouseRank` | constructor / house / rank | 設置物/アイテム動作回帰 / constructor / house / rank | 良い | - | - |
| `testConstructorNoraRank` | constructor / nora / rank | 設置物/アイテム動作回帰 / constructor / nora / rank | 良い | - | - |
| `testObjExtypeGarbagechute` | obj / extype / garbagechute | 設置物/アイテム動作回帰 / obj / extype / garbagechute | 良い | - | - |
| `testGetShadowImageReturnsNull` | 取得 / shadow / image / 戻り / null | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null | 良い | - | - |
| `testGetBoundingNotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetSetBindObjList` | 取得 / 設定 / bind / obj / list | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj / list | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testGetSetBindBody` | 取得 / 設定 / bind / 本体 | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / 本体 | 良い | - | - |
| `testUpDateEmptyListDoesNothing` | up / date / empty / list / does / nothing | 設置物/アイテム動作回帰 / up / date / empty / list / does / nothing | 良い | - | - |
| `testUpDateNullListDoesNothing` | up / date / null / list / does / nothing | 設置物/アイテム動作回帰 / up / date / null / list / does / nothing | 良い | - | - |
| `testUpDateRemovedObjClearsFromList` | up / date / removed / obj / clears / from / list | 設置物/アイテム動作回帰 / up / date / removed / obj / clears / from / list | 良い | - | - |
| `testObjHitProcessNullReturnsZero` | obj / hit / process / null / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / null / 戻り / zero | 良い | - | - |
| `testObjHitProcessDiffuserReturnsZero` | obj / hit / process / diffuser / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / diffuser / 戻り / zero | 良い | - | - |
| `testObjHitProcessDuplicateReturnsZero` | obj / hit / process / duplicate / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / duplicate / 戻り / zero | 良い | - | - |
| `testObjHitProcessFoodRemovesFood` | obj / hit / process / food / removes / food | 設置物/アイテム動作回帰 / obj / hit / process / food / removes / food | 良い | - | - |
| `testSetEnabled` | 設定 / enabled | 設置物/アイテム動作回帰 / 設定 / enabled | 良い | - | - |
| `testGetImageLayerHouseRankEnabledDoesNotThrow` | 取得 / image / layer / house / rank / enabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / house / rank / enabled / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerHouseRankDisabledDoesNotThrow` | 取得 / image / layer / house / rank / disabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / house / rank / disabled / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerNoraRankEnabledDoesNotThrow` | 取得 / image / layer / nora / rank / enabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / nora / rank / enabled / does / 非 / 例外 | 良い | - | - |
| `testScenarioLiveBodyStartsFallingAndCostsCash` | シナリオ / live / 本体 / starts / falling / and / costs / cash | 設置物/アイテム動作回帰 / シナリオ / live / 本体 / starts / falling / and / costs / cash | 良い | - | - |
| `testScenarioDeepFallingBodyIsRemovedFromChuteOnUpdate` | シナリオ / deep / falling / 本体 / 状態 / removed / from / chute / on / 更新 | 設置物/アイテム動作回帰 / シナリオ / deep / falling / 本体 / 状態 / removed / from / chute / on / 更新 | 良い | - | - |

### `GarbageStationTest`
- 状態: 完了 (35/35 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGomiTypeValuesCount` | gomi / type / values / count | 設置物/アイテム動作回帰 / gomi / type / values / count | 良い | - | - |
| `testGomiTypeEnumWaste` | gomi / type / enum / waste | 設置物/アイテム動作回帰 / gomi / type / enum / waste | 良い | - | - |
| `testGomiTypeEnumBitter` | gomi / type / enum / bitter | 設置物/アイテム動作回帰 / gomi / type / enum / bitter | 良い | - | - |
| `testGomiTypeEnumHot` | gomi / type / enum / hot | 設置物/アイテム動作回帰 / gomi / type / enum / hot | 良い | - | - |
| `testGomiTypeEnumLemonPop` | gomi / type / enum / lemon / pop | 設置物/アイテム動作回帰 / gomi / type / enum / lemon / pop | 良い | - | - |
| `testGomiTypeEnumViyugra` | gomi / type / enum / viyugra | 設置物/アイテム動作回帰 / gomi / type / enum / viyugra | 良い | - | - |
| `testGomiTypeEnumNormal` | gomi / type / enum / normal | 設置物/アイテム動作回帰 / gomi / type / enum / normal | 良い | - | - |
| `testGomiTypeEnumSweets1` | gomi / type / enum / sweets1 | 設置物/アイテム動作回帰 / gomi / type / enum / sweets1 | 良い | - | - |
| `testGomiTypeEnumSweets2` | gomi / type / enum / sweets2 | 設置物/アイテム動作回帰 / gomi / type / enum / sweets2 | 良い | - | - |
| `testGomiTypeEnumToStringEqualsName` | gomi / type / enum / to / string / equals / name | 設置物/アイテム動作回帰 / gomi / type / enum / to / string / equals / name | 良い | - | - |
| `testGomiTypeEnumValueOf` | gomi / type / enum / value / of | 設置物/アイテム動作回帰 / gomi / type / enum / value / of | 良い | - | - |
| `testGetShadowImageReturnsNull` | 取得 / shadow / image / 戻り / null | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null | 良い | - | - |
| `testGetBoundingNotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetSetEnable` | 取得 / 設定 / enable | 設置物/アイテム動作回帰 / 取得 / 設定 / enable | 良い | - | - |
| `testGetSetFood` | 取得 / 設定 / food | 設置物/アイテム動作回帰 / 取得 / 設定 / food | 良い | - | - |
| `testGetSetThrowingTime` | 取得 / 設定 / throwing / time | 設置物/アイテム動作回帰 / 取得 / 設定 / throwing / time | 良い | - | - |
| `testGetSetGettingP` | 取得 / 設定 / getting / p | 設置物/アイテム動作回帰 / 取得 / 設定 / getting / p | 良い | - | - |
| `testUpDateDisabledDoesNothing` | up / date / disabled / does / nothing | 設置物/アイテム動作回帰 / up / date / disabled / does / nothing | 良い | - | - |
| `testUpDateEnabledNoTimingDoesNotThrow` | up / date / enabled / なし / timing / does / 非 / 例外 | 設置物/アイテム動作回帰 / up / date / enabled / なし / timing / does / 非 / 例外 | 良い | - | - |
| `testUpDateEnabledWithArraysDoesNotThrow` | up / date / enabled / with / arrays / does / 非 / 例外 | 設置物/アイテム動作回帰 / up / date / enabled / with / arrays / does / 非 / 例外 | 良い | - | - |
| `testReadIniFileDoesNotThrow` | read / ini / file / does / 非 / 例外 | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testObjExtypeAfterManualSet` | obj / extype / after / manual / 設定 | 設置物/アイテム動作回帰 / obj / extype / after / manual / 設定 | 良い | - | - |
| `testSetEnabled` | 設定 / enabled | 設置物/アイテム動作回帰 / 設定 / enabled | 良い | - | - |
| `testHitCheckObjTypeConstant` | hit / 判定 / obj / type / constant | 設置物/アイテム動作回帰 / hit / 判定 / obj / type / constant | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testSetupGarbageStHeadlessExecutesCode` | setup / garbage / st / headless / executes / code | 設置物/アイテム動作回帰 / setup / garbage / st / headless / executes / code | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | 設置物/アイテム動作回帰 / constructor / with / args / headless / executes / code | 良い | - | - |
| `testFeedActionViaUpDateFoodNullCreatesFood` | feed / action / via / up / date / food / null / creates / food | 設置物/アイテム動作回帰 / feed / action / via / up / date / food / null / creates / food | 良い | - | - |
| `testFeedActionViaUpDateFoodNotNullRemoved` | feed / action / via / up / date / food / 非 / null / removed | 設置物/アイテム動作回帰 / feed / action / via / up / date / food / 非 / null / removed | 良い | - | - |
| `testUpDateDisabledEarlyReturn` | up / date / disabled / early / 戻り | 設置物/アイテム動作回帰 / up / date / disabled / early / 戻り | 良い | - | - |
| `testScenarioUpdateAtThrowingTimeCreatesTwoWasteFoods` | シナリオ / 更新 / at / throwing / time / creates / two / waste / foods | 設置物/アイテム動作回帰 / シナリオ / 更新 / at / throwing / time / creates / two / waste / foods | 良い | - | - |
| `testScenarioEmptyFoodSlotIsRemovedAndReplacedOnUpdate` | シナリオ / empty / food / slot / 状態 / removed / and / replaced / on / 更新 | 設置物/アイテム動作回帰 / シナリオ / empty / food / slot / 状態 / removed / and / replaced / on / 更新 | 良い | - | - |

### `GeneratorTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | type/entityKind/interval/value/cost の初期値完全確認 | 設置物/アイテム動作回帰 / 初期値完全回帰 | 良い | - | - |
| `testGetImageLayerEnabledDoesNotThrow` | enabled 状態で getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / imageLayer 返り値回帰 | 良い | - | - |
| `testGetImageLayerDisabledDoesNotThrow` | disabled 状態でも getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / disabled imageLayer 回帰 | 良い | - | - |
| `testGetShadowImageReturnsNull` | Generator は影なし仕様で getShadowImage が null を返すこと | 設置物/アイテム動作回帰 / 影なし仕様回帰 | 良い | - | - |
| `testRemoveListDataDoesNotThrow` | removeFromWorld 後も isRemoved=false のまま（未登録状態）であること | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |

### `HotPlateTest`
- 状態: 完了 (21/21 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testEnableHitCheckNoBindBody` | enable / hit / 判定 / なし / bind / 本体 | 設置物/アイテム動作回帰 / enable / hit / 判定 / なし / bind / 本体 | 良い | - | - |
| `testEnableHitCheckWithBindBody` | enable / hit / 判定 / with / bind / 本体 | 設置物/アイテム動作回帰 / enable / hit / 判定 / with / bind / 本体 | 良い | - | - |
| `testGetSetBindBody` | 取得 / 設定 / bind / 本体 | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / 本体 | 良い | - | - |
| `testGetSetSmoke` | 取得 / 設定 / smoke | 設置物/アイテム動作回帰 / 取得 / 設定 / smoke | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testRemoveListDataWithoutBindBody` | 除去 / list / data / without / bind / 本体 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListDataWithBindBody` | 除去 / list / data / with / bind / 本体 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDateNoBindBody` | up / date / なし / bind / 本体 | 設置物/アイテム動作回帰 / up / date / なし / bind / 本体 | 良い | - | - |
| `testUpDateWithBindBodyGrabbedTrue` | up / date / with / bind / 本体 / grabbed / true | 設置物/アイテム動作回帰 / up / date / with / bind / 本体 / grabbed / true | 良い | - | - |
| `testUpDateWithBindBodyBodyMovedAway` | up / date / with / bind / 本体 / 本体 / moved / away | 設置物/アイテム動作回帰 / up / date / with / bind / 本体 / 本体 / moved / away | 良い | - | - |
| `testConstructorWithCoordsDoesNotThrow` | constructor / with / coords / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerEnabledNoBindBodyDoesNotThrow` | 取得 / image / layer / enabled / なし / bind / 本体 / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / なし / bind / 本体 / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerEnabledWithBindBodyDoesNotThrow` | 取得 / image / layer / enabled / with / bind / 本体 / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / with / bind / 本体 / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerDisabledDoesNotThrow` | 取得 / image / layer / disabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled / does / 非 / 例外 | 良い | - | - |
| `testObjHitProcessExecutesCode` | obj / hit / process / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / executes / code | 良い | - | - |
| `testScenarioBoundBodyOnPlateAccumulatesDamageStressAndPainState` | シナリオ / bound / 本体 / on / plate / accumulates / ダメージ / ストレス / and / pain / state | 設置物/アイテム動作回帰 / シナリオ / bound / 本体 / on / plate / accumulates / ダメージ / ストレス / and / pain / state | 良い | - | - |
| `testScenarioCriticalBurnedBodyBecomesPullableWhileStillBound` | シナリオ / critical / burned / 本体 / becomes / pullable / while / still / bound | 設置物/アイテム動作回帰 / シナリオ / critical / burned / 本体 / becomes / pullable / while / still / bound | 良い | - | - |
| `testScenarioRemovingBoundBodyFromPlateRestoresMobilityAndShadow` | シナリオ / removing / bound / 本体 / from / plate / restores / mobility / and / shadow | 設置物/アイテム動作回帰 / シナリオ / removing / bound / 本体 / from / plate / restores / mobility / and / shadow | 良い | - | - |

### `HouseTest`
- 状態: 完了 (13/13 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | デフォルトコンストラクタ後に houses に登録されること | 設置物/アイテム動作回帰 / constructor 登録回帰 | 良い | - | - |
| `testHouseTableEnum` | 2種類の HouseTable が全フィールド非null・非空・rank>0 でさらに ordinal 順が正しいこと | 設置物/アイテム動作回帰 / HouseTable enum 回帰 | 良い | - | - |
| `testGetSetHouseType` | setHouseType で変更が有効になること（NORA1→NORA2 の変更確認） | 設置物/アイテム動作回帰 / houseType 変更回帰 | 良い | - | - |
| `testGetSetItemRank` | setItemRank で変更が有効になること（NORA→HOUSE の変更確認） | 設置物/アイテム動作回帰 / itemRank 変更回帰 | 良い | - | - |
| `testGetShadowImage` | House は影なし仕様で getShadowImage が null を返すこと | 設置物/アイテム動作回帰 / 影なし仕様回帰 | 良い | - | - |
| `testGetValue` | デフォルトコンストラクタでは value=0 であること | 設置物/アイテム動作回帰 / value 初期値回帰 | 良い | - | - |
| `testRemoveListData` | removeFromWorld 前後で houses の存在が変化すること | 設置物/アイテム動作回帰 / 除去前後対比回帰 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | loadImages が NPE か正常終了のどちらかで完了すること | 設置物/アイテム動作回帰 / loadImages 耐性回帰 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | getImageLayer が 0 以上の値を返すこと | 設置物/アイテム動作回帰 / imageLayer 返り値回帰 | 良い | - | - |
| `testGetBoundingDoesNotThrow` | getBounding が非null を返すこと | 設置物/アイテム動作回帰 / bounding 非null 回帰 | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | 引数付きコンストラクタで PLATFORM/HOUSE 型が設定されること | 設置物/アイテム動作回帰 / constructor 型設定回帰 | 良い | - | - |
| `testScenarioConstructorWithArgsRegistersHouseTypeBoundaryAndCollision` | シナリオ / constructor / with / args / registers / house / type / boundary / and / 衝突 | 設置物/アイテム動作回帰 / シナリオ / constructor / with / args / registers / house / type / boundary / and / 衝突 | 良い | - | - |
| `testScenarioGetImageLayerUsesConfiguredFirstHouseFloorImage` | シナリオ / 取得 / image / layer / uses / configured / first / house / floor / image | 設置物/アイテム動作回帰 / シナリオ / 取得 / image / layer / uses / configured / first / house / floor / image | 良い | - | - |

### `MachinePressTest`
- 状態: 完了 (13/13 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | デフォルトコンストラクタ後に machinePresses に登録されること | 設置物/アイテム動作回帰 / constructor 登録回帰 | 良い | - | - |
| `testConstructorWithCoords` | constructor / with / coords | 設置物/アイテム動作回帰 / constructor / with / coords | 良い | - | - |
| `testGetHitCheckObjType` | hitCheckObjType が YUKKURI|SHIT|VOMIT で各ビットが独立して正しいこと | 設置物/アイテム動作回帰 / hitCheck ビット回帰 | 良い | - | - |
| `testGetShadowImageIsNull` | 影なし仕様で getShadowImage が null を返すこと | 設置物/アイテム動作回帰 / 影なし仕様回帰 | 良い | - | - |
| `testRemoveListData` | removeFromWorld 前後で machinePresses の存在が変化すること | 設置物/アイテム動作回帰 / 除去前後対比回帰 | 良い | - | - |
| `testUpDateNoEffect` | age=0(2400倍数)+enabled で cost が引かれること | 設置物/アイテム動作回帰 / upDate cost 回帰 | 良い | - | - |
| `testUpDateDisabled` | disabled 時は upDate で cash が変化しないこと | 設置物/アイテム動作回帰 / disabled 無効回帰 | 良い | - | - |
| `testObjHitProcessWithNonYukkuri` | 非YUKKURI オブジェクトは 0 返りで除去されないこと | 設置物/アイテム動作回帰 / 非YUKKURI スキップ回帰 | 良い | - | - |
| `testGetImageLayerEnabledDoesNotThrow` | enabled 状態で getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / imageLayer 返り値回帰 | 良い | - | - |
| `testGetImageLayerDisabledDoesNotThrow` | disabled 状態でも getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / disabled imageLayer 回帰 | 良い | - | - |
| `testGetBoundingDoesNotThrow` | getBounding が非null を返すこと | 設置物/アイテム動作回帰 / bounding 非null 回帰 | 良い | - | - |
| `testScenarioPressCycleSilencesAndDamagesHealthyBody` | シナリオ / press / cycle / silences / and / damages / healthy / 本体 | 設置物/アイテム動作回帰 / シナリオ / press / cycle / silences / and / damages / healthy / 本体 | 良い | - | - |
| `testScenarioUpdateAtBillingTickConsumesCashWhenEnabled` | シナリオ / 更新 / at / billing / tick / consumes / cash / when / enabled | 設置物/アイテム動作回帰 / シナリオ / 更新 / at / billing / tick / consumes / cash / when / enabled | 良い | - | - |

### `MixerTest`
- 状態: 完了 (25/25 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testEnableHitCheckNoBind` | enable / hit / 判定 / なし / bind | 設置物/アイテム動作回帰 / enable / hit / 判定 / なし / bind | 良い | - | - |
| `testGetSetBind` | 取得 / 設定 / bind | 設置物/アイテム動作回帰 / 取得 / 設定 / bind | 良い | - | - |
| `testGetSetMix` | 取得 / 設定 / mix | 設置物/アイテム動作回帰 / 取得 / 設定 / mix | 良い | - | - |
| `testGetSetCounter` | 取得 / 設定 / counter | 設置物/アイテム動作回帰 / 取得 / 設定 / counter | 良い | - | - |
| `testGetSetAmount` | 取得 / 設定 / amount | 設置物/アイテム動作回帰 / 取得 / 設定 / amount | 良い | - | - |
| `testGetSetSweet` | 取得 / 設定 / sweet | 設置物/アイテム動作回帰 / 取得 / 設定 / sweet | 良い | - | - |
| `testGetSetSick` | 取得 / 設定 / 病気 | 設置物/アイテム動作回帰 / 取得 / 設定 / 病気 | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testRemoveListDataNoBind` | 除去 / list / data / なし / bind | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListDataWithBind` | 除去 / list / data / with / bind | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcessDisabled` | obj / hit / process / disabled | 設置物/アイテム動作回帰 / obj / hit / process / disabled | 良い | - | - |
| `testObjHitProcessEnabled` | obj / hit / process / enabled | 設置物/アイテム動作回帰 / obj / hit / process / enabled | 良い | - | - |
| `testUpDateNoBindNoMix` | up / date / なし / bind / なし / mix | 設置物/アイテム動作回帰 / up / date / なし / bind / なし / mix | 良い | - | - |
| `testUpDateDisabledWithBind` | up / date / disabled / with / bind | 設置物/アイテム動作回帰 / up / date / disabled / with / bind | 良い | - | - |
| `testUpDateGrabbedWithBind` | up / date / grabbed / with / bind | 設置物/アイテム動作回帰 / up / date / grabbed / with / bind | 良い | - | - |
| `testUpDateBodyMovedAway` | up / date / 本体 / moved / away | 設置物/アイテム動作回帰 / up / date / 本体 / moved / away | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testConstructorWithArgsDoesNotThrow` | constructor / with / args / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / args / does / 非 / 例外 | 良い | - | - |
| `testScenarioUpdateAfterStartDamagesBoundBodyAndAccumulatesMaterial` | シナリオ / 更新 / after / start / damages / bound / 本体 / and / accumulates / material | 設置物/アイテム動作回帰 / シナリオ / 更新 / after / start / damages / bound / 本体 / and / accumulates / material | 良い | - | - |
| `testScenarioMovedAwayBodyAfterGrindingIsReleasedWithCutDamage` | シナリオ / moved / away / 本体 / after / grinding / 状態 / released / with / cut / ダメージ | 設置物/アイテム動作回帰 / シナリオ / moved / away / 本体 / after / grinding / 状態 / released / with / cut / ダメージ | 良い | - | - |
| `testScenarioRemoveListDataAlsoRemovesActiveMixEffect` | シナリオ / 除去 / list / data / also / removes / active / mix / effect | 設置物/アイテム動作回帰 / シナリオ / 除去 / list / data / also / removes / active / mix / effect | 良い | - | - |

### `OrangePoolTest`
- 状態: 完了 (19/19 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testOrangeTypeEnum` | orange / type / enum | 設置物/アイテム動作回帰 / orange / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testIsRescueDefault` | 状態 / rescue / default | 設置物/アイテム動作回帰 / 状態 / rescue / default | 良い | - | - |
| `testSetRescue` | 設定 / rescue | 設置物/アイテム動作回帰 / 設定 / rescue | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcessDisabled` | obj / hit / process / disabled | 設置物/アイテム動作回帰 / obj / hit / process / disabled | 良い | - | - |
| `testGetValueHouseNoRescue` | 取得 / value / house / なし / rescue | 設置物/アイテム動作回帰 / 取得 / value / house / なし / rescue | 良い | - | - |
| `testGetValueNonHouse` | 取得 / value / non / house | 設置物/アイテム動作回帰 / 取得 / value / non / house | 良い | - | - |
| `testGetCostHouse` | 取得 / cost / house | 設置物/アイテム動作回帰 / 取得 / cost / house | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testSetupOrangeHeadlessExecutesCode` | setup / orange / headless / executes / code | 設置物/アイテム動作回帰 / setup / orange / headless / executes / code | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | 設置物/アイテム動作回帰 / constructor / with / args / headless / executes / code | 良い | - | - |
| `testScenarioNormalPoolCleansDirtyBodyAndChargesCost` | シナリオ / normal / pool / cleans / dirty / 本体 / and / charges / cost | 設置物/アイテム動作回帰 / シナリオ / normal / pool / cleans / dirty / 本体 / and / charges / cost | 良い | - | - |
| `testScenarioRescuePoolRevivesDeadBodyAndResetsFootBake` | シナリオ / rescue / pool / revives / 死亡 / 本体 / and / resets / foot / bake | 設置物/アイテム動作回帰 / シナリオ / rescue / pool / revives / 死亡 / 本体 / and / resets / foot / bake | 良い | - | - |

### `PoolTest`
- 状態: 完了 (46/46 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - メニュー構成と選択状態が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testDepthEnumCount` | depth / enum / count | 設置物/アイテム動作回帰 / depth / enum / count | 良い | - | - |
| `testDepthEnumValues` | depth / enum / values | 設置物/アイテム動作回帰 / depth / enum / values | 良い | - | - |
| `testGetAttribute` | 取得 / attribute | 設置物/アイテム動作回帰 / 取得 / attribute | 良い | - | - |
| `testGetMinimumSize` | 取得 / minimum / size | 設置物/アイテム動作回帰 / 取得 / minimum / size | 良い | - | - |
| `testHasShapePopup` | 有無 / shape / popup | 設置物/アイテム動作回帰 / 有無 / shape / popup | 良い | - | - |
| `testRemoveAndIsRemoved` | 除去 / and / 状態 / removed | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetSetAge` | 取得 / 設定 / age | 設置物/アイテム動作回帰 / 取得 / 設定 / age | 良い | - | - |
| `testClockTickNotRemoved` | clock / tick / 非 / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickRemoved` | clock / tick / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testMapContainsInside` | map / contains / inside | 設置物/アイテム動作回帰 / map / contains / inside | 良い | - | - |
| `testMapContainsOutside` | map / contains / outside | 設置物/アイテム動作回帰 / map / contains / outside | 良い | - | - |
| `testFieldContainsInside` | field / contains / inside | 設置物/アイテム動作回帰 / field / contains / inside | 良い | - | - |
| `testFieldContainsOutside` | field / contains / outside | 設置物/アイテム動作回帰 / field / contains / outside | 良い | - | - |
| `testGetSetMapWh` | 取得 / 設定 / map / wh | 設置物/アイテム動作回帰 / 取得 / 設定 / map / wh | 良い | - | - |
| `testGetSetFieldWh` | 取得 / 設定 / field / wh | 設置物/アイテム動作回帰 / 取得 / 設定 / field / wh | 良い | - | - |
| `testGetSetMapPosGetters` | 取得 / 設定 / map / pos / getters | 設置物/アイテム動作回帰 / 取得 / 設定 / map / pos / getters | 良い | - | - |
| `testGetSetFieldPosGetters` | 取得 / 設定 / field / pos / getters | 設置物/アイテム動作回帰 / 取得 / 設定 / field / pos / getters | 良い | - | - |
| `testGetSetBindObjList` | 取得 / 設定 / bind / obj / list | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj / list | 良い | - | - |
| `testGetSetAnWaterPointX` | 取得 / 設定 / an / 水 / point / x | 設置物/アイテム動作回帰 / 取得 / 設定 / an / 水 / point / x | 良い | - | - |
| `testGetSetAnWaterPointY` | 取得 / 設定 / an / 水 / point / y | 設置物/アイテム動作回帰 / 取得 / 設定 / an / 水 / point / y | 良い | - | - |
| `testCheckHitObjNullReturnsFalse` | 判定 / hit / obj / null / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / null / 戻り / false | 良い | - | - |
| `testCheckHitObjNullReturnsFalse2` | 判定 / hit / obj / null / 戻り / false2 | 設置物/アイテム動作回帰 / 判定 / hit / obj / null / 戻り / false2 | 良い | - | - |
| `testCheckAreaOutsidePoolReturnsNone` | 判定 / area / outside / pool / 戻り / none | 設置物/アイテム動作回帰 / 判定 / area / outside / pool / 戻り / none | 良い | - | - |
| `testCheckAreaEdgeXreturnsEdge` | 判定 / area / edge / xreturns / edge | 設置物/アイテム動作回帰 / 判定 / area / edge / xreturns / edge | 良い | - | - |
| `testCheckAreaShallowXreturnsShallow` | 判定 / area / shallow / xreturns / shallow | 設置物/アイテム動作回帰 / 判定 / area / shallow / xreturns / shallow | 良い | - | - |
| `testCheckAreaDeepCenterReturnsDeep` | 判定 / area / deep / center / 戻り / deep | 設置物/アイテム動作回帰 / 判定 / area / deep / center / 戻り / deep | 良い | - | - |
| `testGetPoolEmptyListReturnsNull` | 取得 / pool / empty / list / 戻り / null | 設置物/アイテム動作回帰 / 取得 / pool / empty / list / 戻り / null | 良い | - | - |
| `testGetPoolWithPoolReturnsPool` | 取得 / pool / with / pool / 戻り / pool | 設置物/アイテム動作回帰 / 取得 / pool / with / pool / 戻り / pool | 良い | - | - |
| `testGetPoolOutsideAreaReturnsNull` | 取得 / pool / outside / area / 戻り / null | 設置物/アイテム動作回帰 / 取得 / pool / outside / area / 戻り / null | 良い | - | - |
| `testDeletePoolRemovesFromList` | delete / pool / removes / from / list | 設置物/アイテム動作回帰 / delete / pool / removes / from / list | 良い | - | - |
| `testExecuteShapePopupSetupDoesNotThrow` | execute / shape / popup / setup / does / 非 / 例外 | 設置物/アイテム動作回帰 / execute / shape / popup / setup / does / 非 / 例外 | 良い | - | - |
| `testExecuteShapePopupTopMovesToFront` | execute / shape / popup / top / moves / to / front | 設置物/アイテム動作回帰 / execute / shape / popup / top / moves / to / front | 良い | - | - |
| `testExecuteShapePopupBottomMovesToEnd` | execute / shape / popup / bottom / moves / to / end | 設置物/アイテム動作回帰 / execute / shape / popup / bottom / moves / to / end | 良い | - | - |
| `testExecuteShapePopupUpMovesUp` | execute / shape / popup / up / moves / up | 設置物/アイテム動作回帰 / execute / shape / popup / up / moves / up | 良い | - | - |
| `testExecuteShapePopupDownMovesDown` | execute / shape / popup / down / moves / down | 設置物/アイテム動作回帰 / execute / shape / popup / down / moves / down | 良い | - | - |
| `testObjHitProcessAirborneObjReturnsZero` | obj / hit / process / airborne / obj / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / airborne / obj / 戻り / zero | 良い | - | - |
| `testObjHitProcessObjOnGroundOutsidePool` | obj / hit / process / obj / on / ground / outside / pool | 設置物/アイテム動作回帰 / obj / hit / process / obj / on / ground / outside / pool | 良い | - | - |
| `testDrawPreviewDoesNotThrow` | draw / preview / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / preview / does / 非 / 例外 | 良い | - | - |
| `testDrawShapeDoesNotThrow` | draw / shape / does / 非 / 例外 | 設置物/アイテム動作回帰 / draw / shape / does / 非 / 例外 | 良い | - | - |
| `testCheckContainMapCoordInsidePool` | 判定 / contain / map / coord / inside / pool | 設置物/アイテム動作回帰 / 判定 / contain / map / coord / inside / pool | 良い | - | - |
| `testCheckContainFieldCoordOutsidePool` | 判定 / contain / field / coord / outside / pool | 設置物/アイテム動作回帰 / 判定 / contain / field / coord / outside / pool | 良い | - | - |
| `testObjHitProcessBodyInsidePoolExecutesCode` | obj / hit / process / 本体 / inside / pool / executes / code | 設置物/アイテム動作回帰 / obj / hit / process / 本体 / inside / pool / executes / code | 良い | - | - |
| `testConstructorWithCoordsExecutesCode` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testScenarioEdgeObjectIsLiftedBackToSurface` | シナリオ / edge / object / 状態 / lifted / back / to / surface | 設置物/アイテム動作回帰 / シナリオ / edge / object / 状態 / lifted / back / to / surface | 良い | - | - |
| `testScenarioShallowObjectSinksOneLevelIntoWater` | シナリオ / shallow / object / sinks / one / level / into / 水 | 設置物/アイテム動作回帰 / シナリオ / shallow / object / sinks / one / level / into / 水 | 良い | - | - |

### `ProcessorPlateTest`
- 状態: 完了 (46/46 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testProcessModeEnum` | process / mode / enum | 設置物/アイテム動作回帰 / process / mode / enum | 良い | - | - |
| `testProcessTypeEnum` | process / type / enum | 設置物/アイテム動作回帰 / process / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testEnableHitCheck` | enable / hit / 判定 | 設置物/アイテム動作回帰 / enable / hit / 判定 | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testGetSetEnumProcessType` | 取得 / 設定 / enum / process / type | 設置物/アイテム動作回帰 / 取得 / 設定 / enum / process / type | 良い | - | - |
| `testGetSetProcessedBodyList` | 取得 / 設定 / processed / 本体 / list | 設置物/アイテム動作回帰 / 取得 / 設定 / processed / 本体 / list | 良い | - | - |
| `testGetSetRunningCost` | 取得 / 設定 / running / cost | 設置物/アイテム動作回帰 / 取得 / 設定 / running / cost | 良い | - | - |
| `testGetCostPainMode` | 取得 / cost / pain / mode | 設置物/アイテム動作回帰 / 取得 / cost / pain / mode | 良い | - | - |
| `testGetCostHotplateMode` | 取得 / cost / hotplate / mode | 設置物/アイテム動作回帰 / 取得 / cost / hotplate / mode | 良い | - | - |
| `testGetCostPeelingMode` | 取得 / cost / peeling / mode | 設置物/アイテム動作回帰 / 取得 / cost / peeling / mode | 良い | - | - |
| `testGetCostBlindingMode` | 取得 / cost / blinding / mode | 設置物/アイテム動作回帰 / 取得 / cost / blinding / mode | 良い | - | - |
| `testObjHitProcessDisabled` | obj / hit / process / disabled | 設置物/アイテム動作回帰 / obj / hit / process / disabled | 良い | - | - |
| `testObjHitProcessNullObj` | obj / hit / process / null / obj | 設置物/アイテム動作回帰 / obj / hit / process / null / obj | 良い | - | - |
| `testRemoveListDataEmptyLists` | 除去 / list / data / empty / lists | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListDataWithBody` | 除去 / list / data / with / 本体 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetSetProcessedBodyEffectList` | 取得 / 設定 / processed / 本体 / effect / list | 設置物/アイテム動作回帰 / 取得 / 設定 / processed / 本体 / effect / list | 良い | - | - |
| `testUpDateDisabledEmptyLists` | up / date / disabled / empty / lists | 設置物/アイテム動作回帰 / up / date / disabled / empty / lists | 良い | - | - |
| `testUpDateDisabledWithBodyInList` | up / date / disabled / with / 本体 / in / list | 設置物/アイテム動作回帰 / up / date / disabled / with / 本体 / in / list | 良い | - | - |
| `testUpDateEnabledEmptyLists` | up / date / enabled / empty / lists | 設置物/アイテム動作回帰 / up / date / enabled / empty / lists | 良い | - | - |
| `testUpDateEnabledWithRemovedBody` | up / date / enabled / with / removed / 本体 | 設置物/アイテム動作回帰 / up / date / enabled / with / removed / 本体 | 良い | - | - |
| `testReadIniFileDoesNotThrow` | read / ini / file / does / 非 / 例外 | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testGetCostAccelerateMode` | 取得 / cost / accelerate / mode | 設置物/アイテム動作回帰 / 取得 / cost / accelerate / mode | 良い | - | - |
| `testGetCostBaibaiOkazari` | 取得 / cost / baibai / okazari | 設置物/アイテム動作回帰 / 取得 / cost / baibai / okazari | 良い | - | - |
| `testGetCostShutmouth` | 取得 / cost / shutmouth | 設置物/アイテム動作回帰 / 取得 / cost / shutmouth | 良い | - | - |
| `testGetCostPlucking` | 取得 / cost / plucking | 設置物/アイテム動作回帰 / 取得 / cost / plucking | 良い | - | - |
| `testGetCostPacking` | 取得 / cost / packing | 設置物/アイテム動作回帰 / 取得 / cost / packing | 良い | - | - |
| `testGetImageLayerEnabledReturnsOne` | 取得 / image / layer / enabled / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / enabled / 戻り / one | 良い | - | - |
| `testGetImageLayerDisabledReturnsOne` | 取得 / image / layer / disabled / 戻り / one | 設置物/アイテム動作回帰 / 取得 / image / layer / disabled / 戻り / one | 良い | - | - |
| `testCheckHitObjZnotZeroReturnsFalse` | 判定 / hit / obj / znot / zero / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / znot / zero / 戻り / false | 良い | - | - |
| `testCheckHitObjOutsideRectReturnsFalse` | 判定 / hit / obj / outside / rect / 戻り / false | 設置物/アイテム動作回帰 / 判定 / hit / obj / outside / rect / 戻り / false | 良い | - | - |
| `testSetupProcessorPlateHeadlessDoesNotThrow` | setup / processor / plate / headless / does / 非 / 例外 | 設置物/アイテム動作回帰 / setup / processor / plate / headless / does / 非 / 例外 | 良い | - | - |
| `testConstructorWithCoordsExecutesCode` | constructor / with / coords / executes / code | 設置物/アイテム動作回帰 / constructor / with / coords / executes / code | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testUpDateEnabledHotplateWithLiveBody` | up / date / enabled / hotplate / with / live / 本体 | 設置物/アイテム動作回帰 / up / date / enabled / hotplate / with / live / 本体 | 良い | - | - |
| `testUpDateEnabledPainWithLiveBody` | up / date / enabled / pain / with / live / 本体 | 設置物/アイテム動作回帰 / up / date / enabled / pain / with / live / 本体 | 良い | - | - |
| `testUpDateEnabledPealingWithLiveBody` | up / date / enabled / pealing / with / live / 本体 | 設置物/アイテム動作回帰 / up / date / enabled / pealing / with / live / 本体 | 良い | - | - |
| `testUpDateEnabledBodyFlying` | up / date / enabled / 本体 / flying | 設置物/アイテム動作回帰 / up / date / enabled / 本体 / flying | 良い | - | - |
| `testObjHitProcessLiveBodyPainAddsToList` | obj / hit / process / live / 本体 / pain / adds / to / list | 設置物/アイテム動作回帰 / obj / hit / process / live / 本体 / pain / adds / to / list | 良い | - | - |
| `testObjHitProcessLiveBodyAlreadyInListReturnsOne` | obj / hit / process / live / 本体 / already / in / list / 戻り / one | 設置物/アイテム動作回帰 / obj / hit / process / live / 本体 / already / in / list / 戻り / one | 良い | - | - |
| `testUpDateDisabledWithBodyAndEffect` | up / date / disabled / with / 本体 / and / effect | 設置物/アイテム動作回帰 / up / date / disabled / with / 本体 / and / effect | 良い | - | - |
| `testScenarioPainModeWakesBodyAndAppliesPainState` | シナリオ / pain / mode / wakes / 本体 / and / applies / pain / state | 設置物/アイテム動作回帰 / シナリオ / pain / mode / wakes / 本体 / and / applies / pain / state | 良い | - | - |
| `testScenarioPealingModePealsEligibleBody` | シナリオ / pealing / mode / peals / eligible / 本体 | 設置物/アイテム動作回帰 / シナリオ / pealing / mode / peals / eligible / 本体 | 良い | - | - |
| `testScenarioPackingModePacksFullyProcessedBody` | シナリオ / packing / mode / packs / fully / processed / 本体 | 設置物/アイテム動作回帰 / シナリオ / packing / mode / packs / fully / processed / 本体 | 良い | - | - |

### `ProductChuteTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | デフォルトコンストラクタ後に productChutes に登録されること | 設置物/アイテム動作回帰 / constructor 登録回帰 | 良い | - | - |
| `testConstructorWithCoords` | constructor / with / coords | 設置物/アイテム動作回帰 / constructor / with / coords | 良い | - | - |
| `testGetShadowImageIsNull` | 影なし仕様で getShadowImage が null を返すこと | 設置物/アイテム動作回帰 / 影なし仕様回帰 | 良い | - | - |
| `testGetHitCheckObjType` | hitCheckObjType が YUKKURI+SHIT+FOOD+TOY+OBJECT+VOMIT+STALK の組み合わせ | 設置物/アイテム動作回帰 / hitCheck 組み合わせ回帰 | 良い | - | - |
| `testRemoveListData` | removeFromWorld 前後で productChutes の存在が変化すること | 設置物/アイテム動作回帰 / 除去前後対比回帰 | 良い | - | - |
| `testObjHitProcessWithStone` | 石の処理で 0 返り・石が除去されること | 設置物/アイテム動作回帰 / 石処理副作用回帰 | 良い | - | - |
| `testGetImageLayerEnabledDoesNotThrow` | enabled 状態で getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / imageLayer 返り値回帰 | 良い | - | - |
| `testGetImageLayerDisabledDoesNotThrow` | disabled 状態でも getImageLayer が 1 を返すこと | 設置物/アイテム動作回帰 / disabled imageLayer 回帰 | 良い | - | - |
| `testGetBoundingDoesNotThrow` | getBounding が非null を返すこと | 設置物/アイテム動作回帰 / bounding 非null 回帰 | 良い | - | - |
| `testScenarioDiffuserIsIgnoredWithoutRemovalOrCashChange` | シナリオ / diffuser / 状態 / ignored / without / removal / or / cash / change | 設置物/アイテム動作回帰 / シナリオ / diffuser / 状態 / ignored / without / removal / or / cash / change | 良い | - | - |
| `testScenarioYunbaIsIgnoredWithoutRemovalOrCashChange` | シナリオ / yunba / 状態 / ignored / without / removal / or / cash / change | 設置物/アイテム動作回帰 / シナリオ / yunba / 状態 / ignored / without / removal / or / cash / change | 良い | - | - |
| `testScenarioPackedBodyIsSoldAndRemovedWithNetCashGain` | シナリオ / packed / 本体 / 状態 / sold / and / removed / with / net / cash / gain | 設置物/アイテム動作回帰 / シナリオ / packed / 本体 / 状態 / sold / and / removed / with / net / cash / gain | 良い | - | - |

### `StickyPlateTest`
- 状態: 完了 (20/20 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testStickyTypeEnum` | sticky / type / enum | 設置物/アイテム動作回帰 / sticky / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testEnableHitCheckNoBindBody` | enable / hit / 判定 / なし / bind / 本体 | 設置物/アイテム動作回帰 / enable / hit / 判定 / なし / bind / 本体 | 良い | - | - |
| `testEnableHitCheckWithBindBody` | enable / hit / 判定 / with / bind / 本体 | 設置物/アイテム動作回帰 / enable / hit / 判定 / with / bind / 本体 | 良い | - | - |
| `testGetSetBindBody` | 取得 / 設定 / bind / 本体 | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / 本体 | 良い | - | - |
| `testGetSetFixBack` | 取得 / 設定 / fix / back | 設置物/アイテム動作回帰 / 取得 / 設定 / fix / back | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testRemoveListDataWithoutBindBody` | 除去 / list / data / without / bind / 本体 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testRemoveListDataWithBindBody` | 除去 / list / data / with / bind / 本体 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcessDeadBodyReturnsZero` | obj / hit / process / 死亡 / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / 死亡 / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcessNormalBody` | obj / hit / process / normal / 本体 | 設置物/アイテム動作回帰 / obj / hit / process / normal / 本体 | 良い | - | - |
| `testUpDateNoBindBody` | up / date / なし / bind / 本体 | 設置物/アイテム動作回帰 / up / date / なし / bind / 本体 | 良い | - | - |
| `testUpDateDisabledWithBindBody` | up / date / disabled / with / bind / 本体 | 設置物/アイテム動作回帰 / up / date / disabled / with / bind / 本体 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testSetupStickyPlateHeadlessExecutesCode` | setup / sticky / plate / headless / executes / code | 設置物/アイテム動作回帰 / setup / sticky / plate / headless / executes / code | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | 設置物/アイテム動作回帰 / constructor / with / args / headless / executes / code | 良い | - | - |

### `StoneTest`
- 状態: 完了 (87/87 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | 設置物/アイテム動作回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorHouse` | parameterized / constructor / house | 設置物/アイテム動作回帰 / parameterized / constructor / house | 良い | - | - |
| `testParameterizedConstructorNora` | parameterized / constructor / nora | 設置物/アイテム動作回帰 / parameterized / constructor / nora | 良い | - | - |
| `testParameterizedConstructorYasei` | parameterized / constructor / yasei | 設置物/アイテム動作回帰 / parameterized / constructor / yasei | 良い | - | - |
| `testConstructorMapIndex2BecomesNora` | constructor / map / index2 / becomes / nora | 設置物/アイテム動作回帰 / constructor / map / index2 / becomes / nora | 良い | - | - |
| `testConstructorMapIndex3BecomesNora` | constructor / map / index3 / becomes / nora | 設置物/アイテム動作回帰 / constructor / map / index3 / becomes / nora | 良い | - | - |
| `testConstructorMapIndex4BecomesNora` | constructor / map / index4 / becomes / nora | 設置物/アイテム動作回帰 / constructor / map / index4 / becomes / nora | 良い | - | - |
| `testConstructorMapIndex5BecomesYasei` | constructor / map / index5 / becomes / yasei | 設置物/アイテム動作回帰 / constructor / map / index5 / becomes / yasei | 良い | - | - |
| `testConstructorMapIndex6BecomesYasei` | constructor / map / index6 / becomes / yasei | 設置物/アイテム動作回帰 / constructor / map / index6 / becomes / yasei | 良い | - | - |
| `testConstructorRegisteredInWorld` | constructor / registered / in / world | 設置物/アイテム動作回帰 / constructor / registered / in / world | 良い | - | - |
| `testSetItemRankHouse` | 設定 / item / rank / house | 設置物/アイテム動作回帰 / 設定 / item / rank / house | 良い | - | - |
| `testSetItemRankNora` | 設定 / item / rank / nora | 設置物/アイテム動作回帰 / 設定 / item / rank / nora | 良い | - | - |
| `testSetItemRankYasei` | 設定 / item / rank / yasei | 設置物/アイテム動作回帰 / 設定 / item / rank / yasei | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testHitCheckObjTypeConstant` | hit / 判定 / obj / type / constant | 設置物/アイテム動作回帰 / hit / 判定 / obj / type / constant | 良い | - | - |
| `testGetBoundingNotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testGrabSetsGrabbedTrue` | grab / sets / grabbed / true | 設置物/アイテム動作回帰 / grab / sets / grabbed / true | 良い | - | - |
| `testKickSetsVelocity` | kick / sets / velocity | 設置物/アイテム動作回帰 / kick / sets / velocity | 良い | - | - |
| `testRemoveListDataRemovesFromWorld` | 除去 / list / data / removes / from / world | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcessBodyWithCutreturnsZero` | obj / hit / process / 本体 / with / cutreturns / zero | 設置物/アイテム動作回帰 / obj / hit / process / 本体 / with / cutreturns / zero | 良い | - | - |
| `testObjHitProcessNonBodyReturnsZero` | obj / hit / process / non / 本体 / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / non / 本体 / 戻り / zero | 良い | - | - |
| `testObjHitProcessAdultBodyInjuredReturnZero` | obj / hit / process / adult / 本体 / injured / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / adult / 本体 / injured / 戻り / zero | 良い | - | - |
| `testObjHitProcessAnotherNonBody` | obj / hit / process / another / non / 本体 | 設置物/アイテム動作回帰 / obj / hit / process / another / non / 本体 | 良い | - | - |
| `testIntervalDefault` | interval / default | 設置物/アイテム動作回帰 / interval / default | 良い | - | - |
| `testCheckInterval` | 判定 / interval | 設置物/アイテム動作回帰 / 判定 / interval | 良い | - | - |
| `testGetEnabledDefaultTrue` | 取得 / enabled / default / true | 設置物/アイテム動作回帰 / 取得 / enabled / default / true | 良い | - | - |
| `testSetEnabled` | 設定 / enabled | 設置物/アイテム動作回帰 / 設定 / enabled | 良い | - | - |
| `testInvertEnabled` | invert / enabled | 設置物/アイテム動作回帰 / invert / enabled | 良い | - | - |
| `testGetOption` | 取得 / option | 設置物/アイテム動作回帰 / 取得 / option | 良い | - | - |
| `testSetOption` | 設定 / option | 設置物/アイテム動作回帰 / 設定 / option | 良い | - | - |
| `testGetLooksDefault` | 取得 / looks / default | 設置物/アイテム動作回帰 / 取得 / looks / default | 良い | - | - |
| `testSetLooks` | 設定 / looks | 設置物/アイテム動作回帰 / 設定 / looks | 良い | - | - |
| `testGetLinkParentDefault` | 取得 / link / 親 / default | 設置物/アイテム動作回帰 / 取得 / link / 親 / default | 良い | - | - |
| `testSetLinkParent` | 設定 / link / 親 | 設置物/アイテム動作回帰 / 設定 / link / 親 | 良い | - | - |
| `testHasSetupMenuDefaultFalse` | 有無 / setup / menu / default / false | 設置物/アイテム動作回帰 / 有無 / setup / menu / default / false | 良い | - | - |
| `testEnableHitCheckDefaultTrue` | enable / hit / 判定 / default / true | 設置物/アイテム動作回帰 / enable / hit / 判定 / default / true | 良い | - | - |
| `testGetSetColW` | 取得 / 設定 / col / w | 設置物/アイテム動作回帰 / 取得 / 設定 / col / w | 良い | - | - |
| `testGetSetColH` | 取得 / 設定 / col / h | 設置物/アイテム動作回帰 / 取得 / 設定 / col / h | 良い | - | - |
| `testGetXgetY` | 取得 / xget / y | 設置物/アイテム動作回帰 / 取得 / xget / y | 良い | - | - |
| `testSetXsetY` | 設定 / xset / y | 設置物/アイテム動作回帰 / 設定 / xset / y | 良い | - | - |
| `testGetZdefault` | 取得 / zdefault | 設置物/アイテム動作回帰 / 取得 / zdefault | 良い | - | - |
| `testIsRemovedDefault` | 状態 / removed / default | 設置物/アイテム動作回帰 / 状態 / removed / default | 良い | - | - |
| `testRemove` | 除去 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testIsCanGrabDefault` | 状態 / 可否 / grab / default | 設置物/アイテム動作回帰 / 状態 / 可否 / grab / default | 良い | - | - |
| `testRelease` | release | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testGetValueDefault` | 取得 / value / default | 設置物/アイテム動作回帰 / 取得 / value / default | 良い | - | - |
| `testGetCostDefault` | 取得 / cost / default | 設置物/アイテム動作回帰 / 取得 / cost / default | 良い | - | - |
| `testGetAgeDefault` | 取得 / age / default | 設置物/アイテム動作回帰 / 取得 / age / default | 良い | - | - |
| `testSetAge` | 設定 / age | 設置物/アイテム動作回帰 / 設定 / age | 良い | - | - |
| `testAddAge` | 追加 / age | 設置物/アイテム動作回帰 / 追加 / age | 良い | - | - |
| `testSetVxVyVz` | 設定 / vx / vy / vz | 設置物/アイテム動作回帰 / 設定 / vx / vy / vz | 良い | - | - |
| `testGetObjType` | 取得 / obj / type | 設置物/アイテム動作回帰 / 取得 / obj / type | 良い | - | - |
| `testGetObjExtype` | 取得 / obj / extype | 設置物/アイテム動作回帰 / 取得 / obj / extype | 良い | - | - |
| `testSetObjExtype` | 設定 / obj / extype | 設置物/アイテム動作回帰 / 設定 / obj / extype | 良い | - | - |
| `testCompareTo` | compare / to | 設置物/アイテム動作回帰 / compare / to | 良い | - | - |
| `testSetBxyzGetBxyz` | 設定 / bxyz / 取得 / bxyz | 設置物/アイテム動作回帰 / 設定 / bxyz / 取得 / bxyz | 良い | - | - |
| `testAddBxyz` | 追加 / bxyz | 設置物/アイテム動作回帰 / 追加 / bxyz | 良い | - | - |
| `testResetBpos` | reset / bpos | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testIsEnableWallDefault` | 状態 / enable / 壁 / default | 設置物/アイテム動作回帰 / 状態 / enable / 壁 / default | 良い | - | - |
| `testSetEnableWall` | 設定 / enable / 壁 | 設置物/アイテム動作回帰 / 設定 / enable / 壁 | 良い | - | - |
| `testGetSetOfsXofsY` | 取得 / 設定 / ofs / xofs / y | 設置物/アイテム動作回帰 / 取得 / 設定 / ofs / xofs / y | 良い | - | - |
| `testGetDrawOfsXdrawOfsY` | 取得 / draw / ofs / xdraw / ofs / y | 設置物/アイテム動作回帰 / 取得 / draw / ofs / xdraw / ofs / y | 良い | - | - |
| `testSetOfsXy` | 設定 / ofs / xy | 設置物/アイテム動作回帰 / 設定 / ofs / xy | 良い | - | - |
| `testGetScreenPivotNotNull` | 取得 / screen / pivot / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / pivot / 非 / null | 良い | - | - |
| `testSetScreenPivot` | 設定 / screen / pivot | 設置物/アイテム動作回帰 / 設定 / screen / pivot | 良い | - | - |
| `testGetScreenRectNotNull` | 取得 / screen / rect / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / rect / 非 / null | 良い | - | - |
| `testIsFallingUnderGroundDefault` | 状態 / falling / under / ground / default | 設置物/アイテム動作回帰 / 状態 / falling / under / ground / default | 良い | - | - |
| `testSetFallingUnderGround` | 設定 / falling / under / ground | 設置物/アイテム動作回帰 / 設定 / falling / under / ground | 良い | - | - |
| `testIsbInPoolDefault` | isb / in / pool / default | 設置物/アイテム動作回帰 / isb / in / pool / default | 良い | - | - |
| `testSetbInPool` | setb / in / pool | 設置物/アイテム動作回帰 / setb / in / pool | 良い | - | - |
| `testGetnMostDepthDefault` | getn / most / depth / default | 設置物/アイテム動作回帰 / getn / most / depth / default | 良い | - | - |
| `testSetnMostDepth` | setn / most / depth | 設置物/アイテム動作回帰 / setn / most / depth | 良い | - | - |
| `testGetSetBindObj` | 取得 / 設定 / bind / obj | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj | 良い | - | - |
| `testSetRemoved` | 設定 / removed | 設置物/アイテム動作回帰 / 設定 / removed | 良い | - | - |
| `testSetCanGrab` | 設定 / 可否 / grab | 設置物/アイテム動作回帰 / 設定 / 可否 / grab | 良い | - | - |
| `testSetGrabbed` | 設定 / grabbed | 設置物/アイテム動作回帰 / 設定 / grabbed | 良い | - | - |
| `testSetValue` | 設定 / value | 設置物/アイテム動作回帰 / 設定 / value | 良い | - | - |
| `testSetCost` | 設定 / cost | 設置物/アイテム動作回帰 / 設定 / cost | 良い | - | - |
| `testGetVxyz` | 取得 / vxyz | 設置物/アイテム動作回帰 / 取得 / vxyz | 良い | - | - |
| `testKickXyz` | kick / xyz | 設置物/アイテム動作回帰 / kick / xyz | 良い | - | - |
| `testCalcPosClampBoundary` | calc / pos / 範囲補正 / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / boundary | 良い | - | - |
| `testCalcPosClampUpperBoundary` | calc / pos / 範囲補正 / upper / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / upper / boundary | 良い | - | - |
| `testGetBoundaryShape` | 取得 / boundary / shape | 設置物/アイテム動作回帰 / 取得 / boundary / shape | 良い | - | - |
| `testGetSetImgWimgH` | 取得 / 設定 / img / wimg / h | 設置物/アイテム動作回帰 / 取得 / 設定 / img / wimg / h | 良い | - | - |
| `testGetSetPivXpivY` | 取得 / 設定 / piv / xpiv / y | 設置物/アイテム動作回帰 / 取得 / 設定 / piv / xpiv / y | 良い | - | - |
| `testSetInterval` | 設定 / interval | 設置物/アイテム動作回帰 / 設定 / interval | 良い | - | - |
| `testGetShadowImageNullWhenNotLoaded` | 取得 / shadow / image / null / when / 非 / loaded | 設置物/アイテム動作回帰 / 取得 / shadow / image / null / when / 非 / loaded | 良い | - | - |

### `SuiTest`
- 状態: 完了 (41/41 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testConstructorWithParams` | constructor / with / params | 設置物/アイテム動作回帰 / constructor / with / params | 良い | - | - |
| `testRideOnNullBodyReturnsFalse` | ride / on / null / 本体 / 戻り / false | 設置物/アイテム動作回帰 / ride / on / null / 本体 / 戻り / false | 良い | - | - |
| `testRideOnNydbodyReturnsFalse` | ride / on / nydbody / 戻り / false | 設置物/アイテム動作回帰 / ride / on / nydbody / 戻り / false | 良い | - | - |
| `testRideOnFirstRiderReturnsTrue` | ride / on / first / rider / 戻り / true | 設置物/アイテム動作回帰 / ride / on / first / rider / 戻り / true | 良い | - | - |
| `testRideOnSameBodyTwiceReturnsFalse` | ride / on / same / 本体 / twice / 戻り / false | 設置物/アイテム動作回帰 / ride / on / same / 本体 / twice / 戻り / false | 良い | - | - |
| `testRideOnSecondRiderReturnsTrue` | ride / on / second / rider / 戻り / true | 設置物/アイテム動作回帰 / ride / on / second / rider / 戻り / true | 良い | - | - |
| `testIsridingNullBodyReturnsFalse` | isriding / null / 本体 / 戻り / false | 設置物/アイテム動作回帰 / isriding / null / 本体 / 戻り / false | 良い | - | - |
| `testIsridingNotOnSuiReturnsFalse` | isriding / 非 / on / sui / 戻り / false | 設置物/アイテム動作回帰 / isriding / 非 / on / sui / 戻り / false | 良い | - | - |
| `testIsridingAfterRideOnReturnsTrue` | isriding / after / ride / on / 戻り / true | 設置物/アイテム動作回帰 / isriding / after / ride / on / 戻り / true | 良い | - | - |
| `testIscanridingNoOwnerReturnsFalse` | iscanriding / なし / owner / 戻り / false | 設置物/アイテム動作回帰 / iscanriding / なし / owner / 戻り / false | 良い | - | - |
| `testIscanridingOwnerOnBoardReturnsTrue` | iscanriding / owner / on / board / 戻り / true | 設置物/アイテム動作回帰 / iscanriding / owner / on / board / 戻り / true | 良い | - | - |
| `testNoCanBindNoOwnerReturnsFalse` | なし / 可否 / bind / なし / owner / 戻り / false | 設置物/アイテム動作回帰 / なし / 可否 / bind / なし / owner / 戻り / false | 良い | - | - |
| `testNoCanBindWithOwnerReturnsTrue` | なし / 可否 / bind / with / owner / 戻り / true | 設置物/アイテム動作回帰 / なし / 可否 / bind / with / owner / 戻り / true | 良い | - | - |
| `testRideOffNullBodyDoesNotThrow` | ride / off / null / 本体 / does / 非 / 例外 | 設置物/アイテム動作回帰 / ride / off / null / 本体 / does / 非 / 例外 | 良い | - | - |
| `testRideOffOwnerAllOff` | ride / off / owner / all / off | 設置物/アイテム動作回帰 / ride / off / owner / all / off | 良い | - | - |
| `testRideOffNonOwnerOnlyThatBodyOff` | ride / off / non / owner / only / that / 本体 / off | 設置物/アイテム動作回帰 / ride / off / non / owner / only / that / 本体 / off | 良い | - | - |
| `testGetSetCurrentBindBodyNum` | 取得 / 設定 / current / bind / 本体 / num | 設置物/アイテム動作回帰 / 取得 / 設定 / current / bind / 本体 / num | 良い | - | - |
| `testGetSetBindBody` | 取得 / 設定 / bind / 本体 | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / 本体 | 良い | - | - |
| `testGetSetCurrentDirection` | 取得 / 設定 / current / direction | 設置物/アイテム動作回帰 / 取得 / 設定 / current / direction | 良い | - | - |
| `testGetSetCurrentCondition` | 取得 / 設定 / current / condition | 設置物/アイテム動作回帰 / 取得 / 設定 / current / condition | 良い | - | - |
| `testEnableHitCheckReturnsTrue` | enable / hit / 判定 / 戻り / true | 設置物/アイテム動作回帰 / enable / hit / 判定 / 戻り / true | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetBoundingDoesNotThrow` | 取得 / bounding / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / bounding / does / 非 / 例外 | 良い | - | - |
| `testRemoveListDataDoesNotThrow` | 除去 / list / data / does / 非 / 例外 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testChangeYdoesNotThrow` | change / ydoes / 非 / 例外 | 設置物/アイテム動作回帰 / change / ydoes / 非 / 例外 | 良い | - | - |
| `testUpDateDoesNotThrow` | up / date / does / 非 / 例外 | 設置物/アイテム動作回帰 / up / date / does / 非 / 例外 | 良い | - | - |
| `testClockTickDoesNotThrow` | clock / tick / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testGetSetBindobj` | 取得 / 設定 / bindobj | 設置物/アイテム動作回帰 / 取得 / 設定 / bindobj | 良い | - | - |
| `testGetSetDestX` | 取得 / 設定 / dest / x | 設置物/アイテム動作回帰 / 取得 / 設定 / dest / x | 良い | - | - |
| `testGetSetDestY` | 取得 / 設定 / dest / y | 設置物/アイテム動作回帰 / 取得 / 設定 / dest / y | 良い | - | - |
| `testGetSetVecX` | 取得 / 設定 / vec / x | 設置物/アイテム動作回帰 / 取得 / 設定 / vec / x | 良い | - | - |
| `testGetSetVecY` | 取得 / 設定 / vec / y | 設置物/アイテム動作回帰 / 取得 / 設定 / vec / y | 良い | - | - |
| `testGetSetSpeed` | 取得 / 設定 / speed | 設置物/アイテム動作回帰 / 取得 / 設定 / speed | 良い | - | - |
| `testObjHitProcessNormalConditionReturnsZero` | obj / hit / process / normal / condition / 戻り / zero | 設置物/アイテム動作回帰 / obj / hit / process / normal / condition / 戻り / zero | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testGetShadowImageDoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testClockTickWithOwnerRidingDoesNotThrow` | clock / tick / with / owner / riding / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testUpDateBodyGrabbedRidesOff` | up / date / 本体 / grabbed / rides / off | 設置物/アイテム動作回帰 / up / date / 本体 / grabbed / rides / off | 良い | - | - |
| `testUpDateOwnerRemovedClearsBindobj` | up / date / owner / removed / clears / bindobj | 設置物/アイテム動作回帰 / up / date / owner / removed / clears / bindobj | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |

### `ToiletTest`
- 状態: 完了 (22/22 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testToiletTypeEnum` | toilet / type / enum | 設置物/アイテム動作回帰 / toilet / type / enum | 良い | - | - |
| `testGetHitCheckObjTypeNotAutoClean` | 取得 / hit / 判定 / obj / type / 非 / auto / clean | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type / 非 / auto / clean | 良い | - | - |
| `testGetHitCheckObjTypeAutoClean` | 取得 / hit / 判定 / obj / type / auto / clean | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type / auto / clean | 良い | - | - |
| `testGetAutoCleanDefault` | 取得 / auto / clean / default | 設置物/アイテム動作回帰 / 取得 / auto / clean / default | 良い | - | - |
| `testSetAutoClean` | 設定 / auto / clean | 設置物/アイテム動作回帰 / 設定 / auto / clean | 良い | - | - |
| `testIsForSlaveDefault` | 状態 / for / slave / default | 設置物/アイテム動作回帰 / 状態 / for / slave / default | 良い | - | - |
| `testSetBforSlave` | 設定 / bfor / slave | 設置物/アイテム動作回帰 / 設定 / bfor / slave | 良い | - | - |
| `testIsForSlave` | 状態 / for / slave | 設置物/アイテム動作回帰 / 状態 / for / slave | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testGetShadowImage` | 取得 / shadow / image | 設置物/アイテム動作回帰 / 取得 / shadow / image | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testObjHitProcess` | obj / hit / process | 設置物/アイテム動作回帰 / obj / hit / process | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testCheckHitObjSingleArgDoesNotThrow` | 判定 / hit / obj / single / arg / does / 非 / 例外 | 設置物/アイテム動作回帰 / 判定 / hit / obj / single / arg / does / 非 / 例外 | 良い | - | - |
| `testCheckHitObjTwoArgsDoesNotThrow` | 判定 / hit / obj / two / args / does / 非 / 例外 | 設置物/アイテム動作回帰 / 判定 / hit / obj / two / args / does / 非 / 例外 | 良い | - | - |
| `testSetupToiletHeadlessExecutesCode` | setup / toilet / headless / executes / code | 設置物/アイテム動作回帰 / setup / toilet / headless / executes / code | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | 設置物/アイテム動作回帰 / constructor / with / args / headless / executes / code | 良い | - | - |
| `testScenarioAutoCleanHitRemovesShitInsideCollision` | シナリオ / auto / clean / hit / removes / shit / inside / 衝突 | 設置物/アイテム動作回帰 / シナリオ / auto / clean / hit / removes / shit / inside / 衝突 | 良い | - | - |
| `testScenarioAutoCleanMissDoesNotRemoveShitOutsideCollision` | シナリオ / auto / clean / miss / does / 非 / 除去 / shit / outside / 衝突 | 設置物/アイテム動作回帰 / シナリオ / auto / clean / miss / does / 非 / 除去 / shit / outside / 衝突 | 良い | - | - |

### `ToyTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorParameterized` | constructor / parameterized | 設置物/アイテム動作回帰 / constructor / parameterized | 良い | - | - |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testGetBoundingNotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerHouseRankReturns1` | 取得 / image / layer / house / rank / returns1 | 設置物/アイテム動作回帰 / 取得 / image / layer / house / rank / returns1 | 良い | - | - |
| `testGetImageLayerNoraRankReturns1` | 取得 / image / layer / nora / rank / returns1 | 設置物/アイテム動作回帰 / 取得 / image / layer / nora / rank / returns1 | 良い | - | - |
| `testGetShadowImageDoesNotThrow` | 取得 / shadow / image / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / shadow / image / does / 非 / 例外 | 良い | - | - |
| `testRemoveListDataDoesNotThrow` | 除去 / list / data / does / 非 / 例外 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGrabSetsGrabbedTrue` | grab / sets / grabbed / true | 設置物/アイテム動作回帰 / grab / sets / grabbed / true | 良い | - | - |
| `testKickDoesNotThrow` | kick / does / 非 / 例外 | 設置物/アイテム動作回帰 / kick / does / 非 / 例外 | 良い | - | - |
| `testSetGetOwner` | 設定 / 取得 / owner | 設置物/アイテム動作回帰 / 設定 / 取得 / owner | 良い | - | - |
| `testIsOwnedCorrectOwnerReturnsTrue` | 状態 / owned / correct / owner / 戻り / true | 設置物/アイテム動作回帰 / 状態 / owned / correct / owner / 戻り / true | 良い | - | - |
| `testIsOwnedWrongOwnerReturnsFalse` | 状態 / owned / wrong / owner / 戻り / false | 設置物/アイテム動作回帰 / 状態 / owned / wrong / owner / 戻り / false | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testConstructorNoraOptionSetsNora` | constructor / nora / option / sets / nora | 設置物/アイテム動作回帰 / constructor / nora / option / sets / nora | 良い | - | - |

### `TrampolineTest`
- 状態: 完了 (17/17 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testTrampolineTypeEnum` | trampoline / type / enum | 設置物/アイテム動作回帰 / trampoline / type / enum | 良い | - | - |
| `testGetHitCheckObjType` | 取得 / hit / 判定 / obj / type | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testGetShadowImageReturnsNullWhenImagesNotLoaded` | 取得 / shadow / image / 戻り / null / when / images / 非 / loaded | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null / when / images / 非 / loaded | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGrab` | grab | 設置物/アイテム動作回帰 / grab | 良い | - | - |
| `testKick` | kick | 設置物/アイテム動作回帰 / kick | 良い | - | - |
| `testGetSetOption` | 取得 / 設定 / option | 設置物/アイテム動作回帰 / 取得 / 設定 / option | 良い | - | - |
| `testGetSetAccident1` | 取得 / 設定 / accident1 | 設置物/アイテム動作回帰 / 取得 / 設定 / accident1 | 良い | - | - |
| `testGetSetAccident2` | 取得 / 設定 / accident2 | 設置物/アイテム動作回帰 / 取得 / 設定 / accident2 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetImageLayerDoesNotThrow` | 取得 / image / layer / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / does / 非 / 例外 | 良い | - | - |
| `testCheckHitObjSingleArgDoesNotThrow` | 判定 / hit / obj / single / arg / does / 非 / 例外 | 設置物/アイテム動作回帰 / 判定 / hit / obj / single / arg / does / 非 / 例外 | 良い | - | - |
| `testCheckHitObjTwoArgsDoesNotThrow` | 判定 / hit / obj / two / args / does / 非 / 例外 | 設置物/アイテム動作回帰 / 判定 / hit / obj / two / args / does / 非 / 例外 | 良い | - | - |
| `testSetupTrampolineHeadlessExecutesCode` | setup / trampoline / headless / executes / code | 設置物/アイテム動作回帰 / setup / trampoline / headless / executes / code | 良い | - | - |
| `testConstructorWithArgsHeadlessExecutesCode` | constructor / with / args / headless / executes / code | 設置物/アイテム動作回帰 / constructor / with / args / headless / executes / code | 良い | - | - |

### `TrashTest`
- 状態: 完了 (74/74 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDefaultConstructor` | default / constructor | 設置物/アイテム動作回帰 / default / constructor | 良い | - | - |
| `testParameterizedConstructorBasic` | parameterized / constructor / basic | 設置物/アイテム動作回帰 / parameterized / constructor / basic | 良い | - | - |
| `testParameterizedConstructorPosition` | parameterized / constructor / position | 設置物/アイテム動作回帰 / parameterized / constructor / position | 良い | - | - |
| `testConstructorRegisteredInWorld` | constructor / registered / in / world | 設置物/アイテム動作回帰 / constructor / registered / in / world | 良い | - | - |
| `testMultipleTrashHaveUniqueIds` | multiple / trash / have / unique / ids | 設置物/アイテム動作回帰 / multiple / trash / have / unique / ids | 良い | - | - |
| `testRemoveListDataRemovesFromWorld` | 除去 / list / data / removes / from / world | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testKickSetsVelocity` | kick / sets / velocity | 設置物/アイテム動作回帰 / kick / sets / velocity | 良い | - | - |
| `testKickXyz` | kick / xyz | 設置物/アイテム動作回帰 / kick / xyz | 良い | - | - |
| `testGetBoundingNotNull` | 取得 / bounding / 非 / null | 設置物/アイテム動作回帰 / 取得 / bounding / 非 / null | 良い | - | - |
| `testGetShadowImageNullWhenNotLoaded` | 取得 / shadow / image / null / when / 非 / loaded | 設置物/アイテム動作回帰 / 取得 / shadow / image / null / when / 非 / loaded | 良い | - | - |
| `testGetHitCheckObjTypeDefaultZero` | 取得 / hit / 判定 / obj / type / default / zero | 設置物/アイテム動作回帰 / 取得 / hit / 判定 / obj / type / default / zero | 良い | - | - |
| `testObjHitProcessDefault` | obj / hit / process / default | 設置物/アイテム動作回帰 / obj / hit / process / default | 良い | - | - |
| `testGetObjExtype` | 取得 / obj / extype | 設置物/アイテム動作回帰 / 取得 / obj / extype | 良い | - | - |
| `testGetObjType` | 取得 / obj / type | 設置物/アイテム動作回帰 / 取得 / obj / type | 良い | - | - |
| `testCheckInterval` | 判定 / interval | 設置物/アイテム動作回帰 / 判定 / interval | 良い | - | - |
| `testGetEnabledDefaultTrue` | 取得 / enabled / default / true | 設置物/アイテム動作回帰 / 取得 / enabled / default / true | 良い | - | - |
| `testSetEnabled` | 設定 / enabled | 設置物/アイテム動作回帰 / 設定 / enabled | 良い | - | - |
| `testInvertEnabled` | invert / enabled | 設置物/アイテム動作回帰 / invert / enabled | 良い | - | - |
| `testHasSetupMenuDefaultFalse` | 有無 / setup / menu / default / false | 設置物/アイテム動作回帰 / 有無 / setup / menu / default / false | 良い | - | - |
| `testEnableHitCheckDefaultTrue` | enable / hit / 判定 / default / true | 設置物/アイテム動作回帰 / enable / hit / 判定 / default / true | 良い | - | - |
| `testGetOption` | 取得 / option | 設置物/アイテム動作回帰 / 取得 / option | 良い | - | - |
| `testSetOption` | 設定 / option | 設置物/アイテム動作回帰 / 設定 / option | 良い | - | - |
| `testGetLooksDefault` | 取得 / looks / default | 設置物/アイテム動作回帰 / 取得 / looks / default | 良い | - | - |
| `testSetLooks` | 設定 / looks | 設置物/アイテム動作回帰 / 設定 / looks | 良い | - | - |
| `testGetLinkParentDefault` | 取得 / link / 親 / default | 設置物/アイテム動作回帰 / 取得 / link / 親 / default | 良い | - | - |
| `testSetLinkParent` | 設定 / link / 親 | 設置物/アイテム動作回帰 / 設定 / link / 親 | 良い | - | - |
| `testGetSetColW` | 取得 / 設定 / col / w | 設置物/アイテム動作回帰 / 取得 / 設定 / col / w | 良い | - | - |
| `testGetSetColH` | 取得 / 設定 / col / h | 設置物/アイテム動作回帰 / 取得 / 設定 / col / h | 良い | - | - |
| `testGetXgetY` | 取得 / xget / y | 設置物/アイテム動作回帰 / 取得 / xget / y | 良い | - | - |
| `testSetXsetY` | 設定 / xset / y | 設置物/アイテム動作回帰 / 設定 / xset / y | 良い | - | - |
| `testGetZdefault` | 取得 / zdefault | 設置物/アイテム動作回帰 / 取得 / zdefault | 良い | - | - |
| `testIsRemovedDefault` | 状態 / removed / default | 設置物/アイテム動作回帰 / 状態 / removed / default | 良い | - | - |
| `testRemove` | 除去 | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testSetRemoved` | 設定 / removed | 設置物/アイテム動作回帰 / 設定 / removed | 良い | - | - |
| `testIsCanGrabDefault` | 状態 / 可否 / grab / default | 設置物/アイテム動作回帰 / 状態 / 可否 / grab / default | 良い | - | - |
| `testSetCanGrab` | 設定 / 可否 / grab | 設置物/アイテム動作回帰 / 設定 / 可否 / grab | 良い | - | - |
| `testGrab` | grab | 設置物/アイテム動作回帰 / grab | 良い | - | - |
| `testRelease` | release | 設置物/アイテム動作回帰 / 復活/再生回帰 | 良い | - | - |
| `testSetGrabbed` | 設定 / grabbed | 設置物/アイテム動作回帰 / 設定 / grabbed | 良い | - | - |
| `testGetValueDefault` | 取得 / value / default | 設置物/アイテム動作回帰 / 取得 / value / default | 良い | - | - |
| `testSetValue` | 設定 / value | 設置物/アイテム動作回帰 / 設定 / value | 良い | - | - |
| `testGetCostDefault` | 取得 / cost / default | 設置物/アイテム動作回帰 / 取得 / cost / default | 良い | - | - |
| `testSetCost` | 設定 / cost | 設置物/アイテム動作回帰 / 設定 / cost | 良い | - | - |
| `testGetAgeDefault` | 取得 / age / default | 設置物/アイテム動作回帰 / 取得 / age / default | 良い | - | - |
| `testSetAge` | 設定 / age | 設置物/アイテム動作回帰 / 設定 / age | 良い | - | - |
| `testAddAge` | 追加 / age | 設置物/アイテム動作回帰 / 追加 / age | 良い | - | - |
| `testSetVxVyVz` | 設定 / vx / vy / vz | 設置物/アイテム動作回帰 / 設定 / vx / vy / vz | 良い | - | - |
| `testGetVxyz` | 取得 / vxyz | 設置物/アイテム動作回帰 / 取得 / vxyz | 良い | - | - |
| `testGetSetOfsXofsY` | 取得 / 設定 / ofs / xofs / y | 設置物/アイテム動作回帰 / 取得 / 設定 / ofs / xofs / y | 良い | - | - |
| `testGetDrawOfsXdrawOfsY` | 取得 / draw / ofs / xdraw / ofs / y | 設置物/アイテム動作回帰 / 取得 / draw / ofs / xdraw / ofs / y | 良い | - | - |
| `testSetOfsXy` | 設定 / ofs / xy | 設置物/アイテム動作回帰 / 設定 / ofs / xy | 良い | - | - |
| `testGetScreenPivotNotNull` | 取得 / screen / pivot / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / pivot / 非 / null | 良い | - | - |
| `testSetScreenPivot` | 設定 / screen / pivot | 設置物/アイテム動作回帰 / 設定 / screen / pivot | 良い | - | - |
| `testGetScreenRectNotNull` | 取得 / screen / rect / 非 / null | 設置物/アイテム動作回帰 / 取得 / screen / rect / 非 / null | 良い | - | - |
| `testSetBxyzResetBpos` | 設定 / bxyz / reset / bpos | 設置物/アイテム動作回帰 / 設定 / bxyz / reset / bpos | 良い | - | - |
| `testAddBxyz` | 追加 / bxyz | 設置物/アイテム動作回帰 / 追加 / bxyz | 良い | - | - |
| `testIsEnableWallDefault` | 状態 / enable / 壁 / default | 設置物/アイテム動作回帰 / 状態 / enable / 壁 / default | 良い | - | - |
| `testSetEnableWall` | 設定 / enable / 壁 | 設置物/アイテム動作回帰 / 設定 / enable / 壁 | 良い | - | - |
| `testIsFallingUnderGroundDefault` | 状態 / falling / under / ground / default | 設置物/アイテム動作回帰 / 状態 / falling / under / ground / default | 良い | - | - |
| `testSetFallingUnderGround` | 設定 / falling / under / ground | 設置物/アイテム動作回帰 / 設定 / falling / under / ground | 良い | - | - |
| `testIsbInPoolDefault` | isb / in / pool / default | 設置物/アイテム動作回帰 / isb / in / pool / default | 良い | - | - |
| `testSetbInPool` | setb / in / pool | 設置物/アイテム動作回帰 / setb / in / pool | 良い | - | - |
| `testGetnMostDepthDefault` | getn / most / depth / default | 設置物/アイテム動作回帰 / getn / most / depth / default | 良い | - | - |
| `testSetnMostDepth` | setn / most / depth | 設置物/アイテム動作回帰 / setn / most / depth | 良い | - | - |
| `testGetSetBindObj` | 取得 / 設定 / bind / obj | 設置物/アイテム動作回帰 / 取得 / 設定 / bind / obj | 良い | - | - |
| `testCompareTo` | compare / to | 設置物/アイテム動作回帰 / compare / to | 良い | - | - |
| `testCalcPosClampBoundary` | calc / pos / 範囲補正 / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / boundary | 良い | - | - |
| `testCalcPosClampUpperBoundary` | calc / pos / 範囲補正 / upper / boundary | 設置物/アイテム動作回帰 / calc / pos / 範囲補正 / upper / boundary | 良い | - | - |
| `testGetBoundaryShape` | 取得 / boundary / shape | 設置物/アイテム動作回帰 / 取得 / boundary / shape | 良い | - | - |
| `testGetSetImgWimgH` | 取得 / 設定 / img / wimg / h | 設置物/アイテム動作回帰 / 取得 / 設定 / img / wimg / h | 良い | - | - |
| `testGetSetPivXpivY` | 取得 / 設定 / piv / xpiv / y | 設置物/アイテム動作回帰 / 取得 / 設定 / piv / xpiv / y | 良い | - | - |
| `testSetInterval` | 設定 / interval | 設置物/アイテム動作回帰 / 設定 / interval | 良い | - | - |
| `testSetObjExtype` | 設定 / obj / extype | 設置物/アイテム動作回帰 / 設定 / obj / extype | 良い | - | - |
| `testGetTmpPosNotNull` | 取得 / tmp / pos / 非 / null | 設置物/アイテム動作回帰 / 取得 / tmp / pos / 非 / null | 良い | - | - |

### `YunbaTest`
- 状態: 完了 (66/66 良い)
- クラス要約: `設置物/アイテム動作回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorDefault` | constructor / default | 設置物/アイテム動作回帰 / constructor / default | 良い | - | - |
| `testActionEnumCount` | action / enum / count | 設置物/アイテム動作回帰 / action / enum / count | 良い | - | - |
| `testActionEnumToString` | action / enum / to / string | 設置物/アイテム動作回帰 / action / enum / to / string | 良い | - | - |
| `testActionEnumValueOf` | action / enum / value / of | 設置物/アイテム動作回帰 / action / enum / value / of | 良い | - | - |
| `testGetBounding` | 取得 / bounding | 設置物/アイテム動作回帰 / 取得 / bounding | 良い | - | - |
| `testHitCheckObjType` | hit / 判定 / obj / type | 設置物/アイテム動作回帰 / hit / 判定 / obj / type | 良い | - | - |
| `testHasSetupMenu` | 有無 / setup / menu | 設置物/アイテム動作回帰 / 有無 / setup / menu | 良い | - | - |
| `testGetSetItemRank` | 取得 / 設定 / item / rank | 設置物/アイテム動作回帰 / 取得 / 設定 / item / rank | 良い | - | - |
| `testGetSetColor` | 取得 / 設定 / color | 設置物/アイテム動作回帰 / 取得 / 設定 / color | 良い | - | - |
| `testGetSetDirection` | 取得 / 設定 / direction | 設置物/アイテム動作回帰 / 取得 / 設定 / direction | 良い | - | - |
| `testGetSetActionFlags` | 取得 / 設定 / action / flags | 設置物/アイテム動作回帰 / 取得 / 設定 / action / flags | 良い | - | - |
| `testGetSetActionFlags2` | 取得 / 設定 / action / flags2 | 設置物/アイテム動作回帰 / 取得 / 設定 / action / flags2 | 良い | - | - |
| `testGetSetActionFlags3` | 取得 / 設定 / action / flags3 | 設置物/アイテム動作回帰 / 取得 / 設定 / action / flags3 | 良い | - | - |
| `testGetSetBodyCheck` | 取得 / 設定 / 本体 / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / 本体 / 判定 | 良い | - | - |
| `testGetSetShitCheck` | 取得 / 設定 / shit / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / shit / 判定 | 良い | - | - |
| `testGetSetStalkCheck` | 取得 / 設定 / stalk / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / stalk / 判定 | 良い | - | - |
| `testGetSetNorndCheck` | 取得 / 設定 / nornd / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / nornd / 判定 | 良い | - | - |
| `testGetSetKillCheck` | 取得 / 設定 / kill / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / kill / 判定 | 良い | - | - |
| `testGetSetMineutiCheck` | 取得 / 設定 / mineuti / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / mineuti / 判定 | 良い | - | - |
| `testGetSetNoDamageFallCheck` | 取得 / 設定 / なし / ダメージ / 落下 / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / なし / ダメージ / 落下 / 判定 | 良い | - | - |
| `testGetSetFoodCheck` | 取得 / 設定 / food / 判定 | 設置物/アイテム動作回帰 / 取得 / 設定 / food / 判定 | 良い | - | - |
| `testGetSetDrawLayer` | 取得 / 設定 / draw / layer | 設置物/アイテム動作回帰 / 取得 / 設定 / draw / layer | 良い | - | - |
| `testGetSetLayerCount` | 取得 / 設定 / layer / count | 設置物/アイテム動作回帰 / 取得 / 設定 / layer / count | 良い | - | - |
| `testGetSetAction` | 取得 / 設定 / action | 設置物/アイテム動作回帰 / 取得 / 設定 / action | 良い | - | - |
| `testGetSetTarget` | 取得 / 設定 / target | 設置物/アイテム動作回帰 / 取得 / 設定 / target | 良い | - | - |
| `testGetSetDestXy` | 取得 / 設定 / dest / xy | 設置物/アイテム動作回帰 / 取得 / 設定 / dest / xy | 良い | - | - |
| `testGetSetSpeed` | 取得 / 設定 / speed | 設置物/アイテム動作回帰 / 取得 / 設定 / speed | 良い | - | - |
| `testGetSetDefaultXy` | 取得 / 設定 / default / xy | 設置物/アイテム動作回帰 / 取得 / 設定 / default / xy | 良い | - | - |
| `testGetValueAndCost` | 取得 / value / and / cost | 設置物/アイテム動作回帰 / 取得 / value / and / cost | 良い | - | - |
| `testRemoveListData` | 除去 / list / data | 設置物/アイテム動作回帰 / 除去フラグ回帰 | 良い | - | - |
| `testUpDateAgeNotDivisibleBy2400NoDeduct` | up / date / age / 非 / divisible / by2400 / なし / deduct | 設置物/アイテム動作回帰 / up / date / age / 非 / divisible / by2400 / なし / deduct | 良い | - | - |
| `testUpDateAgeDivisibleBy2400DeductsCost` | up / date / age / divisible / by2400 / deducts / cost | 設置物/アイテム動作回帰 / up / date / age / divisible / by2400 / deducts / cost | 良い | - | - |
| `testClockTickRemovedReturnsRemoved_574` | clock / tick / removed / 戻り / removed / 574 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGrabbedReturnsDonothing` | clock / tick / grabbed / 戻り / donothing | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickZaboveZeroReturnsDonothing` | clock / tick / zabove / zero / 戻り / donothing | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickActionSelectionBranchAllFalse` | clock / tick / action / selection / branch / all / false | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMovementBranchWithDestination` | clock / tick / movement / branch / with / destination | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMovementBranchNoDestination` | clock / tick / movement / branch / なし / destination | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMovementBranchTargetRemoved` | clock / tick / movement / branch / target / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickShitCheckNoShit` | clock / tick / shit / 判定 / なし / shit | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testCheackOtherYunbaTargetNoOtherYunbaReturnsTrue` | cheack / other / yunba / target / なし / other / yunba / 戻り / true | 設置物/アイテム動作回帰 / cheack / other / yunba / target / なし / other / yunba / 戻り / true | 良い | - | - |
| `testCheackOtherYunbaTargetOtherYunbaHasTargetReturnsFalse` | cheack / other / yunba / target / other / yunba / 有無 / target / 戻り / false | 設置物/アイテム動作回帰 / cheack / other / yunba / target / other / yunba / 有無 / target / 戻り / false | 良い | - | - |
| `testClockTickBodyCheckDeadBodyBodyRemoveEnabled` | clock / tick / 本体 / 判定 / 死亡 / 本体 / 本体 / 除去 / enabled | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickShitCheckWithShitDoesNotThrow` | clock / tick / shit / 判定 / with / shit / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickShitCheckWithVomitDoesNotThrow` | clock / tick / shit / 判定 / with / vomit / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickFoodCheckWithEmptyFoodDoesNotThrow` | clock / tick / food / 判定 / with / empty / food / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickMovementNegativeDirectionDoesNotThrow` | clock / tick / movement / negative / direction / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearShitTargetDoesNotThrow` | clock / tick / near / shit / target / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickGrabbedDoesNothing` | clock / tick / grabbed / does / nothing | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickRemovedReturnsRemoved` | clock / tick / removed / 戻り / removed | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickAirborneDoesNothing` | clock / tick / airborne / does / nothing | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testConstructorWithCoordsDoesNotThrow` | constructor / with / coords / does / 非 / 例外 | 設置物/アイテム動作回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | - |
| `testGetImageLayerDefaultLayerCountReturnsZero` | 取得 / image / layer / default / layer / count / 戻り / zero | 設置物/アイテム動作回帰 / 取得 / image / layer / default / layer / count / 戻り / zero | 良い | - | - |
| `testGetImageLayerLayerCount1DoesNotThrow` | 取得 / image / layer / layer / count1 / does / 非 / 例外 | 設置物/アイテム動作回帰 / 取得 / image / layer / layer / count1 / does / 非 / 例外 | 良い | - | - |
| `testGetShadowImageReturnsNullElement` | 取得 / shadow / image / 戻り / null / element | 設置物/アイテム動作回帰 / 取得 / shadow / image / 戻り / null / element | 良い | - | - |
| `testSetupYunbaHeadlessDoesNotThrow` | setup / yunba / headless / does / 非 / 例外 | 設置物/アイテム動作回帰 / setup / yunba / headless / does / 非 / 例外 | 良い | - | - |
| `testLoadImagesHeadlessExecutesCode` | ロード / images / headless / executes / code | 設置物/アイテム動作回帰 / 状態保存復元確認 | 良い | - | - |
| `testClockTickNearDeadBodyBodyRemoveDoesNotThrow` | clock / tick / near / 死亡 / 本体 / 本体 / 除去 / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearDirtyBodyCleanDoesNotThrow` | clock / tick / near / dirty / 本体 / clean / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearDamagedBodyHealDoesNotThrow` | clock / tick / near / damaged / 本体 / heal / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearStalkStalkDoesNotThrow` | clock / tick / near / stalk / stalk / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearEmptyFoodEmpFoodDoesNotThrow` | clock / tick / near / empty / food / emp / food / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearBodyDestroyDoesNotThrow` | clock / tick / near / 本体 / destroy / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickNearSickBodyKabiDoesNotThrow` | clock / tick / near / 病気 / 本体 / kabi / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickBodyCheckLivingDirtyBodyCleanEnabledDoesNotThrow` | clock / tick / 本体 / 判定 / living / dirty / 本体 / clean / enabled / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |
| `testClockTickStalkCheckWithStalkDoesNotThrow` | clock / tick / stalk / 判定 / with / stalk / does / 非 / 例外 | 設置物/アイテム動作回帰 / tick 全体の統合回帰 | 良い | - | - |

## `org.simyukkuri.logic`
### `AntInfestationPolicyTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testJudgeNewAntHitAddsAttachment` | judge / new / ant / hit / adds / attachment | ルール/判定回帰 / judge / new / ant / hit / adds / attachment | 良い | - | - |
| `testJudgeNewAntDirtyAndDontJumpHalveProbabilityTwice` | judge / new / ant / dirty / and / dont / jump / halve / probability / twice | ルール/判定回帰 / 確率計算 2 回半減回帰 | 良い | - | - |

### `BadgeLogicTest`
- 状態: 完了 (16/16 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBadgeTestNullBody` | null body で false かつバッジが付かないこと | ルール/判定回帰 / null ガード回帰 | 良い | - | - |
| `testBadgeTestDeadBody` | dead body で false かつバッジが付かないこと | ルール/判定回帰 / dead ガード回帰 | 良い | - | - |
| `testBadgeTestRemovedBody` | removed body で false かつバッジが付かないこと | ルール/判定回帰 / removed ガード回帰 | 良い | - | - |
| `testVeryNiceWiseGetsGold` | VERY_NICE+WISE → GOLD バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testVeryNiceAverageGetsSilver` | VERY_NICE+AVERAGE → SILVER バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testVeryNiceFoolGetsBronze` | VERY_NICE+FOOL → BRONZE バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testNiceWiseGetsGold` | NICE+WISE → GOLD バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testNiceAverageGetsSilver` | NICE+AVERAGE → SILVER バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testAverageWiseGetsSilver` | AVERAGE+WISE → SILVER バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testAverageFoolGetsBronze` | AVERAGE+FOOL → BRONZE バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testShitheadWiseGetsBronze` | SHITHEAD+WISE → BRONZE バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testSuperShitheadFoolGetsFake` | SUPER_SHITHEAD+FOOL → FAKE バッジが1つ付くこと | ルール/判定回帰 / badge ランク回帰 | 良い | - | - |
| `testStrayFoolGetsFake` | NORAYU → FAKE バッジが1つ付くこと | ルール/判定回帰 / NORAYU badge 回帰 | 良い | - | - |
| `testIdiotGetsFake` | idiot → FAKE バッジが1つ付くこと | ルール/判定回帰 / idiot badge 回帰 | 良い | - | - |
| `testBadgeReplacesExisting` | 1回目GOLD付与・2回目で既存バッジが除去されること | ルール/判定回帰 / badge 交換回帰 | 良い | - | - |
| `testScenarioNewGoldBadgeMakesBodyBeVainAndReducesStress` | シナリオ / new / gold / badge / makes / 本体 / be / vain / and / reduces / ストレス | ルール/判定回帰 / シナリオ / new / gold / badge / makes / 本体 / be / vain / and / reduces / ストレス | 良い | - | - |

### `BedLogicTest`
- 状態: 完了 (28/28 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckBedSleepy` | 判定 / bed / sleepy | ルール/判定回帰 / 判定 / bed / sleepy | 良い | - | - |
| `testSearchBedFound` | search / bed / found | ルール/判定回帰 / search / bed / found | 良い | - | - |
| `testSearchBedNotFound` | search / bed / 非 / found | ルール/判定回帰 / search / bed / 非 / found | 良い | - | - |
| `testConstructorDoesNotThrow` | constructor / does / 非 / 例外 | ルール/判定回帰 / constructor / does / 非 / 例外 | 良い | - | - |
| `testCheckBedIsToFoodReturnsFalse` | 判定 / bed / 状態 / to / food / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / food / 戻り / false | 良い | - | - |
| `testCheckBedIsToShitReturnsFalse` | 判定 / bed / 状態 / to / shit / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / shit / 戻り / false | 良い | - | - |
| `testCheckBedIsToSukkiriReturnsFalse` | 判定 / bed / 状態 / to / sukkiri / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / sukkiri / 戻り / false | 良い | - | - |
| `testCheckBedIsToStealReturnsFalse` | 判定 / bed / 状態 / to / steal / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / steal / 戻り / false | 良い | - | - |
| `testCheckBedNoConditionReturnsFalse` | 判定 / bed / なし / condition / 戻り / false | ルール/判定回帰 / 判定 / bed / なし / condition / 戻り / false | 良い | - | - |
| `testCheckBedSleepyNoBedReturnsFalse` | 判定 / bed / sleepy / なし / bed / 戻り / false | ルール/判定回帰 / 判定 / bed / sleepy / なし / bed / 戻り / false | 良い | - | - |
| `testCheckBedWithBedSleepyExecutesCode` | 判定 / bed / with / bed / sleepy / executes / code | ルール/判定回帰 / 判定 / bed / with / bed / sleepy / executes / code | 良い | - | - |
| `testCheckBedIsToBedTargetNullReturnsFalse` | 判定 / bed / 状態 / to / bed / target / null / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / target / null / 戻り / false | 良い | - | - |
| `testCheckBedIsToBedTargetRemovedClearsFavItem` | 判定 / bed / 状態 / to / bed / target / removed / clears / fav / item | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / target / removed / clears / fav / item | 良い | - | - |
| `testCheckBedIsToBedUnunSlaveClearsFavItem` | 判定 / bed / 状態 / to / bed / unun / slave / clears / fav / item | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / unun / slave / clears / fav / item | 良い | - | - |
| `testCheckBedIsToBedArrivedSetsStay` | 判定 / bed / 状態 / to / bed / arrived / sets / stay | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / arrived / sets / stay | 良い | - | - |
| `testCheckBedIsToBedNotArrivedMovesTo` | 判定 / bed / 状態 / to / bed / 非 / arrived / moves / to | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / 非 / arrived / moves / to | 良い | - | - |
| `testScenarioArrivalAtBedStoresFavoriteBed` | シナリオ / arrival / at / bed / stores / favorite / bed | ルール/判定回帰 / シナリオ / arrival / at / bed / stores / favorite / bed | 良い | - | - |
| `testSearchBedUnunSlaveWithToiletFindsToilet` | search / bed / unun / slave / with / toilet / finds / toilet | ルール/判定回帰 / search / bed / unun / slave / with / toilet / finds / toilet | 良い | - | - |
| `testSearchBedUnunSlaveNoToiletReturnsNull` | search / bed / unun / slave / なし / toilet / 戻り / null | ルール/判定回帰 / search / bed / unun / slave / なし / toilet / 戻り / null | 良い | - | - |
| `testSearchBedNoBedsButHouseFindsHouse` | search / bed / なし / beds / but / house / finds / house | ルール/判定回帰 / search / bed / なし / beds / but / house / finds / house | 良い | - | - |
| `testCheckBedWithHouseOnlyDoesNotThrow` | 判定 / bed / with / house / only / does / 非 / 例外 | ルール/判定回帰 / 判定 / bed / with / house / only / does / 非 / 例外 | 良い | - | - |
| `testCheckBedIsToTakeoutReturnsFalse` | 判定 / bed / 状態 / to / takeout / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / to / takeout / 戻り / false | 良い | - | - |
| `testCheckBedIsIdiotReturnsFalse` | 判定 / bed / 状態 / idiot / 戻り / false | ルール/判定回帰 / 判定 / bed / 状態 / idiot / 戻り / false | 良い | - | - |
| `testCheckBedNearToBirthHighEventReturnsFalse` | 判定 / bed / near / to / birth / high / イベント / 戻り / false | ルール/判定回帰 / 判定 / bed / near / to / birth / high / イベント / 戻り / false | 良い | - | - |
| `testCheckBedNotNearToBirthMiddleEventReturnsFalse` | 判定 / bed / 非 / near / to / birth / middle / イベント / 戻り / false | ルール/判定回帰 / 判定 / bed / 非 / near / to / birth / middle / イベント / 戻り / false | 良い | - | - |
| `testCheckBedIsNydreturnsFalse` | 判定 / bed / 状態 / nydreturns / false | ルール/判定回帰 / 判定 / bed / 状態 / nydreturns / false | 良い | - | - |
| `testCheckBedIsToBedArrivedHasFoodTakeoutDoesNotThrow` | 判定 / bed / 状態 / to / bed / arrived / 有無 / food / takeout / does / 非 / 例外 | ルール/判定回帰 / 判定 / bed / 状態 / to / bed / arrived / 有無 / food / takeout / does / 非 / 例外 | 良い | - | - |
| `testSearchBedFlyingTypeWallModeAdultDoesNotThrow` | search / bed / flying / type / 壁 / mode / adult / does / 非 / 例外 | ルール/判定回帰 / search / bed / flying / type / 壁 / mode / adult / does / 非 / 例外 | 良い | - | - |

### `BodyApproachRuleTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleApproachNonFlyerSetsMoveToBodyPosition` | handle / approach / non / flyer / sets / 移動 / to / 本体 / position | ルール/判定回帰 / handle / approach / non / flyer / sets / 移動 / to / 本体 / position | 良い | - | - |

### `BodyContactEffectRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - イベント進行と状態遷移が壊れない
  - 画像・描画用データが壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleContactEffectsAddsAvoidMoldEventForHealthyActor` | handle / contact / effects / adds / avoid / mold / イベント / for / healthy / actor | ルール/判定回帰 / handle / contact / effects / adds / avoid / mold / イベント / for / healthy / actor | 良い | - | - |
| `testHandleContactEffectsAddsHateNoOkazariWorldEvent` | handle / contact / effects / adds / hate / なし / okazari / world / イベント | ルール/判定回帰 / handle / contact / effects / adds / hate / なし / okazari / world / イベント | 良い | - | - |

### `BodyDeadActionRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleDeadBodyInteractionExcitingBodyHandlesDeadTarget` | exciting+raper な me が dead 相手に doRape し sukkiri/HAPPY になること | ルール/判定回帰 / doRape 副作用回帰 | 良い | - | - |
| `testHandleDeadBodyInteractionParentDeathMakesAdultVerySad` | 親の死亡で成人が VERY_SAD・stress+100・memories-2 になること | ルール/判定回帰 / 親死亡 悲哀回帰 | 良い | - | - |

### `BodyDeadSearchRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleDeadFoundExcitingBranchMovesToSukkiri` | exciting ブランチで true 返りかつ targetBind=false になること | ルール/判定回帰 / exciting ブランチ副作用回帰 | 良い | - | - |
| `testHandleDeadFoundRandomSkipReturnsFalse` | handle / 死亡 / found / random / skip / 戻り / false | ルール/判定回帰 / handle / 死亡 / found / random / skip / 戻り / false | 良い | - | - |

### `BodyEventStateTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `clearActionsResetsMoveFlagsAndMoveTarget` | clearActions で全フラグと moveTargetId がリセットされること | ルール/判定回帰 / clearActions リセット回帰 | 良い | - | - |
| `clearEventResetsCurrentEventAndForceFace` | 非null currentEvent を設定後に clearEvent で null+forceFace=-1になること | ルール/判定回帰 / clearEvent 回帰 | 良い | - | - |
| `setMessageIgnoresEmptyString` | 空文字列で messageTicks 不変、非空文字列で増加する対比確認 | ルール/判定回帰 / setMessage 空文字ガード回帰 | 良い | - | - |
| `setPikoMessageWithCountUpdatesMessageCount` | setPikoMessage(count=3) で messageTicks が 0→3 になること | ルール/判定回帰 / pikoMessage count 回帰 | 良い | - | - |
| `processPendingEventsStartsBodyEventBeforeWorldEvent` | body イベントが先に start され world はまだ start されないこと | ルール/判定回帰 / body イベント優先回帰 | 良い | - | - |
| `processPendingEventsConsumesSimpleEventsWhenResponseDisabled` | simple event が消費され currentEvent=null のまま start されないこと | ルール/判定回帰 / simple event 消費回帰 | 良い | - | - |
| `resolveEventResultActionOverridesDoNothingWithLowPriorityEvent` | fallback=NONE のとき eventResult(SHIT) が返りクリアされること | ルール/判定回帰 / LOW 優先度 NONE 上書き回帰 | 良い | - | - |
| `resolveEventResultActionKeepsExistingActionForLowPriorityEvent` | resolve / イベント / result / action / 維持 / existing / action / for / low / priority / イベント | ルール/判定回帰 / resolve / イベント / result / action / 維持 / existing / action / for / low / priority / イベント | 良い | - | - |
| `resolveEventResultActionOverridesExistingActionForHighPriorityEvent` | HIGH 優先度では fallback より eventResult が優先されクリアされること | ルール/判定回帰 / HIGH 優先度上書き回帰 | 良い | - | - |
| `updateCurrentEventClearsCurrentEventWhenAbortReturned` | update / current / イベント / clears / current / イベント / when / abort / returned | ルール/判定回帰 / update / current / イベント / clears / current / イベント / when / abort / returned | 良い | - | - |
| `updateCurrentEventExecutesWhenBodyReachedTarget` | 目標到達で execute・end が呼ばれ currentEvent=null になること | ルール/判定回帰 / 到達時 execute 回帰 | 良い | - | - |
| `updateCurrentEventKeepsCurrentEventWhenTargetIsFar` | update / current / イベント / 維持 / current / イベント / when / target / 状態 / far | ルール/判定回帰 / update / current / イベント / 維持 / current / イベント / when / target / 状態 / far | 良い | - | - |

### `BodyExcitementRuleTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleExcitingContactRaperBranchConsumesAction` | raper ブランチで doRape され sukkiri 状態になること | ルール/判定回帰 / raper ブランチ副作用回帰 | 良い | - | - |
| `testHandleExcitingContactAdultPartnerFallsBackToSukkiri` | パートナー相手に doSukkiri で sukkiri 状態になること | ルール/判定回帰 / パートナーすっきり回帰 | 良い | - | - |
| `testHandleExcitingContactForceExcitingContinues` | forceExciting で false 返りかつ sukkiri になること | ルール/判定回帰 / forceExciting ブランチ回帰 | 良い | - | - |

### `BodyExcretionRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `kaiyuAlwaysCausesDiarrhea` | KAIYU は常に下痢、NORAYU は nextInt≠0 のとき下痢にならないこと | ルール/判定回帰 / KAIYU 下痢回帰 | 良い | - | - |
| `sicknessAndDamageHalveTheProbabilityBound` | sick+damage で確率境界が半減することを確認 | ルール/判定回帰 / 確率半減回帰 | 良い | - | - |

### `BodyIllnessRuleTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `wiseBodyRecognizesNormalSickness` | WISE は軽症でも病気を見抜けること | ルール/判定回帰 / WISE 軽症検出回帰 | 良い | - | - |
| `foolBodyRequiresSevereSickness` | FOOL は軽症では見抜けず重症なら見抜けること | ルール/判定回帰 / FOOL 重症判定回帰 | 良い | - | - |
| `healthyTargetIsIgnored` | 健康な target は AVERAGE/WISE いずれも無視すること | ルール/判定回帰 / 健康無視回帰 | 良い | - | - |

### `BodyLogicTest`
- 状態: 完了 (455/455 良い)
- クラス要約: `家族関係/家族行動回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - イベント進行と状態遷移が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testScenarioExcitingPartnerDropsCarriedShitAndStartsMoveToSukkiri` | シナリオ / exciting / 相手 / drops / carried / shit / and / starts / 移動 / to / sukkiri | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDirtyChildNearParentGetsCleanedByPeropero` | シナリオ / dirty / 子 / near / 親 / gets / cleaned / by / peropero | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioWakeupCheckIgnoresUnunSlaveButCountsNormalWitness` | シナリオ / wakeup / 判定 / ignores / unun / slave / but / counts / normal / witness | ルール/判定回帰 / シナリオ / wakeup / 判定 / ignores / unun / slave / but / counts / normal / witness | 良い | - | - |
| `testScenarioBabyDirtyChildGetsCleanedAndBothRelaxDuringDoActionOther` | シナリオ / baby / dirty / 子 / gets / cleaned / and / both / relax / during / do / action / other | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerReusesMoveTargetAndCleansDirtyChildImmediately` | シナリオ / 判定 / 相手 / reuses / 移動 / target / and / cleans / dirty / 子 / immediately | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerSurisuriFromPlayerMotherBranchStartsUnboundMoveToBody` | シナリオ / 判定 / 相手 / surisuri / from / player / 母 / branch / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioRudeAdultWithoutOkazariTargetsDecoratedBodyForSteal` | シナリオ / rude / adult / without / okazari / targets / decorated / 本体 / for / steal | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioPheromoneDecoratedTargetOverridesCloserBodyForSteal` | シナリオ / pheromone / decorated / target / overrides / closer / 本体 / for / steal | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioAwakeWitnessBlocksStealApproachDuringCheckPartner` | シナリオ / awake / witness / blocks / steal / approach / during / 判定 / 相手 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioTargetBindNonAdjacentActionMakesTargetStay` | シナリオ / target / bind / non / adjacent / action / makes / target / stay | ルール/判定回帰 / シナリオ / target / bind / non / adjacent / action / makes / target / stay | 良い | - | - |
| `testScenarioRudeStealActionTransfersOkazariFromSleepingTarget` | シナリオ / rude / steal / action / transfers / okazari / from / sleeping / target | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioUnunSlaveStealSuccessPromotesActorAndDemotesTarget` | シナリオ / unun / slave / steal / success / promotes / actor / and / demotes / target | ルール/判定回帰 / シナリオ / unun / slave / steal / success / promotes / actor / and / demotes / target | 良い | - | - |
| `testScenarioStealActionAbortsWhenAwakeWitnessCanSeeActor` | シナリオ / steal / action / aborts / when / awake / witness / 可否 / see / actor | ルール/判定回帰 / シナリオ / steal / action / aborts / when / awake / witness / 可否 / see / actor | 良い | - | - |
| `testScenarioStealActionFailsWhenActorAlreadyHasOkazari` | シナリオ / steal / action / fails / when / actor / already / 有無 / okazari | ルール/判定回帰 / シナリオ / steal / action / fails / when / actor / already / 有無 / okazari | 良い | - | - |
| `testScenarioStealActionFailsWhenActorIsLockmoved` | シナリオ / steal / action / fails / when / actor / 状態 / lockmoved | ルール/判定回帰 / シナリオ / steal / action / fails / when / actor / 状態 / lockmoved | 良い | - | - |
| `testScenarioRemovedTargetClearsPendingActionAndReturnsFalse` | シナリオ / removed / target / clears / pending / action / and / 戻り / false | ルール/判定回帰 / シナリオ / removed / target / clears / pending / action / and / 戻り / false | 良い | - | - |
| `testScenarioFloatingTargetClearsPendingActionForGroundActor` | シナリオ / floating / target / clears / pending / action / for / ground / actor | ルール/判定回帰 / シナリオ / floating / target / clears / pending / action / for / ground / actor | 良い | - | - |
| `testScenarioSmartChildCleansDirtySisterByPeropero` | シナリオ / smart / 子 / cleans / dirty / 姉妹 / by / peropero | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioPartnerSurisuriMakesBothVeryHappyAndStaying` | シナリオ / 相手 / surisuri / makes / both / very / happy / and / staying | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioElderSisterConcernMakesActorSadAndStopsBoth` | シナリオ / elder / 姉妹 / concern / makes / actor / sad / and / stops / both | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioAdultParentTargetsDirtyChildWithBoundMoveToBody` | シナリオ / adult / 親 / targets / dirty / 子 / with / bound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioAdultParentTargetsNeedledChildWithUnboundMoveToBody` | シナリオ / adult / 親 / targets / needled / 子 / with / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioFoolParentWithoutOkazariSkipsApproachingUndecoratedChild` | シナリオ / fool / 親 / without / okazari / skips / approaching / undecorated / 子 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioPartnerRandomApproachStartsUnboundMoveToBody` | シナリオ / 相手 / random / approach / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioChildRandomApproachStartsUnboundMoveToBody` | シナリオ / 子 / random / approach / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioAdultFamilyRandomApproachStartsUnboundMoveToBody` | シナリオ / adult / 家族 / random / approach / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioNeedledPartnerApproachStartsUnboundMoveToBody` | シナリオ / needled / 相手 / approach / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherNeedledPartnerTriggersGuriguriStateChanges` | シナリオ / do / action / other / needled / 相手 / triggers / guriguri / state / changes | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherNeedledChildTriggersGuriguriStateChanges` | シナリオ / do / action / other / needled / 子 / triggers / guriguri / state / changes | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadElderSisterTriggersVerySadStressReaction` | シナリオ / do / action / other / 死亡 / elder / 姉妹 / triggers / very / sad / ストレス / reaction | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadYoungerSisterTriggersVerySadStressReaction` | シナリオ / do / action / other / 死亡 / younger / 姉妹 / triggers / very / sad / ストレス / reaction | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadChildTriggersVerySadStressReaction` | シナリオ / do / action / other / 死亡 / 子 / triggers / very / sad / ストレス / reaction | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadPartnerTriggersVerySadStressReaction` | シナリオ / do / action / other / 死亡 / 相手 / triggers / very / sad / ストレス / reaction | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadParentTriggersSurpriseFaceAndStressReaction` | シナリオ / do / action / other / 死亡 / 親 / triggers / surprise / 表情 / and / ストレス / reaction | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDeadChildStartsUnboundMoveToBody` | シナリオ / 判定 / 相手 / 死亡 / 子 / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDeadPartnerStartsUnboundMoveToBody` | シナリオ / 判定 / 相手 / 死亡 / 相手 / starts / unbound / 移動 / to / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherParentDropsFoodForVeryHungryChild` | シナリオ / do / action / other / 親 / drops / food / for / very / 空腹 / 子 | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherAntCoveredTargetIsNotLickedWhenActorAlsoHasAnts` | シナリオ / do / action / other / ant / covered / target / 状態 / 非 / licked / when / actor / also / 有無 / ants | ルール/判定回帰 / シナリオ / do / action / other / ant / covered / target / 状態 / 非 / licked / when / actor / also / 有無 / ants | 良い | - | - |
| `testScenarioDoActionOtherTreatsAntCoveredTargetByPeropero` | シナリオ / do / action / other / treats / ant / covered / target / by / peropero | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherChildSurisuriMakesBothVeryHappyAndStaying` | シナリオ / do / action / other / 子 / surisuri / makes / both / very / happy / and / staying | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherSisterSurisuriMakesBothVeryHappyAndStaying` | シナリオ / do / action / other / 姉妹 / surisuri / makes / both / very / happy / and / staying | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerGladAboutPartnerSetsStayAndHappy` | シナリオ / 判定 / action / surisuri / from / player / glad / about / 相手 / sets / stay / and / happy | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerEnvyAngryPartnerMakesActorVerySadAndStay` | シナリオ / 判定 / action / surisuri / from / player / envy / angry / 相手 / makes / actor / very / sad / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerFearOnlyMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / fear / only / makes / actor / sad / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / fear / only / makes / actor / sad / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerMercyAboutOtherMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / mercy / about / other / makes / actor / sad / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / mercy / about / other / makes / actor / sad / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutPartnerMakesActorVerySadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / 相手 / makes / actor / very / sad / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutChildWithPainMakesActorVerySadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / 子 / with / pain / makes / actor / very / sad / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerGladAboutChildMakesActorHappyAndStay` | シナリオ / 判定 / action / surisuri / from / player / glad / about / 子 / makes / actor / happy / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerGladAboutMotherMakesActorHappyAndStay` | シナリオ / 判定 / action / surisuri / from / player / glad / about / 母 / makes / actor / happy / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / glad / about / 母 / makes / actor / happy / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerGladAboutFatherKeepsActorHappyAndStay` | シナリオ / 判定 / action / surisuri / from / player / glad / about / 父 / 維持 / actor / happy / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / glad / about / 父 / 維持 / actor / happy / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutFatherWithPainMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / 父 / with / pain / makes / actor / sad / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / concern / about / 父 / with / pain / makes / actor / sad / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerVeryHappyPartnerKeepsActorVeryHappyAndStay` | シナリオ / 判定 / action / surisuri / from / player / very / happy / 相手 / 維持 / actor / very / happy / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutSadPartnerMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / sad / 相手 / makes / actor / sad / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutMotherWithoutPainMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / 母 / without / pain / makes / actor / sad / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / concern / about / 母 / without / pain / makes / actor / sad / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutFatherWithoutPainMakesActorSadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / 父 / without / pain / makes / actor / sad / and / stay | ルール/判定回帰 / シナリオ / 判定 / action / surisuri / from / player / concern / about / 父 / without / pain / makes / actor / sad / and / stay | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerGladAboutYoungerSisterMakesActorHappyAndStay` | シナリオ / 判定 / action / surisuri / from / player / glad / about / younger / 姉妹 / makes / actor / happy / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckActionSurisuriFromPlayerConcernAboutElderSisterWithPainMakesActorVerySadAndStay` | シナリオ / 判定 / action / surisuri / from / player / concern / about / elder / 姉妹 / with / pain / makes / actor / very / sad / and / stay | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherNeedledSisterTriggersGuriguriStateChanges` | シナリオ / do / action / other / needled / 姉妹 / triggers / guriguri / state / changes | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioExcitingAdultPartnersDoSukkiriAndBothRelax` | シナリオ / exciting / adult / partners / do / sukkiri / and / both / relax | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherAddsAvoidMoldEventWithExpectedParticipants` | シナリオ / do / action / other / adds / avoid / mold / イベント / with / expected / participants | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherQueuesAvoidMoldEventOnCleanTargetWhenActorIsSick` | シナリオ / do / action / other / queues / avoid / mold / イベント / on / clean / target / when / actor / 状態 / 病気 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioDoActionOtherAddsHateNoOkazariWorldEventWithExpectedParticipants` | シナリオ / do / action / other / adds / hate / なし / okazari / world / イベント / with / expected / participants | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherHateNoOkazariIsSuppressedWhileActorHasCurrentEvent` | シナリオ / do / action / other / hate / なし / okazari / 状態 / suppressed / while / actor / 有無 / current / イベント | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherHateNoOkazariIsSuppressedForNonNydchild` | シナリオ / do / action / other / hate / なし / okazari / 状態 / suppressed / for / non / nydchild | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadBodyOnanismMakesActorHappyAndStaying` | シナリオ / do / action / other / 死亡 / 本体 / onanism / makes / actor / happy / and / staying | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioDoActionOtherDeadBodyRapeMakesActorSukkiriAndPinsCorpse` | シナリオ / do / action / other / 死亡 / 本体 / rape / makes / actor / sukkiri / and / pins / corpse | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testScenarioDoActionOtherForceExcitingBabyTargetMakesBothBodiesSukkiri` | シナリオ / do / action / other / force / exciting / baby / target / makes / both / bodies / sukkiri | 感情/イベント/反応回帰 | 良い | - | - |
| `testScenarioDoActionOtherExcitingAdultWithoutPartnerFallsBackToOnanism` | シナリオ / do / action / other / exciting / adult / without / 相手 / falls / back / to / onanism | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioDoActionOtherQueuesFuneralEventAndMakesParentVerySad` | シナリオ / do / action / other / queues / funeral / イベント / and / makes / 親 / very / sad | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerQueuesProposeEventWithExpectedParticipants` | シナリオ / 判定 / 相手 / queues / propose / イベント / with / expected / participants | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerQueuesHateNoOkazariWorldEventWithExpectedParticipants` | シナリオ / 判定 / 相手 / queues / hate / なし / okazari / world / イベント / with / expected / participants | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerTalkingActorSuppressesHateNoOkazariEventButStillConsumesTurn` | シナリオ / 判定 / 相手 / talking / actor / suppresses / hate / なし / okazari / イベント / but / still / consumes / turn | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerUnunSlaveSuppressesHateNoOkazariEventButStillConsumesTurn` | シナリオ / 判定 / 相手 / unun / slave / suppresses / hate / なし / okazari / イベント / but / still / consumes / turn | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerNydtargetSuppressesHateNoOkazariEventButStillConsumesTurn` | シナリオ / 判定 / 相手 / nydtarget / suppresses / hate / なし / okazari / イベント / but / still / consumes / turn | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerVeryRudeStartsBoundMoveToSukkiri` | シナリオ / 判定 / 相手 / very / rude / starts / bound / 移動 / to / sukkiri | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerRaperStartsBoundMoveToSukkiri` | シナリオ / 判定 / 相手 / raper / starts / bound / 移動 / to / sukkiri | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerSuperShitheadRapeOnlyStartsBoundMoveToSukkiri` | シナリオ / 判定 / 相手 / super / shithead / rape / only / starts / bound / 移動 / to / sukkiri | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerIdiotTargetCalmsActorWithoutStartingAnyAction` | シナリオ / 判定 / 相手 / idiot / target / calms / actor / without / starting / any / action | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerWithoutTargetFallsBackToStatefulOnanism` | シナリオ / 判定 / 相手 / without / target / falls / back / to / stateful / onanism | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerKillPredatorEventClearsPanicWithoutDroppingCurrentEvent` | シナリオ / 判定 / 相手 / kill / predator / イベント / clears / 恐慌 / without / dropping / current / イベント | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerLowPriorityCurrentEventStillAllowsMoveToSukkiri` | シナリオ / 判定 / 相手 / low / priority / current / イベント / still / allows / 移動 / to / sukkiri | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDeadStrangerMakesAdultLookAndTurnSad` | シナリオ / 判定 / 相手 / 死亡 / stranger / makes / adult / look / and / turn / sad | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDeadStrangerChildRefusesToApproachCorpse` | シナリオ / 判定 / 相手 / 死亡 / stranger / 子 / refuses / to / approach / corpse | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerCallingParentsWakesSleepingParentWithoutStartingAction` | シナリオ / 判定 / 相手 / calling / parents / wakes / sleeping / 親 / without / starting / action | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDifferentRankReturnsFalseWithoutStartingAnyAction` | シナリオ / 判定 / 相手 / different / rank / 戻り / false / without / starting / any / action | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerDeadBodyRandomSkipLeavesActorWithoutAction` | シナリオ / 判定 / 相手 / 死亡 / 本体 / random / skip / leaves / actor / without / action | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerHighPriorityCurrentEventBlocksAllNewActions` | シナリオ / 判定 / 相手 / high / priority / current / イベント / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerToFoodGuardBlocksAllNewActions` | シナリオ / 判定 / 相手 / to / food / guard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerToBedGuardBlocksAllNewActions` | シナリオ / 判定 / 相手 / to / bed / guard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerToShitGuardBlocksAllNewActions` | シナリオ / 判定 / 相手 / to / shit / guard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerWantToShitGuardBlocksAllNewActions` | シナリオ / 判定 / 相手 / want / to / shit / guard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerNearToBirthGuardBlocksAllNewActions` | シナリオ / 判定 / 相手 / near / to / birth / guard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerNydguardBlocksAllNewActions` | シナリオ / 判定 / 相手 / nydguard / blocks / all / new / actions | 家族関係/家族行動回帰 | 良い | - | - |
| `testScenarioCheckPartnerNonExcitingWithCarriedShitKeepsTakeoutAndStartsNothing` | シナリオ / 判定 / 相手 / non / exciting / with / carried / shit / 維持 / takeout / and / starts / nothing | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationChild` | 判定 / my / relation / 子 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationParent` | 判定 / my / relation / 親 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationPartner` | 判定 / my / relation / 相手 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationElderSister` | 判定 / my / relation / elder / 姉妹 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationSister` | 判定 / my / relation / 姉妹 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckMyRelationStranger` | 判定 / my / relation / stranger | ルール/判定回帰 / 判定 / my / relation / stranger | 良い | - | - |
| `testEactionGoEnum` | eaction / go / enum | ルール/判定回帰 / eaction / go / enum | 良い | - | - |
| `testCheckPartnerIsToFoodReturnsFalse` | 判定 / 相手 / 状態 / to / food / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerIsToBedReturnsFalse` | 判定 / 相手 / 状態 / to / bed / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerIsNydreturnsFalse` | 判定 / 相手 / 状態 / nydreturns / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerIsToShitReturnsFalse` | 判定 / 相手 / 状態 / to / shit / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCalcCollisionXnullFromReturnsZero` | calc / 衝突 / xnull / from / 戻り / zero | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCalcCollisionXnullToReturnsZero` | calc / 衝突 / xnull / to / 戻り / zero | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCalcCollisionXbothNull` | calc / 衝突 / xboth / null | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNullArgs` | 判定 / action / surisuri / from / player / null / args | ルール/判定回帰 / 判定 / action / surisuri / from / player / null / args | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNotSurisuriReturnsNone` | 判定 / action / surisuri / from / player / 非 / surisuri / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / 非 / surisuri / 戻り / none | 良い | - | - |
| `testCreateActiveFianceeListEmpty` | 生成 / active / fiancee / list / empty | ルール/判定回帰 / 生成 / active / fiancee / list / empty | 良い | - | - |
| `testCreateActiveChildListEmpty` | 生成 / active / 子 / list / empty | 家族関係/家族行動回帰 | 良い | - | - |
| `testGatheringYukkuriWithToiletL1530` | gathering / yukkuri / with / toilet / l1530 | ルール/判定回帰 / gathering / yukkuri / with / toilet / l1530 | 良い | - | - |
| `testCheckMyRelationFather` | 判定 / my / relation / 父 | ルール/判定回帰 / 判定 / my / relation / 父 | 良い | - | - |
| `testCheckMyRelationChildFather` | 判定 / my / relation / 子 / 父 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouDeadDoesNotThrow` | 判定 / wakeup / other / yukkuri / you / 死亡 / does / 非 / 例外 | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / 死亡 / does / 非 / 例外 | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouAliveDoesNotThrow` | 判定 / wakeup / other / yukkuri / you / alive / does / 非 / 例外 | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / alive / does / 非 / 例外 | 良い | - | - |
| `testDoActionOtherRemovedTargetReturnsFalse` | do / action / other / removed / target / 戻り / false | ルール/判定回帰 / do / action / other / removed / target / 戻り / false | 良い | - | - |
| `testDoActionOtherAirborneTargetDoesNotThrow` | do / action / other / airborne / target / does / 非 / 例外 | ルール/判定回帰 / do / action / other / airborne / target / does / 非 / 例外 | 良い | - | - |
| `testDoActionOtherNydbodyReturnsFalse` | do / action / other / nydbody / 戻り / false | ルール/判定回帰 / do / action / other / nydbody / 戻り / false | 良い | - | - |
| `testGatheringYukkuriFrontEmptyListReturnsFalse` | gathering / yukkuri / front / empty / list / 戻り / false | ルール/判定回帰 / gathering / yukkuri / front / empty / list / 戻り / false | 良い | - | - |
| `testGatheringYukkuriFrontWithEventEmptyListReturnsFalse` | gathering / yukkuri / front / with / イベント / empty / list / 戻り / false | 感情/イベント/反応回帰 | 良い | - | - |
| `testGatheringYukkuriSquareNullTopReturnsFalse` | gathering / yukkuri / square / null / top / 戻り / false | ルール/判定回帰 / gathering / yukkuri / square / null / top / 戻り / false | 良い | - | - |
| `testGatheringYukkuriSquareNullListReturnsFalse` | gathering / yukkuri / square / null / list / 戻り / false | ルール/判定回帰 / gathering / yukkuri / square / null / list / 戻り / false | 良い | - | - |
| `testGatheringYukkuriSquareEmptyArrayReturnsFalse` | gathering / yukkuri / square / empty / array / 戻り / false | ルール/判定回帰 / gathering / yukkuri / square / empty / array / 戻り / false | 良い | - | - |
| `testGatheringYukkuriBackLineNullListReturnsFalse` | gathering / yukkuri / back / line / null / list / 戻り / false | ルール/判定回帰 / gathering / yukkuri / back / line / null / list / 戻り / false | 良い | - | - |
| `testGatheringYukkuriBackLineEmptyListDoesNotThrow` | gathering / yukkuri / back / line / empty / list / does / 非 / 例外 | ルール/判定回帰 / gathering / yukkuri / back / line / empty / list / does / 非 / 例外 | 良い | - | - |
| `testCreateActiveFianceeListHasPartnerReturnsNonNull` | 生成 / active / fiancee / list / 有無 / 相手 / 戻り / non / null | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListWithBabyChildDoesNotThrow` | 生成 / active / 子 / list / with / baby / 子 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentIsAdultDoesNotThrow` | 判定 / near / 親 / 状態 / adult / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentNotAdultNoParentDoesNotThrow` | 判定 / near / 親 / 非 / adult / なし / 親 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerSurisuriTrueDoesNotThrow` | 判定 / action / surisuri / from / player / surisuri / true / does / 非 / 例外 | ルール/判定回帰 / 判定 / action / surisuri / from / player / surisuri / true / does / 非 / 例外 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveNullBreturnsFalse` | 判定 / emotion / from / unun / slave / null / breturns / false | ルール/判定回帰 / 判定 / emotion / from / unun / slave / null / breturns / false | 良い | - | - |
| `testCheckEmotionFromUnunSlaveNullTargetReturnsFalse` | 判定 / emotion / from / unun / slave / null / target / 戻り / false | ルール/判定回帰 / 判定 / emotion / from / unun / slave / null / target / 戻り / false | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouNydreturnsFalse` | 判定 / wakeup / other / yukkuri / you / nydreturns / false | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / nydreturns / false | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouBuriedReturnsFalse` | 判定 / wakeup / other / yukkuri / you / buried / 戻り / false | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / buried / 戻り / false | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouSleepingReturnsFalse` | 判定 / wakeup / other / yukkuri / you / sleeping / 戻り / false | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckNearParentWithRegisteredMotherDoesNotThrow` | 判定 / near / 親 / with / registered / 母 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentWithRegisteredFatherDoesNotThrow` | 判定 / near / 親 / with / registered / 父 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveUnunSlaveBodyDoesNotThrow` | 判定 / emotion / from / unun / slave / unun / slave / 本体 / does / 非 / 例外 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherBothRemovedReturnsFalse` | do / action / other / both / removed / 戻り / false | ルール/判定回帰 / do / action / other / both / removed / 戻り / false | 良い | - | - |
| `testDoActionOtherDifferentPublicRankNotStealDoesNotThrow` | do / action / other / different / public / rank / 非 / steal / does / 非 / 例外 | ルール/判定回帰 / do / action / other / different / public / rank / 非 / steal / does / 非 / 例外 | 良い | - | - |
| `testConstructorDoesNotThrow` | constructor / does / 非 / 例外 | ルール/判定回帰 / constructor / does / 非 / 例外 | 良い | - | - |
| `testCheckPartnerIsCallingParentsReturnsFalse` | 判定 / 相手 / 状態 / calling / parents / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNearToBirthReturnsFalse` | 判定 / 相手 / near / to / birth / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerHighPriorityEventReturnsFalse` | 判定 / 相手 / high / priority / イベント / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerHasShittakeoutNotExcitingReturnsFalse` | 判定 / 相手 / 有無 / shittakeout / 非 / exciting / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingWithPartnerUnBirthReturnsFalse` | 判定 / 相手 / exciting / with / 相手 / un / birth / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingWithPartnerTargetUnBirthReturnsFalse` | 判定 / 相手 / exciting / with / 相手 / target / un / birth / 戻り / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerWithSpritesLoopFindBodyDoesNotThrow` | 判定 / 相手 / with / sprites / loop / find / 本体 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdjacentBodiesNoRelationReturnsTrue` | do / action / other / adjacent / bodies / なし / relation / 戻り / true | ルール/判定回帰 / do / action / other / adjacent / bodies / なし / relation / 戻り / true | 良い | - | - |
| `testDoActionOtherAdjacentBodiesYouDeadAdultReturnsTrue` | do / action / other / adjacent / bodies / you / 死亡 / adult / 戻り / true | ルール/判定回帰 / do / action / other / adjacent / bodies / you / 死亡 / adult / 戻り / true | 良い | - | - |
| `testDoActionOtherNonAdjacentMoveToTargetDoesNotThrow` | do / action / other / non / adjacent / 移動 / to / target / does / 非 / 例外 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherExcitingAdjacentNotRaperDoesNotThrow` | do / action / other / exciting / adjacent / 非 / raper / does / 非 / 例外 | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerSurisuriTrueNoRelationDoesNotThrow` | 判定 / action / surisuri / from / player / surisuri / true / なし / relation / does / 非 / 例外 | ルール/判定回帰 / 判定 / action / surisuri / from / player / surisuri / true / なし / relation / does / 非 / 例外 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerSurisuriTrueAsPartnerDoesNotThrow` | 判定 / action / surisuri / from / player / surisuri / true / as / 相手 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNullReturnsNone` | 判定 / action / surisuri / from / player / null / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / null / 戻り / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerTargetNotSurisuriReturnsNone` | 判定 / action / surisuri / from / player / target / 非 / surisuri / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / target / 非 / surisuri / 戻り / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyCryStrangerSadAboutHappy` | 判定 / action / surisuri / from / player / envy / cry / stranger / sad / about / happy | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / cry / stranger / sad / about / happy | 良い | - | - |
| `testCheckActionSurisuriFromPlayerWorryConcernPartnerSad` | 判定 / action / surisuri / from / player / worry / concern / 相手 / sad | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerHappyPartnerBothVeryHappy` | 判定 / action / surisuri / from / player / happy / 相手 / both / very / happy | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyChildMotherRelation` | 判定 / action / surisuri / from / player / envy / 子 / 母 / relation | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngerRudeMeVeryHappyTarget` | 判定 / action / surisuri / from / player / envy / anger / rude / me / very / happy / target | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckPartnerExcitingWithPartnerReachesMoveToReturnsTrue` | 判定 / 相手 / exciting / with / 相手 / reaches / 移動 / to / 戻り / true | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerToBodyWithTargetCallsDoActionOtherReturnsTrue` | 判定 / 相手 / to / 本体 / with / target / calls / do / action / other / 戻り / true | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherExcitingPartnersAtSamePosDoesNotThrow` | do / action / other / exciting / partners / at / same / pos / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherTargetDeadMeBabyDoesNotThrow` | do / action / other / target / 死亡 / me / baby / does / 非 / 例外 | ルール/判定回帰 / do / action / other / target / 死亡 / me / baby / does / 非 / 例外 | 良い | - | - |
| `testGatheringYukkuriSquareWithOneBodyDoesNotThrow` | gathering / yukkuri / square / with / one / 本体 / does / 非 / 例外 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriSquareWithOneBodyUpDoesNotThrow` | gathering / yukkuri / square / with / one / 本体 / up / does / 非 / 例外 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriFrontOneBodyDoesNotThrow` | gathering / yukkuri / front / one / 本体 / does / 非 / 例外 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckPartnerExcitingNoPartnerSearchesForPartner` | 判定 / 相手 / exciting / なし / 相手 / searches / for / 相手 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherPartnerPropose` | do / action / other / 相手 / propose | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherRaperAttack` | do / action / other / raper / attack | 感情/イベント/反応回帰 | 良い | - | - |
| `testGatheringYukkuriFrontHeavy` | gathering / yukkuri / front / heavy | ルール/判定回帰 / gathering / yukkuri / front / heavy | 良い | - | - |
| `testCheckPartnerNoExcitingLoopFindsBodyDoesNotThrow` | 判定 / 相手 / なし / exciting / loop / finds / 本体 / does / 非 / 例外 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveTargetNotUnunSlaveDoesNotThrow` | 判定 / emotion / from / unun / slave / target / 非 / unun / slave / does / 非 / 例外 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / target / 非 / unun / slave / does / 非 / 例外 | 良い | - | - |
| `testDoActionOtherRudeMeSamePosDoesNotThrow` | do / action / other / rude / me / same / pos / does / 非 / 例外 | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckPartnerProposeMarriage` | 判定 / 相手 / propose / marriage | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherFuneralEventTrigger` | do / action / other / funeral / イベント / trigger | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherOkazariStealSuccess` | do / action / other / okazari / steal / success | ルール/判定回帰 / do / action / other / okazari / steal / success | 良い | - | - |
| `testDoActionOtherMotherLicksDirtyChild` | do / action / other / 母 / licks / dirty / 子 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNormalPriorityEventReturnsFalseL119` | 判定 / 相手 / normal / priority / イベント / 戻り / false / l119 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingWithShitTakeoutDropsL125` | 判定 / 相手 / exciting / with / shit / takeout / drops / l125 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerOldMoveTargetIsBodyL168` | 判定 / 相手 / old / 移動 / target / 状態 / 本体 / l168 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPredatorCausesPanicL237` | 判定 / 相手 / predator / causes / 恐慌 / l237 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingYouBuriedContinueL250` | 判定 / 相手 / exciting / you / buried / continue / l250 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingYouPackedContinueL258` | 判定 / 相手 / exciting / you / packed / continue / l258 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRaperYouDeadCrushedContinueL264` | 判定 / 相手 / raper / you / 死亡 / crushed / continue / l264 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingRankMismatchContinueL277` | 判定 / 相手 / exciting / rank / mismatch / continue / l277 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingAdultMeVsBabyYouContinueL282` | 判定 / 相手 / exciting / adult / me / vs / baby / you / continue / l282 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerYouExcitingRaperContinueL292` | 判定 / 相手 / you / exciting / raper / continue / l292 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingFoundNullDoOnanismL354` | 判定 / 相手 / exciting / found / null / do / onanism / l354 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingRaperMoveToSukkiriL387` | 判定 / 相手 / exciting / raper / 移動 / to / sukkiri / l387 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingFoundIsIdiotSetCalmL395` | 判定 / 相手 / exciting / found / 状態 / idiot / 設定 / calm / l395 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerHateNoOkazariL421` | 判定 / 相手 / hate / なし / okazari / l421 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFoundIsNeedledMotherToChildL469` | 判定 / 相手 / found / 状態 / needled / 母 / to / 子 / l469 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRaperDeadBodyExcitingMoveToSukkiriL556` | 判定 / 相手 / raper / 死亡 / 本体 / exciting / 移動 / to / sukkiri / l556 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveUnunSlavePartnerAbEmote5ReturnsTrue` | 判定 / emotion / from / unun / slave / unun / slave / 相手 / ab / emote5 / 戻り / true | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveUnunSlaveChildFatherReturnsTrue` | 判定 / emotion / from / unun / slave / unun / slave / 子 / 父 / 戻り / true | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveUnunSlaveChildMotherReturnsTrue` | 判定 / emotion / from / unun / slave / unun / slave / 子 / 母 / 戻り / true | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentElderSisterL1961` | 判定 / near / 親 / elder / 姉妹 / l1961 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentCallingParentsSleepingL1973` | 判定 / near / 親 / calling / parents / sleeping / l1973 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentDirtyChildNearParentL1977` | 判定 / near / 親 / dirty / 子 / near / 親 / l1977 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentDirtyChildFarFromParentL1981` | 判定 / near / 親 / dirty / 子 / far / from / 親 / l1981 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentFarFromParentMoveToL2004` | 判定 / near / 親 / far / from / 親 / 移動 / to / l2004 | 家族関係/家族行動回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineWithOneFarBodyL1851` | gathering / yukkuri / back / line / with / one / far / 本体 / l1851 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineCloseDistanceL1866` | gathering / yukkuri / back / line / close / distance / l1866 | ルール/判定回帰 / gathering / yukkuri / back / line / close / distance / l1866 | 良い | - | - |
| `testGatheringYukkuriBackLineDeadBodyInListSkippedL1825` | gathering / yukkuri / back / line / 死亡 / 本体 / in / list / skipped / l1825 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineAlreadyCloseBodySkippedL1842` | gathering / yukkuri / back / line / already / close / 本体 / skipped / l1842 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineWithEventSameEventL1853` | gathering / yukkuri / back / line / with / イベント / same / イベント / l1853 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherPremovedReturnsFalseL621` | do / action / other / premoved / 戻り / false / l621 | ルール/判定回帰 / do / action / other / premoved / 戻り / false / l621 | 良い | - | - |
| `testDoActionOtherPfloatingReturnsFalseL627` | do / action / other / pfloating / 戻り / false / l627 | ルール/判定回帰 / do / action / other / pfloating / 戻り / false / l627 | 良い | - | - |
| `testDoActionOtherBnydReturnsFalseL632` | do / action / other / bnyd / 戻り / false / l632 | ルール/判定回帰 / do / action / other / bnyd / 戻り / false / l632 | 良い | - | - |
| `testDoActionOtherRankMismatchNoStealReturnsFalseL641` | do / action / other / rank / mismatch / なし / steal / 戻り / false / l641 | ルール/判定回帰 / do / action / other / rank / mismatch / なし / steal / 戻り / false / l641 | 良い | - | - |
| `testDoActionOtherRankMismatchWithStealContinuesToContactL642` | do / action / other / rank / mismatch / with / steal / continues / to / contact / l642 | ルール/判定回帰 / do / action / other / rank / mismatch / with / steal / continues / to / contact / l642 | 良い | - | - |
| `testDoActionOtherDeadExcitingRaperDoRapeL663` | do / action / other / 死亡 / exciting / raper / do / rape / l663 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherDeadExcitingNotRaperDoOnanismL668` | do / action / other / 死亡 / exciting / 非 / raper / do / onanism / l668 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherAdultDeadPartnerSadnessForPartnerL687` | do / action / other / adult / 死亡 / 相手 / sadness / for / 相手 / l687 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildDeadParentSadnessForParentL699` | do / action / other / 子 / 死亡 / 親 / sadness / for / 親 / l699 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterDeadYouIsElderSisterL712` | do / action / other / 姉妹 / 死亡 / you / 状態 / elder / 姉妹 / l712 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterDeadYouIsYoungerSisterL714` | do / action / other / 姉妹 / 死亡 / you / 状態 / younger / 姉妹 / l714 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherExcitingAdultPartnerDoSukkiriL782` | do / action / other / exciting / adult / 相手 / do / sukkiri / l782 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherExcitingAdultNoPartnerDoOnanismL785` | do / action / other / exciting / adult / なし / 相手 / do / onanism / l785 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNeedledChildDoGuriguriL801` | do / action / other / needled / 子 / do / guriguri / l801 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNeedledPartnerDoGuriguriL805` | do / action / other / needled / 相手 / do / guriguri / l805 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherFindSickAvoidMoldEventL818` | do / action / other / find / 病気 / avoid / mold / イベント / l818 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherParentGiveFoodToChildL848` | do / action / other / 親 / give / food / to / 子 / l848 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherPartnerSurisuriL869` | do / action / other / 相手 / surisuri / l869 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildSkinshipL887` | do / action / other / 子 / skinship / l887 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipL894` | do / action / other / 姉妹 / skinship / l894 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNonContactMoveToL928` | do / action / other / non / contact / 移動 / to / l928 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNullBreturnsNone` | 判定 / action / surisuri / from / player / null / breturns / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / null / breturns / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNullTargetReturnsNone` | 判定 / action / surisuri / from / player / null / target / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / null / target / 戻り / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerNotSurisuriReturnsNone_4301` | 判定 / action / surisuri / from / player / 非 / surisuri / 戻り / none / 4301 | ルール/判定回帰 / 判定 / action / surisuri / from / player / 非 / surisuri / 戻り / none / 4301 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerRandNotZeroReturnsNone` | 判定 / action / surisuri / from / player / rand / 非 / zero / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / rand / 非 / zero / 戻り / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerBnydReturnsNone` | 判定 / action / surisuri / from / player / bnyd / 戻り / none | ルール/判定回帰 / 判定 / action / surisuri / from / player / bnyd / 戻り / none | 良い | - | - |
| `testCheckActionSurisuriFromPlayerJoyMotherReturnsGo` | 判定 / action / surisuri / from / player / joy / 母 / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / joy / 母 / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerJoyPartnarReturnsGo` | 判定 / action / surisuri / from / player / joy / partnar / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / joy / partnar / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerJoyChildMotherReturnsGo` | 判定 / action / surisuri / from / player / joy / 子 / 母 / 戻り / go | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyYoungSisterReturnsGo` | 判定 / action / surisuri / from / player / envy / young / 姉妹 / 戻り / go | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerJoyOtherRudeReturnsWait` | 判定 / action / surisuri / from / player / joy / other / rude / 戻り / wait | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyCryStrangerReturnsWait` | 判定 / action / surisuri / from / player / envy / cry / stranger / 戻り / wait | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / cry / stranger / 戻り / wait | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryPartnerReturnsWait` | 判定 / action / surisuri / from / player / envy / angry / 相手 / 戻り / wait | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerFearConcernAndPainReturnsWait` | 判定 / action / surisuri / from / player / fear / concern / and / pain / 戻り / wait | ルール/判定回帰 / 判定 / action / surisuri / from / player / fear / concern / and / pain / 戻り / wait | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernMotherWithPainReturnsGo` | 判定 / action / surisuri / from / player / concern / 母 / with / pain / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / 母 / with / pain / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernMotherNoPainReturnsGo` | 判定 / action / surisuri / from / player / concern / 母 / なし / pain / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / 母 / なし / pain / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerMercyStrangerReturnsGo` | 判定 / action / surisuri / from / player / mercy / stranger / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / mercy / stranger / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerJoyElderSisterReturnsGo` | 判定 / action / surisuri / from / player / joy / elder / 姉妹 / 戻り / go | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExistingPartnerGoToPartnerL180` | 判定 / 相手 / existing / 相手 / go / to / 相手 / l180 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPheromoneFoundHasPheromoneL310` | 判定 / 相手 / pheromone / found / 有無 / pheromone / l310 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadFoundAdultMeParentMoveToBodyL569` | 判定 / 相手 / 死亡 / found / adult / me / 親 / 移動 / to / 本体 / l569 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadFoundBabyMeSisterMoveToBodyL581` | 判定 / 相手 / 死亡 / found / baby / me / 姉妹 / 移動 / to / 本体 / l581 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadFoundAdultMeNotParentNotPartnerLookToL575` | 判定 / 相手 / 死亡 / found / adult / me / 非 / 親 / 非 / 相手 / look / to / l575 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRandomPartnerApproachL511` | 判定 / 相手 / random / 相手 / approach / l511 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRandomChildApproachL520` | 判定 / 相手 / random / 子 / approach / l520 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRandomSisterApproachL529` | 判定 / 相手 / random / 姉妹 / approach / l529 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRandomFamilyApproachL538` | 判定 / 相手 / random / 家族 / approach / l538 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDirtyChildMoveToParentL497` | 判定 / 相手 / dirty / 子 / 移動 / to / 親 / l497 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDirtySelfMoveToParentL502` | 判定 / 相手 / dirty / self / 移動 / to / 親 / l502 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerOkazariStealMoveL453` | 判定 / 相手 / okazari / steal / 移動 / l453 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNydchildAdultFoolHateNoOkazariL832` | do / action / other / nydchild / adult / fool / hate / なし / okazari / l832 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherForceExcitingDoSukkiriL791` | do / action / other / force / exciting / do / sukkiri / l791 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherNeedledSisterDoGuriguriL809` | do / action / other / needled / 姉妹 / do / guriguri / l809 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherPartnerNoSurisuriRndfalseL866` | do / action / other / 相手 / なし / surisuri / rndfalse / l866 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipSmartPeroperoL895` | do / action / other / 姉妹 / skinship / smart / peropero / l895 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildBabyDirtyPeroperoByMotherL878` | do / action / other / 子 / baby / dirty / peropero / by / 母 / l878 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerIdiotReturnsNoneL988` | 判定 / action / surisuri / from / player / idiot / 戻り / none / l988 | ルール/判定回帰 / 判定 / action / surisuri / from / player / idiot / 戻り / none / l988 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyEldersisterReturnsGol1105` | 判定 / action / surisuri / from / player / envy / eldersister / 戻り / gol1105 | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / eldersister / 戻り / gol1105 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryParentReturnsWaitl1140` | 判定 / action / surisuri / from / player / envy / angry / 親 / 戻り / waitl1140 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryChildFatherReturnsWaitl1154` | 判定 / action / surisuri / from / player / envy / angry / 子 / 父 / 戻り / waitl1154 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryChildMotherReturnsWaitl1161` | 判定 / action / surisuri / from / player / envy / angry / 子 / 母 / 戻り / waitl1161 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryEldersisterReturnsWaitl1168` | 判定 / action / surisuri / from / player / envy / angry / eldersister / 戻り / waitl1168 | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / angry / eldersister / 戻り / waitl1168 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryYoungsisterReturnsWaitl1175` | 判定 / action / surisuri / from / player / envy / angry / youngsister / 戻り / waitl1175 | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / angry / youngsister / 戻り / waitl1175 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernPartnarWithPainReturnsGol1234` | 判定 / action / surisuri / from / player / concern / partnar / with / pain / 戻り / gol1234 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / partnar / with / pain / 戻り / gol1234 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernChildFatherWithPainReturnsGol1241` | 判定 / action / surisuri / from / player / concern / 子 / 父 / with / pain / 戻り / gol1241 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernChildMotherWithPainReturnsGol1248` | 判定 / action / surisuri / from / player / concern / 子 / 母 / with / pain / 戻り / gol1248 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernEldersisterWithPainReturnsGol1255` | 判定 / action / surisuri / from / player / concern / eldersister / with / pain / 戻り / gol1255 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / eldersister / with / pain / 戻り / gol1255 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernYoungsisterWithPainReturnsGol1262` | 判定 / action / surisuri / from / player / concern / youngsister / with / pain / 戻り / gol1262 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / youngsister / with / pain / 戻り / gol1262 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernChildFatherNoPainReturnsGol1298` | 判定 / action / surisuri / from / player / concern / 子 / 父 / なし / pain / 戻り / gol1298 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernChildMotherNoPainReturnsGol1305` | 判定 / action / surisuri / from / player / concern / 子 / 母 / なし / pain / 戻り / gol1305 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernEldersisterNoPainReturnsGol1312` | 判定 / action / surisuri / from / player / concern / eldersister / なし / pain / 戻り / gol1312 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / eldersister / なし / pain / 戻り / gol1312 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernYoungsisterNoPainReturnsGol1319` | 判定 / action / surisuri / from / player / concern / youngsister / なし / pain / 戻り / gol1319 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / youngsister / なし / pain / 戻り / gol1319 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveIdiotReturnsFalseL1891` | 判定 / emotion / from / unun / slave / idiot / 戻り / false / l1891 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / idiot / 戻り / false / l1891 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveNydReturnsFalseL1895` | 判定 / emotion / from / unun / slave / 非ゆっくり症 / 戻り / false / l1895 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / 非ゆっくり症 / 戻り / false / l1895 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveFatherReturnsTrueL1911` | 判定 / emotion / from / unun / slave / 父 / 戻り / true / l1911 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / 父 / 戻り / true / l1911 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveChildFatherReturnsTrueL1917` | 判定 / emotion / from / unun / slave / 子 / 父 / 戻り / true / l1917 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveEldersisterReturnsTrueL1923` | 判定 / emotion / from / unun / slave / eldersister / 戻り / true / l1923 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / eldersister / 戻り / true / l1923 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveYoungsisterReturnsTrueL1926` | 判定 / emotion / from / unun / slave / youngsister / 戻り / true / l1926 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / youngsister / 戻り / true / l1926 | 良い | - | - |
| `testCheckEmotionFromUnunSlaveOtherReturnsTrueL1929` | 判定 / emotion / from / unun / slave / other / 戻り / true / l1929 | ルール/判定回帰 / 判定 / emotion / from / unun / slave / other / 戻り / true / l1929 | 良い | - | - |
| `testGatheringYukkuriSquareLeftDirectionL1660` | gathering / yukkuri / square / left / direction / l1660 | ルール/判定回帰 / gathering / yukkuri / square / left / direction / l1660 | 良い | - | - |
| `testGatheringYukkuriSquareRightDirectionL1664` | gathering / yukkuri / square / right / direction / l1664 | ルール/判定回帰 / gathering / yukkuri / square / right / direction / l1664 | 良い | - | - |
| `testGatheringYukkuriSquareUpEvenRowL1701` | gathering / yukkuri / square / up / even / row / l1701 | ルール/判定回帰 / gathering / yukkuri / square / up / even / row / l1701 | 良い | - | - |
| `testGatheringYukkuriSquareLeftEvenRowL1729` | gathering / yukkuri / square / left / even / row / l1729 | ルール/判定回帰 / gathering / yukkuri / square / left / even / row / l1729 | 良い | - | - |
| `testGatheringYukkuriSquareRightEvenRowL1743` | gathering / yukkuri / square / right / even / row / l1743 | ルール/判定回帰 / gathering / yukkuri / square / right / even / row / l1743 | 良い | - | - |
| `testGatheringYukkuriSquareUp3BodiesOddRowL1701` | gathering / yukkuri / square / up3 / bodies / odd / row / l1701 | ルール/判定回帰 / gathering / yukkuri / square / up3 / bodies / odd / row / l1701 | 良い | - | - |
| `testGatheringYukkuriSquareDown3BodiesOddRowL1715` | gathering / yukkuri / square / down3 / bodies / odd / row / l1715 | ルール/判定回帰 / gathering / yukkuri / square / down3 / bodies / odd / row / l1715 | 良い | - | - |
| `testGatheringYukkuriSquareLeft3BodiesOddRowL1729` | gathering / yukkuri / square / left3 / bodies / odd / row / l1729 | ルール/判定回帰 / gathering / yukkuri / square / left3 / bodies / odd / row / l1729 | 良い | - | - |
| `testGatheringYukkuriSquareRight3BodiesOddRowL1743` | gathering / yukkuri / square / right3 / bodies / odd / row / l1743 | ルール/判定回帰 / gathering / yukkuri / square / right3 / bodies / odd / row / l1743 | 良い | - | - |
| `testGatheringYukkuriSquareUp4BodiesBkiNotBmovedL1702` | gathering / yukkuri / square / up4 / bodies / bki / 非 / bmoved / l1702 | ルール/判定回帰 / gathering / yukkuri / square / up4 / bodies / bki / 非 / bmoved / l1702 | 良い | - | - |
| `testGatheringYukkuriSquareLeft4BodiesBkiNotBmovedL1731` | gathering / yukkuri / square / left4 / bodies / bki / 非 / bmoved / l1731 | ルール/判定回帰 / gathering / yukkuri / square / left4 / bodies / bki / 非 / bmoved / l1731 | 良い | - | - |
| `testGatheringYukkuriSquareRight4BodiesBkiNotBmovedL1745` | gathering / yukkuri / square / right4 / bodies / bki / 非 / bmoved / l1745 | ルール/判定回帰 / gathering / yukkuri / square / right4 / bodies / bki / 非 / bmoved / l1745 | 良い | - | - |
| `testGatheringYukkuriSquareNullBodyInArrayL1609` | gathering / yukkuri / square / null / 本体 / in / array / l1609 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriSquareFlyingBodyL1621` | gathering / yukkuri / square / flying / 本体 / l1621 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriSquareWithEventMoveToEventL1690` | gathering / yukkuri / square / with / イベント / 移動 / to / イベント / l1690 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriSquareDifferentEventSkippedL1612` | gathering / yukkuri / square / different / イベント / skipped / l1612 | 感情/イベント/反応回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineNullBodyInListL1818` | gathering / yukkuri / back / line / null / 本体 / in / list / l1818 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineFlyingBodyL1836` | gathering / yukkuri / back / line / flying / 本体 / l1836 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineDifferentEventSkippedL1828` | gathering / yukkuri / back / line / different / イベント / skipped / l1828 | 感情/イベント/反応回帰 | 良い | - | - |
| `testCreateActiveFianceeListDeadBodySkippedL1419` | 生成 / active / fiancee / list / 死亡 / 本体 / skipped / l1419 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveFianceeListRemovedBodySkippedL1423` | 生成 / active / fiancee / list / removed / 本体 / skipped / l1423 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveFianceeListUnBirthBodySkippedL1427` | 生成 / active / fiancee / list / un / birth / 本体 / skipped / l1427 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveFianceeListHasChildrenSkippedL1431` | 生成 / active / fiancee / list / 有無 / children / skipped / l1431 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveFianceeListRankMismatchSkippedL1435` | 生成 / active / fiancee / list / rank / mismatch / skipped / l1435 | ルール/判定回帰 / 生成 / active / fiancee / list / rank / mismatch / skipped / l1435 | 良い | - | - |
| `testCreateActiveFianceeListFindSickSkippedL1443` | 生成 / active / fiancee / list / find / 病気 / skipped / l1443 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveFianceeListAgeTooHighSkippedL1447` | 生成 / active / fiancee / list / age / too / high / skipped / l1447 | ルール/判定回帰 / 生成 / active / fiancee / list / age / too / high / skipped / l1447 | 良い | - | - |
| `testCreateActiveFianceeListPartnerExists50percentSkipL1451` | 生成 / active / fiancee / list / 相手 / exists50percent / skip / l1451 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveFianceeListPartnerExistsNotSkippedL1451` | 生成 / active / fiancee / list / 相手 / exists / 非 / skipped / l1451 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListNullChildSkippedL1477` | 生成 / active / 子 / list / null / 子 / skipped / l1477 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListDeadChildSkippedL1481` | 生成 / active / 子 / list / 死亡 / 子 / skipped / l1481 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListRemovedChildSkippedL1485` | 生成 / active / 子 / list / removed / 子 / skipped / l1485 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListUnBirthChildSkippedL1489` | 生成 / active / 子 / list / un / birth / 子 / skipped / l1489 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListTakenChildSkippedL1493` | 生成 / active / 子 / list / taken / 子 / skipped / l1493 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListChildHasChildrenSkippedL1497` | 生成 / active / 子 / list / 子 / 有無 / children / skipped / l1497 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListUnunSlaveChildSkippedL1501` | 生成 / active / 子 / list / unun / slave / 子 / skipped / l1501 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListBirthMessageForcedChildSkipped` | 生成 / active / 子 / list / birth / メッセージ / forced / 子 / skipped | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListBirthEventBlockedChildSkipped` | 生成 / active / 子 / list / birth / イベント / blocked / 子 / skipped | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListNydchildSkippedL1504` | 生成 / active / 子 / list / nydchild / skipped / l1504 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListAdultChildBstateFalseSkippedL1509` | 生成 / active / 子 / list / adult / 子 / bstate / false / skipped / l1509 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveChildListAdultChildBstateTrueSkippedL1514` | 生成 / active / 子 / list / adult / 子 / bstate / true / skipped / l1514 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPartnerRandomApproachL510` | 判定 / 相手 / 相手 / random / approach / l510 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerChildRandomApproachL519` | 判定 / 相手 / 子 / random / approach / l519 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerSisterRandomApproachL528` | 判定 / 相手 / 姉妹 / random / approach / l528 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFamilyRandomApproachL537` | 判定 / 相手 / 家族 / random / approach / l537 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNeedledChildMotherComfortL467` | 判定 / 相手 / needled / 子 / 母 / comfort / l467 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyPartnerL568` | 判定 / 相手 / 死亡 / 本体 / 相手 / l568 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyParentAdultL568` | 判定 / 相手 / 死亡 / 本体 / 親 / adult / l568 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherRankMismatchReturnsFalseL637` | do / action / other / rank / mismatch / 戻り / false / l637 | ルール/判定回帰 / do / action / other / rank / mismatch / 戻り / false / l637 | 良い | - | - |
| `testDoActionOtherFindSickAvoidMoldL817` | do / action / other / find / 病気 / avoid / mold / l817 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherFindSickBothSickL817FalseL822False` | do / action / other / find / 病気 / both / 病気 / l817 / false / l822 / false | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherFindSickL822PfindSickAvoidMoldEvent` | do / action / other / find / 病気 / l822 / pfind / 病気 / avoid / mold / イベント | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testDoActionOtherNeedledChildMotherGuriguriL799` | do / action / other / needled / 子 / 母 / guriguri / l799 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherParentChildSkinshipL852` | do / action / other / 親 / 子 / skinship / l852 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherPartnerSurisuriL866` | do / action / other / 相手 / surisuri / l866 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildParentSkinshipL873_6646` | do / action / other / 子 / 親 / skinship / l873 / 6646 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipL891` | do / action / other / 姉妹 / skinship / l891 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipSmartChildDirtyPeroperoL895` | do / action / other / 姉妹 / skinship / smart / 子 / dirty / peropero / l895 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipElderSisterDamagedL900` | do / action / other / 姉妹 / skinship / elder / 姉妹 / damaged / l900 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterSkinshipYoungerSisterDamagedL902` | do / action / other / 姉妹 / skinship / younger / 姉妹 / damaged / l902 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyAdultNotFamilyLookToL573` | 判定 / 相手 / 死亡 / 本体 / adult / 非 / 家族 / look / to / l573 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyNonAdultSisterMoveToBodyL580` | 判定 / 相手 / 死亡 / 本体 / non / adult / 姉妹 / 移動 / to / 本体 / l580 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyNonAdultUnrelatedRunAwayL586` | 判定 / 相手 / 死亡 / 本体 / non / adult / unrelated / run / away / l586 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerSuperShitheadNextBoolFalseMoveToSukkiriL404` | 判定 / 相手 / super / shithead / next / bool / false / 移動 / to / sukkiri / l404 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNextBoolTrueFoolParentSkipChildL483` | 判定 / 相手 / next / bool / true / fool / 親 / skip / 子 / l483 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNextBoolTrueDirtyChildApproachFamilyL500` | 判定 / 相手 / next / bool / true / dirty / 子 / approach / 家族 / l500 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNeedledPartnerMoveToBodyL473` | 判定 / 相手 / needled / 相手 / 移動 / to / 本体 / l473 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNextBoolTrueDirtyAdultChildParentComfortL495` | 判定 / 相手 / next / bool / true / dirty / adult / 子 / 親 / comfort / l495 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherFoolAdultNydChildHateNoOkazariEventL831` | do / action / other / fool / adult / 非ゆっくり症 / 子 / hate / なし / okazari / イベント / l831 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentFarFromParentMoveToL2000` | 判定 / near / 親 / far / from / 親 / 移動 / to / l2000 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentNearParentNoOpL1988` | 判定 / near / 親 / near / 親 / なし / op / l1988 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNonAdjacentFlyingTypeMoveToL926` | do / action / other / non / adjacent / flying / type / 移動 / to / l926 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckPartnerAntsOnPartnerMoveToBodyL491` | 判定 / 相手 / ants / on / 相手 / 移動 / to / 本体 / l491 | 家族関係/家族行動回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineFarBodyBresultFalseL1863` | gathering / yukkuri / back / line / far / 本体 / bresult / false / l1863 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testGatheringYukkuriSquareWithEvent4BodiesMoveToEventL1771` | gathering / yukkuri / square / with / event4 / bodies / 移動 / to / イベント / l1771 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckPartnerSameAgeNoOkazariL328` | 判定 / 相手 / same / age / なし / okazari / l328 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNeedledChildGuriGuriL799` | do / action / other / needled / 子 / guri / guri / l799 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdultParentChildSkinshipL852` | do / action / other / adult / 親 / 子 / skinship / l852 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildParentSkinshipL873` | do / action / other / 子 / 親 / skinship / l873 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNonAdjacentDistCloseL932` | do / action / other / non / adjacent / dist / close / l932 | ルール/判定回帰 / do / action / other / non / adjacent / dist / close / l932 | 良い | - | - |
| `testCheckPartnerRaperSukkiriL177` | 判定 / 相手 / raper / sukkiri / l177 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherChildParentParentDamagedL881` | do / action / other / 子 / 親 / 親 / damaged / l881 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherSisterDamagedL907` | do / action / other / 姉妹 / damaged / l907 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAntsOnPartnerL839` | do / action / other / ants / on / 相手 / l839 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerSurisuriFromPlayerMotherGoActionL438` | 判定 / 相手 / surisuri / from / player / 母 / go / action / l438 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerKillPredatorEventSetAngryL222` | 判定 / 相手 / kill / predator / イベント / 設定 / angry / l222 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerSakuyaNotAfraidOfRemiryaL213` | 判定 / 相手 / sakuya / 非 / afraid / of / remirya / l213 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerIdiotDeadStrangerSkipL286` | 判定 / 相手 / idiot / 死亡 / stranger / skip / l286 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingRaperInMapSkipL291` | 判定 / 相手 / exciting / raper / in / map / skip / l291 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNearlyBuriedNoOkazariSkipL304` | 判定 / 相手 / nearly / buried / なし / okazari / skip / l304 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyStrangerAdultLookToL573` | 判定 / 相手 / 死亡 / 本体 / stranger / adult / look / to / l573 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyRaperAdultL565FalseL600False` | 判定 / 相手 / 死亡 / 本体 / raper / adult / l565 / false / l600 / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPredatorAdultDeadNonPredL573FalseL597False` | 判定 / 相手 / predator / adult / 死亡 / non / pred / l573 / false / l597 / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPredatorBabyDeadNonPredL580FalseL586False` | 判定 / 相手 / predator / baby / 死亡 / non / pred / l580 / false / l586 / false | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerPredatorInRangeSetPanicL233` | 判定 / 相手 / predator / in / range / 設定 / 恐慌 / l233 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFoolParentSkipApproachL483` | 判定 / 相手 / fool / 親 / skip / approach / l483 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNeedledAdultToAdultPartnerL799miss` | do / action / other / needled / adult / to / adult / 相手 / l799miss | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNeedledBabyStrangerAdultL799falseL803false` | do / action / other / needled / baby / stranger / adult / l799false / l803false | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckPartnerNeedledChildParentApproachL467` | 判定 / 相手 / needled / 子 / 親 / approach / l467 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFlyingBodyFindsTargetL365` | 判定 / 相手 / flying / 本体 / finds / target / l365 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerRudeBodyNoOkazariStealCandidateL331` | 判定 / 相手 / rude / 本体 / なし / okazari / steal / candidate / l331 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryStrangerReturnsWaitl1182` | 判定 / action / surisuri / from / player / envy / angry / stranger / 戻り / waitl1182 | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / angry / stranger / 戻り / waitl1182 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyCryElderSisterL1067` | 判定 / action / surisuri / from / player / envy / cry / elder / 姉妹 / l1067 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyCryYoungerSisterL1074` | 判定 / action / surisuri / from / player / envy / cry / younger / 姉妹 / l1074 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerCallingParentsReturnsFalseL189` | 判定 / 相手 / calling / parents / 戻り / false / l189 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNoTargetExcitingOnanismL352` | 判定 / 相手 / なし / target / exciting / onanism / l352 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerBisUnBirthReturnsFalseL374` | 判定 / 相手 / bis / un / birth / 戻り / false / l374 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFoundIsUnBirthReturnsFalseL378` | 判定 / 相手 / found / 状態 / un / birth / 戻り / false / l378 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingFoundIdiotSetCalmL393` | 判定 / 相手 / exciting / found / idiot / 設定 / calm / l393 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingVeryRudeMoveToSukkiriL385` | 判定 / 相手 / exciting / very / rude / 移動 / to / sukkiri / l385 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingVeryRudeRapeOnlyL402` | 判定 / 相手 / exciting / very / rude / rape / only / l402 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingDeadBodySkipContinueL271` | 判定 / 相手 / exciting / 死亡 / 本体 / skip / continue / l271 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingAdultSkipsBabyL282` | 判定 / 相手 / exciting / adult / skips / baby / l282 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerExcitingRaperSkipsRaperL264` | 判定 / 相手 / exciting / raper / skips / raper / l264 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerBaryStateAllSkipContinueL301` | 判定 / 相手 / bary / state / all / skip / continue / l301 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDifferentRankReturnFalseL460` | 判定 / 相手 / different / rank / 戻り / false / l460 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDeadBodyRandNotZeroReturnFalseL560` | 判定 / 相手 / 死亡 / 本体 / rand / 非 / zero / 戻り / false / l560 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerYouFloatingCannotFlySkipContinueL205` | 判定 / 相手 / you / floating / cannot / fly / skip / continue / l205 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerUnunSlaveEnvyTargetCheckEmotionReturnsTrueL445` | 判定 / 相手 / unun / slave / envy / target / 判定 / emotion / 戻り / true / l445 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerNeedledChildAdultParentMoveToBodyL467` | 判定 / 相手 / needled / 子 / adult / 親 / 移動 / to / 本体 / l467 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFoolParentSkipsChildL483` | 判定 / 相手 / fool / 親 / skips / 子 / l483 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDirtyChildAdultParentMoveToBodyL495` | 判定 / 相手 / dirty / 子 / adult / 親 / 移動 / to / 本体 / l495 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherDifferentRankNotRaperClearActionsL637` | do / action / other / different / rank / 非 / raper / 解除 / actions / l637 | 感情/イベント/反応回帰 | 良い | - | - |
| `testCheckPartnerNeedledAdultNoRelationIsMotherEvaluatedL467` | 判定 / 相手 / needled / adult / なし / relation / 状態 / 母 / evaluated / l467 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerFoolAdultNoRelationIsMotherEvaluatedL483` | 判定 / 相手 / fool / adult / なし / relation / 状態 / 母 / evaluated / l483 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerDirtyAdultNoRelationIsMotherEvaluatedL495` | 判定 / 相手 / dirty / adult / なし / relation / 状態 / 母 / evaluated / l495 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherDifferentRankRaperExcitingIsExcitingEvaluatedL637` | do / action / other / different / rank / raper / exciting / 状態 / exciting / evaluated / l637 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherRaperExcitingLessXsetDirectionRightL767` | do / action / other / raper / exciting / less / xset / direction / right / l767 | 感情/イベント/反応回帰 | 良い | - | - |
| `testDoActionOtherAdultFoolParentBabyNoNydl827Body` | do / action / other / adult / fool / 親 / baby / なし / nydl827 / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdultFoolParentBabyNydboolTrueL829Body` | do / action / other / adult / fool / 親 / baby / nydbool / true / l829 / 本体 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdultFoolNoRelationIsMotherEvaluatedL827` | do / action / other / adult / fool / なし / relation / 状態 / 母 / evaluated / l827 | ルール/判定回帰 / do / action / other / adult / fool / なし / relation / 状態 / 母 / evaluated / l827 | 良い | - | - |
| `testDoActionOtherAdultAverageParentBabyChildPeroperoL857` | do / action / other / adult / average / 親 / baby / 子 / peropero / l857 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdultAverageParentBabyChildSurisuriL861` | do / action / other / adult / average / 親 / baby / 子 / surisuri / l861 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherAdultNoRelationIsParentEvaluatedL852` | do / action / other / adult / なし / relation / 状態 / 親 / evaluated / l852 | 家族関係/家族行動回帰 | 良い | - | - |
| `testDoActionOtherNonAdjacentTargetBindStayCalledL934` | do / action / other / non / adjacent / target / bind / stay / called / l934 | ルール/判定回帰 / do / action / other / non / adjacent / target / bind / stay / called / l934 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerHappyFatherReturnsGo` | 判定 / action / surisuri / from / player / happy / 父 / 戻り / go | ルール/判定回帰 / 判定 / action / surisuri / from / player / happy / 父 / 戻り / go | 良い | - | - |
| `testCheckActionSurisuriFromPlayerHappyPartnerReturnsGo` | 判定 / action / surisuri / from / player / happy / 相手 / 戻り / go | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerHappyElderSisterReturnsGo` | 判定 / action / surisuri / from / player / happy / elder / 姉妹 / 戻り / go | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerSadEnvyStrangerWaitL1084` | 判定 / action / surisuri / from / player / sad / envy / stranger / wait / l1084 | ルール/判定回帰 / 判定 / action / surisuri / from / player / sad / envy / stranger / wait / l1084 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerSadEnvyElderSisterGoL1103` | 判定 / action / surisuri / from / player / sad / envy / elder / 姉妹 / go / l1103 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerEnvyAngryFatherWaitL1140` | 判定 / action / surisuri / from / player / envy / angry / 父 / wait / l1140 | ルール/判定回帰 / 判定 / action / surisuri / from / player / envy / angry / 父 / wait / l1140 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerFearOnlyStrangerWaitL1196` | 判定 / action / surisuri / from / player / fear / only / stranger / wait / l1196 | ルール/判定回帰 / 判定 / action / surisuri / from / player / fear / only / stranger / wait / l1196 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerWorrySadFearFatherGoL1221` | 判定 / action / surisuri / from / player / worry / sad / fear / 父 / go / l1221 | ルール/判定回帰 / 判定 / action / surisuri / from / player / worry / sad / fear / 父 / go / l1221 | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouNydskipContinue` | 判定 / wakeup / other / yukkuri / you / nydskip / continue | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / nydskip / continue | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouUnunSlaveMeNoneSkipContinue` | 判定 / wakeup / other / yukkuri / you / unun / slave / me / none / skip / continue | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / unun / slave / me / none / skip / continue | 良い | - | - |
| `testCheckNearParentWithParentMovesToParent` | 判定 / near / 親 / with / 親 / moves / to / 親 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentAdultBodyEarlyReturn` | 判定 / near / 親 / adult / 本体 / early / 戻り | 家族関係/家族行動回帰 | 良い | - | - |
| `testGatheringYukkuriBackLineWithBodyExecutesLoop` | gathering / yukkuri / back / line / with / 本体 / executes / loop | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCheckActionSurisuriFromPlayerConcernPartnarNoPainReturnsGol1291` | 判定 / action / surisuri / from / player / concern / partnar / なし / pain / 戻り / gol1291 | ルール/判定回帰 / 判定 / action / surisuri / from / player / concern / partnar / なし / pain / 戻り / gol1291 | 良い | - | - |
| `testCheckNearParentNoParentReturnEarlyL1964` | 判定 / near / 親 / なし / 親 / 戻り / early / l1964 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentDirtyChildParentClosePeroperoL1978` | 判定 / near / 親 / dirty / 子 / 親 / close / peropero / l1978 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentDirtyChildParentFarMoveToL1981` | 判定 / near / 親 / dirty / 子 / 親 / far / 移動 / to / l1981 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentElderSisterAsParentL1961` | 判定 / near / 親 / elder / 姉妹 / as / 親 / l1961 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckNearParentCallingParentsParentSleepingWakeupL1972` | 判定 / near / 親 / calling / parents / 親 / sleeping / wakeup / l1972 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouRemovedContinueL2022` | 判定 / wakeup / other / yukkuri / you / removed / continue / l2022 | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / removed / continue / l2022 | 良い | - | - |
| `testCheckWakeupOtherYukkuriYouFarAwayL2032False` | 判定 / wakeup / other / yukkuri / you / far / away / l2032 / false | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / you / far / away / l2032 / false | 良い | - | - |
| `testDoActionOtherUnunSlaveStealRankSwapL741` | do / action / other / unun / slave / steal / rank / swap / l741 | ルール/判定回帰 / do / action / other / unun / slave / steal / rank / swap / l741 | 良い | - | - |
| `testCheckPartnerSecondClosestRndtrueL322` | 判定 / 相手 / second / closest / rndtrue / l322 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCheckPartnerOkazariPheromoneL335` | 判定 / 相手 / okazari / pheromone / l335 | 家族関係/家族行動回帰 | 良い | - | - |
| `testCreateActiveFianceeListSingleBodyReturnsNullL1397` | 生成 / active / fiancee / list / single / 本体 / 戻り / null / l1397 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveFianceeListDisorderBodySkippedL1440` | 生成 / active / fiancee / list / disorder / 本体 / skipped / l1440 | 移動/代謝/ダメージ回帰 | 良い | - | - |
| `testCreateActiveChildListNotAllrightChildSkippedL1505` | 生成 / active / 子 / list / 非 / allright / 子 / skipped / l1505 | 家族関係/家族行動回帰 | 良い | - | - |
| `testGatheringYukkuriSquareLeftEdgeCenterClampX0L1670` | gathering / yukkuri / square / left / edge / center / 範囲補正 / x0 / l1670 | ルール/判定回帰 / gathering / yukkuri / square / left / edge / center / 範囲補正 / x0 / l1670 | 良い | - | - |
| `testGatheringYukkuriSquareRightEdgeCenterClampXmaxL1672` | gathering / yukkuri / square / right / edge / center / 範囲補正 / xmax / l1672 | ルール/判定回帰 / gathering / yukkuri / square / right / edge / center / 範囲補正 / xmax / l1672 | 良い | - | - |
| `testGatheringYukkuriSquareDownEdgeCenterClampYmaxL1678` | gathering / yukkuri / square / down / edge / center / 範囲補正 / ymax / l1678 | ルール/判定回帰 / gathering / yukkuri / square / down / edge / center / 範囲補正 / ymax / l1678 | 良い | - | - |
| `testGatheringYukkuriSquare2BodiesRightBkiFalseClampXl1760` | gathering / yukkuri / square2 / bodies / right / bki / false / 範囲補正 / xl1760 | ルール/判定回帰 / gathering / yukkuri / square2 / bodies / right / bki / false / 範囲補正 / xl1760 | 良い | - | - |
| `testGatheringYukkuriSquare2BodiesDownBkiFalseClampYl1765` | gathering / yukkuri / square2 / bodies / down / bki / false / 範囲補正 / yl1765 | ルール/判定回帰 / gathering / yukkuri / square2 / bodies / down / bki / false / 範囲補正 / yl1765 | 良い | - | - |

### `BodyMovementTest`
- 状態: 完了 (36/36 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - 世界状態の保存/復元と進行が壊れない
  - コレクションの追加/削除/参照が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `normalAdultUsesConfiguredStep` | normal / adult / uses / configured / step | ルール/判定回帰 / normal / adult / uses / configured / step | 良い | - | - |
| `hungryNonPredatorHalvesStep` | hungry / non / predator / halves / step | ルール/判定回帰 / hungry / non / predator / halves / step | 良い | - | - |
| `hungryPredatorKeepsStep` | hungry / predator / 維持 / step | ルール/判定回帰 / hungry / predator / 維持 / step | 良い | - | - |
| `damageSicknessPainAndCarryingHalveStep` | damage / sickness / pain / and / carrying / halve / step | ルール/判定回帰 / damage / sickness / pain / and / carrying / halve / step | 良い | - | - |
| `antsAndBlindCanReduceStepToMinimumOne` | ants / and / blind / 可否 / reduce / step / to / minimum / one | ルール/判定回帰 / ants / and / blind / 可否 / reduce / step / to / minimum / one | 良い | - | - |
| `stepZeroIsCorrectedToOne` | step / zero / 状態 / corrected / to / one | ルール/判定回帰 / step / zero / 状態 / corrected / to / one | 良い | - | - |
| `movementFrequencyUsesAdultStepAsBase` | movement / frequency / uses / adult / step / as / base | ルール/判定回帰 / movement / frequency / uses / adult / step / as / base | 良い | - | - |
| `destinationXAheadSetsPositiveDirection` | destination / xahead / sets / positive / direction | ルール/判定回帰 / destination / xahead / sets / positive / direction | 良い | - | - |
| `destinationXBehindSetsNegativeDirection` | destination / xbehind / sets / negative / direction | ルール/判定回帰 / destination / xbehind / sets / negative / direction | 良い | - | - |
| `destinationXReachedClearsDestination` | destination / xreached / clears / destination | ルール/判定回帰 / destination / xreached / clears / destination | 良い | - | - |
| `destinationYAheadSetsPositiveDirection` | destination / yahead / sets / positive / direction | ルール/判定回帰 / destination / yahead / sets / positive / direction | 良い | - | - |
| `destinationYBehindSetsNegativeDirection` | destination / ybehind / sets / negative / direction | ルール/判定回帰 / destination / ybehind / sets / negative / direction | 良い | - | - |
| `destinationYReachedClearsDestination` | destination / yreached / clears / destination | ルール/判定回帰 / destination / yreached / clears / destination | 良い | - | - |
| `randomDirectionXBeforeThresholdOnlyIncrementsCount` | random / direction / xbefore / threshold / only / increments / count | ルール/判定回帰 / random / direction / xbefore / threshold / only / increments / count | 良い | - | - |
| `randomDirectionXAtThresholdResetsCountAndUpdatesDirection` | random / direction / xat / threshold / resets / count / and / updates / direction | ルール/判定回帰 / random / direction / xat / threshold / resets / count / and / updates / direction | 良い | - | - |
| `randomDirectionYBeforeThresholdOnlyIncrementsCount` | random / direction / ybefore / threshold / only / increments / count | ルール/判定回帰 / random / direction / ybefore / threshold / only / increments / count | 良い | - | - |
| `randomDirectionYAtThresholdResetsCountAndUpdatesDirection` | random / direction / yat / threshold / resets / count / and / updates / direction | ルール/判定回帰 / random / direction / yat / threshold / resets / count / and / updates / direction | 良い | - | - |
| `directionalStepDoublesForExcitingRaper` | directional / step / doubles / for / exciting / raper | ルール/判定回帰 / directional / step / doubles / for / exciting / raper | 良い | - | - |
| `movementVectorUsesDirectionsStepAndSpeed` | movement / vector / uses / directions / step / and / speed | ルール/判定回帰 / movement / vector / uses / directions / step / and / speed | 良い | - | - |
| `movementVectorAddsRemainderStepWhenRandomHits` | movement / vector / adds / remainder / step / when / random / hits | ルール/判定回帰 / movement / vector / adds / remainder / step / when / random / hits | 良い | - | - |
| `movementVectorKeepsBaseVectorWhenRandomMissesRemainder` | movement / vector / 維持 / base / vector / when / random / misses / remainder | ルール/判定回帰 / movement / vector / 維持 / base / vector / when / random / misses / remainder | 良い | - | - |
| `flightDestinationAheadSetsPositiveZDirection` | flight / destination / ahead / sets / positive / zdirection | ルール/判定回帰 / flight / destination / ahead / sets / positive / zdirection | 良い | - | - |
| `flightDestinationReachedClearsDestinationWhenTargetExists` | flight / destination / reached / clears / destination / when / target / exists | ルール/判定回帰 / flight / destination / reached / clears / destination / when / target / exists | 良い | - | - |
| `flightWithoutTargetKeepsHeightLimitAsDestination` | flight / without / target / 維持 / height / limit / as / destination | ルール/判定回帰 / flight / without / target / 維持 / height / limit / as / destination | 良い | - | - |
| `nonFlyingBodyDoesNotUpdateFlightDestination` | non / flying / 本体 / does / 非 / 更新 / flight / destination | ルール/判定回帰 / non / flying / 本体 / does / 非 / 更新 / flight / destination | 良い | - | - |
| `directedMovementClampsToDestinationWithoutOvershoot` | directed / movement / clamps / to / destination / without / overshoot | ルール/判定回帰 / directed / movement / clamps / to / destination / without / overshoot | 良い | - | - |
| `externalMotionClampsXUnderflowAndAddsFallDamage` | external / motion / clamps / xunderflow / and / adds / 落下 / ダメージ | ルール/判定回帰 / external / motion / clamps / xunderflow / and / adds / 落下 / ダメージ | 良い | - | - |
| `externalMotionYOverflowSetsNegativeDirection` | external / motion / yoverflow / sets / negative / direction | ルール/判定回帰 / external / motion / yoverflow / sets / negative / direction | 良い | - | - |
| `externalMotionFallsWhenDepthDiffersWithoutFlight` | external / motion / falls / when / depth / differs / without / flight | ルール/判定回帰 / external / motion / falls / when / depth / differs / without / flight | 良い | - | - |
| `externalMotionLandingClearsNoDamageNextFall` | external / motion / landing / clears / なし / ダメージ / next / 落下 | ルール/判定回帰 / external / motion / landing / clears / なし / ダメージ / next / 落下 | 良い | - | - |
| `directedMovementWallHitWithDestinationIncrementsBlockedCount` | directed / movement / 壁 / hit / with / destination / increments / blocked / count | ルール/判定回帰 / directed / movement / 壁 / hit / with / destination / increments / blocked / count | 良い | - | - |
| `directedMovementAvoidsPoolWhenBodyDislikesWater` | directed / movement / avoids / pool / when / 本体 / dislikes / 水 | ルール/判定回帰 / directed / movement / avoids / pool / when / 本体 / dislikes / 水 | 良い | - | - |
| `moveToClampsDestinationToMapRange` | move / to / clamps / destination / to / map / range | ルール/判定回帰 / move / to / clamps / destination / to / map / range | 良い | - | - |
| `moveToYukkuriClearsActionsAndSetsTargetFlag` | move / to / yukkuri / clears / actions / and / sets / target / flag | ルール/判定回帰 / move / to / yukkuri / clears / actions / and / sets / target / flag | 良い | - | - |
| `runAwayTargetsOppositeCornerAndSetsScare` | run / away / targets / opposite / corner / and / sets / scare | ルール/判定回帰 / run / away / targets / opposite / corner / and / sets / scare | 良い | - | - |
| `runAwayIgnoredWhenBodyCannotAct` | run / away / ignored / when / 本体 / cannot / act | ルール/判定回帰 / run / away / ignored / when / 本体 / cannot / act | 良い | - | - |

### `BodyParentRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckNearParentReturnsWhenAdult` | 判定 / near / 親 / 戻り / when / adult | ルール/判定回帰 / 判定 / near / 親 / 戻り / when / adult | 良い | - | - |
| `testCheckNearParentCallingParentsWakesSleepingParent` | 判定 / near / 親 / calling / parents / wakes / sleeping / 親 | ルール/判定回帰 / 判定 / near / 親 / calling / parents / wakes / sleeping / 親 | 良い | - | - |

### `BodyPartnerSearchRuleTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 家族・関係データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSelectTargetsPrefersPheromoneBodyOverCloserNonPheromone` | フェロモン持ちが優先され近い non-pheromone が選ばれないこと | ルール/判定回帰 / フェロモン優先回帰 | 良い | - | - |
| `testSelectTargetsReportsOkazariCandidateForRudeBody` | ゲスが okazari 候補を記録し自身は候補にならないこと | ルール/判定回帰 / okazari 候補記録回帰 | 良い | - | - |
| `testSelectTargetsSkipsPredatorWhenActorIsPredatorServant` | 捕食者従者が捕食者をスキップして通常ゆっくりを選ぶこと | ルール/判定回帰 / 捕食者スキップ回帰 | 良い | - | - |

### `BodyRenderStateTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 画像コード選択ロジックが壊れない（状態→ImageCode の対応）

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `getFaceImageUsesPealedDeadFace` | dead+pealed → PEALEDDEADFACE コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getFaceImageUsesDeadFaceEvenWhenUnBirth` | dead+unbirth → DEAD コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getFaceImageUsesNydFace` | NYD 状態 → NYD_FRONT_WIDE コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getFaceImageUsesBlinkOverlayWhileSleepingInUnyoMode` | sleeping+unyo → EYE2 コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getBodyBaseImageUsesCrushedImageWhenCrushedWithoutAccessory` | crushed+okazariなし → CRUSHED2 コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getBodyBaseImageUsesFrontShitImageWhileShitting` | shitting → FRONT_SHIT コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getBodyBaseImageUsesBodyImageForNormalState` | 通常状態 → BODY コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getAbnormalBodyImageUsesCutOverlayWhenCriticalDamageIsCut` | critical=CUT → CUT overlay が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getAbnormalBodyImageUsesPealedMeltOverlay` | pealed+melt → overlay が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getEffectImageUsesHungryAndWetOverlays` | hungry+wet → 対応 overlay が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getEffectImageUsesDeadBodyOverlayForDeadUnBirth` | dead+unbirth → dead body overlay が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getEffectImageUsesHighestSickOverlay` | sick → highest sick overlay が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getOlazariImageUsesAccessoryForDefaultOkazari` | default okazari → accessory コードが選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getBraidImageUsesCutImageWhenNoBraidExists` | おさげなし → cut image が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |
| `getBraidImageUsesBackImageForTypeOne` | typeOne → back image が選ばれる | ルール/判定回帰 / 画像コード選択 | 良い | - | - |

### `BodyStealRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHandleOkazariStealSuccessfulStealTransfersOkazari` | 盗み成功でokazari移転・VERY_HAPPY・ストレス減を確認 | ルール/判定回帰 / 盗み成功副作用回帰 | 良い | - | - |
| `testHandleOkazariStealAwakeWitnessBlocksSteal` | 目撃者がいると盗めずokazariと happiness が不変なこと | ルール/判定回帰 / 目撃者ブロック回帰 | 良い | - | - |

### `BodyStressRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `detectsStressThresholds` | limit*2/5, limit*3/5 の 4 境界を完全に確認 | ルール/判定回帰 / ストレス閾値境界回帰 | 良い | - | - |
| `ignoresDeadBodiesByDelegatingToStressValueOnly` | dead/alive 両方でストレス値だけが判定されること | ルール/判定回帰 / dead 無関係ストレス回帰 | 良い | - | - |

### `BodyUnunSlaveEmotionRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEmotionFromUnunSlaveReturnsFalseForNull` | 判定 / emotion / from / unun / slave / 戻り / false / for / null | ルール/判定回帰 / 判定 / emotion / from / unun / slave / 戻り / false / for / null | 良い | - | - |
| `testCheckEmotionFromUnunSlaveHandlesUnunSlaveEnvyReaction` | UNUN_SLAVE が嫉妬し VERY_SAD・ストレス増になること | ルール/判定回帰 / UNUN_SLAVE 嫉妬副作用回帰 | 良い | - | - |

### `BodyVitalsTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `detectsDamageLevels` | ダメージ3段階と isNoDamaged/isDamaged の排他性を確認 | ルール/判定回帰 / ダメージレベル境界回帰 | 良い | - | - |
| `detectsHungerLevelsForLivingBody` | 満腹/普通/空腹/とても空腹の4段階を確認 | ルール/判定回帰 / 飢えレベル境界回帰 | 良い | - | - |
| `deadBodyIsNotHungryOrFull` | dead=true で全状態false、alive時との対比確認 | ルール/判定回帰 / dead 飢え無効回帰 | 良い | - | - |
| `detectsTooHungryAndStarvingFromDamageState` | damage条件でのisTooHungry/isStarving境界とhungry対比確認 | ルール/判定回帰 / tooHungry境界回帰 | 良い | - | - |
| `detectsSicknessStages` | 病気3段階の境界確認（isSick=false/true、isSickHeavily=true） | ルール/判定回帰 / 病気ステージ境界回帰 | 良い | - | - |
| `sickTooHeavilyRequiresHeavyStageAndDamage` | 重症期間+damage必須条件の確認 | ルール/判定回帰 / 重症ダメージ条件回帰 | 良い | - | - |

### `BodyWakeupRuleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckWakeupOtherYukkuriReturnsTrueWhenAwakeBodyVisible` | 判定 / wakeup / other / yukkuri / 戻り / true / when / awake / 本体 / visible | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / 戻り / true / when / awake / 本体 / visible | 良い | - | - |
| `testCheckWakeupOtherYukkuriReturnsFalseWhenVisibleBodySleeping` | 判定 / wakeup / other / yukkuri / 戻り / false / when / visible / 本体 / sleeping | ルール/判定回帰 / 判定 / wakeup / other / yukkuri / 戻り / false / when / visible / 本体 / sleeping | 良い | - | - |

### `EmotionLogicTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 感情計算の入力→出力マッピングが壊れない
  - 家族・関係タイプと Happiness 組み合わせで正しい感情フラグが立つ

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckEmotion_FamilyJoy` | 幸せなパートナー同士 / Joy フラグ | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_ChildEnvy` | 幸せな母親に子が Envy | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_RudeSchadenfreude` | 意地悪が悲しい他人に Joy+Pleasure | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_FamilyWorry` | 痛む家族に Worry+Fear | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_RudeEnvyAndAnger` | 悲しい意地悪が幸せ他人に Anger+Envy | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_SadMeSeeHappyFather_Joy` | 悲しい父親が幸せな子を見て Joy | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_SadMeSeesSadFamily_SadAndWorry` | 悲しい母親が悲しい子を見て Sad+Worry | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testCheckEmotion_VerySadMeSeesSadOther_SadOnly` | 非常に悲しい自分が悲しい他人を見て Sad のみ | ルール/判定回帰 / 感情フラグ | 良い | - | - |
| `testScenario_HappyParentSeesSadInjuredChild_SadWorryFearOnly` | 幸せな親が傷ついた子を見て Sad/Worry/Fear のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_AverageNonRudeSeesSadInjuredStranger_FearOnly` | 普通が傷ついた他人を見て Fear のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_VerySadRudeSeesHappyStranger_AngerAndEnvyOnly` | 非常に悲しい意地悪が幸せ他人を見て Anger+Envy のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_HappyNonRudeSeesHappyStranger_PleasureOnly` | 幸せ普通が幸せ他人を見て Pleasure のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_AverageSeesHappyPartner_EnvyOnly` | 普通が幸せなパートナーを見て Envy のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_SadSeesHappyStranger_SadAndEnvyOnly` | 悲しいが幸せ他人を見て Sad+Envy のみ | ルール/判定回帰 / シナリオ | 良い | - | - |
| `testScenario_AverageRudeSeesSadStranger_JoyAndPleasureOnly` | 普通意地悪が非常に悲しい他人を見て Joy+Pleasure のみ | ルール/判定回帰 / シナリオ | 良い | - | - |

### `EventLogicTest`
- 状態: 完了 (30/30 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 世界状態の保存/復元と進行が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAddWorldEventRegistersEventAndSpeakerMessage` | 追加 / world / イベント / registers / イベント / and / speaker / メッセージ | ルール/判定回帰 / 追加 / world / イベント / registers / イベント / and / speaker / メッセージ | 良い | - | - |
| `testAddBodyEventShortcut` | 追加 / 本体 / イベント / shortcut | ルール/判定回帰 / 追加 / 本体 / イベント / shortcut | 良い | - | - |
| `testAddBodyEventWithCount` | 追加 / 本体 / イベント / with / count | ルール/判定回帰 / 追加 / 本体 / イベント / with / count | 良い | - | - |
| `testAddBodyEventRegistersEventAndSpeakerMessage` | 追加 / 本体 / イベント / registers / イベント / and / speaker / メッセージ | ルール/判定回帰 / 追加 / 本体 / イベント / registers / イベント / and / speaker / メッセージ | 良い | - | - |
| `testAddBodyEventNullMessage` | 追加 / 本体 / イベント / null / メッセージ | ルール/判定回帰 / 追加 / 本体 / イベント / null / メッセージ | 良い | - | - |
| `testEventUpdateAbort` | イベント / 更新 / abort | ルール/判定回帰 / イベント / 更新 / abort | 良い | - | - |
| `testEventUpdateForceExecExecuteTrue` | イベント / 更新 / force / exec / execute / true | ルール/判定回帰 / イベント / 更新 / force / exec / execute / true | 良い | - | - |
| `testEventUpdateForceExecExecuteFalse` | イベント / 更新 / force / exec / execute / false | ルール/判定回帰 / イベント / 更新 / force / exec / execute / false | 良い | - | - |
| `testCheckBodyEventNoEventsReturnsNull` | 判定 / 本体 / イベント / なし / events / 戻り / null | ルール/判定回帰 / 判定 / 本体 / イベント / なし / events / 戻り / null | 良い | - | - |
| `testCheckBodyEventSimpleEventActionTrueRemovesEvent` | 判定 / 本体 / イベント / simple / イベント / action / true / removes / イベント | ルール/判定回帰 / 判定 / 本体 / イベント / simple / イベント / action / true / removes / イベント | 良い | - | - |
| `testCheckBodyEventCheckResponseTrueReturnsEvent` | 判定 / 本体 / イベント / 判定 / response / true / 戻り / イベント | ルール/判定回帰 / 判定 / 本体 / イベント / 判定 / response / true / 戻り / イベント | 良い | - | - |
| `testCheckWorldEventNoEventsReturnsNull` | 判定 / world / イベント / なし / events / 戻り / null | ルール/判定回帰 / 判定 / world / イベント / なし / events / 戻り / null | 良い | - | - |
| `testCheckWorldEventSimpleEventActionTrueSkips` | 判定 / world / イベント / simple / イベント / action / true / skips | ルール/判定回帰 / 判定 / world / イベント / simple / イベント / action / true / skips | 良い | - | - |
| `testCheckWorldEventCheckResponseTrueReturnsEvent` | 判定 / world / イベント / 判定 / response / true / 戻り / イベント | ルール/判定回帰 / 判定 / world / イベント / 判定 / response / true / 戻り / イベント | 良い | - | - |
| `testCheckSimpleWorldEventFromCheck` | 判定 / simple / world / イベント / from / 判定 | ルール/判定回帰 / 判定 / simple / world / イベント / from / 判定 | 良い | - | - |
| `testEventUpdateReachesTarget` | イベント / 更新 / reaches / target | ルール/判定回帰 / イベント / 更新 / reaches / target | 良い | - | - |
| `testCheckBodyEventCountDownTrueRemovesEvent` | 判定 / 本体 / イベント / count / down / true / removes / イベント | ルール/判定回帰 / 判定 / 本体 / イベント / count / down / true / removes / イベント | 良い | - | - |
| `testCheckBodyEventRetNotNullSecondEventCountsDown` | 判定 / 本体 / イベント / ret / 非 / null / second / イベント / counts / down | ルール/判定回帰 / 判定 / 本体 / イベント / ret / 非 / null / second / イベント / counts / down | 良い | - | - |
| `testCheckSimpleWorldEventFromEqualsBodySkips` | 判定 / simple / world / イベント / from / equals / 本体 / skips | ルール/判定回帰 / 判定 / simple / world / イベント / from / equals / 本体 / skips | 良い | - | - |
| `testCheckSimpleBodyEventSimpleTrueRemoved` | 判定 / simple / 本体 / イベント / simple / true / removed | ルール/判定回帰 / 判定 / simple / 本体 / イベント / simple / true / removed | 良い | - | - |
| `testCheckSimpleBodyEventSimpleFalseKept` | 判定 / simple / 本体 / イベント / simple / false / kept | ルール/判定回帰 / 判定 / simple / 本体 / イベント / simple / false / kept | 良い | - | - |
| `testEventUpdateFarFromTargetExecuteNotCalled` | イベント / 更新 / far / from / target / execute / 非 / called | ルール/判定回帰 / イベント / 更新 / far / from / target / execute / 非 / called | 良い | - | - |
| `testEventUpdateZmismatchExecuteNotCalled` | イベント / 更新 / zmismatch / execute / 非 / called | ルール/判定回帰 / イベント / 更新 / zmismatch / execute / 非 / called | 良い | - | - |
| `testClockWorldEventCountDownFalseKeepsEvent` | clock / world / イベント / count / down / false / 維持 / イベント | ルール/判定回帰 / clock / world / イベント / count / down / false / 維持 / イベント | 良い | - | - |
| `testCheckBodyEventCountDownFalseKeepsEvent` | 判定 / 本体 / イベント / count / down / false / 維持 / イベント | ルール/判定回帰 / 判定 / 本体 / イベント / count / down / false / 維持 / イベント | 良い | - | - |
| `testScenarioSelectedBodyEventRemovesOnlyChosenEventAndKeepsTrailingEvent` | シナリオ / selected / 本体 / イベント / removes / only / chosen / イベント / and / 維持 / trailing / イベント | ルール/判定回帰 / シナリオ / selected / 本体 / イベント / removes / only / chosen / イベント / and / 維持 / trailing / イベント | 良い | - | - |
| `testScenarioSimpleBodyEventRemovalDoesNotTouchLaterNormalEvent` | シナリオ / simple / 本体 / イベント / removal / does / 非 / touch / later / normal / イベント | ルール/判定回帰 / シナリオ / simple / 本体 / イベント / removal / does / 非 / touch / later / normal / イベント | 良い | - | - |
| `testScenarioSimpleWorldEventDoesNotRemoveItselfOrLaterNormalEvent` | シナリオ / simple / world / イベント / does / 非 / 除去 / itself / or / later / normal / イベント | ルール/判定回帰 / シナリオ / simple / world / イベント / does / 非 / 除去 / itself / or / later / normal / イベント | 良い | - | - |
| `testScenarioEventUpdateNearTargetExecutesAndClearsWhenExecuteReturnsTrueWithoutForceExec` | シナリオ / イベント / 更新 / near / target / executes / and / clears / when / execute / 戻り / true / without / force / exec | ルール/判定回帰 / シナリオ / イベント / 更新 / near / target / executes / and / clears / when / execute / 戻り / true / without / force / exec | 良い | - | - |
| `testScenarioEventUpdateNearTargetExecutesButKeepsEventWhenExecuteReturnsFalseWithoutForceExec` | シナリオ / イベント / 更新 / near / target / executes / but / 維持 / イベント / when / execute / 戻り / false / without / force / exec | ルール/判定回帰 / シナリオ / イベント / 更新 / near / target / executes / but / 維持 / イベント / when / execute / 戻り / false / without / force / exec | 良い | - | - |

### `FamilyActionLogicTest`
- 状態: 完了 (89/89 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - イベント進行と状態遷移が壊れない
  - 座標変換や幾何値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckFamilyActionEarlyReturnsTasks` | 判定 / 家族 / action / early / 戻り / tasks | ルール/判定回帰 / 判定 / 家族 / action / early / 戻り / tasks | 良い | - | - |
| `testCheckFamilyActionRandomCheckFails` | 判定 / 家族 / action / random / 判定 / fails | ルール/判定回帰 / 判定 / 家族 / action / random / 判定 / fails | 良い | - | - |
| `testCheckFamilyActionEventChecks` | 判定 / 家族 / action / イベント / checks | ルール/判定回帰 / 判定 / 家族 / action / イベント / checks | 良い | - | - |
| `testCheckFamilyActionNotAdult` | 判定 / 家族 / action / 非 / adult | ルール/判定回帰 / 判定 / 家族 / action / 非 / adult | 良い | - | - |
| `testCheckFamilyActionSelfStateChecks` | 判定 / 家族 / action / self / state / checks | ルール/判定回帰 / 判定 / 家族 / action / self / state / checks | 良い | - | - |
| `testCheckFamilyActionPartnerStateChecks` | 判定 / 家族 / action / 相手 / state / checks | ルール/判定回帰 / 判定 / 家族 / action / 相手 / state / checks | 良い | - | - |
| `testCheckFamilyActionChildStateFailsEatingAndShitting` | 判定 / 家族 / action / 子 / state / fails / eating / and / shitting | ルール/判定回帰 / 判定 / 家族 / action / 子 / state / fails / eating / and / shitting | 良い | - | - |
| `testSearchFoodStalkAndWaste` | search / food / stalk / and / waste | ルール/判定回帰 / search / food / stalk / and / waste | 良い | - | - |
| `testCheckFamilyActionHungryChildGoesToEat` | 判定 / 家族 / action / 空腹 / 子 / goes / to / eat | ルール/判定回帰 / 判定 / 家族 / action / 空腹 / 子 / goes / to / eat | 良い | - | - |
| `testCheckFamilyActionShitChildGoesToShit` | 判定 / 家族 / action / shit / 子 / goes / to / shit | ルール/判定回帰 / 判定 / 家族 / action / shit / 子 / goes / to / shit | 良い | - | - |
| `testCheckFamilyActionProudChild` | 判定 / 家族 / action / proud / 子 | ルール/判定回帰 / 判定 / 家族 / action / proud / 子 | 良い | - | - |
| `testCheckFamilyActionHungryChildStartsSuperEatingTimeEvent` | 判定 / 家族 / action / 空腹 / 子 / starts / super / eating / time / イベント | ルール/判定回帰 / 判定 / 家族 / action / 空腹 / 子 / starts / super / eating / time / イベント | 良い | - | - |
| `testCheckFamilyActionBabyNeedsToShitStartsShitExercisesEvent` | 判定 / 家族 / action / baby / needs / to / shit / starts / shit / exercises / イベント | ルール/判定回帰 / 判定 / 家族 / action / baby / needs / to / shit / starts / shit / exercises / イベント | 良い | - | - |
| `testCheckFamilyActionNoEatNoShitStartsProudChildEvent` | 判定 / 家族 / action / なし / eat / なし / shit / starts / proud / 子 / イベント | ルール/判定回帰 / 判定 / 家族 / action / なし / eat / なし / shit / starts / proud / 子 / イベント | 良い | - | - |
| `testRideOnParent` | ride / on / 親 | ルール/判定回帰 / ride / on / 親 | 良い | - | - |
| `testIsRapeTarget` | 状態 / rape / target | ルール/判定回帰 / 状態 / rape / target | 良い | - | - |
| `testCheckFamilyActionIgnoresStrayChild` | 判定 / 家族 / action / ignores / stray / 子 | ルール/判定回帰 / 判定 / 家族 / action / ignores / stray / 子 | 良い | - | - |
| `testSearchToiletDistance` | search / toilet / distance | ルール/判定回帰 / search / toilet / distance | 良い | - | - |
| `testCheckFamilyActionDelegatesToYoungerPartner` | 判定 / 家族 / action / delegates / to / younger / 相手 | ルール/判定回帰 / 判定 / 家族 / action / delegates / to / younger / 相手 | 良い | - | - |
| `testCheckFamilyActionEarlyReturnsOtherFlags` | 判定 / 家族 / action / early / 戻り / other / flags | ルール/判定回帰 / 判定 / 家族 / action / early / 戻り / other / flags | 良い | - | - |
| `testCheckFamilyActionIsNydreturnsFalse` | 判定 / 家族 / action / 状態 / nydreturns / false | ルール/判定回帰 / 判定 / 家族 / action / 状態 / nydreturns / false | 良い | - | - |
| `testCheckFamilyActionIsBirthReturnsFalse` | 判定 / 家族 / action / 状態 / birth / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 状態 / birth / 戻り / false | 良い | - | - |
| `testCheckFamilyActionIsEatingReturnsFalse` | 判定 / 家族 / action / 状態 / eating / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 状態 / eating / 戻り / false | 良い | - | - |
| `testCheckFamilyActionNearToBirthReturnsFalse` | 判定 / 家族 / action / near / to / birth / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / near / to / birth / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerLockmoveReturnsFalse` | 判定 / 家族 / action / 相手 / lockmove / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / lockmove / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerNearToBirthReturnsFalse` | 判定 / 家族 / action / 相手 / near / to / birth / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / near / to / birth / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerShittingReturnsFalse` | 判定 / 家族 / action / 相手 / shitting / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / shitting / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerBirthReturnsFalse` | 判定 / 家族 / action / 相手 / birth / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / birth / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerNeedledReturnsFalse` | 判定 / 家族 / action / 相手 / needled / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / needled / 戻り / false | 良い | - | - |
| `testCheckFamilyActionParentFullChildFullReturnsFalse` | 判定 / 家族 / action / 親 / full / 子 / full / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 親 / full / 子 / full / 戻り / false | 良い | - | - |
| `testCheckFamilyActionChildNeedledReturnsFalse` | 判定 / 家族 / action / 子 / needled / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 子 / needled / 戻り / false | 良い | - | - |
| `testCheckFamilyActionBabyChildShittingWantToShitFalse` | 判定 / 家族 / action / baby / 子 / shitting / want / to / shit / false | ルール/判定回帰 / 判定 / 家族 / action / baby / 子 / shitting / want / to / shit / false | 良い | - | - |
| `testCheckFamilyActionBabyChildShitFullWantToShitFalse` | 判定 / 家族 / action / baby / 子 / shit / full / want / to / shit / false | ルール/判定回帰 / 判定 / 家族 / action / baby / 子 / shit / full / want / to / shit / false | 良い | - | - |
| `testCheckFamilyActionBabyChildHungryAndShit60BothFalse` | 判定 / 家族 / action / baby / 子 / 空腹 / and / shit60 / both / false | ルール/判定回帰 / 判定 / 家族 / action / baby / 子 / 空腹 / and / shit60 / both / false | 良い | - | - |
| `testCheckFamilyActionChildEatingWantToEatFalse` | 判定 / 家族 / action / 子 / eating / want / to / eat / false | ルール/判定回帰 / 判定 / 家族 / action / 子 / eating / want / to / eat / false | 良い | - | - |
| `testCheckFamilyActionChildLockmoveContinue` | 判定 / 家族 / action / 子 / lockmove / continue | ルール/判定回帰 / 判定 / 家族 / action / 子 / lockmove / continue | 良い | - | - |
| `testCheckFamilyActionNoBabyChildWantToShitFalse` | 判定 / 家族 / action / なし / baby / 子 / want / to / shit / false | ルール/判定回帰 / 判定 / 家族 / action / なし / baby / 子 / want / to / shit / false | 良い | - | - |
| `testCheckFamilyActionChildAgeOnlyNoProudChild` | 判定 / 家族 / action / 子 / age / only / なし / proud / 子 | ルール/判定回帰 / 判定 / 家族 / action / 子 / age / only / なし / proud / 子 | 良い | - | - |
| `testGoToShitCheckWaitFailsReturnsFalse` | go / to / shit / 判定 / wait / fails / 戻り / false | ルール/判定回帰 / go / to / shit / 判定 / wait / fails / 戻り / false | 良い | - | - |
| `testGoToShitSuccess` | go / to / shit / success | ルール/判定回帰 / go / to / shit / success | 良い | - | - |
| `testGoToEatNoFoodReturnsFalse` | go / to / eat / なし / food / 戻り / false | ルール/判定回帰 / go / to / eat / なし / food / 戻り / false | 良い | - | - |
| `testGoToEatCheckWaitFailsReturnsFalse` | go / to / eat / 判定 / wait / fails / 戻り / false | ルール/判定回帰 / go / to / eat / 判定 / wait / fails / 戻り / false | 良い | - | - |
| `testGoToEatSuccess` | go / to / eat / success | ルール/判定回帰 / go / to / eat / success | 良い | - | - |
| `testCheckRaperFamilyTargetExistsReturnsFalse` | 判定 / raper / 家族 / target / exists / 戻り / false | ルール/判定回帰 / 判定 / raper / 家族 / target / exists / 戻り / false | 良い | - | - |
| `testCheckRaperFamilyNoTargetReturnsTrue` | 判定 / raper / 家族 / なし / target / 戻り / true | ルール/判定回帰 / 判定 / raper / 家族 / なし / target / 戻り / true | 良い | - | - |
| `testRideOnParentEmptyListReturnsFalse` | ride / on / 親 / empty / list / 戻り / false | ルール/判定回帰 / ride / on / 親 / empty / list / 戻り / false | 良い | - | - |
| `testRideOnParentCheckWaitFailsReturnsFalse` | ride / on / 親 / 判定 / wait / fails / 戻り / false | ルール/判定回帰 / ride / on / 親 / 判定 / wait / fails / 戻り / false | 良い | - | - |
| `testRideOnParentChildNotBabySkippedReturnsFalse` | ride / on / 親 / 子 / 非 / baby / skipped / 戻り / false | ルール/判定回帰 / ride / on / 親 / 子 / 非 / baby / skipped / 戻り / false | 良い | - | - |
| `testRideOnParentBabyChildHungryFoodNearbyDistanceSmallReturnsFalse` | ride / on / 親 / baby / 子 / 空腹 / food / nearby / distance / small / 戻り / false | ルール/判定回帰 / ride / on / 親 / baby / 子 / 空腹 / food / nearby / distance / small / 戻り / false | 良い | - | - |
| `testRideOnParentBabyChildHungryFoodFarReturnsTrue` | ride / on / 親 / baby / 子 / 空腹 / food / far / 戻り / true | ルール/判定回帰 / ride / on / 親 / baby / 子 / 空腹 / food / far / 戻り / true | 良い | - | - |
| `testRideOnParentBabyChildWantToShitToiletFarReturnsTrue` | ride / on / 親 / baby / 子 / want / to / shit / toilet / far / 戻り / true | ルール/判定回帰 / ride / on / 親 / baby / 子 / want / to / shit / toilet / far / 戻り / true | 良い | - | - |
| `testRideOnParentBabyChildNoTargetFoundReturnsFalse` | ride / on / 親 / baby / 子 / なし / target / found / 戻り / false | ルール/判定回帰 / ride / on / 親 / baby / 子 / なし / target / found / 戻り / false | 良い | - | - |
| `testProudChildCheckWaitFailsReturnsFalse` | proud / 子 / 判定 / wait / fails / 戻り / false | ルール/判定回帰 / proud / 子 / 判定 / wait / fails / 戻り / false | 良い | - | - |
| `testProudChildSuccess` | proud / 子 / success | ルール/判定回帰 / proud / 子 / success | 良い | - | - |
| `testSearchFoodSweets1Found` | search / food / sweets1 / found | ルール/判定回帰 / search / food / sweets1 / found | 良い | - | - |
| `testSearchFoodSweets2Found` | search / food / sweets2 / found | ルール/判定回帰 / search / food / sweets2 / found | 良い | - | - |
| `testSearchFoodEmptyFoodSkipped` | search / food / empty / food / skipped | ルール/判定回帰 / search / food / empty / food / skipped | 良い | - | - |
| `testSearchFoodWasteNormalFullParentNotFound` | search / food / waste / normal / full / 親 / 非 / found | ルール/判定回帰 / search / food / waste / normal / full / 親 / 非 / found | 良い | - | - |
| `testSearchFoodStalkFound` | search / food / stalk / found | ルール/判定回帰 / search / food / stalk / found | 良い | - | - |
| `testCheckFamilyActionIsIdiotReturnsFalse` | 判定 / 家族 / action / 状態 / idiot / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 状態 / idiot / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerCriticalDamageSecondBlockReturnsFalse` | 判定 / 家族 / action / 相手 / critical / ダメージ / second / block / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / critical / ダメージ / second / block / 戻り / false | 良い | - | - |
| `testCheckFamilyActionPartnerNoOkazariSecondBlockReturnsFalse` | 判定 / 家族 / action / 相手 / なし / okazari / second / block / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / 相手 / なし / okazari / second / block / 戻り / false | 良い | - | - |
| `testCheckFamilyActionChildCriticalDamageInjuredBothFalse` | 判定 / 家族 / action / 子 / critical / ダメージ / injured / both / false | ルール/判定回帰 / 判定 / 家族 / action / 子 / critical / ダメージ / injured / both / false | 良い | - | - |
| `testCheckFamilyActionChildNoOkazariLoopContinue` | 判定 / 家族 / action / 子 / なし / okazari / loop / continue | ルール/判定回帰 / 判定 / 家族 / action / 子 / なし / okazari / loop / continue | 良い | - | - |
| `testCheckFamilyActionRideLoopChildEventSkip` | 判定 / 家族 / action / ride / loop / 子 / イベント / skip | ルール/判定回帰 / 判定 / 家族 / action / ride / loop / 子 / イベント / skip | 良い | - | - |
| `testCheckFamilyActionHungryChildNoFoodGoesToEatFails` | 判定 / 家族 / action / 空腹 / 子 / なし / food / goes / to / eat / fails | ルール/判定回帰 / 判定 / 家族 / action / 空腹 / 子 / なし / food / goes / to / eat / fails | 良い | - | - |
| `testCheckFamilyActionWantToShitGoToShitCheckWaitFails` | 判定 / 家族 / action / want / to / shit / go / to / shit / 判定 / wait / fails | ルール/判定回帰 / 判定 / 家族 / action / want / to / shit / go / to / shit / 判定 / wait / fails | 良い | - | - |
| `testCheckFamilyActionProudChildCheckWaitFails` | 判定 / 家族 / action / proud / 子 / 判定 / wait / fails | ルール/判定回帰 / 判定 / 家族 / action / proud / 子 / 判定 / wait / fails | 良い | - | - |
| `testCheckFamilyActionRideOnParentReturnsTrueViaBed` | 判定 / 家族 / action / ride / on / 親 / 戻り / true / via / bed | ルール/判定回帰 / 判定 / 家族 / action / ride / on / 親 / 戻り / true / via / bed | 良い | - | - |
| `testSearchFoodDefaultFoodTypeFound` | search / food / default / food / type / found | ルール/判定回帰 / search / food / default / food / type / found | 良い | - | - |
| `testCheckRaperFamilyWithActualRaper` | 判定 / raper / 家族 / with / actual / raper | ルール/判定回帰 / 判定 / raper / 家族 / with / actual / raper | 良い | - | - |
| `testIsRapeTargetDeadBody` | 状態 / rape / target / 死亡 / 本体 | ルール/判定回帰 / 状態 / rape / target / 死亡 / 本体 | 良い | - | - |
| `testIsRapeTargetRaperBody` | 状態 / rape / target / raper / 本体 | ルール/判定回帰 / 状態 / rape / target / raper / 本体 | 良い | - | - |
| `testRideOnParentNullListReturnsFalse` | ride / on / 親 / null / list / 戻り / false | ルール/判定回帰 / ride / on / 親 / null / list / 戻り / false | 良い | - | - |
| `testRideOnParentChildEatingSkipReturnsFalse` | ride / on / 親 / 子 / eating / skip / 戻り / false | ルール/判定回帰 / ride / on / 親 / 子 / eating / skip / 戻り / false | 良い | - | - |
| `testRideOnParentChildShittingSkipReturnsFalse` | ride / on / 親 / 子 / shitting / skip / 戻り / false | ルール/判定回帰 / ride / on / 親 / 子 / shitting / skip / 戻り / false | 良い | - | - |
| `testRideOnParentParentHasTakeoutFoodNoFoodSearchReturnsFalse` | ride / on / 親 / 親 / 有無 / takeout / food / なし / food / search / 戻り / false | ルール/判定回帰 / ride / on / 親 / 親 / 有無 / takeout / food / なし / food / search / 戻り / false | 良い | - | - |
| `testCheckFamilyActionMiddleAgeChildNoBabyLoop` | 判定 / 家族 / action / middle / age / 子 / なし / baby / loop | ルール/判定回帰 / 判定 / 家族 / action / middle / age / 子 / なし / baby / loop | 良い | - | - |
| `testCheckFamilyActionRideLoopSleepingChildCanActionFalse` | 判定 / 家族 / action / ride / loop / sleeping / 子 / 可否 / action / false | ルール/判定回帰 / 判定 / 家族 / action / ride / loop / sleeping / 子 / 可否 / action / false | 良い | - | - |
| `testSearchFoodWasteWithPoorTangFound` | search / food / waste / with / poor / tang / found | ルール/判定回帰 / search / food / waste / with / poor / tang / found | 良い | - | - |
| `testSearchFoodMultipleFoodHigherLooksWins` | search / food / multiple / food / higher / looks / wins | ルール/判定回帰 / search / food / multiple / food / higher / looks / wins | 良い | - | - |
| `testIsRapeTargetRemovedBody` | 状態 / rape / target / removed / 本体 | ルール/判定回帰 / 状態 / rape / target / removed / 本体 | 良い | - | - |
| `testCheckFamilyActionNoChildrenReturnsFalse` | 判定 / 家族 / action / なし / children / 戻り / false | ルール/判定回帰 / 判定 / 家族 / action / なし / children / 戻り / false | 良い | - | - |
| `testSearchFoodWasteWithIsTooHungryFound` | search / food / waste / with / 状態 / too / 空腹 / found | ルール/判定回帰 / search / food / waste / with / 状態 / too / 空腹 / found | 良い | - | - |
| `testScenarioRideOnParentPrefersFoodTargetOverToilet` | シナリオ / ride / on / 親 / prefers / food / target / over / toilet | ルール/判定回帰 / シナリオ / ride / on / 親 / prefers / food / target / over / toilet | 良い | - | - |
| `testScenarioCheckFamilyActionStartsRideEventForSleepyBaby` | シナリオ / 判定 / 家族 / action / starts / ride / イベント / for / sleepy / baby | ルール/判定回帰 / シナリオ / 判定 / 家族 / action / starts / ride / イベント / for / sleepy / baby | 良い | - | - |
| `testScenarioGoToEatDropsTakenOutFoodAndCanImmediatelyRetargetDroppedFood` | シナリオ / go / to / eat / drops / taken / out / food / and / 可否 / immediately / retarget / dropped / food | ルール/判定回帰 / シナリオ / go / to / eat / drops / taken / out / food / and / 可否 / immediately / retarget / dropped / food | 良い | - | - |
| `testScenarioProudChildDirectlyStartsEventAndQueuesWorldEvent` | シナリオ / proud / 子 / directly / starts / イベント / and / queues / world / イベント | ルール/判定回帰 / シナリオ / proud / 子 / directly / starts / イベント / and / queues / world / イベント | 良い | - | - |
| `testScenarioCheckRaperFamilyClearsExcitingOnExistingRapersWhenNoTargetsRemain` | シナリオ / 判定 / raper / 家族 / clears / exciting / on / existing / rapers / when / なし / targets / remain | ルール/判定回帰 / シナリオ / 判定 / raper / 家族 / clears / exciting / on / existing / rapers / when / なし / targets / remain | 良い | - | - |

### `FoodEligibilityTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ルール/判定回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `predatorBodiesCanAlwaysEatBody` | 捕食者は生死問わず食べられること | ルール/判定回帰 / 捕食者食事回帰 | 良い | - | - |
| `nonPredatorCannotEatLivingBody` | 非捕食者は生体不可・死体可であること | ルール/判定回帰 / 非捕食者食事回帰 | 良い | - | - |
| `nonRudeBodiesRejectOkazariBodies` | 非VeryRudeはokazari死体を拒否しVeryRudeは食べられること | ルール/判定回帰 / okazari死体拒否回帰 | 良い | - | - |

### `FoodLogicTest`
- 状態: 完了 (36/36 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - `checkFood` の主要分岐が壊れない
  - `checkTakeout` の拒否/成功が壊れない
  - `checkCanEatBody` の predator / non-predator 判定が壊れない
  - `eatFood` の種別ごとの状態変化が壊れない
  - `searchFood` の主要ポリシーが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckFoodNotHungry` | 判定 / food / 非 / 空腹 | ルール/判定回帰 / 判定 / food / 非 / 空腹 | 良い | - | - |
| `testCheckFoodHungryNoFood` | 判定 / food / 空腹 / なし / food | ルール/判定回帰 / 判定 / food / 空腹 / なし / food | 良い | - | - |
| `testCheckFoodHungryFoundFood` | 判定 / food / 空腹 / found / food | ルール/判定回帰 / 判定 / food / 空腹 / found / food | 良い | - | - |
| `testEatFoodDeadBodyReturnsEarly` | eat / food / 死亡 / 本体 / 戻り / early | ルール/判定回帰 / eat / food / 死亡 / 本体 / 戻り / early | 良い | - | - |
| `testEatFoodNormalBodySweets` | eat / food / normal / 本体 / sweets | ルール/判定回帰 / eat / food / normal / 本体 / sweets | 良い | - | - |
| `testEatFoodNormalBodyFood` | eat / food / normal / 本体 / food | ルール/判定回帰 / eat / food / normal / 本体 / food | 良い | - | - |
| `testEatFoodNormalBodyBitter` | eat / food / normal / 本体 / bitter | ルール/判定回帰 / eat / food / normal / 本体 / bitter | 良い | - | - |
| `testCheckTakeoutNullBodyReturnsFalse` | 判定 / takeout / null / 本体 / 戻り / false | ルール/判定回帰 / 判定 / takeout / null / 本体 / 戻り / false | 良い | - | - |
| `testCheckTakeoutNullObjReturnsFalse` | 判定 / takeout / null / obj / 戻り / false | ルール/判定回帰 / 判定 / takeout / null / obj / 戻り / false | 良い | - | - |
| `testCheckTakeoutVeryHungryReturnsFalse` | 判定 / takeout / very / 空腹 / 戻り / false | ルール/判定回帰 / 判定 / takeout / very / 空腹 / 戻り / false | 良い | - | - |
| `testCheckTakeoutNormalBodyWithFoodNoFavBed` | 判定 / takeout / normal / 本体 / with / food / なし / fav / bed | ルール/判定回帰 / 判定 / takeout / normal / 本体 / with / food / なし / fav / bed | 良い | - | - |
| `testCheckTakeoutWithNonFoodObj` | 判定 / takeout / with / non / food / obj | ルール/判定回帰 / 判定 / takeout / with / non / food / obj | 良い | - | - |
| `testCheckCanEatBodyPredatorTypeReturnsTrue` | 判定 / 可否 / eat / 本体 / predator / type / 戻り / true | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / predator / type / 戻り / true | 良い | - | - |
| `testCheckCanEatBodyNonPredatorVsLiving` | 判定 / 可否 / eat / 本体 / non / predator / vs / living | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / non / predator / vs / living | 良い | - | - |
| `testCheckCanEatBodyNonPredatorVsDead` | 判定 / 可否 / eat / 本体 / non / predator / vs / 死亡 | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / non / predator / vs / 死亡 | 良い | - | - |
| `testCheckFoodIsSleepyAndFull` | 判定 / food / 状態 / sleepy / and / full | ルール/判定回帰 / 判定 / food / 状態 / sleepy / and / full | 良い | - | - |
| `testCheckFoodNonYukkuriDiseaseState` | 判定 / food / non / yukkuri / disease / state | ルール/判定回帰 / 判定 / food / non / yukkuri / disease / state | 良い | - | - |
| `testCheckFoodToBedNotVeryHungryReturnsFalse` | 判定 / food / to / bed / 非 / very / 空腹 / 戻り / false | ルール/判定回帰 / 判定 / food / to / bed / 非 / very / 空腹 / 戻り / false | 良い | - | - |
| `testCheckFoodToBodyNotVeryHungryReturnsFalse` | 判定 / food / to / 本体 / 非 / very / 空腹 / 戻り / false | ルール/判定回帰 / 判定 / food / to / 本体 / 非 / very / 空腹 / 戻り / false | 良い | - | - |
| `testCheckFoodToStealNotVeryHungryReturnsFalse` | 判定 / food / to / steal / 非 / very / 空腹 / 戻り / false | ルール/判定回帰 / 判定 / food / to / steal / 非 / very / 空腹 / 戻り / false | 良い | - | - |
| `testCheckFoodBuriedReturnsFalse` | 判定 / food / buried / 戻り / false | ルール/判定回帰 / 判定 / food / buried / 戻り / false | 良い | - | - |
| `testCheckFoodSleepingReturnsFalse` | 判定 / food / sleeping / 戻り / false | ルール/判定回帰 / 判定 / food / sleeping / 戻り / false | 良い | - | - |
| `testCheckFoodExcitingNotRaperNotSoHungryReturnsFalse` | 判定 / food / exciting / 非 / raper / 非 / so / 空腹 / 戻り / false | ルール/判定回帰 / 判定 / food / exciting / 非 / raper / 非 / so / 空腹 / 戻り / false | 良い | - | - |
| `testCheckFoodRaperExcitingNotStarvingReturnsFalse` | 判定 / food / raper / exciting / 非 / starving / 戻り / false | ルール/判定回帰 / 判定 / food / raper / exciting / 非 / starving / 戻り / false | 良い | - | - |
| `testCheckTakeoutExcitingReturnsFalse` | 判定 / takeout / exciting / 戻り / false | ルール/判定回帰 / 判定 / takeout / exciting / 戻り / false | 良い | - | - |
| `testCheckTakeoutRaperReturnsFalse` | 判定 / takeout / raper / 戻り / false | ルール/判定回帰 / 判定 / takeout / raper / 戻り / false | 良い | - | - |
| `testCheckTakeoutFoodEmptyReturnsFalse` | 判定 / takeout / food / empty / 戻り / false | ルール/判定回帰 / 判定 / takeout / food / empty / 戻り / false | 良い | - | - |
| `testCheckTakeoutAlreadyHasFoodTakeoutReturnsFalse` | 判定 / takeout / already / 有無 / food / takeout / 戻り / false | ルール/判定回帰 / 判定 / takeout / already / 有無 / food / takeout / 戻り / false | 良い | - | - |
| `testCheckTakeoutUnunSlaveNotShitInstanceReturnsFalse` | 判定 / takeout / unun / slave / 非 / shit / instance / 戻り / false | ルール/判定回帰 / 判定 / takeout / unun / slave / 非 / shit / instance / 戻り / false | 良い | - | - |
| `testCheckCanEatBodyIsVeryRudeHasOkazariReturnsTrue` | 判定 / 可否 / eat / 本体 / 状態 / very / rude / 有無 / okazari / 戻り / true | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / 状態 / very / rude / 有無 / okazari / 戻り / true | 良い | - | - |
| `testCheckCanEatBodyTooHungrySickPreyReturnsTrue` | 判定 / 可否 / eat / 本体 / too / 空腹 / 病気 / prey / 戻り / true | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / too / 空腹 / 病気 / prey / 戻り / true | 良い | - | - |
| `testCheckCanEatBodyFoolIntelligenceSickPreyReturnsTrue` | 判定 / 可否 / eat / 本体 / fool / intelligence / 病気 / prey / 戻り / true | ルール/判定回帰 / 判定 / 可否 / eat / 本体 / fool / intelligence / 病気 / prey / 戻り / true | 良い | - | - |
| `testCheckFoodNotVeryHungryToBodyIsToFoodSetsToFoodFalse` | 判定 / food / 非 / very / 空腹 / to / 本体 / 状態 / to / food / sets / to / food / false | ルール/判定回帰 / 判定 / food / 非 / very / 空腹 / to / 本体 / 状態 / to / food / sets / to / food / false | 良い | - | - |
| `testCheckFoodNotVeryHungryToBedIsToFoodSetsToFoodFalse` | 判定 / food / 非 / very / 空腹 / to / bed / 状態 / to / food / sets / to / food / false | ルール/判定回帰 / 判定 / food / 非 / very / 空腹 / to / bed / 状態 / to / food / sets / to / food / false | 良い | - | - |
| `testCheckFoodFlyingEatEventReturnsFalse` | 判定 / food / flying / eat / イベント / 戻り / false | ルール/判定回帰 / 判定 / food / flying / eat / イベント / 戻り / false | 良い | - | - |
| `testCheckFoodOtherHighPriorityEventReturnsFalse` | 判定 / food / other / high / priority / イベント / 戻り / false | ルール/判定回帰 / 判定 / food / other / high / priority / イベント / 戻り / false | 良い | - | - |

### `FoodTakeoutPolicyTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - UNUN_SLAVE の食べ物持ち出しポリシーが壊れない
  - 家族なし時の持ち出し拒否が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `checkTakeout_UnunSlaveWithoutSlaveToilet_ReturnsFalse` | unun / slave / without / slave / toilet / 戻り / false | ルール/判定回帰 / 持ち出しポリシー | 良い | - | - |
| `checkTakeout_UnunSlaveWithSlaveToiletAndNoHit_ReturnsTrue` | unun / slave / with / slave / toilet / and / no / hit / 戻り / true | ルール/判定回帰 / 持ち出しポリシー | 良い | - | - |
| `checkTakeout_FoodWithoutFamily_ReturnsFalse` | food / without / 家族 / 戻り / false | ルール/判定回帰 / 持ち出しポリシー | 良い | - | - |
| `checkTakeout_FoodWithFamilyAndNoOverlap_ReturnsTrue` | food / with / 家族 / and / no / overlap / 戻り / true | ルール/判定回帰 / 持ち出しポリシー | 良い | - | - |

### `StoneLogicTest`
- 状態: 完了 (13/13 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - 石が近い位置でゆっくりに与えるダメージメカニクスが壊れない
  - 赤ちゃんはCUT、成人は距離と知能に応じた負傷または逃走判定が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckPubbleNullBody` | null body で例外なしに完了すること | ルール/判定回帰 / null 安全回帰 | 良い | - | - |
| `testCheckPubbleCutBody` | CUT状態は checkPubble 後も CUT のままであること | ルール/判定回帰 / CUT状態不変回帰 | 良い | - | - |
| `testCheckPubbleNoStones` | 石なしでは criticalDamage が null のままであること | ルール/判定回帰 / 石なし無効回帰 | 良い | - | - |
| `testCheckPubble_stoneFarAway_noEffect` | 遠距離の石では criticalDamage が null のままであること | ルール/判定回帰 / 遠距離無効回帰 | 良い | - | - |
| `testCheckPubble_differentZ_skipped` | 異なるZ層の石ではダメージを受けないこと | ルール/判定回帰 / Z層スキップ回帰 | 良い | - | - |
| `testCheckPubble_adultBodyInjure_doesNotThrow` | check / pubble / adult / 本体 / injure | ルール/判定回帰 / 成人負傷 | 良い | - | - |
| `testCheckPubble_wiseBody_runsAway` | WISE body が適切な距離の石を検知して scared 状態になること | ルール/判定回帰 / WISE runAway 回帰 | 良い | - | - |
| `testConstructor_doesNotThrow` | StoneLogic インスタンスが生成されること | ルール/判定回帰 / constructor 回帰 | 良い | - | - |
| `testCheckPubble_babyBody_bodyCut` | check / pubble / baby / 本体 / body / cut | ルール/判定回帰 / 赤ちゃんCUT | 良い | - | - |
| `testCheckPubble_wiseBodyModerateDistance_callsRunAway` | WISE 中間距離で scared + ダメージなしであること | ルール/判定回帰 / WISE 距離分岐回帰 | 良い | - | - |
| `testCheckPubble_nonWiseModerateDistance_noRunAway` | check / pubble / non / wise / moderate / distance / no / run / away | ルール/判定回帰 / 知能分岐ネガティブ | 良い | - | - |
| `testCheckPubbleMethodExists` | checkPubble メソッドが存在し void 型であること | ルール/判定回帰 / メソッド存在・型回帰 | 良い | - | - |
| `testScenario_WiseBodyNearStoneSetsRunAwayDestinationAndScare` | シナリオ / wise / 本体 / near / stone / sets / run / away / destination / and / scare | ルール/判定回帰 / シナリオ | 良い | - | - |

### `ToiletLogicTest`
- 状態: 完了 (65/65 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - トイレ検索・排泄動作・しぶき処理の複雑なロジックが壊れない
  - NYD状態・UNUN_SLAVE身分・排泄スケジュール・複数トイレの優先度判定が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckShit_BodyAlreadyTalking_NoHate_returnsFalse` | 会話中はしぶき嫌悪なし | ルール/判定回帰 / 状態スキップ | 良い | - | - |
| `testCheckShit_BodyIsToShit_NoHate_returnsFalse` | 排泄中はしぶき嫌悪なし | ルール/判定回帰 / 状態スキップ | 良い | - | - |
| `testCheckShit_BodyUnunSlave_Dead_elseBranch` | 死亡した奴隷は無効 | ルール/判定回帰 / 奴隷 / 死亡 | 良い | - | - |
| `testCheckShit_BodyUnunSlave_NotSlaveRank_elseBranch` | ランクが違う奴隷は無効 | ルール/判定回帰 / 奴隷 / ランク | 良い | - | - |
| `testCheckShit_BodyUnunSlave_Removed_elseBranch` | 除去された奴隷は無効 | ルール/判定回帰 / 奴隷 / 除去 | 良い | - | - |
| `testCheckShit_Damaged_bCanTransportFalse_HatesNearbyShit` | ダメージ状態 / 運搬不可でも嫌悪あり | ルール/判定回帰 / ダメージ | 良い | - | - |
| `testCheckShit_DeadBodyAndSelfInMap_L78_continue` | 死亡体と自分をループでスキップ | ルール/判定回帰 / ループフィルタ | 良い | - | - |
| `testCheckShit_DontMove_bCanTransportFalse_HatesNearbyShit` | 動きロック / 運搬不可でも嫌悪あり | ルール/判定回帰 / ロック状態 | 良い | - | - |
| `testCheckShit_Exciting_ReturnsFalse` | 興奮中はしぶき反応なし | ルール/判定回帰 / 状態スキップ | 良い | - | - |
| `testCheckShit_FeelPain_bCanTransportFalse_HatesNearbyShit` | 痛覚あり / 運搬不可でも嫌悪あり | ルール/判定回帰 / 痛覚 | 良い | - | - |
| `testCheckShit_HasShitTakeout_NearShit_returnsTrue` | 運搬中のしぶきへの反応 | ルール/判定回帰 / 保持状態 | 良い | - | - |
| `testCheckShit_Idiot_returnsFalse` | イディオット体はしぶき嫌悪なし | ルール/判定回帰 / 知能 | 良い | - | - |
| `testCheckShit_NYDBodyReturnsFalse` | NYD状態 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckShit_NearToBirth_ReturnsFalse` | 出産直前 / しぶき反応なし | ルール/判定回帰 / 状態スキップ | 良い | - | - |
| `testCheckShit_NoShitInWorld` | しぶきなし / false | ルール/判定回帰 / しぶきなし | 良い | - | - |
| `testCheckShit_OtherBodyInMap_LoopFallthrough_returnsTrue` | 複数体でのループ処理 | ルール/判定回帰 / ループ | 良い | - | - |
| `testCheckShit_PreExistingUnunSlave_bCanTransportFalse_returnsTrue` | アクティブ奴隷がいると運搬不可 | ルール/判定回帰 / 奴隷占有 | 良い | - | - |
| `testCheckShit_RemovedBodyInMap_continue_returnsTrue` | 除去済みゆっくりをスキップ | ルール/判定回帰 / ループフィルタ | 良い | - | - |
| `testCheckShit_ShitNearby_BodyHatesShit_returnsTrue` | 近くにしぶき / 嫌悪反応 | ルール/判定回帰 / しぶき嫌悪 | 良い | - | - |
| `testCheckShit_ShitNotVeryClose_BodyDoesNotHate_returnsFalse` | 距離外しぶき / 無反応 | ルール/判定回帰 / 距離 | 良い | - | - |
| `testCheckShit_ShitZNonZero_continue_returnsFalse` | Z層が違うしぶきはスキップ | ルール/判定回帰 / Z層 | 良い | - | - |
| `testCheckShit_ToiletExists_ShitNotOnIt_bFoundMyToilet_returnsTrue` | トイレ発見→運搬予定 | ルール/判定回帰 / トイレ発見 | 良い | - | - |
| `testCheckShit_WithShit_BodyAgeNotModulo15` | age%15 判定 | ルール/判定回帰 / スケジュール | 良い | - | - |
| `testCheckShit_WithUnunSlaveInWorld_CanTransportFalse_HatesNearbyShit` | UNUN_SLAVE存在時の影響 | ルール/判定回帰 / 身分制 | 良い | - | - |
| `testCheckShit_bCanTransportTrue_LoopSelfContinue_returnsTrue` | 運搬可能 / 自己スキップと他体処理 | ルール/判定回帰 / ループ | 良い | - | - |
| `testCheckToilet_Arrived_WantShit_Z0_Stay` | トイレ到着 / Z=0 / stay() 呼び出し | ルール/判定回帰 / 到着処理 | 良い | - | - |
| `testCheckToilet_AutoCleanToilet_NoRunAway_returnsFalse` | 自動クリーントイレ / 逃走反応なし | ルール/判定回帰 / トイレ属性 | 良い | - | - |
| `testCheckToilet_CanflyCheck_WallModeAdult_returnsTrue` | 飛行型子供 / ADULTモードで空間走査 | ルール/判定回帰 / 飛行型 | 良い | - | - |
| `testCheckToilet_CurrentEvent_HighPriority_ReturnsFalse` | HIGH優先度イベント中 / false | ルール/判定回帰 / イベント優先度 | 良い | - | - |
| `testCheckToilet_FartherToilet_L252_false` | 遠いトイレは最近距離チェックに落ちる | ルール/判定回帰 / 最近距離 | 良い | - | - |
| `testCheckToilet_HasShit_NotWantShit_NoTarget_moveToYukkuri` | しぶき運搬 / 排泄欲望なし / ゆっくりへ移動 | ルール/判定回帰 / 運搬 | 良い | - | - |
| `testCheckToilet_HasToiletTarget_NotArrived_returnsTrue` | 移動途中 / true | ルール/判定回帰 / トイレ | 良い | - | - |
| `testCheckToilet_HighPriorityEvent_returnsFalse` | HIGH優先度イベント中 / 処理中断 | ルール/判定回帰 / イベント優先度 | 良い | - | - |
| `testCheckToilet_Idiot_returnsFalse` | イディオット体 / false | ルール/判定回帰 / 知能 | 良い | - | - |
| `testCheckToilet_IsDead` | 死亡 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_IsNYD` | NYD / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_IsRude_SkipsBarrierCheck_returnsTrue` | 鈍い体はバリア判定スキップ | ルール/判定回帰 / 態度 | 良い | - | - |
| `testCheckToilet_IsToBed_returnsFalse` | ベッド状態 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_IsToBody_returnsFalse` | ゆっくり化 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_IsToFood_returnsFalse` | 食料化 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_IsToSukkiri_returnsFalse` | すっきり / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_LowPriorityEvent_notBlocked_returnsTrue` | LOW優先度イベント中は処理続行 | ルール/判定回帰 / イベント優先度 | 良い | - | - |
| `testCheckToilet_NYD_NearState_ReturnsFalse` | NYD近傍での判定 | ルール/判定回帰 / 距離感染 | 良い | - | - |
| `testCheckToilet_NearToBirth_returnsFalse` | 出産直前 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckToilet_NoShit` | 排泄不要 / false | ルール/判定回帰 / 基本 | 良い | - | - |
| `testCheckToilet_NonAutoToilet_BodyInside_RunAway` | 通常トイレ内 / 逃走反応 | ルール/判定回帰 / 逃走 | 良い | - | - |
| `testCheckToilet_NonSlave_ArrivedIsToShit_NoShit_returnTrue` | 非奴隷 / isToShit=true / しぶきなし / true | ルール/判定回帰 / 到着 | 良い | - | - |
| `testCheckToilet_NonToiletTarget_ReturnsFalse` | 非トイレ目標 / false | ルール/判定回帰 / 型チェック | 良い | - | - |
| `testCheckToilet_NotWantShit_NoHasShit_NonAutoToilet_returnsFalse` | 通常トイレでの逃走不発 | ルール/判定回帰 / 条件分岐 | 良い | - | - |
| `testCheckToilet_SlaveToiletPreferred_NonSlaveSkipped_L268` | スレーブトイレ優先 / 非奴隷スキップ | ルール/判定回帰 / 優先度 | 良い | - | - |
| `testCheckToilet_Target_Removed_ReturnsFalse` | 目標トイレが除去 / false | ルール/判定回帰 / 環境変化 | 良い | - | - |
| `testCheckToilet_ThreeToilets_FoundNotNull_L266` | 複数トイレで最近距離選択 | ルール/判定回帰 / 最近距離 | 良い | - | - |
| `testCheckToilet_ToShit_TargetZNonZero_ReturnsFalse` | Z!=0施設は無視 | ルール/判定回帰 / Z層 | 良い | - | - |
| `testCheckToilet_TwoToilets_FoundNotNull_L265_nonSlave` | 2つの非奴隷トイレ / 選択 | ルール/判定回帰 / 選択 | 良い | - | - |
| `testCheckToilet_TwoToilets_L267_bothSlaveNoSkip` | 両方スレーブ / 距離順で選別 | ルール/判定回帰 / 選択 | 良い | - | - |
| `testCheckToilet_TwoToilets_L267_slaveSkipsNonSlave` | スレーブが見つかると非奴隷スキップ | ルール/判定回帰 / 優先度 | 良い | - | - |
| `testCheckToilet_UnunSlave_Arrived_NoShit_returnTrue` | UNUN奴隷 / 到着 / しぶきなし / true | ルール/判定回帰 / 奴隷 / 到着 | 良い | - | - |
| `testCheckToilet_UnunSlave_NonSlaveToilet_Skipped_ReturnsFalse` | UNUN奴隷は非奴隷トイレをスキップ | ルール/判定回帰 / 奴隷 / フィルタ | 良い | - | - |
| `testCheckToilet_UnunSlave_OnSlaveToilet_NoShit_ReturnsFalse` | 奴隷専用トイレ / しぶきなし / false | ルール/判定回帰 / 奴隷 | 良い | - | - |
| `testCheckToilet_UnunSlave_isToSteal_NotWantShit_ReturnsFalse` | UNUN奴隷 / 盗み状態 / false | ルール/判定回帰 / 奴隷 / 盗み | 良い | - | - |
| `testCheckToilet_WantShit_NoToilet` | 排泄欲望あり / 施設なし | ルール/判定回帰 / トイレ | 良い | - | - |
| `testCheckToilet_WantShit_WithToilet` | 排泄欲望あり / 施設あり | ルール/判定回帰 / トイレ | 良い | - | - |
| `testCheckToilet_WantShit_WithToilet_WantShitBranch_returnsTrue` | 欲望あり施設検索 | ルール/判定回帰 / トイレ | 良い | - | - |
| `testGetSetBodyUnunSlave` | getter/setter | ルール/判定回帰 / getter | 良い | - | - |
| `testScenario_ArrivalWithCarriedShitDropsItAndRelaxes` | 到着時の投下とストレス低下 | ルール/判定回帰 / シナリオ | 良い | - | - |

### `ToyLogicTest`
- 状態: 完了 (35/35 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - おもちゃ・水・トランポリンの発見・獲得・遊び動作が壊れない
  - 年齢別・知能別の遊びルール、お気に入り設定、所有権判定が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCanPlay` | canPlay 基本判定 | ルール/判定回帰 / canPlay | 良い | - | - |
| `testCanPlay_DeadBodyReturnsFalse` | 死亡 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_HasCurrentEventReturnsFalse` | イベント実行中 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsExcitingReturnsFalse` | 興奮 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsLockmoveReturnsFalse` | 移動ロック / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsScare_ReturnsFalse` | 怖がり / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToBedReturnsFalse` | 寝床 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToBody_ReturnsFalse` | ゆっくり化 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToFoodReturnsFalse` | 食料化 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToShitReturnsFalse` | 排泄 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToStealReturnsFalse` | 盗み / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_IsToSukkiriReturnsFalse` | すっきり / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCanPlay_NYDReturnsFalse` | NYD / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckSui_FavItemSet_NextBoolTrue_ReturnsFalse` | お気に入り水 / nextBoolean=true / false | ルール/判定回帰 / 水 / ランダム | 良い | - | - |
| `testCheckSui_LinkParentIsSui_ReturnsFalse` | 水に乗車中 / false | ルール/判定回帰 / 乗車状態 | 良い | - | - |
| `testCheckSui_NoCanBind_NonFamily_Skip` | 非家族所有の水はスキップ | ルール/判定回帰 / 水 / 家族判定 | 良い | - | - |
| `testCheckSui_NoSui` | 水なし / false | ルール/判定回帰 / 水 | 良い | - | - |
| `testCheckSui_RND150_0_Talking` | 会話中 / RND(150)==0 / スキップ | ルール/判定回帰 / 水 / ランダム | 良い | - | - |
| `testCheckSui_RND150_Zero_ReturnsFalse` | RND(150)==0 / 気が変わる / false | ルール/判定回帰 / ランダム | 良い | - | - |
| `testCheckSui_TwoSui_LoopBreak` | 複数の水 / 最初で処理終了 | ルール/判定回帰 / 水 / ループ | 良い | - | - |
| `testCheckSui_WithSui_CannotPlay` | 水あり / 遊べない | ルール/判定回帰 / 水 | 良い | - | - |
| `testCheckToy_AdultBody_ReturnsFalse` | 成人は遊ばない | ルール/判定回帰 / 年齢 | 良い | - | - |
| `testCheckToy_CanPlayFalse_WithToy` | canPlay=false / おもちゃあり | ルール/判定回帰 / フィルタ | 良い | - | - |
| `testCheckToy_NoToy` | おもちゃなし / false | ルール/判定回帰 / 基本 | 良い | - | - |
| `testCheckToy_Null` | null 入力 | ルール/判定回帰 / null 安全 | 良い | - | - |
| `testCheckToy_ToyInAir_ReturnsTrue` | 空中のおもちゃ発見 | ルール/判定回帰 / Z>0 | 良い | - | - |
| `testCheckToy_TwoToys_LoopBreak` | 複数おもちゃ / 最初の1つで処理終了 | ルール/判定回帰 / ループ | 良い | - | - |
| `testCheckToy_WantToShit_ReturnsFalse` | 排泄欲望 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckTrampoline_AdultNotRude_ReturnsFalse` | 成人・非鈍行 | ルール/判定回帰 / 年齢制限 | 良い | - | - |
| `testCheckTrampoline_NoTrampoline` | トランポリンなし / false | ルール/判定回帰 / トランポリン | 良い | - | - |
| `testCheckTrampoline_Null` | null 入力 | ルール/判定回帰 / null 安全 | 良い | - | - |
| `testCheckTrampoline_TwoTrampolines_LoopBreak` | 複数トランポリン / 最初の1つで処理終了 | ルール/判定回帰 / ループ | 良い | - | - |
| `testCheckTrampoline_WantToShit_ReturnsFalse` | 排泄欲望 / false | ルール/判定回帰 / 状態除外 | 良い | - | - |
| `testCheckTrampoline_WithDefaultTrampolineCannotPlay` | トランポリンあり / 遊べない | ルール/判定回帰 / トランポリン | 良い | - | - |
| `testScenario_FindingNearbyToyMakesItFavoriteAndOwned` | おもちゃ獲得→お気に入り化→幸福 | ルール/判定回帰 / シナリオ | 良い | - | - |

### `TrashLogicTest`
- 状態: 完了 (15/15 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ゴミから尾飾りを獲得するロジックが壊れない
  - 視野範囲判定・複数ゴミの最近距離検索が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testCheckTrashOkazari_HasOkazari_ReturnsFalse` | 既所有 / スキップ | ルール/判定回帰 / 所有保護 | 良い | - | - |
| `testCheckTrashOkazari_NoOkazari_NoTrash_ReturnsFalse` | ゴミなし / false | ルール/判定回帰 / ゴミなし | 良い | - | - |
| `testCheckTrashOkazari_NoOkazari_TrashNearby_ReturnsTrue` | 視野内ゴミ発見 / イベント追加 | ルール/判定回帰 / 発見 | 良い | - | - |
| `testCheckTrashOkazari_TrashOutOfSight_ReturnsFalse` | 視野外ゴミ / false | ルール/判定回帰 / 視野 | 良い | - | - |
| `testCheckTrashOkazari_TrashAtSamePosition_ReturnsTrue` | 同位置ゴミ | ルール/判定回帰 / 距離=0 | 良い | - | - |
| `testCheckTrashOkazari_MultipleTrash_ReturnsTrue` | 複数ゴミ / 最近選定 | ルール/判定回帰 / 最近距離 | 良い | - | - |
| `testCheckTrashOkazari_TrashRemovedFromWorld_ReturnsFalse` | ゴミ除去後 / false | ルール/判定回帰 / 環境変化 | 良い | - | - |
| `testCheckTrashOkazari_HasOkazariUnchangedAfterCall` | 副作用なし | ルール/判定回帰 / 不変性 | 良い | - | - |
| `testCheckTrashOkazari_CalledTwice_NoTrash` | 2回呼び出し / ゴミなし | ルール/判定回帰 / 安定性 | 良い | - | - |
| `testCheckTrashOkazari_CalledTwice_WithTrash` | 2回呼び出し / ゴミあり | ルール/判定回帰 / 安定性 | 良い | - | - |
| `testCheckTrashOkazari_DifferentBody` | 別の体で実行 | ルール/判定回帰 / マルチ | 良い | - | - |
| `testCheckTrashOkazari_HasOkazari_MultipleTrash_ReturnsFalse` | 所有状態 / 複数ゴミ | ルール/判定回帰 / 所有優先度 | 良い | - | - |
| `testCheckTrashOkazari_NegativeEyesight_ReturnsFalse` | 視野負数 / エッジケース | ルール/判定回帰 / 視野 | 良い | - | - |
| `testCheckTrashOkazari_TrashExactlyAtEyesight` | 視野の境界値 / 超える | ルール/判定回帰 / 境界値 | 良い | - | - |
| `testCheckTrashOkazari_TrashJustBeyondEyesight` | 視野の境界値 / ぎりぎり外 | ルール/判定回帰 / 境界値 | 良い | - | - |

### `YukkuriRelationsTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `ルール/判定回帰`
- 回帰目的:
  - ゆっくり間の親子・兄弟・パートナー関係の検出・取得・削除が壊れない
  - null 耐性が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `detectsParentChildRelations` | 親子関係の双方向検出 | ルール/判定回帰 / 親子 | 良い | - | - |
| `detectsPartnerRelations` | パートナー関係の単方向性 | ルール/判定回帰 / パートナー | 良い | - | - |
| `detectsSiblingRelationsByKnownParent` | 兄弟関係の親経由検出 | ルール/判定回帰 / 兄弟 | 良い | - | - |
| `resolvesFamilyMembersByIndex` | インデックス指定での関係メンバ取得 | ルール/判定回帰 / lookup | 良い | - | - |
| `removesFamilyMembersByIndexTarget` | 対象を指定した関係削除 | ルール/判定回帰 / 削除 | 良い | - | - |
| `detectsRelationMineClassification` | 自分視点での関係分類 | ルール/判定回帰 / 分類 | 良い | - | - |
| `unrelatedBodiesAreNotFamily` | 無関係な個体の関係判定 | ルール/判定回帰 / 無関係 | 良い | - | - |
| `nullHandlingMatchesExistingBodyRelationMethods` | null 耐性（isParent/Partner は false、isSister/isFamily は NPE は設計既知制限） | ルール/判定回帰 / null 安全 | 良い | - | isSister/isFamily は null でNPEを投げる既知の設計制限 |

## `org.simyukkuri.system`
### `BasicStrokeExTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `シリアライズ/Stroke初期値回帰`
- 回帰目的:
  - コンストラクタの初期値が壊れない
  - シリアライズ・デシリアライズが壊れない
  - serializable 変換が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorsAndGetters` | constructors / and / getters | シリアライズ/Stroke初期値回帰 / constructors / and / getters | 良い | - | - |
| `testDefaultConstructorAndMiterLimitConstructor` | default / constructor / and / miter / limit / constructor | シリアライズ/Stroke初期値回帰 / default / constructor / and / miter / limit / constructor | 良い | - | - |
| `testSerializableWithAlreadySerializable` | serializable / with / already / serializable | シリアライズ/Stroke初期値回帰 / serializable / with / already / serializable | 良い | - | - |
| `testSerialization` | serialization | シリアライズ/Stroke初期値回帰 / serialization | 良い | - | - |
| `testSerializableUtility` | serializable / utility | シリアライズ/Stroke初期値回帰 / serializable / utility | 良い | - | - |

### `BodyLayerTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - clear によるリセットが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorInitializesArrays` | constructor / initializes / arrays | UI/設定/入力/メッセージ回帰 / constructor / initializes / arrays | 良い | - | - |
| `testClearResetsInjectedArrays` | clear 後に配列サイズが維持され全要素が 0/null であること | UI/設定/入力/メッセージ回帰 / clear 完全リセット回帰 | 良い | - | - |

### `CashTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - キャッシュ加算・減算が正確に反映される
  - 購入・売却の金額計算ロジックが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAddCashAdjustsPlayerBalanceExactly` | 追加 / cash / adjusts / player / balance / exactly | UI/設定/入力/メッセージ回帰 / cash / 加算 | 良い | - | - |
| `testBuyItemSubtractsItemValueExactly` | buy / item / subtracts / item / value / exactly | UI/設定/入力/メッセージ回帰 / buy / item | 良い | - | - |
| `testBuyYukkuriBabyChargesExactlyOneThirdOfBaseCost` | buy / yukkuri / baby / charges / exactly / one / third / of / base / cost | UI/設定/入力/メッセージ回帰 / buy / yukkuri / baby | 良い | - | - |
| `testBuyYukkuriChildChargesExactlyOneHalfOfBaseCost` | buy / yukkuri / 子 / charges / exactly / one / half / of / base / cost | UI/設定/入力/メッセージ回帰 / buy / yukkuri / 子 | 良い | - | - |
| `testBuyYukkuriAdultChargesFullCost` | buy / yukkuri / adult / charges / full / cost | UI/設定/入力/メッセージ回帰 / buy / yukkuri / adult | 良い | - | - |
| `testSellWorthlessYukkuriReturnsZeroAndLeavesCashUnchanged` | sell / worthless / yukkuri / 戻り / zero / and / leaves / cash / unchanged | UI/設定/入力/メッセージ回帰 / sell / worthless | 良い | - | - |
| `testSellHealthyChildPetAddsComputedPetValueToCash` | sell / healthy / 子 / pet / adds / computed / pet / value / to / cash | UI/設定/入力/メッセージ回帰 / sell / pet | 良い | - | - |

### `CustomLogFormatterTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - UI/設定/入力/メッセージ回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testFormat` | 日時フォーマット・区切り・レベル文字列・改行末尾を検証 | UI/設定/入力/メッセージ回帰 / format 仕様回帰 | 良い | - | - |

### `FieldShapeBaseTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `UI/設定/入力/フィールド形状回帰`
- 回帰目的:
  - 座標範囲判定が壊れない
  - 除去状態と tick 進行が壊れない
  - world / field の寸法値が壊れない
  - shape popup の既定値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMapPosAndContains` | map / pos / and / contains | UI/設定/入力/フィールド形状回帰 / map / pos / and / contains | 良い | - | - |
| `testFieldPosAndContains` | field / pos / and / contains | UI/設定/入力/フィールド形状回帰 / field / pos / and / contains | 良い | - | - |
| `testRemoveAndClockTick` | 除去 / and / clock / tick | UI/設定/入力/フィールド形状回帰 / 除去フラグ回帰 | 良い | - | - |
| `testDimensions` | dimensions | UI/設定/入力/フィールド形状回帰 / dimensions | 良い | - | - |
| `testHasShapePopup_defaultReturnsNONE` | 有無 / shape / popup / default / 戻り / none | UI/設定/入力/フィールド形状回帰 / 有無 / shape / popup / default / 戻り / none | 良い | - | - |

### `FrameRateTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `計測ロジック契約回帰`
- 回帰目的:
  - フレームレート更新と初期値が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructor` | コンストラクタ後の初期フレームレートが 0.0f であること | 計測ロジック契約回帰 / constructor 初期値回帰 | 良い | - | - |
| `testGetFrameRateInitial` | 初期値 0.0f かつ 1秒未満カウント後も 0.0f のままであること | 計測ロジック契約回帰 / 時間契約回帰 | 良い | - | - |
| `testCountFrames` | 1秒未満では更新されず 0.0f のまま（時間契約）であること | 計測ロジック契約回帰 / 1秒未満不更新回帰 | 良い | - | - |
| `testFrameRateCalculation` | 1秒後に FPS が 50-150 範囲で更新されること | 計測ロジック契約回帰 / FPS 計算範囲回帰 | 良い | - | - |

### `IconPoolTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - enum 値の定義が壊れない
  - ロード後に画像配列が正しく設定される

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testUiSkinEnumValues` | ui / skin / enum / values | UI/設定/入力/メッセージ回帰 / ui / skin / enum / values | 良い | - | - |
| `testButtonIconEnumValues` | button / icon / enum / values | UI/設定/入力/メッセージ回帰 / button / icon / enum / values | 良い | - | - |
| `testStatusIconEnumValues` | status / icon / enum / values | UI/設定/入力/メッセージ回帰 / status / icon / enum / values | 良い | - | - |
| `testCursorIconEnumValues` | cursor / icon / enum / values | UI/設定/入力/メッセージ回帰 / cursor / icon / enum / values | 良い | - | - |
| `testHelpIconEnumValues` | help / icon / enum / values | UI/設定/入力/メッセージ回帰 / help / icon / enum / values | 良い | - | - |
| `testLoadImagesPopulatesIconArrays` | ロード / images / populate / icon / arrays | UI/設定/入力/メッセージ回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetUiSkinImageArray_notNull` | 非null かつ UiSkin.values().length と同サイズであること | UI/設定/入力/メッセージ回帰 / UiSkin 配列サイズ回帰 | 良い | - | - |
| `testGetButtonIconImageArray_notNull` | 非null かつ ButtonIcon.values().length と同サイズであること | UI/設定/入力/メッセージ回帰 / ButtonIcon 配列サイズ回帰 | 良い | - | - |
| `testGetStatusIconImageArray_notNull` | 非null かつ StatusIcon.values().length と同サイズであること | UI/設定/入力/メッセージ回帰 / StatusIcon 配列サイズ回帰 | 良い | - | - |
| `testGetCursorIconImageArray_notNull` | 非null かつ CursorIcon.values().length と同サイズであること | UI/設定/入力/メッセージ回帰 / CursorIcon 配列サイズ回帰 | 良い | - | - |
| `testGetHelpIconImageArray_notNull` | 非null かつ HelpIcon.values().length と同サイズであること | UI/設定/入力/メッセージ回帰 / HelpIcon 配列サイズ回帰 | 良い | - | - |
| `testConstructor_doesNotThrow` | IconPool インスタンスが生成されること | UI/設定/入力/メッセージ回帰 / constructor 回帰 | 良い | - | - |

### `IniFileReaderTest`
- 状態: 完了 (12/12 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - 世界状態の保存/復元と進行が壊れない
  - 例外系の扱いが壊れない
  - コレクションの追加/削除/参照が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testOpenNonExistent` | 非存在ファイルで open が false を返しインスタンスは生成されること | UI/設定/入力/メッセージ回帰 / open 失敗回帰 | 良い | - | - |
| `testReadNextWithoutOpen` | open() 前に readNext() を呼ぶと NPE になること（設計上の制限） | UI/設定/入力/メッセージ回帰 / open 前 NPE 回帰 | 良い | - | - |
| `testClose` | open() 前に close() を呼ぶと NPE になること（設計上の制限） | UI/設定/入力/メッセージ回帰 / close 前 NPE 回帰 | 良い | - | - |
| `testConstants` | 3定数が正しい値を持ち互いに異なること | UI/設定/入力/メッセージ回帰 / constants 回帰 | 良い | - | - |
| `testOpenRealFileReturnsTrue` | open / real / file / 戻り / true | UI/設定/入力/メッセージ回帰 / open / real / file / 戻り / true | 良い | - | - |
| `testReadNextSectionAndKeyReturnsMap` | read / next / section / and / key / 戻り / map | UI/設定/入力/メッセージ回帰 / 復活/再生回帰 | 良い | - | - |
| `testReadNextCommentLineSkipped` | コメント行スキップ後に section/key/value が正しく読めること | UI/設定/入力/メッセージ回帰 / コメントスキップ回帰 | 良い | - | - |
| `testReadNextEmptyLineSkipped` | 空行スキップ後に section/key/value が正しく読めること | UI/設定/入力/メッセージ回帰 / 空行スキップ回帰 | 良い | - | - |
| `testReadNextEndOfFileReturnsNull` | 1回目はkeyが読め2回目はEOFでnullを返すこと | UI/設定/入力/メッセージ回帰 / EOF null 回帰 | 良い | - | - |
| `testReadNextSectionWithoutBracketEndSkipped` | 閉じ括弧なしセクションスキップ後に有効なsection/keyが読めること | UI/設定/入力/メッセージ回帰 / 無効セクションスキップ回帰 | 良い | - | - |
| `testScenarioReadNextKeepsCurrentSectionAcrossMultipleKeys` | シナリオ / read / next / 維持 / current / section / across / multiple / keys | UI/設定/入力/メッセージ回帰 / シナリオ / read / next / 維持 / current / section / across / multiple / keys | 良い | - | - |
| `testScenarioReadNextSwitchesToLaterSectionBeforeReturningNextKey` | シナリオ / read / next / switches / to / later / section / before / returning / next / key | UI/設定/入力/メッセージ回帰 / シナリオ / read / next / switches / to / later / section / before / returning / next / key | 良い | - | - |

### `ItemMenuTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `UI/メニュー回帰`
- 回帰目的:
  - メニュー構成と選択状態が壊れない
  - ポップアップ構築とキャンセル副作用が壊れない
  - enum の選択可否フラグが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetMenuTargetEnumValues` | 取得 / menu / target / enum / values | UI/メニュー回帰 / 取得 / menu / target / enum / values | 良い | - | - |
| `testUseMenuTargetEnumValues` | use / menu / target / enum / values | UI/メニュー回帰 / use / menu / target / enum / values | 良い | - | - |
| `testShapeMenuTargetEnumValues` | shape / menu / target / enum / values | UI/メニュー回帰 / shape / menu / target / enum / values | 良い | - | - |
| `testCreatePopupMenuBuildsCategories` | create / popup / menu / builds / categories | UI/メニュー回帰 / create / popup / menu / builds / categories | 良い | - | - |
| `testSetGetPopupMenuEnablesExpectedActions` | set / get / popup / menu / enables / expected / actions | UI/メニュー回帰 / set / get / popup / menu / enables / expected / actions | 良い | - | - |
| `testSetShapePopupMenuConfiguresVisibilityAndEnablement` | set / shape / popup / menu / configures / visibility / and / enablement | UI/メニュー回帰 / set / shape / popup / menu / configures / visibility / and / enablement | 良い | - | - |
| `testItemModeCancelClearsHeldItemWhenRequested` | item / mode / cancel / clears / held / item / when / requested | UI/メニュー回帰 / item / mode / cancel / clears / held / item / when / requested | 良い | - | - |
| `testItemModeCancelKeepsHeldItemWhenNotRequested` | item / mode / cancel / keeps / held / item / when / not / requested | UI/メニュー回帰 / item / mode / cancel / keeps / held / item / when / not / requested | 良い | - | - |

### `LoggerYukkuriTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `ログ集計回帰`
- 回帰目的:
  - ログページのラップと更新が壊れない
  - ログ出力ハンドラの重複が壊れない
  - 直近/累積カウントが壊れない
  - リングバッファのオーバーラップと表示処理が状態を壊さない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSetLogPageWrapsWithinValidRange` | log / page / wraps / within / range | ログ集計回帰 / log / page / wraps / within / range | 良い | - | - |
| `testAddLogPageWrapsRelativeToCurrentPage` | add / log / page / wraps / relative / to / current / page | ログ集計回帰 / add / log / page / wraps / relative / to / current / page | 良い | - | - |
| `testSetShowStoresFlag` | show / stores / flag | ログ集計回帰 / show / stores / flag | 良い | - | - |
| `testSetClearLogTimeStoresValue` | clear / log / time / stores / value | ログ集計回帰 / clear / log / time / stores / value | 良い | - | - |
| `testOutputLogFileInstallsSingleFileHandler` | output / log / file / installs / single / file / handler | ログ集計回帰 / output / log / file / installs / single / file / handler | 良い | - | - |
| `testRunCountsCategoriesAgesSickShitAndCash` | run / counts / categories / ages / sick / shit / and / cash | ログ集計回帰 / run / counts / categories / ages / sick / shit / and / cash | 良い | - | - |
| `testGetLogRingBufferWrapsAfterOverwrap` | get / log / ring / buffer / wraps / after / overwrap | ログ集計回帰 / get / log / ring / buffer / wraps / after / overwrap | 良い | - | - |
| `testDisplayLogDoesNotMutateLoggerState` | display / log / does / not / mutate / logger / state | ログ集計回帰 / display / log / does / not / mutate / logger / state | 良い | - | - |

### `MessageBundleTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `メッセージ束の構造回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - メッセージ候補配列の保持とサイズが壊れない
  - MessageTag の列挙順と構造が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorInitializesEmptyBundle` | constructor / initializes / empty / bundle | メッセージ束の構造回帰 / constructor / initializes / empty / bundle | 良い | - | - |
| `testSettersRoundTripStoreAssignedValues` | setters / round / trip / store / assigned / values | メッセージ束の構造回帰 / setters / round / trip / store / assigned / values | 良い | - | - |
| `testMessageTagEnumValuesStayOrdered` | message / tag / enum / values / stay / ordered | メッセージ束の構造回帰 / message / tag / enum / values / stay / ordered | 良い | - | - |

### `MessagePoolTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `メッセージ置換/タグ選択回帰`
- 回帰目的:
  - プレースホルダ置換とタグ選択が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testPlaceholderReplacementNameAndName2` | placeholder / replacement / name / name2 | メッセージ置換/タグ選択回帰 / placeholder / replacement / name / name2 | 良い | - | - |
| `testPlaceholderReplacementPartner` | placeholder / replacement / partner | メッセージ置換/タグ選択回帰 / placeholder / replacement / partner | 良い | - | - |
| `testTagSelectionDamage` | tag / selection / damage | メッセージ置換/タグ選択回帰 / tag / selection / damage | 良い | - | - |
| `testTagSelectionPants` | tag / selection / pants | メッセージ置換/タグ選択回帰 / tag / selection / pants | 良い | - | - |

### `ResourceUtilTest`
- 状態: 完了 (4/4 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - リソース読み込み経路が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetInstance` | シングルトンが同一インスタンスを返しコア属性が読めること | UI/設定/入力/メッセージ回帰 / シングルトン回帰 | 良い | - | - |
| `testReadProperty` | title/version が存在し非空で数字を含む形式であること | UI/設定/入力/メッセージ回帰 / コアプロパティ回帰 | 良い | - | - |
| `testReadNonExistentProperty` | 存在しないキーは null を返し存在するキーは非nullであること | UI/設定/入力/メッセージ回帰 / null返り対比回帰 | 良い | - | - |
| `testGameLocaleIsJapanese` | getLocale が非null で isJapanese と整合していること | UI/設定/入力/メッセージ回帰 / ロケール整合回帰 | 良い | - | - |

### `SpriteTest`
- 状態: 完了 (14/14 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testConstructorCenterCenter` | 全初期値と CENTER ピボット計算、BOTTOM との対比確認 | UI/設定/入力/メッセージ回帰 / CENTER_CENTER 初期値回帰 | 良い | - | - |
| `testConstructorCenterBottom` | BOTTOM ピボット計算と CENTER との pivotY 差の確認 | UI/設定/入力/メッセージ回帰 / CENTER_BOTTOM 回帰 | 良い | - | - |
| `testDefaultConstructor` | デフォルトコンストラクタの全初期値が 0 であること | UI/設定/入力/メッセージ回帰 / default 初期値回帰 | 良い | - | - |
| `testSetSpriteSize` | setSpriteSize でimage・pivot・originalの独立性を確認 | UI/設定/入力/メッセージ回帰 / setSpriteSize 回帰 | 良い | - | - |
| `testAddSpriteSize` | addSpriteSize でoriginal+delta、pivot再計算、original不変を確認 | UI/設定/入力/メッセージ回帰 / addSpriteSize 回帰 | 良い | - | - |
| `testCalcScreenRect` | 左右rectの座標・サイズ・ミラーwidthと高さ同一を確認 | UI/設定/入力/メッセージ回帰 / calcScreenRect 回帰 | 良い | - | - |
| `testSetPivotType` | type変更後にサイズ変更で新typeのpivot計算が適用されること | UI/設定/入力/メッセージ回帰 / setPivotType 遅延計算回帰 | 良い | - | - |
| `testGettersSetters` | 複数setter後のgetter確認とoriginal/image独立性の確認 | UI/設定/入力/メッセージ回帰 / getters/setters 独立性回帰 | 良い | - | - |
| `testPivotCalculationCenter` | 3サイズでの CENTER pivot計算（w>>1, h>>1）を確認 | UI/設定/入力/メッセージ回帰 / CENTER pivot 計算回帰 | 良い | - | - |
| `testPivotCalculationBottom` | 3サイズでの BOTTOM pivot計算（w>>1, h-1）を確認 | UI/設定/入力/メッセージ回帰 / BOTTOM pivot 計算回帰 | 良い | - | - |
| `testScreenRectLeftRight` | 左右rectが同X起点・正負widthを持ち同じ高さを共有すること | UI/設定/入力/メッセージ回帰 / 左右rect 対称回帰 | 良い | - | - |
| `testOriginalVsImageSize` | setSpriteSize後もoriginalが不変でimageが更新されること | UI/設定/入力/メッセージ回帰 / original 不変回帰 | 良い | - | - |
| `testScenarioSetPivotTypeAloneDoesNotRecalculatePivotUntilSizeChanges` | type変更では直前型のpivotが維持されsize変更後に新型で再計算 | UI/設定/入力/メッセージ回帰 / pivot 遅延再計算回帰 | 良い | - | - |
| `testScenarioAddSpriteSizeAlwaysUsesOriginalSizeRatherThanAccumulating` | シナリオ / 追加 / sprite / size / always / uses / original / size / rather / than / accumulating | UI/設定/入力/メッセージ回帰 / シナリオ / 追加 / sprite / size / always / uses / original / size / rather / than / accumulating | 良い | - | - |

### `WorldStateTest`
- 状態: 完了 (50/50 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない
  - イベント進行と状態遷移が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testClearMap` | 解除 / map | UI/設定/入力/メッセージ回帰 / 解除 / map | 良い | - | - |
| `testSetFiledFlag` | 設定 / filed / flag | UI/設定/入力/メッセージ回帰 / 設定 / filed / flag | 良い | - | - |
| `testSetWallLine` | 設定 / 壁 / line | UI/設定/入力/メッセージ回帰 / 設定 / 壁 / line | 良い | - | - |
| `testGetters` | getters | UI/設定/入力/メッセージ回帰 / getters | 良い | - | - |
| `testSetGetMapIndex` | 設定 / 取得 / map / index | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / map / index | 良い | - | - |
| `testSetGetAlarm` | 設定 / 取得 / alarm | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / alarm | 良い | - | - |
| `testSetGetAlarmPeriod` | 設定 / 取得 / alarm / period | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / alarm / period | 良い | - | - |
| `testSetGetBody` | 設定 / 取得 / 本体 | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / 本体 | 良い | - | - |
| `testSetGetShit` | 設定 / 取得 / shit | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / shit | 良い | - | - |
| `testSetGetVomit` | 設定 / 取得 / vomit | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / vomit | 良い | - | - |
| `testSetGetBarrier` | 設定 / 取得 / barrier | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / barrier | 良い | - | - |
| `testSetGetEvent` | 設定 / 取得 / イベント | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / イベント | 良い | - | - |
| `testSetGetSortEffect` | 設定 / 取得 / sort / effect | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / sort / effect | 良い | - | - |
| `testSetGetFrontEffect` | 設定 / 取得 / front / effect | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / front / effect | 良い | - | - |
| `testSetGetFood` | 設定 / 取得 / food | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / food | 良い | - | - |
| `testSetGetTakenOutFood` | 設定 / 取得 / taken / out / food | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / taken / out / food | 良い | - | - |
| `testSetGetTakenOutShit` | 設定 / 取得 / taken / out / shit | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / taken / out / shit | 良い | - | - |
| `testSetGetToilet` | 設定 / 取得 / toilet | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / toilet | 良い | - | - |
| `testSetGetBed` | 設定 / 取得 / bed | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / bed | 良い | - | - |
| `testSetGetToy` | 設定 / 取得 / toy | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / toy | 良い | - | - |
| `testSetGetStone` | 設定 / 取得 / stone | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / stone | 良い | - | - |
| `testSetGetTrampoline` | 設定 / 取得 / trampoline | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / trampoline | 良い | - | - |
| `testSetGetBreedingPool` | 設定 / 取得 / breeding / pool | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / breeding / pool | 良い | - | - |
| `testSetGetGarbageChute` | 設定 / 取得 / garbage / chute | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / garbage / chute | 良い | - | - |
| `testSetGetFoodMaker` | 設定 / 取得 / food / maker | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / food / maker | 良い | - | - |
| `testSetGetOrangePool` | 設定 / 取得 / orange / pool | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / orange / pool | 良い | - | - |
| `testSetGetProductChute` | 設定 / 取得 / product / chute | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / product / chute | 良い | - | - |
| `testSetGetStickyPlate` | 設定 / 取得 / sticky / plate | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / sticky / plate | 良い | - | - |
| `testSetGetHotPlate` | 設定 / 取得 / hot / plate | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / hot / plate | 良い | - | - |
| `testSetGetProcessorPlate` | 設定 / 取得 / processor / plate | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / processor / plate | 良い | - | - |
| `testSetGetMixer` | 設定 / 取得 / mixer | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / mixer | 良い | - | - |
| `testSetGetAutoFeeder` | 設定 / 取得 / auto / feeder | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / auto / feeder | 良い | - | - |
| `testSetGetMachinePress` | 設定 / 取得 / machine / press | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / machine / press | 良い | - | - |
| `testSetGetStalk` | 設定 / 取得 / stalk | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / stalk | 良い | - | - |
| `testSetGetDiffuser` | 設定 / 取得 / diffuser | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / diffuser | 良い | - | - |
| `testSetGetYunba` | 設定 / 取得 / yunba | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / yunba | 良い | - | - |
| `testSetGetSui` | 設定 / 取得 / sui | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / sui | 良い | - | - |
| `testSetGetTrash` | 設定 / 取得 / trash | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / trash | 良い | - | - |
| `testSetGetGarbageStation` | 設定 / 取得 / garbage / station | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / garbage / station | 良い | - | - |
| `testSetGetHouse` | 設定 / 取得 / house | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / house | 良い | - | - |
| `testSetGetBeltconveyorObj` | 設定 / 取得 / beltconveyor / obj | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / beltconveyor / obj | 良い | - | - |
| `testSetGetBeltconveyor` | 設定 / 取得 / beltconveyor | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / beltconveyor | 良い | - | - |
| `testSetGetPool` | 設定 / 取得 / pool | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / pool | 良い | - | - |
| `testSetGetFarm` | 設定 / 取得 / farm | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / farm | 良い | - | - |
| `testSetGetOkazari` | 設定 / 取得 / okazari | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / okazari | 良い | - | - |
| `testSetGetHasDos` | 設定 / 取得 / 有無 / dos | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / 有無 / dos | 良い | - | - |
| `testSetGetWallMap` | 設定 / 取得 / 壁 / map | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / 壁 / map | 良い | - | - |
| `testSetGetFieldMap` | 設定 / 取得 / field / map | UI/設定/入力/メッセージ回帰 / 設定 / 取得 / field / map | 良い | - | - |
| `testScenarioSetFiledFlagWritesToCurrentWorldFieldMapNotPassedArray` | シナリオ / 設定 / filed / flag / writes / to / current / world / field / map / 非 / passed / array | UI/設定/入力/メッセージ回帰 / シナリオ / 設定 / filed / flag / writes / to / current / world / field / map / 非 / passed / array | 良い | - | - |
| `testScenarioSetWallLineMarksPrimaryAndAdjacentCellsThenClearsThem` | シナリオ / 設定 / 壁 / line / marks / primary / and / adjacent / cells / then / clears / them | UI/設定/入力/メッセージ回帰 / シナリオ / 設定 / 壁 / line / marks / primary / and / adjacent / cells / then / clears / them | 良い | - | - |

## `org.simyukkuri.ui`
### `ItemListenerTest`
- 状態: 完了 (6/6 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - メニュー構成と選択状態が壊れない
  - コレクションの追加/削除/参照が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetPopupActionPausesAndResumesSpeed` | popup visible/invisible でスピードが 0 に落ちて元に戻ること | UI/設定/入力/メッセージ回帰 / popup スピード一時停止回帰 | 良い | - | - |
| `testUsePopupActionPausesAndResumesSpeed` | popup visible/invisible で index と selectedGameSpeed が両方元に戻ること | UI/設定/入力/メッセージ回帰 / popup スピード復元回帰 | 良い | - | - |
| `testGetMenuActionPickupBody` | pickup で registry 削除・inventory 追加・isTaken・前後対比 | UI/設定/入力/メッセージ回帰 / body pickup 回帰 | 良い | - | - |
| `testGetMenuActionPickupBodyDetachesFromStalk` | pickup で stalk から detach・bindStalk=null・parentLinkId=-1 | UI/設定/入力/メッセージ回帰 / stalk detach 回帰 | 良い | - | - |
| `testGetMenuActionPickupShit` | shit pickup で shit map 削除・inventory 追加・前後対比 | UI/設定/入力/メッセージ回帰 / shit pickup 回帰 | 良い | - | - |
| `testGetMenuActionPickupVomit` | vomit pickup で vomit map 削除・inventory 追加・前後対比 | UI/設定/入力/メッセージ回帰 / vomit pickup 回帰 | 良い | - | - |

### `MainCommandListenerTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - メニュー構成と選択状態が壊れない
  - コレクションの追加/削除/参照が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGameSpeedComboBoxListenerUpdatesSelectedSpeed` | index 変更でゲームスピードが正しく更新・変化すること | UI/設定/入力/メッセージ回帰 / ゲームスピード変化回帰 | 良い | - | - |
| `testMainItemComboBoxListenerUpdatesGadgetMenu` | index 変更で selectMain が正しく更新・変化すること | UI/設定/入力/メッセージ回帰 / メインメニュー変化回帰 | 良い | - | - |
| `testSubItemComboBoxListenerUpdatesGadgetMenu` | index 変更で selectSub が正しく更新・変化すること | UI/設定/入力/メッセージ回帰 / サブメニュー変化回帰 | 良い | - | - |

### `MainCommandUiTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - 状態表示のラベル更新が壊れない
  - プレイヤー状態表示の更新が壊れない
  - Body 状態からの表示更新が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testClearStatusResetsStatusLabelsAndIcons` | status / labels / and / icons / reset | UI/設定/入力/メッセージ回帰 / status / labels / and / icons / reset | 良い | - | - |
| `testShowPlayerStatusUpdatesCashAndSpermIcon` | show / player / status / updates / cash / and / sperm / icon | UI/設定/入力/メッセージ回帰 / show / player / status / updates / cash / and / sperm / icon | 良い | - | - |
| `testShowStatusReflectsBodyState` | show / status / reflects / body / state | UI/設定/入力/メッセージ回帰 / show / status / reflects / body / state | 良い | - | - |

### `MainCommandUITest`
- 状態: 完了 (17/17 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - MENU_PANE_X 定数の保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMenuPaneXConstant` | menu / pane / x / constant | UI/設定/入力/メッセージ回帰 / constant | 良い | - | - |
| `testGetSetSelectedGameSpeed` | 取得 / 設定 / selected / game / speed | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetSelectedZoomScale` | 取得 / 設定 / selected / zoom / scale | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetGameSpeedCombo` | 取得 / 設定 / game / speed / combo | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetMainItemCombo` | 取得 / 設定 / main / item / combo | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetSubItemCombo` | 取得 / 設定 / sub / item / combo | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetYuStatusLabel` | 取得 / 設定 / yu / status / label | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetStatIconLabel` | 取得 / 設定 / stat / icon / label | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetItemIconLabel` | 取得 / 設定 / item / icon / label | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetSystemButton` | 取得 / 設定 / system / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetScriptButton` | 取得 / 設定 / script / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetTargetButton` | 取得 / 設定 / target / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetPinButton` | 取得 / 設定 / pin / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetHelpButton` | 取得 / 設定 / help / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetOptionButton` | 取得 / 設定 / option / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetPlayerButton` | 取得 / 設定 / player / button | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |
| `testGetSetOptionPopup` | 取得 / 設定 / option / popup | UI/設定/入力/メッセージ回帰 / getter / setter | 良い | - | - |

### `ShowStatusFrameTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - UI/設定/入力/メッセージ回帰

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGiveBodyInfoUpdatesSelectBody` | setSelectedYukkuri/getSelectedYukkuri の往復と null クリアが正しく動作すること | UI/設定/入力/メッセージ回帰 / selectedYukkuri 回帰 | 良い | - | headless 環境で実行可能な形に書き直し |

### `WorldSelectionWindowTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - ワールド選択候補の filePath が壊れない
  - closing 時に MOVE ボタン選択が解除される

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testWorldSelectionEnumValues` | world / selection / enum / values | UI/設定/入力/メッセージ回帰 / world / selection / enum / values | 良い | - | - |
| `testWorldSelectionEnumValueOf` | world / selection / enum / value / of | UI/設定/入力/メッセージ回帰 / world / selection / enum / value / of | 良い | - | - |
| `testWindowClosingClearsMoveButton` | closing / clears / move / button | UI/設定/入力/メッセージ回帰 / closing / clears / move / button | 良い | - | - |

### `YukkuriFilterPanelTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `UI/設定/入力/メッセージ回帰`
- 回帰目的:
  - フィルタ全選択/全解除が壊れない
  - `Action` enum の公開契約が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testActionEnumValues` | action / enum / values | UI/設定/入力/メッセージ回帰 / action / enum / values | 良い | - | - |
| `testActionEnumValueOf` | action / enum / value / of | UI/設定/入力/メッセージ回帰 / action / enum / value / of | 良い | - | - |
| `testButtonListenerSelectAll` | select / all | UI/設定/入力/メッセージ回帰 / select / all | 良い | - | - |
| `testButtonListenerDeselectAll` | deselect / all | UI/設定/入力/メッセージ回帰 / deselect / all | 良い | - | - |
| `testButtonListenerWithoutCheckboxIsNoop` | without / checkbox / is / noop | UI/設定/入力/メッセージ回帰 / without / checkbox / is / noop | 良い | - | - |

## `org.simyukkuri.util`
### `BodyUtilTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - 描画呼び出しが例外を投げない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDrawBodyBasic` | 描画後に少なくとも 1 ピクセルが変化すること | 基盤ユーティリティ回帰 / draw ピクセル回帰 | 良い | - | - |
| `testDrawBodyFullCoverage` | 多数の状態で描画後に少なくとも 1 ピクセルが変化すること | 基盤ユーティリティ回帰 / draw 多状態回帰 | 良い | - | - |
| `testConstructor_doesNotThrow` | YukkuriUtil のインスタンスが生成されること | 基盤ユーティリティ回帰 / constructor 回帰 | 良い | - | - |

### `GameEnvironmentTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - 環境 source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testEnvironmentAccessUsesOverrideWhenSet` | environment / access / uses / override / when / 設定 | 基盤ユーティリティ回帰 / environment / access / uses / override / when / 設定 | 良い | - | - |

### `GameImagesTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - 画像 source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testReadUsesOverrideWhenSet` | read / uses / override / when / 設定 | 基盤ユーティリティ回帰 / read / uses / override / when / 設定 | 良い | - | - |
| `testReadFileUsesOverrideWhenSet` | read / file / uses / override / when / 設定 | 基盤ユーティリティ回帰 / read / file / uses / override / when / 設定 | 良い | - | - |

### `GameLocaleTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - locale source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetLocaleUsesOverrideWhenSet` | 取得 / locale / uses / override / when / 設定 | 基盤ユーティリティ回帰 / 取得 / locale / uses / override / when / 設定 | 良い | - | - |
| `testIsJapaneseUsesOverrideLanguage` | 状態 / japanese / uses / override / language | 基盤ユーティリティ回帰 / 状態 / japanese / uses / override / language | 良い | - | - |

### `GameMessagesTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - message source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testLoadAndGetMessageUseOverrideWhenSet` | ロード / and / 取得 / メッセージ / use / override / when / 設定 | 基盤ユーティリティ回帰 / ロード / and / 取得 / メッセージ / use / override / when / 設定 | 良い | - | - |

### `GameTextTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - text source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testReadUsesOverrideWhenSet` | read / uses / override / when / 設定 | 基盤ユーティリティ回帰 / read / uses / override / when / 設定 | 良い | - | - |

### `GameViewTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - view source の override 伝播が壊れない
  - terrarium action source の override 伝播が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testViewAccessUsesOverrideWhenSet` | view / access / uses / override / when / 設定 | 基盤ユーティリティ回帰 / view / access / uses / override / when / 設定 | 良い | - | - |
| `testTerrariumActionsUseOverrideWhenSet` | terrarium / actions / use / override / when / 設定 | 基盤ユーティリティ回帰 / terrarium / actions / use / override / when / 設定 | 良い | - | - |

### `GameWorldTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - world source の override 伝播が壊れない
  - 既定の GameWorld 参照と SimYukkuri.world の連携が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetDelegatesToSimYukkuriWorldByDefault` | 取得 / delegates / to / sim / yukkuri / world / by / default | 基盤ユーティリティ回帰 / 取得 / delegates / to / sim / yukkuri / world / by / default | 良い | - | - |
| `testSetUpdatesSimYukkuriWorld` | 設定 / updates / sim / yukkuri / world | 基盤ユーティリティ回帰 / 設定 / updates / sim / yukkuri / world | 良い | - | - |
| `testGetUsesOverrideWhenSet` | 取得 / uses / override / when / 設定 | 基盤ユーティリティ回帰 / 取得 / uses / override / when / 設定 | 良い | - | - |

### `ImageCodePrinterTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - mainメソッドが例外で落ちない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMainDoesNotThrow` | 出力行数が ImageCode.values().length と一致し先頭行の形式が正しいこと | 基盤ユーティリティ回帰 / 出力内容回帰 | 良い | - | - |

### `IniFileUtilTest`
- 状態: 完了 (5/5 良い)
- クラス要約: `INI キャッシュ回帰`
- 回帰目的:
  - キャッシュした INI 値が force なしで再利用される
  - yukkuri INI のキャッシュした名前/値がそのまま復元される

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testClassExistence` | IniFileUtil がインスタンス化・リフレクション取得できること | INI キャッシュ回帰 / クラス存在回帰 | 良い | - | - |
| `testReadYukkuriIniFileNullSafety` | null 引数で NPE か正常終了のみ（他の例外は不可）であること | INI キャッシュ回帰 / null 安全契約回帰 | 良い | - | - |
| `testReadYukkuriIniFileWithValidBody` | 有効ボディで呼べ例外時でもデフォルト値が保持されること | INI キャッシュ回帰 / 有効ボディ耐性回帰 | 良い | - | - |
| `testReadYukkuriIniFileWithForceFlag` | force=true で呼べ例外時でもデフォルト値が保持されること | INI キャッシュ回帰 / force フラグ耐性回帰 | 良い | - | - |
| `testConstructor_doesNotThrow` | IniFileUtil インスタンスが生成でき非null であること | INI キャッシュ回帰 / constructor 回帰 | 良い | - | - |

### `ListOperationsTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - コレクションの追加/削除/参照が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testRemoveFirstMatchingValue` | 除去 / first / matching / value | 基盤ユーティリティ回帰 / 除去フラグ回帰 | 良い | - | - |

### `StabilityNPETest`
- 状態: 完了 (3/3 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - null 入力でも NPE が発生しない安全性が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHybridYukkuri_getImage_withNullElements_shouldNotThrow` | hybrid / yukkuri / 取得 / image / with / null / elements | 基盤ユーティリティ回帰 / null / 安全 | 良い | - | - |
| `testMessagePool_getMessage_withNullName_shouldNotThrow` | NPEなしで実行し戻り値が null か有効文字列であること | 基盤ユーティリティ回帰 / null 名前 NPE安全回帰 | 良い | - | テスト環境では null 返りも許容 |
| `testBody_Constructor_withoutGameWorld_shouldNotThrow` | GameWorld=null でも Reimu 生成が成功し AgeState が正しいこと | 基盤ユーティリティ回帰 / GameWorld未初期化コンストラクタ回帰 | 良い | - | - |

### `StabilityNpeTest`
- 状態: 完了 (2/2 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - 画像・描画用データが壊れない
  - 生成時の初期値や生成結果が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHybridYukkuriGetImageWithNullElementsShouldNotThrow` | hybrid / yukkuri / 取得 / image / with / null / elements / should / 非 / 例外 | 基盤ユーティリティ回帰 / hybrid / yukkuri / 取得 / image / with / null / elements / should / 非 / 例外 | 良い | - | - |
| `testBodyConstructorWithoutGameWorldPreservesRequestedAgeState` | 本体 / constructor / without / game / world / preserves / requested / age / state | 基盤ユーティリティ回帰 / 本体 / constructor / without / game / world / preserves / requested / age / state | 良い | - | - |

### `YukkuriLookupTest`
- 状態: 完了 (3/3 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - レジストリ参照と列挙が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testGetBodyInstance` | 登録 / 本体 / id / lookup | 基盤ユーティリティ回帰 / 登録本体参照回帰 | 良い | - | 同一個体が ID で返ることと、未登録 ID が null であることを確認 |
| `testGetBodyInstanceFromObjId` | 登録 / 本体 / obj / id / lookup | 基盤ユーティリティ回帰 / オブジェクト ID 参照回帰 | 良い | - | 同一個体が objId で返ることと、未登録 objId が null であることを確認 |
| `testGetBodyInstances` | 登録 / 本体 / 列挙 | 基盤ユーティリティ回帰 / レジストリ列挙回帰 | 良い | - | レジストリに入れた 2 個体が列挙で拾えることを確認 |

### `YukkuriTypeMappingTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - enum の className / typeId の相互変換が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testTypeClassNameRoundTrip` | type / class / name / round / trip | 基盤ユーティリティ回帰 / enum 相互変換回帰 | 良い | - | 全 enum 値の往復と、未知値/null の null 返却を確認 |

### `YukkuriUtilTest`
- 状態: 完了 (25/25 良い)
- クラス要約: `基盤ユーティリティ回帰`
- 回帰目的:
  - プロパティの更新と保持が壊れない
  - 変換コピーで参照共有や型崩れが起きない
  - 乱数依存の型選択が壊れない
  - アリ付着判定の条件分岐が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testRemoveContent` | remove / first / matching / value | 基盤ユーティリティ回帰 / リスト操作回帰 | 良い | - | 先頭一致の要素だけ削除し、見つからなければ無変更であることを確認 |
| `testChangeBody` | change / 本体 | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | age / damage のコピーと、変更後にコピー先へ波及しないことを確認 |
| `testChangeBodyReimuToReimuCopiesFields` | change / 本体 / reimu / to / reimu / copies / fields | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | x / y / damage のコピーと、変更後にコピー先へ波及しないことを確認 |
| `testChangeBodyMarisaToReimuCopiesFields` | change / 本体 / marisa / to / reimu / copies / fields | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | 型が Reimu のまま x / y をコピーすることを確認 |
| `testChangeBodyCopiesX` | change / 本体 / copies / x | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | x のコピーと、変更後にコピー先へ波及しないことを確認 |
| `testChangeBodyDoesNotShareMutableRelations` | change / 本体 / does / 非 / share / mutable / relations | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | partner / parents / children / sisters の深いコピーと独立性を確認 |
| `testChangeBodyCopiesBodyNameSetDeeply` | change / 本体 / copies / 本体 / name / 設定 / deeply | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | 名前セットの深いコピーと独立性を確認 |
| `testChangeBodyCopiesBodySpriteSetDeeply` | change / 本体 / copies / 本体 / sprite / 設定 / deeply | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | Sprite セットの深いコピーと独立性を確認 |
| `testChangeBodyCopiesBodyStatProfileDeeply` | change / 本体 / copies / 本体 / stat / profile / deeply | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | stat profile の配列コピーと独立性を確認 |
| `testChangeBodyCopiesBodyTimingProfileDeeply` | change / 本体 / copies / 本体 / timing / profile / deeply | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | timing profile の値コピーと独立性を確認 |
| `testChangeBodyCopiesBodyBehaviorProfileDeeply` | change / 本体 / copies / 本体 / behavior / profile / deeply | 基盤ユーティリティ回帰 / 変換コピー回帰 | 良い | - | behavior profile の値コピーと独立性を確認 |
| `testScenarioDosParentRandomTypeFallsBackToConcreteMarisaSubtype` | シナリオ / dos / 親 / random / type / falls / back / to / concrete / marisa / subtype | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | Dos 親のランダム分岐が具体種へ落ちることを確認 |
| `testScenarioNullParentRareRollYieldsSpecificRareType` | シナリオ / null / 親 / rare / roll / yields / specific / rare / type | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | 親なしで rare ロール時に特定種になることを確認 |
| `testScenarioNonDosParentKeepsItsOwnTypeOnParentBranch` | シナリオ / non / dos / 親 / 維持 / its / own / type / on / 親 / branch | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | 非 Dos 親が親分岐で自種を維持することを確認 |
| `testScenarioNullParentParentBranchCanYieldPlainMyon` | シナリオ / null / 親 / 親 / branch / 可否 / yield / plain / myon | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | 親なしの親分岐が plain myon を返し得ることを確認 |
| `testScenarioRandomBranchMapsAliceSlotToArisu` | シナリオ / random / branch / maps / alice / slot / to / arisu | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | random 分岐の Alice スロット対応を確認 |
| `testScenarioRandomBranchCanYieldSpecificRareType` | シナリオ / random / branch / 可否 / yield / specific / rare / type | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | random 分岐で特定 rare 種が出ることを確認 |
| `testScenarioChangelingCanYieldRareSubtype` | シナリオ / changeling / 可否 / yield / rare / subtype | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | changeling が rare subtype を返すことを確認 |
| `testScenarioChangelingCanYieldDeibuFromReimuBranch` | シナリオ / changeling / 可否 / yield / deibu / from / reimu / branch | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | changeling の Reimu 分岐が DEIBU を返すことを確認 |
| `testScenarioGetMarisaTypeCanYieldKotatsumuri` | シナリオ / 取得 / marisa / type / 可否 / yield / kotatsumuri | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | Marisa 型のランダム選択が kotatsumuri を返し得ることを確認 |
| `testScenarioGetMarisaTypeCanYieldTsumuri` | シナリオ / 取得 / marisa / type / 可否 / yield / tsumuri | 基盤ユーティリティ回帰 / 生誕型選択回帰 | 良い | - | Marisa 型のランダム選択が tsumuri を返し得ることを確認 |
| `testScenarioJudgeNewAntHitAddsAttachmentAndSetsAntCount` | シナリオ / judge / new / ant / hit / adds / attachment / and / sets / ant / count | 基盤ユーティリティ回帰 / アリ付着回帰 | 良い | - | ヒットで Ants が付与され antCount が設定されることを確認 |
| `testScenarioJudgeNewAntDirtyAndDontJumpHalveProbabilityTwice` | シナリオ / judge / new / ant / dirty / and / dont / jump / halve / probability / twice | 基盤ユーティリティ回帰 / アリ付着回帰 | 良い | - | dirty / dontJump による確率低下が二重に効くことを確認 |
| `testGetBodyInstanceFromObjId_found_returnsBody` | 取得 / 本体 / instance / from / obj / id / found / 戻り / 本体 | 基盤ユーティリティ回帰 / lookup 回帰 | 良い | - | - |
| `testGetBodyInstanceFromObjId_negativeOne_returnsNull` | 取得 / 本体 / instance / from / obj / id / negative / one / 戻り / null | 基盤ユーティリティ回帰 / lookup 回帰 | 良い | - | - |
| `testGetBodyInstanceFromObjId_notFound_returnsNull` | 取得 / 本体 / instance / from / obj / id / not / found / 戻り / null | 基盤ユーティリティ回帰 / lookup 回帰 | 良い | - | - |
| `testGetYukkuriClassName` | 取得 / yukkuri / class / name | 基盤ユーティリティ回帰 / 型変換回帰 | 良い | - | - |
| `testGetYukkuriType` | 取得 / yukkuri / type | 基盤ユーティリティ回帰 / 型変換回帰 | 良い | - | - |

## `org.simyukkuri.yukkuri`
### `AliceTest`
- 状態: 完了 (14/14 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAliceIdentity` | alice / identity | 基礎回帰 / alice / identity | 良い | - | - |
| `testAliceNames` | alice / names | 基礎回帰 / alice / names | 良い | - | - |
| `testAliceHybridType` | alice / hybrid / type | 基礎回帰 / alice / hybrid / type | 良い | - | - |
| `testAliceTuneParametersDoesNotSetRapist` | alice / tune / parameters / does / 非 / 設定 / rapist | 基礎回帰 / alice / tune / parameters / does / 非 / 設定 / rapist | 良い | - | - |
| `testAliceTuneParametersSetsRapist` | alice / tune / parameters / sets / rapist | 基礎回帰 / alice / tune / parameters / sets / rapist | 良い | - | - |
| `testAliceIsHybrid` | alice / 状態 / hybrid | 基礎回帰 / alice / 状態 / hybrid | 良い | - | - |
| `testAliceIsAliceRaperWhenNotRaper` | alice / 状態 / alice / raper / when / 非 / raper | 基礎回帰 / alice / 状態 / alice / raper / when / 非 / raper | 良い | - | - |
| `testAliceIsAliceRaperWhenRaper` | alice / 状態 / alice / raper / when / raper | 基礎回帰 / alice / 状態 / alice / raper / when / raper | 良い | - | - |
| `testAliceIsRaperExcitingFace` | alice / 状態 / raper / exciting / 表情 | 基礎回帰 / alice / 状態 / raper / exciting / 表情 | 良い | - | - |
| `testAliceDefaultConstructor` | alice / default / constructor | 基礎回帰 / alice / default / constructor | 良い | - | - |
| `testAliceParameterizedConstructor` | alice / parameterized / constructor | 基礎回帰 / alice / parameterized / constructor | 良い | - | - |
| `testAliceGetMountPoint` | alice / 取得 / mount / point | 基礎回帰 / alice / 取得 / mount / point | 良い | - | - |
| `testAliceCheckTransform` | alice / 判定 / transform | 基礎回帰 / alice / 判定 / transform | 良い | - | - |
| `testCoordinateExecutesCode` | coordinate / executes / code | 基礎回帰 / coordinate / executes / code | 良い | - | - |

### `AyayaTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testAyayaIdentity` | ayaya / identity | 基礎回帰 / ayaya / identity | 良い | - | - |
| `testAyayaNames` | ayaya / names | 基礎回帰 / ayaya / names | 良い | - | - |
| `testAyayaHybridType` | ayaya / hybrid / type | 基礎回帰 / ayaya / hybrid / type | 良い | - | - |
| `testAyayaIsHybrid` | ayaya / 状態 / hybrid | 基礎回帰 / ayaya / 状態 / hybrid | 良い | - | - |
| `testAyayaDefaultConstructor` | ayaya / default / constructor | 基礎回帰 / ayaya / default / constructor | 良い | - | - |
| `testAyayaParameterizedConstructor` | ayaya / parameterized / constructor | 基礎回帰 / ayaya / parameterized / constructor | 良い | - | - |
| `testAyayaGetMountPoint` | ayaya / 取得 / mount / point | 基礎回帰 / ayaya / 取得 / mount / point | 良い | - | - |
| `testAyayaCheckTransform` | ayaya / 判定 / transform | 基礎回帰 / ayaya / 判定 / transform | 良い | - | - |

### `BodySerializationTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testBodyAttributesSerialization` | 本体 / attributes / serialization | 基礎回帰 / 本体 / attributes / serialization | 良い | - | - |

### `ChenTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 個体識別と名前が壊れない
  - 混血規則が壊れない
  - 変換可否と mount point の既定挙動が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testChenIdentity` | chen / identity | 基礎回帰 / chen / identity | 良い | - | - |
| `testChenNames` | chen / names | 基礎回帰 / chen / names | 良い | - | - |
| `testChenHybridType` | chen / hybrid / type | 基礎回帰 / chen / hybrid / type | 良い | - | - |
| `testChenIsHybrid` | chen / 状態 / hybrid | 基礎回帰 / chen / 状態 / hybrid | 良い | - | - |
| `testChenParameterizedConstructor` | chen / parameterized / constructor | 基礎回帰 / chen / parameterized / constructor | 良い | - | - |
| `testChenGetMountPoint` | chen / 取得 / mount / point | 基礎回帰 / chen / 取得 / mount / point | 良い | - | - |
| `testChenCheckTransform` | chen / 判定 / transform | 基礎回帰 / chen / 判定 / transform | 良い | - | - |

### `ChirunoTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testChirunoIdentity` | chiruno / identity | 基礎回帰 / chiruno / identity | 良い | - | - |
| `testChirunoNames` | chiruno / names | 基礎回帰 / chiruno / names | 良い | - | - |
| `testChirunoHybridType` | chiruno / hybrid / type | 基礎回帰 / chiruno / hybrid / type | 良い | - | - |
| `testChirunoIsHybrid` | chiruno / 状態 / hybrid | 基礎回帰 / chiruno / 状態 / hybrid | 良い | - | - |
| `testChirunoParameterizedConstructor` | chiruno / parameterized / constructor | 基礎回帰 / chiruno / parameterized / constructor | 良い | - | - |
| `testChirunoGetMountPoint` | chiruno / 取得 / mount / point | 基礎回帰 / chiruno / 取得 / mount / point | 良い | - | - |
| `testChirunoCheckTransform` | chiruno / 判定 / transform | 基礎回帰 / chiruno / 判定 / transform | 良い | - | - |

### `DeibuTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 個体識別と名前が壊れない
  - 混血規則が壊れない
  - tuneParameters の決定的変更が壊れない
  - mount point と画像選択が壊れない
  - 変換可否と image variant の保持が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDeibuIdentity` | deibu / identity | 基礎回帰 / deibu / identity | 良い | - | - |
| `testDeibuParameterizedConstructor` | deibu / parameterized / constructor | 基礎回帰 / deibu / parameterized / constructor | 良い | - | - |
| `testDeibuNames` | deibu / names | 基礎回帰 / deibu / names | 良い | - | - |
| `testDeibuHybridType` | deibu / hybrid / type | 基礎回帰 / deibu / hybrid / type | 良い | - | - |
| `testDeibuTuneParameters` | deibu / tune / parameters | 基礎回帰 / deibu / tune / parameters | 良い | - | - |
| `testDeibuGetMountPointUnknownKeyReturnsNull` | deibu / 取得 / mount / point | 基礎回帰 / deibu / 取得 / mount / point | 良い | - | - |
| `testDeibuCheckTransformDoesNotTransformByDefault` | deibu / 判定 / transform | 基礎回帰 / deibu / 判定 / transform | 良い | - | - |
| `testDeibuImageVariantStateAccessor` | deibu / image / variant / state | 基礎回帰 / deibu / image / variant / state | 良い | - | - |
| `testGetImageSetsLayerImageAndDirection` | deibu / 取得 / image | 基礎回帰 / deibu / 取得 / image | 良い | - | - |
| `testDeibuExtendsReimu` | deibu / extends / reimu | 基礎回帰 / 継承 | 良い | - | - |
| `testDeibuHybridTypeWithMarisa` | deibu / hybrid / type / with / marisa | 基礎回帰 / 混血 / marisa | 良い | - | - |
| `testDeibuHybridTypeWithOther` | deibu / hybrid / type / with / other | 基礎回帰 / 混血 / other | 良い | - | - |
| `testDeibuNagasiMethods` | deibu / nagasi / methods | 基礎回帰 / nagasi | 良い | - | - |

### `DosMarisaTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 個体識別と名前が壊れない
  - 混血規則が壊れない
  - mount point と画像選択が壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testDosMarisaIdentity` | dos / marisa / identity | 基礎回帰 / dos / marisa / identity | 良い | - | - |
| `testDosMarisaNames` | dos / marisa / names | 基礎回帰 / dos / marisa / names | 良い | - | - |
| `testDosMarisaHybridType` | dos / marisa / hybrid / type | 基礎回帰 / dos / marisa / hybrid / type | 良い | - | - |
| `testDosMarisaExtendsMarisa` | dos / marisa / extends / marisa | 基礎回帰 / dos / marisa / extends / marisa | 良い | - | - |
| `testDosMarisaParameterizedConstructor` | dos / marisa / parameterized / constructor / type | 基礎回帰 / dos / marisa / parameterized / constructor | 良い | - | - |
| `testDosMarisaTuneParameters` | dos / marisa / tune / parameters / deterministic / random | 基礎回帰 / dos / marisa / tune / parameters | 良い | - | - |
| `testDosMarisaGetMountPoint` | dos / marisa / 取得 / mount / point / unknown / key | 基礎回帰 / dos / marisa / 取得 / mount / point | 良い | - | - |
| `testDosMarisaCheckTransform` | dos / marisa / 判定 / transform / returns / null | 基礎回帰 / dos / marisa / 判定 / transform | 良い | - | - |
| `testDosMarisaHybridTypeWithOther` | dos / marisa / hybrid / type / with / other | 基礎回帰 / 混血 / other | 良い | - | - |
| `testDosMarisaHybridTypeWithReimu` | dos / marisa / hybrid / type / with / reimu | 基礎回帰 / 混血 / reimu | 良い | - | - |
| `testDosMarisaHybridTypeWithWasaReimu` | dos / marisa / hybrid / type / with / wasa / reimu | 基礎回帰 / 混血 / wasa / reimu | 良い | - | - |

### `EikiTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testEikiIdentity` | eiki / identity | 基礎回帰 / eiki / identity | 良い | - | - |
| `testEikiNames` | eiki / names | 基礎回帰 / eiki / names | 良い | - | - |
| `testEikiHybridType` | eiki / hybrid / type | 基礎回帰 / eiki / hybrid / type | 良い | - | - |
| `testEikiIsHybrid` | eiki / 状態 / hybrid | 基礎回帰 / eiki / 状態 / hybrid | 良い | - | - |
| `testEikiParameterizedConstructor` | eiki / parameterized / constructor | 基礎回帰 / eiki / parameterized / constructor | 良い | - | - |
| `testEikiGetMountPoint` | eiki / 取得 / mount / point | 基礎回帰 / eiki / 取得 / mount / point | 良い | - | - |
| `testEikiCheckTransform` | eiki / 判定 / transform | 基礎回帰 / eiki / 判定 / transform | 良い | - | - |

### `FranTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testFranIdentity` | fran / identity | 基礎回帰 / fran / identity | 良い | - | - |
| `testFranNames` | fran / names | 基礎回帰 / fran / names | 良い | - | - |
| `testFranHybridType` | fran / hybrid / type | 基礎回帰 / fran / hybrid / type | 良い | - | - |
| `testFranIsHybrid` | fran / 状態 / hybrid | 基礎回帰 / fran / 状態 / hybrid | 良い | - | - |
| `testFranDefaultConstructor` | fran / default / constructor | 基礎回帰 / fran / default / constructor | 良い | - | - |
| `testFranParameterizedConstructor` | fran / parameterized / constructor | 基礎回帰 / fran / parameterized / constructor | 良い | - | - |
| `testFranGetMountPoint` | fran / 取得 / mount / point | 基礎回帰 / fran / 取得 / mount / point | 良い | - | - |
| `testFranCheckTransform` | fran / 判定 / transform | 基礎回帰 / fran / 判定 / transform | 良い | - | - |

### `HybridYukkuriTest`
- 状態: 完了 (24/24 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testHybridYukkuriIdentity` | hybrid / yukkuri / identity | 基礎回帰 / hybrid / yukkuri / identity | 良い | - | - |
| `testHybridYukkuriIsHybrid` | hybrid / yukkuri / 状態 / hybrid | 基礎回帰 / hybrid / yukkuri / 状態 / hybrid | 良い | - | - |
| `testHybridYukkuriHybridType` | hybrid / yukkuri / hybrid / type | 基礎回帰 / hybrid / yukkuri / hybrid / type | 良い | - | - |
| `testHybridYukkuriDoreiGettersSetters` | hybrid / yukkuri / dorei / getters / setters | 基礎回帰 / hybrid / yukkuri / dorei / getters / setters | 良い | - | - |
| `testHybridYukkuriGetBaseBody` | hybrid / yukkuri / 取得 / base / 本体 | 基礎回帰 / hybrid / yukkuri / 取得 / base / 本体 | 良い | - | - |
| `testHybridYukkuriNameGettersSetters` | hybrid / yukkuri / name / getters / setters | 基礎回帰 / hybrid / yukkuri / name / getters / setters | 良い | - | - |
| `testHybridYukkuriImagesGetterSetter` | hybrid / yukkuri / images / getter / setter | 基礎回帰 / hybrid / yukkuri / images / getter / setter | 良い | - | - |
| `testGetMyNameNullAnMyNameFallsBackToNameJ` | 取得 / my / name / null / an / my / name / falls / back / to / name / j | 基礎回帰 / 取得 / my / name / null / an / my / name / falls / back / to / name / j | 良い | - | - |
| `testGetMyNameDnullAnMyNameDfallsBackToNameJ` | 取得 / my / name / dnull / an / my / name / dfalls / back / to / name / j | 基礎回帰 / 取得 / my / name / dnull / an / my / name / dfalls / back / to / name / j | 良い | - | - |
| `testSetBodyRankNullDoreisDoesNotThrow` | 設定 / 本体 / rank / null / doreis / does / 非 / 例外 | 基礎回帰 / 設定 / 本体 / rank / null / doreis / does / 非 / 例外 | 良い | - | - |
| `testSetBodyRankWithDoreiSetsOnAll` | 設定 / 本体 / rank / with / dorei / sets / on / all | 基礎回帰 / 設定 / 本体 / rank / with / dorei / sets / on / all | 良い | - | - |
| `testGetBaseBodyOutOfRangeReturnsDorei4` | 取得 / base / 本体 / out / of / range / 戻り / dorei4 | 基礎回帰 / 取得 / base / 本体 / out / of / range / 戻り / dorei4 | 良い | - | - |
| `testSetImagesGetter` | 設定 / images / getter | 基礎回帰 / 設定 / images / getter | 良い | - | - |
| `testRemoveWithAllDoreisDoesNotThrow` | 除去 / with / all / doreis / does / 非 / 例外 | 基礎回帰 / 除去フラグ回帰 | 良い | - | - |
| `testGetMountPointDoreiNullThrowsNpe` | 取得 / mount / point / dorei / null / 例外 / npe | 基礎回帰 / 取得 / mount / point / dorei / null / 例外 / npe | 良い | - | - |
| `testGetMountPointWithDoreiDoesNotThrow` | 取得 / mount / point / with / dorei / does / 非 / 例外 | 基礎回帰 / 取得 / mount / point / with / dorei / does / 非 / 例外 | 良い | - | - |
| `testGetImageImagesNullThrowsNpe` | 取得 / image / images / null / 例外 / npe | 基礎回帰 / 取得 / image / images / null / 例外 / npe | 良い | - | - |
| `testGetImageAfterTuneParametersImagesSlotNullThrowsNpe` | 取得 / image / after / tune / parameters / images / slot / null / 例外 / npe | 基礎回帰 / 取得 / image / after / tune / parameters / images / slot / null / 例外 / npe | 良い | - | - |
| `testConstructorWithCoordsDoesNotThrow` | constructor / with / coords / does / 非 / 例外 | 基礎回帰 / constructor / with / coords / does / 非 / 例外 | 良い | - | - |
| `testLoadImagesHyblidNoParentsExecutesCode` | ロード / images / hyblid / なし / parents / executes / code | 基礎回帰 / 状態保存復元確認 | 良い | - | - |
| `testLoadImagesHyblidWithPresetDoreiExecutesCode` | ロード / images / hyblid / with / preset / dorei / executes / code | 基礎回帰 / 状態保存復元確認 | 良い | - | - |
| `testLoadImagesStaticDoesNotThrow` | ロード / images / static / does / 非 / 例外 | 基礎回帰 / 状態保存復元確認 | 良い | - | - |
| `testTuneParametersAndGetImagesSetsArray` | tune / parameters / and / 取得 / images / sets / array | 基礎回帰 / tune / parameters / and / 取得 / images / sets / array | 良い | - | - |
| `testGetHybridTypeAlwaysReturnsHybridType` | 取得 / hybrid / type / always / 戻り / hybrid / type | 基礎回帰 / 取得 / hybrid / type / always / 戻り / hybrid / type | 良い | - | - |

### `KimeemaruTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testKimeemaruIdentity` | kimeemaru / identity | 基礎回帰 / kimeemaru / identity | 良い | - | - |
| `testKimeemaruNames` | kimeemaru / names | 基礎回帰 / kimeemaru / names | 良い | - | - |
| `testKimeemaruHybridType` | kimeemaru / hybrid / type | 基礎回帰 / kimeemaru / hybrid / type | 良い | - | - |
| `testKimeemaruIsHybrid` | kimeemaru / 状態 / hybrid | 基礎回帰 / kimeemaru / 状態 / hybrid | 良い | - | - |
| `testKimeemaruParameterizedConstructor` | kimeemaru / parameterized / constructor | 基礎回帰 / kimeemaru / parameterized / constructor | 良い | - | - |
| `testKimeemaruGetMountPoint` | kimeemaru / 取得 / mount / point | 基礎回帰 / kimeemaru / 取得 / mount / point | 良い | - | - |
| `testKimeemaruCheckTransform` | kimeemaru / 判定 / transform | 基礎回帰 / kimeemaru / 判定 / transform | 良い | - | - |

### `MarisaKotatsumuriTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMarisaKotatsumuriIdentity` | marisa / kotatsumuri / identity | 基礎回帰 / marisa / kotatsumuri / identity | 良い | - | - |
| `testMarisaKotatsumuriNames` | marisa / kotatsumuri / names | 基礎回帰 / marisa / kotatsumuri / names | 良い | - | - |
| `testMarisaKotatsumuriExtendsBody` | marisa / kotatsumuri / extends / 本体 | 基礎回帰 / marisa / kotatsumuri / extends / 本体 | 良い | - | - |
| `testMarisaKotatsumuriParameterizedConstructor` | marisa / kotatsumuri / parameterized / constructor | 基礎回帰 / marisa / kotatsumuri / parameterized / constructor | 良い | - | - |
| `testMarisaKotatsumuriGetMountPoint` | marisa / kotatsumuri / 取得 / mount / point | 基礎回帰 / marisa / kotatsumuri / 取得 / mount / point | 良い | - | - |
| `testMarisaKotatsumuriCheckTransform` | marisa / kotatsumuri / 判定 / transform | 基礎回帰 / marisa / kotatsumuri / 判定 / transform | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | 基礎回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testGetAnImageVerStateCtrlNagasiDoesNotThrow` | 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 基礎回帰 / 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 良い | - | - |

### `MarisaReimuTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMarisaReimuIdentity` | marisa / reimu / identity | 基礎回帰 / marisa / reimu / identity | 良い | - | - |
| `testMarisaReimuExtendsReimu` | marisa / reimu / extends / reimu | 基礎回帰 / marisa / reimu / extends / reimu | 良い | - | - |
| `testMarisaReimuNames` | marisa / reimu / names | 基礎回帰 / marisa / reimu / names | 良い | - | - |
| `testMarisaReimuIsHybrid` | marisa / reimu / 状態 / hybrid | 基礎回帰 / marisa / reimu / 状態 / hybrid | 良い | - | - |
| `testMarisaReimuDefaultConstructor` | marisa / reimu / default / constructor | 基礎回帰 / marisa / reimu / default / constructor | 良い | - | - |
| `testMarisaReimuMyNames` | marisa / reimu / my / names | 基礎回帰 / marisa / reimu / my / names | 良い | - | - |
| `testMarisaReimuParameterizedConstructor` | marisa / reimu / parameterized / constructor | 基礎回帰 / marisa / reimu / parameterized / constructor | 良い | - | - |
| `testMarisaReimuGetMountPoint` | marisa / reimu / 取得 / mount / point | 基礎回帰 / marisa / reimu / 取得 / mount / point | 良い | - | - |
| `testMarisaReimuCheckTransform` | marisa / reimu / 判定 / transform | 基礎回帰 / marisa / reimu / 判定 / transform | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | 基礎回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testGetAnImageVerStateCtrlNagasiDoesNotThrow` | 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 基礎回帰 / 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 良い | - | - |

### `MarisaTest`
- 状態: 完了 (21/21 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMarisaIdentity` | marisa / identity | 基礎回帰 / marisa / identity | 良い | - | - |
| `testMarisaNames` | marisa / names | 基礎回帰 / marisa / names | 良い | - | - |
| `testMarisaHybridType` | marisa / hybrid / type | 基礎回帰 / marisa / hybrid / type | 良い | - | - |
| `testMarisaTuneParameters` | marisa / tune / parameters | 基礎回帰 / marisa / tune / parameters | 良い | - | - |
| `testMarisaParameterizedConstructor` | marisa / parameterized / constructor | 基礎回帰 / marisa / parameterized / constructor | 良い | - | - |
| `testMarisaNagasiMethods` | marisa / nagasi / methods | 基礎回帰 / marisa / nagasi / methods | 良い | - | - |
| `testMarisaCheckTransform` | marisa / 判定 / transform | 基礎回帰 / marisa / 判定 / transform | 良い | - | - |
| `testMarisaGetMountPoint` | marisa / 取得 / mount / point | 基礎回帰 / marisa / 取得 / mount / point | 良い | - | - |
| `testMarisaHybridTypeWithReimu` | marisa / hybrid / type / with / reimu | 基礎回帰 / marisa / hybrid / type / with / reimu | 良い | - | - |
| `testMarisaHybridTypeWithWasaReimu` | marisa / hybrid / type / with / wasa / reimu | 基礎回帰 / marisa / hybrid / type / with / wasa / reimu | 良い | - | - |
| `testMarisaHybridTypeWithOther` | marisa / hybrid / type / with / other | 基礎回帰 / marisa / hybrid / type / with / other | 良い | - | - |
| `testExecTransformCannotTransformDoesNotThrow` | exec / transform / cannot / transform / does / 非 / 例外 | 基礎回帰 / exec / transform / cannot / transform / does / 非 / 例外 | 良い | - | - |
| `testExecTransformReplacesBodyAtSameUniqueId` | exec / transform / replaces / 本体 / at / same / unique / id | 基礎回帰 / exec / transform / replaces / 本体 / at / same / unique / id | 良い | - | - |
| `testExecTransformPreservesPartnerAndChildRelations` | exec / transform / preserves / 相手 / and / 子 / relations | 基礎回帰 / exec / transform / preserves / 相手 / and / 子 / relations | 良い | - | - |
| `testExecTransformPregnantBodyKeepsPregnancyAndFamilyRelations` | exec / transform / pregnant / 本体 / 維持 / pregnancy / and / 家族 / relations | 基礎回帰 / exec / transform / pregnant / 本体 / 維持 / pregnancy / and / 家族 / relations | 良い | - | - |
| `testExecTransformStalkPregnantBodyKeepsStalkPregnancyAndFamilyRelations` | exec / transform / stalk / pregnant / 本体 / 維持 / stalk / pregnancy / and / 家族 / relations | 基礎回帰 / exec / transform / stalk / pregnant / 本体 / 維持 / stalk / pregnancy / and / 家族 / relations | 良い | - | - |
| `testLoadIniFileDoesNotThrow` | ロード / ini / file / does / 非 / 例外 | 基礎回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetBodyBaseImageNormalStateExecutesCode` | 取得 / 本体 / base / image / normal / state / executes / code | 基礎回帰 / 取得 / 本体 / base / image / normal / state / executes / code | 良い | - | - |
| `testGetBodyBaseImageBurnedDeadExecutesCode` | 取得 / 本体 / base / image / burned / 死亡 / executes / code | 基礎回帰 / 取得 / 本体 / base / image / burned / 死亡 / executes / code | 良い | - | - |
| `testGetBodyBaseImageCrushedExecutesCode` | 取得 / 本体 / base / image / crushed / executes / code | 基礎回帰 / 取得 / 本体 / base / image / crushed / executes / code | 良い | - | - |
| `testGetBodyBaseImagePealedExecutesCode` | 取得 / 本体 / base / image / pealed / executes / code | 基礎回帰 / 取得 / 本体 / base / image / pealed / executes / code | 良い | - | - |

### `MarisaTsumuriTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMarisaTsumuriIdentity` | marisa / tsumuri / identity | 基礎回帰 / marisa / tsumuri / identity | 良い | - | - |
| `testMarisaTsumuriNames` | marisa / tsumuri / names | 基礎回帰 / marisa / tsumuri / names | 良い | - | - |
| `testMarisaTsumuriExtendsMarisa` | marisa / tsumuri / extends / marisa | 基礎回帰 / marisa / tsumuri / extends / marisa | 良い | - | - |
| `testMarisaTsumuriParameterizedConstructor` | marisa / tsumuri / parameterized / constructor | 基礎回帰 / marisa / tsumuri / parameterized / constructor | 良い | - | - |
| `testMarisaTsumuriGetMountPoint` | marisa / tsumuri / 取得 / mount / point | 基礎回帰 / marisa / tsumuri / 取得 / mount / point | 良い | - | - |
| `testMarisaTsumuriCheckTransform` | marisa / tsumuri / 判定 / transform | 基礎回帰 / marisa / tsumuri / 判定 / transform | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | 基礎回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testGetAnImageVerStateCtrlNagasiDoesNotThrow` | 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 基礎回帰 / 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 良い | - | - |

### `MeirinTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMeirinIdentity` | meirin / identity | 基礎回帰 / meirin / identity | 良い | - | - |
| `testMeirinNames` | meirin / names | 基礎回帰 / meirin / names | 良い | - | - |
| `testMeirinHybridType` | meirin / hybrid / type | 基礎回帰 / meirin / hybrid / type | 良い | - | - |
| `testMeirinIsHybrid` | meirin / 状態 / hybrid | 基礎回帰 / meirin / 状態 / hybrid | 良い | - | - |
| `testMeirinIsServantOfPredatorMasters` | meirin / 状態 / servant / of / predator / masters | 基礎回帰 / meirin / 状態 / servant / of / predator / masters | 良い | - | - |
| `testMeirinParameterizedConstructor` | meirin / parameterized / constructor | 基礎回帰 / meirin / parameterized / constructor | 良い | - | - |
| `testMeirinGetMountPoint` | meirin / 取得 / mount / point | 基礎回帰 / meirin / 取得 / mount / point | 良い | - | - |
| `testMeirinCheckTransform` | meirin / 判定 / transform | 基礎回帰 / meirin / 判定 / transform | 良い | - | - |

### `MyonTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMyonIdentity` | myon / identity | 基礎回帰 / myon / identity | 良い | - | - |
| `testMyonNames` | myon / names | 基礎回帰 / myon / names | 良い | - | - |
| `testMyonHybridType` | myon / hybrid / type | 基礎回帰 / myon / hybrid / type | 良い | - | - |
| `testMyonIsHybrid` | myon / 状態 / hybrid | 基礎回帰 / myon / 状態 / hybrid | 良い | - | - |
| `testMyonDefaultConstructor` | myon / default / constructor | 基礎回帰 / myon / default / constructor | 良い | - | - |
| `testMyonParameterizedConstructor` | myon / parameterized / constructor | 基礎回帰 / myon / parameterized / constructor | 良い | - | - |
| `testMyonGetMountPoint` | myon / 取得 / mount / point | 基礎回帰 / myon / 取得 / mount / point | 良い | - | - |
| `testMyonCheckTransform` | myon / 判定 / transform | 基礎回帰 / myon / 判定 / transform | 良い | - | - |

### `NitoriTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testNitoriIdentity` | nitori / identity | 基礎回帰 / nitori / identity | 良い | - | - |
| `testNitoriNames` | nitori / names | 基礎回帰 / nitori / names | 良い | - | - |
| `testNitoriHybridType` | nitori / hybrid / type | 基礎回帰 / nitori / hybrid / type | 良い | - | - |
| `testNitoriIsHybrid` | nitori / 状態 / hybrid | 基礎回帰 / nitori / 状態 / hybrid | 良い | - | - |
| `testNitoriParameterizedConstructor` | nitori / parameterized / constructor | 基礎回帰 / nitori / parameterized / constructor | 良い | - | - |
| `testNitoriGetMountPoint` | nitori / 取得 / mount / point | 基礎回帰 / nitori / 取得 / mount / point | 良い | - | - |
| `testNitoriCheckTransform` | nitori / 判定 / transform | 基礎回帰 / nitori / 判定 / transform | 良い | - | - |

### `PatchTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testPatchIdentity` | patch / identity | 基礎回帰 / patch / identity | 良い | - | - |
| `testPatchNames` | patch / names | 基礎回帰 / patch / names | 良い | - | - |
| `testPatchHybridType` | patch / hybrid / type | 基礎回帰 / patch / hybrid / type | 良い | - | - |
| `testPatchIsHybrid` | patch / 状態 / hybrid | 基礎回帰 / patch / 状態 / hybrid | 良い | - | - |
| `testPatchDefaultConstructor` | patch / default / constructor | 基礎回帰 / patch / default / constructor | 良い | - | - |
| `testPatchParameterizedConstructor` | patch / parameterized / constructor | 基礎回帰 / patch / parameterized / constructor | 良い | - | - |
| `testPatchGetMountPoint` | patch / 取得 / mount / point | 基礎回帰 / patch / 取得 / mount / point | 良い | - | - |
| `testPatchCheckTransform` | patch / 判定 / transform | 基礎回帰 / patch / 判定 / transform | 良い | - | - |

### `RanTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 境界値とクランプが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testRanIdentity` | ran / identity | 基礎回帰 / ran / identity | 良い | - | - |
| `testRanNames` | ran / names | 基礎回帰 / ran / names | 良い | - | - |
| `testRanHybridType` | ran / hybrid / type | 基礎回帰 / ran / hybrid / type | 良い | - | - |
| `testRanIsHybrid` | ran / 状態 / hybrid | 基礎回帰 / ran / 状態 / hybrid | 良い | - | - |
| `testRanDefaultConstructor` | ran / default / constructor | 基礎回帰 / ran / default / constructor | 良い | - | - |
| `testRanParameterizedConstructor` | ran / parameterized / constructor | 基礎回帰 / ran / parameterized / constructor | 良い | - | - |
| `testRanGetMountPoint` | ran / 取得 / mount / point | 基礎回帰 / ran / 取得 / mount / point | 良い | - | - |
| `testRanCheckTransform` | ran / 判定 / transform | 基礎回帰 / ran / 判定 / transform | 良い | - | - |

### `ReimuMarisaTest`
- 状態: 完了 (9/9 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testReimuMarisaIdentity` | reimu / marisa / identity | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaExtendsMarisa` | reimu / marisa / extends / marisa | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaNames` | reimu / marisa / names | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaIsHybrid` | reimu / marisa / 状態 / hybrid | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaParameterizedConstructor` | reimu / marisa / parameterized / constructor | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaGetMountPoint` | reimu / marisa / 取得 / mount / point | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuMarisaCheckTransform` | reimu / marisa / 判定 / transform | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | 基礎回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testGetAnImageVerStateCtrlNagasiDoesNotThrow` | 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 基礎回帰 / 取得 / an / image / ver / state / ctrl / nagasi / does / 非 / 例外 | 良い | - | - |

### `ReimuTest`
- 状態: 完了 (25/25 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testReimuIdentity` | reimu / identity | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuNames` | reimu / names | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuHybridType` | reimu / hybrid / type | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuDefaultConstructor` | reimu / default / constructor | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuTuneParameters` | reimu / tune / parameters | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuNagasiMethods` | reimu / nagasi / methods | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuIsNotHybrid` | reimu / 状態 / 非 / hybrid | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuParameterizedConstructor` | reimu / parameterized / constructor | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuGetMountPoint` | reimu / 取得 / mount / point | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuCheckTransform` | reimu / 判定 / transform | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuHybridTypeWithMarisa` | reimu / hybrid / type / with / marisa | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testReimuHybridTypeWithOther` | reimu / hybrid / type / with / other | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testGetImageImagePackNullThrowsNpe` | 取得 / image / image / 梱包 / null / 例外 / npe | 基礎回帰 / 取得 / image / image / 梱包 / null / 例外 / npe | 良い | - | - |
| `testExecTransformHeadlessExecutesCode` | exec / transform / headless / executes / code | 基礎回帰 / exec / transform / headless / executes / code | 良い | - | - |
| `testExecTransformReplacesBodyAtSameUniqueId` | exec / transform / replaces / 本体 / at / same / unique / id | 基礎回帰 / exec / transform / replaces / 本体 / at / same / unique / id | 良い | - | - |
| `testExecTransformPreservesPartnerAndChildRelations` | exec / transform / preserves / 相手 / and / 子 / relations | 基礎回帰 / exec / transform / preserves / 相手 / and / 子 / relations | 良い | - | - |
| `testExecTransformPregnantBodyKeepsPregnancyAndFamilyRelations` | exec / transform / pregnant / 本体 / 維持 / pregnancy / and / 家族 / relations | 基礎回帰 / exec / transform / pregnant / 本体 / 維持 / pregnancy / and / 家族 / relations | 良い | - | - |
| `testExecTransformStalkPregnantBodyKeepsStalkPregnancyAndFamilyRelations` | exec / transform / stalk / pregnant / 本体 / 維持 / stalk / pregnancy / and / 家族 / relations | 基礎回帰 / exec / transform / stalk / pregnant / 本体 / 維持 / stalk / pregnancy / and / 家族 / relations | 良い | - | - |
| `testLoadIniFileDoesNotThrow` | ロード / ini / file / does / 非 / 例外 | 基礎回帰 / 状態保存復元確認 | 良い | - | - |
| `testGetBodyBaseImageImagePackNullThrowsNpe` | 取得 / 本体 / base / image / image / 梱包 / null / 例外 / npe | 基礎回帰 / 取得 / 本体 / base / image / image / 梱包 / null / 例外 / npe | 良い | - | - |
| `testGetBodyBaseImageNormalStateExecutesCode` | 取得 / 本体 / base / image / normal / state / executes / code | 基礎回帰 / 取得 / 本体 / base / image / normal / state / executes / code | 良い | - | - |
| `testGetBodyBaseImageBurnedDeadExecutesCode` | 取得 / 本体 / base / image / burned / 死亡 / executes / code | 基礎回帰 / 取得 / 本体 / base / image / burned / 死亡 / executes / code | 良い | - | - |
| `testGetBodyBaseImageCrushedExecutesCode` | 取得 / 本体 / base / image / crushed / executes / code | 基礎回帰 / 取得 / 本体 / base / image / crushed / executes / code | 良い | - | - |
| `testGetBodyBaseImagePealedExecutesCode` | 取得 / 本体 / base / image / pealed / executes / code | 基礎回帰 / 取得 / 本体 / base / image / pealed / executes / code | 良い | - | - |
| `testGetImageWithImagePackExecutesCode` | 取得 / image / with / image / 梱包 / executes / code | 基礎回帰 / 取得 / image / with / image / 梱包 / executes / code | 良い | - | - |

### `RemiryaTest`
- 状態: 完了 (9/9 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testRemiryaIdentity` | remirya / identity | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaParameterizedConstructor` | remirya / parameterized / constructor | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaNames` | remirya / names | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaHybridType` | remirya / hybrid / type | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaTuneParameters` | remirya / tune / parameters | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaNagasiMethods` | remirya / nagasi / methods | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaIsHybrid` | remirya / 状態 / hybrid | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaGetMountPoint` | remirya / 取得 / mount / point | 基礎回帰 / 復活/再生回帰 | 良い | - | - |
| `testRemiryaCheckTransform` | remirya / 判定 / transform | 基礎回帰 / 復活/再生回帰 | 良い | - | - |

### `SakuyaTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSakuyaIdentity` | sakuya / identity | 基礎回帰 / sakuya / identity | 良い | - | - |
| `testSakuyaNames` | sakuya / names | 基礎回帰 / sakuya / names | 良い | - | - |
| `testSakuyaHybridType` | sakuya / hybrid / type | 基礎回帰 / sakuya / hybrid / type | 良い | - | - |
| `testSakuyaIsHybrid` | sakuya / 状態 / hybrid | 基礎回帰 / sakuya / 状態 / hybrid | 良い | - | - |
| `testSakuyaIsServantOfPredatorMasters` | sakuya / 状態 / servant / of / predator / masters | 基礎回帰 / sakuya / 状態 / servant / of / predator / masters | 良い | - | - |
| `testSakuyaParameterizedConstructor` | sakuya / parameterized / constructor | 基礎回帰 / sakuya / parameterized / constructor | 良い | - | - |
| `testSakuyaGetMountPoint` | sakuya / 取得 / mount / point | 基礎回帰 / sakuya / 取得 / mount / point | 良い | - | - |
| `testSakuyaCheckTransform` | sakuya / 判定 / transform | 基礎回帰 / sakuya / 判定 / transform | 良い | - | - |

### `SerializationTest`
- 状態: ハーネス/fixture
- クラス要約: `状態保存復元確認`
- 回帰目的:
  - Yukkuri のシリアライズ/デシリアライズで状態（チューニング後の damageLimitBase）が正しく復元される

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `main(String[])` | シリアライズ / デシリアライズ往復 | 状態保存復元確認 | ハーネス/fixture | JUnit @Test なし。SerializationTestTest.testMainDoesNotThrow が JUnit 統合を担う | - |

### `SerializationTestTest`
- 状態: 完了 (1/1 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 例外系の扱いが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testMainDoesNotThrow` | main() 実行後に test_save.dat が作成されること | 基礎回帰 / シリアライズファイル生成回帰 | 良い | - | - |

### `SuwakoTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testSuwakoIdentity` | suwako / identity | 基礎回帰 / suwako / identity | 良い | - | - |
| `testSuwakoNames` | suwako / names | 基礎回帰 / suwako / names | 良い | - | - |
| `testSuwakoHybridType` | suwako / hybrid / type | 基礎回帰 / suwako / hybrid / type | 良い | - | - |
| `testSuwakoIsHybrid` | suwako / 状態 / hybrid | 基礎回帰 / suwako / 状態 / hybrid | 良い | - | - |
| `testSuwakoParameterizedConstructor` | suwako / parameterized / constructor | 基礎回帰 / suwako / parameterized / constructor | 良い | - | - |
| `testSuwakoGetMountPoint` | suwako / 取得 / mount / point | 基礎回帰 / suwako / 取得 / mount / point | 良い | - | - |
| `testSuwakoCheckTransform` | suwako / 判定 / transform | 基礎回帰 / suwako / 判定 / transform | 良い | - | - |

### `TarinaiReimuTest`
- 状態: 完了 (11/11 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testTarinaiReimuIdentity` | tarinai / reimu / identity | 基礎回帰 / tarinai / reimu / identity | 良い | - | - |
| `testTarinaiReimuExtendsTarinai` | tarinai / reimu / extends / tarinai | 基礎回帰 / tarinai / reimu / extends / tarinai | 良い | - | - |
| `testTarinaiReimuIsIdiot` | tarinai / reimu / 状態 / idiot | 基礎回帰 / tarinai / reimu / 状態 / idiot | 良い | - | - |
| `testTarinaiReimuParameterizedConstructor` | tarinai / reimu / parameterized / constructor | 基礎回帰 / tarinai / reimu / parameterized / constructor | 良い | - | - |
| `testTarinaiReimuGetMountPoint` | tarinai / reimu / 取得 / mount / point | 基礎回帰 / tarinai / reimu / 取得 / mount / point | 良い | - | - |
| `testTarinaiReimuCheckTransform` | tarinai / reimu / 判定 / transform | 基礎回帰 / tarinai / reimu / 判定 / transform | 良い | - | - |
| `testIsIdiotDoesNotThrow` | 状態 / idiot / does / 非 / 例外 | 基礎回帰 / 状態 / idiot / does / 非 / 例外 | 良い | - | - |
| `testTuneParametersDoesNotThrow` | tune / parameters / does / 非 / 例外 | 基礎回帰 / tune / parameters / does / 非 / 例外 | 良い | - | - |
| `testGetHybridTypeWithMarisa` | 取得 / hybrid / type / with / marisa | 基礎回帰 / 取得 / hybrid / type / with / marisa | 良い | - | - |
| `testGetHybridTypeDefault` | 取得 / hybrid / type / default | 基礎回帰 / 取得 / hybrid / type / default | 良い | - | - |
| `testGetNameJ` | 取得 / name / j | 基礎回帰 / 取得 / name / j | 良い | - | - |

### `TarinaiTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testTarinaiIdentity` | tarinai / identity | 基礎回帰 / tarinai / identity | 良い | - | - |
| `testTarinaiNames` | tarinai / names | 基礎回帰 / tarinai / names | 良い | - | - |
| `testTarinaiHybridType` | tarinai / hybrid / type | 基礎回帰 / tarinai / hybrid / type | 良い | - | - |
| `testTarinaiIsIdiot` | tarinai / 状態 / idiot | 基礎回帰 / tarinai / 状態 / idiot | 良い | - | - |
| `testTarinaiTuneParameters` | tarinai / tune / parameters | 基礎回帰 / tarinai / tune / parameters | 良い | - | - |
| `testTarinaiDefaultConstructor` | tarinai / default / constructor | 基礎回帰 / tarinai / default / constructor | 良い | - | - |
| `testTarinaiIsNotHybrid` | tarinai / 状態 / 非 / hybrid | 基礎回帰 / tarinai / 状態 / 非 / hybrid | 良い | - | - |
| `testTarinaiParameterizedConstructor` | tarinai / parameterized / constructor | 基礎回帰 / tarinai / parameterized / constructor | 良い | - | - |
| `testTarinaiGetMountPoint` | tarinai / 取得 / mount / point | 基礎回帰 / tarinai / 取得 / mount / point | 良い | - | - |
| `testTarinaiCheckTransform` | tarinai / 判定 / transform | 基礎回帰 / tarinai / 判定 / transform | 良い | - | - |

### `TenkoTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testTenkoIdentity` | tenko / identity | 基礎回帰 / tenko / identity | 良い | - | - |
| `testTenkoNames` | tenko / names | 基礎回帰 / tenko / names | 良い | - | - |
| `testTenkoHybridType` | tenko / hybrid / type | 基礎回帰 / tenko / hybrid / type | 良い | - | - |
| `testTenkoIsHybrid` | tenko / 状態 / hybrid | 基礎回帰 / tenko / 状態 / hybrid | 良い | - | - |
| `testTenkoParameterizedConstructor` | tenko / parameterized / constructor | 基礎回帰 / tenko / parameterized / constructor | 良い | - | - |
| `testTenkoGetMountPoint` | tenko / 取得 / mount / point | 基礎回帰 / tenko / 取得 / mount / point | 良い | - | - |
| `testTenkoCheckTransform` | tenko / 判定 / transform | 基礎回帰 / tenko / 判定 / transform | 良い | - | - |

### `UdongeTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testUdongeIdentity` | udonge / identity | 基礎回帰 / udonge / identity | 良い | - | - |
| `testUdongeNames` | udonge / names | 基礎回帰 / udonge / names | 良い | - | - |
| `testUdongeHybridType` | udonge / hybrid / type | 基礎回帰 / udonge / hybrid / type | 良い | - | - |
| `testUdongeIsHybrid` | udonge / 状態 / hybrid | 基礎回帰 / udonge / 状態 / hybrid | 良い | - | - |
| `testUdongeParameterizedConstructor` | udonge / parameterized / constructor | 基礎回帰 / udonge / parameterized / constructor | 良い | - | - |
| `testUdongeGetMountPoint` | udonge / 取得 / mount / point | 基礎回帰 / udonge / 取得 / mount / point | 良い | - | - |
| `testUdongeCheckTransform` | udonge / 判定 / transform | 基礎回帰 / udonge / 判定 / transform | 良い | - | - |

### `WasaReimuTest`
- 状態: 完了 (10/10 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testWasaReimuIsHybrid` | wasa / reimu / 状態 / hybrid | 基礎回帰 / wasa / reimu / 状態 / hybrid | 良い | - | - |
| `testWasaReimuDefaultConstructor` | wasa / reimu / default / constructor | 基礎回帰 / wasa / reimu / default / constructor | 良い | - | - |
| `testWasaReimuNames` | wasa / reimu / names | 基礎回帰 / wasa / reimu / names | 良い | - | - |
| `testWasaReimuExtendsReimu` | wasa / reimu / extends / reimu | 基礎回帰 / wasa / reimu / extends / reimu | 良い | - | - |
| `testWasaReimuNagasiMethods` | wasa / reimu / nagasi / methods | 基礎回帰 / wasa / reimu / nagasi / methods | 良い | - | - |
| `testWasaReimuParameterizedConstructor` | wasa / reimu / parameterized / constructor | 基礎回帰 / wasa / reimu / parameterized / constructor | 良い | - | - |
| `testWasaReimuGetMountPoint` | wasa / reimu / 取得 / mount / point | 基礎回帰 / wasa / reimu / 取得 / mount / point | 良い | - | - |
| `testWasaReimuCheckTransform` | wasa / reimu / 判定 / transform | 基礎回帰 / wasa / reimu / 判定 / transform | 良い | - | - |
| `testWasaReimuHybridTypeWithMarisa` | wasa / reimu / hybrid / type / with / marisa | 基礎回帰 / wasa / reimu / hybrid / type / with / marisa | 良い | - | - |
| `testWasaReimuHybridTypeWithOther` | wasa / reimu / hybrid / type / with / other | 基礎回帰 / wasa / reimu / hybrid / type / with / other | 良い | - | - |

### `YurusanaeTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testYurusanaeIdentity` | yurusanae / identity | 基礎回帰 / yurusanae / identity | 良い | - | - |
| `testYurusanaeNames` | yurusanae / names | 基礎回帰 / yurusanae / names | 良い | - | - |
| `testYurusanaeHybridType` | yurusanae / hybrid / type | 基礎回帰 / yurusanae / hybrid / type | 良い | - | - |
| `testYurusanaeIsHybrid` | yurusanae / 状態 / hybrid | 基礎回帰 / yurusanae / 状態 / hybrid | 良い | - | - |
| `testYurusanaeParameterizedConstructor` | yurusanae / parameterized / constructor | 基礎回帰 / yurusanae / parameterized / constructor | 良い | - | - |
| `testYurusanaeGetMountPoint` | yurusanae / 取得 / mount / point | 基礎回帰 / yurusanae / 取得 / mount / point | 良い | - | - |
| `testYurusanaeCheckTransform` | yurusanae / 判定 / transform | 基礎回帰 / yurusanae / 判定 / transform | 良い | - | - |

### `YuukaTest`
- 状態: 完了 (8/8 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testYuukaIdentity` | yuuka / identity | 基礎回帰 / yuuka / identity | 良い | - | - |
| `testYuukaNames` | yuuka / names | 基礎回帰 / yuuka / names | 良い | - | - |
| `testYuukaHybridType` | yuuka / hybrid / type | 基礎回帰 / yuuka / hybrid / type | 良い | - | - |
| `testYuukaIsHybrid` | yuuka / 状態 / hybrid | 基礎回帰 / yuuka / 状態 / hybrid | 良い | - | - |
| `testYuukaDefaultConstructor` | yuuka / default / constructor | 基礎回帰 / yuuka / default / constructor | 良い | - | - |
| `testYuukaParameterizedConstructor` | yuuka / parameterized / constructor | 基礎回帰 / yuuka / parameterized / constructor | 良い | - | - |
| `testYuukaGetMountPoint` | yuuka / 取得 / mount / point | 基礎回帰 / yuuka / 取得 / mount / point | 良い | - | - |
| `testYuukaCheckTransform` | yuuka / 判定 / transform | 基礎回帰 / yuuka / 判定 / transform | 良い | - | - |

### `YuyukoTest`
- 状態: 完了 (7/7 良い)
- クラス要約: `基礎回帰`
- 回帰目的:
  - 保存/復元後に状態が壊れない
  - 生成時の初期値や生成結果が壊れない
  - プロパティの更新と保持が壊れない
  - 画像・描画用データが壊れない

| メソッド | 意図 | 回帰の種類 | 評価 | 不足点 | 補足 |
| --- | --- | --- | --- | --- | --- |
| `testYuyukoIdentity` | yuyuko / identity | 基礎回帰 / yuyuko / identity | 良い | - | - |
| `testYuyukoNames` | yuyuko / names | 基礎回帰 / yuyuko / names | 良い | - | - |
| `testYuyukoHybridType` | yuyuko / hybrid / type | 基礎回帰 / yuyuko / hybrid / type | 良い | - | - |
| `testYuyukoIsHybrid` | yuyuko / 状態 / hybrid | 基礎回帰 / yuyuko / 状態 / hybrid | 良い | - | - |
| `testYuyukoParameterizedConstructor` | yuyuko / parameterized / constructor | 基礎回帰 / yuyuko / parameterized / constructor | 良い | - | - |
| `testYuyukoGetMountPoint` | yuyuko / 取得 / mount / point | 基礎回帰 / yuyuko / 取得 / mount / point | 良い | - | - |
| `testYuyukoCheckTransform` | yuyuko / 判定 / transform | 基礎回帰 / yuyuko / 判定 / transform | 良い | - | - |
