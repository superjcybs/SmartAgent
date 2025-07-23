package com.example.smartagent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdvertListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdvertAdapter adapter;
    private List<AdvertItem> advertList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_list);

        recyclerView = findViewById(R.id.recyclerViewAdverts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        advertList = new ArrayList<>();
        advertList.add(new AdvertItem("Buy MTN Data - GH₵5", "Contact 054xxxxxxx"));
        advertList.add(new AdvertItem("Sell Airtime - GH₵10", "Call 055xxxxxxx now"));
        advertList.add(new AdvertItem("Low Interest Loans", "Apply today and receive approval in 24 hours!"));
        advertList.add(new AdvertItem("Affordable Data Bundles", "Get 10GB for only GHS 10.00"));
        advertList.add(new AdvertItem("Momo Agent Training", "Become a certified MoMo agent in 3 days."));

        adapter = new AdvertAdapter(advertList, this);
        recyclerView.setAdapter(adapter);
    }

    static class AdvertItem {
        String title;
        String description;

        AdvertItem(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    static class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

        List<AdvertItem> items;

        AdvertAdapter(List<AdvertItem> items, AdvertListActivity advertListActivity) {
            this.items = items;
        }

        @NonNull
        @Override
        public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advert, parent, false);
            return new AdvertViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
            AdvertItem advert = items.get(position);
            holder.title.setText(advert.title);
            holder.description.setText(advert.description);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class AdvertViewHolder extends RecyclerView.ViewHolder {
            TextView title, description;

            AdvertViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.advertTitle);
                description = itemView.findViewById(R.id.advertDescription);
            }
        }
    }
}