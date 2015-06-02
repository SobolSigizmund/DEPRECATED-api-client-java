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
import com.google.api.services.genomics.model.CallReadGroupSetsRequest;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Parameters(commandDescription = "Call variants on existing read group sets or" +
    " BAM files from Google Cloud Storage")
public class CallVariantsCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID where the called variants will be stored.",
      required = true)
  public String datasetId;

  @Parameter(names = "--read_group_set_id",
      description = "The ID of the read group set to call variants on.")
  public String readGroupSetId;

  @Parameter(names = "--bam_file",
      description = "A BAM file (as Google Cloud Storage gs:// URL) to call variants on." +
          " You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.bam) or use the flag multiple times if a wildcard" +
          " won't work.")
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
    if (readGroupSetId == null && bamFiles == null) {
      System.out.println("One of --read_group_set_id or --bam_file must be provided.");
      return;
    } else if (readGroupSetId != null && bamFiles != null) {
      System.out.println("You can not provide both --read_group_set_id and --bam_file.");
      return;
    }

    // Validate the dataset
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Saving called variants into: " + dataset.getName());

    // Start the job
    Genomics.Readgroupsets.Call req = genomics.readgroupsets().call(
        new CallReadGroupSetsRequest()
            .setDatasetId(datasetId)
            .setReadGroupSetId(readGroupSetId)
            .setSourceUris(bamFiles));
    String jobId = req.execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Saving called variants into " + dataset.getName() + " from "
        + Joiner.on(',').join(bamFiles == null ? Arrays.asList(readGroupSetId) : bamFiles));
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Variant calling job: ");
    printJob(job);
  }
}
