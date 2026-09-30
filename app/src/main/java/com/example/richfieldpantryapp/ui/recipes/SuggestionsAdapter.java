package com.example.richfieldpantryapp.ui.recipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.matching.IngredientStatus;
import com.example.richfieldpantryapp.matching.RecipeMatch;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeMatch match);
    }

    private final List<SuggestionRow> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public SuggestionsAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRows(List<SuggestionRow> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SuggestionRow.TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_section_header, parent, false));
        } else if (viewType == SuggestionRow.TYPE_EMPTY) {
            return new EmptyViewHolder(inflater.inflate(R.layout.item_empty_state, parent, false));
        }
        return new RecipeViewHolder(inflater.inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SuggestionRow row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText(row.text);
        } else if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).message.setText(row.text);
        } else if (holder instanceof RecipeViewHolder && row.match != null) {
            bindRecipe((RecipeViewHolder) holder, row.match);
        }
    }

    private void bindRecipe(RecipeViewHolder holder, final RecipeMatch match) {
        Context context = holder.itemView.getContext();
        holder.name.setText(match.recipe.recipe.name);
        holder.subtitle.setText(describe(context, match));
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(match));
    }

    private static String describe(Context context, RecipeMatch match) {
        int total = match.statuses.size();
        if (match.canMake()) {
            String text = context.getResources().getQuantityString(R.plurals.recipe_have_all, total, total);
            if (match.usesApproximateConversion()) {
                text += "\n" + context.getString(R.string.approx_units_note);
            }
            return text;
        }
        List<String> missing = new ArrayList<>();
        List<String> notEnough = new ArrayList<>();
        for (IngredientStatus status : match.gaps()) {
            if (status.state == IngredientStatus.State.MISSING) {
                missing.add(status.ingredient.name);
            } else {
                notEnough.add(status.ingredient.name);
            }
        }
        List<String> parts = new ArrayList<>();
        if (!missing.isEmpty()) {
            parts.add(context.getString(R.string.recipe_missing, TextUtils.join(", ", missing)));
        }
        if (!notEnough.isEmpty()) {
            parts.add(context.getString(R.string.recipe_not_enough, TextUtils.join(", ", notEnough)));
        }
        return TextUtils.join("\n", parts);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_section_title);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        final TextView message;

        EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_empty_message);
        }
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView subtitle;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_recipe_name);
            subtitle = itemView.findViewById(R.id.tv_recipe_subtitle);
        }
    }
}
