package com.example.smartagent;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyManager.UssdResponseCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.text.InputFilter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final String TAG = "USSD_APP";

    EditText phoneInput, amountInput;
    RadioGroup simGroup;

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PERMISSION);
        }
    }
    private boolean isAccessibilityServiceEnabled(Class<?> service) {
        int accessibilityEnabled = 0;
        final String serviceId = getPackageName() + "/" + service.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String enabledService = splitter.next();
                    if (enabledService.equalsIgnoreCase(serviceId)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    private void resetTransactionContext() {
        SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

checkAndRequestPermissions();
        if (!isAccessibilityServiceEnabled(UssdAccessibilityService.class)) {
            promptEnableAccessibility();
        }

        phoneInput = findViewById(R.id.phoneNumber);
        amountInput = findViewById(R.id.amountInput);
        simGroup = findViewById(R.id.simGroup);

        phoneInput.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
        });

        TextView helpTextView = findViewById(R.id.help);
        helpTextView.setOnClickListener(v -> {
            showHelpDialog();
        });

        TextView historyView = findViewById(R.id.hwehistory);
        historyView.setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
        });

        TextView advertView = findViewById(R.id.advert);
        advertView.setOnClickListener(v -> {
            startActivity(new Intent(this, AdvertListActivity.class));
        });

