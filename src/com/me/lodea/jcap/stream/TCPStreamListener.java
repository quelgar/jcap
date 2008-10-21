/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 4:33:39 AM
 */

package com.me.lodea.jcap.stream;

import com.me.lodea.jcap.PacketListener;
import com.me.lodea.jcap.PacketEvent;
import com.me.lodea.jcap.packet.HeaderFactory;
import com.me.lodea.jcap.packet.Packet;
import com.me.lodea.jcap.packet.HeaderException;

import java.util.List;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.IOException;


public final class TCPStreamListener implements PacketListener
{

    private final HeaderFactory factory;
    private final List segments = new LinkedList();
    private int streamPos = 0;

    public TCPStreamListener(final HeaderFactory factory)
    {
        this.factory = factory;
    }

    public void packetCaptured(final PacketEvent event)
    {
        final Packet packet = new Packet(event.getData());
        try
        {
            if (packet.addHeaders(factory))
            {

            }

        }
        catch (HeaderException e)
        {

        }
    }

    public InputStream createInputStream()
    {
        return null;
    }

    private final class TCPInputStream extends InputStream
    {

        private int markedPos = 0;
        private int pos = 0;
        private StreamSegment currentSegment;
        private int currentSegIndex;

        {
            if (!segments.isEmpty())
            {
                currentSegment = (StreamSegment)segments.get(0);
                currentSegIndex = currentSegment.startIndex;
            }
        }

        public void mark(final int readlimit)
        {
            markedPos = pos;
        }

        public void reset()
        {
            pos = markedPos;
        }

        public boolean markSupported()
        {
            return true;
        }

        public int read(final byte[] b) throws IOException
        {
            return super.read(b);
        }

        public int read(final byte[] b, final int off, final int len) throws IOException
        {
            return super.read(b, off, len);
        }

        public long skip(final long n) throws IOException
        {
            return super.skip(n);
        }

        public int available() throws IOException
        {
            return super.available();
        }

        public void close() throws IOException
        {
            super.close();
        }

        public int read() throws IOException
        {
            return 0;
        }
    }


    private static final class StreamSegment
    {

        final byte[] data;
        final int startIndex;

        StreamSegment(final byte[] data, final int startIndex)
        {
            this.data = data;
            this.startIndex = startIndex;
        }

        boolean finished(final int pos)
        {
            return pos >= data.length;
        }

    }
}
