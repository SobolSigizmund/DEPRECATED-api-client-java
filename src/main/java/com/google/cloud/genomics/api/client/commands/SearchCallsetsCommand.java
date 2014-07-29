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
import com.google.api.services.genomics.model.SearchCallsetsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Search over callsets")
public class SearchCallsetsCommand extends SimpleCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to get callsets for.",
      required = true)
  public String datasetId;

  @Parameter(names = "--name",
      description = "Only return callsets for which a substring of the name matches this string.")
  public String name;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {

    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Getting callsets from: " + dataset.getName());

    List<String> datasetIds = new ArrayList<>();
    datasetIds.add(datasetId);

    SearchCallsetsRequest content = new SearchCallsetsRequest()
        .setDatasetIds(datasetIds)
        .setName(name);
    executeAndPrint(genomics.callsets().search(content));
  }
}
