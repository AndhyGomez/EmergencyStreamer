package com.example.streamingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpScreen extends AppCompatActivity
{

    /*
     * Class Objects
     */
    Button signUp;
    TextView loginNow;
    EditText first;
    EditText last;
    EditText emailId;
    EditText pass1;
    EditText passVerif;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        // Set OnClickListener for sign up button
        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                 Store email, and password after verifying info first
                 */

                // For now will just assume info is correct
                closeWindow(v);
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
}
