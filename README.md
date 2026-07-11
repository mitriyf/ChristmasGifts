# 🎁 **ChristmasGifts** [![CodeFactor](https://www.codefactor.io/repository/github/mitriyf/christmasgifts/badge)](https://www.codefactor.io/repository/github/mitriyf/christmasgifts)
## 🛷 Deliver gifts to your server that will bring pleasant surprises to your players 
This plugin adds a gift delivery mechanic for your players. 
- $ Versions 1.7.1-26.1+ are supported.
- $ Has been tested on versions: 1.7.10, 1.8.8, 1.9.4, 1.12.2, 1.13.2, 1.16.5, 1.21
- $ On versions 1.7.10 and lower, the gift may have non-standard behavior. For example, it may become a redstone block. Do not change it to a head on 1.7, as it can be broken by a piston.
# 🚶 Wander the world
Wandering around the world, the player can get a gift from Santa, but did the Grinch put his rags in it?

![2024_10_29_15_47_36](https://github.com/user-attachments/assets/9dbf7a66-2234-45f5-85d9-f3538e6392ef)
## 🛠️ Supported:
### 🔮 Support Hex, MiniMessage (1.18+).
### 🌍 Languages:
- en_US (English (US))
- ru_RU (Russian)
- de_DE (German)
- Others (Add them yourself by enabling localizations)
### 🌐 Plugins:
- **WorldGuard, WorldEdit** - Check where your gifts will spawn.
- **PlaceholderAPI** - Add the ability to interact with the plugin via placeholders, and also add placeholders to messages from the configuration.
- **DecentHolograms/HolographicDisplays/FancyHolograms** - Put a hologram over the gift. 

![image](https://github.com/user-attachments/assets/50eaaa9b-1cfe-4609-b305-85d4453c44eb)

### 🔎 Checks:
- The plugin will automatically detect your server version so that it starts working correctly with your project.
- The plugin will check the working conditions and, if necessary, will warn about errors and turn off.
- Replacement of some parts of the configuration in case of their absence.
- The gift was protected from destruction attempts.

![2024_10_29_16_23_061-ezgif com-optimize](https://github.com/user-attachments/assets/143b3c1d-d69b-4a13-bd5e-10acf4750c32)

## ♾️ Functions:
### ⌨️Command (/gifts):
Get a list of commands using /gifts help
- /gifts reload - Reload the plugin.
- /gifts add Player - Spawn a gift near the player.
- /gifts put NamePlayer Amount - Set your gift limit for the player.
- /gifts check NamePlayer - Check the gift limit for players.
- /gifts loot - Get help about subcommand loot.
- /gifts locale - Check your client's language.
### 🚀Launch:
- Functions for saving limits and gifts when crashing on your server.
- Auto-issue a gift to make the issue faster.
- Select the operating mode. This plugin can work in the following operating modes:
  On command - 0 and others.
  Chance when the player moves - 1
  Every some time (ticks, 20 ticks = 1 second) - 2
// Use mode 2 for a better experience with plug-in performance
- Chance adjustment.
- The lock is spawned in the world, the biome.
- The lock is spawned with fly and shift.
### ✨Particles:
- Add particles to the gift at any interaction with it through the configuration (RGB, supported only from 1.9.1+)

![image](https://github.com/user-attachments/assets/e26cef7e-70ed-4c11-9aaf-03168326c662)

### ⚙️Config:
- Send actions to players using messages (MiniMessage support from 1.18+).
- Settings for gifts, loot, holograms
### 🎁Loot:
- Loot checks and in case of errors, errors will be sent to the console.
- Add loot via the /gifts
### 🔐Storage:
- Storing limit data and gifts in case of crashes using a file.
### 🔄Updater

## 📝 Configurations:
View them by navigating through the files using the following path: src\main\resources

# You can consider the rest of the possibilities when using the plugin.
