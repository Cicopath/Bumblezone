### **(V.6.6.19 Changes) (1.19.4 Minecraft)**

##### Structures:
Fixed one of Cell Maze's piece not having properly randomize loot in its Honey Cocoon.

Increased chances of Pollinated Streams of having a spider spawner piece.

Added new item tag called `the_bumblezone:structures/disallowed_flowers_in_cocoon_loot` that can disallow flowers from showing
 up in Honey Cocoons in Bumblezone's structures. This is mainly used to prevent Botania's "special" flowers from
 spawning as loot for better balancing. Regular Botania flowers can still spawn. Just now overpowered flowers now.

Added new Entity Type tags that will be used for mod compat where a few of Bumblezone's Spider Spawners will be
 converted into spawners for certain other mod's mobs. You can use these tags to quickly add rare chances of new
 mob spawners in Bumblezone! The tags are:

- `the_bumblezone:structure_spawner_mobs/battle_cubes_rare_spawner_type`

- `the_bumblezone:structure_spawner_mobs/cell_maze_rare_spawner_type`

- `the_bumblezone:structure_spawner_mobs/pollinated_stream_rare_spawner_type`

- `the_bumblezone:structure_spawner_mobs/spider_infested_bee_dungeon_rare_spawner_type`

##### Mod Compat:
Better Animal Plus's Tarantula mob spawners may rarely spawn in Spider Infested Bee Dungeons and Pollinated Streams structures.

Twilight Forest's Hedge Spider mob spawners may rarely spawn in Spider Infested Bee Dungeons, Cell Maze, and Pollinated Streams structures.

Twilight Forest's Swarm Spider mob spawners may rarely spawn in Spider Infested Bee Dungeons and Cell Maze structures.

Earth To Java Mobs's Bone Spider mob spawners may rarely spawn in Battle Cubes structures.


### **(V.6.6.18 Changes) (1.19.2 Minecraft)**

##### Mod Compat:
JEI, REI, and EMI now shows Bee Queen trades for tradeable items! Special thanks to GizmoTheMoonPig for adding this!
 (Left click items to see trades that gives the item. Right click items to see trades that uses the item. Right click Bee Queen Spawn Egg to see all possible trades.)

##### Items:
Fixed Bumblebee Chestplate and Honeybee Leggings rendering improperly if more than one of them is rendering in the world.

Added Stinger Spear to `c:spears` and Honey Crystal Shield to `c:shields` item tags for a bit more mod compatibility.

Added some Bumblezone items to various community `c` item tags for a bit more mod compatibility.

##### Fluids:
Honey Fluid and Royal Jelly Fluid now turns into Glistering Honey Crystal block when it touches non-honey fluids!

##### Blocks:
Sugar Infused Stone and Sugar Infused Cobblestone will now turn neighboring Water tagged fluids into Sugar Water fluid.

String Curtains now says message to use String to extend curtain if one right-clicks the curtain with another curtain item.

More items now setup so they give Crystalline Flower more XP than normal when consumed.
 Beacon, Glowstone, Spectral Arrow, Glistering Melon Slice, Golden Carrot, Golden Apple, and End Crystal gives a little more XP to flower when consumed.
 Enchanted Books gives more XP when consumed by flower, so they can be partially recycled into new enchantments.
 Royal Jelly Bottle gives an insane more XP due to its rarity.
 Royal Jelly Bucket/Block now maxes out the Crystalline Flower regardless of its XP requirements.

Two new item tags were added for the above Crystalline Flower consuming changes:

- `the_bumblezone:crystalline_flower/xp_1000_when_consumed`

- `the_bumblezone:crystalline_flower/xp_maxed_when_consumed`

##### Entities:
Adult Honey Slime now will split into 2 to 4 baby Honey Slimes when killed.

Adult Honey Slime that you harvested honey off of now will split into 2 to 4 tiny vanilla Slimes when killed.

Baby Honey Slimes now drops loot when killed

When a Honey Slime is killed, all nearby Honey Slimes will aggro against you. This includes splitting adults into babies.

Removed many wooden trades from Bee Queen so there's less wood pollution in the trade system. Adjusted some trades as well for balance.

##### Advancements:
Changed the Music Disc advancement to now progress when obtaining the Music Discs from anywhere. Not just Wandering Trader's trades.
 As long as the Bumblezone Music Disc gets into your player's inventory, it'll count towards the advancement progress.

Added new item tag `the_bumblezone:queens_desire/honey_drunk_trigger_items` item tag so now Honey Drunk advancement
 can be completed with more tagged modded honey bottle items other than vanilla's Honey Bottle item.

##### Lang:
Updated ru_ru.json file. Special thanks to MageInBlack!