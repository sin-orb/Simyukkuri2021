# Refactoring API Review

Scope:
- `src/main/java` の `public` / `protected` class, enum, method
- 名前に `Map` / `List` の語が残るものを機械的に抽出
- `Listener` などの false positive は除外

## 残件一覧

### Class / Enum

- `org.simyukkuri.command.GadgetMenu.GadgetList`
  - `List` が実装型に見えて意味が弱い
  - 候補: `GadgetItem`, `GadgetCommand`, `GadgetChoice`

- `org.simyukkuri.system.MessageMap`
  - `Map` が実装をそのまま露出している
  - 候補: `MessageCatalog`, `MessageTable`, `MessageSet`

- `org.simyukkuri.util.ListUtil`
  - 汎用 helper としても `List` だけを名前に出すのは弱い
  - 候補: `CollectionUtil`, `IntegerListUtil`, `ListOperations`

### Method

- `org.simyukkuri.entity.core.living.yukkuri.YukkuriFamilyDelegate.checkRemovedFamilyList()`
  - `List` が意味を足していない
  - 候補: `checkRemovedFamilyMembers()`, `refreshRemovedFamilyMembers()`

## Notes

- `Map` / `List` が「型」であることは署名だけで分かるので、public/protected API の名前に残す必要はない。
- `Listener` のように `List` を含むだけの別語は対象外。
- この一覧は「意味として通らない候補」の棚卸しであり、すべてが即座に修正対象とは限らない。
