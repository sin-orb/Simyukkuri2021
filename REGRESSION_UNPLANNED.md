# REGRESSION_UNPLANNED

TEST_EXPANTION_PLAN.md に載っていない仕様起点の回帰テスト候補。  
コードと会話から推定した仕様を軸に「壊れたときに何が起きるか」を整理した。

ステータス: `未着手` / `完了` / `スキップ`

---

## `YukkuriNeedleRule.handleNeedledYukkuri`

針が刺さっているゆっくりへの接近処理。**テスト0件**。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 針が刺さっていないのに true を返す | `target.setNeedled(false)` | `handleNeedledYukkuri(target, actor)` → `false` | 未着手 |
| 親が刺さった子供のそばに行かない | `target.setNeedled(true)`, actor=adult+母, target=child | `actor.isGuriguri()` == true かつ `return true` | 未着手 |
| つがいが刺さった相手のそばに行かない | `target.setNeedled(true)`, `target.setPartner(actor)` | `actor.isGuriguri()` == true かつ `return true` | 未着手 |
| 関係なしでも clearActions されない | `target.setNeedled(true)`, 無関係ゆっくり | `return true`（clearActions は呼ばれる）かつ `actor.isGuriguri()` == false | 未着手 |

**備考:** `GameRandom.nextInt(1) == 0` は常に true（nextInt(1) は必ず 0）。姉妹分岐の「50% 確率」コメントは誤り。実装上は確定でぐりぐりする。

---

## `YukkuriIllnessRule.findSick`

かびの見抜き判定。知能差による感知能力の差異がテストされていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| WISE が軽症ゆっくりを見逃す | `intel=WISE`, `target.setSickPeriod(incubation+1)` (barely sick) | `findSick(WISE, target)` → `true` | 未着手 |
| FOOL が軽症ゆっくりを正しく見逃さない | `intel=FOOL`, barely sick | `findSick(FOOL, target)` → `false` | 未着手 |
| FOOL が重症ゆっくりを見逃す | `intel=FOOL`, `target.setSickPeriod(incubation*8+1)` (SickHeavily) | `findSick(FOOL, target)` → `true` | 未着手 |
| null が渡されてもクラッシュする | `target=null` | `findSick(AVERAGE, null)` → `false` | 未着手 |

---

## `YukkuriExcretionRule.getDiarrhea`

下痢確率判定。KAIYU ランク・ダメージ/病気による確率半減・下限クランプが未テスト。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| KAIYU が確率判定を受ける | `body.setRank(YukkuriRank.KAIYU)` | `getDiarrhea(body)` → `true`（RNG 無関係） | 未着手 |
| 健康体なのに確率が下がる | 健常ゆっくり + ConstState(0) で RNG=0 | `getDiarrhea(body)` → `true`（diarrheaProb=通常値, nextInt=0） | 未着手 |
| 病気でも確率が下がらない | `body.setSickPeriod(incubation+1)` → isSick=true | `getDiarrhea(body)` が diarrheaProb/2 で評価される（ConstState(0) で確認） | 未着手 |
| diarrheaProb=0 でクラッシュする | diarrheaProb が 0 になる状態 | `getDiarrhea(body)` → `true`（clamp→1 で nextInt(1)==0 確定） | 未着手 |

---

## `YukkuriWakeupRule.checkWakeupOtherYukkuri`

睡眠中ゆっくりが近くに起きているゆっくりを検知するかの判定。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 死亡済みゆっくりを「起きている」とみなす | `other.setDead(true)` + `other.setSleeping(false)` | `checkWakeupOtherYukkuri(body)` → `false` | 未着手 |
| NYD ゆっくりに反応してしまう | `other.setCoreAnkoState(NON_YUKKURI_DISEASE)` → isNyd=true | `checkWakeupOtherYukkuri(body)` → `false` | 未着手 |
| NONE ランクが UNUN_SLAVE を起こしてしまう | `body.setPublicRank(NONE)`, `other.setPublicRank(UNUN_SLAVE)`, other=起床中 | `checkWakeupOtherYukkuri(body)` → `false` | 未着手 |
| 埋没中ゆっくりに反応してしまう | `other.setBurialState(BurialState.ALL)` | `checkWakeupOtherYukkuri(body)` → `false` | 未着手 |
| 視野内の起床中ゆっくりに反応しない | `other.setSleeping(false)` + 視野内に配置 | `checkWakeupOtherYukkuri(body)` → `true` | 未着手 |
| 全員が寝ているのに true になる | `other.setSleeping(true)` のみ | `checkWakeupOtherYukkuri(body)` → `false` | 未着手 |

