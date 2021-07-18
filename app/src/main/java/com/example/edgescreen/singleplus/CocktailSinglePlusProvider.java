
package com.example.edgescreen.singleplus;

import com.example.edgescreen.R;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

public class CocktailSinglePlusProvider extends SlookCocktailProvider {

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager, int[] cocktailIds) {
        panelUpdate(context, cocktailManager, cocktailIds);
    }

    public void panelUpdate(Context context, SlookCocktailManager manager, int[] cocktailIds) {
        int layoutId = R.layout.sample_edge_single_plus;
        RemoteViews rv = new RemoteViews(context.getPackageName(), layoutId);
        rv.setViewVisibility(R.id.main_background, View.VISIBLE);
        rv.setImageViewResource(R.id.main_background, R.drawable.apps_edge);
        if (cocktailIds != null) {
            for (int id : cocktailIds) {
                manager.updateCocktail(id, rv);
            }
        }
    }

}
