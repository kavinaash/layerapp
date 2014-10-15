package com.layer.quickstart.layer;

import android.os.AsyncTask;

import com.layer.quickstart.layer.util.LayerAuthenticationHelper;
import com.layer.quickstart.layer.util.LayerIDServiceAuthHelper;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;

/**
 * Sample implementation of LayerAuthenticationListener
 */
public class LayerAuthenticationListenerImpl implements LayerAuthenticationListener {

    @Override
    public void onAuthenticated(LayerClient client, String arg1) {
        System.out.println("Authentication successful");
    }

    @Override
    public void onAuthenticationChallenge(final LayerClient layerClient, final String nonce) {
        final String mUserId = "1234";

        //The Layer Identity Service can only be used for authentication with Staging apps
        //You will need to implement your own backend to generate identity tokens
        LayerAuthenticationHelper authHelper = new LayerIDServiceAuthHelper();
        authHelper.authenticate(layerClient, "https://layer-identity-provider.herokuapp.com/identity_tokens", mUserId, nonce);
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        System.out.println("There was an error authenticating");
    }

    @Override
    public void onDeauthenticated(LayerClient client) {

    }
}
