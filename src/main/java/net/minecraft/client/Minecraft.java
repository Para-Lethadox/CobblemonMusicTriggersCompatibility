package net.minecraft.client;

public final class Minecraft {
    private static final Minecraft INSTANCE = new Minecraft();
    private Player player;

    private Minecraft() {
    }

    public static Minecraft getInstance() {
        return INSTANCE;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static final class Player {
    }
}
