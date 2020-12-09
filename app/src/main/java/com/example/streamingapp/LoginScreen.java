package com.example.streamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;

import java.util.ArrayList;

public class LoginScreen extends AppCompatActivity
{

    /*
     * Class Objects
     */
    Button login;
    Button register;
    TextView forgotPass;
    EditText emailIn;
    String emailVal;
    EditText passIn;
    String passVal;

    static ArrayList<User> credentials = new ArrayList<User>();

    private static final String SALTBANK = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$~%^&*?.,";

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for login button
        login = findViewById(R.id.loginbtn);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                // Store email input
                emailIn = findViewById(R.id.emailid);
                emailVal = emailIn.getText().toString();

                // Store password input
                passIn = findViewById(R.id.passid);
                passVal = passIn.getText().toString();

                signIn(emailVal, passVal);


            }
        });

        forgotPass = findViewById(R.id.forgotPass);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, ResetPasswordActivity.class));
            }
        });


        // Set OnClickListener for register button
        register = findViewById(R.id.signUpNow);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openReg(v);
            }
        });
    }

    /**
     * Description: This method starts the activity of the SignUpScreen class
     *
     * @param v View to be passed
     */
    private void openReg(View v)
    {
        Intent openWindow = new Intent(this, SignUpScreen.class);
        startActivity(openWindow);
    }

    /**
     * Description: This method starts the activity of the MainActivity class
     *
     * @param v View to be passed
     */
    private void openMain(View v)
    {
        Intent openWindow = new Intent(this, MainActivity.class);
        startActivity(openWindow);
    }

    /**
     * Description: Checks if user credentials are valid
     *
     * @param username
     * @param password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean isUserValid(String username, String password)
    {
        String userSalt;
        String userPw;
        String hash;

        boolean isUserValid = false;

        for(User index: credentials)
        {
            if(username.equals(index.username))
            {
                userSalt = index.salt;
                userPw = password;

                hash = generateHash(userPw, userSalt);

                if(hash.equals(index.passwordHash))
                {
                    isUserValid = true;
                }
            }
        }

        return isUserValid;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    /**
     * Description: Generate salt to aid password security
     *
     * @return generated salt
     */
    public static String generateSalt()
    {
        final int SALTSIZE = 5;

        String passwordSalt;

        StringBuilder generatedSalt = new StringBuilder();

        for(int i = 0; i <  SALTSIZE; i++)
        {
            int character = (int)(Math.random()*SALTBANK.length());
            generatedSalt.append(SALTBANK.charAt(character));
        }

        passwordSalt = generatedSalt.toString();

        return passwordSalt;
    }

    /**
     * Description: Generates a hashcode of a concatenation of the users entered
     * password and the password salt
     *
     * @param password user entered password as a string
     * @param passwordSalt randomly generated salt as a string
     * @return hashed password
     */
    public static String generateHash(String password, String passwordSalt)
    {
        String pwConcat;
        char hashToChar;
        int ascii;

        pwConcat = password.concat(passwordSalt);

        // Create a new int array
        int[] asciiHash = new int[pwConcat.length()];

        // Converts string to char array
        char[] stringAsChars = pwConcat.toCharArray();

        // Converts hash to chars
        char[] hashAsChars = new char[pwConcat.length()];

        for(int charAt = 0; charAt < stringAsChars.length; charAt++)
        {
            ascii = stringAsChars[charAt];

            asciiHash[charAt] = ascii + 1;
        }


        for (int i = 0; i < asciiHash.length; i++)
        {
            hashToChar = (char) asciiHash[i];

            hashAsChars[i] = hashToChar;
        }

        String passwordHash = new String(hashAsChars);

        return passwordHash;
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser();
                            Intent startingScreen = new Intent(LoginScreen.this, MainActivity.class);
                            startActivity(startingScreen);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                            // Open Sign up Screen on failed authentication
                            Intent openWindow = new Intent(LoginScreen.this, SignUpScreen.class);
                            startActivity(openWindow);
                            
                            // dont change screen
                            // [START_EXCLUDE]
                            checkForMultiFactorFailure(task.getException());
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginScreen.this, "Authentication/login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END sign_in_with_email]
    }

    public void checkForMultiFactorFailure(Exception e) {
        // Multi-factor authentication with SMS is currently only available for
        // Google Cloud Identity Platform projects. For more information:
        // https://cloud.google.com/identity-platform/docs/android/mfa
        if (e instanceof FirebaseAuthMultiFactorException) {
            Log.w(TAG, "multiFactorFailure", e);
            Intent intent = new Intent();
            MultiFactorResolver resolver = ((FirebaseAuthMultiFactorException) e).getResolver();
            intent.putExtra("EXTRA_MFA_RESOLVER", resolver);
            // change screeen
            finish();
        }
    }



}
