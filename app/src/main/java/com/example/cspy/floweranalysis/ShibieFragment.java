package com.example.cspy.floweranalysis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ShibieFragment extends Fragment {

    private final int TAKE_PHOTO = 0;
    private final int CHOOSE_PIC = 1;

    private final int CAMERA_OK = 11;
    private final int STORE_OK = 12;
    Uri imageUri;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));


                        Toast.makeText(getContext(), "图片拍摄成功", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "取消拍摄", Toast.LENGTH_SHORT).show();
                }
                break;
            case CHOOSE_PIC:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "图片选择成功", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
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
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageUri = FileProvider.getUriForFile(getActivity(), "com.example.cspy.floweranalysis.fileprovider", outputImage);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void choosePicture() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PIC);
    }


    private void cropPicture(File image) {
        if (imageUri != null) {
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            intent.setDataAndType(Uri.fromFile(new File(image.path)), "image/*");
//            intent.putExtra("crop", "true");
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
//            intent.putExtra("outputX", 720);
//            intent.putExtra("outputY", 720);
//            intent.putExtra("scale", true);
//            intent.putExtra("scaleUpIfNeeded", true);
//            intent.putExtra("return-data", false);
//            startActivityForResult(intent, 0x222);
        }

    }
}