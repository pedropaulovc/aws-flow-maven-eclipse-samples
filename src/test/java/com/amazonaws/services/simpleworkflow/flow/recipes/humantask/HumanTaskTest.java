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
package com.amazonaws.services.simpleworkflow.flow.recipes.humantask;

import java.util.ArrayList;

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
public class HumanTaskTest {

    private TestHumanTaskActivitiesImpl activitiesImpl;

    private final class TestHumanTaskActivitiesImpl implements HumanTaskActivities {

        private final ArrayList<String> result = new ArrayList<String>();

        @Override
        public void automatedActivity() {
            result.add("automatedActivity");

        }

        @Override
        public void sendNotification(String input) {
            result.add("sendNotification:" + input);

        }

        @Override
        public String humanActivity() {
            result.add("humanActivity");
            return "test";
        }

        public ArrayList<String> getResult() {
            return result;
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    @Before
    public void setUp() {
        activitiesImpl = new TestHumanTaskActivitiesImpl();
        workflowTest.addActivitiesImplementation(activitiesImpl);
        workflowTest.addWorkflowImplementationType(HumanTaskWorkflowImpl.class);
    }

    @Test
    /*
     * Tests the decider implementation only. Testing of {@link
     * ManualActivityCompletion} implementations is not currently supported by
     * the Flow Framework JUnit integration.
     */
    public void testWorkflow() {
        HumanTaskWorkflowClientFactory factory = new HumanTaskWorkflowClientFactoryImpl();
        HumanTaskWorkflowClient workflowClient = factory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        assertResult(done);
    }

    @Asynchronous
    private void assertResult(Promise<Void> done) {
        ArrayList<String> result = activitiesImpl.getResult();
        Assert.assertEquals("automatedActivity", result.get(0));
        Assert.assertEquals("humanActivity", result.get(1));
        Assert.assertEquals("sendNotification:test", result.get(2));
    }

}
