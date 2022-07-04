package ch.smartgridready.communicator.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.smartgridready.sgr.ns.v0.SGrModbusDeviceDescriptionType;
import communicator.helper.DeviceDescriptionLoader;
import communicator.helper.GenDriverAPI4Modbus;
import communicator.helper.GenDriverAPI4ModbusRTUMock;
import communicator.impl.SGrModbusDevice;


/** 
 * <p>
 * This is a sample implementation of communicator that uses the SmartGridready communication
 * handler. The communication handler uses the SmartGridready generic interface to communicate
 * with any attached device/product in a generic way. The communication handler converts the
 * generic API commands to commands understood by the external interface of the device/product.
 * <p> 
 * The command translation is done by the communication handler based on a device description
 * loaded from a device description XML file.
 * <p>
 * There are several communication protocols/technologies available to communicate with the device:
 * <ul>  
 * 		<li>Modbus RTU</li>
 * 		<li>Modbus TCP</li>
 * 		<li>http / REST Swagger</li>
 * </ul> 
 * The communicator is responsible instantiate/load the suitable driver for the attached 
 * devices/products.  
 * <p>
 * The sample shows the basic steps to set up the communicator to talk to a simple 
 * SmartGridready Modbus device and read a value from the device.
 * 
 **/
public class SampleCommunicator {
	
	private static final Logger LOG =  LoggerFactory.getLogger(SampleCommunicator.class);
	
	private static final String XML_BASE_DIR = "../../SGrSpecifications/XMLInstances/ExtInterfaces/";
	
	public static void main( String argv[] ) {				
		
		try {	
			 
			// Step 1: 
			// Use the DeviceDescriptionLoader class to Load the device description from an XML file.
			//
			DeviceDescriptionLoader<SGrModbusDeviceDescriptionType> loader = new DeviceDescriptionLoader<>();
			SGrModbusDeviceDescriptionType sgcpMeter = loader.load( XML_BASE_DIR, "betaModbusABBMeterV0.1.2.xml");
			
			// Step 2: 
			// Load the suitable device driver to communicate with the device. The example below uses
			// mocked driver for modbus RTU.
			//
			// Change the the driver to the real driver, suitable for your device. For example:
			// - GenDriverAPI4Modbus mbTCP = new GenDriverAPI4ModbusTCP();
			// - GenDriverAPI4Modbus mbRTU = new GenDriverAPI4ModbusRTU();
			//
			GenDriverAPI4Modbus mbRTUMock = new GenDriverAPI4ModbusRTUMock();
			
			// Step 2a (Modbus RTU only):
			// Initialise the serial COM port used by the modbus transport service.
			//
			mbRTUMock.initTrspService("COM9");
				
			// Step 3:
			// Instantiate a modbus device. Provide the device description and the device driver
			// instance to be used for the device.
			SGrModbusDevice abbMeterNo1 = new SGrModbusDevice(sgcpMeter, mbRTUMock );			
			try {				
				
				// Step 4 (Modbus RTU only):
				// Set the unit identifier of the device to read out. 
				mbRTUMock.setUnitIdentifier((byte) 11);
				
				// Step 5: 
				// Read the values from the device. 
				// - "ActiveEnerBalanceAC" is the name of the functional profile.
				// - "ActiveImportAC", ActiveExportAC and ActiveNetAC are the names of the Datapoints that
				//   report the values corresponding to their names.
				// 
				// Hint: You can only read values for functional profiles and datapoints that exist 
				// in the device description XML.
				//
				String acImport = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveImportAC");
				String acExport = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveExportAC");
				String acNet = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveNetAC");
				LOG.info("ABBMeter ActiveEnerBalanceAC [KWh]: acImport={} acExport={}  acNet={} \n", acImport, acExport, acNet);	
			}
			catch ( Exception e)
			{
				LOG.error( "Error reading value from device. ", e);
			}									 									
			
		}
		catch ( Exception e )
		{
			LOG.error( "Error loading device description. ", e);
		}									
	}		
}