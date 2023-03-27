package com.example.tutornite.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.activities.BaseActivity;
import com.example.tutornite.activities.SessionDetailsActivity;
import com.example.tutornite.models.UserModel;
import com.example.tutornite.utils.Constants;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> userDetailsModel;
    String currentUserID;
    String fromWhichScreen;

    public TutorsAdapter(Context context,
                           ArrayList<UserModel> userDetailsModel,
                           String uid, String fromWhichScreen) {
        this.context = context;
        this.userDetailsModel = userDetailsModel;
        this.currentUserID = uid;
        this.fromWhichScreen = fromWhichScreen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tutor_profile_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userModel = userDetailsModel.get(position);

        if (!TextUtils.isEmpty(userModel.getUserImage())) {
            Glide.with(context)
                    .load(Base64.decode(userModel.getUserImage(), Base64.DEFAULT))
                    .into(holder.img_user_image);
        }

        holder.txt_tutor_name.setText(userModel.getFirstName() + " " + userModel.getLastName());



        holder.lin_contact_lay.setOnClickListener(view -> {
//            openWebPage("mailto:" + userModel.getEmail());
        });

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return userDetailsModel.size();
    }

    public void removeItem(int position) {
        userDetailsModel.remove(position);
        notifyItemRemoved(position);
    }

//    public void openWebPage(String url) {
//        try {
//            Uri webpage = Uri.parse(url);
//            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
//            startActivity();
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, getString(R.string.no_apps_can_manage), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img_user_image;
        TextView txt_tutor_name;
        LinearLayout lin_contact_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user_image = itemView.findViewById(R.id.img_user_image);
            txt_tutor_name = itemView.findViewById(R.id.txt_tutor_name);
            lin_contact_lay = itemView.findViewById(R.id.lin_contact_lay);
        }
    }
}

