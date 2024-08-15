package com.telepathicgrunt.the_bumblezone.configs.forge;

import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.stream.Collectors;

public class BzModCompatibilityConfig {
	public static final ForgeConfigSpec GENERAL_SPEC;

	public static ForgeConfigSpec.ConfigValue<String> alternativeFluidToReplaceHoneyFluid;
	public static ForgeConfigSpec.BooleanValue allowHoneyFluidTanksFeedingCompat;

	public static ForgeConfigSpec.BooleanValue spawnPokecubeBeePokemon;
	public static ForgeConfigSpec.BooleanValue beePokemonGetsProtectionEffect;
	public static ForgeConfigSpec.DoubleValue spawnrateOfPokecubeBeePokemon;

	public static ForgeConfigSpec.BooleanValue spawnTropicraftBeesMob;
	public static ForgeConfigSpec.DoubleValue spawnrateOfTropicraftBeesMobs;
	public static ForgeConfigSpec.BooleanValue allowTropicraftSpawnFromDispenserFedBroodBlock;

	public static ForgeConfigSpec.BooleanValue spawnResourcefulBeesBeesMob;
	public static ForgeConfigSpec.DoubleValue spawnrateOfResourcefulBeesMobsBrood;
	public static ForgeConfigSpec.DoubleValue spawnrateOfResourcefulBeesMobsOther;
	public static ForgeConfigSpec.DoubleValue RBOreHoneycombSpawnRateBeeDungeon;
	public static ForgeConfigSpec.DoubleValue RBOreHoneycombSpawnRateSpiderBeeDungeon;
	public static ForgeConfigSpec.BooleanValue spawnResourcefulBeesHoneycombVeins;
	public static ForgeConfigSpec.BooleanValue allowResourcefulBeesBeeJarRevivingEmptyBroodBlock;
	public static ForgeConfigSpec.BooleanValue allowResourcefulBeesSpawnFromDispenserFedBroodBlock;

	public static ForgeConfigSpec.BooleanValue spawnProductiveBeesBeesMob;
	public static ForgeConfigSpec.DoubleValue spawnrateOfProductiveBeesMobs;
	public static ForgeConfigSpec.BooleanValue allowProductiveBeesBeeCageRevivingEmptyBroodBlock;
	public static ForgeConfigSpec.BooleanValue allowProductiveBeesSpawnFromDispenserFedBroodBlock;
	public static ForgeConfigSpec.BooleanValue spawnProductiveBeesHoneycombVariants;
	public static ForgeConfigSpec.DoubleValue PBOreHoneycombSpawnRateBeeDungeon;
	public static ForgeConfigSpec.DoubleValue PBOreHoneycombSpawnRateSpiderBeeDungeon;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> allowedCombsForDungeons;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> allowedBees;

	public static ForgeConfigSpec.BooleanValue allowFriendsAndFoesBeekeeperTradesCompat;
	public static ForgeConfigSpec.BooleanValue injectBzItemsIntoQuarkEnchantmentTooltipsCompat;

	public static ForgeConfigSpec.BooleanValue allowBeeBottleRevivingEmptyBroodBlock;

	public static ForgeConfigSpec.BooleanValue allowBeeBucketRevivingEmptyBroodBlock;

	public static ForgeConfigSpec.BooleanValue allowPotionOfBeesRevivingEmptyBroodBlock;

	public static ForgeConfigSpec.BooleanValue allowGoodallBottledBeesRevivingEmptyBroodBlock;

	public static ForgeConfigSpec.BooleanValue allowBeekeeperTradesCompat;

	public static ForgeConfigSpec.BooleanValue allowLootrCompat;

	static {
		ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
		setupConfig(configBuilder);
		GENERAL_SPEC = configBuilder.build();
	}

