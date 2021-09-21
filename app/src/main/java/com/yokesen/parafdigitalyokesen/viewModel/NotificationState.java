package com.yokesen.parafdigitalyokesen.viewModel;

import io.reactivex.subjects.BehaviorSubject;

public final class NotificationState {

    private static final BehaviorSubject<String> behaviorSubject
            = BehaviorSubject.create();


    public static BehaviorSubject<String> getSubject() {
        return behaviorSubject;
    }
}
