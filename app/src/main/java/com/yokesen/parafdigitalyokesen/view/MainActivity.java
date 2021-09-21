package com.yokesen.parafdigitalyokesen.view;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.LoginModel;
import com.yokesen.parafdigitalyokesen.view.forgot_password.ForgotPassword;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    EditText etPassword, etEmail;
    ImageView ivPassword;
    TextView tvSignUp, tvForgotPas;
    APIInterface apiInterface;
    Util util = new Util();
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        initGoogleSignIn();


    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void initComponent() {
        tvSignUp = findViewById(R.id.tvSignUp);

        Button buttonLogin = findViewById(R.id.btnLogin);

        Button btnGoogle = findViewById(R.id.btnGooglw);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivPassword = findViewById(R.id.show_pass_btn);
        tvForgotPas = findViewById(R.id.forgotPassword);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        buttonLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //gotoNavigationBar();
                        loginClick();
                    }
                }
        );

        //onClick for Goto Register
        tvSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoRegister();
                    }
                }

        );
        tvForgotPas.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoForgotPass();
                    }
                }
        );
    }


    void loginClick(){
        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (validation(email, password)){
            doLogin(email, password);
        }
    }
    void doLogin(String email, String password){
        Observable<LoginModel> callLogin = apiInterface.loginUser(email, password);
        callLogin.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResult, this::handleError);
//        callLogin.enqueue(new Callback<LoginModel>() {
//            @Override
//            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
//                if(response.isSuccessful()){
//                    LoginModel result = response.body();
//                    Log.d("RegisterAPI", "RegisterAPI"+ response.body());
//                    sharedPrefLogin(result.getToken());
//                    gotoNavigationBar();
//                }
//                else{
//                    Log.d("RegisterAPI", "RegisterAPIError"+ response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginModel> call, Throwable t) {
//
//            }
//        });
    }

    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                String email = acc.getEmail();
                String displayName = acc.getDisplayName();
                String token = acc.getIdToken();
                Uri picture = acc.getPhotoUrl();
                util.toastMisc(this, "Email:" + email);
            }catch (Exception e){
                e.printStackTrace();
                util.toastMisc(this, "Error Google Sign In");
            }
        }
    }

    boolean validation(String email, String password){
        boolean passValidator = false, emailValidator= false;
        if(email.trim().equals("")){
            emailValidator = true;
        }else{
            emailValidator = false;
        }
        util.changeColorEditText(etEmail, emailValidator, this);

        if(password.trim().equals("") || password.trim().length()<6){
            passValidator = true;

        }else{
            passValidator = false;
        }
        util.changeColorEditText(etPassword, passValidator, this);

        if(!emailValidator && !passValidator){
            return true;
        }else{
            return false;
        }
    }
    private void handleResult(LoginModel loginModel) {
        if(loginModel != null){

            PreferencesRepo preferencesRepo = new PreferencesRepo(this);
            preferencesRepo.setToken(loginModel.getToken());
            gotoNavigationBar();
        }else{
            Log.d("LoginAPI", "LoginAPI "+  "Emptty" );
        }

    }
    private void handleError(Throwable throwable) {
        String wrong = throwable.getMessage().toLowerCase();
        if (wrong.contains("unauthorized")|| wrong.contains("401")){

            Toast.makeText(this, "Please check your email or password.",
                    Toast.LENGTH_LONG).show();
        }else{

            util.toastError(this, "API LOGIN ERROR", throwable);
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_login, menu);
        return true;
    }

    public void gotoRegister() {
        Intent intent = new Intent(this, register_activity.class);
        startActivity(intent);
    }

    public void gotoNavigationBar() {
        Intent intent = new Intent(this, NavBarActivity.class);
        finish();
        startActivity(intent);
    }

    public void gotoForgotPass() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void ShowHidePass(View view){
        if(view.getId()==R.id.show_pass_btn){
            if(etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);
                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide);
            }
        }
    }

}