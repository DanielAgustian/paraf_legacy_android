package com.example.parafdigitalyokesen.view.ui.collab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.GetTypeCategoryModel;
import com.example.parafdigitalyokesen.model.MyReqDetailModel;
import com.example.parafdigitalyokesen.model.TypeCategoryModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class RecreateCollabActivity extends AppCompatActivity {

    private Uri fileUri;
    private int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    int recreateType = 0, recreateCategory;
    File fileSending;
    private boolean nameValidator, emailValidator, documentNameValidator;

    Button btnContinue, btnCancel, btnDueDate, btnTime;
    private Dialog customDialog, waitingDialog;
    LinearLayout llWaitingData, llDoneData, llUploadData;
    //SignYourselfViewModel VM;
    Util util;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    List<TypeCategoryModel> category, types;
    String token;
    TypeCategoryModel selectedCategory, selectedTypes;

    ListView lvSigners;
    ArrayList<InviteSignersModel> list = new ArrayList<>();
    InviteSignersDialogAdapter invAdapter;

    MyReqDetailModel detailModel;
    EditText etName, etEmail, etDocumentName, etDescription, etLink, etMessage;

    String choosenDate = "", choosenTime= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_signature);
        initNetwork();
        getPermission();
        initData();
        initDialog();
        initDialogWaiting();
    }

    private int getIntentData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        return id;
    }

    private void initNetwork() {
        util = new Util();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
    }

    public void initSpinner(){

        Log.d("HomeTOKEN", token);
        Observable<GetTypeCategoryModel> getCategory= apiInterface.getCategories(token);
        getCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessCategory, this::onFailedCategory);

        Observable<GetTypeCategoryModel> getTypes= apiInterface.getTypes(token);
        getTypes.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessTypes, this::onFailedTypes);

    }
    private void initData(){
        Observable<GetMyReqDetailModel> getDetails = apiInterface.getCollabDetail(token, getIntentData());
        getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        util.toastError(this, "API GET COLLAB DETAIL", throwable);
        throwable.printStackTrace();
    }

    private void onSuccess(GetMyReqDetailModel getMyReqDetailModel) {
        if(getMyReqDetailModel !=null){
            detailModel = getMyReqDetailModel.getData();
            initComponent();
            initSpinner();
        }
    }
    public void initRecyclerView(){
        lvSigners = findViewById(R.id.lvInviteSigners);
        list = populateList(1);
        invAdapter = new InviteSignersDialogAdapter(
                this, list, false
        );
        lvSigners.setAdapter(invAdapter);
    }

