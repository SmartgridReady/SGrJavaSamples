import ch.smartgridready.JsonMapper;
import com.smartgridready.ns.v0.JSonArrayOutputProduct;
import com.smartgridready.ns.v0.JSonElemProduct;
import com.smartgridready.ns.v0.JSonOutputProduct;
import com.smartgridready.ns.v0.RAEmptyValue;
import com.smartgridready.ns.v0.V0Factory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonMapperTest
{

    private static  final  Map<String, String> KEYWORD_MAP_TARIFF_OUT = new HashMap<>();
    {
        KEYWORD_MAP_TARIFF_OUT.put("startTime",  "[*].startTime");
        KEYWORD_MAP_TARIFF_OUT.put("endTime",    "[*].endTime");
        KEYWORD_MAP_TARIFF_OUT.put("tariffName", "[*].tariffs[*].name");
        KEYWORD_MAP_TARIFF_OUT.put("tariff",     "[*].tariffs[*].tariff");
    }

    private static final String[] EXPECTED_TARIFF_RECORDS = {
        "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=15.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:00:00+01:00\", tariff=12.0, endTime=\"2023-11-17T00:15:00+01:00\", tariffName=\"moonlight\"}",
        "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=17.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:15:00+01:00\", tariff=14.0, endTime=\"2023-11-17T00:30:00+01:00\", tariffName=\"moonlight\"}",
        "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=16.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"sunlight\"}",
        "{startTime=\"2023-11-17T00:30:00+01:00\", tariff=13.0, endTime=\"2023-11-17T00:45:00+01:00\", tariffName=\"moonlight\"}"
    };

    @Test
    public void convertFlatToFlatJson() throws Exception {

        String receivedJson = loadJson("TariffOut.json");

        JSonOutputProduct descriptor = createFlatJsonDescriptor();

        Map<Integer, Map<String, String>> tariffRecords = JsonMapper.mapToFlatJson(receivedJson, KEYWORD_MAP_TARIFF_OUT);

        assertEquals(6, tariffRecords.size());

        Object tarifRecord[] = tariffRecords.values().toArray();
        for (int i = 0; i < tarifRecord.length; i++) {
            assertEquals(EXPECTED_TARIFF_RECORDS[i], tarifRecord[i].toString());
        }
    }



    private JSonOutputProduct createFlatJsonDescriptor() {

        JSonOutputProduct jsonResult = V0Factory.eINSTANCE.createJSonOutputProduct();
        JSonArrayOutputProduct jsonArrayItem = V0Factory.eINSTANCE.createJSonArrayOutputProduct();

        jsonArrayItem.getElem().add(createDateElement(  "startTime", "$.startTime"));
        jsonArrayItem.getElem().add(createDateElement(  "endTime", "$.endTime"));
        jsonArrayItem.getElem().add(createStringElement("tariffName", "tariffName"));
        jsonArrayItem.getElem().add(createNumberElement("tariff", "$.tariff"));

        jsonResult.getArray().add(jsonArrayItem);

        return jsonResult;
    }


    private static JSonElemProduct createDateElement(String name, String xPath) {

        JSonElemProduct element = V0Factory.eINSTANCE.createJSonElemProduct();
        element.setDate(RAEmptyValue._);
        element.setKey(name);
        element.setQuery(xPath);

        return element;
    }

    private static JSonElemProduct createStringElement(String name, String xPath) {

        JSonElemProduct element = V0Factory.eINSTANCE.createJSonElemProduct();
        element.setString(RAEmptyValue._);
        element.setKey(name);
        element.setQuery(xPath);

        return element;
    }

    private static JSonElemProduct createNumberElement(String name, String xPath) {

        JSonElemProduct element = V0Factory.eINSTANCE.createJSonElemProduct();
        element.setNumber(RAEmptyValue._);
        element.setKey(name);
        element.setQuery(xPath);

        return element;
    }

    private static String loadJson(String resourceName) throws Exception {

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        return writer.toString();
    }

}
