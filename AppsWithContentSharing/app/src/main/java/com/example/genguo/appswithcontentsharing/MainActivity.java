package com.example.genguo.appswithcontentsharing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Uri imageUri0;
    private Uri imageUri1;
    private ShareActionProvider shareActionProvider;
    private Intent requestFileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.d("Kingo","start by intent: "+ action + " "+type);
        setImageUri();
        requestFileIntent = new Intent();
        requestFileIntent.setType("image/*");
        requestFileIntent.setAction(Intent.ACTION_PICK);

        /*
         must add intent-filter in AndroidManifest.xml,
         otherwise the activity cannot be started by outside intents
         */
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                //handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                //handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    private void setImageUri() {
        String name = "joe.jpg";
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.joe);
        File imagePath = new File(getFilesDir(), "images");
        File newFile = new File(imagePath, name);
        if (!newFile.exists()) {
            saveBitmapToFile(imagePath, name, bm, Bitmap.CompressFormat.JPEG, 70);
        }
        File requestFile = new File(imagePath, "requestFile.jpg");
        imageUri1 = FileProvider.getUriForFile(this, "com.example.genguo.appswithcontentsharing.fileprovider", newFile);
        if (requestFile.exists()) {
            imageUri0 = FileProvider.getUriForFile(this, "com.example.genguo.appswithcontentsharing.fileprovider", requestFile);
        } else{
            imageUri0 = imageUri1;
        }
    }

    public boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        if(!dir.exists()) {
            dir.mkdir();
        }
        File imageFile = new File(dir,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format,quality,fos);

            fos.close();

            return true;
        }
        catch (IOException e) {
            Log.e("Kingo",e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        //use MenuItemCompact instead of item.getActionProvider() for appcompactactivity
        Log.d("Kingo", "item: "+item);
        shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(item);
        return true;
    }

    public void sendTextContent(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello from Kingo");
        intent.setType("text/plain");
        boolean wantsChooser = false;
        if(wantsChooser) {
            /*
             This has some advantages:
             Even if the user has previously selected a default action for this intent, the chooser will still be displayed.
             If no applications match, Android displays a system message.
             You can specify a title for the chooser dialog.
             */
            startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
        } else {
            startActivity(intent);
        }
    }

    public void sendBinaryContent(View v){
       /*
        Binary data is shared using the ACTION_SEND action combined with setting the appropriate MIME type
        and placing the URI to the data in an extra named EXTRA_STREAM.
         */
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri0);
        Log.d("Kingo","send binary: "+imageUri0);
        intent.setType("image/jpeg");
        //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        revokeUriPermission(imageUri0, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
    }

    public void sendMultiple(View v){
        ArrayList<Uri> imageUris = new ArrayList<>();
        imageUris.add(imageUri0);
        imageUris.add(imageUri1);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
    }

    public void setEasyShare(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Easy Share");
        setShareIntent(intent);
    }

    public void requestFile(View v){
        //startActivity(requestFileIntent);
        startActivityForResult(requestFileIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Kingo","on result");
        if(resultCode != RESULT_OK){
            return;
        } else {
            Log.d("Kingo","save on result");
           Uri returnUri = data.getData();
            printInfo(returnUri);
            try {
                File imagePath = new File(getFilesDir(), "images");
                File newFile = new File(imagePath, "requestFile.jpg");
                final int chunkSize = 1024;  // We'll read in one kB at a time
                byte[] imageData = new byte[chunkSize];

                    InputStream in = getContentResolver().openInputStream(returnUri);
                    OutputStream out = new FileOutputStream(newFile);  // I'm assuming you already have the File object for where you're writing to

                    int bytesRead;
                    while ((bytesRead = in.read(imageData)) > 0) {
                        out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
                    }
                in.close();
                out.close();
                setImageUri();
            }catch (FileNotFoundException e){
                e.printStackTrace();
                Log.e("Kingo", "Return file not found!");
            }
            catch (IOException e){
                e.printStackTrace();
                Log.e("Kingo", "Error!");
            }
        }
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        Log.d("Kingo", "set share intent before");
        if (shareActionProvider != null) {
            Log.d("Kingo", "set share intent");
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void printInfo(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null,null);
        int nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        Log.d("King", " uri name: "+ cursor.getString(nameIdx));
        Log.d("King", " uri size: "+ cursor.getLong(sizeIdx));
        cursor.close();
    }

}
