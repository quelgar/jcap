/**
 * $Id: CAByteArrayOutputStream.java 346 2004-06-24 07:47:57Z odela01 $
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Jul 22, 2003
 * Time: 6:10:27 PM
 */

package com.me.lodea.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * A <code>ByteArrayOutputStream</code> with some extras.
 */
public final class LodeaByteArrayOutputStream extends ByteArrayOutputStream
{

    private static final int BUFSIZE = 2*1024;

    public LodeaByteArrayOutputStream()
    {
        super();
    }

    /**
     * @see ByteArrayOutputStream#ByteArrayOutputStream(int)
     */
    public LodeaByteArrayOutputStream(final int size)
    {
        super(size);
    }


    /**
     * Writes all the available data from the specified <code>InputStream</code>.
     * This method will keep reading from the <code>InputStream</code> until
     * end of file is reached. <strong>Use at your own risk</strong> -
     * if the <code>InputStream</code> has a large amount of data, the
     * method will perform very badly and use a lot of memory.
     *
     * @param in The stream to read data from.
     */ 
    public void writeEntireStream(final InputStream in) throws IOException
    {
        final byte[] buf = new byte[BUFSIZE];
        int bytesRead;
        while ((bytesRead = in.read(buf)) >= 0)
        {
            write(buf, 0, bytesRead);
        }
    }

}
