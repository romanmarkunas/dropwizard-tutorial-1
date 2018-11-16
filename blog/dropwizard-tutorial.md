# Getting started with Dropwizard

Dropwizard is a framework for rapid application development. Dropwizard pulls 
and glues together Jetty application server, Jersey REST framework, Jackson for 
serialization/deserialization and a bunch of other libraries, so you have 
everything at hand to develop full-featured Java-based RESTful web-applications.

This tutorial will go through core Dropwizard components and how they are 
assembled together to have working Dropwizard application.

Ready sample application can be found [here](https://github.com/romanmarkunas/dropwizard-tutorial-1).

## Pulling necessary dependencies

For this section I'll be using Gradle as dependency manager, since 
[Dropwizard website](https://www.dropwizard.io/1.3.5/docs/getting-started.html) 
already has examples using Maven.

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
    
    @Override
    public void run(Configuration configuration, Environment environment) {}
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
Let's assume for now that we will run it from IDE as we'll talk a bit more about 
packaging later. 

To run app main method:
```
java ApplicationClass server path/to/config.yaml
```

If you follow supplied code this will result in:
```
java com.romanmarkunas.dwtutorial1.HelloApplication server config/config.yml
```

The successful launch will manifest with:
```
org.eclipse.jetty.server.Server: Started @3908ms
```

However scrolling up we can also see:
```
The following paths were found for the configured resources: NONE
```

this means we don't have any endpoints defined yet. So currently we have a 
full-fledged server running for no reason. Let's fix that!

## Defining endpoint

For training purposes we will an endpoint that will return current server time 
and how many times this endpoint is invoked. Since Java methods can only return 
1 value, we will need to create a POJO with these 2:

```java
public class TimeAndInvocations {

    // fields and constructor omitted
    
    @JsonProperty
    public LocalTime getTime() {
        return time;
    }

    @JsonProperty
    public int getCurrentInvocations() {
        return currentInvocations;
    }
}
```

Now let's define endpoint itself:

```java
@Path("/time")
@Produces(MediaType.APPLICATION_JSON)
public class TimeResource {

    private final AtomicInteger invocations = new AtomicInteger(0);

    @GET
    public TimeAndInvocations getTimeAndInvocations() {
        int invocation = this.invocations.incrementAndGet();
        return new TimeAndInvocations(LocalTime.now(), invocation);
    }
}
```

This defines _GET /time_ endpoint that returns JSON-formatted time and invocation 
count. All these annotations that allows to represent endpoint as java object 
come from JAX-RS (JSR 311 & JSR 339) and are implemented by Jersey.

However before we can hit our endpoint, we must let Dropwizard/Jersey know about 
it in HelloApplication class:
 
 ```java
@Override
public void run(Configuration configuration, Environment environment) {
    environment.jersey().register(new TimeResource());
}
```

That's it! Routing of incoming requests into Jersey environment is already done 
by Dropwizard and launching our app again confirms that:

```
The following paths were found for the configured resources:
  GET /time (com.romanmarkunas.dwtutorial1.TimeResource)```
```

## Deserialization configuration

Let's look what our endpoint returns:

```json
{
    "time": [
        9,
        4,
        51,
        367000000
    ],
    "currentInvocations": 1
}
```

Jackson is the library that converts our TimeAndInvocations objects into JSON 
string. As you can see by default LocalTime serializer just prints fields. 

Let's fix this:

```java
@JsonProperty
@JsonSerialize(using = LocalTimeSerializer.class)
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss")
public LocalTime getTime() {
    return time;
}
```

which results in output:

```json
{
    "time": "09:45:15",
    "currentInvocations": 3
}
```

Here we added another Jackson annotation which hints serializer how it should 
form a string representation of that object. Jackson comes with plenty of 
serializers for Java library classes and other serializers can be registered 
into Jackson environment, similarly how we didi it with TimeResource. Otherwise, 
if Jackson doe not find specific serializer for and objects it works with, it 
will default to aforementioned field-by-field serialization. 

## Packaging

As mentioned before, Dropwizard promotes single JAR packaging. To make a single 
JAR we need to instruct build tool to put all depended upon classes into JAR 
file. I have covered this in [separate tutorial before](http://romanmarkunas.com/web/blog/creating-fat-jars-with-gradle/).

## Instead of conclusion

That's it for this introductory article. Hopefully after this reader will have 
vague ide of what Dropwizard is, how it may be used and roughly how it's 
components are interacting to bring that. 
