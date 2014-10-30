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
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ExportVariantsCommandTest extends CommandTest {

  @Test
  public void testScopes() throws Exception {
    assertTrue(new ExportVariantsCommand().getScopes()
        .contains(GenomicsScopes.BIGQUERY));
  }

  @Test
  public void testExportVariants() throws Exception {
    ExportVariantsCommand command = new ExportVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.variantSetId = "abc";
    command.projectNumber = 3L;
    command.bigqueryDataset = "bigqueryDataset";
    command.bigqueryTable = "bigqueryTable";

    // Get the dataset
    Mockito.when(datasets.get("abc")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(
        new Dataset().setId("abc").setName("1kg"));

    // Export them
    Mockito.when(variantSets.export(
        Mockito.anyString(), Mockito.any(ExportVariantSetRequest.class)))
        .thenReturn(variantExport);
    Mockito.when(variantExport.execute()).thenReturn(
        new ExportVariantSetResponse().setJobId("8675309"));

    // Get the job
    Mockito.when(jobs.get("8675309")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(new Job().setDetailedStatus("description1"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Exporting variants for variant set 1kg"));
    assertTrue(output, output.contains("Export job:"));
    assertTrue(output, output.contains("description1"));
  }

}
