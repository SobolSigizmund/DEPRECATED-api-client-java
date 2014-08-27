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
import com.google.api.services.genomics.model.*;

import java.io.IOException;
import java.util.List;

/**
 * Imports variants from bam files, fetches the resulting job, and optionally polls for its status.
 * Fetches the variant summary if the job completes.
*/
@Parameters(commandDescription = "Import variants from Google Cloud Storage")
public class ImportVariantsCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to import into.",
      required = true)
  public String datasetId;

  @Parameter(names = "--vcf_file",
      description = "A VCF file (as Google Cloud Storage gs:// URL) to be be imported." +
          " You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.vcf) or use the flag multiple times if a wildcard" +
          " won't work.",
      required = true)
  public List<String> vcfFiles;

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
    System.out.println("Importing variants into: " + dataset.getName());

    // Start the import
    Genomics.Variants.GenomicsImport req = genomics.variants().genomicsImport(
        new ImportVariantsRequest().setDatasetId(datasetId)
            .setSourceUris(vcfFiles));
    String jobId = req.execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Import variants to " + dataset.getName() + " from "
        + Joiner.on(',').join(vcfFiles));
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Import job: ");
    printJob(job);

    // If the job is finished, get the variant summary
    if (isJobFinished(job)) {
      GetVariantsSummaryResponse summary = genomics.variants().getSummary()
          .setDatasetId(datasetId).execute();
      System.out.println("Imported variant summary: " + summary.toPrettyString() + "\n");
    }
  }
}
