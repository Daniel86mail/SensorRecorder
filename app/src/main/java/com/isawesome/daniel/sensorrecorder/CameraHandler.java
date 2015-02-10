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



public class CameraHandler extends MainActivity{

    private static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private Boolean m_callToActivity;
    private String m_photoFileName;
    private Context m_callerContext;

    public void TakePictureAction(int sessionId, Context context){
        m_callToActivity = true;
        m_callerContext = context;
        dispatchTakePictureIntent(sessionId);
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



    private void dispatchTakePictureIntent(int sessionId) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(m_callerContext.getPackageManager()) != null) {
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
                m_photoFileName = photoFile.getName();
                Activity caller = (Activity) m_callerContext;
                caller.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            SendBroadcast(true, m_photoFileName, m_callerContext);
        }
        m_callToActivity = false;
    }

    public boolean GetActivityCallStatus(){
        return m_callToActivity;
    }


    private void SendBroadcast(Boolean result, String Msg, Context context){
        Intent i = new Intent();
        i.setAction("CameraHandler.PictureTaken");
        i.putExtra("FileName", Msg);
        context.sendBroadcast(i);
    }
}
