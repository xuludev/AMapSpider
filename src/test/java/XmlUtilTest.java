import com.amap.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Administrator on 2016/6/3.
 */
public class XmlUtilTest {

    @Test
    public void testCreateNodeIfNotExist() {
        try {
            Document document = XmlUtil.parse("C:/Users/Administrator/Desktop/xml1.xml");
            Document document1 = XmlUtil.createNodeIfNotExist(document, "暴雨", "彩虹色");
            XmlUtil.write(document1);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
