package com.yokesen.parafdigitalyokesen.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;

public class UtilWidget {
    Context context;
    public UtilWidget(Context context) {
        this.context = context;
    }

    public void makeApprovalDialog(String title, String data){
        Dialog remindDialog = new Dialog(context);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_approval);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText(title);
        textData.setText(data);
        Button btnContinue = remindDialog.findViewById(R.id.btnCloseDialog);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });

    }
    public Dialog makeLoadingDialog(){
        Dialog remindDialog = new Dialog(context);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_waiting);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        textTitle.setVisibility(View.GONE);
        return remindDialog;
    }
}
