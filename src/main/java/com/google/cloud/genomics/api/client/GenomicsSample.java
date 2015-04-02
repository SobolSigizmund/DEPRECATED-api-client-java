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

import com.beust.jcommander.ParameterException;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.cloud.genomics.api.client.commands.BaseCommand;
import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.common.base.Suppliers;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Genomics Java client sample application.
 */
public class GenomicsSample {
  public static void main(String[] args) throws IOException {
    CommandLine cmdLine = new CommandLine();

    try {
      // Parse the command line
      cmdLine.setArgs(args);

      // Authorization
      BaseCommand command = cmdLine.getCommand();
      List<String> scopes = command.getScopes();
      VerificationCodeReceiver receiver = command.noLocalServer ? new GooglePromptReceiver() :
          new LocalServerReceiver();

      GenomicsFactory genomicsFactory = GenomicsFactory.builder("genomics_java_client")
          .setScopes(scopes)
          .setUserName("user" + scopes.toString())
          .setVerificationCodeReceiver(Suppliers.ofInstance(receiver))
          .setRootUrl(command.rootUrl)
          .setServicePath("/")
          .build();

      File clientSecrets = new File(command.clientSecretsFilename);
      if (!clientSecrets.exists()) {
        System.err.println(
            "Client secrets file " + command.clientSecretsFilename + " does not exist."
            + " Visit https://cloud.google.com/genomics/install-genomics-tools#authenticate to learn how"
            + " to install a client_secrets.json file.  If you have installed a client_secrets.json"
            + " in a specific location, use --client_secrets_filename <path>/client_secrets.json.");
        return;
      }

      File dataStoreFile = new File(System.getProperty("user.home"), ".store/genomics_java_client");
      command.setDataStoreFactory(new ReadableFileDataStoreFactory(dataStoreFile));
      command.handleRequest(genomicsFactory.fromClientSecretsFile(clientSecrets));

    } catch (IllegalArgumentException | ParameterException e) {
      cmdLine.printHelp(e.getMessage() + "\n", System.out);
    } catch (GoogleJsonResponseException e) {
      System.out.println("API request failed: " + BaseCommand.getErrorMessage(e));
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
