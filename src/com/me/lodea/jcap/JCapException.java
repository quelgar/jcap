/*
 * $Id$
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Nov 6, 2004
 * Time: 6:32:01 PM
 */

package com.me.lodea.jcap;

public class JCapException extends Exception
{
    private static final long serialVersionUID = 1299870000638505751L;


    protected JCapException(final String message)
    {
        super(message);
    }

    protected JCapException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    protected JCapException(final Throwable cause)
    {
        super(cause);
    }

}
