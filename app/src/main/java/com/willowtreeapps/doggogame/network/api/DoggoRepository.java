package com.willowtreeapps.doggogame.network.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.willowtreeapps.doggogame.model.Doggo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoggoRepository {

    @NonNull
    private final DoggoApi api;
    @Nullable
    private List<Doggo> doggos;
    private DoggoCallback callback;

    public DoggoRepository(@NonNull DoggoApi api) {
        this.api = api;
    }


    public void clear() {
        callback = null;
    }

    public void load() {
        if (doggos == null) {
            doggos = new ArrayList<>();
            fetchDoggos();
        }
    }

    @Nullable
    public List<Doggo> getDoggos() {
        return doggos;
    }

    public boolean hasDoggos() {
        return doggos != null && !doggos.isEmpty();
    }

    public void setCallback(DoggoCallback callback) {
        this.callback = callback;
    }

    private void fetchDoggos() {
        api.getBreeds().enqueue(new Callback<DoggoBreedResponse>() {
            @Override
            public void onResponse(Call<DoggoBreedResponse> call, Response<DoggoBreedResponse> response) {
                //TODO: #FIX-API
            }

            @Override
            public void onFailure(Call<DoggoBreedResponse> call, Throwable t) {
                if (callback != null) {
                    callback.onError(t);
                }
            }
        });
    }

    private void getImages(final List<String> breedNames) {
        final List<Doggo> doggoList = new ArrayList<>();
        for (int i = 0; i < breedNames.size(); i++) {
            final String breedName = breedNames.get(i);
            final int currentIndex = i;
            api.getDoggoImage(breedName).enqueue(new Callback<DoggoImage>() {
                @Override
                public void onResponse(Call<DoggoImage> call, Response<DoggoImage> response) {
                    if (response.isSuccessful()) {
                        //Take the response and build our doggo, adding it to our list
                        DoggoImage image = response.body();
                        if (image != null && image.getStatus().equals(DoggoImage.SUCCESS)) {
                            doggoList.add(new Doggo(breedName, image.getUrl()));
                        }
                        //Last image means we have all our doggos
                        if (currentIndex == (breedNames.size() - 1) && callback != null) {
                            doggos = doggoList;
                            callback.onSuccess(doggos);
                        }
                        return;
                    }
                    onFailure(call, getError(response, "Error getting image"));
                }

                @Override
                public void onFailure(Call<DoggoImage> call, Throwable t) {
                    if (callback != null) {
                        callback.onError(t);
                    }
                }
            });
        }
    }

    private Throwable getError(Response<?> response, String defaultMessage) {
        try {
            return new RuntimeException(response.errorBody().string());
        } catch (IOException | NullPointerException e) {
            return new RuntimeException(defaultMessage);
        }
    }

    public interface DoggoCallback {
        void onSuccess(List<Doggo> doggos);
        void onError(Throwable error);
    }
}
