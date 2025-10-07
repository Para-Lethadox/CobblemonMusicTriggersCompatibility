package net.neoforged.neoforge.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class PacketDistributor {
    private PacketDistributor() {
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        // No-op in stub
    }
}
