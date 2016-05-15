package com.example.genguo.appswithmultimedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO_CODE = 10;
    private static final int CAPTURE_VIDEO_CODE = 11;
    private ImageView imageView;
    private VideoView videoView;
    private String imageFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_PLAY);
        startService(intent);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        imageView = (ImageView)findViewById(R.id.imageView);
        videoView = (VideoView)findViewById(R.id.videoView);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePhoto(View v){
        Log.d("kingo","take photo");
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File image = null;
            try{
                image = createImageFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            if(image!=null){
                Log.d("kingo", " file"+image.getAbsolutePath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(image));
            }
            startActivityForResult(intent,TAKE_PHOTO_CODE);
        }
    }

    public void captureVideo(View v){
        Log.d("Kingo", "capture video");
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, CAPTURE_VIDEO_CODE);
        }
    }

    public void useCamera(View v){
        Intent intent = new Intent(this, UseCameraAcivity.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "JPEG_"+timestamp+"_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(name,".jpg",dir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("kingo","on activity result");
        if(requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK){
            Bundle extra = data.getExtras();
            if(extra != null) {
                Bitmap bitmap = (Bitmap) extra.get("data");
                imageView.setImageBitmap(bitmap);
            }
            else {
                setPic();
            }
        }
        else if(requestCode == CAPTURE_VIDEO_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Log.d("kingo","on video result:"+uri);
            videoView.setVideoURI(uri);
            videoView.start();
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 200;
        int targetH = 200;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}
