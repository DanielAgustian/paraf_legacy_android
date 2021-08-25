package com.example.parafdigitalyokesen.view.ui.collab.bottom_sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parafdigitalyokesen.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import kotlin.reflect.KType;

public class BottomSheetSeeQR extends BottomSheetDialogFragment implements View.OnClickListener {
    String type;
    public BottomSheetSeeQR(String type) {
        this.type = type;
    }
    LinearLayout llSave, llShare, llRequestFile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_see_qr,
                container, false);
        llSave= v.findViewById(R.id.llSaveCollabDialog);
        llShare = v.findViewById(R.id.llShareCollabDialog);
        llRequestFile = v.findViewById(R.id.llRequestFileDialog);
        if(type.equals("view")){
            llSave.setVisibility(View.GONE);
            llShare.setVisibility(View.GONE);
            llRequestFile.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onClick(View view) {

    }
}
