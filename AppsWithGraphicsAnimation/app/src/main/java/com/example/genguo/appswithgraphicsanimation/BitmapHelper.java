package com.example.genguo.appswithgraphicsanimation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by genguo on 5/15/16.
 */
public final class BitmapHelper {

    public static final class Size{
        private final int width;
        private final int height;
        public Size(int width, int height){
            this.width = width;
            this.height = height;
        }
        public int getWidth(){
            return width;
        }

        public int getHeight(){
            return height;
        }
    }

    private BitmapHelper(){ }

    public static Bitmap getBitmapForSize(Resources resources, int resouceId, Size size){
        Size originSize = getSize(resources, resouceId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(originSize, size);
        return BitmapFactory.decodeResource(resources, resouceId,options);
    }

    public static Size getSize(Resources resources, int resourceId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceId, options);
        return new Size(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(Size originSize, Size targetSize){
        final int height = originSize.getHeight();
        final int width = originSize.getWidth();
        final int targetHeight = targetSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        int inSampleSize = 1;
        if(height > targetHeight && width > targetWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while(halfHeight/inSampleSize > targetHeight && halfWidth/inSampleSize > targetWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
