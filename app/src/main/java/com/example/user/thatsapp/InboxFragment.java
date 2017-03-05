package com.example.user.thatsapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Map;

import static com.example.user.thatsapp.MainActivity.contactList;
import static com.example.user.thatsapp.MainActivity.currentUserEmail;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {


    //Declare firebase item
    private Firebase mSelfRef;
    //Declare UserDatabseName class
    UserDatabaseName dbname = new UserDatabaseName();
    //Declare onject arraylist with InboxItem Object
    ArrayList<InboxItem> inboxitem;
    //Declare adapter
    InboxListAdapter aInboxAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set context fore firebase
        Firebase.setAndroidContext(this.getActivity());
        //Declare fragment view
        final View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        //Declare id of inboxlist list view
        final ListView inboxlist = (ListView) view.findViewById(R.id.inbox_list);
        //Declare activity
        final Activity mActivity = getActivity();
        //Declare text view id
        final TextView inboxtext = (TextView) view.findViewById(R.id.inbox_text);
        //Initiate inboxitem arraylist
        inboxitem = new ArrayList<>();
        //Initiate adapter to for inboxitem arraylist
        aInboxAdapter = new InboxListAdapter(getContext(),inboxitem);
        //Set url of current user databse
        mSelfRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UserDetails/"+dbname.convertEmailToDbName(currentUserEmail)+"/message");
        //Synced the data to local storage for sudden offline view purpose
        mSelfRef.keepSynced(true);
        //Set addChildEventListener for firebase database to catch the data
        mSelfRef.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //When the url database changes, catch each of the map data one by one from database url store in map variable.
                Map map = dataSnapshot.getValue(Map.class);
                //Split out the map value and store in single string variable
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String chatWithUser = map.get("chatWithUser").toString();
                //If the message is not send by the current user
                if(!userName.equals(currentUserEmail))
                {
                    // and if the inboxitem list is not empty check the duplicate user name in inbox list
                    if(!inboxitem.isEmpty()) {

                        int checkpos = checkposition(inboxitem, userName);
                        String c = String.valueOf(checkpos);
                        //checkpos change to the list position if any same username found in inboxlist, if found, replace the username and message to the inboxlist  position.
                        if(checkpos > -1) {
                            inboxitem.remove(checkpos);
                            inboxitem.add(0, new InboxItem(userName, message));
                            aInboxAdapter.notifyDataSetChanged();
                        }
                        //else if not found same user name in the inboxlist, add a new value to inboxlist and set position to 0 to push the latest data on top of the list.
                        else{
                            inboxitem.add(0, new InboxItem(userName,message));
                            aInboxAdapter.notifyDataSetChanged();
                        }
                    }
                    //else if inboxlist is empty, add new value to the inboxlist
                    else{
                        inboxitem.add(0, new InboxItem(userName,message));
                        aInboxAdapter.notifyDataSetChanged();
                    }
                }
                //else if the message is send by current user
                else{
                    //and the inbox is not empty, check inbox list is not empty
                    if(!inboxitem.isEmpty()) {
                        //check if any duplicate user found catch the position and replace the values on that inboxlist position.
                        int checkpos = checkposition(inboxitem, chatWithUser);
                        if(checkpos > -1) {
                            inboxitem.remove(checkpos);
                            inboxitem.add(0, new InboxItem(chatWithUser, message));
                            aInboxAdapter.notifyDataSetChanged();
                        }
                        //else if not found, add new value and set the position to 0 to push the latest value on top of the list.
                        else{
                            inboxitem.add(0, new InboxItem(chatWithUser,message));
                            aInboxAdapter.notifyDataSetChanged();
                        }
                    }
                    //else if inboxlist is empty, add new value to the inboxlist
                    else{
                        inboxitem.add(0, new InboxItem(chatWithUser,message));
                        aInboxAdapter.notifyDataSetChanged();
                    }
                }
                //Double check the inbox is empty or not for display the message to user.
                if(inboxitem.isEmpty())
                {
                    inboxtext.setText("No message...");
                }
                else{
                    inboxtext.setText("");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

            //reset the adaoter and get up to date list for viewing to the user
            aInboxAdapter = new InboxListAdapter(getContext(),inboxitem);
            aInboxAdapter.notifyDataSetChanged();
            inboxlist.setAdapter(aInboxAdapter);
            //set onItemClickListener to allow user click on it and proceed to the ChatActivity with the user chat with selected
            inboxlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    InboxItem a = inboxitem.get(position);
                    UserChatDetails.chatWith = a.getEmail();
                    startActivity(new Intent(mActivity,ChatActivity.class));
                }
            });




        // Inflate the layout for this fragment
        return view;
    }
    //checkposition class that return the position if found same name or return -1 if same name is not found
    public int checkposition(ArrayList<InboxItem> list, String email){
        int pos = -1;
        for(InboxItem items : list){
            if(items.getEmail().equals(email)){
                pos = list.indexOf(items);
                break;
            }
        }
        return pos;
    }
}
