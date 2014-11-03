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
import com.google.api.services.genomics.model.PairedFastqSource;
import com.google.common.collect.Lists;

import java.util.List;

@Parameters(commandDescription = "Align paired end FASTQs from Google Cloud Storage")
public class AlignPairedFastqsCommand extends BaseAlignFastqsCommand {

  @Parameter(names = "--first_fastq_file",
      description = "A paired end FASTQ file (as Google Cloud Storage gs:// URL) to be" +
          " aligned. The first of each paired file should be specified here, in an order that" +
          " matches the second of each paired file specified by --second_fastq_file." +
          " You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.fastq) or use the flag multiple times if a wildcard" +
          " won't work.",
      required = true)
  public List<String> firstFastqFiles;

  @Parameter(names = "--second_fastq_file",
      description = "A paired end FASTQ file (as Google Cloud Storage gs:// URL) to be" +
          " aligned. The second of each paired file should be specified here, in an order that" +
          " matches the first of each paired file specified by --first_fastq_file." +
          " You can use a wildcard in this flag to specify multiple files at once" +
          " (e.g. gs://mybucket/myfiles/*.fastq) or use the flag multiple times if a wildcard" +
          " won't work.",
      required = true)
  public List<String> secondFastqFiles;

  @Override
  protected AlignReadGroupSetsRequest getRequest() {
    return new AlignReadGroupSetsRequest()
        .setPairedFastqSource(new PairedFastqSource()
            .setFirstSourceUris(firstFastqFiles)
            .setSecondSourceUris(secondFastqFiles)
            .setMetadata(getMetadata()));
  }

  @Override
  protected List<String> getSources() {
    List<String> sources = Lists.newArrayList(firstFastqFiles);
    sources.addAll(secondFastqFiles);
    return sources;
  }
}
