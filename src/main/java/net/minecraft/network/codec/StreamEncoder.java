package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamEncoder<B, T> {
    void encode(B buffer, T value);
}
