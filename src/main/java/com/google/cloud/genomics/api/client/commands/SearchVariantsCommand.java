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
import com.beust.jcommander.internal.Lists;
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.*;
import com.google.cloud.genomics.utils.Paginator;
import com.google.cloud.genomics.utils.Paginator.ShardBoundary;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Search over variants")
public class SearchVariantsCommand extends SearchCommand {

  @Parameter(names = {"--id", "--variant_set_id"},
      description = "The Genomics API variant set ID to get variants for.",
      required = true)
  public String datasetId;

  @Parameter(names = "--call_set_id",
      description = "Only return calls from these call sets.")
  public List<String> callSetIds;

  @Parameter(names = "--call_set_name",
      description = "An alternative to call_set_id, only return calls from call sets " +
          "with these names.")
  public List<String> callSetNames;

  @Parameter(names = "--reference_name",
      description = "The sequence name to query over (e.g. 'X', '23')",
      required = true)
  public String referenceName;

  @Parameter(names = "--start",
      description = "The start position (0-based) of this query.",
      required = true)
  public Long start;

  @Parameter(names = "--end",
      description = "The end position (0-based, exclusive) of this query.",
      required = true)
  public Long end;

  @Override
  public void handleRequest(Genomics genomics) throws IOException {
    Dataset dataset = getDataset(genomics, datasetId);
    if (dataset == null) {
      return;
    }
    System.out.println("Getting variants from: " + dataset.getName());

    if (callSetNames != null) {
      getCallSetsByName(genomics);
      if (callSetIds.isEmpty()) {
        // We couldn't find any valid call sets to query
        return;
      }
    }

    SearchVariantsRequest request = new SearchVariantsRequest()
        .setVariantSetIds(Lists.newArrayList(datasetId))
        .setReferenceName(referenceName)
        .setStart(start)
        .setEnd(end)
        .setPageSize(getMaxResults());

    if (callSetIds != null) {
      request.setCallSetIds(callSetIds);
    }

    printResults(Paginator.Variants.create(genomics, ShardBoundary.OVERLAPS),
                 request);
  }

  private void getCallSetsByName(Genomics genomics) throws IOException {
    List<String> datasetIds = Lists.newArrayList();
    datasetIds.add(datasetId);

    if (callSetIds == null) {
      callSetIds = Lists.newArrayList();
    }

    for (String name : callSetNames) {
      SearchCallSetsRequest request = new SearchCallSetsRequest()
          .setVariantSetIds(datasetIds).setName(name);
      List<CallSet> callSets = genomics.callsets().search(request).execute().getCallSets();
      if (callSets == null || callSets.isEmpty()) {
        System.out.println("No call sets found with the name " + name);
        continue;
      }
      for (CallSet callSet : callSets) {
        callSetIds.add(callSet.getId());
      }
    }
  }
}
