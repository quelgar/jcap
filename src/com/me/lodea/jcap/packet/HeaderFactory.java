/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 4:39:29 AM
 */

package com.me.lodea.jcap.packet;


public interface HeaderFactory
{

    Header nextHeader(Header previousHeader);

}