---

## `YukkuriDeadSearchRule.handleDeadFound`

パートナー探索中に死体ゆっくりに遭遇したときの反応処理。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| exciting でない個体がすっきりに向かう | `actor.setExciting(true)` | `actor.isMoveToSukkiri()` == true, `return true` | 未着手 |
| 90% skip が機能しない | ConstState(1) (nextInt(10)=1≠0) | `handleDeadFound(actor, target, 0, 0)` → `false` | 未着手 |
| ランク違いの死体に反応してしまう | `actor.setPublicRank(NONE)`, `target.setPublicRank(UNUN_SLAVE)`, ConstState(0) | `return false`（ランク不一致でスキップ） | 未着手 |
| 成体が家族死体に向かわない | adult actor + 親子関係 target(dead) + ConstState(0) | `actor.getMoveTargetId() == target` | 未着手 |
| 非成体が姉妹死体に向かわない | child actor + 同父 sister(dead) + ConstState(0) | `actor.getMoveTargetId() == target` | 未着手 |
| 成体が他人の死体に向かってしまう | adult actor + 無関係 target(dead) + ConstState(0) | `return false`（lookTo は呼ばれるが move はしない） | 未着手 |
| 非成体が他人の死体を見て逃げない | child actor + 無関係 target(dead) + ConstState(0) | `actor.isRunAway()` または相当する状態 | 未着手 |
| しゃべり中でも怯えメッセージが出る | `actor.setMessageCount(1)` → isTalking=true | scare メッセージが追加されない（talking guard） | 未着手 |
| 非レイパー・非捕食種が死体で memories を失わない | 通常 actor + dead target + ConstState(0) | `actor.getMemories()` が -1 減っている | 未着手 |

---

## `YukkuriParentRule.checkNearParent`

子ゆっくりが親を探して近づく処理。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 成体が親探しをしてしまう | `body.setAgeState(ADULT)` | メソッド呼び出し後に行動が変わらない（early return） | 未着手 |
| 親がいなくても例外なくクラッシュする | 親・姉なし body | `checkNearParent(body)` → assertDoesNotThrow | 未着手 |
| 泣き叫んでいる子が寝ている親を起こさない | `body.setCallingParents(true)` + `parent.setSleeping(true)` | `parent.isSleeping()` == false（wakeup された） | 未着手 |
| 汚れた子がアリに食われているのに親がペロペロしない | `body.setDirty(true)` + 親が `canEventResponse()=true` + 接近距離内 | `parent.isPeropero()` == true | 未着手 |
| 汚れた子が遠くにいるのに moveTo が呼ばれない | `body.setDirty(true)` + 親が遠い | `body.getMoveTargetId()` が親の位置を示す | 未着手 |
| 距離が十分近いのに移動してしまう | `distanceToParent < eyesight/32` | `body.getMoveTargetId()` が変わらない（close enough, return early） | 未着手 |

---

## `YukkuriSelectionRule.createActiveFiances`

つがい候補リスト生成。多数のフィルタが連続する。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 自分だけの世界でも候補が返る | registry.size()==1 | `createActiveFiances(body, 0)` → `null` | 未着手 |
| 既つがいなのに全候補を探索してしまう | `body.setPartner(partner)` を registry に登録 | 返却リストに partner のみ含まれる | 未着手 |
| 死んだ候補がリストに入る | `candidate.setDead(true)` | 候補リストに含まれない | 未着手 |
| ランクが違う相手がリストに入る | `body.setPublicRank(NONE)`, `candidate.setPublicRank(UNUN_SLAVE)` | 候補リストに含まれない | 未着手 |
| 障害ゆっくりがリストに入る | `candidate.hasDisorder()=true` (setDisorder等) | 候補リストに含まれない | 未着手 |
| 病気ゆっくりが賢い actor にバレずリストに入る | AVERAGE actor + barely sick candidate | 候補リストに含まれない | 未着手 |
| 幼い相手を age ガードなしに候補にする | `age=ADULT.ordinal()`, `candidate.setAgeState(BABY)` | 候補リストに含まれない（ロリコン防止） | 未着手 |
| すでにつがいのいる相手がいつも除外される | `candidate.setPartner(third)` + ConstState(0, nextBoolean=false) | 候補リストに含まれる（50% 確率で通過） | 未着手 |

