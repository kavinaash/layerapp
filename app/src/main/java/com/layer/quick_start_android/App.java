package com.layer.quick_start_android;

import android.app.Application;
import android.content.Context;

import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.util.Util;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/**
 * Created by crypsis on 19/9/16.
 */
public class App extends Application {
    private static Context sInstance;
    private static Flavor sFlavor = new com.layer.quick_start_android.Flavor();

    private static LayerClient sLayerClient;
    private static AuthenticationProvider sAuthProvider;
    private static Picasso sPicasso;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = getApplicationContext();
        // Enable verbose logging in debug builds
        if (BuildConfig.DEBUG) {
            com.layer.atlas.util.Log.setLoggingEnabled(true);
            LayerClient.setLoggingEnabled(this, true);
        }

        // Allow the LayerClient to track app state
        LayerClient.applicationCreated(this);

    }

    public static Context getInstance() {
        return sInstance;
    }
    /**
     * Authenticates with the AuthenticationProvider and Layer, returning asynchronous results to
     * the provided callback.
     *
     * @param credentials Credentials associated with the current AuthenticationProvider.
     * @param callback    Callback to receive authentication results.
     */
    @SuppressWarnings("unchecked")
    public static void authenticate(MyAuthenticationProvider.Credentials credentials, AuthenticationProvider.Callback callback) {
        LayerClient client = getLayerClient();
        if (client == null) return;
        String layerAppId = getLayerAppId();
        if (layerAppId == null) return;
        getAuthenticationProvider()
                .setCredentials(credentials)
                .setCallback(callback);
        client.authenticate();
    }

    /**
     * Deauthenticates with Layer and clears cached AuthenticationProvider credentials.
     *
     * @param callback Callback to receive deauthentication success and failure.
     */
    public static void deauthenticate(final Util.DeauthenticationCallback callback) {
        Util.deauthenticate(getLayerClient(), new Util.DeauthenticationCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDeauthenticationSuccess(LayerClient client) {
                getAuthenticationProvider().setCredentials(null);
                callback.onDeauthenticationSuccess(client);
            }

            @Override
            public void onDeauthenticationFailed(LayerClient client, String reason) {
                callback.onDeauthenticationFailed(client, reason);
            }
        });
    }


    //==============================================================================================
    // Getters / Setters
    //==============================================================================================

    /**
     * Gets or creates a LayerClient, using a default set of LayerClient.Options and flavor-specific
     * App ID and Options from the `generateLayerClient` method.  Returns `null` if the flavor was
     * unable to create a LayerClient (due to no App ID, etc.).
     *
     * @return New or existing LayerClient, or `null` if a LayerClient could not be constructed.
     * @see Flavor#generateLayerClient(Context, LayerClient.Options)
     */
    public static LayerClient getLayerClient() {
        if (sLayerClient == null) {
            // Custom options for constructing a LayerClient
            LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                    .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                    .autoDownloadMimeTypes(Arrays.asList(
                            TextCellFactory.MIME_TYPE,
                            ThreePartImageUtils.MIME_TYPE_INFO,
                            ThreePartImageUtils.MIME_TYPE_PREVIEW));

            // Allow flavor to specify Layer App ID and customize Options.
            sLayerClient = sFlavor.generateLayerClient(sInstance, options);

            // Flavor was unable to generate Layer Client (no App ID, etc.)
            if (sLayerClient == null) return null;

            /* Register AuthenticationProvider for handling authentication challenges */
            sLayerClient.registerAuthenticationListener(getAuthenticationProvider());
        }
        return sLayerClient;
    }

    public static String getLayerAppId() {
        return sFlavor.getLayerAppId();
    }

    public static AuthenticationProvider getAuthenticationProvider() {
        if (sAuthProvider == null) {
            sAuthProvider = sFlavor.generateAuthenticationProvider(sInstance);

            // If we have cached credentials, try authenticating with Layer
            LayerClient layerClient = getLayerClient();
            if (layerClient != null && sAuthProvider.hasCredentials()) layerClient.authenticate();
        }
        return sAuthProvider;
    }


    public static Picasso getPicasso() {
        if (sPicasso == null) {
            // Picasso with custom RequestHandler for loading from Layer MessageParts.
            sPicasso = new Picasso.Builder(sInstance)
                    .addRequestHandler(new MessagePartRequestHandler(getLayerClient()))
                    .build();
        }
        return sPicasso;
    }

    /**
     * Flavor is used by Atlas Messenger to switch environments.
     *
//     * @see com.layer.messenger.flavor.Flavor
     */
    public interface Flavor {
        String getLayerAppId();

        LayerClient generateLayerClient(Context context, LayerClient.Options options);

        AuthenticationProvider generateAuthenticationProvider(Context context);
    }
}
