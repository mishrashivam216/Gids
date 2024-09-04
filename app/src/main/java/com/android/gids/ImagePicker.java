package com.android.gids;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImagePicker {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private Uri imageUri;

    public void showImagePickerDialog(final Fragment fragment) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(fragment.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "Pic_" + System.currentTimeMillis() + ".jpg");
                    imageUri = FileProvider.getUriForFile(fragment.getContext(),
                            fragment.getContext().getPackageName() + ".fileprovider", photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fragment.startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    fragment.startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Bitmap handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                return handleCameraResult(context);
            } else if (requestCode == SELECT_FILE) {
                return handleGalleryResult(context, data);
            }
        }
        return null;
    }

    private Bitmap handleCameraResult(Context context) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private Bitmap handleGalleryResult(Context context, Intent data) {
        if (data != null) {
            try {
                Uri selectedImageUri = data.getData();
                InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to select image from gallery", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }


}
