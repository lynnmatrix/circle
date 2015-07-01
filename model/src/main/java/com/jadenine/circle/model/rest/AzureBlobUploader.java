package com.jadenine.circle.model.rest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by linym on 7/1/15.
 */
public class AzureBlobUploader {
    // upload file to azure blob storage
    public static Boolean upload(String sasUrl, InputStream inputStream, String mimeType) {
        try {
            if(null == mimeType) {
                mimeType = "application/octet-stream";
            }
            int bytesRead;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            inputStream.close();

            byte[] bytes = bos.toByteArray();
            // Post our image data (byte array) to the server
            URL url = new URL(sasUrl.replace("\"", ""));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.addRequestProperty("Content-Type", mimeType);
            urlConnection.setRequestProperty("Content-Length", "" + bytes.length);
            urlConnection.setRequestProperty("x-ms-blob-type", "BlockBlob");
            // Write file data to server
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.write(bytes);
            wr.flush();
            wr.close();
            int response = urlConnection.getResponseCode();
            if (response == 201 && urlConnection.getResponseMessage().equals("Created")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

