# Config Manager

A rudimentary (idempotent) configuration management tool (fat jar)

## Getting Started

These instructions will get you the application up and running on your local machine.

## Software Architecture
(TODO: uml diagram)
<br />
The application uses the Spring Shell project to build a shell based application. Built on top of jsch-extension library to accept SSH and SCP commands to configure a remote machine as per the spring boot yaml configuration. This application uses the Spring framework annotations to set the SSH configs and services for running the commands against.


## Prerequisites

Configure the remote machine for SSH access using RSA key pair
<br />
(TODO: configure in bootstrap.sh)

#### Application Dependencies:
##### Build Dependnecies
* JDK
* Maven

##### Runtime Dependnecies
* JRE

```
ssh-copy-id -i src/test/resources/keys/id_rsa.pub root@<remote-machine-ip>
```

## Installing

Packaging the application as a fat jar
```
mvn install:install-file -Dfile=$(pwd)/src/test/resources/lib/jsch-extension.jar -DgroupId=com.jcraft -DartifactId=jsch -Dversion=0.1.55 -Dpackaging=jar
mvn -DskipTests=true clean install
```


## Running the application

Run the command below to start the application. The application properties file as shows is placed in `src/test/resources` folder. The path to various files inside the properties file is using `/tmp` path. Please update to to absolute path on your localmachine.
```
./target/config-manager-0.0.1-SNAPSHOT.jar --spring.config.location=$(pwd)/src/test/resources/application.yml
```
## Commands
#### Show available commands
```
help
```

#### Show config
Shows the tool's config for remote configuration (remote hosts, files, packages & ssh config). Tool is using Ansible like configuration.

```
show-config
```

#### Run apt packages
This will install all the named packages from the config properties file.
```
run-packages
```

#### Copy file
```
copy-files
```

## Usage (Config)
The properties file has 4 main sections:
* files (LIST)
    * src: absolute path of the source file to be copied (along with filename)
    * dest: absolute path of the destination file to be copied on remote machine
    * filename: destination file name
    * owner: file meta data owner
    * group: file meta data group
    * mode: file meta data mode
    * restart (LIST): list of apt package that need restarts

* packages (LIST)
    * name: name of the apt package
    * state (ENUMs): state of the package
        * present: for installing apt package
        * enabled: for installing apt package a nd starting the service
        * absent: for uninstalling the apt package

* hosts (LIST): Remote machines to be configured

* ssh:
    * username: ssh username
    * private_key_file: ssh RSA private key
    

## Improvements
* Use Java thread Runnable interface to configure remote machines in parallel.
* Enabled proper logging with discrete messages
* Enable caching mechanism to store the cache of the configuration to pick up the configuration in case of connectivity errors
* Write Unit test using against `ssh:ubuntu16` docker image
* Enhance application to work a proxy network


## Acknowledgments

* Springboot framework
* jsch-extension

