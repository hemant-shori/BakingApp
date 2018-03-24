package com.hemant.bakingapplication.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.models.Recipe;

import java.util.ArrayList;

public class BakingRecipesAdapter extends RecyclerView.Adapter<BakingRecipesAdapter.GridRecipesViewHolder> {
    public interface OnRecipesClickListener {
        void onItemClicked(Recipe recipe);
    }

    private ArrayList<Recipe> recipes;
    private final OnRecipesClickListener onRecipesClickListener;

    public BakingRecipesAdapter(OnRecipesClickListener onRecipesClickListener) {
        this.recipes = null;
        this.onRecipesClickListener = onRecipesClickListener;
    }

    public void swapRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GridRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_view, parent, false);
        return new GridRecipesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GridRecipesViewHolder holder, int position) {
        holder.recipeNameTextView.setText(recipes.get(holder.getAdapterPosition()).getName());
        holder.servingTextView.setText(recipes.get(holder.getAdapterPosition()).getServing());
        Glide.with(holder.itemView.getContext())
                .load(Uri.parse(recipes.get(holder.getAdapterPosition()).getRecipePosterURL()))
                .into(holder.recipePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (recipes == null)
            return 0;
        else
            return recipes.size();
    }

    public class GridRecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView recipePosterImageView;
        final TextView servingTextView, recipeNameTextView;

        GridRecipesViewHolder(View itemView) {
            super(itemView);
            servingTextView = itemView.findViewById(R.id.tv_servings);
            recipeNameTextView = itemView.findViewById(R.id.tv_recipe_name);
            recipePosterImageView = itemView.findViewById(R.id.iv_recipe_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecipesClickListener.onItemClicked(recipes.get(getAdapterPosition()));
        }
    }
}
