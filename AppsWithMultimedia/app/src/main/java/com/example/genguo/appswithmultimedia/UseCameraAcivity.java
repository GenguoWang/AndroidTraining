package com.example.genguo.appswithmultimedia;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class UseCameraAcivity extends AppCompatActivity {
    private Camera mCamera;
    private Preview mPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use_camera);
        mPreview = new Preview(this);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);
        if(safeCameraOpen(1)){
            Log.d("king", "camera opened");
            mPreview.setCamrea(mCamera);
        }
    }

    private boolean safeCameraOpen(int id){
        boolean qOpened = false;
        try{
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        }
        catch (Exception e){
            Log.e("kingo", "failed open camera");
            e.printStackTrace();
        }
        return qOpened;
    }

    private void releaseCameraAndPreview(){
        mPreview.setCamrea(null);
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
}
