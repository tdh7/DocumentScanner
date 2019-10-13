package com.tdh7.documentscanner.model;

import android.graphics.PointF;

import java.util.ArrayList;

public class Edge {
    public Edge() {
        mPoints = new PointF[4];

    }

    private final PointF[] mPoints;
    public PointF get(int position) {
        return mPoints.get(position);
    }

    public boolean isValid() {
        return mPoints.size()==4;
    }

    public Edge set(int i, float pointX, float pointY) {
        mPoints.get(
    }
}
