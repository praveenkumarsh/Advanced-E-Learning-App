package com.praveen.learningapp.Services_Module;

import java.io.*;
//import com.squareup.okhttp.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Translate {
    private static String subscriptionKey = "f24c0da385eb48d7810670763333f849";
    HttpUrl url = new HttpUrl.Builder()
            .scheme("https")
            .host("api.cognitive.microsofttranslator.com")
            .addPathSegment("/translate")
            .addQueryParameter("api-version", "3.0")
            .addQueryParameter("to", "th")
            .addQueryParameter("toScript", "latn")
            .build();

    // Instantiates the OkHttpClient.
    OkHttpClient client = new OkHttpClient();

    public Translate(){
        subscriptionKey = "f24c0da385eb48d7810670763333f849";
        url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.cognitive.microsofttranslator.com")
                .addPathSegment("/translate")
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("to", "hi")
                .addQueryParameter("toScript", "latn")
                .build();
        client = new OkHttpClient();
    }



    // This function performs a POST request.
    public String Post(String text) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\"Text\": \""+text+"\"}]");
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey).addHeader("Content-type", "application/json").addHeader("Ocp-Apim-Subscription-Region", "centralindia").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // This function prettifies the json response.
    public String prettify(String json_text) {

        json_text = json_text.substring(1,json_text.length()-1);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("translations");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject = jsonArray.getJSONObject(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Object trans = "Retry";

        try {
            trans = jsonObject.get("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trans.toString();
    }
}