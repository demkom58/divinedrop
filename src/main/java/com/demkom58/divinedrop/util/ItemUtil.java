package com.demkom58.divinedrop.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class ItemUtil {
    private static final String NO_PICKUP_METADATA = "no_pickup";

    /**
     * {@code Entity#setSneaking(boolean)}, or null below 1.20, where the
     * method is declared on {@link org.bukkit.entity.HumanEntity} only.
     */
    @Nullable
    private static final MethodHandle SET_SNEAKING = findSetSneaking();

    private ItemUtil() {
    }

    public static boolean hasNoPickupFlag(Item item) {
        return item.getPickupDelay() == Short.MAX_VALUE || item.hasMetadata(NO_PICKUP_METADATA);
    }

    /**
     * Tells whether name tag occlusion can be applied on this server.
     *
     * @return true if the sneak flag is reachable through the API.
     */
    public static boolean supportsDiscrete() {
        return SET_SNEAKING != null;
    }

    /**
     * Marks an entity as discrete, which is the sneak flag under another name.
     *
     * <p>The client picks the name tag font mode from {@code Entity#isDiscrete()}:
     * a discrete entity gets a depth tested tag, everything else gets a
     * see-through one. Flagging a dropped item therefore stops its tag from
     * being drawn through walls and terrain. The item itself is left alone,
     * since items have no sneaking pose or animation.
     *
     * @param entity   - entity to flag.
     * @param discrete - true to keep its name tag behind blocks.
     *
     * @return true if the flag was applied.
     */
    public static boolean setDiscrete(@NotNull final Entity entity, final boolean discrete) {
        if (SET_SNEAKING == null)
            return false;

        try {
            SET_SNEAKING.invoke(entity, discrete);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Nullable
    private static MethodHandle findSetSneaking() {
        try {
            return MethodHandles.lookup().findVirtual(
                    Entity.class,
                    "setSneaking",
                    MethodType.methodType(void.class, boolean.class)
            );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }
}
