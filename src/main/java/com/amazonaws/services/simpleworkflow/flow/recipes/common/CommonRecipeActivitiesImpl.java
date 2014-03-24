package com.amazonaws.services.simpleworkflow.flow.recipes.common;

import java.util.ArrayList;
import java.util.Random;

import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContext;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * Common activities used in the Recipes package
 * 
 */
public class CommonRecipeActivitiesImpl implements CommonRecipeActivities {
    private ArrayList<String> calls = new ArrayList<String>();
    
    @Asynchronous
    public Promise<ArrayList<String>> getCalls(Promise<?>...waitFor) {
        return Promise.asPromise(calls);
    }

    @Asynchronous
    public Promise<Integer> getCallCount(Promise<?>...waitFor) {
        return Promise.asPromise(calls.size());
    }
    
    public void resetCalls() {
        this.calls = new ArrayList<String>();
    }

    @Override
    public void doNothing() {
        calls.add("doNothing");
    }

    @Override
    public int generateRandomNumber() {
        calls.add("generateRandomNumber");
        return new Random().nextInt();
    }

    @Override
    public boolean generateRandomBoolean() {
        calls.add("generateRandomBoolean");
        return new Random().nextBoolean();
    }

    @Override
    public void delayActivity(long time) {
        calls.add("delayActivity");
        ActivityExecutionContextProvider provider = new ActivityExecutionContextProviderImpl();
        ActivityExecutionContext context = provider.getActivityExecutionContext();
        try {
            while (true) {
                Thread.sleep(time * 1000);
                context.recordActivityHeartbeat(null);
            }
        }
        catch (InterruptedException e) {

        }

    }
}
