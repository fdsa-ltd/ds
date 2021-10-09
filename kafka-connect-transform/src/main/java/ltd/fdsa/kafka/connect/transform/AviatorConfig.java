/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ltd.fdsa.kafka.connect.transform;

import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AviatorConfig extends AbstractConfig {
    static {
        // 自定义函数
        AviatorEvaluator.addFunction(new string2date());
        AviatorEvaluator.addFunction(new date2string());
    }

    private static final Map<String, Expression> cache = new HashMap<String, Expression>();

    public final Expression expression;
    public final String field;
    static final String EXPRESSION_CONF = "expression";
    static final String EXPRESSION_DOC = "The aviator expression to generate new data record." +
            "For example if you wanted full from `firstname` and `lastname`, you would use `name = db.firstname + ' ' + db.lastname`.";
    public static final String FIELD_NAME_CONF = "field";
    static final String FIELD_NAME_DOC = "The field name of the new result from expression.";

    static class string2date extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            String input = FunctionUtils.getStringValue(arg1, env);
            String format = FunctionUtils.getStringValue(arg2, env);
            if (Strings.isNullOrEmpty(format)) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return new AviatorRuntimeJavaType(sdf.parse(input));
            } catch (ParseException e) {
                log.error("SimpleDateFormat.parse", e);
                return AviatorNil.NIL;
            }
        }

        @Override
        public String getName() {
            return "string.date";
        }
    }

    static class date2string extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Date date = (Date) FunctionUtils.getJavaObject(arg1, env);
            String format = FunctionUtils.getStringValue(arg2, env);
            if (Strings.isNullOrEmpty(format)) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return new AviatorString(sdf.format(date));
        }

        @Override
        public String getName() {
            return "date.string";
        }
    }

    public AviatorConfig(Map<String, ?> settings) {
        super(config(), settings);
        String expression = this.getString(EXPRESSION_CONF);
        if (!cache.containsKey(expression)) {
            // 编译表达式
            Expression ep = AviatorEvaluator.compile(expression);
            cache.put(expression, ep);
        }
        this.expression = cache.get(expression);


        var name = this.getString(FIELD_NAME_CONF);
        if (Strings.isNullOrEmpty(name)) {
            this.field = "db";
        } else {
            this.field = name;
        }
    }

    public static ConfigDef config() {
        return new ConfigDef()
                .define(FIELD_NAME_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, FIELD_NAME_DOC)
                .define(EXPRESSION_CONF, ConfigDef.Type.LIST, ConfigDef.Importance.HIGH, EXPRESSION_DOC);
    }

}
