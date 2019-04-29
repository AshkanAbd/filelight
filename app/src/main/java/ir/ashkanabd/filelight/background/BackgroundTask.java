package ir.ashkanabd.filelight.background;

import android.os.AsyncTask;

public class BackgroundTask extends AsyncTask<Void, Void, Void> {
    private PreExecute preExecute;
    private TaskExecute<Void> taskExecute;
    private PostExecute<Void> postExecute;
    private TaskUpdate<Void> taskUpdate;

    @Override
    protected void onPreExecute() {
        if (preExecute != null)
            preExecute.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (taskExecute != null)
            taskExecute.execute(voids);
        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        if (postExecute != null)
            postExecute.execute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        if (taskUpdate != null)
            taskUpdate.update(values);
    }

    public void setPreExecute(PreExecute preExecute) {
        this.preExecute = preExecute;
    }

    public void setTaskExecute(TaskExecute<Void> taskExecute) {
        this.taskExecute = taskExecute;
    }

    public void setPostExecute(PostExecute<Void> postExecute) {
        this.postExecute = postExecute;
    }

    public void setTaskUpdate(TaskUpdate<Void> taskUpdate) {
        this.taskUpdate = taskUpdate;
    }
}
