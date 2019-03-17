package com.willowtreeapps.doggogame.ui;

import androidx.annotation.NonNull;

import com.willowtreeapps.doggogame.model.Doggo;

import java.util.List;

public interface Question {

    interface View {
        void setWrongAnswer(int index);

        void setCorrectAnswer(int index);

        void setTitle(String breed);

        void setImage(int index, @NonNull String url);

        void showFirstTryToast(String breed);
    }

    interface Presenter {
        void takeView(View view);

        void checkAnswer(int index);

        int getCorrectIndex();

        void setDoggos(List<Doggo> doggos);

        void pickDoggos();

        void pickCorrectDoggo(int savedIndex);
    }
}
