package com.romanmarkunas.dwtutorial1;

public class HelloApplication extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new HelloApplication().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new TimeResource());
    }
}
