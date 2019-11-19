package com.tdh7.documentscanner.model;

import com.tdh7.documentscanner.ui.fragments.DocumentAdapter;

public class DocumentInfo implements DocumentAdapter.DocumentObject {
    public DocumentInfo(String fileTitle, String path, String description) {
        mFileTitle = fileTitle;
        mPath = path;
        mDescription = description;
    }

    public final String mFileTitle;
    public final String mPath;
    public final String mDescription;
}
