package org.example.config;


import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.google.gson.Gson;
import org.example.config.AwsSecrets;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    private final Gson gson = new Gson();

    //  Add this to inject into AuthService
    @Bean
    public AWSSecretsManager secretsManagerClient() {
        return AWSSecretsManagerClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
    }

    @Bean
    public DataSource dataSource() {
        AwsSecrets secrets = getSecret();

        String jdbcUrl = "jdbc:" + secrets.getEngine() + "://" + secrets.getHost() + ":" + secrets.getPort()
                + ";databaseName=ltlab;encrypt=false"; // using fixed DB name

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(secrets.getUsername())
                .password(secrets.getPassword())
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();
    }

    private AwsSecrets getSecret() {
        String secretName = "/api/creds/rds"; // or full ARN
        String region = "us-east-1";

        AWSSecretsManager client = secretsManagerClient();

        try {
            GetSecretValueRequest request = new GetSecretValueRequest()
                    .withSecretId(secretName);

            GetSecretValueResult result = client.getSecretValue(request);
            String secret = result.getSecretString();

            System.out.println("Secret value is: " + secret);
            return gson.fromJson(secret, AwsSecrets.class);

        } catch (AWSSecretsManagerException e) {
            System.err.println("Failed to fetch secret: " + e.getErrorMessage());
            throw new RuntimeException("Unable to fetch secret from AWS Secrets Manager", e);
        }
    }
}
