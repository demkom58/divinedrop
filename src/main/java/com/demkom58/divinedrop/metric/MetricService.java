package com.demkom58.divinedrop.metric;

import com.demkom58.divinedrop.DivineDrop;
import com.demkom58.divinedrop.util.bstats.ConfigurationValueChart;
import com.demkom58.divinedrop.util.bstats.Metrics;
import lombok.Getter;

@Getter
public class MetricService {

    interface MetricKeys {

        // config.yml => ${lang}
        String LANGUAGE = "config_language";

        // config.yml => ${pickup-items-on-sneak}
        String SHIFT_PICKUP = "config_shift_pickup";

        // config.yml => ${drop-cleaner.enabled}
        String DROP_REMOVER = "config_drop_remover";

        // config.yml => ${drop-cleaner.enable-custom-countdowns}
        String CUSTOM_COUNTDOWNS = "config_custom_countdowns";

        // config.yml => ${save-player-dropped-items}
        String DEATH_DROP_SAVING = "config_death_drop_saving";

        // config.yml => ${timer-for-loaded-items}
        String TIMER_ON_LOAD = "config_timer_on_load";

    }

    private final DivineDrop instance;

    private final Metrics metrics;

    public MetricService(final DivineDrop instance){
        this.instance = instance;

        this.metrics = new Metrics(instance);
    }

    public void start(){
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.LANGUAGE, "lang"));
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.SHIFT_PICKUP, "pickup-items-on-sneak"));
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.DROP_REMOVER, "drop-cleaner.enabled"));
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.CUSTOM_COUNTDOWNS, "drop-cleaner.enable-custom-countdowns"));
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.DEATH_DROP_SAVING, "drop-cleaner.save-player-dropped-items"));
        metrics.addCustomChart(new ConfigurationValueChart(getInstance(), MetricKeys.TIMER_ON_LOAD, "drop-cleaner.timer-for-loaded-items"));

        metrics.setup();
    }
}
