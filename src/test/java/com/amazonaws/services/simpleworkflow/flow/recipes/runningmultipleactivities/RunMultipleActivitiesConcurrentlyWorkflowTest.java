package com.amazonaws.services.simpleworkflow.flow.recipes.runningmultipleactivities;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.junit.AsyncAssert;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;
import com.amazonaws.services.simpleworkflow.flow.recipes.common.CommonRecipeActivitiesImpl;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class RunMultipleActivitiesConcurrentlyWorkflowTest {

    private final class TesActivitiesImpl extends CommonRecipeActivitiesImpl {

        int next;
        
        
        public void setNext(int next) {
            this.next = next;
        }

        @Override
        public void doNothing() {
            throw new UnsupportedOperationException("doNothing should not be called");
        }

        @Override
        public int generateRandomNumber() {
            super.generateRandomNumber();
            return next++;
        }

        @Override
        public boolean generateRandomBoolean() {
            throw new UnsupportedOperationException("generateRandomBoolean should not be called");
        }

        @Override
        public void delayActivity(long time) {
            throw new UnsupportedOperationException("delayActivity should not be called");
        }
    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final RunMultipleActivitiesConcurrentlyWorkflowClientFactory workflowClientFactory = new RunMultipleActivitiesConcurrentlyWorkflowClientFactoryImpl();

    private TesActivitiesImpl activitiesImpl;

    @Before
    public void init() {
        activitiesImpl = new TesActivitiesImpl();
        workflowTest.addActivitiesImplementation(activitiesImpl);
        workflowTest.addWorkflowImplementationType(RunMultipleActivitiesConcurrentlyWorkflowImpl.class);

    }

    @Test
    public void testRunThreeActivities() {
        activitiesImpl.resetCalls();
        activitiesImpl.setNext(3);
        RunMultipleActivitiesConcurrentlyWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.runMultipleActivitiesConcurrently();
        ArrayList<String> expectedTrace = new ArrayList<String>();
        expectedTrace.add("generateRandomNumber");
        expectedTrace.add("generateRandomNumber");
        expectedTrace.add("generateRandomNumber");
        AsyncAssert.assertEquals(expectedTrace, activitiesImpl.getCalls(done));
    }

    @Test
    public void testRunTwoActivities() {
        activitiesImpl.resetCalls();
        activitiesImpl.setNext(2);
        RunMultipleActivitiesConcurrentlyWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.runMultipleActivitiesConcurrently();
        ArrayList<String> expectedTrace = new ArrayList<String>();
        expectedTrace.add("generateRandomNumber");
        expectedTrace.add("generateRandomNumber");
        AsyncAssert.assertEquals(expectedTrace, activitiesImpl.getCalls(done));
    }
    
}
