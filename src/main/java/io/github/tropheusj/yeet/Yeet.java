package io.github.tropheusj.yeet;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.BowItem;

import net.minecraft.world.item.Item;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import io.github.tropheusj.yeet.networking.YeetNetworking;

import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yeet implements ModInitializer {
	public static final String ID = "yeet";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final int TICKS_FOR_MAX_WIND_UP = 2 * 20;
	public static final int TICKS_FOR_SUPERCHARGE_1 = 4 * 20;
	public static final int TICKS_FOR_SUPERCHARGE_2 = 6 * 20;

	// bow power maxes out at 20 ticks
	public static final float SUPERCHARGE_1_POWER = BowItem.getPowerForTime(20) * 1.5f;
	public static final float SUPERCHARGE_2_POWER = BowItem.getPowerForTime(20) * 2;

	public static final RegistryEntryAttachment<Item, Float> EXPLOSIVENESS = RegistryEntryAttachment.builder(
			BuiltInRegistries.ITEM,
			id("explosiveness"),
			Float.class,
			ExtraCodecs.POSITIVE_FLOAT
	).build();

	public static final ResourceKey<DamageType> YEET_DAMAGE_TYPE = ResourceKey.create(
			Registries.DAMAGE_TYPE, id("yeet")
	);

	@Override
	public void onInitialize(ModContainer mod) {
		YeetNetworking.init();
		DefaultYeetBehaviors.init();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}

	public static float getWindUp(float partialTicks, int chargeTicks, float min, float max) {
		// sinusoidal function starting at 0, maxing out at pi/2 after TICKS_FOR_MAX_WIND_UP
		float progress = Math.min(TICKS_FOR_MAX_WIND_UP, chargeTicks + partialTicks);
		float mult = Mth.TWO_PI / (2 * TICKS_FOR_MAX_WIND_UP);
		return (max / 2) * (-Mth.cos(progress * mult) + 1) + min;
	}

	public static float getPower(int chargeTicks) {
		if (chargeTicks >= TICKS_FOR_SUPERCHARGE_2) {
			return SUPERCHARGE_2_POWER;
		} else if (chargeTicks >= TICKS_FOR_SUPERCHARGE_1) {
			return SUPERCHARGE_1_POWER;
		} else {
			return BowItem.getPowerForTime(chargeTicks);
		}
	}

	public static float getDamageAmount(int chargeTicks) {
		if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_2) {
			return 6;
		} else if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
			return 4;
		} else if (chargeTicks >= Yeet.TICKS_FOR_MAX_WIND_UP) {
			return 2;
		} else {
			return 0;
		}
	}
}
