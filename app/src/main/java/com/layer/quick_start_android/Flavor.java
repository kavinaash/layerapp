package com.layer.quick_start_android;

import android.content.Context;

import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;

/**
 * Created by crypsis on 6/9/16.
 */
public class Flavor implements App.Flavor {


    public final static String LAYER_APP_ID = "layer:///apps/staging/170be0e8-6846-11e6-a7a9-d9a4c50e244c";

    // Set your Google Cloud Messaging Sender ID from your Google Developers Console.
    private final static String GCM_SENDER_ID = "24320527281";

    @Override
    public String getLayerAppId() {
        return (LAYER_APP_ID != null) ? LAYER_APP_ID : null;
    }

    @Override
    public LayerClient generateLayerClient(Context context, LayerClient.Options options) {
        String layerAppId = getLayerAppId();
        options=new LayerClient.Options();
        if (layerAppId == null) {
            if (Log.isLoggable(Log.ERROR)) Log.e(context.getString(R.string.app_id_required));
            return null;
        }
        if (GCM_SENDER_ID != null) options.googleCloudMessagingSenderId(GCM_SENDER_ID);
//        CustomEndpoint.setLayerClientOptions(options);
        return LayerClient.newInstance(context, layerAppId, options);
    }

    @Override
    public AuthenticationProvider generateAuthenticationProvider(Context context) {
        return new MyAuthenticationProvider(context);
    }
}
