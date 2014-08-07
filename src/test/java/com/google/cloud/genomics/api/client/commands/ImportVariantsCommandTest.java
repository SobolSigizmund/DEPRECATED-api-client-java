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
import com.google.api.services.genomics.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ImportVariantsCommandTest extends CommandTest {

  @Test
  public void testScopes() throws Exception {
    assertTrue(new ImportVariantsCommand().getScopes()
        .contains(GenomicsScopes.DEVSTORAGE_READ_WRITE));
  }

  @Test
  public void testImportVariants() throws Exception {
    ImportVariantsCommand command = new ImportVariantsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.vcfFiles = Lists.newArrayList("uri1", "uri2");
    command.datasetId = "abc";

    // Get the dataset
    Mockito.when(datasets.get("abc")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(
        new Dataset().setId("abc").setName("1kg"));

    // Import them
    Mockito.when(variants.genomicsImport(Mockito.any(ImportVariantsRequest.class)))
        .thenReturn(variantImport);
    Mockito.when(variantImport.execute()).thenReturn(
        new ImportVariantsResponse().setJobId("8675309"));

    // Get the job
    Mockito.when(jobs.get("8675309")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(
        new Job().setDescription("description1").setStatus("success"));

    // Get the variant summayry
    Mockito.when(variants.getSummary()).thenReturn(variantSummary);
    Mockito.when(variantSummary.setDatasetId("abc")).thenReturn(variantSummary);
    Mockito.when(variantSummary.execute()).thenReturn(new GetVariantsSummaryResponse()
        .setContigBounds(Lists.newArrayList(new ContigBound().setContig("contigX"))));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Importing variants into: 1kg"));
    assertTrue(output, output.contains("Import job:"));
    assertTrue(output, output.contains("description1"));
    assertTrue(output, output.contains("Imported variant summary:"));
    assertTrue(output, output.contains("contigX"));
  }

}
