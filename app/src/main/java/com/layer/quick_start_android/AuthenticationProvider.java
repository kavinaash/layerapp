package com.layer.quick_start_android;

import android.app.Activity;

import com.layer.sdk.LayerClient;
import com.layer.sdk.listeners.LayerAuthenticationListener;

/**
 * Created by crypsis on 12/9/16.
 */
public interface AuthenticationProvider<TCredentials> extends LayerAuthenticationListener.BackgroundThread.Weak {
    /**
     * Sets this AuthenticationProvider's credentials.  Credentials should be cached to handle
     * future authentication challenges.  When `credentials` is `null`, the cached credentials
     * should be cleared.
     *
     * @param credentials Credentials to cache.
     * @return This AuthenticationProvider.
     */
    AuthenticationProvider<MyAuthenticationProvider.Credentials> setCredentials(MyAuthenticationProvider.Credentials credentials);


    /**
     * Returns `true` if this AuthenticationProvider has cached credentials, or `false` otherwise.
     *
     * @return `true` if this AuthenticationProvider has cached credentials, or `false` otherwise.
     */
    boolean hasCredentials();

    /**
     * Sets the authentication callback for reporting authentication success and failure.
     *
     * @param callback Callback to receive authentication success and failure.
     * @return This AuthenticationProvider.
     */
    AuthenticationProvider<MyAuthenticationProvider.Credentials> setCallback(Callback callback);

    /**
     * Routes the user to a login screen if required.  If routing, return `true` and start the
     * desired login Activity.
     *
     * @param layerClient
     * @param layerAppId
     * @param from
     * @return
     */
    boolean routeLogin(LayerClient layerClient, String layerAppId, Activity from);

    /**
     * Callback for handling authentication success and failure.
     */
    interface Callback {
        void onSuccess(AuthenticationProvider provider, String userId);

        void onError(AuthenticationProvider provider, String error);
    }
}
