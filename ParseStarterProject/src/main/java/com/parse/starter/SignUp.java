package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.a;
import static com.google.android.gms.analytics.internal.zzy.j;
import static com.google.android.gms.analytics.internal.zzy.m;

public class SignUp extends AppCompatActivity {

    EditText username;
    EditText password;
    Button register;
    ParseUser parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ExampleObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {

                }
            }
        });
        init();
    }

    public void register(View view)
    {
        parseUser = new ParseUser();

        parseUser.setUsername(username.getText().toString());
        parseUser.setPassword(password.getText().toString());

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Log.i("DONE", "IS IT WORKING?");
                    putPhoto();

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setTitle("Registration")
                            .setMessage("Sign up successful")
                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                }
            }
        });


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void putPhoto()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.defaultpic);

        //data is written into a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //compresses the image to PNG format and told to be in byte array
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

        //stores the bitmap into small bytes
        byte[] byteArray = stream.toByteArray();
        //each file has the bytes of an image
        ParseFile file = new ParseFile("image.png", byteArray);

        //where images are stored
        parseUser.put("profilePicture",file);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    Log.i("Saved","Imaged success");
                }
            }
        });

    }

    public void init()
    {
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        register = (Button)findViewById(R.id.register);
    }
}
