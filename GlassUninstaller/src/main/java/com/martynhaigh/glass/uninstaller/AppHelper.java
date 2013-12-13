/*
 * Copyright (C) 2007 The Android Open Source Project 
 * Copyright (C) 2013 Michael DiGiovanni Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
// Original code snipped from the Android Home SDK Sample app
package com.martynhaigh.glass.uninstaller;

import android.app.Activity;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppHelper {
    public static final String LOGTAG = "Uninstaller";

    private Activity mActivity;

    private ListView mListView;

    private Typeface mRobotoLight;
    private static ArrayList<AppInfo> mApplications;

    private final Set<String> mAddedPackages = new HashSet<String>();

    public AppHelper(Activity activity) {
        mActivity = activity;

        mRobotoLight = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Light.ttf");

        ((TextView) activity.findViewById(android.R.id.text1)).setTypeface(mRobotoLight);
    }

    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    public void bindApplications() {
        if (mListView == null) {
            mListView = (ListView) mActivity.findViewById(android.R.id.list);
        }
        mListView.setAdapter(new ApplicationsAdapter(mActivity, mApplications));
        mListView.setSelection(0);

        mListView.setOnItemClickListener(new ApplicationLauncher());
    }

    /**
     * Loads the list of installed applications in mApplications.
     */
    public void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }


        if (mApplications == null) {
            mApplications = new ArrayList<AppInfo>();
        }
        mApplications.clear();

        final PackageManager packageManager = mActivity.getPackageManager();
        List<ApplicationInfo> installedApplications =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        Collections.sort(installedApplications, new ApplicationInfo.DisplayNameComparator(packageManager));

        if (installedApplications != null) {
            final int count = installedApplications.size();

            for (int i = 0; i < count; i++) {
                AppInfo application = new AppInfo();
                ApplicationInfo info = installedApplications.get(i);

                // Let's filter out this app
                if (!mAddedPackages.contains(info.packageName) &&
                        !isSystemPackage(info)) {
                    mAddedPackages.add(info.packageName);
                    application.title = info.loadLabel(packageManager);
                    application.setActivity(info.packageName);

                    mApplications.add(application);
                }
            }
        }
    }

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    /**
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<AppInfo> {
        private static final int TYPE_SPACE = 0;
        private static final int TYPE_ITEM = 1;

        public ApplicationsAdapter(Context context, ArrayList<AppInfo> apps) {
            super(context, 0, apps);
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == TYPE_ITEM;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == TYPE_ITEM) {
                final AppInfo info = mApplications.get(position);
                if (convertView == null) {
                    final LayoutInflater inflater = mActivity.getLayoutInflater();
                    convertView = inflater.inflate(R.layout.item_app, parent, false);
                    ((TextView) convertView.findViewById(android.R.id.text1))
                            .setTypeface(mRobotoLight);
                }


                final TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
                textView.setText(info.title);
            } else {
                if (convertView == null) {
                    final LayoutInflater inflater = mActivity.getLayoutInflater();
                    convertView = inflater.inflate(R.layout.item_empty, parent, false);
                }
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getCount() - 1) {
                return TYPE_SPACE;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2; // 1 is the standard, the second is just a holder
        }
    }

    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            AppInfo app = (AppInfo) parent.getItemAtPosition(position);
            mActivity.startActivity(app.intent);
        }
    }

}
