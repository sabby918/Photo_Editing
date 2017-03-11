package com.example.sarah.editpicture;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    ///////////////////////////////////////////////////////////////////////////

    TextView textTitle;
    ImageView image;
    SeekBar barR, barG, barB, barAlpha;
    int ColorValues;

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_PIC_REQUEST = 101;
    private static final String PREFS_NAME = "PrefsFile";
    private static final String COLOR_VALUES = "ColorVals";
    private static final String PICTURE = "Picture";

    ///////////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textTitle = (TextView)findViewById(R.id.title);
        image = (ImageView)findViewById(R.id.image);
        barR = (SeekBar)findViewById(R.id.red);
        barG = (SeekBar)findViewById(R.id.green);
        barB = (SeekBar)findViewById(R.id.blue);
        barAlpha = (SeekBar)findViewById(R.id.alpha);

        barR.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        barG.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        barB.setOnSeekBarChangeListener(myOnSeekBarChangeListener);
        barAlpha.setOnSeekBarChangeListener(myOnSeekBarChangeListener);

        //default color
        ColorValues = barAlpha.getProgress() * 0x1000000
                + barR.getProgress() * 0x10000
                + barG.getProgress() * 0x100
                + barB.getProgress();


        image.setColorFilter(ColorValues, Mode.MULTIPLY);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        ColorValues = settings.getInt(COLOR_VALUES, ColorValues);
    }


///////////////////////////////Menu//////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.savePicture:
                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                MediaStore.Images.Media.insertImage
                        (getContentResolver(), bitmap, "yourTitle", "yourDescription");

        }
        return true;
    }

/////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////On Click Functions/////////////////////////////////////////////////////
    public void selectPicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }


    public void takePicture(View view){

        try{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////Activity///////////////////////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    image.setImageURI(selectedImage);// To display selected image in image view
                    break;
                }
            case CAMERA_PIC_REQUEST:
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    thumbnail = Bitmap.createScaledBitmap(thumbnail, 700, 500, true);
                    ImageView imageView = (ImageView) findViewById(R.id.image);
                    image.setImageBitmap(thumbnail);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                break;
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////


//////////////////////////////Warning before exit///////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close\nthis activity? \n\nProgress will NOT be saved ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null);
            AlertDialog dialog = builder.show();
            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
            dialog.show();
    }

///////////////////////////////////////////////////////////////////////////////////////////////

 /////////////////////////Editing the Picture//////////////////////////////////////////////////
    OnSeekBarChangeListener myOnSeekBarChangeListener = new OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            //Colorize ImageView
            int newColor = barAlpha.getProgress() * 0x1000000
                    + barR.getProgress() * 0x10000
                    + barG.getProgress() * 0x100
                    + barB.getProgress();
            image.setColorFilter(newColor, Mode.MULTIPLY);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            ColorValues = newColor;

            editor.putInt(COLOR_VALUES, ColorValues);
            // Commit the edits
            editor.commit();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);

        icicle.putInt(COLOR_VALUES, ColorValues);
    }
}