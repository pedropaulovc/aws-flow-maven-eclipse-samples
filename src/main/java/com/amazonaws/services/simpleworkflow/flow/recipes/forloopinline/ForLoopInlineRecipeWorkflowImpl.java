package com.amazonaws.services.simpleworkflow.flow.recipes.forloopinline;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.recipes.common.CommonRecipeActivitiesClient;
import com.amazonaws.services.simpleworkflow.flow.recipes.common.CommonRecipeActivitiesClientImpl;

/**
 * <b>Name</b>: Run an activity repeatedly for a fixed number of times
 * 
 * <b>Problem</b>: You want to execute an activity repeatedly for a fixed number
 * of times, which is known at compile time.
 * 
 * <b>Solution</b>: Use a Java for loop to call the activity for the desired
 * number of times. Pass the {@link Promise} returned from the previous
 * invocation as an argument to the activity to ensure that it is executed only
 * after the previous invocation has completed.
 */
public class ForLoopInlineRecipeWorkflowImpl implements ForLoopInlineRecipeWorkflow {

    private final CommonRecipeActivitiesClient client;

    public ForLoopInlineRecipeWorkflowImpl() {
        client = new CommonRecipeActivitiesClientImpl();
    }

    //This constructor is added to enable unit testing by using mocks
    public ForLoopInlineRecipeWorkflowImpl(CommonRecipeActivitiesClient client) {
        this.client = client;
    }

    @Override
    public void loop(int n) {
        if (n > 0) {
            Promise<Void> hasRun = Promise.Void();
            for (int i = 0; i < n; i++) {
                hasRun = client.doNothing(hasRun);
            }
        }
    }
}
