package com.telventi.afirma.wsclient.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.io.PrintStream;
import java.io.Serializable;

public class UtilsBase64
{
    public class OutputStream extends FilterOutputStream
    {

        public void write(int i)
            throws IOException
        {
            buffer[position++] = (byte)i;
            if(position >= bufferLength)
            {
                if(encode)
                {
                    super.out.write(encode3to4(buffer, bufferLength));
                    lineLength += 4;
                    if(lineLength >= 76)
                    {
                        super.out.write(10);
                        lineLength = 0;
                    }
                } else
                {
                    super.out.write(decode4to3(buffer));
                }
                position = 0;
            }
        }

        public void write(byte abyte0[], int i, int j)
            throws IOException
        {
            for(int k = 0; k < j; k++)
                write(abyte0[i + k]);

        }

        public void flush()
            throws IOException
        {
            if(position > 0)
                if(encode)
                    super.out.write(encode3to4(buffer, position));
                else
                    throw new IOException("Base64 input not properly padded.");
            super.flush();
            super.out.flush();
        }

        public void close()
            throws IOException
        {
            flush();
            super.close();
            super.out.close();
            buffer = null;
            super.out = null;
        }

        private boolean encode;
        private int position;
        private byte buffer[];
        private int bufferLength;
        private int lineLength;

        public OutputStream(java.io.OutputStream outputstream)
        {
            this(outputstream, true);
        }

        public OutputStream(java.io.OutputStream outputstream, boolean flag)
        {
            super(outputstream);
            encode = flag;
            bufferLength = flag ? 3 : 4;
            buffer = new byte[bufferLength];
            position = 0;
            lineLength = 0;
        }
    }

    public class InputStream extends FilterInputStream
    {

        public int read()
            throws IOException
        {
            if(position < 0)
                if(encode)
                {
                    byte abyte0[] = new byte[3];
                    numSigBytes = 0;
                    for(int i = 0; i < 3; i++)
                        try
                        {
                            int k = super.in.read();
                            if(k >= 0)
                            {
                                abyte0[i] = (byte)k;
                                numSigBytes++;
                            }
                        }
                        catch(IOException ioexception)
                        {
                            if(i == 0)
                                throw ioexception;
                        }

                    if(numSigBytes > 0)
                    {
                        encode3to4(abyte0, 0, numSigBytes, buffer, 0);
                        position = 0;
                    }
                } else
                {
                    byte abyte1[] = new byte[4];
                    int j = 0;
                    for(j = 0; j < 4; j++)
                    {
                        int l = 0;
                        do
                            l = super.in.read();
                        while(l >= 0 && UtilsBase64.DECODABET[l & 0x7f] < -5);
                        if(l < 0)
                            break;
                        abyte1[j] = (byte)l;
                    }

                    if(j == 4)
                    {
                        numSigBytes = decode4to3(abyte1, 0, buffer, 0);
                        position = 0;
                    }
                }
            if(position >= 0)
            {
                if(position >= numSigBytes)
                    return -1;
                byte byte0 = buffer[position++];
                if(position >= bufferLength)
                    position = -1;
                return byte0;
            } else
            {
                return -1;
            }
        }

        public int read(byte abyte0[], int i, int j)
            throws IOException
        {
            int k;
            for(k = 0; k < j; k++)
            {
                int l = read();
                if(l < 0)
                    return -1;
                abyte0[i + k] = (byte)l;
            }

            return k;
        }

        private boolean encode;
        private int position;
        private byte buffer[];
        private int bufferLength;
        private int numSigBytes;

        public InputStream(java.io.InputStream inputstream)
        {
            this(inputstream, false);
        }

        public InputStream(java.io.InputStream inputstream, boolean flag)
        {
            super(inputstream);
            encode = flag;
            bufferLength = flag ? 4 : 3;
            buffer = new byte[bufferLength];
            position = -1;
        }
    }


    public UtilsBase64()
    {
    }
    
    private byte[] encode3to4(byte abyte0[], int i)
    {
        byte abyte1[] = new byte[4];
        encode3to4(abyte0, 0, i, abyte1, 0);
        return abyte1;
    }

    private byte[] encode3to4(byte abyte0[], int i, int j, byte abyte1[], int k)
    {
        int l = (j <= 0 ? 0 : (abyte0[i] << 24) >>> 8) | (j <= 1 ? 0 : (abyte0[i + 1] << 24) >>> 16) | (j <= 2 ? 0 : (abyte0[i + 2] << 24) >>> 24);
        switch(j)
        {
        case 3: // '\003'
            abyte1[k] = ALPHABET[l >>> 18];
            abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
            abyte1[k + 2] = ALPHABET[l >>> 6 & 0x3f];
            abyte1[k + 3] = ALPHABET[l & 0x3f];
            return abyte1;

        case 2: // '\002'
            abyte1[k] = ALPHABET[l >>> 18];
            abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
            abyte1[k + 2] = ALPHABET[l >>> 6 & 0x3f];
            abyte1[k + 3] = 61;
            return abyte1;

        case 1: // '\001'
            abyte1[k] = ALPHABET[l >>> 18];
            abyte1[k + 1] = ALPHABET[l >>> 12 & 0x3f];
            abyte1[k + 2] = 61;
            abyte1[k + 3] = 61;
            return abyte1;
        }
        return abyte1;
    }

