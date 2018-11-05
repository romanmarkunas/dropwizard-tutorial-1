# Getting started with Dropwizard

Dropwizard is a framework for rapid application development. Dropwizard pulls 
and glues together Jetty application server, Jersey REST framework, Jackson for 
serialization/deserialization and a bunch of other libraries, so you have 
everything at hand to develop full-featured Java-based RESTful web-applications.

This tutorial will go through core Dropwizard components and how they are 
assembled together to have working Dropwizard application.

Ready sample application can be found [here](TODO link to repo).

## Pulling necessary dependencies

For this section I'll be using Gradle as dependency manager, since 
[Dropwizard website](TODO link to their getting started) already has examples 
using Maven.

For simple app we need only one dependency + junit for unit tests:

```groovy
dependencies {
    compile "io.dropwizard:dropwizard-core:$vDropwizard"

    testCompile "junit:junit:4.12"
    testCompile "io.dropwizard:dropwizard-testing:$vDropwizard"
}
```

Dropwizard testing package contains junit rules to create endpoints or whole 
embedded application instance within tests. 

## Application entry point

Since Dropwizard's artifact is executable JAR, we need to define application 
entry point as part of our application class:

```java
public class HelloApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new HelloApplication().run(args);
    }
}
```

Main method is not necessarily to be located here, but it's usually done so by 
convention. Now all we have to do in main method is to call for #run(args) which 
will startup application container, parse configuration and do all other lovely 
stuff we'll discuss further in this article.

## Application container

Dropwzard applications are served by embedded Jetty container. Most of 
configuring ang gluing Jetty together with other Dropwizard parts is already 
done (if you are curious how Jetty is launched within Dropwizard, take a look at 
io.dropwizard.cli.ServerCommand#run method - this is server entry point after 
successful input and configuration initialization). What we need to do, is to 
configure some application-specific server parameters in configuration file:

```yaml
server:
  type: simple
  applicationContextPath: /
```

For this example I used simple Jetty server factory, which creates server with 
reasonable default settings and public/admin ports being 8080/8081. The only 
setting specified here is _applicationContextPath_, because default value is 
_/applciation/_ and I don't want all endpoints to be prefixed with that. 

At startup Dropwizard will parse this yaml file and deserialize it into Java 
factories that are used to create objects representing different components.

In case you wonder what _type: simple_ means in this configuration - this is value 
of @JsonTypeName annotation. That way Jackson will know concrete ServerFactory 
implementation class that will need to be deserialized from this configuration. 

## Running

So once we have application class and configuration in place we can run our app! 

## Defining endpoint

Now let's try to do something useful with our application.

## Other configuration

## Deserialization configuration

## Instead of conclusion

That's it for this introductory article. Hopefully after this reader will have 
vague ide of what Dropwizard is, how it may be used and roughly how it's 
components are interacting to bring that. 
