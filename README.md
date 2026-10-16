# Sprinting Horses

Sprinting Horses is a Minecraft Fabric mod that adds stamina-based sprinting to horses, donkeys, mules, zombie horses, and skeleton horses.

Hold the sprint key while riding a supported equine to move faster. The mod adds a stamina bar above the vanilla horse jump bar and handles exhaustion.

Current release: **1.1.0**

## Features

| Feature | Description |
| --- | --- |
| Mount Sprint | Hold the sprint key while riding a supported mount to gain speed. |
| Stamina HUD | Shows a custom stamina bar while riding supported mounts. |
| Exhaustion Modes | Choose between On, Partial, and Off through Mod Menu. |
| Armor Scaling | Horse armor changes sprint speed and max stamina for horses and zombie horses. |
| Jump Cost | Jumping spends stamina and is blocked while exhausted. |
| Donkey and Mule Support | These mounts use the same control style without armor scaling. |
| Undead Horse Support | Zombie horses sprint like normal horses; skeleton horses sprint without armor scaling. |

## Compatibility Roadmap

| Mod / Resource Pack | Status |
| --- | --- |
| Better Mount HUD | 🟨 In Development |
| Chai's Better HUD | 🟥 Planned |
| Raised | 🟥 Planned |
| Horse Statistics | 🟥 Planned |
| AppleSkin | 🟥 Planned |
| Vanilla Tweaks HUD | 🟥 Planned |
| Dark UI / GUI Resource Packs | 🟥 Planned |

- 🟩 Supported — Compatibility tested and confirmed.
- 🟨 In Development — Compatibility is being tested or receiving fixes.
- 🟥 Planned — Compatibility has not been investigated yet.

## Preview

![Horse Running Preview](https://cdn.modrinth.com/data/cached_images/94e37ed78388c30cc02c909a62a1305a9605baa2.gif)

## How It Works

1. Prepare a supported mount, including taming or saddling where vanilla requires it.
2. Ride it and hold the sprint key.
3. Watch the stamina bar drain while sprinting.
4. Release the sprint key to recover stamina.
5. If exhaustion is enabled, empty stamina triggers a recovery period before sprinting again.

## Supported Mounts

| Mount | Armor Scaling | Notes |
| --- | --- | --- |
| Horse | Yes | Better armor gives more speed and stamina. |
| Zombie Horse | Yes | Prepared for armor support. |
| Donkey | No | Uses the same sprint multiplier as an unarmored mount. |
| Mule | No | Uses the same sprint multiplier as a donkey. |
| Skeleton Horse | No | Does not use armor scaling. |

## Exhaustion Modes

| Mode | Behavior |
| --- | --- |
| On | Stamina drains, exhaustion uses the red recharge bar, and sprinting/jumping are blocked while exhausted. |
| Partial | Stamina drains without the full red exhaustion mode. Sprinting and jumping are blocked at empty stamina until recovery starts. |
| Off | Stamina does not drain and the mod HUD stays hidden. |

## Compatibility

- Public release 1.1.0: Minecraft 1.21.1
- Mod loader: Fabric
- Java: 21 or newer
- Required: Fabric API 0.116.12 or newer for Minecraft 1.21.1
- Environment: Client and Server
- Optional integration: Mod Menu

The source tree retains multiversion compatibility work, but other Minecraft versions are not included in the initial 1.1.0 publication and should not be considered validated for this release.

## Support

For bugs, suggestions, or support, use the Discord support forum:

https://discord.gg/pcKQSDgTzh

## License

This project is licensed under the MIT License.
