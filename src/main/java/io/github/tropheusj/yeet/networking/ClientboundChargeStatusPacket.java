package io.github.tropheusj.yeet.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ClientboundChargeStatusPacket(UUID player, boolean charging) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, ClientboundChargeStatusPacket> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, ClientboundChargeStatusPacket::player,
			ByteBufCodecs.BOOL, ClientboundChargeStatusPacket::charging,
			ClientboundChargeStatusPacket::new
	);

	public ClientboundChargeStatusPacket(Player player, boolean charging) {
		this(player.getUUID(), charging);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return YeetNetworking.CLIENTBOUND_CHARGE_STATUS;
	}
}
