package com.tdh7.documentscanner.controller.session;

import android.app.Activity;
import android.graphics.Bitmap;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.tdh7.documentscanner.model.BitmapDocument;
import com.tdh7.documentscanner.util.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentSession {
    public static DocumentSession sDocumentSession;
    public static DocumentSession getInstance() {
        return sDocumentSession;
    }

    private ArrayList<BitmapDocument> mDocuments = new ArrayList<>();
    public ArrayList<BitmapDocument> getDocuments() {
        return mDocuments;
    }

    public void add(BitmapDocument document) {
        if(document!=null) mDocuments.add(document);
    }

    public void addAll(List<BitmapDocument> documents) {
        if(documents!=null) mDocuments.addAll(documents);
    }

    private DocumentSession() {

    }

    public static void init() {
        if(sDocumentSession==null)
        sDocumentSession = new DocumentSession();
    }

    public static void destroy() {
        sDocumentSession = null;
    }

    public void clear() {
        mDocuments.clear();
    }

    private Bitmap[] convertToBitmaps() {
        if(mDocuments.isEmpty()) return null;

        Bitmap[] bitmaps = new Bitmap[mDocuments.size()];

        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = mDocuments.get(i).buildResultBitmap();
        }
        return bitmaps;
    }

    public String saveToFile(String filePath, String fileName) {
        boolean buildFail = false;
        String buildMessage = "";
        Bitmap[] resultBitmap = null;
        try {
            resultBitmap = convertToBitmaps();
        } catch (Exception e) {
            buildFail = true;
            buildMessage = "Couldn't get the images";
        }


        Document resultDocument = null;
        File fo = new File(filePath, fileName);
        if (!fo.getParentFile().exists()) {
            fo.getParentFile().mkdirs();
        }

        if(!buildFail)
        if(resultBitmap==null||resultBitmap.length==0) {
            buildFail = true;
            buildMessage = "Failure to create bitmap";
        }


        if(!buildFail) {
            Image image;
            for (int i = 0; i < resultBitmap.length; i++) {

                try {
                    Bitmap bitmap = resultBitmap[i];
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    image = Image.getInstance(bytes.toByteArray());
                } catch (Exception e) {
                    image = null;
                    buildFail = true;
                    buildMessage = "Something wrong at page " + (i + 1);
                }

                if (buildFail) break;
                // init document with the size of new page
                if (resultDocument == null) {
                    resultDocument = new Document(image);
                    try {
                        PdfWriter.getInstance(resultDocument, new FileOutputStream(fo));
                        resultDocument.open();
                    } catch (Exception e) {
                        buildFail = true;
                        buildMessage = "Couldn't create new file";
                    }
                }

                if(!buildFail)
                    try {
                        resultDocument.setPageSize(image);
                        resultDocument.newPage();
                        image.setAbsolutePosition(0,0);
                        resultDocument.add(image);
                    } catch (Exception e) {
                        buildFail = true;
                        buildMessage = "Couldn't create page " + (i + 1);
                    }

                if(buildFail) break;
            }
        }

        //Document document = new Document(new Rectangle(bitmap.getWidth(), bitmap.getHeight()), 0, 0, 0, 0);
        if(resultDocument!=null)
        resultDocument.close();

        return buildMessage;
    }

    public BitmapDocument get(int current) {
        return mDocuments.get(current);
    }
}
