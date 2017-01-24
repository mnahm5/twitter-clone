package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static android.R.attr.name;
import static com.parse.starter.R.id.etUsername;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        redirectUser();
    }

    public void LoginLink(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(loginIntent);
    }

    public void SignUp(final View view) {
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);

        if (etPassword.getText().toString().equals(etPassword2.getText().toString())) {
            ParseUser parseUser = new ParseUser();
            parseUser.setUsername(etUsername.getText().toString());
            parseUser.setPassword(etPassword.getText().toString());
            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Account Created",
                                Toast.LENGTH_SHORT
                        );
                        LoginLink(view);
                    }
                    else {
                        Toast.makeText(
                                getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        );
                    }
                }
            });
        }
        else {
            Toast.makeText(
                    getApplicationContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void redirectUser() {
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), UserList.class);
            startActivity(intent);
        }
    }
}
