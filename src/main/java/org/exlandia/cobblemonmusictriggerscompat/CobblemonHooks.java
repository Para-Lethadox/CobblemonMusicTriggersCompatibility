package org.exlandia.cobblemonmusictriggerscompat;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload;

public final class CobblemonHooks {
    private CobblemonHooks() {
    }

    public static void register() {
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(event -> sendAll(new BattleTriggerPayload("battle_start")));
        CobblemonEvents.BATTLE_VICTORY.subscribe(event -> sendAll(new BattleTriggerPayload("battle_end")));
        CobblemonEvents.BATTLE_FLED.subscribe(event -> sendAll(new BattleTriggerPayload("battle_end")));
    }

    private static void sendAll(BattleTriggerPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }
}
