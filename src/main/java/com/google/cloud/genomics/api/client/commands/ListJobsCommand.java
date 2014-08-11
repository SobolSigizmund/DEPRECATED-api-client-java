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
import com.google.api.services.genomics.model.SearchJobsRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Parameters(commandDescription = "List past jobs run by this command line, or list them " +
    "by project number")
public class ListJobsCommand extends BaseCommand {

  @Parameter(names = "--status",
      description = "Whether to look up the Job statuses")
  public boolean includeStatus = false;

  @Parameter(names = {"--project_number", "--project_id"},
      description = "Get jobs for a specific Google Developer's Console project number " +
          "(By default only jobs created from this command line will be shown)")
  public Long projectId;

  @Parameter(names = {"--created_after", "--after"},
      description = "When searching by project, get jobs created after this date " +
          "(Format: yyyy-MM-dd)")
  public Date createdAfter;

  @Parameter(names = {"--created_before", "--before"},
      description = "When searching by project, get jobs created before this date " +
          "(Format: yyyy-MM-dd)")
  public Date createdBefore;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    Map<String, String> launchedJobs = getLaunchedJobs();

    if (projectId != null) {
      SearchJobsRequest request = new SearchJobsRequest().setProjectId(projectId);
      if (createdAfter != null) {
        request.setCreatedAfter(createdAfter.getTime());
      }
      if (createdBefore != null) {
        request.setCreatedBefore(createdBefore.getTime());
      }

      List<Job> jobs = genomics.jobs().search(request).execute().getJobs();
      if (jobs == null || jobs.isEmpty()) {
        System.out.println("No jobs found");
        return;
      }

      for (Job job : jobs) {
        printJob(genomics, job.getId(), getDescription(job, launchedJobs), job);
      }

    } else {
      if (createdAfter != null || createdBefore != null) {
        System.out.println("Filtering jobs by date is only supported when searching by project.");
        return;
      }

      for (Map.Entry<String, String> job : launchedJobs.entrySet()) {
        printJob(genomics, job.getKey(), job.getValue(), null);
      }
    }
  }

  private String getDescription(Job job, Map<String, String> launchedJobs) {
    if (launchedJobs != null && launchedJobs.containsKey(job.getId())) {
      return launchedJobs.get(job.getId());
    }

    // TODO: Do a better job of describing a job via the API
    return "Unknown job type";
  }

  private void printJob(Genomics genomics, String id, String description, Job job)
      throws IOException {
    System.out.println(id + ": " + description);
    if (!includeStatus) {
      return;
    }

    if (job == null) {
      job = getJob(genomics, id, false);
    }

    System.out.println(job.toPrettyString() + "\n");
  }
}
