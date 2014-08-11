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
import com.google.api.services.genomics.model.Job;
import com.google.api.services.genomics.model.SearchJobsRequest;
import com.google.api.services.genomics.model.SearchJobsResponse;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ListJobsCommandTest extends CommandTest {

  @Test
  public void testListJobs_withoutStatus() throws Exception {
    ListJobsCommand command = new ListJobsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.addJobToHistory("jobid", "description");
    assertEquals(1, command.getLaunchedJobs().size());

    command.handleRequest(null /* should be unused */);

    String output = outContent.toString();
    assertTrue(output, output.contains("jobid: description"));
  }

  @Test
  public void testListJobs_withStatus() throws Exception {
    ListJobsCommand command = new ListJobsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());

    command.addJobToHistory("jobid", "description");
    command.includeStatus = true;

    Mockito.when(jobs.get("jobid")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(new Job().setStatus("pending"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("jobid: description"));
    assertTrue(output, output.contains("pending"));
  }

  @Test
  public void testListJobs_byProjectId() throws Exception {
    ListJobsCommand command = new ListJobsCommand();
    command.setDataStoreFactory(new MemoryDataStoreFactory());
    command.addJobToHistory("previousJob", "previousDescription");

    command.projectId = 9L;

    Mockito.when(jobs.search(new SearchJobsRequest().setProjectId(9L))).thenReturn(jobSearch);
    Mockito.when(jobSearch.execute()).thenReturn(new SearchJobsResponse().setJobs(
        Lists.newArrayList(new Job().setId("previousJob"), new Job().setId("newjob"))));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("previousJob: previousDescription"));
    assertTrue(output, output.contains("newjob: Unknown job type"));
  }

}
