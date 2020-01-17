package com.demkom58.divinedrop.drop;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.config.ConfigData;
import com.demkom58.divinedrop.config.DataContainer;
import com.demkom58.divinedrop.config.StaticData;
import com.demkom58.divinedrop.util.DivineTimer;
import com.demkom58.divinedrop.version.VersionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemHandler {

    private final DivineDrop plugin;
    private final VersionManager versionManager;
    private final ConfigData data;

    @Getter
    private final ItemRegistry registry;

    private final DivineTimer itemTickTimer;

    public ItemHandler(@NotNull final DivineDrop plugin,
                       @NotNull final VersionManager versionManager,
                       @NotNull final ConfigData data) {
        this.plugin = plugin;
        this.versionManager = versionManager;
        this.data = data;

        this.registry = new ItemRegistry(plugin, data, this);

        final Set<Item> timedItems = registry.getTimedItems();
        this.itemTickTimer = new DivineTimer(registry, plugin, () -> timedItems.forEach(this::itemTick));
    }

    public void reload() {
        itemTickTimer.stop();

        if (!data.isCleanerEnabled()) {
            registry.getTimedItems().forEach(item -> item.removeMetadata(StaticData.METADATA_COUNTDOWN, plugin));
            registry.getDeathDropItems().clear();
            registry.getTimedItems().clear();
        } else itemTickTimer.start();

        Bukkit.getServer().getWorlds().forEach(world -> world.getEntities().stream()
                .filter(entity -> entity instanceof Item)
                .forEach(item -> registry.loadedItem((Item) item)));
    }

    public void disable() {
        this.itemTickTimer.stop();

        final Set<Item> timedItems = registry.getTimedItems();
        timedItems.forEach(item -> {
            item.setCustomName(null);
            item.setCustomNameVisible(false);
        });

        timedItems.clear();
    }

    public void itemTick(@NotNull final Item item) {
        List<MetadataValue> metaCountdowns = item.getMetadata(StaticData.METADATA_COUNTDOWN);

        if (metaCountdowns.isEmpty()) {
            setupMetaTimer(item);
            metaCountdowns = item.getMetadata(StaticData.METADATA_COUNTDOWN);
        }

        if (metaCountdowns.isEmpty())
            return;

        DataContainer container = (DataContainer) metaCountdowns.get(0).value();
        if (container == null)
            return;

        tickTimer(item, container);
    }

    public String getFormattedName(@NotNull final Item item) {
        return data.getFormat()
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item));
    }

    public String getDisplayName(@NotNull final Item item) {
        return versionManager.getVersion().getI18NDisplayName(item.getItemStack());
    }

    public void removeTimer(@NotNull final Item item) {
        item.setCustomName(data.getFormat()
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item))
        );

        registry.getTimedItems().remove(item);
        item.removeMetadata(StaticData.METADATA_COUNTDOWN, plugin);
    }

    public void tickTimer(@NotNull final Item item,
                          @NotNull final DataContainer container) {
        this.updateTimedItem(item, container);
        container.timerDecrement();
    }

    public void updateTimedItem(@NotNull final Item item,
                                @NotNull final DataContainer container) {
        if (container.getTimer() <= 0) {
            item.remove();
            registry.getTimedItems().remove(item);
        }

        if (container.getFormat() == null)
            container.setFormat("");

        item.setMetadata(StaticData.METADATA_COUNTDOWN, new FixedMetadataValue(plugin, container));
        item.setCustomName(container.getFormat()
                .replace(StaticData.TIMER_PLACEHOLDER, String.valueOf(container.getTimer()))
                .replace(StaticData.SIZE_PLACEHOLDER, String.valueOf(item.getItemStack().getAmount()))
                .replace(StaticData.NAME_PLACEHOLDER, getDisplayName(item))
        );
    }

    public void setupMetaTimer(@NotNull final Item item) {
        final ItemMeta meta = item.getItemStack().getItemMeta();
        final String name = meta != null
                ? meta.hasDisplayName() ? meta.getDisplayName() : ""
                : "";

        int timer = data.getTimerValue();
        String format = data.getCleanerFormat();
        Material material = item.getItemStack().getType();

        if (data.isEnableCustomCountdowns()) {
            final Map<String, DataContainer> filterMap = data.getCleanerCountdowns().get(material);

            if (filterMap != null) {
                final DataContainer specifiedContainer = filterMap.get(name);
                if (specifiedContainer != null) {
                    timer = specifiedContainer.getTimer();
                    format = specifiedContainer.getFormat();
                }

                final DataContainer voidContainer = filterMap.get("");
                if (voidContainer != null
                        && name.isEmpty()
                        && specifiedContainer == null) {
                    timer = voidContainer.getTimer();
                    format = voidContainer.getFormat();
                }

                final DataContainer dataContainer = filterMap.get("*");
                if (dataContainer != null
                        && (!name.isEmpty() || voidContainer == null)
                        && specifiedContainer == null) {

                    timer = dataContainer.getTimer();
                    format = dataContainer.getFormat();
                }
            }

        }

        final DataContainer container = new DataContainer(timer, format);
        if (data.isSavePlayerDeathDroppedItems()
                && registry.getDeathDropItems().contains(item.getItemStack())) {
            removeTimer(item);
            return;
        }

        if (timer == -1) {
            removeTimer(item);
            return;
        }

        updateTimedItem(item, container);
    }
}
