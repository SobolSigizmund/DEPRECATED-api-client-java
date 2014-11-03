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
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.CallReadGroupSetsRequest;
import com.google.api.services.genomics.model.CallReadGroupSetsResponse;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CallVariantsCommandTest extends CommandTest {

  @Test
  public void testScopes() throws Exception {
    assertTrue(new CallVariantsCommand().getScopes()
        .contains(GenomicsScopes.DEVSTORAGE_READ_WRITE));
  }

  @Test
  public void testCalling_missingSource() throws Exception {
    CallVariantsCommand command = new CallVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());
    command.datasetId = "abc";

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("must be provided"));
  }

  @Test
  public void testCalling_tooManyParams() throws Exception {
    CallVariantsCommand command = new CallVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());
    command.datasetId = "abc";
    command.readGroupSetIds = Lists.newArrayList("rgs");
    command.bamFiles = Lists.newArrayList("uri1");

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("You can not provide both"));
  }

  @Test
  public void testCalling() throws Exception {
    CallVariantsCommand command = new CallVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.bamFiles = Lists.newArrayList("uri1");
    command.datasetId = "abc";

    Mockito.when(datasets.get("abc")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(
        new Dataset().setId("abc").setName("1kg"));

    Mockito.when(readsets.call(new CallReadGroupSetsRequest()
        .setDatasetId("abc").setSourceUris(Lists.newArrayList("uri1"))))
        .thenReturn(readsetCall);
    Mockito.when(readsetCall.execute()).thenReturn(
        new CallReadGroupSetsResponse().setJobId("8675309"));

    // Get the job
    Mockito.when(jobs.get("8675309")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(
        new Job().setDetailedStatus("description1").setImportedIds(Lists.newArrayList("r1")));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("called variants into: 1kg"));
    assertTrue(output, output.contains("Variant calling job:"));
    assertTrue(output, output.contains("description1"));
  }

}
