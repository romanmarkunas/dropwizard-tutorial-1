## Dropwizard vs SpringBoot

// TODO - do I need this in that article?

Both of frameworks came out to ease the pain of developing and deploying Java 
applications that run in application containers, like TomCat, Jetty, Undertow, 
Glassfish, etc. This is achieved by packaging application server together with 
actual code. This way project artifact will be deployable, self-contained JAR 
file that can be launched and debugged within IDE. Both promote RESTful
approach and fairly similar structure of application.

However here similarities end. SpringBoot has Spring dependency injection in 
it's core and all necessary components are assembled together using it. 
SpringBoot will allow you to select application components from variety of 
supported libraries, for example, you could use any embeddable application 
container to serve requests or wire in any supported database using all familiar 
Spring annotations.

Dropwizard takes more conservative approach here. So while you can still utilise 
any dependency injection framework, by default it is wired directly, so you'll 
have to write object wiring yourself. Also you cannot replace core components 
like Jersey.

Now it may look like Dropwizard is a clear winner here, however all mentioned 
Spring flexibility does come with steeper learning curve and will always require 
extra cognitive work to understand how objects are wired together and which 
components are actually used. And many times minimal tool that does the job is 
all it takes.
