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

import com.amazonaws.services.simpleworkflow.flow.ActivityTaskFailedException;
import com.amazonaws.services.simpleworkflow.flow.ChildWorkflowException;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.junit.AsyncAssert;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class HandleErrorWorkflowTest {

    private final class TestResourceManagementActivities implements ResourceManagementActivities {

        private boolean fail;

        private boolean handleableException;

        public TestResourceManagementActivities(boolean fail, boolean handleableException) {
            this.fail = fail;
            this.handleableException = handleableException;
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
                if (handleableException) {
                    throw new ResourceNoResponseException();
                }
                else {
                    throw new IllegalArgumentException();
                }
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

    private final HandleErrorWorkflowClientFactory handleErrorWorkflowfactory = new HandleErrorWorkflowClientFactoryImpl();

    private TestResourceManagementActivities activitiesImplementation;

    List<String> expectedTrace;

    @Before
    public void setUp() throws Exception {

        workflowTest.addWorkflowImplementationType(HandleErrorWorkflowImpl.class);
        expectedTrace = new ArrayList<String>();
    }

    //in this case, the activity does not throws an exception
    @Test
    public void testHandleErrorWorkflowWithoutException() {
        activitiesImplementation = new TestResourceManagementActivities(false, false);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        HandleErrorWorkflowClient workflowClient = handleErrorWorkflowfactory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        expectedTrace.add("allocating");
        expectedTrace.add("using");
        AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(done));
    }

    // in this case, the activity throws an exception that can be handled. 
    @Test
    public void testHandleErrorWorkflowWithHandleableException() {
        activitiesImplementation = new TestResourceManagementActivities(true, true);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        HandleErrorWorkflowClient workflowClient = handleErrorWorkflowfactory.getClient();
        Promise<Void> done = workflowClient.startWorkflow();
        expectedTrace.add("allocating");
        expectedTrace.add("using");
        expectedTrace.add("reportBadResource");
        AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(done));
    }

    // in this case, the activity throws an exception that cannot be handled. 
    @Test(expected = IllegalArgumentException.class)
    public void testHandleErrorWorkflowWithNonHandleableException() {
        activitiesImplementation = new TestResourceManagementActivities(true, false);
        workflowTest.addActivitiesImplementation(activitiesImplementation);

        final HandleErrorWorkflowClient workflowClient = handleErrorWorkflowfactory.getClient();

        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                workflowClient.startWorkflow();

            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                if (e instanceof ChildWorkflowException && e.getCause() instanceof ActivityTaskFailedException) {
                    throw e.getCause().getCause();
                }
                else {
                    throw e;
                }
            }

            @Override
            protected void doFinally() throws Throwable {
                List<String> expectedTrace = new ArrayList<String>();
                expectedTrace.add("allocating");
                expectedTrace.add("using");
                AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(Promise.Void()));
            }
        };

    }
}
