# Changelog

## 1.0.0

- First public release for Minecraft Fabric 1.21.1.
- Restricted mount sprinting to horses, donkeys, mules, skeleton horses, and zombie horses.
- Removed sprint support for camels, pigs, striders, and all other mounts.
- Removed the custom Golden Carrot on a Stick and Crimson Fungus on a Stick, including their recipes and assets.
- Preserved the existing equine sprint controls, speed scaling, stamina, exhaustion, jump cost, HUD, and sound behavior.
- Centralized supported-mount validation and prevented stamina or jump effects from applying to unsupported equines.
- Hide the custom stamina HUD whenever Minecraft's HUD is hidden with F1.
- Add Hold and Toggle sprint activation modes using Minecraft's configured sprint key.
- Reset Toggle activation after dismounting, changing mounts, stamina depletion, death, disconnection, or world changes.
- Improve the stamina, experience, and jump bar layout while Better Mount HUD compatibility remains in development.
- Add English and Brazilian Portuguese translations for Sprint Activation.
- Add automated coverage for HUD visibility and Hold/Toggle activation behavior.
- Updated metadata and documentation for the Minecraft 1.21.1 Fabric release.
