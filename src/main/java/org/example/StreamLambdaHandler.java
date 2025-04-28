package org.example;

import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.codepipeline.model.PutJobSuccessResultRequest;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(EmployyeDetailsApplication.class);
        } catch (ContainerInitializationException initError) {
            initError.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", initError);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
        outputStream.close();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(inputStream);

            if (root.has("CodePipeline.job")) {
                String jobId = root.get("CodePipeline.job").get("id").asText();

                AWSCodePipeline pipelineClient = AWSCodePipelineClientBuilder.defaultClient();
                PutJobSuccessResultRequest successRequest = new PutJobSuccessResultRequest().withJobId(jobId);
                pipelineClient.putJobSuccessResult(successRequest);

                System.out.println(" CodePipeline job success reported for jobId: " + jobId);
            }
        } catch (Throwable ignored) {
            // No "Exception e", but still logs error
            System.err.println(" ERROR_CODE: CP-FAIL-001 - Failed to notify CodePipeline of success.");
        }
    }
}
