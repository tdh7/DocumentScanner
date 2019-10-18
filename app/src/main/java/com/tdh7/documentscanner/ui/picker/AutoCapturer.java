package com.tdh7.documentscanner.ui.picker;

import java.lang.ref.WeakReference;

public class AutoCapturer {
    private final WeakReference<CameraPickerFragment> mRef;

    public AutoCapturer(CameraPickerFragment fragment) {
        mRef = new WeakReference<>(fragment);
    }

    private float mTolerance = 0.1f;
    private float getMaxTolerance() {
        return mTolerance*2.5f;
    }
    private boolean isLockCaptured() {
        CameraPickerFragment fragment = mRef.get();
        return fragment==null || fragment.mCaptureIcon.isLockingCapture();
    }

    public void setTolerance(float tole) {
        mTolerance = (tole >1) ? 1: ((tole < 0) ? 0 : tole);
    }

    public float getTolerance() {
        return mTolerance;
    }

    private void fireCapture() {
        if(isLockCaptured()) return;

        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) {
            fragment.fireCapture();
        }
    }

    public long getStableDuration() {
        return mStableDuration;
    }

    public void setStableDuration(long stableDuration) {
        mStableDuration = stableDuration;
    }

    private long mStableDuration = 1000;

    /**
     *   4 điểm không là 4 đỉnh
     *
     * @return
     */
    private boolean shouldCapture() {
        if(!isValidEdges()) return false;

        return true;
    }

    /**
     * -. Tính toán và lưu Tọa độ trung bình ${mAverageEdges} của của các edge
     * -. Mỗi lần record, tọa độ hợp lệ là :
     *       +. Record khác đỉnh hoặc record đầu tiên (0)
     *     +. Nằm không quá  ${mAverageEdges} +/- ${mTolerance}
     *  -. Record không hợp lệ khiến toàn bộ giá trị trở về mặc định (0)
     * -. Record liên tiếp cuối cùng được vượt qua deltaTime sẽ kích hoạt autoCapture
     *  -. Record vượt được 1/2 deltaTime sẽ kích hoạt message "Hold still.."
     *  -. Record vượt qua deltaTime sẽ kích hoạt message "Capturing..."
     *  -. Record dưới deltaTime sẽ kích hoạt mesage "Looking for document..."
     *  -. Capture thành công sẽ xóa bỏ hiển thị message, đồng thời khóa tạm thời tính năng record
     */

    private float[] mAverageEdges = new float[] {0,1,0,1,0,0,1,1};
    private int mTotalRecords = 0;
    private float[] mLatestEdges = new float[] {0,1,0,1,0,0,1,1};
    private final float[] mInvalidEdges = new float[]{0,1,0,1,0,0,1,1};

    private boolean isValidEdges() {
        for (int i = 0; i < 8; i++) {
            if(mInvalidEdges[i]!=mLatestEdges[i]) return false;
        }
        return true;
    }

    private float getX(float[] points, int i) {
        return points[i];
    }

    private float getY(float[] points, int i) {
        return points[i+4];
    }

    private boolean isRecordPointValid(int i, float x, float y) {

    }

    public float[] getLatestEdges() {
        return mLatestEdges;
    }

    private void onProcess(float[] points) {
        for (int i = 0; i < 8; i++) {
            mLatestEdges[0] = points[0];
        }

    }

    public void destroy() {
        mRef.clear();
    }

    public float[] getAverageEdges() {
        return mAverageEdges;
    }
}
