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

import com.amazonaws.services.simpleworkflow.flow.interceptors.Decorator;
import com.amazonaws.services.simpleworkflow.flow.interceptors.ExponentialRetryPolicy;
import com.amazonaws.services.simpleworkflow.flow.interceptors.RetryDecorator;

/**
 * 
 * <b>Name</b>: Specify a retry policy for a specific invocation of an Activity
 * 
 * <b>Problem</b>: You want to retry an activity if it fails to finish the
 * execution either because of an exception in the activity execution or a
 * timeout.
 * 
 * <b>Solution</b>: The configuration specified on @ExponentialRetry for
 * retrying activity is static and applies to every invocation of the activity.
 * However, if you want to use different retry policies according the situations
 * where the activity is invoked, you can use the RetryDecorator class that can
 * construct the retry policy at runtime.
 * 
 */
public class DecoratorRetryWorkflowImpl implements RetryWorkflow {

    private RetryActivitiesClient client = new RetryActivitiesClientImpl();

    public void process() {

        /**
         * The two parameters(initialRetryIntervalSeconds, maximumAttempts) for
         * retry policy can be setup at runtime instead of the compile time. For
         * example, they can be return from another activity or an asynchronous
         * method, or be passed to the workflow externally.
         */
        long initialRetryIntervalSeconds = 5;
        int maximumAttempts = 5;
        ExponentialRetryPolicy retryPolicy = new ExponentialRetryPolicy(initialRetryIntervalSeconds).withMaximumAttempts(maximumAttempts);
        
        Decorator retryDecorator = new RetryDecorator(retryPolicy);
        client = retryDecorator.decorate(RetryActivitiesClient.class, client);
        handleUnreliableActivity();
    }

    public void handleUnreliableActivity() {
        client.unreliableActivity();
    }

}
