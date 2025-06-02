package com.h5base;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

public class LocalWebServer extends NanoHTTPD {

    private final Context context;
    private final String rootFolder;

    public LocalWebServer(Context context, int port, String rootFolder) {
        super(port);
        this.context = context;
        this.rootFolder = rootFolder; // Ví dụ: "1_Defense"
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        // Nếu vào trang gốc thì load index.html
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        String assetPath = rootFolder + uri;

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(assetPath);
            String mime = getMimeTypeForFile(uri);

            return newFixedLengthResponse(Response.Status.OK, mime, inputStream, inputStream.available());

        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found: " + uri);
        }
    }
}
