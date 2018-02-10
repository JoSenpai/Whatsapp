/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import static com.google.android.gms.analytics.internal.zzy.s;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  EditText username;
  EditText password;
  Button login;
  TextView signUp;
  ImageView wallpaper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
    wallpaper.setOnClickListener(this);

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }

  public void login(View view)
  {
    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if(e == null)
        {
          Log.i("Login Success!", "Successful");

          Intent intent = new Intent(MainActivity.this, Chats.class);
          startActivity(intent);

        }else
        {
          Log.i("Login Failed", "The user does not exist");
        }
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
      }
    });
  }

  public void init()
  {
    username = (EditText)findViewById(R.id.username);
    password = (EditText)findViewById(R.id.password);
    login = (Button)findViewById(R.id.login);
    signUp = (TextView)findViewById(R.id.signUp);
    wallpaper = (ImageView)findViewById(R.id.wallpaper);
  }

  public void signUp(View view)
  {
    Intent intent = new Intent(this, SignUp.class);
    startActivity(intent);
  }

  @Override
  public void onClick(View view) {
    if(view.getId() == R.id.wallpaper)
    {
      InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }
}