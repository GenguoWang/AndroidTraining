package com.example.genguo.appswithmultimedia;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by genguo on 5/14/16.
 */
public class Preview extends ViewGroup implements SurfaceHolder.Callback{
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("kingo", "surface created");
    }

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    List<Size> supportedSizes;
    Preview(Context context){
        super(context);
        surfaceView = new SurfaceView(context);
        addView(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamrea(Camera camrea){
        if(this.camera == camrea){return;}
        stopPreviewAndFreeCamera();
        this.camera = camrea;
        if(camrea != null){
            List<Size> localSizes = camrea.getParameters().getSupportedPreviewSizes();
            supportedSizes = localSizes;
            requestLayout();
            try{
                camrea.setPreviewDisplay(surfaceHolder);
            } catch (IOException e){
                e.printStackTrace();
            }
            camrea.startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(width, height);
        requestLayout();
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera != null){
            camera.startPreview();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private void stopPreviewAndFreeCamera() {

        if (camera != null) {
            camera.stopPreview();

            camera.release();

            camera = null;
        }
    }
}
