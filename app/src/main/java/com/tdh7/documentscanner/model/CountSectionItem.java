package com.tdh7.documentscanner.model;

import com.tdh7.documentscanner.ui.fragments.DocumentAdapter;

public class CountSectionItem implements DocumentAdapter.DocumentObject {
        private final String mTitle;
        private final String mDescription;

    public CountSectionItem setCount(int count) {
        mCount = count;
        return this;
    }

    private int mCount = 0;

        public CountSectionItem(String title, int count) {
            mTitle = title;
            mCount = count;
            mDescription ="";
        }

        public CountSectionItem(String title, String description, int count) {
            mTitle = title;
            mDescription = description;
            mCount = count;
        }

        public CountSectionItem(String title) {
            mTitle = title;
            mDescription = "";
            mCount = 0;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }

        public int getCount() {
            return mCount;
        }

    public CountSectionItem downCount() {
        if(mCount!=0) mCount--;
        return this;
    }
}