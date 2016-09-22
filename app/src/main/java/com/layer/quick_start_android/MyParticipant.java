//package com.layer.quick_start_android;
//
//import android.net.Uri;
//
//import com.layer.atlas.provider.Participant;
//
///**
// * Created by crypsis on 1/9/16.
// */
//public class MyParticipant implements Participant {
//    private String mId;
//    private String mFirstName;
//    private String mLastName;
//    private String mEmail;
//    private Uri mAvatarUrl;
//    @Override
//    public String getId() {
//        return mId;
//    }
//
//    public void setId(String id) {
//        mId = id;
//    }
//
//    public String getFirstName() {
//        return mFirstName;
//    }
//
//    public void setFirstName(String firstName) {
//        mFirstName = firstName;
//    }
//
//    public String getLastName() {
//        return mLastName;
//    }
//
//    public void setLastName(String lastName) {
//        mLastName = lastName;
//    }
//
//    public String getEmail() {
//        return mEmail;
//    }
//
//    public void setEmail(String email) {
//        mEmail = email;
//    }
//
//    @Override
//    public String getName() {
//        return (getFirstName() + " " + getLastName()).trim();
//    }
//
//    @Override
//    public Uri getAvatarUrl() {
//        return mAvatarUrl;
//    }
//
//    public void setAvatarUrl(Uri avatarUrl) {
//        mAvatarUrl = avatarUrl;
//    }
//
//    @Override
//    public int compareTo(Participant another) {
//        int first = getFirstName().toLowerCase().compareTo(((MyParticipant) another).getFirstName().toLowerCase());
//        if (first != 0) return first;
//        return getLastName().toLowerCase().compareTo(((MyParticipant) another).getLastName().toLowerCase());
//    }
//
//}
