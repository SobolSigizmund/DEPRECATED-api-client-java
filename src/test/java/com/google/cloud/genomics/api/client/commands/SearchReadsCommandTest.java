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

import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class SearchReadsCommandTest extends CommandTest {

  @Test
  public void testCommand_noRange() throws Exception {
    SearchReadsCommand command = new SearchReadsCommand();

    Mockito.when(reads.search(Mockito.any(SearchReadsRequest.class))).thenReturn(readSearch);
    Mockito.when(readSearch.execute()).thenReturn(new SearchReadsResponse());

    command.handleRequest(genomics);
  }

  @Test
  public void testCommand_withRange() throws Exception {
    SearchReadsCommand command = new SearchReadsCommand();
    command.sequenceStart = 1;
    command.sequenceEnd = 10;

    Mockito.when(reads.search(Mockito.any(SearchReadsRequest.class))).thenReturn(readSearch);
    Mockito.when(readSearch.execute()).thenReturn(new SearchReadsResponse());

    command.handleRequest(genomics);
  }

}
