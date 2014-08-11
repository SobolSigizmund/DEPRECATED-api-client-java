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

import com.google.api.services.genomics.model.Job;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GetJobsCommandTest extends CommandTest {

  @Test
  public void testGetJobs_withoutPolling() throws Exception {
    GetJobsCommand command = new GetJobsCommand();
    command.jobIds = Lists.newArrayList("j1", "j2");

    Mockito.when(jobs.get("j1")).thenReturn(jobGet);
    Mockito.when(jobs.get("j2")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(
        new Job().setDescription("description1"),
        new Job().setDescription("description2"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("description1"));
    assertTrue(output, output.contains("description2"));
  }

  @Test
  public void testGetJobs_withPolling() throws Exception {
    GetJobsCommand command = new GetJobsCommand();
    command.jobIds = Lists.newArrayList("j1");
    command.pollForStatus = true;
    command.pollingDelay = 10;

    Mockito.when(jobs.get("j1")).thenReturn(jobGet);
    Mockito.when(jobGet.execute()).thenReturn(
        new Job().setStatus("started").setId("j1"),
        new Job().setStatus("started"),
        new Job().setStatus("pending").setDescription("pending-description"),
        new Job().setStatus("pending"),
        new Job().setStatus("success").setDescription("description-done"));

    command.handleRequest(genomics);

    String output = outContent.toString();
    assertTrue(output, output.contains("Waiting for job: j1"));
    assertTrue(output, output.contains("..Job status changed: "));
    assertTrue(output, output.contains("pending-description"));
    assertTrue(output, output.contains(".."));
    assertTrue(output, output.contains("description-done"));

    assertFalse(output, output.contains("started"));
  }

}
