package io.github.tropheusj.yeet.mixin;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.YeetEvents;
import io.github.tropheusj.yeet.extensions.ItemEntityExtensions;

import net.minecraft.world.level.ClipContext;

import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.HitResult;

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
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityExtensions {
	@Shadow
	@Nullable
	public abstract Entity getOwner();

	@Shadow
	public abstract void playerTouch(Player player);

	@Shadow
	private int age;

	@Unique
	private int chargeTicks;

	public ItemEntityMixin(EntityType<?> variant, Level world) {
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
		if (chargeTicks > 0 && level() instanceof ServerLevel level && isAlive()) {
			YeetEvents.TICK.invoker().onTick((ItemEntity) (Object) this, chargeTicks);

			if (isRemoved())
				return;

			Vec3 pos = position();
			Vec3 vel = getDeltaMovement();
			Vec3 next = pos.add(vel);

			BlockHitResult blockHit = level.clip(
					new ClipContext(pos, next, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
			);
			if (blockHit.getType() != HitResult.Type.MISS) {
				YeetEvents.HIT_BLOCK.invoker().onHitBlock((ItemEntity) (Object) this, chargeTicks, blockHit);
			}

			if (isRemoved())
				return;

			EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
					level, this, pos, next, this.getBoundingBox().expandTowards(vel).inflate(1.0), this::canHitEntity
			);
			if (entityHit != null) {
				YeetEvents.HIT_ENTITY.invoker().onHitEntity((ItemEntity) (Object) this, chargeTicks, entityHit);
			}

			if (onGround()) {
				chargeTicks = 0;
				clearFire();
			}
		}
	}

	@Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
	private void dontBurnSupercharged(CallbackInfoReturnable<Boolean> cir) {
		if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
			cir.setReturnValue(Boolean.TRUE);
		}
	}

	@Unique
	private boolean canHitEntity(Entity entity) {
		if (entity == getOwner()) {
			return age > 20; // avoid hitting as soon as it spawns
		}
		return entity.canBeHitByProjectile();
	}

	@Override
	public void yeet$setChargeTicks(int chargeTicks) {
		this.chargeTicks = chargeTicks;
	}

	@Override
	public int yeet$getChargeTicks() {
		return chargeTicks;
	}
}
