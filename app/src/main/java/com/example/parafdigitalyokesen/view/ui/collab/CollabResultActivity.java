package com.example.parafdigitalyokesen.view.ui.collab;

import android.app.Dialog;
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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.MyReqDetailModel;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SignatureDetailModel;
import com.example.parafdigitalyokesen.model.SignersModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CollabResultActivity extends AppCompatActivity implements View.OnClickListener{
    List<SignersModel> signers;
    LinearLayout llRemind, llShare, llSave, llDuplicate, llInviteSigners, llRegenerate, llRename, llDelete;
    TextView tvPerson;
    private Dialog remindDialog;
    private Dialog deleteDialog, regenerateDialog, dialogRename;
    int where;
    String type;
    ArrayList<String> typeShare =  new ArrayList<>();
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    MyReqDetailModel detailModel;
    PictureDrawable pd;
    Util util = new Util();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_result);
        where = getIntent().getIntExtra("type", 0);
        initData();
        initToolbar();
    }

    /*
     * where is identifier where are they from
     *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
     * */
    private void initData() {
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        int id = getIntent().getIntExtra("id", -1) ;
        if(id != -1){

            Observable<GetMyReqDetailModel> getDetails = apiInterface.getCollabDetail(token, id);
            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }
    }

    private void onSuccess(GetMyReqDetailModel getMyReqDetailModel) {
        if(getMyReqDetailModel != null){
            signers = getMyReqDetailModel.getData().getSigners();
            detailModel = getMyReqDetailModel.getData();
            initRecyclerView();
            initComponent();
            setData(getMyReqDetailModel.getData());
        }
    }



    private void onFailed(Throwable throwable) {
        util.toastError(this, "API Collab Detail", throwable);
        throwable.printStackTrace();
    }

    private void setData(MyReqDetailModel data) {
        pd = util.makeQRCOde(data.getQr_code());
        ImageView iv = findViewById(R.id.ivQRScan);
        iv.setImageDrawable(util.makeQRCOde(data.getQr_code()));

        TextView tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvCreatedBy.setText(data.getCreatedBy());

        TextView tvEmail = findViewById(R.id.tvEmailRS);
        tvEmail.setText(data.getEmailReq());

        TextView tvInitiated = findViewById(R.id.tvInitiated);
        tvInitiated.setText(data.getInitiatedOn());

        TextView tvTitle = findViewById(R.id.tvDocName);
        tvTitle.setText(data.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRes);
        tvDesc.setText(data.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRes);
        tvCategory.setText(data.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRes);
        tvTypes.setText(data.getType());

        TextView tvlink = findViewById(R.id.tvLinkRes);
        tvlink.setText(data.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRes);
        tvStatus.setText(data.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRes);
        tvSize.setText(data.getSize());

        TextView tvPersonRespond = findViewById(R.id.tvPersonRespond);
        tvPersonRespond.setText(data.getTotalSigners()+ "person left to sign");
    }
    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerCollabResult);
        rv.setNestedScrollingEnabled(false);

        Log.d("SignersModel", signers.size()+ "");
        SignersAdapter adapter = null;
        if(where == 2){
            adapter = new SignersAdapter(signers, 3, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
        } else if(where == 3){
            adapter = new SignersAdapter(signers, 4, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
        } else {
            adapter = new SignersAdapter(signers, 5, getSupportFragmentManager(), detailModel.getId());
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
        dialogRename  = new Dialog(this);
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
        Dialog dialog  = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_save);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancelSave);
        TextView textTitle =dialog.findViewById(R.id.tvTitleDialogSave);
        textTitle.setText(detailModel.getTitle() +"will be saved to Download");
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
        deleteDialog = new Dialog(this);
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
    public void regenerate(){
        regenerateDialog = new Dialog(this);
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



    //--------------------------Dialog Logic ------------------------------//
     private void doRename(String rename) {
        Observable<SimpleResponse> putRename = apiInterface.putRenameSign(token, detailModel.getId(), rename);
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRename, this::onFailedRename);
    }

    private void onFailedRename(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RENAME. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessRename(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(dialogRename);
            initData();
        }
    }

    private void doDelete() {
        Observable<SimpleResponse> putRename = apiInterface.putDeleteSign(token, detailModel.getId());
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessDelete, this::onFailedDelete);
    }

    private void onSuccessDelete(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(deleteDialog);
            initData();
        }
    }

    private void onFailedDelete(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RENAME. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void gotoRecreateForm() {
        Intent intent = new Intent(this, RecreateCollabActivity.class);
        intent.putExtra("type", where);
        intent.putExtra("id", detailModel.getId());

        finish();
        startActivity(intent);
    }

    private void doSave() {

    }


    private void doShare(String type){
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
                    .toString() + "/" + detailModel.getTitle()+ ".png");
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
                MediaStore.Images.Media.insertImage(getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  if(type.equals(typeShare.get(1))){
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + detailModel.getTitle()+ ".jpg");
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());
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
            MyReqDetailModel detailModel = this.detailModel;
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, detailModel.getTitle()+detailModel.getCreatedBy()+".png");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri itemUri = getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContentResolver().update(itemUri, contentValues, null, null);
                    Toast.makeText(this, "Successfully Download Image: "+ itemUri.getPath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else  if(type.equals(typeShare.get(1))){
            ContentValues contentValues = new ContentValues();
            MyReqDetailModel detailModel = this.detailModel;
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, detailModel.getTitle()+detailModel.getCreatedBy()+".jpg");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Downloads.IS_PENDING, true);
            Uri uri = null;
            uri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            Uri itemUri = getContentResolver().insert(uri, contentValues);

            if (itemUri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(itemUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, false);
                    getContentResolver().update(itemUri, contentValues, null, null);
                    Toast.makeText(this, "Successfully Download Image: "+ itemUri.getPath(),
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
        String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/"+detailModel.getTitle()+ detailModel.getCreatedBy() + ".pdf";
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
        Toast.makeText(this, "Successfully Download PDF: "+ file.getPath(),
                Toast.LENGTH_LONG).show();
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
