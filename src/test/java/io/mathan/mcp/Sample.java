/*
 * mathan-mcp23017-i2c
 * Copyright (c) 2020 Matthias Hanisch
 * matthias@mathan.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mathan.mcp;

import io.mathan.mcp.MCP23017.Pin;
import java.util.Arrays;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;
import net.sf.yad2xx.mpsse.I2C;

public class Sample {
  public static void main(String[] args) throws FTDIException, InterruptedException {
    System.loadLibrary("FTDIInterface");
    // Get all available FTDI Devices
    Device[] devices = FTDIInterface.getDevices();
    Device device = devices[0];
    I2C d = new I2C(device);
    d.open();
    MCP23017 m = new MCP23017(d, (byte) 0x20);
    MCP23017 m2 = new MCP23017(d, (byte) 0x21);
    m.setupOutput(Pin.GPIO_B_0);
    m.setupOutput(Pin.GPIO_B_2);
    m.setupOutput(Pin.GPIO_B_1);
    m.setupOutput(Pin.GPIO_B_3);
    m.setupOutput(Pin.GPIO_B_4);
    m.setupOutput(Pin.GPIO_B_5);
    m2.setupInput(Pin.GPIO_A_6);
    m2.setupInput(Pin.GPIO_A_7);
    for(int i=0;i<100;i++) {
      System.out.println("1="+m2.isSet(Pin.GPIO_A_6));
      System.out.println("2="+m2.isSet(Pin.GPIO_A_7));
      m.on(Pin.GPIO_B_0);
      m.on(Pin.GPIO_B_1);
      m.on(Pin.GPIO_B_2);
      m.on(Pin.GPIO_B_3);
      m.on(Pin.GPIO_B_4);
      m.on(Pin.GPIO_B_5);
      m.update();
      d.delay(50);
      m.off(Pin.GPIO_B_0);
      m.off(Pin.GPIO_B_1);
      m.off(Pin.GPIO_B_2);
      m.off(Pin.GPIO_B_3);
      m.off(Pin.GPIO_B_4);
      m.off(Pin.GPIO_B_5);
      m.update();
      d.delay(50);
    }

//    d.transactWrite((byte)0x20, (byte) 0x01, (byte)0xFE);
//    d.delay(1000);
//    d.transactWrite((byte)0x20, (byte) 0x13, (byte)0x01);
//    d.delay(2000);
//    d.transactWrite((byte)0x20, (byte) 0x13, (byte)0x00);
//    d.delay(2000);


    d.close();
  }
}
