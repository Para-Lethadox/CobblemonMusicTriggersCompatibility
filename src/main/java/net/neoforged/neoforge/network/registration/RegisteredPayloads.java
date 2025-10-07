package net.neoforged.neoforge.network.registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class RegisteredPayloads {
    private static final List<String> REGISTERED = new ArrayList<>();

    private RegisteredPayloads() {
    }

    public static <T extends CustomPacketPayload> void register(String id,
                                                                String version,
                                                                CustomPacketPayload.Type<T> type,
                                                                StreamCodec<FriendlyByteBuf, T> codec,
                                                                BiConsumer<T, PayloadContext> handler) {
        REGISTERED.add(id + ":" + version + ":" + type);
    }

    public static List<String> registered() {
        return REGISTERED;
    }
}
