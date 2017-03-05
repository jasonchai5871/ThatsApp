package com.example.user.thatsapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import  com.example.user.thatsapp.ContactActivity;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import static com.example.user.thatsapp.MainActivity.contactList;
import static com.example.user.thatsapp.MainActivity.currentUserEmail;
import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {



    //Declare firebase item
    private Firebase mUserContactDetailRef;

    public ContactFragment() {
        // Required empty public constructor

    }




    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Declare activity
        final Activity mActivity = getActivity();
        //Declare SharedPreferences to store data in json file
        final SharedPreferences appSharePrefs  = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final SharedPreferences.Editor prefsEditor = appSharePrefs.edit();
        //Declare gson
        final Gson gson = new Gson();
        //Declare fragment view
        final View view;
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        //Declare xml items
        final ListView contactlistview = (ListView) view.findViewById(R.id.contact_list);
        final TextView contactText = (TextView) view.findViewById(R.id.contact_tab_text);
        //Declare android default arraylist adapter in contactList arraylist
        final ArrayAdapter<String> testingadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, contactList);

        //Set context for firebase use
        Firebase.setAndroidContext(this.getActivity());
        //Declare UserDatabaseName class to call the convertEmailToDbName class function
        UserDatabaseName dbname = new UserDatabaseName();
        //Set the user's firebase database url
        mUserContactDetailRef = new Firebase ("https://thatsapp-86aef.firebaseio.com/UserDetails/"+dbname.convertEmailToDbName(currentUserEmail)+"/contact_list");
        //Set addValueEventListener to listen the data in firebase database
        mUserContactDetailRef.addValueEventListener(new ValueEventListener() {
            //Load the data from firebase database and listen to data changes in the url database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Declare string variable
                String contact;
                //Declare iterable variable with firebase DataSnapshot type to capture the data from firebase database
                Iterable<DataSnapshot> contactlistdata = dataSnapshot.getChildren();
                //Clear the contactList to prevent repeat data
                contactList.clear();
                //Load the data from firebase DataSnapshot to contactList with valid data
                for (DataSnapshot child:contactlistdata){
                    contact = child.getValue().toString().toLowerCase();

                    if(!contact.equals("nothing")) {
                        contactList.add(contact);
                        testingadapter.notifyDataSetChanged();
                    }

                }
                testingadapter.notifyDataSetChanged();

                //check if the contactList is not empty, then store the contactList in json file with tag Contact List
                if(!contactList.isEmpty()) {
                    String json = gson.toJson(contactList);
                    prefsEditor.putString("Contact List",json);
                    prefsEditor.commit();
                    contactText.setHint(null);
                }else {

                    contactText.setHint("No contacts...");

                }



            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        testingadapter.notifyDataSetChanged();
        //Checking if contactList is not empty, set the adapter for view purpose and set onItemClickListener on the contactlistview
        if(!contactList.isEmpty()) {
            Collections.sort(contactList);
            testingadapter.notifyDataSetChanged();
            contactlistview.setAdapter(testingadapter);
            //If any item is click in contactlistview assign the item value to static variable chatwith from UserChatDetails class and proceed to ChatActivity
            contactlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserChatDetails.chatWith = contactList.get(position);
                    startActivity(new Intent(mActivity,ChatActivity.class));
                }
            });
            contactText.setHint(null);

        }else {

            contactText.setHint("No contacts...");

        }




        // Inflate the layout for this fragment
        return view;
    }



}
