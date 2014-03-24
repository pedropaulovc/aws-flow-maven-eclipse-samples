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

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;

/**
 * 
 * <b>Name</b>: Handle failure and perform cleanup.
 * 
 * <b>Problem</b>: You want to execute some code to clean up after an activity
 * finishes its execution successfully, due to an error or because of a timeout.
 * 
 * <b>Solution</b>: Use {@link TryCatchFinally} to handle errors and perform
 * clean up.
 * 
 * <b>Discussion</b>: Exceptions raised from activities and asynchronous methods
 * cannot be handled using standard Java try-catch-finally blocks. Asynchronous
 * methods and activities return a {@link Promise} immediately and the actual
 * code is executed asynchronously, potentially in a different processes,
 * therefore the caller is not on the stack when the code is actually executed
 * to handle the exception.
 * 
 * The TryCatchFinally class is the equivalent of the try-catch-final block for
 * asynchronous programs. This recipe demonstrates the use of TryCatchFinally to
 * handle exceptions raised from activities and perform clean up. Exceptions
 * raised by asynchronous code invoked from the doTry() method can be handled in
 * the doCatch() method. The doFinally() method is similar to the finally block
 * and is executed whether or not an exception is thrown.
 * 
 */

public class CleanupResourceWorkflowImpl implements CleanupResourceWorkflow {

    private final ResourceManagementActivitiesClient client = new ResourceManagementActivitiesClientImpl();

    @Override
    public void startWorkflow() {

        final Promise<Integer> resourceId = client.allocateResource();

        /**
         * This TryCatchFinally is a sibling of allocateResource() activity. If
         * allocateResource throws an exception, TryCatchFinally will be
         * cancelled. The semantics of cancellation are as follows: If the doTry
         * method has not been executed when the exception was thrown then the
         * cancellation is immediate and none of doTry, doCatch or doFinally
         * will be executed. If doTry was already executed then all outstanding
         * tasks created in doTry will be cancelled, doCatch will be called with
         * CancellationException and then doFinally will be called.
         */
        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                client.useResource(resourceId);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                //this cannot be canceled
                client.rollbackChanges(resourceId);
            }

            // doFinally() will be executed whether an exception is thrown or not
            @Override
            protected void doFinally() throws Throwable {
                // make sure that the action of clean up will be executed
                if (resourceId.isReady()) {
                    client.cleanUpResource(resourceId);
                }
            }
        };

    }

}
