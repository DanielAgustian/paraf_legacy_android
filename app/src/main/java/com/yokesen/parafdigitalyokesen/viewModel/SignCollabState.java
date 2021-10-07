package com.yokesen.parafdigitalyokesen.viewModel;

import android.content.Context;

import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.refresh;
import com.yokesen.parafdigitalyokesen.model.GetSignatureModel;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.BehaviorSubject;



public class SignCollabState {
    private static final BehaviorSubject<refresh> behaviorSubject
            = BehaviorSubject.create();


    public static BehaviorSubject<refresh> getSubject() {
        return behaviorSubject;
    }

    private static final BehaviorSubject<String> behaviorSubjectDetail
            = BehaviorSubject.create();
    public static BehaviorSubject<String> getSubjectDetail() {
        return behaviorSubjectDetail;
    }

    public static Observable<GetSignatureModel> getCollabWaiting(Context context){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(context);
        String token = preferencesRepo.getToken();
        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabReqList(token);
        return getSignatureList;
    }

    public static Observable<GetSignatureModel> getCollabAccepted(Context context){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(context);
        String token = preferencesRepo.getToken();
        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabAcceptedList(token);
        return getSignatureList;
    }
    public static Observable<GetSignatureModel> getCollabRejected(Context context){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(context);
        String token = preferencesRepo.getToken();
        Observable<GetSignatureModel> getSignatureList = apiInterface.getCollabRejectedList(token);
        return getSignatureList;
    }

}
