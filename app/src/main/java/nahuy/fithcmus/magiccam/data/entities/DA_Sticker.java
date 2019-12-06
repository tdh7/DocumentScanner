package nahuy.fithcmus.magiccam.data.entities;

/**
 * Created by huy on 6/20/2017.
 */

public class DA_Sticker {

    private String fileName;
    private String filePath;

    public DA_Sticker(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
