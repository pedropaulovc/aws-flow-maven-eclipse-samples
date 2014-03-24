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
public class ExclusiveChoiceWorkflowTest {

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

        @Asynchronous
        public Promise<List<String>> getTrace(Promise<Void> isReady) {
            return Promise.asPromise(trace);
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
        workflowTest.addWorkflowImplementationType(ExclusiveChoiceWorkflowImpl.class);
    }

    @Test
    public void testExclusiveChoiceWorkflow() {
        OrderChoiceWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Void> done = workflowClient.processOrder();
        List<String> expectedTrace = new ArrayList<String>();
        expectedTrace.add("singleOrdering");
        expectedTrace.add("orderApple");
        expectedTrace.add("done");
        AsyncAssert.assertEquals(expectedTrace, activitiesImplementation.getTrace(done));
    }

}
