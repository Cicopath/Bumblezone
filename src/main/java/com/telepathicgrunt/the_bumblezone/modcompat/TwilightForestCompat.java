package com.telepathicgrunt.the_bumblezone.modcompat;

import com.telepathicgrunt.the_bumblezone.entities.EntityTeleportationHookup;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;

public class TwilightForestCompat {
	private static final String ENDER_BOW_ATTACHED_TAG = "twilightforest:ender";
	private static final ResourceLocation ENDER_BOW_RL = new ResourceLocation("twilightforest", "ender_bow");

	public static void setupCompat() {
		// Keep at end so it is only set to true if no exceptions was thrown during setup
		ModChecker.twilightForestPresent = true;
	}

	public static boolean isTeleportHandled(EntityHitResult entityHitResult, Projectile projectile) {
		if (projectile != null &&
			projectile.saveWithoutId(new CompoundTag()).getCompound("ForgeData").getBoolean(ENDER_BOW_ATTACHED_TAG) &&
			GeneralUtils.isInTag(Registry.ITEM, BzTags.ITEM_SPECIAL_DEDICATED_COMPAT, Registry.ITEM.get(ENDER_BOW_RL)))
		{
			return EntityTeleportationHookup.runEntityHitCheck(entityHitResult, projectile.getOwner(), projectile);
		}
		return false;
	}
}
