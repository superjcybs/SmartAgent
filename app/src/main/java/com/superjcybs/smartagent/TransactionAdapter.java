package com.superjcybs.smartagent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class TransactionAdapter extends ArrayAdapter<TransactionItem> {
    private Context context;
    private List<TransactionItem> transactions;

    public TransactionAdapter(Context context, List<TransactionItem> transactions) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        }

        TransactionItem item = transactions.get(position);

        TextView tnxPhone = convertView.findViewById(R.id.tnx_phone);
        TextView tnxDate = convertView.findViewById(R.id.tnx_date);
        TextView tnxNetwork = convertView.findViewById(R.id.tnx_network);
        TextView tnxType = convertView.findViewById(R.id.tnx_type);
        TextView tnxAmount = convertView.findViewById(R.id.tnx_amount);
        TextView tnxStatus = convertView.findViewById(R.id.tnx_status);
        Button markSuccessful = convertView.findViewById(R.id.btn_mark_successful);
        Button markUnsuccessful = convertView.findViewById(R.id.btn_mark_unsuccessful);

        tnxPhone.setText(item.phone);
        tnxDate.setText(item.dateTime);
        tnxType.setText(item.tnxtype);
        tnxNetwork.setText(item.network);
        tnxAmount.setText(item.amount);
        tnxStatus.setText(item.status);


        // Set initial state of the "Mark Successful" button
        if ("successful".equalsIgnoreCase(item.status)) { // Or use your constant
            markSuccessful.setEnabled(false);
            // markUnsuccessful.setEnabled(true); // If applicable
        } else {
            markSuccessful.setEnabled(true);
            // markUnsuccessful.setEnabled(true); // Or based on its own logic
        }

        // Set initial state of the "Mark Unsuccessful" button (similar logic)
        if ("unsuccessful".equalsIgnoreCase(item.status)) { // Or use your constant
            markUnsuccessful.setEnabled(false);
            // markSuccessful.setEnabled(true); // If applicable
        } else {
            markUnsuccessful.setEnabled(true);
            // markSuccessful.setEnabled(true); // Or based on its own logic
        }

        markSuccessful.setOnClickListener(v -> {

            // Optional: Show a confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to mark this as successful?")
                    .setPositiveButton("Yes", (dialog, which) -> {
            item.status = "successful";
                        tnxStatus.setText(item.status);
                        markSuccessful.setEnabled(false);
                        Toast.makeText(context, "Marked as successful", Toast.LENGTH_SHORT).show();
                        // Potentially save to database or trigger other updates
                    })
                    .setNegativeButton("No", null)
                    .show();
//            Toast.makeText(context, "Marked as successful", Toast.LENGTH_SHORT).show();
        });

        markUnsuccessful.setOnClickListener(v -> {
            // You'll likely want a similar confirmation dialog here too
            new AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to mark this as unsuccessful?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        item.status = "unsuccessful";
                        tnxStatus.setText(item.status);
                        markUnsuccessful.setEnabled(false);
                        markSuccessful.setEnabled(true); // Enable the other button
                        Toast.makeText(context, "Marked as unsuccessful", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}
