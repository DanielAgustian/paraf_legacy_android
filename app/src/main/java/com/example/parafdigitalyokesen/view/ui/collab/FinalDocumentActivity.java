package com.example.parafdigitalyokesen.view.ui.collab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class FinalDocumentActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvAddSigners;
    LinearLayout llUploadData, llWaitingData, llDoneData;
    ImageView ivEmptyFile;
    ListView lvSigners;
    ArrayList<InviteSignersModel> list;
    EditText etMessage;
    Button btnFinalDoc;
    Util util = new Util();
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    File fileSending;
    int progress = 0;
    private Uri fileUri;
    private final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_document);
        initNetwork();
        getPermission();
        initToolbar();
        initComponent();
        initRecyclerView();
    }

    private void initNetwork() {

        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tvAddSignersFinalDoc:
                break;
            case R.id.uploadDataFinal:
                filePicker();
                break;
            case R.id.ivEmptyFileFinal:
                emptyFile();
                break;
            case R.id.btnFinalDoc:
                sentDocMethod();

                break;
            default:
                break;
        }
    }
    //--------------------Sent Document ------------------------------
    void sentDocMethod(){
        if (validation()){
            try {
                int id = getIntent().getIntExtra("id", -1);
                Log.d("FILE_GETNAMe", fileSending.getName());
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(getContentResolver().getType(fileUri)),
                                fileSending
                        );
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", fileSending.getName(), requestFile);


                String token = preferencesRepo.getToken();
                Observable<SimpleResponse> postNewSign = apiInterface.SendDocument(
                        token,
                        getDataEmails(),
                        body,
                        util.requestBodyString(String.valueOf(id)),
                        util.requestBodyString(etMessage.getText().toString())
                );
                postNewSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessSentDoc, this::onFailedSentDoc);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void onFailedSentDoc(Throwable throwable) {
        util.toastError(this, "API SENT FINAL DOC", throwable);
    }

    private void onSuccessSentDoc(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            back();
        }
    }

    private boolean validation() {
        return true;
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


    //------------------------File Picker--------------------------//

    private void filePicker() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

        chooseFile.setType("*/*");

        chooseFile = Intent.createChooser(chooseFile, "Choose a file");

        startActivityForResult(chooseFile, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }
    private void emptyFile() {
        filePath = "";
        llDoneData.setVisibility(View.GONE);
        llUploadData.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){
            if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){
                fileUri = data.getData();
                convertURiToFile(fileUri);
                llUploadData.setVisibility(View.GONE);
                llWaitingData.setVisibility(View.VISIBLE);
                TextView tvWaitingData = findViewById(R.id.tvWaitingUploadFinal);
                tvWaitingData.setText(filePath);
                setProgressValue(fileUri);
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

    //For progress bar
    private void setProgressValue(Uri uri) {
        ProgressBar simpleProgressBar = findViewById(R.id.simpleProgressBarFinal);
        // set the progress

        TextView tvWaitingUplaod = findViewById(R.id.tvWaitingUploadFinal);
        tvWaitingUplaod.setText(getNameFile(uri));
        simpleProgressBar.setProgress(progress);

        // thread is used to change the progress value
        CountDownTimer mCountDownTimer = new CountDownTimer(2000,200) {

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
                TextView tvUploadData = findViewById(R.id.tvDoneUploadFinal);
                tvUploadData.setText(getNameFile(uri));
                progress = 0;
            }
        };
        mCountDownTimer.start();


    }


    //------------------------------------------------------------


    private void initComponent() {
        tvAddSigners = findViewById(R.id.tvAddSignersFinalDoc);
        llUploadData = findViewById(R.id.uploadDataFinal);
        llWaitingData = findViewById(R.id.waitingUploadDataFinal);
        llDoneData = findViewById(R.id.doneUploadDataFinal);
        ivEmptyFile = findViewById(R.id.ivEmptyFileFinal);
        btnFinalDoc = findViewById(R.id.btnFinalDoc);

        tvAddSigners.setOnClickListener(this);
        llUploadData.setOnClickListener(this);
        ivEmptyFile.setOnClickListener(this);
        btnFinalDoc.setOnClickListener(this);

        llDoneData.setVisibility(View.GONE);
        llWaitingData.setVisibility(View.GONE);

        etMessage = findViewById(R.id.etMsgFinal);
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFinalDoc);
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
        lvSigners = findViewById(R.id.lvSignersFinalDocument);
//        lvSigners.setNestedScrollingEnabled(false);
        list = populateList(1);
        InviteSignersDialogAdapter invAdapter = new InviteSignersDialogAdapter(
                this, list
        );
        lvSigners.setAdapter(invAdapter);
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
    public void back(){
        this.finish();
    }

    private ArrayList<String> getDataEmails(){
        ArrayList<String> emails = new ArrayList<>();
        for (int i =0; i< list.size(); i++){
            View viewListItem = lvSigners.getChildAt(i);
            EditText editText = viewListItem.findViewById(R.id.etDialogItemInvEmail);
            String string = editText.getText().toString();
            emails.add(string);
        }
        return emails;
    }
}