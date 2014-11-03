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
package com.google.cloud.genomics.api.client.commands.align;

import com.beust.jcommander.Parameter;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.AlignReadGroupSetsRequest;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import com.google.cloud.genomics.api.client.commands.BaseCommand;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.List;

public abstract class BaseAlignCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to put the aligned data into.",
      required = true)
  public String datasetId;

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

  protected abstract AlignReadGroupSetsRequest getRequest();
  protected abstract List<String> getSources();

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    // Validate the dataset
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Putting aligned read group sets into: " + dataset.getName());

    Genomics.Readgroupsets.Align req = genomics.readgroupsets()
        .align(getRequest().setDatasetId(datasetId));
    String jobId = req.execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Aligning read group sets to " + dataset.getName() + " from "
        + Joiner.on(',').join(getSources()));
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Aligning job: ");
    printJob(job);
  }
}
