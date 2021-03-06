package com.obscuro.obscuro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference dbRef;
    private static final String TAG = "Registration";
    private static final String TAG2 = "AddToDatabase";

    EditText username, password, confirmPassword, email;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText)findViewById(R.id.register_email_editText);
        username = (EditText)findViewById(R.id.register_username_editText);
        password = (EditText)findViewById(R.id.register_password_editText);
        confirmPassword = (EditText)findViewById(R.id.confirm_password_editText);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = mFirebaseDatabase.getReference();
    }

    /**
     * Registers the user.
     *
     * @param v the current view that the data is coming from.
     */
    public void register(View v) {
        if (verifyUsernameLength() && verifyPasswordLength() && verifyConfirmPassword()) {
            Log.d(TAG, "register: Attempt");
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                userID = user.getUid();
                                writeNewUser(username.getText().toString(), email.getText().toString(), password.getText().toString());
                                Log.d(TAG, TAG2 + "createdUser");
                                Toast.makeText(RegisterActivity.this, "Registration successful!.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Creates new User object and saves to Firebase database
     * @param username user's username
     * @param password user's password
     * @param email whether or not user is an admin
     */
    private void writeNewUser(String username, String email, String password) {
        User user = new User(username, email, password);
        user.setUid(userID);
        dbRef.child("users").child(userID).setValue(user);
    }

    /**
     * Verifies that the email is the correct length.
     *
     * @return boolean true if the password has the correct length and false if it does not.
     *
     */
    private boolean verifyUsernameLength() {
        if (username.getText().toString().length() < 3){
            Toast.makeText(getApplicationContext(), "Email must contain at least 3 characters.", Toast.LENGTH_SHORT).show();
            username.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Verifies that the password has the correct length.
     *
     * @return boolean true if the password has the correct length and false if it does not.
     *
     */
    private boolean verifyPasswordLength() {
        if (password.getText().toString().length() > 16 || password.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be of length 6-16.", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Verifies that the password and confirm password are the same.
     *
     * @return a boolean false is passwords aren't the same and true if they are.
     *
     */
    private boolean verifyConfirmPassword() {
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords do not match.",Toast.LENGTH_SHORT).show();
            password.setText("");
            confirmPassword.setText("");
            password.requestFocus();
            return false;
        }
        return true;
    }

    public void toLoginActivity(View v){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
