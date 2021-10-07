package com.yokesen.parafdigitalyokesen.view.add_sign;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yokesen.parafdigitalyokesen.R;

import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetSignNumberModel;
import com.yokesen.parafdigitalyokesen.model.SignNumberModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.GetSignDetailModel;
import com.yokesen.parafdigitalyokesen.model.GetTypeCategoryModel;
import com.yokesen.parafdigitalyokesen.model.SignatureDetailModel;
import com.yokesen.parafdigitalyokesen.model.TypeCategoryModel;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class SignYourselfActivity extends AppCompatActivity  {
    private Uri fileUri;
    private int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    File fileSending;
    private boolean nameValidator, emailValidator, documentNameValidator, kodeValidator = false;

    Button btnContinue, btnCancel ;
    private Dialog customDialog, waitingDialog;
    LinearLayout llWaitingData, llDoneData, llUploadData, llKode;
    //SignYourselfViewModel VM;
    Util util;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    List<TypeCategoryModel> category, types;
    TypeCategoryModel selectedCategory, selectedTypes;
    UtilWidget utiWidget = new UtilWidget(this);
    EditText etName, etEmail, etDocumentName, etDescription, etLink, etSignNumber, etKode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_yourself);
        initNetwork();
        getPermission();
        initSignNumber();
        //initComponent();
        initSpinner();
        initDialog();
        initDialogWaiting();

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
        long intervetion = 30 * 60 * 1000;
        long milisNow = Calendar.getInstance().getTimeInMillis();
        long milisSelisih = milisNow - milisStart;

        if(isActive == 1 && passcode!= null && !passcode.equals("")){

            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        int isBiometricActive = preferencesRepo.getBiometric();
        if(isBiometricActive == 1){
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }
        }

    }

    private void initNetwork() {
        util = new Util();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
    }


    private void initSignNumber() {
        String token = preferencesRepo.getToken();
        Observable<GetSignNumberModel> getSignNumber= apiInterface.getSignNumber(token);
        getSignNumber.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessSignNumber, this::onFailedSignNumber);

    }



    public void initSpinner(){
        String token = preferencesRepo.getToken();
        Log.d("HomeTOKEN", token);

        Observable<GetTypeCategoryModel> getCategory= apiInterface.getCategories(token);
        getCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessCategory, this::onFailedCategory);

        Observable<GetTypeCategoryModel> getTypes= apiInterface.getTypes(token);
        getTypes.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessTypes, this::onFailedTypes);

    }

    private void getPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    //=-----------------------------Method for Add SIgn------------------

    private boolean validation(){
        if(etName.getText().toString().trim().length()< 1){
            nameValidator = true;
        }else{
            nameValidator = false;
        }

        if(etEmail.getText().toString().trim().length()< 5){
            emailValidator = true;
        }else{
            emailValidator = false;
        }

        if(etDocumentName.getText().toString().trim().length()< 1){
            documentNameValidator = true;
        }else{
            documentNameValidator = false;
        }

        util.changeColorEditText(etName, nameValidator, this);
        util.changeColorEditText(etEmail, emailValidator, this);
        util.changeColorEditText(etDocumentName, documentNameValidator, this);
        if(!nameValidator && !emailValidator && !documentNameValidator ){
            return true;
        }else{
            return false;
        }
    }

    void addSignMethod(){
        if (validation()){
            waitingDialog.show();
            try {

                //Log.d("FILE_GETNAMe", fileSending.getName());
                if(selectedTypes.getId() == 3){

                    String kode = etKode.getText().toString();

                    if(kode.length() == 6){
                        //NEED TO SEND KODE
                        if(fileUri != null){
                            RequestBody requestFile =
                                    RequestBody.create(
                                            MediaType.parse(getContentResolver().getType(fileUri)),
                                            fileSending
                                    );
                            MultipartBody.Part body =
                                    MultipartBody.Part.createFormData("file", fileSending.getName(), requestFile);


                            String token = preferencesRepo.getToken();
                            Observable<GetSignDetailModel> postNewSign = apiInterface.addNewSignWithCode(
                                    token,
                                    util.requestBodyString(etName.getText().toString()),
                                    util.requestBodyString(etEmail.getText().toString()),
                                    util.requestBodyString(etDocumentName.getText().toString()),
                                    util.requestBodyString(String.valueOf(selectedCategory.getId())),
                                    util.requestBodyString(String.valueOf(selectedTypes.getId())),
                                    util.requestBodyString(etDescription.getText().toString()),
                                    util.requestBodyString(etLink.getText().toString()),
                                    body,
                                    util.requestBodyString(etSignNumber.getText().toString()),
                                    util.requestBodyString(etKode.getText().toString())
                            );
                            postNewSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);

                        }
                        else{
                            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                            MultipartBody.Part body = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);

                            String token = preferencesRepo.getToken();
                            Observable<GetSignDetailModel> postNewSign = apiInterface.addNewSignWithCode(
                                    token,
                                    util.requestBodyString(etName.getText().toString()),
                                    util.requestBodyString(etEmail.getText().toString()),
                                    util.requestBodyString(etDocumentName.getText().toString()),
                                    util.requestBodyString(String.valueOf(selectedCategory.getId())),
                                    util.requestBodyString(String.valueOf(selectedTypes.getId())),
                                    util.requestBodyString(etDescription.getText().toString()),
                                    util.requestBodyString(etLink.getText().toString()),
                                    body,
                                    util.requestBodyString(etSignNumber.getText().toString()),
                                    util.requestBodyString(etKode.getText().toString())
                            );
                            postNewSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);

                        }
                    }else{
                        utiWidget.makeApprovalDialog("Code Error", "Please write only 6 characters in kode textbox!");
                    }

                }else{
                    // No need to send Kode
                    if(fileUri != null){
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(fileUri)),
                                        fileSending
                                );
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", fileSending.getName(), requestFile);


                        String token = preferencesRepo.getToken();
                        Observable<GetSignDetailModel> postNewSign = apiInterface.addNewSign(
                                token,
                                util.requestBodyString(etName.getText().toString()),
                                util.requestBodyString(etEmail.getText().toString()),
                                util.requestBodyString(etDocumentName.getText().toString()),
                                util.requestBodyString(String.valueOf(selectedCategory.getId())),
                                util.requestBodyString(String.valueOf(selectedTypes.getId())),
                                util.requestBodyString(etDescription.getText().toString()),
                                util.requestBodyString(etLink.getText().toString()),
                                body,
                                util.requestBodyString(etSignNumber.getText().toString())

                        );
                        postNewSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);

                    }
                    else{
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

                        MultipartBody.Part body = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);

                        String token = preferencesRepo.getToken();
                        Observable<GetSignDetailModel> postNewSign = apiInterface.addNewSign(
                                token,
                                util.requestBodyString(etName.getText().toString()),
                                util.requestBodyString(etEmail.getText().toString()),
                                util.requestBodyString(etDocumentName.getText().toString()),
                                util.requestBodyString(String.valueOf(selectedCategory.getId())),
                                util.requestBodyString(String.valueOf(selectedTypes.getId())),
                                util.requestBodyString(etDescription.getText().toString()),
                                util.requestBodyString(etLink.getText().toString()),
                                body,
                                util.requestBodyString(etSignNumber.getText().toString())
                        );
                        postNewSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);

                    }
                }


            }catch (Exception e){
                e.printStackTrace();
                waitingDialog.dismiss();
                util.toastMisc(this, "API NEW SIGN ERROR"+ e.getMessage());
            }

        }
    }

    private void onFailedAddSign(Throwable throwable) {
        waitingDialog.dismiss();
        Toast.makeText(this, "ERROR IN POST API NEW SIGN. Try again = "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();

    }

    private void onSuccessAddSign(GetSignDetailModel model) {
        waitingDialog.dismiss();
        gotoResultPage(model.getHome());
    }


    //---------------------Method for Get Sign Number--------------------------------
    private void onFailedSignNumber(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Sign Number. Try again",
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessSignNumber(GetSignNumberModel getSignNumberModel) {
        if(getSignNumberModel!=null){
            SignNumberModel model = getSignNumberModel.getHome();
            String signnumber = util.signNumber(model.getUserId(), model.getCounter());
            initComponent(signnumber);
        }

    }

    //---------------------Method For Get Types Spinner-------------------------------

    private void onFailedTypes(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Types. Try again",
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessTypes(GetTypeCategoryModel getTypeCategoryModel) {
        if(getTypeCategoryModel != null){
            types = getTypeCategoryModel.getData();
            getDataSpinnerTypes();
        }
    }
    private void getDataSpinnerTypes(){
        llKode = findViewById(R.id.llKode);
        Spinner spinnerDocType = findViewById(R.id.spinnerDocTypeyNewSign);
        ArrayAdapter<TypeCategoryModel> adapterDocType = new ArrayAdapter<TypeCategoryModel>(this, R.layout.simple_spinner_item, types);
        adapterDocType.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinnerDocType.setAdapter(adapterDocType);
        spinnerDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getSelectedItem();
                Log.d("CategorySelected", selectedTypes.getName());
                if(selectedTypes.getId() == 3){
                    llKode.setVisibility(View.VISIBLE);
                } else{
                    llKode.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getItemAtPosition(0);

            }
        });
    }


    //-----------------------Method for Category Spinner -----------------------

    private void onFailedCategory(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Category. Try again",
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessCategory(GetTypeCategoryModel getTypeCategoryModel) {
        if(getTypeCategoryModel != null){
            category = getTypeCategoryModel.getData();
            getDataSpinnerCategory();
        }
    }

    private void getDataSpinnerCategory(){
        Spinner spinnerCategory = findViewById(R.id.spinnerCategoryNewSign);
        ArrayAdapter<TypeCategoryModel> adapterCategory = new ArrayAdapter<TypeCategoryModel>(
                this,
                R.layout.simple_spinner_item,
                category);
        adapterCategory.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinnerCategory.setAdapter(adapterCategory);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = (TypeCategoryModel) spinnerCategory.getSelectedItem();
                Log.d("CategorySelected", selectedCategory.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = (TypeCategoryModel) spinnerCategory.getItemAtPosition(0);
            }
        });
    }








    //------------------------------ Initialize Component-----------------
    public void initComponent(String signNumber){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignYourself);
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
        Button btnCreate = findViewById(R.id.btnSignYourself);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //--> Goto Init Dialog
                customDialog.show();
            }
        });
        llUploadData = findViewById(R.id.uploadData);
        llUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePicker();
            }
        });

        llDoneData = findViewById(R.id.doneUploadData);
        llDoneData.setVisibility(View.GONE);
        llWaitingData = findViewById(R.id.waitingUploadData);
        llWaitingData.setVisibility(View.GONE);

        ImageView ivEmptyFile = findViewById(R.id.ivEmptyFile);
        ivEmptyFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyFile();
            }
        });
        etSignNumber = findViewById(R.id.etSignNumber);
        etSignNumber.setText(signNumber);
        etName = findViewById(R.id.etNameSignYour);
        etName.setText(preferencesRepo.getNameProfile());
        etEmail = findViewById(R.id.etEmailNewSign);
        etEmail.setEnabled(false);
        etEmail.setText(preferencesRepo.getEmailProfile());
        etDocumentName = findViewById(R.id.etDocNameNewSign);
        etDescription = findViewById(R.id.etDescNewSign);
        etLink = findViewById(R.id.etLinkNewSign);
        etKode = findViewById(R.id.etKode);
    }


    //---------------------------FilePicker Method--------------------
    private void emptyFile() {

        llDoneData.setVisibility(View.GONE);
        llUploadData.setVisibility(View.VISIBLE);
    }

    //Method to get Trigger File Picker
    private void filePicker() {


        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        chooseFile.setType("*/*");
        chooseFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");

        startActivityForResult(chooseFile, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    //For get data from filepicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){
            if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){
                fileUri = data.getData();
                convertURiToFile(fileUri);
//                File file = new File(fileUri.getPath());
//                Log.d("GET_URI", file.getAbsolutePath());

                llUploadData.setVisibility(View.GONE);
                llWaitingData.setVisibility(View.VISIBLE);
                TextView tvWaitingData = findViewById(R.id.tvWaitingUpload);
                tvWaitingData.setText(fileUri.getPath());
                setProgressValue(fileUri.getPath());
            }
        }
    }

    void convertURiToFile(Uri uri){
        File fileCopy = new File(getCacheDir(), getNameFile(uri));
        int maxBufferSize = 1 * 1024 * 1024;
        try {
            InputStream  inputStream = getContentResolver().openInputStream(uri);
            Log.e("GETFILE","Size " + inputStream);
            int  bytesAvailable = inputStream.available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            final byte[] buffers = new byte[bufferSize];
            FileOutputStream outputStream = new FileOutputStream(fileCopy);
            int read = 0;
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size","Size " + fileCopy.length());
            inputStream.close();
            outputStream.close();

            Log.e("GETFILE","path " + fileCopy.getPath());
            fileSending = fileCopy;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getNameFile(Uri uri){
        Cursor cursor = this.getContentResolver()
                    .query(uri, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.i("GETFILE", "Display Name: " + displayName);
                    return displayName;
        } else{
            return "Unkwnown";
        }
    }
    int progress = 0;
    //For progress bar
    private void setProgressValue(String filePath) {
        ProgressBar simpleProgressBar = findViewById(R.id.simpleProgressBar);
        // set the progress


        simpleProgressBar.setProgress(progress);

        // thread is used to change the progress value
        CountDownTimer mCountDownTimer=new CountDownTimer(2000,200) {

            @Override
            public void onTick(long millisUntilFinished) {
                progress = progress+10;
                simpleProgressBar.setProgress(progress);

            }

            @Override
            public void onFinish() {
                //Do what you want

                simpleProgressBar.setProgress(100);
                llWaitingData.setVisibility(View.GONE);
                llDoneData.setVisibility(View.VISIBLE);
                TextView tvUploadData = findViewById(R.id.tvDoneUpload);
                tvUploadData.setText(filePath);
            }
        };
        mCountDownTimer.start();


    }


    public void initDialogWaiting(){
        waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.setCancelable(false);
    }

    public void initDialog(){
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_confirmation);
        customDialog.setCancelable(true);
        btnContinue = customDialog.findViewById(R.id.btnContinue);
        btnCancel = customDialog.findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                addSignMethod();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customDialog.dismiss();
            }
        });
    }

    public void back(){
        this.finish();
    }

    void gotoResultPage(SignatureDetailModel sign){
        this.finish();
        Intent intent = new Intent(this, ResultSignature.class);
        intent.putExtra("where", "mysign");
        intent.putExtra("id", sign.getId());
        startActivity(intent);
    }

}