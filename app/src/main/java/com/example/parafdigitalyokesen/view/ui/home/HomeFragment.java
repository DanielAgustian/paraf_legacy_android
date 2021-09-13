package com.example.parafdigitalyokesen.view.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.GetHomeModel;
import com.example.parafdigitalyokesen.model.StatHomeModel;
import com.example.parafdigitalyokesen.view.ui.NotificationListActivity;
import com.example.parafdigitalyokesen.view.ui.SearchActivity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    TextView tvToday, tvAccepted, tvRejected, tvRequest;
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        initComponent(root);
        initHomeState();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initHomeState();
    }

    private void initHomeState() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getActivity());

        String token = preferencesRepo.getToken();
        Log.d("HomeTOKEN", token);
        Observable<GetHomeModel> callHome = apiInterface.getHomeStat(token);
        callHome.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(getActivity(), "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetHomeModel model) {
        if(model!=null){
            Log.d("HomeState", model.getHome().getToday());
            tvAccepted.setText(model.getHome().getAccepted());
            tvToday.setText(model.getHome().getToday());
            tvRequest.setText(model.getHome().getToday());
            tvRejected.setText(model.getHome().getRejected());
        } else{
            Log.d("Error", "Empty Model");
        }
    }

    @Override
    public void onClick(View view) {
        int numeric = view.getId();
        if(numeric == R.id.etSearchHome){
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        } else if (numeric == R.id.flNotif){
            Intent intent = new Intent(getActivity(), NotificationListActivity.class);
            startActivity(intent);
        }
    }

    void initComponent(View root){
        tvAccepted = root.findViewById(R.id.tvAccepted);
        tvRejected = root.findViewById(R.id.tvRejected);
        tvRequest = root.findViewById(R.id.tvRequest);
        tvToday = root.findViewById(R.id.tvToday);


        EditText etSearch = root.findViewById(R.id.etSearchHome);
        etSearch.setOnClickListener(this);
        FrameLayout flNotif = root.findViewById(R.id.flNotif);
        flNotif.setOnClickListener(this);

    }

}