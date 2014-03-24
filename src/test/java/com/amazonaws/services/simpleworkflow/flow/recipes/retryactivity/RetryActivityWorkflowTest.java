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
public class RetryActivityWorkflowTest {

    private final class TestRetryActivities implements RetryActivities {

        private int counter = 0;
        private int maxFailures = 3;

        public void setMaxFailures(int max){
            maxFailures = max;
        }
        
        @Asynchronous
        public Promise<Integer> getCounter(Promise<?> isReady) {
            return Promise.asPromise(counter);
        }

        @Override
        public void unreliableActivity() {
            counter++;
            if (counter <= maxFailures) {
                throw new IllegalStateException("Intentional failure");
            }
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final RetryWorkflowClientFactoryImpl retryWorkflowClientFactory = new RetryWorkflowClientFactoryImpl();

    private TestRetryActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
        workflowTest.addWorkflowImplementationType(RetryActivityWorkflowImpl.class);
    }

    //activity fails three times and is retried until it succeeds on the fourth retry
    @Test
    public void testRetryActivityWorkflowSuccess() {
        activitiesImplementation = new TestRetryActivities();
        activitiesImplementation.setMaxFailures(3);
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        RetryWorkflowClient workflow = retryWorkflowClientFactory.getClient();
        Promise<Void> done = workflow.process();
        AsyncAssert.assertEquals(4, activitiesImplementation.getCounter(done));
    }

    //activity is retried max number of times without success
    @Test(expected = IllegalStateException.class)
    public void testRetryActivityWorkflowFailure() {

        activitiesImplementation = new TestRetryActivities();
        activitiesImplementation.maxFailures = 11;
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
                AsyncAssert.assertEquals(11, activitiesImplementation.getCounter(Promise.Void()));
            }

        };

    }
}
