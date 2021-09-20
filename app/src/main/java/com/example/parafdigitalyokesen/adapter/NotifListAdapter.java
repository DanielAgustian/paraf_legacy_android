package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.NotificationModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.example.parafdigitalyokesen.viewModel.NotificationState;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotifListAdapter  extends RecyclerView.Adapter<NotifListAdapter.ViewHolder>{
    List<NotificationModel> notifs;
    NotificationModel notif;
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
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                notif = notifs.get(position);
                APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                PreferencesRepo preferencesRepo = new PreferencesRepo(context);
                String token = preferencesRepo.getToken();
                Observable<SimpleResponse> GetSearchData = apiInterface.ReadNotifOnce(token, notif.getId());
                GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                        this::onSuccessGetNotif, this::onFailedGetNotif);

            }

            private void onFailedGetNotif(Throwable throwable) {
                Util util = new Util();
                util.toastError(context, "API READ NOTIF", throwable);
            }

            private void onSuccessGetNotif(SimpleResponse simpleResponse) {
                if(simpleResponse!= null){
                    NotificationState.getSubject().onNext("ref");
                    clickDataIntent(notif, context);
                }
            }
        });
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

    private void clickDataIntent(NotificationModel notif, Context context){
        String message = notif.getTitle().toLowerCase();
        if(message.contains("new request") || message.contains("sign invitation") ||
                message.contains("final") || message.contains("forget")){
            Intent intent= new Intent(context, RespondSignature.class);
            intent.putExtra("id", notif.getCollabId());
            context.startActivity(intent);
        } else if (message.contains("accept") || message.contains("reject") ){
            Intent intent= new Intent(context, CollabResultActivity.class);
            intent.putExtra("id", notif.getCollabId());
            intent.putExtra("type", 2);
            context.startActivity(intent);
        }
    }

}
