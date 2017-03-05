package com.example.user.thatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.effect.Effect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    //Declare xml items
    private Button mRegisterBtn;
    private EditText mRegisterEmailField;
    private EditText mRegisterPasswordField;
    private EditText mRegisterPasswordConfirmField;

    //Declare firebase items
    private Firebase mUsersEmailRef;
    private Firebase mUserDetailRef;

    //Declare progress dialog
    private ProgressDialog mProgressDialog;
    //Delcare Firebase authentication item
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //set this activity context to firebase before running fireabse database function.
        Firebase.setAndroidContext(this);

        //Declare items
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mRegisterBtn = (Button) findViewById(R.id.SignUp_button);
        mRegisterEmailField = (EditText) findViewById(R.id.SignUp_email);
        mRegisterPasswordField = (EditText) findViewById(R.id.SignUp_password);
        mRegisterPasswordConfirmField = (EditText) findViewById(R.id.SignUp_password_confirm);

        //Set on click listener to register button and call the funtion registerUser when click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    //Declare registerUser function
    private  void registerUser(){
        //Declare final variables for override function use purpose
        final String email = mRegisterEmailField.getText().toString();
        final String password = mRegisterPasswordField.getText().toString();
        final String password_confirm = mRegisterPasswordConfirmField.getText().toString();
        //Set firebase online database url
        mUsersEmailRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UsersEmail");
        mUserDetailRef = new Firebase("https://thatsapp-86aef.firebaseio.com/UserDetails");

        //Check if email, password and password confirm text box is empty, pop up error to the user
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm))
        {
            if (TextUtils.isEmpty(email))
                Toast.makeText(SignUpActivity.this, "Please fill in your email.", Toast.LENGTH_LONG).show();
            else if (TextUtils.isEmpty(password))
                Toast.makeText(SignUpActivity.this, "Please fill in your password.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(SignUpActivity.this, "Please retype your password to confirm your password.", Toast.LENGTH_LONG).show();
        }
        else if (!password.equals(password_confirm))
        {
            //else if the password and confirm password is not match, show the error to the user
            Toast.makeText(SignUpActivity.this, "Password and confirm password is not same. Please retype password.", Toast.LENGTH_LONG).show();
        }
        else
        {
            //if all items is correctly input, proceedto firebase authentication register progress and start the progress dialog
            mProgressDialog.setMessage("Registering User...");
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //If registration on firebase authentication is successful, save the user email and password to firebase authentication and create database url for new user.
                    //Beside this the authentication status will be auto over write to new user details. Proceed to Mainactivity and auto check authentication to login.
                    if (task.isSuccessful())
                    {
                        String name;
                        UserDatabaseName dbname = new UserDatabaseName();
                        name = dbname.convertEmailToDbName(email.toLowerCase());
                        Firebase mChildUserDataRef = mUserDetailRef.child(name);
                        Firebase mChildUserContactRef = mChildUserDataRef.child("contact_list");
                        mChildUserContactRef.push().setValue("nothing");
                        mUsersEmailRef.push().setValue(email);
                        Toast.makeText(SignUpActivity.this, "Registration successful... Welcome!" + email, Toast.LENGTH_LONG).show();




                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                    }
                    else
                    {
                        //Else if the task is unsuccessful try catch the firebase return error and show the proper error to the user.
                        String error_message;
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            error_message = "Invalid Password... Password requires more that 6 alphabets.";
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            error_message = "Invalid Email...";
                        } catch(FirebaseAuthUserCollisionException e) {
                            error_message = "This Email is already been used. Please try other email.";
                        } catch(FirebaseNetworkException e) {
                            error_message = "Network disconnected...Unable to register.";
                        } catch(Exception e) {
                            error_message = "Registration fail. Please try again later";
                        }
                        Toast.makeText(SignUpActivity.this, ""+error_message, Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }

                }
            });

        }
    }

}
