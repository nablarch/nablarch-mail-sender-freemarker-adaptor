package nablarch.common.mail.freemarker;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.common.mail.TemplateEngineProcessingException;

/**
 * {@link FreeMarkerMailProcessor}のテストクラス。
 */
public class FreeMarkerMailProcessorTest {

    private final FreeMarkerMailProcessor sut = new FreeMarkerMailProcessor();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * テンプレートエンジンの処理の確認。
     */
    @Test
    public void testProcess() {
        String templateId = "hello";
        String lang = null;
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("foo", templateId);
        variables.put("bar", 123);

        TemplateEngineProcessedResult result = sut.process(templateId, lang, variables);

        assertThat(result.getSubject(), is("件名テスト：hello"));
        assertThat(result.getMailBody(), is("本文テスト１：hello\r\n本文テスト２：123\r\n"));
    }

    /**
     * 言語を指定したテンプレートエンジンの処理の確認。
     */
    @Test
    public void testProcessWithLang() {
        String templateId = "hello";
        String lang = "en";
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("foo", "hello");
        variables.put("bar", 123);

        TemplateEngineProcessedResult result = sut.process(templateId, lang, variables);

        assertThat(result.getSubject(), is("subject test: hello"));
        assertThat(result.getMailBody(), is("body test 1: hello\r\nbody test 2: 123\r\n"));
    }

    /**
     * 発生した{@link IOException}を{@link TemplateEngineProcessingException}でラップすること。
     */
    @Test
    public void testProcess_io_exception() {
        expectedException.expect(TemplateEngineProcessingException.class);
        expectedException.expectCause(isA(IOException.class));

        //テンプレートが見つからない場合、IOExceptionのサブクラスである
        //TemplateNotFoundExceptionが投げられる。
        sut.process("not_found", null, Collections.<String, Object> emptyMap());
    }

    /**
     * 発生した{@link TemplateException}を{@link TemplateEngineProcessingException}でラップすること。
     */
    @Test
    public void testProcess_template_exception() {
        expectedException.expect(TemplateEngineProcessingException.class);
        expectedException.expectCause(isA(TemplateException.class));

        //テンプレートで参照されている変数を渡していない場合、
        //TemplateExceptionが投げられる。
        sut.process("hello", null, Collections.<String, Object> emptyMap());
    }

    /**
     * デリミタを変更した場合の確認。
     */
    @Test
    public void testProcess_alter_delimiter() {
        String templateId = "alter-delimiter";
        String lang = null;
        Map<String, Object> variables = Collections.emptyMap();

        sut.setDelimiter("@@@");

        TemplateEngineProcessedResult result = sut.process(templateId, lang, variables);

        assertThat(result.getSubject(), is("---"));
        assertThat(result.getMailBody(), is("Alter delimiter test."));
    }

    @Before
    public void setUp() {
        Configuration configuration = new Configuration(Configuration.getVersion());
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate("hello",
                "件名テスト：${foo}\r\n---\r\n本文テスト１：${foo}\r\n本文テスト２：${bar}\r\n");
        templateLoader.putTemplate("hello_en",
                "subject test: ${foo}\r\n---\r\nbody test 1: ${foo}\r\nbody test 2: ${bar}\r\n");
        templateLoader.putTemplate("alter-delimiter",
                "---\r\n@@@\r\nAlter delimiter test.");
        configuration.setTemplateLoader(templateLoader);
        sut.setConfiguration(configuration);
    }
}
