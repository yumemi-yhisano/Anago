package io.github.kobakei.anago;

import android.app.Application;

import io.github.kobakei.anago.di.AppComponent;
import io.github.kobakei.anago.di.AppModule;
import io.github.kobakei.anago.di.DaggerAppComponent;

/**
 * Created by keisuke on 2016/09/18.
 */

public class AnagoApplication extends Application {

    private AppComponent injector;

    @Override
    public void onCreate() {
        super.onCreate();

        injector = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getInjector() {
        return injector;
    }
}
