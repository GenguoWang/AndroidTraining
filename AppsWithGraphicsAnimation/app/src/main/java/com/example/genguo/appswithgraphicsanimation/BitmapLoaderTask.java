package com.example.genguo.appswithgraphicsanimation;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by genguo on 5/15/16.
 */
public class BitmapLoaderTask extends AsyncTask<Integer, Void, Bitmap>{
    private final WeakReference<ImageView> imageViewWeakReference;
    private final Resources resources;

    public BitmapLoaderTask(ImageView imageView, Resources resources){
        this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.resources = resources;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        int data = params[0];
        return BitmapHelper.getBitmapForSize(resources, data, new BitmapHelper.Size(100,100));
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(imageViewWeakReference != null && bitmap != null){
            final ImageView imageView = imageViewWeakReference.get();
            if(imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
