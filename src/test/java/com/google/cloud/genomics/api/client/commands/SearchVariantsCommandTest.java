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
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchCallSetsResponse;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.SearchVariantsResponse;
import com.google.api.services.genomics.model.Variant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SearchVariantsCommandTest extends CommandTest {

  @Test
  public void testSearchVariants() throws Exception {
    SearchVariantsCommand command = new SearchVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.datasetId = "dataset";
    command.sequenceName = "chr1";
    command.sequenceStart = 1L;
    command.sequenceEnd = 5L;

    Mockito.when(datasets.get("dataset")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(new Dataset().setId("id").setName("1kg"));

    Mockito.when(variants.search(Mockito.any(SearchVariantsRequest.class)))
        .thenReturn(variantSearch);
    Mockito.when(variantSearch.execute()).thenReturn(new SearchVariantsResponse()
        .setVariants(Lists.<Variant>newArrayList()));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Getting variants from: 1kg"));
  }

  @Test
  public void testSearchVariants_byCallsetName() throws Exception {
    SearchVariantsCommand command = new SearchVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.datasetId = "dataset";
    command.sequenceName = "chr1";
    command.sequenceStart = 1L;
    command.sequenceEnd = 5L;
    command.callSetNames = Lists.newArrayList("c1", "c2");

    Mockito.when(datasets.get("dataset")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(new Dataset().setId("id").setName("1kg"));

    Mockito.when(callsets.search(new SearchCallSetsRequest()
        .setVariantSetIds(Lists.newArrayList("dataset"))
        .setName("c1")))
        .thenReturn(callsetSearch);
    Mockito.when(callsets.search(new SearchCallSetsRequest()
        .setVariantSetIds(Lists.newArrayList("dataset"))
        .setName("c2")))
        .thenReturn(callsetSearch);
    Mockito.when(callsetSearch.execute()).thenReturn(
        new SearchCallSetsResponse().setCallSets(Lists.newArrayList(new CallSet().setId("id1"))),
        new SearchCallSetsResponse() /* No callset results for c2 */);

    Mockito.when(variants.search(new SearchVariantsRequest()
        .setVariantSetIds(Lists.newArrayList("dataset"))
        .setPageToken("")
        .setReferenceName("chr1")
        .setStart(1L)
        .setEnd(5L)
        .setCallSetIds(Lists.newArrayList("id1"))
        .setPageSize(10)))
        .thenReturn(variantSearch);
    Mockito.when(variantSearch.execute()).thenReturn(
        new SearchVariantsResponse().setVariants(
            Lists.newArrayList(new Variant().setReferenceName("contig"))));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("No call sets found with the name c2"));
    assertTrue(output, output.contains("contig"));
  }

  @Test
  public void testDeprecationWarning() throws Exception {
    SearchVariantsCommand command = new SearchVariantsCommand();
    command.pageToken = "xyz";

    command.handleRequest(null);
    String output = outContent.toString();
    assertTrue(output, output.contains("--page_token is now deprecated"));
  }

}
