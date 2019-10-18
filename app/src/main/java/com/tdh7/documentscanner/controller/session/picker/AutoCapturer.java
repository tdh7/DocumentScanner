package com.tdh7.documentscanner.controller.session.picker;

import android.util.Log;

import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;

import java.lang.ref.WeakReference;

public class AutoCapturer {
    private static final String TAG = "AutoCapturer";
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
        return fragment==null || fragment.isLockingCapture();
    }

    public void setTolerance(float tole) {
        mTolerance = (tole >1) ? 1: ((tole < 0) ? 0 : tole);
    }

    public float getTolerance() {
        return mTolerance;
    }

    private void fireCapture() {
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) {
            fragment.fireCapture();
            deactiveAutoCapture();
        }
    }

    private void toast(String s) {
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) fragment.toast(s);
    }

    public long getStableDuration() {
        return mStableDuration;
    }

    public void setStableDuration(long stableDuration) {
        mStableDuration = stableDuration;
    }

    private long mStableDuration = 3000;
    private long mStartTime = 0;
    private long mDeltaTime = 0;

    private void resetTime() {
        mStartTime = System.currentTimeMillis();
        mDeltaTime = 0;
        mTotalRecords = 0;
        toast("Looking for document…");
    }

    private void arrayCopy(float[] from, float[] to) {
        System.arraycopy(from, 0, to, 0, 8);
    }

    private void calculateAverage() {
        if(mTotalRecords==1) arrayCopy(mLatestEdges,mAverageEdges);
        else {
            for (int i = 0; i < 4; i++) {
                setX(mAverageEdges,i,
                        (getX(mAverageEdges,i)*(mTotalRecords-1) + getX(mLatestEdges,mFoundPosInLatest[i]))/mTotalRecords);
                setY(mAverageEdges,i,
                        (getY(mAverageEdges,i)*(mTotalRecords-1) + getY(mLatestEdges,mFoundPosInLatest[i]))/mTotalRecords);
            }
        }
    }

    /**
     *   4 điểm không là 4 đỉnh
     *
     * @return
     */
    private boolean shouldCapture() {
        if(!isLatestEdgesValid()) {
            if(mTotalRecords!=0) resetTime();
            return false;
        }

        // ở đây nghĩa là các điểm đã hợp lệ
        mTotalRecords++;
        mDeltaTime = System.currentTimeMillis() - mStartTime;

        if(mStartTime>=mStableDuration&&mTotalRecords>=10) {
            // Capturing...
            toast("Capturing…, total record "+ mTotalRecords);
            fireCapture();

        } else if(mStartTime>=mStableDuration/2&&mTotalRecords>=5) {
            // hold still...
            toast("Hold still…");
        } else {
            // do nothing
        }

        calculateAverage();

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

    /// foundPos lưu trữ vị trí của điểm trong bản ghi mới nhất so với vị trí bản ghi average
    /// foundPos[a] = b,
    /// điểm thứ a trong average chính là điểm thứ b trong bản ghi latest
    private final int[] mFoundPosInLatest = new int[]{0,1,2,3};

    private boolean isDetectedEdges() {
        for (int i = 0; i < 8; i++) {
            if(mInvalidEdges[i]!=mLatestEdges[i]) return true;
        }
        return false;
    }

    private float getX(final float[] points,final int i) {
        return points[i];
    }

    private float getY(final float[] points,final int i) {
        return points[i+4];
    }

    private void setX(final float[] points,final int i, float x) {
        points[i] = x;
    }

    private void setY(final float[] points,final int i, float y) {
        points[i+4] = y;
    }

    private boolean isLatestEdgesValid() {
        if(!isDetectedEdges()) return false;
        if(mTotalRecords==0) {
            Log.d(TAG, "points detected with the first record");
            return true;
        }

        boolean shouldContinue = false;
        for(int posInAverage = 0;posInAverage < 4; posInAverage++) {

            // so sánh điểm piA và piL
            for(int posInLatest = 0; posInLatest < 4;posInLatest++) {

                // nghiễm nhiên đi tới điểm j thì các điểm foundPos < j chứa các point đã xác định, ta loại trừ chúng
                shouldContinue = false;
                for(int posFound = 0;posFound < posInAverage;posFound++) {
                    if (mFoundPosInLatest[posFound] == posInLatest) {
                        shouldContinue = true;
                        break;
                    }
                }


                if(shouldContinue) continue;

                //Tới đây nghĩa là chúng không nằm trong những điểm đã xác định
                // so sánh pia và pil, nếu trong khoảng chấp nhận thì: gán posFound[pia] = pil và break
                // nếu không phải thì thôi, nhưng nếu là giá trị cuối cùng (3)
                // thì nghĩa là không tìm thấy điểm tương ứng => Không hợp lệ, trả về false
                if(isInTolerance(posInAverage, posInLatest)) {
                    mFoundPosInLatest[posInAverage] = posInLatest;
                   // Log.d(TAG, "found average "+posInAverage+" in item "+posInLatest+" of latest");
                    break;
                } else if(posInLatest==3) {
                    Log.d(TAG, "couldn't find average "+posInAverage+" ["+getX(mAverageEdges,posInAverage)+"; "+getY(mAverageEdges,posInAverage)+"], so false at record "+mTotalRecords);
                    return false;
                }
            }
        }

        // Tới đây nghĩa là mọi thứ ổn
        Log.d(TAG, "points detected with records "+mTotalRecords);
        return true;

    }

    private boolean isInTolerance(int posInAverage, int posInLatest) {
        return Math.pow(getX(mAverageEdges,posInAverage) - getX(mLatestEdges,posInLatest),2)
                + Math.pow(getY(mAverageEdges,posInAverage) - getY(mLatestEdges,posInLatest),2)
                <= mTolerance*mTolerance;
    }

    public float[] getLatestEdges() {
        return mLatestEdges;
    }

    private boolean mActiveAutoCapture = false;
    public void activeAutoCapture() {
        mActiveAutoCapture = true;
        resetTime();
    }

    public void deactiveAutoCapture() {
        mActiveAutoCapture = false;
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) fragment.hideToast();
    }

    public synchronized void onProcess(float[] points) {
        if(!mActiveAutoCapture) return;
        if(isLockCaptured()) return;
        if(points.length!=8) return;
        for (int i = 0; i < 8; i++) {
            mLatestEdges[0] = points[0];
        }
        shouldCapture();
    }

    public void destroy() {
        mRef.clear();
    }

    public float[] getAverageEdges() {
        return mAverageEdges;
    }
}
