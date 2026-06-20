# Sprinting Horses

Sprinting Horses is a Minecraft Fabric mod that adds stamina-based sprinting to rideable mounts.

Hold the sprint key while riding a supported mount to move faster. The mod adds a stamina bar above the vanilla horse jump bar, handles exhaustion, and includes new upgraded on-a-stick items for pigs and striders.

## Features

| Feature | Description |
| --- | --- |
| Mount Sprint | Hold the sprint key while riding a supported mount to gain speed. |
| Stamina HUD | Shows a custom stamina bar while riding supported mounts. |
| Exhaustion Modes | Choose between On, Partial, and Off through Mod Menu. |
| Armor Scaling | Horse armor changes sprint speed and max stamina for horses and zombie horses. |
| Jump Cost | Jumping spends stamina and is blocked while exhausted. |
| Donkey, Mule, and Camel Support | These mounts use the same control style without armor scaling. |
| Undead Horse Support | Zombie horses sprint like normal horses; skeleton horses sprint without armor scaling. |
| Custom Pig and Strider Items | Golden Carrot on a Stick and Crimson Fungus on a Stick boost pigs and striders. |

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
| Camel | No | Vanilla camel sprint behavior is normalized to work with the mod. |
| Skeleton Horse | No | Does not use armor scaling. |
| Pig | Item-based | Faster with Golden Carrot on a Stick. |
| Strider | Item-based | Faster with Crimson Fungus on a Stick. |

## Exhaustion Modes

| Mode | Behavior |
| --- | --- |
| On | Stamina drains, exhaustion uses the red recharge bar, and sprinting/jumping are blocked while exhausted. |
| Partial | Stamina drains without the full red exhaustion mode. Sprinting and jumping are blocked at empty stamina until recovery starts. |
| Off | Stamina does not drain and the mod HUD stays hidden. |

## Items

### Golden Carrot on a Stick

Crafted like a Carrot on a Stick, but using a Golden Carrot instead of a regular carrot. Pigs can be controlled with it and move faster.

### Crimson Fungus on a Stick

Crafted like a Warped Fungus on a Stick, but using Crimson Fungus instead. Striders can be controlled with it and move faster.

## Compatibility

- Minecraft: 1.21.1
- Mod loader: Fabric
- Environment: Client and Server
- Optional integration: Mod Menu

## Support

For bugs, suggestions, or support, use the Discord support forum:

https://discord.gg/pcKQSDgTzh

## License

This project is licensed under the MIT License.
