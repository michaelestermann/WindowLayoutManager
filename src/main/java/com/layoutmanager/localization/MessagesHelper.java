package com.layoutmanager.localization;

import com.intellij.AbstractBundle;

import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessagesHelper {
    @NonNls
    public static final String BUNDLE_NAME = "com.layoutmanager.ui.messages";

    private static SoftReference<ResourceBundle> thisBundle;

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = thisBundle != null ? thisBundle.get() : null;
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            } catch (MissingResourceException e) {
                bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
            }
            thisBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) String key,
                                 @NotNull Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }
}
