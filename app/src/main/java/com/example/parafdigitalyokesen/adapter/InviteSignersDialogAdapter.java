package com.example.parafdigitalyokesen.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.InviteSignersModel;

import java.util.ArrayList;

public class InviteSignersDialogAdapter extends BaseAdapter {
    private Context context;
    public static ArrayList<InviteSignersModel> inviteSignersModelArrayList;
    public InviteSignersDialogAdapter(Context context, ArrayList<InviteSignersModel>model){
        this.context = context;
        this.inviteSignersModelArrayList = model;
    }
    @Override
    public int getCount() {
        return inviteSignersModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return inviteSignersModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_dialog_invite, null, true);
            holder.etEmailSigners = view.findViewById(R.id.etDialogItemInvEmail);
            view.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        holder.etEmailSigners.setText(inviteSignersModelArrayList.get(position).getEtEmail());

        holder.etEmailSigners.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inviteSignersModelArrayList.get(position).setEtEmail(holder.etEmailSigners.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return view;
    }
    private class ViewHolder {

        protected EditText etEmailSigners;
    }
}
