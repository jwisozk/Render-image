package com.jwisozk.render;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DrawThread extends Thread{
    Canvas canvas;
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
//    private Bitmap picture;
//    private Matrix matrix;
//    private long prevTime;
    int colorBackground;
    private Map<Integer, Bitmap> bitmapMap;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources){
        this.surfaceHolder = surfaceHolder;

        // загружаем картинку, которую будем отрисовывать
//        picture = BitmapFactory.decodeResource(resources, R.drawable.one);
//        picture = getBitmapFromAsset(resources, "2.png");
        colorBackground = resources.getColor(R.color.colorMainBackground);
//        // формируем матрицу преобразований для картинки
//        matrix = new Matrix();
//        matrix.postScale(3.0f, 3.0f);
//        matrix.postTranslate(100.0f, 100.0f);
//
//        // сохраняем текущее время
//        prevTime = System.currentTimeMillis();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    public void setBitmapMap(Map<Integer, Bitmap> bitmapMap) {
        this.bitmapMap = bitmapMap;
    }

    @Override
    public void run() {
        while (runFlag) {
            // получаем текущее время и вычисляем разницу с предыдущим
            // сохраненным моментом времени
//            long now = System.currentTimeMillis();
//            long elapsedTime = now - prevTime;
//            if (elapsedTime > 30){
//                // если прошло больше 30 миллисекунд - сохраним текущее время
//                // и повернем картинку на 2 градуса.
//                // точка вращения - центр картинки
//                prevTime = now;
//                matrix.preRotate(2.0f, picture.getWidth() / 2, picture.getHeight() / 2);
//            }
            canvas = null;
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvas.drawColor(colorBackground);
                    if (bitmapMap != null) {
                        for (Map.Entry<Integer, Bitmap> entry : bitmapMap.entrySet()) {
                            canvas.drawBitmap(entry.getValue(), 0, 0, null);
                        }
                    }
                }

            }
            finally {
                if (canvas != null) {
                    // отрисовка выполнена. выводим результат на экран
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

//    public Bitmap getBitmapFromAsset(Resources resources, String strName) {
//        AssetManager assetManager = resources.getAssets();
//        try {
//            InputStream inputStream = assetManager.open(strName);
//            return BitmapFactory.decodeStream(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
