package com.amazonaws.services.simpleworkflow.flow.recipes.retryactivity;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

/**
 * <b>Name</b>: Retry an activity until it succeeds or a maximum number of
 * retries is reached
 * 
 * <b>Problem</b>: You want to retry an activity (or any part of a workflow)
 * that may fail due to ephemeral errors.
 * 
 * <b>Solution</b>: Use {@link TryCatchFinally} to catch the error and set a
 * flag to indicate that an error occured. Use an asynchronous method to retry
 * the failed activity until it succeeds or a maximum number of retry attempts
 * have been made.
 * 
 * <b>Discussion<b/> Since each retry of the activity creates new events in the
 * workflow history, we cap the number of retries to prevent the history from
 * growing too large.
 */
public class RetryActivityWorkflowImpl implements RetryWorkflow {

    private final RetryActivitiesClient client = new RetryActivitiesClientImpl();

    private final int maxRetries = 10;

    private int retryCount;

    @Override
    public void process() {

        final Settable<Boolean> retryActivity = new Settable<Boolean>();

        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                client.unreliableActivity();
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                if (++retryCount <= maxRetries) {
                    retryActivity.set(true);
                }
                else {
                    throw e;
                }
            }

            @Override
            protected void doFinally() throws Throwable {
                if (!retryActivity.isReady()) {
                    retryActivity.set(false);
                }

            }
        };

        //This will call process() to retry the activity if it fails. 
        //doCatch() cannot be cancelled so we don't call process() directly from it  
        restartRunUnreliableActivityTillSuccess(retryActivity);
    }

    @Asynchronous
    private void restartRunUnreliableActivityTillSuccess(Settable<Boolean> retryActivity) {
        if (retryActivity.get()) {
            process();
        }
    }
}
