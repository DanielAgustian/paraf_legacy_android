package com.yokesen.parafdigitalyokesen.viewModel;

import com.yokesen.parafdigitalyokesen.constant.refresh;

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

}
