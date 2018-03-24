package com.hemant.bakingapplication.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hemant.bakingapplication.R;

import static com.hemant.bakingapplication.widget.FavoriteRecipeWidgetProvider.WIDGET_INGREDIENTS_LIST_KEY;

class ListProviderViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private String[] recipeSteps = new String[]{};
    private Context context = null;

    @SuppressWarnings("unused")
    ListProviderViewFactory(Context context, Intent intent) {
        this.context = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        populateRecipeStepsFromSharedPreferences(context);
    }

    private void populateRecipeStepsFromSharedPreferences(Context context) {
        String selectedRecipe = PreferenceManager.getDefaultSharedPreferences(context).getString(WIDGET_INGREDIENTS_LIST_KEY, "");
        if (!TextUtils.isEmpty(selectedRecipe)) {
            recipeSteps = selectedRecipe.split("\\$");
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return recipeSteps.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_steps_master_list_item_text_view);
        remoteViews.setTextViewText(R.id.tv_recipe_step_master, recipeSteps[position]);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
