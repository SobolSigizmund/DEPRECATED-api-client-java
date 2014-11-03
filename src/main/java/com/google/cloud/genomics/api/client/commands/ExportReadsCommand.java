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
import com.google.api.services.genomics.model.ExportReadGroupSetsRequest;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Export read group sets into Google Cloud Storage")
public class ExportReadsCommand extends BaseCommand {

  private static final Function<ReadGroupSet,String> GET_NAME
      = new Function<ReadGroupSet, String>() {
    @Override
    public String apply(ReadGroupSet readset) {
      return readset.getName();
    }
  };

  @Parameter(names = "--project_number",
      description = "The Google Developer's Console project number that " +
          "will be billed for this export.",
      required = true)
  public Long projectNumber;

  @Parameter(names = "--read_group_set_id",
      description = "The IDs of the read group sets to export.",
      required = true)
  public List<String> readGroupSetIds;

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

    // Validate the read group sets
    List<ReadGroupSet> readGroupSets = Lists.newArrayList();
    for (String id : readGroupSetIds) {
      try {
        readGroupSets.add(genomics.readgroupsets().get(id).execute());
      } catch (GoogleJsonResponseException e) {
        String message = e.getDetails() == null ? "" : e.getDetails().getMessage();
        System.out.println("The read group set ID " + id + " won't work: "
            + message + "\n");
        return;
      }
    }

    String readsetNames = Joiner.on(',').join(Lists.transform(readGroupSets, GET_NAME));
    System.out.println("Exporting read group sets " + readsetNames);

    ExportReadGroupSetsRequest request = new ExportReadGroupSetsRequest()
        .setExportUri(exportUri)
        .setReadGroupSetIds(readGroupSetIds)
        .setProjectNumber(projectNumber);
    String jobId = genomics.readgroupsets().export(request).execute().getJobId();

    // Get the resulting job
    addJobToHistory(jobId, "Exporting read group sets: " + readsetNames + " to "
        + exportUri);
    System.out.println("Export job: ");
    printJob(getJob(genomics, jobId, pollForStatus));
  }
}
