# Enum Refactoring Review

This document lists enum class names and enum constant names that are still
meaning-weak, typo-prone, or structurally inconsistent.

Criteria:
- Class names should describe the domain, not the implementation type.
- Constant names should be readable without comments.
- Mixed-case enum constants are treated as suspicious unless they are clearly
  an established domain term.

## High Priority

### [x] `CoreAnkoState`
- Current constants:
  - `NORMAL`
  - `NON_YUKKURI_DISEASE_NEAR`
  - `NON_YUKKURI_DISEASE`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `YukkuriRelationType`
- Current constants:
  - `OTHER`
  - `FATHER`
  - `MOTHER`
  - `PARTNER`
  - `CHILD_OF_FATHER`
  - `CHILD_OF_MOTHER`
  - `ELDER_SISTER`
  - `YOUNGER_SISTER`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `CriticalDamageType`
- Current constants:
  - `INJURED`
  - `CUT`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `EffectType`
- Current constants:
  - `BAKED`
  - `HIT`
  - `MIXED`
  - `STEAMED`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `FootBake`
- Current constants:
  - `NONE`
  - `MEDIUM`
  - `CRITICAL`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `YukkuriBake`
- Current constants:
  - `NONE`
  - `MEDIUM`
  - `CRITICAL`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `UnbirthBabyState`
- Current constants:
  - `NONE`
  - `KILLED`
  - `ATTACKED`
  - `SAD`
  - `HAPPY`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `PublicRank`
- Current constants:
  - `NONE`
  - `UNUN_SLAVE`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `TickResult`
- Current constants:
  - `NONE`
  - `BIRTH`
  - `SHIT`
  - `CRUSHED_SHIT`
  - `DEAD`
  - `REMOVED`
  - `SUKKIRI`
  - `VOMIT`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `WorldSelection`
- Current constants:
  - `MYROOM`
  - `GARDEN`
  - `STREET`
  - `PARK1`
  - `PARK2`
  - `FOREST1`
  - `FOREST2`
  - `PLANT1`
  - `PLANT2`
  - `DISPOSER`
- Problems:
  - none remaining
- Suggested direction:
  - done

## Medium Priority

### [x] `GadgetMenu.ActionTarget`
- Current constants:
  - `IMMEDIATE`
  - `BODY`
  - `GADGET`
  - `BODY_AND_GADGET`
  - `TERRAIN`
  - `WALL`
  - `FIELD`
  - `TERRAIN_AND_GADGET`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `GadgetMenu.MainCategoryName`
- Current constants:
  - `MAIN`
  - `TOOL`
  - `BODY_CHANGE`
  - `AMPOULE`
  - `FOODS`
  - `CLEAN`
  - `ACCESSORY`
  - `PANTS`
  - `FLOOR`
  - `BARRIER`
  - `TOYS`
  - `CONVEYOR`
  - `VOICE`
  - `DEBUG`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `GadgetMenu.GadgetMenuChoice`
- Problems:
  - none remaining
- Suggested direction:
  - done

### [x] `ItemMenu.ShapeMenu`
- Current constants:
  - `SETUP`
  - `HARVEST`
  - `TOP`
  - `UP`
  - `DOWN`
  - `BOTTOM`
- Problems:
  - none remaining
- Suggested direction:
  - done

### `ImageCode`
- Problems:
  - [x] Mixed-case constant `EXCITING_raper` -> `EXCITING_RAPER`
  - Mixed suffixes / numbering that should be audited for consistency
- Suggested direction:
  - `EXCITING_RAPER`
  - revisit numbered variants and make the naming family consistent

### `ImageCode` family naming
- Examples worth auditing:
  - `YUNYAA1`, `YUNYAA2`, `YUNYAA3`
  - `YUNYAA1_HAIR2`
  - `MROLL_LEFT2_HAIR2`
  - `MROLL_RIGHT2_HAIR2`
- Problems:
  - Some numbers encode variant state but the pattern is not self-evident
- Suggested direction:
  - keep the domain names, but standardize the numbering scheme

## Lower Priority but Worth Cleaning

### `AgeState`
- Class name is fine, but it is one of the few enums that still uses `DEFAULT`-like
  conceptual naming in adjacent areas.

### `MessagePool.Action`
- Very large enum.
- The constants are mostly domain words, but it is a maintenance hotspot.
- Needs a separate pass if the project wants stronger naming guarantees.

### `YukkuriType`
- Mostly good.
- Some class-name strings and display strings should be audited separately,
  but the enum name itself is acceptable.

## Notes

- This review intentionally excludes `enum`-like nested types that are private
  implementation details unless they leak into public API.
- This is a naming review only. No compatibility aliases are assumed.
