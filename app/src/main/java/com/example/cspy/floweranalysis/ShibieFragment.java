package com.example.cspy.floweranalysis;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ShibieFragment extends Fragment {

    private String tempPicPath;
    private String tempTakePhotoPath;

    private static final String TAG = "ShibieFragment";

    private final int TAKE_PHOTO = 0;
    private final int CHOOSE_PIC = 1;

    private final int CAMERA_OK = 11;
    private final int STORE_OK = 12;
    private final int CROP_PIC = 13;

    Uri imageUri;

    Bitmap bitmap;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "图片拍摄成功", Toast.LENGTH_SHORT).show();
                    cropPicture(tempTakePhotoPath);
                } else {
                    Toast.makeText(getContext(), "取消拍摄", Toast.LENGTH_SHORT).show();
                }
                break;
            case CHOOSE_PIC:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "图片选择成功", Toast.LENGTH_SHORT).show();
                    handleImageOnKitkat(data);

                } else {
                    Toast.makeText(getContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_PIC:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "图片剪切成功 照片位置:" + tempPicPath, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onActivityResult: tempPicPath:" + tempPicPath);

                    Intent intent = new Intent(getActivity(), ShowResultActivity.class);
                    intent.putExtra("imagePath", tempPicPath);
                    startActivity(intent);


                } else {
                    Toast.makeText(getContext(), "剪切图片失败", Toast.LENGTH_SHORT).show();
                }
            default:
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(getContext(), "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORE_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePicture();
                } else {
                    Toast.makeText(getContext(), "未授权，打开相册失败", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_shibie,container,false);

        Button btnFromSD = view.findViewById(R.id.fromSD);
        Button btnFromCamera = view.findViewById(R.id.fromCramera);

        tempPicPath = getActivity().getExternalCacheDir() + "/temp.jpg";
        tempTakePhotoPath = getActivity().getExternalCacheDir() + "/tempPhoto.jpg";

        btnFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_OK);
                } else {
                    takePhoto();
                }
            }
        });

        btnFromSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORE_OK);
                } else {
                    choosePicture();
                }
            }
        });

        return view;
    }

    private void takePhoto() {
        File outputImage = new File(tempTakePhotoPath);

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Uri picUri = Uri.fromFile(outputImage);
        Uri picUri = FileProvider.getUriForFile(getActivity(), "com.example.cspy.floweranalysis.fileprovider", outputImage);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void choosePicture() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PIC);
    }

    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        Log.e(TAG, "handleImageOnKitkat: imagePath:" + imagePath);
        cropPicture(imagePath);

    }



    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void cropPicture(String imagePath) {

        File outputImage = new File(tempPicPath);

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.parse(imagePath), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));

        startActivityForResult(intent, CROP_PIC);

    }


}
