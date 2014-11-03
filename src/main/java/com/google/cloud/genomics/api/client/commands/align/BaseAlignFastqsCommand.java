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
import com.google.api.services.genomics.model.FastqMetadata;

public abstract class BaseAlignFastqsCommand extends BaseAlignCommand {

  @Parameter(names = "--read_group_name",
      description = "Optionally specifies the read group name.")
  public String readGroupName;

  @Parameter(names = "--library_name",
      description = "Optionally specifies the library name.")
  public String libraryName;

  @Parameter(names = "--platform_name",
      description = "Optionally specifies the platform name. For example: CAPILLARY, LS454," +
          " ILLUMINA, SOLID, HELICOS, IONTORRENT, PACBIO.")
  public String platformName;

  @Parameter(names = "--platform_unit",
      description = "Optionally specifies the platform unit. For example: flowcell-barcode.lane" +
          " for Illumina or slide for SOLID.")
  public String platformUnit;

  @Parameter(names = "--sample_name",
      description = "Optionally specifies the sample name.")
  public String sampleName;

  protected FastqMetadata getMetadata() {
    return new FastqMetadata()
        .setLibraryName(libraryName)
        .setPlatformName(platformName)
        .setPlatformUnit(platformUnit)
        .setReadGroupName(readGroupName)
        .setSampleName(sampleName);
  }
}
