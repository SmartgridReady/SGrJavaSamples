# SGr-JavaSamples

SGr-JavaSamples provides sample projects that demonstrate the use of the SGr Communication Handler Library. The goal is to set up a test environment that allows to connect SGr components and different 'products' (heat pump, charging station, inverter, battery, electricity meter etc.) through the SGr communication interface. 

![SGr Architecture Overview](SGr-Architecture-Overview.png "SGr Architecture Overview")

## Components

### Component: Communicator
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>Communicator Provider (3rd Party)</td></tr>
    <tr><td><b>Description:</b></td><td>The 'Communicator' communicates with one or more 'Products' through the SGr 'Generic Interface'. 
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>For each product, the communicator instantiates a Communication Handler to which a description of the product interface in XML is given.</p>
                <p>The Communicator loads a device driver for the communication interface of the product (e.g. Modbus RTU/TCP, REST...).</p>
                <p>The communicator reads or sets (analyses and/or controls) the data points.</p>
                <p></p> </td></tr>        
    <tr><td><b>SGrProject:</b></td>
    <td><a href="https://github.com/SmartgridReady/SGrJavaSamples/tree/master/SampleCommunicator">SGrJavaSamples/SampleCommunicator<a></td>
    </r>
</table>

<br><br>

### Component: Generic Interface
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>SGr Core Team</td></tr>
    <tr><td><b>Description:</b></td><td>SGr-defined 'Product'-independent interface. 
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>The Generic Interface is used by the Communicator to communicate with the products in the SGr network.​</p>
                <p></p> </td></tr>        
    <tr><td><b>SGrProject:</b></td>
    <td><a href="https://github.com/SmartgridReady/SGrSpecifications/tree/master/SchemaDatabase/SGr/Generic">SmartgridReady/SGrSpecifications/SchemaDatabase/SGr/Generic<a></td>
    </r>
</table>  

<br><br>

### Component: Communication Handler
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>SGr Core Team</td></tr>
    <tr><td><b>Description:</b></td><td>This is the core component of the SGr software and it is responsible for the processing and implementation of the SGr 'Generic Interface' on the 'External Interface' of the 'Product'.<br>
    Instantiated by the 'Communicator' and used to communicate with the connected 'Product'.
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>​Responsibilities are:<br>
- Reading the XML device profiles <br>
- Processing commands of the Generic Interface <br>
- Execute the commands on the External Interface of the product (device-specific interface)<br>
- Sending the commands to the Product through the transport service specified by the Product.
</p>
                <p></p> </td></tr>
    <tr><td><b>Library:</b></td><td>commhandler4modbus.jar</td></tr>                                                                                          
    <tr><td><b>SGrProject:</b></td>
    <td><a href="https://github.com/SmartgridReady/SGrJava/tree/master/InterfaceFactory/CommHandler4Modbus">SmartgridReady/SGrJava/InterfaceFactory/CommHandler4Modbus<a></td>
    </r>
</table> 

<br><br>

### Component: XML (XML-Profile)
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>Provider of the 'Product'</td></tr>
    <tr><td><b>Description:</b></td><td>​The XML file describes the ''function profiles', data points and attributes that can be addressed over the SGr interface. The XML file also provides general information about the 'Product'.
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>Providing general data on the Product.</p>
                <p>Provide the data necessary for mapping the SGr Generic Interface with the External Interface.</p> </td></tr>        
    <tr><td><b>SGrProject:</b></td>
    <td><a href="https://github.com/SmartgridReady/SGrSpecifications/tree/master/XMLInstances/ExtInterfaces">SmartgridReady/SGrSpecifications/XMLInstances/ExtInterfaces<a></td>
    </r>
</table> 

<br><br>

### Component: Transport Layer (Transport Service)
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>- SGr Core Team<br>- 3rd Party Provider</td></tr>
    <tr><td><b>Description:</b></td><td>The 'TransportService' is the link to the physical communication interface of the 'Product'. The SGr Core Team provides the EasyModbus Library for Modbus.
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>​The SGr Transport Service supports the following communication technologies to provide the following transport services:<br>
                - Modbus​, REST/JSON​, Sunspec​<br>
                - Support is planned for:​ OCPP 2.0​, IEC-61968-9​, IEC-608070-5-104</p>
                <p></p> </td></tr> 
    <tr><td><b>Library:</b></td>
    <td>easymodbus.jar</td>                            
    <tr><td><b>SGrProject:</b></td>    
    <td>für Modbus:<br><a href="https://github.com/SmartgridReady/SGrJavaDrivers/tree/master/EasyModbus">SmartgridReady/SGrJavaDrivers/EasyModbus<a></td>
    </r>
