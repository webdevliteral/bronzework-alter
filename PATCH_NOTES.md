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

## 2026-04-09 — v0.2

### Bug Fixes

- **Goblins, giant spiders, imps, rats, and rams no longer deform during
  combat.** Each species now plays its own correct attack, block, and
  death animations. Previously they fell back on a human-player default
  whose bone IDs didn't exist on a non-humanoid skeleton, which twisted
  the mesh on every swing/hit/death.
- All cosmetic variants of these NPCs around Lumbridge are covered, not
  just the canonical entries. Report any species that still deforms and
  it'll be added in a follow-up.

### Internal / Admin

- Added `::npcanim <id>` admin command — prints the cache animation IDs
  (stand, walk, rotate, run, etc.) for an NPC. Used to discover where
  combat animations live for NPCs that aren't already mapped in the API.
- Added `::tryanim <id>` admin command — plays an arbitrary animation on
  your own character so an admin can visually identify what each ID
  looks like and fish out the attack/block/death IDs.
- Established the per-NPC combat-def authoring pattern under
  `plugins/content/npcs/combat/`. Adding more species is now a small,
  repeatable content task instead of a code change.

### Known Issues

- Many NPCs outside the Lumbridge starter area still use the broken
  default animations. They'll be fixed species-by-species as we work
  through the content list.

---

## 2026-04-08 — v0.1

### Bug Fixes

- **Eating food now actually heals you.** Bread, fish, cooked meat, and
  every other food in the game can be eaten by clicking "Eat" — the food
  is consumed from your inventory and your hitpoints go up. Previously
  the click did nothing because of a server-side option-mapping bug.
- **NPCs now drop loot when killed.** Killing any NPC produces a drop
  at their death tile, owned by the killer. Currently every NPC drops
  bones as a placeholder — real per-NPC drop tables (e.g. raw chicken
  + feathers from chickens, runes from goblins, etc.) will be added in
  content sessions. The drop pipeline itself (rolling, spawning,
  ownership, despawn timers) is fully wired up so adding real tables
  is just a matter of authoring the data.
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

- Every NPC currently drops only bones — no varied loot tables yet.
  This is a placeholder while real per-NPC drop data is authored.
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
