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

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Get datasets by ID")
public class GetDatasetsCommand extends BaseCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID.",
      required = true)
  public List<String> datasetIds;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    for (String datasetId : datasetIds) {
      Dataset dataset = getDataset(genomics, datasetId);
      if (dataset != null) {
        System.out.println(dataset.toPrettyString() + "\n");
      }
    }
  }
}
