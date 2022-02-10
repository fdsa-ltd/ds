package ltd.fdsa.job.admin.config;


import freemarker.template.TemplateModelException;
import ltd.fdsa.ds.core.util.I18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * web mvc config
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebMvcConfig implements WebFilter {
    @Autowired
    private freemarker.template.Configuration configuration;

    public static Mono<ServerWebExchange> getRequest() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(ServerWebExchange.class));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        try {
            configuration.setSharedVariable("i18n", I18nUtil.getInstance(""));
        } catch (TemplateModelException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange)
                .subscriberContext(ctx -> ctx.put(ServerWebExchange.class, exchange));

    }
}
