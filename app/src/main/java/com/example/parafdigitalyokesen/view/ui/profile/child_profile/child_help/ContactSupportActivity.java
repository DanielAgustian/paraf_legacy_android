package com.example.parafdigitalyokesen.view.ui.profile.child_profile.child_help;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;

import org.w3c.dom.Text;

public class ContactSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        initComponent();
        initToolbar();
    }
    void initComponent(){
        TextView tvCS = findViewById(R.id.tvTerms);
        tvCS.setText(contactText());
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCS);
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
    private String contactText(){
        return "Our priorities is to help you experiencing the best of our product and services. Should you have any questions, facing problems during your journey with Teken or in case you need to get any information about us, feel free to reach us through:";
    }
    public void back(){
        this.finish();
    }
}