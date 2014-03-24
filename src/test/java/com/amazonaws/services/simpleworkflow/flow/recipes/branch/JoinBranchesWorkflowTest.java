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
package com.amazonaws.services.simpleworkflow.flow.recipes.branch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.junit.AsyncAssert;
import com.amazonaws.services.simpleworkflow.flow.junit.FlowBlockJUnit4ClassRunner;
import com.amazonaws.services.simpleworkflow.flow.junit.WorkflowTest;

@RunWith(FlowBlockJUnit4ClassRunner.class)
public class JoinBranchesWorkflowTest {

    private final class TestBranchActivities implements BranchActivities {

        @Override
        public int doSomeWork() {
            return 1;
        }

        @Override
        public int reportResult(int sum) {
            return sum;
        }

    }

    @Rule
    public WorkflowTest workflowTest = new WorkflowTest();

    private final JoinBranchesWorkflowClientFactory workflowClientFactory = new JoinBranchesWorkflowClientFactoryImpl();

    private TestBranchActivities activitiesImplementation;

    @Before
    public void setUp() throws Exception {
        activitiesImplementation = new TestBranchActivities();
        workflowTest.addActivitiesImplementation(activitiesImplementation);
        workflowTest.addWorkflowImplementationType(JoinBranchesWorkflowImpl.class);
    }

    @Test
    public void testJoinBranchesWorkflow() {
        JoinBranchesWorkflowClient workflowClient = workflowClientFactory.getClient();
        Promise<Integer> result = workflowClient.parallelComputing(10);
        AsyncAssert.assertEquals(10, result);
    }

}
