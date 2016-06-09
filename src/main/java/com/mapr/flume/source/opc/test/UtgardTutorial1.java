package com.mapr.flume.source.opc.test;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.*;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.*;

public class UtgardTutorial1 {
/*
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().reset();
//        SLF4JBridgeHandler.install();
        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();
        ci.setHost("10.0.0.157");
//        ci.setDomain("10.0.0.157");
        ci.setUser("Administrator");
        ci.setPassword("?QhA(umAVR");
        ci.setProgId("SWToolbox.TOPServer.V5");
        // ci.setClsid("680DFBF7-C92D-484D-84BE-06DC3DECCD68"); // if ProgId is not working, try it using the Clsid instead
//        final String itemId = "Simulation Examples.Functions.User1";
//        final String itemId = "Simulation Examples.Functions.Sine1";
//        final String itemId = "_System._Time_Second";
//        final String itemId = "Simulation Examples.Functions._System._NoError";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.Byte1";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.Word1";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.ByteArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.DWordArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.Char1";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.CharArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.FloatArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.DoubleArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.LongArray";
//        final String itemId = "Data Type Examples.8 Bit Device.R Registers.ShortArray";
        final String itemId = "Data Type Examples.8 Bit Device.R Registers.WordArray";
        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            // connect to server
            server.connect();
            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 100);
            access.addItem(itemId, new DataCallback() {
                private long last = 0;
                @Override
                public void changed(Item item, ItemState state) {
                    System.out.println(state);
//                    long current = state.getTimestamp().getTimeInMillis();
//                    if(last != current) {
                        try {
                            JIVariant value = state.getValue();
                            Object object = value.getObject();
                            System.out.println(convert(object));
                        } catch (JIException e) {
                            e.printStackTrace();
                        }
//                        last = current;
//                    }
//                    System.out.println(item.getId());
//                    try {
//                        System.out.println(state.getValue().getObjectAsString2());
//                    } catch (JIException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(state);
                }
            });
            // start reading
            access.bind();
            // wait a little bit
            Thread.sleep(100 * 1000);
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println("Exception " + e);
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }

    public static String convert(Object object) {
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
    } */
}