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
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.cloud.genomics.utils.Paginator;
import com.google.common.collect.Lists;

import java.io.IOException;

@Parameters(commandDescription = "Search over read group sets")
public class SearchReadsetsCommand extends SearchCommand {

  @Parameter(names = "--dataset_id",
      description = "The Genomics API dataset ID to get read group sets for.",
      required = true)
  public String datasetId;

  @Parameter(names = "--name",
      description = "Only return read group sets for which a substring of the " +
          "name matches this string.")
  public String name;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {

    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Getting read group sets from: " + dataset.getName());

    SearchReadGroupSetsRequest request = new SearchReadGroupSetsRequest()
        .setDatasetIds(Lists.newArrayList(datasetId))
        .setName(name)
        .setPageSize(getMaxResults());
    printResults(Paginator.ReadGroupSets.create(genomics), request);
  }
}
