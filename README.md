# unchained
This application was generated using JHipster 4.5.3, you can find documentation and help at [https://jhipster.github.io/documentation-archive/v4.5.3](https://jhipster.github.io/documentation-archive/v4.5.3).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

#### Hyperledger Fabric Network (configuration requirements)
1. [Docker][]: We use docker to stand up the peers and certificate authorites used in the Hyperledger Fabric network.
2. [GO][]: We use the GO programming language to write the smart contracts used the Hyperledger Fabric network. 
    - You will need to setup and configure your environment to work with the GO programming language.

    Setup GO workspace:
    
        mkdir $HOME/go
        cd $HOME/go
        mkdir bin pkg src
        
    Setup GO environment:
        
        export GOPATH="$HOME/go"
        export PATH="$PATH:$GOPATH/bin"
    
#### Jhipster (configuration requirements)
1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
2. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle

#### Hyperledger Fabric Network (development requirements)
Create the below directory in your $GOPATH

    $GOPATH/src/github.com/chaincode_fileshare/

Our chain code is stopred in the [chain-code][] project. In order to make sure this project pulls down the correct chaincode, pull the [chain-code][] project into the above directory. Once you have it pulled down, you should have all of your chaincode in your $GOPATH at:

    $GOPATH/src/github.com/chaincode_fileshare/chain-code

Navigate back to where this project is stored and open the below directory in its own terminal window:

    <this-project's-root>/src/test/fixture/sdkintegration
    
Then, run the following command to start the network.

    ./fabric.sh up
    
##### Note
The first time you run the below command, it will take time to pull down the docker images and stand up the docker containers. Once the last container is created, and during all following executions of this command, it takes a short time for the network to be ready. An indicator that the network is up can be seen by a long list of messages reading 
    
    " ... [flogging] setModuleLevel -> ... "
    
To bring the network down, run the following command.

    ./fabric.sh down
    
At this time, you will need to bring the network down and back up again whenever you make changes to your GO chaincode. A quick execution of this can be done by running the following command.

    ./fabric.sh restart

#### JHipster (development requirements)
After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install

We use yarn scripts and [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    yarn start

[Yarn][] is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `yarn update` and `yarn install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `yarn help update`.

The `yarn run` command will list all of the scripts available to run for this project.

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    yarn add --exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    yarn add --dev --exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:

Edit [src/main/webapp/app/vendor.ts](src/main/webapp/app/vendor.ts) file:
~~~
import 'leaflet/dist/leaflet.js';
~~~

Edit [src/main/webapp/content/css/vendor.css](src/main/webapp/content/css/vendor.css) file:
~~~
@import '~leaflet/dist/leaflet.css';
~~~

Note: there are still few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using angular-cli

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

    ng generate component my-component

will generate few files:

    create src/main/webapp/app/my-component/my-component.component.html
    create src/main/webapp/app/my-component/my-component.component.ts
    update src/main/webapp/app/app.module.ts

## Building for production

To optimize the unchained application for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar target/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./mvnw clean test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    yarn test


For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.
For example, to start a mariadb database in a docker container, run:

    docker-compose -f src/main/docker/mariadb.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mariadb.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw package -Pprod docker:build

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: https://jhipster.github.io
[JHipster 4.5.3 archive]: https://jhipster.github.io/documentation-archive/v4.5.3

[Using JHipster in development]: https://jhipster.github.io/documentation-archive/v4.5.3/development/
[Using Docker and Docker-Compose]: https://jhipster.github.io/documentation-archive/v4.5.3/docker-compose
[Using JHipster in production]: https://jhipster.github.io/documentation-archive/v4.5.3/production/
[Running tests page]: https://jhipster.github.io/documentation-archive/v4.5.3/running-tests/
[Setting up Continuous Integration]: https://jhipster.github.io/documentation-archive/v4.5.3/setting-up-ci/


[Node.js]: https://nodejs.org/
[Yarn]: https://yarnpkg.org/
[Docker]: https://www.docker.com/community-edition#/download
[GO]: https://golang.org/doc/install
[GO with brew]: http://todsul.com/tech/setup-golang-on-mac-os-x/
[Webpack]: https://webpack.github.io/
[Angular CLI]: https://cli.angular.io/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
[Leaflet]: http://leafletjs.com/
[DefinitelyTyped]: http://definitelytyped.org/

[chain-code]: https://gitlab.ippon.fr/unchained/chain-code