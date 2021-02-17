# KTH-DD2480-CI-Lab-2
![Build Status](https://travis-ci.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2.svg?branch=main)
## Developers
### Getting Started
* To start developing, you first need to have Maven installed. (Not sure if it comes with IntelliJ). You can check that it is installed by running `mvn --version` in your terminal/powershell.
* Clone this project to a location of choice, e.g `git clone ...`.
* Run `mvn clean install` in the root of the project folder, that is, same level were this README.md is located. This will download all the dependencies needed to run the project.
* To start the project, run `mvn exec:java "-Dexec.mainClass=ContinuousIntegrationServer"`. You can also run the project by running or debugging the main function in the ContinuousIntegrationServer class using an IDE. This would start a server on port 8080 that you can access in `http://localhost:8080/`. You can also access the frontend dashboard in `http://localhost:8080/dashboard`.
* To start the tests, run `mvn test`.

### JavaDoc
The JavaDoc can be accessed from here: https://kth-dd2480-ci-lab-2-java-doc.web.app/package-summary.html

#### Frontend
* In order to get started on developing the frontend part of the CI, you will need node.js version 12 or later.
* You can start the frontend server by going running `npm install` and then `npm start` in the src/main/webapp/ci-frontend folder.
### Add New Dependencies
There are multiples ways to add dependencies to a maven project.  
* **Using IntelliJ**:  
  * Adding them to class path will automatically also add them to the pom.xml file. Example:<br/> <img style="width: 50%" src="./assets/intelliJ-dependecies-add-example.png">
* **Adding them manually**. 
  * IntelliJ might sometimes not find the package that we want to import. In these cases, the packages need to be manually added to the pom.xml file. You can either search only for the package and add the dependency in the pom.xml file. Or you can do the search in the pom.xml file itself: <br/> <img style="width: 50%" src="./assets/intelliJ-dependecies-add-manually-example.png">

#### Deployment
Before you start the sever, you need to add an system variable with a personal access token. The name of the system variable should be `KTH_DD2480_CI_TOKEN` and have the value of you access token, e.g "35ddf2a243272a435da75d4cesas9ead5asdf584a" (Just an example, this specific access token do not work). Instruction of how to add a new system variable can be found here: https://www.architectryan.com/2018/08/31/how-to-change-environment-variables-on-windows-10/

### Naming Conventions
#### Commit messages
* `#<issue-num> fix|feat|docs|test|style: <Description of commit>`
* Make sure last commit in PR follows the style above
* Local commits can be in any form descriptive to yourself/others, but should be explanatory
* The `<Description of commit>` should start with a capitalized letter

#### Branch name
* `issue/x-<description-of-branch>`

## Statement of contributions
The main procedure for developing the continuous integration (CI) server was to use group meetings to discuss what
parts the server should consist of and in what order they could be implemented. The team member that implemented a 
part that could be tested was also responsible for creating tests for it.

More over, every merge into main needs to have an approved review. A set of rules was set in the repository to prevent pushes directly into main and merges when no approved review existed. Squash and merge was used for every pull-request to prevent unnecessary commits in the main branch. As a result, commits such as "Fixed typo" or "Added comment" is not visible and will minimize showing irrelevant changes in the main branch. The information is however not lost, as it is still visible under the given pull-request.
### Contributions of each member
* **Adam Jonsson**: Added frontend for the CI server [PR](PR-SKELETONFRONTE). Fixed bug regarding commit status [PR](PR-SYSVARTOKEN).  Added the skelton to for the ContinuousIntegrationServer.java [PR](PR-CISKELETON). Created repository as an origination and added rules for the repository.
* **Hovig Manjikian**: Added tests to for the ContinuousIntegrationServer.java [PR](PR-CISKELETON). Added the functionality of parsing the webhook and fetching the relevant revision from GitHub [PR](PR-FETCHDATA). Reviewed [PR](PR-SKELETONFRONTE), [PR](PR-SYSVARTOKEN), [PR](PR-PARSHIST). Configured Ngrok and depoyed the server. 
* **Isak Vilhelmsson**: Added functionality for CI to extract and run a repo zip ([PR][PR-EXTRACTANDRUN]). Did setup for the 'contributions' part of the README ([PR][PR-CONTR]). Reviewed ([PR][PR-JAVADOC]), ([PR][PR-URLHANDLER]), ([PR][PR-INFOCARDS]), ([PR][PR-FETCHDATA]), ([PR][PR-CISKELETON]).
* **Lara Rostami**: Implemented functionality for collecting the history of all builds in .json-format and sending it to the frontend ([PR][PR-HISTORY]).
* **Tony Le**: Implemented functionality for the CI to retrieve and change commit status ([PR][PR-SETCOMMITSTAT]), and ensured that all classes and methods had proper comments and descriptions for Javadoc generation ([PR][PR-JAVADOC]).

### P - Features & Grading
This section exist to make it more easier for the TA to grade this assignment.  
* P0 - See this README file and this repository.
* P1 - The CI server extract the project from the pushed commit and runs `mvn clean install` which builds the project. The CI server scan the output of the command and notifies GITHUB if the build was success full or not. 
* P2 - The CI server the same command `mvn clean install` which also tests the project. Here as well, the CI server scan the output of the command and notifies GITHUB if the build was success full or not.
* P3 - We used GitHubs REST-API to notify when the server began the building and testing. It also notifies when  the server was done by either returning "success" or "failure". The history of the notification of our CI server can be seen in the latest commits in the main and assessment branch.
* P4 - We have used a prefix conversion for all commits and have linked issues and pull-request to them all: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/commits/main.
* P5 - We created a JavaDoc and published it on github.io which can be accessed here: https://kth-dd2480-ci-lab-2-java-doc.web.app/package-summary.html

* **(P+)** P6 - We are storing the build history, even if the server is rebooted. The build history can be accessed here:
  * (All builds) http://ba7b413bd96c.ngrok.io/dashboard/
  * (Build Failed) http://ba7b413bd96c.ngrok.io/dashboard?commitSHA=fe0c606fd6d49d94c29e95fc70466b1035d524ee
  * (Build Succeeded) http://ba7b413bd96c.ngrok.io/dashboard?commitSHA=6aed4581a17e6664174c12c16175a36cefbabb14

* **(P+)** P7 - We have built an frontend for the build history that we are proud of:
  * http://ba7b413bd96c.ngrok.io/dashboard/

* **(P+)** P8 - All commits have (expect the first one) has an issue and a PR linked to it.
  * See main branch here: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/commits/main

[PR-CONTR]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/29
[PR-JAVADOC]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/27
[PR-URLHANDLER]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/26
[PR-INFOCARDS]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/25
[PR-PARSHIST]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/21
[PR-BUILDSTOJSON]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/19
[PR-SYSVARTOKEN]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/18
[PR-SETCOMMITSTAT]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/16
[PR-EXTRACTANDRUN]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/15
[PR-SKELETONFRONTE]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/14
[PR-FETCHDATA]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/9
[PR-CISKELETON]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/6
[PR-LINKTRAVIS]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/4
[PR-NAMINGCONV]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/3
[PR-HISTORY]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/11
[PR-URLHANDLER]: https://github.com/KTH-DD2480-Group-2/KTH-DD2480-CI-Lab-2/pull/26
