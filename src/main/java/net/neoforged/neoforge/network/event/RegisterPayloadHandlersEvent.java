package net.neoforged.neoforge.network.event;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RegisterPayloadHandlersEvent {
    public PayloadRegistrar registrar(String id) {
        return new PayloadRegistrar(id);
    }
}
