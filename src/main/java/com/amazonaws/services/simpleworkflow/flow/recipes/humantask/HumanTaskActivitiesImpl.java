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

import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContext;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.ManualActivityCompletion;

public class HumanTaskActivitiesImpl implements HumanTaskActivities {

    ActivityExecutionContextProvider contextProvider = new ActivityExecutionContextProviderImpl();

    @Override
    public void automatedActivity() {
        System.out.println("Automated activity executed");

    }

    @Override
    public void sendNotification(String input) {
        System.out.println("Message: " + input);

    }

    @Override
    @ManualActivityCompletion
    public String humanActivity() {
        ActivityExecutionContext executionContext = contextProvider.getActivityExecutionContext();
        String taskToken = executionContext.getTaskToken();
        System.out.println("Task received, completion token: " + taskToken);
        //This will not be returned to the caller
        return null;
    }

}
