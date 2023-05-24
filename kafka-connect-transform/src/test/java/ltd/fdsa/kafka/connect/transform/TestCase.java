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
package cn.zhumingwu.kafka.connect.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class TestCase {
    Expression[] expressions;

    @Before
    public void before() {
        expressions = new Expression[]{
                AviatorEvaluator.compile("nil"),//null
                AviatorEvaluator.compile("1==1"),//Boolean
                AviatorEvaluator.compile("first_name + ' ' + last_name"),//String
                AviatorEvaluator.compile("age"),//直接返回 Integer
                AviatorEvaluator.compile("instant"),//直接返回 Instant
                AviatorEvaluator.compile("amount"),//instant Float
                AviatorEvaluator.compile("sysdate()"),//Date
                AviatorEvaluator.compile("3.14"), //Double
                AviatorEvaluator.compile("3.14M"), //BigDecimal
                AviatorEvaluator.compile("11111"),// Long
                AviatorEvaluator.compile("11111N"),// BigInteger
                AviatorEvaluator.compile("math.pow(2N,32)"),// BigInteger
                AviatorEvaluator.compile("math.pow(10M,64)"),// BigDecimal
                AviatorEvaluator.compile("9999999999999999999999999999999999999999999999999999999999999999 + 9999999999999999999999999999999999999999999999999999999999999999"),// Float

        };
    }

    @Test
    public void testSend() {
        Date d = new Date();

        Map<String, Object> env = new HashMap<String, Object>();
        env.put("first_name", "zhu");
        env.put("last_name", "mingwu");
        env.put("age", 18);

        env.put("salary", 10000L);
        env.put("amount", 1000.0F);
        env.put("instant", d.toInstant());

        try {
            for (var ep : this.expressions) {
                var value = ep.execute(env);
                if (value != null) {
                    if (value instanceof String) {
                        log.debug("class: {}, value: {}, string: {}", "String", value, value.toString());
                    } else if (value instanceof Integer) {
                        log.debug("class: {}, value: {}, string: {}", "Integer", value, value.toString());
                    } else if (value instanceof Float) {
                        log.debug("class: {}, value: {}, string: {}", "Float", value, value.toString());
                    } else if (value instanceof Long) {
                        log.debug("class: {}, value: {}, string: {}", "Long", value, value.toString());
                    } else if (value instanceof Double) {
                        log.debug("class: {}, value: {}, string: {}", "Double", value, value.toString());
                    } else if (value instanceof BigInteger) {
                        log.debug("class: {}, value: {}, string: {}", "BigInteger", value, value.toString());
                    } else if (value instanceof BigDecimal) {
                        log.debug("class: {}, value: {}, string: {}", "BigDecimal", value, value.toString());
                    } else if (value instanceof Boolean) {
                        log.debug("class: {}, value: {}, string: {}", "Boolean", value, value.toString());
                    } else if (value instanceof Date) {
                        log.debug("class: {}, value: {}, string: {}", "Date", value, value.toString());
                    } else {
                        log.debug("class: {}, value: {}, string: {}", value.getClass(), "ELSE", value.toString());
                    }
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
