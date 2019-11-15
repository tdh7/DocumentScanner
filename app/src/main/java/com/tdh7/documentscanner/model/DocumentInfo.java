package com.tdh7.documentscanner.model;

public class DocumentInfo {
    public DocumentInfo(String fileTitle, String path, String description) {
        mFileTitle = fileTitle;
        mPath = path;
        mDescription = description;
    }

    public final String mFileTitle;
    public final String mPath;
    public final String mDescription;
}
