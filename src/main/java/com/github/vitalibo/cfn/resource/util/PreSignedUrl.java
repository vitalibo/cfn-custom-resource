package com.github.vitalibo.cfn.resource.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitalibo.cfn.resource.model.ResourceProvisionResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@AllArgsConstructor
public class PreSignedUrl {

    private final ObjectMapper jackson;
    private final URL responseUrl;

    public PreSignedUrl(ObjectMapper jackson, String responseUrl) throws MalformedURLException {
        this(jackson, new URL(responseUrl));
    }

    public void upload(ResourceProvisionResponse response) throws IOException {
        upload((HttpURLConnection) responseUrl.openConnection(), jackson.writeValueAsString(response));
    }

    void upload(HttpURLConnection connection, String response) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(
            connection.getOutputStream());
        out.write(response);
        out.close();

        if (connection.getResponseCode() / 100 != 2) {
            throw new IOException("Upload response failed with status: " + connection.getResponseMessage());
        }
    }

}