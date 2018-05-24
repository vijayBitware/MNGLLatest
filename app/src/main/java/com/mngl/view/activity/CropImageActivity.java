package com.mngl.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.mngl.R;
import com.mngl.utils.MyApplication;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CropImageActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener  {

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 100;

    public static final String CROPPED_IMAGE_PATH = "cropped_image_path";
    public static final String EXTRA_IMAGE_URI = "cropped_image_path";

    public static final String FIXED_ASPECT_RATIO = "extra_fixed_aspect_ratio";
    public static final String EXTRA_ASPECT_RATIO_X = "extra_aspect_ratio_x";
    public static final String EXTRA_ASPECT_RATIO_Y = "extra_aspect_ratio_y";

    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";

    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    private CropImageView mCropImageView;

    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private boolean isFixedAspectRatio = false;

    Bitmap croppedImage;
    @BindView(R.id.txtCrop)
    TextView txtCrop;
    private TextRecognizer detector;
    //endregion

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);
        detector = new TextRecognizer.Builder(MyApplication.getContext()).build();


        if(!getIntent().hasExtra(EXTRA_IMAGE_URI)) {
            cropFailed();
            return;
        }

        isFixedAspectRatio = getIntent().getBooleanExtra(FIXED_ASPECT_RATIO , false);
        mAspectRatioX = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_VALUES);
        mAspectRatioY = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_Y, DEFAULT_ASPECT_RATIO_VALUES);

        Uri imageUri = Uri.parse(getIntent().getStringExtra(EXTRA_IMAGE_URI));
        // Initialize components of the app
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        // If you want to fix the aspect ratio, set it to 'true'
        mCropImageView.setFixedAspectRatio(isFixedAspectRatio);
//        mCropImageView.setCropRect(new Rect(200,20,200,20));

        if (savedInstanceState == null) {
            mCropImageView.setImageUriAsync(imageUri);
        }
    }

    private void cropFailed() {
        Toast.makeText(mCropImageView.getContext(), "Image crop failed", Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnGetCroppedImageCompleteListener(null);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            //Toast.makeText(mCropImageView.getContext(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(mCropImageView.getContext(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(mCropImageView.getContext(), "Unable to load image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        if (error == null) {
            croppedImage = bitmap;
            try {
                String path = saveToInternalStorage(this, bitmap);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(CROPPED_IMAGE_PATH, path);
                //resultIntent.putExtra("orignal_path",)
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                cropFailed();
            }
        } else {
           cropFailed();
        }
    }

    private String saveToInternalStorage(Context context, Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"image.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //Bitmap scaledBitmap = getCompressedBitmap(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }

    @OnClick(R.id.txtCrop)
    public void onCrop(){
        mCropImageView.getCroppedImageAsync(mCropImageView.getCropShape(), 100, 50);
        //readTextFromImage(CROPPED_IMAGE_PATH);
    }
}