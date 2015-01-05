package com.layer.quick_start_android;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by neilmehta on 1/5/15.
 */
public class MessageView {

    private LinearLayout myParent;

    private TextView senderTV;
    private TextView messageTV;

    public MessageView(LinearLayout parent, Message msg){
        myParent = parent;

        senderTV = new TextView(parent.getContext());
        myParent.addView(senderTV);

        messageTV = new TextView(parent.getContext());
        myParent.addView(messageTV);

        UpdateMessage(msg);
    }

    public void UpdateMessage(Message msg){
        String senderTxt = craftSenderText(msg);
        String msgTxt = craftMsgText(msg);

        senderTV.setText(senderTxt);
        messageTV.setText(msgTxt);
    }

    private String craftSenderText(Message msg){
        String senderTxt = msg.getSentByUserId();
        if(msg.getReceivedAt() != null) {
            senderTxt += " @ " + new SimpleDateFormat("HH:mm:ss").format(msg.getReceivedAt());
        }

        if(msg.getSentByUserId() == MainActivity.getParticipantUserID()){
            senderTxt += " - Read";
        } else if (msg.getRecipientStatus(MainActivity.getParticipantUserID()) == null) {
            senderTxt += " - Sent";
        } else {
           switch(msg.getRecipientStatus(MainActivity.getParticipantUserID())){
               case PENDING:
                   senderTxt += " - Pending";
                   break;

               case SENT:
                   senderTxt += " - Sent";
                   break;

               case DELIVERED:
                   senderTxt += " - Delivered";
                   break;

               case READ:
                   senderTxt += " - Read";
                   break;


           }
        }

        return senderTxt;
    }

    private String craftMsgText(Message msg){
        String msgText = "";

        List<MessagePart> parts = msg.getMessageParts();
        for(int i = 0; i < msg.getMessageParts().size(); i++){
            try {
                msgText += new String(parts.get(i).getData(), "UTF-8") + "\n";
            } catch (UnsupportedEncodingException e) {

            }
        }

        return msgText;
    }
}
