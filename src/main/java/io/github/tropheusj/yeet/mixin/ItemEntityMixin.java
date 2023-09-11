package io.github.tropheusj.yeet.mixin;

import io.github.tropheusj.yeet.extensions.ItemEntityExtensions;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityExtensions {
	@Shadow
	public abstract @Nullable Entity getOwner();

	@Shadow
	public abstract void playerTouch(Player player);

	@Unique
	private boolean yote;

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
		if (yote && level() instanceof ServerLevel level) {
			Vec3 pos = position();
			Vec3 vel = getDeltaMovement();
			Vec3 next = pos.add(vel);
			EntityHitResult hit = ProjectileUtil.getEntityHitResult(
					level, this, pos, next, this.getBoundingBox().expandTowards(vel).inflate(1.0), this::canHitEntity
			);
			if (hit != null) {
				Entity entity = hit.getEntity();
				if (entity instanceof ServerPlayer player) {
					playerTouch(player);
				}
				if (vel.length() > 1) {
					entity.hurt(level.damageSources().generic(), 4);
				}
			}
		}
	}

	@Unique
	private boolean canHitEntity(Entity entity) {
		return entity.canBeHitByProjectile() && getOwner() != entity;
	}

	@Override
	public void yeet$setYote(boolean yote) {
		this.yote = yote;
	}

	@Override
	public boolean yeet$isYote() {
		return yote;
	}
}
