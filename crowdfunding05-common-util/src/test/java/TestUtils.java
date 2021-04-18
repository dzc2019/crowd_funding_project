import com.zcdeng.crowd.util.CrowdUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TestUtils {
    @Test
    public void testOSS() throws FileNotFoundException {
        InputStream fileInputStream = TestUtils.class.getResourceAsStream("20201202200304.png");
        CrowdUtil.uploadFileToOss(
                "http://oss-cn-beijing.aliyuncs.com",
                "LTAI4GF2S2S6KK6h9pyjhEtz",
                "C1DartUYaBDhmNH9ZBLJdAYkU914Zp",
                fileInputStream,
                "crowdfunding-dzc",
                "http://crowdfunding-dzc.oss-cn-beijing.aliyuncs.com",
                "20201202200304.png"
        );
    }
}
