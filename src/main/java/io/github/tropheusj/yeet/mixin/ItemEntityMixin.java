package io.github.tropheusj.yeet.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.ItemEntityExtensions;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityExtensions {
	@Shadow
	public abstract void playerTouch(Player player);

	@Shadow
	public abstract @Nullable Entity getOwner();

	@Unique
	private int chargeTicks;
	@Unique
	private int ticksOnGround;

	protected ItemEntityMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;tick()V"
			)
	)
	private void yeetHandling(CallbackInfo ci) {
		if (this.chargeTicks > 0 && this.level() instanceof ServerLevel && this.isAlive()) {
			if (this.onGround() || this.isInFluid()) {
				this.ticksOnGround++;
			} else {
				this.ticksOnGround = 0;
			}

			if (this.ticksOnGround > 5) {
				this.chargeTicks = 0;
				this.clearFire();
			}
		}
	}

	@Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
	private void dontBurnSupercharged(CallbackInfoReturnable<Boolean> cir) {
		if (this.chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
			cir.setReturnValue(Boolean.TRUE);
		}
	}

	@ModifyExpressionValue(
			method = "playerTouch",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/item/ItemEntity;pickupDelay:I"
			)
	)
	private int makePlayerCatch(int pickupDelay, Player player) {
		if (this.chargeTicks == 0 || player == this.getOwner())
			return pickupDelay;
		return 0;
	}

	@Unique
	private boolean isInFluid() {
		return !this.level().getFluidState(this.blockPosition()).isEmpty();
	}

	@Override
	public void yeet$setChargeTicks(int chargeTicks) {
		this.chargeTicks = chargeTicks;
	}

	@Override
	public int yeet$getChargeTicks() {
		return this.chargeTicks;
	}
}
