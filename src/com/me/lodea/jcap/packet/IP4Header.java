/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 2:40:17 AM
 */

package com.me.lodea.jcap.packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;


public final class IP4Header extends Header
{

    private int tos;
    private int totalSize;
    private int id;
    private boolean dontFragment;
    private boolean moreFragments;
    private int fragmentOffset;
    private int ttl;
    private int protocol;
    private int checksum;
    private InetAddress src;
    private InetAddress dst;

    protected int decode(final DataInputStream data) throws HeaderException,
        IOException
    {
        final byte firstByte = data.readByte();
        final int version = (firstByte & 0x0F) >>> 4;
        if (version != 4)
        {
            logger.warning("Expecting IP version 4, but found: " + version);
        }
        final int headerLen = (firstByte & 0xF0) * 4;

        tos = data.readUnsignedByte();

        totalSize = data.readUnsignedShort();

        id = data.readUnsignedShort();

        final int fragmentData = data.readUnsignedShort();
        dontFragment = (fragmentData & 0x40) != 0;
        moreFragments = (fragmentData & 0x20) != 0;
        fragmentOffset = fragmentData & 0x1FFF;

        ttl = data.readUnsignedByte();

        protocol = data.readUnsignedByte();

        checksum = data.readUnsignedShort();

        final byte[] addr = new byte[4];
        data.readFully(addr);
        src = InetAddress.getByAddress(addr);
        data.readFully(addr);
        dst = InetAddress.getByAddress(addr);

        data.skipBytes(headerLen - 20); // skip options

        return headerLen;
    }

    public int getTypeOfService()
    {
        return tos;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public int getId()
    {
        return id;
    }

    public boolean isDontFragment()
    {
        return dontFragment;
    }

    public boolean isMoreFragments()
    {
        return moreFragments;
    }

    public int getFragmentOffset()
    {
        return fragmentOffset;
    }

    public int getTTL()
    {
        return ttl;
    }

    public int getProtocol()
    {
        return protocol;
    }

    public int getChecksum()
    {
        return checksum;
    }

    public InetAddress getSrc()
    {
        return src;
    }

    public InetAddress getDst()
    {
        return dst;
    }

}
