package io.github.tropheusj.yeet.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ItemFrameYeetBehavior implements YeetBehavior {
	@Override
	public boolean hitEntity(Entity entity, ItemEntity item, int chargeTicks) {
		return false;
	}

	@Override
	public boolean hitBlock(Level level, BlockPos pos, BlockState state, ItemEntity item, int chargeTicks) {
		return false;
	}
}
