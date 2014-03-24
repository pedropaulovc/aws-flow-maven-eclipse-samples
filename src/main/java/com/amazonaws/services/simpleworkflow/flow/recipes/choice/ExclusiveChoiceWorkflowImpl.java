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
package com.amazonaws.services.simpleworkflow.flow.recipes.choice;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * 
 * <b>Name</b>: Execute one of many code paths.
 * 
 * <b>Problem</b>: You want to implement a workflow that executes one of several
 * alternative code paths based on the result of an activity.
 * 
 * <b>Solution</b>: Use an {@link Asynchronous} method and standard Java control
 * flow statements, such as if-then-else or switch, to conditionally execute
 * different code paths.
 * 
 * <b>Discussion</b>: This recipe demonstrates how to execute a specific
 * activity based on the result of a previous activity. The first activity is
 * called using the appropriate activity method on the generated client. The
 * {@link Promise} returned by this method will become ready upon the completion
 * of the activity. An {@link Asynchronous} method is used to asynchronously
 * wait on this {@link Promise} and choose the next activity to execute using a
 * switch statement.
 * 
 */

public class ExclusiveChoiceWorkflowImpl implements OrderChoiceWorkflow {

    private final OrderActivitiesClient client = new OrderActivitiesClientImpl();

    @Override
    public void processOrder() {
        Promise<OrderChoice> itemChoice = client.getItemOrder();
        Promise<Void> waitFor = processItemOrder(itemChoice);
        client.finishOrder(waitFor);
    }

    /**
     * chooses an activity to execute
     */
    @Asynchronous
    public Promise<Void> processItemOrder(Promise<OrderChoice> itemChoice) {
        OrderChoice choice = itemChoice.get();
        Promise<Void> result = null;
        switch (choice) {
        case APPLE:
            result = client.orderApple();
            break;
        case ORANGE:
            result = client.orderOrange();
            break;
        case LETTUCE:
            result = client.orderLettuce();
            break;
        case CABBAGE:
            result = client.orderCabbage();
            break;
        }
        return result;
    }

}