---

## `YukkuriSelectionRule.createActiveChildren`

家族イベント対象の子ゆリスト生成。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 子なしで null が返らない | `body.getChildrenCount()==0` | `createActiveChildren(body, true)` → `null` | 未着手 |
| プレイヤーに持たれた子がリストに入る | `child.setTaken(true)` | 子リストに含まれない | 未着手 |
| UNUN_SLAVE の子がリストに入る | `child.setPublicRank(UNUN_SLAVE)` | 子リストに含まれない | 未着手 |
| 産まれたてのイベントブロック中の子が入る | `child.setBirthEventBlockedTicks(1)` | 子リストに含まれない | 未着手 |
| firstGround 中の赤ゆがリストに入る | `child.setFirstGround(true)` | 子リストに含まれない | 未着手 |
| NYD の子がリストに入る | `child.setCoreAnkoState(NON_YUKKURI_DISEASE)` | 子リストに含まれない | 未着手 |
| includeChildren=false で子ゆが入る | `child.setAgeState(CHILD)` + `includeChildren=false` | 子リストに含まれない（赤ゆのみ） | 未着手 |
| includeChildren=true で大人が入る | `child.setAgeState(ADULT)` + `includeChildren=true` | 子リストに含まれない | 未着手 |

---

## `YukkuriMovement.applyExternalMotion` — 着地時処理

`applyExternalMotion` の着地パス。NYD ガードのみ既テスト。その他の着地分岐が未テスト。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 初接地なのに stress 軽減が起きない | `body.setFirstGround(true)`, 落下 | `body.getStress()` が -400 の軽減、memories +20 | 未着手 |
| NYD 中に初接地 stress 軽減が発生する | `body.setFirstGround(true)` + `isNyd=true` | stress 不変（isNyd=true ガードで軽減なし）← **修正済みバグの回帰テスト** | 未着手 |
| isPealed なのに着地死亡しない | `body.setPealed(true)` + 高落下 | `body.isDead()` == true | 未着手 |
| ベッドでも通常ダメージを受ける | `body.checkOnBed()=true` + BABY 以外 + 大落下 | ダメージが damageCut=4 で減少（通常の 1/4） | 未着手 |
| noDamageNextFall フラグが機能しない | `body.setNoDamageNextFall(true)` + 落下 | ダメージなし + フラグが false にリセット | 未着手 |
| 落下後のダメージで死んでも setCrushed されない | `isDead()=true` になる落下 | `body.isCrushed()` == true | 未着手 |

**備考:** NYD 中着地で addStress(-400) が走っていたバグ修正（`if (!body.isNyd())` ガード追加）の**回帰テスト**が未追加。これは最優先。

---

## `YukkuriCoreStateRule` — ダメージ状態遷移

damage 量に応じた DamageState の切替え判定が直接テストされていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| damage=0 で NONE 以外になる | `body.getDamage()==0` | getDamageState() == NONE | 未着手 |
| limit/2 ちょうどで VERY にならない | `setDamage(getDamageLimit()/2)` | getDamageState() == VERY | 未着手 |
| limit*3/4 ちょうどで TOOMUCH にならない | `setDamage(getDamageLimit()*3/4)` | getDamageState() == TOOMUCH | 未着知 |
| limit 超えで toDead が呼ばれない | `addDamage(getDamageLimit()+1)` | `body.isDead()` == true | 未着手 |

---

## `FoodLogic.eatFood` — isTooHungry 連携

メモリから判明している重要仕様が回帰テストで明示されていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| hungry=0 + damage=NONE で isTooHungry になる | `body.setHungry(0)` + damage=0 | `body.isTooHungry()` == false (damage=NONE のとき false 仕様) | 未着手 |
| hungry=0 + damage=VERY で isTooHungry にならない | `body.setHungry(0)` + `WorldTestHelper.setDamage(body, limit/2+1)` | `body.isTooHungry()` == true | 未着手 |

---

## イベント系 — 未テストの execute/start パス

COVERAGE_STATUS の `event` パッケージは Branch 11.9%。主要イベントの execute ロジックがほぼ未テスト。

