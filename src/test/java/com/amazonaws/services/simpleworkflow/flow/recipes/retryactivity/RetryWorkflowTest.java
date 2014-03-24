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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.junit.AsyncAssert;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class RetryWorkflowTest {

    private final class TestRetryActivities implements RetryActivities {

        private int counter = 0;

        @Asynchronous
        public Promise<Integer> getCounter(Promise<?> isReady) {
            return Promise.asPromise(counter);
        }

        @Override
        public void unreliableActivity() {
            counter++;
            if (counter < 3) {
                throw new IllegalStateException("Intentional failure");
            }
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final RetryWorkflowClientFactory retryWorkflowClientFactory = new RetryWorkflowClientFactoryImpl();

    private TestRetryActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testAsyncExecutorRetryWorkflow() {
        activitiesImplementation = new TestRetryActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        testRetryWorkflow(AsyncExecutorRetryWorkflowImpl.class);
    }

    @Test
    public void testCustomLogicRetryWorkflow() {
        activitiesImplementation = new TestRetryActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        testRetryWorkflow(CustomLogicRetryWorkflowImpl.class);
    }

    @Test
    public void testDecoratorRetryWorkflow() {
        activitiesImplementation = new TestRetryActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        testRetryWorkflow(DecoratorRetryWorkflowImpl.class);
    }

    public void testRetryWorkflow(Class<? extends RetryWorkflow> workflowType) {
        workflowTest.addWorkflowImplementationType(workflowType);
        RetryWorkflowClient workflow = retryWorkflowClientFactory.getClient();
        Promise<Void> done = workflow.process();
        AsyncAssert.assertEquals(3, activitiesImplementation.getCounter(done));
    }

}
