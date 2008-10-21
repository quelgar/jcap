/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 4:59:21 PM
 */

package com.me.lodea.jcap;

import com.me.lodea.io.StreamEditor;
import com.me.lodea.io.NullStreamEditor;

import java.net.NetworkInterface;
import java.util.Collection;
import java.util.ArrayList;
import java.io.*;


public final class JCapSession
{

    private static final String PROP_NATIVELOADED = "com.me.lodea.jcap.nativeLoaded";

    static
    {
        synchronized (Object.class)
        {
            if (!Boolean.getBoolean(PROP_NATIVELOADED))
            {
                System.setProperty(PROP_NATIVELOADED, String.valueOf(true));
                try
                {
                    System.loadLibrary("jcap");
                }
                catch (SecurityException e)
                {
                    System.setProperty(PROP_NATIVELOADED, String.valueOf(false));
                    throw e;
                }
                catch (UnsatisfiedLinkError e)
                {
                    System.setProperty(PROP_NATIVELOADED, String.valueOf(false));
                    throw e;
                }
            }
        }
    }

    public static native NetworkInterface getDefaultInterface()
        throws JCapException;

    public static JCapSession openSession(final NetworkInterface iface,
        final boolean promisc, final int snaplen, final int timeout)
    {
        if (iface == null)
        {
            throw new NullPointerException("Network interface cannot be null");
        }
        return new JCapSession(iface, null, false, promisc, snaplen, timeout);
    }

    public static JCapSession openSession(final NetworkInterface iface,
        final boolean promisc, final int snaplen)
    {
        return openSession(iface, promisc, snaplen, 0);
    }
    
    public static JCapSession openSession(final File dumpFile)
    {
        if (dumpFile == null)
        {
            throw new NullPointerException("dump file argument must not be null");
        }
        return new JCapSession(null, dumpFile, false, false, 0, 0);
    }
    
    public static JCapSession openSession(final InputStream in)
        throws IOException
    {
        final File tmpDump = File.createTempFile("jcap", "dump");
        final OutputStream out = new FileOutputStream(tmpDump);
        try
        {
            final StreamEditor editor = NullStreamEditor.getStandard();
            editor.process(in, out);
        }
        finally
        {
            out.close();
        }
        return new JCapSession(null, tmpDump, false, false, 0, 0);
    }

    /**
     * Stores a native pointer to native code state. Accessed only from
     * native code.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private long nativePointer;

    private final NetworkInterface iface;
    private final int snaplen;
    private final boolean promisc;
    private final int timeout;
    private final Collection<PacketListener> listeners = new ArrayList<PacketListener>(5);
    private final File dumpFile;
    private final boolean tmpDumpFile;


    private JCapSession(final NetworkInterface iface, final File dumpFile,
        final boolean tmpDumpFile, final boolean promisc,
        final int snaplen, final int timeout)
    {
        this.iface = iface;
        this.snaplen = snaplen;
        this.promisc = promisc;
        this.timeout = timeout;
        this.dumpFile = dumpFile;
        this.tmpDumpFile = tmpDumpFile;
        pcapOpen(iface == null ? null : iface.getName(),
            dumpFile == null ? null : dumpFile.getPath(),
            promisc, snaplen, timeout);
    }

    private native void pcapOpen(final String ifaceName, final String filename,
        final boolean promisc, final int snaplen, final int timeout);

    public void addPacketListener(final PacketListener listener)
    {
        listeners.add(listener);
    }

    public void removePacketListener(final PacketListener listener)
    {
        listeners.remove(listener);
    }

    public native void setFilter(final String filter);

    public native int capture(final int maxPackets);

    /**
     * Fire a {@code PacketEvent} to all listeners. This is only called by
     * native code.
     *
     * @param event The event to fire.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private void firePacketEvent(final PacketEvent event)
    {
        for (final PacketListener listener : listeners)
        {
            listener.packetCaptured(event);
        }
    }

    public void close()
    {
        pcapClose();
        if (tmpDumpFile)
        {
            dumpFile.delete();
        }
    }

    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }

    private native void pcapClose();

}
