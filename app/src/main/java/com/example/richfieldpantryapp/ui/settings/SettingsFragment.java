package com.example.richfieldpantryapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.settings.SettingsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {
    private SettingsManager settings;
    private PantryRepo repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settings = new SettingsManager(requireContext());
        repository = PantryRepo.getInstance(requireContext());

        MaterialSwitch highlight = view.findViewById(R.id.switch_highlight_expiring);
        highlight.setChecked(settings.isHighlightExpiring());
        highlight.setOnCheckedChangeListener((button, checked) -> settings.setHighlightExpiring(checked));

        final Spinner windowSpinner = view.findViewById(R.id.spinner_expiry_window);
        ArrayAdapter<CharSequence> windowAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.expiry_window_days, android.R.layout.simple_spinner_item);
        windowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windowSpinner.setAdapter(windowAdapter);
        windowSpinner.setSelection(indexOf(windowAdapter, settings.getExpiringWindowDays()));
        windowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                try {
                    settings.setExpiringWindowDays(Integer.parseInt(parent.getItemAtPosition(position).toString()));
                } catch (NumberFormatException ignored) {
                    settings.setExpiringWindowDays(SettingsManager.DEFAULT_EXPIRING_DAYS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing to do
            }
        });

        MaterialSwitch excludeExpired = view.findViewById(R.id.switch_exclude_expired);
        excludeExpired.setChecked(settings.isExcludeExpired());
        excludeExpired.setOnCheckedChangeListener((button, checked) -> settings.setExcludeExpired(checked));

        MaterialSwitch almostThere = view.findViewById(R.id.switch_almost_there);
        almostThere.setChecked(settings.isShowAlmostThere());
        almostThere.setOnCheckedChangeListener((button, checked) -> settings.setShowAlmostThere(checked));

        Button clearPantry = view.findViewById(R.id.btn_clear_pantry);
        clearPantry.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_clear_pantry)
                .setMessage(R.string.settings_clear_pantry_confirm)
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_clear, (dialog, which) -> {
                    repository.clearPantry();
                    Toast.makeText(requireContext(), R.string.toast_pantry_cleared, Toast.LENGTH_SHORT).show();
                })
                .show());
    }

    private static int indexOf(ArrayAdapter<CharSequence> adapter, int value) {
        for (int i = 0; i < adapter.getCount(); i++) {
            CharSequence item = adapter.getItem(i);
            if (item != null && item.toString().equals(String.valueOf(value))) {
                return i;
            }
        }
        return 0;
    }
}
