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

import java.io.IOException;
import java.util.Map;

@Parameters(commandDescription = "List past jobs run by this command line")
public class ListJobsCommand extends BaseCommand {

  @Parameter(names = "--status",
      description = "Whether to look up the Job statuses")
  public boolean includeStatus = false;

  @Override
  public void handleRequest(Genomics genomics)
      throws IOException {

    Map<String, String> launchedJobs = getLaunchedJobs();
    for (Map.Entry<String, String> job : launchedJobs.entrySet()) {
      System.out.println(job.getKey() + ": " + job.getValue());

      if (includeStatus) {
        System.out.println(getJob(genomics, job.getKey(), false).toPrettyString() + "\n");
      }

    }
  }
}
