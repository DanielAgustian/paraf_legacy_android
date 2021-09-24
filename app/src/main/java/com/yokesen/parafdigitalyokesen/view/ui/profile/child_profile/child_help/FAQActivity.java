package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;

public class FAQActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvStarted, tvExport, tvManage, tvSecurity, tvTroubleShoot;
    boolean bStarted = false, bExport = false, bManage = false, bSecurity = false, bTroubleShoot = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);
        initToolbar();
        initComponent();
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFAQ);
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
    public void initComponent(){
        RelativeLayout rlStarted = findViewById(R.id.rlStartedFAQ);
        RelativeLayout rlExport = findViewById(R.id.rlCExportFAQ);
        RelativeLayout rlManage =findViewById(R.id.rlManageFAQ);
        RelativeLayout rlSecurity = findViewById(R.id.rlSecurityFAQ);
        RelativeLayout rlTroubleShoot = findViewById(R.id.rlTroubleShoot);

        rlStarted.setOnClickListener(this);
        rlExport.setOnClickListener(this);
        rlManage.setOnClickListener(this);
        rlSecurity.setOnClickListener(this);
        rlTroubleShoot.setOnClickListener(this);

        writeFAQ();
    }





    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.rlStartedFAQ:
                bStarted = !bStarted;
                openSesame(R.id.ivStarted, bStarted, tvStarted);
                break;
            case R.id.rlCExportFAQ:
                bExport = !bExport;
                openSesame(R.id.ivExport, bExport, tvExport);
                break;
            case R.id.rlManageFAQ:
                bManage = !bManage;
                openSesame(R.id.ivManage, bManage, tvManage);
                break;
            case R.id.rlSecurityFAQ:
                bSecurity = !bSecurity;
                openSesame(R.id.ivSecurity, bSecurity, tvSecurity);
                break;
            case R.id.rlTroubleShoot:
                bTroubleShoot = !bTroubleShoot;
                openSesame(R.id.ivTroubleshoot, bTroubleShoot, tvTroubleShoot);
                break;
        }
    }

    private void openSesame(int imageViewId, boolean clicked, TextView textView){
        ImageView ivArrow = findViewById(imageViewId);
        if(clicked){
            textView.setVisibility(View.VISIBLE);
            ivArrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_arrow_up));
        }else{
            textView.setVisibility(View.GONE);
            ivArrow.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_arrow_right));
        }
    }


    private void writeFAQ() {

        tvStarted = findViewById(R.id.tvStarted);
        tvExport = findViewById(R.id.tvExport);
        tvManage = findViewById(R.id.tvManage);
        tvSecurity = findViewById(R.id.tvSecurityFaq);
        tvTroubleShoot = findViewById(R.id.tvTroubleShoot);

        tvStarted.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et molestie eu, leo adipiscing non id viverra nullam. Non duis turpis ipsum dictum elementum mattis. Sit interdum metus, pretium mattis aliquet eu quis nunc nisl. Eget mauris condimentum facilisis auctor.");
        tvExport.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et molestie eu, leo adipiscing non id viverra nullam. Non duis turpis ipsum dictum elementum mattis. Sit interdum metus, pretium mattis aliquet eu quis nunc nisl. Eget mauris condimentum facilisis auctor.");
        tvManage.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et molestie eu, leo adipiscing non id viverra nullam. Non duis turpis ipsum dictum elementum mattis. Sit interdum metus, pretium mattis aliquet eu quis nunc nisl. Eget mauris condimentum facilisis auctor.");
        tvSecurity.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et molestie eu, leo adipiscing non id viverra nullam. Non duis turpis ipsum dictum elementum mattis. Sit interdum metus, pretium mattis aliquet eu quis nunc nisl. Eget mauris condimentum facilisis auctor.");
        tvTroubleShoot.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Et molestie eu, leo adipiscing non id viverra nullam. Non duis turpis ipsum dictum elementum mattis. Sit interdum metus, pretium mattis aliquet eu quis nunc nisl. Eget mauris condimentum facilisis auctor.");

    }
}