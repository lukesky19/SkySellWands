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

A: 1.21.4, 1.21.5, 1.21.6, 1.21.7, and 1.21.8.

Q: Are there any plans to support any other versions?

A: I will always do my best to support the latest versions of the game. I will sometimes support other versions until I no longer use them.

Q: Does this work on Spigot? Paper? (Insert other server software here)?

A: I only support Paper, but this will likely also work on forks of Paper (untested). There are no plans to support any other server software (i.e., Spigot or Folia).

## Building
* Go to [SkyLib](https://github.com/lukesky19/SkyLib) and follow the "For Developers" instructions.
* Then run:
  ```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
