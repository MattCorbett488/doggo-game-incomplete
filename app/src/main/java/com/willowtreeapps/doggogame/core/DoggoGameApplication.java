package com.willowtreeapps.doggogame.core;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import com.willowtreeapps.doggogame.network.api.DoggoRepository;
import com.willowtreeapps.doggogame.network.NetworkModule;
import com.willowtreeapps.doggogame.BuildConfig;

import javax.inject.Inject;

public class DoggoGameApplication extends Application {

    private ApplicationComponent component;

    @Inject
    DoggoRepository repository;

    public static DoggoGameApplication get(@NonNull Context context) {
        return (DoggoGameApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
        component.inject(this);
        repository.load();
    }

    public ApplicationComponent component() {
        return component;
    }

    protected ApplicationComponent buildComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule(BuildConfig.BASE_URL))
                .build();
    }
}
