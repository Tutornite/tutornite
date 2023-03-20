package com.example.tutornite.adapters;

import static com.example.tutornite.utils.Constants.FROM_HOME_SCREEN;
import static com.example.tutornite.utils.Constants.FROM_ORGANISED_SESSION;
import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.Constants.remoteUpcomingSessions;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.activities.SessionDetailsActivity;
import com.example.tutornite.interfaces.SessionEventsInterface;
import com.example.tutornite.models.SessionDetailsModel;
import com.example.tutornite.utils.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    Context context;
    ArrayList<SessionDetailsModel> sessionsListDetailsModel;
    SessionEventsInterface sessionEventsInterface;
    String currentUserID;
    String fromWhichScreen;

    public SessionsAdapter(Context context,
                           ArrayList<SessionDetailsModel> sessionsListDetailsModel,
                           SessionEventsInterface sessionEventsInterface,
                           String uid, String fromWhichScreen) {
        this.context = context;
        this.sessionsListDetailsModel = sessionsListDetailsModel;
        this.sessionEventsInterface = sessionEventsInterface;
        this.currentUserID = uid;
        this.fromWhichScreen = fromWhichScreen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.events_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionDetailsModel sessionDetailsModel = sessionsListDetailsModel.get(position);

        if (!TextUtils.isEmpty(sessionDetailsModel.getUserThumb())) {
            Glide.with(context)
                    .load(Base64.decode(sessionDetailsModel.getUserThumb(), Base64.DEFAULT))
                    .into(holder.img_user_image);
        }

        holder.txt_event_title.setText(sessionDetailsModel.getSessionTitle());
        holder.txt_event_short_desc.setText(sessionDetailsModel.getSessionDetails());
        holder.txt_event_address.setText(sessionDetailsModel.getSessionLocation());

        holder.txt_event_date.setText(convertTimestampToFormat(app_date_format, sessionDetailsModel.getSessionDateTime()));
        holder.txt_event_time.setText(convertTimestampToFormat("hh:mm a", sessionDetailsModel.getSessionDateTime()).toUpperCase());

        holder.lin_join_lay.setOnClickListener(view -> {
            sessionEventsInterface.joinSession(sessionDetailsModel.getDocumentID(),
                    sessionDetailsModel.getSessionTitle(), position);
        });

        holder.lin_cancel_lay.setOnClickListener(view -> {
            sessionEventsInterface.cancelSession(sessionDetailsModel.getDocumentID(), position);
        });

        holder.lin_delete_lay.setOnClickListener(view -> {
            sessionEventsInterface.deleteSession(sessionDetailsModel.getDocumentID(), position);
        });

        holder.lin_author_lay.setVisibility(View.INVISIBLE);
        holder.lin_cancel_lay.setVisibility(View.INVISIBLE);
        holder.lin_delete_lay.setVisibility(View.INVISIBLE);
        holder.lin_join_lay.setVisibility(View.INVISIBLE);

        if (fromWhichScreen.equalsIgnoreCase(FROM_ORGANISED_SESSION)) {
            holder.lin_delete_lay.setVisibility(View.VISIBLE);
        } else if (fromWhichScreen.equalsIgnoreCase(FROM_HOME_SCREEN)) {
            if (remoteUpcomingSessions.contains(sessionDetailsModel.getDocumentID())) {
                holder.lin_cancel_lay.setVisibility(View.VISIBLE);
            } else if (sessionDetailsModel.getPostedByUID().equals(currentUserID)) {
                holder.lin_author_lay.setVisibility(View.VISIBLE);
            } else {
                holder.lin_join_lay.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(view -> {
            Constants.sessionDetails = sessionDetailsModel;
            Intent intent = new Intent(context, SessionDetailsActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return sessionsListDetailsModel.size();
    }

    public void removeItem(int position) {
        sessionsListDetailsModel.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img_user_image;
        TextView txt_event_title, txt_event_short_desc, txt_event_address, txt_event_date, txt_event_time;
        LinearLayout lin_join_lay, lin_cancel_lay, lin_author_lay, lin_delete_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_event_title = itemView.findViewById(R.id.txt_event_title);
            txt_event_short_desc = itemView.findViewById(R.id.txt_event_short_desc);
            img_user_image = itemView.findViewById(R.id.img_user_image);
            txt_event_address = itemView.findViewById(R.id.txt_event_address);
            txt_event_date = itemView.findViewById(R.id.txt_event_date);
            txt_event_time = itemView.findViewById(R.id.txt_event_time);
            lin_join_lay = itemView.findViewById(R.id.lin_join_lay);
            lin_cancel_lay = itemView.findViewById(R.id.lin_cancel_lay);
            lin_author_lay = itemView.findViewById(R.id.lin_author_lay);
            lin_delete_lay = itemView.findViewById(R.id.lin_delete_lay);
        }
    }
}

