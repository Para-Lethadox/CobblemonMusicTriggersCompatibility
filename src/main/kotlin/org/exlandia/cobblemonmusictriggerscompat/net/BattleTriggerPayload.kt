package org.exlandia.cobblemonmusictriggerscompat.net

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import org.exlandia.cobblemonmusictriggerscompat.Cobblemonmusictriggerscompat

data class BattleTriggerPayload(
    val identifier: String, // ex: "battle_start" ou "battle_end"
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<BattleTriggerPayload> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<BattleTriggerPayload>(
            ResourceLocation.fromNamespaceAndPath(
                Cobblemonmusictriggerscompat.ID,
                "battle_trigger"
            )
        )

        // IMPORTANT : codec sur FriendlyByteBuf (ou RegistryFriendlyByteBuf)
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, BattleTriggerPayload> =
            StreamCodec.of(
                { buf, payload -> buf.writeUtf(payload.identifier) },
                { buf -> BattleTriggerPayload(buf.readUtf()) }
            )
    }
}
