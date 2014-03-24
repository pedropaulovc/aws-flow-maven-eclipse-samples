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
package com.amazonaws.services.simpleworkflow.flow.recipes.conditionloop;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * 
 * <b>Name</b>: Execute an activity for a fixed number of times.
 * 
 * <b>Problem</b>: You want to repeatedly execute one or more activities a fixed
 * number of times but you do not know the number of times to execute in
 * advance.
 * 
 * <b>Solution</b>: Use a recursive asynchronous method to execute an activity
 * repeatedly until a condition has been satisfied.
 * 
 * <b>Discussion</b>: In asynchronous programs recursion is used to implement
 * loops that execute each iteration after the previous one has completed. In
 * this recipe, the number of iterations is determined by calling an activity.
 * This activity returns a {@link Promise <Integer>} that is passed to an
 * asynchronous method which first calls the desired activity and then calls
 * itself passing the {@link Promise} returned by the activity as an argument
 * along with the count of remaining iterations. Since an asynchronous method is
 * executed only after all its {@link Promise} arguments are ready, each
 * iteration will wait until the previous one has completed. The recursion stops
 * after the desired number of iterations.
 * 
 */

public class ConditionalLoopWorkflowImpl implements ConditionalLoopWorkflow {

    private final ConditionalLoopActivitiesClient client = new ConditionalLoopActivitiesClientImpl();

    @Override
    public void startWorkflow() {
        Promise<Integer> recordCount = client.getRecordCount();
        processRecords(recordCount);
    }

    @Asynchronous
    public void processRecords(Promise<Integer> recordCount) {
        processRecords(recordCount.get());
    }

    @Asynchronous
    public void processRecords(int records, Promise<?>... waitFor) {
        if (records >= 1) {
            Promise<Void> nextWaitFor = client.processRecord();
            processRecords(records - 1, nextWaitFor);
        }
    }

}
