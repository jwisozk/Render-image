package com.jwisozk.render;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class BitmapOperations {
    private final Resources resources;

    public BitmapOperations(Resources resources) {
        this.resources = resources;
    }

    public Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = resources.getAssets();
        try {
            int sizeBitmap = (int) resources.getDimension(R.dimen.frame_width);
            InputStream inputStream = assetManager.open(strName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return Bitmap.createScaledBitmap(bitmap, sizeBitmap, sizeBitmap, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Bitmap> createBitmapList(List<String> imgNameList) {
        if (imgNameList != null) {
            List<Bitmap> bitmapList = new ArrayList<>();
            for (int i = 0; i < imgNameList.size(); i++) {
                Bitmap bm = getBitmapFromAsset(imgNameList.get(i));
                if (bm != null) {
                    bitmapList.add(bm);
                }
            }
            return bitmapList;
        }
        return null;
    }

    public List<Bitmap> getTrimmedBitmapList(List<Bitmap> bitmapList) {
        List<Bitmap> trimmedBitmapList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(bitmapList.size());
        List<Worker> tasks = new ArrayList<>(bitmapList.size());
        for (int i = 0; i < bitmapList.size(); i++)
            tasks.add(new Worker(bitmapList.get(i)));
        List<Future<Bitmap>> results;
        try {
            results = executor.invokeAll(tasks);
            for (Future<Bitmap> result : results)
                trimmedBitmapList.add(result.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return trimmedBitmapList;
    }

    public List<Bitmap> changeBitmapScale(List<Bitmap> bitmapList, float scale) {
        for (int i = 0; i < bitmapList.size(); i++) {
            Bitmap bitmap = bitmapList.get(i);
            int width = (int) (bitmap.getWidth() * scale);
            int height = (int) (bitmap.getHeight() * scale);
            bitmapList.set(i, Bitmap.createScaledBitmap(bitmap, width, height, false));
        }
        return bitmapList;
    }
}
