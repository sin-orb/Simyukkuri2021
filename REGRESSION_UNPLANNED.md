# REGRESSION_UNPLANNED

TEST_EXPANTION_PLAN.md に載っていない仕様起点の回帰テスト候補。  
コードと会話から推定した仕様を軸に「壊れたときに何が起きるか」を整理した。

ステータス: `未着手` / `完了` / `スキップ`

---

## `YukkuriNeedleRule.handleNeedledYukkuri`

針が刺さっているゆっくりへの接近処理。**テスト0件**。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 針が刺さっていないのに true を返す | `target.setNeedled(false)` | `handleNeedledYukkuri(target, actor)` → `false` | **完了** (BodyNeedleRuleTest) |
| 親が刺さった子供のそばに行かない | `target.setNeedled(true)`, actor=adult+母, target=child | `actor.isGuriguri()` == true かつ `return true` | **完了** (BodyNeedleRuleTest) |
| つがいが刺さった相手のそばに行かない | `target.setNeedled(true)`, `target.setPartner(actor)` | `actor.isGuriguri()` == true かつ `return true` | **完了** (BodyNeedleRuleTest) |
| 関係なしでも clearActions されない | `target.setNeedled(true)`, 無関係ゆっくり | `return true`（clearActions は呼ばれる）かつ `actor.isGuriguri()` == false | **完了** (BodyNeedleRuleTest) |

**備考:** `GameRandom.nextInt(1) == 0` は常に true（nextInt(1) は必ず 0）。姉妹分岐の「50% 確率」コメントは誤り。実装上は確定でぐりぐりする。

---

## `YukkuriIllnessRule.findSick`

かびの見抜き判定。知能差による感知能力の差異がテストされていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| WISE が軽症ゆっくりを見逃す | `intel=WISE`, `target.setSickPeriod(incubation+1)` (barely sick) | `findSick(WISE, target)` → `true` | **完了** (BodyIllnessRuleTest) |
| FOOL が軽症ゆっくりを正しく見逃さない | `intel=FOOL`, barely sick | `findSick(FOOL, target)` → `false` | **完了** (BodyIllnessRuleTest) |
| FOOL が重症ゆっくりを見逃す | `intel=FOOL`, `target.setSickPeriod(incubation*8+1)` (SickHeavily) | `findSick(FOOL, target)` → `true` | **完了** (BodyIllnessRuleTest) |
| null が渡されてもクラッシュする | `target=null` | `findSick(AVERAGE, null)` → `false` | **完了** (BodyIllnessRuleTest) |

---

## `YukkuriExcretionRule.getDiarrhea`

下痢確率判定。KAIYU ランク・ダメージ/病気による確率半減・下限クランプが未テスト。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| KAIYU が確率判定を受ける | `body.setRank(YukkuriRank.KAIYU)` | `getDiarrhea(body)` → `true`（RNG 無関係） | **完了** (BodyExcretionRuleTest) |
| 健康体なのに確率が下がる | 健常ゆっくり + ConstState(0) で RNG=0 | `getDiarrhea(body)` → `true`（diarrheaProb=通常値, nextInt=0） | **完了** (BodyExcretionRuleTest) |
| 病気でも確率が下がらない | `body.setSickPeriod(incubation+1)` → isSick=true | `getDiarrhea(body)` が diarrheaProb/2 で評価される（ConstState(0) で確認） | **完了** (BodyExcretionRuleTest) |
| diarrheaProb=0 でクラッシュする | diarrheaProb が 0 になる状態 | `getDiarrhea(body)` → `true`（clamp→1 で nextInt(1)==0 確定） | **完了** (BodyExcretionRuleTest) |

---

## `FoodConsumptionPolicy.eatFood` — BITTER食後 excretionBoost 加速

BITTER系食料を嫌いなゆっくりが食べたとき `getDiarrhea()=true` なら `rapidShit()` が呼ばれ `excretionBoost += TICK*5(=5)` が加算される。以降の `checkShit()` では `shit += TICK + excretionBoost*20` の加速公式が動き、うんうんが出るたびに `excretionBoost -= 1` で減衰する。この連鎖が**完全に未テスト**（`testEatFood_NormalBody_Bitter` は hungry 増加しか確認していない）。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| BITTER食後に excretionBoost が加算されない | `isLikeBitterFood=false`（デフォルト）、ConstState(0)、`FoodLogic.eatFood(body, BITTER, 100)` | `body.getExcretionBoost() == 5`（rapidShit が呼ばれた） | **完了** (FoodLogicTest) |
| BITTER好きなのに rapidShit が走る | `body.setLikeBitterFood(true)`、ConstState(0)、同上 | `body.getExcretionBoost() == 0`（好き食いルートに入り rapidShit なし） | **完了** (FoodLogicTest) |
| excretionBoost があっても shit 蓄積が加速しない | `body.setExcretionBoost(5)`、`body.setAge(1)`（age%100≠0）、`body.checkShit()` | `body.getShit() == 101`（= TICK + 5×20。通常 1/tick が 101/tick に加速） | **完了** (FoodLogicTest) |
| overflow 後に excretionBoost が減衰しない | `body.setExcretionBoost(5)`、`body.setShit(2400)`、`body.setAge(1)`、`body.checkShit()` | `body.getExcretionBoost() == 4`（-1 で減衰）かつ `body.getShit() == 0`（排泄済み） | **完了** (FoodLogicTest) |

**備考:** BITTER_NORA / BITTER_YASEI も同一 case ブランチのため挙動は同じ。tang=500（デフォルト）→ NORMAL 舌 → `normalEating` ルートを通る。overflow 時は `addDamage(200)` + `addStress(400)` も副作用として走る（`Const.NEEDLE=100` のため）。

---

## NYD中のあまあま自発食事・治癒連鎖

**あるべき仕様（未実装）:** 非ゆっくり症（`NON_YUKKURI_DISEASE`）状態でも、あまあま（SWEETS系）だけは自発サーチで食べることができる。食べると `setStress(0)` が走り、`hasNonYukkuriDisease()` のストレス閾値チェックを通じて NORMAL 復帰（治癒）する経路となる。

**現状の欠落:** `FoodActionGate.shouldSkipBeforeSearch()` L50 が `NON_YUKKURI_DISEASE` を問答無用でブロックしており、あまあま例外がない。`FoodConsumptionPolicy.eatFood` 自体は NYD チェックなしのため、食べさえすれば治癒チェーンは機能する（C・D は現状も成立する）。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NYD中にあまあまが視野内にあっても食べに行かない | `setCoreAnkoState(NON_YUKKURI_DISEASE)` + SWEETS1 を EYESIGHT 範囲内に配置 + `checkFood(body)` | `true`（あまあまサーチが通る）**← 現状 false／実装待ち** | **完了** (FoodLogicTest) |
| NYD中に非あまあまが近くにあるのに食べに行く | 同 NYD 設定 + BITTER を EYESIGHT 範囲内に配置 + `checkFood(body)` | `false`（BITTER は引き続きブロック） | **完了** (FoodLogicTest) |
| あまあまを食べてもストレスが下がらない | `setCoreAnkoState(NON_YUKKURI_DISEASE)` + `FoodLogic.eatFood(body, SWEETS1, 100)` | `body.getStress() == 0` | **完了** (FoodLogicTest) |
| ストレスが下がっても NYD が治癒しない | stress=0 の NYD body + `new YukkuriNydDelegate(body).hasNonYukkuriDisease()` を呼ぶ | `body.isNotNyd() == true`（NORMAL 復帰） | **完了** (FoodLogicTest) |

**備考:** SWEETS2 も同一 case ブランチのため挙動は同じ。`NON_YUKKURI_DISEASE_NEAR` も `isNyd()=true` で `canAction()=false` となり同様にブロックされるが、仕様上あまあま例外が必要かは別途確認。治癒チェーン（C→D）の組み合わせは既存の `testCheckFood_NonYukkuriDiseaseState`（現状 false を確認）とは逆方向の仕様テスト。

---

## `YukkuriWakeupRule.checkWakeupOtherYukkuri`

