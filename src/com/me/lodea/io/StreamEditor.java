/*
 * $Id: StreamEditor.java 346 2004-06-24 07:47:57Z odela01 $
 *
 * Created by IntelliJ IDEA.
 * User: odela01
 * Date: Sep 12, 2002
 * Time: 7:35:56 PM
 */

package com.me.lodea.io;

import java.io.*;


/**
 * Allows for a stream of data to be edited/modified.
 * This interface can be implemented to edit or modify the data from an
 * <code>InputStream</code> and then write the modified data to an
 * <code>OutputStream</code>.
 * <code>StreamEditor</code>s can be called by classes that operate on
 * streams to provide arbitrary editing of the streams.
 *
 * <p>This is an example of the <em>Command</em> design pattern.
 *
 * @version $Revision: 1.1 $  $Date: 2002/09/19 05:52:43 $
 */
public interface StreamEditor
{

    /**
     * Read data from an <code>InputStream</code>,
     * modify it in some unspecified way
     * and write the results to an <code>OutputStream</code>.
     * How much data is read
     * or written is entirely up to the implementation. However, the
     * streams should not be closed by this method. In particular cases,
     * the stream data may be written out without modification, in which
     * case this method should return false.
     *
     * <p>Implementations may specify restrictions on the type of data
     * that they accept in the stream. For example, an particular
     * implementation may require the data to be text only.
     *
     * @param in the input data to be modified.
     * @param out the destination of the modified data.
     * @return true if the data written out differes from that read in,
     *  false otherwise.
     */
    public boolean process(InputStream in, OutputStream out)
        throws IOException;

}
