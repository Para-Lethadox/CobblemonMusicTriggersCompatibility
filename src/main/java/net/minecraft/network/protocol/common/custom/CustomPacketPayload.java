package net.minecraft.network.protocol.common.custom;

public interface CustomPacketPayload {
    Type<? extends CustomPacketPayload> type();

    final class Type<T extends CustomPacketPayload> {
        private final Object identifier;

        public Type(Object identifier) {
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return String.valueOf(identifier);
        }
    }
}
