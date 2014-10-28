package com.layer.quickstart.layer.util;

import android.os.AsyncTask;

import com.layer.sdk.LayerClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * This code only works with the Layer Identity Service and should only be used in your Layer staging environment
 * The Layer Identity Service can only be used for authentication with apps that are in Staging
 * You will need to implement your own backend to generate identity tokens
 */
public final class LayerStagingAuthenticationAdapter {
    private final static String STAGING_IDENTITY_SERVICE_URL = "https://layer-identity-provider.herokuapp.com/identity_tokens";

    public static void authenticate (final LayerClient layerClient, final String userId, final String nonce) {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpPost post = new HttpPost(STAGING_IDENTITY_SERVICE_URL);
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");

                    System.out.println("Nonce 2 is: " + nonce);
                    JSONObject json = new JSONObject()
                            .put("app_id", layerClient.getAppId())
                            .put("user_id", userId)
                            .put("nonce", nonce );
                    post.setEntity(new StringEntity(json.toString()));

                    HttpResponse response = (new DefaultHttpClient()).execute(post);
                    String eit = (new JSONObject(EntityUtils.toString(response.getEntity())))
                            .optString("identity_token");

                    System.out.println(eit);

                    layerClient.answerAuthenticationChallenge(eit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute();
    }

}
