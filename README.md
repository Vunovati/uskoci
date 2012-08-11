uskoci
======
Download with:
$ git clone git@github.com:Vunovati/uskoci.git

Build with:
$ mvn clean install

Deployable war can be found in uskoci/design/frontend/target/uskoci-REST-API.war

backend is already added as jar dependency to classpath in WEB-INF/lib

To deploy in an embedded jetty server automatically run:

$ cd design/frontend

& mvn jetty:run-war

visit: http://localhost:8080/

## Build Status
[![Build Status](https://buildhive.cloudbees.com/job/Vunovati/job/uskoci/badge/icon)](https://buildhive.cloudbees.com/job/Vunovati/job/uskoci/)