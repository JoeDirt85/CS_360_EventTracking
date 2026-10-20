package com.example.cs_360_projectone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //declare database
    private Database database;

    //declaration of variables for widgets
    private EditText username;
    private EditText password;
    private Button login;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initializes and connects to the UI widgets
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.createAccount);

        //setup and connect buttons to the click listeners
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //instantiate database
        database = new Database(this);

        //creates strings that get username and password
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString();

        //checks if the EditText fields are empty
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            Toast.makeText(this,
                    "Please enter both your username and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //branch for the login button
        if (v.getId() == R.id.login) {
            //branch for successful login
            if (database.checkUserInformation(enteredUsername, enteredPassword)) {
                Toast.makeText(this,
                        "Login successful",
                        Toast.LENGTH_SHORT).show();

                //retrieves the userId using the entered username and password
                int userId = database.getUserId(enteredUsername, enteredPassword);

                //creates intent targeting the Display activity
                Intent intent = new Intent(MainActivity.this, Display.class);

                //includes the userId in the intent
                intent.putExtra("USER_ID", userId);
                //launches the screen
                startActivity(intent);
            }
            //branch for unsuccessful login, refers them to create account
            else {
                Toast.makeText(this,
                        "Account not found, click sign up",
                        Toast.LENGTH_SHORT).show();
            }

        }

        //branch for creating account
        else if (v.getId() == R.id.createAccount) {
            //branch if account already exists
            if (database.checkUserName(enteredUsername)) {
                Toast.makeText(this,
                        "Account already exists, click login",
                        Toast.LENGTH_SHORT).show();
            }
            //branch for successful account creation
            else {
                if (database.addUserInformation(enteredUsername, enteredPassword)) {
                    Toast.makeText(this,
                            "Account successfully created",
                            Toast.LENGTH_SHORT).show();

                    //retrieves the userId using the entered username and password
                    int userId = database.getUserId(enteredUsername, enteredPassword);

                    //creates intent targeting the Display activity
                    Intent intent = new Intent(MainActivity.this, Display.class);

                    //includes the userId in the intent
                    intent.putExtra("USER_ID", userId);
                    //launches the screen
                    startActivity(intent);
                }
            }
        }
    }
}