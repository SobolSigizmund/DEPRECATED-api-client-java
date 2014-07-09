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
package com.google.cloud.genomics.api.client.commands;

import com.beust.jcommander.Parameter;
import com.google.api.client.util.Lists;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.Job;

import java.io.IOException;
import java.util.List;

/**
 * All commands supported by the GenomicsSample must extend this class
 */
public abstract class BaseCommand {
  private static final String GENOMICS_SCOPE = "https://www.googleapis.com/auth/genomics";
  protected static final String BIGQUERY_SCOPE = "https://www.googleapis.com/auth/bigquery";
  protected static final String DEVSTORAGE_SCOPE =
      "https://www.googleapis.com/auth/devstorage.read_write";

  public static final String JOB_SUCCESS = "success";
  public static final String JOB_FAILURE = "failure";

  @Parameter(names = "--root_url",
      description = "set the Genomics API root URL",
      hidden = true)
  public String rootUrl = "https://www.googleapis.com/genomics/v1beta";

  @Parameter(names = "--client_secrets_filename",
      description = "Path to client_secrets.json")
  public String clientSecretsFilename = "client_secrets.json";

  public List<String> getScopes() {
    List<String> scopes = Lists.newArrayList();
    scopes.add(GENOMICS_SCOPE);
    return scopes;
  }

  public abstract void handleRequest(Genomics genomics) throws IOException;


  // Helper functions

  protected boolean isJobFinished(Job job) {
    String status = job.getStatus();
    return JOB_SUCCESS.equals(status) || JOB_FAILURE.equals(status);
  }

  protected Job getJob(Genomics genomics, String jobId, boolean pollForStatus)
      throws IOException {
    Genomics.Jobs.Get jobRequest = genomics.jobs().get(jobId);
    Job job = jobRequest.execute();

    if (pollForStatus && !isJobFinished(job)) {
      System.out.println("Waiting for job: " + job.getId());
      while (!isJobFinished(job)) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        System.out.print(".");
        job = jobRequest.execute();
      }
      System.out.println();
    }
    return job;
  }
}
