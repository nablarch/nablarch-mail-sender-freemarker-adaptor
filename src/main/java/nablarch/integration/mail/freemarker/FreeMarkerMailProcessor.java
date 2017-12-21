package nablarch.integration.mail.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nablarch.common.mail.TemplateEngineMailProcessor;
import nablarch.common.mail.TemplateEngineProcessedResult;
import nablarch.common.mail.TemplateEngineProcessingException;

/**
 * FreeMarkerを使用する{@link TemplateEngineMailProcessor}の実装クラス。
 * 
 * @author Taichi Uragami
 *
 */
public class FreeMarkerMailProcessor implements TemplateEngineMailProcessor {

    /** FreeMarkerの設定 */
    private Configuration configuration;

    /** 件名と本文を分けるデリミタ */
    private String delimiter;

    /**
     * テンプレートIDと言語から取得されたテンプレートと変数をマージして、その結果を返す。
     * 
     * <p>
     * テンプレートの検索は{@link Configuration#getTemplate(String, Locale)}が使われる。
     * テンプレートと変数のマージは{@link Template#process(Object, java.io.Writer)}が使われる。
     * </p>
     * 
     * @see Configuration#getTemplate(String, Locale)
     * @see Template#process(Object, java.io.Writer)
     */
    @Override
    public TemplateEngineProcessedResult process(String templateId, String lang,
            Map<String, Object> variables) {
        Locale locale = null;
        if (lang != null) {
            locale = new Locale(lang);
        }
        StringWriter out = new StringWriter();
        try {
            Template template = configuration.getTemplate(templateId, locale);
            template.process(variables, out);
        } catch (IOException e) {
            throw new TemplateEngineProcessingException(e);
        } catch (TemplateException e) {
            throw new TemplateEngineProcessingException(e);
        }
        if (delimiter != null) {
            return TemplateEngineProcessedResult.valueOf(out.toString(), delimiter);
        }
        return TemplateEngineProcessedResult.valueOf(out.toString());
    }

    /**
     * FreeMarkerのエントリーポイントとなる{@link Configuration}を設定する。
     * 
     * @param configuration FreeMarkerの設定
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 件名と本文を分けるデリミタを設定する。
     * 
     * <p>
     * なにも設定されていなければ{@link TemplateEngineProcessedResult#DEFAULT_DELIMITER デフォルトのデリミタ}が使用される。
     * </p>
     * 
     * @param delimiter 件名と本文を分けるデリミタ
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
