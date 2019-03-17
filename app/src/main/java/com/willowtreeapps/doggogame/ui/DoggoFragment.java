package com.willowtreeapps.doggogame.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.willowtreeapps.doggogame.core.DoggoGameApplication;
import com.willowtreeapps.doggogame.glide.GlideApp;
import com.willowtreeapps.doggogame.ui.presenter.QuestionPresenter;
import com.willowtreeapps.doggogame.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DoggoFragment extends Fragment implements Question.View {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    private static final Interpolator FAST_OUT_LINEAR_IN = new FastOutLinearInInterpolator();
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final String KEY_DOGGO = "doggo";

    //Animation times for round completion, in milliseconds
    private static final int ANIMATION_DEFAULT_MS = 800;
    private static final int ANIMATION_CORRECT_MS = 1600;
    private static final int ANIMATION_OFFSET_MS = 120;

    private Listener listener;

    @Inject
    QuestionPresenter presenter;

    private TextView title;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>(5);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DoggoGameApplication.get(getActivity()).component().inject(this);

        presenter.takeView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doggo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.title);
        container = view.findViewById(R.id.face_container);

        for (int i = 0; i < container.getChildCount(); i++) {
            final int index = i;
            ImageView face = (ImageView) container.getChildAt(index);
            faces.add(face);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.checkAnswer(index);
                }
            });
        }

        presenter.pickDoggos();
        presenter.pickCorrectDoggo(savedInstanceState == null ? -1 : savedInstanceState.getInt(KEY_DOGGO));

        if (savedInstanceState == null) {
            //Animate in the title
            title.setAlpha(0);
            ObjectAnimator.ofFloat(title, View.ALPHA, 1).start();

            //Animate in each face
            for (int i = 0; i < container.getChildCount(); i++) {
                AnimatorSet set = getAnimateIn(faces.get(i), ANIMATION_DEFAULT_MS + (ANIMATION_OFFSET_MS * i));
                set.setInterpolator(OVERSHOOT);
                set.start();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_DOGGO, presenter.getCorrectIndex());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }

    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }

    @Override
    public void setTitle(String breed) {
        title.setText(getString(R.string.question, breed));
    }

    @Override
    public void setWrongAnswer(int index) {
        removeClickListener(index);

        ObjectAnimator anim = ObjectAnimator.ofInt((ImageView) container.getChildAt(index),
                "colorFilter",
                ContextCompat.getColor(getContext(), R.color.alphaRed));
        anim.setEvaluator(ARGB_EVALUATOR);
        anim.start();
    }

    @Override
    public void setCorrectAnswer(int index) {
        removeAllClickListeners();

        ObjectAnimator anim = ObjectAnimator.ofInt((ImageView) container.getChildAt(index),
                "colorFilter",
                ContextCompat.getColor(getContext(), R.color.alphaGreen));
        anim.setEvaluator(ARGB_EVALUATOR);
        anim.start();

        animateNextRound(index);
    }

    @Override
    public void setImage(int index, @NonNull String url) {
        GlideApp.with(faces.get(index))
                .load(url)
                .placeholder(R.drawable.ic_face_white_48dp)
                .circleCrop()
                .into(faces.get(index));
    }

    @Override
    public void showFirstTryToast(String breed) {
        Toast.makeText(getContext(), getString(R.string.first_try, breed), Toast.LENGTH_SHORT).show();
    }

    private void animateNextRound(int correctIndex) {
        //Animate out the title
        Animator titleAnimator = ObjectAnimator.ofFloat(title, View.ALPHA, 0);
        titleAnimator.setStartDelay(ANIMATION_DEFAULT_MS);
        titleAnimator.start();

        //Animate each face
        for (int i = 0; i < container.getChildCount(); i++) {
            if (i != correctIndex) {
                getAnimateOut(faces.get(i), ANIMATION_DEFAULT_MS + (ANIMATION_OFFSET_MS * i)).start();
            } else {
                AnimatorSet set = getAnimateOut(faces.get(i), ANIMATION_CORRECT_MS + (ANIMATION_OFFSET_MS * i));
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (listener != null) {
                            listener.onRoundComplete();
                        }
                    }
                });
                set.start();
            }
        }
    }

    private AnimatorSet getAnimateOut(View view, int delayTime) {
        AnimatorSet set = new AnimatorSet();

        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0);

        set.playTogether(scaleX, scaleY);
        set.setStartDelay(delayTime);
        set.setInterpolator(FAST_OUT_LINEAR_IN);

        return set;
    }

    private AnimatorSet getAnimateIn(View view, int delayTime) {
        AnimatorSet set = new AnimatorSet();

        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1);

        set.playTogether(scaleX, scaleY);
        set.setStartDelay(delayTime);
        set.setInterpolator(FAST_OUT_LINEAR_IN);

        return set;
    }
    private void removeClickListener(int index) {
        faces.get(index).setOnClickListener(null);
    }

    private void removeAllClickListeners() {
        for (ImageView face : faces) {
            face.setOnClickListener(null);
        }
    }

    public interface Listener {
        void onRoundComplete();
    }
}
