/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 2:13:20 AM
 */

package com.me.lodea.jcap.packet;

import java.io.Serializable;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Packet implements Serializable
{

    private static final long serialVersionUID = 0L;


    private final byte[] data;
    private final List headers = new ArrayList(5);
    private int pos = 0;


    public Packet(final byte[] data)
    {
        this.data = data;
    }

    public boolean addHeader(final Header header) throws HeaderException
    {
        final DataInputStream dis = new DataInputStream(
            new ByteArrayInputStream(data, pos, data.length - pos));
        final int headerLen;
        try
        {
            headerLen = header.init(dis);
        }
        catch (IOException e)
        {
            throw new HeaderException(e);
        }
        if (headerLen < 0)
        {
            return false;
        }
        pos += headerLen;
        headers.add(header);
        return true;
    }

    public boolean addHeaders(final HeaderFactory factory) throws HeaderException
    {
        Header header = null;
        while ((header = factory.nextHeader(header)) != null)
        {
            addHeader(header);
        }
        return true;
    }

    public List getHeaders()
    {
        return Collections.unmodifiableList(headers);
    }

    public Header getLastHeader()
    {
        return (Header)headers.get(headers.size() - 1);
    }

}
