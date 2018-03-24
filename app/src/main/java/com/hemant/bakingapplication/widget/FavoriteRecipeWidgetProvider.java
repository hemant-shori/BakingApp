package com.hemant.bakingapplication.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.hemant.bakingapplication.R;
import com.hemant.bakingapplication.activities.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FavoriteRecipeWidgetProvider extends AppWidgetProvider {
    public static final String WIDGET_INGREDIENTS_LIST_KEY = "WIDGET_INGREDIENTS_LIST_KEY";
    public static final String WIDGET_INGREDIENT_NAME_KEY = "WIDGET_INGREDIENT_NAME_KEY";

    private static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateSingleAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    private static void updateSingleAppWidget(Context context, AppWidgetManager appWidgetManager,
                                              int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_recipe_widget);
        String recipeName = PreferenceManager.getDefaultSharedPreferences(context).getString(WIDGET_INGREDIENT_NAME_KEY, context.getString(R.string.SelectFavoriteRecipeHere));


        if (!TextUtils.equals(recipeName, context.getString(R.string.SelectFavoriteRecipeHere))) {
            recipeName = String.format("%s %s",recipeName, context.getString(R.string.Ingredients));
            //setup recipes listView
            Intent remoteServiceIntent = new Intent(context, RecipeWidgetService.class);
            remoteServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.lv_widget_recipe_steps, remoteServiceIntent);
        } else {
            views.setEmptyView(R.id.lv_widget_recipe_steps, R.id.appwidget_empty_tv);
        }
        views.setTextViewText(R.id.tv_widget_recipe_name, recipeName);

        //call mainActivity as pending Intent
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.recipe_widget_frame, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void forceUpdateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }
}

