package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.ContactModel;

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
    }

    @Override
    public int getItemCount() {
        return myContact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvEmail;
        public CircleImageView civContact;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameContact);
            tvEmail = itemView.findViewById(R.id.tvEmailMyContact);
            civContact = itemView.findViewById(R.id.civContact);
        }
    }
}
