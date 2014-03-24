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
package com.amazonaws.services.simpleworkflow.flow.recipes.handleerror;

import com.amazonaws.services.simpleworkflow.flow.ActivityTaskFailedException;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.NoWait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatch;

/**
 * 
 * <b>Name</b>: Call one or more activities in case of an error.
 * 
 * <b>Problem</b>: You want to schedule a different block of activities
 * depending on failure type inside your original block of activities.
 * 
 * <b>Solution</b>: Use TryCatch to handle exceptions thrown from asynchronous
 * code. In the doCatch method set a {@link Settable} which is passed to an
 * asynchronous method to start the appropriate activity.
 * 
 * <b>Discussion</b>: Exceptions raised from activities and asynchronous methods
 * cannot be handled using standard Java try-catch-finally blocks. Asynchronous
 * methods and activities return a {@link Promise} immediately and the actual
 * code is executed asynchronously, potentially in a different processes,
 * therefore the caller is not on the stack when the code is actually executed
 * to handle the exception.
 * 
 * The TryCatchFinally class is the equivalent of the try-catch-final block for
 * asynchronous programs. The doTry(), doCatch() and doFinally() methods of this
 * class are similar to the try-catch-finally blocks. Unlike doTry, doCatch and
 * doFinally are not cancellable and in general should not be used to call
 * asynchronous code. This recipes shows a pattern that can be used to call
 * asynchronous methods and activities if an exception is thrown.
 * 
 */

public class HandleErrorWorkflowImpl implements HandleErrorWorkflow {

    private final ResourceManagementActivitiesClient client = new ResourceManagementActivitiesClientImpl();

    /**
     * The code in doCatch() is not cancellable and an attempt to cancel
     * TryCatch (for example by cancelling the workflow execution) will wait
     * until doCatch is complete. Since we want activities to be cancellable,
     * they are called from the "handleException" method which is outside the
     * TryCatch.
     * 
     */
    @Override
    public void startWorkflow() throws Throwable {

        final Settable<Throwable> exception = new Settable<Throwable>();

        final Promise<Integer> resourceId = client.allocateResource();

        new TryCatch() {

            @Override
            protected void doTry() throws Throwable {
                Promise<Void> waitFor = client.useResource(resourceId);
                setState(exception, null, waitFor);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                setState(exception, e, Promise.Void());
            }
        };

        handleException(exception, resourceId);

    }

    @Asynchronous
    public void handleException(Promise<Throwable> ex, Promise<Integer> resourceId) throws Throwable {
        Throwable e = ex.get();
        if (e != null) {
            if (e instanceof ActivityTaskFailedException) {
                //get the original exception thrown from the activity
                Throwable inner = e.getCause();
                //schedule different activities to handle different types of exception
                if (inner instanceof ResourceNoResponseException) {
                    client.reportBadResource(resourceId.get());
                }
                else if (inner instanceof ResourceNotAvailableException) {
                    client.refreshResourceCatalog(resourceId.get());
                }
                else {
                    throw e;
                }
            }
            else {
                throw e;
            }
        }
    }

    @Asynchronous
    public void setState(@NoWait Settable<Throwable> exception, Throwable ex, Promise<Void> WaitFor) {
        exception.set(ex);
    }

}
