package com.superjcybs.smartagent;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.*;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubscriberAdapter adapter;
    private List<Subscriber> subscriberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerView = findViewById(R.id.recyclerViewSubscribers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscriberList = new ArrayList<>();
        adapter = new SubscriberAdapter(subscriberList);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getUid().equals("Vhf95g3gsbdAKU3FO5UwNGNFDZr2")) {
            loadSubscribers();
        } else {
            Toast.makeText(this, "Access denied", Toast.LENGTH_LONG).show();
            finish();
        }


//        loadSubscribers();
    }

    private void loadSubscribers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("subscribers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscriberList.clear();
                long now = System.currentTimeMillis();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String email = child.child("email").getValue(String.class);
                    long subscribedAt = child.child("subscribed_at").getValue(Long.class);
                    long nextDate = child.child("next_subscription_date").getValue(Long.class);

//                    subscriberList.add(new Subscriber(email, subscribedAt, nextDate));

                    // Show only expired users
                    if (nextDate < now) {
                        subscriberList.add(new Subscriber(child.getKey(), email, subscribedAt, nextDate));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class Subscriber {
        String userId;
        String email;
        long subscribedAt;
        long nextSubscription;

        Subscriber(String userId, String email, long subscribedAt, long nextSubscription) {
            this.userId = userId;
            this.email = email;
            this.subscribedAt = subscribedAt;
            this.nextSubscription = nextSubscription;
        }
    }
}
