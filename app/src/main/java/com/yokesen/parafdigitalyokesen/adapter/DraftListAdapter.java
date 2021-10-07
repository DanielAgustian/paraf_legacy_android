package com.yokesen.parafdigitalyokesen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.model.SignModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.view.ui.draft.bottom_sheet.BottomSheetDraftInfo;

import java.util.List;

public class DraftListAdapter extends RecyclerView.Adapter<DraftListAdapter.ViewHolder>{
    private List<SignModel> models;
    private boolean isGrid;
    private FragmentManager fragmentManager;
    private int type;
    Util util =new Util();
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
        tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        ivList.setImageBitmap(util.makeQRCOde(sign.getQr_code()));

        if(type == 0 ){
            tvStatus.setVisibility(View.GONE);
        }
        else if (type == 1){
            tvStatus.setText(sign.getDue_date());
            String status = models.get(position).getStatus();
            if(status == null || status.equals("")){
                    tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPending));
            }else if(status.toLowerCase().contains("rejected")){
                tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorError));
            }

        }
        else if (type == 2){
            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPending) );
            tvStatus.setText(sign.getStatus());
        } else if (type == 3){
            tvStatus.setVisibility(View.GONE);
        }
        else if (type == 4){
            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorError) );
            tvStatus.setText(sign.getStatus());
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
