package com.tony.zrpc.common.serialize.json;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.annotation.*;

import java.text.SimpleDateFormat;

public class JsonSerialization {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    public byte[] serialize(Object output) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(output);
        return bytes;
    }

    public Object deserialize(byte[] input, Class clazz) throws Exception {
        Object parse = objectMapper.readValue(input,clazz);
        return parse;
    }
}
