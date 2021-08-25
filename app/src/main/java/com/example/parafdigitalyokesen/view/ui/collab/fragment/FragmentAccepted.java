package com.example.parafdigitalyokesen.view.ui.collab.fragment;

import android.os.Bundle;
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
import com.example.parafdigitalyokesen.adapter.DraftListAdapter;
import com.example.parafdigitalyokesen.model.SignModel;

import java.util.List;

public class FragmentAccepted extends Fragment {
    ImageView ivList, ivGrid;
    boolean isGrid = false;
    RecyclerView rvToday;
    List<SignModel> sign;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_child_collab, container, false);
        initComponent(root);
        initSpinner(root);
        initRecyclerView(root);
        return root;
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
        sign = SignModel.generateList();
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


    }
}
