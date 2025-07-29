package com.superjcybs.smartagent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

    // Sets up Google Sign-In to request the user's email.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
                    // 🌟 Step 1: Log user data from SharedPreferences
                    Log.d("JERRYSEE", "USER AUTHENTICATION success: " + account.getEmail() + " (" + account.getId() + ")");
                    Toast.makeText(this, "USER AUTHENTICATED: "+ account.getEmail(), Toast.LENGTH_SHORT).show();

                    // 🌟 Step 2: Sign in with Firebase
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener(this, firebaseTask -> {
                                if (firebaseTask.isSuccessful()) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("JERRYSEE", "FIREBASE login successful: " + firebaseUser + ")");

                        // 🌟 Step 3: Store locally
                        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        long now = System.currentTimeMillis();
                        boolean isFirstTime = !prefs.contains("registration_date");

                        if (isFirstTime) {
                            long nextSub = now + (30L * 24 * 60 * 60 * 1000);
                            editor.putLong("registration_date", now);
                            editor.putLong("next_subscription_date", nextSub);
                        }

                        editor.putString("user_id", account.getId());
                        editor.putString("user_name", account.getDisplayName());
                        editor.putString("user_email", account.getEmail());
                        editor.putBoolean("is_logged_in", true);
                        editor.putLong("login_timestamp", now);
                        editor.apply();
                                    PrefLogger.logAllPrefs(this, "user_data");


                                    Log.d("JERRYSEE", "Now saving to firestore"+ firebaseUser + account.getId());
                    // 🌟 Step 4: Save to Firestore using firebase UID
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(firebaseUser.getUid())  // <-- secure
                            .set(new HashMap<String, Object>() {{
                                put("user_id", firebaseUser.getUid());
                                put("name", firebaseUser.getDisplayName());
                                put("email", firebaseUser.getEmail());
                                put("login_timestamp", now);
                            }})
                            .addOnSuccessListener(unused -> {
                                Log.d("JERRYSEE", "User saved, proceeding to MainActivity");
                                Toast.makeText(LoginActivity.this, "User saved to DB", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("JERRYSEE", "Error saving user", e);
                                Toast.makeText(LoginActivity.this, "Error saving user to cloud", Toast.LENGTH_SHORT).show();
                            });

                                } else {
                                    Log.w("JERRYSEE", "Firebase login failed", firebaseTask.getException());
                                    Toast.makeText(this, "Firebase login failed", Toast.LENGTH_SHORT).show();
                                }

                                // 🌐 Save to Firebase Firestore
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//                    Map<String, Object> userData = new HashMap<>();
//                    userData.put("user_id", account.getId());
//                    userData.put("name", account.getDisplayName());
//                    userData.put("email", account.getEmail());
//                    userData.put("login_timestamp", now);
//
//                    // You may store by user ID or email
//                    db.collection("users").document(Objects.requireNonNull(account.getId())).set(userData)
//                            .addOnSuccessListener(unused -> {
//                                startActivity(new Intent(this, MainActivity.class));
//                                finish();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(this, "Error saving user to cloud", Toast.LENGTH_SHORT).show();
//                            });
                            });
                } else {
                    Log.d("JERRYSEE", "USER AUTHENTICATION failed");
                    Toast.makeText(this, "USER AUTHENTICATION failed", Toast.LENGTH_SHORT).show();
                }
                // if the account is null, the sign-in failed
            } catch (ApiException e) {
                if (e.getStatusCode() == 12502) {
                    Toast.makeText(this, "Sign-in was cancelled by user.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                }
        }
    }
}
