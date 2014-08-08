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
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.ExportReadsetsRequest;
import com.google.api.services.genomics.model.ExportReadsetsResponse;
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.Readset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ExportReadsetsCommandTest extends CommandTest {

  @Test
  public void testScopes() throws Exception {
    assertTrue(new ExportReadsetsCommand().getScopes()
        .contains(GenomicsScopes.DEVSTORAGE_READ_WRITE));
  }

  @Test
  public void testExportReadsets() throws Exception {
    ExportReadsetsCommand command = new ExportReadsetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.readsetIds = Lists.newArrayList("r1", "r2");
    command.projectId = 3L;
    command.exportUri = "exportme";

    // Get the readsets
    Mockito.when(readsets.get("r1")).thenReturn(readsetGet);
    Mockito.when(readsets.get("r2")).thenReturn(readsetGet);
    Mockito.when(readsetGet.execute()).thenReturn(
        new Readset().setName("name1"),
        new Readset().setName("name2"));

    // Export them
    Mockito.when(readsets.export(Mockito.any(ExportReadsetsRequest.class)))
        .thenReturn(readsetExport);
    Mockito.when(readsetExport.execute()).thenReturn(
        new ExportReadsetsResponse().setJobId("8675309"));

    // Get the job
    Mockito.when(jobs.get("8675309")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(new Job().setDescription("description1"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Exporting readsets name1,name2"));
    assertTrue(output, output.contains("Export job:"));
    assertTrue(output, output.contains("description1"));
  }

  @Test
  public void testExportReadsets_badIds() throws Exception {
    ExportReadsetsCommand command = new ExportReadsetsCommand();
    command.readsetIds = Lists.newArrayList("bad");

    // Get the readsets
    Mockito.when(readsets.get(Mockito.anyString())).thenThrow(GoogleJsonResponseException.class);
    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("The readset ID bad won't work"));
  }

}
