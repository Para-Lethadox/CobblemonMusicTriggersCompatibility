package org.exlandia.cobblemonmusictriggerscompat

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.server.ServerLifecycleHooks
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload

object CobblemonHooks {

    fun register() {
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe { _: BattleStartedPreEvent ->
            sendAll(BattleTriggerPayload("battle_start"))
        }
        CobblemonEvents.BATTLE_VICTORY.subscribe { _: BattleVictoryEvent ->
            sendAll(BattleTriggerPayload("battle_end"))
        }
        CobblemonEvents.BATTLE_FLED.subscribe { _: BattleFledEvent ->
            sendAll(BattleTriggerPayload("battle_end"))
        }
    }

    private fun sendAll(payload: BattleTriggerPayload) {
        PacketDistributor.sendToAllPlayers(payload) // utilitaire NeoForge
    }
}
