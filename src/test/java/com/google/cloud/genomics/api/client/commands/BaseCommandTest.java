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
import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.GenomicsScopes;
import com.google.api.services.genomics.model.Dataset;
import com.google.api.services.genomics.model.Job;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BaseCommandTest {
  private BaseCommand command;

  @Before
  public void setup() {
    command = new BaseCommand() {
      @Override
      public void handleRequest(Genomics genomics) throws IOException {}
    };
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
    MemoryDataStoreFactory factory = new MemoryDataStoreFactory();
    command.setDataStoreFactory(factory);

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
    MemoryDataStoreFactory factory = new MemoryDataStoreFactory();
    command.setDataStoreFactory(factory);

    assertTrue(command.getLaunchedJobs().isEmpty());

    command.addJobToHistory("j1", "name1");

    Map<String,String> jobs = command.getLaunchedJobs();
    assertEquals(1, jobs.size());
    assertEquals("name1", jobs.get("j1"));
  }
}
