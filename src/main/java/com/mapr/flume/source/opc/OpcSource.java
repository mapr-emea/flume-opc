package com.mapr.flume.source.opc;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIUnsigned;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

public class OpcSource extends AbstractSource implements EventDrivenSource, Configurable {
    private static final Logger logger = LoggerFactory.getLogger(OpcSource.class);
    private final CounterGroup counterGroup;
    private String hostname;
    private String domain;
    private String user;
    private String password;
    private String progId;
    private String clsId;
    private Integer fetchIntervalInMs;
    private Boolean distinctTimeStamp;
    private Boolean distinctValue;
    private String format;
    private String itemId;
    private String itemIds;
    private String timeFormat;
    private Server server;
    private AccessBase access;

    public OpcSource() {
        counterGroup = new CounterGroup();
    }

    @Override
    public void configure(Context context) {
        hostname = context.getString(OpcSourceConstants.CONFIG_HOSTNAME, "localhost");
        user = context.getString(OpcSourceConstants.CONFIG_USER);
        domain = context.getString(OpcSourceConstants.CONFIG_DOMAIN, "localhost");
        password = context.getString(OpcSourceConstants.CONFIG_PASSWORD);
        progId = context.getString(OpcSourceConstants.CONFIG_PROG_ID);
        clsId = context.getString(OpcSourceConstants.CONFIG_CLS_ID);
        fetchIntervalInMs = context.getInteger(OpcSourceConstants.CONFIG_FETCH_INTERVAL_MS, 100);
        distinctTimeStamp = context.getBoolean(OpcSourceConstants.CONFIG_DISTINCT_TIMESTAMP, false);
        distinctValue = context.getBoolean(OpcSourceConstants.CONFIG_DISTINCT_VALUE, false);
        itemId = context.getString(OpcSourceConstants.CONFIG_ITEM_ID);
        itemIds = context.getString(OpcSourceConstants.CONFIG_ITEM_IDS);
        timeFormat = context.getString(OpcSourceConstants.CONFIG_TIME_FORMAT, "millis");
        format = context.getString(OpcSourceConstants.CONFIG_LINE_FORMAT, "{ITEM_ID};{TIME};{VALUE};{QUALITY};{ERROR}");
    }

    @Override
    public void start() {
        LogManager.getLogManager().reset();
//        SLF4JBridgeHandler.install();
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(hostname);
        ci.setDomain(domain);
        ci.setUser(user);
        ci.setPassword(password);
        ci.setProgId(progId);
        ci.setClsid(clsId);
        // create a new server
        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            counterGroup.incrementAndGet("open.attempts");
            server.connect();
            access = new SyncAccess(server, fetchIntervalInMs);
            DataCallback itemCallback = createItemCallback();
            access.addItem(itemId, itemCallback);
            access.bind();

        } catch (JIException | AlreadyConnectedException | DuplicateGroupException | UnknownHostException | AddFailedException | NotConnectedException e) {
            throw new OpcException("Error during start", e);
        }
    }

    private DataCallback createItemCallback() {
        return new DataCallback() {
            private long lastTime = 0;
            private String lastValue = "";
            @Override
            public void changed(Item item, ItemState state) {
                long currentTime = state.getTimestamp().getTimeInMillis();
                if(distinctTimeStamp && currentTime == lastTime) {
                    // equal time and distinctTime, do not track
                    return;
                }
                lastTime = currentTime;
                try {
                    JIVariant value = state.getValue();
                    Object object = value.getObject();
                    String converted = convert(object);
                    if(distinctValue && converted.equals(lastValue)) {
                        // equal value and distinctValue, do not track
                        return;
                    }
                    lastValue = converted;
                    String message = format;
                    message = message.replace("{ITEM_ID}", item.getId());
                    if("millis".equalsIgnoreCase(timeFormat)) {
                        message = message.replace("{TIME}", Long.toString(state.getTimestamp().getTimeInMillis()));
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
                        message = message.replace("{TIME}", sdf.format(state.getTimestamp().getTime()));
                    }
                    message = message.replace("{VALUE}", converted);
                    message = message.replace("{QUALITY}", Short.toString(state.getQuality()));
                    message = message.replace("{ERROR}", Integer.toString(state.getErrorCode()));
                    Event event = EventBuilder.withBody(message, Charset.forName("UTF-8"));
                    getChannelProcessor().processEvent(event);
                    counterGroup.incrementAndGet("events.processed");
                    logger.debug("Event processed: " + message);

                } catch(ChannelException | JIException e) {
                    logger.error("During item evaluation", e);
                    counterGroup.incrementAndGet("events.failed");
                }
            }
        };
    }


    private String convert(Object object) {
        if(object == null) {
            return "[empty]";
        }
        if(object instanceof JIString) {
            return ((JIString)object).getString();
        } else if(object instanceof IJIUnsigned) {
            return ((IJIUnsigned)object).getValue().toString();
        }  else if(object instanceof JIArray) {
            JIArray array = (JIArray) object;
            Object[] arrayInstance = (Object[])array.getArrayInstance();
            StringBuilder buf = new StringBuilder();
            boolean firstRun = true;
            for (Object o : arrayInstance) {
                if(!firstRun) {
                    buf.append(',');
                }
                buf.append(convert(o));
                firstRun = false;
            }
            return buf.toString();
        }
        else if(object instanceof Character) {
            char o = (Character) object;
            return Integer.toString((int)o);
        } else {
            return object.toString();
        }
    }

    @Override
    public void stop() {
        try {
            // implement a bit saver
            access.clear();
            Thread.sleep(2000);
            access.unbind();
        } catch (InterruptedException | JIException e) {
            logger.error("OPC exception occured: ", e);
        }
    }
}