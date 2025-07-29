package com.superjcybs.smartagent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {
//    private static final long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000; // 30 days in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // references your splash layout -  shows the splash layout.

        // Animate logo
        ImageView logo = findViewById(R.id.logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in)); // or R.anim.scale

// Delay splash logic by 2 seconds
        new Handler().postDelayed(() -> {
        // ---------check if the user is signed in with Google------
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            // If not signed in, go to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

// If signed in → Checks subscribed_at timestamp from SharedPreferences to see how long left
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            PrefLogger.logAllPrefs(this, "user_data");

            long now = System.currentTimeMillis();
        long THIRTY_DAYS = 30L * 24 * 60 * 60 * 1000; // in milliseconds
        long subscribedAt = prefs.getLong("subscribed_at", 0);
        long registrationDate = prefs.getLong("registration_date", 0);
        long trialEnds = registrationDate + THIRTY_DAYS;

//        long loginTimestamp = prefs.getLong("login_timestamp", 0);
//        long currentTime = System.currentTimeMillis();

//        if (subscribedAt == 0 || (now - subscribedAt) > THIRTY_DAYS) {
//            // Not subscribed or expired, show subscription page
//            startActivity(new Intent(this, SubscriptionActivity.class));
//        } else {
//            // Subscribed and valid, show main activity page
//            startActivity(new Intent(this, MainActivity.class));
//        }

            if (subscribedAt != 0 && (now - subscribedAt) <= THIRTY_DAYS) {
                // ✅ User has an active subscription
                startActivity(new Intent(this, MainActivity.class));
            } else if (registrationDate != 0 && now <= trialEnds) {
                // ✅ Still in the free trial period
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // ❌ Not subscribed and not in free trial
                startActivity(new Intent(this, SubscriptionActivity.class));
            }

            // Optional: closes SplashActivity
        finish();
        }, 2000); // 2000ms = 2 seconds
    }
//        if (loginTimestamp == 0 || currentTime - loginTimestamp > THIRTY_DAYS_MS) {
//            // Time expired, show subscription
//            startActivity(new Intent(this, SubscriptionActivity.class));
//        }
//        else {
//            // still valid
//            startActivity(new Intent(this, MainActivity.class));
//        }
//        finish();

        // Go to LoginActivity first
//        Intent intent = new Intent(this, com.example.quickdial.ui.login.LoginActivity.class);
//        startActivity(intent);
//        finish(); // Optional: closes SplashActivity

// -------SHOW MAIN ACTIVITY AFTER SPLASH------
//        new Handler().postDelayed(() -> {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//            finish(); // Close the splash activity so the user can't return to it
//        }, 2000); // Delay: 2 seconds
//    }
    // ---------SHOW LOGIN PAGE AFTER SPLASH--------
//            new Handler().postDelayed(() -> {
//        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//        finish(); // Close the splash activity so the user can't return to it
//    }, 2000); // Delay: 2 seconds
//}
//    new Handler().postDelayed(() -> {
//        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }, 1000);


}