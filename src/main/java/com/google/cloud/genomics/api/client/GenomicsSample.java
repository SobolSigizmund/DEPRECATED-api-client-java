/*
Copyright 2014 Google Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.cloud.genomics.api.client;

import com.beust.jcommander.ParameterException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.genomics.Genomics;
import com.google.cloud.genomics.api.client.commands.BaseCommand;

import java.io.*;
import java.util.List;

/**
 * Genomics Java client sample application.
 */
public class GenomicsSample {
  private static final String APPLICATION_NAME = "Google-GenomicsSample/1.0";
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/genomics_java_client");

  private static GoogleClientSecrets loadClientSecrets(String clientSecretsFilename) {
    File f = new File(clientSecretsFilename);
    if (f.exists()) {
      try {
        InputStream inputStream = new FileInputStream(new File(clientSecretsFilename));
        return GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
            new InputStreamReader(inputStream));
      } catch (Exception e) {
        System.err.println("Could not load client_secrets.json");
      }
    } else {
      System.err.println("Client secrets file " + clientSecretsFilename + " does not exist."
          + "  Visit https://developers.google.com/genomics to learn how"
          + " to install a client_secrets.json file.  If you have installed a client_secrets.json"
          + " in a specific location, use --client_secrets_filename <path>/client_secrets.json.");
    }
    return null;
  }

  private static Genomics buildService(NetHttpTransport httpTransport, final Credential credential,
      String rootUrl) {
    return new Genomics.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credential)
        .setApplicationName(APPLICATION_NAME)
        .setRootUrl(rootUrl)
        .setServicePath("/")
        .setHttpRequestInitializer(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
              credential.initialize(httpRequest);
              httpRequest.setReadTimeout(60000); // 60 seconds
            }
          }).build();
  }

  private static Credential authorize(NetHttpTransport httpTransport,
      FileDataStoreFactory dataStoreFactory, List<String> scopes, String clientSecretsFilename,
      boolean noLocalServer) throws Exception {
    GoogleClientSecrets clientSecrets = loadClientSecrets(clientSecretsFilename);
    if (clientSecrets == null) {
      return null;
    }

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
        .setDataStoreFactory(dataStoreFactory).build();
    VerificationCodeReceiver receiver = noLocalServer ? new GooglePromptReceiver() :
        new LocalServerReceiver();
    return new AuthorizationCodeInstalledApp(flow, receiver)
        .authorize("user" + scopes.size());
  }

  public static void main(String[] args) throws IOException {
    CommandLine cmdLine = new CommandLine();

    try {
      // Parse the command line
      cmdLine.setArgs(args);

      // Authorization
      NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

      BaseCommand command = cmdLine.getCommand();
      command.setDataStoreFactory(dataStoreFactory);

      Credential credential = authorize(httpTransport, dataStoreFactory, command.getScopes(),
          command.clientSecretsFilename, command.noLocalServer);
      if (credential == null) {
        return;
      }

      try {
        credential.refreshToken();
      } catch (NullPointerException e) {
        System.err.append("Couldn't refresh the OAuth token. Are you using a different client secrets file?\n" +
            "If you want to use a different file, first clear your stored credentials: " +
            "http://google-genomics.readthedocs.org/en/latest/api-client-java/resetting_auth.html \n\n");
        return;
      }

      // Route to appropriate request method
      Genomics genomics = buildService(httpTransport, credential, command.rootUrl);
      command.handleRequest(genomics);

    } catch (IllegalArgumentException | ParameterException e) {
      cmdLine.printHelp(e.getMessage() + "\n", System.err);
    } catch (GoogleJsonResponseException e) {
      System.err.println("API request failed: " + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
