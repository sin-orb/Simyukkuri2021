# TEST_EXPANTION_PLAN

「このロジックが壊れたときにこのテストが止める」を軸にした拡充設計・進捗管理。  
カバレッジ数値は補助情報（詳細は COVERAGE_STATUS.md 参照）。

ステータス: `未着手` / `対応中` / `完了` / `スキップ`（headless 不可など）

---

## `FoodLogic.checkFood` / サブポリシー群

`checkFood` は FoodActionGate → 到着判定 → 各サーチポリシー → FoodFoundReaction という構造に分離されている。

### FoodActionGate.shouldSkipBeforeSearch

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NYD 状態なのに食べ物サーチを開始してしまう | `body.setNYD(true)` | `checkFood(body)` → `false` | 完了 (testCheckFood_NonYukkuriDiseaseState) |
| 死亡済みなのにサーチが走る | `body.setDead(true)` | `checkFood(body)` → `false` | 完了 (testCheckFood_Dead_ReturnsFalse) |
| canAction=false のときスキップしない | `body.setPacked(true)` (canAction=false) + isVeryHungry=false | `checkFood(body)` → `false` | 完了 (testCheckFood_CanActionFalse_NotVeryHungry_ReturnsFalse) |
| forceEat フラグが立っているのに通常スキップされる | SuperEatingTimeEvent(State.START) を currentEvent にセット | `checkFood(body)` → `true` かつ `body.isToFood()` | 完了 (testCheckFood_ForceEat_SuperEatingTimeEventStart_BypassesRandomSkip) |

### Stalk 所有者ガード（B1 分岐）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 自分が植わっている茎を自分で食べようとする | `stalk.setPlantYukkuri(body.getUniqueId())` → `body.setMoveTargetId(stalk)` → `body.setToFood(true)` | `checkFood(body)` → `false`、`body.isToFood()` == false | 完了 (testCheckFood_SelfPlantedStalk_SkipsAndClearsTarget) |
| 他人の茎でも非埋没なら食べようとしてしまう | 他のゆっくりを `stalk.setPlantYukkuri(other)` で植え、`other.setBurialState(NONE)` | `checkFood(body)` → `false` | 完了 (testCheckFood_OtherPlantedStalk_NonBuried_ReturnsFalse) |
| 他人の茎が完全埋没なら食べようとしない | `other.setBurialState(ALL)` | `checkFood(body)` → 到着処理へ（`isToFood()` を保持） | 完了 (testCheckFood_OtherPlantedStalk_FullyBuried_DoesNotClearTarget) |

### 捕食種 vs 通常種 サーチ分岐

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 捕食種が通常種サーチに入り、他ゆっくりを食べようとしない | `body.setPredatorType(PredatorType.BITE)` | `checkFood(body)` → `true`、移動先が Yukkuri 系ターゲット | 完了 (testCheckFood_PredatorType_WithPrey_ReturnsTrue) |
| 通常種が捕食種サーチに入り、ゆっくりを食べようとする | `body.setPredatorType(null)` | `checkFood(body)` が通常食料 Food を選ぶ | 完了 (testCheckFood_Hungry_FoundFood で既にカバー) |
| 足焼き CRITICAL の非飛行種が nearestSearch に入らない | `body.setFootBakePeriod(damageLimitBase+1)` + `body.setFlyingType(false)` | 最寄り食料を選択することを確認 | 完了 (testCheckFood_FootBakeCritical_NonFlying_UsesNearestSearch / FindsNearestFood) |

### UNUN_SLAVE 専用サーチ

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| UNUN_SLAVE が通常食料サーチに入り、勝手に食べる | `body.setPublicRank(PublicRank.UNUN_SLAVE)` | `checkFood(body)` が FoodUnunSlaveSearchPolicy を経由する（親が許可した food のみ選ぶ） | 完了 (testCheckFood_UnunSlave_WithNormalFood_ReturnsFalse) |

### FoodArrivalActionPolicy（到着時処理）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 到着した食べ物が途中で消されても食べようとする | `food.setRemoved(true)` 後に `body.setToFood(true)` + `body.setMoveTargetId(food)` | `checkFood(body)` → `false`、`body.isToFood()` == false | 完了 (testCheckFood_FoodTargetRemoved_ReturnsFalse) |
| 空中に浮いた食べ物（Z≠0）を飛行できない種が食べようとする | `food.setZ(10)`、`body.setFlyingType(false)` | `checkFood(body)` → `false` | 完了 (testCheckFood_AirborneFood_NonFlying_ReturnsFalse) |

