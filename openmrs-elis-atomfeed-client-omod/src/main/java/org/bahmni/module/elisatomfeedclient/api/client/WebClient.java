package org.bahmni.module.elisatomfeedclient.api.client;

import org.bahmni.module.elisatomfeedclient.api.FeedProperties;
import org.bahmni.module.elisatomfeedclient.api.exception.OpenElisFeedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

@Component
public class WebClient {

    private int connectTimeout = 10000;
    private int readTimeout = 20000;

    @Autowired
    public WebClient(FeedProperties properties) {
        this.connectTimeout = properties.getConnectTimeout();
        this.readTimeout = properties.getReadTimeout();
    }

    public String get(URI uri, Map<String, String> headers) {
        HttpURLConnection connection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
            connection.setDoOutput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            throw new OpenElisFeedException(e.getMessage(), e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return stringBuilder.toString();
    }

}
