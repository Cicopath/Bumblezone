### **(V.7.3.2 Changes) (1.20.1 Minecraft)**

##### Items:
Made all Royal Jelly items in JEI/REI/EMI have info for how to obtain more of the Royal Jelly Bottles.

Fixed Life Essence item crashing when used near or above world height limit.

##### Blocks:
Fixed Crystalline Flower behaving/placing incorrectly when placed near or at world build height.

##### Structures:
Increased spawnrate of some structures.

##### Mod Compat:
(Forge): Made Buzzier Bees's Honeycomb Bricks and Chiseled Honeycomb Bricks spawn in Bumblezone caves.

##### Misc:
Fixed MC-268617 with a mixin. This is so people with Modrinth App can play Bumblezone. https://github.com/modrinth/theseus/issues/1048


### **(V.7.3.1 Changes) (1.20.1 Minecraft)**

##### Items:
Fixed Essence items not re-activating properly after their cooldown expires.

##### Music:
Forgot to compress the new song from 36MB down to 5MB and make it properly mono.


### **(V.7.3.0 Changes) (1.20.1 Minecraft)**

##### Structures:
Added new music for the White Sempiternal Sanctum event! Special thanks to Punpudle for creating the song!!

##### Entities:
Cosmic Crystal Entity's smashing attack now explodes when hitting its target rather than only when hitting blocks.

##### Items:
Added a music disc for the brand new song! (Song is called A First A Last)

Honey Compasses used in Creative Mode will now say they spawned a new compass in inventory rather than failed to locate.

Fixed Bee Bread not giving Beehemoths the Beenergized effect when feeding.

(Fabric/Quilt): Made Honey Bucket, Royal Jelly Bucket, Royal Jelly Bottle, Sugar Water Bucket, and Sugar Water Bottle all use the Fabric API's FluidStorage API.
 So these items are now seen as containing the Bumblezone fluids. Vanilla Honey Bottle is untouched to prevent mod compat issues.

##### Blocks:
Fixed possible rare Honey Cocoon crash during worldgen if it is replaced with air before chunk is fully made.

##### Fluids:
(Fabric/Quilt): Backported diagonal textures for Honey Fluid and Royal Jelly Honey Fluid. Forge cannot support this due to lack of API.

##### Configs:
Added allowWanderingTraderMusicDiscsTrades config option.
 Also moved the music disc configs into their own section in the configs.

(Fabric/Quilt): Added missing translations for knowingEssenceStructureNameServer,
 knowingEssenceStructureNameClient, disableEssenceBlockShaders, treeDungeonRarity


### **(V.7.2.21 Changes) (1.20.1 Minecraft)**

##### Fluids:
New textures for Honey Fluid and Royal Jelly Honey Fluid! Special thanks to Kryppers for creating these new textures!

##### Items:
Fixed Flower Headwear not respect item cooldowns disabling item abilities.

##### Entity:
Fix Rootmin crash when certain combinations of mods are on.
