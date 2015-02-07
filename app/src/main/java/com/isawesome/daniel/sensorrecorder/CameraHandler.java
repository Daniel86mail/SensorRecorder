package com.isawesome.daniel.sensorrecorder;

/**
 * Created by Daniel on 07/02/2015.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.content.Intent;
import android.provider.MediaStore;
import android.net.Uri;
import android.app.Activity;



public class CameraHandler {

    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    public void TakePictureAction(int sessionId, Context context){
        dispatchTakePictureIntent(sessionId, context);
    }



    private File createImageFile(int sessionId) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFolder = String.format("/Session_%d" , sessionId);
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/SensorRecorder" + imageFolder);  //make this common to Logger and camera Handler
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent(int sessionId, Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(sessionId);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                Activity callerActivity = (Activity) context;
                callerActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
