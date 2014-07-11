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
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.Job;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Get jobs by ID and optionally poll until they complete")
public class GetJobsCommand extends BaseCommand {

  @Parameter(names = "--job_id",
      description = "The Genomics API job ID.",
      required = true)
  public List<String> jobIds;

  @Parameter(names = "--poll",
      description = "If set, the client will query for job status " +
          "until it either finishes or fails.")
  public boolean pollForStatus = false;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    for (String jobId : jobIds) {
      Job job = getJob(genomics, jobId, pollForStatus);
      System.out.println(job.toPrettyString());
    }

    // TODO: Should we look up readsets/variants?
  }
}
