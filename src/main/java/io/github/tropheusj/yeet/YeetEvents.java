package io.github.tropheusj.yeet;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.EntityHitResult;

import org.quiltmc.qsl.base.api.event.Event;

/**
 * Various events involving yeeting items.
 * Removing the item entity is a valid operation,
 */
public class YeetEvents {
	public static final Event<Yeet> YEET = Event.create(Yeet.class, callbacks -> (player, item, ticks) -> {
		for (Yeet callback : callbacks) {
			if (item.isAlive()) {
				callback.onYeet(player, item, ticks);
			}
		}
	});

	public interface Yeet {
		void onYeet(ServerPlayer player, ItemEntity item, int chargeTicks);
	}

	public static final Event<Tick> TICK = Event.create(Tick.class, callbacks -> (item, ticks) -> {
		for (Tick callback : callbacks) {
			if (item.isAlive()) {
				callback.onTick(item, ticks);
			}
		}
	});

	public interface Tick {
		void onTick(ItemEntity item, int chargeTicks);
	}

	public static final Event<BlockHit> HIT_BLOCK = Event.create(BlockHit.class, callbacks -> (item, ticks, hit) -> {
		for (BlockHit callback : callbacks) {
			if (item.isAlive()) {
				callback.onHitBlock(item, ticks, hit);
			}
		}
	});

	public interface BlockHit {
		void onHitBlock(ItemEntity item, int chargeTicks, BlockHitResult hit);
	}

	public static final Event<EntityHit> HIT_ENTITY = Event.create(EntityHit.class, callbacks -> (item, ticks, hit) -> {
		for (EntityHit callback : callbacks) {
			if (item.isAlive()) {
				callback.onHitEntity(item, ticks, hit);
			}
		}
	});

	public interface EntityHit {
		void onHitEntity(ItemEntity item, int chargeTicks, EntityHitResult hit);
	}
}
