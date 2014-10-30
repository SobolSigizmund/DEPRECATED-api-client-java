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

import com.beust.jcommander.internal.Lists;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.ListDatasetsResponse;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.ReferenceBound;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsResponse;
import com.google.api.services.genomics.model.VariantSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ListDatasetsCommandTest extends CommandTest {

  @Test
  public void testListDatasets_noneAvailable() throws Exception {
    ListDatasetsCommand command = new ListDatasetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.handleRequest(null /* should be unused */);

    String output = outContent.toString();
    assertTrue(output, output.contains("There aren't any"));
  }

  @Test
  public void testListDatasets_withoutDetails() throws Exception {
    ListDatasetsCommand command = new ListDatasetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.addDatasetToHistory(new Dataset().setName("name").setId("id"));
    command.handleRequest(null /* should be unused */);

    String output = outContent.toString();
    assertTrue(output, output.contains("name (ID: id)"));
  }

  @Test
  public void testListDatasets_withDetails() throws Exception {
    ListDatasetsCommand command = new ListDatasetsCommand();
    command.projectNumber = 10L;
    command.includeDetails = true;

    Mockito.when(datasets.list()).thenReturn(datasetList);
    Mockito.when(datasetList.setProjectNumber(10L)).thenReturn(datasetList);
    Mockito.when(datasetList.execute()).thenReturn(new ListDatasetsResponse()
        .setDatasets(Lists.newArrayList(
            new Dataset().setId("id1").setName("name").setProjectNumber(1234L))));

    // Readset summary
    Mockito.when(readsets.search(new SearchReadGroupSetsRequest()
        .setDatasetIds(Lists.newArrayList("id1")).setPageSize(100)))
        .thenReturn(readsetSearch);
    Mockito.when(readsetSearch.execute()).thenReturn(new SearchReadGroupSetsResponse()
        .setReadGroupSets(Lists.newArrayList(new ReadGroupSet(), new ReadGroupSet())));

    // Variant set
    Mockito.when(variantSets.get("id1")).thenReturn(variantSetGet);
    Mockito.when(variantSetGet.execute()).thenReturn(new VariantSet().setReferenceBounds(
        Lists.newArrayList(new ReferenceBound().setReferenceName("contigX"))));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("name (ID: id1)"));
    assertTrue(output, output.contains("1234"));
    assertTrue(output, output.contains("Read group sets: 2"));
    assertTrue(output, output.contains("Variant set:"));
    assertTrue(output, output.contains("contigX"));
  }

  @Test
  public void testListDatasets_withDeletedData() throws Exception {
    ListDatasetsCommand command = new ListDatasetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    Dataset dataset = new Dataset().setName("name1").setId("id");
    command.addDatasetToHistory(dataset);
    command.addDatasetToHistory(new Dataset().setName("name2").setId("deleted"));
    command.includeDetails = true;


    Mockito.when(datasets.get(Mockito.anyString())).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute())
        .thenReturn(dataset)
        .thenThrow(GoogleJsonResponseException.class);

    // Readset summary
    Mockito.when(readsets.search(Mockito.any(SearchReadGroupSetsRequest.class)))
        .thenReturn(readsetSearch);
    Mockito.when(readsetSearch.execute()).thenReturn(new SearchReadGroupSetsResponse());

    // Variant set
    Mockito.when(variantSets.get(Mockito.anyString())).thenReturn(variantSetGet);
    Mockito.when(variantSetGet.execute()).thenReturn(new VariantSet());

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("name1 (ID: id)"));
    assertTrue(output, output.contains("name2 (ID: deleted)"));
    assertTrue(output, output.contains("Dataset not found"));
  }

  @Test
  public void testListDatasets_fromEmptyProject() throws Exception {
    ListDatasetsCommand command = new ListDatasetsCommand();
    command.projectNumber = 10L;

    Mockito.when(datasets.list()).thenReturn(datasetList);
    Mockito.when(datasetList.setProjectNumber(10L)).thenReturn(datasetList);
    Mockito.when(datasetList.execute()).thenReturn(new ListDatasetsResponse());
    command.handleRequest(genomics);
  }

}
