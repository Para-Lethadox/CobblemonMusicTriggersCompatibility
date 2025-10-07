package org.exlandia.cobblemonmusictriggerscompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exlandia.cobblemonmusictriggerscompat.net.BattleTriggerPayload;

public final class CobblemonHooks {
    private static final Logger LOGGER = LogManager.getLogger(CobblemonHooks.class);
    private static final String EVENTS_CLASS = "com.cobblemon.mod.common.api.events.CobblemonEvents";

    private CobblemonHooks() {
    }

    public static void register() {
        Class<?> eventsClass = loadClass(EVENTS_CLASS);
        if (eventsClass == null) {
            LOGGER.warn("Cobblemon events class not present; battle music triggers disabled");
            return;
        }

        Object eventsInstance = resolveInstance(eventsClass);

        boolean startHooked = subscribeFirst(eventsClass, eventsInstance,
                new String[]{"BATTLE_STARTED_PRE", "BATTLE_STARTED", "BATTLE_STARTED_POST", "BATTLE_START", "BATTLE_START_POST", "BATTLE_BEGIN", "BATTLE_OPENED"},
                "battle_start");

        boolean anyEndHooked = subscribeAll(eventsClass, eventsInstance,
                Arrays.asList(
                        new String[]{"BATTLE_VICTORY", "BATTLE_WON", "BATTLE_WIN", "BATTLE_SUCCESS", "BATTLE_COMPLETE", "BATTLE_COMPLETED", "BATTLE_COMPLETED_POST", "BATTLE_FINISHED", "BATTLE_FINISHED_POST", "BATTLE_ENDED", "BATTLE_ENDED_POST"},
                        new String[]{"BATTLE_DEFEAT", "BATTLE_LOSS", "BATTLE_LOST"},
                        new String[]{"BATTLE_CONCLUDED", "BATTLE_CONCLUDED_POST", "BATTLE_TERMINATED", "BATTLE_STOPPED", "BATTLE_STOPPED_POST"},
                        new String[]{"BATTLE_FLED", "BATTLE_FORFEIT", "BATTLE_ABORTED", "BATTLE_CANCELLED", "BATTLE_CANCELLED_PRE", "BATTLE_CANCELLED_POST"}
                ),
                "battle_end");

        if (!startHooked) {
            LOGGER.warn("Unable to locate any Cobblemon battle start event hook; start trigger disabled");
        }

        if (!anyEndHooked) {
            LOGGER.warn("Unable to locate any Cobblemon battle end event hook; end trigger disabled");
        }
    }

    private static boolean subscribeFirst(Class<?> eventsClass, Object eventsInstance, String[] candidateNames, String triggerId) {
        for (String candidate : candidateNames) {
            if (subscribe(eventsClass, eventsInstance, candidate, triggerId)) {
                LOGGER.info("Subscribed to Cobblemon battle start hook '{}'", candidate);
                return true;
            }
        }
        return false;
    }

    private static boolean subscribeAll(Class<?> eventsClass, Object eventsInstance, List<String[]> candidateGroups, String triggerId) {
        boolean any = false;
        for (String[] group : candidateGroups) {
            for (String candidate : group) {
                if (subscribe(eventsClass, eventsInstance, candidate, triggerId)) {
                    LOGGER.info("Subscribed to Cobblemon battle end hook '{}'", candidate);
                    any = true;
                }
            }
        }
        return any;
    }

    private static boolean subscribe(Class<?> eventsClass, Object eventsInstance, String memberName, String triggerId) {
        Object hook = resolveHook(eventsClass, eventsInstance, memberName);
        if (hook == null) {
            return false;
        }

        Method subscribeMethod = findSubscriptionMethod(hook.getClass());
        if (subscribeMethod == null) {
            LOGGER.debug("Cobblemon hook '{}' does not expose a compatible subscribe method", memberName);
            return false;
        }

        Class<?> parameterType = subscribeMethod.getParameterTypes()[0];
        Object listener = createListener(parameterType, triggerId);
        if (listener == null) {
            LOGGER.warn("Unsupported listener type '{}' for Cobblemon hook '{}'", parameterType.getName(), memberName);
            return false;
        }

        try {
            subscribeMethod.setAccessible(true);
            subscribeMethod.invoke(hook, listener);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.warn("Failed to subscribe to Cobblemon hook '{}': {}", memberName, e.toString());
            LOGGER.debug("Exception while subscribing", e);
            return false;
        }
    }

