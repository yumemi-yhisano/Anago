package io.github.kobakei.anago.viewmodel;

import android.app.Activity;

import javax.inject.Inject;

import io.github.kobakei.anago.activity.SignInActivity;
import io.github.kobakei.anago.usecase.SignOutUseCase;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ホーム画面のビューモデル
 * Created by keisuke on 2016/09/19.
 */

public class HomeViewModel extends ActivityViewModel {

    private final SignOutUseCase signOutUseCase;

    @Inject
    public HomeViewModel(Activity activity, SignOutUseCase signOutUseCase) {
        super(activity);
        this.signOutUseCase = signOutUseCase;
    }

    public void onSignOutClick() {
        Subscription subscription = signOutUseCase.run()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    SignInActivity.startActivity(getActivity());
                });
        getCompositeSubscription().add(subscription);
    }
}
