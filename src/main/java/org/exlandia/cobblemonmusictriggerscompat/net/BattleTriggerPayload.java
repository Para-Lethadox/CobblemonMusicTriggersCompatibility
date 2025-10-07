package org.exlandia.cobblemonmusictriggerscompat.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.exlandia.cobblemonmusictriggerscompat.Cobblemonmusictriggerscompat;

public final class BattleTriggerPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BattleTriggerPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Cobblemonmusictriggerscompat.ID,
                            "battle_trigger"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, BattleTriggerPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeUtf(payload.identifier),
                    buf -> new BattleTriggerPayload(buf.readUtf())
            );

    private final String identifier;

    public BattleTriggerPayload(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    @Override
    public CustomPacketPayload.Type<BattleTriggerPayload> type() {
        return TYPE;
    }
}
