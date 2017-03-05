package com.example.user.thatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Declare xml items
    private Button mLoginBtn;
    private TextView signup;
    private EditText mEmailField;
    private EditText mPasswordField;
    //Declare Progress Dialog
    private ProgressDialog mProgressDialog;

    //Declare firebase Authentication function
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //Decare static variable for catch after login user and data details
    protected static ArrayList<String> contactList = new ArrayList<String>();
    protected static String currentUserEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare Progress Dialog
        mProgressDialog = new ProgressDialog(this);
        //Declare firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        //Declare xlm items
        mLoginBtn = (Button) findViewById(R.id.login_button);
        mEmailField = (EditText) findViewById(R.id.login_email);
        mPasswordField = (EditText) findViewById(R.id.login_password);
        //Create firebase Authentication state listener to catch the login status
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //If there is already login, capture the current user email and proceed to Contact Activity
                if(firebaseAuth.getCurrentUser() != null)
                {
                    currentUserEmail = firebaseAuth.getCurrentUser().getEmail().toString();
                    startActivity(new Intent(MainActivity.this, ContactActivity.class));
                    finish();

                }

            }
        };
        //Create on click listener to login button, when click go to startLogin function
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startLogin();

            }
        });


        //Declare and create on click listener on sign up text view link
        signup = (TextView) findViewById(R.id.sign_up_link);
        signup.setPaintFlags(signup.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);


        //When click, proceed to SignUp Activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });



    }
    //When back button is click, finish this activity.
    @Override
    public void onBackPressed() {
        finish();
    }

    //When start the application, set the Listener for check authentication status
    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    //Declare startLogin void function
    private void startLogin(){
        //Declare final variable got override function use
        final String email = mEmailField.getText().toString();
        final String password = mPasswordField.getText().toString();

        //If email or password text box is empty, pop up error message for user else proceed to firebase authentication checking
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            if(TextUtils.isEmpty(email))
                Toast.makeText(MainActivity.this, "Email field is empty.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(MainActivity.this, "Password field is empty.", Toast.LENGTH_LONG).show();
        }
        else
        {
            //Set progresss dialog message and start it
            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.show();
            //Call firebase email and password  authentication checking function
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                //If the authentication checking is complete
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //If the authentication email and password is insert wrongly the task will be unsuccessful and proceed to try catching the firebase error and show to the user. And close the progress dialog
                    if (!task.isSuccessful())
                    {
                        String error_message;
                        try {
                            throw task.getException();
                        } catch(FirebaseNetworkException e) {
                            error_message = "Network disconnected...Unable to login.";
                        }catch(FirebaseAuthInvalidCredentialsException e) {
                            error_message = "Wrong email or password input...Please try again.";
                        }
                        catch(Exception e) {
                            error_message = "Login fail. Please try again later";
                        }
                        Toast.makeText(MainActivity.this, "" + error_message, Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                    else
                    {
                        //Else if the login is successful, authentication status will be over write and firebase will recheck the authentication status and proceed to ContactActivity.
                        currentUserEmail = email.toLowerCase();
                        Toast.makeText(MainActivity.this, "Welcome " + currentUserEmail, Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }


}
