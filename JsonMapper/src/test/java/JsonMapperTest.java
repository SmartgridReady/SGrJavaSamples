import ch.smartgridready.JsonBuilder;
import ch.smartgridready.JsonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonMapperTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    private static final Map<String, String> KEYWORD_MAP_TARIFF_SWISSPOWER = new HashMap<>();
    static {
        KEYWORD_MAP_TARIFF_SWISSPOWER.put("startTime",  "prices[*].start_timestamp");
        KEYWORD_MAP_TARIFF_SWISSPOWER.put("endTime",    "prices[*].end_timestamp");
        KEYWORD_MAP_TARIFF_SWISSPOWER.put("tariff",     "prices[*].price");
        KEYWORD_MAP_TARIFF_SWISSPOWER.put("unit",       "prices[*].unit");
    }

    private static final Map<String, String> KEYWORD_MAP_TARIFF_E_GROUP = new HashMap<>();
    static {
        KEYWORD_MAP_TARIFF_E_GROUP.put("startTime", "[*].start_timestamp");
        KEYWORD_MAP_TARIFF_E_GROUP.put("startTime", "[*].end_timestamp");
        KEYWORD_MAP_TARIFF_E_GROUP.put("tariff/vario_grid", "[*].vario_grid");
        KEYWORD_MAP_TARIFF_E_GROUP.put("tariff/vario_plus", "[*].vario_plus");
        KEYWORD_MAP_TARIFF_E_GROUP.put("tariff/dt_plus",    "[*].dt_plus");
        KEYWORD_MAP_TARIFF_E_GROUP.put("unit",              "[*].unit");
    }





    private static  final LinkedHashMap<String, String> KEYWORD_MAP_TARIFF_OUT1 = new LinkedHashMap<>();
    static {
        KEYWORD_MAP_TARIFF_OUT1.put("startTime",  "[*].start_time");
        KEYWORD_MAP_TARIFF_OUT1.put("endTime",    "[*].end_time");
        KEYWORD_MAP_TARIFF_OUT1.put("tariffName", "[*].tariffs[*].name");
        KEYWORD_MAP_TARIFF_OUT1.put("tariff",     "[*].tariffs[*].tariff");
        KEYWORD_MAP_TARIFF_OUT1.put("unit",       "[*].tariffs[*].unit");
    }

    private static final String[] EXPECTED_TARIFF_RECORDS_IN1 = {
        "{startTime=2023-11-17T00:00:00+01:00, tariff=15.0, endTime=2023-11-17T00:15:00+01:00, tariffName=sunlight}",
        "{startTime=2023-11-17T00:00:00+01:00, tariff=12.0, endTime=2023-11-17T00:15:00+01:00, tariffName=moonlight}",
        "{startTime=2023-11-17T00:15:00+01:00, tariff=17.0, endTime=2023-11-17T00:30:00+01:00, tariffName=sunlight}",
        "{startTime=2023-11-17T00:15:00+01:00, tariff=14.0, endTime=2023-11-17T00:30:00+01:00, tariffName=moonlight}",
        "{startTime=2023-11-17T00:30:00+01:00, tariff=16.0, endTime=2023-11-17T00:45:00+01:00, tariffName=sunlight}",
        "{startTime=2023-11-17T00:30:00+01:00, tariff=13.0, endTime=2023-11-17T00:45:00+01:00, tariffName=moonlight}"
    };

    private static final String[] EXPECTED_TARIFF_RECORDS_IN2 = {
            "{startTime=2023-11-17T00:00:00+01:00, tariff=15.0, endTime=2023-11-17T00:15:00+01:00, tariffName=sunlight}",
            "{startTime=2023-11-17T00:15:00+01:00, tariff=17.0, endTime=2023-11-17T00:30:00+01:00, tariffName=sunlight}",
            "{startTime=2023-11-17T00:30:00+01:00, tariff=16.0, endTime=2023-11-17T00:45:00+01:00, tariffName=sunlight}",
            "{startTime=2023-11-17T00:00:00+01:00, tariff=12.0, endTime=2023-11-17T00:15:00+01:00, tariffName=moonlight}",
            "{startTime=2023-11-17T00:15:00+01:00, tariff=14.0, endTime=2023-11-17T00:30:00+01:00, tariffName=moonlight}",
            "{startTime=2023-11-17T00:30:00+01:00, tariff=13.0, endTime=2023-11-17T00:45:00+01:00, tariffName=moonlight}"
    };

    private static final String[] EXPECTED_TARIFF_RECORDS_IN3 = {
            "{tariffName=sunlight, startTime=2023-11-17T00:00:00+01:00, tariff=15.0, endTime=2023-11-17T00:15:00+01:00}",
            "{tariffName=moonlight, startTime=2023-11-17T00:00:00+01:00, tariff=12.0, endTime=2023-11-17T00:15:00+01:00}",
            "{tariffName=sunlight, startTime=2023-11-17T00:15:00+01:00, tariff=17.0, endTime=2023-11-17T00:30:00+01:00}",
            "{tariffName=moonlight, startTime=2023-11-17T00:15:00+01:00, tariff=14.0, endTime=2023-11-17T00:30:00+01:00}",
            "{tariffName=sunlight, startTime=2023-11-17T00:15:00+01:00, tariff=16.0, endTime=2023-11-17T00:30:00+01:00}",
            "{tariffName=moonlight, startTime=2023-11-17T00:30:00+01:00, tariff=13.0, endTime=2023-11-17T00:45:00+01:00}"
    };

    private static final String[] EXPECTED_TARIFF_RECORDS_SWISSPOWER = {
            "{unit=Rp./kWh, startTime=2024-02-14T00:00:00.000+02:00, tariff=6.841903, endTime=2024-02-14T00:15:00.000+02:00}",
            "{unit=Rp./kWh, startTime=2024-02-14T00:15:00.000+02:00, tariff=6.020112, endTime=2024-02-14T00:30:00.000+02:00}",
            "{unit=Rp./kWh, startTime=2024-02-14T00:30:00.000+02:00, tariff=6.655699, endTime=2024-02-14T00:45:00.000+02:00}",
            "{unit=Rp./kWh, startTime=2024-02-14T00:45:00.000+02:00, tariff=6.671192, endTime=2024-02-14T01:00:00.000+02:00}"
    };

    @Test
    void convertToFlatList_TariffIn1() throws Exception {

        String receivedJson = loadJson("TariffIn1.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN1);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN1[i], tarifRecord[i].toString());
        }
    }

    @Test
    void convertToFlatList_TariffIn2() throws Exception {

        String receivedJson = loadJson("TariffIn2.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN2);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN2[i], tarifRecord[i].toString());
        }
    }

    @Test
    void convertToFlatList_TariffIn3() throws Exception {

        String receivedJson = loadJson("TariffIn3.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_IN3);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_IN3[i], tarifRecord[i].toString());
        }
    }

    @Test
    void convertToFlatList_TariffInSwisspower() throws Exception {

        String receivedJson = loadJson("TariffInSwisspower.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(receivedJson, KEYWORD_MAP_TARIFF_SWISSPOWER);

        assertEquals(4, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS_SWISSPOWER[i], tarifRecord[i].toString());
        }
    }

    @Test
    void jsonBuild() throws Exception {

        String expectedOutputJson = loadJson("TariffOut1.json");
        String inputJson = loadJson("TariffIn1.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(inputJson, KEYWORD_MAP_TARIFF_IN1);

        JsonBuilder builder = new JsonBuilder(KEYWORD_MAP_TARIFF_OUT1);
        String jsonResult = builder.buildJson(tariffRecords.values());

        assertEquals(MAPPER.readTree(expectedOutputJson), MAPPER.readTree(jsonResult));
    }

    @Test
    void jsonBuildSwisspower() throws Exception {
        String expectedOutputJson = loadJson("TariffOutSwisspower.json");
        String inputJson = loadJson("TariffInSwisspower.json");
        Map<JsonMapper.Key, Map<String, Object>> tariffRecords = JsonMapper.mapToFlatList(inputJson, KEYWORD_MAP_TARIFF_SWISSPOWER);

        JsonBuilder builder = new JsonBuilder(KEYWORD_MAP_TARIFF_OUT1);
        String jsonResult = builder.buildJson(tariffRecords.values());

        assertEquals(MAPPER.readTree(expectedOutputJson), MAPPER.readTree(jsonResult));
    }

    private static String loadJson(String resourceName) throws Exception {

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        return writer.toString();
    }

}
