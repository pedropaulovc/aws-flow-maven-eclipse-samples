package com.amazonaws.services.simpleworkflow.flow.recipes.forloopinline;

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
public class ForLoopInlineRecipeWorkflowTest {

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final ForLoopInlineRecipeWorkflowClientFactory workflowClientFactory = new ForLoopInlineRecipeWorkflowClientFactoryImpl();

    private CommonRecipeActivitiesImpl activitiesImpl;

    @Before
    public void init() {
        activitiesImpl = new CommonRecipeActivitiesImpl();
        workflowTest.addActivitiesImplementation(activitiesImpl);
        workflowTest.addWorkflowImplementationType(ForLoopInlineRecipeWorkflowImpl.class);
    }

    @Test
    public void testLoopCallsActivityFiveTimes() {
        activitiesImpl.resetCalls();
        ForLoopInlineRecipeWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.loop(5);
        AsyncAssert.assertEquals(5, activitiesImpl.getCallCount(done));
    }

    @Test
    public void testLoopDoesntCallActivityWhenNEquals0() {
        activitiesImpl.resetCalls();
        ForLoopInlineRecipeWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.loop(0);
        AsyncAssert.assertEquals(0, activitiesImpl.getCallCount(done));
    }

    @Test
    public void testLoopDoesntCallActivityWhenNEqualsNeg1() {
        activitiesImpl.resetCalls();
        ForLoopInlineRecipeWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.loop(-1);
        AsyncAssert.assertEquals(0, activitiesImpl.getCallCount(done));
    }
}