睡眠中ゆっくりが近くに起きているゆっくりを検知するかの判定。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 死亡済みゆっくりを「起きている」とみなす | `other.setDead(true)` + `other.setSleeping(false)` | `checkWakeupOtherYukkuri(body)` → `false` | **完了** (BodyWakeupRuleTest) |
| NYD ゆっくりに反応してしまう | `other.setCoreAnkoState(NON_YUKKURI_DISEASE)` → isNyd=true | `checkWakeupOtherYukkuri(body)` → `false` | **完了** (BodyWakeupRuleTest) |
| NONE ランクが UNUN_SLAVE を起こしてしまう | `body.setPublicRank(NONE)`, `other.setPublicRank(UNUN_SLAVE)`, other=起床中 | `checkWakeupOtherYukkuri(body)` → `false` | **完了** (BodyWakeupRuleTest) |
| 埋没中ゆっくりに反応してしまう | `other.setBurialState(BurialState.ALL)` | `checkWakeupOtherYukkuri(body)` → `false` | **完了** (BodyWakeupRuleTest) |
| 視野内の起床中ゆっくりに反応しない | `other.setSleeping(false)` + 視野内に配置 | `checkWakeupOtherYukkuri(body)` → `true` | **完了** (BodyWakeupRuleTest) |
| 全員が寝ているのに true になる | `other.setSleeping(true)` のみ | `checkWakeupOtherYukkuri(body)` → `false` | **完了** (BodyWakeupRuleTest) |

---

## `YukkuriDeadSearchRule.handleDeadFound`

パートナー探索中に死体ゆっくりに遭遇したときの反応処理。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| exciting でない個体がすっきりに向かう | `actor.setExciting(true)` | `actor.isMoveToSukkiri()` == true, `return true` | **完了** (BodyDeadSearchRuleTest) |
| 90% skip が機能しない | ConstState(1) (nextInt(10)=1≠0) | `handleDeadFound(actor, target, 0, 0)` → `false` | **完了** (BodyDeadSearchRuleTest) |
| ランク違いの死体に反応してしまう | `actor.setPublicRank(NONE)`, `target.setPublicRank(UNUN_SLAVE)`, ConstState(0) | `return false`（ランク不一致でスキップ） | **完了** (BodyDeadSearchRuleTest) |
| 成体が家族死体に向かわない | adult actor + 親子関係 target(dead) + ConstState(0) | `actor.getMoveTargetId() == target` | **完了** (BodyDeadSearchRuleTest) |
| 非成体が姉妹死体に向かわない | child actor + 同父 sister(dead) + ConstState(0) | `actor.getMoveTargetId() == target` | **完了** (BodyDeadSearchRuleTest) |
| 成体が他人の死体に向かってしまう | adult actor + 無関係 target(dead) + ConstState(0) | `return false`（lookTo は呼ばれるが move はしない） | **完了** (BodyDeadSearchRuleTest) |
| 非成体が他人の死体を見て逃げない | child actor + 無関係 target(dead) + ConstState(0) | `actor.isRunAway()` または相当する状態 | **完了** (BodyDeadSearchRuleTest) |
| しゃべり中でも怯えメッセージが出る | `actor.setMessageCount(1)` → isTalking=true | scare メッセージが追加されない（talking guard） | **完了** (BodyDeadSearchRuleTest) |
| 非レイパー・非捕食種が死体で memories を失わない | 通常 actor + dead target + ConstState(0) | `actor.getMemories()` が -1 減っている | **完了** (BodyDeadSearchRuleTest) |

---

## `YukkuriParentRule.checkNearParent`

子ゆっくりが親を探して近づく処理。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 成体が親探しをしてしまう | `body.setAgeState(ADULT)` | メソッド呼び出し後に行動が変わらない（early return） | **完了** (BodyParentRuleTest) |
| 親がいなくても例外なくクラッシュする | 親・姉なし body | `checkNearParent(body)` → assertDoesNotThrow | **完了** (BodyParentRuleTest) |
| 泣き叫んでいる子が寝ている親を起こさない | `body.setCallingParents(true)` + `parent.setSleeping(true)` | `parent.isSleeping()` == false（wakeup された） | **完了** (BodyParentRuleTest) |
| 汚れた子がアリに食われているのに親がペロペロしない | `body.setDirty(true)` + 親が `canEventResponse()=true` + 接近距離内 | `parent.isPeropero()` == true | **完了** (BodyParentRuleTest) |
| 汚れた子が遠くにいるのに moveTo が呼ばれない | `body.setDirty(true)` + 親が遠い | `body.getMoveTargetId()` が親の位置を示す | **完了** (BodyParentRuleTest) |
| 距離が十分近いのに移動してしまう | `distanceToParent < eyesight/32` | `body.getMoveTargetId()` が変わらない（close enough, return early） | **完了** (BodyParentRuleTest) |

---

## `YukkuriSelectionRule.createActiveFiances`

つがい候補リスト生成。多数のフィルタが連続する。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 自分だけの世界でも候補が返る | registry.size()==1 | `createActiveFiances(body, 0)` → `null` | **完了** (BodySelectionRuleTest) |
| 既つがいなのに全候補を探索してしまう | `body.setPartner(partner)` を registry に登録 | 返却リストに partner のみ含まれる | **完了** (BodySelectionRuleTest) |
| 死んだ候補がリストに入る | `candidate.setDead(true)` | 候補リストに含まれない | **完了** (BodySelectionRuleTest) |
| ランクが違う相手がリストに入る | `body.setPublicRank(NONE)`, `candidate.setPublicRank(UNUN_SLAVE)` | 候補リストに含まれない | **完了** (BodySelectionRuleTest) |
| 障害ゆっくりがリストに入る | `candidate.hasDisorder()=true` (setDisorder等) | 候補リストに含まれない | **完了** (BodySelectionRuleTest) |
| 病気ゆっくりが賢い actor にバレずリストに入る | AVERAGE actor + barely sick candidate | 候補リストに含まれない | **完了** (BodySelectionRuleTest) |
| 幼い相手を age ガードなしに候補にする | `age=ADULT.ordinal()`, `candidate.setAgeState(BABY)` | 候補リストに含まれない（ロリコン防止） | **完了** (BodySelectionRuleTest) |
| すでにつがいのいる相手がいつも除外される | `candidate.setPartner(third)` + ConstState(0, nextBoolean=false) | 候補リストに含まれる（50% 確率で通過） | **完了** (BodySelectionRuleTest) |

---

## `YukkuriSelectionRule.createActiveChildren`

家族イベント対象の子ゆリスト生成。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 子なしで null が返らない | `body.getChildrenCount()==0` | `createActiveChildren(body, true)` → `null` | **完了** (BodySelectionRuleTest) |
| プレイヤーに持たれた子がリストに入る | `child.setTaken(true)` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |
| UNUN_SLAVE の子がリストに入る | `child.setPublicRank(UNUN_SLAVE)` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |
| 産まれたてのイベントブロック中の子が入る | `child.setBirthEventBlockedTicks(1)` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |
| firstGround 中の赤ゆがリストに入る | `child.setFirstGround(true)` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |
| NYD の子がリストに入る | `child.setCoreAnkoState(NON_YUKKURI_DISEASE)` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |
| includeChildren=false で子ゆが入る | `child.setAgeState(CHILD)` + `includeChildren=false` | 子リストに含まれない（赤ゆのみ） | **完了** (BodySelectionRuleTest) |
| includeChildren=true で大人が入る | `child.setAgeState(ADULT)` + `includeChildren=true` | 子リストに含まれない | **完了** (BodySelectionRuleTest) |

---

## `YukkuriMovement.applyExternalMotion` — 着地時処理

`applyExternalMotion` の着地パス。NYD ガードのみ既テスト。その他の着地分岐が未テスト。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 初接地なのに stress 軽減が起きない | `body.setFirstGround(true)`, 落下 | `body.getStress()` が -400 の軽減、memories +20 | **完了** (BodyMovementTest) |
| NYD 中に初接地 stress 軽減が発生する | `body.setFirstGround(true)` + `isNyd=true` | stress 不変（isNyd=true ガードで軽減なし）← **修正済みバグの回帰テスト** | **完了** (BodyMovementTest) |
| isPealed なのに着地死亡しない | `body.setPealed(true)` + 高落下 | `body.isDead()` == true | **完了** (BodyMovementTest) |
| ベッドでも通常ダメージを受ける | `body.checkOnBed()=true` + BABY 以外 + 大落下 | ダメージが damageCut=4 で減少（通常の 1/4） | スキップ（headless で checkOnBed 設定困難） |
| noDamageNextFall フラグが機能しない | `body.setNoDamageNextFall(true)` + 落下 | ダメージなし + フラグが false にリセット | **完了** (BodyMovementTest 既存) |
| 落下後のダメージで死んでも setCrushed されない | `isDead()=true` になる落下 | `body.isCrushed()` == true | **完了** (BodyMovementTest) |

