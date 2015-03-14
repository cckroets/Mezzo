package cs446.mezzo.data;

/**
 * @author curtiskroetsch
 */
public abstract class SimpleAsyncTask<Result> extends AsyncMezzoTask<Void, Void, Result> {

    public SimpleAsyncTask(Callback<Result> callback) {
        super(callback);
    }

    @Override
    protected final Result doInBackground(Void... params) {
        return doInBackground();
    }

    public abstract Result doInBackground();
}
