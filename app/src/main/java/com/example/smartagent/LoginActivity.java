//package com.example.quickdial;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.auth.api.signin.*;
//        import com.google.android.gms.tasks.Task;
//
//public class LoginPageActivity extends AppCompatActivity {
//
//    private GoogleSignInClient mGoogleSignInClient;
//    private static final int RC_SIGN_IN = 100;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        Object GoogleSignIn;
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            goToMain();
//        } else {
//            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            if (task.isSuccessful()) {
//                goToMain();
//            } else {
//                Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
//
//    private void goToMain() {
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
//    }
//}

// METHOD 2
package com.example.smartagent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    // Sets up Google Sign-In to request the user's email.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    // Creates the sign-in client
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    // When the user taps the button, the Google sign-in flow starts
        findViewById(R.id.googleSignInButton).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handles the result when the Google sign-in screen returns
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // get the signed in account
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                // 🌟 User signed in successfully, Save user data and subscription info
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    long currentTime = System.currentTimeMillis();

                    editor.putString("user_id", account.getId());
                    editor.putString("user_name", account.getDisplayName());
                    editor.putString("user_email", account.getEmail());
                    editor.putBoolean("is_logged_in", true);  // useful flag
                    editor.putLong("login_timestamp", System.currentTimeMillis());
//                    editor.putLong("next_renewal", renewalTime);
//                    editor.putLong("firstLogin", )
//                    prefs.edit().putLong("login_timestamp", currentTime).apply();

                    editor.apply();  // save changes

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                // if the account is null, the sign-in failed
            } catch (ApiException e) {
                Toast.makeText(this, "Sign-in failed, please try again:" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }
}
