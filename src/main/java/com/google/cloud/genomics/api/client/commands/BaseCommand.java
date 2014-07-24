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
import com.beust.jcommander.internal.Maps;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All commands supported by the GenomicsSample must extend this class
 */
public abstract class BaseCommand {
  public static final String JOB_SUCCESS = "success";
  public static final String JOB_FAILURE = "failure";

  public static final String JOB_HISTORY_ID = "JobHistory";
  public static final String DATASET_HISTORY_ID = "DatasetHistory";

  @Parameter(names = "--root_url",
      description = "set the Genomics API root URL",
      hidden = true)
  public String rootUrl = "https://www.googleapis.com/genomics/v1beta";

  @Parameter(names = "--nolocalserver",
      description = "Disable the starting up of a local server for the auth flows",
      hidden = true)
  public boolean noLocalServer = false;

  @Parameter(names = "--client_secrets_filename",
      description = "Path to client_secrets.json")
  public String clientSecretsFilename = "client_secrets.json";

  DataStoreFactory dataStoreFactory;

  public List<String> getScopes() {
    List<String> scopes = Lists.newArrayList();
    scopes.add(GenomicsScopes.GENOMICS);
    return scopes;
  }

  public void setDataStoreFactory(DataStoreFactory dataStoreFactory) {
    this.dataStoreFactory = dataStoreFactory;
  }

  public abstract void handleRequest(Genomics genomics) throws IOException;


  protected void addItemToHistory(String datastore, String key, String value) throws IOException {
    DataStore<Serializable> history = dataStoreFactory.getDataStore(datastore);
    history.set(key, value);
  }

  protected Map<String, String> getHistoryItems(String datastore) throws IOException {
    DataStore<Serializable> jobHistory = dataStoreFactory.getDataStore(datastore);

    Map<String, String> items = Maps.newHashMap();

    Set<String> keys = jobHistory.keySet();
    for (String key : keys) {
      items.put(key, (String) jobHistory.get(key));
    }

    return items;
  }

  // Dataset methods

  protected void addDatasetToHistory(Dataset dataset) throws IOException {
    addItemToHistory(DATASET_HISTORY_ID, dataset.getId(), dataset.getName());
  }

  protected Map<String, String> getPreviousDatasets() throws IOException {
    return getHistoryItems(DATASET_HISTORY_ID);
  }

  protected Dataset getDataset(Genomics genomics, String datasetId) throws IOException {
    return getDataset(genomics, datasetId, true);
  }

  protected Dataset getDataset(Genomics genomics, String datasetId, boolean handleException)
      throws IOException {
    try {
      Dataset dataset = genomics.datasets().get(datasetId).execute();
      addDatasetToHistory(dataset);
      return dataset;
    } catch (GoogleJsonResponseException e) {
      if (!handleException) {
        throw e;
      }

      System.err.println("That datasetId won't work: " + e.getDetails().getMessage() + "\n");

      Map<String, String> previousDatasets = getPreviousDatasets();
      if (previousDatasets.isEmpty()) {
        System.err.println("There aren't any recently used datasets, " +
            "if you want to make a new one try the 'createdataset' command.");
        System.err.println("You can also try the public 1000 Genomes dataset ID: 376902546192");

      } else {
        System.err.println("In the past, you've used these datasets: ");
        for (Map.Entry<String, String> dataset : previousDatasets.entrySet()) {
          System.out.println(dataset.getKey() + ": " + dataset.getValue());
        }
      }

      // TODO: This call won't do what we want right now
      // ListDatasetsResponse allDatasets = genomics.datasets().list().execute();
      // System.err.println("These are the datasets you have access to: " + allDatasets);
      return null;
    }
  }


  // Job methods

  protected void addJobToHistory(String jobId, String description) throws IOException {
    addItemToHistory(JOB_HISTORY_ID, jobId, description);
  }

  protected Map<String, String> getLaunchedJobs() throws IOException {
    return getHistoryItems(JOB_HISTORY_ID);
  }

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
