/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 5:15:49 PM
 */

package com.me.lodea.jcap;

import java.util.EventListener;


public interface PacketListener extends EventListener
{

    void packetCaptured(PacketEvent event);

}
