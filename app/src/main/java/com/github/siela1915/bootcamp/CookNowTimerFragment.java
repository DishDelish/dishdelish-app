package com.github.siela1915.bootcamp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class CookNowTimerFragment extends Fragment {
    private TimerViewModel timerViewModel;
    private int stepIndex;
    private CountDownTimer timerOnDisplay;

    public CookNowTimerFragment() {
        super(R.layout.fragment_cook_now_timer_trigger);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
        stepIndex = requireArguments().getInt("index");

        // set on click listener
        ((ImageView) view.findViewById(R.id.cookNowTimerTrigger)).setOnClickListener(v -> {
            // cancel timer display before open the editor dialog
            if (timerOnDisplay != null) {
                timerOnDisplay.cancel();
            }
            clearTimeDisplay(view);
            openTimerEditor(view);
        });

        setTimerDisplay(view, stepIndex);
    }

    private void setTimerDisplay(View view, int stepIndex) {
        clearTimeDisplay(view);

        if (timerViewModel.doesTimerExist(stepIndex)) {
            TextView remainingTime = view.findViewById(R.id.cookNowTimerRemainingTime);
            CountDownTimerWithPause countDownTimerWithPause = timerViewModel.getTimerList().get(stepIndex);

            // set the display of remaining time under the clock icon
            if (countDownTimerWithPause.isPaused()) {
                remainingTime.setText(Time.milliSecToString(countDownTimerWithPause.getPauseTime()));
            } else {
                timerOnDisplay = new CountDownTimer(countDownTimerWithPause.getRemainingTime(), countDownTimerWithPause.getCountdownInterval()) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        remainingTime.setText(Time.milliSecToString(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        remainingTime.setText(R.string.cook_now_timer_finish_message);
                    }
                };
                timerOnDisplay.start();
            }
        }
    }

    private void clearTimeDisplay(View view) {
        TextView remainingTime = view.findViewById(R.id.cookNowTimerRemainingTime);
        remainingTime.setText(null);
    }

    private void openTimerEditor(View view) {
        TimerEditorDialog timerEditorDialog = new TimerEditorDialog();
        timerEditorDialog.setDialogResult(() -> {
            setTimerDisplay(view, stepIndex);
        });
        String timerEditorTag = "cook_now_timer_editor_dialog";
        Bundle bundle = new Bundle();
        bundle.putInt("index", stepIndex);
        timerEditorDialog.setArguments(bundle);
        timerEditorDialog.show(getActivity().getSupportFragmentManager(), timerEditorTag);
    }
}
