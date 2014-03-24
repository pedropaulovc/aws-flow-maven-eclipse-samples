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

import com.amazonaws.services.simpleworkflow.flow.ActivityTaskFailedException;
import com.amazonaws.services.simpleworkflow.flow.ChildWorkflowException;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.junit.AsyncAssert;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class ExponentialRetryAnnotationWorkflowTest {

    public final class TestExponentialRetryActivities implements ExponentialRetryAnnotationActivities {

        private int counter = 0;

        @Asynchronous
        public Promise<Integer> getCounter(Promise<?> isReady) {
            return Promise.asPromise(counter);
        }

        public boolean throwRetryableException;

        public TestExponentialRetryActivities(boolean throwRetryableException) {
            this.throwRetryableException = throwRetryableException;
        }

        @Override
        public void unreliableActivity() {
            counter++;
            if (counter < 3) {
                if (this.throwRetryableException) {
                    throw new IllegalStateException("Intentional failure");
                }
                else {
                    throw new IllegalArgumentException("Intentional failure");
                }
            }
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final RetryWorkflowClientFactory retryWorkflowClientFactory = new RetryWorkflowClientFactoryImpl();

    private TestExponentialRetryActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
        workflowTest.addWorkflowImplementationType(ExponentialRetryAnnotationWorkflowImpl.class);
    }

    //in this case, the exception thrown from the activity will cause a retry.
    @Test
    public void testExponentialRetryWorkflowWithRetryableException() {
        activitiesImplementation = new TestExponentialRetryActivities(true);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        RetryWorkflowClient workflow = retryWorkflowClientFactory.getClient();
        Promise<Void> done = workflow.process();
        AsyncAssert.assertEquals(3, activitiesImplementation.getCounter(done));
    }

    //in this case, the  exception thrown from the activity won't cause a retry
    @Test(expected = IllegalArgumentException.class)
    public void testExponentialRetryWorkflowWithNonRetryableException() {

        activitiesImplementation = new TestExponentialRetryActivities(false);
        workflowTest.addActivitiesImplementation(activitiesImplementation);

        final RetryWorkflowClient workflow = retryWorkflowClientFactory.getClient();

        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                workflow.process();
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
                AsyncAssert.assertEquals(1, activitiesImplementation.getCounter(Promise.Void()));
            }

        };

    }

}
