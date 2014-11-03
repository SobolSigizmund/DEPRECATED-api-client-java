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
package com.google.cloud.genomics.api.client.commands.align;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.api.services.genomics.model.AlignReadGroupSetsRequest;
import com.google.api.services.genomics.model.InterleavedFastqSource;

import java.util.List;

@Parameters(commandDescription = "Align interleaved FASTQs from Google Cloud Storage")
public class AlignInterleavedFastqsCommand extends BaseAlignFastqsCommand {

  @Parameter(names = "--fastq_file",
      description = "An interleaved FASTQ file (as Google Cloud Storage gs:// URL) to be" +
          " aligned. You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.fastq) or use the flag multiple times if a wildcard" +
          " won't work.",
      required = true)
  public List<String> fastqFiles;

  @Override
  protected AlignReadGroupSetsRequest getRequest() {
    return new AlignReadGroupSetsRequest()
        .setInterleavedFastqSource(new InterleavedFastqSource()
            .setSourceUris(fastqFiles)
            .setMetadata(getMetadata()));
  }

  @Override
  protected List<String> getSources() {
    return fastqFiles;
  }
}
