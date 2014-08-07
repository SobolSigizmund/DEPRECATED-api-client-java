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
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BaseCommandTest extends CommandTest {
  private BaseCommand command;

  @Before
  public void setup() {
    command = new BaseCommand() {
      @Override
      public void handleRequest(Genomics genomics) throws IOException {}
    };
    MemoryDataStoreFactory factory = new MemoryDataStoreFactory();
    command.setDataStoreFactory(factory);
  }

  @Test
  public void testIsJobFinished() throws Exception {
    Job job = new Job();
    assertFalse(command.isJobFinished(job));

    job.setStatus("running");
    assertFalse(command.isJobFinished(job));

    job.setStatus("success");
    assertTrue(command.isJobFinished(job));

    job.setStatus("failure");
    assertTrue(command.isJobFinished(job));
  }

  @Test
  public void testScopes() throws Exception {
    assertEquals(Lists.newArrayList(GenomicsScopes.GENOMICS), command.getScopes());
  }

  @Test
  public void testDatasetHistory() throws Exception {
    assertTrue(command.getPreviousDatasets().isEmpty());

    Dataset dataset = new Dataset();
    dataset.setId("d1");
    dataset.setName("name1");
    command.addDatasetToHistory(dataset);

    Map<String,String> datasets = command.getPreviousDatasets();
    assertEquals(1, datasets.size());
    assertEquals("name1", datasets.get("d1"));
  }

  @Test
  public void testJobHistory() throws Exception {
    assertTrue(command.getLaunchedJobs().isEmpty());

    command.addJobToHistory("j1", "name1");

    Map<String,String> jobs = command.getLaunchedJobs();
    assertEquals(1, jobs.size());
    assertEquals("name1", jobs.get("j1"));
  }

  @Test
  public void testGetBadDataset_noHistory() throws Exception {
    Mockito.when(datasets.get("d1")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenThrow(GoogleJsonResponseException.class);

    assertEquals(null, command.getDataset(genomics, "d1"));

    String output = outContent.toString();
    Assert.assertTrue(output, output.contains("That datasetId won't work"));
    Assert.assertTrue(output, output.contains("1000 Genomes dataset ID"));
  }

  @Test
  public void testGetBadDataset_withHistory() throws Exception {
    Dataset dataset = new Dataset();
    dataset.setId("d1");
    dataset.setName("name1");
    command.addDatasetToHistory(dataset);

    Mockito.when(datasets.get("d2")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenThrow(GoogleJsonResponseException.class);

    assertEquals(null, command.getDataset(genomics, "d2"));

    String output = outContent.toString();
    Assert.assertTrue(output, output.contains("That datasetId won't work"));
    Assert.assertTrue(output, output.contains("d1: name1"));
  }

  @Test
  public void testGetBadDataset_withException() throws Exception {
    Mockito.when(datasets.get("d1")).thenReturn(datasetGet);
    Mockito.when(datasetGet.execute()).thenThrow(GoogleJsonResponseException.class);

    try {
     command.getDataset(genomics, "d1", false);
    } catch (GoogleJsonResponseException e) {
      // expected
      return;
    }

    fail();
  }
}
