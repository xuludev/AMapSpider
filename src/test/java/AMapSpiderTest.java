import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by LucasX on 2016/5/3.
 */
public class AMapSpiderTest {

    @Test
    public void encode() throws UnsupportedEncodingException {
        String str = "120.718893|31.347639|120.883687|31.441858";
        String newStr = URLEncoder.encode(str, "UTF-8");
        System.out.println(newStr);
    }

    @Test
    public void decode() throws UnsupportedEncodingException {
        String str = "%E7%BE%8E%E9%A3%9F";
        StringBuffer stringBufferResult = new StringBuffer();
        for (int i = 0; i < str.length(); i++)
        {
            char chr = str.charAt(i);
            if (chr == '%')
            {
                StringBuffer stringTmp = new StringBuffer();
                stringTmp.append(str.charAt(i + 1)).append(str.charAt(i + 2));
                //转换字符，16进制转换成整型
                stringBufferResult.append((char)(Integer.valueOf(stringTmp.toString(), 16).intValue()));
                i = i + 2;
                continue;
            }
            stringBufferResult.append(chr);
        }

        String newStr = new String(stringBufferResult.toString().getBytes("ISO-8859-1"), "UTF-8"); //编码转换
        System.out.println(newStr);
    }
}