### FoodFoundReaction（発見時処理）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 食べ物を見つけたのにターゲット設定されない | 食料を eyesight 範囲内に置く | `checkFood(body)` → `true`、`body.getMoveTargetId()` == food.getObjId() | 完了 (testCheckFood_Hungry_FoundFood) |
| forceEat ルートで isToTakeout / isToFood が逆になる | forceEat=true のルートを踏む | `body.isToFood()` == true または `body.isToTakeout()` == true（状況による） | スキップ（FoodArrivalActionPolicy.handleArrivedFood の内部ルートで takeout/food の区別が難しい。次フェーズで対処） |

---

## `YukkuriLogic.checkPartner` / サブルール群

### YukkuriPartnerEntryRule.shouldSkipPartnerAction

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| NYD 状態でもパートナー行動が走る | `body.setNYD(true)` | `checkPartner(body)` → `false` | 完了 (testCheckPartner_IsNYDReturnsFalse) |
| 死亡済みでもループに入る | `body.setDead(true)` | `checkPartner(body)` → `false` | 完了 (testCheckPartner_Dead_NoTarget_ReturnsFalse) |
| バリー中（BurialState.ALL）でもパートナー行動 | `body.setBurialState(BurialState.ALL)` | `checkPartner(body)` → `false` | 完了 (testCheckPartner_BurialStateAll_Self_ReturnsFalse) |
| isExciting=false なのにレイパーとして動く | `target.setExciting(false)`、`target.setRaper(true)` + rank 不一致 | `doActionOther` → false（rank 不一致で通過できない） | 完了 (testDoActionOther_NotExciting_Raper_RankMismatch_ReturnsFalse) |

### resolveMappedTarget（既定ターゲット優先）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ターゲット登録済みなのに再探索してしまう | `body.setMoveTargetId(you.getUniqueId())`、you を registry に登録 | `checkPartner(body)` → `doActionOther(you, body)` を呼ぶ | 完了 (testCheckPartner_ToBodyWithTarget_CallsDoActionOther_ReturnsTrue) |

### パートナー優先（getPartnerIfPreferred）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| つがい設定済みなのに別個体を選ぶ | `body.setPartner(you)` + `you.setPartner(body)` | `checkPartner(body)` → you に向かう（`body.getMoveTargetId()` == you.getUniqueId()） | 完了 (testCheckPartner_ExistingPartner_GoToPartner_L180) |

### shouldGoToParent（泣き叫び中の親捜し）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 泣き叫んでいる子ゆっくりがパートナー行動に入る | shouldGoToParent=true の条件を再現 | `checkPartner(body)` → `false` | 完了 (testCheckPartner_isCallingParents_returnsFalse) |

### YukkuriPartnerSearchRule.selectTargets（ターゲット探索）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| okazari 持ちが okazari 候補として記録されない | `you.setOkazari(new Okazari())` + 視野内に配置 | `checkPartner(body)` 後に `body.getOkazariCandidateId()` == you.getUniqueId() | 完了 (testCheckPartner_OkazariPheromone_L335) |
| 捕食者が通常パートナー探索でターゲット候補になる | predator を視野内に配置 | body がパートナーとして predator を選ばない | 完了 (testCheckPartner_PredatorCausesPanic_L237) |
| rank 不一致の相手に近づく | me.rank != you.rank | `doActionOther` が早期 continue する（接触なし） | 完了 (testDoActionOther_RankMismatch_NoSteal_ReturnsFalse_L641) |

### handleNoFoundTarget（見つからなかった時）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ターゲットがいないのにオナニズムが発動しない | `SimYukkuri.RND = new ConstState(0)` + 周囲にゆっくりなし | `body.getEvents()` にオナニズムイベントが追加される | 完了 (testCheckPartner_NoTarget_ExcitingOnanism_L352) |

---

