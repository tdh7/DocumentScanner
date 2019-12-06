package nahuy.fithcmus.magiccam.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import nahuy.fithcmus.magiccam.data.clients.database.filter.FilterChannelAccessObject;
import nahuy.fithcmus.magiccam.data.clients.database.filter.FilterKernelAccessObject;
import nahuy.fithcmus.magiccam.presentation.entities.FilterChannel;

/**
 * Created by huy on 6/18/2017.
 */

public class DA_MyGLShader {
    @SerializedName("pk")
    private Integer filterId;
    @SerializedName("filter_name")
    private String filterName;
    @SerializedName("filter_shader_name")
    private String filterShader;
    @SerializedName("filter_present_img")
    private String filterPresentImg;
    @SerializedName("filter_using_channel")
    private boolean usingEChannel;
    @SerializedName("filter_using_kernel")
    private boolean usingKernel;
    @SerializedName("filter_buffer_list")
    private String bufferList;
    @SerializedName("filter_buffer_order")
    private String orderCode;
    @SerializedName("filter_download_link")
    private String downloadLink;

    private String filterImg;
    private ArrayList<DA_FilterChannel> filterChannels;
    private ArrayList<DA_FilterKernel> filterKernels;
    private String[] bufferShader;

    public DA_MyGLShader(Integer filterId, String filterName, String filterPresentImg, String downloadLink) {
        this.filterId = filterId;
        this.filterName = filterName;
        this.filterPresentImg = filterPresentImg;
        this.downloadLink = downloadLink;
    }

    public DA_MyGLShader(Integer filterId, String filterName, String filterShader, String filterPresentImg, boolean usingEChannel, boolean usingKernel, String bufferList, String orderCode) {
        this.filterId = filterId;
        this.filterName = filterName;
        this.filterShader = filterShader;
        this.filterPresentImg = filterPresentImg;
        this.usingEChannel = usingEChannel;
        this.usingKernel = usingKernel;
        this.bufferList = bufferList;
        this.orderCode = orderCode;
    }

    public DA_MyGLShader(String filterName, String filterShader, String filterImg, String orderCode) {
        this.filterName = filterName;
        this.filterShader = filterShader;
        this.filterImg = filterImg;
        this.orderCode = orderCode;
    }

    public DA_MyGLShader(String filterName, String filterShader, String filterImg, String orderCode, ArrayList<DA_FilterChannel> filterChannels) {
        this.filterName = filterName;
        this.filterShader = filterShader;
        this.filterImg = filterImg;
        this.orderCode = orderCode;
        this.filterChannels = filterChannels;
        this.usingEChannel = true;
    }

    public DA_MyGLShader(String filterName, String filterShader, String filterImg, String orderCode, ArrayList<DA_FilterChannel> filterChannels, ArrayList<DA_FilterKernel> filterKernels) {
        this.filterName = filterName;
        this.filterShader = filterShader;
        this.filterImg = filterImg;
        this.orderCode = orderCode;
        this.filterChannels = filterChannels;
        this.filterKernels = filterKernels;
        this.usingEChannel = true;
        this.usingKernel = true;
    }

    public DA_MyGLShader(String filterName, String filterShader, String filterImg, String orderCode, String[] bufferShader) {
        this.filterName = filterName;
        this.filterShader = filterShader;
        this.filterImg = filterImg;
        this.orderCode = orderCode;
        this.bufferShader = bufferShader;
    }

    public boolean isUsingKernel() {
        return usingKernel;
    }

    public ArrayList<DA_FilterKernel> getKernel() {
        if(isUsingKernel()){
            if(filterKernels != null)
                return filterKernels;
            filterKernels = FilterKernelAccessObject.getInstance().getKernel(filterName);
            return filterKernels;
        }
        return null;
    }

    public boolean isUsingEChannel() {
        return usingEChannel;
    }

    public ArrayList<DA_FilterChannel> getFilterChannels() {
        if(isUsingEChannel()){
            if(filterChannels != null)
                return filterChannels;
            filterChannels = FilterChannelAccessObject.getInstance().getFilterChannels(filterName);
            return filterChannels;
        }
        return null;
    }

    public String getFilterName() {
        return filterName;
    }

    public String getFilterShader() {
        return filterShader;
    }

    public String getFilterImg() {
        return filterImg;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setUsingChannel(boolean usingChannel) {
        this.usingEChannel = usingChannel;
    }

    public void setUsingKernel(boolean usingKernel) {
        this.usingKernel = usingKernel;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public Integer getId() {
        return filterId;
    }
}