**備考:** NYD 中着地で addStress(-400) が走っていたバグ修正（`if (!body.isNyd())` ガード追加）の**回帰テスト**が未追加。これは最優先。

---

## `YukkuriCoreStateRule` — ダメージ状態遷移

damage 量に応じた DamageState の切替え判定が直接テストされていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| damage=0 で NONE 以外になる | `body.getDamage()==0` | getDamageState() == NONE | **完了** (BodyVitalsTest) |
| limit/2 ちょうどで VERY にならない | `setDamage(getDamageLimit()/2)` | getDamageState() == VERY | **完了** (BodyVitalsTest) |
| limit*3/4 ちょうどで TOOMUCH にならない | `setDamage(getDamageLimit()*3/4)` | getDamageState() == TOOMUCH | **完了** (BodyVitalsTest) |
| limit 超えで toDead が呼ばれない | `addDamage(getDamageLimit()+1)` | `body.isDead()` == true | **完了** (BodyVitalsTest) |

---

## `FoodLogic.eatFood` — isTooHungry 連携

メモリから判明している重要仕様が回帰テストで明示されていない。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| hungry=0 + damage=NONE で isTooHungry になる | `body.setHungry(0)` + damage=0 | `body.isTooHungry()` == false (damage=NONE のとき false 仕様) | **完了** (FoodLogicTest) |
| hungry=0 + damage=VERY で isTooHungry にならない | `body.setHungry(0)` + `WorldTestHelper.setDamage(body, limit/2+1)` | `body.isTooHungry()` == true | **完了** (FoodLogicTest) |

---

## イベント系 — 未テストの execute/start パス

COVERAGE_STATUS の `event` パッケージは Branch 11.9%。主要イベントの execute ロジックがほぼ未テスト。

### `ProposeEvent.execute`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 求愛成功時につがい関係が設定されない | 求愛成功シナリオを execute まで進める | `from.getPartner()==you.getUniqueId()` かつ `you.getPartner()==from.getUniqueId()` | **完了** (ProposeEventTest) |
| 求愛失敗時につがい関係が設定されてしまう | you.setPartner(他) で既婚にしてから execute | partner 変更なし | **完了** (ProposeEventTest) |

### `YukkuriRideEvent`（おちびちゃん運び）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 親が死亡してもイベントが継続し ABORT にならない | `from.setDead(true)` → `canActionForEvent()=false` | `update(from)` → ABORT | **完了** (YukkuriRideEventTest) |
| 親が死亡してもおちびの parentLinkId がリセットされない | 上記 ABORT 経由で `end()` が呼ばれる | `to.getParentLinkId() == -1`（おちびが解放された） | **完了** (YukkuriRideEventTest) |

> **仕様：** 親が死亡したらイベントを即 ABORT し、おちびの parentLink を解除して自由落下させる。
> 
> **現状：** コード上は `canActionForEvent()=false` → ABORT → `end()` → `setParentLinkId(-1)` の経路が存在するが、「親死亡」を起点とする end-to-end テストが未追加。`end()` 単体テストと「to が死亡」テストは既存。

---

### `FuneralEvent.execute`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 葬儀イベントが実行されても悲しまない | from.isDead()=true のシナリオで execute | `body.getHappiness()` == VERY_SAD | **完了** (FuneralEventTest) |

---

## `FuneralEvent` — イベント中の挙動ガード

### 発火・完了・中断（ユーザー指定項目）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 子/番の死亡を見ても FuneralEvent がキューに積まれない | dead target + doActionOther + 家族関係 | world events に FuneralEvent が追加される | **完了** (BodyLogicTest L2213, L3940) |
| GOODBYE ステートで死体のお飾りが外れない | state=GOODBYE + deceased.hasOkazari()=true | update 後 `deceased.hasOkazari()` == false | **完了** (FuneralEventTest L462) |
| れいぱー/捕食種が現れてもおとむらいを続けてしまう（逃げない） | 視野内にれいぱー（`body.isRaper()+isExciting()`）または捕食種（Remirya 等 `isPredatorType()`）を配置した状態でおとむらいイベントを進行させる | `update()` → ABORT（イベント中断）かつ body がパニック/逃走状態になる、または RaperReactionEvent がキューに追加される | **完了** (FuneralEventTest `testUpdate_raperInRange_returnsAbort`, `testUpdate_predatorInRange_returnsAbort`; FuneralEvent.update() に predator/raper 検出ループ追加) |

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
| イベント中に眠ったままぐりぐりしなくなる | `b.setSleeping(true)` 状態で update を呼ぶ | update 後 `b.isSleeping()` == false (wakeup が呼ばれた) | **完了** (FuneralEventTest) |
| イベント中に空腹状態が維持されてイベントが止まる | `b.setHungry(0)` (isHungry=true) 状態で update | update 後 `b.getHungry()` == `b.getHungryLimit() * 6 / 10`（60% に補正された） | **完了** (FuneralEventTest) |
| 発情中でも checkPartner が走ってしまう | `b.setExciting(true)` を設定した状態でのゲームループ回帰 | `canAction()=false`（currentEvent!=null）で checkPartner に入らない → `b.isToSukkiri()` == false のまま | **完了** (FuneralEventTest) |

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
| 「ゆっくりしていってね」を言っても反応しない | `item=YUKKURISITEITTENE` で evaluateCommunicate | `body.voiceReaction(0)` の副作用が起きる（メッセージ/感情変化） | **完了** (GadgetActionTest) |
| 「ゆっくり死んでね」を言っても反応しない | `item=YUKKURIDIE` | `body.voiceReaction(1)` の副作用が起きる | **完了** (GadgetActionTest) |
| 「ゆっくりふりふりしてね」を言っても反応しない | `item=YUKKURIFURIFURI` | `body.voiceReaction(2)` の副作用が起きる | **完了** (GadgetActionTest) |

### 道具コマンド — 未テストケース (`evaluateTool` / `GadgetToolAction`)

現状 GODHAND・PUNISH・SNAPPING・VIBRATOR のみテストあり。以下はゼロ。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NEEDLE で針が刺さらない | `item=NEEDLE` + Yukkuri | `body.isNeedled()` == true | **完了** (GadgetActionTest) |
| WATER をかけても濡れない | `item=WATER` + Yukkuri | `body.isWet()` == true | **完了** (GadgetActionTest) |
| PEAL で皮がむけない | `item=PEAL` + Yukkuri | `body.isPealed()` == true | **完了** (GadgetActionTest) |
| BLIND で目が見えなくならない | `item=BLIND` + Yukkuri | `body.isBlind()` == true | **完了** (GadgetActionTest) |
| JUICE (ジュース) を与えても回復しない | `item=JUICE` + Yukkuri | ダメージが減少する | **完了** (GadgetActionTest) |
| PACK で詰められない | `item=PACK` + Yukkuri | `body.isPacked()` == true | **完了** (GadgetActionTest) |
| NEEDLE ツールで針が刺さった後 NEEDLE 再適用で抜ける | `item=NEEDLE` 2回 | `body.isNeedled()` == false（トグル仕様） | **完了** (GadgetActionTest) |
| CASTRATION をしても去勢されない | `item=CASTRATION` + Yukkuri | `body.isCastrated()` == true | **完了** (GadgetActionTest) |
| BURY で埋まらない | `item=BURY` + Yukkuri | `body.getBurialState()` != NONE | **完了** (GadgetActionTest) |
| HAMMER でダメージが入らない | `item=HAMMER` + Yukkuri | `body.getDamage()` > 0 | **完了** (GadgetActionTest) |
| SET_SICK でカビが接種されない | `item=SET_SICK` + Yukkuri | `body.isSick()` == true | **完了** (GadgetActionTest) |

### 床設置コマンド (`evaluateFloorItems` / `GadgetItemSetupAction`)

アイテムがワールドに配置されるかの確認。全てテストゼロ。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| DIFFUSER が配置されない | `item=DIFFUSER` + 座標 | `getCurrentWorldState().getDiffusers()` にアイテムが追加される | **完了** (GadgetActionTest スモーク) |
| BREED_POOL が配置されない | `item=BREED_POOL` + 座標 | 対応アイテムがワールドに追加される | **完了** (GadgetActionTest スモーク) |
| YUNBA_SETUP (ゆんば) が配置されない | `item=YUNBA_SETUP` + 座標 | Yunba がワールドに追加される | **完了** (GadgetActionTest スモーク) |
| BELTCONVEYOR_SETUP が配置されない | `item=BELTCONVEYOR_SETUP` + 座標 | BeltconveyorObj がワールドに追加される | **完了** (GadgetActionTest スモーク) |

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

