package com.me.lodea.jcap;


public final class JCapPermissionException extends JCapException
{

    private static final long serialVersionUID = 5369487134863056164L;


    JCapPermissionException(final String message)
    {
        super(message);
    }

    JCapPermissionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    JCapPermissionException(final Throwable cause)
    {
        super(cause);
    }

}
