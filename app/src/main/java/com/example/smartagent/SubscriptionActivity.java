package com.example.smartagent;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.flutterwave.raveandroid.RaveUiManager;
//import com.flutterwave.raveandroid.RavePayManager;
//import com.flutterwave.raveandroid.RaveConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

//SharedPreferences prefs = getSharedPreferences("user_saves", MODE_PRIVATE);
//String name = prefs.getString("user_name", "Guest");
//String email = prefs.getString("user_email", null);
//long nextRenewalMillis = prefs.getLong("next_renewal", 0);
//// Optional: Convert to readable date
//String renewalDate = DateFormat.getDateInstance().format(new Date(nextRenewalMillis));



public class SubscriptionActivity extends AppCompatActivity {
    private static final int FLW_REQUEST_CODE = 782;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        findViewById(R.id.btnSubscribe1).setOnClickListener(v -> {
            startFlutterwavePayment();

            String ussd = "*170*1*7*054XXXXXXX*5#";
                String encodedUssd = ussd.replace("#", Uri.encode("#"));
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedUssd));

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    startActivity(intent);
                }

            long currentTime = System.currentTimeMillis();
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            prefs.edit().putLong("login_timestamp", currentTime).apply();
            prefs.edit().putLong("subscribed_at", System.currentTimeMillis()).apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.btnSubscribe3).setOnClickListener(v -> {
//            launchFlutterwave();

            long currentTime = System.currentTimeMillis();
            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
            prefs.edit().putLong("login_timestamp", currentTime).apply();
            prefs.edit().putLong("subscribed_at", System.currentTimeMillis()).apply();

            long expiry = prefs.getLong("sub_expiry", 0);
            if (System.currentTimeMillis() > expiry) {
                startActivity(new Intent(this, SubscriptionActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

        });
    }

//    private void launchFlutterwave() {
//        new RavePayManager(this)
//                .setAmount(5)
//                .setCountry("GH")
//                .setCurrency("GHS")
//                .setEmail("user@example.com")  // Replace with logged-in user email
//                .setfName("TS")
//                .setlName("Automate")
//                .setNarration("Monthly Subscription")
//                .setPublicKey("FLWPUBK-xxxxxxxxxx") // 🔑 Your Public Key
//                .setEncryptionKey("FLWSECK-xxxxxxxxxx") // 🔐 Your Encryption Key
//                .setTxRef("TSBFSUB_" + System.currentTimeMillis())
//                .acceptCardPayments(true)
//                .acceptMpesaPayments(false)
//                .acceptGHMobileMoneyPayments(true)
//                .onStagingEnv(false) // Change to true for sandbox testing
//                .initialize();
//    }
    private void markSubscriptionActive() {
        long now = System.currentTimeMillis();
        long thirtyDays = 30L * 24 * 60 * 60 * 1000;

        getSharedPreferences("auth", MODE_PRIVATE).edit()
                .putLong("sub_expiry", now + thirtyDays)
                .apply();
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You must subscribe to continue", Toast.LENGTH_SHORT).show();
    }
    private void startFlutterwavePayment() {
        String txRef = "tsapp_" + System.currentTimeMillis();

        new RaveUiManager(this)
                .setAmount(5.00)
                .setCurrency("GHS")
                .setEmail(GoogleSignIn.getLastSignedInAccount(this).getEmail())
                .setfName("TS")
                .setlName("Automate")
                .setNarration("Subscription Payment")
                .setPublicKey("FLWPUBK_TEST-XXXXXXXXXX") // your test public key
                .setEncryptionKey("FLWSECK_TEST-XXXXXXXXXX") // your test encryption key
                .setTxRef(txRef)
                .acceptGHMobileMoneyPayments(true)
                .acceptCardPayments(true)
                .acceptUssdPayments(true)
                .onStagingEnv(true)
                //.withTheme(R.style.MyCustomTheme) // optional styling
                .initialize();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.hasExtra("response")) {
            String status = data.getStringExtra("response");
            if (status != null && status.contains("\"status\":\"successful\"")) {
                // Save subscription date
                SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                prefs.edit().putLong("subscribed_at", System.currentTimeMillis()).apply();

                Toast.makeText(this, "Payment successful! Thank you.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Payment failed or canceled. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
//     if (requestCode == FLW_REQUEST_CODE && data != null) {
//        String response = data.getStringExtra("response");
//        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
//            // ✅ Payment successful, continue
//            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
//            markSubscriptionActive(); // Implement this to save 30-day window
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        } else if (resultCode == RavePayActivity.RESULT_ERROR) {
//            Toast.makeText(this, "Payment error", Toast.LENGTH_SHORT).show();
//        } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
//            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
//        }
//    } else {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        /*
//         *  We advise you to do a further verification of transaction's details on your server to be
//         *  sure everything checks out before providing service or goods.
//         */
//        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
//            String message = data.getStringExtra("response");
//            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
//                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
//                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
//                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
