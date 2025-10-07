package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamDecoder<B, T> {
    T decode(B buffer);
}