	private static void setupConfig(ForgeConfigSpec.Builder builder) {
		builder.push("Mod Compatibility Options");
			builder.push("General Compat Options");

			alternativeFluidToReplaceHoneyFluid = builder
							.comment(" \n-----------------------------------------------------\n",
									" The fluid to replace Bumblezone's Honey Fluid in the dimension. Requires restart.",
									" Note, this will not replace already placed Bumblezone Honey Fluid.\n")
							.translation("the_bumblezone.config.alternativefluidtoreplacehoneyfluid")
							.define("alternativeFluidToReplaceHoneyFluid", "");

			allowHoneyFluidTanksFeedingCompat = builder
							.comment(" \n-----------------------------------------------------\n",
									" Will let you feed any item that has a Forge fluid capability attached and has",
									" any fluid that is tagged forge:fluid/honey inside. This works alongside the bee_feeding item tag.",
									" An item can still be fed even if bee_feeding tag doesn't have the item as long as the",
									" item fit the above conditions with the fluid capability and this config is set to true.\n")
							.translation("the_bumblezone.config.allowhoneyfluidtanksfeedingcompat")
							.define("allowHoneyFluidTanksFeedingCompat", true);

			builder.pop();

			builder.push("Pokecube Options");

			spawnPokecubeBeePokemon = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Pokecube's bee-like pokemon in The Bumblezone and from Honey Brood Blocks.\n")
					.translation("the_bumblezone.config.spawnpokecubebeepokemon")
					.define("spawnPokecubeBeePokemon", true);

			beePokemonGetsProtectionEffect = builder
					.comment(" \n-----------------------------------------------------\n",
							" Pokecube's bee-like pokemon that spawn in The Bumblezone will get",
							" Protection of the Hive effect. Attacking these pokemon in bumblezone will",
							" give you Wrath of the Hive effect and swarmed of angry bees.\n")
					.translation("the_bumblezone.config.beePokemonGetsProtectionEffect")
					.define("beePokemonGetsProtectionEffect", true);

			spawnrateOfPokecubeBeePokemon = builder
					.comment(" \n-----------------------------------------------------\n",
							" Chance of a Bee spawning from Honeycomb Brood Blocks being replaced by Pokecube's bee pokemon.",
							" 0 is no PC's mobs and 1 is max PC's mobs.\n")
					.translation("the_bumblezone.config.spawnrateofpokecubebeepokemon")
					.defineInRange("spawnrateOfPokecubeBeePokemon", 0.05D, 0D, 1D);

			builder.pop();

			builder.push("Tropicraft Options");

			spawnTropicraftBeesMob = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Tropicraft's Tropibee in The Bumblezone and from Honey Brood Blocks alongside",
							" regular bees at a spawnrateOfTropicraftBeesMobs chance when spawning regular bees.\n")
					.translation("the_bumblezone.config.spawntropicraftbeesmob")
					.define("spawnTropicraftBeesMob", true);

			spawnrateOfTropicraftBeesMobs = builder
					.comment(" \n-----------------------------------------------------\n",
							" Chance of a Bee spawning from Honeycomb Brood Blocks in Bumblezone dimension being replaced by Tropicraft's Tropibee.",
							" 0 is no Tropicraft's mobs and 1 is max Tropicraft's mobs.\n")
					.translation("the_bumblezone.config.spawnrateoftropicraftbeesmobs")
					.defineInRange("spawnrateOfTropicraftBeesMobs", 0.025D, 0D, 1D);

			allowTropicraftSpawnFromDispenserFedBroodBlock = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow Honeycomb Brood blocks fed by Dispenser to be able to have chance of spawning Tropicraft's Tropibee.\n")
					.translation("the_bumblezone.config.allowtropicraftspawnfromdispenserfedbroodblock")
					.define("allowTropicraftSpawnFromDispenserFedBroodBlock", true);

			builder.pop();

			builder.push("Resourceful Bees Options");

