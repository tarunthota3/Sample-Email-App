package com.example.inclass09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {
    EditText fname,lname,email,cpassword,rpassword;
    Button btn_signup,btn_cancel;
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String API_KEY="API_KEY";
    public static final String USER="USER";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        fname=findViewById(R.id.et_fname);
        lname=findViewById(R.id.et_lname);
        email=findViewById(R.id.et_smail);
        cpassword=findViewById(R.id.et_cpass);
        rpassword=findViewById(R.id.et_rpass);
        setTitle("Sign Up");

        btn_signup=findViewById(R.id.btn_ssignup);
        btn_cancel=findViewById(R.id.btn_scancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(i1);
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cpassword.getText().toString().equals(rpassword.getText().toString())) {
                    if (fname.getText().toString().equals(""))
                        fname.setError("Required Field");
                    else if (lname.getText().toString().equals(""))
                        lname.setError("Required Field");
                    else if (email.getText().toString().equals(""))
                        email.setError("Required Field");
                    else if (cpassword.getText().toString().equals(""))
                        cpassword.setError("Required Field");
                    else {
                        final OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("email", email.getText().toString())
                                .add("password", cpassword.getText().toString())
                                .add("fname", fname.getText().toString())
                                .add("lname", lname.getText().toString())
                                .build();

                        Request request = new Request.Builder()
                                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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
                                    JSONObject jsonObject = new JSONObject(responseBody.string());
                                    final String message=jsonObject.getString("message");
                                    Log.d("demo", "JSONOBJECT: " + jsonObject);
                                    if (!response.isSuccessful()){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                        throw new IOException("Unexpected code " + response);

                                    }


                                    Headers responseHeaders = response.headers();
                                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                        Log.d("demo", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                    }



                                    if (jsonObject.getString("status").equals("ok")) {
                                        String token = jsonObject.getString("token");
                                        String fullname=jsonObject.getString("user_fname") + " " + jsonObject.getString("user_lname");
                                        Log.d("full name",fullname);
                                        editor.putString(API_KEY, token);
                                        editor.putString(USER,fullname);
                                        editor.commit();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this,"User has been created",Toast.LENGTH_SHORT).show();
                                        Intent i1=new Intent(RegisterActivity.this,HomeScreenActivity.class);
                                        startActivity(i1);
                                    }
                                });
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Choose Password and Retype password should be same",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
