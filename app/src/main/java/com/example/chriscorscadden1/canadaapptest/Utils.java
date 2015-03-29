package com.example.chriscorscadden1.canadaapptest;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static void CopyStream(InputStream is, OutputStream os)
    {
        // Byte size limit for the streams
        final int buffer_size=1024;
        try
        {
            // Stores the bytes that are copied from the input stream to the output stream
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                // Reads from the input stream until we reach the buffer_size limit
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                // Writes to the output stream
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