## `Yunba.clockTick`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| grabbed 中に action が設定されてしまう | `yunba.setGrabbed(true)` | `clockTick()` → NONE、`yunba.getAction()` == null | 完了 (testClockTick_Grabbed_ClearsAction) |
| Z>0 のとき落下せず空中停止する | `yunba.setZ(20)` | `clockTick()` 後に `yunba.getZ()` == 15（5下がる） | 完了 (testClockTick_ZAboveZero_DecreasesZ) |
| Z>0 のとき action が null にリセットされない | `yunba.setAction(Action.SHIT)`、`yunba.setZ(10)` | `clockTick()` 後 `yunba.getAction()` == null | 完了 (testClockTick_ZAboveZero_ClearsAction) |
| トイレ上のしぶきを掃除ターゲットに設定してしまう | Shit を Toilet の内側に配置（checkHitObj=true） | `clockTick()` 後 `yunba.getAction()` != Action.SHIT（スキップ） | スキップ（headless環境でTranslate座標変換の不一致により checkHitObj が機能しない） |
| 植わっているゆっくりがいる Stalk を掃除しようとする | `stalk.setPlantYukkuri(someBody.getUniqueId())` | `clockTick()` 後 `yunba.getAction()` != Action.STALK | 完了 (testClockTick_StalkWithPlantedYukkuri_SkipsStalk) |
| FOOD タイプが STALK でない Food を stalk 掃除として選ぶ | `food.setFoodType(FoodType.SWEETS1)` | `yunba.getAction()` != Action.STALK | 完了 (testClockTick_StalkCheck_NonStalkFood_Skipped) |
| 他の Yunba のターゲットを横取りする | 別の Yunba が同じ Shit を target にしている | `yunba.getAction()` != Action.SHIT（cheackOtherYunbaTarget でスキップ） | 完了 (testCheackOtherYunbaTarget_OtherYunbaHasTarget_ReturnsFalse) |

---

## `Pool.objHitProcess`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 空中（Z>0）の個体がプールに影響を受ける | `body.setZ(5)` | `objHitProcess(body)` → 0 返却、`body.isWet()` == false | 完了 (testObjHitProcess_AirborneObj_ReturnsZero) |
| EDGE エリアに入った個体がずぶ濡れになる | EDGE 深さになる座標に body を配置 | `body.isInPool()` == true、`body.isWet()` == false | 完了 (testObjHitProcess_EdgeArea_BodyNotWet) |
| SHALLOW で水好きの個体がダメージを受ける | `body.setLikeWater(true)` + SHALLOW エリア | `body.getDamage()` の増加なし | 完了 (testObjHitProcess_ShallowArea_LikesWater_NoDamage) |
| DEEP で非水好き・深く沈んだ個体がロック移動にならない | `body.setZ(-4)`（depthLimit=-3 超え）、`body.setLikeWater(false)` | `body.isLockmove()` == true | 完了 (testObjHitProcess_DeepArea_NonLikeWater_DeepEnough_LockMove) |
| 溶けている個体の沈没確率が下がらない | `body.setMelt(true)` | deepWaterChance が半減し沈没が起きやすくなる（統計的確認または RNG 固定） | 完了 (testObjHitProcess_DeepArea_MeltBody_SinksWhenRngZero + NonMeltBody_DoesNotSinkOnSeed25) — ConstState(0) では melt/non-melt 両方沈むため完全な区別には不十分だが、「melt=false + SequenceRandom([25]) で沈まない」でレグレッション網羅 |
| BABY がダメージ閾値で正しく判定されない | `body.setAgeState(AgeState.BABY)` + depthLimit=1 超えの Z | 対応するダメージ分岐が発動する | 完了 (testObjHitProcess_DeepArea_Baby_ShallowZNotLocked / DeepZLocked) |

---