</table> 

<br><br>

### Component: External Interface (EI)
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>Manufacturer of the 'Product'</td></tr>
    <tr><td><b>Description:</b></td><td>The 'External Interface' is the interface provided by the 'Product'.
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>Provides the External Interface of the product. This is described in the Product XML Profile.</p></td></tr>                
</table> 

<br><br>

### Komponente: Product
<table valign="top">
    <tr><td><b>Implementor:</b></td><td>Manufacturer of the 'Product'</td></tr>
    <tr><td><b>Description:</b></td><td>The Product is a device that provides properties, data points and control options.
E.g. heat pump, charging station, inverter, battery, electricity meter 
    <tr><td valign="top"><b>Responsibilities:</b></td><td>
                <p>
</p></td></tr>
</table>

<br><br>

## How to use SGrJavaSamples


### Requirements / Prerequisits
- Gradle version >= 7.3.3. Note: If no IDE with Gradle integration is used, Gradle must first be installed locally: https://gradle.org/install/
- Java JDK version >= Java 1.8

### Clone
- Clone this repo to your local device: https://github.com/SmartgridReady/SGrJavaSamples.git

### Build
- Run gradle 'build' target in your IDE or use the command line:<br>
```bash>gradle clean build```

<br><br>

### Code description for the SampleCommunicator

Step 1:
Use the DeviceDescriptionLoader class to load the device description from an XML file.
<br><br>
```DeviceDescriptionLoader<SGrModbusDeviceDescriptionType> loader = newDeviceDescriptionLoader<>();```<br>
```SGrModbusDeviceDescriptionType sgcpMeter = loader.load( XML_BASE_DIR,"betaModbusABBMeterV0.1.2.xml");```
<br><br>

Step2:
Load the suitable device driver to communicate with the device. The example below uses a mocked driver for modbus RTU.
Change the driver to the real driver, suitable for your device. For example:
<br><br>
```GenDriverAPI4Modbus mbTCP = new GenDriverAPI4ModbusTCP();```<br>
```GenDriverAPI4Modbus mbRTU = new GenDriverAPI4ModbusRTU();```<br>
```GenDriverAPI4Modbus mbRTUMock = new GenDriverAPI4ModbusRTUMock();```
<br><br>

Step 2a (Modbus RTU only):
Initialie the serial COM port used by the modbus transport service.
```mbRTUMock.initTrspService("COM9");```
<br><br>

Step 3:
Instantiate a modbus device. Provide the device description and the device driver instance to be used for the device.<br><br>
```SGrModbusDevice abbMeterNo1 = new SGrModbusDevice(sgcpMeter, mbRTUMock );```<br> ```try {```
<br><br>

Step 4 (Modbus RTU only): Set the unit identifier of the device to read out. <br>
```mbRTUMock.setUnitIdentifier((byte) 11);```
<br><br>

Step 5: Read the values from the device. 
- "ActiveEnerBalanceAC" is the name of the functional profile.
- "ActiveImportAC", "ActiveExportAC" and "ActiveNetAC" are the names of the Datapoints that report the values corresponding to their names.

<i>Hint: You can only read values for functional profiles and datapoints that exist in the device description XML.</i><br>
```String acImport = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveImportAC");```<br>
```String acExport = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveExportAC");```<br>
```String acNet = abbMeterNo1.getVal("ActiveEnerBalanceAC", "ActiveNetAC");```
<br><br>

The complete sample code can be found on github:<br>
https://github.com/SmartgridReady/SGrJavaSamples/blob/documentation/SampleCommunicator/src/main/java/ch/smartgridready/communicator/example/SampleCommunicator.java
<br><br>

## Further information / contact information

Glossary: *ToDo Link*

Webssite: https://smartgridready.ch/ <br>
E-Mail: info@smartgridready.ch
