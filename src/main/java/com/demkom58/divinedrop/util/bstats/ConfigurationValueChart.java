package com.demkom58.divinedrop.util.bstats;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationValueChart extends Metrics.SimplePie {

    private final JavaPlugin instance;

    /**
     * Class constructor.
     *
     * @param chartId  The id of the chart.
     */
    public ConfigurationValueChart(final JavaPlugin instance, String chartId, final String key) {
        super(chartId, () -> {
            final Configuration configuration = instance.getConfig();

            if (configuration.isBoolean(key))
                return configuration.getBoolean(key) ? "Enabled" : "Disabled";
            else
                return configuration.getString(key).toLowerCase();
        });

        this.instance = instance;
    }
}
