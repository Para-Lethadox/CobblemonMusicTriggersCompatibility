package com.cobblemon.mod.common.api.events;

import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedPreEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;

public final class CobblemonEvents {
    public static final EventHook<BattleStartedPreEvent> BATTLE_STARTED_PRE = new EventHook<>();
    public static final EventHook<BattleVictoryEvent> BATTLE_VICTORY = new EventHook<>();
    public static final EventHook<BattleFledEvent> BATTLE_FLED = new EventHook<>();

    private CobblemonEvents() {
    }
}
