package com.yokesen.parafdigitalyokesen.view.ui.draft;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.adapter.DraftPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DraftFragment extends Fragment {

    private DraftViewModel dashboardViewModel;
    ViewPager2 vpDraft;
    TabLayout tabLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DraftViewModel.class);
        View root = inflater.inflate(R.layout.fragment_draft, container, false);
        tabLayout = root.findViewById(R.id.tabLayoutDraft);

        vpDraft = root.findViewById(R.id.vpDraft);
        vpDraft.setAdapter(new DraftPageAdapter(getActivity()));
        new TabLayoutMediator(
                tabLayout, vpDraft, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                 if (position == 0){
                    tab.setText("My Signature");
                } else if (position == 1){
                    tab.setText("My Request");
                }
            }
        }
        ).attach();

        return root;
    }
}