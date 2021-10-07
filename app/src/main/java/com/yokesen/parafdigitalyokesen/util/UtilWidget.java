package com.yokesen.parafdigitalyokesen.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.yokesen.parafdigitalyokesen.R;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executor;

public class UtilWidget {
    Context context;
    public UtilWidget(Context context) {
        this.context = context;
    }

    public void makeApprovalDialog(String title, String data){
        Dialog remindDialog = new Dialog(context);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_approval);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText(title);
        textData.setText(data);
        Button btnContinue = remindDialog.findViewById(R.id.btnCloseDialog);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });

    }
    public Dialog makeLoadingDialog(){
        Dialog remindDialog = new Dialog(context);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_waiting);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        textTitle.setVisibility(View.GONE);
        return remindDialog;
    }

    public void biometricPrompt(){
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        executor = ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if(errString.toString().toLowerCase().contains("enrolled")){
                    Toast.makeText(context,
                            "You dont have any fingerprint recorded.", Toast.LENGTH_SHORT)
                            .show();
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    context.startActivity(intent);

                }
                Toast.makeText(context,
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(context, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Teken")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
    public static class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }


}
