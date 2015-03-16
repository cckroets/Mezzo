package cs446.mezzo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.StatFs;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import cs446.mezzo.R;

/**
 * @author curtiskroetsch
 */
public class SizePickerPreference extends DialogPreference implements NumberPicker.Formatter {

    private static final int MIN_VALUE = 1;
    private static final int DEFAULT_VALUE = 1;
    private static final float SCALE = 1024.f;

    private static final String UNIT = "GB";

    NumberPicker mNumberPicker;
    Integer mCurrentValue;

    public SizePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.pref_size);
    }

    private static float gigabytesAvailable(Context context) {
        final StatFs stat = new StatFs(context.getDir("temp", Context.MODE_PRIVATE).getParent());
        final long bytesAvailable = (long) stat.getBlockSize() * stat.getAvailableBlocks();
        return bytesAvailable / (SCALE * SCALE * SCALE);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.pref_num_picker);
        mNumberPicker.setFormatter(this);
        mNumberPicker.setMinValue(MIN_VALUE);
        mNumberPicker.setMaxValue((int) (gigabytesAvailable(getContext())));
        mNumberPicker.setValue(mCurrentValue);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mCurrentValue = mNumberPicker.getValue();
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    public String format(int value) {
        return value + UNIT;
    }

}
