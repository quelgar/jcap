/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 4:54:03 PM
 */

package com.me.lodea.jcap.packet;

public class TCPIPHeaderFactory implements HeaderFactory
{

    private static final HeaderFactory ethernetBased = new TCPIPHeaderFactory();

    public static HeaderFactory getEthernetBased()
    {
        return ethernetBased;
    }

    public final Header nextHeader(final Header previousHeader)
    {
        if (previousHeader == null)
        {
            return createBaseHeader();
        }

        if (previousHeader instanceof EthernetHeader)
        {
            final EthernetHeader header = (EthernetHeader)previousHeader;
            if (header.isType(EthernetHeader.TYPE_IP4))
            {
                return new IP4Header();
            }
        }

        return null;
    }

    protected Header createBaseHeader()
    {
        return new EthernetHeader();
    }

}
