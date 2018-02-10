package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import static android.R.attr.layout;
import static android.R.attr.resource;
import static com.parse.starter.R.id.custom;
import static com.parse.starter.R.id.profilePicture;

class CustomAdapter extends BaseAdapter {

    ImageView myImage;
    TextView myText;
    TextView myDesc;
    Bitmap bitmap;

    private Context mContext;
    private List<User> users;


    CustomAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.users = users;
    }

    public void getCurrentPhoto()
    {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereExists("profilePicture");
        query.whereEqualTo("username", users);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null)
                {
                    if(objects.size() > 0)
                    {
                        Log.i("Object size ", Integer.toString(objects.size()));
                        for(ParseUser object : objects)
                        {
                            ParseFile file = (ParseFile)object.get("profilePicture");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null & data != null)
                                    {
                                        Log.i("FINALLY","LOADED");
                                        bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                        myImage.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }
                    }else
                    {
                        Log.i("Object size", " ZERO");
                        myImage.setImageResource(R.drawable.defaultpic);
                    }
                }else
                {
                    Log.i("e is null", "cmon");
                    myImage.setImageResource(R.drawable.defaultpic);
                }
            }
        });

    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //Date currentTime = Calendar.getInstance().getTime();

        View customView = View.inflate(mContext, R.layout.activity_custom_adapter, null);

        myText = (TextView)customView.findViewById(R.id.listText);
        myDesc = (TextView)customView.findViewById(R.id.listDesc);
        myImage = (ImageView)customView.findViewById(R.id.listImage);

        myText.setText(users.get(i).getUsername());
        myDesc.setText(users.get(i).getDescription());
        myImage.setImageBitmap(users.get(i).getProfilePicture());

        return customView;
    }
}