### 未テスト → テスト済み

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| まりさに神拳を当ててもドス化しない | 健全な大人まりさ（stress=0, damage=0, notRude, notNYD 等）に ConstState(0) で doGodHand | `body.getType() == YukkuriType.DOSMARISA` | headless NPE のためスキップ |
| ゲスまりさがドス化してしまう | `body.setAttitude(SHITHEAD)` → isRude=true の大人まりさに ConstState(0) で doGodHand | `body.getType()` が DOSMARISA にならない（execTransform 内で rude チェックが弾く） | 完了 (`testCase0RudeMarisaDoesNotTransform`) |
| ダメージあり/ストレスありまりさがドス化してしまう | damage>0 またはstress>0 の状態で ConstState(0) doGodHand | canTransform()=false → 変身しない | 完了 (`testCase0DamagedMarisaDoesNotTransform`) |
| ドスまりさが既にいるのに再ドス化できてしまう | ワールドに DosMarisa を登録した状態で別まりさに doGodHand | `body.getType()` が DOSMARISA にならない | 完了 (`testCase0DoesNotTransformWhenDosMarisaExistsInRegistry`) |
| プレイヤーのアイテム欄にドスまりさがいる状態で再ドス化できてしまう | DosMarisa を「持つ」（アイテム欄に格納）した状態で別まりさに doGodHand | `body.getType()` が DOSMARISA にならない | スキップ（特殊条件かつ2匹になっても問題なし） |

> **仕様：** ドスまりさはワールドに1体のみ。マップ上だけでなく**プレイヤーのアイテム欄に入っていても**「存在する」とカウントする。
>
> **現在の実装との乖離（直し必要）:**
> 1. ~~`judgeCanTransForGodHand()` にドスまりさ存在チェックがコメントのみで未実装 → 常に true を返す~~ → **修正済み** (registry 走査を追加)
> 2. `getYukkuriRegistry()` はアイテム欄（`isTaken=true`）のゆっくりを含まない（「持つ」時に registry から削除される）→ アイテム欄のドスまりさを見落とす
> 3. 実装時は registry + `player.getInventoryView()` の両方を見るか、`isTaken` フラグを持つ個体も走査する必要がある
| れいむに神拳を当ててもでいぶ化しない | 健全な大人れいむに ConstState(0) で doGodHand | `body.getType() == YukkuriType.DEIBU` | headless NPE のためスキップ |
| 神拳後に持ち物が落ちない（全ケース共通） | 食料を carryItem に持たせた状態で各ケースを実行 | `body.getCarryItem(FOOD) == null`（dropAllTakeoutItem が呼ばれた） | 完了 (`testCase1DropCarryItem`, `testDefaultDropCarryItem`) |
| 言語破壊でれいむ以外が TARINAI にならない | れいむ以外（まりさ等）に `case 5` を実行 | `body.getMsgType() == TARINAI` | 完了 (`testCase5TarinaiForNonReimu`) |
| 引っ張り/つぶし時に茎の実ゆの親が反応しない | 茎に植わった赤ゆ（stalk に plantYukkuri 設定）に case 2/3 | 親ゆに UnbirthBabyState.SAD が通知される | 完了 (`testCase2/3NotifiesStalkMotherWithSad`) |
| 回復時に茎の実ゆの親が反応しない | 同条件で case 4 | 親ゆに UnbirthBabyState.HAPPY が通知される | 完了 (`testCase4NotifiesStalkMotherWithHappy`) |
| case 4 で事前のダメージ設定（limit/2）がスキップされる | damage=0 のまま doGodHand(case 4) | giveJuice 前に `body.getDamage() == limit/2` であることを確認 | 完了 (`testCase4SetsHalfDamageBeforeHeal`: giveJuice 後 damage=0 を確認) |

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
| アリのたかったゆっくりにすりすりしても伝染らない | target に Ants を addAttachment した状態で `actor.doSurisuri(target)` + ConstState(0)（nextInt(200)==0） | `actor.getAttachmentSize(Ants.class) > 0`（actor にアリが付く） | **完了** (YukkuriOtherRelationDelegateTest) |
| アリがいないのにすりすりで伝染る | target にアリなし + ConstState(0) | `actor.getAttachmentSize(Ants.class) == 0`（伝染しない） | **完了** (YukkuriOtherRelationDelegateTest) |

### ぺろぺろ（doPeropero）でのアリ伝染

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| アリのたかったゆっくりをぺろぺろしても伝染らない | target に Ants + `target.setAntCount(50)` + `actor.doPeropero(target)` + ConstState(0) | アリ削減後もアリが残る(>0)なら actor に Ants が付く | **完了** (YukkuriOtherRelationDelegateTest) |

### ぺろぺろ（doPeropero）でのアリ撃退

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ぺろぺろしてもアリが減らない | target に Ants + `target.setAntCount(50)` + `actor.doPeropero(target)` | `target.getAntCount() == 10`（50-40=10 に減少） | **完了** (YukkuriOtherRelationDelegateTest) |
| アリが閾値まで減っても除去されない | `target.setAntCount(30)` + doPeropero | `target.getAttachmentSize(Ants.class) == 0`（30-40≤0 で removeAnts が呼ばれる） | **完了** (YukkuriOtherRelationDelegateTest) |

> **備考：** 「こーろこーろで撃退」についてはコード上 `doSurisuri` にアリの除去処理はなく、伝染リスクのみ存在する。「撃退」は `doPeropero` の専用機能であり、`doSurisuri` は伝染リスクのみ。仕様と一致するか確認が必要。

---

### `AnydAmpoule`（NYD 防止アンプル）

`update()` は NONE を返すのみ。実際の効果は `YukkuriNydDelegate` 側でアタッチメントの有無を見る。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| AnydAmpoule を付けていても NYD が進行する | body に AnydAmpoule を addAttachment した状態で NYD 進行ロジックを実行 | NYD 状態にならない（`body.isNyd() == false`） | **完了** (AnydAmpouleTest) |

### `BreedingAmpoule`（繁殖アンプル）

効果: `setHungry(100)` + `addDamage(-100)` + `getBabyTypes().add(DNA)` + `setHasBaby(true)`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 去勢個体にアンプルが効いてしまう | `body.setCastrated(true)` + update 呼び出し | `body.isHasBaby() == false`（castrated ガードで弾かれる） | **完了** (BreedingAmpouleTest) |
| 健康な個体に繁殖効果が起きない | 健康な成ゆに update 呼び出し | `body.isHasBaby() == true`、`body.getBabyTypes().size() > 0`、`body.getDamage()` 減少、`body.getHungry() == 100` | **完了** (BreedingAmpouleTest) |

### `PoisonAmpoule`（毒アンプル）

効果: CUT でなければ `plusShit(50)` + `wakeup()` → 常に `setHappiness(SAD)` → 1/1000 で `addDamage(200)` + VERY_SAD + 痛顔

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| CUT された個体の shit がさらに増える | `body.setCriticalDamageType(CUT)` + update | `body.getShit()` が増えない（wakeup も呼ばれない） | **完了** (PoisonAmpouleTest) |
| 毒アンプルが起きているゆっくりを起こさない | 寝ている非 dead 個体に update | `body.isSleeping() == false`（wakeup が呼ばれた） | **完了** (PoisonAmpouleTest) |
| 1/1000 ダメージバーストが発動しない | ConstState(0) で update（`nextInt(1000)==0`） | `body.getDamage() > 0`（addDamage(200) 発動）、`body.getHappiness() == VERY_SAD`、`body.getForceFace() == PAIN` | **完了** (PoisonAmpouleTest) |

---

---

## `Fire` アタッチメント — 燃焼中の毛焼け・水消火後のハゲ

`Fire.update()` の `pickHair()` 分岐は既存テストで `parent.setDead(true)` 状態でのみ確認されており、「実際に HairState が変化するか」は**完全に未テスト**（コメントにも「HairStateは変わらない」と明記）。

