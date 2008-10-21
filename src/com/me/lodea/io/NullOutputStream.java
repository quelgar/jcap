/*
 * $Id: NullOutputStream.java 346 2004-06-24 07:47:57Z odela01 $
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Sep 2, 2002
 * Time: 9:14:03 PM
 */

package com.me.lodea.io;

import java.io.OutputStream;


/**
 * An <code>OutputStream</code> that throws away any data written to it.
 * This class does not need to be instantiated - just use the
 * <code>BITBUCKET</code> static variable directly.
 *
 * <p>If you're wondering
 * why in God's name you would want such a class, it's useful for calling
 * methods that write to OutputStreams and have other side effects.
 * Sometimes you're not interested in the data written to the stream, only
 * the side effects.
 */
public final class NullOutputStream extends OutputStream
{

    /**
     * A NullOutputStream instance that throws away any data written to it.
     */
    public static final NullOutputStream BITBUCKET = new NullOutputStream();

    private NullOutputStream()
    {
    }

    public void write(final byte[] b)
    {
    }

    public void write(final byte[] b, final int off, final int len)
    {
    }

    public void write(final int b)
    {
    }

}
