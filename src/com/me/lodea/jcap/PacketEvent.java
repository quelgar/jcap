/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 5:17:39 PM
 */

package com.me.lodea.jcap;

import java.util.EventObject;


public final class PacketEvent extends EventObject
{

    private final long timeStamp;
    private final int realLen;
    private final byte[] data;

    PacketEvent(final Object source, final long timeStamp,
        final int realLen, final byte[] data)
    {
        super(source);
        this.timeStamp = timeStamp;
        this.realLen = realLen;
        this.data = data;
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
        return data.length;
    }

    public byte[] getData()
    {
        return data;
    }

}
