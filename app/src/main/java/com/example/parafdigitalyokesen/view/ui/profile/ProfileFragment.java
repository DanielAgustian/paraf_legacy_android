package com.example.parafdigitalyokesen.view.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.ChangePasswordActivity;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.HelpActivity;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.MyContact;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.MyInformation;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.NotificationActivity;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.SecurityPrivacyActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_profile, container, false);
       initComponent(root);

        return root;
    }

    public void initComponent(View root){
        RelativeLayout rlMyInfo = root.findViewById(R.id.rlMyInfo);
        rlMyInfo.setOnClickListener(this);

        RelativeLayout rlChangePassword = root.findViewById(R.id.rlChangePassword);
        rlChangePassword.setOnClickListener(this);

        RelativeLayout rlMyContact = root.findViewById(R.id.rlMyContact);
        rlMyContact.setOnClickListener(this);

        RelativeLayout rlNotification = root.findViewById(R.id.rlNotification);
        rlNotification.setOnClickListener(this);

        RelativeLayout rlSecurity = root.findViewById(R.id.rlSecurity);
        rlSecurity.setOnClickListener(this);

        RelativeLayout rlHelp = root.findViewById(R.id.rlHelp);
        rlHelp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
            default:
                Log.d("ProfileFragment", "Error Data");
                break;
        }
    }

    private void gotoProfileChild(Class childProfile){
        Intent intent = new Intent(getActivity(), childProfile);
        startActivity(intent);
    }

}