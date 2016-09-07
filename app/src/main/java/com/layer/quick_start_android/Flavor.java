package com.layer.quick_start_android;

import android.content.Context;

import com.layer.atlas.provider.ParticipantProvider;
import com.layer.sdk.LayerClient;

/**
 * Created by crypsis on 6/9/16.
 */
public class Flavor implements MainActivity.Flavor {
    @Override
    public ParticipantProvider generateParticipantProvider(Context context, LayerClient layerClient) {
        return new MyParticipantProvider(context,layerClient).setConversation();
    }
}
