package com.yokesen.parafdigitalyokesen.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.Variables;
import com.yokesen.parafdigitalyokesen.model.GetPasscodeModel;
import com.yokesen.parafdigitalyokesen.model.GetProfileModel;
import com.yokesen.parafdigitalyokesen.model.GetTypeCategoryModel;
import com.yokesen.parafdigitalyokesen.model.PasscodeModel;
import com.yokesen.parafdigitalyokesen.model.ProfileModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.yokesen.parafdigitalyokesen.view.ui.bottom_sheet_nav.BottomSheetNav;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yokesen.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.yokesen.parafdigitalyokesen.view.ui.draft.ResultAfterRespond;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.ConfirmPasscode;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NavBarActivity  extends AppCompatActivity {
    Util util = new Util();
    long milisStart = 0;
    PreferencesRepo preferencesRepo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        NavigationUI.setupWithNavController(navView, navController);
        FloatingActionButton btnFab = findViewById(R.id.fab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetNav bottomSheet = new BottomSheetNav();
                bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
            }
        });


        getProfile();
        getPasscodeData();
        handlingDynamicLink();
        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

    }

    private void getPasscodeData() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        String token = preferencesRepo.getToken();
        Observable<GetPasscodeModel> getProfile= apiInterface.GetPasscode(token);
        getProfile.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessPasscode, this::onFailedPasscode);
    }

    private void onFailedPasscode(Throwable throwable) {
        util.toastError(this, "API GET PASSCODE", throwable);
    }

    private void onSuccessPasscode(GetPasscodeModel getPasscodeModel) {
        if(getPasscodeModel != null){
            PasscodeModel model = getPasscodeModel.getData();
            preferencesRepo.setPasscode(model.getPasscode());
            preferencesRepo.setAllowPasscode(model.getIsActive());
        }
    }

    void getProfile(){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        String token = preferencesRepo.getToken();
        Observable<GetProfileModel> getProfile= apiInterface.getProfile(token);
        getProfile.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessProfile, this::onFailedTypes);
    }

    private void onFailedTypes(Throwable throwable) {
        util.toastError(this, "API PROFILE", throwable);
    }

    private void onSuccessProfile(GetProfileModel getProfileModel) {
        if(getProfileModel != null){
            ProfileModel model = getProfileModel.getHome();
            preferencesRepo.setProfile(model.getEmail(), model.getName());
        }
    }

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

    private void biometricPrompt() {
        util.toastMisc(this, "Begin biomteric");
        UtilWidget utilWidget = new UtilWidget(this);
        utilWidget.biometricPrompt();
    }

    private void handlingDynamicLink(){
        Variables var = new Variables();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        String myLink = "";
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            myLink = deepLink.toString();
                            String [] data =  myLink.split("/");
                            int id = Integer.parseInt(data[6]);
                            if(data[4].equals(var.typeSign[0])){

                                Intent intent= new Intent(NavBarActivity.this, ResultSignature.class);
                                intent.putExtra("where", "mysign");
                                intent.putExtra("id", id);
                                startActivity(intent);
                            } else if (data[4].equals(var.typeSign[1])){

                                Intent intent= new Intent(NavBarActivity.this, ResultAfterRespond.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            } else if (data[4].equals(var.typeSign[2])){
                                Intent intent = new Intent(NavBarActivity.this, CollabResultActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        }

                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NAVBARACTIVITY", "getDynamicLink:onFailure", e);
                    }
                });
    }
}
