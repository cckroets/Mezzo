package cs446.mezzo.data;

/**
 * @author curtiskroetsch
 */
public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(Exception e);
}