    public String encodeObject(Serializable serializable)
    {
        ByteArrayOutputStream bytearrayoutputstream = null;
        OutputStream outputstream = null;
        ObjectOutputStream objectoutputstream = null;
        try
        {
            bytearrayoutputstream = new ByteArrayOutputStream();
            outputstream = new OutputStream(bytearrayoutputstream, true);
            objectoutputstream = new ObjectOutputStream(outputstream);
            objectoutputstream.writeObject(serializable);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
            String s = null;
            return s;
        }
        finally
        {
            try
            {
                objectoutputstream.close();
            }
            catch(Exception exception1) { }
            try
            {
                outputstream.close();
            }
            catch(Exception exception2) { }
            try
            {
                bytearrayoutputstream.close();
            }
            catch(Exception exception3) { }
        }
        return new String(bytearrayoutputstream.toByteArray());
    }

    public String encodeBytes(byte abyte0[])
    {
        return encodeBytes(abyte0, 0, abyte0.length);
    }

    public String encodeBytes(byte abyte0[], int i, int j)
    {
        int k = (j * 4) / 3;
        byte abyte1[] = new byte[k + (j % 3 <= 0 ? 0 : 4) + k / 76];
        int l = 0;
        int i1 = 0;
        int j1 = j - 2;
        int k1 = 0;
        while(l < j1)
        {
            encode3to4(abyte0, l, 3, abyte1, i1);
            if((k1 += 4) == 76)
            {
                abyte1[i1 + 4] = 10;
                i1++;
                k1 = 0;
            }
            l += 3;
            i1 += 4;
        }
        if(l < j)
        {
            encode3to4(abyte0, l, j - l, abyte1, i1);
            i1 += 4;
        }
        return new String(abyte1, 0, i1);
    }

    public String encodeString(String s)
    {
        return encodeBytes(s.getBytes());
    }

    private byte[] decode4to3(byte abyte0[])
    {
        byte abyte1[] = new byte[3];
        int i = decode4to3(abyte0, 0, abyte1, 0);
        byte abyte2[] = new byte[i];
        for(int j = 0; j < i; j++)
            abyte2[j] = abyte1[j];

        return abyte2;
    }

    private int decode4to3(byte abyte0[], int i, byte abyte1[], int j)
    {
        if(abyte0[i + 2] == 61)
        {
            int k = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12;
            abyte1[j] = (byte)(k >>> 16);
            return 1;
        }
        if(abyte0[i + 3] == 61)
        {
            int l = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12 | (DECODABET[abyte0[i + 2]] << 24) >>> 18;
            abyte1[j] = (byte)(l >>> 16);
            abyte1[j + 1] = (byte)(l >>> 8);
            return 2;
        } else
        {
            int i1 = (DECODABET[abyte0[i]] << 24) >>> 6 | (DECODABET[abyte0[i + 1]] << 24) >>> 12 | (DECODABET[abyte0[i + 2]] << 24) >>> 18 | (DECODABET[abyte0[i + 3]] << 24) >>> 24;
            abyte1[j] = (byte)(i1 >> 16);
            abyte1[j + 1] = (byte)(i1 >> 8);
            abyte1[j + 2] = (byte)i1;
            return 3;
        }
    }

    public byte[] decode(String s)
    {
        byte abyte0[] = s.getBytes();
        return decode(abyte0, 0, abyte0.length);
    }

    public String decodeToString(String s)
    {
        return new String(decode(s));
    }

    public Object decodeToObject(String s)
    {
        byte abyte0[] = decode(s);
        ByteArrayInputStream bytearrayinputstream = null;
        ObjectInputStream objectinputstream = null;
        try
        {
            try
            {
                bytearrayinputstream = new ByteArrayInputStream(abyte0);
                objectinputstream = new ObjectInputStream(bytearrayinputstream);
                Object obj = objectinputstream.readObject();
                return obj;
            }
            catch(IOException ioexception)
            {
                ioexception.printStackTrace();
                Object obj1 = null;
                return obj1;
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                classnotfoundexception.printStackTrace();
            }
            Object obj2 = null;
            return obj2;
        }
        finally
        {
            try
            {
                bytearrayinputstream.close();
            }
            catch(Exception exception1) { }
            try
            {
                objectinputstream.close();
            }
            catch(Exception exception2) { }
        }
    }

    public byte[] decode(byte abyte0[], int i, int j)
    {
        int k = (j * 3) / 4;
        byte abyte1[] = new byte[k];
        int l = 0;
        byte abyte2[] = new byte[4];
        int i1 = 0;

        for(int j1 = 0; j1 < j; j1++)
        {
            byte byte0 = (byte)(abyte0[j1] & 0x7f);
            byte byte1 = DECODABET[byte0];
            if(byte1 >= -5)
            {
                if(byte1 < -1)
                    continue;
                abyte2[i1++] = byte0;
                if(i1 <= 3)
                    continue;
                l += decode4to3(abyte2, 0, abyte1, l);
                i1 = 0;
                if(byte0 == 61)
                    break;
            } else
            {
                System.err.println("Bad Base64 input character at " + j1 + ": " + abyte0[j1] + "(decimal)");
                return null;
            }
        }

        byte abyte3[] = new byte[l];
        System.arraycopy(abyte1, 0, abyte3, 0, l);
        return abyte3;
    }

    public static final boolean ENCODE = true;
    public static final boolean DECODE = false;
    private static final byte ALPHABET[] = {
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100,
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55,
        56, 57, 43, 47
    };
    private static final byte DECODABET[] = {
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5,
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9,
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9,
        -9, -9, -9, 62, -9, -9, -9, 63, 52, 53,
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9,
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
        25, -9, -9, -9, -9, -9, -9, 26, 27, 28,
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51, -9, -9, -9, -9
    };
}
