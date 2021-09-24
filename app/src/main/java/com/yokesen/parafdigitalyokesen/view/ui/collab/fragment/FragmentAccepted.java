package com.yokesen.parafdigitalyokesen.view.ui.collab.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.Variables;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.DraftListAdapter;
import com.yokesen.parafdigitalyokesen.model.GetSignatureModel;
import com.yokesen.parafdigitalyokesen.model.SignModel;
import com.yokesen.parafdigitalyokesen.viewModel.SignCollabState;
import com.yokesen.parafdigitalyokesen.constant.refresh;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FragmentAccepted extends Fragment {
    ImageView ivList, ivGrid;
    boolean isGrid = false;
    RecyclerView rvToday;
    List<SignModel> sign;
    View root;
    Util util = new Util();
    DisposableObserver disposableRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_child_collab, container, false);


        initSpinner(root);
        initData();
        observe();
        return root;
    }
    private void observe() {
        disposableRefresh = SignCollabState.getSubject().subscribeWith(new DisposableObserver<refresh>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull refresh refresh) {
                if(refresh == com.yokesen.parafdigitalyokesen.constant.refresh.COLLAB_ACC){
                    initData();
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposableRefresh.dispose();
    }
    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();

        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabAcceptedList(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
    private void initDataSort(String sort) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();

        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabAcceptedListSort(token, sort);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
    private void onFailed(Throwable throwable) {
        util.toastError(getContext(),"Collab Request List API", throwable);
    }

    private void onSuccess(GetSignatureModel getSignatureModel) {
        if(getSignatureModel!=null){
            sign = getSignatureModel.getData();
            if(sign.size()> 0){
                initRecyclerView(root);
                initComponent(root);
            }else{
                emptyList(root);
            }

        }
    }
    private void emptyList(View root) {

        rvToday = root.findViewById(R.id.rvListGridDraft);
        LinearLayout llEmptyList = root.findViewById(R.id.llEmptyList);
        rvToday.setVisibility(View.GONE);
        llEmptyList.setVisibility(View.VISIBLE);

    }
    private void initComponent(View root){
        ivList = root.findViewById(R.id.listIconCollab);

        ivGrid = root.findViewById(R.id.gridIconCollab);
        ivList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrid = false;
                ivList.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivGrid.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGrayText), android.graphics.PorterDuff.Mode.MULTIPLY);

                initRecyclerView(root);
            }
        });
        ivGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrid = true;
                ivGrid.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                ivList.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorLightGrayText), android.graphics.PorterDuff.Mode.MULTIPLY);

                initRecyclerView(root);
            }
        });
    }
    private void initRecyclerView(View root){
        rvToday = root.findViewById(R.id.rvListGridDraft);

        FragmentManager fragmentManager = getParentFragmentManager();

        DraftListAdapter adapter = new DraftListAdapter(sign, isGrid, fragmentManager,3);

        //rvToday.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvToday.setLayoutManager(isGrid ? new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL):new LinearLayoutManager(getActivity()));
        rvToday.setAdapter(adapter);
    }
    public void initSpinner(View root){
        String[] spinnerLatestList = new String[]{
                "Recent", "Newest First", "Oldest First", "A-Z", "Z-A"
        };

        Spinner spinnerLatest = root.findViewById(R.id.spinnerLatest);

        ArrayAdapter<String> adapterLatest = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerLatestList);

        adapterLatest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLatest.setAdapter(adapterLatest);
        spinnerLatest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    initData();
                } else{
                    Variables var = new Variables();
                    initDataSort(var.arraySpinner[i-1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
