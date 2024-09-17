package com.layoutmanager.ui.settings;

import blazing.chain.LZSEncoding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.layoutmanager.persistence.Layout;

public class LayoutSerializer {
    public String serialize(Layout layout) {
        Gson gson = new GsonBuilder().create();
        String jsonContent = gson.toJson(layout);
        return LZSEncoding.compressToBase64(jsonContent);
    }

    public Layout deserialize(String encodedContent) {
        String jsonContent = LZSEncoding.decompressFromBase64(encodedContent);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonContent, Layout.class);
    }
}
