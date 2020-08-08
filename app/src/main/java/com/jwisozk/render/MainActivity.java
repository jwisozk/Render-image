package com.jwisozk.render;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private SurfaceViewCustom surfaceViewCustom;
    private final static String CHECKBOX = "checkbox_";
    private final static String IMAGE_VIEW = "imageView_";
    private final static String IMG_FORMAT = ".png";
    private final static String ID = "id";
    private final static String TITLE = "Totoro";
    private final Map<Integer, Bitmap> bitmapMap = new ConcurrentHashMap<>();
    List<Bitmap> bitmapList;
    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);
        ViewGroup frame = findViewById(R.id.result_frame);
        drawView = new DrawView(this);
        frame.addView(drawView);
//        surfaceViewCustom = new SurfaceViewCustom(this);
//        frame.addView(surfaceViewCustom);




        AssetManager assetManager = getResources().getAssets();
        String[] imgNameArray = getAllImagesFromAssets(assetManager);
        if (imgNameArray != null) {
            final List<String> imgNameList = new ArrayList<>(Arrays.asList(imgNameArray));
            bitmapList = createBitmapList(imgNameList);
            setImages(bitmapList);
        }
        setImgNameCheckedList();
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


    public void setImages(List<Bitmap> bitmapList) {
        if (bitmapList != null) {
            for (int i = 0; i < bitmapList.size(); i++) {
                Bitmap bm = bitmapList.get(i);
                if (bm != null) {
                    int resID = getResources().getIdentifier(IMAGE_VIEW + (i + 1), ID, getPackageName());
                    ImageView img = findViewById(resID);
                    img.setImageBitmap(createTrimmedBitmap(bm));
                }
            }
        }
    }

    private String[] getAllImagesFromAssets(AssetManager assetManager) {
        try {
            return assetManager.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setImgNameCheckedList() {
        ViewGroup wrapper = findViewById(R.id.wrapper);
        for (int i = 1; i <= wrapper.getChildCount(); i++) {
            int resID = getResources().getIdentifier(CHECKBOX + i, ID, getPackageName());
            CheckBox checkBox = findViewById(resID);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String checkboxId = buttonView.getResources().getResourceEntryName(buttonView.getId());
                    String id = checkboxId.substring(checkboxId.length() - 1);
                    String imgName = id + IMG_FORMAT;
                    if (isChecked) {
                        bitmapMap.put(Integer.parseInt(id), getBitmapFromAsset(imgName));
                    } else {
                        bitmapMap.remove(Integer.parseInt(id));
                    }
                    drawView.setBitmapMap(bitmapMap);
                    drawView.invalidate();
                }
            });
        }
    }

    public Bitmap getBitmapOverlay() {
        if (bitmapMap != null) {
            int sizeBitmap = (int) getResources().getDimension(R.dimen.frame_width);
            Bitmap bmOverlay = Bitmap.createBitmap(sizeBitmap, sizeBitmap, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmOverlay);
            for (Map.Entry<Integer, Bitmap> entry : bitmapMap.entrySet()) {
                canvas.drawBitmap(entry.getValue(), 0, 0, null);
            }
            return bmOverlay;
        }
        return null;
    }

