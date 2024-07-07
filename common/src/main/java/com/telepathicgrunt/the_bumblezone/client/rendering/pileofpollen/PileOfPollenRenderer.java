package com.telepathicgrunt.the_bumblezone.client.rendering.pileofpollen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.events.client.BzBlockRenderedOnScreenEvent;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;


// CLIENT-SIDED
public class PileOfPollenRenderer {

    private static final ResourceLocation TEXTURE_POLLEN = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/block/pile_of_pollen/pile_of_pollen.png");
    private static final ResourceLocation TEXTURE_POLLEN_SUSPICIOUS = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "textures/block/pile_of_pollen/pile_of_pollen_suspicious_1.png");

    public static boolean pileOfPollenOverlay(BzBlockRenderedOnScreenEvent event) {
        BlockState blockState = event.state();
        if (event.type().equals(BzBlockRenderedOnScreenEvent.Type.BLOCK) && blockState.is(BzTags.POLLEN_BLOCKS)) {
            Player playerEntity = event.player();
            PoseStack matrixStack = event.stack();
            boolean isInPollen = false;
            for(int x = -1; x <= 1; x++) {
                for(int z = -1; z <= 1; z++) {
                    for(int y = -1; y <= 1; y++) {
                        // Squared to make it positive always for the addition
                        // Skips all non corner spots
                        if((x*x) + (y*y) + (z*z) <= 2) continue;

                        double eyePosX = playerEntity.getX() + x * playerEntity.getBbWidth() * 0.155F;
                        double eyePosY = playerEntity.getEyeY() + y * 0.12F;
                        double eyePosZ = playerEntity.getZ() + z * playerEntity.getBbWidth() * 0.155F;
                        Vec3 eyePosition = new Vec3(eyePosX, eyePosY, eyePosZ);
                        BlockPos eyeBlockPos = BlockPos.containing(eyePosition);
                        BlockState eyeBlock = playerEntity.level().getBlockState(eyeBlockPos);
                        VoxelShape blockBounds = eyeBlock.getShape(playerEntity.level(), eyeBlockPos);
                        if (!blockBounds.isEmpty()) {
                            Vec3 eyePos = eyePosition.subtract(Vec3.atLowerCornerOf(eyeBlockPos));
                            if (blockBounds.bounds().contains(eyePos)) {
                                isInPollen = true;
                                x = 2;
                                z = 2;
                                break;
                            }
                        }
                    }
                }
            }

            if(!isInPollen) {
                return true;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, blockState.is(BzBlocks.PILE_OF_POLLEN_SUSPICIOUS.get()) ? TEXTURE_POLLEN_SUSPICIOUS : TEXTURE_POLLEN);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float opacity = 1f;
            float brightness = 0.3f;
            float redStrength = 1f;
            float greenStrength = 0.9f;
            float blueStrength = 0.8f;
            RenderSystem.setShaderColor(brightness * redStrength, brightness * greenStrength, brightness * blueStrength, opacity);

            float pitch = -playerEntity.getYRot() / 64.0F;
            float yaw = playerEntity.getXRot() / 64.0F;
            float yawPlus4 = 4.0F + yaw;
            float pitchPlus4 = 4.0F + pitch;

            float movementScaling = 0.85f;
            Vector3f playerPosition = playerEntity.position().multiply(movementScaling, movementScaling, movementScaling).toVector3f();
            float smallXZOffset = playerPosition.x() * playerPosition.z() * 0;
            float smallYOffset = playerPosition.y() * 0.33f;

            Matrix4f matrix4f = matrixStack.last().pose();
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, -1.0F, -1.0F, -0.5F).setUv(pitchPlus4 - smallXZOffset, yawPlus4 - playerPosition.y());
            bufferbuilder.addVertex(matrix4f, 1.0F, -1.0F, -0.5F).setUv(pitch - smallXZOffset, yawPlus4 - playerPosition.y());
            bufferbuilder.addVertex(matrix4f, 1.0F, 1.0F, -0.5F).setUv(pitch - smallXZOffset, yaw - playerPosition.y());
            bufferbuilder.addVertex(matrix4f, -1.0F, 1.0F, -0.5F).setUv(pitchPlus4 - smallXZOffset, yaw - playerPosition.y());
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.setShaderColor(brightness * redStrength, brightness * greenStrength, brightness * blueStrength, opacity * 0.33f);
            bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, -1.0F, -1.0F, -0.5F).setUv(pitchPlus4 - playerPosition.x(), yawPlus4 - smallYOffset);
            bufferbuilder.addVertex(matrix4f, 1.0F, -1.0F, -0.5F).setUv(pitch - playerPosition.x(), yawPlus4 - smallYOffset);
            bufferbuilder.addVertex(matrix4f, 1.0F, 1.0F, -0.5F).setUv(pitch - playerPosition.x(), yaw - smallYOffset);
            bufferbuilder.addVertex(matrix4f, -1.0F, 1.0F, -0.5F).setUv(pitchPlus4 - playerPosition.x(), yaw - smallYOffset);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.setShaderColor(brightness * redStrength, brightness * greenStrength, brightness * blueStrength, opacity * 0.33f);
            bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(matrix4f, -1.0F, -1.0F, -0.5F).setUv(pitchPlus4 - playerPosition.z(), yawPlus4 - smallYOffset);
            bufferbuilder.addVertex(matrix4f, 1.0F, -1.0F, -0.5F).setUv(pitch - playerPosition.z(), yawPlus4 - smallYOffset);
            bufferbuilder.addVertex(matrix4f, 1.0F, 1.0F, -0.5F).setUv(pitch - playerPosition.z(), yaw - smallYOffset);
            bufferbuilder.addVertex(matrix4f, -1.0F, 1.0F, -0.5F).setUv(pitchPlus4 - playerPosition.z(), yaw - smallYOffset);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.disableBlend();
            return true;
        }
        return false;
    }
}
