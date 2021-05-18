package com.chinaunicom.torn.mcloud.component;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdk.nashorn.internal.objects.NativeArray.pop;
import static jdk.nashorn.internal.runtime.JSType.toInteger;


public class CIDRUtils {
    private final String cidr;

    private InetAddress inetAddress;
    private InetAddress startAddress;
    private InetAddress endAddress;
    private final int prefixLength;


    private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,2})"; // 0 -> 32
    private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
    private static final Pattern cidrPattern = Pattern.compile(SLASH_FORMAT);
    private static final int NBITS = 32;

    private final int netmask;
    private final int address;
    private final int network;
    private final int broadcast;

    private static final String PARSE_FAIL = "Could not parse [%s]";

    public CIDRUtils(String cidr) throws UnknownHostException {
        final Matcher matcher = cidrPattern.matcher(cidr);

        this.cidr = cidr;

        /* split CIDR to address and prefix part */
        if (this.cidr.contains("/")) {
            int index = this.cidr.indexOf("/");
            String addressPart = this.cidr.substring(0, index);
            String networkPart = this.cidr.substring(index + 1);

            inetAddress = InetAddress.getByName(addressPart);
            prefixLength = Integer.parseInt(networkPart);

            calculate();
        } else {
            throw new IllegalArgumentException("not an valid CIDR format!");
        }


        if (matcher.matches()) {
            this.address = matchAddress(matcher);

            /* Create a binary netmask from the number of bits specification /x */

            final int trailingZeroes = NBITS - rangeCheck(Integer.parseInt(matcher.group(5)), 0, NBITS);
            /*
            * An IPv4 netmask consists of 32 bits, a contiguous sequence
            * of the specified number of ones followed by all zeros.
            * So, it can be obtained by shifting an unsigned integer (32 bits) to the left by
            * the number of trailing zeros which is (32 - the # bits specification).
            * Note that there is no unsigned left shift operator, so we have to use
            * a long to ensure that the left-most bit is shifted out correctly.
            */
            this.netmask = (int) (0x0FFFFFFFFL << trailingZeroes );

            /* Calculate base network address */
            this.network = address & netmask;

            /* Calculate broadcast address */
            this.broadcast = network | ~netmask;
        } else {
          throw new IllegalArgumentException(String.format(PARSE_FAIL, cidr));
        }
    }



    private void calculate() throws UnknownHostException {

        ByteBuffer maskBuffer;
        int targetSize;
        if (inetAddress.getAddress().length == 4) {
            maskBuffer =
                    ByteBuffer
                            .allocate(4)
                            .putInt(-1);
            targetSize = 4;
        } else {
            maskBuffer = ByteBuffer.allocate(16)
                    .putLong(-1L)
                    .putLong(-1L);
            targetSize = 16;
        }

        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

        System.out.println(maskBuffer.array());
        ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
        System.out.println(buffer);
        System.out.println(buffer.array());
        BigInteger ipVal = new BigInteger(1, buffer.array());
        System.out.println(ipVal);


        BigInteger startIp = ipVal.and(mask);
        BigInteger endIp = startIp.add(mask.not());
        System.out.println(mask);
        System.out.println(mask.not());
        System.out.println(mask.divideAndRemainder(mask));


        byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
        byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

        this.startAddress = InetAddress.getByAddress(startIpArr);
        this.endAddress = InetAddress.getByAddress(endIpArr);

    }

    private byte[] toBytes(byte[] array, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<Byte>();
        while (counter < targetSize && (array.length - 1 - counter >= 0)) {
            newArr.add(0, array[array.length - 1 - counter]);
            counter++;
        }

        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {

            newArr.add(0, (byte) 0);
        }

        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }
        return ret;
    }

    public String getNetworkAddress() {

        return this.startAddress.getHostAddress();
    }

    public String getBroadcastAddress() {
        return this.endAddress.getHostAddress();
    }


    public boolean isInRange(String ipAddress) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        BigInteger start = new BigInteger(1, this.startAddress.getAddress());
        BigInteger end = new BigInteger(1, this.endAddress.getAddress());
        BigInteger target = new BigInteger(1, address.getAddress());

        int st = start.compareTo(target);
        int te = target.compareTo(end);

        return (st == -1 || st == 0) && (te == -1 || te == 0);
    }




    public String[] getAllAddresses() {
        final int ct = getAddressCount();
        final String[] addresses = new String[ct];
        if (ct == 0) {
            return addresses;
        }
        for (int add = low(), j=0; add <= high(); ++add, ++j) {
            addresses[j] = format(toArray(add));
        }
        return addresses;
    }

    @Deprecated
    public int getAddressCount() {
        final long countLong = getAddressCountLong();
        if (countLong > Integer.MAX_VALUE) {
            throw new RuntimeException("Count is larger than an integer: " + countLong);
        }
        // N.B. cannot be negative
        return (int)countLong;
    }

    /**
     * Get the count of available addresses.
     * Will be zero for CIDR/31 and CIDR/32 if the inclusive flag is false.
     * @return the count of addresses, may be zero.
     * @since 3.4
     */
    public long getAddressCountLong() {
        final long b = broadcastLong();
        final long n = networkLong();
        final long count = b - n + (isInclusiveHostCount() ? 1 : -1);
        return count < 0 ? 0 : count;
    }

    private static final long UNSIGNED_INT_MASK = 0x0FFFFFFFFL;

    private long networkLong()  { return network &  UNSIGNED_INT_MASK; }
    private long broadcastLong(){ return broadcast &  UNSIGNED_INT_MASK; }

    private int low() {
        return isInclusiveHostCount() ? network :
            broadcastLong() - networkLong() > 1 ? network + 1 : 0;
    }

    private int high() {
        return isInclusiveHostCount() ? broadcast :
            broadcastLong() - networkLong() > 1 ? broadcast -1  : 0;
    }

    private boolean inclusiveHostCount = false;

    public boolean isInclusiveHostCount() {
        return inclusiveHostCount;
    }

    private static int matchAddress(final Matcher matcher) {
        int addr = 0;
        for (int i = 1; i <= 4; ++i) {
            final int n = rangeCheck(Integer.parseInt(matcher.group(i)), 0, 255);
            addr |= (n & 0xff) << 8*(4-i);
        }
        return addr;
    }

    private static int rangeCheck(final int value, final int begin, final int end) {
        if (value >= begin && value <= end) { // (begin,end]
            return value;
        }

        throw new IllegalArgumentException("Value [" + value + "] not in range ["+begin+","+end+"]");
    }

    private String format(final int[] octets) {
        final StringBuilder str = new StringBuilder();
        for (int i =0; i < octets.length; ++i){
            str.append(octets[i]);
            if (i != octets.length - 1) {
                str.append(".");
            }
        }
        return str.toString();
    }

    private int[] toArray(final int val) {
        final int ret[] = new int[4];
        for (int j = 3; j >= 0; --j) {
            ret[j] |= val >>> 8*(3-j) & 0xff;
        }
        return ret;
    }


}
