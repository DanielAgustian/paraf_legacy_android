package com.example.parafdigitalyokesen.view.add_sign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.GetTypeCategoryModel;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.TypeCategoryModel;
import com.example.parafdigitalyokesen.util.UtilWidget;

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

public class ReqSignatureActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnContinue, btnCancel, btnDueDate, btnTime;
    LinearLayout llUploadData, llWaitingData, llDoneData, llAddSigners;
    ListView lvSigners;
    private Dialog customDialog, waitingDialog;
    private Uri fileUri;
    Util util = new Util();
    File fileSending;
    UtilWidget utiWidget = new UtilWidget(this);
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    List<TypeCategoryModel> category, types;
    TypeCategoryModel selectedCategory, selectedTypes;

    InviteSignersDialogAdapter invAdapter;
    ArrayList<InviteSignersModel> list;

    private EditText etName, etEmail, etDocumentName, etDescription, etLink, etMessage;
    private final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String filePath, choosenDate = "", choosenTime = "";
    int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_signature);
        initNetwork();
        initToolbar();
        initRecyclerView();
        initSpinner();
        initDialog();
        initDialogWaiting();
        initComponent();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnDueDate){
            dateTimePicker();
        } else if(view.getId() ==R.id.btnTime){
            timepIcker();
        } else if (view.getId() == R.id.uploadDataReq){
            filePicker();
        } else if (view.getId()== R.id.ivEmptyFileReq){
            emptyFile();
        } else if (view.getId() == R.id.llAddSigners){
            addSigners();
        }
    }


    //------------------------File Picker Method---------------------------------
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
                progress = 0;
            }
        };
        mCountDownTimer.start();


    }
    //----------------------------------------------------------------


    //---------------------------Date picker----------------------
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
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
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


    //----------------------Init Component + Data-----------------------------//

    private void initNetwork() {

        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
    }

    public void initToolbar(){
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
    }

    public void initRecyclerView(){
        lvSigners = findViewById(R.id.lvInviteSigners);
        list = populateList(1);
        invAdapter = new InviteSignersDialogAdapter(
                this, list, false
        );
        lvSigners.setAdapter(invAdapter);
    }

    private void addSigners() {
        InviteSignersModel model = new InviteSignersModel();
        model.setEtName("");
        model.setEtEmail("");
        list.add(model);
        invAdapter.notifyDataSetChanged();
        int data = list.size() * 90;
        data = util.dpToPx(data, this);
        lvSigners.getLayoutParams().height = data;
        lvSigners.requestLayout();
    }


    public void back(){
        this.finish();
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
    //-------------------Init Spinner Category + Types ----------------------------//
    public void initSpinner(){

        String token = preferencesRepo.getToken();

        Log.d("HomeTOKEN", token);
        Observable<GetTypeCategoryModel> getCategory= apiInterface.getCategories(token);
        getCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessCategory, this::onFailedCategory);

        Observable<GetTypeCategoryModel> getTypes= apiInterface.getTypes(token);
        getTypes.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessTypes, this::onFailedTypes);

    }
    //---------------------Method For Get Types Spinner-------------------------------

    private void onFailedTypes(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Types. Try again" + throwable.getMessage(),
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

        spinnerDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getSelectedItem();
                Log.d("CategorySelected", selectedTypes.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTypes = (TypeCategoryModel) spinnerDocType.getItemAtPosition(0);

            }
        });
    }


    //-----------------------Method for Category Spinner -----------------------

    private void onFailedCategory(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Category. Try again"+ throwable.getMessage(),
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
    public void initDialogWaiting(){
        waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.setCancelable(false);
    }

    public void initComponent(){
        llUploadData = findViewById(R.id.uploadDataReq);
        btnDueDate = findViewById(R.id.btnDueDate);
        btnTime = findViewById(R.id.btnTime);
        Button btnCreate = findViewById(R.id.btnReqSign);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
            }
        });
        btnDueDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        llUploadData.setOnClickListener(this);
        llDoneData = findViewById(R.id.doneUploadDataReq);
        llDoneData.setVisibility(View.GONE);
        llWaitingData = findViewById(R.id.waitingUploadDataReq);
        llWaitingData.setVisibility(View.GONE);
        llAddSigners = findViewById(R.id.llAddSigners);
        llAddSigners.setOnClickListener(this);
        ImageView ivEmptyFile = findViewById(R.id.ivEmptyFileReq);
        ivEmptyFile.setOnClickListener(this);

        etName = findViewById(R.id.etNameSignYour);
        etEmail = findViewById(R.id.etEmailNewSign);
        etDocumentName = findViewById(R.id.etDocNameNewSign);
        etDescription = findViewById(R.id.etDescNewSign);
        etLink = findViewById(R.id.etLinkNewSign);
        etMessage = findViewById(R.id.etMsgNewSign);


    }
    void gotoResultPage(int id){
        this.finish();
        Intent intent = new Intent(this, ResultReqSignActivity.class);

        intent.putExtra("id", id);
        startActivity(intent);
    }

    //=-----------------------------Method for Add SIgn------------------

    private boolean validation(){
        boolean nameValidator = false,
                emailValidator=false,
                documentNameValidator =false,
                dateValidator = false,
                timeValidator = false,
                emailListValidator = false;
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

        if (choosenDate != ""){
            dateValidator = false;
        }else{
            dateValidator = true;
        }
        if(choosenTime != ""){
            timeValidator = false;
        } else{
            timeValidator = true;
        }

        if(getDataEmails().size() < 1){
            emailListValidator = true;
        } else{
            emailListValidator = false;
        }

        util.changeColorEditText(etName, nameValidator, this);
        util.changeColorEditText(etEmail, emailValidator, this);
        util.changeColorEditText(etDocumentName, documentNameValidator, this);
        if(dateValidator){
            utiWidget.makeApprovalDialog("Date Empty", "Please Choose Date.");
        }
        if(timeValidator){
            utiWidget.makeApprovalDialog("Time Empty", "PLease Choose Time");
        }
        if(emailListValidator){
            utiWidget.makeApprovalDialog("No Email", "Please write at least one email address");
        }

        if(!nameValidator && !emailValidator && !documentNameValidator
                && !dateValidator && !timeValidator){
            return true;
        }else{
            return false;
        }
    }

    void addSignMethod(){
        if (validation()){

            waitingDialog.show();

            try {
                Log.d("FILE_GETNAMe", fileSending.getName());
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
                Observable<GetSignDetailModel> addReqSign = apiInterface.addReqSign(
                        token,
                        util.requestBodyString(etName.getText().toString()),
                        util.requestBodyString(etEmail.getText().toString()),
                        util.requestBodyString(etDocumentName.getText().toString()),
                        util.requestBodyString(String.valueOf(selectedCategory.getId())),
                        util.requestBodyString(String.valueOf(selectedTypes.getId())),
                        util.requestBodyString(etDescription.getText().toString()),
                        util.requestBodyString(etLink.getText().toString()),
                        body,
                        util.requestBodyString(choosenDate),
                        util.requestBodyString(choosenTime),
                        util.requestBodyString(etMessage.getText().toString()),
                        getDataEmails()
                );
                addReqSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAddSign, this::onFailedAddSign);
            }catch (Exception e){
                e.printStackTrace();
                waitingDialog.dismiss();
            }

        }
    }

    private void onFailedAddSign(Throwable throwable) {
        waitingDialog.dismiss();
        Toast.makeText(this, "ERROR IN POST API NEW SIGN. Try again = "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();

    }

    private void onSuccessAddSign(GetSignDetailModel model) {
        if(model!=null){
            waitingDialog.dismiss();
            gotoResultPage(model.getHome().getId());
        }
    }

    private ArrayList<String> getDataEmails(){
        ArrayList<String> emails = new ArrayList<>();
        for (int i =0; i< list.size(); i++){
            View viewListItem = lvSigners.getChildAt(i);
            EditText editText = viewListItem.findViewById(R.id.etDialogItemInvEmail);
            String string = editText.getText().toString();
            if(!(string.equals(""))){
                emails.add(string);
            }
        }
        return emails;
    }
}