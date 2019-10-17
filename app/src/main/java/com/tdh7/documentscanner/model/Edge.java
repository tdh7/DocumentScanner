package com.tdh7.documentscanner.model;

import android.graphics.PointF;

public class Edge {
    public Edge() {
        mPoints = new PointF[4];
        for (int i = 0; i < 4; i++) {
            mPoints[i] = new PointF(0,0);
        }
    }

    private final PointF[] mPoints;
    public PointF get(int position) {
        return mPoints[position];
    }

    public boolean isValid() {
        return mPoints.length==4;
    }

    public Edge set(int i, float pointX, float pointY) {
        mPoints[i].x = pointX;
        mPoints[i].y = pointY;
        return this;
    }

}
