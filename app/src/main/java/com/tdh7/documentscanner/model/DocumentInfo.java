package com.tdh7.documentscanner.model;

import android.content.Context;
import android.text.format.DateUtils;

import com.tdh7.documentscanner.ui.fragments.DocumentAdapter;
import com.tdh7.documentscanner.util.Util;

public class DocumentInfo implements DocumentAdapter.DocumentObject {

    public DocumentInfo(String fileTitle, String path) {
        mFileTitle = fileTitle;
        mPath = path;
    }

    public final String mFileTitle;
    public final String mPath;
    public String mDescription = "";
    public void buildDescription(Context context) {
        mDescription = new StringBuilder(Util.humanReadableByteCount(mFileSize )).append(" • ")
                .append(DateUtils.formatDateTime(context, mLastModified, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY)).toString();
    }

    public long getLastModified() {
        return mLastModified;
    }

    public DocumentInfo setLastModified(long mCreatedTime) {
        this.mLastModified = mCreatedTime;
        return this;
    }

    public long mLastModified = 0;

    public long getFileSize() {
        return mFileSize;
    }

    public DocumentInfo setFileSize(long fileSize) {
        mFileSize = fileSize;
        return this;
    }

    public long mFileSize = 0;
}
