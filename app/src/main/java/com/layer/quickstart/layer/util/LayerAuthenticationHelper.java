package com.layer.quickstart.layer.util;

import com.layer.sdk.LayerClient;

public interface LayerAuthenticationHelper {
    public void authenticate (final LayerClient layerClient, final String url, final String userId, final String nonce);
}
