package io.github.tropheusj.yeet.mixin;

import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
