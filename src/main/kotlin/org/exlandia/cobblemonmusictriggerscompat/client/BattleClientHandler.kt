package org.exlandia.cobblemonmusictriggerscompat.client

import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload

// API Music Triggers v7 :
import mods.thecomputerizer.musictriggers.api.channels.ChannelHelper
import mods.thecomputerizer.theimpossiblelibrary.api.wrapper.WrapperHelper

object BattleClientHandler {
    private val LOGGER = LogUtils.getLogger()

    fun onBattleTrigger(payload: BattleTriggerPayload) {
        val player = Minecraft.getInstance().player ?: run {
            LOGGER.debug("No local player; dropping trigger {}", payload.identifier)
            return
        }
        // Convertit le joueur MC en PlayerAPI de TIL, requis par l’API MT
        val apiPlayer = WrapperHelper.getPlayerAPI(player)

        // Déclenche le "command trigger" côté client
        ChannelHelper.executeCommandTrigger(apiPlayer, payload.identifier)
        LOGGER.debug("MT command trigger sent: {}", payload.identifier)
    }
}
