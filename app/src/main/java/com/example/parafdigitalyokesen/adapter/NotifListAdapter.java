package com.example.parafdigitalyokesen.adapter;

import android.app.Notification;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.NotificationModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifListAdapter  extends RecyclerView.Adapter<NotifListAdapter.ViewHolder>{
    List<NotificationModel> notifs;
    public NotifListAdapter(List<NotificationModel> notifs) {
        this.notifs = notifs;
    }

    @NonNull
    @Override
    public NotifListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_view_notif_item_list, parent, false);
        NotifListAdapter.ViewHolder viewHolder = new NotifListAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotifListAdapter.ViewHolder holder, int position) {
        NotificationModel model = notifs.get(position);
        TextView tvName = holder.tvName;
        tvName.setText(model.getTitle());

        TextView tvDetails = holder.tvDetails;
        tvDetails.setText(Html.fromHtml(model.getDetails()));
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvDetails;
        public ImageView ivNotifIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameNotif);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            ivNotifIcon = itemView.findViewById(R.id.ivNotifItem);
        }
    }
}
