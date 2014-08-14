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

import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Readset;
import com.google.api.services.genomics.model.SearchReadsetsRequest;
import com.google.api.services.genomics.model.SearchReadsetsResponse;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SearchReadsetsCommandTest extends CommandTest {

  @Test
  public void testCommand() throws Exception {
    SearchReadsetsCommand command = new SearchReadsetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.datasetId = "dataset";
    command.name = "12878";

    Mockito.when(datasets.get("dataset")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(new Dataset().setId("id").setName("1kg"));

    Mockito.when(readsets.search(new SearchReadsetsRequest()
        .setDatasetIds(Lists.newArrayList("dataset"))
        .setName("12878")
        .setMaxResults(BigInteger.TEN)))
        .thenReturn(readsetSearch);
    Mockito.when(readsetSearch.execute()).thenReturn(new SearchReadsetsResponse()
        .setReadsets(Lists.<Readset>newArrayList()));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Getting readsets from: 1kg"));
  }

}
