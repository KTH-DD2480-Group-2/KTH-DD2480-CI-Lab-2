# KTH-DD2480-CI-Lab-2
[![Build Status](https://travis-ci.com/AdamJonsson/KTH-DD2480-CI-Lab-2.svg?branch=main)](https://travis-ci.com/AdamJonsson/KTH-DD2480-CI-Lab-2)
## Developers
### Getting Started
* To start developing, you first need to have Maven installed. (Not sure if it comes with IntelliJ). You can check that it is installed by running `mvn --version` in your terminal/powershell.
* Clone this project to a location of choice, e.g `git clone ...`.
* Run `mvn clean install` in the root of the project folder, that is, same level were this README.md is located. This will download all the dependencies needed to run the project.
* To start the project, run `mvn exec:java`.
* To start the tests, run `mvn test`.
### Add New Dependencies
There are multiples ways to add dependencies to a maven project.  
* **Using IntelliJ**:  
  * Adding them to class path will automatically also add them to the pom.xml file. Example:<br/> <img style="width: 50%" src="./assets/intelliJ-dependecies-add-example.png">
* **Adding them manually**. 
  * IntelliJ might sometimes not find the package that we want to import. In these cases, the packages need to be manually added to the pom.xml file. You can either search only for the package and add the dependency in the pom.xml file. Or you can do the search in the pom.xml file itself: <br/> <img style="width: 50%" src="./assets/intelliJ-dependecies-add-manually-example.png">
### Naming Conventions
#### Commit messages
* `#<issue-num> fix|feat|docs|test|style: <Description of commit>`
* Make sure last commit in PR follows the style above
* Local commits can be in any form descriptive to yourself/others, but should be explanatory
* The `<Description of commit>` should start with a capitalized letter

#### Branch name
* `issue/x-<description-of-branch>`


