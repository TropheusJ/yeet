package io.github.tropheusj.yeet;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class DefaultYeetBehaviors {
	public static final YeetEvents.EntityHit FILL_ITEM_FRAME = (item, chargeTicks, hit) -> {
		if (hit.getEntity() instanceof ItemFrame itemFrame && itemFrame.getItem().isEmpty()) {
			itemFrame.setItem(item.getItem().copy());
			item.discard();
		}
	};
	public static final YeetEvents.EntityHit GIVE_TO_PLAYER = (item, chargeTicks, hit) -> {
		if (hit.getEntity() instanceof ServerPlayer player) {
			item.playerTouch(player);
		}
	};
	public static final YeetEvents.EntityHit DAMAGE_TARGET = (item, chargeTicks, hit) -> {
		float damageAmount = getDamageAmount(chargeTicks);
		if (damageAmount > 0) {
			DamageSource source = item.damageSources().source(Yeet.YEET_DAMAGE_TYPE);
			hit.getEntity().hurt(source, damageAmount);
		}
	};
//	public static final YeetEvents.EntityHit SPAWN_EGG = (item, chargeTicks, hit) -> {
//		ItemStack stack = item.getItem();
//		if (stack.getItem() instanceof SpawnEggItem egg
//				&& item.getOwner() instanceof ServerPlayer player
//				&& hit.getEntity() instanceof Mob mob) {
//			//noinspection unchecked
//			EntityType<? extends Mob> type = (EntityType<? extends Mob>) mob.getType();
//			Optional<Mob> spawned = egg.spawnOffspringFromSpawnEgg(
//					player, mob, type, player.serverLevel(), hit.getLocation(), stack.copy()
//			);
//		}
//	};

	public static void init() {
		YeetEvents.HIT_ENTITY.register(FILL_ITEM_FRAME);
		YeetEvents.HIT_ENTITY.register(GIVE_TO_PLAYER);
		YeetEvents.HIT_ENTITY.register(DAMAGE_TARGET);
		YeetEvents.HIT_ENTITY.register(DefaultYeetBehaviors::explode);
		YeetEvents.HIT_BLOCK.register(DefaultYeetBehaviors::explode);
	}

	private static float getDamageAmount(int chargeTicks) {
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

	private static void explode(ItemEntity item, int chargeTicks, HitResult hit) {
		if (chargeTicks > Yeet.TICKS_FOR_SUPERCHARGE_1) {
			Yeet.EXPLOSIVENESS.get(item.getItem().getItem()).ifPresent(value -> {
				Vec3 pos = hit.getLocation();
				item.level().explode(item, pos.x, pos.y, pos.z, value, Level.ExplosionInteraction.TNT);
				item.discard();
			});
		}
	}
}
