package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SignersModel;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.example.parafdigitalyokesen.view.ui.draft.bottom_sheet.BottomSheetDraftInfo;

import java.util.List;

import kotlin.reflect.KFunction;

public class DraftListAdapter extends RecyclerView.Adapter<DraftListAdapter.ViewHolder>{
    private List<SignModel> models;
    private boolean isGrid;
    private FragmentManager fragmentManager;
    private int type;

    public DraftListAdapter(List<SignModel> model, boolean isGrid, FragmentManager fragmentManager, int type) {
        this.models = model;
        this.isGrid = isGrid;
        this.fragmentManager = fragmentManager;
        this.type = type;
    }
    /*
    * type is an identifier for what's fragment that access here.
    * 0=> DraftCompletedFragment
    * 1=> Draft Request Fragment
    * 2=> FragmentRequested in Collab
    * 3=> FragmentAccepted
    * 4=> FragmentRejected
    * */
    @NonNull
    @Override
    public DraftListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(isGrid?
                R.layout.recycler_view_draft_item_grid:
                R.layout.recycler_view_draft_item_list, parent, false);
        DraftListAdapter.ViewHolder viewHolder = new DraftListAdapter.ViewHolder(contactView);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                SignModel sign = models.get(position);
//                if(type == 0){
//                    //Click for Draft/Sign My Signature
//                    Intent intent= new Intent(context, ResultSignature.class);
//                    intent.putExtra("where", "mysign");
//                    view.getContext().startActivity(intent);
//                }
//                else if(type < 2){
//                    //Click for Draft/Sign My Request
//                    Intent intent= new Intent(context, RespondSignature.class);
//                    view.getContext().startActivity(intent);
//                }else{
//
//                }
                BottomSheetDraftInfo bottomSheet = new BottomSheetDraftInfo(sign, type);
                bottomSheet.show(fragmentManager, "ModalBottomSheet");
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
        ImageView ivList = holder.ivList;

        //Log.d("QRCODE", sign.getQr_code());
        if(type== 0 ){
            try {
                SVG svg = SVG.getFromString(sign.getQr_code());
                PictureDrawable  pd = new PictureDrawable(svg.renderToPicture());
                ivList.setImageDrawable(pd);
            } catch (SVGParseException e) {
                e.printStackTrace();
            }
            tvStatus.setVisibility(View.GONE);
        }else if (type == 1){
            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        }
        else if (type == 2){

            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPending) );
        } else if (type == 3){
            tvStatus.setVisibility(View.GONE);
        }
        else if (type == 4){

            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorError) );
        }
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
}
