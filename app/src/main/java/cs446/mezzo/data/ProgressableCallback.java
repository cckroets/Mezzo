package cs446.mezzo.data;

/**
 * @author curtiskroetsch
 */
public interface ProgressableCallback<T> extends Callback<T> {

    void onProgress(float completion);

}
