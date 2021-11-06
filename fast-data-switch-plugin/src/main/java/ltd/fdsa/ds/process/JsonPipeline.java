package ltd.fdsa.ds.process;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.config.Configuration;
import ltd.fdsa.ds.api.container.Plugin;
import ltd.fdsa.ds.api.model.Column;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.model.Result;
import ltd.fdsa.ds.api.pipeline.Process;
import ltd.fdsa.ds.api.pipeline.impl.AbstractPipeline;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@ToString
@Plugin(name = "表达式", description = "通过表达式增加数据")
public class ExpressionPipeline extends AbstractPipeline implements Process {
    private static final Map<String, Expression> cache = new HashMap<String, Expression>();
    private static final String EXPRESSION_KEY = "expression";
    private static final String NAME_KEY = "name";
    private static final String NAME_DEFAULT = "new_field";

    private Expression expression;
    private String field;

    static {
        // 自定义函数
        // AviatorEvaluator.addFunction(new stringSplit());
    }

    @Override
    public Result<String> init(Configuration configuration) {
        var result = super.init(configuration);
        if (result.getCode() == 200) {
            String expression = this.config.getString(EXPRESSION_KEY);
            if (!cache.containsKey(expression)) {
                cache.put(expression, AviatorEvaluator.compile(expression));
            }
            this.expression = cache.get(expression);
            this.field = this.config.getString(NAME_KEY, NAME_DEFAULT);
            return Result.success();
        }
        return result;

    }

    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        for (var record : records) {
            Map<String, Object> env = new HashMap<String, Object>(128);
            for (var column : record.entrySet()) {
                env.put(column.getKey(), column.getValue());
            }
            var result = this.expression.execute(env);
            for (var item : this.nextSteps) {
                record.Add(new Column(this.field, result));
                item.collect(record);
            }
        }
    }


    class stringSplit extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            String input = FunctionUtils.getStringValue(arg1, env);
            String value = FunctionUtils.getStringValue(arg2, env);
            String[] list = input.split(value);
            return new AviatorRuntimeJavaType(list);
        }

        @Override
        public String getName() {
            return "string.split";
        }
    }
}