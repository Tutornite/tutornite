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
            openEmailApp(userModel.getEmail());
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


    public void openEmailApp(String emailId) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailId });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email body");

        Intent chooserIntent = Intent.createChooser(emailIntent, "Select Email App");
        context.startActivity(chooserIntent);
    }

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

