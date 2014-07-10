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
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.ImportReadsetsRequest;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.Readset;

import java.io.IOException;
import java.util.List;

/**
 * Imports readsets from bam files, fetches the resulting job, and optionally polls for its status.
 * Fetches the imported readsets if the job completes.
*/
@Parameters(commandDescription = "Import readsets from Google Cloud Storage")
public class ImportReadsetsCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to import into.",
      required = true)
  public String datasetId;

  @Parameter(names = "--bam_file",
      description = "A BAM file (as Google Cloud Storage gs:// URL) to be be imported." +
          " Use the flag multiple times to import multiple BAM files at a time.",
      required = true)
  public List<String> bamFiles;

  @Parameter(names = "--poll",
      description = "If set, the client will query for job status " +
          "until it either finishes or fails.")
  public boolean pollForStatus = false;

  @Override
  public List<String> getScopes() {
    List<String> scopes = super.getScopes();
    scopes.add(DEVSTORAGE_SCOPE);
    return scopes;
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    // TODO: Validate the GCS files first?
    // TODO: Allow specifying a gcs bucket, list all the contents, and start jobs for each set of x

    // Validate the dataset
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Importing readsets into: " + dataset.getName());

    // Start the import
    Genomics.Readsets.GenomicsImport req = genomics.readsets().genomicsImport(
        new ImportReadsetsRequest().setDatasetId(datasetId)
            .setSourceUris(bamFiles));
    String jobId = req.execute().getJobId();

    // Get the resulting job
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Import job:" + job.toPrettyString());
    addJobToHistory(jobId, "Import readsets to " + dataset.getName() + " from "
        + Joiner.on(',').join(bamFiles));

    // If the job is finished, get the imported ids
    if (job.getImportedIds() != null) {
      for (String readsetId : job.getImportedIds()) {
        Readset readset = genomics.readsets().get(readsetId)
            .setFields("id,name,fileData(fileUri)").execute();
        System.out.println("Imported readset:" + readset.toPrettyString());
      }
    }
  }
}
