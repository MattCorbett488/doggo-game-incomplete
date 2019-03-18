package com.willowtreeapps.doggogame.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.willowtreeapps.doggogame.core.DoggoGameApplication;
import com.willowtreeapps.doggogame.model.Doggo;
import com.willowtreeapps.doggogame.network.api.DoggoRepository;
import com.willowtreeapps.doggogame.R;

import java.util.List;

import javax.inject.Inject;

import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

public class DoggoActivity extends AppCompatActivity implements DoggoRepository.DoggoCallback, DoggoFragment.Listener {

    private static final String FRAG_TAG = "NameGameFragmentTag";

    @Inject
    DoggoRepository doggoRepository;

    private View progress;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doggo_activity);
        DoggoGameApplication.get(this).component().inject(this);

        progress = findViewById(R.id.progress);
        container = findViewById(R.id.container);

        progress.setVisibility(VISIBLE);
        container.setVisibility(INVISIBLE);
        initDoggos();
    }

    @Override
    public void onSuccess(List<Doggo> doggos) {
        progress.setVisibility(INVISIBLE);
        container.setVisibility(VISIBLE);
        //TODO: #SET-UP-ACTIVITY
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doggoRepository.clear();
    }

    @Override
    public void onError(@NonNull Throwable error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void initDoggos() {
        //TODO: #SET-UP-ACTIVITY
    }

    private void startRound() {
        if (getSupportFragmentManager().findFragmentByTag(FRAG_TAG) == null) {
            goToNextRound();
        }
    }

    @Override
    public void onRoundComplete() {
        //TODO: #ROUND-COMPLETE
    }

    private void goToNextRound() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DoggoFragment(), FRAG_TAG)
                .commit();
    }
}
