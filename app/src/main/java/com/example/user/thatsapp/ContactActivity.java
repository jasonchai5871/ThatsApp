package com.example.user.thatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.user.thatsapp.MainActivity.contactList;
import static com.example.user.thatsapp.MainActivity.currentUserEmail;

import java.util.ArrayList;
import java.util.Collection;


public class ContactActivity extends AppCompatActivity {
    //Declare xml items
    TabLayout mContactTabLayout;
    ViewPager mContactViewPager;
    //Declare Viewpager Adapter
    Contact_ViewPagerAdapter mContactViewPagerAdapter;
    //Declare firebase item
    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //Decare new arraylist for pre-load database
        ArrayList<String> checking = new ArrayList<String>();
        //Declare SharedPreferences function to load json file
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        //Declare select ContactList tag to json
        String json = appSharedPrefs.getString("Contact List", "");
        //store json items to checking arraylist
        checking = gson.fromJson(json,ArrayList.class);
        //check if checking arraylist is not null, then assign checking items in static arraylist contactList.
        if (checking != null){
            contactList = checking;
        }
        //Declare the id to xml items
        mContactTabLayout = (TabLayout) findViewById(R.id.contact_tab_layout);
        mContactViewPager = (ViewPager) findViewById(R.id.contact_view_pager);
        //Declare and set fragment to viewpager adapter
        mContactViewPagerAdapter = new Contact_ViewPagerAdapter(getSupportFragmentManager());
        mContactViewPagerAdapter.addFragments(new InboxFragment(), "Inbox");
        mContactViewPagerAdapter.addFragments(new ContactFragment(), "Contact");
        mContactViewPagerAdapter.addFragments(new SearchFragment(), "Search");
        //set viewpager adapter to the tab layout
        mContactViewPager.setAdapter(mContactViewPagerAdapter);
        //Set up the viewpager
        mContactTabLayout.setupWithViewPager(mContactViewPager);



    }

    //Finish the activity when back button is click
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedid = item.getItemId();
        //When sign out is selected, finish the activity and called firebase authentication sign out function. Lastly clear all on going activity and restart the application.
        switch (selectedid){
            case R.id.Sign_out_item:
           mAuth.getInstance().signOut();
            Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restart);
            finish() ;

            break;


        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //create a menu bar according to the items in main_menu xml
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
