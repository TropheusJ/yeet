package io.github.tropheusj.yeet;

import java.util.Set;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import io.github.tropheusj.yeet.networking.YeetNetworking;

import org.jetbrains.annotations.Nullable;

public class Yeet implements ModInitializer {
	public static final String ID = "yeet";

	public static final int TICKS_FOR_MAX_WIND_UP = (int) (1.5 * 20);
	public static final int TICKS_FOR_SUPERCHARGE_1 = 4 * 20;
	public static final int TICKS_FOR_SUPERCHARGE_2 = 7 * 20;

	// power is a multiplier on the thrown item velocity
	// bow maxes out at 1
	public static final float MAX_WIND_UP_POWER = 1;
	public static final float SUPERCHARGE_1_POWER = MAX_WIND_UP_POWER * 1.5f;
	public static final float SUPERCHARGE_2_POWER = MAX_WIND_UP_POWER * 2;

	// poses where it makes sense to angle the arm for winding up
	public static final Set<Pose> GOOD_POSES = Set.of(Pose.STANDING, Pose.CROUCHING, Pose.SITTING);

	@Override
	public void onInitialize() {
		YeetNetworking.init();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	public static float getWindUp(int chargeTicks, float partialTicks, float min, float max) {
		float ticks = Math.min(TICKS_FOR_MAX_WIND_UP, chargeTicks + partialTicks); // max out at max ticks
		float progress = ticks / TICKS_FOR_MAX_WIND_UP;
		return smoothLerp(progress, min, max);
	}

	public static float smoothLerp(float progress, float a, float b) {
		float inSineDomain = Mth.map(progress, 0, 1, -Mth.HALF_PI, Mth.HALF_PI);
		float smoothProgress = (Mth.sin(inSineDomain) + 1) / 2;
		return Mth.lerp(smoothProgress, a, b);
	}

	public static float getPower(int chargeTicks) {
		if (chargeTicks >= TICKS_FOR_SUPERCHARGE_2) {
			return SUPERCHARGE_2_POWER;
		} else if (chargeTicks >= TICKS_FOR_SUPERCHARGE_1) {
			return SUPERCHARGE_1_POWER;
		} else {
			return getWindUp(chargeTicks, 0, 0, MAX_WIND_UP_POWER);
		}
	}

	@Nullable
	public static BlockState getSuperchargeFireState(int chargeTicks) {
		if (chargeTicks >= TICKS_FOR_SUPERCHARGE_2) {
			return Blocks.SOUL_FIRE.defaultBlockState();
		} else if (chargeTicks >= TICKS_FOR_SUPERCHARGE_1) {
			return Blocks.FIRE.defaultBlockState();
		} else {
			return null;
		}
	}
}
