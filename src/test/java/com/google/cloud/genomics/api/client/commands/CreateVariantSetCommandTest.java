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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.VariantSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Map;

@RunWith(JUnit4.class)
public class CreateVariantSetCommandTest extends CommandTest {

  @Test
  public void testCreateVariantSet() throws Exception {
    CreateVariantSetCommand command = new CreateVariantSetCommand();
    command.datasetId = "dsid";
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    Dataset dataset1 = new Dataset().setId("dsid").setName("name1");

    Mockito.when(datasets.get("dsid")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(dataset1);

    VariantSet expectedVariantSet = new VariantSet().setDatasetId("dsid");
    VariantSet responseVariantSet = new VariantSet().setDatasetId("dsid").setId("shiny");

    Mockito.when(variantSets.create(expectedVariantSet)).thenReturn(variantSetCreate);
    Mockito.when(variantSetCreate.execute()).thenReturn(responseVariantSet);

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Created variant set id: shiny"));
  }

}
