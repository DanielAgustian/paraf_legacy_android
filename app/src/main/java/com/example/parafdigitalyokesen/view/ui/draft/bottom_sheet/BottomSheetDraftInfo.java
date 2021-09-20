package com.example.parafdigitalyokesen.view.ui.draft.bottom_sheet;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.PictureDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.util.UtilFile;
import com.example.parafdigitalyokesen.util.UtilWidget;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.collab.RecreateCollabActivity;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.example.parafdigitalyokesen.view.ui.draft.ResultAfterRespond;
import com.example.parafdigitalyokesen.viewModel.SignCollabState;
import com.example.parafdigitalyokesen.viewModel.refresh;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BottomSheetDraftInfo extends BottomSheetDialogFragment implements View.OnClickListener {
    SignModel sign;
    int where;
    String type = "";

    View v;
    Util util;
    Dialog regenerateDialog, dialogRename, deleteDialog;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    String token;
    ArrayList<String> typeShare =  new ArrayList<>();
    UtilWidget utilWidget = new UtilWidget(getContext());
    PictureDrawable pd;
    /*
    * where is identifier where are they from
    *  0=> Draft My Sign Fragment
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
        v = inflater.inflate(R.layout.bottom_sheet_nav_info_draft,
                container, false);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(getActivity());
        token = preferencesRepo.getToken();
        initComponent(v);


        return v;
    }

    private void initComponent(View v) {
        util = new Util();
        TextView tvTitle = v.findViewById(R.id.tvTitleInfo);
        tvTitle.setText(sign.getTitle());

        TextView tvDetails = v.findViewById(R.id.tvDetailsInfo);
        tvDetails.setText("Created at "+sign.getTime());



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

        pd = util.makeQRCOde(sign.getQr_code());

        ImageView iv = v.findViewById(R.id.ivBotNavInfo);
        iv.setImageDrawable(pd);

        if(where == 1){
            llRegenerate.setVisibility(View.GONE);
            llDelete.setVisibility(View.GONE);
            llRename.setVisibility(View.GONE);
        }
        if (!(where == 2)){
            llRemind.setVisibility(View.GONE);
        }
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
                Log.e("onClickError", "onClickWrong ID");
                break;
        }
    }

    public void detailsActivity(){
        if(where  == 0){
            Intent intent= new Intent(getActivity(), ResultSignature.class);
            intent.putExtra("where", "mysign");
            intent.putExtra("id", sign.getId());
            startActivity(intent);
        }else if (where == 1){

            String status = sign.getStatus();
            if(status == null || status.equals("")){
                Intent intent= new Intent(getActivity(), RespondSignature.class);
                intent.putExtra("id", sign.getId());
                startActivity(intent);
            }else if (status.toLowerCase().contains("accepted")){
                Intent intent= new Intent(getActivity(), ResultAfterRespond.class);
                intent.putExtra("id", sign.getId());
                startActivity(intent);
            }
            else if(status.toLowerCase().contains("rejected")){
                Intent intent= new Intent(getActivity(), ResultAfterRespond.class);
                intent.putExtra("id", sign.getId());
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(getActivity(), CollabResultActivity.class);
            intent.putExtra("type", where);
            intent.putExtra("id", sign.getId());
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
                doRemind();
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
        doShare();
    }


    private void rename(){
        dialogRename  = new Dialog(getActivity());
        dialogRename.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRename.setContentView(R.layout.dialog_rename);
        dialogRename.setCancelable(true);
        dialogRename.show();
        Button btnContinue = dialogRename.findViewById(R.id.btnContinueRename);
        Button btnCancel = dialogRename.findViewById(R.id.btnCancelRename);
        EditText etRename = dialogRename.findViewById(R.id.etRenameDoc);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = etRename.getText().toString();
                doRename(rename);

            }


        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialogRename);
            }
        });
    }

    private void save(){
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");
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
                type = typeShare.get(1);
            }
        });
        rbPng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = typeShare.get(0);
            }
        });
        rbPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = typeShare.get(2);
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
                doSave();
            }
        });

    }



    public void delete(){
        deleteDialog = new Dialog(getActivity());
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.dialog_confirmation);
        deleteDialog.setCancelable(true);
        deleteDialog.show();
        Button btnContinue = deleteDialog.findViewById(R.id.btnContinue);
        Button btnCancel = deleteDialog.findViewById(R.id.btnCancel);
        TextView textTitle =deleteDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = deleteDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to delete this signature?");
        btnContinue.setText("Delete");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDelete();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(deleteDialog);
            }
        });
    }
    public void duplicate(){
        Dialog duplicateDialog = new Dialog(getActivity());
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
    public void regenerate(){
        regenerateDialog = new Dialog(getActivity());
        regenerateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        regenerateDialog.setContentView(R.layout.dialog_confirmation);
        regenerateDialog.setCancelable(true);
        regenerateDialog.show();
        Button btnContinue = regenerateDialog.findViewById(R.id.btnContinue);
        Button btnCancel = regenerateDialog.findViewById(R.id.btnCancel);
        TextView textTitle = regenerateDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = regenerateDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to re-create this signature?");
        btnContinue.setText("Re-create");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(regenerateDialog);
                gotoRecreateForm();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(regenerateDialog);
            }
        });
    }

    public void  dismissDialog(Dialog dialog){
        dialog.dismiss();
    }

    //---------------------Do Dialog----------------
    private void doRename(String rename) {
        Observable<SimpleResponse> putRename = apiInterface.putRenameSign(token, sign.getId(), rename);
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRename, this::onFailedRename);
    }

    private void onFailedRename(Throwable throwable) {
        util.toastError(getContext(), "API RENAME ERROR", throwable);
    }

    private void onSuccessRename(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(dialogRename);
            dismiss();
            toRefreshList();
        }
    }

    private void doDelete() {
        Observable<SimpleResponse> putRename = apiInterface.putDeleteSign(token, sign.getId());
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessDelete, this::onFailedDelete);
    }

    private void onSuccessDelete(SimpleResponse simpleResponse) {
        if(simpleResponse != null){

            dismissDialog(deleteDialog);
            dismiss();
            toRefreshList();
        }
    }

    private void onFailedDelete(Throwable throwable) {
        util.toastError(getContext(), "API DELETE", throwable);
    }

    private void gotoRecreateForm() {
        if(where <= 1){
            Intent intent = new Intent(getContext(), RecreateSignActivity.class);

            intent.putExtra("id", sign.getId());
            //intent.putExtra("model", detailModel);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getContext(), RecreateCollabActivity.class);
            intent.putExtra("type", where);
            intent.putExtra("id", sign.getId());
            //intent.putExtra("model", detailModel);
            startActivity(intent);
        }


    }

    private void doSave() {
        UtilFile utilFile = new UtilFile(getContext());
        if(type.equals("") ){
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                utilFile.downloadFileAPI29((util.makeBitmap(pd)), type, typeShare, sign.getTitle());
            }else{
                utilFile.downloadFile(util.makeBitmap(pd), type, typeShare, sign.getTitle());
            }
        }
    }


    private void doShare(){
        util.shareLink(getContext(), "paraf.yokesen.com");
    }

    private void doRemind() {
        Observable<SimpleResponse> Remind= apiInterface.GetRemindReq(token, sign.getId());
        Remind.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRemind, this::onFailedRemind);
    }

    private void onSuccessRemind(SimpleResponse simpleResponse) {
        if(simpleResponse!=null){
            utilWidget.makeApprovalDialog("Remind Success", "You have succesfully remind the signers!");
        }
    }

    private void onFailedRemind(Throwable throwable) {
        util.toastError(getContext() , "API REMIND", throwable);
    }

    private void toRefreshList(){
        switch(where){
            case 0:
                SignCollabState.getSubject().onNext(refresh.MY_SIGN);
                break;
            case 1:
                SignCollabState.getSubject().onNext(refresh.MY_REQ);
                break;
            case 2:
                SignCollabState.getSubject().onNext(refresh.COLLAB_WAIT);
                break;
            case 3:
                SignCollabState.getSubject().onNext(refresh.COLLAB_ACC);
                break;
            case 4:
                SignCollabState.getSubject().onNext(refresh.COLLAB_REJ);
                break;
        }
    }




}
