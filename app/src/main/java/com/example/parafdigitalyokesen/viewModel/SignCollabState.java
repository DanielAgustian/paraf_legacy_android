package com.example.parafdigitalyokesen.viewModel;

import io.reactivex.subjects.BehaviorSubject;



public class SignCollabState {
    private static final BehaviorSubject<refresh> behaviorSubject
            = BehaviorSubject.create();


    public static BehaviorSubject<refresh> getSubject() {
        return behaviorSubject;
    }
}
