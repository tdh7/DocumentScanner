package nahuy.fithcmus.magiccam.presentation.entities;

/**
 * Created by huy on 5/23/2017.
 */

public class FilterKernel {

    private int kernelId;
    private float[] kernel;

    public FilterKernel(int kernelId, String kernel) {
        this.kernelId = kernelId;
        makeKernelFromStr(kernel);
    }

    public FilterKernel(int kernelId, float[] kernel) {
        this.kernelId = kernelId;
        this.kernel = kernel;
    }

    public int getKernelId() {
        return kernelId;
    }

    public float[] getKernel() {
        return kernel;
    }

    private void makeKernelFromStr(String kernelStr){
        String[] kernelArr = kernelStr.split(",");
        kernel = new float[kernelArr.length];
        for(int i = 0; i < kernelArr.length;  i++){
            kernel[i] = Float.parseFloat(kernelArr[i]);
        }
    }
}
