package com.layoutmanager.ui.icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;


public class Icons {
    public static final class Menu {
        @NotNull
        public static final Icon CreateNewLayout = IconLoader.getIcon("com/layoutmanager/ui/icons/NewLayout.svg", Icons.class);

        @NotNull
        public static final Icon OverwriteLayout = IconLoader.getIcon("com/layoutmanager/ui/icons/OverwriteLayout.svg", Icons.class);

        @NotNull
        public static final Icon RestoreLayout = IconLoader.getIcon("com/layoutmanager/ui/icons/RestoreLayout.svg", Icons.class);

        @NotNull
        public static final Icon DeleteLayout = IconLoader.getIcon("com/layoutmanager/ui/icons/DeleteLayout.svg", Icons.class);
    }
}