client-python
=============

Getting started
---------------

This Python client provides a simple web application which fetches data from the
`Google Genomics API`_, the NCBI Genomics API or the Local Readstore through a web
interface.

It can be run with app engine or without.

.. _Google Genomics Api: https://developers.google.com/genomics

Running on App Engine
~~~~~~~~~~~~~~~~~~~~~

To run with app engine, you'll need to `set up a Google App Engine environment
<https://developers.google.com/appengine/docs/python/gettingstartedpython27/introduction>`_.

Then, set ``USE_APPENGINE=True`` in the top of ``main.py``.

Run the app engine app locally and visit ``http://localhost:8080`` to browse data
from the API.

Running with paste and webapp2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

First you'll need to `install pip <http://www.pip-installer.org/en/latest/installing.html>`_.

Then install the required dependencies and run the ``localserver.py`` file::

  pip install WebOb Paste webapp2 jinja2
  python localserver.py

Enabling the Google API
~~~~~~~~~~~~~~~~~~~~~~~

If you want to pull in data from `Google Genomics API`_ you will need to set
``REQUIRE_OAUTH=True`` in ``main.py``. OAuth only works when App Engine is
enabled, so be sure to set ``USE_APPENGINE=True`` as well. 
See the above section for more details.

You will also need to follow the `authentication instructions
<https://developers.google.com/genomics#authenticate>`_ to generate a valid
``client_secrets.json`` file. However, for this application you want to generate
secrets for a *Web Application* rather than a *Native Application*.

Replace the ``client_secrets.json`` file in this directory with your new secrets
file.

Code layout
-----------

main.py:
  queries the Genomics API and handles all OAuth flows. It also serves up the HTML
  pages.

main.html:
  is the main HTML page. It is displayed once the user has granted OAuth access to
  the Genomics API.
  It provides the basic page layout, but most of the display logic is handled in
  JavaScript.

static/js/main.js:
  provides some JS utility functions, and calls into ``readgraph.js``.

static/js/readgraph.js:
  handles the visualization of reads. It contains the most complex code and uses
  `d3.js <http://d3js.org>`_ to display actual Read data.

The python client also depends on several external libraries:

`oauth2client`_:
  provides a python decorator which handles the entire OAuth user flow

`httplib2`_:
  is required by the OAuth library

`D3`_:
  is a javascript library used to make rich visualizations

`Underscore.js`_:
  is a javascript library that provides a variety of utilities

`Bootstrap`_:
  supplies a great set of default css, icons, and js helpers

In ``main.html``, `jQuery <http://jquery.com>`_ is also loaded from an external
site.

.. _oauth2client: https://code.google.com/p/google-api-python-client/wiki/OAuth2Client
.. _httplib2: https://github.com/jcgregorio/httplib2
.. _D3: http://d3js.org
.. _Underscore.js: http://underscorejs.org
.. _Bootstrap: http://getbootstrap.com


Project status
--------------

Goals
~~~~~
* Provide an easily deployable demo that demonstrates what Genomics API interop
  can achieve for the community.
* Provide an example of how to use the Genomics APIs and OAuth to build a
  non-trivial python application.


Current status
~~~~~~~~~~~~~~
This code _wants_ to be in active development, but has few contributions coming
in at the moment.

Currently, it provides a basic genome browser that can consume genomic data
from any API provider. It deploys on App Engine (to meet the
'easily deployable' goal), and has a layman-friendly UI.

Awesome possible features include:

* Incorporating variant data into the UI.
* Add more information to the read display (show inserts, highlight mismatches
  against the reference, etc)
* Possibly cleaning up the js code to be more plugin friendly - so that pieces
  could be shared and reused (d3 library? jquery plugin?)
* Staying up to date on API changes (readset name filtering can now be handled
  by the API, and readset searching now has pagination, etc)
* Better searching of Snpedia (or another provider)
* Other enhancement ideas are very welcome
* (for smaller/additional tasks see the GitHub issues)
