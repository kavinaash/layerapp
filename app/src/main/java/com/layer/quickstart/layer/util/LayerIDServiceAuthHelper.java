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
 */
public final class LayerIDServiceAuthHelper implements LayerAuthenticationHelper {

    public void authenticate (final LayerClient layerClient, final String url, final String userId, final String nonce) {
        (new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpPost post = new HttpPost(url);
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("Accept", "application/json");

                    JSONObject json = new JSONObject()
                            .put("app_id", layerClient.getAppId())
                            .put("user_id", userId)
                            .put("nonce", nonce );
                    post.setEntity(new StringEntity(json.toString()));

                    HttpResponse response = (new DefaultHttpClient()).execute(post);
                    String eit = (new JSONObject(EntityUtils.toString(response.getEntity())))
                            .optString("identity_token");

                    layerClient.answerAuthenticationChallenge(eit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).execute();
    }

}
