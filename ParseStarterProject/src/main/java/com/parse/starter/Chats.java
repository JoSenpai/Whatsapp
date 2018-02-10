package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.content.Intent;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.analytics.internal.zzy.B;
import static com.google.android.gms.analytics.internal.zzy.a;
import static com.google.android.gms.analytics.internal.zzy.c;
import static com.google.android.gms.analytics.internal.zzy.i;
import static com.google.android.gms.analytics.internal.zzy.s;

public class Chats extends AppCompatActivity {

    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();

    //new and improved
    ArrayList<User> userArrayList = new ArrayList<>();
    Bitmap bitmap;

    ListAdapter listAdapter;
    CustomAdapter customAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profile) {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
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
                            Toast.makeText(Chats.this, "Image sent", Toast.LENGTH_SHORT).show();
                            getCurrentPhoto();
                        }else {
                            Toast.makeText(Chats.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        setTitle("Chats");

        final ListView chats = (ListView) findViewById(R.id.chatList);
        listAdapter = new CustomAdapter(this, userArrayList);

        List<Map<String, String>> chatData = new ArrayList<Map<String, String>>();

        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Log.i("Objects", "More than 0");
                        List<Map<String, String>> chatData = new ArrayList<Map<String, String>>();

                        for (ParseObject users : objects) {

                            /*
                            Map<String, String> chatInfo = new HashMap<String, String>();
                            chatInfo.put("username", users.getString("username"));
                            chatInfo.put("content", users.getString("status"));
                            chatData.add(chatInfo);
                            */
                            usernames.add(users.getString("username"));

                            userArrayList.add(new User(users.getString("username"), users.getString("status"), null));

                        }
                        System.out.println(usernames);
                        getCurrentPhoto();
                        //SimpleAdapter simpleAdapter = new SimpleAdapter(Chats.this, chatData, android.R.layout.simple_list_item_2, new String[]{"username","content"}, new int[]{android.R.id.text1, android.R.id.text2});
                        customAdapter = new CustomAdapter(getApplicationContext(), userArrayList);

                        chats.setAdapter(customAdapter);
                        Log.i("User size", Integer.toString(userArrayList.size()));
                    }
                }
            }
        });

        chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(usernames.get(i));

                Intent intent = new Intent(Chats.this, Conversation.class);
                intent.putExtra("name", usernames.get(i));
                startActivity(intent);

            }
        });

    }

    public void getCurrentPhoto()
    {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereExists("profilePicture");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        Log.i("Object size ", Integer.toString(objects.size()));
                        for(final ParseUser object : objects)
                        {
                            ParseFile file = (ParseFile)object.get("profilePicture");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null & data != null)
                                    {
                                        bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                        for(int i = 0; i < userArrayList.size(); i++)
                                        {
                                            if(userArrayList.get(i).getUsername().equals(object.getUsername()))
                                            {
                                                userArrayList.get(i).setProfilePicture(bitmap);
                                            }
                                        }
                                        customAdapter.notifyDataSetChanged();
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
