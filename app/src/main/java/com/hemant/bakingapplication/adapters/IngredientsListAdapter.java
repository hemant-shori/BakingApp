package com.hemant.bakingapplication.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.models.Ingredient;

import java.util.ArrayList;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientsViewHolder> {

    private ArrayList<Ingredient> ingredientArrayList;

    public IngredientsListAdapter(ArrayList<Ingredient> ingredientArrayList) {
        this.ingredientArrayList = ingredientArrayList;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_card_view, parent, false);
        return new IngredientsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientsViewHolder holder, int position) {
        holder.ingredientsQuantityTextView.setText(String.format("%s %s", ingredientArrayList.get(position).getQuantity(), ingredientArrayList.get(position).getMeasure()));
        holder.ingredientNameTextView.setText(ingredientArrayList.get(position).getIngredientName());
    }

    @Override
    public int getItemCount() {
        if (ingredientArrayList == null)
            return 0;
        else
            return ingredientArrayList.size();
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientNameTextView, ingredientsQuantityTextView;

        IngredientsViewHolder(View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.tv_ingredient_name);
            ingredientsQuantityTextView = itemView.findViewById(R.id.tv_ingredient_quantity);
        }

    }
}
