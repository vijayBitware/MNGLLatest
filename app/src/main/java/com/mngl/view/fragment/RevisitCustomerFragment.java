package com.mngl.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mngl.webservice.WebServiceImageNew;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class RevisitCustomerFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    JSONObject obj = null;
    JSONObject main_obj = null;
    byte[] imageArrayToStoreInDB;
    FontChangeCrawler fontChanger;
    View view;
    @BindView(R.id.spReason)
    Spinner spReason;
    ArrayList<String> list;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.imgMeter)
    ImageView imgMeter;
    @BindView(R.id.imgCapture)
    ImageView imgCapture;
    File f;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private static int GALLARY = 3;
    final int CAMERA_CAPTURE = 1;
    Bitmap tempBmp;

    Uri fileUri;
    @BindView(R.id.frameMeter)
    FrameLayout frameMeter;

    Realm realm;
    @BindView(R.id.edtComment)
    EditText edtComment;
    Bundle bundle;
    String mrunum, bpnum, meterreading, meternum, customerName, customerNo, totalAmnt, address;
    @BindView(R.id.txtMeterNumber)
    TextView txtMeterNumber;
    @BindView(R.id.txtBPNumber)
    TextView txtBPNumber;
    boolean imageCaptured = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_revisit_customer, container, false);

        // bind the view using butterknife
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    private void init() {
        SharedPref pref = new SharedPref(mActivity);
        realm = Realm.getDefaultInstance();
        bundle = getArguments();
        address = bundle.getString("address");
        customerName = bundle.getString("customername");
        customerNo = bundle.getString("customerno");
        totalAmnt = bundle.getString("totalamnt");
        mrunum = bundle.getString("mrunum");
        meternum = bundle.getString("meternum");
        meterreading = bundle.getString("meterreading");

        txtMeterNumber.setText(meternum);
        bpnum = bundle.getString("bpnum");
        txtBPNumber.setText(bpnum);
        list = new ArrayList<>();

        list.add("DOOR LOCK");
        list.add("ADDRESS NOT FOUND");
        list.add("CUSTOMER NOT ALLOWED");
        list.add("SOCIETY NOT ALLOWED");
        list.add("CONVERSATION NOT DONE");
        list.add("METER NOT INSTALLED");
        list.add("NO GAS CONNECTION");
        list.add("OTHER");

        ImageView imgFilter = mActivity.findViewById(R.id.imgFilter);
        imgFilter.setVisibility(View.GONE);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spReason.setAdapter(adapter);

    }

    @OnClick(R.id.imgBack)
    public void onClickBack() {
        mActivity.popFragments();
    }

    @OnClick(R.id.btnSave)
    public void onClickSaveDetails() {
        Constant.FRAGMENTCUSTOMER = "revisitagain";
        if (validation()) {
            takeScreenshot();
        }

    }

    private boolean validation() {
        if (!imageCaptured) {
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Please add image");

            return false;
        }
        return true;
    }

    @OnClick(R.id.imgMeter)
    public void onClickCaptureImage() {

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            System.out.println("***********version*********" + MyVersion);
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                mMediaDialogListener.onCameraClick();

            }
        } else {

            mMediaDialogListener.onCameraClick();

        }

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

            }
        } else {

            mMediaDialogListener.onCameraClick();

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

                    imgCapture.setVisibility(View.INVISIBLE);
                    imgMeter.setImageBitmap(tempBmp);
                    System.out.println("Pic file Uri====" + fileUri.getPath() + ", Bitmap-------" + bitmap);
                    imageCaptured = true;
                    UploadPhotoDialog.profile_ = fileUri.getPath().toString();

                    getServiceResponseForPhoto(tempBmp);

                } catch (Exception e) {
                    System.out.println("Exception camera click--" + e.toString());
                }

            } else if (requestCode == GALLARY && resultCode == RESULT_OK && null != data) {

                System.out.println("Picture path in GALLARY:");

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                try {
                    File f = null;
                    if (picturePath != null)
                        f = new File(picturePath);
                    else
                        Toast.makeText(getActivity(), "Error while rendering image.", Toast.LENGTH_SHORT).show();

                    f.createNewFile();
                    UploadPhotoDialog.profile_ = f.toString();
                    System.out.println("Picture path in UploadPhotoDialog.profile_:" + UploadPhotoDialog.profile_);
                    cursor.close();
                } catch (FileNotFoundException e) {
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error while rendering image.", Toast.LENGTH_SHORT).show();
                }
                Bitmap bmp = UploadPhotoDialog.decodeSampledBitmapFromPath(picturePath, 500, 500);

                if (bmp != null)
                    tempBmp = bmp;

                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();
                // downsizing image as it throws OutOfMemory Exception for larger  // images
                options.inSampleSize = 8;

                final Bitmap bitmap = BitmapFactory.decodeFile(UploadPhotoDialog.profile_, options);
                tempBmp = bitmap;

                System.out.println("Picture path :" + UploadPhotoDialog.profile_);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(UploadPhotoDialog.profile_);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                System.out.println("Orientation >>> " + orientation);

                if (orientation == 6) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    matrix.setRotate(90, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                    tempBmp = rotatedBitmap;
                }
                imageCaptured = true;
                imgCapture.setVisibility(View.INVISIBLE);
                imgMeter.setImageBitmap(tempBmp);
                getServiceResponse();

            }
        }
    }

    /**
     * This method for upload profile picture to server
     */
    public void getServiceResponse() {
        try {
            if (!(UploadPhotoDialog.profile_.equalsIgnoreCase(""))) {
                // create a file to write bitmap data
                System.out.println("******++******" + UploadPhotoDialog.profile_);
                f = new File(getActivity().getCacheDir(), "android.png");
                f.createNewFile();
                // Convert bitmap to byte array
                Bitmap bitmap = UploadPhotoDialog.decodeSampledBitmapFromPath(UploadPhotoDialog.profile_, 500, 500); //50*50
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                // write the bytes in file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f, false);
                    fos.write(bitmapdata);
                    FileOutputStream fos2;

                    f = new File(getActivity().getCacheDir(), "android1.png");
                    f.createNewFile();
                    fos2 = new FileOutputStream(f, false);
                    fos2.write(bitmapdata);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void getServiceResponseForPhoto(Bitmap bitmap) {
        tempBmp = bitmap;
        try {
            f = new File(Environment.getExternalStorageDirectory() + "/_camera.png");
            if (f != null) {
                f.createNewFile();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array
                // save it in your external storage.
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(byteArray);
                tempBmp = bitmap;
                FileOutputStream fos2;

            } else {
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fontChanger = new FontChangeCrawler(mActivity.getAssets());
        fontChanger.replaceFonts((ViewGroup) mActivity.findViewById(android.R.id.content));
    }

    private void takeScreenshot() {

        View view1 = frameMeter;

        Bitmap b = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        view1.draw(canvas);

        String extr = Environment.getExternalStorageDirectory().toString();
        File myPath = new File(extr, "test" + ".jpg");
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), b,
                    "Screen", "screen");

            // Convert bitmap to byte array
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

    private void saveDetailsToDatabase(final String userId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Customer customer = new Customer();
                customer.setUser_id(userId);
                customer.setUserId(SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                if (spReason.getSelectedItem().toString().equalsIgnoreCase("door lock")) {
                    customer.setNote(spReason.getSelectedItem().toString());
                } else {
                    customer.setNote(spReason.getSelectedItem().toString());
                }

                customer.setIsAvailable("false");
                customer.setAddres(address);
                customer.setMu_number(mrunum);
                customer.setMeter_no(meternum);
                customer.setMeter_reading(meterreading);
                customer.setImage(imageArrayToStoreInDB);
                customer.setBp_number(bpnum);
                customer.setComment(edtComment.getText().toString());
                customer.setCustomerName(customerName);
                customer.setCustomerMobile(customerNo);
                customer.setTotalAmnt(totalAmnt);
                realm.copyToRealm(customer);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("TAG", "onSuccess >> Data Inserted Successfully..!!");
                Constant.BP_NUMBER = bpnum;
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

    private void uploadAsynchData() {
        // TODO Auto-generated method stub

        obj = new JSONObject();
        main_obj = new JSONObject();
        JSONArray req = new JSONArray();

        try {

            String bp_number = bpnum;
            String meter_reading = meterreading;
            String note = spReason.getSelectedItem().toString();

            String meterimg = Base64.encodeToString(imageArrayToStoreInDB, Base64.DEFAULT);

            System.out.println("******bppppp*****" + bp_number);
            JSONObject reqObj = new JSONObject();
            reqObj.put("is_available", "false");
            reqObj.put("bp_number", bp_number);
            reqObj.put("meter_reading", meter_reading);
            reqObj.put("comment", edtComment.getText().toString());

            if (spReason.getSelectedItem().toString().equalsIgnoreCase("door lock")) {
                reqObj.put("note", note);
            } else {
                reqObj.put("note", note);
            }

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
                    Constant.BP_NUMBER = bpnum;
                    SnackBarUtils.showSnackBarBlue(MyApplication.getContext(), mActivity.findViewById(android.R.id.content), "Customer details saved successfully");
                    mActivity.popFragments();
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

}
