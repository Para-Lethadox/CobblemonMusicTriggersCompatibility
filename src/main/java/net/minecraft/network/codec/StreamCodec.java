package net.minecraft.network.codec;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class StreamCodec<B, T> {
    private final BiConsumer<B, T> writer;
    private final Function<B, T> reader;

    private StreamCodec(BiConsumer<B, T> writer, Function<B, T> reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public static <B, T> StreamCodec<B, T> of(BiConsumer<B, T> writer, Function<B, T> reader) {
        return new StreamCodec<>(Objects.requireNonNull(writer), Objects.requireNonNull(reader));
    }

    public void encode(B buffer, T value) {
        writer.accept(buffer, value);
    }

    public T decode(B buffer) {
        return reader.apply(buffer);
    }
}
