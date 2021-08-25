package com.example.parafdigitalyokesen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.parafdigitalyokesen.view.ui.collab.fragment.FragmentAccepted;
import com.example.parafdigitalyokesen.view.ui.collab.fragment.FragmentRejected;
import com.example.parafdigitalyokesen.view.ui.collab.fragment.FragmentRequested;

public class CollabPageAdapter extends FragmentStateAdapter {
    public CollabPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                FragmentRequested frag = new FragmentRequested();
                return frag;
            case 1:
                FragmentAccepted fragment = new FragmentAccepted();
                return fragment;
            case 2:
                FragmentRejected fragment2 = new FragmentRejected();
                return fragment2;

            default:
                return null;

        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
