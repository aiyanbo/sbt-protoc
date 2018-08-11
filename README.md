# sbt-protoc

A plugin for sbt that transforms *.proto files to Java source files. 

# Usage

#### Adding the plugin dependency

In your project, create a file for plugin library dependencies project/plugins.sbt and add the following line:

```
addSbtPlugin("org.jmotor.sbt" % "sbt-protoc" % "1.0.0-SNAPSHOT")
```

#### Enabled sbt-protc plugin

**build.sbt**

```
enablePlugins(ProtocPlugin)
```

#### Additional protobuf sources in your project

```

|- project
|---- src/mian/proto
|-------- hello.proto

```

# Settings

| name | default | description |
| --- | --- | --- |
| protocVersion | 3.6.1 | protoc version
| grpcVersion | latest | grpc version, if you not set used the latest.

# Credits

- [sbt-protobuf](https://github.com/sbt/sbt-protobuf)
