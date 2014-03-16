/*
 * Copyright 2012-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.simpleworkflow.flow.examples.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;


/**
 * Configuration Helper to used to create SWF and S3 clients
 */

public class ConfigHelper {
	private Properties sampleConfig;

    private String swfServiceUrl;
    private String swfAccessId;
    private String swfSecretKey;
    private String s3AccessId;
    private String s3SecretKey;
    private String domain;
    private long domainRetentionPeriodInDays;

	private Properties awsCredentials;

    private ConfigHelper(File propertiesFile) throws IOException {
        loadProperties(propertiesFile);
    }

    private void loadProperties(File propertiesFile) throws IOException {

        FileInputStream inputStream = new FileInputStream(propertiesFile);
        sampleConfig = new Properties();
        sampleConfig.load(inputStream);

        // use default AWS credentials
        awsCredentials = new Properties();
        awsCredentials.load(new FileInputStream(new File(System.getProperty("user.home"), ".aws-credentials-master")));

        this.swfServiceUrl = sampleConfig.getProperty(ConfigKeys.SWF_SERVICE_URL_KEY);
        this.swfAccessId = awsCredentials.getProperty("AWSAccessKeyId");
        this.swfSecretKey = awsCredentials.getProperty("AWSSecretKey");

        this.s3AccessId = awsCredentials.getProperty("AWSAccessKeyId");
        this.s3SecretKey = awsCredentials.getProperty("AWSSecretKey");

        this.domain = sampleConfig.getProperty(ConfigKeys.DOMAIN_KEY);
        this.domainRetentionPeriodInDays = Long.parseLong(sampleConfig.getProperty(ConfigKeys.DOMAIN_RETENTION_PERIOD_KEY));
    }

    public static ConfigHelper createConfig() throws IOException, IllegalArgumentException {

        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        ConfigHelper configHelper = new ConfigHelper(new File(SampleConstants.ACCESS_PROPERTIES_FILENAME));

        return configHelper;
    }

    public AmazonSimpleWorkflow createSWFClient() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(this.swfAccessId, this.swfSecretKey);
        AmazonSimpleWorkflow client = new AmazonSimpleWorkflowClient(awsCredentials);
        client.setEndpoint(this.swfServiceUrl);
        return client;
    }

    public AmazonS3 createS3Client() {
        AWSCredentials s3AWSCredentials = new BasicAWSCredentials(this.s3AccessId, this.s3SecretKey);
        AmazonS3 client = new AmazonS3Client(s3AWSCredentials);
        return client;
    }

    public String getDomain() {
        return domain;
    }

    public long getDomainRetentionPeriodInDays() {
        return domainRetentionPeriodInDays;
    }

    public String getValueFromConfig(String key) {
    	return sampleConfig.getProperty(key);
    }
}
