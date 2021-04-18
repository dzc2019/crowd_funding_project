import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.mapper.AdminMapper;
import com.zcdeng.crowd.util.CrowdUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestUtil {


    @Test
    public void testMd5(){
        String m = CrowdUtil.md5("123456");
        System.out.println(m);
    }

    @Test
    public void getPassword(){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }


}
