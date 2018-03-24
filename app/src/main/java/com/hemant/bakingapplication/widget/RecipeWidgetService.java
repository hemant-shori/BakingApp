package com.hemant.bakingapplication.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;


public class RecipeWidgetService extends RemoteViewsService {
    @SuppressWarnings("unused")
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        return new ListProviderViewFactory(this.getApplicationContext(), intent);
    }
}
