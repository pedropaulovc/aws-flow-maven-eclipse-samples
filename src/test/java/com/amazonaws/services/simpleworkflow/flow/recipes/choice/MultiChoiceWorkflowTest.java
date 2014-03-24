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
package com.amazonaws.services.simpleworkflow.flow.recipes.choice;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class MultiChoiceWorkflowTest {

    private final class TestOrderActivities implements OrderActivities {

        private List<String> trace = new ArrayList<String>();

        @Override
        public void orderApple() {
            String state = "orderApple";
            trace.add(state);

        }

        @Override
        public void orderOrange() {
            String state = "orderOrange";
            trace.add(state);

        }

        @Override
        public void orderLettuce() {
            String state = "orderLettuce";
            trace.add(state);

        }

        @Override
        public void orderCabbage() {
            String state = "orderCabbage";
            trace.add(state);
        }

        @Override
        public OrderChoice getItemOrder() {
            String state = "singleOrdering";
            trace.add(state);
            OrderChoice choice = OrderChoice.APPLE;
            return choice;
        }

        @Override
        public List<OrderChoice> getBasketOrder() {
            String state = "multiOrdering";
            trace.add(state);
            List<OrderChoice> orderChoices = new ArrayList<OrderChoice>();
            orderChoices.add(OrderChoice.APPLE);
            orderChoices.add(OrderChoice.ORANGE);
            return orderChoices;
        }

        @Override
        public void finishOrder() {
            String state = "done";
            trace.add(state);
        }

        public List<String> getTrace() {
            return trace;
        }

    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final OrderChoiceWorkflowClientFactory workflowClientFactory = new OrderChoiceWorkflowClientFactoryImpl();

    private TestOrderActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
        activitiesImplementation = new TestOrderActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        workflowTest.addWorkflowImplementationType(MultiChoiceWorkflowImpl.class);
    }

    @Test
    public void testMultiChoiceWorkflow() {
        OrderChoiceWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.processOrder();
        assertTrace(done);
    }

    @Asynchronous
    public void assertTrace(Promise<Void> done) {
        List<String> obtainedTrace = activitiesImplementation.getTrace();

        // Check if the first and last elements of the trace are in order
        Assert.assertEquals("multiOrdering", obtainedTrace.remove(0));
        Assert.assertEquals("done", obtainedTrace.remove(obtainedTrace.size() - 1));

        // Create the expected Trace with all the orders
        List<String> expectedTrace = new ArrayList<String>();
        expectedTrace.add("orderApple");
        expectedTrace.add("orderOrange");

        // Compare the traces out of order allowing repeated orders
        // if present in expected trace.
        for (String traceElement : expectedTrace) {
            Assert.assertTrue(obtainedTrace.contains(traceElement));
            obtainedTrace.remove(traceElement);
        }
        Assert.assertEquals(0, obtainedTrace.size());
    }

}
