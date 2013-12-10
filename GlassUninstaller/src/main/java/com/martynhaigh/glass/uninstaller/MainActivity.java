package com.martynhaigh.glass.uninstaller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

    private AppHelper mAppHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppHelper = new AppHelper(this);
        mAppHelper.loadApplications(true);

        mAppHelper.bindApplications();

        final ListView list = (ListView) findViewById(android.R.id.list);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list.smoothScrollToPositionFromTop(position, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

}
