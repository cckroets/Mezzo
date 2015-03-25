package cs446.mezzo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.Calendar;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.TimeoutSetEvent;
import roboguice.inject.InjectView;


/**
 * Created by ulkarakhundzada on 2015-03-16.
 */

public class TimePickerPreference extends DialogPreference {

    private int mLastHour;
    private int mLastMinute;
    private TimePicker mPicker;

    public TimePickerPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    public long getInMilliseconds() {
        return ((mLastHour * 3600) + (mLastMinute * 60)) * 1000;
    }

    public static int getHour(String time) {
        final String[] pieces = time.split(":");

        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        final String[] pieces = time.split(":");

        return Integer.parseInt(pieces[1]);
    }

    @Override
    protected View onCreateDialogView() {
        mPicker = new TimePicker(getContext());
        mPicker.setIs24HourView(true);
        mPicker.setCurrentHour(mLastHour);
        mPicker.setCurrentMinute(mLastMinute);
//        // I want to activate minute picker by default. But findViewById() is throwing exception.
//        int id = Resources.getSystem().getIdentifier("android:id/minutes", null, null);
//        NumberPicker minutePicker = (NumberPicker) mPicker.findViewById(id);
//        minutePicker.performClick();
        return mPicker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            mLastHour = mPicker.getCurrentHour();
            mLastMinute = mPicker.getCurrentMinute();

            final String time = String.valueOf(mLastHour) + ":" + String.valueOf(mLastMinute);

            if (callChangeListener(time)) {
                persistString(time);
            }

            EventBus.post(new TimeoutSetEvent(getInMilliseconds()));
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        final String time = getPersistedString("00:30");

        mLastHour = getHour(time);
        mLastMinute = getMinute(time);
    }
}