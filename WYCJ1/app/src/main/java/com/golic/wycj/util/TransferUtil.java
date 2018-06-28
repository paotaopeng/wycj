package com.golic.wycj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;

public class TransferUtil
{
    public static String getStringFromStream(InputStream in) throws IOException
    {
        if (in == null)
        {
            return null;
        }
        int len = -1;
        byte[] b = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((len = in.read(b)) != -1)
        {
            baos.write(b, 0, len);
        }

        return baos.toString();
    }

    public static boolean transfer(InputStream source, OutputStream target,
            ProgressDialog pd)
    {
        if (source == null || target == null || pd == null)
        {
            return false;
        }

        try
        {
            int max = source.available();
            pd.setMax(max);
            int downloadSize = 0;
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = source.read(buf)) != -1)
            {
//                try
//                {
//                    Thread.sleep(10);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
                target.write(buf, 0, len);
                downloadSize += len;
                pd.setProgress(downloadSize);
            }
            source.close();
            target.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transfer(InputStream source, OutputStream target,
            ProgressDialog pd, int max)
    {
        if (source == null || target == null || pd == null)
        {
            return false;
        }

        try
        {
            pd.setMax(max/1024);
            int downloadSize = 0;
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = source.read(buf)) != -1)
            {
//                try
//                {
//                    Thread.sleep(10);
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
                target.write(buf, 0, len);
                downloadSize += len;
                pd.setProgress(downloadSize/1024);
            }
            source.close();
            target.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transfer(File source, File target)
    {
        InputStream in;
        OutputStream out;
        try
        {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            return transfer(in, out);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transfer(InputStream source, OutputStream target)
    {
        if (source == null || target == null)
        {
            return false;
        }
        try
        {
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = source.read(buf)) != -1)
            {
                target.write(buf, 0, len);
            }
            source.close();
            target.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean transfer(InputStream source, File target)
    {
        try
        {
            return transfer(source, new FileOutputStream(target));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
