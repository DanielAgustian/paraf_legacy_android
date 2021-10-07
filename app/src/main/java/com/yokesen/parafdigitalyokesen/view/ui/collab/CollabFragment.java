package com.yokesen.parafdigitalyokesen.view.ui.collab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.adapter.CollabPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yokesen.parafdigitalyokesen.model.GetCollabCounterModel;
import com.yokesen.parafdigitalyokesen.model.GetSignCounterModel;
import com.yokesen.parafdigitalyokesen.view.ui.SearchActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
                    tab.setCustomView(R.layout.tab_badge);
                    TextView textView =  tab.getCustomView().findViewById(R.id.tvBadge);
                    textView.setText("5");
                    TextView tvTitle =  tab.getCustomView().findViewById(R.id.tvTabTitle);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvTitle.setText("Requested");
                } else if (position == 1){
                    tab.setCustomView(R.layout.tab_badge);
                    TextView textView =  tab.getCustomView().findViewById(R.id.tvBadge);
                    textView.setText("1");
                    TextView tvTitle=  tab.getCustomView().findViewById(R.id.tvTabTitle);
                    tvTitle.setText("Accepted");
                } else if (position == 2){
                    tab.setCustomView(R.layout.tab_badge);
                    TextView textView =  tab.getCustomView().findViewById(R.id.tvBadge);
                    textView.setText("3");
                    TextView tvTitle =  tab.getCustomView().findViewById(R.id.tvTabTitle);
                    tvTitle.setText("Rejected");
                }
            }
        }
        ).attach();
        getCounter();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View selected = tab.getCustomView();
                TextView tvTitle = selected.findViewById(R.id.tvTabTitle);
                tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View selected = tab.getCustomView();
                TextView tvTitle = selected.findViewById(R.id.tvTabTitle);
                tvTitle.setTextColor(getResources().getColor(R.color.colorGrayText));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    private void getCounter() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();
        Log.d("HomeTOKEN", token);
        Observable<GetCollabCounterModel> getSignatureList = apiInterface.GetCollabCounter(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(getActivity(), "ERROR IN FETCHING API My Signature List. Error:"+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetCollabCounterModel model) {
        if(model!=null){
            int accepted = model.getData().getAccepted();
            int requested = model.getData().getRequested();
            int rejected = model.getData().getRejected();
            putSize(0, requested);
            putSize(1, accepted);
            putSize(2, rejected);
        }
    }
    private void putSize(int tabPosition, int size){
        View tabViewSign = tabLayout.getTabAt(tabPosition).getCustomView();
        TextView tvSign = tabViewSign.findViewById(R.id.tvBadge);
        String valueSign = String.valueOf(size);
        tvSign.setText(valueSign);
        if(size< 1){
            LinearLayout llBadge = tabViewSign.findViewById(R.id.llBadge);
            llBadge.setVisibility(View.GONE);
        }
    }
}