package io.github.tropheusj.yeet.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum ServerboundChargeStatusPacket implements CustomPacketPayload {
	INSTANCE;

	public static final StreamCodec<ByteBuf, ServerboundChargeStatusPacket> CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return YeetNetworking.SERVERBOUND_CHARGE_STATUS;
	}
}
