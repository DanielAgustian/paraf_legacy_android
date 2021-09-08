package com.example.parafdigitalyokesen.view.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    List<String> strings = new ArrayList<String>();
    EditText etSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        strings = Arrays.asList("MySignature", "My Request", "Waiting Signature", "Accepted Signature", "Rejected Signature");
        initSuggestion();
        initComponent();
    }

    private void initComponent() {
        etSearch  = findViewById(R.id.acTVSearch);


        LinearLayout child_flex1 = findViewById(R.id.child_flex1);
        LinearLayout child_flex2 = findViewById(R.id.child_flex2);
        LinearLayout child_flex3 = findViewById(R.id.child_flex3);
        LinearLayout child_flex4 = findViewById(R.id.child_flex4);
        LinearLayout child_flex5 = findViewById(R.id.child_flex5);

        child_flex1.setOnClickListener(this);
        child_flex2.setOnClickListener(this);
        child_flex3.setOnClickListener(this);
        child_flex4.setOnClickListener(this);
        child_flex5.setOnClickListener(this);

    }

    private void initSuggestion() {
        TextView tvflex1 = findViewById(R.id.flexLL1);
        tvflex1.setText(strings.get(0));

        TextView tvflex2 = findViewById(R.id.flexLL2);
        tvflex2.setText(strings.get(1));

        TextView tvflex3 = findViewById(R.id.flexLL3);
        tvflex3.setText(strings.get(2));

        TextView tvflex4 = findViewById(R.id.flexLL4);
        tvflex4.setText(strings.get(3));

        TextView tvflex5 = findViewById(R.id.flexLL5);
        tvflex5.setText(strings.get(4));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.child_flex1:
                etSearch.setText(strings.get(0));
                break;
            case R.id.child_flex2:
                etSearch.setText(strings.get(1));
                break;
            case R.id.child_flex3:
                etSearch.setText(strings.get(2));
                break;
            case R.id.child_flex4:
                etSearch.setText(strings.get(3));
                break;
            case R.id.child_flex5:
                etSearch.setText(strings.get(4));
                break;
            default:
                break;
        }
    }
}