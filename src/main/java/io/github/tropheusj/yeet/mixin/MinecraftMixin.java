package io.github.tropheusj.yeet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.tropheusj.yeet.LocalChargeTracker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@ModifyExpressionValue(
			method = "handleKeybinds",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/minecraft/client/Options;keyDrop:Lnet/minecraft/client/KeyMapping;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z",
					ordinal = 0
			)
	)
	private static boolean yeet(boolean pressed) {
		return LocalChargeTracker.INSTANCE.consumeYeet();
	}
}
