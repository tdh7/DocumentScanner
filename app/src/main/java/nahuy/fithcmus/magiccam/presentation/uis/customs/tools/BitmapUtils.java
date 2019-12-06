package nahuy.fithcmus.magiccam.presentation.uis.customs.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nahuy.fithcmus.magiccam.presentation.Constants;

/**
 * Created by huy on 5/24/2017.
 */

public class BitmapUtils {

    private static final String LOG_TAG = BitmapUtils.class.getName();

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromUri(Context context, Uri imageUri, int reqWidth, int reqHeight) throws FileNotFoundException {

        AssetFileDescriptor fileDescriptor =null;
        fileDescriptor =
                context.getContentResolver().openAssetFileDescriptor( imageUri, "r");

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

    }

    private static Bitmap decodeSampledBitmapFromPath(Context context, String imgPath, int reqWidth, int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);

    }

    public static String getRealPathFromURI(final Context context, final Uri contentURI) {
        Cursor cursor = context.getContentResolver()
                .query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static Bitmap getBitmapFromUriLink(final Context context, final Uri bitmapFile) {

        Bitmap bmToEdit = null;
        Bitmap originBitmap = null;
        try {
            bmToEdit = decodeSampledBitmapFromUri(context, bitmapFile, 800, 400);
            // MediaStore.Images.Media.getBitmap(context.getContentResolver(), bitmapFile);
            String path = getRealPathFromURI(context, bitmapFile);
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            originBitmap = Bitmap.createBitmap(bmToEdit, 0, 0, bmToEdit.getWidth(), bmToEdit.getHeight(), matrix, true);
        } catch (IOException ioe) {
            Log.e(Constants.BITMAP_LOAD_BITMAP, ioe.getMessage());
            if (bmToEdit != null) {
                bmToEdit.recycle();
            }
            if (originBitmap != null) {
                originBitmap.recycle();
                originBitmap = null;
            }
        }

        return originBitmap;

    }

    public static Bitmap getBitmapFromTmpPath(final Context context, final String path){
        Bitmap bmToEdit = null;
        Bitmap originBitmap = null;
        try {
            bmToEdit = decodeSampledBitmapFromPath(context, path, 800, 400);
            // MediaStore.Images.Media.getBitmap(context.getContentResolver(), bitmapFile);
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            originBitmap = Bitmap.createBitmap(bmToEdit, 0, 0, bmToEdit.getWidth(), bmToEdit.getHeight(), matrix, true);
        } catch (IOException ioe) {
            Log.e(Constants.BITMAP_LOAD_BITMAP, ioe.getMessage());
            if (bmToEdit != null) {
                bmToEdit.recycle();
            }
            if (originBitmap != null) {
                originBitmap.recycle();
                originBitmap = null;
            }
        }

        return originBitmap;
    }

    public static Bitmap decodeRawBitmap(String path) {
        File bmFile = new File(path);
        if (bmFile.exists()) {
            return BitmapFactory.decodeFile(path);
        }
        else{
            return null;
        }
    }

    public static Uri saveTempBitmap(final Bitmap bm){
        long currentTime = System.currentTimeMillis();
        String folderName = "" + currentTime;
        String fileName = currentTime + ".jpg";
        File tmpSaveFolder = new File(Constants.FILE_DIRS, folderName);
        if(tmpSaveFolder.exists() == false){
            tmpSaveFolder.mkdir();
        }
        File tmpSaveFile = new File(tmpSaveFolder, fileName);
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(tmpSaveFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, f);
        }
        catch (IOException ioe){
            Log.e(LOG_TAG, ioe.getMessage());
            return null;
        }
        finally {
            try{
                f.close();
            }
            catch (IOException ioe){

            }
        }
        return Uri.parse(tmpSaveFile.toString());
    }

    public static String savePicture(Context context, Bitmap bm) {
        long time = System.currentTimeMillis();
        FileOutputStream fos = null;
        File des = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_FILE_HEAD + time + ".jpg");
        try {
            // Save bitmap to file.
            fos = new FileOutputStream(des);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Update gallery.
            MediaScannerConnection.scanFile(context, new String[]{des.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
        } catch (Exception e) {
            Log.e(Constants.LOG_E, e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (Exception e) {

            }
        }
        return des.getAbsolutePath();
    }

    public static Bitmap createScaledBitmap(Bitmap bm, int desiredWidth, int desiredHeight){
        return bm.createScaledBitmap(bm, desiredWidth, desiredHeight, false);
    }
}
