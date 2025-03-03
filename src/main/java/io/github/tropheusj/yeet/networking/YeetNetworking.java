package io.github.tropheusj.yeet.networking;

import java.util.Collection;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class YeetNetworking {
	public static final CustomPacketPayload.Type<ClientboundChargeStatusPacket> CLIENTBOUND_CHARGE_STATUS = new CustomPacketPayload.Type<>(
			Yeet.id("clientbound_charge_status")
	);
	public static final CustomPacketPayload.Type<ServerboundChargeStatusPacket> SERVERBOUND_CHARGE_STATUS = new CustomPacketPayload.Type<>(
			Yeet.id("serverbound_charge_status")
	);

	public static void init() {
		PayloadTypeRegistry.playC2S().register(SERVERBOUND_CHARGE_STATUS, ServerboundChargeStatusPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(CLIENTBOUND_CHARGE_STATUS, ClientboundChargeStatusPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_CHARGE_STATUS, (packet, context) -> {
			ServerPlayer player = context.player();
			((PlayerExtensions) player).yeet$startCharging();
			notifyTracking(player, true); // only sent to server when starting
		});
	}

	public static void sendStopCharging(ServerPlayer player) {
		notifyTracking(player, false);
	}

	private static void notifyTracking(ServerPlayer player, boolean charging) {
		Collection<ServerPlayer> tracking = PlayerLookup.tracking(player);
		if (!tracking.isEmpty()) {
			ClientboundChargeStatusPacket packet = new ClientboundChargeStatusPacket(player, charging);
			tracking.forEach(otherPlayer -> {
				if (otherPlayer != player) {
					ServerPlayNetworking.send(otherPlayer, packet);
				}
			});
		}
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(CLIENTBOUND_CHARGE_STATUS, YeetNetworking::handleClientboundChargeStatus);
	}

	@Environment(EnvType.CLIENT)
	private static void handleClientboundChargeStatus(ClientboundChargeStatusPacket packet, ClientPlayNetworking.Context ctx) {
		Player player = ctx.player().clientLevel.getPlayerByUUID(packet.player());
		if (player instanceof PlayerExtensions ex && player != ctx.player()) { // also implicit null check
			ex.yeet$setCharging(packet.charging());
		}
	}

	@Environment(EnvType.CLIENT)
	public static void sendStartCharging() {
		ClientPlayNetworking.send(ServerboundChargeStatusPacket.INSTANCE);
	}
}
