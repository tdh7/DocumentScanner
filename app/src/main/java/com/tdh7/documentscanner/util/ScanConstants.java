package com.tdh7.documentscanner.util;

import android.os.Environment;

/**
 * Created by jhansi on 15/03/15.
 */
public class ScanConstants {

    public final static int PICKFILE_REQUEST_CODE = 1;
    public final static int START_CAMERA_REQUEST_CODE = 2;

    public final static String SCANNED_RESULT = "scannedResult";
    public final static String PACKAGE_NAME = "com.tdh7.documentscanner";
    public final static String DOT = ".";

    public final static String PDF_PATH = Environment.getExternalStorageDirectory().getPath() + "/DocumentScanner";
    public final static String IMAGE_PATH = PDF_PATH + "/tmp";

    public final static String SELECTED_BITMAP = "selectedBitmap";
    public static final String APP_PROVIDER = PACKAGE_NAME + DOT + "provider";
}
