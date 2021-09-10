package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.ContactModel;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.MyContact;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyContactListAdapter extends RecyclerView.Adapter<MyContactListAdapter.ViewHolder> {
    private List<ContactModel> myContact;
    public MyContactListAdapter(List<ContactModel> myContact) {
        this.myContact = myContact;
    }

    @NonNull
    @Override
    public MyContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recylcer_view_my_contact_item, parent, false);
        MyContactListAdapter.ViewHolder viewHolder = new MyContactListAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyContactListAdapter.ViewHolder holder, int position) {
        ContactModel contact = myContact.get(position);
        TextView tvName = holder.tvName;
        tvName.setText(contact.getName());
        TextView tvEmail = holder.tvEmail;
        tvEmail.setText(contact.getEmail());
        if(contact.getPhoto()!=null){
            if(!(contact.getPhoto().equals(""))){
                ImageView civ = holder.civContact;
                new MyContactListAdapter.DownLoadImageTask(civ).execute(contact.getPhoto());
            }
        }
    }

    @Override
    public int getItemCount() {
        return myContact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvEmail;
        public ImageView civContact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameContact);
            tvEmail = itemView.findViewById(R.id.tvEmailMyContact);
            civContact = itemView.findViewById(R.id.circleAvatarContact);
        }
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
