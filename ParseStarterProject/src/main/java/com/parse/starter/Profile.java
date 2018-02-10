package com.parse.starter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static android.R.attr.data;
import static com.google.android.gms.analytics.internal.zzy.d;
import static com.google.android.gms.analytics.internal.zzy.e;
import static com.google.android.gms.analytics.internal.zzy.p;
import static com.google.android.gms.analytics.internal.zzy.q;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    ImageView profilePicture;
    EditText status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");
        profilePicture = (ImageView)findViewById(R.id.profilePicture);
        status = (EditText) findViewById(R.id.status);
        profilePicture.setOnClickListener(this);
        getCurrentStatus();
        getCurrentPhoto();
    }

    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if share is selected, if no permission ask for permission
        if(item.getItemId() == R.id.profilePicture) {
            //if version is marshmellow ask for permission, else just get photo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            } else {
                getPhoto();
            }
        } else {
            getPhoto();
        }
    }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //when image is clicked in the media
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            //similar to url, but link to image
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.i("Photo","Received");

                //data is written into a byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                //compresses the image to PNG format and told to be in byte array
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                //stores the bitmap into small bytes
                byte[] byteArray = stream.toByteArray();
                System.out.println(byteArray);
                //each file has the bytes of an image
                ParseFile file = new ParseFile("image.png", byteArray);

                ParseUser currentUser = ParseUser.getCurrentUser();
                //where images are stored
                currentUser.put("profilePicture",file);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(Profile.this, "Image saved", Toast.LENGTH_SHORT).show();
                            getCurrentPhoto();
                        }else {
                            Toast.makeText(Profile.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {

        //if share is selected, if no permission ask for permission
        if(view.getId() == R.id.profilePicture) {
            //if version is marshmellow ask for permission, else just get photo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //ask for permission
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
                } else {
                    getPhoto();
                }
            } else {
                getPhoto();
            }
        }
    }

    @Override
    public void onBackPressed() {
        storeStatus();
        super.onBackPressed();
    }

    public void storeStatus()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("status", status.getText().toString());
        currentUser.saveInBackground();
    }

    public void getCurrentStatus()
    {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereExists("status");
        query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null)
                {
                    for(ParseUser object:objects) {
                        status.setText(object.get("status").toString());
                    }
                }
            }
        });
    }

    public void getCurrentPhoto()
    {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereExists("profilePicture");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        Log.i("Object size is", Integer.toString(objects.size()));
                        for(ParseUser object : objects)
                        {
                            ParseFile file = (ParseFile)object.get("profilePicture");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null & data != null)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                        profilePicture.setImageBitmap(bitmap);
                                    }
                                }
                            });


                        }
                    }
                }
            }
        });

    }
}
