package cn.zhumingwu.kafka.connect.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.iot.cbor.*;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.kafka.connect.data.*;
import org.apache.kafka.connect.storage.Converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class CborConverter implements Converter {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }


    @Override
    public byte[] fromConnectData(String topic, Schema schema, Object value) {
        if (schema == null || value == null) {
            return null;
        }
        try {
            Struct struct = (Struct) value;
            if (struct == null) {
                return null;
            }
            CborMap map = CborMap.create();
            for (var field : schema.fields()) {
                log.info("field:{}", field);
              var object =  struct.get(field.name());

//              var sss =  CborByteString.create(null);
                map.put(field.name(), CborObject.createFromJavaObject(object));
            }
            log.info("map: {}", map.toJsonString());
            return map.toCborByteArray();
        } catch (CborConversionException ex) {
            log.error("JsonProcessingException:", ex);
            return new byte[0];
        }
    }

    @Override
    public SchemaAndValue toConnectData(String topic, byte[] value) {
        if (value == null) {
            log.info("value is null");
            return SchemaAndValue.NULL;
        }
        if (value.length <= 0) {
            log.info("value is empty");
            return SchemaAndValue.NULL;
        }
        try {
            var map = CborMap.createFromCborByteArray(value);
            var builder = SchemaBuilder.struct();
            for (var entry : map.entrySet()) {
                var name = entry.getKey().toJsonString();
                var object = entry.getValue().toJavaObject();
                if (object instanceof Boolean) {
                    builder.field(name, SchemaBuilder.bool().optional().build());
                } else if (object instanceof Byte) {
                    builder.field(name, SchemaBuilder.int8().optional().build());
                } else if (object instanceof Short) {
                    builder.field(name, SchemaBuilder.int16().optional().build());
                } else if (object instanceof Integer) {
                    builder.field(name, SchemaBuilder.int32().optional().build());
                } else if (object instanceof Long) {
                    builder.field(name, SchemaBuilder.int64().optional().build());
                } else if (object instanceof Float) {
                    builder.field(name, SchemaBuilder.float32().optional().build());
                } else if (object instanceof Double) {
                    builder.field(name, SchemaBuilder.float64().optional().build());
                } else if (object instanceof java.sql.Time) {
                    builder.field(name, Time.SCHEMA);
                } else if (object instanceof java.sql.Timestamp) {
                    builder.field(name, Timestamp.SCHEMA);
                } else if (object instanceof java.sql.Date) {
                    builder.field(name, Date.SCHEMA);
                } else if (object instanceof BigDecimal) {
                    builder.field(name, Decimal.schema(3));
                } else {
                    builder.field(name, SchemaBuilder.string().optional().build());
                }
            }
            var schema = builder.schema();
            Struct result = new Struct(schema.schema());
            for (var entry : map.entrySet()) {
                var name = entry.getKey().toJsonString();
                var object = entry.getValue().toJavaObject();
                result.put(name, object);
            }
            log.info("struct:{}", result);
            return new SchemaAndValue(schema, result);

        } catch (CborParseException e) {
            log.error("ConnectData failed:{}", e);
            return SchemaAndValue.NULL;
        }
    }

}