### `ProposeEvent.execute`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 求愛成功時につがい関係が設定されない | 求愛成功シナリオを execute まで進める | `from.getPartner()==you.getUniqueId()` かつ `you.getPartner()==from.getUniqueId()` | 未着手 |
| 求愛失敗時につがい関係が設定されてしまう | you.setPartner(他) で既婚にしてから execute | partner 変更なし | 未着手 |

### `YukkuriRideEvent`（おちびちゃん運び）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 親が死亡してもイベントが継続し ABORT にならない | `from.setDead(true)` → `canActionForEvent()=false` | `update(from)` → ABORT | 未着手 |
| 親が死亡してもおちびの parentLinkId がリセットされない | 上記 ABORT 経由で `end()` が呼ばれる | `to.getParentLinkId() == -1`（おちびが解放された） | 未着手 |

> **仕様：** 親が死亡したらイベントを即 ABORT し、おちびの parentLink を解除して自由落下させる。
> 
> **現状：** コード上は `canActionForEvent()=false` → ABORT → `end()` → `setParentLinkId(-1)` の経路が存在するが、「親死亡」を起点とする end-to-end テストが未追加。`end()` 単体テストと「to が死亡」テストは既存。

---

### `FuneralEvent.execute`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 葬儀イベントが実行されても悲しまない | from.isDead()=true のシナリオで execute | `body.getHappiness()` == VERY_SAD | 未着手 |

---

## `FuneralEvent` — イベント中の挙動ガード

### 発火・完了・中断（ユーザー指定項目）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 子/番の死亡を見ても FuneralEvent がキューに積まれない | dead target + doActionOther + 家族関係 | world events に FuneralEvent が追加される | **完了** (BodyLogicTest L2213, L3940) |
| GOODBYE ステートで死体のお飾りが外れない | state=GOODBYE + deceased.hasOkazari()=true | update 後 `deceased.hasOkazari()` == false | **完了** (FuneralEventTest L462) |
| れいぱー/捕食種が現れてもおとむらいを続けてしまう（逃げない） | 視野内にれいぱー（`body.isRaper()+isExciting()`）または捕食種（Remirya 等 `isPredatorType()`）を配置した状態でおとむらいイベントを進行させる | `update()` → ABORT（イベント中断）かつ body がパニック/逃走状態になる、または RaperReactionEvent がキューに追加される | 未着手 |

---

## イベント中の捕食種/れいぱー出現による中断 — 全イベント設計方針

「自分が脅威を扱っている側か、逃げるべき状況か」を基準に分類。  
現状は **いずれのイベントにも捕食種/れいぱー出現の ABORT 分岐がない**。

### 中断すべき（捕食種/れいぱーが視野に入ったら逃げる）

| イベント | 理由 |
|---|---|
| `FuneralEvent` | おとむらい中でも身の危険が優先 |
| `ProposeEvent` | 求愛どころではない |
| `BreedEvent` | 交尾中でも逃げるべき |
| `ShitExercisesEvent` | 排泄練習中でも逃げるべき |
| `SuperEatingTimeEvent` | 強制給餌中でも逃げるべき（子への給餌側も含む） |
| `ProudChildEvent` | 子自慢中でも逃げるべき |
| `SuiRideEvent` / `YukkuriRideEvent` | 乗り物イベント中でも逃げるべき |
| `HateNoOkazariEvent` | 愚痴どころではない |
| `FavCopyEvent` | お気に入りコピー中でも逃げるべき |
| `GetTrashOkazariEvent` | ゴミお飾り取得中でも逃げるべき |
| `AvoidMoldEvent` | もうカビどころではない |
| `EatBodyEvent` | 死体を食べていたが捕食種/れいぱーが現れたら逃げるべき（同族食いに気づく前でも後でも中断） |

### 中断しない（自分が脅威を扱っている側、または捕捉済み）

| イベント | 理由 |
|---|---|
| `FlyingEatEvent` | 自分が捕食する側（空中捕食） |
| `KillPredeatorEvent` | 捕食種/れいぱーを制裁しにいっている最中 |
| `PredatorsGameEvent` | 同上 |
| `EatBodyEvent` | ※中断する側へ移動 |
| `RaperReactionEvent` | すでにれいぱー対処中 |
| `RaperWakeupEvent` | れいぱー自身のイベント |

### グレーゾーン → 仕様確定

