# Crossflow
Crossflow is a distributed data processing framework that supports dispensation of work across multiple opinionated and low-commitment workers.

## Running from Source
To run Crossflow from source you will need Eclipse and Apache Thrift. Brief instructions are provided below.

### Set up Eclipse
- Start with a J2EE distribution, from https://www.eclipse.org/downloads/packages/release/2020-09/r/eclipse-ide-enterprise-java-developers
- Install the following Eclipse features through Eclipse's `Help -> Install New Software` dialog
  - The Graphical Modelling Framework (GMF) Tooling SDK, from http://download.eclipse.org/modeling/gmp/gmf-tooling/updates/releases/ 
  (choose keep my installation the same option)
  - The following Epsilon features, from http://download.eclipse.org/epsilon/interim/
  	- Epsilon Core
  	- Epsilon Core Develoment Tools
  	- Epsilon EMF Integration
  	- Epsilon GMF Integration
  	- Picto
  - Ivy from http://www.apache.org/dist/ant/ivyde/updatesite

### Install Thrift
- Install Apache Thrift (http://thrift.apache.org/)
	- Standalone executable for Windows
	- Homebrew for Mac (`brew install thrift`)

### Import Crossflow Code
- Clone the https://github.com/crossflowlabs/crossflow.git repository
- Check out the `kolovos` branch
- Import all projects under the `plugins` and `tests` folders

### Retrieve Ivy Dependencies
- Right-click on the `org.crossflow.runtime.dependencies` and select `Ivy -> Retrieve dependencies`

### Generate Code for the Tests Project
- Right-click on `org.crossflow.tests/generate-all-tests.xml` and select `Run as -> ANT Build` to run the Crossflow code generator against all models under `/org.crossflow.tests/models`

### Run JUnit Tests
- JUnit tests can be executed through the `CrossflowTests` test suite in `org.crossflow.tests`
