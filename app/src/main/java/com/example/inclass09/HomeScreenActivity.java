package com.example.inclass09;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HomeScreenActivity extends AppCompatActivity implements InboxAdapter.InteractActivity {
    TextView textView,textView1;
    ImageView logout,addnew;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager rv_layoutManager;
    RecyclerView.Adapter adapter;
    JSONArray jsonArray;
    public static final String API_KEY="API_KEY";
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String USER_DETAILS="USER DETAILS";
    String apikey;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        textView=findViewById(R.id.tv_name);
        apikey=sharedPreferences.getString("API_KEY","");
        logout=findViewById(R.id.imageLogout);
        addnew=findViewById(R.id.imageNew);
        recyclerView=findViewById(R.id.recyclerView);
        setTitle("Inbox");
        recyclerView.setHasFixedSize(true);
        rv_layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_layoutManager);
        Gson gson=new Gson();
        String json=sharedPreferences.getString(USER_DETAILS,"");
        Log.d("json",json);
        User user=gson.fromJson(json,User.class);
        Log.d("user",sharedPreferences.getString("USER",""));
        textView.setText(sharedPreferences.getString("USER",""));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(HomeScreenActivity.this,MainActivity.class);
                startActivity(i1);
            }
        });
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(HomeScreenActivity.this,CreateMailActivity.class);
                startActivity(i1);
            }
        });

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
                .header("Authorization","BEARER "+apikey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    JSONObject jsonObject=new JSONObject(responseBody.string());
                    jsonArray=jsonObject.getJSONArray("messages");
                    Log.d("messages",jsonArray.toString());
                    adapter=new InboxAdapter(jsonArray,HomeScreenActivity.this);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d("bha","onReponse: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                        }
                    });
//                    System.out.println(responseBody.string());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        //textView.setText(sharedPreferences.getString(API_KEY,""));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void deleteItem(int position) {
        final OkHttpClient client = new OkHttpClient();
        Request request = null;
        try {
            request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+jsonArray.getJSONObject(position).getInt("id"))
                    .header("Authorization","BEARER "+sharedPreferences.getString(API_KEY,""))
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                    Log.d("bha",responseBody.string() );
                }
            }
        });

        jsonArray.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void nextItem(int position) {
        Bundle extras=new Bundle();
        try {
            extras.putString("sender",jsonArray.getJSONObject(position).getString("sender_fname"));
            extras.putString("subject",jsonArray.getJSONObject(position).getString("subject"));
            extras.putString("message",jsonArray.getJSONObject(position).getString("message"));
            extras.putString("date",jsonArray.getJSONObject(position).getString("created_at"));
            Intent i1=new Intent(HomeScreenActivity.this,DisplayMailActivity.class);
            i1.putExtras(extras);
            startActivity(i1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
