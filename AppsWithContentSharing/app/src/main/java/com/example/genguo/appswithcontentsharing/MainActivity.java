package com.example.genguo.appswithcontentsharing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Uri imageUri0;
    private Uri imageUri1;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.d("Kingo","start by intent: "+ action + " "+type);
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
        // todo, and content provider for the image, and make the uri
        // or use media store.
        intent.putExtra(Intent.EXTRA_STREAM, imageUri0);
        intent.setType("image/jpeg");
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

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        Log.d("Kingo", "set share intent before");
        if (shareActionProvider != null) {
            Log.d("Kingo", "set share intent");
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

}
