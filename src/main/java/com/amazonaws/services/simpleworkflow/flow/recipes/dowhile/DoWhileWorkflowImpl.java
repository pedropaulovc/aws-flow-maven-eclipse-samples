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
package com.amazonaws.services.simpleworkflow.flow.recipes.dowhile;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * 
 * <b>Name</b>:Do-while loop
 * 
 * <b>Problem</b>: You want to execute one or more activities repeatedly while
 * a condition holds true.
 * 
 * <b>Solution</b>: Use mutually recursive asynchronous methods to implement an
 * asynchronous do-while loop.
 * 
 * <b>Discussion</b>: In asynchronous programs recursion is used to implement
 * loops that execute each iteration after the previous one has completed. In
 * this recipe, an asynchronous method calls an activity and passes the
 * {@link Promise} returned by it to another asynchronous method. The second
 * method tests the loop condition, and if it is true, calls the first method
 * again. Since an asynchronous method is executed only after all its
 * {@link Promise} arguments are ready, each iteration will wait until the
 * previous one has completed. The recursion stops after the condition is no
 * longer true.
 * 
 */

public class DoWhileWorkflowImpl implements DoWhileWorkflow {

    private final DoWhileActivitiesClient client = new DoWhileActivitiesClientImpl();

    @Override
    public void doWhile() {
        doBody();
    }

    @Asynchronous
    private void doBody() {
        Promise<Integer> bodyResult = client.getRandomNumber();
        whileNext(bodyResult);
    }

    @Asynchronous
    private void whileNext(Promise<Integer> bodyResult) {
        if (bodyResult.get() >= 1) {
            doBody();
        }
    }
}
