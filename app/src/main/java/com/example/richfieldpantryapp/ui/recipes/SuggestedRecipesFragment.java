package com.example.richfieldpantryapp.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SuggestedRecipesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance) {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Suggested Recipes Screen");
        placeholder.setPadding(48,48,48,48);
        return placeholder;
    }
}
