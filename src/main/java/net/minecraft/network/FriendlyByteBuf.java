package net.minecraft.network;

public class FriendlyByteBuf {
    private String value;

    public void writeUtf(String text) {
        this.value = text;
    }

    public String readUtf() {
        return this.value == null ? "" : this.value;
    }
}
