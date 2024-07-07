package com.telepathicgrunt.the_bumblezone.client.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.telepathicgrunt.the_bumblezone.client.armor.ArmorModelProvider;
import com.telepathicgrunt.the_bumblezone.events.client.BzRegisterArmorProviderEvent;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FabricArmorRenderer implements ArmorRenderer {

    private final ArmorModelProvider provider;

    public FabricArmorRenderer(ArmorModelProvider provider) {
        this.provider = provider;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource source, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> model) {
        ArmorRenderer.renderPart(
                poseStack,
                source,
                light,
                stack,
                provider.getFinalModel(entity, stack, slot, model),
                provider.getArmorTexture(entity, stack, slot, null)
        );
    }

    public static void setupArmor() {
        BzRegisterArmorProviderEvent.EVENT.invoke(new BzRegisterArmorProviderEvent((item, provider) -> {
            ArmorModelProvider.register(item, provider);
            ArmorRenderer.register(new FabricArmorRenderer(provider), item);
        }));
    }
}
