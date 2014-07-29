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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Joiner;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.ExportReadsetsRequest;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.Readset;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Export readsets from Google Genomics into Google Cloud Storage")
public class ExportReadsetsCommand extends BaseCommand {

  private static final Function<Readset,String> GET_NAME = new Function<Readset, String>() {
    @Override
    public String apply(Readset readset) {
      return readset.getName();
    }
  };

  @Parameter(names = "--project_id",
      description = "The ID of the project that will be billed for this export.",
      required = true)
  public Long projectId;

  @Parameter(names = "--readset_id",
      description = "The IDs of the readsets to export.",
      required = true)
  public List<String> readsetIds;

  @Parameter(names = "--export_uri",
      description = "A Google Cloud Storage gs:// URL where the exported readset BAM file " +
          "will be created.",
      required = true)
  public String exportUri;

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
    // TODO: Validate the project ID

    // Validate the readsets
    List<Readset> readsets = Lists.newArrayList();
    for (String readsetId : readsetIds) {
      try {
        readsets.add(genomics.readsets().get(readsetId).execute());
      } catch (GoogleJsonResponseException e) {
        System.err.println("The readset ID " + readsetId + " won't work: "
            + e.getDetails().getMessage() + "\n");
        return;
      }
    }

    String readsetNames = Joiner.on(',').join(Lists.transform(readsets, GET_NAME));
    System.out.println("Exporting readsets " + readsetNames);

    ExportReadsetsRequest request = new ExportReadsetsRequest()
        .setExportUri(exportUri)
        .setReadsetIds(readsetIds)
        .setProjectId(projectId);
    String jobId = genomics.readsets().export(request).execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Exporting readsets: " + readsetNames + " to "
        + exportUri);
    Job job = getJob(genomics, jobId, pollForStatus);
    System.out.println("Export job: " + job.toPrettyString());
  }
}
