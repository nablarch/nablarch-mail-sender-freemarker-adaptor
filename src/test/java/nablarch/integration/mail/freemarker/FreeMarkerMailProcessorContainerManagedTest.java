package nablarch.integration.mail.freemarker;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import freemarker.template.Configuration;
import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.ComponentFactory;
import nablarch.test.support.SystemRepositoryResource;

/**
 * {@link FreeMarkerMailProcessor}をコンポーネント設定ファイルで構築する場合のテストクラス。
 */
public class FreeMarkerMailProcessorContainerManagedTest {

    @Rule
    public SystemRepositoryResource systemRepositoryResource = new SystemRepositoryResource(
            "nablarch/integration/mail/freemarker/FreeMarkerMailProcessorContainerManagedTest.xml");

    /**
     * コンポーネント設定ファイルで構築するテスト。
     */
    @Test
    public void testProcessConfiguredByXml() {

        FreeMarkerMailProcessor sut = SystemRepository.get("templateEngineMailProcessor");

        //テンプレートエンジンの処理をして設定済みConfigurationを
        //使用できていることを確認する。
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("foo", "0");
        variables.put("bar", false);
        variables.put("bazs", Arrays.asList("1", "2", "3"));
        TemplateEngineProcessedResult result = sut.process("testProcessConfiguredByXml.ftl", null,
                Collections.unmodifiableMap(variables));

        assertThat(result.getSubject(), is("あああ0"));
        assertThat(result.getMailBody(), is("いいい\r\nえええ1\r\nえええ2\r\nえええ3\r\n"));
    }

    public static class ConfigurationFactory implements ComponentFactory<Configuration> {

        @Override
        public Configuration createObject() {
            Configuration cfg = new Configuration(Configuration.getVersion());
            ClassLoader classLoader = ConfigurationFactory.class.getClassLoader();
            cfg.setClassLoaderForTemplateLoading(classLoader, "nablarch/integration/mail/freemarker/");
            return cfg;
        }
    }
}
