package com.example.parafdigitalyokesen.view.ui.collab.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.adapter.DraftListAdapter;
import com.example.parafdigitalyokesen.model.GetSignatureModel;
import com.example.parafdigitalyokesen.model.SignModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FragmentRequested extends Fragment {
    ImageView ivList, ivGrid;
    boolean isGrid = false;
    RecyclerView rvToday;
    List<SignModel> sign;
    View root;
    Util util = new Util();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_child_collab, container, false);

        initData();
        initSpinner(root);
        return root;
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

        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabReqList(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        util.toastError(getContext(),"Collab Request List API", throwable);
    }

    private void onSuccess(GetSignatureModel getSignatureModel) {
        if(getSignatureModel!=null){
            sign = getSignatureModel.getData();
            initRecyclerView(root);
            initComponent(root);
        }
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
        DraftListAdapter adapter = new DraftListAdapter(sign, isGrid, fragmentManager, 2);

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


    }
}