## `SuperEatingTimeEvent.update`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| from（親ゆ）が消えてもイベントを続ける | `from.setRemoved(true)` | `update(child)` → ABORT | 完了 (testUpdate_fromRemoved_returnsAbort) |
| target（食料）が消えても親が悲しみにならない | `target.setRemoved(true)` | `from.getHappiness()` == VERY_SAD、`update()` → ABORT | 完了 (testUpdate_targetFoodRemoved_fromBecomesVerySad) |
| NYD 中の子ゆにイベントが継続実行される | `child.setNYD(true)` | `update(child)` → ABORT | 完了 (testUpdate_bodyNYD_returnsAbort) |
| 産気づいた子ゆへのイベントが止まらない | `child.setNearToBirth(true)` | `update(child)` → ABORT | 完了 (testUpdate_nearToBirth_returnsAbort) |
| 空腹の子ゆの hunger が 60% に補正されない | `child.setHungry(0)` | `update(child)` 後 `child.getHungry()` == `child.getHungryLimit() * 6 / 10` | 完了 (testUpdate_childHungry_hungrySetTo60Percent) |
| 子ゆが全員いなくなっても親がイベントを続ける | `createActiveChildren` が空リストを返す状況 | `update(from)` → ABORT | 完了 (testUpdate_from_noChildren_returnsAbort) |
| tick % 20 のスキップが機能しない | tick=1 の状態でコール | `update()` → null（継続待機） | 完了 (testUpdate_tick1_skipsExecution_returnsNull) |

---

## `ProposeEvent` / `FuneralEvent` / `ShitExercisesEvent`

### ProposeEvent

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 相手が拒否しているのに求愛が成功する | `you.setPartner(-1)` かつ rank 不一致 | update が求愛成功状態にならない | 完了 (testCheckEventResponse_Stranger + testCheckEventResponse_Participants でカバー) |
| 相手が既にパートナー持ちなのに上書きする | `you.setPartner(other.id)` | `you.getPartner()` が変わらない | 完了 (testAcceptPropose_AlreadyMarried) |

### FuneralEvent

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| パートナー死亡時に悲しみ反応が発動しない | `from.setDead(true)` + partner 関係設定 | `body.getHappiness()` == VERY_SAD | 完了 (testDoActionOther_AdultDeadPartner_SadnessForPartner_L687 でdoActionOther側をカバー) |
| 子供の死亡時に泣き反応がない | child との親子設定後 child.setDead(true) | 泣きイベントが body に追加される | 完了 (testDoActionOther_ChildDeadParent_SadnessForParent_L699 でカバー) |

### ShitExercisesEvent

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 排泄練習が完了しても子ゆの排泄欲が解消されない | child の shit を満たした状態で execute | child の shit が 0 に近づく or 排泄イベント終了 | 完了 (testScenario_ChildUnunSuccessSetsDoShitAndClearsShitGauge でカバー) |

---

## `YukkuriLogic.doActionOther` （残り未カバー分岐）

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| rank 不一致でも接触行動してしまう | me.rank != target.rank、isToSteal=false | `doActionOther` → false（接触なし） | 完了 (testDoActionOther_RankMismatch_NoSteal_ReturnsFalse_L641) |
| rank 不一致でも steal フラグで接触できてしまう | isToSteal=true | `doActionOther` → steal ルートに入る | 完了 (testDoActionOther_RankMismatch_WithSteal_ContinuesToContact_L642) |
| 死亡した exciting なレイパーがオナニズムをしない | `target.setDead(true)` + `me.isExciting()=true` + `me.isRaper()=true` | `doActionOther` 後に me に onanism イベントが追加 | 完了 (testDoActionOther_DeadExcitingRaper_DoRape_L663) |
| exciting+非レイパーで死亡対象に対してオナニズムしない | `target.setDead(true)` + `me.isExciting()=true` + `me.isRaper()=false` | onanism イベントが me に追加 | 完了 (testDoActionOther_DeadExcitingNotRaper_DoOnanism_L668) |
| パートナー死亡時に悲しみ反応が起動しない | `target.setDead(true)` + me-target がパートナー関係 | me.getHappiness() == VERY_SAD | 完了 (testDoActionOther_AdultDeadPartner_SadnessForPartner_L687) |
| カビイベント回避が発動しない | `target.isSick()=true` | me の AvoidMoldEvent が追加される | 完了 (testDoActionOther_FindSick_AvoidMoldEvent_L818) |
| 親が汚れた子供を舐めない | child.isDirty()=true + me が親 | me に舐めるイベント追加 | 完了 (testDoActionOther_MotherLicksDirtyChild) |

---

---