| イベント | 判定 | 理由 |
|---|---|---|
| `BegForLifeEvent` | **中断する** | プレイヤーに命乞いをしているが、捕食種/れいぱーが視野に入ったらそちらから逃げるべき |
| `RevengeAttackEvent` | **中断しない** | 怒りが恐怖を上回る。ぶっころ |

**実装方針（案）:** 各イベントの `update()` 冒頭に共通ガード「視野内に捕食種/れいぱーがいれば ABORT」を追加する。中断しないイベントにはこのガードを入れない。

### イベント中の通常行動抑制

FuneralEvent.update() 内に眠り/空腹の明示的対処あり（L237-243）。発情は canAction() ガードで間接的に抑制される。しかし各挙動が「起きないこと」を確認するテストがゼロ。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| イベント中に眠ったままぐりぐりしなくなる | `b.setSleeping(true)` 状態で update を呼ぶ | update 後 `b.isSleeping()` == false (wakeup が呼ばれた) | 未着手 |
| イベント中に空腹状態が維持されてイベントが止まる | `b.setHungry(0)` (isHungry=true) 状態で update | update 後 `b.getHungry()` == `b.getHungryLimit() * 6 / 10`（60% に補正された） | 未着手 |
| 発情中でも checkPartner が走ってしまう | `b.setExciting(true)` を設定した状態でのゲームループ回帰 | `canAction()=false`（currentEvent!=null）で checkPartner に入らない → `b.isToSukkiri()` == false のまま | 未着手 |

---

---

## ユーザーコマンド — `GadgetAction` 各カテゴリの副作用確認

**現状の既存カバレッジ（GadgetActionTest 29件・GadgetToolTest 11件）:**
- `immediateEvaluate`: YU_CLEAN, BODY, SHIT, ALL, ETC ✓
- `evaluateTool` (GODHAND ケース0-5, PUNISH, SNAPPING, VIBRATOR): GadgetToolTest ✓
- `evaluateAmpoule` (ORANGE, POISON): GadgetActionTest ✓
- `evaluateAccessory` (おかざり取付/取外): GadgetActionTest ✓
- `evaluatePants` (おくるみ): GadgetActionTest ✓
- `evaluateClean` (INDIVIDUAL): GadgetActionTest ✓
- `leftClickEvaluate` の RANK_SET, FEED, VAIN, SET_CLEANING: GadgetActionTest ✓

**テスト0件のカテゴリ・コマンド:**

### 声かけコマンド (`evaluateCommunicate` / `GadgetBodyAction`)

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 「ゆっくりしていってね」を言っても反応しない | `item=YUKKURISITEITTENE` で evaluateCommunicate | `body.voiceReaction(0)` の副作用が起きる（メッセージ/感情変化） | 未着手 |
| 「ゆっくり死んでね」を言っても反応しない | `item=YUKKURIDIE` | `body.voiceReaction(1)` の副作用が起きる | 未着手 |
| 「ゆっくりふりふりしてね」を言っても反応しない | `item=YUKKURIFURIFURI` | `body.voiceReaction(2)` の副作用が起きる | 未着手 |

### 道具コマンド — 未テストケース (`evaluateTool` / `GadgetToolAction`)

現状 GODHAND・PUNISH・SNAPPING・VIBRATOR のみテストあり。以下はゼロ。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NEEDLE で針が刺さらない | `item=NEEDLE` + Yukkuri | `body.isNeedled()` == true | 未着手 |
| WATER をかけても濡れない | `item=WATER` + Yukkuri | `body.isWet()` == true | 未着手 |
| PEAL で皮がむけない | `item=PEAL` + Yukkuri | `body.isPealed()` == true | 未着手 |
| BLIND で目が見えなくならない | `item=BLIND` + Yukkuri | `body.isBlind()` == true | 未着手 |
| JUICE (ジュース) を与えても回復しない | `item=JUICE` + Yukkuri | ダメージが減少する | 未着手 |
| PACK で詰められない | `item=PACK` + Yukkuri | `body.isPacked()` == true | 未着手 |
| NEEDLE ツールで針が刺さった後 NEEDLE 再適用で抜ける | `item=NEEDLE` 2回 | `body.isNeedled()` == false（トグル仕様） | 未着手 |
| CASTRATION をしても去勢されない | `item=CASTRATION` + Yukkuri | `body.isCastrated()` == true | 未着手 |
| BURY で埋まらない | `item=BURY` + Yukkuri | `body.getBurialState()` != NONE | 未着手 |
| HAMMER でダメージが入らない | `item=HAMMER` + Yukkuri | `body.getDamage()` > 0 | 未着手 |
| SET_SICK でカビが接種されない | `item=SET_SICK` + Yukkuri | `body.isSick()` == true | 未着手 |

