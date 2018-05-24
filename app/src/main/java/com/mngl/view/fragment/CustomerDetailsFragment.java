package com.mngl.view.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.mngl.R;

import com.mngl.realm.Customer;
import com.mngl.utils.AlertClass;
import com.mngl.utils.Constant;
import com.mngl.utils.FontChangeCrawler;
import com.mngl.utils.MyApplication;
import com.mngl.utils.NetworkStatus;
import com.mngl.utils.SharedPref;
import com.mngl.utils.SnackBarUtils;
import com.mngl.utils.UploadPhotoDialog;

import com.mngl.view.activity.CropImageActivity;

import com.mngl.webservice.APIRequest;
import com.mngl.webservice.BaseResponse;
import com.mngl.webservice.WebServiceImageNew;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

public class CustomerDetailsFragment extends BaseFragment implements APIRequest.ResponseHandler, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_PICK_IMAGE = 2365;
    private static final int REQUEST_CROP_IMAGE = 2342;
    FontChangeCrawler fontChanger;
    View view;
    @BindView(R.id.btnNotAvailable)
    Button btnNotAvailable;
    @BindView(R.id.imgMeter)
    ImageView imgMeter;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.imgCapture)
    ImageView imgCapture;
    @BindView(R.id.frameMeter)
    FrameLayout frameMeter;
    @BindView(R.id.imgRightBracket)
    ImageView imgRightBracket;
    @BindView(R.id.imgLeftBracket)
    ImageView imgLeftBracket;
    File f;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private static int GALLARY = 3;
    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    Bitmap tempBmp;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    Uri fileUri;
    @BindView(R.id.txtMruNumber)
    TextView txtMruNumber;
    @BindView(R.id.txtBpNumber)
    TextView txtBpNumber;
    @BindView(R.id.edtMeterNumber)
    EditText edtMeterNumber;
    @BindView(R.id.edtMeterReading)
    EditText edtMeterReading;
    @BindView(R.id.edtComment)
    EditText edtComment;
    Realm realm;
    byte[] imageArrayToStoreInDB = null;
    JSONObject obj = null;
    JSONObject main_obj = null;
    private float minScale = 1f;
    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @BindView(R.id.txtCustomerAddress)
    TextView txtCustomerAddress;
    @BindView(R.id.txtCustomerPhone)
    TextView txtCustomerPhone;
    @BindView(R.id.txtTotalAmount)
    TextView txtTotalAmount;
    @BindView(R.id.txtMeterNumber)
    TextView txtMeterNumber;
    String bpNumber, meterNumber;
    @BindView(R.id.spMrNote)
    Spinner spMrNote;
    ArrayList<String> list;
    @BindView(R.id.linearMeterNo)
    LinearLayout linearMeterNo;
    boolean imageCaptured = false;
    private TextRecognizer detector;
    @BindView(R.id.txtBPNumber)
    TextView txtBPNumber;
    private String currentDateandTime = "";
    private final int IMAGE_MAX_SIZE = 1024;
    private ContentResolver mContentResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_customer_details, container, false);
        // bind the view using butterknife
        ButterKnife.bind(this, view);

        if (Constant.FRAGMENTCUSTOMER.equalsIgnoreCase("revisitagain")) {
            Constant.FRAGMENTCUSTOMER = "yes";
            mActivity.popFragments();
        } else {

        }
        init();

        setData();
        return view;
    }

    private void setData() {
        Bundle bundle = getArguments();
        txtMruNumber.setText(bundle.getString("mru_name"));
        bpNumber = bundle.getString("bp_no");
        txtBpNumber.setText("#" + bpNumber);
        txtCustomerName.setText(bundle.getString("name"));
        txtCustomerAddress.setText(bundle.getString("address"));
        txtCustomerPhone.setText(bundle.getString("mobile"));
        txtTotalAmount.setText("Total Amount :" + bundle.getString("amount"));
        meterNumber = bundle.getString("meter_number");
        txtMeterNumber.setText(meterNumber);
        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.GONE);
        txtBPNumber.setText(bpNumber);
    }

    private void init() {
        //Realm.init(mActivity);

        SharedPref pref = new SharedPref(mActivity);
        detector = new TextRecognizer.Builder(MyApplication.getContext()).build();

        realm = Realm.getDefaultInstance();
        list = new ArrayList<>();

        list.add("ACTUAL METER READING");
        list.add("METER NUMBER MISMATCH");
        list.add("NOT USING GAS");
        list.add("ADDRESS CORRECTION");
        list.add("METER DISCONNECTION");
        list.add("TEMPORARY DESTINATION");
        list.add("METER CHANGE");
        list.add("METER FAULTY");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spMrNote.setAdapter(adapter);

        spMrNote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).toString().equalsIgnoreCase("METER NUMBER MISMATCH")) {
                    linearMeterNo.setVisibility(View.VISIBLE);
                } else {
                    linearMeterNo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* mContentResolver = getActivity().getContentResolver();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        createDirIfNotExists();*/

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    @OnClick(R.id.btnNotAvailable)
    public void onClickNotAvailable() {
        Bundle bundle = new Bundle();
        bundle.putString("address", txtCustomerAddress.getText().toString());
        bundle.putString("customername", txtCustomerName.getText().toString());
        bundle.putString("customerno", txtCustomerPhone.getText().toString());
        bundle.putString("totalamnt", txtTotalAmount.getText().toString());
        bundle.putString("bpnum", bpNumber);
        bundle.putString("meternum", meterNumber);
        bundle.putString("meterreading", edtMeterReading.getText().toString());
        bundle.putString("mrunum", txtMruNumber.getText().toString());
        mActivity.pushFragments(new RevisitCustomerFragment(), false, true, bundle, "revisit");

    }

    @OnClick(R.id.imgCapture)
    public void onClickCaptureMeter() {

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            System.out.println("***********version*********" + MyVersion);
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                mMediaDialogListener.onCameraClick();
                //takePhoto();
                //Intent i = new Intent(mActivity, PreviewDemo.class);
                //startActivity(i);

            }
        } else {
            mMediaDialogListener.onCameraClick();
            //takePhoto();

        }
    }

    @OnClick(R.id.btnSave)
    public void onClickSaveDetails() {
        Constant.FRAGMENTCUSTOMER = "yes";
        if (validation()) {
            takeScreenshot();
        }

    }

    private boolean validation() {
        if (edtMeterReading.getText().toString().equalsIgnoreCase("")) {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Please enter meter reading");

            return false;
        } else if (!imageCaptured) {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Please add meter image");

            return false;
        }
        return true;
    }

    private void saveDetailsToDatabase(final String userId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Customer customer = new Customer();
                customer.setUserId(SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                customer.setIsAvailable("true");
                customer.setComment(edtComment.getText().toString());
                customer.setUser_id(userId);
                customer.setCustomerName(txtCustomerName.getText().toString());
                customer.setCustomerMobile(txtCustomerPhone.getText().toString());
                customer.setMu_number(txtMruNumber.getText().toString());
                customer.setMeter_no(edtMeterNumber.getText().toString());
                customer.setMeter_reading(edtMeterReading.getText().toString());
                customer.setImage(imageArrayToStoreInDB);
                customer.setBp_number(bpNumber);
                if (spMrNote.getSelectedItem().toString().equalsIgnoreCase("select mr note")) {
                    customer.setNote("");
                } else {
                    customer.setNote(spMrNote.getSelectedItem().toString());
                }

                realm.copyToRealm(customer);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");
                Constant.BP_NUMBER = bpNumber;
                SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Customer details saved successfully");

                mActivity.popFragments();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("TAG", "onError >>" + error.toString());
            }
        });
    }

    private void takeScreenshot() {

        imgCapture.setVisibility(View.INVISIBLE);
        imgRightBracket.setVisibility(View.INVISIBLE);
        imgLeftBracket.setVisibility(View.INVISIBLE);
        View view1 = frameMeter;

        Bitmap b = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        view1.draw(canvas);

        String extr = Environment.getExternalStorageDirectory().toString();
        f = new File(extr, "test" + ".jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), b,
                    "Screen", "screen");

            // Convert bitmap to byte array
            b = UploadPhotoDialog.decodeSampledBitmapFromPath(f.toString(), 320, 240);//50*50

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            imageArrayToStoreInDB = bitmapdata;

            if (NetworkStatus.isConnectingToInternet(getContext())) {

                uploadAsynchData();

            } else {
                saveDetailsToDatabase(Constant.userId);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void uploadAsynchData() {
        // TODO Auto-generated method stub
        Log.v("IN BROADCAST RECEIVER", "uploadAsynchData");

        obj = new JSONObject();
        main_obj = new JSONObject();
        JSONArray req = new JSONArray();

        try {

            String bp_number = bpNumber;
            String meter_reading = edtMeterReading.getText().toString();
            String comment = edtComment.getText().toString();

            String meterimg = Base64.encodeToString(imageArrayToStoreInDB, Base64.DEFAULT);

            JSONObject reqObj = new JSONObject();
            reqObj.put("is_available", "true");
            reqObj.put("bp_number", bp_number);
            reqObj.put("meter_reading", meter_reading);
            if (spMrNote.getSelectedItem().toString().equalsIgnoreCase("select mr note")) {
                reqObj.put("note", "");
            } else {
                reqObj.put("note", spMrNote.getSelectedItem().toString());
            }

            reqObj.put("comment", comment);
            reqObj.put("meter_image", meterimg);

            req.put(reqObj);

            SharedPref sharedPref = new SharedPref(mActivity);
            SharedPreferences preferences = SharedPref.getPreferences();

            main_obj.put("result", req);

            System.out.println("Json Obj>>>>>" + main_obj.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        submitDetails();

    }

    private void submitDetails() {

        try {

            WebServiceImageNew service = new WebServiceImageNew(callback);

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("result", new StringBody(main_obj.toString()));

            service.getService(mActivity, Constant.submitCustomerDetails, reqEntity);
        } catch (NullPointerException e) {
            System.out.println("Nullpointer Exception at Login Screen" + e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    WebServiceImageNew.CallbackImage callback = new WebServiceImageNew.CallbackImage() {
        @Override
        public void onSuccessImage(int reqestcode, JSONObject rootjson) {

            try {
                if (rootjson.getString("code").equalsIgnoreCase("200")) {
                    Constant.BP_NUMBER = bpNumber;

                    mActivity.popFragments();
                    SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Customer details saved successfully");

                } else if (rootjson.getString("code").equalsIgnoreCase("401")) {
                    AlertClass.sessionExpiredDialog(getContext(), rootjson.getString("msg"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorImage(int reqestcode, String error) {
        }

    };

    @OnClick(R.id.imgBack)
    public void onClickBack() {
        mActivity.popFragments();
    }

    @OnClick(R.id.imgMeter)
    public void onClickCaptureImage() {

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            System.out.println("***********version*********" + MyVersion);
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                // takePhoto();
                mMediaDialogListener.onCameraClick();

            }
        } else {
            //takePhoto();

            mMediaDialogListener.onCameraClick();
        }
    }

    UploadPhotoDialog.onMediaDialogListener mMediaDialogListener = new UploadPhotoDialog.onMediaDialogListener() {

        @Override
        public void onGalleryClick() {
            // TODO Auto-generated method stub
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(i, GALLARY);
        }

        @Override
        public void onDeleteClick() {

        }

        @Override
        public void onCameraClick() {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            System.out.println("File URI------" + fileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE);
          /*  Constant.orignalBitmap = null;
            // TODO Auto-generated method stub
            Constant.mImagePath = null;
            onGetImages(ConstantsImageCrop.IntentExtras.ACTION_CAMERA);*/
        }

    };

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
    * returning image / video
    */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("Image Result code-------" + resultCode);

        if (resultCode == RESULT_OK) {
           /* if (requestCode == REQUEST_PICK_IMAGE) {
                imgCapture.setVisibility(View.INVISIBLE);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bitmap = BitmapFactory.decodeFile(Constant.imageUri.getPath(), options);

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(Constant.imageUri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                System.out.println("Orientation >>> " + orientation);

                if (orientation == 6) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    matrix.setRotate(90, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                    bitmap = rotatedBitmap;
                }
                imgMeter.setImageBitmap(bitmap);
                imageCaptured = true;
                String imagePath = data.getStringExtra("image_path");

                readTextFromImage(getImageFromStorage(imagePath));

            }*/
            if (requestCode == CAMERA_CAPTURE) {
                try {
                    // bimatp factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger  // images
                    options.inSampleSize = 8;

                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    tempBmp = bitmap;

                    ExifInterface exif = new ExifInterface(fileUri.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    System.out.println("Orientation >>> " + orientation);

                    if (orientation == 6) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        matrix.setRotate(90, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                        tempBmp = rotatedBitmap;
                    }

                    imgCapture.setVisibility(View.GONE);
                    imgMeter.setImageBitmap(tempBmp);
                    System.out.println("Pic file Uri====" + fileUri.getPath() + ", Bitmap-------" + bitmap + "***" + fileUri);

                    UploadPhotoDialog.profile_ = fileUri.getPath().toString();

                    //getServiceResponseForPhoto(tempBmp);
                    System.out.println("****fileUri*********" + fileUri);
                    performCrop(fileUri);

                } catch (Exception e) {
                    System.out.println("Exception camera click--" + e.toString());
                }

            } else if (requestCode == PIC_CROP) {
                System.out.println("************PIC_CROP***********");
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                myDir.mkdirs();
                String fname = "Image-" + "test" + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                Log.i("LOAD", root + fname);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UploadPhotoDialog.profile_ = file.toString();
                System.out.println("******image******" + UploadPhotoDialog.profile_.toString());

                // retrieve a reference to the ImageView
                /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 40, bytes);
                try {
                    // you can create a new file name "test.jpg" in sdcard folder.
                    f = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
                    f.createNewFile();
                    // write the bytes in file
                    FileOutputStream fo;
                    UploadPhotoDialog.profile_ = f.getPath().toString();
                    fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
*/


                System.out.println("**********photo******" + UploadPhotoDialog.profile_);
                Intent intent = new Intent(mActivity, CropImageActivity.class);
                //  Uri imageUri = getPickImageResultUri(data);
                Constant.imageCrop = getResizedBitmap(thePic, 500,500);
                intent.putExtra(CropImageActivity.EXTRA_IMAGE_URI, UploadPhotoDialog.profile_);

                startActivityForResult(intent, REQUEST_CROP_IMAGE);
              /* *//**//* if (thePic != null)
                    tempBmp = UploadPhotoDialog.decodeSampledBitmapFromPath(f.toString(), 320, 240);//50*50
                imgMeter.setImageBitmap(tempBmp);
                // display the returned cropped image
                getServiceResponse();*//**//**/

            } else if (requestCode == REQUEST_CROP_IMAGE) {
                System.out.println("Image crop success :" + data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH));
                String imagePath = new File(data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH), "image.jpg").getAbsolutePath();
                // Intent result = new Intent();
                //result.putExtra("image_path", imagePath);
                //setResult(Activity.RESULT_OK, result);
                //finish();

                //String imagePath = data.getStringExtra("image_path");
                imgMeter.setImageBitmap(Constant.imageCrop);
                imageCaptured = true;
                readTextFromImage(getImageFromStorage(imagePath));
            }/* else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    //hideCropping();
                    //profile_image.setVisibility(View.VISIBLE);
                    return;
                }

                Constant.mImagePath = Constant.mFileTemp.getPath();
                Log.e("Fragment uSer profile", "Camera capture > " + Constant.mImagePath);
                Constant.orignalImagePath = Constant.mImagePath;
                Constant.imageToFilter = Constant.mImagePath;
                Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
                Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                Bitmap bmp = BitmapFactory.decodeFile(Constant.mImagePath, options);
                // tempBmp = bmp;

                if (!(bmp == null)) {
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(Constant.mImagePath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        System.out.println("Orientation >>> " + orientation);

                        switch (orientation) {
                            case 8:
                                Matrix matrix = new Matrix();
                                matrix.setRotate(270);
                                matrix.setRotate(270, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                                bmp = rotatedBitmap;
                                break;
                            case 6:
                                Matrix matrix1 = new Matrix();
                                matrix1.setRotate(90);
                                matrix1.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                Bitmap rotatedBitmap1 = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix1, true);// Return result
                                bmp = rotatedBitmap1;
                                break;
                            case 3:
                                Matrix matrix2 = new Matrix();
                                matrix2.setRotate(180);
                                matrix2.setRotate(180, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                Bitmap rotatedBitmap2 = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix2, true);// Return result
                                bmp = rotatedBitmap2;
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Constant.orignalBitmap = bmp;
                    Constant.finalBitmapImage = Constant.orignalBitmap;
                    startActivity(new Intent(mActivity, ActivityCropImage.class));
                    // overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    //getServiceResponseForPhoto(bmp);
                }
                // init();
            }*/
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (result1 == PackageManager.PERMISSION_GRANTED || result4 == PackageManager.PERMISSION_GRANTED || result5 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("************granted*********");
                    mMediaDialogListener.onCameraClick();

                } else {
                    //not granted
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Need Permissions");
                    builder.setMessage("This app needs camera permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestForSpecificPermission();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        System.out.println("**************" + response.toString());

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    /*private void takePhoto() {
        //startActivityForResult(new Intent(getContext(), ImagePickerActivity.class), REQUEST_PICK_IMAGE);
        startActivityForResult(new Intent(getContext(), PreviewDemo.class), REQUEST_PICK_IMAGE);
    }*/

    private void readTextFromImage(Bitmap bitmap) {
        if (detector.isOperational() && bitmap != null) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> textBlocks = detector.detect(frame);
            String blocks = "";
            String lines = "";
            String words = "";
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                blocks = blocks + tBlock.getValue() + "\n" + "\n";
                for (Text line : tBlock.getComponents()) {
                    //extract scanned text lines here
                    lines = lines + line.getValue() + "\n";
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        words = words + element.getValue() + ", ";
                    }
                }
            }
            if (textBlocks.size() == 0) {
                Log.e("TAG", "Scan Failed: Found nothing to scan");
            } else {
                edtMeterReading.setText(lines);
            }
        } else {

        }
    }

    private Bitmap getImageFromStorage(String path) {
        try {
            File f = new File(path);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 512, 512);

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Helper method to carry out crop operation
     *
     * @param picUri
     */
    public void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * This method for upload profile picture to server
     */
    public void getServiceResponse() {
        try {
            if (!(UploadPhotoDialog.profile_.equalsIgnoreCase(""))) {
                // create a file to write bitmap data
                f = new File(mActivity.getCacheDir(), "android.png");
                f.createNewFile();
                // Convert bitmap to byte array
                Bitmap bitmap = UploadPhotoDialog.decodeSampledBitmapFromPath(UploadPhotoDialog.profile_, 320, 240); //50*50
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                // write the bytes in file
                FileOutputStream fos = new FileOutputStream(f, false);
                fos.write(bitmapdata);
                FileOutputStream fos2;

                // create a file to write bitmap data
                File f1 = new File(mActivity.getCacheDir(), "useroriginal.png");
                f1.createNewFile();
                // Convert bitmap to byte array
                Bitmap bitmap1 = UploadPhotoDialog.decodeSampledBitmapFromPath(UploadPhotoDialog.profile_, 320, 240);
                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos1);
                byte[] bitmapdata1 = bos1.toByteArray();
                // write the bytes in file
                FileOutputStream fos1 = new FileOutputStream(f1);
                fos1.write(bitmapdata1);

            } else {
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

// create a matrix for the manipulation

        Matrix matrix = new Matrix();

// resize the bit map

        matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

}
