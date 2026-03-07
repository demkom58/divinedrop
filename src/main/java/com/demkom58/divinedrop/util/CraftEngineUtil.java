package com.demkom58.divinedrop.util;

import com.demkom58.divinedrop.lang.Language;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class CraftEngineUtil {
    private static final String BUKKIT_ITEM_MANAGER_CLASS = "net.momirealms.craftengine.bukkit.item.BukkitItemManager";
    private static final String CRAFT_ENGINE_ITEM_CLASS = "net.momirealms.craftengine.core.item.Item";
    private static final String CRAFT_ENGINE_CORE_CLASS = "net.momirealms.craftengine.core.plugin.CraftEngine";

    private static boolean initialized = false;
    private static boolean available = false;

    private static Method managerInstanceMethod;
    private static Method wrapMethod;
    private static Method hoverNameJsonMethod;
    private static Method craftEngineInstanceMethod;
    private static Method translationManagerMethod;
    private static Method clientLangDataMethod;
    private static Method langDataTranslateMethod;

    private CraftEngineUtil() {
    }

    @Nullable
    public static String getDisplayName(@Nullable final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        initialize();
        if (!available) {
            return null;
        }

        try {
            final Object manager = managerInstanceMethod.invoke(null);
            if (manager == null) {
                return null;
            }

            final Object wrappedItem = wrapMethod.invoke(manager, itemStack);
            if (wrappedItem == null) {
                return null;
            }

            final Object optionalJson = hoverNameJsonMethod.invoke(wrappedItem);
            if (!(optionalJson instanceof Optional)) {
                return null;
            }

            final Object json = ((Optional<?>) optionalJson).orElse(null);
            if (!(json instanceof String)) {
                return null;
            }

            final String legacyText = jsonToLegacyText((String) json);
            return legacyText.isEmpty() ? null : legacyText;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            final Class<?> managerClass = Class.forName(BUKKIT_ITEM_MANAGER_CLASS);
            final Class<?> itemClass = Class.forName(CRAFT_ENGINE_ITEM_CLASS);
            final Class<?> craftEngineClass = Class.forName(CRAFT_ENGINE_CORE_CLASS);
            final Class<?> translationManagerClass = Class.forName("net.momirealms.craftengine.core.plugin.locale.TranslationManager");
            final Class<?> langDataClass = Class.forName("net.momirealms.craftengine.core.plugin.locale.LangData");

            managerInstanceMethod = managerClass.getMethod("instance");
            wrapMethod = managerClass.getMethod("wrap", ItemStack.class);
            hoverNameJsonMethod = itemClass.getMethod("hoverNameJson");
            craftEngineInstanceMethod = craftEngineClass.getMethod("instance");
            translationManagerMethod = craftEngineClass.getMethod("translationManager");
            clientLangDataMethod = translationManagerClass.getMethod("clientLangData");
            langDataTranslateMethod = langDataClass.getMethod("translate", String.class);
            available = true;
        } catch (Throwable ignored) {
            available = false;
        }

        initialized = true;
    }

    private static String jsonToLegacyText(final String json) {
        try {
            final BaseComponent[] components = ComponentSerializer.parse(json);
            final String translatedText = resolveTranslation(components);
            if (translatedText != null && !translatedText.isEmpty()) {
                return translatedText;
            }
            final String legacyText = TextComponent.toLegacyText(components);
            final String languageOverride = resolveLanguageOverride(legacyText);
            return languageOverride != null ? languageOverride : legacyText;
        } catch (Throwable ignored) {
            final String languageOverride = resolveLanguageOverride(json);
            return languageOverride != null ? languageOverride : json;
        }
    }

    @Nullable
    private static String resolveTranslation(final BaseComponent[] components) {
        if (components.length != 1 || !(components[0] instanceof TranslatableComponent)) {
            return null;
        }

        final String translateKey = ((TranslatableComponent) components[0]).getTranslate();
        if (translateKey == null || translateKey.isEmpty()) {
            return null;
        }

        final String languageOverride = resolveLanguageOverride(translateKey);
        if (languageOverride != null) {
            return languageOverride;
        }

        try {
            final Object craftEngine = craftEngineInstanceMethod.invoke(null);
            if (craftEngine == null) {
                return null;
            }

            final Object translationManager = translationManagerMethod.invoke(craftEngine);
            if (translationManager == null) {
                return null;
            }

            final Object langDataObject = clientLangDataMethod.invoke(translationManager);
            if (!(langDataObject instanceof Map)) {
                return null;
            }

            @SuppressWarnings("unchecked")
            final Map<String, Object> clientLangData = (Map<String, Object>) langDataObject;

            final String preferred = lookupTranslation(clientLangData, translateKey, "zh_cn");
            if (preferred != null) {
                return preferred;
            }

            final String systemLocale = Locale.getDefault().toString().toLowerCase(Locale.ENGLISH);
            final String systemLocaleTranslation = lookupTranslation(clientLangData, translateKey, systemLocale);
            if (systemLocaleTranslation != null) {
                return systemLocaleTranslation;
            }

            final String english = lookupTranslation(clientLangData, translateKey, "en_us");
            if (english != null) {
                return english;
            }

            for (Object langData : clientLangData.values()) {
                final String translated = translate(langData, translateKey);
                if (translated != null && !translated.isEmpty()) {
                    return translated;
                }
            }
        } catch (Throwable ignored) {
            return null;
        }

        return null;
    }

    @Nullable
    private static String resolveLanguageOverride(@Nullable final String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        final Language language = Language.getInstance();
        if (language == null || !language.contains(key)) {
            return null;
        }

        final String translated = language.getLocName(key);
        return translated != null && !translated.equals(key) ? translated : null;
    }

    @Nullable
    private static String lookupTranslation(final Map<String, Object> clientLangData,
                                            final String translateKey,
                                            final String localeKey) {
        final Object langData = clientLangData.get(localeKey);
        return translate(langData, translateKey);
    }

    @Nullable
    private static String translate(@Nullable final Object langData,
                                    final String translateKey) {
        if (langData == null) {
            return null;
        }

        try {
            final Object translated = langDataTranslateMethod.invoke(langData, translateKey);
            return translated instanceof String ? (String) translated : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
