This source-plugin for Flume is able to consume data from an OPC server. OPC is an industry standard for sensoric data.

More information: http://www.opcdatahub.com/WhatIsOPC.html

Used library: https://openscada.atlassian.net/wiki/display/OP/HowToStartWithUtgard

OPC server trial: https://www.softwaretoolbox.com/topserver/ (Windows required)


Generate IDE files

```
gradle idea

 or

gradle eclipse
```

Build with

```
gradle clean build
```

Copy artifact from `build/libs/mapr-opc-<version>.jar` to `<FLUME_HOME>/libs/`


Run sample config

```
# Naming the components on the current agent.
OpcToHdfs.sources = opc
OpcToHdfs.channels = MemChannel
OpcToHdfs.sinks = hdfs-sink-1

OpcToHdfs.sources.opc.type = com.mapr.flume.source.opc.OpcSource
OpcToHdfs.sources.opc.channels = MemChannel
OpcToHdfs.sources.opc.host = 10.0.0.157
#OpcToHdfs.sources.opc.domain =
OpcToHdfs.sources.opc.user = Administrator
OpcToHdfs.sources.opc.password = yourpasswprd
OpcToHdfs.sources.opc.progId = SWToolbox.TOPServer.V5
#OpcToHdfs.sources.opc.clsid = 680DFBF7-C92D-484D-84BE-06DC3DECCD68
# ItemID from OPC server
OpcToHdfs.sources.opc.itemId = Simulation Examples.Functions.User1
# Fetchinterval in seconds
OpcToHdfs.sources.opc.fetchIntervalInMs = 100
# If true, the event is suppressed if the timestamp has not changed
OpcToHdfs.sources.opc.distinctTimeStamp = false
# If true, the event is suppressed if the value is equal to the one before.
OpcToHdfs.sources.opc.distinctValue = false
# Format per line, if you don't like to have a field, just remove it.
OpcToHdfs.sources.opc.lineFormat = {ITEM_ID};{TIME};{VALUE};{QUALITY};{ERROR}
# millis Java millis or a Java Date Format, e.g. yyyy-MM-dd HH:mm:ss
OpcToHdfs.sources.opc.timeFormat = millis

# Describing/Configuring the sink

OpcToHdfs.sinks.hdfs-sink-1.channel = MemChannel
OpcToHdfs.sinks.hdfs-sink-1.type = hdfs
OpcToHdfs.sinks.hdfs-sink-1.hdfs.path = maprfs:///tmp/testoutput
OpcToHdfs.sinks.hdfs-sink-1.hdfs.filePrefix = test
OpcToHdfs.sinks.hdfs-sink-1.hdfs.fileSuffix = .csv
OpcToHdfs.sinks.hdfs-sink-1.hdfs.fileType = DataStream
OpcToHdfs.sinks.hdfs-sink-1.hdfs.writeFormat = Text
OpcToHdfs.sinks.hdfs-sink-1.hdfs.rollInterval = 60
OpcToHdfs.sinks.hdfs-sink-1.hdfs.rollSize = 1000000
OpcToHdfs.sinks.hdfs-sink-1.hdfs.rollCount = 100
OpcToHdfs.sinks.hdfs-sink-1.hdfs.batchSize = 100
OpcToHdfs.sinks.hdfs-sink-1.hdfs.idleTimeout = 60


#OpcToHdfs.sinks.HDFS.serializer = text

# Describing/Configuring the channel
OpcToHdfs.channels.MemChannel.type = memory
OpcToHdfs.channels.MemChannel.capacity = 10000
OpcToHdfs.channels.MemChannel.transactionCapacity = 100

```