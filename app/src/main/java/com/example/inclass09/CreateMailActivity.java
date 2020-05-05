package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CreateMailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String API_KEY="API_KEY";
    String apikey;
    EditText subject,message;
    Button send,cancel;
    Spinner spinner;
    ArrayList<String> userslist=new ArrayList<String>();
    ArrayList<String> userslistnew=new ArrayList<String>();
    ArrayList<Integer> idlist=new ArrayList<Integer>();
    int position_id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mail);
        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        apikey=sharedPreferences.getString(API_KEY,"");
        subject=findViewById(R.id.et_subject);
        message=findViewById(R.id.et_message);
        send=findViewById(R.id.btn_send);
        cancel=findViewById(R.id.btn_scancel);
        spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        setTitle("Create new Mail");
        final OkHttpClient client = new OkHttpClient();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(CreateMailActivity.this,HomeScreenActivity.class);
                startActivity(i1);
            }
        });
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                .header("Authorization","BEARER "+apikey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d("bha","onReponse: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray=jsonObject.getJSONArray("users");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object= jsonArray.getJSONObject(i);
                        String user=object.getString("fname") + " " +object.getString("lname");
                        String id=object.getString("id");
                        userslist.add(user);
                        idlist.add(Integer.parseInt(id));
                    }
//                    System.out.println(responseBody.string());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userslistnew.addAll(userslist);
                            ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(CreateMailActivity.this,android.R.layout.simple_spinner_item,userslistnew);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OkHttpClient client = new OkHttpClient();
                Log.d("position", String.valueOf(idlist.get(spinner.getSelectedItemPosition())));
                RequestBody formBody = new FormBody.Builder()
                        .add("receiver_id", String.valueOf(idlist.get(spinner.getSelectedItemPosition())))
                        .add("subject",subject.getText().toString())
                        .add("message",message.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                        .header("Authorization","BEARER "+apikey)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);

                            Headers responseHeaders = response.headers();
                            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                Log.d("demo",responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }
                            //Log.d("demo",responseBody.string());

//                        JSONObject jsonObject=new JSONObject(responseBody.string());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CreateMailActivity.this,"Sent succesfully",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        position_id=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
