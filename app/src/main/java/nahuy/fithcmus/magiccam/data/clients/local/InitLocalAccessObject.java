package nahuy.fithcmus.magiccam.data.clients.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
//import android.test.InstrumentationTestCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.Constants;
import nahuy.fithcmus.magiccam.data.clients.database.filter.FilterChannelAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.filter.ShaderAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.frame.FrameAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.sticker.StickerAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.sticker_text.StickerTextAccessObject;
import nahuy.fithcmus.magiccam.data.entities.DA_FilterChannel;
import nahuy.fithcmus.magiccam.data.entities.DA_Frame;
import nahuy.fithcmus.magiccam.data.entities.DA_MyGLShader;
import nahuy.fithcmus.magiccam.data.entities.DA_Sticker;
import nahuy.fithcmus.magiccam.data.entities.DA_StickerText;

import static nahuy.fithcmus.magiccam.data.Constants.DATABASE_PATH;

/**
 * Created by huy on 6/18/2017.
 */

public class InitLocalAccessObject {

    public boolean loadingDatabaseFromAssetFolder(Context context){
        File inAppFile = new File(context.getFilesDir(), nahuy.fithcmus.magiccam.data.Constants.DATABASE_NAME);
        if(inAppFile.exists()){
            inAppFile.delete();
        }
        if (!inAppFile.exists()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = context.getAssets().open(Constants.DATABASE_NAME);
                os = new FileOutputStream(inAppFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException ioe) {
                return false;
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        DATABASE_PATH = inAppFile.getAbsolutePath();
        SharedPreferences.Editor editor = context
                .getSharedPreferences(Constants.DATABASE_TAG, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.DATABASE_TAG_PATH, DATABASE_PATH);
        return true;
    }

    public boolean loadingInitFileFromAssetFolder(Context context){
        File shaderApp = new File(context.getFilesDir(), Constants.LOCAL_FSH_NAME);
        File channelApp = new File(context.getFilesDir(), Constants.LOCAL_CHANNEL_NAME);
        File frameApp = new File(context.getFilesDir(), Constants.LOCAL_FRAME_NAME);
        File stickerApp = new File(context.getFilesDir(), Constants.LOCAL_STICKER_NAME);
        File stickerTextApp = new File(context.getFilesDir(), Constants.LOCAL_STICKER_TEXT_NAME);
        File effectImgApp = new File(context.getFilesDir(), Constants.LOCAL_EFFECT_IMG_NAME);
        deleteRecursive(shaderApp);
        deleteRecursive(channelApp);
        deleteRecursive(frameApp);
        deleteRecursive(stickerApp);
        deleteRecursive(stickerTextApp);
        deleteRecursive(effectImgApp);
        copyAssetFolder(context, shaderApp);
        copyAssetFolder(context, channelApp);
        copyAssetFolder(context, frameApp);
        copyAssetFolder(context, stickerApp);
        copyAssetFolder(context, stickerTextApp);
        copyAssetFolder(context, effectImgApp);
        return true;
    }

    public boolean checkIfDatabaseIsCreated(Context context){
        final SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constants.DATABASE_TAG,
                Context.MODE_PRIVATE);
        boolean isCreated = sharedPreferences.getBoolean(Constants.DATABASE_TAG_IS_CREATED, false);
        if(isCreated){
            Constants.DATABASE_PATH = sharedPreferences.getString(Constants.DATABASE_TAG_PATH, "");
        }
        else{

        }
        return isCreated;
    }

    public boolean initDataInDatabase(Context context){
        // Init shader
        boolean result1 = initShader(context);
        boolean result2 = initFrame(context);
        boolean result3 = initSticker(context);
        boolean result4 = initStickerText(context);

        return result1 & result2 & result3 & result4;
    }

    private boolean initShader(Context context){
        boolean result = true;
        String channelFolder = context.getFilesDir().getAbsolutePath() + "/" + Constants.LOCAL_CHANNEL_NAME + "/";
        String imgFolder = context.getFilesDir().getAbsolutePath() + "/" + Constants.LOCAL_EFFECT_IMG_NAME + "/";
        ArrayList<DA_MyGLShader> objectToAdd = new ArrayList<>();

        // Normal
        objectToAdd.add(new DA_MyGLShader("F_Normal", "normal.fsh", imgFolder + "normal.png", "M1"));

        // Black and white
        objectToAdd.add(new DA_MyGLShader("F_B&W", "b_w.fsh", imgFolder + "bw.png", "M1"));

        // Sepia
        objectToAdd.add(new DA_MyGLShader("F_Sepia", "sepia.fsh", imgFolder + "sepia.png", "M1"));

        // Vintage
        objectToAdd.add(new DA_MyGLShader("F_Vintage", "vintage.fsh", imgFolder + "vintage.png", "M1"));

        // Lut(20)
        for(int i = 0; i < 20; i++){
            String fileName = String.format("lut%d.png", i + 1);
            String shaderName = String.format("F_Lut%d", i + 1);
            String shaderImg = String.format("lut%d.png", i + 1);
            DA_FilterChannel da_filterChannel = new DA_FilterChannel(channelFolder + fileName, "", 512, 512);
            ArrayList<DA_FilterChannel> da_filterChannels = new ArrayList<>();
            da_filterChannels.add(da_filterChannel);
            objectToAdd.add(new DA_MyGLShader(shaderName, "lut.fsh", imgFolder + shaderImg, "M1,F1",
                    da_filterChannels));
        }

        // Sketch
        objectToAdd.add(new DA_MyGLShader("F_Sketch", "sketch.fsh", imgFolder + "sketch.png", "M1"));

        // Emboss
        objectToAdd.add(new DA_MyGLShader("F_Emboss", "emboss.fsh", imgFolder + "emboss.png", "M1"));

        // Crosshatch
        objectToAdd.add(new DA_MyGLShader("F_CrossHatch", "cross_hatch.fsh", imgFolder + "crosshatch.png", "M1"));

        // Toon
        // objectToAdd.add(new DA_MyGLShader("F_Toon", "toon.fsh", imgFolder + "normal.png", "M1"));

        // Glow
        // objectToAdd.add(new DA_MyGLShader("F_Glow", "edge_glow.fsh", imgFolder + "normal.png", "M1"));

        // Navigate
        objectToAdd.add(new DA_MyGLShader("F_Navigate", "navigate.fsh", imgFolder + "navigate.png", "M1"));

        for(DA_MyGLShader da_myGLShader : objectToAdd){
            result = ShaderAccessObject.getInstance().addFilter(da_myGLShader);
        }

        return result;
    }

    private boolean initFrame(Context context){
        boolean result = true;
        String imgFolder = context.getFilesDir().getAbsolutePath() + "/" + Constants.LOCAL_FRAME_NAME + "/";
        ArrayList<DA_Frame> objectToAdd = new ArrayList<>();

        objectToAdd.add(new DA_Frame(imgFolder + "frame0.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame1.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame2.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame3.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame4.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame5.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame6.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame7.png", 1,1));
        objectToAdd.add(new DA_Frame(imgFolder + "frame8.png", 1,1));

        for(DA_Frame da_frame : objectToAdd){
            result = FrameAccessObject.getInstance().addFrame(da_frame);
        }

        return result;
    }

    private boolean initSticker(Context context){
        boolean result = true;
        String imgFolder = context.getFilesDir().getAbsolutePath() + "/" + Constants.LOCAL_STICKER_NAME + "/";
        ArrayList<DA_Sticker> objectToAdd = new ArrayList<>();

        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt1.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt2.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt3.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt4.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt5.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "animalt6.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea1.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea2.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea3.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea4.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea5.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea6.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea7.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "bubblea8.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel1.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel2.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel3.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel4.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel5.png"));
        objectToAdd.add(new DA_Sticker("", imgFolder + "cutesquirrel6.png"));

        for(DA_Sticker da_sticker : objectToAdd){
            result = StickerAccessObject.getInstance().addSticker(da_sticker);
        }

        return result;
    }

    private boolean initStickerText(Context context){
        boolean result = true;
        String imgFolder = context.getFilesDir().getAbsolutePath() + "/" + Constants.LOCAL_STICKER_TEXT_NAME+ "/";
        ArrayList<DA_StickerText> objectToAdd = new ArrayList<>();

        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble_normal.png", "#000000"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_b.png", "#ffffff"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_g.png", "#000000"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_or.png", "#ffffff"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_r.png", "#ffff00"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_w.png", "#000000"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_y.png", "#00ff00"));
        objectToAdd.add(new DA_StickerText("", imgFolder + "bubble1_p.png", "#ffffff"));

        for(DA_StickerText da_sticker_text : objectToAdd){
            result = StickerTextAccessObject.getInstance().addSticker(da_sticker_text);
        }

        return result;
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private boolean copyAssetFolder(Context context, File folderName){

        try {
            boolean createdResult = folderName.mkdir();

            if(!createdResult){
                throw new Exception("Can't create folder!");
            }

            AssetManager assetFiles = context.getAssets();

            // MyHtmlFiles is the name of folder from inside our assets folder
            String[] files = assetFiles.list(folderName.getName());

            // Initialize streams
            InputStream in = null;
            OutputStream out = null;

            for (int i = 0; i < files.length; i++) {

                // @Folder name is also case sensitive
                // @MyHtmlFiles is the folder from our assets

                in = assetFiles.open(folderName.getName() + "/" + files[i]);


                // Currently we will copy the files to the root directory
                // but you should create specific directory for your app

                out = new FileOutputStream(
                        folderName.getAbsolutePath() + "/"
                                + files[i]);

                copyAssetFiles(in, out);
            }

        } catch (FileNotFoundException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void copyAssetFiles(InputStream in, OutputStream out) {
        try {
            int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