    private static Object resolveHook(Class<?> eventsClass, Object eventsInstance, String identifier) {
        List<String> candidates = buildMemberCandidates(identifier);
        for (String candidate : candidates) {
            Field field = findField(eventsClass, candidate);
            if (field != null) {
                try {
                    Object target = Modifier.isStatic(field.getModifiers()) ? null : eventsInstance;
                    if (target == null && !Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object value = field.get(target);
                    if (value != null) {
                        return value;
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.debug("Unable to access Cobblemon field '{}': {}", candidate, e.toString());
                }
            }

            Method method = findZeroArgMethod(eventsClass, candidate);
            if (method != null) {
                try {
                    Object target = Modifier.isStatic(method.getModifiers()) ? null : eventsInstance;
                    if (target == null && !Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    method.setAccessible(true);
                    Object value = method.invoke(target);
                    if (value != null) {
                        return value;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOGGER.debug("Unable to invoke Cobblemon method '{}': {}", candidate, e.toString());
                }
            }
        }
        return null;
    }

    private static List<String> buildMemberCandidates(String identifier) {
        List<String> names = new ArrayList<>();
        names.add(identifier);

        String camel = toCamelCase(identifier);
        String lowerCamel = toLowerCamelCase(camel);

        names.add("get" + identifier);
        names.add(identifier.toLowerCase(Locale.ROOT));
        names.add(camel);
        names.add(lowerCamel);
        names.add("get" + camel);
        names.add("get" + upperFirst(lowerCamel));

        return names;
    }

    private static Field findField(Class<?> type, String name) {
        try {
            return type.getField(name);
        } catch (NoSuchFieldException ignored) {
        }

        try {
            Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
        }

        return null;
    }

    private static Method findZeroArgMethod(Class<?> type, String name) {
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() == 0 && method.getName().equals(name)) {
                return method;
            }
        }

        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterCount() == 0 && method.getName().equals(name)) {
                method.setAccessible(true);
                return method;
            }
        }

        return null;
    }

    private static Method findSubscriptionMethod(Class<?> hookClass) {
        List<String> candidateNames = Arrays.asList("subscribe", "register", "listen", "addListener");
        for (Method method : hookClass.getMethods()) {
            if (method.getParameterCount() == 1 && candidateNames.contains(method.getName())) {
                return method;
            }
        }

        for (Method method : hookClass.getDeclaredMethods()) {
            if (method.getParameterCount() == 1 && candidateNames.contains(method.getName())) {
                method.setAccessible(true);
                return method;
            }
        }

        return null;
    }

    private static Object createListener(Class<?> parameterType, String triggerId) {
        if (Consumer.class.isAssignableFrom(parameterType)) {
            @SuppressWarnings("unchecked")
            Consumer<Object> consumer = event -> sendAll(new BattleTriggerPayload(triggerId));
            return consumer;
        }

        if (Objects.equals(parameterType.getName(), "kotlin.jvm.functions.Function1")) {
            InvocationHandler handler = (proxy, method, args) -> {
                String name = method.getName();
                if ("invoke".equals(name) && args != null && args.length == 1) {
                    sendAll(new BattleTriggerPayload(triggerId));
                    return null;
                }
                if ("getArity".equals(name)) {
                    return 1;
                }
                return defaultValue(method.getReturnType());
            };
            return Proxy.newProxyInstance(parameterType.getClassLoader(), new Class<?>[]{parameterType}, handler);
        }

        return null;
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == float.class) {
            return 0f;
        }
        if (returnType == double.class) {
            return 0d;
        }
        if (returnType == char.class) {
            return (char) 0;
        }
        return null;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Missing Cobblemon class '{}': {}", className, e.toString());
            LOGGER.debug("Class loading failure", e);
            return null;
        }
    }

    private static Object resolveInstance(Class<?> eventsClass) {
        Object instance = null;
        try {
            Field instanceField = eventsClass.getField("INSTANCE");
            instanceField.setAccessible(true);
            instance = instanceField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        if (instance != null) {
            return instance;
        }

        try {
            return eventsClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ignored) {
        }

        return null;
    }

    private static String toCamelCase(String identifier) {
        StringBuilder builder = new StringBuilder();
        for (String part : identifier.split("[_ ]+")) {
            if (part.isEmpty()) {
                continue;
            }
            builder.append(upperFirst(part.toLowerCase(Locale.ROOT)));
        }
        return builder.toString();
    }

    private static String toLowerCamelCase(String camelCase) {
        if (camelCase.isEmpty()) {
            return camelCase;
        }
        return Character.toLowerCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    private static String upperFirst(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static void sendAll(BattleTriggerPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }
}
