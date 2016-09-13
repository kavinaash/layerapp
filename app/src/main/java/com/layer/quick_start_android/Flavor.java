package com.layer.quick_start_android;

import android.content.Context;

import com.layer.atlas.provider.ParticipantProvider;

/**
 * Created by crypsis on 6/9/16.
 */
public class Flavor implements MainActivity.Flavor {
    @Override
    public ParticipantProvider generateParticipantProvider(Context context, AuthenticationProvider authenticationProvider) {

        return new MyParticipantProvider(context).setAuthenticationProvider(authenticationProvider);
    }



    @Override
    public AuthenticationProvider generateAuthenticationProvider(Context context) {
        return new MyAuthenticationProvider(context);
    }
}
