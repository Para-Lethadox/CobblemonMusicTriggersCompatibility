package org.exlandia.cobblemonmusictriggerscompat

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.LogManager
import org.exlandia.cobblemonmusictriggerscompat.client.BattleClientHandler
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload

@Mod(Cobblemonmusictriggerscompat.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Cobblemonmusictriggerscompat.ID)
object Cobblemonmusictriggerscompat {
    const val ID = "cobblemonmusictriggerscompat"
    private val LOGGER = LogManager.getLogger(ID)

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Common setup — registering Cobblemon hooks")
        CobblemonHooks.register()
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Client setup")
    }

    @SubscribeEvent
    fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Dedicated server setup")
    }

    /**
     * Enregistrement des payloads NeoForge (phase Play).
     */
    @SubscribeEvent
    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Cobblemonmusictriggerscompat.ID).versioned("1")
        registrar.playToClient(
            BattleTriggerPayload.TYPE,
            BattleTriggerPayload.STREAM_CODEC
        ) { payload, ctx ->
            // handler côté client (main thread par défaut)
            org.exlandia.cobblemonmusictriggerscompat.client.BattleClientHandler.onBattleTrigger(payload)
        }
    }

}