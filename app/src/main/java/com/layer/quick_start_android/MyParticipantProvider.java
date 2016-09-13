package com.layer.quick_start_android;

import android.content.Context;
import android.os.AsyncTask;

import com.layer.atlas.provider.Participant;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.atlas.util.Log;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.ConversationOptions;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by crypsis on 1/9/16.
 */
public class MyParticipantProvider implements ParticipantProvider {

    private final Context mcontext;
    Conversation conversation;
    ConversationOptions options;
    LayerClient mlayerClient;
    MyAuthenticationProvider mAuthenticationProvider;
    private final Map<String, MyParticipant> mParticipantMap = new HashMap<String, MyParticipant>();
    private final AtomicBoolean mFetching = new AtomicBoolean(false);

    public MyParticipantProvider(Context context) {
        mcontext = context.getApplicationContext();
                load();
        fetchParticipants();
    }
    public  MyParticipantProvider setConversation()
    {
        fetchParticipants();
//        setParticipants(myparticipantsFromJson());
        return this;
    }


    private static List<MyParticipant> myparticipantsFromJson(){
        List<MyParticipant> participants = new ArrayList<>(1);

            MyParticipant participant = new MyParticipant();
        participant.setId("1234");

            participant.setFirstName("avi");
            participant.setLastName("Naash");
            participant.setEmail("avinaash@gmail.com");
            participant.setAvatarUrl(null);
            participants.add(participant);

        return participants;
    }
    public MyParticipantProvider setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        mAuthenticationProvider = (MyAuthenticationProvider) authenticationProvider;
        fetchParticipants();
        return this;
    }

    @Override
    public Map<String, Participant> getMatchingParticipants(String filter, Map<String, Participant> result) {

        if (result == null) {
            result = new HashMap<String, Participant>();
        }
        synchronized (mParticipantMap) {
            // With no filter, return all Participants
            if (filter == null) {
                result.putAll(mParticipantMap);
                return result;
            }

            // Filter participants by substring matching first- and last- names
            filter = filter.toLowerCase();
            for (MyParticipant p : mParticipantMap.values()) {
                boolean matches = false;
                if (p.getName() != null && p.getName().toLowerCase().contains(filter))
                    matches = true;
                if (matches) {
                    result.put(p.getId(), p);
                } else {
                    result.remove(p.getId());
                }
            }

            return result;
        }


    }
    private boolean load() {
        synchronized (mParticipantMap) {
            String jsonString = mcontext.getSharedPreferences("participants", Context.MODE_PRIVATE).getString("json", null);
            if (jsonString == null) return false;

            try {
                for (MyParticipant participant : participantsFromJson(new JSONArray(jsonString))) {
                    mParticipantMap.put(participant.getId(), participant);
                }
                return true;
            } catch (JSONException e) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e(e.getMessage(), e);
                }
            }
            return false;
        }
    }


    @Override
    public Participant getParticipant(String userId) {
        synchronized (mParticipantMap) {
            MyParticipant participant = mParticipantMap.get(userId);
            if (participant != null) return participant;
            fetchParticipants();
            return null;
        }
    }

    private MyParticipantProvider setParticipants(Collection<MyParticipant> participants) {
        List<String> newParticipantIds = new ArrayList<>(participants.size());
        synchronized (mParticipantMap) {
            for (MyParticipant participant : participants) {
                String participantId = participant.getId();
                if (!mParticipantMap.containsKey(participantId)) {
                    newParticipantIds.add(participantId);
                }
                mParticipantMap.put(participantId, participant);
            }
            save();
        }
//        alertParticipantsUpdated(newParticipantIds);
        return this;
    }

        private boolean save() {
        synchronized (mParticipantMap) {
            try {
                mcontext.getSharedPreferences("participants", Context.MODE_PRIVATE).edit()
                        .putString("json", participantsToJson(mParticipantMap.values()).toString())
                        .commit();
                return true;
            } catch (JSONException e) {
                if (Log.isLoggable(Log.ERROR)) {
                    Log.e(e.getMessage(), e);
                }
            }
        }
        return false;
    }
    private MyParticipantProvider fetchParticipants() {

        if (mAuthenticationProvider == null) return this;
        MyAuthenticationProvider.Credentials credentials = mAuthenticationProvider.getCredentials();
        if (credentials == null) return this;
        if (credentials.getAuthToken() == null) return this;

        if (!mFetching.compareAndSet(false, true)) return this;
        new AsyncTask<MyAuthenticationProvider.Credentials, Void, Void>() {
            protected Void doInBackground(MyAuthenticationProvider.Credentials... params) {
                try {
                    // Post request
                   MyAuthenticationProvider.Credentials credentials = params[0];
                    String url = "http://layer-identity-provider.herokuapp.com/users.json";
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("X_LAYER_APP_ID", credentials.getLayerAppId());
                    if (credentials.getEmail() != null) {
                        connection.addRequestProperty("X_AUTH_EMAIL", credentials.getEmail());
                    }
                    if (credentials.getAuthToken() != null) {
                        connection.addRequestProperty("X_AUTH_TOKEN", credentials.getAuthToken());
                    }

                    // Handle failure
                    int statusCode = connection.getResponseCode();
                    if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                        if (Log.isLoggable(Log.ERROR)) {
                            Log.e(String.format("Got status %d when fetching participants", statusCode));
                        }
                        return null;
                    }

                    // Parse response
                    InputStream in = new BufferedInputStream(connection.getInputStream());
//                    String result = streamToString(in);
                    String result = IOUtils.toString(in, "UTF-8");

                    in.close();
                    connection.disconnect();
                    JSONArray json = new JSONArray(result);
                    setParticipants(participantsFromJson(json));
                } catch (Exception e) {
                    if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
                } finally {
                    mFetching.set(false);
                }
                return null;
            }
        }.execute(credentials);
        return this;