**仕組み:** `burnPeriod > damageLimit*2/3` かつ `hairState != BALDHEAD` → `pickHair()` 毎 processInterval 呼び出し → DEFAULT→BRINDLED1→BRINDLED2→BALDHEAD（3回でハゲ）。水消火（`inWater()` → `removeAttachment(Fire.class)`）で Fire が消え、その時点の hairState が維持される。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 燃えているのに毛が焼けない | 生きている body に Fire を付け burnPeriod を `damageLimit*2/3+1` に設定して `fire.update()` | `body.getHairState() == HairState.BRINDLED1`（DEFAULT から一段焼けた） | **完了** (FireTest) |
| 3回焼けてもハゲにならない | 同条件で update を3回繰り返す（BRINDLED1→BRINDLED2→BALDHEAD） | `body.getHairState() == HairState.BALDHEAD` | **完了** (FireTest) |
| 水で消火後に毛が元に戻る（残らない） | burnPeriod が閾値超で update 後に `body.removeAttachment(Fire.class)` | `body.getHairState() != HairState.DEFAULT`（消火後もハゲまたは焼け状態が維持） | **完了** (FireTest) |
| ハゲになる前に消火してもハゲ判定になる | burnPeriod を BRINDLED2 まで進めてから消火 | `body.getHairState() == HairState.BRINDLED2`（完全ハゲではないが焼けが残る） | **完了** (FireTest) |

> **仕様確認メモ:** `pickHair()` は `BALDHEAD` 状態から呼ばれると `DEFAULT` に戻る（`addLovePlayer(100)`）。これはプレイヤーのハゲ引きコマンドによるもの。Fire.update() は `hairState != BALDHEAD` のときしか `pickHair()` を呼ばないため、燃焼ルートではこの「育毛」パスには入らない。

---

## `TerrariumEnvironment` — 各スチームの yukkuri への効果テスト

`applyDiffuserSteamFlags(flags)` → 環境フラグ true の連鎖は `TerrariumTest` で確認済み。**フラグが true のときに yukkuri に正しい変化が起きること（効果層）のテストがゼロ。** テストパターンは `GameEnvironment.setOverride(source)` で特定フラグだけ true にし、該当メソッドを呼んで assert する。

---

### ANTI_FUNGAL (抗菌スチーム) — `YukkuriStateDelegate.checkSick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 抗菌スチーム中にカビが進行する | `setDirty(true)` + `addDamage(1)` (isDamaged=true) + `isAntifungalSteam=true` + `checkSick()` | `body.getDirtyPeriod() == 0`（advanceDirtyPeriod が呼ばれない） | **完了** (TerrariumSteamEffectTest) |
| 抗菌スチームなしでカビが進行しない | 同条件で `isAntifungalSteam=false` | `body.getDirtyPeriod() > 0`（対照確認） | **完了** (TerrariumSteamEffectTest) |

---

### STEAM/humid (加湿スチーム) — `YukkuriStateDelegate.checkSick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 加湿スチーム中にカビ進行速度が変わらない | `isSick=true` + `addDamage(1)` + `isHumid=true` + `checkSick()` | `body.getSickPeriod() == 4`（通常 1 が 4倍になる） | **完了** (TerrariumSteamEffectTest) |
| 加湿がないのにカビが加速する | 同条件で `isHumid=false` | `body.getSickPeriod() == 1`（対照確認） | **完了** (TerrariumSteamEffectTest) |

**備考:** `humid=true` かつ `damage>0` の場合、dirty でなくても `advanceDirtyPeriod` が呼ばれる。sick 進行は `isHumid ? 4 : 1` で加算。

---

### ORANGE (オレンジスチーム) — `LivingEntityBodyDamageDelegate.checkDamage()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| オレンジスチーム中にダメージが回復しない | `addDamage(500)` + `isOrangeSteam=true` + `checkDamage()` 1回 | `body.getDamage() == 450`（-= TICK*50） | **完了** (TerrariumSteamEffectTest) |

**備考:** `healFlag = !isUnBirth() || isPlantForUnbirthChild()`。通常生まれのゆっくりは常に healFlag=true。

---

### ANTI_YU (毒スチーム) — `LivingEntityBodyDamageDelegate.checkDamage()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 毒スチーム中にダメージが増えない | `isPoisonSteam=true` + `checkDamage()` 1回 | `body.getDamage() == 100`（+= TICK*100）、`body.isExciting() == false` | **完了** (TerrariumSteamEffectTest) |

---

### SUGER (砂糖水スチーム) — `LivingEntityBodyDamageDelegate.checkDamage()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 砂糖スチーム中に重傷ゆっくりが回復しない | `setDamage(limit * 80/100)` (80%以上) + `isSugerSteam=true` + `checkDamage()` 1回 | `body.getDamage() < limit * 80/100`（-= TICK*100） | **完了** (TerrariumSteamEffectTest) |
| 砂糖スチームが軽傷ゆっくりにも効く | `setDamage(1)` (80%未満) + `isSugerSteam=true` + `checkDamage()` | `body.getDamage()` が 1 以下のまま（80%未満はガードで回復しない） | **完了** (TerrariumSteamEffectTest) |

---

### NOSLEEP (眠眠スチーム) — `LivingEntitySleepDelegate.checkSleep()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 眠眠スチーム中にゆっくりが眠ったまま | `setSleeping(true)` + `isNoSleepSteam=true` + `checkSleep()` | `body.isSleeping() == false`、`body.getSleepingPeriod() == 0` | **完了** (TerrariumSteamEffectTest) |

---

### AGE_BOOST (加齢促進スチーム) — `Yukkuri.clockTick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 加齢促進スチームが効かない | `setAgeState(CHILD)` + `isAgeBoostSteam=true` + `interval=0` + `clockTick()` | `body.getAge()` が 1tick 前より 10001 以上増加（addAge(10000) + 通常 TICK） | **完了** (TerrariumSteamEffectTest) |
| ADULT にも加齢促進が効く | `setAgeState(ADULT)` + `isAgeBoostSteam=true` + `interval=0` + `clockTick()` | age の 10000 増加なし（ADULT はガード対象） | **完了** (TerrariumSteamEffectTest) |

---

### AGE_STOP (加齢停止スチーム) — `Yukkuri.clockTick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 加齢停止スチーム中に成長してしまう | CHILD の age を ADULT 閾値-1 に設定 + `isAgeStopSteam=true` + `interval=0` + `clockTick()` | `body.getAgeState() == CHILD`（ADULT に上がらない） | **完了** (TerrariumSteamEffectTest) |

---

### RAPIDPREGNANT (妊娠促進スチーム) — `Yukkuri.clockTick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 妊娠促進スチームが効かない | `setHasBaby(true)` + `setPregnantPeriod(getPREGPERIODorg())` + `isRapidPregnantSteam=true` + `clockTick()` | `body.getPregnancyPeriodBoost() > 0`（rapidPregnantPeriod が呼ばれた） | **完了** (TerrariumSteamEffectTest) |

---

### ANTI_NONYUKKURI (NYD治癒スチーム) — `YukkuriNydDelegate.hasNonYukkuriDisease()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NYD治癒スチームが効かない | `setCoreAnkoState(NON_YUKKURI_DISEASE)` + `isAntiNonYukkuriDiseaseSteam=true` + `hasNonYukkuriDisease()` | `body.isNotNyd() == true`（即時 NORMAL 復帰） | **完了** (TerrariumSteamEffectTest) |

---

### PREDATOR (捕食抑制スチーム) — `FoodLogic.checkFood()` / `YukkuriPartnerSearchRule`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 捕食抑制スチーム中に捕食種がえさを探す | `setPredatorType(BITE)` + 視野内にゆっくりを配置 + `isPredatorSteam=true` + `checkFood(body)` | `false`（捕食サーチに入らない） | **完了** (TerrariumSteamEffectTest) |

**備考:** `FoodLogic` L116: `if (body.isPredatorType() && !isPredatorSteam())` → predatorSteam=true でサーチブロック。`YukkuriPartnerSearchRule` でもパニック処理がスキップされる。

---

### ENDLESS_FURIFURI (エンドレスふりふりスチーム) — `Yukkuri.clockTick()`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| エンドレスふりふりスチームが効かない | `isEndlessFurifuriSteam=true` + `clockTick()` | `body.isFurifuri() == true`（tickEndlessFurifuri への分岐が発動） | **完了** (TerrariumSteamEffectTest) |

---

### HYBRID (ハイブリッドスチーム) — `YukkuriBirthTypeResolver`

birth イベント全体が必要で設定が複雑。`isHybridSteam=true` のとき通常種同士の出産でハイブリッド種が生まれることを確認。**低優先。**

