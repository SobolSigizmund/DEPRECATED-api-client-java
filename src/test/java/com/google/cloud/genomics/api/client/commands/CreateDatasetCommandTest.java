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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CreateDatasetCommandTest extends CommandTest {

  @Test
  public void testCreateDataset() throws Exception {
    CreateDatasetCommand command = new CreateDatasetCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.projectNumber = 5L;
    command.name = "dataset";

    Dataset expectedDataset = new Dataset().setName("dataset")
        .setProjectNumber(5L).setIsPublic(false);

    Mockito.when(datasets.create(expectedDataset)).thenReturn(datasetCreate);
    Mockito.when(datasetCreate.execute()).thenReturn(expectedDataset.clone().setId("id"));

    command.handleRequest(genomics);

    Map<String,String> previousDatasets = command.getPreviousDatasets();
    assertEquals(1, previousDatasets.size());
    assertEquals("dataset", previousDatasets.get("id"));

    String output = outContent.toString();
    assertTrue(output, output.contains("The new dataset was created"));
    assertTrue(output, output.contains("id"));
  }

}
