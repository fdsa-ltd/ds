package ltd.fdsa.job.admin.config;


import ltd.fdsa.ds.core.util.I18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * web mvc config
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private freemarker.template.Configuration configuration;

    @PostConstruct
    public void setConfigure() throws Exception {
        configuration.setSharedVariable("i18n", I18nUtil.getInstance(""));
    }
}
