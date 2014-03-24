package com.amazonaws.services.simpleworkflow.flow.recipes.runningmultipleactivities;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.recipes.common.CommonRecipeActivitiesClient;
import com.amazonaws.services.simpleworkflow.flow.recipes.common.CommonRecipeActivitiesClientImpl;

/**
 * <b>Name</b>: Run a fixed number of activities concurrently.
 * 
 * <b>Problem</b>: You want to run a small number of activities in parallel and
 * merge the result when they all complete.
 * 
 * <b>Solution</b>: Call the activity methods and use an asynchronous method to
 * wait for the completion of all activities by passing it the returned
 * {@link Promise} objects as arguments.
 */
public class RunMultipleActivitiesConcurrentlyWorkflowImpl implements RunMultipleActivitiesConcurrentlyWorkflow {

    private final CommonRecipeActivitiesClient client;

    public RunMultipleActivitiesConcurrentlyWorkflowImpl() {
        client = new CommonRecipeActivitiesClientImpl();
    }

    //This constructor is added to enable unit testing by using mocks
    public RunMultipleActivitiesConcurrentlyWorkflowImpl(CommonRecipeActivitiesClient client) {
        this.client = client;
    }

    @Override
    public void runMultipleActivitiesConcurrently() {
        //running first activity
        Promise<Integer> result1 = client.generateRandomNumber();
        //running second activity
        Promise<Integer> result2 = client.generateRandomNumber();
        //join the results
        processResults(result1, result2);
    }

    @Asynchronous
    public void processResults(Promise<Integer> result1, Promise<Integer> result2) {
        if (result1.get() + result2.get() > 5) {
            client.generateRandomNumber();
        }

    }
}
