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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SignatureDetailModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.view.add_sign.ReqSignatureActivity;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.add_sign.SignYourselfActivity;
import com.example.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.example.parafdigitalyokesen.view.ui.draft.ResultAfterRespond;
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

        ImageView iv = v.findViewById(R.id.ivBotNavInfo);
        iv.setImageDrawable(util.makeQRCOde(sign.getQr_code()));

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
                doShare(type);
            }
        });
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
        Dialog dialog  = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_save);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancelSave);
        TextView textTitle =dialog.findViewById(R.id.tvTitleDialogSave);
        textTitle.setText(sign.getTitle() + "will be saved to Download");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
                doSave();
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
        }
    }

    private void doDelete() {
        Observable<SimpleResponse> putRename = apiInterface.putDeleteSign(token, sign.getId());
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessDelete, this::onFailedDelete);
    }

    private void onSuccessDelete(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(deleteDialog);

        }
    }

    private void onFailedDelete(Throwable throwable) {
        util.toastError(getContext(), "API DELETE", throwable);
    }

    private void gotoRecreateForm() {
        Intent intent = new Intent(getContext(), RecreateSignActivity.class);

        intent.putExtra("id", sign.getId());
        //intent.putExtra("model", detailModel);
        startActivity(intent);
    }

    private void doSave() {

    }


    private void doShare(String type){
        PictureDrawable pd = util.makeQRCOde(sign.getQr_code());
        if(type.equals("") ){
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                downloadFileAPI29((util.makeBitmap(pd)), type);
            }else{
                downloadFile(util.makeBitmap(pd), type);
            }

        }
    }

    private void downloadFile(Bitmap bitmap, String type) {
        if(type.equals(typeShare.get(0))){
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + sign.getTitle()+ ".png");
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  if(type.equals(typeShare.get(1))){
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + sign.getTitle()+ ".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {

            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch ( FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(getContext().getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals(typeShare.get(2))){
            downloadPDF(bitmap);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void downloadFileAPI29(Bitmap bitmap, String type) {
        Log.d("FILETYPE", type);

        if(type.equals(typeShare.get(0))){
            ContentValues contentValues = new ContentValues();
            SignModel sign = this.sign;
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, sign.getTitle()+sign.getDue_date()+".png");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri itemUri = getContext().getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContext().getContentResolver().update(itemUri, contentValues, null, null);
                    Toast.makeText(getContext(), "Successfully Download Image: "+ itemUri.getPath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else  if(type.equals(typeShare.get(1))){
            ContentValues contentValues = new ContentValues();

            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, sign.getTitle()+sign.getTime()+".jpg");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentResolver cr = getContext().getContentResolver();
            Uri itemUri = cr.insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = cr.openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    cr.update(itemUri, contentValues, null, null);
                    Toast.makeText(getContext(), "Successfully Download Image: "+ itemUri.getPath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if (type.equals(typeShare.get(2))){
            downloadPDF(bitmap);
        }
    }
    void downloadPDF(Bitmap bitmap){
        String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/"+sign.getTitle()+ sign.getTime() + ".pdf";
        File file = new File(stringFilePath);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));


        page.getCanvas().drawBitmap(bitmap,0,0 , paint);
        pdfDocument.finishPage(page);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        }
        catch (Exception e){
            e.printStackTrace();

        }
        pdfDocument.close();
        Toast.makeText(getContext(), "Successfully Download PDF: "+ file.getPath(),
                Toast.LENGTH_LONG).show();
    }


}
