package org.exlandia.cobblemonmusictriggerscompat.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.exlandia.cobblemonmusictriggerscompat.Cobblemonmusictriggerscompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class BattleTriggerPayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BattleTriggerPayload> TYPE = createType();

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

    @SuppressWarnings("unchecked")
    private static CustomPacketPayload.Type<BattleTriggerPayload> createType() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                Cobblemonmusictriggerscompat.ID,
                "battle_trigger"
        );

        Class<CustomPacketPayload.Type> typeClass = (Class<CustomPacketPayload.Type>) (Class<?>) CustomPacketPayload.Type.class;
        try {
            Constructor<CustomPacketPayload.Type> constructor;
            try {
                constructor = typeClass.getDeclaredConstructor(ResourceLocation.class);
            } catch (NoSuchMethodException missing) {
                constructor = typeClass.getDeclaredConstructor(Object.class);
            }

            constructor.setAccessible(true);
            return (CustomPacketPayload.Type<BattleTriggerPayload>) constructor.newInstance(id);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new IllegalStateException("Unable to construct CustomPacketPayload.Type", ex);
        }
    }
}
