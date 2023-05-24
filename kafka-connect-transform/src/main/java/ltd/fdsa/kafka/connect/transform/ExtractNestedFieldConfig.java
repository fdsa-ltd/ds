/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.zhumingwu.kafka.connect.transform;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class ExtractNestedFieldConfig extends AbstractConfig {
  public final String outerFieldName;
  public final String innerFieldName;
  public final String outputFieldName;

  public ExtractNestedFieldConfig(Map<String, ?> settings) {
    super(config(), settings);
    this.outerFieldName = getString(OUTER_FIELD_NAME_CONF);
    this.innerFieldName = getString(INNER_FIELD_NAME_CONF);
    this.outputFieldName = getString(OUTPUT_FIELD_NAME_CONF);
  }

  public static final String OUTER_FIELD_NAME_CONF = "input.outer.field.name";
  static final String OUTER_FIELD_NAME_DOC = "The field on the parent struct containing the child struct. " +
      "For example if you wanted the extract `address.state` you would use `address`.";
  public static final String INNER_FIELD_NAME_CONF = "input.inner.field.name";
  static final String INNER_FIELD_NAME_DOC = "The field on the child struct containing the field to be extracted. " +
      "For example if you wanted the extract `address.state` you would use `state`.";
  public static final String OUTPUT_FIELD_NAME_CONF = "output.field.name";
  static final String OUTPUT_FIELD_NAME_DOC = "The field to place the extracted value into.";

  public static ConfigDef config() {
    return new ConfigDef()
        .define(OUTER_FIELD_NAME_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, OUTER_FIELD_NAME_DOC)
        .define(INNER_FIELD_NAME_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, INNER_FIELD_NAME_DOC)
        .define(OUTPUT_FIELD_NAME_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, OUTPUT_FIELD_NAME_DOC);
  }

}
