package ch.smartgridready.communicator.example;

import communicator.async.process.ExecStatus;
import communicator.async.process.Parallel;
import communicator.async.process.Processor;
import communicator.async.process.ReadExec;
import communicator.async.process.Sequence;
import communicator.async.process.WriteExec;
import communicator.common.api.Float32Value;
import communicator.common.api.Float64Value;
import communicator.common.api.StringValue;
import communicator.common.api.Value;
import communicator.common.runtime.GenDriverModbusException;
import communicator.modbus.impl.SGrModbusDevice;
import communicator.rest.exception.RestApiAuthenticationException;
import communicator.rest.impl.SGrRestApiDevice;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(value = MockitoExtension.class)
public class AsynchronousSampleCommunicator {

    private static final int TIME_TOLERANCE_MS = 2;

    private static final String DEVICE_ERROR = "DEVICE ERROR";

    private static final Logger LOG = LoggerFactory.getLogger(AsynchronousSampleCommunicator.class);
    @Mock
    SGrModbusDevice wagoModbusDevice;
    @Mock
    SGrRestApiDevice clemapRestApiDevice;

    @Mock
    SGrRestApiDevice clemapRestApiDevice_2;

    @Mock
    SGrModbusDevice garoModbusDevice_A;
    @Mock
    SGrModbusDevice garoModbusDevice_B;

    private Value withDelay(long delay, Value value) {
        Awaitility.await().pollDelay(Duration.ofMillis(delay)).until(() -> true);
        LOG.debug("Delay {}ms is over.", delay);
        return value;
    }

    private void withDelay(long delay) {
        Awaitility.await().pollDelay(Duration.ofMillis(delay)).until(() -> true);
        LOG.debug("Delay {}ms is over.", delay);
    }

    @Test
    void
    buildAndRunDataStructure() throws Exception {
        initStubs();
        doBuildAndRunDatstructureTest(ExecStatus.SUCCESS, null);
    }

    @Test
    void buildAndRunDataStructureWithException() throws Exception {
        initStubsWithException();
        doBuildAndRunDatstructureTest(ExecStatus.ERROR, "DEVICE ERROR");
    }

    private void doBuildAndRunDatstructureTest(ExecStatus expectedStatus, String expectedExceptionMessage) {

        // Setup READ tasks
        ReadExec<Value> wago_voltageAC_l1 = new ReadExec<>("VoltageAC", "VoltageL1", wagoModbusDevice::getVal);
        ReadExec<Value> wago_voltageAC_l2 = new ReadExec<>("VoltageAC", "VoltageL2", wagoModbusDevice::getVal);
        ReadExec<Value> wago_voltageAC_l3 = new ReadExec<>("VoltageAC", "VoltageL3", wagoModbusDevice::getVal);
        ReadExec<Value> clemap_actPowerAC_tot = new ReadExec<>("ActivePowerAC", "ActivePowerACtot", clemapRestApiDevice::getVal);
        ReadExec<Value> clemap_actPowerAC_tot_2 = new ReadExec<>("ActivePowerAC", "ActivePowerACtot", clemapRestApiDevice_2::getVal);

        // Setup WRITE tasks
        WriteExec<Value> garo_wallbox_A_hems_curr_lim = new WriteExec<>("Curtailment", "HemsCurrentLimit", garoModbusDevice_A::setVal);
        WriteExec<Value> garo_wallbox_B_hems_curr_lim = new WriteExec<>("Curtailment", "HemsCurrentLimit", garoModbusDevice_B::setVal);

        // Wire tasks
        Processor readChain = new Parallel()        // 2000
                .add(new Sequence()                // 1500
                        .add(wago_voltageAC_l1)
                        .add(wago_voltageAC_l2)
                        .add(wago_voltageAC_l3))
                .add(new Parallel()                    // 2000
                        .add(clemap_actPowerAC_tot)     // 750
                        .add(clemap_actPowerAC_tot_2))  // 2000
                .await(wago_voltageAC_l1,
                        wago_voltageAC_l2,
                        wago_voltageAC_l3,
                        clemap_actPowerAC_tot,
                        clemap_actPowerAC_tot_2);

        Processor writeChain = new Parallel()
                .add(garo_wallbox_A_hems_curr_lim)
                .add(garo_wallbox_B_hems_curr_lim)
                .await(garo_wallbox_A_hems_curr_lim,
                        garo_wallbox_B_hems_curr_lim);

        // Run readChain
        readChain.process();

        // Get results from read-chain.
        // Example: wago_voltageAC_l1.getReadValue();
        // Do some calculations and determine new control values:
        garo_wallbox_A_hems_curr_lim.setWriteValue(Float32Value.of(10f));
        garo_wallbox_B_hems_curr_lim.setWriteValue(Float32Value.of(5f));
        // Process the write-chain:
        writeChain.process();


        LOG.info(wago_voltageAC_l1.toString());
        LOG.info(wago_voltageAC_l2.toString());
        LOG.info(wago_voltageAC_l3.toString());
        LOG.info(clemap_actPowerAC_tot.toString());
        LOG.info(clemap_actPowerAC_tot_2.toString());
        LOG.info(garo_wallbox_A_hems_curr_lim.toString());
        LOG.info(garo_wallbox_B_hems_curr_lim.toString());

        wago_voltageAC_l1.cleanup();
        wago_voltageAC_l2.cleanup();
        wago_voltageAC_l3.cleanup();
        clemap_actPowerAC_tot.cleanup();
        garo_wallbox_A_hems_curr_lim.cleanup();
        garo_wallbox_B_hems_curr_lim.cleanup();
    }


    private void initStubs() throws Exception {
        when(wagoModbusDevice.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> withDelay(500, Float32Value.of(220f)));

        when(clemapRestApiDevice.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> withDelay(750, Float32Value.of(20f)));

        when(clemapRestApiDevice_2.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> withDelay(2000, Float64Value.of(50d)));

        doAnswer((Answer<Void>) invocation -> {
            withDelay(500);
            return null;
        }).when(garoModbusDevice_A).setVal(any(), any(), any());

        doAnswer((Answer<Void>) invocation -> {
            withDelay(250);
            return null;
        }).when(garoModbusDevice_B).setVal(any(), any(), any());
    }

    private void initStubsWithException() throws Exception {

        when(wagoModbusDevice.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> {
                    withDelay(500, StringValue.of("220"));
                    throw new GenDriverModbusException(DEVICE_ERROR);
                });

        when(clemapRestApiDevice.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> {
                    withDelay(750, StringValue.of("20.0"));
                    throw new RestApiAuthenticationException(DEVICE_ERROR);
                });

        when(clemapRestApiDevice_2.getVal(any(), any())).thenAnswer(
                (Answer<Value>) invocation -> {
                    withDelay(2000, StringValue.of("50.0"));
                    throw new RestApiAuthenticationException(DEVICE_ERROR);
                });

        doAnswer((Answer<Void>) invocation -> {
            withDelay(1000);
            throw new GenDriverModbusException(DEVICE_ERROR);
        }).when(garoModbusDevice_A).setVal(any(), any(), any());

        doAnswer((Answer<Void>) invocation -> {
            withDelay(1000);
            throw new GenDriverModbusException(DEVICE_ERROR);
        }).when(garoModbusDevice_B).setVal(any(), any(), any());
    }
}
