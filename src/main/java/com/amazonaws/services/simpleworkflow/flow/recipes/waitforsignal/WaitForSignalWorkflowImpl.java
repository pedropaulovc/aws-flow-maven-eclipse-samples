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
package com.amazonaws.services.simpleworkflow.flow.recipes.waitforsignal;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClock;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.OrPromise;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.Settable;

/**
 * <b>Name</b>: Wait for an external signal.
 * 
 * <b>Problem</b>: Make the workflow execution take a different code path if a
 * signal is received within a given period of time.
 * 
 * <b>Solution</b>: Use a timer to wait for the desired period and when signal
 * is received cancel the timer.
 * 
 * <b>Discussion</b>: A running workflow execution can be notified of an
 * external event by sending it a signal. For instance an order processing
 * workflow may be sent a signal to modify the order. When a signal is received
 * you can set a field of type {@link Settable} to unblock other parts of the
 * workflow implementation that depend on that signal. A signal may be received
 * by a workflow execution any time while it is open. You can make the execution
 * wait for the signal by creating a timer for the desired duration. The timer
 * should be cancelled when the signal is received. To cancel the timer
 * automatically create it within an asynchronous method that is marked as
 * daemon. You can then create an {@link OrPromise} from the {@link Promise} for
 * this timer and the {@link Settable} and pass it to another asynchronous
 * method which will get executed when either the signal has been received or
 * the timer has fired.
 * 
 * @author asadj
 * 
 */
public class WaitForSignalWorkflowImpl implements WaitForSignalWorkflow {

    private final int changeOrderPeriod = 30;

    private Settable<Integer> signalReceived = new Settable<Integer>();

    private final WaitForSignalActivitiesClient client = new WaitForSignalActivitiesClientImpl();

    private WorkflowClock clock;

    public WaitForSignalWorkflowImpl() {
        DecisionContextProvider provider = new DecisionContextProviderImpl();
        DecisionContext context = provider.getDecisionContext();
        clock = context.getWorkflowClock();
    }

    @Override
    public void placeOrder(int amount) {
        Promise<Void> timer = startDaemonTimer(changeOrderPeriod);
        OrPromise signalOrTimer = new OrPromise(timer, signalReceived);
        processOrder(amount, signalOrTimer);
    }

    @Asynchronous
    private void processOrder(int originalAmount, Promise<?> waitFor) {
        int amount = originalAmount;
        if (signalReceived.isReady())
            amount = signalReceived.get();
        client.processOrder(amount);
    }

    @Override
    public void changeOrder(int amount) {
        if(!signalReceived.isReady()){
            signalReceived.set(amount);
        }
    }

    @Asynchronous(daemon = true)
    private Promise<Void> startDaemonTimer(int seconds) {
        Promise<Void> timer = clock.createTimer(seconds);
        return timer;
    }

}
