package src.event.impl;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.World;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.event.EventPacket.EventPriority;
import src.system.Sprite;
import src.util.WorldTestHelper;

public class SuiSpeakeTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        SuiSpeake event = new SuiSpeake();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        SuiSpeake event = new SuiSpeake(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysReturnsFalse() {
        Yukkuri b = createBody();
        SuiSpeake event = new SuiSpeake();
        assertFalse(event.checkEventResponse(b));

        Yukkuri from = createBody();
        SuiSpeake event2 = new SuiSpeake(from, null, null, 1);
        assertFalse(event2.checkEventResponse(b));
        assertFalse(event2.checkEventResponse(from));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsTrue() {
        Yukkuri b = createBody();
        SuiSpeake event = new SuiSpeake();
        assertTrue(event.execute(b));
    }

    // --- simpleEventAction: currentEvent != null ---

    @Test
    public void testSimpleEventAction_WithCurrentEvent() {
        Yukkuri b = createBody();
        // setCurrentEvent(EventPacket) で currentEvent を非 null にする
        b.setCurrentEvent(new SuiSpeake());
        SuiSpeake event = new SuiSpeake();
        // b.getCurrentEvent() != null により即 true を返す
        assertTrue(event.simpleEventAction(b));
    }

    // --- simpleEventAction: isTalking() == true ---

    @Test
    public void testSimpleEventAction_Talking() {
        Yukkuri b = createBody();
        // isTalking() は messageTicks > 0 で true になる
        b.setMessageTicks(1);
        SuiSpeake event = new SuiSpeake();
        assertTrue(event.simpleEventAction(b));
    }

    // --- start: empty メソッドなので例外なし ---

    @Test
    public void testStart_DoesNotThrow() {
        Yukkuri b = createBody();
        SuiSpeake event = new SuiSpeake();
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysFalse() {
        Yukkuri b = createBody();
        SuiSpeake event = new SuiSpeake();
        assertFalse(event.checkEventResponse(b));
    }

    // --- execute ---

    @Test
    public void testExecute_alwaysTrue() {
        Yukkuri b = createBody();
        SuiSpeake event = new SuiSpeake();
        assertTrue(event.execute(b));
    }

    // --- simpleEventAction: canEventResponse() returns false ---

    @Test
    public void testSimpleEventAction_DeadBody_doesNotThrow() {
        Yukkuri b = createBody();
        b.setDead(true);
        SuiSpeake event = new SuiSpeake();
        assertDoesNotThrow(() -> event.simpleEventAction(b));
    }

    // --- toString ---

    @Test
    public void testToString() {
        SuiSpeake event = new SuiSpeake();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- simpleEventAction: RND always returns 0 → bypass early-return ---

    @Test
    public void testSimpleEventAction_fromEqualsB_returnsFalse() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri b = createBody();
            SuiSpeake event = new SuiSpeake(b, null, null, 1);
            // from == b → line 84 → return false
            assertFalse(event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    @Test
    public void testSimpleEventAction_fromNull_targetNull_nextBoolTrue_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }

                @Override
                public boolean nextBoolean() {
                    return true;
                }
            };
            Yukkuri b = createBody();
            // Use parameterized constructor with null bodies to ensure from=-1, target=-1
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, null, 1);
            // from=null, target=null, nextBoolean=true → setBodyEventResMessage +
            // addWorldEvent
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    @Test
    public void testSimpleEventAction_fromNull_targetNull_nextBoolFalse_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }

                @Override
                public boolean nextBoolean() {
                    return false;
                }
            };
            Yukkuri b = createBody();
            // Use parameterized constructor with null bodies to ensure from=-1, target=-1
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, null, 1);
            // from=null, target=null, !isRude, nextBoolean=false → setMessage(YukkuringSui)
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    @Test
    public void testSimpleEventAction_fromNotNull_differentBody_targetNull_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            SuiSpeake event = new SuiSpeake(from, null, null, 1);
            // from != null, from != b, target=null, distance check → no parent/partner →
            // returns true
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    @Test
    public void testSimpleEventAction_fromNotNull_bIsPartnerOfFrom_targetNull_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            from.setPartner(b.getUniqueID());
            b.setPartner(from.getUniqueID());
            SuiSpeake event = new SuiSpeake(from, null, null, 1);
            // from != null, b == partner of from, target=null →
            // setMessage(WantingSuiPartner)
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: out of range → no action ---
    @Test
    public void testSimpleEventAction_fromNull_targetFar_noAction_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            src.entity.core.world.item.Sui sui = createSui(db, 1000, 1000); // far from b
            Yukkuri b = createBody(); // at 0,0
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: db==null → return false ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_dbNull_returnsFalse() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            src.entity.core.world.item.Sui sui = createSui(null, 0, 0); // bindobj=null
            Yukkuri b = createBody();
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertFalse(event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: db.isFather(b) → DrivingSuiPAPA ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_dbIsFather_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            b.setParents(new int[] { db.getUniqueID(), -1 }); // db is father of b
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: db.isParent(b) but not father (mother) →
    // DrivingSuiMAMA ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_dbIsMother_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            b.setParents(new int[] { -1, db.getUniqueID() }); // db is mother of b
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: b.isPartner(db) → DrivingSuiPartner ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_bIsPartnerOfDb_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            b.setPartner(db.getUniqueID());
            db.setPartner(b.getUniqueID());
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: b.isParent(db) → DrivingSuiChild ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_bIsParentOfDb_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            db.setParents(new int[] { b.getUniqueID(), -1 }); // b is father of db
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: db.isSister(b), db older → DrivingSuiOldSister
    // ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_dbIsElderSister_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri sharedMama = createBody();
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            db.setParents(new int[] { -1, sharedMama.getUniqueID() });
            b.setParents(new int[] { -1, sharedMama.getUniqueID() });
            db.setAge(200); // db older than b (b has default age ~50-150)
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: db.isSister(b), b older → DrivingSuiYoungSister
    // ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_dbIsYoungerSister_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri sharedMama = createBody();
            Yukkuri db = createBody();
            Yukkuri b = createBody();
            db.setParents(new int[] { -1, sharedMama.getUniqueID() });
            b.setParents(new int[] { -1, sharedMama.getUniqueID() });
            db.setAge(0);
            b.setAge(200); // b is older
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from==null, target!=null: no relationship → addBodyEvent ---
    @Test
    public void testSimpleEventAction_fromNull_targetSui_noRelationship_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri db = createBody();
            Yukkuri b = createBody(); // no relationship with db
            src.entity.core.world.item.Sui sui = createSui(db, 0, 0);
            SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target==null: b.isParent(from) → WantingSuiParent ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetNull_bIsParentOfFrom_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            from.setParents(new int[] { b.getUniqueID(), -1 }); // b is parent of from
            SuiSpeake event = new SuiSpeake(from, null, null, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target==null: far apart → no message ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetNull_tooFar_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            from.setX(1000);
            from.setY(1000); // far from b
            Yukkuri b = createBody(); // at 0,0
            from.setPartner(b.getUniqueID());
            b.setPartner(from.getUniqueID());
            SuiSpeake event = new SuiSpeake(from, null, null, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: from.isMother(b) → hasSuiPAPAChild ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_fromIsMother_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            b.setParents(new int[] { -1, from.getUniqueID() }); // from is mother of b
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: from.isFather(b) (not mother) → hasSuiMAMAChild
    // ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_fromIsFather_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            b.setParents(new int[] { from.getUniqueID(), -1 }); // from is father of b
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: b.isPartner(from) → hasSuiPartner ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_bIsPartner_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            b.setPartner(from.getUniqueID());
            from.setPartner(b.getUniqueID());
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: b.isParent(from) → hasSuiChild ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_bIsParent_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            from.setParents(new int[] { b.getUniqueID(), -1 }); // b is father of from
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: from.isElderSister(b) → hasSuiOldSister ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_fromIsElderSister_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri sharedMama = createBody();
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            from.setParents(new int[] { -1, sharedMama.getUniqueID() });
            b.setParents(new int[] { -1, sharedMama.getUniqueID() });
            from.setAge(200);
            b.setAge(0); // from is elder
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: from.isSister(b) but not elder →
    // hasSuiYoungSister ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_fromIsYoungerSister_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri sharedMama = createBody();
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            from.setParents(new int[] { -1, sharedMama.getUniqueID() });
            b.setParents(new int[] { -1, sharedMama.getUniqueID() });
            from.setAge(0);
            b.setAge(200); // b is elder, from is younger
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: no relationship → YukkuringSui ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_noRelationship_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody(); // no relationship
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 0, 0);
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    // --- from!=null, target!=null: out of range → no message ---
    @Test
    public void testSimpleEventAction_fromNotNull_targetSui_tooFar_doesNotThrow() {
        java.util.Random savedRND = SimYukkuri.RND;
        try {
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }
            };
            Yukkuri from = createBody();
            Yukkuri b = createBody();
            src.entity.core.world.item.Sui sui = createSui(new Reimu(), 1000, 1000); // far from b (at 0,0)
            SuiSpeake event = new SuiSpeake(from, null, sui, 1);
            assertDoesNotThrow(() -> event.simpleEventAction(b));
        } finally {
            SimYukkuri.RND = savedRND;
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_RudeBodyWithoutSuiQueuesWorldSpeakEventAndSetsWantingMessage() {
            java.util.Random savedRND = SimYukkuri.RND;
            try {
                SimYukkuri.RND = new java.util.Random() {
                    @Override
                    public int nextInt(int bound) {
                        return 0;
                    }

                    @Override
                    public boolean nextBoolean() {
                        return true;
                    }
                };
                Yukkuri b = createBody();
                b.setAttitude(src.enums.Attitude.SHITHEAD);
                SuiSpeake event = new SuiSpeake((Yukkuri) null, null, null, 1);

                assertTrue(event.simpleEventAction(b));

                assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size(),
                        "wanting-sui branch should queue exactly one world event");
                assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof SuiSpeake,
                        "wanting-sui branch should queue another SuiSpeake world event");
                assertEquals(b.getUniqueID(),
                        SimYukkuri.world.getCurrentMap().getEvent().get(0).getFrom(),
                        "queued world event should remember which body started talking about sui");
            } finally {
                SimYukkuri.RND = savedRND;
            }
        }

        @Test
        void testScenario_UnrelatedDriverQueuesFollowupBodySpeakEvent() {
            java.util.Random savedRND = SimYukkuri.RND;
            try {
                SimYukkuri.RND = new java.util.Random() {
                    @Override
                    public int nextInt(int bound) {
                        return 0;
                    }
                };
                Yukkuri driver = createBody();
                Yukkuri observer = createBody();
                src.entity.core.world.item.Sui sui = createSui(driver, 0, 0);
                SuiSpeake event = new SuiSpeake((Yukkuri) null, null, sui, 1);

                assertTrue(event.simpleEventAction(observer));

                assertEquals(1, observer.getEventList().size(),
                        "unrelated observer should receive exactly one follow-up body event");
                assertTrue(observer.getEventList().get(0) instanceof SuiSpeake,
                        "unrelated observer should queue a follow-up SuiSpeake body event");
                assertNull(observer.getCurrentEvent(), "follow-up sui speak should only be queued at this stage");
            } finally {
                SimYukkuri.RND = savedRND;
            }
        }
    }

    // --- Helper ---

    private static Yukkuri createBody() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    private static src.entity.core.world.item.Sui createSui(Yukkuri bindobj, int x, int y) {
        src.entity.core.world.item.Sui sui = new src.entity.core.world.item.Sui(x, y, 0);
        sui.setBindobj(bindobj);
        return sui;
    }
}