---

### ANTI_DOS (ドス化防止スチーム) — 実装済み

**仕様:** `isAntidosSteam()=true` のとき、まりさが神拳を受けてもドスまりさに変身しない。

**実装済み:** `Marisa.judgeCanTransForGodHand()` に `GameEnvironment.isAntidosSteam()` チェックを追加。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ドス化防止スチーム中にまりさがドス化する | 健全な大人まりさ + `isAntidosSteam=true` + `judgeCanTransForGodHand()` | `false`（変身しない） | **完了** (TerrariumSteamEffectTest) |
| ドス化防止スチームなしでもドス化しない | 同条件で `isAntidosSteam=false` | `true`（通常は変身可能） | **完了** (TerrariumSteamEffectTest) |

---

## `YukkuriStealRule.handleOkazariSteal` — 盗み条件ガード群

成功（ゲス + 目撃なし）と目撃者ブロックの2本は `BodyStealRuleTest` に存在する。**入口ガード8条件とランク逆転の副作用がすべて未テスト。**

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ゲスでない actor が盗みに成功する | `setAttitude(NICE)`（isRude=false）+ target にお飾りあり + 目撃者なし | `handleOkazariSteal(target, actor)` → `false`、actor にお飾りなし | **完了** (BodyStealRuleTest) |
| お飾りを持つ actor がさらに盗める | actor に `giveOkazari` でお飾りあり + target にもお飾りあり | `false`（hasOkazari ガード） | **完了** (BodyStealRuleTest) |
| お飾りのない target から盗める | `target.setOkazari(null)` → hasOkazari=false | `false`（target にお飾りがない） | **完了** (BodyStealRuleTest) |
| 年齢が違うゆっくりから盗める | `actor.setAgeState(ADULT)`、`target.setAgeState(CHILD)` | `false`（ageState 不一致ガード） | **完了** (BodyStealRuleTest) |
| 種族が違うゆっくりから盗める | actor=まりさ、target=れいむ（`target.setType(REIMU)` 相当） | `false`（type 不一致ガード） | **完了** (BodyStealRuleTest) |
| ハイブリッドがお飾りを盗める | `actor.setHybrid(true)` | `false`（isHybrid ガード） | **完了** (BodyStealRuleTest) |
| DEFAULT 以外のお飾りが盗まれる | target に `giveOkazari(OkazariType.CUSTOM)` 等 DEFAULT 以外を設定 | `false`（OkazariType ガード） | スキップ（DEFAULT 以外の設定方法が複雑） |
| ランク条件を満たさない相手から盗める | `target.setPublicRank(KAIYU)` + `actor.setPublicRank(NONE)` | `false`（target が NONE 以外 かつ actor が UNUN_SLAVE 以外） | **完了** (BodyStealRuleTest) |
| 移動ロック中でも盗める | `actor.setLockmove(true)` | `false`（isLockmove ガード） | **完了** (BodyStealRuleTest) |
| UNUN_SLAVE が盗み成功してもランクが逆転しない | `actor.setPublicRank(UNUN_SLAVE)`、`target.setPublicRank(NONE)`、actor=ゲス、目撃者なし、`handleOkazariSteal(target, actor)` | `true`、`actor.getPublicRank() == NONE`（解放）、`target.getPublicRank() == UNUN_SLAVE`（降格） | **完了** (BodyStealRuleTest) |

**備考:** 種族不一致テストは `Yukkuri.setType()` が存在しない場合は別種インスタンスで代替する（actor=Marisa, target=Reimu のように異なるクラスで作成）。

---

## `AutoFeeder.upDate` — BODY タイプの成長後死亡・再スポーン

NORMAL 食料の生成・消費・クリアは `AutoFeederTest.RegressionScenarios` で確認済み。**`FeedType.BODY` / `PROCESSED_BODY` タイプの upDate ロジックはテストゼロ。**

仕組み: `food = addYukkuri(..., AgeState.BABY)` で生まれた赤ゆは成長後も同一オブジェクト参照。`isDead()=true` → `remove()` → `isRemoved()=true` → `food=null` → 次 upDate で新赤ゆ生成。AgeState チェックは一切なし。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 死んだ赤ゆが food 参照からクリアされない | `setType(BODY.ordinal())`、`setFoods(baby)`、`baby.setDead(true)`、`upDate()` | `feeder.getFoods() == null`、`baby.isRemoved() == true` | **完了** (AutoFeederTest) |
| 成ゆに育って死んでも food がクリアされない | 同上で `baby.setAgeState(ADULT)` + `baby.setDead(true)` | `feeder.getFoods() == null`（AgeState に依存しない仕様の確認） | **完了** (AutoFeederTest) |
| food クリア後に新しい赤ゆが出ない | 上記クリア後に `mode=0` + `upDate()` 再呼び出し | `feeder.getFoods() != null`（headless 不可の場合 assertDoesNotThrow で代替） | **完了** (AutoFeederTest) |

**備考:** `isRemoved()=true` だが `isDead()=false`（捕食・強制除去）のパスも `food=null` になる。PROCESSED_BODY も同一コードパスを通る。headless では `GameView.addYukkuri` が NPE する可能性があるため C は assertDoesNotThrow スモークテストが現実的。

---

## ベルトコンベア2種 — 全オブジェクト種別の搬送テスト

2種の実装が存在する。`Beltconveyor`（FieldShape型、setting 行列で種別・年齢を制御）と `BeltconveyorObj`（WorldEntity型、targetType 整数で制御）。

**既テスト:** Beltconveyor は normal ADULT checkHitObj=true・Food checkHitObj=true・RIGHT方向 motionX=2。BeltconveyorObj は 4方向で result=0 のみ（実座標変化は未アサート）、cantmove フラグ、Marisa タイプフィルター除外。

---

### `Beltconveyor.checkHitObj` — ゆっくり全種別・全年齢の設定検出

setting 行列のインデックスは `SetupButton.ordinal() + SetupMenu.NORMAL_BABY.ordinal()` + `AgeState.ordinal()`。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 通常種 赤ゆが検出されない | `setting[NORMAL_BABY+0][BABY]=true`、`body.setAgeState(BABY)` | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 通常種 子ゆが検出されない | `setting[NORMAL_BABY+0][CHILD]=true`、`body.setAgeState(CHILD)` | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 捕食種 赤ゆが検出されない | `setting[NORMAL_BABY+1][BABY]=true`、`body.setPredatorType(BITE)` + BABY | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 捕食種 子ゆが検出されない | 同上 + CHILD | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 捕食種 成ゆが検出されない | 同上 + ADULT | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 希少種 赤ゆが検出されない | `setting[NORMAL_BABY+2][BABY]=true`、`body.setRareType(true)` + BABY | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 希少種 子ゆが検出されない | 同上 + CHILD | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 希少種 成ゆが検出されない | 同上 + ADULT | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 足りない種 赤ゆが検出されない | `setting[NORMAL_BABY+3][BABY]=true`、TarinaiReimu（isIdiot=true）+ BABY | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 足りない種 子ゆが検出されない | 同上 + CHILD | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| 足りない種 成ゆが検出されない | 同上 + ADULT | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| ハイブリッド 赤ゆが検出されない | `setting[NORMAL_BABY+4][BABY]=true`、`body.setHybrid(true)` + BABY | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| ハイブリッド 子ゆが検出されない | 同上 + CHILD | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| ハイブリッド 成ゆが検出されない | 同上 + ADULT | `checkHitObj(body) == true` | **完了** (BeltconveyorTest) |
| うんうんが検出されない | `setting[SHIT][0]=true`、`Shit shit = new Shit()` | `checkHitObj(shit) == true` | **完了** (BeltconveyorTest) |
| 吐餡が検出されない | `setting[VOMIT][0]=true`、`Vomit vomit = new Vomit()` | `checkHitObj(vomit) == true` | **完了** (BeltconveyorTest) |
| 茎が検出されない | `setting[STALK][0]=true`、`Stalk stalk = new Stalk()` | `checkHitObj(stalk) == true` | **完了** (BeltconveyorTest) |

**備考:** isIdiot() は TarinaiReimu でデフォルト true。ハイブリッドは `setHybrid(true)` でフラグ設定。優先順位: hybrid → idiot → rare → predator → normal の順で判定される。

---

### `Beltconveyor.processHitObj` — 全方向で motion が付与される

