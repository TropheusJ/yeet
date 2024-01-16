package io.github.tropheusj.yeet.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.tropheusj.yeet.LocalChargeTracker;
import io.github.tropheusj.yeet.YeetClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@WrapOperation(
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
	private boolean yeet(KeyMapping keyDrop, Operation<Boolean> original) {
		if (YeetClient.YEET_KEY.same(keyDrop))
			return LocalChargeTracker.INSTANCE.consumeYeet();
		return LocalChargeTracker.INSTANCE.consumeYeet() || original.call(keyDrop);
	}
}
