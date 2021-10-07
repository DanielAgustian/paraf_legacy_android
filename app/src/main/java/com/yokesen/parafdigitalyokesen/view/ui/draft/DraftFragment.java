package com.yokesen.parafdigitalyokesen.view.ui.draft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.yokesen.parafdigitalyokesen.adapter.DraftPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yokesen.parafdigitalyokesen.model.GetSignCounterModel;
import com.yokesen.parafdigitalyokesen.model.GetSignatureModel;
import com.yokesen.parafdigitalyokesen.view.ui.SearchActivity;
import com.yokesen.parafdigitalyokesen.viewModel.SignCollabState;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        //tabLayout.getTabAt();

        new TabLayoutMediator(
                tabLayout, vpDraft, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(R.layout.tab_badge);
                 if (position == 0){
                     TextView textView =  tab.getCustomView().findViewById(R.id.tvBadge);
                     textView.setText("5");
                     TextView tvTitle =  tab.getCustomView().findViewById(R.id.tvTabTitle);
                     tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                     tvTitle.setText("My Signature");
                } else if (position == 1){
                     TextView textView =  tab.getCustomView().findViewById(R.id.tvBadge);
                     textView.setText("5");
                     TextView tvTitle =  tab.getCustomView().findViewById(R.id.tvTabTitle);
                     tvTitle.setTextColor(getResources().getColor(R.color.colorGrayText));
                     tvTitle.setText("My Request");
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

        EditText etSearch = root.findViewById(R.id.etSearchSign);
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
        Observable<GetSignCounterModel> getSignatureList = apiInterface.GetSignCounter(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(getActivity(), "ERROR IN FETCHING API My Signature List. Error:"+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetSignCounterModel model) {
        if(model!=null){
            int size = model.getData().getRequest();
            int sizeSign = model.getData().getSignature();
            putSize(0, sizeSign);
            putSize(1, size);
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