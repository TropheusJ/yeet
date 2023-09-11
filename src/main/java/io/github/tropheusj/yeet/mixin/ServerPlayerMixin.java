package io.github.tropheusj.yeet.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.authlib.GameProfile;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.YeetEvents;
import io.github.tropheusj.yeet.extensions.ItemEntityExtensions;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;

import io.github.tropheusj.yeet.networking.YeetNetworking;

import net.minecraft.world.entity.item.ItemEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@WrapWithCondition(
			method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
			)
	)
	private boolean yeet(Level level, Entity entity) {
		if (entity instanceof ItemEntityExtensions item && this instanceof PlayerExtensions self) {
			int chargeTicks = self.yeet$getChargeTicks();

			if (chargeTicks != 0) { // release any charge
				self.yeet$stopCharging();
				YeetNetworking.sendStopCharging((ServerPlayer) (Object) this);
			}
			if (chargeTicks > 10) { // only actually a yeet if sufficiently charged
				YeetEvents.YEET.invoker().onYeet((ServerPlayer) (Object) this, (ItemEntity) entity, chargeTicks);
				if (entity.isRemoved())
					return false;

				item.yeet$setChargeTicks(chargeTicks);
				if (chargeTicks >= Yeet.TICKS_FOR_SUPERCHARGE_1) {
					entity.setSecondsOnFire(60 * 5);
				}
				// based on BowItem and arrows
				float power = Yeet.getPower(chargeTicks);
				float pitch = this.getXRot();
				float yaw = this.getYRot();
				Vec3 vel = new Vec3(
						power * -Mth.sin(yaw * (Mth.PI / 180)) * Mth.cos(pitch * (Mth.PI / 180)),
						power * -Mth.sin(pitch * (Mth.PI / 180)),
						power * Mth.cos(yaw * (Mth.PI / 180)) * Mth.cos(pitch * (Mth.PI / 180))
				);
				entity.setDeltaMovement(vel);
			}
		}

		return true;
	}
}
