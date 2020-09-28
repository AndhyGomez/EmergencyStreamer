package com.example.streamingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Set OnClickListener for login button
        login = findViewById(R.id.loginbtn);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean credentialsValid;

                // Store email input
                emailIn = findViewById(R.id.emailid);
                emailVal = emailIn.getText().toString();

                // Store password input
                passIn = findViewById(R.id.passid);
                passVal = passIn.getText().toString();

                credentialsValid = isUserValid(emailVal, passVal);

                if(credentialsValid)
                {
                    Toast.makeText(LoginScreen.this, "Login Success.", Toast.LENGTH_LONG).show();

                    openMain(v);
                }
                else {
                    Toast.makeText(LoginScreen.this, "Credentials Invalid.", Toast.LENGTH_LONG).show();
                }
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
}