//    private void addSigners() {
//        InviteSignersModel model = new InviteSignersModel();
//        model.setEtName("");
//        model.setEtEmail("");
//        list.add(model);
//        invAdapter.notifyDataSetChanged();
//        int data = list.size() * 90;
//        data = util.dpToPx(data, this);
//        lvSigners.getLayoutParams().height = data;
//        lvSigners.requestLayout();
//    }
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


        if(!nameValidator && !emailValidator && !documentNameValidator){
            return true;
        }else{
            return false;
        }
    }

    void addSignMethod(){
        if (validation()){
            try {
                RequestBody requestFile = null;
                MultipartBody.Part body = null;
                if(fileUri!=null){
                    requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(fileUri)),
                                    fileSending
                            );
                    body =
                            MultipartBody.Part.createFormData("file", fileSending.getName(), requestFile);
                }else{
                    requestFile = RequestBody.create(MediaType.parse("text/plain"), "");

                    body = MultipartBody.Part.createFormData("attachment", "", requestFile);
                }

                String token = preferencesRepo.getToken();
                Observable<GetSignDetailModel> postRecreateSign = apiInterface.putRecreateSignCollab(
                        token,
                        detailModel.getId(),
                        util.requestBodyString(etName.getText().toString()),
                        util.requestBodyString(etEmail.getText().toString()),
                        util.requestBodyString(etDocumentName.getText().toString()),
                        util.requestBodyString(String.valueOf(selectedCategory.getId())),
                        util.requestBodyString(String.valueOf(selectedTypes.getId())),
                        util.requestBodyString(etDescription.getText().toString()),
                        util.requestBodyString(etLink.getText().toString()),
                        body,
                        util.requestBodyString(choosenDate),
                        util.requestBodyString(choosenTime)
                );
                postRecreateSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);

            }catch (Exception e){
                e.printStackTrace();
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

        gotoResultPage(detailModel.getId());
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

        Spinner spinnerDocType = findViewById(R.id.spinnerDocTypeReqSign);
        ArrayAdapter<TypeCategoryModel> adapterDocType = new ArrayAdapter<TypeCategoryModel>(this, android.R.layout.simple_spinner_item, types);
        adapterDocType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocType.setAdapter(adapterDocType);

        for (int i=0; i< types.size(); i++){
            TypeCategoryModel element = types.get(i);
            if(element.getName().equals(detailModel.getType())) {
                spinnerDocType.setSelection(i);
                recreateType = i;
            }
        }
        spinnerDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getSelectedItem();
                Log.d("CategorySelected", selectedTypes.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getItemAtPosition(recreateType);

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
        Spinner spinnerCategory = findViewById(R.id.spinnerCategoryReqSign);
        ArrayAdapter<TypeCategoryModel> adapterCategory = new ArrayAdapter<TypeCategoryModel>(
                this,
                android.R.layout.simple_spinner_item,
                category);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        for(int i=0; i< category.size() ; i++){
            TypeCategoryModel element = category.get(i);
            if(element.getName().equals(detailModel.getCategory())){
                spinnerCategory.setSelection(i);
                recreateCategory = i;
            }
        }

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = (TypeCategoryModel) spinnerCategory.getSelectedItem();
                Log.d("CategorySelected", selectedCategory.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = (TypeCategoryModel) spinnerCategory.getItemAtPosition(recreateCategory);
            }
        });
    }

    //------------------------------ Initialize Component-----------------
    public void initComponent(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReqSign);
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
        Button btnCreate = findViewById(R.id.btnReqSign);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //--> Goto Init Dialog
                customDialog.show();
            }
        });
        llUploadData = findViewById(R.id.uploadDataReq);
        llUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePicker();
            }
        });

        llDoneData = findViewById(R.id.doneUploadDataReq);
        llDoneData.setVisibility(View.GONE);
        llWaitingData = findViewById(R.id.waitingUploadDataReq);
        llWaitingData.setVisibility(View.GONE);

        ImageView ivEmptyFile = findViewById(R.id.ivEmptyFileReq);
        ivEmptyFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyFile();
            }
        });

        etName = findViewById(R.id.etNameSignYour);
        etEmail = findViewById(R.id.etEmailNewSign);
        etDocumentName = findViewById(R.id.etDocNameNewSign);
        etDescription = findViewById(R.id.etDescNewSign);
        etLink = findViewById(R.id.etLinkNewSign);
        if(detailModel !=null){
            etName.setText(detailModel.getCreatedBy());
            etEmail.setText(detailModel.getEmailReq());
            etDocumentName.setText(detailModel.getTitle());
            etDescription.setText(detailModel.getDescription());
            etLink.setText(detailModel.getLink());
        }
        etMessage = findViewById(R.id.etMsgNewSign);
        btnDueDate = findViewById(R.id.btnDueDate);
        btnTime = findViewById(R.id.btnTime);

        btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timepIcker();
            }
        });
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
                TextView tvWaitingData = findViewById(R.id.tvWaitingUploadReq);
                tvWaitingData.setText(fileUri.getPath());
                setProgressValue(fileUri.getPath());
            }
        }
    }

    void convertURiToFile(Uri uri){
        File fileCopy = new File(getCacheDir(), getNameFile(uri));
        int maxBufferSize = 1 * 1024 * 1024;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
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
                TextView tvUploadData = findViewById(R.id.tvDoneUploadReq);
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
                waitingDialog.show();
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

    void gotoResultPage(int id){
        this.finish();
        Intent intent = new Intent(this, CollabResultActivity.class);
        intent.putExtra("type", getIntent().getIntExtra("type", 0));
        intent.putExtra("id", id);
        startActivity(intent);
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
    void dateTimePicker(){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DATE, dayOfMonth);
                        choosenDate = util.CalendarToDateString(c);
                        btnDueDate.setText(choosenDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    //---------------------------------Time Picker------------------------
    void timepIcker()
    {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        choosenTime = util.CalendarToTimeString(c);
                        btnTime.setText(choosenTime);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }
}