package nahuy.fithcmus.magiccam.data.entities;

/**
 * Created by huy on 6/20/2017.
 */

public class DA_StickerText extends DA_Sticker{

    private String color;

    public DA_StickerText(String fileName, String filePath, String color) {
        super(fileName, filePath);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
