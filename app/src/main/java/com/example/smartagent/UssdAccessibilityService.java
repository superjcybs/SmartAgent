package com.example.smartagent;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import android.util.Log;
import java.util.List;

public class UssdAccessibilityService extends AccessibilityService {
        private static final String TAG = "USSD_SERVICE";

    private void resetTransactionContext() {
        SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            int eventType = event.getEventType();
            String textEvent = event.getText().toString();

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

                List<CharSequence> texts = event.getText();
                if (texts.isEmpty()) {
//                if (texts == null || texts.isEmpty()) {
                    Log.d(TAG, "Event has no text");
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (CharSequence cs : texts) {
                    builder.append(cs.toString());
                }

                String text = builder.toString();
                Log.d(TAG, "Captured Text: " + text);

                handleUssdFlow(text);
            }
        }

        private void handleUssdFlow(String text) {
            SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
            String flowType = prefs.getString("flowType", "");
            String phone = prefs.getString("phone", "");
            String amount = prefs.getString("amount", "");
            String sim = prefs.getString("sim", "");
            int step = prefs.getInt("step", 0);

//            String text = event.getText().toString();
            Log.d(TAG, "Flow: " + flowType + " | Step: " + step + " | Text: " + text);

            if (flowType.equals("cashin")) {
                if (text.contains("Cash In") && step == 0) {
                    performInput("3");
                    updateStep(1);
                } else if (text.contains("Mobile Money User") && step == 1) {
                    performInput("1");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
                    performInput(phone);
                    updateStep(4);
                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
                    performInput(amount);
                    updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Cash-In to " + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);

                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
//                } else if (text.contains("Enter MM PIN") && step == 5) {
//                    Toast.makeText(this, "Ready to confirm. Please approve manually.", Toast.LENGTH_LONG).show();
//                    updateStep(0);
                }
            }

            else if (flowType.equals("cashout")) {
                if (text.contains("Cash Out") && step == 0) {
                    performInput("2");
                    updateStep(1);
                } else if (text.contains("Mobile Money User") && step == 1) {
                    performInput("1");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
                    performInput(phone);
                    updateStep(4);
                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
                    performInput(amount);
                    updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Cash-Out to " + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);


                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
                } else if (text.contains("Confirm") && step == 4) {
                    Toast.makeText(this, "Ready to confirm. Please approve manually.", Toast.LENGTH_LONG).show();
                    updateStep(0);
                }
            }

            else if (flowType.equals("paytoagent")) {
                if (text.contains("Pay To") && step == 0) {
                    performInput("1");
                    updateStep(1);
                } else if (text.contains("Agent") && step == 1) {
                    performInput("1");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
                    performInput(phone);
                    updateStep(4);
                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
                    performInput(amount);
                    updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Pay-To-Agent" + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);

                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
                } else if (text.contains("Confirm") && step == 5) {
                    Toast.makeText(this, "Ready to confirm. Please approve manually.", Toast.LENGTH_LONG).show();
                    updateStep(0);
                }
            }

            else if (flowType.equals("paytomerchant")) {
                if (text.contains("Pay To") && step == 0) {
                    performInput("1");
                    updateStep(1);
                } else if (text.toLowerCase().contains("merchant") && step == 1) {
                    performInput("2");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter merchant id")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("enter amount")) && step == 3) {
                    performInput(amount);
                    updateStep(4);
                }else if ((text.toLowerCase().contains("reference")) && step == 4) {
                        performInput("1");
                        updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Pay-To-Merchant " + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);

                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
                }
            }
            else if (flowType.equals("sellairtime")) {
                if (text.contains("Airtime&Bundles") && step == 0) {
                    performInput("5");
                    updateStep(1);
                } else if (text.contains("Sell Airtime") && step == 1) {
                    performInput("1");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
                    performInput(phone);
                    updateStep(4);
//                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
//                    performInput(amount);
//                    updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Airtime-sale " + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);

                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
                }
            }
            else if (flowType.equals("selldata")) {
                if (text.contains("Airtime&Bundles") && step == 0) {
                    performInput("5");
                    updateStep(1);
                } else if (text.contains("MTNBundle") && step == 1) {
                    performInput("2");
                    updateStep(2);
                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
                    performInput(phone);
                    updateStep(3);
                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
                    performInput(phone);
                    updateStep(4);
//                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
//                    performInput(amount);
//                    updateStep(5);

                    // ✅ Log transaction after amount is entered
                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
                    String label = "Data-sale to " + phone + " - GH₵" + amount;
                    TransactionHistoryActivity.saveTransaction(prefs, label);
                    Log.d("USSD_SERVICE", "Transaction logged: " + label);

                    // Reset shared preferences after cash-in flow is done
                    resetTransactionContext();

                    updateStep(0);
                }
            }
             else if (flowType.equals("checkbalance")) {
                if (text.toLowerCase().contains("my wallet") && step == 0) {
                    performInput("7");
                    updateStep(1);
                } else if (text.toLowerCase().contains("check balance") && step == 1) {
                    performInput("1");
                    updateStep(2);
                }
                // Reset shared preferences after cash-in flow is done
                resetTransactionContext();
                updateStep(0);
            }
//             else if (flowType.equals("customussd")) {
//                if (text.contains("Cash In") && step == 0) {
//                    performInput("3");
//                    updateStep(1);
//                } else if (text.contains("Mobile Money User") && step == 1) {
//                    performInput("1");
//                    updateStep(2);
//                } else if ((text.toLowerCase().contains("enter mobile number")) && step == 2) {
//                    performInput(phone);
//                    updateStep(3);
//                } else if ((text.toLowerCase().contains("repeat mobile number")) && step == 3) {
//                    performInput(phone);
//                    updateStep(4);
//                } else if ((text.toLowerCase().contains("enter amount")) && step == 4) {
//                    performInput(amount);
//                    updateStep(5);
//
//                    // ✅ Log transaction after amount is entered
//                    prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
//                    String label = "Cash-In to " + phone + " - GH₵" + amount;
//                    TransactionHistoryActivity.saveTransaction(prefs, label);
//                    Log.d("USSD_SERVICE", "Transaction logged: " + label);
//
//                }
//            }
        }

