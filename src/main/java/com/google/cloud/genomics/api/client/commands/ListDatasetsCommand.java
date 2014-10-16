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
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.ListDatasetsResponse;

import java.io.IOException;
import java.util.Map;

@Parameters(commandDescription = "List recent datasets used by this command line, or list them " +
    "by project number")
public class ListDatasetsCommand extends BaseCommand {

  @Parameter(names = "--details",
      description = "Whether to display the full details of each dataset " +
          "(besides just name and ID)")
  public boolean includeDetails = false;

  @Parameter(names = {"--project_number", "--project_id"},
      description = "Get datasets for a specific Google Developer's Console project number " +
          "(By default only recently used datasets will be shown)")
  public Long projectId;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {

    if (projectId != null) {
      ListDatasetsResponse datasets = genomics.datasets().list().setProjectId(projectId).execute();
      if (datasets.getDatasets() == null) {
        return;
      }
      for (Dataset dataset : datasets.getDatasets()) {
        printDataset(genomics, dataset, includeDetails);
      }

    } else {
      Map<String, String> datasets = getPreviousDatasets();
      if (datasets.isEmpty()) {
        System.out.println("There aren't any recently used datasets. " +
            "Specify a --project_id flag to get datasets for a specific project, or use the " +
            "'createdataset' command to make a new dataset.\n" +
            "You can find Google Genomics Public Data in project 761052378059");
        return;
      }

      for (Map.Entry<String, String> dataset : datasets.entrySet()) {
        printDataset(genomics, dataset.getKey(), dataset.getValue(), null, includeDetails);
      }
    }
  }
}