//        Button historyButton = findViewById(R.id.history);
//        historyButton.setOnClickListener(v -> {
//            startActivity(new Intent(this, TransactionHistoryActivity.class));
//        });

        findViewById(R.id.checkbalance).setOnClickListener(v -> {
            if (collectTransactionInputs("checkbalance")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
        });

        findViewById(R.id.cashin).setOnClickListener(v -> {
            if (collectTransactionInputs("cashin")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
        });

        findViewById(R.id.cashout).setOnClickListener(v -> {
            if(collectTransactionInputs("cashout")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
        });

        findViewById(R.id.paytoagent).setOnClickListener(v -> {
            if (collectTransactionInputs("paytoagent")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
        });

        findViewById(R.id.paytomerchant).setOnClickListener(v -> {
            if (collectTransactionInputs("paytomerchant")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
        });

        findViewById(R.id.sellairtime).setOnClickListener(v -> {
            if (collectTransactionInputs("sellairtime")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*171#", simSelection);
            }
            Toast.makeText(this, "Sell Airtime flow not yet implemented.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.selldata).setOnClickListener(v -> {
            if (collectTransactionInputs("selldata")) {
                String simSelection = getSimSelection();
                triggerUssdWithSim("*138#", simSelection);
            }
            sendUssdFallback("*138#");
        });

        findViewById(R.id.customussd).setOnClickListener(v -> {
            showCustomUssdDialog();
        });
    }

    /**
     * Collects phone, amount, sim selection once and stores in SharedPreferences
     */
    private boolean collectTransactionInputs(String flowType) {
        String phone = phoneInput.getText().toString().trim().replaceAll("\\s+", "");
        String amount = amountInput.getText().toString().trim();

        // For non-checkbalance flows, validate phone and amount
        if (!flowType.equals("checkbalance")) {
            if (phone.length() != 10) {
                Toast.makeText(this, "Enter valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (amount.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        int selectedSimId = simGroup.getCheckedRadioButtonId();
        if (selectedSimId == -1) {
            Toast.makeText(this, "Select SIM", Toast.LENGTH_SHORT).show();
            return false;
        }

        String simSelected = (selectedSimId == R.id.sim1) ? "SIM1" : "SIM2";

        // Save to SharedPreferences for AccessibilityService to use
        SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
        prefs.edit()
                .putString("flowType", flowType)
                .putString("phone", phone)
                .putString("amount", amount)
                .putString("sim", simSelected)
                .putInt("step", 0)
                .apply();

        Log.d(TAG, "Flow: " + flowType + ", Phone: " + phone + ", Amount: " + amount + ", SIM: " + simSelected);
        return true;
    }


    /**
     * Gets SIM selection from SharedPreferences
     */
    private String getSimSelection() {
        SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
        return prefs.getString("sim", "SIM1");
    }

    /**
     * Dual-SIM USSD dialing using TelecomManager
     */
    private void triggerUssdWithSim(String ussdCode, String simSelection) {
        String encodedHash = Uri.encode("#");
        String ussd = ussdCode.replace("#", encodedHash);
        Uri uri = Uri.parse("tel:" + ussd);

        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            return;
        }

        List<PhoneAccountHandle> phoneAccountHandles = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            phoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
        }

        if (phoneAccountHandles == null || phoneAccountHandles.isEmpty()) {
            Toast.makeText(this, "No SIM available for calling", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAccountHandle handle = null;

        if (simSelection.equals("SIM1")) {
            handle = phoneAccountHandles.get(0);
        } else if (simSelection.equals("SIM2")) {
            if (phoneAccountHandles.size() > 1) {
                handle = phoneAccountHandles.get(1);
            } else {
                Toast.makeText(this, "SIM2 not found. Using SIM1.", Toast.LENGTH_SHORT).show();
                handle = phoneAccountHandles.get(0);
            }
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL, uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            callIntent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle);
        }
// ✅ Clear the inputs AFTER triggering
        phoneInput.setText("");
        amountInput.setText("");
        startActivity(callIntent);
    }

//    private void triggerUssdCode(String ussdCode) {
//        String encodedHash = Uri.encode("#");
//        String ussd = ussdCode.replace("#", encodedHash);
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd));
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
//        } else {
//            startActivity(intent);
//        }
//    }


    /**
     * Fallback USSD dial using sendUssdRequest (Android 8+)
     */
    private void sendUssdFallback(String ussdCode) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.sendUssdRequest(ussdCode, new UssdResponseCallback() {
                        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, String response) {
                            Log.d(TAG, "USSD Response: " + response);
                            Toast.makeText(MainActivity.this, "Response: " + response, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                            Log.e(TAG, "USSD Failed: " + failureCode);
                            triggerUssdWithSim(ussdCode, "SIM1"); // Default to SIM1 fallback
                        }
                    }, new Handler(Looper.getMainLooper()));
                }
            } catch (Exception e) {
                Log.e(TAG, "USSD Exception: " + e.getMessage());
                triggerUssdWithSim(ussdCode, "SIM1");
            }
        } else {
            triggerUssdWithSim(ussdCode, "SIM1");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendUssdFallback("*124#");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showCustomUssdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter USSD Code");

        EditText input = new EditText(this);
        input.setHint("*123#");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!code.startsWith("*") || !code.endsWith("#")) {
                Toast.makeText(this, "Invalid USSD format", Toast.LENGTH_SHORT).show();
            } else {
                String simSelection = getSimSelection();
                triggerUssdWithSim(code, simSelection);
//                sendUssdFallback(code);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void promptEnableAccessibility() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Accessibility Service")
                .setMessage("To automate USSD responses, please enable Accessibility for TS Automate.")
                .setPositiveButton("Enable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setTitle("Describe your issue (max 140 characters)");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
        input.setHint("Type your issue here...");
        input.setPadding(32, 24, 32, 24);
        input.setBackgroundResource(R.drawable.custom_edittext_bg); // optional custom bg
        builder.setView(input);

        builder.setIcon(R.drawable.ic_help); // your drawable icon for help

        builder.setPositiveButton("Send", (dialog, which) -> {
            String message = input.getText().toString().trim();

            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            sendToWhatsApp(message);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendToWhatsApp(String message) {
        String phoneNumber = "233247792110"; // Ghana number in international format
        String encodedMsg = Uri.encode(message);
        String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(message);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // Try regular WhatsApp
        if (isAppInstalled("com.whatsapp")) {
            intent.setPackage("com.whatsapp");
        }
        // Try WhatsApp Business
        else if (isAppInstalled("com.whatsapp.w4b")) {
            intent.setPackage("com.whatsapp.w4b");
        }
        else {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show();
        }
        // Check if WhatsApp is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
