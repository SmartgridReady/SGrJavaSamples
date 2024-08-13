package com.smartgridready.communicator.example.helper;

import com.smartgridready.ns.v0.ModbusInterfaceDescription;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.modbus.GenDriverAPI4Modbus;
import com.smartgridready.driver.api.modbus.GenDriverModbusException;
import com.smartgridready.driver.api.modbus.GenDriverSocketException;
import com.smartgridready.driver.api.modbus.GenDriverAPI4ModbusConnectable;
import com.smartgridready.communicator.modbus.api.ModbusGatewayFactory;
import com.smartgridready.communicator.modbus.api.ModbusGateway;
import com.smartgridready.communicator.modbus.helper.GenDriverAPI4ModbusRTUMock;
import com.smartgridready.communicator.modbus.helper.ModbusUtil;

// copied from SGrJava due to inacessible classes
public class MockModbusGatewayFactory implements ModbusGatewayFactory {

    @Override
    public ModbusGateway create(ModbusInterfaceDescription interfaceDescription) throws GenDriverException {
        String identifier = ModbusUtil.getModbusGatewayIdentifier(interfaceDescription);
        return new ModbusGateway(identifier, interfaceDescription, new GenDriverAPI4ModbusLegacyWrapper(new GenDriverAPI4ModbusRTUMock()));
    }
    
    private static class GenDriverAPI4ModbusLegacyWrapper implements GenDriverAPI4ModbusConnectable {

        private boolean isConnected;
        private GenDriverAPI4Modbus transport;

        public GenDriverAPI4ModbusLegacyWrapper(GenDriverAPI4Modbus transport) {
            isConnected = false;
            this.transport = transport;
        }

        @Override
        public boolean connect() throws GenDriverException {
            // assume transport was initialized externally
            isConnected = true;
            return isConnected;
        }

        @Override
        public void disconnect() throws GenDriverException {
            transport.disconnect();
            isConnected = false;
        }

        @Override
        public boolean isConnected() {
            return isConnected;
        }

        @Override
        public boolean[] ReadCoils(int arg0, int arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            return transport.ReadCoils(arg0, arg1);
        }

        @Override
        public boolean[] ReadDiscreteInputs(int arg0, int arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            return transport.ReadDiscreteInputs(arg0, arg1);
        }

        @Override
        public int[] ReadHoldingRegisters(int arg0, int arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            return transport.ReadHoldingRegisters(arg0, arg1);
        }

        @Override
        public int[] ReadInputRegisters(int arg0, int arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            return transport.ReadInputRegisters(arg0, arg1);
        }

        @Override
        public void WriteMultipleCoils(int arg0, boolean[] arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            transport.WriteMultipleCoils(arg0, arg1);
        }

        @Override
        public void WriteMultipleRegisters(int arg0, int[] arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            transport.WriteMultipleRegisters(arg0, arg1);
        }

        @Override
        public void WriteSingleCoil(int arg0, boolean arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            transport.WriteSingleCoil(arg0, arg1);
        }

        @Override
        public void WriteSingleRegister(int arg0, int arg1)
                throws GenDriverException, GenDriverSocketException, GenDriverModbusException {
            transport.WriteSingleRegister(arg0, arg1);
        }
    }
}
