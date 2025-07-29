# ⚔️ Soulbind SMP – Plugin Spec Overview

A PvP-focused survival plugin where Soul Fragments fuel permanent buffs, cursed gear, and long-term progression. Players interact directly via commands—no fixed locations required.

## 🩸 Soulbinding Core Mechanic

**Defeated Player**: Drops a Soul Fragment item on death

Soul Fragments exist as:
- 🧱 **Item Form**: tradable, storable, droppable
- 📊 **Stat Form**: converted via `/bindsoul`, used for buffs and milestones

**Drop Exception**: Dying while wielding a cursed item (e.g., the Cursed Mace) causes fragments to be consumed silently, not dropped

## 🪙 Soul Fragments – Usage & Mechanics

| Action | Effect | Location Required |
|--------|--------|-------------------|
| Eat Fragment | +1 Permanent Heart (Max HP) | Anywhere |
| Infuse Gear | Unlock item-specific perks | Anywhere |
| Trade Fragment Items | Exchange or gift items with other players | Anywhere |
| Convert to Stat | `/bindsoul` converts item → stat | Anywhere |
| Withdraw Stat | `/withdrawsoul <amount>` reduces stat count | Anywhere |

**Note**: Only stat fragments count toward milestone perks

## 🧬 Soul Fragment Milestone Buffs

Stat-based fragment totals unlock passive, stackable perks. Perks deactivate if count drops below the requirement.

| Fragment Count | Perk Description |
|----------------|------------------|
| 10+ | 🏃‍♂️ **Speed I** — permanent movement boost |
| 20+ | 🛡️ **Resistance I** — flat damage reduction |
| 25+ | ❤️ **+5 Max HP** — permanent health bonus |
| 30+ | 🔥 **/bloodrage** — Strength III for 30s (2 min cooldown) |

`/soulsight` (optional) shows fragment count + active buffs

## 🌀 Infused Gear System

Infused items use commands to trigger unique abilities:

- 🗡️ **Sword of the Drained** (retextured neth sword) - Steals 1 permanent heart on kill
- 👟 **Boots of Echoes** (neth boots) - Grants Speed II

## 🔨 Cursed Mace of Malediction

**Command**: `/usemace`

**Effect**: 30% chance to permanently shatter 1 heart on hit (permanently removes it)

### ☠️ Curse Mechanics

- Once picked up, the mace cannot be dropped, unequipped, or stored
- Wielder must kill a player every 24 real-world hours
- If not: silently lose 1 stat fragment
- Dying with the mace: fragment consumed, no item drop
- No visual or message triggers—curse effects are fully silent