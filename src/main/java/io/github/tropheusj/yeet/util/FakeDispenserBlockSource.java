package io.github.tropheusj.yeet.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
public class FakeDispenserBlockSource implements BlockSource {
	private static final BlockState dispenserState = Blocks.DISPENSER.defaultBlockState();
	private static final Direction dispenserFacing = dispenserState.getValue(DispenserBlock.FACING);
	private static final Direction intoDispenser = dispenserFacing.getOpposite();

	private final ServerLevel level;
	private final BlockPos pos;
	private final Vec3 posVec;

	public FakeDispenserBlockSource(ServerLevel level, BlockPos target) {
		this.level = level;
		this.pos = target.relative(intoDispenser);
		this.posVec = Vec3.atCenterOf(this.pos);
	}

	@Override
	public double x() {
		return posVec.x;
	}

	@Override
	public double y() {
		return posVec.y;
	}

	@Override
	public double z() {
		return posVec.z;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public BlockState getBlockState() {
		return dispenserState;
	}

	@Override
	public <T extends BlockEntity> T getEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServerLevel getLevel() {
		return level;
	}
}
