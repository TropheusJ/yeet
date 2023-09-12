package io.github.tropheusj.yeet.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.tropheusj.yeet.extensions.ItemEntityExtensions;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements PlayerExtensions {
	@Unique
	private int chargeTicks;
	@Unique
	private boolean charging;

	@Inject(method = "tick", at = @At("HEAD"))
	private void tickYeetCharge(CallbackInfo ci) {
		if (charging) {
			this.chargeTicks++;
		}
	}

	@WrapWithCondition(
			method = "aiStep",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;touch(Lnet/minecraft/world/entity/Entity;)V",
					ordinal = 0 // non-exp
			)
	)
	private boolean allowHitEventToFire(Player self, Entity entity) {
		if (entity instanceof ItemEntityExtensions ex && ex.yeet$getChargeTicks() > 0) {
			return false; // skip touching here, let the item handle events
		} else {
			return true;
		}
	}

	@Override
	public int yeet$getChargeTicks() {
		return chargeTicks;
	}

	@Override
	public void yeet$setCharging(boolean charging) {
		this.charging = charging;
		if (!charging) {
			this.chargeTicks = 0;
		}
	}
}
