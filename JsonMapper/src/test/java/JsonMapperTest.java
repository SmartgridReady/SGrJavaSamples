import ch.smartgridready.JsonMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonMapperTest
{

    private static  final  Map<String, String> KEYWORD_MAP_TARIFF_IN1 = new HashMap<>();
    static {
        KEYWORD_MAP_TARIFF_IN1.put("startTime",  "[*].startTime");
        KEYWORD_MAP_TARIFF_IN1.put("endTime",    "[*].endTime");
        KEYWORD_MAP_TARIFF_IN1.put("tariffName", "[*].tariffs[*].name");
        KEYWORD_MAP_TARIFF_IN1.put("tariff",     "[*].tariffs[*].tariff");
    }

    private static final Map<String, String> KEYWORD_MAP_TARIFF_IN2 = new HashMap<>();
    static {
        KEYWORD_MAP_TARIFF_IN2.put("tariffName", "[*].tariffName");
        KEYWORD_MAP_TARIFF_IN2.put("startTime",  "[*].periods[*].startTime");
        KEYWORD_MAP_TARIFF_IN2.put("endTime",    "[*].periods[*].endTime");
        KEYWORD_MAP_TARIFF_IN2.put("tariff",     "[*].periods[*].tariff");
    }

    private static final Map<String, String> KEYWORD_MAP_TARIFF_IN3 = new HashMap<>();
    static {
        KEYWORD_MAP_TARIFF_IN3.put("startTime",  "[*].startTime");
        KEYWORD_MAP_TARIFF_IN3.put("endTime",    "[*].endTime");
        KEYWORD_MAP_TARIFF_IN3.put("tariffName", "[*].tariffName");
        KEYWORD_MAP_TARIFF_IN3.put("tariff",     "[*].tariff");
    }

    private static final String[] EXPECTED_TARIFF_RECORDS_IN1 = {
        "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=15.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=12.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"moonlight\"}",
        "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=17.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=14.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"moonlight\"}",
        "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=16.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=13.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"moonlight\"}"
    };

    private static final String[] EXPECTED_TARIFF_RECORDS_IN2 = {
            "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=15.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"sunlight\"}",
            "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=17.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"sunlight\"}",
            "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=16.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"sunlight\"}",
            "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=12.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"moonlight\"}",
            "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=14.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"moonlight\"}",
            "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=13.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"moonlight\"}"
    };

    private static final String[] EXPECTED_TARIFF_RECORDS_IN3 = {
            "{tariffName=\"sunlight\", startTime=\"2023-11-17T00:00:00+01:00\", tariff=15.0, endTime=\"2023-11-17T00:15:00+01:00\"}",
            "{tariffName=\"moonlight\", startTime=\"2023-11-17T00:00:00+01:00\", tariff=12.0, endTime=\"2023-11-17T00:15:00+01:00\"}",
            "{tariffName=\"sunlight\", startTime=\"2023-11-17T00:15:00+01:00\", tariff=17.0, endTime=\"2023-11-17T00:30:00+01:00\"}",
            "{tariffName=\"moonlight\", startTime=\"2023-11-17T00:15:00+01:00\", tariff=14.0, endTime=\"2023-11-17T00:30:00+01:00\"}",
            "{tariffName=\"sunlight\", startTime=\"2023-11-17T00:15:00+01:00\", tariff=16.0, endTime=\"2023-11-17T00:30:00+01:00\"}",
            "{tariffName=\"moonlight\", startTime=\"2023-11-17T00:30:00+01:00\", tariff=13.0, endTime=\"2023-11-17T00:45:00+01:00\"}"
    };

    @Test
    public void convertToFlatList_TariffIn1() throws Exception {

        String receivedJson = loadJson("TariffIn1.json");
        Map<JsonMapper.Key, Map<String, String>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN1);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN1[i], tarifRecord[i].toString());
        }
    }

    @Test
    public void convertToFlatList_TariffIn2() throws Exception {

        String receivedJson = loadJson("TariffIn2.json");
        Map<JsonMapper.Key, Map<String, String>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN2);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN2[i], tarifRecord[i].toString());
        }
    }

    @Test
    public void convertToFlatList_TariffIn3() throws Exception {

        String receivedJson = loadJson("TariffIn3.json");
        Map<JsonMapper.Key, Map<String, String>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN3);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN3[i], tarifRecord[i].toString());
        }
    }



    private static String loadJson(String resourceName) throws Exception {

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        return writer.toString();
    }

}
