/*
 * Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 * 
 * http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.simpleworkflow.flow.recipes.retryactivity;

import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClock;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatch;
import com.amazonaws.services.simpleworkflow.flow.interceptors.AsyncExecutor;
import com.amazonaws.services.simpleworkflow.flow.interceptors.AsyncRetryingExecutor;
import com.amazonaws.services.simpleworkflow.flow.interceptors.AsyncRunnable;
import com.amazonaws.services.simpleworkflow.flow.interceptors.ExponentialRetryPolicy;

/**
 * 
 * <b>Name</b>: Retry an Activity using a policy that is not known statically.
 * 
 * <b>Problem</b>: You want to retry an activity if it fails either because of
 * an exception in the activity execution or a timeout, and retry parameters are
 * not known at compile time so @ExponentialRetry Annotation is not an option.
 * 
 * <b>Solution</b>: Use AsyncRetryExecutor to set a retry policy at runtime.
 * 
 * <b>Discussion</b>: The AsyncRetryingExecutor provides a more flexible way for
 * retrying. The constructor of this class accepts a RetryPolicy object that
 * defines the retry policy. The execute method in AsyncRetryingExecutor accepts
 * an object of type AsyncRunnable. The run method of the AsyncRunnable
 * implementation will be retried if it fails. 
 * 
 */
public class AsyncExecutorRetryWorkflowImpl implements RetryWorkflow {

    private final RetryActivitiesClient client = new RetryActivitiesClientImpl();

    private final DecisionContextProvider contextProvider = new DecisionContextProviderImpl();

    private final WorkflowClock clock = contextProvider.getDecisionContext().getWorkflowClock();

    public void process() {

        /**
         * The two parameters(initialRetryIntervalSeconds, maximumAttempts) for
         * retry policy can be setup at runtime instead of compile time. For
         * example, they can be returned from another activity or an asynchronous
         * method, or be passed to the workflow externally.
         */
        long initialRetryIntervalSeconds = 5;
        int maximumAttempts = 5;
        handleUnreliableActivity(initialRetryIntervalSeconds, maximumAttempts);
    }

    /**
     * Use AsyncRetryingExecutor to construct a retry policy at runtime. The run
     * method in the anonymous class of AsyncRunnable will be retried if it
     * fails.
     */
    public void handleUnreliableActivity(long initialRetryIntervalSeconds, int maximumAttempts) {

        ExponentialRetryPolicy retryPolicy = new ExponentialRetryPolicy(initialRetryIntervalSeconds).withMaximumAttempts(maximumAttempts);
        final AsyncExecutor executor = new AsyncRetryingExecutor(retryPolicy, clock);

        // the run method will be retried if some exception throws 
        new TryCatch() {

            @Override
            protected void doTry() throws Throwable {

                executor.execute(new AsyncRunnable() {

                    @Override
                    public void run() throws Throwable {
                        client.unreliableActivity();
                    }
                });
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
            }

        };

    }
}
