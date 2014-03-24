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
public class ConditionalLoopWorkflowTest {

    private final class TestConditionalLoopActivities implements ConditionalLoopActivities {

        private int counter = 0;

        public int getRecordCount() {
            return 5;
        }

        @Override
        public void processRecord() {
            counter++;
        }

        @Asynchronous
        public Promise<Integer> getCounter(Promise<?> isReady) {
            return Promise.asPromise(counter);
        }

    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final ConditionalLoopWorkflowClientFactory conditionLoopWorkflowfactory = new ConditionalLoopWorkflowClientFactoryImpl();

    private TestConditionalLoopActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
        activitiesImplementation = new TestConditionalLoopActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        workflowTest.addWorkflowImplementationType(ConditionalLoopWorkflowImpl.class);
    }

    @Test
    public void testConditionLoopWorkflow() {
        ConditionalLoopWorkflowClient workflowClient = conditionLoopWorkflowfactory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        AsyncAssert.assertEquals(5, activitiesImplementation.getCounter(done));
    }

}
