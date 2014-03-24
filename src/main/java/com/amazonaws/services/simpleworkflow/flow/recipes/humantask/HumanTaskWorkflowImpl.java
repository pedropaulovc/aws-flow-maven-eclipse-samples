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
import com.amazonaws.services.simpleworkflow.flow.annotations.ManualActivityCompletion;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * <b>Name</b>: Workflow with a human task.
 * 
 * <b>Problem</b>: You want an activity in the workflow to be performed by a
 * person.
 * 
 * <b>Solution</b>: Model the human task as a manually completed activity.
 * 
 * <b>Discussion</b>: By default, when an activity method returns, it completes
 * the activity task. You can turn off automatic completion by annotating the
 * activity method implementation with the {@link ManualActivityCompletion}
 * annotation, and having a person manually complete it instead. In order to
 * manually complete the task, its task token is required; the task token is
 * used by Amazon SWF to uniquely identify the task. You can get the task token
 * in the activity method using the {@link ActivityExecutionContext}. The task
 * token should then be given to the person who is going to complete the task.
 * For example, the activity may send an email with the task token to the person
 * responsible for the task. The activity task can then be completed at a later
 * time using {@link ManualActivityCompletionClient} and providing the task
 * token. This mechanism can also be used to pass the task token to an external
 * service or a separate process which then completes the task.
 * 
 * @author asadj
 * 
 */
public class HumanTaskWorkflowImpl implements HumanTaskWorkflow {

    HumanTaskActivitiesClient client;

    public HumanTaskWorkflowImpl() {
        client = new HumanTaskActivitiesClientImpl();
    }

    @Override
    public void startWorkflow() {
        Promise<Void> automatedResult1 = client.automatedActivity();
        Promise<String> humanResult = client.humanActivity(automatedResult1);
        client.sendNotification(humanResult);
    }

}
