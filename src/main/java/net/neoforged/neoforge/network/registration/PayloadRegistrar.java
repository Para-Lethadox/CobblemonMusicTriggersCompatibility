package net.neoforged.neoforge.network.registration;

import java.util.Objects;
import java.util.function.BiConsumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PayloadRegistrar {
    private final String id;
    private String version = "0";

    public PayloadRegistrar(String id) {
        this.id = id;
    }

    public PayloadRegistrar versioned(String version) {
        this.version = Objects.requireNonNull(version);
        return this;
    }

    public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type,
                                                             StreamCodec<FriendlyByteBuf, T> codec,
                                                             BiConsumer<T, PayloadContext> handler) {
        // In this stub implementation we simply remember that a handler was registered.
        RegisteredPayloads.register(id, version, type, codec, handler);
    }
}
