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
import com.google.api.services.genomics.model.Dataset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GetDatasetsCommandTest extends CommandTest {

  @Test
  public void testGetDatasets() throws Exception {
    GetDatasetsCommand command = new GetDatasetsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.datasetIds = Lists.newArrayList("d1", "d2");

    Dataset dataset1 = new Dataset().setId("d1").setName("name1");
    Dataset dataset2 = new Dataset().setId("d2").setName("name2");

    Mockito.when(datasets.get("d1")).thenReturn(datasetGet);
    Mockito.when(datasets.get("d2")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenReturn(dataset1, dataset2);

    command.handleRequest(genomics);

    Map<String,String> previousDatasets = command.getPreviousDatasets();
    assertEquals(2, previousDatasets.size());
    assertEquals("name1", previousDatasets.get("d1"));
    assertEquals("name2", previousDatasets.get("d2"));
  }

}
