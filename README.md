genomics-tools [![Build Status](https://travis-ci.org/GoogleCloudPlatform/genomics-tools.png?branch=master)](https://travis-ci.org/GoogleCloudPlatform/genomics-tools)
==============

The projects in this repository are focused around the <a href="https://developers.google.com/genomics">Google Genomics API</a>.

* **client-java** provides a command line interface for API queries, and demonstrates how a more complex Java
client might be written.

* **client-python** provides an example web interface that depends on API queries, and demonstrates how a more complex
Python client might be written. It uses
<a href="https://developers.google.com/appengine/docs/python/gettingstartedpython27/introduction">Google App Engine</a>
to deploy.

* **client-r** provides a simple r script that transforms API query results into GAlignments.

* **mapreduce-python** uses the <a href="https://developers.google.com/appengine/docs/python/dataprocessing/">MapReduce Python</a>
feature of Google App Engine to do complex calculations over API data.

* **protobufs** includes the protocol buffers used by the API. All client libraries,
documentation, and output are auto generated from these files.

* **readstore-local-java** implements the Genomics API locally, reading its data from a local
<a href="http://samtools.sourceforge.net/SAMv1.pdf">BAM file</a>.


### Contributing changes

* See [CONTRIB.md](CONTRIB.md)


### Licensing

* See [LICENSE](LICENSE)
