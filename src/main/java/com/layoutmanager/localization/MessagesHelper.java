package com.layoutmanager.localization;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;

import java.lang.ref.Reference;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessagesHelper {
    @NonNls
    public static final String BUNDLE_NAME = "com.layoutmanager.ui.messages";

    private static Reference<ResourceBundle> thisBundle;

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(thisBundle);
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
        return CommonBundle.message(getBundle(), key, params);
    }
}
