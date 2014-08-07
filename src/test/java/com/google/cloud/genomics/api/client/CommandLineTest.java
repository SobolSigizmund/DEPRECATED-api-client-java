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
package com.google.cloud.genomics.api.client;

import com.beust.jcommander.MissingCommandException;
import com.google.cloud.genomics.api.client.commands.ListJobsCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(JUnit4.class)
public class CommandLineTest {

  @Test
  public void testSetArgs_noCommand() throws Exception {
    CommandLine commandLine = new CommandLine();

    try {
    commandLine.setArgs(new String[]{""});
    } catch (MissingCommandException e) {
      // Expected
      return;
    }
    fail();
  }

  @Test
  public void testSetArgs() throws Exception {
    CommandLine commandLine = new CommandLine();
    commandLine.setArgs(new String[]{"listjobs"});
    assertTrue(commandLine.getCommand() instanceof ListJobsCommand);
  }
}
