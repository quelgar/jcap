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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;


public final class SimpleDump implements PacketListener
{

    public void packetCaptured(final PacketEvent event)
    {
        System.out.println("Time = " + new Date(event.getTimeStamp()));
        System.out.println("Captured = " + event.getCaptureLength() + " bytes");
        System.out.println("Length = " + event.getRealLength() + " bytes");
    }

    public static void main(final String[] args) throws SocketException
    {
        if (args.length < 1)
        {
            System.out.println("Must specify the interface to sniff");
            System.exit(100);
        }
        final NetworkInterface iface = NetworkInterface.getByName(args[0]);
        if (iface == null)
        {
            System.out.println("Interface " + args[0] + " not found");
            System.exit(200);
        }
        final JCapSession jcap = JCapSession.openSession(iface, false, 2000);
        jcap.addPacketListener(new SimpleDump());
        jcap.capture(10);
    }

}
