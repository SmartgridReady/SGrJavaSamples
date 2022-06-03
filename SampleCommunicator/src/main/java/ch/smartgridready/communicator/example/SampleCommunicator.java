package ch.smartgridready.communicator.example;


import ch.smartgridready.sgr.ns.v0.SGrModbusDeviceDescriptionType;
import static ch.smartgridready.sgr.ns.v0.SubProfileTypeEnumType.ACTIVE_ENER_BALANCE_AC;
import communicator.helper.DeviceDescriptionLoader;
import communicator.helper.GenDriverAPI4Modbus;
import communicator.helper.GenDriverAPI4ModbusRTUMock;
import communicator.helper.GenDriverAPI4ModbusTCP;
import communicator.impl.SGrModbusDevice;

public class SampleCommunicator {
	
	private static final String XML_BASE_DIR = "../../SGrSpecification/XMLInstances/ExtInterfaces/"; 
	
	public static void main( String argv[] ) {				
		
		try {	
			
			DeviceDescriptionLoader<SGrModbusDeviceDescriptionType> loader = new DeviceDescriptionLoader<>();
			SGrModbusDeviceDescriptionType sgcpMeter = loader.load( XML_BASE_DIR, "betaModbusABBMeterV0.1.2.xml");
			
			GenDriverAPI4Modbus mbRTU = new GenDriverAPI4ModbusRTUMock();
			GenDriverAPI4Modbus mbTCP = new GenDriverAPI4ModbusTCP();	
			
			SGrModbusDevice sgcpDevice = new SGrModbusDevice(sgcpMeter, mbRTU );
						
			try {
				
				mbRTU.setUnitIdentifier((byte) 11);
				String Val1 = sgcpDevice.getVal(ACTIVE_ENER_BALANCE_AC.getName(), "ActiveImportAC");
				String Val2 = sgcpDevice.getVal("ActiveEnerBalanceAC", "ActiveExportAC");
				String Val3 = sgcpDevice.getVal("ActiveEnerBalanceAC", "ActiveNetAC");
				System.out.printf("ABBMeter ActiveEnerBalanceAC [KWh]:  " + Val1 + ",  " + Val2 + ",  " + Val3 + " %n");

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