/*
    Assignment: Inclass09
    Team members:
    Akhil Madhamshetty:801165622
    Tarun thota:801164383
 */
package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    EditText username,password;
    Button btn_login,btn_signup;
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String API_KEY="API_KEY";
    public static final String USER_DETAILS="USER DETAILS";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        username=(EditText)findViewById(R.id.et_email);
        password=(EditText)findViewById(R.id.et_password);
        btn_login=findViewById(R.id.btn_login);
        btn_signup=findViewById(R.id.btn_signup);
        setTitle("Mailer");
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals(""))
                    username.setError("Required Field");
                else if (password.getText().toString().equals(""))
                    password.setError("Required Field");
                else {
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", username.getText().toString())
                            .add("password", password.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://ec2-18-191-172-10.us-east-2.compute.amazonaws.com:3000/api/auth/login")
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try (ResponseBody responseBody = response.body()) {
                                if (!response.isSuccessful()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,"Incorrect email and/or password",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    throw new IOException("Unexpected code " + response);
                                }


                                Headers responseHeaders = response.headers();
                                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                    Log.d("demo", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }
                                //Log.d("demo",responseBody.string());
                                JSONObject jsonObject=new JSONObject(responseBody.string());
                                String token=jsonObject.getString("token");
//                        JSONObject jsonObject=new JSONObject(responseBody.string());
                                if (jsonObject.getString("auth").equals("true")) {
                                    Log.d("data","I am here");
                                    editor.putString(API_KEY, token);
                                    editor.commit();
                                    Intent i1 = new Intent(MainActivity.this, HomeScreenActivity.class);
                                    startActivity(i1);
                                }
                                else
                                    Toast.makeText(MainActivity.this,"Incorrect email and/or password",Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }


        });
    }
}