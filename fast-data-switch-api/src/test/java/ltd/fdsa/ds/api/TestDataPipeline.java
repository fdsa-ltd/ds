package ltd.fdsa.ds.api;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.props.DefaultConfig;
import ltd.fdsa.ds.api.container.PluginManager;
import ltd.fdsa.ds.api.job.context.JobContext;
import ltd.fdsa.ds.api.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @ClassName:
 * @description:
 * @since 2020-10-28
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {String.class})
@Slf4j
public class TestDataPipeline {
    @Autowired
    private Environment env;

    /***
     * 查询所有
     */
    @Test
    public void TestSimplePipeline() {

        // step 1 初始化引擎
        PluginManager pluginManager = PluginManager.getDefaultInstance();
        // 加载插件
        pluginManager.scan("./target");
        // step 3 得到作业所有任务
        var url = this.getClass().getClassLoader().getResource("simple_job.json");
        var json = FileUtils.readFile(url.getFile());
        var config = DefaultConfig.getJsonConfig(json);
        JobContext context = new JobContext(new Metric(), config);
        context.start();
    }
}
