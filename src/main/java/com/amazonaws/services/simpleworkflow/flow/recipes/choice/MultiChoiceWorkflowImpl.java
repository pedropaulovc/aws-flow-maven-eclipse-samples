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

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.Wait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Promises;

/**
 * <b>Name</b>: Create different parallel branches in a workflow based on input.
 * 
 * <b>Problem</b>: You want to create a different set of parallel branches in a
 * workflow depending on the input and join when all of them complete.
 * 
 * <b>Solution</b>: Create a list of Promises that were returned from the
 * parallel branches and pass them to an asynchronous method to process results
 * when all branches complete.
 * 
 * <b>Discussion</b>: If the set of parallel branches is known statically, you
 * can create an asynchronous method that takes the promise returned by each
 * branch as an argument and processes the results when all promises become
 * ready. However, if you want one or more branches to execute only under some
 * conditions, the number of parameters of the asynchronous method cannot be
 * determined statically. In such cases you can have the asynchronous method
 * take an argument of type List<Promise<..>> and annotate it with {@link Wait}.
 * Alternatively you can use {@link Promises.listOfPromisesToPromise} to convert
 * the list of {@link Promise}s to a {@link Promise}. This will ensure that the
 * asynchronous method is invoked only when all Promise objects within the list
 * become ready.
 */

public class MultiChoiceWorkflowImpl implements OrderChoiceWorkflow {

    private final OrderActivitiesClient client = new OrderActivitiesClientImpl();

    @Override
    public void processOrder() {
        Promise<List<OrderChoice>> basketChoice = client.getBasketOrder();
        Promise<List<Void>> waitFor = processBasketOrder(basketChoice);
        client.finishOrder(waitFor);
    }

    @Asynchronous
    public Promise<List<Void>> processBasketOrder(Promise<List<OrderChoice>> basketChoice) {

        List<OrderChoice> choices = basketChoice.get();
        List<Promise<Void>> results = new ArrayList<Promise<Void>>();

        /**
         * All activities that are started in this loop are executed in parallel
         * as their invocation from the switch case is non blocking.
         */
        for (OrderChoice choice : choices) {
            Promise<Void> result = processSingleChoice(choice);
            results.add(result);
        }

        /**
         * listOfPromisesToPromise is an utility method that accepts a list of
         * promises and returns a single promise that becomes ready when all
         * promises of an input list are ready.
         */
        return Promises.listOfPromisesToPromise(results);
    }

    public Promise<Void> processSingleChoice(OrderChoice choice) {
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
