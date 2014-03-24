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

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

/**
 * 
 * <b>Name</b>: Retrying an Activity with a custom retry policy
 * 
 * <b>Problem</b>: You want to retry an activity if it fails to finish the
 * execution either because of an exception during the activity execution or a
 * timeout, and there is some special needs to control retry logic beyond what
 * can be accomplished by the built in RetryExecutor.
 * 
 * <b>Solution</b>: Use {@link TryCatchFinally} to catch exceptions and retry
 * the activity using custom logic.
 * 
 * <b>Discussion</b>: Using the built-in retry support you can retry failed
 * activities by specifying a retry policy. If the built in policy does not
 * suffice for your use case, then you should use {@link TryCatchFinally} which
 * provides a generic mechanism for error handling in asynchronous programs.
 * 
 */

public class CustomLogicRetryWorkflowImpl implements RetryWorkflow {

    private final RetryActivitiesClient client = new RetryActivitiesClientImpl();

    public void process() {
        callActivityWithRetry();
    }

    /**
     * If some exception is thrown in doTry(), the doCatch method will be
     * executed. Otherwise, the doCatch method will be ignored
     */
    @Asynchronous
    public void callActivityWithRetry() {

        final Settable<Throwable> failure = new Settable<Throwable>();
        new TryCatchFinally() {

            protected void doTry() throws Throwable {
                client.unreliableActivity();
            }

            /**
             * The code in doCatch is not cancellable. If we call method to
             * retry from doCatch, then in case of workflow cancellation there
             * will be no attempt to cancel the retried method. To ensure that
             * cancellation is always happening, the recursive retry is moved
             * out outside of TryCatch.
             */
            protected void doCatch(Throwable e) {
                failure.set(e);
            }

            protected void doFinally() throws Throwable {
                if (!failure.isReady()) {
                    failure.set(null);
                }
            }

        };

        retryOnFailure(failure);
    }

    @Asynchronous
    private void retryOnFailure(Promise<Throwable> failureP) {
        Throwable failure = failureP.get();
        if (failure != null && shouldRetry(failure)) {
            callActivityWithRetry();
        }
    }

    protected Boolean shouldRetry(Throwable e) {
        //custom logic to decide to retry the activity or not
        return true;
    }

}
