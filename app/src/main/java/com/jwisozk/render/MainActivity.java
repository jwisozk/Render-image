package com.jwisozk.render;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String ID = "id";
    private final static String CHECKBOX = "checkbox_";
    private final static String IMAGE_VIEW = "imageView_";
    private final static String INTENT_TYPE = "image/*";
    private final static String TITLE = "Totoro";
    private final Map<Integer, Bitmap> bitmapMap = new HashMap<>();
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

        AssetManager assetManager = getAssets();
        String[] imgNameArray = getAllImagesFromAssets(assetManager);
        if (imgNameArray != null) {
            BitmapOperations bitmapOperations = new BitmapOperations(getResources());
            final List<String> imgNameList = new ArrayList<>(Arrays.asList(imgNameArray));
            bitmapList = bitmapOperations.createBitmapList(imgNameList);
            List<Bitmap> minBitmapList = new ArrayList<>(bitmapList);
            minBitmapList = bitmapOperations.changeBitmapScale(minBitmapList, 0.5f);
            minBitmapList = bitmapOperations.getTrimmedBitmapList(minBitmapList);
            minBitmapList = bitmapOperations.changeBitmapScale(minBitmapList, 2f);
            setImages(minBitmapList);
        }
        setOnCheckedListener();
    }

    public void setImages(List<Bitmap> bitmapList) {
        if (bitmapList != null) {
            String pn = getPackageName();
            for (int i = 0; i < bitmapList.size(); i++) {
                Bitmap bm = bitmapList.get(i);
                if (bm != null) {
                    int resID = getResources().getIdentifier(IMAGE_VIEW + (i + 1), ID, pn);
                    ImageView img = findViewById(resID);
                    img.setImageBitmap(bm);
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

    private void setOnCheckedListener() {
        ViewGroup wrapper = findViewById(R.id.wrapper);
        for (int i = 1; i <= wrapper.getChildCount(); i++) {
            int resID = getResources().getIdentifier(CHECKBOX + i, ID, getPackageName());
            CheckBox checkBox = findViewById(resID);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String checkboxId = buttonView.getResources()
                            .getResourceEntryName(buttonView.getId());
                    Integer id = Integer.parseInt(checkboxId.substring(checkboxId.length() - 1));
                    if (isChecked) {
                        bitmapMap.put(id, bitmapList.get(id - 1));
                    } else {
                        bitmapMap.remove(id);
                    }
                    drawView.setBitmapMap(bitmapMap);
                    drawView.invalidate();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {
            sendImage();
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

    private void sendImage() {
        Bitmap bitmap = drawView.getBitmap();
        if (bitmap != null) {
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                    TITLE, null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(INTENT_TYPE);
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.impossible_to_share, Toast.LENGTH_SHORT).show();
        }
    }
}