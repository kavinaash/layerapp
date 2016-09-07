package com.layer.quick_start_android;

import android.content.Intent;

import com.layer.atlas.AtlasConversationsRecyclerView;
import com.layer.atlas.adapters.AtlasConversationsAdapter;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;

/**
 * Created by crypsis on 26/8/16.
 */
public class ConversationsListActivity extends MainActivity
         {
    AtlasConversationsRecyclerView conversationsList;

    public ConversationsListActivity(MainActivity mainActivity,LayerClient layerClient) {
        mainActivity.setContentView(R.layout.conversation_list);
        conversationsList = ((AtlasConversationsRecyclerView)mainActivity.findViewById(R.id.conversations_list));


        conversationsList.init(layerClient,getParticipantProvider(),mainActivity.getPicasso());
        conversationsList.setOnConversationClickListener(new AtlasConversationsAdapter.OnConversationClickListener() {
            @Override
            public void onConversationClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                Intent intent = new Intent(ConversationsListActivity.this,MessagesListActivity.class);
                intent.putExtra("conversationId",conversation.getId());
                startActivity(intent);


            }

            @Override
            public boolean onConversationLongClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                return false;
            }
        });


    }










    }

