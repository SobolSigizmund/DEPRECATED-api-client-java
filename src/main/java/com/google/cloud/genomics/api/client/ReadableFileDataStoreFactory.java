/*
 * Copyright (c) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.genomics.api.client;

import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Maps;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An extension of the FileDataStoreFactory class which makes the saved files human-readable.
 * This allows users to hand edit the files if they wish.
 *
 * The cost of this readability is that the DataStores can only contain Strings.
 */
public class ReadableFileDataStoreFactory extends FileDataStoreFactory {

  public ReadableFileDataStoreFactory(File dataDirectory) throws IOException {
    super(dataDirectory);
  }

  @Override
  protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
    // Note: The ReadableFileDataStore only supports Strings
    return (DataStore<V>) new ReadableFileDataStore(this, getDataDirectory(), id);
  }

  // This class should extend the FileDataStore or AbstractMemoryDataStore
  // but unfortunately both are both private
  public static class ReadableFileDataStore extends AbstractDataStore<String> {

    private static final String SEPARATOR = "----";
    private final File dataFile;
    private HashMap<String, String> keyValueMap = Maps.newHashMap();

    ReadableFileDataStore(ReadableFileDataStoreFactory dataStore, File dataDirectory, String id)
        throws IOException {
      super(dataStore, id);
      this.dataFile = new File(dataDirectory, id);

      if (IOUtils.isSymbolicLink(dataFile)) {
        throw new IOException("unable to use a symbolic link: " + dataFile);
      }
      if (dataFile.createNewFile()) {
        // If necessary, create a new file
        save();

      } else {
        // Load credentials from existing file
        FileInputStream inputStream = new FileInputStream(dataFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        if (line == null) {
          return;
        }

        if (!line.contains(SEPARATOR)) {
          loadLegacyFile();
          return;
        }

        while (line != null) {
          String[] splits = line.split(SEPARATOR);
          keyValueMap.put(splits[0], splits[1]);
          line = reader.readLine();
        }
      }
    }

    void loadLegacyFile() throws IOException {
      HashMap<String, byte[]> oldKeyValueMap = IOUtils.deserialize(new FileInputStream(dataFile));
      for (Map.Entry<String, byte[]> entry : oldKeyValueMap.entrySet()) {
        keyValueMap.put(entry.getKey(), IOUtils.<String>deserialize(entry.getValue()));
      }
    }

    void save() throws IOException {
      PrintStream printStream = new PrintStream(new FileOutputStream(dataFile));
      for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
        printStream.println(entry.getKey() + SEPARATOR + entry.getValue());
      }

      printStream.close();
    }

    @Override
    public ReadableFileDataStoreFactory getDataStoreFactory() {
      return (ReadableFileDataStoreFactory) super.getDataStoreFactory();
    }

    @Override
    public Set<String> keySet() throws IOException {
      return keyValueMap.keySet();
    }

    @Override
    public Collection<String> values() throws IOException {
      return keyValueMap.values();
    }

    @Override
    public String get(String key) throws IOException {
      return keyValueMap.get(key);
    }

    @Override
    public DataStore<String> set(String key, String value) throws IOException {
      keyValueMap.put(key, value);
      save();
      return this;
    }

    @Override
    public DataStore<String> clear() throws IOException {
      keyValueMap.clear();
      save();
      return this;
    }

    @Override
    public DataStore<String> delete(String key) throws IOException {
      keyValueMap.remove(key);
      save();
      return this;
    }
  }

}
