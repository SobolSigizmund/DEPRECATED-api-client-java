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
import com.google.api.client.json.GenericJson;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.SearchJobsRequest;
import com.google.cloud.genomics.utils.Paginator;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Parameters(commandDescription = "List past jobs run by this command line, or list them " +
    "by project number")
public class ListJobsCommand extends SearchCommand {

  @Parameter(names = "--status",
      description = "This field is deprecated. The value is ignored.")
  public boolean includeStatus = false;

  @Parameter(names = "--project_number",
      description = "Get jobs for a specific Google Developer's Console project number " +
          "(By default only jobs created from this command line will be shown)")
  public Long projectNumber;

  @Parameter(names = {"--created_after", "--after"},
      description = "When searching by project, get jobs created after this date " +
          "(Format: yyyy-MM-dd)")
  public Date createdAfter;

  @Parameter(names = {"--created_before", "--before"},
      description = "When searching by project, get jobs created before this date " +
          "(Format: yyyy-MM-dd)")
  public Date createdBefore;

  @Override
  protected <E extends GenericJson> void prepareResult(E job) {
    if (job instanceof Job) {
      // TODO: Generify this
      Long created = ((Job) job).getCreated();
      if (created != null) {
        job.set("createdString", DATE_FORMAT.format(new Date(created)));
      }
    }
  }

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    if (projectNumber != null) {
      SearchJobsRequest request = new SearchJobsRequest()
          .setProjectNumber(projectNumber)
          .setPageSize(getMaxResults());
      if (createdAfter != null) {
        request.setCreatedAfter(createdAfter.getTime());
      }
      if (createdBefore != null) {
        request.setCreatedBefore(createdBefore.getTime());
      }

      printResults(Paginator.Jobs.create(genomics), request);

    } else {
      System.out.println("Listing jobs recently run by this command line. " +
          "Use --project_number to get more specific details on jobs and to search by date.\n");

      if (createdAfter != null || createdBefore != null) {
        System.out.println("Filtering jobs by date is only supported when searching by project.");
        return;
      } else if (fields != null) {
        System.out.println("--fields is only supported when searching by project.");
        return;
      }

      Map<String, String> launchedJobs = getLaunchedJobs();
      if (launchedJobs.isEmpty()) {
        System.out.println("No recent jobs found. Try searching by project instead.");
      }

      for (Map.Entry<String, String> job : launchedJobs.entrySet()) {
        System.out.println(job.getKey() + ": " + job.getValue());
      }
    }
  }

}
