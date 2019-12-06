package nahuy.fithcmus.magiccam.data.entities;

/**
 * Created by huy on 6/18/2017.
 */

public class DA_FilterChannel {
    private String path;
    private String filterName;
    private int w;
    private int h;

    public DA_FilterChannel(String path, String filterName, int w, int h) {
        this.path = path;
        this.filterName = filterName;
        this.w = w;
        this.h = h;
    }

    public String getPath() {
        return path;
    }

    public String getFilterName() {
        return filterName;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
