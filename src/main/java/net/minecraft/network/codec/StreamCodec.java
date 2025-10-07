package net.minecraft.network.codec;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Minimal stub of Minecraft's {@code StreamCodec} interface so the mod can compile without the
 * official game classes. The real runtime type is an interface with a static {@code of} factory,
 * so we mirror that structure to avoid {@link IncompatibleClassChangeError} when the genuine
 * classes are loaded.
 */
public interface StreamCodec<B, T> {

    void encode(B buffer, T value);

    T decode(B buffer);

    static <B, T> StreamCodec<B, T> of(BiConsumer<B, T> writer, Function<B, T> reader) {
        Objects.requireNonNull(writer, "writer");
        Objects.requireNonNull(reader, "reader");
        return new StreamCodec<>() {
            @Override
            public void encode(B buffer, T value) {
                writer.accept(buffer, value);
            }

            @Override
            public T decode(B buffer) {
                return reader.apply(buffer);
            }
        };
    }
}
