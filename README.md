<a href="https://opensource.org/licenses/Apache-2.0"><img src="https://img.shields.io/badge/license-apache2-blue.svg"></a>

# yad2xx extension for [MCP23017] using I2C

This is Java API extension to [yad2xx]. 16 GPIO pins can be controlled as input or output. 

```
// configure an I2C instance
I2C device;
// create MCP23017 instance
MCP23017 mcp = new MCP23017(device);
// register some pins as output
mcp.setupOutput(Pin.GPIO_B_0);
mcp.setupOutput(Pin.GPIO_B_2);
mcp.setupOutput(Pin.GPIO_B_1);
mcp.setupOutput(Pin.GPIO_B_3);
mcp.setupOutput(Pin.GPIO_B_4);
mcp.setupOutput(Pin.GPIO_B_5);
// register some pins as input
mcp.setupInput(Pin.GPIO_A_6);
mcp.setupInput(Pin.GPIO_A_7);
// set an output pin to ON
mcp.on(Pin.GPIO_B_0);
// check if input on pin
if (mcp.isSet(Pin.GPIO_A_6)) {
  ...
}
```
## yad2xx linking

This project is using the yad2xx library released under LGPL3. The dynamic linking is done using Maven dependency mechanism. To change the linking to
a different version of the library just change the dependency. A copy of the library binary and license is available in the lib folder. A source artifact
has not been provided by the publisher of the library. You can find the source code on the SourceForge project page of [yad2xx].


## Chip details

The datasheet of [MCP23017] can be found in the doc folder.

[MCP23017]: https://www.microchip.com/wwwproducts/en/MCP23017
[yad2xx]: https://sourceforge.net/projects/yad2xx
[holtek]: https://holtek.com
