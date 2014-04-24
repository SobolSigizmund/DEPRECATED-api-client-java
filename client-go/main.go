// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package main

import (
	"bufio"
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strings"

	"code.google.com/p/goauth2/oauth"

	genomics "github.com/GoogleCloudPlatform/genomics-tools/client-go/v1beta"
)

var (
	oauthJsonFile = flag.String("use_oauth", "",
		"Path to client_secrets.json")
)

func obtainOauthCode(url string) string {
	fmt.Println("Please visit the below URL to obtain OAuth2 code.")
	fmt.Println()
	fmt.Println(url)
	fmt.Println()
	fmt.Println("Please enter the code here:")

	line, _, _ := bufio.NewReader(os.Stdin).ReadLine()

	return string(line)
}

func prepareClient() (*http.Client, error) {
	if *oauthJsonFile == "" {
		return &http.Client{}, nil
	}

	jsonData, err := ioutil.ReadFile(*oauthJsonFile)
	if err != nil {
		return nil, err
	}

	var data struct {
		Installed struct {
			Client_Id     string
			Client_Secret string
			Redirect_Uris []string
			Auth_Uri      string
			Token_Uri     string
		}
	}
	err = json.Unmarshal(jsonData, &data)
	if err != nil {
		return nil, err
	}

	config := &oauth.Config{
		ClientId:     data.Installed.Client_Id,
		ClientSecret: data.Installed.Client_Secret,
		RedirectURL:  data.Installed.Redirect_Uris[0],
		Scope: strings.Join([]string{
			"https://www.googleapis.com/auth/genomics",
			"https://www.googleapis.com/auth/devstorage.read_write",
		}, " "),
		AuthURL:    data.Installed.Auth_Uri,
		TokenURL:   data.Installed.Token_Uri,
		TokenCache: oauth.CacheFile(".oauth2_cache.json"),
	}

	transport := &oauth.Transport{Config: config}
	token, err := config.TokenCache.Token()
	if err != nil {
		url := config.AuthCodeURL("")
		code := obtainOauthCode(url)
		token, err = transport.Exchange(code)
		if err != nil {
			return nil, err
		}
	}

	transport.Token = token
	client := transport.Client()

	return client, nil
}

func main() {
	flag.Parse()
	client, err := prepareClient()
	if err != nil {
		log.Fatal(err)
	}
	baseApi, err := genomics.New(client)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println("Searching for readsets in 1000 Genomes project...")
	readsets, err := baseApi.Readsets.Search(&genomics.SearchReadsetsRequest{
		DatasetIds: []string{"376902546192"},
	}).Do()
	if err != nil {
		log.Fatal(err)
	}
	readset := readsets.Readsets[0]
	readsetId := readset.Id
	fmt.Printf("The first readset ID is %q.\n", readsetId)

	_, err = baseApi.Readsets.Get(readsetId).Do()
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println("Searching for reads in the first sequence...")
	sequenceName := readset.FileData[0].RefSequences[0].Name
	tok := ""
	for i := 1; i <= 2; i++ {
		resp, err := baseApi.Reads.Search(&genomics.SearchReadsRequest{
			PageToken:     tok,
			ReadsetIds:    []string{readsetId},
			SequenceName:  sequenceName,
			SequenceStart: 1,
			SequenceEnd:   ^uint64(0),
		}).Do()
		if err != nil {
			log.Fatal(err)
		}
		fmt.Printf("Found %v reads in page %v:\n", len(resp.Reads), i)
		for _, read := range resp.Reads {
			fmt.Printf("\tId: %v\tName: %v\n", read.Id, read.Name)
		}
		tok = resp.NextPageToken
		fmt.Printf("Next page token is %q.\n", tok)
	}
}
