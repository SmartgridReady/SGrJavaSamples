/**
*Copyright(c) 2022-2024 Verein SmartGridready Switzerland
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

author: IBT/cb, FHNW/mkr
*/

package com.smartgridready.communicator.example;

import com.smartgridready.ns.v0.DeviceFrame;
import com.smartgridready.driver.api.modbus.DataBits;
import com.smartgridready.driver.api.modbus.Parity;
import com.smartgridready.driver.api.modbus.StopBits;
import com.smartgridready.communicator.common.helper.DeviceDescriptionLoader;
import com.smartgridready.communicator.modbus.impl.SGrModbusDevice;

import de.re.easymodbus.adapter.GenDriverAPI4ModbusRTU;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class provides an example on how to communicate with a WAGO smart meter
 * over Modbus RTU (RS-485), using the current SmartGridready commhandler library.
 * <br>
 * The device is instantiated the old fashioned way, without using the device builder.
 * <br>
 * The program requires an actual serial port and an attached device.
 */
public class WagoSmartMeterCommunicator {

	private static final Logger LOG = LoggerFactory.getLogger(WagoSmartMeterCommunicator.class);

	private static final String XML_BASE_DIR = "../../SGrSpecifications/XMLInstances/ExtInterfaces/";

	private static final String PROFILE_VOLTAGE_AC = "VoltageAC";
	private static final String PROFILE_CURRENT_AC = "CurrentAC";

	private static final String PROFILE_POWER_FACTOR = "PowerFactor";
	private static final String PROFILE_ACTIVE_ENERGY_AC = "ActiveEnergyAC";
	private static final String PROFILE_ACTIVE_POWER_AC = "ActivePowerAC";
	private static final String PROFILE_REACTIVE_POWER_AC = "ReactivePowerAC";
	private static final String PROFILE_APPARENT_POWER_AC = "ApparentPowerAC";
	private static final String PROFILE_ACTIVE_ENERGY_BALANCE_AC = "ActiveEnergyBalanceAC";
	private static final String PROFILE_REACTIVE_ENERGY_BALANCE_AC = "ReactiveEnergyBalanceAC";
	private static final String PROFILE_POWER_QUADRANT = "PowerQuadrant";
	private static final String PROFILE_CURRENT_DIRECTION = "CurrentDirection";

	private static final String DEVICE_DESCRIPTION_FILE_NAME = "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1.xml";
	private static final String SERIAL_PORT_NAME = "COM3";
	
	public static void main(String[] argv) {

		try {
			// configuration placeholders to be replaced in EID
			Properties configProperties = new Properties();
			configProperties.setProperty("port_name", SERIAL_PORT_NAME);

			// load device description from EID
			DeviceDescriptionLoader loader = new DeviceDescriptionLoader();
			DeviceFrame tstMeter = loader.load(XML_BASE_DIR, DEVICE_DESCRIPTION_FILE_NAME, configProperties);

			// initialize transport
			GenDriverAPI4ModbusRTU mbRTU = new GenDriverAPI4ModbusRTU();
			mbRTU.initTrspService(SERIAL_PORT_NAME, 9600, Parity.EVEN, DataBits.EIGHT, StopBits.ONE);
			
			// create device instance
			SGrModbusDevice devWagoMeter = new SGrModbusDevice(tstMeter, mbRTU);

			// run device tests
			testDevice(mbRTU, devWagoMeter);

			// close transport
			mbRTU.disconnect();
		} catch (Exception e) {
			LOG.error("Error loading device description.", e);
		}							
	}

