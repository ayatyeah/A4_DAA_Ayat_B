package org.yeah.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yeah.model.Graph;
import java.io.File;
import java.io.IOException;

public class JSONReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Graph readGraph(String filePath) throws IOException {
        return mapper.readValue(new File(filePath), Graph.class);
    }
}