package com.yokesen.parafdigitalyokesen.view.add_sign;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.Variables;
import com.yokesen.parafdigitalyokesen.util.DynamicLinkUtil;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.yokesen.parafdigitalyokesen.adapter.SignersAdapter;
import com.yokesen.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.InviteSignersModel;
import com.yokesen.parafdigitalyokesen.model.MyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.SignersModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResultReqSignActivity extends AppCompatActivity implements View.OnClickListener {
    List<SignersModel> signers;
    private Dialog deleteDialog, regenerateDialog, dialogRename;
    LinearLayout llDelete, llRegen, llSave, llDuplicate, llInvite, llShare, llRename;
    LinearLayout llPersonRespond;

    String type = "";
    ArrayList<String> typeShare =  new ArrayList<>();
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    MyReqDetailModel detailModel;
    Util util = new Util();
    PictureDrawable pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_signature);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        initData();
        initToolbar();
        initComponent();
    }
    long milisStart = 0;
    @Override
    protected void onPause() {
        super.onPause();
        milisStart = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String passcode = preferencesRepo.getPasscode();
        int isActive = preferencesRepo.getAllowPasscode();

        if(isActive == 1 && passcode!= null && passcode.equals("")){
            long intervetion = 30 * 60 * 1000;
            long milisNow = Calendar.getInstance().getTimeInMillis();
            long milisSelisih = milisNow - milisStart;
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        //biometricPrompt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llDelete:
                delete();
                break;
            case R.id.llRegen:
                regenerate();
                break;
            case R.id.llSave:
                save();
                break;
            case R.id.llRename:
                rename();
                break;
            case R.id.llShare:
                sharing();
                break;
            case R.id.llInvite:
                inviteSigners();
                break;
            case R.id.llDuplicate:
                duplicate();
                break;
        }
    }

    private void initData() {
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");
        int id = getIntent().getIntExtra("id", -1) ;
        if(id != -1){

            Observable<GetMyReqDetailModel> getDetails = apiInterface.getMyRequestDetail(token, id);
            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }
    }

    private void onSuccess(GetMyReqDetailModel model) {
        if(model != null){
            detailModel = model.getData();
            initRecyclerView();
            setData();
        }
    }

    private void setData() {
        pd = util.makeQRCOde(detailModel.getQr_code());
        ImageView iv = findViewById(R.id.ivQRScan);
        iv.setImageDrawable(pd);

        TextView tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvCreatedBy.setText(detailModel.getRequestBy());

        TextView tvEmail = findViewById(R.id.tvEmailRS);
        tvEmail.setText(detailModel.getEmailReq());

        TextView tvInitiated = findViewById(R.id.tvInitiated);
        tvInitiated.setText(detailModel.getInitiatedOn());

        TextView tvTitle = findViewById(R.id.tvDocName);
        tvTitle.setText(detailModel.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRes);
        tvDesc.setText(detailModel.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRes);
        tvCategory.setText(detailModel.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRes);
        tvTypes.setText(detailModel.getType());

        TextView tvlink = findViewById(R.id.tvLinkRes);
        tvlink.setText(detailModel.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRes);
        tvStatus.setText(detailModel.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRes);
        tvSize.setText(detailModel.getSize());

        TextView tvPerson = findViewById(R.id.tvPersonRespond);
        tvPerson.setText(detailModel.getTotalSigners()+ " person left to sign");

        TextView tvCreatedByPlace = findViewById(R.id.tvCreatedByPlace);
        tvCreatedByPlace.setText("Request By");
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API GET DETAILS. "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    //-------------------------- OpenDialog -----------------------
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
                this, list, false
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

    private void sharing(){
        DynamicLinkUtil dlUtil = new DynamicLinkUtil(this);
        Variables var = new Variables();
        util.shareLink(this, dlUtil.dynamicLinkParaf(var.typeSign[2], detailModel.getId()));
//        Dialog dialog  = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_doctype);
//        dialog.setCancelable(true);
//        dialog.show();
//        Button btnContinue = dialog.findViewById(R.id.btnContinueType);
//        Button btnCancel = dialog.findViewById(R.id.btnCancelType);
//        RadioButton rbPng = dialog.findViewById(R.id.radio_png);
//        RadioButton rbJpg = dialog.findViewById(R.id.radio_jpeg);
//        RadioButton rbPdf = dialog.findViewById(R.id.radio_pdf);
//        rbJpg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                type = typeShare.get(1);
//            }
//        });
//        rbPng.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                type = typeShare.get(0);
//            }
//        });
//        rbPdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                type = typeShare.get(2);
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismissDialog(dialog);
//
//            }
//        });
//        btnContinue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismissDialog(dialog);
//                doShare(type);
//            }
//        });
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    doSave(util.makeBitmap(pd));
                }else{
                    doSaveBelow29(util.makeBitmap(pd));
                }

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


    //------------------Init-----------------------------
    public void initComponent(){
        llDelete = findViewById(R.id.llDelete);
        llRegen = findViewById(R.id.llRegen);
        llSave  =findViewById(R.id.llSave);
        llDuplicate = findViewById(R.id.llDuplicate);
        llShare = findViewById(R.id.llShare);
        llRename = findViewById(R.id.llRename);
        llInvite = findViewById(R.id.llInvite);


        llDelete.setOnClickListener(this); //Done
        llRegen.setOnClickListener(this); //Done
        llSave.setOnClickListener(this); //Done
        llDuplicate.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRename.setOnClickListener(this);//Done
        llInvite.setOnClickListener(this);
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarResultSig);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mainActivity", "Back Clicked");
                back();
            }
        });

    }

    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerViewSigners);
        signers = detailModel.getSigners();
        SignersAdapter adapter =  new SignersAdapter(signers, 0, getSupportFragmentManager(), detailModel.getId());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void back(){
        this.finish();
    }


    // -----------------------LOGIC FOR MODAL-------------------------//
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
        Intent intent = new Intent(this, RecreateSignActivity.class);

        intent.putExtra("id", detailModel.getId());
        intent.putExtra("model", detailModel);
        startActivity(intent);
    }

    void doSaveBelow29(Bitmap bitmap){
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

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void doSave(Bitmap bitmap) {
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
}