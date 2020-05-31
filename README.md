# MC Randomizer
This project is a passion project to see how far I could mess with MC loot tables.

## Commands
|Command|Use|
|:---|:---|
|/randomizer|Displays a help message for randomizer.|
|/randomizer snapshot|Saves a snapshot of the current loot table.|
|/randomizer blocks|Randomizes the block loot table.|
|/randomizer entities|Randomizes the entity loot table.|
|/randomizer chests|Randomizes the chest loot table.|

## Supported versions:
* 1.15.2
* 1.14.4
* 1.13.2
* 1.13.1

## Loot tables able to be randomized
* Block loot table
* Entity loot table
* Chest loot table (the chests found in villager blacksmiths, etc..)

## TODO
* Add support for other versions
* Implement some tests for custom hash map

## FAQ
**Why won't my project compile** <br />
Your project probably won't compile because it doesn't contain all the supported spigot jars. <br />
To add support is very simple through maven's m2 configuration. <br />
<br />
Step 1: <br />
Download all the jars through BuildTools. An in-depth instruction page can be found here: https://www.spigotmc.org/wiki/buildtools/ <br />
Step 2: <br />
Install all the jars into your m2. <br />
The format I've used is this: `spigot.cb:cb{version}:{version}` <br />
To add this to your m2, go into the folder with the specific version jar and run this command: <br />
`mvn install:install-file -Dfile="{spigot_jar_file}" -Dpackaging="jar" -DgroupId="spigot.cb" -DartifactId="cb{version}" -Dversion="{version}"` <br />
You must replace `{spigot_jar_file}` with the file of the spigot jar, and `{version}` with the version of the jar. <br />

**Will you support version x** <br />
Probably, yes, just have to get time to look into the nms for it.