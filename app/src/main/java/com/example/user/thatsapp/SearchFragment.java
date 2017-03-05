package com.example.user.thatsapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.user.thatsapp.MainActivity.contactList;
import static com.example.user.thatsapp.MainActivity.currentUserEmail;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    //Declare firebase item
    private Firebase mUserEmailRef;
    private Firebase mUserDetailsRef;
    //Declare string arraylist
    private ArrayList<String> mSearchEmailList = new ArrayList<String>();
    //Delcare xml items
    ImageButton mSearchButton;
    EditText mEmailInput;
    //Declare UserDatabaseName class
    UserDatabaseName dbname = new UserDatabaseName();
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set context for firebase
        Firebase.setAndroidContext(this.getActivity());
        //Declare activity
        final Activity mActivity = this.getActivity();
        //Declare fragment view
        final View view;
        view = inflater.inflate(R.layout.fragment_search, container, false);
        //initiate id to search button and edit text email input
        mSearchButton = (ImageButton) view.findViewById(R.id.search_contact_button);
        mEmailInput = (EditText) view.findViewById(R.id.search_email_edit_text);
        //Initiate id to searchcontactlist list view
        final ListView searchcontactlist = (ListView) view.findViewById(R.id.search_contact_list);
        //Initiate android default adapter to searchcontactlist list
        final ArrayAdapter<String> testingadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mSearchEmailList);

        //Set onClickListener to search button
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when click catch the value from user input, and clear the search list to prevent duplicate output
                final String email = mEmailInput.getText().toString();
                mSearchEmailList.clear();
                testingadapter.notifyDataSetChanged();
                //check if  user input email is not empty
                if(!email.isEmpty()) {
                    //Set firebase database url and addValueEventListener to read the data of the url
                    mUserEmailRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UsersEmail");
                    mUserEmailRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Declare string variable and iterable variable with DataSnapshot object retrieve from firebase database.
                            String contact;
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                            //Compare each data retrieve from firebase database url and find any data match with user input
                            for (DataSnapshot child : children) {
                                contact = child.getValue().toString().toLowerCase();
                                if (!contact.equals("UserEmail") ){
                                    //if found match add the value to search list
                                    if(contact.equals(email) && !contact.equals(currentUserEmail)) {
                                        mSearchEmailList.add(contact);
                                        testingadapter.notifyDataSetChanged();

                                    }
                                }

                            }
                            //Declare id for text view
                            TextView contactText = (TextView) view.findViewById(R.id.search_fragment_text);
                            //after matching the value, check if search list is not empty
                            if(!mSearchEmailList.isEmpty()) {
                                //set the adapter
                                searchcontactlist.setAdapter(testingadapter);
                                if(contactList.isEmpty()) {
                                    //if user contact list is empty, provide context menu for search list item.
                                    registerForContextMenu(searchcontactlist);
                                }
                                else{
                                    //else if user contact is not empty, checking the search email is existing in current user contact or not
                                    int count = contactList.size();
                                    boolean check = false;
                                    for (int i = 0; i < count ; i++)
                                    {
                                        if(contactList.get(i).toString().equals(email)){
                                            check = true;
                                            break;
                                        }
                                    }
                                    //if found same email in contact list, pop up message to user
                                    if (check)
                                        Toast.makeText(mActivity,"This email is already been added to contact.",Toast.LENGTH_LONG).show();
                                    //else provide context menu for search list item.
                                    else
                                        registerForContextMenu(searchcontactlist);
                                }
                                contactText.setHint(null);

                            }
                            //else if search list is empty, pop up no result to the user.
                            else{

                                contactText.setHint("No result....");
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //create context menu according to xml search_context_menu item
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_context_menu, menu);
    }
    //set on selected item in contet menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String email;

        switch (item.getItemId()){
            //if add selection is selected
            case R.id.search_add_item:
                //push the value of email request to add to firebase database of user contact databse
                mUserDetailsRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UserDetails/"+dbname.convertEmailToDbName(currentUserEmail)+"/contact_list");
                email = mSearchEmailList.get(info.position).toString();
                mUserDetailsRef.push().setValue(email);

                break;
            default:break;
        }

        return super.onContextItemSelected(item);

    }
}
