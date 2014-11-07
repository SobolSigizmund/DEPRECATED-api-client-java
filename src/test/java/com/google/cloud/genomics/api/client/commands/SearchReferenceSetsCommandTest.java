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

import com.google.api.services.genomics.model.ReferenceSet;
import com.google.api.services.genomics.model.SearchReferenceSetsRequest;
import com.google.api.services.genomics.model.SearchReferenceSetsResponse;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class SearchReferenceSetsCommandTest extends CommandTest {

  @Test
  public void testCommand() throws Exception {
    SearchReferenceSetsCommand command = new SearchReferenceSetsCommand();

    Mockito.when(referenceSets.search(Mockito.any(SearchReferenceSetsRequest.class)))
        .thenReturn(referenceSetSearch);
    Mockito.when(referenceSetSearch.execute()).thenReturn(new SearchReferenceSetsResponse()
        .setReferenceSets(Lists.<ReferenceSet>newArrayList()));

    command.handleRequest(genomics);
  }

}
