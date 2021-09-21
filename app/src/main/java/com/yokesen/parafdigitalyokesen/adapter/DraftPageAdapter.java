package com.yokesen.parafdigitalyokesen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yokesen.parafdigitalyokesen.view.ui.draft.fragment.DraftMySIgnatureFragment;
import com.yokesen.parafdigitalyokesen.view.ui.draft.fragment.DraftMyRequestFragment;

public class DraftPageAdapter extends FragmentStateAdapter {


    public DraftPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){

            case 0:
                DraftMySIgnatureFragment fragment = new DraftMySIgnatureFragment();
                return fragment;
            case 1:
                DraftMyRequestFragment draftRequestSignature = new DraftMyRequestFragment();
                return draftRequestSignature;

            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
