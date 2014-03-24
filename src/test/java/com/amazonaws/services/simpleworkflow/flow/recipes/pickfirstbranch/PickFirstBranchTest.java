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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.simpleworkflow.flow.ActivitySchedulingOptions;
import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClock;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class PickFirstBranchTest {

    private final class TestActivityClient extends SearchActivitiesClientImpl {

        DecisionContextProviderImpl contextProvider = new DecisionContextProviderImpl();

        @Override
        protected Promise<java.util.List<java.lang.String>> searchCluster1Impl(final Promise<java.lang.String> query,
                final ActivitySchedulingOptions optionsOverride, Promise<?>... waitFor) {
            DecisionContext context = contextProvider.getDecisionContext();
            WorkflowClock clock = context.getWorkflowClock();
            //start a 30 second timer
            Promise<Void> timerFired = clock.createTimer(30);
            //fail test if the timer fires
            shouldNotGetCalled(timerFired);
            // this Promise will never be ready
            return new Settable<List<String>>();
        }

        @Override
        protected Promise<java.util.List<java.lang.String>> searchCluster2Impl(final Promise<java.lang.String> query,
                final ActivitySchedulingOptions optionsOverride, Promise<?>... waitFor) {
            List<String> results = new ArrayList<String>();
            results.add("result1");
            results.add("result2");
            return Promise.asPromise(results);
        }

        @Asynchronous
        void shouldNotGetCalled(Promise<?> waitFor) {
            Assert.fail("This method should not get called");
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    @Test()
    public void testWorkflow() {
        PickFirstBranchWorkflowImpl workflow = new PickFirstBranchWorkflowImpl(new TestActivityClient());
        Promise<List<String>> result = workflow.search("test");
        assertGreeting(result);
    }

    @Asynchronous
    private void assertGreeting(Promise<List<String>> done) {
        List<String> results = done.get();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("result1", results.get(0));
        Assert.assertEquals("result2", results.get(1));
    }
}
