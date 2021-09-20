package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.draft.ResultAfterRespond;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder>{
    private List<SignModel> models;

    private FragmentManager fragmentManager;

    Util util =new Util();
    int type;
    public SearchListAdapter(List<SignModel> model,FragmentManager fragmentManager, int type) {
        this.models = model;
        this.fragmentManager = fragmentManager;
        this.type = type;
    }
    /*
     * type is an identifier for what's fragment that access here.
     * -1 => Search General
     * 0  => MySign
     * 1  => MyReq
     * 2  => FragmentRequested in Collab
     * 3  => FragmentAccepted
     * 4  => FragmentRejected
     * */
    @NonNull
    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(
                R.layout.recycler_view_draft_item_list, parent, false);
        SearchListAdapter.ViewHolder viewHolder = new SearchListAdapter.ViewHolder(contactView);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                SignModel sign = models.get(position);
                if(type == -1){
                    filteringData(contactView, sign);
                }else if( type == 0){
                    gotoResultSiganture(contactView, sign);
                } else if (type == 1){
                    gotoResultAfterRespond(contactView, sign);
                } else{
                    gotoCollabResult(contactView, sign);
                }

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SignModel sign = models.get(position);
        TextView tvTitle = holder.tvTitle;
        tvTitle.setText(sign.getTitle());
        TextView tvTime = holder.tvTime;
        tvTime.setText(sign.getTime());
        TextView tvStatus = holder.tvStatus;
        tvStatus.setText(sign.getDue_date());
        ImageView ivList = holder.ivList;
        //Log.d("QRCODE", sign.getQr_code());
        tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        ivList.setImageDrawable(util.makeQRCOde(sign.getQr_code()));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle, tvTime, tvStatus;
        public ImageView ivList;
        public ViewHolder(View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleSignList);
            tvTime = itemView.findViewById(R.id.tvTimeSignList);
            tvStatus = itemView.findViewById(R.id.tvStatusListDraft);
            ivList = itemView.findViewById(R.id.ivSignList);
        }
    }

    public void gotoResultSiganture(View contactView, SignModel sign){
        Intent intent= new Intent(contactView.getContext(), ResultSignature.class);
        intent.putExtra("where", "mysign");
        intent.putExtra("id", sign.getId());
        contactView.getContext().startActivity(intent);
    }
    public void gotoResultAfterRespond(View contactView, SignModel sign){
        Intent intent= new Intent(contactView.getContext(), ResultAfterRespond.class);
        intent.putExtra("id", sign.getId());
        contactView.getContext().startActivity(intent);
    }

    public void gotoCollabResult(View contactView, SignModel sign){
        Intent intent = new Intent(contactView.getContext(), CollabResultActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", sign.getId());
        contactView.getContext().startActivity(intent);
    }

    public void filteringData(View contactView, SignModel sign){
        String location = sign.getLocation().toLowerCase();
        if(location.contains("collab")){
            gotoCollabResult(contactView, sign);
        } else if (location.contains("sign")){
            gotoResultSiganture(contactView, sign);
        } else if (location.contains("req")){
            gotoResultAfterRespond(contactView, sign);
        }
    }
}

