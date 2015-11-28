# Timed Text Toolkit (ttt)

A collection of related tools that provide support for or make use of the W3C Timed Text Markup Language (TTML).

## Core Tools

- Timed Text Verifier (ttv)
- Timed Text Transformer (ttx)
- Timed Text Transforming Verifier (ttxv)
- Timed Text Presentation Engine (ttpe)

## Conversion Tools

- Lambda CAP to TTML Converter (cap2tt)

## Build Requirements

Prior to performing a build, the following requirements must be met:

- Install Java JDK 7.
- Install maven 3.
- Perform one-time installation of third party library dependencies as follows, where `$TTT_HOME` is set to the directory where the `ttt` repository is cloned:

<pre>
    $ cd $TTT_HOME
    $ mvn -f ttt-deps install
</pre>

This last step installs snapshot copies of `batik`, `fontbox`, and `xmlunit` dependencies that contain modifications specific to `ttt`.

## Build and Install

In order to build and install `ttt` in the local maven repository, run `mvn` (*maven*) as follows:

`mvn clean install`

## Site Construction

In order to build the `ttt` site content, including checkstyle and findbugs reports, run `mvn` (*maven*) as follows **after performing the above build and install step**:

`mvn site site:stage`

The resulting staged site data can be accessed at `target/staging/index.html`. The findbugs reports can be found under the Project Reports link for each module.

## Tool Dependencies

At present, `ttt` is being developed using the following versions of tools:

<pre>
    $ java -version
    java version "1.7.0_80"
    Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
    Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)

    $ mvn -version
    Apache Maven 3.3.3 (7994120775791599e205a5524ec3e0dfe41d4a06; 2015-04-22T05:57:37-06:00)
</pre>

Additional dependencies are managed by maven.

## Maven Coordinates

<pre>
  &lt;groupId&gt;com.skynav.ttt&lt;/groupId&gt;
  &lt;artifactId&gt;ttt&lt;/artifactId&gt;
</pre>
