package nahuy.fithcmus.magiccam.data.clients.local;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryAlbumInfo;
import nahuy.fithcmus.magiccam.data.entities.gallery.DA_GalleryImageInfo;

/**
 * Created by huy on 3/27/2017.
 */

public class GalleryDAO {

    private static GalleryDAO galleryInstance = new GalleryDAO();

    private static final Uri EXTERNAL_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String ID = MediaStore.Images.ImageColumns._ID;
    private static final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
    private static final String BUCKET_ID = MediaStore.Images.Media.BUCKET_ID;
    private static final String BUCKET_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;

    private static final String[] IMAGE_PROJECTION_SIMPLE =
            new String[]{
                    ID
            }
            ;

    private static final String[] IMAGE_PROJECTION_IN_DETAL =
            new String[] {
                    ID, DATE_TAKEN, BUCKET_ID
            };

    private static final String[] IMAGE_PROJECTION_ALBUM =
            new String[]{
                    ID, DATE_TAKEN, BUCKET_NAME, BUCKET_ID
            };

    // Cursor for query images.
    private Cursor imgCursor = null;
    private static final long DEFAULT_RECENT_ALBUM_ID = -1;
    private String SORT_ORDER = " DESC";

    public List<DA_GalleryImageInfo> getImages(final Context mainContext){
        // Return list.
        List<DA_GalleryImageInfo> gridImg = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;

        if(imgCursor == null){
            try{
                imgCursor = mainContext.getContentResolver().query(EXTERNAL_URI,
                        IMAGE_PROJECTION_ALBUM, selection, selectionArgs, DATE_TAKEN + SORT_ORDER);

                final int idIndex = imgCursor.getColumnIndex(ID);
                final int dateIndex = imgCursor.getColumnIndex(DATE_TAKEN);
                final int albumNameIndex = imgCursor.getColumnIndex(BUCKET_NAME);
                final int albumIdIndex = imgCursor.getColumnIndex(BUCKET_ID);

                while(imgCursor.moveToNext()){
                    final long id = imgCursor.getLong(idIndex);
                    final long dateTaken = imgCursor.getLong(dateIndex);
                    final String albumName = imgCursor.getString(albumNameIndex);
                    final long albumId = imgCursor.getLong(albumIdIndex);

                    gridImg.add(new DA_GalleryImageInfo(Uri.withAppendedPath(EXTERNAL_URI, Long.toString(id))
                            , albumName, albumId, dateTaken));
                }
            }
            catch (Exception e){
                // Log.e(LOG_TAG, e.getMessage());
            }
            finally {
                imgCursor.close();
            }
        }

        imgCursor = null;

        return gridImg;
    }

    public List<DA_GalleryImageInfo> getImagesInAlbum(final Context mainContext, long currentAlbumId) {
        // Return list.
        List<DA_GalleryImageInfo> gridImg = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;

        if(currentAlbumId != DEFAULT_RECENT_ALBUM_ID) {
            selection = String.format("%s = ?", BUCKET_ID);
            selectionArgs = new String[]{
                    Long.toString(currentAlbumId)
            };
        }

        if(imgCursor == null){
            try{
                imgCursor = mainContext.getContentResolver().query(EXTERNAL_URI,
                        IMAGE_PROJECTION_ALBUM, selection, selectionArgs, DATE_TAKEN + SORT_ORDER);

                final int idIndex = imgCursor.getColumnIndex(ID);
                final int dateIndex = imgCursor.getColumnIndex(DATE_TAKEN);
                final int albumNameIndex = imgCursor.getColumnIndex(BUCKET_NAME);
                final int albumIdIndex = imgCursor.getColumnIndex(BUCKET_ID);

                while(imgCursor.moveToNext()){
                    final long id = imgCursor.getLong(idIndex);
                    final long dateTaken = imgCursor.getLong(dateIndex);
                    final String albumName = imgCursor.getString(albumNameIndex);
                    final long albumId = imgCursor.getLong(albumIdIndex);

                    gridImg.add(new DA_GalleryImageInfo(Uri.withAppendedPath(EXTERNAL_URI, Long.toString(id))
                            , albumName, albumId, dateTaken));
                }
            }
            catch (Exception e){
                // Log.e(LOG_TAG, e.getMessage());
            }
            finally {
                imgCursor.close();
            }
        }

        imgCursor = null;

        return gridImg;
    }

    public List<DA_GalleryAlbumInfo> getAlbumList(final Context mainContext){
        // Return list.
        ArrayList<DA_GalleryAlbumInfo> lstOfAlbums = new ArrayList<>();

        if(imgCursor == null){
            try{

                imgCursor = mainContext.getContentResolver().query(EXTERNAL_URI,
                        IMAGE_PROJECTION_ALBUM, null, null, DATE_TAKEN + SORT_ORDER);

                final int idIndex = imgCursor.getColumnIndex(ID);
                final int dateIndex = imgCursor.getColumnIndex(DATE_TAKEN);
                final int albumNameIndex = imgCursor.getColumnIndex(BUCKET_NAME);
                final int albumIdIndex = imgCursor.getColumnIndex(BUCKET_ID);
                boolean firstTimeTraverse = true;

                while(imgCursor.moveToNext()){
                    boolean albumExists = false;
                    final long id = imgCursor.getLong(idIndex);
                    final long dateTaken = imgCursor.getLong(dateIndex);
                    final String albumName = imgCursor.getString(albumNameIndex);
                    final long albumId = imgCursor.getLong(albumIdIndex);

                    if(firstTimeTraverse){
                        lstOfAlbums.add(new DA_GalleryAlbumInfo(-1,
                                "Recent",
                                Uri.withAppendedPath(EXTERNAL_URI, Long.toString(id))));
                        firstTimeTraverse = false;
                    }

                    for(int i = 0; i < lstOfAlbums.size(); i++) {
                        // Recent photos
                        if (i == 0) {
                            lstOfAlbums.get(i).increaseGalleryImage();
                        }
                        if (lstOfAlbums.get(i).isThisAlbum(albumName)) {
                            // Add gallery image or increase number of gallery image here.
                            lstOfAlbums.get(i).increaseGalleryImage();
                            albumExists = true;
                        }
                    }

                    // If album is not exist, we create a new one and add it to result list.
                    if(!albumExists){
                        lstOfAlbums.add(new DA_GalleryAlbumInfo(albumId,
                                albumName,
                                Uri.withAppendedPath(EXTERNAL_URI, Long.toString(id))));
                    }

                }

            }
            catch (Exception e){
                // Log.e(LOG_TAG, e.getMessage());
            }
            finally {
                imgCursor.close();
            }
        }

        imgCursor = null;

        return lstOfAlbums;
    }

    public static GalleryDAO getInstance() {
        return galleryInstance;
    }

    private void GalleryDAO() {}

}
