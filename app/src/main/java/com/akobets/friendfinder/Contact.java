package com.akobets.friendfinder;

import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by akobets on 28.08.2015.
 */
public class Contact {

    public static final char TYPE_SEPARATOR = 0x00C1;

    private String contact;
    private ArrayList<String> friendsContact;
    private int ivFotoId;


    {
        contact = "No name";
        friendsContact = new ArrayList<>();
        ivFotoId = R.drawable.foto_default;
    }


    public static ArrayList<String> parsingFriendsString(String friendsString) {

        ArrayList<String> parsedFriends = new ArrayList<>();
        String tempName = "";
        int cnt = 0;
        for (int i = 0; i < friendsString.length(); i++) {
            if (friendsString.codePointAt(i) == 44) {
                tempName = friendsString.substring(cnt, i);
                cnt = i + 2;
                parsedFriends.add(tempName);
            }

        }
        return parsedFriends;
    }


    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ArrayList<String> getFriendsList() {
        return friendsContact;
    }

    public String getFriendsString() {
        String friends = "";
        if (friendsContact.size() > 0) {
            for (String i : friendsContact) {
                friends += i + ", ";
            }
            friends = friends.substring(0, friends.length() - 2);
        }
        return friends;
    }


    public void setFriendsList(ArrayList<String> friendsContact) {
        this.friendsContact = friendsContact;
    }

    public void removeFriendsList(ArrayList<String> friendsContactForRemove) {
        for (String i : friendsContactForRemove) {
            for (String j : friendsContact) {
                if (i.equals(j)) {
                    friendsContact.remove(j);
                    break;
                }
            }
        }
    }

    public void removeFriendContact(int index) {
        friendsContact.remove(index);
    }

    public void removeFriendContact(String delFriend) {
        friendsContact.remove(delFriend);
    }

    public void addFriendsContact(ArrayList<String> friendsContactForAdd) {
        for (String i : friendsContactForAdd) {
            friendsContact.add(i);
            break;
        }
    }

    public void addFriendsContact(String friendContact) {
        friendsContact.add(friendContact);
    }

    public int getIvFotoId() {
        return ivFotoId;
    }

    public void setIvFotoId(int ivFotoId) {
        this.ivFotoId = ivFotoId;
    }

    public ArrayList<String> getNonFriend(ArrayList<String> contacts) {
        ArrayList<String> nonFriendContacts = contacts;
        for (String i : friendsContact) {
            for (String j : nonFriendContacts) {
                if (i.equals(j)) {
                    nonFriendContacts.remove(j);
                    break;
                }
            }
        }
        return nonFriendContacts;
    }

    public String getStringFormContact() {
        String stringFormContract;
        stringFormContract = contact + TYPE_SEPARATOR;
        stringFormContract += String.valueOf(ivFotoId) + TYPE_SEPARATOR;
        for (int i = 0; i < friendsContact.size(); i++) {
            stringFormContract += friendsContact.get(i) + TYPE_SEPARATOR;
        }
        stringFormContract = stringFormContract.substring(0, stringFormContract.length() - 1);
        return stringFormContract;
    }
}
