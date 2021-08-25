package com.example.parafdigitalyokesen.view.ui.draft.bottom_sheet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.view.add_sign.ReqSignatureActivity;
import com.example.parafdigitalyokesen.view.add_sign.SignYourselfActivity;
import com.example.parafdigitalyokesen.view.ui.collab.CollabResultActivity;
import com.example.parafdigitalyokesen.view.ui.draft.RespondSignature;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDraftInfo extends BottomSheetDialogFragment implements View.OnClickListener {
    SignModel sign;
    int where;
    /*
    * where is identifier where are they from
    *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
    * */
    public BottomSheetDraftInfo(SignModel sign, int where) {
        this.sign = sign;
        this.where =where;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_nav_info_draft,
                container, false);
        LinearLayout llDetails = v.findViewById(R.id.llDetailsDraft);
        llDetails.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.llDetailsDraft:
//                Intent intent = new Intent(getActivity(), RespondSignature.class);
                Intent intent = new Intent(getActivity(), CollabResultActivity.class);
                intent.putExtra("type", where);
                startActivity(intent);
                break;
            default:
                Log.d("onClickError", "onClickWrong ID");
                break;
        }
    }
}
