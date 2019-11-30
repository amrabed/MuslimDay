package amrabed.android.release.evaluation.edit;

import amrabed.android.release.evaluation.data.entities.Task;

public class Modification {
    static final int ADD = 0;
    static final int UPDATE = 1;
    static final int DELETE = 2;

    public final Task task;
    final int operation;

    Modification(Task task, int operation) {
        this.task = task;
        this.operation = operation;
    }
}
