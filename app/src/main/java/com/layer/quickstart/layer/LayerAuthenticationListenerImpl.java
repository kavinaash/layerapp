package com.layer.quickstart.layer;

import com.layer.quickstart.layer.util.LayerStagingAuthenticationAdapter;
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
        final String mUserId = "ENTER_USER_ID_HERE";
        System.out.println("Nonce is: " + nonce);
        LayerStagingAuthenticationAdapter.authenticate(layerClient, mUserId, nonce);
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        System.out.println("There was an error authenticating");
    }

    @Override
    public void onDeauthenticated(LayerClient client) {

    }
}
