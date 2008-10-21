/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 5:00:16 PM
 */

package com.me.lodea.jcap.datagram;

import com.me.lodea.jcap.PacketListener;
import com.me.lodea.jcap.PacketEvent;
import com.me.lodea.jcap.packet.Packet;
import com.me.lodea.jcap.packet.TCPIPHeaderFactory;
import com.me.lodea.jcap.packet.HeaderException;
import com.me.lodea.jcap.packet.IP4Header;

import java.util.Map;
import java.util.HashMap;
import java.util.List;


public final class IPDatagramListener implements PacketListener
{

    private final Map datagramMap = new HashMap();


    public void packetCaptured(final PacketEvent event)
    {
        final Packet packet = new Packet(event.getData());
        try
        {
            packet.addHeaders(TCPIPHeaderFactory.getEthernetBased());
            final IP4Header ip4Header = (IP4Header)packet.getHeaders().get(1);
            final List fragments = (List)datagramMap.get(
                new Integer(ip4Header.getId()));
        }
        catch (HeaderException e)
        {

        }
    }

}
