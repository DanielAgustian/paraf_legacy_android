package com.yokesen.parafdigitalyokesen.viewModel;

import io.reactivex.subjects.BehaviorSubject;

public class SecurityState {
    private static final BehaviorSubject<String> behaviorSubjectPasscode
            = BehaviorSubject.create();


    public static BehaviorSubject<String> getSubject() {
        return behaviorSubjectPasscode;
    }
}
