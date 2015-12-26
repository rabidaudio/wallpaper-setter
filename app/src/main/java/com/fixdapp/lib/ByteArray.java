package com.fixdapp.lib;

/**
 * Created by charles on 12/9/15.
 *
 * Utility method for getting data from a byte array. Kind of a lighter-weight version of
 * a {@link java.nio.ByteBuffer}, but without position traversal and with bitwise accessor methods.
 */
public class ByteArray {

    private byte[] data;

    public ByteArray(byte[] data){
        this.data = data;
    }

    public ByteArray(String hex){
        this.data = hexToBytes(hex);
    }

    public byte[] raw() {
        return data;
    }

    public int size(){
        return data.length;
    }

    /**
     * get a NON-two's compliment signed version of a byte at a particular index (-128 - 127)
     */
    public int getByte(int index){
        return (data[index] & 0xFF) - 128;
    }

    /**
     * get an unsigned version (0-255) of a particular byte
     */
    public int getUnsignedByte(int index){
        return data[index] & 0xFF;
    }

    public int getUnsignedShort(int index){
        return ((data[index] & 0xFF) << 8) + (data[index+1] & 0xFF);
    }

    /**
     * get the value of a particular bit, from MSB to LSB (e.g. 0 for index 0 and 1 for index 15 of 0XXXXXXX XXXXXXX1)
     */
    public boolean getBit(int index){
        return getBit(index / 8, 7 - (index % 8));
    }

    /**
     * get the value of a bit by byte and bit indexes. bytes are indexed by MSB but bits by LSB
     * e.g. to get 1 from XXXXXXXX XXXXX1XX => (1, 2)
     */
    public boolean getBit(int byteIndex, int bitIndex){
        return getBit(data[byteIndex], bitIndex);
    }

    /**
     * get a subset range of bits inclusive, where start is from MSB
     * e.g. (0, 1, 4) for X0111XXX => 0111
     */
    public int getBits(int startIndex, int endIndex){
        if(startIndex > endIndex){
            throw new IllegalArgumentException("start index must be less than or equal to end index");
        }
        int val = 0;
        for(int i = startIndex; i<=endIndex; i++){
            val += (getBit(i) ? 1 : 0) << (endIndex-i);
        }
        return val;
    }

    /**
     * get a subset range of bits, where start and end are from LSB
     * e.g. (0, 3, 6) for X0111XXX => 0111
     */
    public int getBits(int byteIndex, int start, int end){
        return getBits(data[byteIndex], start, end);
    }

    public String getAsASCIIString(){
        return new String(data);
    }

    public String toString(){
        return bytesToHex(data);
    }


    /******************************** UTILITY METHODS *********************************************/

    public static boolean getBit(byte data, int position){
        if(position<0 || position > 7){
            throw new IllegalArgumentException("position must be between 0 and 7 inclusive");
        }
        return (data & (1 << position)) != 0;
    }

    public static int getBits(byte data, int start, int end){
        if(start <0 || start>7 || end<0||end>7){
            throw new IllegalArgumentException("positions must be between 0 and 7 inclusive");
        }
        if(start>end){
            throw new IllegalArgumentException("end must be greater than start");
        }
        if(start == end){
            return getBit(data, start) ? 1 : 0;
        }
        int mask = 0;
        for(int i=0; i<8; i++){
            if(i>=start && i<=end){
                mask += (1 << i);
            }
        }
        return (data & mask)>>start;
    }

    protected static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Print byte array as a hex string
     *
     * @see <a href="http://stackoverflow.com/a/9855338/1539043">Source</a>
     */
    public static String bytesToHex(byte... bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Convert a hex String into a byte array
     */
    public static byte[] hexToBytes(String byteArray){
        //cleanup
        byteArray = byteArray.replaceAll("\\s", "").toUpperCase();
        if(byteArray.startsWith("0X")){
            byteArray = byteArray.substring(2);
        }
        // make sure it is a hex string
        if(!byteArray.matches("^[0-9A-F]+$")){
            throw new IllegalArgumentException("Invalid hex string: "+byteArray);
        }
        //if odd number of chars, add a leading zero
        if((byteArray.length() % 2) != 0){
            byteArray = "0"+byteArray;
        }
        //create
        byte[] result = new byte[byteArray.length()/2];
        for(int i = 0; i<result.length; i++){
            result[i] = hexToByte(byteArray.substring(2*i, 2*i+2));
        }
        return result;
    }

    /**
     * Turn a single 2 character hex string into a byte
     */
    public static byte hexToByte(String hex){
        return (byte)(Integer.parseInt(hex, 16) & 0xFF);
    }
}