        private void performInput(String value) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode == null) {
                Log.d(TAG, "Root window is null, cannot input");
                return;
            }

            // Recursively set input
            findAndSetEditText(rootNode, value);

            // Click Send button if available
            List<AccessibilityNodeInfo> sendButtons = rootNode.findAccessibilityNodeInfosByText("Send");
            for (AccessibilityNodeInfo button : sendButtons) {
                if (button.isClickable() && button.getClassName().equals("android.widget.Button")) {
                    button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "Send button clicked");
                    break;
                }
            }
        }

    private void findAndSetEditText(AccessibilityNodeInfo node, String value) {
        if (node == null) return;

        if ("android.widget.EditText".contentEquals(node.getClassName())) {
            if (node.isEnabled() && node.isFocusable()) {
                Bundle args = new Bundle();
                args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value);
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
                Log.d(TAG, "Input set: " + value);
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findAndSetEditText(node.getChild(i), value);
        }
    }
//            boolean inputSuccess = false;
//
//            List<AccessibilityNodeInfo> inputFields = rootNode.findAccessibilityNodeInfosByClassName("android.widget.EditText");
//            for (AccessibilityNodeInfo inputField : inputFields) {
//                if (inputField.isEnabled() && inputField.isFocusable()) {
//                    Bundle args = new Bundle();
//                    args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value);
//                    inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
//                    inputSuccess = true;
//                    Log.d(TAG, "Input set: " + value);
//                }
//            }
//
//            List<AccessibilityNodeInfo> sendButtons = rootNode.findAccessibilityNodeInfosByText("Send");
//            for (AccessibilityNodeInfo button : sendButtons) {
//                if (button.isClickable() && button.getClassName().equals("android.widget.Button")) {
//                    button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d(TAG, "Send button clicked");
//                    break;
//                }
//            }
//
//            if (!inputSuccess) {
//                Log.d(TAG, "No input field found to set text");
//            }


        private void updateStep(int newStep) {
            SharedPreferences prefs = getSharedPreferences("ussd_context", MODE_PRIVATE);
            prefs.edit().putInt("step", newStep).apply();
            Log.d(TAG, "Step updated to: " + newStep);
        }

        @Override
        public void onInterrupt() {
            Log.d(TAG, "Service Interrupted");
        }
}
