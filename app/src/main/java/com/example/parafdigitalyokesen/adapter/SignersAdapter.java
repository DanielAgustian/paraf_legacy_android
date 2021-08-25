package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.SignersModel;
import com.example.parafdigitalyokesen.view.ui.collab.bottom_sheet.BottomSheetSeeQR;

import java.util.List;

public class SignersAdapter extends RecyclerView.Adapter<SignersAdapter.ViewHolder> {


    @NonNull
    @Override
    public SignersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_view_signers_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SignersModel signers = mSigners.get(position);
        TextView tvName = holder.tvNameRv;
        tvName.setText(signers.getName());
        TextView tvEmail = holder.tvEmailRV;
        tvEmail.setText(signers.getEmail());
    }

    @Override
    public int getItemCount() {
        return mSigners.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvNameRv, tvEmailRV, tvListStatus, tvReqDocs;
        public LinearLayout llButton;
        public Button btnView, btnSave;
        public ViewHolder(View itemView){
            super(itemView);
            tvNameRv = itemView.findViewById(R.id.tvListName);
            tvEmailRV = itemView.findViewById(R.id.tvListEmail);
            tvReqDocs = itemView.findViewById(R.id.tvListReqFile);
            llButton = itemView.findViewById(R.id.llButtonSigners);
            tvListStatus = itemView.findViewById(R.id.tvListStatusSigners);
            btnView = itemView.findViewById(R.id.btnViewQR);
            btnSave = itemView.findViewById(R.id.btnSaveQR);




            if(type<3){
                llButton.setVisibility(View.GONE);
            }else if (type == 3){
                llButton.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                btnView.setOnClickListener(btnViewListener);

            }
            else if (type == 4){
                llButton.setVisibility(View.VISIBLE);
                tvReqDocs.setVisibility(View.VISIBLE);
                btnView.setOnClickListener(btnViewListener);
                btnSave.setOnClickListener(btnSaveListener);
            }
            else{
                llButton.setVisibility(View.GONE);
            }
        }
    }
    private List<SignersModel> mSigners;
    private int type;
    private FragmentManager fragmentManager;
    /*
    * type is identifier for what layout is accessing this adapter.
    * 0=> Result Signature in Add_sign
    * 1=> Respond Signature in Draft
    * 2=> Result After Respond in Draft
    * 3=> Collab Result from Requested
    * 4=> Collab Result from Accepted
    * 5=> Collab Result from Rejected
    * */
    public SignersAdapter(List<SignersModel> mSigners, int type, FragmentManager fragmentManager) {
        this.mSigners = mSigners;
        this.type = type;
        this.fragmentManager = fragmentManager;
    }

    View.OnClickListener btnViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BottomSheetSeeQR bottomSheetSeeQR = new BottomSheetSeeQR("view");
            bottomSheetSeeQR.show(fragmentManager, "ModalBottomSheet");
        }
    };

    View.OnClickListener btnSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BottomSheetSeeQR bottomSheetSeeQR = new BottomSheetSeeQR("save");
            bottomSheetSeeQR.show(fragmentManager, "ModalBottomSheet");
        }
    };
}
