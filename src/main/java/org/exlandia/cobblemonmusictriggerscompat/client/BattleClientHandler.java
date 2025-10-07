package org.exlandia.cobblemonmusictriggerscompat.client;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload;
import mods.thecomputerizer.musictriggers.api.channels.ChannelHelper;
import mods.thecomputerizer.theimpossiblelibrary.api.wrapper.WrapperHelper;

public final class BattleClientHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private BattleClientHandler() {
    }

    public static void onBattleTrigger(BattleTriggerPayload payload) {
        Minecraft.Player player = Minecraft.getInstance().getPlayer();
        if (player == null) {
            LOGGER.debug("No local player; dropping trigger {}", payload.identifier());
            return;
        }
        Object apiPlayer = WrapperHelper.getPlayerAPI(player);
        ChannelHelper.executeCommandTrigger(apiPlayer, payload.identifier());
        LOGGER.debug("MT command trigger sent: {}", payload.identifier());
    }
}
