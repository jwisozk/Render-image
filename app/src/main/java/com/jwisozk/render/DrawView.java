package com.jwisozk.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.view.View;

import java.util.Map;


class DrawView extends View {

    private Map<Integer, Bitmap> bitmapMap;

    public DrawView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmapMap(canvas);
    }

    private void drawBitmapMap(Canvas canvas) {
        if (bitmapMap != null) {
            for (Map.Entry<Integer, Bitmap> entry : bitmapMap.entrySet()) {
                canvas.drawBitmap(entry.getValue(), 0, 0, null);
            }
        }
    }

    public void setBitmapMap(Map<Integer, Bitmap> bitmapMap) {
        this.bitmapMap = bitmapMap;
    }

    public Bitmap getBitmap() {
        if (bitmapMap == null || bitmapMap.size() == 0)
            return null;
        int sizeBitmap = (int) getResources().getDimension(R.dimen.frame_width);
        Bitmap bitmap = Bitmap.createBitmap(sizeBitmap, sizeBitmap, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawBitmapMap(canvas);
        return bitmap;
    }
}
