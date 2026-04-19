package ru.yandex.practicum.collector.util;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

public class AvroSerializer {
    public static <T> byte[] serialize(T data, Schema schema) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

            SpecificDatumWriter<T> writer = new SpecificDatumWriter<>(schema);
            writer.write(data, encoder);

            encoder.flush();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error serializing Avro", e);
        }
    }
}
