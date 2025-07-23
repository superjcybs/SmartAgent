package com.example.smartagent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> transactionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        listView = findViewById(R.id.transaction_list);
        transactionList = new ArrayList<>();

        loadTransactions();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactionList);
        listView.setAdapter(adapter);
    }

    private void loadTransactions() {
        SharedPreferences prefs = getSharedPreferences("transaction_log", MODE_PRIVATE);
        Set<String> savedLogs = prefs.getStringSet("logs", new HashSet<>());
        transactionList.clear();
        transactionList.addAll(savedLogs);
    }

    public static void saveTransaction(SharedPreferences prefs, String label) {
        Set<String> logs = prefs.getStringSet("logs", new HashSet<>());
        Set<String> updatedLogs = new HashSet<>(logs);
        updatedLogs.add(label);
        prefs.edit().putStringSet("logs", updatedLogs).apply();
    }
}