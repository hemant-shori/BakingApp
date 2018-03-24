package com.hemant.bakingapplication.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.models.RecipeStep;

import java.util.ArrayList;

public class RecipeMasterListAdapter extends RecyclerView.Adapter<RecipeMasterListAdapter.MasterListViewHolder> {
    private int mSelectedItem = 0;

    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(int position);
    }

    private final OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private final ArrayList<RecipeStep> recipeStepArrayList;

    public RecipeMasterListAdapter(ArrayList<RecipeStep> recipeStepArrayList, OnRecyclerViewItemClickListener onRecyclerViewItemClickListener, int selectedItemPosition) {
        this.recipeStepArrayList = recipeStepArrayList;
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
        this.mSelectedItem = selectedItemPosition;
    }

    @NonNull
    @Override
    public MasterListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_steps_master_list_item_text_view, parent, false);
        return new MasterListViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterListViewHolder holder, int position) {
        RecipeStep recipeStep = recipeStepArrayList.get(position);
        if (mSelectedItem == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
        holder.bind(recipeStep.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (recipeStepArrayList == null) {
            return 0;
        } else {
            return recipeStepArrayList.size();
        }
    }

    public class MasterListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View itemView;

        MasterListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        void bind(String shortDescription) {
            ((TextView) itemView).setText(shortDescription);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_recipe_step_master) {
                mSelectedItem = getAdapterPosition();
                onRecyclerViewItemClickListener.onItemClicked(getAdapterPosition());
                notifyDataSetChanged();
            }
        }
    }
}
