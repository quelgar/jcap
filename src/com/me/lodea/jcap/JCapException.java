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

    public JCapException(final String message)
    {
        super(message);
    }

    public JCapException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public JCapException(final Throwable cause)
    {
        super(cause);
    }

}
