package com.example.parafdigitalyokesen.view.ui.bottom_sheet_nav;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.view.add_sign.ReqSignatureActivity;
import com.example.parafdigitalyokesen.view.add_sign.SignYourselfActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetNav extends BottomSheetDialogFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_nav,
                container, false);
        LinearLayout layoutSignYourself = v.findViewById(R.id.layoutSignYourself);
        LinearLayout layoutReqSign = v.findViewById(R.id.layoutReqSignature);

        layoutSignYourself.setOnClickListener(this);
        layoutReqSign.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.layoutSignYourself:
                Intent intent = new Intent(getActivity(), SignYourselfActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutReqSignature:
                Intent intent2 = new Intent(getActivity(), ReqSignatureActivity.class);
                startActivity(intent2);
                break;
            default:
                Log.d("onClickError", "onClickWrong ID");
                break;
        }
    }
}
