package com.example.richfieldpantryapp.ui.pantry;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.util.Formatters;

import java.util.ArrayList;
import java.util.List;


public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);

        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean highlightExpiring = true;
    private int expiringWindowDays = 3;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<PantryItem> newItems, boolean highlightExpiring, int expiringWindowDays) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        this.highlightExpiring = highlightExpiring;
        this.expiringWindowDays = expiringWindowDays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PantryItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.name.setText(item.name);
        holder.quantity.setText(Formatters.quantityWithUnit(item.quantity, item.unit));

        if (item.expiryEpochMillis == null) {
            holder.expiry.setText(R.string.no_expiry);
            holder.expiry.setTextColor(holder.defaultExpiryColor);
        } else {
            int days = Formatters.daysUntil(item.expiryEpochMillis);
            holder.expiry.setText(Formatters.expiryLabel(context, item.expiryEpochMillis, days));
            int color;
            if (days < 0) {
                color = ContextCompat.getColor(context, R.color.status_expired);
            } else if (highlightExpiring && days <= expiringWindowDays) {
                color = ContextCompat.getColor(context, R.color.status_expiring);
            } else {
                color = holder.defaultExpiryColor;
            }
            holder.expiry.setTextColor(color);
        }

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final ImageButton edit;
        final ImageButton delete;
        final int defaultExpiryColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            quantity = itemView.findViewById(R.id.tv_item_quantity);
            expiry = itemView.findViewById(R.id.tv_item_expiry);
            edit = itemView.findViewById(R.id.btn_edit);
            delete = itemView.findViewById(R.id.btn_delete);
            defaultExpiryColor = expiry.getCurrentTextColor();
        }
    }
}
