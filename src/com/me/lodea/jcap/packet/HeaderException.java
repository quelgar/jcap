/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 7, 2004
 * Time: 4:29:16 AM
 */

package com.me.lodea.jcap.packet;

import com.me.lodea.jcap.JCapException;


public final class HeaderException extends JCapException
{
    
    public HeaderException(final String message)
    {
        super(message);
    }

    public HeaderException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public HeaderException(final Throwable cause)
    {
        super(cause);
    }

}
