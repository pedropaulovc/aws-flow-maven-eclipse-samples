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
package com.amazonaws.services.simpleworkflow.flow.recipes.branch;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.Wait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * 
 * <b>Name</b>: Wait for the completion of parallel activities.
 * 
 * <b>Problem</b>: You want the workflow to start activities in parallel and
 * then wait for all of them to complete before proceeding with the next steps.
 * 
 * <b>Solution</b>: Use an asynchronous method to wait for the completion of all
 * activities executing in parallel.
 * 
 * <b>Discussion</b>: When you call an asynchronous method, it is executed
 * asynchronously after all {@link Promise} arguments passed to it become ready.
 * Therefore you can use asynchronous methods to wait for the completion of
 * tasks without blocking.
 * 
 * In order to wait for the completion of parallel activities in your workflow,
 * you should pass the {@link Promise} object returned from each activity
 * method to an asynchronous method and implement the next steps of the
 * workflow in that method.
 * 
 * If the number of parallel activities is small and known at compile time then
 * you can have the asynchronous method accept a {@link Promise} argument for
 * each activity. Otherwise you can use a collection of {@link Promise}s as an
 * argument. This argument must be annotated with the {@link Wait} annotation to
 * indicate that all {@link Promise} objects in the collection, and not just the
 * collection object itself, should be waited for.
 */

public class JoinBranchesWorkflowImpl implements JoinBranchesWorkflow {

    private final BranchActivitiesClient client = new BranchActivitiesClientImpl();

    @Override
    public Promise<Integer> parallelComputing(int branches) {

        List<Promise<Integer>> results = new ArrayList<Promise<Integer>>();
        //create multiple activities that will be executed in parallel
        for (int i = 0; i < branches; i++) {
            Promise<Integer> result = client.doSomeWork();
            results.add(result);
        }

        Promise<Integer> sum = joinBranches(results);

        return client.reportResult(sum);

    }

    /**
     * This method will not be executed until all promises in the 'results'
     * argument are in the ready state. An alternative to using the {@link Wait}
     * annotation, is to convert the list of {@link Promise}s to an
     * {@link AndPromise} using {@link Promises.listOfPromisesToPromise}.
     */
    @Asynchronous
    public Promise<Integer> joinBranches(@Wait List<Promise<Integer>> results) {
        int sum = 0;
        for (Promise<Integer> result : results) {
            sum += result.get();
        }
        return Promise.asPromise(sum);
    }

}
