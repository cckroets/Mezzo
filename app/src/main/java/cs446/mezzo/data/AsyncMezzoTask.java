package cs446.mezzo.data;

import android.os.AsyncTask;

/**
 * @author curtiskroetsch
 */
public abstract class AsyncMezzoTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    Callback<Result> mCallback;
    Exception mException;

    public AsyncMezzoTask(Callback<Result> callback) {
        mCallback = callback;
    }

    protected void setException(Exception e) {
        mException = e;
    }

    public Callback<Result> getCallback() {
        return mCallback;
    }
    public void setCallback(Callback<Result> callback) { mCallback = callback; }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            mCallback.onSuccess(result);
        } else if (mCallback != null) {
            mCallback.onFailure(mException);
        }
    }
}
