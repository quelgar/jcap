/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 2:49:14 AM
 */

package com.me.lodea.jcap.packet;

import java.io.DataInputStream;
import java.io.IOException;


public final class EthernetHeader extends Header
{

    public static final int TYPE_IP4 = 0x0800;
    public static final int TYPE_ARP = 0x0806;
    public static final int TYPE_RARP = 0x8035;
    public static final int TYPE_APPLETALK = 0x809B;
    public static final int TYPE_AARP = 0x80F3;
    public static final int TYPE_IPV6 = 0x86DD;

    final byte[] src = new byte[6];
    final byte[] dst = new byte[6];
    int type;


    protected int decode(final DataInputStream data)
        throws HeaderException, IOException
    {
        data.readFully(dst);
        data.readFully(src);
        type = data.readUnsignedShort();
        return 14;
    }

    public byte[] getSourceAddress()
    {
        return (byte[])src.clone();
    }

    public byte[] getDestinationAddress()
    {
        return (byte[])dst.clone();
    }

    public int getType()
    {
        return type;
    }

    public boolean isType(final int type)
    {
        return type == this.type;
    }
    
}
