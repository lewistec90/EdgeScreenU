
package com.example.edgescreen.singleplus;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.example.edgescreen.R;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;

public class LongClickCocktailProvider extends SlookCocktailProvider {

    private static final String TAG = LongClickCocktailProvider.class.getSimpleName();

    private static final String ACTION_REMOTE_LONG_CLICK = "com.example.cocktailslooksample.action.ACTION_REMOTE_LONGCLICK";

    private static final String ACTION_REMOTE_CLICK = "com.example.cocktailslooksample.action.ACTION_REMOTE_CLICK";

    private static final String ACTION_PULL_TO_REFRESH = "com.example.cocktailslooksample.action.ACTION_PULL_TO_REFRESH";
    static int mLongClickCount = 0;
    static boolean mIsLongClicked = false;

    private static RemoteViews mRemoteListView = null;
    private static RemoteViews mLongClickStateView = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        switch(action) {
            case ACTION_REMOTE_LONG_CLICK:
                performRemoteLongClick(context, intent);
                break;
            case ACTION_REMOTE_CLICK:
                performRemoteClick(context, intent);
                break;
            case ACTION_PULL_TO_REFRESH:
                performPullToRefresh(context);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailManager,
                         int[] cocktailIds) {
        if(mLongClickStateView == null) {
            mLongClickStateView = createStateView(context);
        }
        if(mRemoteListView == null) {
            mRemoteListView = createRemoteListView(context);
        }
        cocktailManager.updateCocktail(cocktailIds[0], mRemoteListView, mLongClickStateView);

        // set pull to refresh
        Intent refreshintent = new Intent(ACTION_PULL_TO_REFRESH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0xff, refreshintent, PendingIntent.FLAG_UPDATE_CURRENT);
        SlookCocktailManager.getInstance(context).setOnPullPendingIntent(cocktailIds[0], R.id.remote_list, pendingIntent);
    }


    @Override
    public void onVisibilityChanged(Context context, int cocktailId, int visibility) {
        super.onVisibilityChanged(context, cocktailId, visibility);
    }

    private RemoteViews createStateView(Context context) {
        RemoteViews stateView = new RemoteViews(context.getPackageName(),
                R.layout.single_plus_long_click_state_layout);
        SlookCocktailManager.getInstance(context).setOnLongClickPendingIntent(stateView, R.id.state_btn1, getLongClickIntent(context, R.id.state_btn1, 0));
        stateView.setOnClickPendingIntent(R.id.state_btn1, getClickIntent(context, R.id.state_btn1, 0));
        stateView.setOnClickPendingIntent(R.id.state_btn2, getClickIntent(context, R.id.state_btn2, 0));
        return stateView;
    }

    private RemoteViews createRemoteListView(Context context) {
        RemoteViews remoteListView = new RemoteViews(context.getPackageName(), R.layout.single_plus_remote_list_view);
        Intent remoteIntent = new Intent(context, LongClickRemoteViewService.class);
        remoteListView.setRemoteAdapter(R.id.remote_list, remoteIntent);
        SlookCocktailManager.getInstance(context).setOnLongClickPendingIntentTemplate(remoteListView, R.id.remote_list, getLongClickIntent(context, R.id.remote_list, 0));
        remoteListView.setPendingIntentTemplate(R.id.remote_list, getClickIntent(context, R.id.remote_list, 0));
        return remoteListView;
    }

    private PendingIntent getLongClickIntent(Context context, int id, int key) {
        Intent longClickIntent = new Intent(ACTION_REMOTE_LONG_CLICK);
        longClickIntent.putExtra("id", id);
        longClickIntent.putExtra("key", key);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, id, longClickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    private PendingIntent getClickIntent(Context context, int id, int key) {
        Intent clickIntent = new Intent(ACTION_REMOTE_CLICK);
        clickIntent.putExtra("id", id);
        clickIntent.putExtra("key", key);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, id, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    private void performRemoteClick(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);
        switch (id) {
            case R.id.state_btn1:
                mIsLongClicked = false;
                updatePanelClickState(context);
                break;
            case R.id.state_btn2:
                SlookCocktailManager cocktailManager = SlookCocktailManager.getInstance(context);
                int[] cocktailIds = cocktailManager.getCocktailIds(new ComponentName(context, LongClickCocktailProvider.class));
                cocktailManager.notifyCocktailViewDataChanged(cocktailIds[0], R.id.remote_list);
                break;
            case R.id.remote_list:
                int itemId = intent.getIntExtra("item_id", -1);
                int itemBgColor = intent.getIntExtra("bg_color", -1);
                String toastString = String.format(context.getResources().getString(R.string.remote_list_item_clicked), itemId);
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void performRemoteLongClick(Context context, Intent intent) {
        StringBuffer debugString = new StringBuffer("ACTION_REMOTE_LONGCLICK");
        int id = intent.getIntExtra("id", -1);
        debugString.append("id=").append(intent.getIntExtra("id", -1));
        Log.d(TAG, debugString.toString());
        switch (id) {
            case R.id.state_btn1:
                mIsLongClicked = true;
                mLongClickCount++;
                updatePanelClickState(context);
                break;
            case R.id.remote_list:
                int itemBgColor = intent.getIntExtra("bg_color", -1);
                String toastString = String.format(context.getResources().getString(R.string.remote_list_item_long_clicked), itemBgColor);
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
                break;
            default:
                break;

        }
    }

    private void performPullToRefresh(Context context) {
        SlookCocktailManager cocktailManager = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = cocktailManager.getCocktailIds(new ComponentName(context, LongClickCocktailProvider.class));

        cocktailManager.notifyCocktailViewDataChanged(cocktailIds[0], R.id.remote_list);
    }

    private void updatePanelClickState(Context context) {
        SlookCocktailManager cocktailManager = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = cocktailManager.getCocktailIds(new ComponentName(context, LongClickCocktailProvider.class));
        if(mLongClickStateView == null) {
            mLongClickStateView = createStateView(context);
        }
        if(mRemoteListView == null) {
            mRemoteListView = createRemoteListView(context);
        }
        String clickStateText = context.getResources().getString(R.string.clicked);
        if (mIsLongClicked) {
            clickStateText = context.getResources().getString(R.string.long_clicked);
        }
        mLongClickStateView.setTextViewText(R.id.display_text1, Integer.toString(mLongClickCount));
        mLongClickStateView.setTextViewText(R.id.display_text2, clickStateText);
        cocktailManager.updateCocktail(cocktailIds[0], mRemoteListView, mLongClickStateView);
    }

}