### 床設置コマンド (`evaluateFloorItems` / `GadgetItemSetupAction`)

アイテムがワールドに配置されるかの確認。全てテストゼロ。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| DIFFUSER が配置されない | `item=DIFFUSER` + 座標 | `getCurrentWorldState().getDiffusers()` にアイテムが追加される | 未着手 |
| BREED_POOL が配置されない | `item=BREED_POOL` + 座標 | 対応アイテムがワールドに追加される | 未着手 |
| YUNBA_SETUP (ゆんば) が配置されない | `item=YUNBA_SETUP` + 座標 | Yunba がワールドに追加される | 未着手 |
| BELTCONVEYOR_SETUP が配置されない | `item=BELTCONVEYOR_SETUP` + 座標 | BeltconveyorObj がワールドに追加される | 未着手 |

**備考:** 床設置系は `putObjEx` (GUI 依存) を経由するため headless 環境では assertDoesNotThrow 相当のスモークテストが現実的。

---

## `GadgetTool.doGodHand` — 神拳の各ケース抜け

`nextInt(8)` で 8 ケースに分岐。各ケースの副作用がすべてテストされているわけではない。

### テスト済み（部分的）

| ケース | 確認済み内容 |
|---|---|
| 0 (変身不可) | `isRapist()` がトグルされる |
| 1 (切断) | `CriticalDamageType == CUT` |
| 2 (引っ張り) | `abFlagGodHand[1]=true`, `[2]=false`、つぶし→引っ張り切替 |
| 3 (つぶし) | `abFlagGodHand[1]=false`, `[2]=true`、引っ張り→つぶし切替 |
| 4 (回復) | `damage == 0`（giveJuice 後） |
| 5 (言語破壊・れいむ) | `getMsgType() == TARINAIREIMU` |
| default (膨張1回目) | `abFlagGodHand[0]=true`、肛門未閉鎖 |
| default (膨張2回目) | `isAnalClose()==true`、shit 量 10 倍 |

### 未テスト

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| まりさに神拳を当ててもドス化しない | 健全な大人まりさ（stress=0, damage=0, notRude, notNYD 等）に ConstState(0) で doGodHand | `body.getType() == YukkuriType.DOSMARISA` | 未着手 |
| ゲスまりさがドス化してしまう | `body.setAttitude(SHITHEAD)` → isRude=true の大人まりさに ConstState(0) で doGodHand | `body.getType()` が DOSMARISA にならない（execTransform 内で rude チェックが弾く） | 未着手 |
| ダメージあり/ストレスありまりさがドス化してしまう | damage>0 またはstress>0 の状態で ConstState(0) doGodHand | canTransform()=false → 変身しない | 未着手 |
| ドスまりさが既にいるのに再ドス化できてしまう | ワールドに DosMarisa を登録した状態で別まりさに doGodHand | `body.getType()` が DOSMARISA にならない | 未着手 |
| プレイヤーのアイテム欄にドスまりさがいる状態で再ドス化できてしまう | DosMarisa を「持つ」（アイテム欄に格納）した状態で別まりさに doGodHand | `body.getType()` が DOSMARISA にならない | 未着手 |

