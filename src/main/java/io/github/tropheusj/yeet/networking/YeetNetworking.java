package io.github.tropheusj.yeet.networking;

import java.util.Collection;
import java.util.UUID;

import io.github.tropheusj.yeet.Yeet;
import io.github.tropheusj.yeet.extensions.PlayerExtensions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class YeetNetworking {
	// client -> server: empty
	// server -> client: UUID, boolean
	public static final ResourceLocation CHARGE_STATUS = Yeet.id("charge_status");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(CHARGE_STATUS, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				((PlayerExtensions) player).yeet$startCharging();
				notifyTracking(player, true); // only sent to server when starting
			});
		});
	}

	public static void sendStopCharging(ServerPlayer player) {
		notifyTracking(player, false);
	}

	private static void notifyTracking(ServerPlayer player, boolean charging) {
		Collection<ServerPlayer> tracking = PlayerLookup.tracking(player);
		if (!tracking.isEmpty()) {
			FriendlyByteBuf packet = PacketByteBufs.create();
			packet.writeUUID(player.getUUID());
			packet.writeBoolean(charging);

			tracking.forEach(otherPlayer -> {
				if (otherPlayer != player) {
					ServerPlayNetworking.send(otherPlayer, CHARGE_STATUS, packet);
				}
			});
		}
	}

	@ClientOnly
	public static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(CHARGE_STATUS, (client, handler, buf, responseSender) -> {
			UUID playerId = buf.readUUID();
			boolean charging = buf.readBoolean();
			client.execute(() -> {
				if (client.level != null) {
					Player player = client.level.getPlayerByUUID(playerId);
					if (player instanceof PlayerExtensions ex && player != client.player) { // also implicit null check
						ex.yeet$setCharging(charging);
					}
				}
			});
		});
	}

	@ClientOnly
	public static void sendStartCharging() {
		ClientPlayNetworking.send(CHARGE_STATUS, PacketByteBufs.empty());
	}
}
