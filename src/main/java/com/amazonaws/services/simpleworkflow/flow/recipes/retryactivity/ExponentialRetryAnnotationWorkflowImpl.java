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

/**
 * 
 * <b>Name</b>: Apply retry policy to all invocations of an Activity
 * 
 * <b>Problem</b>: You want to retry all invocations of an activity of a certain
 * type if it fails to finish the execution either because of an exception in
 * the activity execution or a timeout.
 * 
 * <b>Solution</b>: @ExponentialRetry annotation can be used to apply a retry
 * policy to all invocations of an activity.
 * 
 * <b>Discussion</b>: The @ExponentialRetry annotation provides us with an easy
 * way to retry an activity when it fails. When @ExponentialRetry annotation is
 * used on an activity or asynchronous method, it sets a exponential retry
 * policy for all invocations of that activity or the asynchronous method. An
 * exponential backoff is also applied to the retry attempts.
 * 
 */

public class ExponentialRetryAnnotationWorkflowImpl implements RetryWorkflow {

    private final ExponentialRetryAnnotationActivitiesClient client = new ExponentialRetryAnnotationActivitiesClientImpl();

    public void process() {
        handleUnreliableActivity();
    }

    public void handleUnreliableActivity() {
        client.unreliableActivity();
    }

}
