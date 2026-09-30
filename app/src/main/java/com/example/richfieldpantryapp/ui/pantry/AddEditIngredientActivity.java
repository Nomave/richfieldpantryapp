package com.example.richfieldpantryapp.ui.pantry;

import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.util.Formatters;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID = "com.example.richfieldpantryapp.EXTRA_ITEM";

    private static final String STATE_EXPIRY = "state_expiry";
    private static final double MAX_QUANTITY = 1_000_000;

    private PantryRepo repository;

    private TextInputLayout tilName;
    private TextInputLayout tilQuantity;
    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private Spinner spinnerUnit;
    private TextView tvExpiry;
    private Button btnClearExpiry;

    private long itemId = -1L;
    @Nullable
    private Long expiryMillis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        repository = PantryRepo.getInstance(this);
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1L);
        final boolean editing = itemId >= 0;

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(editing ? R.string.title_edit_ingredient : R.string.title_add_ingredient);
        }

        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        spinnerUnit = findViewById(R.id.spinner_unit);
        tvExpiry = findViewById(R.id.tv_expiry);
        btnClearExpiry = findViewById(R.id.btn_clear_expiry);
        Button btnSetExpiry = findViewById(R.id.btn_set_expiry);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnDelete = findViewById(R.id.btn_delete);

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_EXPIRY)) {
            expiryMillis = savedInstanceState.getLong(STATE_EXPIRY);
        }
        updateExpiryLabel();

        btnSetExpiry.setOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> {
            expiryMillis = null;
            updateExpiryLabel();
        });
        btnSave.setOnClickListener(v -> save());
        btnDelete.setVisibility(editing ? View.VISIBLE : View.GONE);
        btnDelete.setOnClickListener(v -> confirmDelete());

        if (editing && savedInstanceState == null) {
            repository.getPantryItem(itemId, item -> {
                if (item == null || isFinishing() || isDestroyed()) {
                    return;
                }
                populate(item);
            });
        }
    }

    private void populate(PantryItem item) {
        etName.setText(item.name);
        etQuantity.setText(Formatters.quantity(item.quantity));
        selectUnit(item.unit);
        expiryMillis = item.expiryEpochMillis;
        updateExpiryLabel();
    }

    private void selectUnit(String unit) {
        for (int i = 0; i < spinnerUnit.getCount(); i++) {
            Object option = spinnerUnit.getItemAtPosition(i);
            if (option != null && option.toString().equalsIgnoreCase(unit)) {
                spinnerUnit.setSelection(i);
                return;
            }
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (expiryMillis != null) {
            cal.setTimeInMillis(expiryMillis);
        }
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.clear();
            chosen.set(year, month, dayOfMonth);
            expiryMillis = chosen.getTimeInMillis();
            updateExpiryLabel();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void updateExpiryLabel() {
        if (expiryMillis == null) {
            tvExpiry.setText(R.string.no_expiry);
            btnClearExpiry.setVisibility(View.GONE);
        } else {
            tvExpiry.setText(getString(R.string.expiry_set_to, Formatters.date(expiryMillis)));
            btnClearExpiry.setVisibility(View.VISIBLE);
        }
    }

    /** Validates the form; returns the item to save or null if there are errors. */
    @Nullable
    private PantryItem validate() {
        boolean valid = true;

        String name = textOf(etName);
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() > 60) {
            tilName.setError(getString(R.string.error_name_too_long));
            valid = false;
        } else {
            tilName.setError(null);
        }

        double quantity;
        try {
            quantity = Double.parseDouble(textOf(etQuantity).replace(',', '.'));
        } catch (NumberFormatException e) {
            quantity = -1;
        }
        if (quantity <= 0) {
            tilQuantity.setError(getString(R.string.error_quantity_invalid));
            valid = false;
        } else if (quantity > MAX_QUANTITY) {
            tilQuantity.setError(getString(R.string.error_quantity_too_large));
            valid = false;
        } else {
            tilQuantity.setError(null);
        }

        if (!valid) {
            return null;
        }
        Object selectedUnit = spinnerUnit.getSelectedItem();
        String unit = selectedUnit == null ? "pcs" : selectedUnit.toString();
        return new PantryItem(name, quantity, unit, expiryMillis);
    }

    private void save() {
        PantryItem item = validate();
        if (item == null) {
            return;
        }
        if (itemId >= 0) {
            item.id = itemId;
            repository.updatePantryItem(item);
            Toast.makeText(this, R.string.toast_updated, Toast.LENGTH_SHORT).show();
        } else {
            repository.insertPantryItem(item);
            Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, textOf(etName)))
                .setNegativeButton(R.string.btn_cancel, null)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    repository.deletePantryItemById(itemId);
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (expiryMillis != null) {
            outState.putLong(STATE_EXPIRY, expiryMillis);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