	private static void testDevice(GenDriverAPI4ModbusRTU mbRTU, SGrModbusDevice devWagoMeter) {
		float fVal3;
		String sVal3;
		String sVal4;
		float fVal1;
		float fVal4;
		String sVal1;
		float fVal2;
		String sVal2;

		try {
			LOG.info("\nTesting WAGO Meter");

			fVal1 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageL1").getFloat32();
			fVal2 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageL2").getFloat32();
			fVal3 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageL3").getFloat32();
			fVal4 = devWagoMeter.getVal("Frequency", "Frequency").getFloat32();
			LOG.info("  VoltageAC L1,2,3/Frequency [V,Hz]: {},  {},  {},  {}", fVal1, fVal2, fVal3, fVal4);

			fVal1 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageACL1-L2").getFloat32();
			fVal2 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageACL1-L3").getFloat32();
			fVal3 = devWagoMeter.getVal(PROFILE_VOLTAGE_AC, "VoltageACL2-L3").getFloat32();
			LOG.info("  VoltageAC L12/13/23 [V]:           {},  {},  {}", fVal1, fVal2, fVal3);

			fVal1 = devWagoMeter.getVal(PROFILE_CURRENT_AC, "CurrentACL1").getFloat32();
			fVal2 = devWagoMeter.getVal(PROFILE_CURRENT_AC, "CurrentACL2").getFloat32();
			fVal3 = devWagoMeter.getVal(PROFILE_CURRENT_AC, "CurrentACL3").getFloat32();
			LOG.info("  CurrentAC L1/2/3 [V]:              {},  {},  {}", fVal1, fVal2, fVal3);

			fVal1 = devWagoMeter.getVal(PROFILE_POWER_FACTOR, PROFILE_POWER_FACTOR).getFloat32();
			fVal2 = devWagoMeter.getVal(PROFILE_POWER_FACTOR, "PowerFactorL1").getFloat32();
			fVal3 = devWagoMeter.getVal(PROFILE_POWER_FACTOR, "PowerFactorL2").getFloat32();
			fVal4 = devWagoMeter.getVal(PROFILE_POWER_FACTOR, "PowerFactorL3").getFloat32();
			LOG.info("  Powerfactor tot/L1/L2/L3:          {},  {},  {},  {}", fVal1, fVal2, fVal3, fVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_AC, "ActiveEnergyACtot").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_AC, "ActiveEnergyACL1").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_AC, "ActiveEnergyACL2").getString();
			sVal4 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_AC, "ActiveEnergyACL3").getString();
			LOG.info("  ActiveEnergyAC [kWh]:         {},  {},  {},  {}", sVal1, sVal2, sVal3, sVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_ACTIVE_POWER_AC, "ActivePowerACtot").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_ACTIVE_POWER_AC, "ActivePowerACL1").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_ACTIVE_POWER_AC, "ActivePowerACL2").getString();
			sVal4 = devWagoMeter.getVal(PROFILE_ACTIVE_POWER_AC, "ActivePowerACL3").getString();
			LOG.info("  ActivePowerAC [kW]:           {}, {},  {},  {}", sVal1, sVal2, sVal3, sVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_REACTIVE_POWER_AC, "ReactivePowerACtot").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_REACTIVE_POWER_AC, "ReactivePowerACL1").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_REACTIVE_POWER_AC, "ReactivePowerACL2").getString();
			sVal4 = devWagoMeter.getVal(PROFILE_REACTIVE_POWER_AC, "ReactivePowerACL3").getString();
			LOG.info("  ReactivePowerAC [kvar]:       {}, {},  {},  {}", sVal1, sVal2, sVal3, sVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_APPARENT_POWER_AC, "ApparentPowerACtot").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_APPARENT_POWER_AC, "ApparentPowerACL1").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_APPARENT_POWER_AC, "ApparentPowerACL2").getString();
			sVal4 = devWagoMeter.getVal(PROFILE_APPARENT_POWER_AC, "ApparentPowerACL3").getString();
			LOG.info("  ApparentPowerAC [kva]:        {}, {},  {},  {}", sVal1, sVal2, sVal3, sVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_BALANCE_AC, "ActiveImportAC").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_BALANCE_AC, "ActiveExportAC").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_ACTIVE_ENERGY_BALANCE_AC, "ActiveNetAC").getString();
			LOG.info("  ActiveEnergyBalanceAC [KWh]:    {}, {},  {}", sVal1, sVal2, sVal3);

			sVal1 = devWagoMeter.getVal(PROFILE_REACTIVE_ENERGY_BALANCE_AC, "ReactiveImportAC").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_REACTIVE_ENERGY_BALANCE_AC, "ReactiveExportAC").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_REACTIVE_ENERGY_BALANCE_AC, "ReactiveNetAC").getString();
			LOG.info("  ReactiveEnergyBalanceAC [kvarh]:{}, {},  {}", sVal1, sVal2, sVal3);

			sVal1 = devWagoMeter.getVal(PROFILE_POWER_QUADRANT, "PwrQuadACtot").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_POWER_QUADRANT, "PwrQuadACL1").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_POWER_QUADRANT, "PwrQuadACL2").getString();
			sVal4 = devWagoMeter.getVal(PROFILE_POWER_QUADRANT, "PwrQuadACL3").getString();
			LOG.info("  PowerQuadrant  tot/L1/L3/L3 :       {}, {}, {}, {}", sVal1, sVal2, sVal3, sVal4);

			sVal1 = devWagoMeter.getVal(PROFILE_CURRENT_DIRECTION, "CurrentDirL1").getString();
			sVal2 = devWagoMeter.getVal(PROFILE_CURRENT_DIRECTION, "CurrentDirL2").getString();
			sVal3 = devWagoMeter.getVal(PROFILE_CURRENT_DIRECTION, "CurrentDirL3").getString();
			LOG.info("  CurrentDirection  L1/L3/L3 :        {}, {},  {}", sVal1, sVal2, sVal3);
		}
		catch ( RuntimeException e)
		{
			LOG.info("Thread interrupted");
			Thread.currentThread().interrupt();
		}
		catch ( Exception e)
		{
			LOG.error( "Error reading value from device.", e);
		}
	}
}
