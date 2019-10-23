package com.tdh7.documentscanner.controller.picker;

import android.util.Log;

import androidx.annotation.StringRes;

import com.tdh7.documentscanner.R;
import com.tdh7.documentscanner.ui.picker.CameraPickerFragment;
import com.tdh7.documentscanner.util.Util;

import java.lang.ref.WeakReference;

public class AutoCapturer {
    private static final String TAG = "AutoCapturer";
    private final WeakReference<CameraPickerFragment> mRef;

    public AutoCapturer(CameraPickerFragment fragment) {
        mRef = new WeakReference<>(fragment);
    }

    private float mTolerance = 0.15f;

    public static final int STATE_DISABLING = 0;
    public static final int STATE_LOOKING = 1;
    public static final int STATE_HOLDING_STILL = 2;
    public static final int STATE_CAPTURING = 3;

    public static String getStateName(int s) {
        switch (s) {
            case STATE_DISABLING: return "disabling";
            case STATE_LOOKING: return "looking";
            case STATE_HOLDING_STILL: return "holding still";
            case STATE_CAPTURING: return "capturing";
            default:return "invalid_state";
        }
    }

    public synchronized int getState() {
        return mState;
    }

    public void disable() {
        setState(STATE_DISABLING);
    }

    public synchronized void setState(int state) {
        if(mState!= state) {
            mState = state;
            switch (state) {
                case STATE_DISABLING:
                    hideToast();
                    break;
                case STATE_LOOKING:
                    toast(R.string.looking_for_document);
                    break;
                case STATE_HOLDING_STILL:
                    toast(R.string.hold_still);
                    break;
                case STATE_CAPTURING:
                    toast(R.string.capturing);
                    break;
            }
        }
    }

    public int mState = STATE_DISABLING;

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

    private synchronized void fireCapture() {
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) {
            Log.d(TAG, "fire: " +
                    "p[0] = ("+mAverageEdges[0]+"; " + mAverageEdges[4]+"), " +
                    "p[1] = ("+mAverageEdges[1]+"; " + mAverageEdges[5]+"), "+
                    "p[2] = ("+mAverageEdges[2]+"; " + mAverageEdges[6]+"), "+
                    "p[3] = ("+mAverageEdges[3]+"; " + mAverageEdges[7]+")");
            fragment.fireCaptureByAutoCapturer();
        }
    }

    private void toast(@StringRes int s) {
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) fragment.toast(s);
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

    private void arrayCopy(float[] from, float[] to) {
        System.arraycopy(from, 0, to, 0, 8);
    }

    private void calculateAverage() {
        if(mTotalRecords==1)
            System.arraycopy(mLatestEdges, 0, mAverageEdges, 0, 8);
        else {
            for (int i = 0; i < 4; i++) {
                setX(mAverageEdges,i,
                        (getX(mAverageEdges,i)*(mTotalRecords-1) + getX(mLatestEdges,mFoundPosInLatest[i]))/mTotalRecords);
                setY(mAverageEdges,i,
                        (getY(mAverageEdges,i)*(mTotalRecords-1) + getY(mLatestEdges,mFoundPosInLatest[i]))/mTotalRecords);
            }
        }
    }

    private void shouldCapture() {
        switch (mState) {
            case STATE_DISABLING:
                // Do nothing
                break;
            case STATE_LOOKING:
                if(!areLatestEdgesAcceptable()) {
                    // go back to start
                    backToBeginning();
                    break;
                }
                record();

                    // switch to holding
                    if (mDeltaTime >= mStableDuration/2 && mTotalRecords >= 4) {
                        mDeltaTime = mStableDuration/2;
                        mStartTime = System.currentTimeMillis() - mDeltaTime;
                        mTotalRecords = 4;
                        setState(STATE_HOLDING_STILL);
                    }
                break;
            case STATE_HOLDING_STILL:
                if(!areLatestEdgesAcceptable()) mWrongCount++; else mWrongCount = 0;
                if(mWrongCount>1) {
                    // go back to start
                    backToBeginning();
                    setState(STATE_LOOKING);
                    break;
                }

                record();

                if(mDeltaTime >= mStableDuration&& mTotalRecords >=7) {
                    mDeltaTime = mStableDuration;
                    mTotalRecords = 7;
                    mStartTime = System.currentTimeMillis() - mDeltaTime;
                    setState(STATE_CAPTURING);
                        // this will go straight to capturing case
                } else break;
            case STATE_CAPTURING:
                setState(STATE_DISABLING);
                fireCapture();
                break;
        }
    }

    private int mWrongCount = 0;
    private void record() {
        mTotalRecords++;
        mDeltaTime = System.currentTimeMillis() - mStartTime;
        calculateAverage();
        Log.d(TAG, "current average: " +
                "p[0] = ("+mAverageEdges[0]+"; " + mAverageEdges[4]+"), " +
                "p[1] = ("+mAverageEdges[1]+"; " + mAverageEdges[5]+"), "+
                "p[2] = ("+mAverageEdges[2]+"; " + mAverageEdges[6]+"), "+
                "p[3] = ("+mAverageEdges[3]+"; " + mAverageEdges[7]+")");
        Log.d(TAG, "current latest: " +
                "p[0] = ("+mLatestEdges[0]+"; " + mLatestEdges[4]+"), " +
                "p[1] = ("+mLatestEdges[1]+"; " + mLatestEdges[5]+"), "+
                "p[2] = ("+mLatestEdges[2]+"; " + mLatestEdges[6]+"), "+
                "p[3] = ("+mLatestEdges[3]+"; " + mLatestEdges[7]+")");
    }

    private void backToBeginning() {
        mStartTime = System.currentTimeMillis();
        mDeltaTime = 0;
        mWrongCount = 0;
        mTotalRecords = 0;
        Util.getDefaultValue(mAverageEdges);
        Util.getDefaultValue(mLatestEdges);
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

    private boolean areLatestEdgesNotDefaultValue() {
        return Util.isNotDefaultValue(mLatestEdges);
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

    private boolean areLatestEdgesAcceptable() {
        if(!areLatestEdgesNotDefaultValue()) return false;
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
                    Log.d(TAG, "couldn't find average "+posInAverage+" ["+getX(mAverageEdges,posInAverage)+"; "+getY(mAverageEdges,posInAverage)+"], so false at record "+mTotalRecords+", state "+getStateName(mState));
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

    public void activeAutoCapture() {
        backToBeginning();
        setState(STATE_LOOKING);
    }

    private void hideToast() {
        CameraPickerFragment fragment = mRef.get();
        if(fragment!=null) fragment.hideToast();
    }

    public synchronized void onProcess(float[] points) {
        if(isLockCaptured()) return;
        if(points.length!=8) return;
        System.arraycopy(points, 0, mLatestEdges, 0, 8);
        shouldCapture();
    }

    public void destroy() {
        mRef.clear();
    }

    public float[] getAverageEdges() {
        return mAverageEdges;
    }


}