//        try {
//            options = new ConversationOptions().distinct(true);
//            // Try creating a new distinct conversation with the given user
//            conversation = mlayerClient.newConversation(options,Arrays.asList("Device"));
//        } catch (LayerConversationException e) {
//            // If a distinct conversation with the given user already exists, use that one instead
////            conversation = e.getConversation();
//        }
//
//        if (!mFetching.compareAndSet(false, true)) return this;
//        new AsyncTask<Void, Void, Void>() {
//
//
//            protected Void doInBackground(Void... params) {
//                try {
//                    // Post request
//
//                    String url = "http://layer-identity-provider.herokuapp.com/users.json";
//                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//                    connection.setDoInput(true);
//                    connection.setDoOutput(false);
//                    connection.setRequestMethod("GET");
//                    connection.addRequestProperty("Content-Type", "application/json");
//                    connection.addRequestProperty("Accept", "application/json");
//                    connection.addRequestProperty("X_LAYER_APP_ID", mlayerClient.getAppId().toString());
//
//
//                    // Handle failure
//                    int statusCode = connection.getResponseCode();
//                    if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
//                        if (Log.isLoggable(Log.ERROR)) {
//                            Log.e(String.format("Got status %d when fetching participants", statusCode));
//
//                        }
//                        return null;
//                    }
//
//                    // Parse response
//                    InputStream in = new BufferedInputStream(connection.getInputStream());
////                    String result = streamToString(in);
//                    String myString = IOUtils.toString(in, "UTF-8");
//                    in.close();
//                    connection.disconnect();
//                    JSONArray json = new JSONArray(myString);
//                    setParticipants(participantsFromJson(json));
//                } catch (Exception e) {
//                    if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
//                } finally {
//                    mFetching.set(false);
//                }
//                return null;
//            }
//        }.execute();
//        return this;

    }
    private static List<MyParticipant> participantsFromJson(JSONArray participantArray) throws JSONException {
        List<MyParticipant> participants = new ArrayList<>(participantArray.length());
        for (int i = 0; i < participantArray.length(); i++) {
            JSONObject participantObject = participantArray.getJSONObject(i);
            MyParticipant participant = new MyParticipant();
            participant.setId(participantObject.optString("id", null));
            participant.setFirstName(trimmedValue(participantObject, "first_name", null));
            participant.setLastName(trimmedValue(participantObject, "last_name", null));
            participant.setEmail(trimmedValue(participantObject, "email", null));
            participant.setAvatarUrl(null);
            participants.add(participant);
        }
        return participants;
    }
    private static JSONArray participantsToJson(Collection<MyParticipant> participants) throws JSONException {
        JSONArray participantsArray = new JSONArray();
        for (MyParticipant participant : participants) {
            JSONObject participantObject = new JSONObject();
            participantObject.put("id", participant.getId());
            participantObject.put("first_name", participant.getFirstName());
            participantObject.put("last_name", participant.getLastName());
            participantObject.put("email", participant.getEmail());
            participantsArray.put(participantObject);
        }
        return participantsArray;
    }
    private static String trimmedValue(JSONObject o, String name, String fallback) {
        String s = o.optString(name, fallback);
        return (s == null) ? null : s.trim();
    }
}
