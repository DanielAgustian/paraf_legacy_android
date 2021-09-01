package com.example.parafdigitalyokesen.view.ui.collab;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SignersModel;

import java.util.ArrayList;
import java.util.List;

public class CollabResultActivity extends AppCompatActivity implements View.OnClickListener{
    List<SignersModel> signers;
    LinearLayout llRemind, llShare, llSave, llDuplicate, llInviteSigners, llRegenerate, llRename, llDelete;
    TextView tvPerson;
    private Dialog remindDialog;
    int where;
    String type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_result);

        where = getIntent().getIntExtra("type", 0);

        initRecyclerView();
        initToolbar();
        initComponent();
    }

    /*
     * where is identifier where are they from
     *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
     * */

    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerCollabResult);
        rv.setNestedScrollingEnabled(false);
        signers = SignersModel.generateModel(3);

        SignersAdapter adapter = null;
        if(where == 2){
            adapter = new SignersAdapter(signers, 3, getSupportFragmentManager());
            rv.setAdapter(adapter);
        } else if(where == 3){
            adapter = new SignersAdapter(signers, 4, getSupportFragmentManager());
            rv.setAdapter(adapter);
        } else {
            adapter = new SignersAdapter(signers, 5, getSupportFragmentManager());
            rv.setAdapter(adapter);
        }

        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCollabResult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Respond Signature", "Back Clicked");
                back();
            }
        });

    }
    public void initComponent(){
        llRemind = findViewById(R.id.llRemindCollab);
        llRemind.setOnClickListener(this);


        llSave = findViewById(R.id.llSaveCollabRes);
        llSave.setOnClickListener(this);

        llShare = findViewById(R.id.llShareCollabRes);
        llShare.setOnClickListener(this);

        llDuplicate = findViewById(R.id.llDuplicateCollabRes);
        llDuplicate.setOnClickListener(this);

        llInviteSigners = findViewById(R.id.llInviteCollabRes);
        llInviteSigners.setOnClickListener(this);

        llRegenerate = findViewById(R.id.llRegenCollabRes);
        llRegenerate.setOnClickListener(this);

        llRename = findViewById(R.id.llRenameCollabRes);
        llRename.setOnClickListener(this);

        llDelete = findViewById(R.id.llDeleteCollabRes);
        llDelete.setOnClickListener(this);

        LinearLayout llRejected = findViewById(R.id.llRejected);
        LinearLayout llApproved = findViewById(R.id.llApproved);
        tvPerson = findViewById(R.id.tvPersonRespond);
        if(where == 2){
            llSave.setVisibility(View.GONE);
            llRejected.setVisibility(View.GONE);
        } else if (where == 3){
            llRemind.setVisibility(View.GONE);
            llRejected.setVisibility(View.GONE);
        } else if (where == 4){
            //Rejected
            llApproved.setVisibility(View.GONE);
            tvPerson.setText("Sign Rejected");
            tvPerson.setTextColor(ContextCompat.getColor(this, R.color.colorError));
        }

    }

    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llRemindCollab:
                remindDialog();
                break;
            case R.id.llSaveCollabRes:
                save();
                break;
            case R.id.llShareCollabRes:
                sharing();
                break;
            case R.id.llDuplicateCollabRes:
                duplicate();
                break;
            case R.id.llInviteCollabRes:
                inviteSigners();
                break;
            case R.id.llRegenCollabRes:
                regenerate();
                break;
            case R.id.llRenameCollabRes:
                rename();
                break;
            case R.id.llDeleteCollabRes:
                delete();
                break;
            default:
                break;
        }
    }
    //-------------Process For Bottom Menu---------------------------------//
    private void sharing(){

        Dialog dialog  = new Dialog(this);

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

    public void duplicate(){
        Dialog duplicateDialog = new Dialog(this);
        duplicateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        duplicateDialog.setContentView(R.layout.dialog_confirmation);
        duplicateDialog.setCancelable(true);
        duplicateDialog.show();
        Button btnContinue = duplicateDialog.findViewById(R.id.btnContinue);
        Button btnCancel = duplicateDialog.findViewById(R.id.btnCancel);
        TextView textTitle = duplicateDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = duplicateDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to duplicate this signature?");
        btnContinue.setText("Duplicate");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(duplicateDialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(duplicateDialog);
            }
        });
    }

    private void inviteSigners(){
        Dialog dialog  = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite);
        dialog.setCancelable(true);
        dialog.show();
        ListView lvSigners = dialog.findViewById(R.id.lvDialogInvite);
        lvSigners.setNestedScrollingEnabled(false);
        ArrayList<InviteSignersModel> list = populateList(2);
        InviteSignersDialogAdapter invAdapter = new InviteSignersDialogAdapter(
                this, list
        );
        lvSigners.setAdapter(invAdapter);
        Button btnContinue = dialog.findViewById(R.id.btnContinueInvite);
        Button btnCancel = dialog.findViewById(R.id.btnCancelInvite);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewListItem = lvSigners.getChildAt(0);
                EditText editText = viewListItem.findViewById(R.id.etDialogItemInvEmail);
                String string = editText.getText().toString();
                Log.d("Dialog Invite", string);
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
    private void rename(){
        Dialog dialog  = new Dialog(this);
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
        Dialog dialog  = new Dialog(this);
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
        Dialog dialog = new Dialog(this);
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
        Dialog dialog = new Dialog(this);
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

    //To remind signers
    public void remindDialog(){
        remindDialog = new Dialog(this);
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

    //Dialog to remind signers if has been reminded before
    public void remindDialog2(){
        remindDialog = new Dialog(this);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_confirmation);
        remindDialog.setCancelable(true);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to remind your signers?");
        textData.setText("You’ve already remind your signers before, want to remind them again?");
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


    //---------------------------Misc Logic---------------------
    public ArrayList<InviteSignersModel> populateList(int position){
        ArrayList<InviteSignersModel> list = new ArrayList<>();
        for(int i = 0; i < position; i++){
            InviteSignersModel invModel = new InviteSignersModel();
            invModel.setEtName("");
            invModel.setEtEmail("");
            list.add(invModel);
        }
        return list;
    }
}
