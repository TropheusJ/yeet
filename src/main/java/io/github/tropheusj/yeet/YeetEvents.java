package io.github.tropheusj.yeet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.quiltmc.qsl.base.api.event.Event;

/**
 * Various events involving yeeting items.
 * Removing the item entity is a valid operation.
 */
public class YeetEvents {
	public static final Event<OnYeet> YEET = Event.create(OnYeet.class, callbacks -> (player, item, ticks) -> {
		for (OnYeet callback : callbacks) {
			callback.onYeet(player, item, ticks);
			if (Yeet.isInvalid(item))
				break;
		}
	});

	public interface OnYeet {
		void onYeet(ServerPlayer player, ItemEntity item, int chargeTicks);
	}

	public static final Event<Tick> TICK = Event.create(Tick.class, callbacks -> (item, ticks) -> {
		for (Tick callback : callbacks) {
			callback.onTick(item, ticks);
			if (Yeet.isInvalid(item))
				break;
		}
	});

	public interface Tick {
		void onTick(ItemEntity item, int chargeTicks);
	}

	public static final Event<BlockHit> HIT_BLOCK = Event.create(BlockHit.class, callbacks -> (item, ticks, hit) -> {
		for (BlockHit callback : callbacks) {
			callback.onHitBlock(item, ticks, hit);
			if (Yeet.isInvalid(item))
				break;
		}
	});

	public interface BlockHit {
		void onHitBlock(ItemEntity item, int chargeTicks, BlockHitResult hit);
	}

	public static final Event<EntityHit> HIT_ENTITY = Event.create(EntityHit.class, callbacks -> (item, ticks, hit) -> {
		for (EntityHit callback : callbacks) {
			callback.onHitEntity(item, ticks, hit);
			if (Yeet.isInvalid(item))
				break;
		}
	});

	public interface EntityHit {
		void onHitEntity(ItemEntity item, int chargeTicks, EntityHitResult hit);
	}

	/**
	 * Utility interface for listening for any hit.
	 */
	public interface GenericHit extends EntityHit, BlockHit {
		void onHit(ItemEntity item, int chargeTicks, HitResult hit);

		@Override
		default void onHitBlock(ItemEntity item, int chargeTicks, BlockHitResult hit) {
			onHit(item, chargeTicks, hit);
		}

		@Override
		default void onHitEntity(ItemEntity item, int chargeTicks, EntityHitResult hit) {
			onHit(item, chargeTicks, hit);
		}
	}
}
