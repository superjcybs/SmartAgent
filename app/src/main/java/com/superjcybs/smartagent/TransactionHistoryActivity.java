package com.superjcybs.smartagent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private TransactionAdapter adapter;
    private ArrayList<TransactionItem> transactionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listView = findViewById(R.id.transaction_list);
        transactionList = new ArrayList<>();

        loadTransactions();

        adapter = new TransactionAdapter(this, transactionList);
        listView.setAdapter(adapter);
    }

    private void loadTransactions() {
//        transactionList.add(new TransactionItem("0597098435", "MTN", "29-May-2025 09:54 PM", "GHS 1.0", "successful", "Cash In"));
//        transactionList.add(new TransactionItem("0594179848", "MTN", "29-May-2025 06:14 AM", "GHS 20.0", "successful", "Cash In"));

        SharedPreferences prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
        String json = prefs.getString("logs", "[]");

        Gson gson = new Gson();
        ArrayList<TransactionItem> items = gson.fromJson(json, new TypeToken<ArrayList<TransactionItem>>(){}.getType());

        transactionList.clear();
        transactionList.addAll(items);
//        for (TransactionItem item : items) {
//            transactionList.add(item); // later replace with custom adapter
//        }

            // OLD LOADING CODE
//        SharedPreferences prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
//        Set<String> savedLogs = prefs.getStringSet("logs", new HashSet<>());
//        transactionList.clear();
//        transactionList.addAll(savedLogs);

    // Load transactions from SharedPreferences
//        SharedPreferences prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
//        String json = prefs.getString("logs_json", null);
//        if (json != null) {
//            Type type = new TypeToken<List<TransactionItem>>() {}.getType();
//            List<TransactionItem> saved = new Gson().fromJson(json, type);
//            transactionList.clear();
//            transactionList.addAll(saved);
//        }


        // LOADING FROM FIRESTORE
//        db.collection("users")
//                .document(userId)
//                .collection("transactions")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        TransactionItem item = doc.toObject(TransactionItem.class);
//                        transactionList.add(item);
//                    }
//                    adapter.notifyDataSetChanged();
//                });

    }

    public static void saveTransaction(SharedPreferences prefs, TransactionItem item) {
        Gson gson = new Gson();
        String json = prefs.getString("logs", "[]");
        ArrayList<TransactionItem> logs = gson.fromJson(json, new TypeToken<ArrayList<TransactionItem>>(){}.getType());

        logs.add(item);
        String updatedJson = gson.toJson(logs);
        prefs.edit().putString("logs", updatedJson).apply();
//        TransactionItem item = new TransactionItem(phone, network, datetime, amount, status, tnxtype);
//        saveTransaction(item);
//          // save in shared preferences

//            SharedPreferences prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
//            Gson gson = new Gson();
//
//            List<TransactionItem> list = getTransactionListFromPrefs(prefs);
//            list.add(item);
//
//            String json = gson.toJson(list);
//            prefs.edit().putString("logs_json", json).apply();
        }

            // OLD SAVING CODE
//        Set<String> logs = prefs.getStringSet("logs", new HashSet<>());
//        Set<String> updatedLogs = new HashSet<>(logs);
//        updatedLogs.add(label);
//        prefs.edit().putStringSet("logs", updatedLogs).apply();

        // SAVING TO FIRESTORE
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        db.collection("users")
//                .document(userId)
//                .collection("transactions")
//                .add(item) // item should be a Map or POJO
//                .addOnSuccessListener(documentReference -> {
//                    Log.d("Firestore", "Transaction saved!");
//                });
//

}