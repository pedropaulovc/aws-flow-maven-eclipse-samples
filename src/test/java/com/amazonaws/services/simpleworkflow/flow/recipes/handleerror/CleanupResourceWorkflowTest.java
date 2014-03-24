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
package com.amazonaws.services.simpleworkflow.flow.recipes.handleerror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class CleanupResourceWorkflowTest {

    private final class TestResourceManagementActivities implements ResourceManagementActivities {

        private boolean fail;

        public TestResourceManagementActivities(boolean fail) {
            this.fail = fail;
        }

        private List<String> trace = new ArrayList<String>();

        @Asynchronous
        public Promise<List<String>> getTrace(Promise<?> isReady) {
            return Promise.asPromise(trace);
        }

        @Override
        public void useResource(int resourceId) throws Exception {
            String state = "using";
            trace.add(state);
            if (fail) {
                throw new ResourceNoResponseException();
            }

        }

        @Override
        public int allocateResource() {
            String state = "allocating";
            trace.add(state);
            int resourceId = new Random().nextInt(100);
            return resourceId;

        }

        @Override
        public void cleanUpResource(int resourceId) {
            String state = "cleanUp";
            trace.add(state);
        }

        @Override
        public void reportBadResource(int resourceId) {
            String state = "reportBadResource";
            trace.add(state);
        }

        @Override
        public void refreshResourceCatalog(int resourceId) {
            String state = "refreshResourceCatalog";
            trace.add(state);
        }

        @Override
        public void rollbackChanges(int resourceId) {
            String state = "rollbackChanges";
            trace.add(state);            
        }

    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final CleanupResourceWorkflowClientFactory cleanupResourceWorkflowfactory = new CleanupResourceWorkflowClientFactoryImpl();

    private TestResourceManagementActivities activitiesImplementation;

    List<String> expectedTrace = new ArrayList<String>();

    @Before
    public void setUp() throws Exception {
        workflowTest.addWorkflowImplementationType(CleanupResourceWorkflowImpl.class);
    }

    // no matter whether an exception is thrown in doTry(), doFinally() will be executed.  
    @Test
    public void testHandleErrorWorkflowWithException() {
        activitiesImplementation = new TestResourceManagementActivities(true);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        CleanupResourceWorkflowClient workflowClient = cleanupResourceWorkflowfactory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        expectedTrace.add("allocating");
        expectedTrace.add("using");
        expectedTrace.add("rollbackChanges");
        expectedTrace.add("cleanUp");
        AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(done));
    }

    // no matter whether an exception is thrown in doTry(), doFinally() will be executed. 
    @Test
    public void testHandleErrorWorkflowWithoutException() {
        activitiesImplementation = new TestResourceManagementActivities(false);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        CleanupResourceWorkflowClient workflowClient = cleanupResourceWorkflowfactory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        expectedTrace.add("allocating");
        expectedTrace.add("using");
        expectedTrace.add("cleanUp");
        AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(done));
    }

}
