This is the replication package for our paper "UnCertaGator: Propagation of Uncertainty Information in Development Processes Using Consistency Automation". This package contains the source code for the UnCertaGator implementation and the case-studies used in the evaluation in a ready-to-run configuration.

# Requirements

- Java JDK 17
- Maven 3

# Installation and Execution

Ensure Java and maven are installed.
Unpack the zip file and navigate into each of the two directories `CPS-brakeSystemCaseStudy/` or `SA-TeaStore/`. Run ``mvn clean verify``. The tests corresponding to the scenarios in the evaluation are executed after the projects have built.

# Overview

This artefact contains the resources for the 3 case studies used in the UnCertaGator paper and contains its implementation.

It is provided as a docker image, with all required dependencies installed. For local development, maven and Java are required.

## Autonomous Vehicle

`AV-1-AutonomousVehicle` To evaluate the applicability of the uncertainty metamodel on in the autonomous vehicle domain. Consulte `AV-1-AutonomousVehicle/model` for the concrete model files.

## Cyber-Physical Systems

`CPS-brakeSystemCaseStudy` For the cyber-physical systems case study, using a simplified brake-system and perform an applicability study that covers different scenarios of a potential application of the UnCertaGator to show its functional correctness.

Go to `CPS-brakeSystemCaseStudy/` and run `mvn clean verify` to rerun the scenarios. The scenarios are for reproducibility implemented in unit tests. The prefix of each test (e.g., CPS1) corresponds to the scenarios in the paper. 

Go to `CPS-brakeSystemCaseStudy/vsum/src/test/java/tools/vitruv/methodologisttemplate/vsum/uncertainty` to inspect the scenarios or extend them at your convenience.

Go to `CPS-brakeSystemCaseStudy/model/src/main/ecore/` to inspect the metamodels and the uncertainty metamodel used.

Go to `CPS-brakeSystemCaseStudy/consistency/src/main/reactions/tools/vitruv/methodologisttemplate/consistency` to inspect the case-studies consistency rules, as well as the UnCertaGators code, which implements the generic uncertainty propagation and consistency rules presented in the paper.

`CPS-brakeSystemCaseStudy/consistency/src/main/reactions/tools/vitruv/methodologisttemplate/consistency` provides the consistency presevervation rules used to implement the features of the UnCertaGator. This implementation is integrated into the Vitruvius framework. Therefore, building the project, creates the required artifacts, generates the model code, etc. to make it usable (i.e., execute the tests, which implement the scenarios presented.)

## Software Architecture TeaStore

`SA-TeaStore` For the software-engineering / Architecture TeaStore case study, using models of development artifacts and performance models.

Go to `SA-TeaStore/` and run `mvn clean verify` to rerun the scenarios. The scenarios are for reproducibility implemented in unit tests. The prefix of each test (e.g., SA1 to SA4) corresponds to the scenarios in the paper. 

o to `SA-TeaStore/vsum/src/test/java/tools/vitruv/methodologisttemplate/vsum/scenarios` to inspect the scenarios or extend them at your convenience.

Go to `SA-TeaStore/model/src/main/ecore/` to inspect the metamodels and the uncertainty metamodel used.

Go to `SA-TeaStore/consistency/src/main/reactions/tools/vitruv/methodologisttemplate/consistency` to inspect the case-studies consistency rules which create correspondence automatically, as well as the UnCertaGators code, which implements the generic uncertainty propagation and consistency rules presented in the paper.

`SA-TeaStore/consistency/src/main/reactions/tools/vitruv/methodologisttemplate/consistency` provides the consistency presevervation rules used to implement the features of the UnCertaGator. This implementation is integrated into the Vitruvius framework. Therefore, building the project, creates the required artifacts, generates the model code, etc. to make it usable (i.e., execute the tests, which implement the scenarios presented.)