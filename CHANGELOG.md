### **(V.6.3.5 Changes) (1.19.2 Minecraft)**

##### Effects:
Improved the effect icon for Protection of the Hive and Wrath of the Hive.

##### Config:
Changed the default config value for keepBeeEssenceOnRespawning from false to true and renamed it to
 keepEssenceOfTheBeesOnRespawning so that everyone who had ran Bumblezone before now gets this true value.
 Less confusion this way.

### **(V.6.3.4 Changes) (1.19.2 Minecraft)**

##### Entities:
Fixed Bee Queen being always angry and spawning bees when set to noAi by command. (Also disables trading when noAi)

Fixed Beehemoth being able to be fed, tamed, and ridden when set to noAi by command.

##### Items:
Pollen Puff has a chance of spawning Pumpkin Stem nearby on valid blocks if it hits a Snow Golem with Pumpkin still on.

Pollen Puff has a chance of spawning Pumpkin Stem nearby on valid blocks if it hits a Morebabies's Snow Golem with Pumpkin still on.


### **(V.6.3.3 Changes) (1.19.2 Minecraft)**

##### Blocks:
Added two new configs for Crystalline Flower block: crystallineFlowerConsumeExperienceUI and crystallineFlowerConsumeItemUI
 Turning those off along with these two configs will disable flower's consuming entirely:
 crystallineFlowerConsumeItemEntities and crystallineFlowerConsumeExperienceOrbEntities

Added a third config called crystallineFlowerEnchantingPowerAllowedPerTier which sets the "enchanting power" of the flower per tier.
 Think of this like how Enchanting Tables only shows stronger or rarer enchantments when you have more bookshelves.
 Except here, the flower's tier times this config value is used as the threshold to know what enchantment and level to show.

Crystalline Flower now blacklists Veinmining's Vein Mining enchantment from being an option in this flower's UI.
 Controlled by `the_bumblezone:blacklisted_crystalline_flower_enchantments` enchantment tag.

##### Items:
Pollen Puff has a chance of spawning Red or Brown Mushroom if it hits Adventurez's red or Brown Fungus mob respectively of their type.

Pollen Puff can spawn Chorus Grass if it hits Better End's End Slime mob.

Pollen Puff can spawn Jungle Plant if it hits Better Nether's Jungle Skeleton.

Pollen Puff can spawn Spore Blossom if it hits Bosses of Mass Destruction's Void Blossom. Why you would do this? No clue lol.

Pollen Puff has a chance of spawning vine if it hits Rotten Creatures's Swampy mob.


### **(V.6.3.2 Changes) (1.19.2 Minecraft)**

##### Blocks:
Fixed Crystalline Flower's enchantment text symbols not showing correct symbol.


### **(V.6.3.1 Changes) (1.19.2 Minecraft)**

##### Blocks:
Fixed client side blockstate visual sometimes being incorrect when mass feeding Crystalline Flower by dropping items into it

##### Structures:
Made Hanging Gardens more common

##### Biomes:
Tried to lower chance of Spider/Cave Spider spawning in relation to Enderman and Phantoms


### **(V.6.3.0 Changes) (1.19.2 Minecraft)**

##### Blocks:
Fixed Glistering Honey Crystal not dropping itself when mined with Silk Touch.

Improved Glistering Honey Crystal's sparkling so it can appear anywhere on the block instead of just the corners.

Improved Glistering Honey Crystal's sparkling to use a new animated particle texture now.

Fixed unsuccessful lighting of Super Candles/Incense Candles still damaging/consuming the lighting item used by the player.

Added Crystalline Flower block!
 Spawns in the new Hanging Garden structure!
 Right click it to open up a GUI where you can grow the plant by giving it you XP or stacks of items to consume. Grows up to 7 blocks (tiers) high.
 Put a book or enchanted book into the enchantment side and you can select what enchantment you want to add to it! This will cost some tiers to work.
 More and stronger enchantments are available the higher the block's tier! But you cannot add more enchantments to books that have 3 or more enchantments already.
 You can grow the block's tiers even outside the GUI by dropping items into the block or letting experience orbs touch the block!
 Some items grant more progress towards upgrading the tiers. And when using tiers to enchant, some Honey Crystal Shards will drop!
 You can blacklist items from consumption, blacklist what enchantments are available, or change what items can be enchanted by tags:
 `the_bumblezone:crystalline_flower/cannot_consume` (item tag)
 `the_bumblezone:crystalline_flower/can_be_enchanted` (item tag)
 `the_bumblezone:crystalline_flower/xp_2_when_consumed` (item tag)
 `the_bumblezone:crystalline_flower/xp_5_when_consumed` (item tag)
 `the_bumblezone:crystalline_flower/xp_25_when_consumed` (item tag)
 `the_bumblezone:crystalline_flower/xp_100_when_consumed` (item tag)
 `the_bumblezone:blacklisted_crystalline_flower_enchantments` (enchantment tag)

Fixed Pile of Pollen, Sticky Honey Redstone, and Sticky Honey Residue blocking pistons when they should be destroyed on push.

##### Structures:
Added Hanging Gardens structure that you can find Crystalline Flower in!
 Be sure to look up! Can be found in Sugar Water Floor or Pollinated Fields biomes.

##### Biomes:
Fixed Pigs sometimes rarely spawning instead of Enderman.

Fixed Pollinated Fields and Pollinated Pillar sometimes placing the Pollen Piles on invalid blocks it cannot survive on.

##### Advancements:
Added more info for bee feeding and sugar infused stone advancements to clarify how to complete them.

Added advancement for Hanging Garden and Crystalline Flower block.