> **仕様：** ドスまりさはワールドに1体のみ。マップ上だけでなく**プレイヤーのアイテム欄に入っていても**「存在する」とカウントする。
>
> **現在の実装との乖離（直し必要）:**
> 1. `judgeCanTransForGodHand()` にドスまりさ存在チェックがコメントのみで未実装 → 常に true を返す
> 2. `getYukkuriRegistry()` はアイテム欄（`isTaken=true`）のゆっくりを含まない（「持つ」時に registry から削除される）→ アイテム欄のドスまりさを見落とす
> 3. 実装時は registry + `player.getInventoryView()` の両方を見るか、`isTaken` フラグを持つ個体も走査する必要がある
| れいむに神拳を当ててもでいぶ化しない | 健全な大人れいむに ConstState(0) で doGodHand | `body.getType() == YukkuriType.DEIBU` | 未着手 |
| 神拳後に持ち物が落ちない（全ケース共通） | 食料を carryItem に持たせた状態で各ケースを実行 | `body.getCarryItem(FOOD) == null`（dropAllTakeoutItem が呼ばれた） | 未着手 |
| 言語破壊でれいむ以外が TARINAI にならない | れいむ以外（まりさ等）に `case 5` を実行 | `body.getMsgType() == TARINAI` | 未着手 |
| 引っ張り/つぶし時に茎の実ゆの親が反応しない | 茎に植わった赤ゆ（stalk に plantYukkuri 設定）に case 2/3 | 親ゆに UnbirthBabyState.SAD が通知される | 未着手 |
| 回復時に茎の実ゆの親が反応しない | 同条件で case 4 | 親ゆに UnbirthBabyState.HAPPY が通知される | 未着手 |
| case 4 で事前のダメージ設定（limit/2）がスキップされる | damage=0 のまま doGodHand(case 4) | giveJuice 前に `body.getDamage() == limit/2` であることを確認 | 未着手 |

---

---

## アンプルの tick 効果 — 未テスト分

各アンプルは `Attachment.clockTick()` → `update()` で `processInterval` tick ごとに効果を発揮する。

---

## アリの伝染・撃退 (`YukkuriOtherRelationDelegate`)

`doSurisuri`・`doPeropero` でのアリ関連挙動がすべて未テスト。  
現状の `YukkuriOtherRelationDelegateTest` は死体ガードの `assertDoesNotThrow` のみ。

### こーろこーろ（doSurisuri）でのアリ伝染

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| アリのたかったゆっくりにすりすりしても伝染らない | target に Ants を addAttachment した状態で `actor.doSurisuri(target)` + ConstState(0)（nextInt(200)==0） | `actor.getAttachmentSize(Ants.class) > 0`（actor にアリが付く） | 未着手 |
| アリがいないのにすりすりで伝染る | target にアリなし + ConstState(0) | `actor.getAttachmentSize(Ants.class) == 0`（伝染しない） | 未着手 |

### ぺろぺろ（doPeropero）でのアリ伝染

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| アリのたかったゆっくりをぺろぺろしても伝染らない | target に Ants + `target.setAntCount(50)` + `actor.doPeropero(target)` + ConstState(0) | アリ削減後もアリが残る(>0)なら actor に Ants が付く | 未着手 |

### ぺろぺろ（doPeropero）でのアリ撃退

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ぺろぺろしてもアリが減らない | target に Ants + `target.setAntCount(50)` + `actor.doPeropero(target)` | `target.getAntCount() == 10`（50-40=10 に減少） | 未着手 |
| アリが閾値まで減っても除去されない | `target.setAntCount(30)` + doPeropero | `target.getAttachmentSize(Ants.class) == 0`（30-40≤0 で removeAnts が呼ばれる） | 未着手 |

> **備考：** 「こーろこーろで撃退」についてはコード上 `doSurisuri` にアリの除去処理はなく、伝染リスクのみ存在する。「撃退」は `doPeropero` の専用機能であり、`doSurisuri` は伝染リスクのみ。仕様と一致するか確認が必要。

---

### `AnydAmpoule`（NYD 防止アンプル）

`update()` は NONE を返すのみ。実際の効果は `YukkuriNydDelegate` 側でアタッチメントの有無を見る。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| AnydAmpoule を付けていても NYD が進行する | body に AnydAmpoule を addAttachment した状態で NYD 進行ロジックを実行 | NYD 状態にならない（`body.isNyd() == false`） | 未着手 |

### `BreedingAmpoule`（繁殖アンプル）

効果: `setHungry(100)` + `addDamage(-100)` + `getBabyTypes().add(DNA)` + `setHasBaby(true)`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 去勢個体にアンプルが効いてしまう | `body.setCastrated(true)` + update 呼び出し | `body.isHasBaby() == false`（castrated ガードで弾かれる） | 未着手 |
| 健康な個体に繁殖効果が起きない | 健康な成ゆに update 呼び出し | `body.isHasBaby() == true`、`body.getBabyTypes().size() > 0`、`body.getDamage()` 減少、`body.getHungry() == 100` | 未着手 |

### `PoisonAmpoule`（毒アンプル）

