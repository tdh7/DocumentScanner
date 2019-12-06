package nahuy.fithcmus.magiccam.data.entities;

/**
 * Created by huy on 6/20/2017.
 */

public class DA_Frame {

    private String fileName;
    private String filePath;
    private Integer width;
    private Integer height;

    public DA_Frame(String filePath, Integer width, Integer height) {
        this.filePath = filePath;
        this.width = width;
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}
