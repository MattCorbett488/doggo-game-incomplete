package com.willowtreeapps.doggogame.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.squareup.moshi.Moshi;
import com.willowtreeapps.doggogame.network.api.DoggoApi;
import com.willowtreeapps.doggogame.network.api.DoggoRepository;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class NetworkModule {

    public static final String NETWORK_CACHE = "network_cache";

    private static final int GLOBAL_TIMEOUT = 30; // seconds

    @NonNull
    private final HttpUrl endpoint;

    public NetworkModule(@NonNull String endpoint) {
        this(HttpUrl.parse(endpoint));
    }

    public NetworkModule(@NonNull HttpUrl endpoint) {
        this.endpoint = endpoint;
    }

    @Provides @NonNull @Singleton
    public HttpUrl provideEndpoint() {
        return this.endpoint;
    }

    @Provides @NonNull @Singleton @Named(NETWORK_CACHE)
    public File provideNetworkCacheDirectory(@NonNull Context context) {
        return context.getDir(NETWORK_CACHE, Context.MODE_PRIVATE);
    }

    @Provides @NonNull @Singleton
    public Cache provideNetworkCache(@NonNull @Named(NETWORK_CACHE) File cacheDir) {
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
        return new Cache(cacheDir, cacheSize);
    }

    @Provides @NonNull @Singleton
    public OkHttpClient provideHttpClient(@NonNull Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(GLOBAL_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(GLOBAL_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(GLOBAL_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    @Provides @NonNull @Singleton
    public DoggoApi provideDoggoApi(@NonNull Moshi moshi, @NonNull OkHttpClient client, @NonNull HttpUrl url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(DoggoApi.class);
    }

    @Provides @NonNull @Singleton
    public Moshi providesMoshi() {
        return new Moshi.Builder().build();
    }

    @Provides @NonNull @Singleton
    public DoggoRepository provideDoggoRepository(@NonNull DoggoApi api) {
        return new DoggoRepository(api);
    }

}