			spawnResourcefulBeesBeesMob = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Resourceful Bees in The Bumblezone and from Honey Brood Blocks alongside",
							" regular bees at a spawnrateOfResourcefulBeesMobs chance when spawning regular bees.",
							" You can datapack the_bumblezone:resourcefulbees/spawnable_from_brood_block entity tag",
							" and/or datapack the_bumblezone:resourcefulbees/spawnable_from_chunk_creation entity tag",
							" for more control of what kinds of bees spawns.\n")
					.translation("the_bumblezone.config.spawnresourcefulbeesbeesmob")
					.define("spawnResourcefulBeesBeesMob", true);

			spawnrateOfResourcefulBeesMobsBrood = builder
					.comment(" \n-----------------------------------------------------\n",
							" Chance of a Bee spawning from Honeycomb Brood Blocks in Bumblezone dimension being replaced by Resourceful Bee's mob.",
							" 0 is no RB's mobs and 1 is max RB's mobs.\n")
					.translation("the_bumblezone.config.spawnrateofresourcefulbeesmobsbrood")
					.defineInRange("spawnrateOfResourcefulBeesMobsBrood", 0.03D, 0D, 1D);

			spawnrateOfResourcefulBeesMobsOther = builder
					.comment(" \n-----------------------------------------------------\n",
							" Chance of a regular Bee spawning in Bumblezone being replaced by Resourceful Bee's mob.",
							" 0 is no RB's mobs and 1 is max RB's mobs.\n")
					.translation("the_bumblezone.config.spawnrateofresourcefulbeesmobsother")
					.defineInRange("spawnrateOfResourcefulBeesMobsOther", 0.008D, 0D, 1D);

			RBOreHoneycombSpawnRateBeeDungeon = builder
					.comment(" \n-----------------------------------------------------\n",
							" How much of Bee Dungeons is made of honeycombs from the_bumblezone:resourcefulbees/spawns_in_bee_dungeons block tag.",
							" 0 is no RB's honeycombs and 1 is max RB's honeycombs.\n")
					.translation("the_bumblezone.config.rborehoneycombspawnratebeedungeon")
					.defineInRange("RBOreHoneycombSpawnRateBeeDungeon", 0.06D, 0D, 1D);

			RBOreHoneycombSpawnRateSpiderBeeDungeon = builder
					.comment(" \n-----------------------------------------------------\n",
							" How much of Spider Infested Bee Dungeons is made of honeycombs from the_bumblezone:resourcefulbees/spawns_in_spider_infested_bee_dungeons block tag.",
							" 0 is no RB's honeycombs and 1 is max RB's honeycombs.\n")
					.translation("the_bumblezone.config.rborehoneycombspawnratespiderbeedungeon")
					.defineInRange("RBOreHoneycombSpawnRateSpiderBeeDungeon", 0.12D, 0D, 1D);

			spawnResourcefulBeesHoneycombVeins = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Resourceful Bees's various honeycomb variants in The Bumblezone at all",
							" kinds of heights and height bands. Start exploring to find where they spawn!",
							" ",
							" NOTE: Will require a restart of the world to take effect. \n")
					.translation("the_bumblezone.config.spawnresourcefulbeeshoneycombveins")
					.define("spawnResourcefulBeesHoneycombVeins", true);


			allowResourcefulBeesBeeJarRevivingEmptyBroodBlock = builder
				.comment(" \n-----------------------------------------------------\n",
						" Allow Bee Jars with bees inside to turn Empty Honeycomb Brood blocks into ",
						" a regular Honeycomb Brood Block with a larva inside! \n")
				.translation("the_bumblezone.config.allowresourcefulbeesbeejarrevivingemptybroodblock")
				.define("allowResourcefulBeesBeeJarRevivingEmptyBroodBlock", true);

			allowResourcefulBeesSpawnFromDispenserFedBroodBlock = builder
				.comment(" \n-----------------------------------------------------\n",
						" Allow Honeycomb Brood blocks fed by Dispenser to be able to have chance of spawning Resourceful Bees's bees.\n")
				.translation("the_bumblezone.config.allowresourcefulbeesspawnfromdispenserfedbroodblock")
				.define("allowResourcefulBeesSpawnFromDispenserFedBroodBlock", true);

			builder.pop();

			builder.push("Productive Bees Options");

			spawnProductiveBeesBeesMob = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Productive Bees in The Bumblezone and from Honey Brood Blocks alongside",
							" regular bees at a spawnrateOfProductiveBeesMobs chance when spawning regular bees.\n")
					.translation("the_bumblezone.config.spawnproductivebeesbeesmob")
					.define("spawnProductiveBeesBeesMob", true);

			spawnrateOfProductiveBeesMobs = builder
					.comment(" \n-----------------------------------------------------\n",
							" Chance of a Bee spawning in Bumblezone or from Honeycomb Brood Blocks being replaced by Productive Bee's mob.",
							" 0 is no PB's mobs and 1 is max PB's mobs.\n")
					.translation("the_bumblezone.config.spawnrateofproductivebeesmobs")
					.defineInRange("spawnrateOfProductiveBeesMobs", 0.03D, 0D, 1D);

			spawnProductiveBeesHoneycombVariants = builder
					.comment(" \n-----------------------------------------------------\n",
							" Spawn Productive Bees's various honeycomb variants in The Bumblezone at all",
							" kinds of heights and height bands. Start exploring to find where they spawn!",
							" Disabling this config will make all Productive Bees comb blocks not spawn in Bumblezone dimension.",
							" ",
							" To add or remove specific combs from spawning, datapack replace this placed feature tag file:",
							"`data/the_bumblezone/tags/worldgen/placed_feature/productive_bees_combs.json`",
							" ",
							" NOTE: This config will require a restart of the world to take effect. \n")
					.translation("the_bumblezone.config.spawnproductivebeeshoneycombvariants")
					.define("spawnProductiveBeesHoneycombVariants", true);

			allowedCombsForDungeons = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow what Productive Bees combs should be able to spawn in Bumblezone dungeons.")
					.translation("the_bumblezone.config.allowedcombsfordungeons")
					.defineList("allowedCombsForDungeons",
							List.of("productivebees:diamond",
									"productivebees:iron",
									"productivebees:coal",
									"productivebees:redstone",
									"productivebees:copper",
									"productivebees:lapis",
									"productivebees:gold",
									"productivebees:emerald",
									"productivebees:obsidian",
									"productivebees:experience",
									"productivebees:magmatic",
									"productivebees:amethyst",
									"productivebees:prismarine",
									"productivebees:crystalline",
									"productivebees:sugarbag",
									"productivebees:glowing",
									"productivebees:frosty",
									"productivebees:slimy",
									"productivebees:silky",
									"productivebees:blazing",
									"productivebees:ender",
									"productivebees:skeletal",
									"productivebees:ghostly",
									"productivebees:zombie"
							), (t) -> true);

			allowedBees = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow what Productive Bees bees should be able to spawn in Bumblezone. ")
					.translation("the_bumblezone.config.allowedBees")
					.defineList("allowedBees",
							List.of("productivebees:iron",
									"productivebees:coal",
									"productivebees:redstone",
									"productivebees:copper",
									"productivebees:lapis",
									"productivebees:gold",
									"productivebees:emerald",
									"productivebees:obsidian",
									"productivebees:experience",
									"productivebees:magmatic",
									"productivebees:amethyst",
									"productivebees:prismarine",
									"productivebees:crystalline",
									"productivebees:sugarbag",
									"productivebees:glowing",
									"productivebees:frosty",
									"productivebees:slimy",
									"productivebees:silky",
									"productivebees:blazing",
									"productivebees:ender",
									"productivebees:skeletal",
									"productivebees:ghostly",
									"productivebees:zombie"
							), (t) -> true);

			allowProductiveBeesBeeCageRevivingEmptyBroodBlock = builder
				.comment(" \n-----------------------------------------------------\n",
						" Allow Bee Cages with bees inside to turn Empty Honeycomb Brood blocks into ",
						" a regular Honeycomb Brood Block with a larva inside! \n")
				.translation("the_bumblezone.config.allowproductivebeesbeecagerevivingemptybroodblock")
				.define("allowProductiveBeesBeeCageRevivingEmptyBroodBlock", true);

			allowProductiveBeesSpawnFromDispenserFedBroodBlock = builder
				.comment(" \n-----------------------------------------------------\n",
						" Allow Honeycomb Brood blocks fed by Dispenser to be able to have chance of spawning Productive Bees's bees.\n")
				.translation("the_bumblezone.config.allowproductivebeesspawnfromdispenserfedbroodblock")
				.define("allowProductiveBeesSpawnFromDispenserFedBroodBlock", true);

			PBOreHoneycombSpawnRateBeeDungeon = builder
					.comment(" \n-----------------------------------------------------\n",
							" How much of Bee Dungeons is made of ore-based honeycombs.",
							" 0 is no PB's honeycombs and 1 is max PB's honeycombs.\n")
					.translation("the_bumblezone.config.pborehoneycombspawnratebeedungeon")
					.defineInRange("PBOreHoneycombSpawnRateBeeDungeon", 0.125D, 0D, 1D);

			PBOreHoneycombSpawnRateSpiderBeeDungeon = builder
					.comment(" \n-----------------------------------------------------\n",
							" How much of Spider Infested Bee Dungeons is made of ore-based honeycombs.",
							" 0 is no PB's honeycombs and 1 is max PB's honeycombs.\n")
					.translation("the_bumblezone.config.pborehoneycombspawnratespiderbeedungeon")
					.defineInRange("PBOreHoneycombSpawnRateSpiderBeeDungeon", 0.25D, 0D, 1D);

			builder.pop();

			builder.push("Friends and Foes Options");

			allowFriendsAndFoesBeekeeperTradesCompat = builder
					.comment(" \n-----------------------------------------------------\n",
							" Adds Bumblezone items to Friends and Foes's Beekeeper trades!\n")
					.translation("the_bumblezone.config.allowfriendsandfoesbeekeepertradescompat")
					.define("allowFriendsAndFoesBeekeeperTradesCompat", true);

			builder.pop();

			builder.push("Quark Options");

			injectBzItemsIntoQuarkEnchantmentTooltipsCompat = builder
					.comment(" \n-----------------------------------------------------\n",
							" Adds Bumblezone items symbols to Quark's enchantment tooltips!\n")
					.translation("the_bumblezone.config.injectbzitemsintoquarkenchantmenttooltipscompat")
					.define("injectBzItemsIntoQuarkEnchantmentTooltipsCompat", true);

			builder.pop();

			builder.push("Buzzier Bees Options");

			allowBeeBottleRevivingEmptyBroodBlock = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow Bee Bottle to turn Empty Honeycomb Brood blocks into a regular Honeycomb Brood Block with a larva inside! \n")
					.translation("the_bumblezone.config.allowbeebottlerevivingemptybroodblock")
					.define("allowBeeBottleRevivingEmptyBroodBlock", true);

			builder.pop();

			builder.push("Forbidden Arcanus Options");

			allowBeeBucketRevivingEmptyBroodBlock = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow Bee Bucket to turn Empty Honeycomb Brood blocks into a regular Honeycomb Brood Block with a larva inside! \n")
					.translation("the_bumblezone.config.allowbeebucketrevivingemptybroodblock")
					.define("allowBeeBucketRevivingEmptyBroodBlock", true);

			builder.pop();


			builder.push("Potion of Bees Options");

			allowPotionOfBeesRevivingEmptyBroodBlock = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow Potion of Bees to turn Empty Honeycomb Brood blocks into ",
							" a regular Honeycomb Brood Block with a larva inside! \n")
					.translation("the_bumblezone.config.allowpotionofbeesrevivingemptybroodblock")
					.define("allowPotionOfBeesRevivingEmptyBroodBlock", true);

			builder.pop();


			builder.push("Goodall Options");

			allowGoodallBottledBeesRevivingEmptyBroodBlock = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow Bottled Bees to turn Empty Honeycomb Brood blocks into ",
							" a regular Honeycomb Brood Block with a larva inside! \n")
					.translation("the_bumblezone.config.allowgoodallbottledbeesrevivingemptybroodblock")
					.define("allowGoodallBottledBeesRevivingEmptyBroodBlock", true);

			builder.pop();


			builder.push("Beekeeper Options");

			allowBeekeeperTradesCompat = builder
					.comment(" \n-----------------------------------------------------\n",
							" Adds Bumblezone items to Beekeeper mod's Beekeeper trades!\n")
					.translation("the_bumblezone.config.allowbeekeepertradescompat")
					.define("allowBeekeeperTradesCompat", true);

			builder.pop();


			builder.push("Lootr Options");

			allowLootrCompat = builder
					.comment(" \n-----------------------------------------------------\n",
							" Allow loot Cocoons to have compat with Lootr\n")
					.translation("the_bumblezone.config.allowLootrCompat")
					.define("allowLootrCompat", true);

			builder.pop();

		builder.pop();
	}

	public static void copyToCommon() {
		BzModCompatibilityConfigs.alternativeFluidToReplaceHoneyFluid = alternativeFluidToReplaceHoneyFluid.get();
		BzModCompatibilityConfigs.allowHoneyFluidTanksFeedingCompat = allowHoneyFluidTanksFeedingCompat.get();

		BzModCompatibilityConfigs.allowPotionOfBeesRevivingEmptyBroodBlock = allowPotionOfBeesRevivingEmptyBroodBlock.get();

		BzModCompatibilityConfigs.allowBeekeeperTradesCompat = allowBeekeeperTradesCompat.get();

		BzModCompatibilityConfigs.allowFriendsAndFoesBeekeeperTradesCompat = allowFriendsAndFoesBeekeeperTradesCompat.get();

		BzModCompatibilityConfigs.injectBzItemsIntoQuarkEnchantmentTooltipsCompat = injectBzItemsIntoQuarkEnchantmentTooltipsCompat.get();

		BzModCompatibilityConfigs.spawnTropicraftBeesMob = spawnTropicraftBeesMob.get();
		BzModCompatibilityConfigs.spawnrateOfTropicraftBeesMobs = spawnrateOfTropicraftBeesMobs.get();
		BzModCompatibilityConfigs.allowTropicraftSpawnFromDispenserFedBroodBlock = allowTropicraftSpawnFromDispenserFedBroodBlock.get();

		BzModCompatibilityConfigs.spawnProductiveBeesBeesMob = spawnProductiveBeesBeesMob.get();
		BzModCompatibilityConfigs.spawnrateOfProductiveBeesMobs = spawnrateOfProductiveBeesMobs.get();
		BzModCompatibilityConfigs.spawnProductiveBeesHoneycombVariants = spawnProductiveBeesHoneycombVariants.get();
		BzModCompatibilityConfigs.allowedCombsForDungeons = allowedCombsForDungeons.get().stream().map(String::toString).collect(Collectors.toList());
		BzModCompatibilityConfigs.allowedBees = allowedBees.get().stream().map(String::toString).collect(Collectors.toList());
		BzModCompatibilityConfigs.allowProductiveBeesBeeCageRevivingEmptyBroodBlock = allowProductiveBeesBeeCageRevivingEmptyBroodBlock.get();
		BzModCompatibilityConfigs.allowProductiveBeesSpawnFromDispenserFedBroodBlock = allowProductiveBeesSpawnFromDispenserFedBroodBlock.get();
		BzModCompatibilityConfigs.PBOreHoneycombSpawnRateBeeDungeon = PBOreHoneycombSpawnRateBeeDungeon.get();
		BzModCompatibilityConfigs.PBOreHoneycombSpawnRateSpiderBeeDungeon = PBOreHoneycombSpawnRateSpiderBeeDungeon.get();

		BzModCompatibilityConfigs.allowBeeBottleRevivingEmptyBroodBlock = allowBeeBottleRevivingEmptyBroodBlock.get();

		BzModCompatibilityConfigs.allowBeeBucketRevivingEmptyBroodBlock = allowBeeBucketRevivingEmptyBroodBlock.get();

		BzModCompatibilityConfigs.allowGoodallBottledBeesRevivingEmptyBroodBlock = allowGoodallBottledBeesRevivingEmptyBroodBlock.get();

		BzModCompatibilityConfigs.spawnPokecubeBeePokemon = spawnPokecubeBeePokemon.get();
		BzModCompatibilityConfigs.beePokemonGetsProtectionEffect = beePokemonGetsProtectionEffect.get();
		BzModCompatibilityConfigs.spawnrateOfPokecubeBeePokemon = spawnrateOfPokecubeBeePokemon.get();

		BzModCompatibilityConfigs.spawnResourcefulBeesBeesMob = spawnResourcefulBeesBeesMob.get();
		BzModCompatibilityConfigs.spawnrateOfResourcefulBeesMobsBrood = spawnrateOfResourcefulBeesMobsBrood.get();
		BzModCompatibilityConfigs.spawnrateOfResourcefulBeesMobsOther = spawnrateOfResourcefulBeesMobsOther.get();
		BzModCompatibilityConfigs.RBOreHoneycombSpawnRateBeeDungeon = RBOreHoneycombSpawnRateBeeDungeon.get();
		BzModCompatibilityConfigs.RBOreHoneycombSpawnRateSpiderBeeDungeon = RBOreHoneycombSpawnRateSpiderBeeDungeon.get();
		BzModCompatibilityConfigs.spawnResourcefulBeesHoneycombVeins = spawnResourcefulBeesHoneycombVeins.get();
		BzModCompatibilityConfigs.allowResourcefulBeesBeeJarRevivingEmptyBroodBlock = allowResourcefulBeesBeeJarRevivingEmptyBroodBlock.get();
		BzModCompatibilityConfigs.allowResourcefulBeesSpawnFromDispenserFedBroodBlock = allowResourcefulBeesSpawnFromDispenserFedBroodBlock.get();

		BzModCompatibilityConfigs.allowLootrCompat = allowLootrCompat.get();
	}
}