package com.yokesen.parafdigitalyokesen.view.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetMyInfoModel;
import com.yokesen.parafdigitalyokesen.model.MyInformationModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.util.UtilFile;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.MainActivity;
import com.yokesen.parafdigitalyokesen.view.ui.PDFEditorActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.qr_scanner.QrScannerActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.ChangePasswordActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.HelpActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.MyContact;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.MyInformation;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.NotificationActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.SecurityPrivacyActivity;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private ProfileViewModel mViewModel;
    TextView tvName, tvEmail;
    ImageView circleAvatar;
    Dialog waitingDialog;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    GoogleSignInClient googleSignInClient;
    LinearLayout llVerifyNotice;
    UtilWidget uw = new UtilWidget(getActivity());
    Util util = new Util();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_profile, container, false);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(getActivity());
        initComponent(root);
        initDialogWaiting();
        initData();
        initGoogleSignOut();


        return root;
    }

    private void initGoogleSignOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {


        String token = preferencesRepo.getToken();

        Observable<GetMyInfoModel> callHome = apiInterface.getMyInfo(token);
        try {
            callHome.subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onSuccess, this::onFailed);
        } catch (Exception e){
            Log.e("ProfileCrash", e.getMessage());
        }
    }

    private void onFailed(Throwable throwable) {
        Log.e("ProfileCrash", throwable.getMessage());
        Toast.makeText(getActivity(), "ERROR IN FETCHING API PROFILE. Try again" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetMyInfoModel model) {
        if(model != null){
            MyInformationModel myInfo = model.getHome();
            tvName.setText(myInfo.getName());
            tvEmail.setText(myInfo.getEmail());

            if(myInfo.isEmailVerified()){
                llVerifyNotice.setVisibility(View.GONE);
            } else{
                llVerifyNotice.setVisibility(View.VISIBLE);
            }

            if (myInfo.getPhoto() != null){
                new UtilWidget.DownLoadImageTask(circleAvatar).execute(myInfo.getPhoto());
            }
        }
    }

    public void initComponent(View root){
        tvName = root.findViewById(R.id.tvProfileName);
        tvEmail = root.findViewById(R.id.tvProfileEmail);
        circleAvatar = root.findViewById(R.id.circleAvatarProfile);
        llVerifyNotice = root.findViewById(R.id.llVerifyNotice);

        RelativeLayout rlMyInfo = root.findViewById(R.id.rlMyInfo);
        rlMyInfo.setOnClickListener(this);

        RelativeLayout rlChangePassword = root.findViewById(R.id.rlChangePassword);
        rlChangePassword.setOnClickListener(this);

        RelativeLayout rlPDFEditor = root.findViewById(R.id.rlPDFEditor);
        rlPDFEditor.setOnClickListener(this);
        rlPDFEditor.setVisibility(View.GONE);

        RelativeLayout rlMyContact = root.findViewById(R.id.rlMyContact);
        rlMyContact.setOnClickListener(this);

        RelativeLayout rlNotification = root.findViewById(R.id.rlNotification);
        rlNotification.setOnClickListener(this);

        RelativeLayout rlSecurity = root.findViewById(R.id.rlSecurity);
        rlSecurity.setOnClickListener(this);

        RelativeLayout rlHelp = root.findViewById(R.id.rlHelp);
        rlHelp.setOnClickListener(this);

        RelativeLayout rlLogOut = root.findViewById(R.id.rlLogOut);
        rlLogOut.setOnClickListener(this);

        RelativeLayout rlScanQR = root.findViewById(R.id.rlScanQr);
        rlScanQR.setOnClickListener(this);

        LinearLayout llEditButton = root.findViewById(R.id.editPPLL);
        llEditButton.setOnClickListener(this);

        TextView tvVerify =  root.findViewById(R.id.tvVerifyLink);
        String content = "Please verify your email address, verify email";

        ClickableSpan spannable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                clickVerify();
            }
        };
        int startIndex = content.indexOf("verify email");
        int lastIndex = startIndex + "verify email".length();
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(spannable, startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvVerify.setText(spannableString);
        tvVerify.setHighlightColor(getResources().getColor(R.color.colorBackgroundTab));
        tvVerify.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void clickVerify() {
        String token = preferencesRepo.getToken();

        Observable<SimpleResponse> callResend = apiInterface.resendVerificationEmail(token);
        try {
            callResend.subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onSuccessResend, this::onFailedResend);
        } catch (Exception e){
            Log.e("ProfileCrash", e.getMessage());
        }
    }

    private void onFailedResend(Throwable throwable){
        util.toastMisc(getActivity(), "Email failed to be sent.");
    }

    private void onSuccessResend(SimpleResponse response) {
        util.toastMisc(getContext(), "Email has been succesfully sent!");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlScanQr:
                gotoProfileChild(QrScannerActivity.class);
                break;
            case R.id.rlMyInfo:
                gotoProfileChild(MyInformation.class);
                break;
            case R.id.rlChangePassword:
                gotoProfileChild(ChangePasswordActivity.class);
                break;
            case R.id.rlMyContact:
                gotoProfileChild(MyContact.class);
                break;
            case R.id.rlNotification:
                gotoProfileChild(NotificationActivity.class);
                break;
            case R.id.rlSecurity:
                gotoProfileChild(SecurityPrivacyActivity.class);
                break;
            case R.id.rlHelp:
                gotoProfileChild(HelpActivity.class);
                break;
            case R.id.rlLogOut:
                logOut();
                break;
            case R.id.editPPLL:
                editProfilePicture();
                break;
            case R.id.rlPDFEditor:
                gotoProfileChild(PDFEditorActivity.class);
                break;
            default:
                Log.d("ProfileFragment", "Error Data");
                break;
        }
    }



    private void logOut() {
        String token = preferencesRepo.getToken();

        Observable<SimpleResponse> callHome = apiInterface.logOutUser(token);

        try {
            callHome.subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onSuccessLogout, this::onFailedLogOut);
        } catch (Exception e){
            Log.e("ProfileCrash", e.getMessage());
        }
    }

    private void onFailedLogOut(Throwable throwable) {
        Util util= new Util();
        util.toastError(getActivity(), "API Log Out", throwable);
    }

    private void onSuccessLogout(SimpleResponse simpleResponse) {
        if (simpleResponse!=null){
            preferencesRepo.logOut();
            if(GoogleSignIn.getLastSignedInAccount(getActivity()) != null){
                googleSignInClient.signOut();
            }
            if( AccessToken.getCurrentAccessToken() != null){
                LoginManager.getInstance().logOut();
            }
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void gotoProfileChild(Class childProfile){
        Intent intent = new Intent(getActivity(), childProfile);
        startActivity(intent);
    }
    private void editProfilePicture() {
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        chooseFile.setType("image/*");
        chooseFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");

        startActivityForResult(chooseFile, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){
            if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){

                waitingDialog.show();
               Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    circleAvatar.setImageBitmap(bitmap);
                    UtilFile uf = new UtilFile(getContext());
                    File file = uf.convertURiToFile(uri);
                    sentFile(file, uri);

                } catch (IOException e) {
                    e.printStackTrace();
                    Util util = new Util();
                    util.toastMisc(getContext(), "Error in Get File");
                    waitingDialog.dismiss();
                }
            }
        }
    }

    private void sentFile(File file, Uri uri){
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContext().getContentResolver().getType(uri)),
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        String token = preferencesRepo.getToken();
        Observable<SimpleResponse> postPhoto = apiInterface.editPhoto(
                token,
                body
        );
        postPhoto.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessPhoto, this::onFailedAddSign);

    }

    private void onFailedAddSign(Throwable throwable) {
        Util util = new Util();
        util.toastError(getContext(), "API EDIT PHOTO", throwable);
        waitingDialog.dismiss();
    }

    private void onSuccessPhoto(SimpleResponse response) {
        if(response != null){
            initData();
            waitingDialog.dismiss();
        }
    }

    public void initDialogWaiting(){
        waitingDialog = new Dialog(getContext());
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.setCancelable(false);
    }
}