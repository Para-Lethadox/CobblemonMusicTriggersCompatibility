package org.exlandia.cobblemonmusictriggerscompat.net;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.BiConsumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exlandia.cobblemonmusictriggerscompat.Cobblemonmusictriggerscompat;

/**
 * Utility that mirrors NeoForge's {@code PayloadRegistrar#playToClient} API at runtime via reflection.
 * <p>
 * The project compiles against lightweight stubs of the networking classes, but those stubs can drift
 * from the actual runtime signatures exposed by Minecraft or NeoForge. By resolving the registration
 * method reflectively we can support a range of API revisions without bundling duplicate copies of
 * the networking classes inside the mod jar.
 */
public final class PayloadRegistrationHelper {
    private static final Logger LOGGER = LogManager.getLogger(Cobblemonmusictriggerscompat.ID + "/network");

    private static final Method PLAY_TO_CLIENT;
    private static final Class<?> HANDLER_TYPE;
    private static final Method HANDLER_METHOD;

    static {
        Method target = null;
        Class<?> handlerType = null;
        Method handlerMethod = null;

        for (Method method : PayloadRegistrar.class.getMethods()) {
            if (!"playToClient".equals(method.getName())) {
                continue;
            }

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 3) {
                continue;
            }

            if (!CustomPacketPayload.Type.class.isAssignableFrom(params[0])) {
                continue;
            }
            if (!StreamCodec.class.isAssignableFrom(params[1])) {
                continue;
            }

            target = method;
            handlerType = params[2];
            handlerMethod = findFunctionalMethod(handlerType);
            break;
        }

        if (target != null) {
            target.setAccessible(true);
        } else {
            LOGGER.error("Unable to resolve PayloadRegistrar#playToClient via reflection; payloads will not be registered");
        }

        PLAY_TO_CLIENT = target;
        HANDLER_TYPE = handlerType;
        HANDLER_METHOD = handlerMethod;
    }

    private PayloadRegistrationHelper() {
    }

    /**
     * Registers a client-bound custom payload using the runtime networking API.
     */
    public static <T extends CustomPacketPayload> void registerPlayToClient(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<FriendlyByteBuf, T> codec,
            BiConsumer<T, Object> handler
    ) {
        Objects.requireNonNull(registrar, "registrar");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(handler, "handler");

        if (PLAY_TO_CLIENT == null) {
            throw new IllegalStateException("No compatible PayloadRegistrar#playToClient method found");
        }

        Object adapted = adaptHandler(handler);

        try {
            PLAY_TO_CLIENT.invoke(registrar, type, codec, adapted);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to invoke PayloadRegistrar#playToClient", ex);
        }
    }

    private static Method findFunctionalMethod(Class<?> handlerType) {
        if (handlerType == null || handlerType == BiConsumer.class) {
            return null;
        }

        Method found = null;
        for (Method method : handlerType.getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (method.isDefault()) {
                // default interface methods should not be the functional target
                continue;
            }

            if (found != null) {
                return null; // Not a SAM type.
            }

            found = method;
        }

        if (found != null) {
            found.setAccessible(true);
        }

        return found;
    }

    private static <T extends CustomPacketPayload> Object adaptHandler(BiConsumer<T, Object> handler) {
        if (HANDLER_TYPE == null || HANDLER_TYPE == BiConsumer.class || HANDLER_TYPE.isInstance(handler)) {
            return handler;
        }

        if (HANDLER_METHOD != null && HANDLER_METHOD.getParameterCount() >= 1) {
            return Proxy.newProxyInstance(
                    HANDLER_TYPE.getClassLoader(),
                    new Class<?>[]{HANDLER_TYPE},
                    new HandlerProxy<>(handler, HANDLER_METHOD)
            );
        }

        throw new IllegalStateException("Unsupported payload handler parameter type: " + HANDLER_TYPE.getName());
    }

    private static final class HandlerProxy<T extends CustomPacketPayload> implements InvocationHandler {
        private final BiConsumer<T, Object> delegate;
        private final Method functional;

        HandlerProxy(BiConsumer<T, Object> delegate, Method functional) {
            this.delegate = delegate;
            this.functional = functional;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(functional)) {
                @SuppressWarnings("unchecked")
                T payload = (T) (args.length > 0 ? args[0] : null);
                Object context = args.length > 1 ? args[1] : null;
                delegate.accept(payload, context);
                return null;
            }

            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            if (method.isDefault()) {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), lookup);
                MethodHandle handle = privateLookup.unreflectSpecial(method, method.getDeclaringClass()).bindTo(proxy);
                return handle.invokeWithArguments(args);
            }

            throw new IllegalStateException("Unexpected invocation on payload handler proxy: " + method);
        }
    }
}
