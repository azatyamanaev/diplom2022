package ru.itis.glabplugin.utils;

import com.google.common.base.Strings;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;

/**
 * 13.06.2022
 *
 * @author Azat Yamanaev
 */
public class UrlOpener {

    private static final Logger logger = Logger.getInstance(UrlOpener.class);

    public static void openUrl(String url) {
        logger.debug("Opening default browser for ", url);
        com.intellij.ide.BrowserUtil.browse(url);
    }
}
