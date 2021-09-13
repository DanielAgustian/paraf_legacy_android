package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.SignersModel;
import com.example.parafdigitalyokesen.view.ui.collab.bottom_sheet.BottomSheetSeeQR;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignersAdapter extends RecyclerView.Adapter<SignersAdapter.ViewHolder> {

    View contactView;
    @NonNull
    @Override
    public SignersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        contactView = inflater.inflate(R.layout.recycler_view_signers_item, parent, false);
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
        TextView tvStatus = holder.tvListStatus;
        tvStatus.setText(signers.getInfo());
        if(signers.getPhoto() != null){
            if(!(signers.getPhoto().equals(""))){
                ImageView civ = holder.civPhoto;
                new DownLoadImageTask(civ).execute(signers.getPhoto());
            }
        }
        if(signers.getStatus().trim().toLowerCase().contains("waiting")){
            tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPending));
        }

        Button btnSave = holder.btnSave;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetSeeQR bottomSheetSeeQR = new BottomSheetSeeQR("save",mSigners.get(position) , id);
                bottomSheetSeeQR.show(fragmentManager, "ModalBottomSheet");
            }
        });

        Button btnView = holder.btnView;
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetSeeQR bottomSheetSeeQR = new BottomSheetSeeQR("save", mSigners.get(position), id);
                bottomSheetSeeQR.show(fragmentManager, "ModalBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSigners.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvNameRv, tvEmailRV, tvListStatus, tvReqDocs;
        public LinearLayout llButton;
        public Button btnView, btnSave;
        public ImageView civPhoto;
        public ViewHolder(View itemView){
            super(itemView);
            tvNameRv = itemView.findViewById(R.id.tvListName);
            tvEmailRV = itemView.findViewById(R.id.tvListEmail);
            tvReqDocs = itemView.findViewById(R.id.tvListReqFile);
            llButton = itemView.findViewById(R.id.llButtonSigners);
            tvListStatus = itemView.findViewById(R.id.tvListStatusSigners);
            btnView = itemView.findViewById(R.id.btnViewQR);
            btnSave = itemView.findViewById(R.id.btnSaveQR);
            civPhoto = itemView.findViewById(R.id.circleAvatar);



            if(type<3){
                llButton.setVisibility(View.GONE);
            }else if (type == 3){
                llButton.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.INVISIBLE);


            }
            else if (type == 4){
                llButton.setVisibility(View.VISIBLE);
                tvReqDocs.setVisibility(View.VISIBLE);

            }
            else{
                llButton.setVisibility(View.GONE);
            }
        }
    }
    private List<SignersModel> mSigners;
    private int type;
    private FragmentManager fragmentManager;
    private int id;
    /*
    * type is identifier for what layout is accessing this adapter.
    * 0=> Result Signature in Add_sign
    * 1=> Respond Signature in Draft
    * 2=> Result After Respond in Draft
    * 3=> Collab Result from Requested
    * 4=> Collab Result from Accepted
    * 5=> Collab Result from Rejected
    * */
    public SignersAdapter(List<SignersModel> mSigners, int type, FragmentManager fragmentManager, int id) {
        this.mSigners = mSigners;
        this.type = type;
        this.id = id;
        this.fragmentManager = fragmentManager;
    }


    private class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
