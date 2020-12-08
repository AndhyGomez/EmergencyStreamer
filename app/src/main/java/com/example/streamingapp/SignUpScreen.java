package com.example.streamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.streamingapp.LoginScreen.credentials;

public class SignUpScreen extends AppCompatActivity
{


    /*
     * Class Objects
     */
    Button signUp;
    TextView loginNow;
    EditText first;
    String firstName;
    EditText last;
    String lastName;
    EditText emailId;
    String userEmail;
    EditText pass1;
    String pass;
    EditText passVerif;
    String passVerify;

    private static final String SALTBANK = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$~%^&*?.,";

    /*
    * Instance of the database and firebase for athentication
     */
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for sign up button
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Initialize EditText objects
                first = findViewById(R.id.first);
                firstName = first.getText().toString();

                last = findViewById(R.id.last);
                lastName = last.getText().toString();

                emailId = findViewById(R.id.emailreg);
                userEmail = emailId.getText().toString();

                pass1 = findViewById(R.id.pass1);
                pass = pass1.getText().toString();

                passVerif = findViewById(R.id.pass2);
                passVerify = passVerif.getText().toString();

                /*
                 Store email, and password after verifying info first
                 */
                if(firstName.isEmpty() || lastName.isEmpty() || userEmail.isEmpty() || pass.isEmpty() || passVerify.isEmpty())
                {
                    Toast.makeText(SignUpScreen.this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                }
                else if(!pass.equals(passVerify))
                {
                    Toast.makeText(SignUpScreen.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                }
                else{
                    // Generate salt and hash
                    String pwSalt = generateSalt();
                    String pwHash = generateHash(pass, pwSalt);

                    // Create a new User
                    User user = new User(userEmail, pwSalt, pwHash);

                    // Add user to array list
                    credentials.add(user);

                    //Creating account
                    createAccount();

                    Toast.makeText(SignUpScreen.this, "Account Created.", Toast.LENGTH_LONG).show();

                    closeWindow(v);
                }
            }
        });

        // Set OnClickListener for login now text
        loginNow = findViewById(R.id.dismiss);
        loginNow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeWindow(v);
            }
        });
    }

    /**
     * Description: This method closes the pop up window
     *
     * @param v View to be closed
     */
    public void closeWindow(View v)
    {
        finish();
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



    public void saveExtraData(){

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userID = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        Map<String, Object> user = new HashMap<>();
        // format id, email, username
        user.put("userID", userID);
        user.put("KEY_USERNAME", userEmail);
        user.put("search", userEmail.toLowerCase());


        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                }

            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void createAccount(){

        mAuth.createUserWithEmailAndPassword(userEmail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignIn Success", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("MainActivity", "Email sent.");
                                                Toast.makeText(SignUpScreen.this, "Email has been sent", Toast.LENGTH_LONG).show();
                                                saveExtraData();
                                            }
                                        }
                                    });
                            // where u would change to app screen

                            Intent startingScreen = new Intent(SignUpScreen.this, LoginScreen.class);
                            startActivity(startingScreen);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignInFailed", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpScreen.this, "Authentication Failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }


}
