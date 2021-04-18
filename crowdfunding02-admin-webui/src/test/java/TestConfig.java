import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.entity.Role;
import com.zcdeng.crowd.mapper.AdminMapper;
import com.zcdeng.crowd.mapper.RoleMapper;
import com.zcdeng.crowd.service.api.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-persist-mybatis.xml", "classpath:spring-persist-tx.xml"})
public class TestConfig {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RoleMapper roleMapper;


    @Test
    public void testRoleSave() {

        for (int i = 0; i < 235; i++) {
            roleMapper.insert(new Role(null, "role" + i));
        }
    }

    @Test
    public void testDataSource() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    @Autowired
    private AdminMapper mapper;

    @Test
    public void testMapper() {
        System.out.println(mapper.selectByPrimaryKey(1));
    }

    @Autowired
    private AdminService adminService;

    @Test
    public void testTx() {
        adminService.saveAdmin(new Admin(null, "123", "456", "789", "0", "2019-10-01"));

    }

    @Test
    public void testSaveAdminMulti() {
        for (int i = 0; i < 352; i++) {
            adminMapper.insert(new Admin(null, "loginAcct" + i, "userPswd" + i, "userName" + i,
                    "email" + i + "@qq.com", null));
        }
    }


}