//    private void setSurface() {
//        SurfaceView surface = findViewById(R.id.surface);
//        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                // Do some drawing when surface is ready
//                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//                Bitmap bm1 = getBitmapFromAsset("1.png");
//                Bitmap bm2 = getBitmapFromAsset("2.png");
//                Bitmap bm3 = getBitmapFromAsset("3.png");
//                Bitmap bm4 = getBitmapFromAsset("4.png");
//                Bitmap bm5 = getBitmapFromAsset("5.png");
//                Bitmap bm6 = getBitmapFromAsset("6.png");
//                Bitmap bm7 = getBitmapFromAsset("7.png");
//                Canvas canvas = holder.lockCanvas();
//                canvas.drawBitmap(bm1, 0, 0, paint);
//                canvas.drawBitmap(bm2, 0, 0, paint);
//                canvas.drawBitmap(bm3, 0, 0, paint);
//                canvas.drawBitmap(bm4, 0, 0, paint);
//                canvas.drawBitmap(bm5, 0, 0, paint);
//                canvas.drawBitmap(bm6, 0, 0, paint);
//                canvas.drawBitmap(bm7, 0, 0, paint);
////                canvas.drawColor(Color.RED);
//                holder.unlockCanvasAndPost(canvas);
//
//
////                if (bm != null) {
////                    Log.d("Main", bm.getWidth() + "");
////                }
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            }
//        });
//    }


    public Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        try {
            int sizeBitmap = (int) getResources().getDimension(R.dimen.frame_width);
            InputStream inputStream = assetManager.open(strName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return Bitmap.createScaledBitmap(bitmap, sizeBitmap, sizeBitmap, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static Bitmap createTrimmedBitmap(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth = bmp.getWidth();
        int smallX = 0, largeX = imgWidth, smallY = 0, largeY = imgHeight;
        int left = imgWidth, right = imgWidth, top = imgHeight, bottom = imgHeight;
        for (int i = 0; i < imgWidth; i++) {
            for (int j = 0; j < imgHeight; j++) {
                if (bmp.getPixel(i, j) != Color.TRANSPARENT) {
                    if ((i - smallX) < left) {
                        left = (i - smallX);
                    }
                    if ((largeX - i) < right) {
                        right = (largeX - i);
                    }
                    if ((j - smallY) < top) {
                        top = (j - smallY);
                    }
                    if ((largeY - j) < bottom) {
                        bottom = (largeY - j);
                    }
                }
            }
        }
        bmp = Bitmap.createBitmap(bmp, left, top, imgWidth - left - right, imgHeight - top - bottom);
        return bmp;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_send) {
            Bitmap bitmap = drawView.getBitmap();
            if (bitmap != null) {
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, TITLE, null);
                Uri bitmapUri = Uri.parse(bitmapPath);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Кажется, делится пока нечем!", Toast.LENGTH_SHORT).show();
            }
//            Intent intent = drawView.saveBitmap();
//            if (intent != null)
//                startActivity(intent);
//            Bitmap bitmapOverlay = getBitmapOverlay();
//
//            String filename = "pippo.png";
//            File sd = Environment.getExternalStorageDirectory();
//            File dest = new File(sd, filename);
//
////            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
//            try {
//                FileOutputStream out = new FileOutputStream(dest);
//                bitmapOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);
//                out.flush();
//                out.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Bitmap bitmapImg = null;
//            for (Map.Entry<Integer, Bitmap> entry : bitmapMap.entrySet()) {
//                Bitmap bitmap = createTrimmedBitmap(entry.getValue());
//                bitmapImg = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
//            }
//            if (bitmapImg != null) {
//                resetCheckedImg();
//                bitmapMap.put(0, bitmapImg);
//            }
//            Bitmap bm = getBitmapFromAsset("1.png");
//            surfaceViewCustom.setPicture(bm);
//            List<String> imgNameList = getImgNameCheckedList();
//            ResultFragment resultFragment = ResultFragment.newInstance(imgNameList);
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.fragment_container, resultFragment);
//            ft.addToBackStack(null);
//            ft.commit();
            return true;
        }

        if (id == R.id.action_reset) {
            resetCheckedImg();
            return true;
        }

        if (id == R.id.action_random) {
            setCheckImg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetCheckedImg() {
        ViewGroup wrapper = findViewById(R.id.wrapper);
        for (int i = 1; i <= wrapper.getChildCount(); i++) {
            int resID = getResources().getIdentifier(CHECKBOX + i, ID, getPackageName());
            CheckBox checkBox = findViewById(resID);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            }
        }
    }

    private void setCheckImg() {
        ViewGroup wrapper = findViewById(R.id.wrapper);
        for (int i = 1; i <= wrapper.getChildCount(); i++) {
            int resID = getResources().getIdentifier(CHECKBOX + i, ID, getPackageName());
            CheckBox checkBox = findViewById(resID);
            checkBox.setChecked(Math.random() < 0.5);
        }
    }
}