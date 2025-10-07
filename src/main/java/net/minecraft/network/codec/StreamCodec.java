package net.minecraft.network.codec;

import java.util.Objects;

/**
 * Minimal stub of Minecraft's {@code StreamCodec} interface so the mod can compile without the
 * official game classes. The real runtime type is an interface with a static {@code of} factory,
 * so we mirror that structure to avoid linkage errors when the genuine classes are loaded.
 */
public interface StreamCodec<B, T> extends StreamEncoder<B, T>, StreamDecoder<B, T> {

    static <B, T> StreamCodec<B, T> of(StreamEncoder<B, T> encoder, StreamDecoder<B, T> decoder) {
        Objects.requireNonNull(encoder, "encoder");
        Objects.requireNonNull(decoder, "decoder");
        return new StreamCodec<>() {
            @Override
            public void encode(B buffer, T value) {
                encoder.encode(buffer, value);
            }

            @Override
            public T decode(B buffer) {
                return decoder.decode(buffer);
            }
        };
    }
}