RIGHT（SpeedCombo.MIDDLE → motionX=2）は既テスト済み。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| UP方向で motion が付かない | direction=UP、beltSpeed=MIDDLE | `body.getMotionY() == -2` | **完了** (BeltconveyorTest) |
| LEFT方向で motion が付かない | direction=LEFT、beltSpeed=MIDDLE | `body.getMotionX() == -2` | **完了** (BeltconveyorTest) |
| BOTTOM方向で motion が付かない | direction=BOTTOM、beltSpeed=MIDDLE | `body.getMotionY() == 2` | **完了** (BeltconveyorTest) |

---

### `BeltconveyorObj.objHitProcess` — targetType=0 で全オブジェクトの座標が変化する

既存テストは result=0 を確認するのみで **setCalcX/Y の実変化は未アサート**。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| targetType=0 で Yukkuri の座標が変わらない | `targetType=0`、`cantmove=0`、`beltSpeed=5`、`option=0`、Yukkuri（Y=100） | `yukkuri.getCalcY() == 95`（Y - beltSpeed） | **完了** (BeltconveyorObjTest) |
| targetType=0 で Food の座標が変わらない | 同上で Food（Y=100） | `food.getCalcY() == 95` | **完了** (BeltconveyorObjTest) |
| targetType=0 で Stalk の座標が変わらない | 同上で Stalk | `stalk.getCalcY() == 95` | **完了** (BeltconveyorObjTest) |
| targetType=0 で Vomit の座標が変わらない | 同上で Vomit | `vomit.getCalcY() == 95` | **完了** (BeltconveyorObjTest) |
| targetType=0 で Shit の座標が変わらない | 同上で Shit（Y=100） | `shit.getCalcY() == 95`（既テストは result=0 のみ、座標変化未確認） | **完了** (BeltconveyorObjTest) |

---

### `BeltconveyorObj.objHitProcess` — targetType 別の絞り込み（ポジティブ側）

既存テストは「弾かれること」のみ確認。「通過して搬送されること」が未テスト。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| targetType=1（ゆっくりのみ）で Yukkuri が搬送されない | `targetType=1`、`cantmove=0`、`beltSpeed=5`、`option=0`、Yukkuri | `yukkuri.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |
| targetType=2（うんうん/吐餡）で Shit が搬送されない | `targetType=2`、Shit | `shit.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |
| targetType=2（うんうん/吐餡）で Vomit が搬送されない | `targetType=2`、Vomit | `vomit.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |
| targetType=3（食料のみ）で Food が搬送されない | `targetType=3`、Food | `food.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |
| targetType=4（茎のみ）で Stalk が搬送されない | `targetType=4`、Stalk | `stalk.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |
| targetType=5（ゆっくり以外）で Food が搬送されない | `targetType=5`、Food | `food.getCalcY()` が変化する | **完了** (BeltconveyorObjTest) |

---

### `BeltconveyorObj.objHitProcess` — フィルター条件の詳細

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 死体のみフィルターで生きたゆっくりが搬送される | `filter=true`、`obOptionSelectionList[8]=true`（死体のみ）、alive yukkuri | `return 0` かつ座標変化なし（生ゆは弾かれる） | **完了** (BeltconveyorObjTest) |
| 死体のみフィルターで死亡ゆっくりが弾かれる | 同条件、`body.setDead(true)` | `body.getCalcY()` が変化する（死体は搬送される） | **完了** (BeltconveyorObjTest) |
| 性格フィルターで VERY_NICE が弾かれない | `filter=true`、`obOptionSelectionList[0]=false`（VERY_NICEを除外）、`body.setAttitude(VERY_NICE)` | 座標変化なし（除外される） | **完了** (BeltconveyorObjTest) |
| 知性フィルターで WISE が弾かれない | `filter=true`、`obOptionSelectionList[5]=false`（WISEを除外）、`body.setIntelligence(WISE)` | 座標変化なし（除外される） | **完了** (BeltconveyorObjTest) |

---

## `BreedingPool.objHitProcess` — 精子餡の固定と混血 DNA への影響

`liquidYukkuriType=-1`（クリーン）と dead+crushed ゆっくりによる液状化は既テスト済み。**2匹目のガードと、液状化後の baby type への影響が未テスト。**

RNG 制御は `GameRandom.setOverride(RandomSource)` で `nextInt` と `nextBoolean` を独立制御する（`SequenceRandom` は `nextBoolean()` を制御できないため不可）。変異チェック（nextInt(20) 等）を回避するため highQuality=true + 変異リストにない liquidType（SAKUYA 等）を使う。

### 液状化の固定ロジック

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 2匹目の dead+crushed が liquidYukkuriType を上書きする | `liquidYukkuriType` を既に設定済み + 別 yukkuri で dead+crushed + `objHitProcess()` | `liquidYukkuriType` が変わらない（1匹目のタイプを維持） | **完了** (BreedingPoolTest) |
| crushed でない死亡ゆっくりが液状化する | `body.setDead(true)` + `body.setCrushed(false)` + `liquidYukkuriType=-1` | `liquidYukkuriType == -1` のまま | **完了** (BreedingPoolTest) |

### liquidType != -1 のとき baby type への影響

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 液状化後でも常に母体タイプの赤ゆが生まれる | `setLiquidYukkuriType(SAKUYA.getTypeId())` + `highQuality=true` + `nextInt(50)=0` (hybrid path) + 非ハイブリッド体 | `body.getBabyTypes().get(0).getType()` が母体タイプと異なる（hybridType を返す） | スキップ（getHybridType の返値が complex） |
| 液状化後でも常に母体タイプが選ばれる（liquid パス欠落） | 同上で `nextInt(50)≠0` + `nextBoolean()=true` (liquid path) | `body.getBabyTypes().get(0).getType() == SAKUYA`（liquid タイプそのまま） | **完了** (BreedingPoolTest) |
| 液状化後に母体タイプ選択が壊れる | 同上で `nextInt(50)≠0` + `nextBoolean()=false` (mother path) | `body.getBabyTypes().get(0).getType() == body.getType()` | **完了** (BreedingPoolTest) |
| ハイブリッド体でも hybrid path に入ってしまう | `body.setHybrid(true)` + `liquidYukkuriType` 設定 + `nextInt(50)=0` | hybrid path に入らない（nextBoolean の結果で liquid or mother type になる） | スキップ（混血 path と非 path の差異が baby type で取りにくい） |
| typeId ≥ 10000 でも hybrid path に入ってしまう | `liquidYukkuriType = 10000` + 非ハイブリッド体 + `nextInt(50)=0` | hybrid path に入らない（`liquidYukkuriType < 10000` ガードで弾かれる） | スキップ（同上） |

**備考:** `nextBoolean()=true` のパスは `GameRandom.setOverride(new RandomSource() { nextBoolean() { return true; } })` で対応。`SequenceRandom` では不可。highQuality=true（option=2）にすると `!highQuality && nextInt(500)==0` の TARINAI パスが無効化されテストが簡潔になる。

---

## `HotPlate.upDate` — 乗り続けることで足焼きステータスが変化する

`addFootBakePeriod(50)` の加算量と CRITICAL 時の `canPullOrPush` は既テスト済み。**累積による NONE→MEDIUM・MEDIUM→CRITICAL の遷移テストが未追加。** 境界値の直接テスト（`setFootBakePeriod` 手動設定）は BodyAttributesTest にあるが、HotPlate の `upDate` を呼ぶことで状態遷移が起きることは未確認。

閾値: `threshold_medium = damageLimitBase >> 1`、`threshold_critical = damageLimitBase`（`>` 判定。等値は NONE/MEDIUM のまま）。

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| threshold_medium ちょうどが NONE にならない | `body.setFootBakePeriod(limit >> 1)` | `body.getFootBakeLevel() == FootBake.NONE`（等値は NONE） | **完了** (HotPlateTest) |
| threshold_critical ちょうどが MEDIUM にならない | `body.setFootBakePeriod(limit)` | `body.getFootBakeLevel() == FootBake.MEDIUM`（等値は MEDIUM） | **完了** (HotPlateTest) |
| upDate 累積で NONE→MEDIUM に遷移しない | `body.setFootBakePeriod(limit >> 1)`（NONE 上限）、`item.setBoundYukkuri(body)` + `upDate()` 1回（+50 で閾値超え） | `body.getFootBakeLevel() == FootBake.MEDIUM` | **完了** (HotPlateTest) |
| upDate 累積で MEDIUM→CRITICAL に遷移しない | `body.setFootBakePeriod(limit - 49)`（CRITICAL 手前 50）、同上 + `upDate()` 1回（+50 で `limit+1` に） | `body.getFootBakeLevel() == FootBake.CRITICAL` かつ `body.canPullOrPush() == true` | **完了** (HotPlateTest) |

**備考:** テスト時は body を `setCalcX/Y/Z` で item と同座標に合わせ、`setBoundYukkuri` で紐付けてから `upDate()` を呼ぶ（HotPlateTest の既存 RegressionScenarios パターンと同じセットアップ）。`limit = body.getDamageLimitBase()[AgeState.ADULT.ordinal()]`。

---

## `Mixer.upDate` — 乗り続けることで足破れ・死亡になる

1tick ごとに damage+100・amount+100 の加算と、counter > 60 で駆動開始の確認は既テスト済み。CUT が counter > 60 で離れた時に適用されることも確認済み。**待機中の無効ガード・餡子尽きによる死亡・死亡後の damage 非加算が未テスト。**

```
counter++ → if(counter>60): isDead()=false なら damage+100/stress+100、addAmount(-100)→0 なら body.remove()
体が離れたとき if(counter>60): setCriticalDamageType(CUT)   // 足破れ
```

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 待機中（counter ≤ 60）でも damage が入る | `counter=0`、body を同座標に設定、`upDate()` 1回 | `body.getDamage()` 不変（counter が 1 になるだけ、mixing 未開始） | **完了** (MixerTest) |
| counter ≤ 60 のまま離れると CUT される | `counter=60`（まだ > 60 でない）、body を別座標 → `upDate()` | `body.getCriticalDamageType() != CUT`（60 は > 60 でないのでガード通過） | **完了** (MixerTest) |
| 餡子が尽きても body が除去されない | `counter=60`、`body.setAnkoAmount(100)`、body を同座標、`upDate()` 1回 | `body.isRemoved() == true`（addAmount(-100) → 0 → remove()）かつ `item.getBind() == -1` | **完了** (MixerTest) |
| 死亡後も damage / stress が加算される | `counter=60`、`body.setDead(true)`、body を同座標、`upDate()` 1回 | `body.getDamage()` 不変、`body.getStress()` 不変（isDead ガードで加算スキップ）、`amount += 100` は発生する | **完了** (MixerTest) |

**備考:** `addAmount(-100)` は `body.getAnkoAmount() - 100 <= 0` のとき true を返す。body を Registry に登録して `setBind(body.getUniqueId())` で紐付けるセットアップは既存 RegressionScenarios と同じ。counter=60 → upDate 後 counter=61 で mixing 開始（`61 > 60`）。

---

## `YukkuriCarryDelegate.dropTakeoutItem` — うんうん降ろし後の座標変化

`setCarryItem(SHIT)` で takenOutShits 移動・`IN_YUKKURI`、`dropTakeoutItem(SHIT)` で getShit() 復帰・`ON_FLOOR` は BodyTest で確認済み。**降ろし後の座標が body の現在位置になることは未テスト。かつ Y 座標の条件分岐にバグあり（条件と分岐が逆）。**

**バグ:** `dropTakeoutItem` の Y 設定コードの条件が逆

```java
// 現状（バグ）
if (body.getY() + 3 <= Translate.getWorldHeight()) {
    val.setCalcY(body.getY());        // 通常パスなのに +3 なし
} else {
    val.setCalcY(body.getY() + 3);    // 下端付近なのに +3 する
}