## `FoodArrivalActionPolicy.handleArrivedFood`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 空の食料到着時に clearActions せず食べようとする | `food.setAmount(0)` (isEmpty=true) + `body.setToFood(true)` | `handleArrivedFood` → `false`、`body.isToFood()` == false | 完了 (testHandleArrivedFood_EmptyFood_ClearsActionsReturnsFalse) |
| SWEETS1 を食べても hungry が増加しない | `!body.isToTakeout()` + SWEETS1 food → `eatFood` | `body.getHungry()` が増加している | 完了 (testHandleArrivedFood_Sweets1_IncreasesHungry) |
| isToTakeout=true + !isVeryHungry でも食べてしまう | `body.setToTakeout(true)`、hungry 中程度 | `body.getCarryItem(FOOD)` == food（持ち帰りモードになる） | 完了 (testHandleArrivedFood_ToTakeout_NotVeryHungry_CarriesFood) |
| isToTakeout=true でも isVeryHungry なら食べる | `body.setToTakeout(true)`、hungry=0 (isVeryHungry) | `body.getHungry()` > 0（食べた） | 完了 (testHandleArrivedFood_ToTakeout_VeryHungry_EatsInstead) |
| 捕食種が Yukkuri に到達しても bodyInjure が呼ばれない | prey + `body.isPredatorType(BITE)` + `!isPredatorSteam` | `prey.getCriticalDamege() != null` を確認 | 完了 (testHandleArrivedFood_PredatorType_PreyCriticalDamageSet) |

---

## `BeltconveyorObj.objHitProcess`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| enabled=false でも搬送してしまう | `item.setEnabled(false)` | `objHitProcess(obj)` → 0 | スキップ（BeltconveyorObj.enabled は描画制御のみ。objHitProcess にガードなし） |
| targetType=1（Yukkuri のみ）で Shit が通過してしまう | `item.setTargetType(1)` + Shit を渡す | `objHitProcess(shit)` → 0 | 完了 (testObjHitProcess_TargetType1_NonBody_ReturnsZero) |
| targetType=2（Shit/Vomit のみ）で Yukkuri が通過してしまう | `item.setTargetType(2)` + Yukkuri を渡す | `objHitProcess(body)` → 0 | 完了 (testObjHitProcess_TargetType2_Body_ReturnsZero) |
| targetType=3（Food のみ）で Yukkuri が通過してしまう | `item.setTargetType(3)` + Yukkuri を渡す | `objHitProcess(body)` → 0 | 完了 (testObjHitProcess_TargetType3_Body_ReturnsZero) |

---

## `BreedingPool.objHitProcess`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| enabled=false でも繁殖させてしまう | `pool.setEnabled(false)` | `objHitProcess(body)` → 0 | 完了 (testObjHitProcess_Disabled) |
| isCastrated=true の個体が繁殖プールで妊娠する | `body.setCastrated(true)` + `!stalkPool` | `objHitProcess` → 0（妊娠しない） | 完了 (testObjHitProcess_BodyCastration_ReturnsZero) |
| 通常ゆっくりがプール内で妊娠しない | `!isDead`、age%10==0 | `objHitProcess` 後 `body.isHasBaby()` == true | 完了 (testObjHitProcess_AlivePools_AddsBaby) |
| stalkPool=true で茎が実らない | `pool.setStalkPool(true)` + ADULT body | `body.isHasStalk()` == true | 完了 (BreedingPoolTest の stalkPool シナリオでカバー) |

---

## `YukkuriPartnerActionRule.handleFoundTarget`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| exciting=true + raper=true でもすっきりに向かわない | `body.setExciting(true)` + `body.setRaper(true)` | `body.isToSukkiri()` == true | 完了 (testCheckPartner_ExcitingRaper_MoveToSukkiri_L387 等でカバー) |
| exciting=true + !raper でも ProposeEvent が追加されない | `body.setExciting(true)` + `body.setRaper(false)` + 独身 | `body.getEvents()` に ProposeEvent が追加される | 完了 (testDoActionOther_PartnerPropose でカバー) |
| isRude + !targetHasOkazari でも HateNoOkazariEvent が追加されない | `body.setAttitude(SHITHEAD)` + `ConstState(0)` で RND=0 | `world.getEvents()` に HateNoOkazariEvent が追加される | 完了 (testCheckPartner_HateNoOkazari_L421 でカバー) |

---

