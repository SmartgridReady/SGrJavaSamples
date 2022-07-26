/**
*Copyright(c) 2021 Verein SmartGridready Switzerland
* 
This Open Source Software is BSD 3 clause licensed:
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
   the documentation and/or other materials provided with the distribution.
3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from 
   this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
OF THE POSSIBILITY OF SUCH DAMAGE.

This Module includes automatically generated code, generated from SmartGridready Modus XML Schema definitions
check for "EI-Modbus" and "Generic" directories in our Namespace http://www.smartgridready.ch/ns/SGr/V0/

*/
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