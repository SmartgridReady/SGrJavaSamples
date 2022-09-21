/**
*Copyright(c) 2022 Verein SmartGridready Switzerland
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

This Module references automatically generated code, generated from SmartGridready Modbus XML Schema definitions
check for "EI-Modbus" and "Generic" directories in our Namespace http://www.smartgridready.ch/ns/SGr/V0/

author: IBT/cb
*/

package ch.smartgridready.communicator.example;

import com.smartgridready.ns.v0.SGrModbusDeviceDescriptionType;

import communicator.helper.DeviceDescriptionLoader;
import communicator.impl.SGrModbusDevice;
import de.re.easymodbus.adapter.GenDriverAPI4ModbusRTU;


public class SampCommunicatorModbusRTU {


	private static final String XML_BASE_DIR = "../../SGrSpecifications/XMLInstances/ExtInterfaces/"; 
	
	public static void main( String argv[] ) {				
		
		float fVal1 = (float) 0.0, fVal2 = (float) 0.0, fVal3 = (float) 0.0, fVal4 = (float) 0.0;
		String  sVal1 = "0.0", sVal2 = "0.0", sVal3 = "0.0", sVal4 ="0.0";
		
		try {	
			
			DeviceDescriptionLoader<SGrModbusDeviceDescriptionType> loader = new DeviceDescriptionLoader<>();
			SGrModbusDeviceDescriptionType tstMeter = loader.load( XML_BASE_DIR, "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml");
			
			GenDriverAPI4ModbusRTU mbRTU = new GenDriverAPI4ModbusRTU();
			mbRTU.initTrspService("COM9");	
			
			SGrModbusDevice devWagoMeter = new SGrModbusDevice(tstMeter, mbRTU );
				
			
			try {	
			// set device address of devWagoMeter
				
			mbRTU.setUnitIdentifier((byte) 7);
				
		    System.out.println();
			System.out.println("Testing WAGO Meter");
			Thread.sleep(25);
			fVal1 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageL1").getFloat32(); 
			Thread.sleep(10);            
			fVal2 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageL2").getFloat32();
			Thread.sleep(10);
			fVal3 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageL3").getFloat32();
			Thread.sleep(10);
			fVal4 = devWagoMeter.getValByGDPType("Frequency", "Frequency").getFloat32();
			System.out.printf("  VoltageAC L1,2,3/Frequency [V,Hz]: " + fVal1 + ",  " + fVal2 + ",  "
					+ fVal3 + ",  " + fVal4 + " %n");
			Thread.sleep(10);
			fVal1 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageACL1-L2").getFloat32();
			Thread.sleep(10);
			fVal2 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageACL1-L3").getFloat32();
			Thread.sleep(10);
			fVal3 = devWagoMeter.getValByGDPType("VoltageAC", "VoltageACL2-L3").getFloat32();
			System.out.printf("  VoltageAC L12/13/23 [V]:           " + fVal1 + ",  " + fVal2 + ",  "
					+ fVal3 + " %n");
			Thread.sleep(10);
			fVal1 = devWagoMeter.getValByGDPType("CurrentAC", "CurrentACL1").getFloat32();
			Thread.sleep(10);
			fVal2 = devWagoMeter.getValByGDPType("CurrentAC", "CurrentACL2").getFloat32();
			Thread.sleep(10);
			fVal3 = devWagoMeter.getValByGDPType("CurrentAC", "CurrentACL3").getFloat32();
			System.out.printf("  CurrentAC L1/2/3 [V]:              " + fVal1 + ",  " + fVal2 + ",  "
					+ fVal3 + " %n");
			Thread.sleep(10);
			fVal1 = devWagoMeter.getValByGDPType("PowerFactor", "PowerFactor").getFloat32();
			Thread.sleep(10);
			fVal2 = devWagoMeter.getValByGDPType("PowerFactor", "PowerFactorL1").getFloat32();
			Thread.sleep(10);
			fVal3 = devWagoMeter.getValByGDPType("PowerFactor", "PowerFactorL2").getFloat32();
			Thread.sleep(10);
			fVal4 = devWagoMeter.getValByGDPType("PowerFactor", "PowerFactorL3").getFloat32();
			System.out.printf("  Powerfactor tot/L1/L2/L3:          " + fVal1 + ",  " + fVal2 + ",  "
					+ fVal3 + ",  " + fVal4 + " %n");
			Thread.sleep(10); 
			sVal1 = devWagoMeter.getVal("ActiveEnergyAC", "ActiveEnergyACtot");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ActiveEnergyAC", "ActiveEnergyACL1");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ActiveEnergyAC", "ActiveEnergyACL2");
			Thread.sleep(10);
			sVal4 = devWagoMeter.getVal("ActiveEnergyAC", "ActiveEnergyACL3");
			System.out.printf("  ActiveEnergyAC [kWh]:         " + sVal1 + ",  " + sVal2 + ",  " + sVal3
					+ ",  " + sVal4 + " %n");
			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("ActivePowerAC", "ActivePowerACtot");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ActivePowerAC", "ActivePowerACL1");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ActivePowerAC", "ActivePowerACL2");
			Thread.sleep(10);
			sVal4 = devWagoMeter.getVal("ActivePowerAC", "ActivePowerACL3");
			System.out.printf("  ActivePowerAC [kW]:           " + sVal1 + ", " + sVal2 + ",  " + sVal3
					+ ",  " + sVal4 + " %n");
			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("ReactivePowerAC", "ReactivePowerACtot");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ReactivePowerAC", "ReactivePowerACL1");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ReactivePowerAC", "ReactivePowerACL2");
			Thread.sleep(10);
			sVal4 = devWagoMeter.getVal("ReactivePowerAC", "ReactivePowerACL3");
			System.out.printf("  ReactivePowerAC [kvar]:       " + sVal1 + ", " + sVal2 + ",  " + sVal3
					+ ",  " + sVal4 + " %n");
			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("ApparentPowerAC", "ApparentPowerACtot");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ApparentPowerAC", "ApparentPowerACL1");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ApparentPowerAC", "ApparentPowerACL2");
			Thread.sleep(10);
			sVal4 = devWagoMeter.getVal("ApparentPowerAC", "ApparentPowerACL3");
			System.out.printf("  ApparentPowerAC [kva]:        " + sVal1 + ", " + sVal2 + ",  " + sVal3
					+ ",  " + sVal4 + " %n");
			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("ActiveEnerBalanceAC", "ActiveImportAC");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ActiveEnerBalanceAC", "ActiveExportAC");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ActiveEnerBalanceAC", "ActiveNetAC");
			System.out.printf("  ActiveEnerBalanceAC [KWh]:    " + sVal1 + ", " + sVal2 + ",  " + sVal3 + " %n");

			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("ReactiveEnerBalanceAC", "ReactiveImportAC");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("ReactiveEnerBalanceAC", "ReactiveExportAC");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("ReactiveEnerBalanceAC", "ReactiveNetAC");
			System.out.printf("  ReactiveEnerBalanceAC [kvarh]:" + sVal1 + ", " + sVal2 + ",  " + sVal3  + " %n");
			
			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("PowerQuadrant", "PwrQuadACtot");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("PowerQuadrant", "PwrQuadACL1");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("PowerQuadrant", "PwrQuadACL2");
			Thread.sleep(10);
			sVal4 = devWagoMeter.getVal("PowerQuadrant", "PwrQuadACL3");
			System.out.printf("  PowerQuadrant  tot/L1/L3/L3 :       " + sVal1 + ", " + sVal2 + ", " + sVal3
					+ ",  " + sVal4 + " %n");

			Thread.sleep(10);
			sVal1 = devWagoMeter.getVal("CurrentDirection", "CurrentDirL1");
			Thread.sleep(10);
			sVal2 = devWagoMeter.getVal("CurrentDirection", "CurrentDirL2");
			Thread.sleep(10);
			sVal3 = devWagoMeter.getVal("CurrentDirection", "CurrentDirL3");
			System.out.printf("  CurrentDirection  L1/L3/L3 :        " + sVal1 + ", " + sVal2 + ",  " + sVal3
					+  " %n");
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
