package com.jwisozk.render;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.concurrent.Callable;

class Worker implements Callable<Bitmap> {
    private final Bitmap bmp;

    Worker(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Bitmap call() {
        return createTrimmedBitmap(bmp);
    }

    public Bitmap createTrimmedBitmap(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth = bmp.getWidth();
        int smallX = 0, smallY = 0;
        int left = imgWidth, right = imgWidth, top = imgHeight, bottom = imgHeight;
        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {
                if (bmp.getPixel(i, j) != Color.TRANSPARENT) {
                    if ((i - smallX) < left) {
                        left = (i - smallX);
                    }
                    if ((imgWidth - i) < right) {
                        right = (imgWidth - i);
                    }
                    if ((j - smallY) < top) {
                        top = (j - smallY);
                    }
                    if ((imgHeight - j) < bottom) {
                        bottom = (imgHeight - j);
                    }
                }
            }
        }
        bmp = Bitmap.createBitmap(bmp, left, top,
                imgWidth - left - right, imgHeight - top - bottom);
        return bmp;
    }
}
