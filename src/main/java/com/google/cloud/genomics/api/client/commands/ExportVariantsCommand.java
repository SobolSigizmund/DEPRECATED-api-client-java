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
import com.beust.jcommander.Parameters;
import com.google.api.client.util.Joiner;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.ExportVariantsRequest;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Export variants from Google Genomics into Google Big Query")
public class ExportVariantsCommand extends BaseCommand {

  @Parameter(names = { "--variant_set_id", "--dataset_id" },
      description = "The Genomics API variant set ID to export from.",
      required = true)
  public String datasetId;

  @Parameter(names = { "--project_number", "--project_id" },
      description = "The Google Developer's Console project number that owns the BigQuery dataset.",
      required = true)
  public Long projectId;

  @Parameter(names = { "--call_set_id", "--callset_id" },
      description = "If provided, only variant call information from " +
          "the specified call sets will be exported. By default all " +
          "variant calls are exported.",
      required = false)
  public List<String> callSetIds;

  @Parameter(names = "--bigquery_dataset",
      description = "The BigQuery dataset to export data to. " + 
          "Note that this is distinct from the Genomics concept " +
          "of \"dataset\". The caller must have WRITE access to " +
          "this BigQuery dataset.",
      required = true)
  public String bigqueryDataset;

  @Parameter(names = "--bigquery_table",
      description = "The BigQuery table to export data to. " +
        "If the table doesn't exists, it will be created." +
        "If it already exists, it will be overwritten.",
      required = true)
  public String bigqueryTable;

  @Parameter(names = "--poll",
      description = "If set, the client will query for job status " +
          "until it either finishes or fails.")
  public boolean pollForStatus = false;

  @Override
  public List<String> getScopes() {
    List<String> scopes = super.getScopes();
    scopes.add(GenomicsScopes.BIGQUERY);
    return scopes;
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    // Validate the dataset
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }

    System.out.println("Exporting variants for dataset " + dataset.getName());
    if (callSetIds != null && !callSetIds.isEmpty()) {
      System.out.println(Joiner.on(',').join(callSetIds));
    }

    // Start the export
    ExportVariantsRequest request = new ExportVariantsRequest()
        .setVariantSetId(datasetId)
        .setProjectId(projectId)
        .setCallSetIds(callSetIds)
        .setBigqueryDataset(bigqueryDataset)
        .setBigqueryTable(bigqueryTable);
    String jobId = genomics.variants().export(request).execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Exporting variants: from " + dataset.getName() +
        " to " + bigqueryDataset + "." + bigqueryTable);
    System.out.println("Export job: ");
    printJob(getJob(genomics, jobId, pollForStatus));
  }
}
