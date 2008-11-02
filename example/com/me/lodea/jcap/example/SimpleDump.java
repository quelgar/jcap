/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 7:40:45 PM
 */

package com.me.lodea.jcap.example;

import com.me.lodea.jcap.JCapSession;
import com.me.lodea.jcap.PacketListener;
import com.me.lodea.jcap.PacketEvent;
import com.me.lodea.jcap.JCapException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Collection;
import java.util.LinkedList;
import java.nio.ByteBuffer;


@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToSystemExit"})
public final class SimpleDump implements PacketListener
{

    public static void printEvent(final PacketEvent event)
    {
        System.out.println("Time = " + new Date(event.getTimeStamp()));
        System.out.println(
            "Captured = " + event.getCaptureLength() + " bytes");
        System.out.println("Length = " + event.getRealLength() + " bytes");
        final ByteBuffer buf = event.data();
        while (buf.hasRemaining())
        {
            final byte b = buf.get();
            if (b > 0x1F && b < 0x7F)
            {
                System.out.print((char)b);
            }
            else
            {
                System.out.print('.');
            }
        }
        System.out.println();
        System.out.println();
    }

    private final Collection<PacketEvent> events = new LinkedList<PacketEvent>();

    public void packetCaptured(final PacketEvent event)
    {
        events.add(event);
        printEvent(event);
    }

    public static void execute(final String interfaceName)
        throws SocketException, JCapException
    {
        final NetworkInterface iface = NetworkInterface.getByName(
            interfaceName);
        if (iface == null)
        {
            System.out.println("Interface " + interfaceName + " not found");
            System.exit(200);
        }
        final JCapSession jcap = JCapSession.openSession(iface, false, 2000);
        final SimpleDump dump1 = new SimpleDump();
        jcap.addPacketListener(dump1);
        jcap.capture(6);

        System.out.println("**** CAPTURE DONE ****");
        System.out.printf("Stored %d events%n%n", dump1.events.size());
        for (final PacketEvent e : dump1.events)
        {
            printEvent(e);
        }

        jcap.close();

        System.out.println("**** FIRST PCAP CLOSED ****");

        // event data no longer usable - accessing it will probably crash JVM!!

        final JCapSession jcap2 = JCapSession.openSession(iface, false, 30);
        jcap2.addPacketListener(new SimpleDump());
        jcap2.capture(4);
    }

    public static void main(final String[] args)
        throws SocketException, JCapException
    {
        if (args.length < 1)
        {
            System.out.println("Must specify the interface to sniff");
            System.exit(100);
        }
        execute(args[0]);
    }

}
