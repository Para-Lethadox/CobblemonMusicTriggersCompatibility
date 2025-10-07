package org.exlandia.cobblemonmusictriggerscompat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exlandia.cobblemonmusictriggerscompat.client.BattleClientHandler;
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload;

@Mod(Cobblemonmusictriggerscompat.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Cobblemonmusictriggerscompat.ID)
public final class Cobblemonmusictriggerscompat {
    public static final String ID = "cobblemonmusictriggerscompat";
    private static final Logger LOGGER = LogManager.getLogger(ID);

    public Cobblemonmusictriggerscompat() {
        LOGGER.info("Constructing Cobblemon Music Triggers Compat");
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Common setup — registering Cobblemon hooks");
        CobblemonHooks.register();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Client setup");
    }

    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Dedicated server setup");
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ID).versioned("1");
        registrar.playToClient(
                BattleTriggerPayload.TYPE,
                BattleTriggerPayload.STREAM_CODEC,
                (payload, context) -> BattleClientHandler.onBattleTrigger(payload)
        );
    }
}
