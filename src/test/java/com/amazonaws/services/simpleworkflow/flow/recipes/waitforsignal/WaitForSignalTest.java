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
package com.amazonaws.services.simpleworkflow.flow.recipes.waitforsignal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class WaitForSignalTest {

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private TestWaitForSignalActivitiesImpl activitiesImpl;

    private class TestWaitForSignalActivitiesImpl implements WaitForSignalActivities {

        private int amount;

        @Override
        public void processOrder(int amount) {
            this.amount = amount;

        }

        public int getAmount() {
            return amount;
        }
    };

    @Before
    public void setUp() {
        activitiesImpl = new TestWaitForSignalActivitiesImpl();
        workflowTest.addActivitiesImplementation(activitiesImpl);
        workflowTest.addWorkflowImplementationType(WaitForSignalWorkflowImpl.class);
    }

    @Test
    public void testWorkflowSignaled() {
        WaitForSignalWorkflowClientFactory factory = new WaitForSignalWorkflowClientFactoryImpl();
        WaitForSignalWorkflowClient workflowClient = factory.getClient();
        Promise<Void> done = workflowClient.placeOrder(100);
        //wait for execution to start then send it a signal
        Promise<?> runId = workflowClient.getRunId();
        //change amount to 200
        sendSignal(workflowClient, 200, runId);
        assertAmount(200, done);
    }

    @Test
    public void testWorkflowNotSignaled() {
        //speed up the clock to fire timers faster for testing
        workflowTest.setClockAccelerationCoefficient(10);
        WaitForSignalWorkflowClientFactory factory = new WaitForSignalWorkflowClientFactoryImpl();
        WaitForSignalWorkflowClient workflowClient = factory.getClient();
        Promise<Void> done = workflowClient.placeOrder(100);
        //the workflow execution should proceed with the amount specified at start in the absense of signal after wait period
        assertAmount(100, done);
    }

    @Asynchronous
    private void sendSignal(WaitForSignalWorkflowClient workflowClient, int amount, Promise<?>... waitFor) {
        workflowClient.changeOrder(amount);
    }

    @Asynchronous
    private void assertAmount(int expected, Promise<Void> waitFor) {
        Assert.assertEquals(activitiesImpl.getAmount(), expected);
    }

}
