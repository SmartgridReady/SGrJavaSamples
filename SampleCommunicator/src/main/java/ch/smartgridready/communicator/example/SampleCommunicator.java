package ch.smartgridready.communicator.example;

import com.smartgridready.ns.v0.SGrModbusDeviceDescriptionType;
import communicator.common.runtime.GenDriverAPI4Modbus;
import communicator.helper.DeviceDescriptionLoader;
import communicator.helper.GenDriverAPI4ModbusRTUMock;
import communicator.impl.SGrModbusDevice;
import de.re.easymodbus.adapter.GenDriverAPI4ModbusTCP;

public class SampleCommunicator {
	
	private static final String XML_BASE_DIR = "../../SGrSpecifications/XMLInstances/ExtInterfaces/"; 
	
	public static void main( String argv[] ) {				
		
		try {	
			
			DeviceDescriptionLoader<SGrModbusDeviceDescriptionType> loader = new DeviceDescriptionLoader<>();
			SGrModbusDeviceDescriptionType sgcpMeter = loader.load( XML_BASE_DIR, "SGr_04_0016_xxxx_ABBMeterV0.2.1.xml");
			
			GenDriverAPI4Modbus mbRTU = new GenDriverAPI4ModbusRTUMock();
			GenDriverAPI4Modbus mbTCP = new GenDriverAPI4ModbusTCP();
			
			SGrModbusDevice sgcpDevice = new SGrModbusDevice(sgcpMeter, mbRTU );
						
			try {
				
				mbRTU.setUnitIdentifier((byte) 11);
				String Val1 = sgcpDevice.getVal("CurrentAC", "CurrentACL1");
				String Val2 = sgcpDevice.getVal("CurrentAC", "CurrentACL2");
				String Val3 = sgcpDevice.getVal("CurrentAC", "CurrentACL3");
				String Val4 = sgcpDevice.getVal("CurrentAC", "CurrentACN");
				System.out.printf("ABBMeter ActiveEnerBalanceAC [KWh]:  " + Val1 + ",  " + Val2 + ",  " + Val3 + ", " + Val4 + " %n");

			}
			catch ( Exception e)
			{
				System.out.println( "Error reading value from device. " + e);
				e.printStackTrace();
			}
		}
		catch ( Exception e )
		{
			System.out.println( "Error loading device description. " + e);
		}									
	}
		
}