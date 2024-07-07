package com.telepathicgrunt.the_bumblezone.modcompat.neoforge;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.BzAddBuiltinDataPacks;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.ModCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Optional;

public class BuzzierBeesCompat implements ModCompat {
    private static final ResourceLocation BEE_BOTTLE_RL = ResourceLocation.fromNamespaceAndPath("buzzier_bees", "bee_bottle");

    public BuzzierBeesCompat() {
        Optional<Item> bottledBee = BuiltInRegistries.ITEM.getOptional(BEE_BOTTLE_RL);

        if (bottledBee.isPresent() && BzModCompatibilityConfigs.allowBeeBottleRevivingEmptyBroodBlock) {
            setupDispenserCompat(bottledBee.get()); // adds compatibility with bottled bee in dispensers
        }

        Bumblezone.MOD_COMPAT_DATAPACKS.add(addBuiltinDataPacks ->
                addBuiltinDataPacks.add(
                        ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "buzzier_bees_compat"),
                        Component.literal("Bumblezone - Buzzier Bees Compat"),
                        BzAddBuiltinDataPacks.PackMode.FORCE_ENABLED
                )
        );

        // Keep at end so it is only set to true if no exceptions was thrown during setup
        ModChecker.buzzierBeesPresent = true;
    }

    private static void setupDispenserCompat(Item containerItem) {
//        BroodBlockModdedCompatDispenseBehavior newDispenseBehavior = new BroodBlockModdedCompatDispenseBehavior(
//                DispenserBlockInvoker.getDISPENSER_REGISTRY().get(containerItem),
//                (originalModdedDispenseBehavior, blockSource, itemStack, serverLevel, blockPos, blockState) -> {
//                    serverLevel.setBlockAndUpdate(blockPos, BzBlocks.HONEYCOMB_BROOD.get().defaultBlockState()
//                            .setValue(HoneycombBrood.FACING, blockState.getValue(EmptyHoneycombBrood.FACING))
//                            .setValue(HoneycombBrood.STAGE, itemStack.hasTag() && itemStack.getOrCreateTag().getInt("Age") < 0 ? 2 : 3));
//
//                    itemStack.shrink(1);
//
//                    if(!BzGeneralConfigs.dispensersDropGlassBottles) {
//                        if (!itemStack.isEmpty()) {
//                            if (blockSource.blockEntity() instanceof DispenserBlockEntity) {
//                                DispenserBlockEntity dispenser = blockSource.blockEntity();
//                                ItemStack honeyBottle = new ItemStack(Items.GLASS_BOTTLE);
//                                if (!HopperBlockEntity.addItem(null, dispenser, honeyBottle, null).isEmpty()) {
//                                    BroodBlockModdedCompatDispenseBehavior.DEFAULT_DROP_ITEM_BEHAVIOR.dispense(blockSource, honeyBottle);
//                                }
//                            }
//                        }
//                        else {
//                            itemStack = new ItemStack(Items.GLASS_BOTTLE);
//                        }
//                    }
//                    else {
//                        BroodBlockModdedCompatDispenseBehavior.DEFAULT_DROP_ITEM_BEHAVIOR.dispense(blockSource, new ItemStack(Items.GLASS_BOTTLE));
//                    }
//
//                    return itemStack;
//                }
//        );
//
//        DispenserBlock.registerBehavior(containerItem, newDispenseBehavior);
    }

    @Override
    public EnumSet<Type> compatTypes() {
        return EnumSet.of(Type.EMPTY_BROOD);
    }

    @Override
    public InteractionResult onEmptyBroodInteract(ItemStack itemstack, Player playerEntity, InteractionHand playerHand) {
//        if (!BzModCompatibilityConfigs.allowBeeBottleRevivingEmptyBroodBlock) return InteractionResult.PASS;
//        if (BuiltInRegistries.ITEM.getKey(itemstack.getItem()).equals(BEE_BOTTLE_RL)) {
//            if (!playerEntity.isCrouching()) {
//                if (!playerEntity.isCreative()) {
//                    playerEntity.setItemInHand(playerHand, new ItemStack(Items.GLASS_BOTTLE)); //replaced bottled bee with glass bottle
//                }
//
//                return itemstack.hasTag() && itemstack.getOrCreateTag().getInt("Age") < 0 ? InteractionResult.CONSUME_PARTIAL : InteractionResult.SUCCESS;
//            }
//        }

        return InteractionResult.PASS;
    }
}
