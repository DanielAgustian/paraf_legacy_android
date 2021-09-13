package com.example.parafdigitalyokesen.view.ui.draft.fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.adapter.DraftListAdapter;
import com.example.parafdigitalyokesen.model.GetSignatureModel;
import com.example.parafdigitalyokesen.model.SignModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DraftMyRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DraftMyRequestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DraftMyRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DraftRequestSignature.
     */
    // TODO: Rename and change types and number of parameters
    public static DraftMyRequestFragment newInstance(String param1, String param2) {
        DraftMyRequestFragment fragment = new DraftMyRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    View root;
    RecyclerView rvToday;
    List<SignModel> sign;
    ImageView ivGrid, ivList;
    boolean isGrid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_today, container, false);
        //initData();
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
        Log.d("HomeTOKEN", token);
        Observable<GetSignatureModel> getSignatureList = apiInterface.getMyReqList(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(getActivity(), "ERROR IN FETCHING API MySigantureList. Error:"+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetSignatureModel model) {
        if(model!=null){
            sign = model.getData();
            initRecyclerView(root);
            initComponent(root);
        }
    }

    private void initComponent(View root){
        ivList = root.findViewById(R.id.listIcon);

        ivGrid = root.findViewById(R.id.gridIcon);
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
        DraftListAdapter adapter = new DraftListAdapter(sign, isGrid, fragmentManager, 1);

        //rvToday.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvToday.setLayoutManager(isGrid ? new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL):new LinearLayoutManager(getActivity()));
        rvToday.setAdapter(adapter);
    }
    public void initSpinner(View root){
        String[] spinnerLatestList = new String[]{
                "Waiting", "Accepted", "Rejected"
        };
        Spinner spinnerLatest = root.findViewById(R.id.spinnerLatest);

        ArrayAdapter<String> adapterLatest = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerLatestList);

        adapterLatest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLatest.setAdapter(adapterLatest);

        spinnerLatest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    initData();
                } else if (i==1){
                    initDataAccepted();
                } else if (i==2){
                    initDataRejected();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                initData();
            }
        });
    }
    private void initDataRejected() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();
        Log.d("HomeTOKEN", token);
        Observable<GetSignatureModel> getSignatureList = apiInterface.getMyReqRejList(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
    private void initDataAccepted() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();
        Log.d("HomeTOKEN", token);
        Observable<GetSignatureModel> getSignatureList = apiInterface.getMyReqAccList(token);
        getSignatureList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
}