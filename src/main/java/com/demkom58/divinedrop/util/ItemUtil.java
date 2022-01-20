package com.demkom58.divinedrop.util;

import org.bukkit.entity.Item;

public final class ItemUtil {
    private static final String NO_PICKUP_METADATA = "no_pickup";

    private ItemUtil() {
    }

    public static boolean hasNoPickupFlag(Item item) {
        return item.getPickupDelay() == Short.MAX_VALUE || item.hasMetadata(NO_PICKUP_METADATA);
    }
}
