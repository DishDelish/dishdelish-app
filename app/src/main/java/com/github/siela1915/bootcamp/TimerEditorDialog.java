package com.github.siela1915.bootcamp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.siela1915.bootcamp.UploadRecipe.TextValidator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimerEditorDialog extends DialogFragment {
    private final long interval = 1000;
    private OnMyDialogResult mDialogResult;
    private int currentStepIndex;
    private TimerViewModel timerViewModel;
    private View timerEditorDialogView;
    private CountDownTimer timerOnDisplay;
    private LayoutInflater inflater;
    private final List<CountDownTimer> timerOnDisplayList = new ArrayList<>();

    public TimerEditorDialog() {
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        timerEditorDialogView = inflater.inflate(R.layout.dialog_timer_editor, null);

        timerViewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
        currentStepIndex = getArguments().getInt("index");

        if (timerViewModel.doesTimerExist(currentStepIndex)) {
            // if the specified timer exists enter editing mode
            enterEditMode(currentStepIndex);
        } else {
            // else enter creating mode to add a new timer
            enterCreateMode(currentStepIndex);
        }

        loadTimerList();

        // Inflate and set the layout for the dialog
        builder.setView(timerEditorDialogView)
                .setPositiveButton(R.string.cook_now_timer_close_button, (dialog, id) -> {
                    // cancel all the timers that control display
                    if (timerOnDisplay != null) timerOnDisplay.cancel();
                    clearListOfTimerOnDisplay();

                    mDialogResult.finish();
                });
        return builder.create();
    }

    private void loadTimerList() {
        Map<Integer, CountDownTimerWithPause> timerList = timerViewModel.getTimerList();
        if (!timerList.isEmpty()) {
            LinearLayout timerListWrapper = timerEditorDialogView.findViewById(R.id.timerListContentWrapper);
            timerList.forEach((idx, timer) -> {
                if (idx != currentStepIndex) addTimer(idx, timer, timerListWrapper);
            });
        }
    }

    private void addTimer(Integer index, CountDownTimerWithPause countDownTimerWithPause, LinearLayout timerListWrapper) {
        View timer = inflater.inflate(R.layout.timer_item, null, false);

        ((TextView) timer.findViewById(R.id.timerName)).setText(getResources().getString(R.string.cook_now_timer_item_title) + " " + (index + 1));
        TextView timerRemainingTime = timer.findViewById(R.id.timerRemainingTime);

        // set the display of remaining time under the clock icon
        if (countDownTimerWithPause.ismPaused()) {
            timerRemainingTime.setText(Time.milliSecToString(countDownTimerWithPause.getRemainingTime()));
        } else {
            CountDownTimer timerToBeDisplayedOnList = new CountDownTimer(countDownTimerWithPause.getRemainingTime(), countDownTimerWithPause.getmCountdownInterval()) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timerRemainingTime.setText(Time.milliSecToString(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    timerRemainingTime.setText(R.string.cook_now_timer_finish_message);
                }
            };
            timerToBeDisplayedOnList.start();
            timerOnDisplayList.add(timerToBeDisplayedOnList);
        }

        timerListWrapper.addView(timer);
    }

    private void enterCreateMode(int index) {
        // set title
        ((TextView) timerEditorDialogView.findViewById(R.id.timerCreateTitleName)).setText(getResources().getString(R.string.cook_now_timer_item_title) + " " + (index + 1));

        if (timerOnDisplay != null) timerOnDisplay.cancel();
        timerEditorDialogView.findViewById(R.id.timerEdit).setVisibility(View.GONE);
        timerEditorDialogView.findViewById(R.id.timerCreate).setVisibility(View.VISIBLE);

        ((Button) timerEditorDialogView.findViewById(R.id.startTimer)).setOnClickListener(l -> {
            if (requiredFieldsFilledAndValid()) {
                long time = getTime();
                CountDownTimerWithPause newTimer = new CountDownTimerWithPause(time, interval) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                    }
                };

                timerViewModel.addTimer(index, newTimer);
                newTimer.start();
                enterEditMode(index);
            } else {
                Toast.makeText(requireActivity(), R.string.cook_now_timer_create_invalid_input_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long getTime() {
        long hour = Long.parseLong(((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerHour)).getEditText().getText().toString());
        long min = Long.parseLong(((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerMin)).getEditText().getText().toString());
        long sec = Long.parseLong(((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerSec)).getEditText().getText().toString());
        return hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
    }

    private boolean requiredFieldsFilledAndValid() {
        String hour = ((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerHour)).getEditText().getText().toString();
        String min = ((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerMin)).getEditText().getText().toString();
        String sec = ((TextInputLayout) timerEditorDialogView.findViewById(R.id.timerSec)).getEditText().getText().toString();
        return TextValidator.isTextValid(hour) && TextValidator.isTextValid(min) && TextValidator.isTextValid(sec);
    }

    private void enterEditMode(int index) {
        // set title
        ((TextView) timerEditorDialogView.findViewById(R.id.timerEditTitleName)).setText(getResources().getString(R.string.cook_now_timer_item_title) + " " + (index + 1));

        timerEditorDialogView.findViewById(R.id.timerCreate).setVisibility(View.GONE);
        timerEditorDialogView.findViewById(R.id.timerEdit).setVisibility(View.VISIBLE);

        CountDownTimerWithPause countDownTimerWithPause = timerViewModel.getTimerList().get(index);

        ((Button) timerEditorDialogView.findViewById(R.id.pauseTimer)).setOnClickListener(l -> pauseTimer(index));
        ((Button) timerEditorDialogView.findViewById(R.id.resumeTimer)).setOnClickListener(l -> resumeTimer(index));
        ((Button) timerEditorDialogView.findViewById(R.id.cancelTimer)).setOnClickListener(l -> cancelTimer(index));

        if (countDownTimerWithPause.ismPaused()) {
            setTime(countDownTimerWithPause.getmPauseTime());
        } else {
            timerOnDisplay = createDisplayTimer(countDownTimerWithPause.getRemainingTime(), countDownTimerWithPause.getmCountdownInterval());
        }
    }

    private void cancelTimer(int index) {
        timerViewModel.deleteTimer(index);
        if (timerOnDisplay != null) timerOnDisplay.cancel();
        enterCreateMode(index);
    }

    private void resumeTimer(int index) {
        long remainingTime = timerViewModel.resumeTimer(index);
        if (timerOnDisplay != null) timerOnDisplay.cancel();
        timerOnDisplay = createDisplayTimer(remainingTime, interval);
    }

    private void pauseTimer(int index) {
        long remainingTime = timerViewModel.pauseTimer(index);
        if (timerOnDisplay != null) timerOnDisplay.cancel();
        setTime(remainingTime);
    }

    private void setTime(long remainingTime) {
        Time time = Time.millisecondsToTime(remainingTime);

        ((TextView) timerEditorDialogView.findViewById(R.id.timerHourNumber)).setText(String.valueOf(time.getHour()));
        ((TextView) timerEditorDialogView.findViewById(R.id.timerMinNumber)).setText(String.valueOf(time.getMin()));
        ((TextView) timerEditorDialogView.findViewById(R.id.timerSecNumber)).setText(String.valueOf(time.getSec()));
    }

    private CountDownTimer createDisplayTimer(long time, long interval) {
        CountDownTimer newTimer = new CountDownTimer(time, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                setTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                setTime(0);
            }
        };
        newTimer.start();
        return newTimer;
    }

    private void clearListOfTimerOnDisplay() {
        timerOnDisplayList.forEach(timer -> timer.cancel());
    }
}
