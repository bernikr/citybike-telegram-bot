package com.kralofsky.citybikes.citybikeAPI.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private Map<String, String> cookies = new HashMap<>();

    public Document load(String url) throws IOException {
        return load(url, Connection.Method.GET, Collections.emptyMap());
    }

    public Document load(String url, Connection.Method method, Map<String, String> data) throws IOException {
        Connection.Response res = Jsoup.connect(url)
                .cookies(cookies)
                .method(method)
                .data(data)
                .execute();
        cookies.putAll(res.cookies());
        return res.parse();
    }
}