## `FoodNearestSearchPolicy.searchFoodNearest`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| isFull=true でも食料を探す | `body.setHungry(body.getHungryLimit())` (isFull=true) | `searchFoodNearest` → null | 完了 (testSearchFoodNearest_FullBody_ReturnsNull) |
| 視野外の食料を選んでしまう | eyesight=0 + 食料あり | `searchFoodNearest` → null（視野外） | 完了 (testSearchFoodNearest_EyesightZero_ReturnsNull) |
| 視野内の食料を見つけない | eyesight 範囲内に食料 | `searchFoodNearest` → 食料を返す | 完了 (testCheckFood_FootBakeCritical_NonFlying_FindsNearestFood で間接確認) |

---

---

## `FoodArrivalActionPolicy.handleArrivedFood` — Stalk/UNUN_SLAVE 給餌分岐

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| Z=0 の茎に到達しても hungry が増えない | `stalk.getZ()==0`, plantBody=null | `body.getHungry() > 0` | 完了 (testHandleArrivedFood_Stalk_NoPlantBody_EatsStalk) |
| 完全埋没したゆっくりの茎を食べて VERY_HAPPY にならない | `plantBody.setBurialState(ALL)` | `body.getHappiness() == VERY_HAPPY` | 完了 (testHandleArrivedFood_Stalk_WithPlantBodyBurialAll_VeryHappy) |
| 非埋没の茎に到達して誤って VERY_HAPPY になる | `plantBody.setBurialState(NONE)` | `body.getHappiness() != VERY_HAPPY` | 完了 (testHandleArrivedFood_Stalk_WithPlantBodyBurialNone_NotVeryHappy) |
| UNUN_SLAVE がうんうん運搬時に stress が増えない | `body.setPublicRank(UNUN_SLAVE)` + `isToTakeout=true` + Shit | `body.getStress() == before + 20` | 完了 (testHandleArrivedFood_UnunSlave_ToTakeout_Shit_StressIncreases) |
| 通常ゆっくりに UNUN_SLAVE 専用 stress が乗る | 通常 + `isToTakeout=true` + Shit | `body.getStress() == before`（増加なし） | 完了 (testHandleArrivedFood_NonUnunSlave_ToTakeout_Shit_NoExtraStress) |

---

## `YukkuriGatheringRule` (集合行動)

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 対象が null/空 のときクラッシュしない | null/[]に渡す | false を返す | 完了 (BodyLogicTest 既存テスト群) |
| DOWN 以外の方向で集合できない | LEFT/RIGHT/UP direction | assertDoesNotThrow | 完了 (testGatheringYukkuriSquare_DirectionLeft/Right/Up_DoesNotThrow) |
| 対象が目標位置から遠いのに success=true になる | body が target から離れた位置 | `false` | 完了 (testGatheringYukkuriSquare_SingleBodyFarFromTarget_ReturnsFalse) |
| 3体 (odd layout) で例外が起きる | 3体 array | assertDoesNotThrow | 完了 (testGatheringYukkuriSquare_MultiBody_OddLayout_DoesNotThrow) |
| gatheringYukkuriBackLine で遠い体が success=true になる | body が leader から遠い | `false` | 完了 (testGatheringYukkuriBackLine_SingleBodyFar_ReturnsFalse) |

---

## `FoodMaker.objHitProcess` (食料生成の各タイプ別レシピ)

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| ありすが通常種として処理され誤ったレシピになる | Alice BABY (crushed) を第1原料 | `foodAmount==1`, removed, stockFood==-1 | 完了 (testObjHitProcess_stockNegative_AliceBody_Baby_ProcessedAsIngredient) |
| ちぇんが通常種として処理され誤ったレシピになる | Chen BABY (crushed) を第1原料 | `foodAmount==1`, removed, stockFood==-1 | 完了 (testObjHitProcess_stockNegative_ChenBody_Baby_ProcessedAsIngredient) |
| ありすストック + えさ:ふつう → FOOD ではなく VIYUGRA が出ない | `setStockFood(2)` + `FoodType.FOOD` | VIYUGRA が生産される (makeTable[2][5]=5) | 完了 (testObjHitProcess_AliceStock_FoodInput_ProducesViyugra) |
| ちぇんストック + えさ:ふつう → FOOD ではなく BITTER が出ない | `setStockFood(3)` + `FoodType.FOOD` | BITTER が生産される (makeTable[3][5]=2) | 完了 (testObjHitProcess_ChenStock_FoodInput_ProducesBitter) |

