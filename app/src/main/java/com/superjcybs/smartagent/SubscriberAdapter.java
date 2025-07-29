package com.superjcybs.smartagent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.SubscriberViewHolder> {

    List<AdminDashboardActivity.Subscriber> list;

    public SubscriberAdapter(List<AdminDashboardActivity.Subscriber> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SubscriberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscriber, parent, false);
        return new SubscriberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriberViewHolder holder, int position) {
        AdminDashboardActivity.Subscriber subscriber = list.get(position);

        holder.email.setText(subscriber.email);
        holder.subscribedAt.setText("Subscribed: " + formatDate(subscriber.subscribedAt));
        holder.nextSub.setText("Next: " + formatDate(subscriber.nextSubscription));

        holder.renew.setOnClickListener(v -> {
            long newNextDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // +30 days

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("subscribers").child(subscriber.userId);
            userRef.child("next_subscription_date").setValue(newNextDate);
            userRef.child("subscribed_at").setValue(System.currentTimeMillis());

            Toast.makeText(v.getContext(), "Renewed for: " + subscriber.email, Toast.LENGTH_SHORT).show();
        });
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SubscriberViewHolder extends RecyclerView.ViewHolder {
        TextView email, subscribedAt, nextSub;
        Button renew;
        public SubscriberViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.textEmail);
            subscribedAt = itemView.findViewById(R.id.textSubscribedAt);
            nextSub = itemView.findViewById(R.id.textNextSub);
            renew = itemView.findViewById(R.id.btnRenew);
        }
    }
}
