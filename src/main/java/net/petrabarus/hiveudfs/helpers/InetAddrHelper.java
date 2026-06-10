package net.petrabarus.hiveudfs.helpers;


public class InetAddrHelper {

        public static long IPToLong(String ip) {
                if (ip == null || ip.isEmpty()) {
                        return 0L;
                }

                long result = 0;
                int segment = 0;      // 记录当前正在解析的段（0-255）
                int shift = 24;       // 初始左移位数，每解析完一段减少 8

                for (int i = 0; i < ip.length(); i++) {
                        char c = ip.charAt(i);

                        if (c == '.') {
                                // 遇到分隔符，将当前段拼接到结果中
                                result |= (long)(segment & 0xFF) << shift;
                                shift -= 8;
                                segment = 0;  // 重置，准备解析下一段
                        } else {
                                // 核心：手动将字符转为数字，替代 Integer.parseInt
                                // '0' 的 ASCII 码是 48，字符相减直接得到数值
                                segment = segment * 10 + (c - '0');
                        }
                }

                // 处理最后一段（因为最后没有 '.' 触发拼接）
                result |= (long)(segment & 0xFF);

                return result;
        }
        public static String longToIP(long ip) {
                return ((ip >> 24) & 0xFF) + "."
                        + ((ip >> 16) & 0xFF) + "."
                        + ((ip >> 8) & 0xFF) + "."
                        + (ip & 0xFF);

        }

}
