# Creating fat JAR with gradle

Here’s a quick tutorial on how to package your java application and dependencies into single fat JAR (shadow JAR, capsule, however it’s called) with gradle.
 
## Easy solution using built-in JAR gradle plugin
 
```groovy
jar {
   from {configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
}
```

What it means:
* _jar_ - packaging task available in gradle as part of _java_ plugin
* _from_ - copies passed FileTree into JAR
* _configurations_ - holds all configurations (set of dependencies)
* _compile_ - I used compile configuration for this particular example, but this could be any other or even a separate configuration just for fat JAR
* _zipTree_ - this returns zipped file tree object, because JAR files essentially are ZIP files with specific file arrangement and content

While this is nice one-liner that uses only built-in gradle stuff, it will work only in cases when there are no name clashes in all dependencies. For example, if more than one dependency implements Java SPI service interface you will have multiple resources with the same name and only last one will make it into fat JAR.

## Using Shadow JAR plugin

```groovy
buildscript {
    dependencies {
        classpath 'com.github.jengelman.gradle.plugins:shadow:4.0.1'
    }
}

apply plugin: 'com.github.johnrengelman.shadow'

shadowJar {
    mergeServiceFiles()
}
```

Internally this plugin does the same thing as previous onl-liner, plus:
* It collates SPI service files
* inherits configuration from _jar_ task so that you may define manifest in standard familiar way:

```groovy
jar {
    manifest {
        attributes(
            'Main-Class': "com.example.CraneBookingApplication"
        )
    }
}
```

## Using Shadow JAR plugin with Windows

There is important caveat for Windows users however with previous method. Shadow JAR uses _\n_ as line separator when merging SPI files. Consequently, when JAR is run on Windows JVM will not be able to correctly read file line-by-line. To avoid this, we define custom service file transformer in _build.gradle_ that will use _\r\n_ as line separator:

```groovy
import com.github.jengelman.gradle.plugins.shadow.relocation.Relocator
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

class WindowsTransformer extends ServiceFileTransformer {

    @Override
    void transform(String path, InputStream is, List<Relocator> relocators) {
        def lines = is.readLines()
        
        relocators.each {rel ->
            if (rel.canRelocateClass(new File(path).name)) {
                path = rel.relocateClass(path)
            }
            lines.eachWithIndex { String line, int i ->
                if (rel.canRelocateClass(line)) {
                    lines[i] = rel.relocateClass(line)
                }
            }
        }
        
        lines.each { line -> serviceEntries[path]
                .append(new ByteArrayInputStream((line + "\r\n").getBytes()))
        }
    }
}

shadowJar {
//    mergeServiceFiles() - should not be used, as it uses default transformer
    transform(WindowsTransformer.class)
}