効果: CUT でなければ `plusShit(50)` + `wakeup()` → 常に `setHappiness(SAD)` → 1/1000 で `addDamage(200)` + VERY_SAD + 痛顔

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| CUT された個体の shit がさらに増える | `body.setCriticalDamageType(CUT)` + update | `body.getShit()` が増えない（wakeup も呼ばれない） | 未着手 |
| 毒アンプルが起きているゆっくりを起こさない | 寝ている非 dead 個体に update | `body.isSleeping() == false`（wakeup が呼ばれた） | 未着手 |
| 1/1000 ダメージバーストが発動しない | ConstState(0) で update（`nextInt(1000)==0`） | `body.getDamage() > 0`（addDamage(200) 発動）、`body.getHappiness() == VERY_SAD`、`body.getForceFace() == PAIN` | 未着手 |

---

---

## `Fire` アタッチメント — 燃焼中の毛焼け・水消火後のハゲ

`Fire.update()` の `pickHair()` 分岐は既存テストで `parent.setDead(true)` 状態でのみ確認されており、「実際に HairState が変化するか」は**完全に未テスト**（コメントにも「HairStateは変わらない」と明記）。

**仕組み:** `burnPeriod > damageLimit*2/3` かつ `hairState != BALDHEAD` → `pickHair()` 毎 processInterval 呼び出し → DEFAULT→BRINDLED1→BRINDLED2→BALDHEAD（3回でハゲ）。水消火（`inWater()` → `removeAttachment(Fire.class)`）で Fire が消え、その時点の hairState が維持される。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 燃えているのに毛が焼けない | 生きている body に Fire を付け burnPeriod を `damageLimit*2/3+1` に設定して `fire.update()` | `body.getHairState() == HairState.BRINDLED1`（DEFAULT から一段焼けた） | 未着手 |
| 3回焼けてもハゲにならない | 同条件で update を3回繰り返す（BRINDLED1→BRINDLED2→BALDHEAD） | `body.getHairState() == HairState.BALDHEAD` | 未着手 |
| 水で消火後に毛が元に戻る（残らない） | burnPeriod が閾値超で update 後に `body.removeAttachment(Fire.class)` | `body.getHairState() != HairState.DEFAULT`（消火後もハゲまたは焼け状態が維持） | 未着手 |
| ハゲになる前に消火してもハゲ判定になる | burnPeriod を BRINDLED2 まで進めてから消火 | `body.getHairState() == HairState.BRINDLED2`（完全ハゲではないが焼けが残る） | 未着手 |

> **仕様確認メモ:** `pickHair()` は `BALDHEAD` 状態から呼ばれると `DEFAULT` に戻る（`addLovePlayer(100)`）。これはプレイヤーのハゲ引きコマンドによるもの。Fire.update() は `hairState != BALDHEAD` のときしか `pickHair()` を呼ばないため、燃焼ルートではこの「育毛」パスには入らない。

---

## 全体的な優先度メモ

| 優先 | 対象 | 理由 |
|---|---|---|
| **高** | `applyExternalMotion` NYD 着地回帰 | **バグ修正済みコードの回帰未追加** |
| **高** | `YukkuriNeedleRule` | ※直接テストはゼロだが `doActionOther` 経由で間接カバー済み。直接単体テストは追加価値あり |
| **高** | `YukkuriIllnessRule` FOOL/WISE 分岐 | 疫病伝播の感知差が正しく機能しないと流行阻止できない |
| **中** | `YukkuriDeadSearchRule` ランク/関係分岐 | 家族死亡反応は感情的コアメカニクス |
| **中** | `YukkuriSelectionRule` フィルタ群 | つがい・家族イベントの対象選択がすべてここ依存 |
| **中** | `YukkuriWakeupRule` guard 群 | 睡眠妨害の誤検知は行動ループに波及 |
| **低** | `YukkuriExcretionRule` diarrheaProb clamp | 影響範囲が限定的 |
| **低** | `YukkuriCoreStateRule` 境界値 | BodyVitalsTest で間接カバー済みの部分あり |
| **中** | ユーザーコマンド — 声かけ (VOICE) | voiceReaction の副作用がゼロテスト |
| **中** | ユーザーコマンド — 道具 NEEDLE/WATER/PEAL/BLIND 等 | ゆっくりへの直接状態変化系がほぼ未確認 |
| **低** | ユーザーコマンド — 床設置系 | GUI依存で headless ではスモークテストが限界 |
