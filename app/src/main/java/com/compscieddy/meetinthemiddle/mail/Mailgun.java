package com.compscieddy.meetinthemiddle.mail;

import com.compscieddy.meetinthemiddle.util.Lawg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by elee on 7/1/16.
 */

public class Mailgun {

  private static final Lawg L = Lawg.newInstance(Mailgun.class.getSimpleName());

  // TODO - RETROFIT WITH MAILGUN HERE TOO
  // https://gist.github.com/hpsaturn/5fd39a4e7d6ffb156197

  public static void sendSimpleMessage() {
    OkHttpClient client = new OkHttpClient();

    String credential = Credentials.basic("api", "key-25708d0a9850dce0da550bf5a8f57017");

    RequestBody body = new FormBody.Builder()
        .add("from", "Excited User <mailgun@compscieddy.com>")
        .add("to", "eeddeellee@gmail.com")
        .add("subject", "Sent by Meet in the Middle (testing)")
        .add("text", "I thank you all for joining me on this journey to learn more Android together and create a product from scratch, together.")
        .build();

    Request request = new Request.Builder()
        .url("https://api.mailgun.net/v3/compscieddy.com/messages")
        .header("Authorization", credential)
        .post(body)
        .build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
          response.body().close();
          throw new IOException("Unexpected code " + response);
        } else {
          L.d("Call " + call + " response " + response);
        }

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
          L.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        String responseData = response.body().string();
        try {
          JSONObject json = new JSONObject(responseData);
        } catch (JSONException e) {
          L.e("JSONException " + e);
          e.printStackTrace();
        }

      }
    });

  }

}
