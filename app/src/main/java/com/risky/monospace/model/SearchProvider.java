package com.risky.monospace.model;

public enum SearchProvider {
    GOOGLE("https://google.com/search?q="),
    BING("https://bing.com/search?q="),
    DUCK_DUCK_GO("https://duckduckgo.com/?q="),
    BRAVE("https://search.brave.com/search?q=");

    public final String baseUrl;

    SearchProvider(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static SearchProvider toProvider(int position) {
        return values()[position];
    }
}
