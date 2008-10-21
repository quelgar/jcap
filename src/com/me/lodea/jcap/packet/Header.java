/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 4:06:00 AM
 */

package com.me.lodea.jcap.packet;

import java.io.Serializable;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;


public abstract class Header implements Serializable
{

    private static final long serialVersionUID = 0L;

    public static final Logger logger = Logger.getLogger(Header.class.getName());

    private DataInputStream data;


    public final int init(final DataInputStream data)
        throws HeaderException, IOException
    {
        this.data = data;
        return decode(data);
    }

    protected abstract int decode(final DataInputStream data)
        throws HeaderException, IOException;

    public final byte[] getData()
    {
        try
        {
            final int len = this.data.available();
            this.data.mark(len);
            final byte[] data = new byte[len];
            this.data.readFully(data);
            this.data.reset();
            return data;
        }
        catch (IOException e)
        {
            assert false : "Should not happen";
        }
        return new byte[0];
    }

}
