package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CookNowStepFragment extends Fragment {
    public CookNowStepFragment () {
        super(R.layout.fragment_cook_now_step);
    }

    public static CookNowStepFragment newInstance(int index, String step) {
        CookNowStepFragment fragment = new CookNowStepFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("step", step);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        int index = requireArguments().getInt("index");
        String step = requireArguments().getString("step");
        //add to the beginning and any line breaks a "bullet point" character
        step = "\u25CF " + step.replaceAll("\n", "\n\u25CF ");

        // load content to layout
        String stepTitle = getResources().getString(R.string.cook_now_step_title) + " " + (index + 1);
        ((TextView) view.findViewById(R.id.cookNowStepTitle)).setText(stepTitle);

        ((TextView) view.findViewById(R.id.cookNowStepContent)).setText(step);
    }
}