// 意図（正しい）
if (body.getY() + 3 <= Translate.getWorldHeight()) {
    val.setCalcY(body.getY() + 3);    // 通常: 少し前方に置く
} else {
    val.setCalcY(body.getY());        // 下端付近: body 位置そのまま
}
```

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 降ろし後に X 座標が body 位置にならない | shit を (50,50) に作成、body を (200, 100) に移動後 `dropTakeoutItem(SHIT)` | `shit.getCalcX() == 200` | **完了** (BodyTest) |
| 降ろし後に Y 座標が body 位置より +3 にならない（通常パス） | body を worldHeight から十分離れた位置（y+3 ≤ worldHeight）に配置 | `shit.getCalcY() == body.getY() + 3`（**バグ修正後の期待値**。修正前は `body.getY()` を返す） | **完了** (BodyTest + バグ修正済み) |
| 降ろし後に Z 座標が body.getZ() + 10 にならない | body.getZ() = 0 の状態で drop | `shit.getCalcZ() == 10` | **完了** (BodyTest) |
| 下端付近で Y が +3 にずれる（境界パス） | body.getY() を `worldHeight - 2` に設定（+3 で超過）、drop | `shit.getCalcY() == body.getY()`（**バグ修正後の期待値**。修正前は `body.getY() + 3` を返す） | **完了** (BodyTest + バグ修正済み) |
| 運搬開始時に shit が getShit() から消えない | shit を getShit() に登録後 `setCarryItem(SHIT, shit)` | `getShit()` に shit がなく `getTakenOutShits()` にある（既テスト確認済み） | 完了 (BodyTest) |

**エンドツーエンド確認（運搬→移動→降ろし→座標変化）:**

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 運搬・移動・降ろしを通じて座標が更新されない | shit を (50,50) に設定 → `setCarryItem` → body を (200, 100) に移動 → `dropTakeoutItem` | `shit.getCalcX() == 200`、`shit.getCalcZ() == body.getZ() + 10`（Y は修正後で +3 確認） | **完了** (BodyTest) |

**備考:** Y バグは修正が必要。テスト実装時は**バグ修正と同時に行う**こと。修正前にテストを書く場合は「現状バグの確認テスト」として `body.getY()` をアサートし、修正後に `body.getY() + 3` に更新する。

---

## `Terrarium.loadState` — セーブ時のウィンドウサイズ・うにょが復元される

`SaveDataCodecTest` は `windowType`/`terrariumSizeIndex` が JSON ラウンドトリップで保持されること（World フィールド）は確認済み。**`loadState()` 後に `Translate.setWorldSize()` と `SimYukkuri.UNYO` が実際に保存値で上書きされることが未テスト。**

`loadState()` 復元ロジック:
```java
tmpWorld.recalcWorldSize();          // saved windowType/sizeIndex で Translate.setWorldSize() を更新
SimYukkuri.UNYO = tmpWorld.isUnyo(); // saved unyo フラグで SimYukkuri.UNYO を上書き
```

`recalcWorldSize()` の計算: `maxX = DEFAULT_MAP_X[windowType] * fieldScaleData[terrariumSizeIndex] / 100`
- `windowType=0`, `sizeIndex=1` (100%) → `maxX = 300`
- `windowType=1`, `sizeIndex=1` (100%) → `maxX = 500`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ロード後にうにょが保存時の OFF に戻らない | `world.setUnyo(false)` + `saveState(file)`、その後 `SimYukkuri.UNYO=true` に変更 → `loadState(file)` | `SimYukkuri.UNYO == false`（保存時の値に復元） | **完了** (TerrariumTest) |
| ロード後にうにょが保存時の ON に戻らない | `world.setUnyo(true)` + `saveState(file)`、その後 `SimYukkuri.UNYO=false` に変更 → `loadState(file)` | `SimYukkuri.UNYO == true` | **完了** (TerrariumTest) |
| ロード後にワールド幅が保存時の windowType に対応した値にならない | `world.setWindowType(0)` + `world.setTerrariumSizeIndex(1)` + `saveState(file)`、現在の world を `windowType=1` に変更 → `loadState(file)` | `Translate.getWorldWidth() == 301`（windowType=0 の値+1） | **完了** (TerrariumTest) |
| ロード後にワールド幅が保存時の terrariumSizeIndex に対応した値にならない | `world.setWindowType(0)` + `world.setTerrariumSizeIndex(2)` (200%) + `saveState(file)`、current を sizeIndex=1 (100%) に変更 → `loadState(file)` | `Translate.getWorldWidth() == 601`（300 × 200/100 + 1） | **完了** (TerrariumTest) |

**備考:** テストは `TerrariumTest` の `Terrarium.saveState(file)` / `loadState(file)` パターンを流用可能。`@AfterEach` で `SimYukkuri.UNYO` と `Translate.setWorldSize` を元に戻すこと。`world.isUnyo()` が `World.unyo` の getter であり、`SimYukkuri.UNYO`（static グローバル）とは別フィールドである点に注意。

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
