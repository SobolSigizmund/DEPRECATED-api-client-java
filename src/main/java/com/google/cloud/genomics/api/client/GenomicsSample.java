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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsRequest;
import com.google.api.services.genomics.model.ImportReadsetsRequest;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsetsRequest;
import org.kohsuke.args4j.CmdLineException;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Genomics Java client sample application.
 */
public class GenomicsSample {
  private static final String APPLICATION_NAME = "Google-GenomicsSample/1.0";
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/genomics_java_client");
  private static final String DEVSTORAGE_SCOPE =
      "https://www.googleapis.com/auth/devstorage.read_write";
  private static final String GENOMICS_SCOPE = "https://www.googleapis.com/auth/genomics";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static FileDataStoreFactory dataStoreFactory;
  private static NetHttpTransport httpTransport;
  private static CommandLine cmdLine;

  private static GoogleClientSecrets loadClientSecrets(String clientSecretsFilename) {
    File f = new File(clientSecretsFilename);
    if (f.exists()) {
      try {
        InputStream inputStream = new FileInputStream(new File(clientSecretsFilename));
        return GoogleClientSecrets.load(JSON_FACTORY,
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

  private static Genomics buildService(final Credential credential) {
    return new Genomics.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .setRootUrl(cmdLine.rootUrl)
        .setServicePath("/")
        .setHttpRequestInitializer(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
              credential.initialize(httpRequest);
              httpRequest.setReadTimeout(60000); // 60 seconds
            }
          }).build();
  }

  private static Credential authorize(List<String> scopes) throws Exception {
    GoogleClientSecrets clientSecrets = loadClientSecrets(cmdLine.clientSecretsFilename);
    if (clientSecrets == null) {
      return null;
    }

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, scopes).setDataStoreFactory(dataStoreFactory).build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user" + scopes.size());
  }

  public static void main(String[] args) throws IOException {
    cmdLine = new CommandLine();

    try {
      // Parse the command line
      cmdLine.setArgs(args);

      // Authorization
      List<String> scopes = Lists.newArrayList();
      scopes.add(GENOMICS_SCOPE);
      if (cmdLine.requestType == CommandLine.RequestType.IMPORTREADSETS) {
        scopes.add(DEVSTORAGE_SCOPE);
      }

      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      Credential credential = authorize(scopes);
      if (credential == null) {
        return;
      }

      // Route to appropriate request method
      Genomics genomics = buildService(credential);
      executeAndPrint(getRequest(cmdLine, genomics));

    } catch (IllegalArgumentException | CmdLineException e) {
      cmdLine.printHelp(e.getMessage() + "\n", System.err);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static GenomicsRequest<? extends GenericJson> getRequest(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    switch (cmdLine.requestType) {
      case IMPORTREADSETS:
        return importReadsets(cmdLine, genomics);
      case SEARCHREADSETS:
        return searchReadsets(cmdLine, genomics);
      case GETREADSET:
        return getReadset(cmdLine, genomics);
      case GETJOB:
        return getJob(cmdLine, genomics);
      case SEARCHREADS:
        return searchReads(cmdLine, genomics);
    }

    throw new IllegalArgumentException();
  }

  private static void assertOrThrow(boolean condition, String headline) throws IllegalArgumentException {
    if (!condition) {
      throw new IllegalArgumentException(headline);
    }
  }

  private static void executeAndPrint(GenomicsRequest<? extends GenericJson> req) throws IOException {
    req.setDisableGZipContent(true);
    if (!cmdLine.fields.isEmpty()) {
      req.setFields(cmdLine.fields);
    }
    GenericJson result = req.execute();
    System.out.println("result: " + (cmdLine.prettyPrint ? result.toPrettyString() : result.toString()));
  }

  static Genomics.Readsets.GenomicsImport importReadsets(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    assertOrThrow(!cmdLine.datasetId.isEmpty(), "Must specify a dataset_id\n");
    assertOrThrow(cmdLine.bamFiles.size() > 0, "Must specify at least one BAM file\n");

    ImportReadsetsRequest content = new ImportReadsetsRequest()
        .setDatasetId(cmdLine.datasetId)
        .setSourceUris(cmdLine.bamFiles);
    return genomics.readsets().genomicsImport(content);
  }

  static Genomics.Readsets.Search searchReadsets(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    assertOrThrow(!cmdLine.datasetIds.isEmpty(), "Currently, dataset_ids is required. " +
        "This requirement will go away in the future.");

    SearchReadsetsRequest content = new SearchReadsetsRequest().setDatasetIds(cmdLine.datasetIds);
    return genomics.readsets().search(content);
  }

  static Genomics.Readsets.Get getReadset(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    assertOrThrow(!cmdLine.readsetId.isEmpty(), "Must specify a readset_id");
    return genomics.readsets().get(cmdLine.readsetId);
  }

  static Genomics.Jobs.Get getJob(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    assertOrThrow(!cmdLine.jobId.isEmpty(), "Must specify a job_id");
    return genomics.jobs().get(cmdLine.jobId);
  }

  static Genomics.Reads.Search searchReads(CommandLine cmdLine, Genomics genomics)
      throws IOException, IllegalArgumentException {
    SearchReadsRequest content = new SearchReadsRequest()
        .setReadsetIds(cmdLine.readsetIds)
        .setPageToken(cmdLine.pageToken);

    // Range parameters must all be specified or none.
    if (!cmdLine.sequenceName.isEmpty() || cmdLine.sequenceStart > 0 || cmdLine.sequenceEnd > 0) {
      assertOrThrow(!cmdLine.sequenceName.isEmpty(), "Must specify a sequence_name");
      assertOrThrow(cmdLine.sequenceStart > 0, "sequence_start must be greater than 0");
      // getting this far implies target_start is greater than 0
      assertOrThrow(cmdLine.sequenceEnd >= cmdLine.sequenceStart,
          "sequence_end must be greater than sequence_start");

      content
          .setSequenceName(cmdLine.sequenceName)
          .setSequenceStart(BigInteger.valueOf(cmdLine.sequenceStart))
          .setSequenceEnd(BigInteger.valueOf(cmdLine.sequenceEnd));
    }
    return genomics.reads().search(content);
  }
}
