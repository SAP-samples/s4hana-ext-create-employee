[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/s4hana-ext-create-employee)](https://api.reuse.software/info/github.com/SAP-samples/s4hana-ext-create-employee)

# SAP S/4HANA Cloud Extensions - SAP S/4HANA Cloud App for One-Click User Creation
This repository contains the sample code for the [SAP S/4HANA Cloud App for One-Click User Creation tutorial](http://tiny.cc/s4-create-employee).

*This code is only one part of the tutorial, so please follow the tutorial before attempting to use this code.*

## Description

SAP S/4HANA Cloud App for One-Click User Creation is an admin side-by-side extension scenario. Within the scenario, you use the Create Employee and IDP User app to make the creation of new employees and users - along with business role assignments - easier for SAP S/4HANA Cloud, as the user is also created in the IDP. It puts several manual admin tasks, that would usually need to be performed in different UIs, into one UI.

#### SAP Extensibility Explorer

This tutorial is one of multiple tutorials that make up the [SAP Extensibility Explorer](https://sap.com/extends4) for SAP S/4HANA Cloud.
SAP Extensibility Explorer is a central place where anyone involved in the extensibility process can gain insight into various types of extensibility options. At the heart of SAP Extensibility Explorer, there is a rich repository of sample scenarios which show, in a hands-on way, how to realize an extensibility requirement leveraging different extensibility patterns.


Requirements
-------------
- An account in SAP Business Technology Platform (BTP) with a subaccount in the _Cloud Foundry_ environment.
- [Java SE **8** Development Kit (JDK)](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Apache Maven](http://maven.apache.org/download.cgi) to build the application.
- [Cloud Foundry Command Line Interface (CF CLI)](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) tool in case you want to deploy the application to Cloud Foundry.
- An SAP S/4HANA Cloud tenant. This is a commercial paid product.

Download and Installation
-------------
This repository is a part of the sample [SAP S/4HANA Cloud App for One-Click User Creation](https://help.sap.com/viewer/688f5e6d61944d078987a5376cf78b3e/SHIP/en-US/). In step 'Build and Deploy the Create Employee and IDP User App' in the tutorial the instructions for use can be found.

[Please download the zip file by clicking here](https://github.com/SAP/s4hana-ext-create-employee/archive/master.zip) so that the code can be used in the tutorial.

Known issues
---------------------
As this scenario uses SOAP protocol, you might have to adapt/update wsdl file, obtained from the S/4HANA system, to work with this code.

How to obtain support
---------------------
If you have issues with this sample, please open a report using [GitHub issues](https://github.com/SAP/s4hana-ext-create-employee/issues).

License
-------
Copyright © 2020 SAP SE or an SAP affiliate company. All rights reserved.
This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE file](LICENSES/Apache-2.0.txt).
