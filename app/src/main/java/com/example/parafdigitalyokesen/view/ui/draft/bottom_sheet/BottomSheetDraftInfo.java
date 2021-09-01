package com.example.parafdigitalyokesen.view.ui.draft.bottom_sheet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.view.add_sign.ReqSignatureActivity;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.add_sign.SignYourselfActivity;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDraftInfo extends BottomSheetDialogFragment implements View.OnClickListener {
    SignModel sign;
    int where;
    String type = "";
    /*
    * where is identifier where are they from
    *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
    * */
    public BottomSheetDraftInfo(SignModel sign, int where) {
        this.sign = sign;
        this.where =where;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_nav_info_draft,
                container, false);
        LinearLayout llDetails = v.findViewById(R.id.llDetailsDraft);
        LinearLayout llSave = v.findViewById(R.id.llSaveDraft);
        LinearLayout llShare = v.findViewById(R.id.llShareDraft);
        LinearLayout llRegenerate = v.findViewById(R.id.llRegenDraft);
        LinearLayout llRename = v.findViewById(R.id.llRenameDraft);
        LinearLayout llDelete = v.findViewById(R.id.llDeleteDraft);
        LinearLayout llRemind = v.findViewById(R.id.llRemindDraft);
        llDetails.setOnClickListener(this);
        llSave.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRegenerate.setOnClickListener(this);
        llRename.setOnClickListener(this);
        llDelete.setOnClickListener(this);
        llRemind.setOnClickListener(this);

        if (!(where == 2)){
            llRemind.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.llDetailsDraft:
                detailsActivity();
                break;
            case R.id.llSaveDraft:
                save();
                break;
            case R.id.llShareDraft:
                sharing();
                break;
            case R.id.llRegenDraft:
                regenerate();
                break;
            case R.id.llRenameDraft:
                rename();
                break;
            case R.id.llDeleteDraft:
                delete();
                break;
            case R.id.llRemindDraft:
                remindDialog();
                break;
            default:
                Log.d("onClickError", "onClickWrong ID");
                break;
        }
    }

    public void detailsActivity(){
        if(where  == 0){
            Intent intent= new Intent(getActivity(), ResultSignature.class);
            intent.putExtra("where", "mysign");
            startActivity(intent);
        }else if (where == 1){
            Intent intent= new Intent(getActivity(), RespondSignature.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), CollabResultActivity.class);
            intent.putExtra("type", where);
            startActivity(intent);
        }

    }

    public void remindDialog(){
        Dialog remindDialog = new Dialog(getActivity());
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_confirmation);
        remindDialog.setCancelable(true);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to remind your signers?");
        textData.setVisibility(View.GONE);
        Button btnContinue = remindDialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Remind");
        Button btnCancel = remindDialog.findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
    }
    private void sharing(){

        Dialog dialog  = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_doctype);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueType);
        Button btnCancel = dialog.findViewById(R.id.btnCancelType);
        RadioButton rbPng = dialog.findViewById(R.id.radio_png);
        RadioButton rbJpg = dialog.findViewById(R.id.radio_jpeg);
        RadioButton rbPdf = dialog.findViewById(R.id.radio_pdf);
        rbJpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "jpg";
            }
        });
        rbPng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "png";
            }
        });
        rbPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "pdf";
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(dialog);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(dialog);
            }
        });
    }


    private void rename(){
        Dialog dialog  = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueRename);
        Button btnCancel = dialog.findViewById(R.id.btnCancelRename);
        EditText etRename = dialog.findViewById(R.id.etRenameDoc);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = etRename.getText().toString();
                dismissDialog(dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
    }

    private void save(){
        Dialog dialog  = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_save);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancelSave);
        TextView textTitle =dialog.findViewById(R.id.tvTitleDialogSave);
        textTitle.setText("File Name will be saved to Download");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
    }

    public void delete(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView textTitle =dialog.findViewById(R.id.tvTitleDialog);
        TextView textData = dialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to delete this signature?");
        btnContinue.setText("Delete");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(dialog);
            }
        });
    }
    public void regenerate(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView textTitle = dialog.findViewById(R.id.tvTitleDialog);
        TextView textData = dialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to regenerate this signature?");
        btnContinue.setText("Regenerate");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
    }

    public void  dismissDialog(Dialog dialog){
        dialog.dismiss();
    }
}
