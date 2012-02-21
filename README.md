![Catacomb Snatch](http://i.imgur.com/uSFJF.png)

This is the source code of the Humble bundle release from Mojang.

They did not license it, so my guess is we are free to have this source code here and work on it.

### TODO's (in my view):
* Before starting the game, get a menu with what difficulty and level choicer.
* add levels
* add options menu for audio options etc..

### TODO list from [Minecraft Forum](http://www.minecraftforum.net/topic/1042382-the-unofficial-catacomb-snatch-patch/)
* Options Screen
* Save/Load Feature
* Enemies Spawn Outside Of Level Sometimes
* Add AI-player In Single Player
* Add Scarab
* Add Sphynx-Mummy
* Add Map Loader
* Pause Menu For Single And Mulitplayer
* Possibly (AND THIS IS A VERY BIG 'POSSIBLY') Co-Op
* Make It So Turrets Don't Fire At ALL Entities
* Make It Able To Change The Resolution Through `MojamComponent.java`

### How to build

* Get Gradle (www.gradle.org) ***Currently using 1.0-minlestone-8***
* gradle build

Your output artifact is in build/libs/Catacomb-Snatch-1.0.0.jar

You can run it the same way you would run catacomb snatch otherwise (java -jar build/libs/Catacomb-Snatch-1.0.0.jar)
Or use gradle run to build it and run it.

### How to make a native Mac build

In eclipse go to
File>Export>Other>Mac OS X application bundle 
choose the main class and fill in the appropriate information and then click finish to compile your build.

You also have the option to use an icon for the application (must be an icns file) which is provided below

Mac Icon download: http://catacomb-snatch.googlecode.com/files/cs_icon.icns

Catacomb Snatch
===============

### OBJECTIVE
Catacomb Snatch is a 2D top-down shooter/RTS. Your job is to collect 50 batches of treasure from the Treasure Trove located at the center of the map before your opponent. 

### BASIC GAMEPLAY
* Gather money by destroying enemies and collecting the coins that drop out
* Buy automatic turrets, harvesters and bombs from your Base Camp to help you defend and expand your territory, earn money and destroy obstacles
* Build a railway to the Treasure Trove and create Carts to gather batches 50 Treasure

### CONTROLS

* *Move*:                       Arrows / WASD
* *Shoot(lock firing angle)*:   Space / C / Shift / Alt / Ctrl
* *Buy/Pick Up*:                E / Z
* *Lay/Remove Track*:           R / X (10/15 Coins to add/remove railway sections)
* *Create a Cart*:              R / X while standing on the first rail in your base (50 coins)

### HEALTH
The player starts with 10/10 HP, which regenerates over time if you take damage.

### ITEMS
When enemies or Tombs are destroyed, coins appear. Grab them before they disappear so you can buy the following items at your Base Camp! (use E or Z while standing in front of an item)

* *Turret (150 coins)*: Shoots baddies in general vicinity once placed. Can be picked up and         moved. 
* *Collector (300 coins)*: Sucks up coins within a large radius. Pick up the collector to receive the coins. They can also be destroyed by enemies, so protect them!
* *Bomb (500 coins)*: Used to destroy purple gemmed walls. Shoot the bomb or have turrets shoot it to detonate it once placed.
* *Cart (50 coins)*: Rail carts can be bought by standing on the first section of track at your Base Camp. These will travel along rails that you lay down and will collect treasure from the Treasure Trove at the center of the map.

### ENEMIES
* Bats
* Cobras
* Mummies: Take a lot of damage, drop a lot of coins!
* Tombs: These spawn enemies
* The other player!

### MULTIPLAYER
* Catacomb Snatch can be played with your Ex-Friends! One of you will Host the game while the other will join.
* If you're hosting behind a firewall/router, you'll have to forward Port 3000 to your computer's IP
* If you don't have access to your firewall/router, you might consider using Hamachi

### FAQ

Q: How do I win the game?

A: Get to the center of the map by blowing up walls, and collect the treasure with Rail-Droids (Carts).

Q: Will Mojang continue developing the game?

A: Hopefully, but they released the source code so that others can as well.

Q: How do we get the source code?

A: By clicking the "Download source code" link in the download page sent to you after you donated.

Q: How do I compile the source-code?

A: 

- Download Eclipse JDT. #license:proprietary 
- Latest: http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/indigosr1
- Create a new project and copy all the files from the source zip into the project directory (just drag & drop them into eclipse. if it asks to overwrite press yes). 
- Open the Project properties (right click on the Project) and go to the Java Build Path settings. 
- In the Source tab add the "res" directory from your project to the build path. 
- Then select the Libraries Path tab and click Add External Jars and select all 4 libraries in the "lib" directory (also the one you copied from the zip into your project). 
- Open the com.mojang.mojam.MojamComponent.java class and hit F11. Select Java Application as run configuration when it asks for it.

### KNOWN BUGS
* Wrong victory screen is shown when game is won.
* If the Collector is destroyed, the game might crash/freeze.
* Some enemies spawn outside of level
* Sometimes Tombs spawn inside the Base Camp
    
### MISSING FEATURES
* No AI-player in single player.
* Scarab animations are done, but not included in the game.
* Sphynx-Mummy not included.
* Button graphics are unfinished.

### TRIVIA
* Graphics Gale was used for sprite animations
