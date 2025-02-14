# SkySellWands
## Description
* SkySellWands adds sell wands that uses SkyShop's API selling.
## Required Dependencies
* SkyLib
* SkyShop
* Vault
## Soft Dependencies
* BentoBox
* WorldGuard
* QuickShop-Hikari
## Commands
- /skysellwand - Command to open the shop.
  - Alias:
    - /sellwand
- /skysellwand give <player_name> <# of uses> <amount> - Gives a player a sellwand with the defined uses and amount.
- /skysellwand reload - Reloads the plugin.
- /skysellwand help - Displays the help message.
## Permisisons
- `skysellwands.commands.skysellwand` - The permission to access the /skysellwand base command.
- `skysellwands.commands.skysellwands.give` - The permission to use /skysellwand give.
- `skysellwands.commands.skysellwands.help` - The permission to use /skysellwand help.
- `skysellwands.commands.skysellwands.reload` The permission to access /skysellwand reload.
## Issues, Bugs, or Suggestions
* Please create a new [Github Issue](https://github.com/lukesky19/SkySellWands/issues) with your issue, bug, or suggestion.
* If an issue or bug, please post any relevant logs containing errors related to SkySellWands and your configuration files.
* I will attempt to solve any issues or implement features to the best of my ability.
## FAQ
Q: What versions does this plugin support?

A: 1.21.0, 1.21.1, 1.21.2, 1.21.3, and 1.21.4.

Q: Are there any plans to support any other Minecraft versions?

A: I only plan to support one major game version at a time.

Q: Does this work on Spigot and Paper?

A: This plugin only works with Paper, it makes use of many newer API features that don't exist in the Spigot API. There are no plans to support Spigot.

Q: Is Folia supported?

A: There is no Folia support at this time. I may look into it in the future though.

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
