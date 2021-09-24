package com.yokesen.parafdigitalyokesen.view.ui.collab;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.adapter.CollabPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yokesen.parafdigitalyokesen.view.ui.SearchActivity;

public class CollabFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 vpCollab;
    private CollabViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(CollabViewModel.class);
        View root = inflater.inflate(R.layout.fragment_collab, container, false);
        tabLayout = root.findViewById(R.id.tabLayoutCollab);
        vpCollab = root.findViewById(R.id.vpCollab);
        vpCollab.setAdapter(new CollabPageAdapter(getActivity()));
        new TabLayoutMediator(
                tabLayout, vpCollab, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0){
                    tab.setText("Requested");
                } else if (position == 1){
                    tab.setText("Accepted");
                } else if (position == 2){
                    tab.setText("Rejected");
                }
            }
        }
        ).attach();
        EditText etSearch = root.findViewById(R.id.etSearchCollab);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}