package com.example.inclass09;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    Context ctx;
    public static InteractActivity interact;
    JSONArray messages;

    public InboxAdapter(JSONArray jsonArray, HomeScreenActivity homeScreenActivity) {
        this.messages=jsonArray;
        this.ctx=homeScreenActivity;
        Log.d("Inbox Subjects",messages.toString());

    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        interact = (InteractActivity) ctx;
        try {

            holder.tv_subject.setText(messages.getJSONObject(position).getString("subject"));

            String datestr = messages.getJSONObject(position).getString("created_at");
            String toDate = parseDateToddMMyyyy(datestr);
            holder.tv_date.setText(toDate);

            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interact.deleteItem(position);
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interact.nextItem(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return messages.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_subject, tv_date;
        ImageView img_delete;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_date=itemView.findViewById(R.id.tv_date);
            img_delete=itemView.findViewById(R.id.iv_delete);
            cardView=itemView.findViewById(R.id.cardView);


        }
    }


    public interface InteractActivity{
        void deleteItem(int position);
        void nextItem(int position);
    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }
}