---

## `YukkuriFoundAffinityRule.handleFoundAffinity`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| rank 不一致でも近寄ってしまう | `actor.rank != target.rank` | `false` | 完了 (testHandleFoundAffinity_RankMismatch_ReturnsFalse) |
| 針刺しゆっくりが無視される | `target.setNeedled(true)` | `true` | 完了 (testHandleFoundAffinity_TargetNeedled_SameRank_ReturnsTrue) |
| 針刺しつがいが RNG==0 で接近しない | needled + partner + RNG=0 | `true` & moveTargetId==target | 完了 (testHandleFoundAffinity_TargetNeedled_IsPartner_RngZero_MovesToTarget) |
| 針刺し親子が RNG==0 で接近しない | needled + 親子 + RNG=0 | `true` & moveTargetId==target | 完了 (testHandleFoundAffinity_TargetNeedled_IsParentChild_RngZero_MovesToTarget) |
| 無関係+全RNG!=0 で true を返す | 無関係 RNG=1 | `false` | 完了 (testHandleFoundAffinity_NotNeedled_NoRelation_AllRngNonZero_ReturnsFalse) |
| partner が RNG==0 で接近しない | partner + RNG=0 | moveTargetId==target | 完了 (testHandleFoundAffinity_NotNeedled_IsPartner_RngZero_MovesToPartner) |
| 子が RNG==0 で親のところへ向かわない | child+parent RNG=0 | moveTargetId==target | 完了 (testHandleFoundAffinity_NotNeedled_IsChild_RngZero_MovesToParent) |
| 姉妹が RNG==0 で向かわない | 同父 child×2 RNG=0 | moveTargetId==target | 完了 (testHandleFoundAffinity_NotNeedled_IsSister_RngZero_MovesToSister) |
| 親が RNG==0 で子のところへ向かわない | adult+child RNG=0 | moveTargetId==target | 完了 (testHandleFoundAffinity_NotNeedled_AdultIsFamily_RngZero_MovesToChild) |
| FOOL+!okazari でも子に近づく | FOOL + !hasOkazari + child + nextBoolean=true | `true` (近づかない分岐) | 完了 (testHandleFoundAffinity_NotNeedled_IdiotNoOkazari_ChildRelation_ReturnsTrue) |

---

## `YukkuriSkinshipRule.handleSkinship`

| 壊れると | テスト条件 | assert | ステータス |
|---|---|---|---|
| 条件なしでも true を返す | 無関係ゆっくり | `false` | 完了 (testHandleSkinship_NoConditionMatches_ReturnsFalse) |
| アリに食われた相手を放置する | `target.addAttachment(new Ants())` | `true` & actor.isPeropero() | 完了 (testHandleSkinship_TargetHasAnts_ActorNoAnts_DoesPeroperoReturnsTrue) |
| 自分もアリに食われていても peropero する | both have Ants | `true` & !actor.isPeropero() | 完了 (testHandleSkinship_BothHaveAnts_ClearsActionsNoPeroperoReturnsTrue) |
| 空腹な子への食料吐き出しが起きない | parent+veryHungryChild+food | carryItem==null | 完了 (testHandleSkinship_ActorIsParent_TargetVeryHungry_ActorHasFood_DropsFood) |
| 親子スキンシップが発動しない | adult actor + child target | `true` | 完了 (testHandleSkinship_AdultActor_ChildTarget_ParentRelation_ReturnsTrueWithSkinship) |
| 母+汚れた子でぺろぺろが起きない | mother+dirty child | `true` & actor.isPeropero() | 完了 (testHandleSkinship_AdultActor_ChildTarget_MotherRelation_DirtyChildPeropero) |
| つがいへのすりすりが起きない | partner + nextBoolean=true | `true` | 完了 (testHandleSkinship_Partner_ReturnsTrueWithSurisuri) |
| 子→親のスキンシップが起きない | child actor + parent target | `true` | 完了 (testHandleSkinship_ChildActor_ParentTarget_ReturnsTrueWithSkinship) |
| 姉妹スキンシップが起きない | sister + nextBoolean=true | `true` | 完了 (testHandleSkinship_ChildActor_SisterTarget_ReturnsTrueWithSkinship) |
