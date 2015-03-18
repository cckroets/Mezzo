package cs446.mezzo.view;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cs446.mezzo.R;
import cs446.mezzo.data.Callback;
import cs446.mezzo.injection.Injector;
import cs446.mezzo.metadata.art.AlbumArtManager;
import cs446.mezzo.music.Song;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class MezzoImageView extends FrameLayout {

    @InjectView(R.id.ph_image)
    ImageView mImageView;

    @InjectView(R.id.ph_text)
    AutoResizeTextView mTextView;

    public MezzoImageView(Context context) {
        super(context);
        init();
    }

    public MezzoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MezzoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_placeholder_imageview, this);
        Injector.injectViews(this, this);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setText(CharSequence charSequence) {
        mTextView.setBackgroundColor(getColor(charSequence));
        mTextView.setText(charSequence);
    }

    public void bindWithSong(AlbumArtManager manager, Song song) {
        setText(shorten(song.getTitle()));
        manager.setAlbumArt(mImageView, song);
    }

    public void bindWithSong(AlbumArtManager manager, Song song, Callback<Palette> paletteCallback) {
        setText(shorten(song.getTitle()));
        manager.setAlbumArt(mImageView, song, paletteCallback);
    }

    private int getColor(CharSequence string) {
        final int[] colors = getResources().getIntArray(R.array.placeholders);
        return colors[Math.abs(string.hashCode()) % colors.length];
    }

    private String shorten(String string) {
        return string;/*
        final StringBuilder builder = new StringBuilder();
        for (String word : string.split(" ")) {
            final char firstChar = word.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                builder.append(firstChar);
            }
        }
        return builder.toString();*/
    }
}
