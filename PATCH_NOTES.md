# Bronzework Games — Patch Notes

This file is the source of truth for player-facing patch notes until a
proper release-notes feature is built into the website. Each released
entry should be dated, scoped to player-visible behavior, and written in
plain English (not commit-message language).

When the website patch-notes feature ships, this file's contents should
be migrated into whatever storage the website uses (probably a JSON or
CMS backend). Until then, append entries below the `## Unreleased`
section as we ship them.

---

## Unreleased

### Bug Fixes

- **Eating food now actually heals you.** Bread, fish, cooked meat, and
  every other food in the game can be eaten by clicking "Eat" — the food
  is consumed from your inventory and your hitpoints go up. Previously
  the click did nothing because of a server-side option-mapping bug.
- **Banking is no longer broken.** Several distinct bugs were fixed:
  - Depositing items now updates your inventory correctly. Previously,
    the inventory could glitch or lock your character because the
    server was sending invalid item data to the client.
  - When you open the bank, your inventory tab now correctly switches
    to deposit-mode (Deposit-1, Deposit-5, Deposit-10, Deposit-All,
    Deposit-X). Previously you could only use the "Deposit All" button
    on the bank UI itself.
  - When you close the bank, your inventory tab restores to its normal
    options (Eat, Use, Wear, Examine, etc.). Previously the deposit
    options would persist, allowing players to remotely deposit items
    while walking around.
  - Placeholders work. You can toggle the placeholder option in the
    bank UI and withdrawing the last of a stack will leave a greyed-out
    placeholder slot in the bank. Previously enabling placeholders
    caused the bank to break and locked your character.
  - After closing the bank, right-clicking inventory items now shows
    the full menu (Use, Wear, Eat, etc.) again. Previously you had to
    log out and back in to restore the menu options.

### Internal / Admin

- Added `::iteminfo <item-id>` admin command for debugging item
  interface options. Useful for diagnosing missing right-click handlers.

### Known Issues

- Items dropped from killing NPCs are not yet appearing on the ground.
  Investigation in progress.
- Some character animations may appear deformed or visually broken.
  This is a known cosmetic issue and does not affect gameplay.
- The "Jagex Account" button on the title screen launches a real Jagex
  authentication flow. **Don't use it.** Use the username/password
  fields directly with any name (offline mode accepts anything).
- A small number of accounts get stuck on a "Please choose a name to
  use chat" prompt and can't interact. Workaround: create a new
  character. Will be fixed when the display-name flow is properly
  implemented.

---

## Format guide for future entries

```
## YYYY-MM-DD — vX.Y

### New Content
- ...

### Features
- ...

### Bug Fixes
- ...

### Quality of Life
- ...

### Balance
- ...

### Internal / Admin
- ...

### Known Issues
- ...
```

Categories are optional — only include the ones with entries. Each
bullet should be one sentence, present tense, focused on the player
outcome rather than the implementation. If a fix needs context, use
a sub-bullet for the explanation.

Bad: `Fixed RsModIndexedObjectProvider returning InventoryObject(slot, -1, -1)`
Good: `Fixed depositing items causing your inventory to glitch or your character to lock`
