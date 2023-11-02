package com.telepathicgrunt.the_bumblezone.configs;

import eu.midnightdust.lib.config.MidnightConfig;

public class BzConfig extends MidnightConfig {

    @Comment
    public static Comment beeAggressionComment;

    @Entry
    public static boolean beehemothTriggersWrath = false;

    @Entry
    public static boolean allowWrathOfTheHiveOutsideBumblezone = false;

    @Entry
    public static boolean showWrathOfTheHiveParticles = true;

    @Entry
    public static boolean aggressiveBees = true;

    @Entry(min=1, max=10000)
    public static int aggressionTriggerRadius = 64;

    @Entry(min=1, max=10000)
    public static int howLongWrathOfTheHiveLasts = 1680;

    @Entry(min=1, max=10000)
    public static int howLongProtectionOfTheHiveLasts = 1680;

    @Entry(min=1, max=100)
    public static int speedBoostLevel = 2;

    @Entry(min=1, max=100)
    public static int absorptionBoostLevel = 1;

    @Entry(min=1, max=100)
    public static int strengthBoostLevel = 1;


    @Comment
    public static Comment blockMechanicsComment;

    @Entry
    public static boolean dispensersDropGlassBottles = false;

    @Entry(min=1, max=1000)
    public static int broodBlocksBeeSpawnCapacity = 40;

    @Entry
    public static boolean pileOfPollenHyperFireSpread = false;

    @Entry
    public static boolean superCandlesBurnsMobs = true;

    @Comment
    public static Comment crystallineFlowerComment;

    @Entry
    public static boolean crystallineFlowerConsumeItemEntities = true;

    @Entry
    public static boolean crystallineFlowerConsumeExperienceOrbEntities = true;

    @Entry
    public static boolean crystallineFlowerConsumeExperienceUI = true;

    @Entry
    public static boolean crystallineFlowerConsumeItemUI = true;

    @Entry
    public static int crystallineFlowerEnchantingPowerAllowedPerTier = 8;

    @Entry
    public static int crystallineFlowerExtraTierCost = 0;

    @Entry
    public static int crystallineFlowerExtraXpNeededForTiers = 0;


    @Comment
    public static Comment dimensionComment;

    @Entry
    public static boolean enableInitialWelcomeMessage = true;

    @Entry(min=0, max=100000)
    public static double fogBrightnessPercentage = 110;

    @Entry(min=0, max=100)
    public static double fogThickness = 4;

    @Entry
    public static boolean enableDimensionFog = true;

    @Entry
    public static boolean onlyOverworldHivesTeleports = false;

    @Entry
    public static boolean forceExitToOverworld = false;

    @Entry
    public static boolean warnPlayersOfWrongBlockUnderHive = true;

    @Entry
    public static boolean enableExitTeleportation = true;

    @Entry
    public static boolean enableEntranceTeleportation = true;

    @Entry
    public static boolean forceBumblezoneOriginMobToOverworldCenter = true;

    @Entry
    public static String defaultDimension = "minecraft:overworld";


    @Comment
    public static Comment dungeonsComment;

    @Entry(min=0, max=1000)
    public static int beeDungeonRarity = 1;

    @Entry(min=0, max=1000)
    public static int spiderInfestedBeeDungeonRarity = 8;

    @Entry(min=0, max=1)
    public static float spawnerRateSpiderBeeDungeon = 0.2f;


    @Comment
    public static Comment brewingRecipeComment;

    @Entry
    public static boolean glisteringHoneyBrewingRecipe = true;

    @Entry
    public static boolean beeStingerBrewingRecipe = true;


    @Comment
    public static Comment generalComment;

    @Entry(min=0, max=100)
    public static double beehemothSpeed = 0.95;

    @Entry(min=0, max=256)
    public static int beeQueenBonusTradeRewardMultiplier = 3;

    @Entry(min=0, max=2000000)
    public static int beeQueenBonusTradeDurationInTicks = 24000;

    @Entry(min=0, max=1000000)
    public static int beeQueenBonusTradeAmountTillSatified = 24;

    @Entry
    public static boolean specialBeeSpawning = true;

    @Entry
    public static boolean beeLootInjection = true;

    @Entry
    public static boolean moddedBeeLootInjection = true;

    @Entry
    public static int nearbyBeesPerPlayerInBz = 25;

    @Entry
    public static boolean keepEssenceOfTheBeesOnRespawning = true;

    @Entry
    public static int musicDiscTimeLengthFlightOfTheBumblebee = 84;

    @Entry
    public static int musicDiscTimeLengthHoneyBee = 216;

    @Entry
    public static int musicDiscTimeLengthLaBeeDaLoca = 176;

    @Entry
    public static int musicDiscTimeLengthBeeLaxingWithTheHomBees = 300;


    @Comment
    public static Comment modCompatComment;

    @Entry
    public static boolean allowFriendsAndFoesBeekeeperTradesCompat = true;

    @Entry
    public static boolean allowGoodallBottledBeesRevivingEmptyBroodBlock = true;


    @Comment
    public static Comment clientComment;

    @Entry(min=0, max=1)
    public static double lgbtBeeRate = 0.013;

    @Entry(min=0, max=1)
    public static double ukraineBeeRate = 0.004;

    @Entry
    public static boolean enableAltBeeSkinRenderer = true;

    @Entry
    public static boolean playWrathOfHiveEffectMusic = true;

    @Entry
    public static boolean renderBeeQueenBonusTradeItem = true;

}
