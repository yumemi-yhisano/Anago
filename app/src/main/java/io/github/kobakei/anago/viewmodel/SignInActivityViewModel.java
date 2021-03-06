package io.github.kobakei.anago.viewmodel;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import io.github.kobakei.anago.activity.BaseActivity;
import io.github.kobakei.anago.activity.HomeActivity;
import io.github.kobakei.anago.entity.Error;
import io.github.kobakei.anago.usecase.CheckSessionUseCase;
import io.github.kobakei.anago.usecase.SignInUseCase;
import io.github.kobakei.anago.util.NetUtil;
import io.github.kobakei.anago.viewmodel.base.ActivityViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * サインイン画面ビューモデル
 * Created by keisuke on 2016/09/18.
 */

public class SignInActivityViewModel extends ActivityViewModel {

    // 使用するユースケース
    private final SignInUseCase signInUseCase;
    private final CheckSessionUseCase checkSessionUseCase;

    // この画面の状態を表す変数
    public ObservableField<String> name;
    public ObservableField<String> password;
    public ObservableBoolean buttonEnabled;

    @Inject
    public SignInActivityViewModel(BaseActivity activity, SignInUseCase signInUseCase,
                                   CheckSessionUseCase checkSessionUseCase) {
        super(activity);

        this.signInUseCase = signInUseCase;
        this.checkSessionUseCase = checkSessionUseCase;

        name = new ObservableField<>();
        password = new ObservableField<>();
        buttonEnabled = new ObservableBoolean(false);

        checkSessionUseCase.run()
                .compose(bindToLifecycle().forSingle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(authToken -> {
                    Timber.v("Check session: " + authToken.token);
                    Toast.makeText(getContext(), "Already signed in", Toast.LENGTH_SHORT).show();
                    goToNext();
                }, Timber::e);
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Timber.v("text changed");
        buttonEnabled.set(!TextUtils.isEmpty(name.get()) && !TextUtils.isEmpty(password.get()));
    }

    public void onButtonClick(View view) {
        Timber.v("Button clicked. name=" + name.get());

        signInUseCase.run(name.get(), password.get())
                .compose(getActivity().bindToLifecycle().forSingle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(authToken -> {
                    Toast.makeText(getContext(), "Token: " + authToken.token, Toast.LENGTH_SHORT).show();
                    goToNext();
                }, throwable -> {
                    Timber.e(throwable);
                    Error error = NetUtil.convertError(throwable);
                    if (error != null) {
                        Toast.makeText(getContext(), error.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToNext() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        getContext().startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }
}
