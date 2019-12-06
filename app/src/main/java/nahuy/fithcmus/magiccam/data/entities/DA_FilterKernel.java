package nahuy.fithcmus.magiccam.data.entities;

/**
 * Created by huy on 6/18/2017.
 */

public class DA_FilterKernel {
    private int kernelId;
    private float[] kernel;

    public DA_FilterKernel(int kernelId, String kernel) {
        this.kernelId = kernelId;
        makeKernelFromStr(kernel);
    }

    public DA_FilterKernel(int kernelId, float[] kernel) {
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
