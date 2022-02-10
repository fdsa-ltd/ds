package ltd.fdsa.job.admin.config;

import ltd.fdsa.job.admin.repository.*;
import ltd.fdsa.job.admin.scheduler.JobScheduler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Component
public class JobAdminConfig implements InitializingBean, DisposableBean {


    private JobScheduler JobScheduler;

    // ---------------------- JobScheduler ----------------------
    // conf
    @Value("${project.job.i18n}")
    private String i18n;
    @Value("${project.job.accessToken}")
    private String accessToken;
    @Value("${spring.mail.username}")
    private String emailUserName;

    @Override
    public void afterPropertiesSet() throws Exception {

        JobScheduler = new JobScheduler();
        JobScheduler.init();
    }

    @Override
    public void destroy() throws Exception {
        JobScheduler.destroy();
    }

    public String getI18n() {
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailUserName() {
        return emailUserName;
    }


}
