package com.example.streamingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity
{
    /*
     * Class Objects
     */
    Button login;
    Button register;
    TextView forgotPass;
    EditText emailIn;
    EditText passIn;

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
                // Temporary Action for testing
                openMain(v);
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
}
