package com.layer.quick_start_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import com.layer.atlas.AtlasConversationsRecyclerView;

/**
 * Created by crypsis on 26/8/16.
 */
public class ConversationsListActivity extends MainActivity {
    AtlasConversationsRecyclerView conversationsList;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list);


//
//        conversationsList = (AtlasConversationsRecyclerView) findViewById(R.id.conversations_list);
//        conversationsList=new AtlasConversationsRecyclerView(ConversationsListActivity.this);
//        conversationsList.init(App.getLayerClient(),App.getPicasso());
//        conversationsList.setOnConversationClickListener(new AtlasConversationsAdapter.OnConversationClickListener() {
//            @Override
//            public void onConversationClick(AtlasConversationsAdapter adapter, Conversation conversation) {
//                Intent intent = new Intent(ConversationsListActivity.this, MessagesListActivity.class);
//                intent.putExtra("conversationId", conversation.getId());
//                startActivity(intent);
//
//            }
//
//            @Override
//            public boolean onConversationLongClick(AtlasConversationsAdapter adapter, Conversation conversation) {
//                return false;
//            }
//        });
//
//
//        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ConversationsListActivity.this, "hif", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

//    public void newConversation(View v) {
//        Toast.makeText(this, "new", Toast.LENGTH_SHORT).show();
//    }

}

