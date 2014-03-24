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
package com.amazonaws.services.simpleworkflow.flow.recipes.pickfirstbranch;

import java.util.List;
import java.util.concurrent.CancellationException;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.OrPromise;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatch;

/**
 * <b>Name</b>: Pick the result of the branch that completes first.
 * 
 * <b>Problem</b>: You want to execute parallel branches in a workflow and join
 * them as soon as any one of them completes.
 * 
 * <b>Solution</b>: Use a {@link TryCatch} to wrap each parallel branch. When a
 * branch completes, call cancel() on the {@link TryCatch} wrappers of all other
 * branches to cancel them.
 * 
 * <b>Discussion</b>: In order to implement this use case, you need a) the
 * ability to wait for any branch to complete and b) the ability to cancel all
 * other branches as soon as one branch completes.
 * 
 * The first requirement can be addressed by using an {@link OrPromise} that is
 * created from the promises returned by all the parallel branches. The
 * {@link OrPromise} will become ready as soon as the promise returned by any
 * branch becomes ready.
 * 
 * {@link TryCatch} can be used to fulfill the second requirement. Besides error
 * handling, {@link TryCatch} also allows you to cancel the execution of the
 * branch scoped within it. You can create each parallel branch in a separate
 * TryCatch and call cancel() to cancel an individual branch.
 * 
 * @author asadj
 * 
 */
public class PickFirstBranchWorkflowImpl implements PickFirstBranchWorkflow {

    private final SearchActivitiesClient client;

    private TryCatch branch1;

    private TryCatch branch2;

    public PickFirstBranchWorkflowImpl() {
        client = new SearchActivitiesClientImpl();
    }

    // this constructor is for unit testing with mock client
    public PickFirstBranchWorkflowImpl(SearchActivitiesClient client) {
        this.client = client;
    }

    @Override
    public Promise<List<String>> search(final String query) {

        //start parallel branches to run same query on 2 clusters
        Promise<List<String>> branch1Result = searchOnCluster1(query);
        Promise<List<String>> branch2Result = searchOnCluster2(query);

        //branch1OrBranch2 will be ready when either branch completes 
        OrPromise branch1OrBranch2 = new OrPromise(branch1Result, branch2Result);
        return processResults(branch1OrBranch2);
    }

    @SuppressWarnings("unchecked")
    @Asynchronous
    Promise<List<String>> processResults(OrPromise result) {
        Promise<List<String>> output = null;
        Promise<List<String>> branch1Result = (Promise<List<String>>) result.getValues()[0];
        Promise<List<String>> branch2Result = (Promise<List<String>>) result.getValues()[1];
        //branch1 has result
        if (branch1Result.isReady()) {
            output = branch1Result;
            //cancel branch2 if it is not complete yet
            if (!branch2Result.isReady()) {
                branch2.cancel(null);
            }
        }
        //branch2 has result, cancel branch 1
        else {
            output = branch2Result;
            branch1.cancel(null);
        }
        return output;
    }

    Promise<List<String>> searchOnCluster1(final String query) {
        final Settable<List<String>> result = new Settable<List<String>>();
        branch1 = new TryCatch() {

            @Override
            protected void doTry() throws Throwable {
                Promise<List<String>> cluster1Result = client.searchCluster1(query); 
                result.chain(cluster1Result);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                if (!(e instanceof CancellationException)) {
                    throw e;
                }
            }

        };
        return result;
    }

    Promise<List<String>> searchOnCluster2(final String query) {
        final Settable<List<String>> result = new Settable<List<String>>();
        branch2 = new TryCatch() {

            @Override
            protected void doTry() throws Throwable {
                Promise<List<String>> cluster2Result = client.searchCluster2(query); 
                result.chain(cluster2Result);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                if (!(e instanceof CancellationException)) {
                    throw e;
                }
            }

        };
        return result;
    }

}
