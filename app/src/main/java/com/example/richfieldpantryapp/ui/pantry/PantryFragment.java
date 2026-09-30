package com.example.richfieldpantryapp.ui.pantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.settings.SettingsManager;
import com.example.richfieldpantryapp.util.Formatters;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class PantryFragment extends Fragment implements PantryAdapter.Listener {

    private PantryRepo repository;
    private SettingsManager settings;
    private PantryAdapter adapter;
    private TextView emptyView;
    private TextView expiringBanner;
    private List<PantryItem> currentItems = new ArrayList<>();
    private boolean loaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = PantryRepo.getInstance(requireContext());
        settings = new SettingsManager(requireContext());

        emptyView = view.findViewById(R.id.tv_empty);
        expiringBanner = view.findViewById(R.id.tv_expiring_banner);

        RecyclerView recycler = view.findViewById(R.id.recycler_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));

        repository.getPantryItems().observe(getViewLifecycleOwner(), items -> {
            currentItems = items != null ? items : new ArrayList<>();
            loaded = true;
            render();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        if (adapter == null || !loaded) {
            return;
        }
        boolean highlight = settings.isHighlightExpiring();
        int windowDays = settings.getExpiringWindowDays();
        adapter.setItems(currentItems, highlight, windowDays);
        emptyView.setVisibility(currentItems.isEmpty() ? View.VISIBLE : View.GONE);

        int expiringSoon = 0;
        for (PantryItem item : currentItems) {
            if (item.expiryEpochMillis != null) {
                int days = Formatters.daysUntil(item.expiryEpochMillis);
                if (days >= 0 && days <= windowDays) {
                    expiringSoon++;
                }
            }
        }
        if (highlight && expiringSoon > 0) {
            expiringBanner.setText(getResources().getQuantityString(
                    R.plurals.expiring_banner, expiringSoon, expiringSoon));
            expiringBanner.setVisibility(View.VISIBLE);
        } else {
            expiringBanner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(PantryItem item) {
        // Pass the row id to the edit screen through the Intent
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.id);
        startActivity(intent);
    }

    @Override
    public void onDelete(final PantryItem item) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, item.name))
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    repository.deletePantryItem(item);
                    Toast.makeText(requireContext(), R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
