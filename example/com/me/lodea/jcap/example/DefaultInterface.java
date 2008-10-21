/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 4:54:56 PM
 */

package com.me.lodea.jcap.example;

import com.me.lodea.jcap.JCapSession;
import com.me.lodea.jcap.JCapException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public final class DefaultInterface
{

    public static void main(final String[] args) throws JCapException,
        SocketException
    {
        for (final Enumeration en = NetworkInterface.getNetworkInterfaces();
            en.hasMoreElements(); )
        {
            final NetworkInterface iface = (NetworkInterface)en.nextElement();
            printInterface(iface);
        }
        final NetworkInterface iface = JCapSession.getDefaultInterface();
        System.out.print("JCap default interface = ");
        printInterface(iface);
    }

    private static void printInterface(final NetworkInterface iface)
    {
        System.out.print(iface.getName());
        System.out.print(" : ");
        System.out.println(iface.getDisplayName());
    }

}
