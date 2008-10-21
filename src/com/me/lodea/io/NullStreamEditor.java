/*
 * $Id: NullStreamEditor.java 195 2004-06-04 11:11:43Z odela01 $
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Jun 3, 2004
 * Time: 5:39:45 PM
 */

package com.me.lodea.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public final class NullStreamEditor implements StreamEditor
{

    private static StreamEditor standard = null;

    public static synchronized StreamEditor getStandard()
    {
        if (standard == null)
        {
            standard = new NullStreamEditor(64*1024);
        }
        return standard;
    }

    
    private final byte[] buf;

    public NullStreamEditor(final int bufferSize)
    {
        buf = new byte[bufferSize];
    }

    public boolean process(final InputStream in, final OutputStream out)
        throws IOException
    {
        int bytesRead;
        while ((bytesRead = in.read(buf)) >= 0)
        {
            out.write(buf, 0, bytesRead);
        }
        return false;
    }

}
