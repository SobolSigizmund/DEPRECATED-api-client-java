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
import com.google.api.services.genomics.model.ImportReadGroupSetsRequest;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.ReadGroupSet;

import java.io.IOException;
import java.util.List;

/**
 * Imports read group sets from bam files, fetches the resulting job, and optionally
 * polls for its status.
 * Fetches the imported read group sets if the job completes.
*/
@Parameters(commandDescription = "Import read group sets from Google Cloud Storage")
public class ImportReadsCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to import into.",
      required = true)
  public String datasetId;

  @Parameter(names = "--bam_file",
      description = "A BAM file (as Google Cloud Storage gs:// URL) to be imported." +
          " You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.bam) or use the flag multiple times if a wildcard" +
          " won't work.",
      required = true)
  public List<String> bamFiles;

  @Parameter(names = "--poll",
      description = "If set, the client will query for job status " +
          "until it either finishes or fails.")
  public boolean pollForStatus = false;

  @Override
  public List<String> getScopes() {
    List<String> scopes = super.getScopes();
    scopes.add(GenomicsScopes.DEVSTORAGE_READ_WRITE);
    return scopes;
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    // TODO: Validate the GCS files first?

    // Validate the dataset
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Importing read group sets into: " + dataset.getName());

    // Start the import
    Genomics.Readgroupsets.GenomicsImport req = genomics.readgroupsets().genomicsImport(
        new ImportReadGroupSetsRequest().setDatasetId(datasetId)
            .setSourceUris(bamFiles));
    String jobId = req.execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Import read group sets to " + dataset.getName() + " from "
        + Joiner.on(',').join(bamFiles));
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Import job: ");
    printJob(job);

    // If the job is finished, get the imported ids
    if (job.getImportedIds() != null) {
      for (String id : job.getImportedIds()) {
        ReadGroupSet readGroupSet = genomics.readgroupsets().get(id)
            .setFields("id,name,filename").execute();
        System.out.println("Imported read group set: " + readGroupSet.toPrettyString());
      }
    }
  }
}
