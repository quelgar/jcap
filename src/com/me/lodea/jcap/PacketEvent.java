/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 5:17:39 PM
 */

package com.me.lodea.jcap;

import java.nio.ByteBuffer;


public final class PacketEvent
{

    private final long timeStamp;
    private final int realLen;
    private final ByteBuffer data;
    private final JCapSession source;


    PacketEvent(final JCapSession source, final long timeStamp,
        final int realLen, final ByteBuffer data)
    {
        this.source = source;
        this.timeStamp = timeStamp;
        this.realLen = realLen;
        this.data = data;
    }

    public JCapSession getSource()
    {
        return source;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public int getRealLength()
    {
        return realLen;
    }

    public int getCaptureLength()
    {
        return data.capacity();
    }

    public ByteBuffer data()
    {
        return data.asReadOnlyBuffer();
    }

}
