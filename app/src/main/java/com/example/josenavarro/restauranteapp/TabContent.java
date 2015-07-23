package com.example.josenavarro.restauranteapp;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by Jose Navarro on 27/04/2015.
 */
public class TabContent implements TabHost.TabContentFactory {
    private Context mContext;
    public TabContent(Context context){
        mContext = context;
    }
    @Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
}
