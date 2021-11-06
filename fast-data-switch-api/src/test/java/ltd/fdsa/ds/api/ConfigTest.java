package ltd.fdsa.ds.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.props.Configuration;
import ltd.fdsa.ds.api.props.DefaultConfig;
import ltd.fdsa.ds.api.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;


/**
 * @ClassName:
 * @description:
 * @since 2020-10-28
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {String.class})
@Slf4j
public class ConfigTest {
    private void doPrint(Configuration config) {
        var name = config.get("name");
        log.info("job.name:{}", name);
        var pipelines = config.getConfigurations("pipelines");
        for (var pipeline : pipelines) {
            log.info("pipeline.class:{}", pipeline.get("class"));
        }
        var pipeline = config.getConfiguration("pipelines.1");
        log.info("pipeline.class:{}", pipeline.get("class"));
    }

    @Test
    public void TestCreateConfig() throws JsonProcessingException {
        var list = new User[10];
        for (var i = 0; i < list.length; i++) {
            list[i] = User.builder()
                    .age(Math.random())
                    .name("test" + i)
                    .createTime(new Date())
                    .id(i).types(null).build();
        }
        var obj = User.builder().age(18D).createTime(new Date())
                .id(1).name("adam zhu")
                .types(Arrays.asList("男", "高", "富", "帅"))
                .friends(list).build();
        ObjectMapper objectMapper = new ObjectMapper();
        var content = objectMapper.writeValueAsString(obj);
        DefaultConfig config = (DefaultConfig) DefaultConfig.getYamlConfig(content);// YamlConfig(content);
        System.out.println("===========props:===========");
        System.out.println(config.toString());
        System.out.println("===========json:===========");
        System.out.println(config.toJson());
        System.out.println("===========yaml:===========");
        System.out.println(config.toYaml());
    }

    @Test
    public void TestYamlConfig() {
        //得到配置
        var url = this.getClass().getClassLoader().getResource("simple_job.yml");
        var content = FileUtils.readFile(url.getFile());
        var config = DefaultConfig.getYamlConfig(content);// YamlConfig(content);
        doPrint(config);
    }

    @Test
    public void TestJsonConfig() {
        //得到配置
        var url = this.getClass().getClassLoader().getResource("simple_job.json");
        var content = FileUtils.readFile(url.getFile());
        var config = DefaultConfig.getJsonConfig(content);
        doPrint(config);
    }

    @Test
    public void TestPropertyConfig() {
        //得到配置
        var url = this.getClass().getClassLoader().getResource("simple_job.properties");
        var content = FileUtils.readFile(url.getFile());
        var config = DefaultConfig.getPropsConfig(content);
        doPrint(config);
    }
}
