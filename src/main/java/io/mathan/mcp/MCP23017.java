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

import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.mpsse.I2C;

/**
 * Using MCP23017 you can control up to 16 GPIO output. (GPIO input will be added in the future). To enable
 * a GPIO as output pin call {@link #setupOutput(Pin)}. Afterwards you can use {@link #on(Pin)} and {@link #off(Pin)} to
 * control the pin. If you want to send the updates for the pins to the MCP23017 call {@link #update()}.
 */
public class MCP23017 {
  private static final byte ADDRESS_DEFAULT = 0x20;
  private static final byte CMD_INIT_GPIOA = 0x00;
  private static final byte CMD_INIT_GPIOB = 0x01;
  private static final byte CMD_DATA_GPIOA = 0x12;
  private static final byte CMD_DATA_GPIOB = 0x13;
  private static final byte CMD_PULL_UP_GPIOA = 0x0C;
  private static final byte CMD_PULL_UP_GPIOB = 0x0D;


  private byte bufferInitGpioA = (byte) 0xFF;
  private byte bufferInitGpioB = (byte) 0xFF;
  private byte bufferDataGpioA = (byte) 0x00;
  private byte bufferDataGpioB = (byte) 0x00;
  private byte bufferPullUpGpioA = (byte) 0x00;
  private byte bufferPullUpGpioB = (byte) 0x00;

  private final I2C device;
  private final byte address;

  public MCP23017(I2C device) {
    this(device, ADDRESS_DEFAULT);
  }

  public MCP23017(I2C device, byte address) {
    this.device = device;
    this.address = address;
  }

  public void setupOutput(Pin pin) throws FTDIException {
    if(pin.isA()) {
      bufferInitGpioA &=~pin.mask;
      bufferPullUpGpioA &=~pin.mask;
      this.device.transactWrite(address, CMD_INIT_GPIOA, bufferInitGpioA);
      this.device.transactWrite(address, CMD_PULL_UP_GPIOA, bufferPullUpGpioA);
    } else {
      bufferInitGpioB &=~pin.mask;
      bufferPullUpGpioB &=~pin.mask;
      this.device.transactWrite(address, CMD_INIT_GPIOB, bufferInitGpioB);
      this.device.transactWrite(address, CMD_PULL_UP_GPIOB, bufferPullUpGpioB);
    }
    this.device.delay(200);
  }

  public void setupInput(Pin pin) throws FTDIException {
    if(pin.isA()) {
      bufferInitGpioA |=pin.mask;
      bufferPullUpGpioA |=pin.mask;
      this.device.transactWrite(address, CMD_INIT_GPIOA, bufferInitGpioA);
      this.device.transactWrite(address, CMD_PULL_UP_GPIOA, bufferPullUpGpioA);
    } else {
      bufferInitGpioB |=pin.mask;
      bufferPullUpGpioB |=pin.mask;
      this.device.transactWrite(address, CMD_INIT_GPIOB, bufferInitGpioB);
      this.device.transactWrite(address, CMD_PULL_UP_GPIOB, bufferPullUpGpioB);
    }
    this.device.delay(200);
  }

  public void on(Pin pin) {
    //TODO check for output
    if(pin.isA()) {
      bufferDataGpioA |=pin.mask;
    } else {
      bufferDataGpioB |=pin.mask;
    }
  }

  public void off(Pin pin) {
    //TODO check for output
    if(pin.isA()) {
      bufferDataGpioA &=~pin.mask;
    } else {
      bufferDataGpioB &=~pin.mask;
    }
  }

  public boolean isSet(Pin pin) throws FTDIException {
    //TODO check for input
    if(pin.isA()) {
      Byte data = null;
      while((data=getByte(CMD_DATA_GPIOA))==null);
      return (data &pin.mask)==0;
    } else {
      Byte data = null;
      while ((data = getByte(CMD_DATA_GPIOB)) == null);
      return (data & pin.mask) == 0;
    }
  }

  private Byte getByte(byte cmdData) throws FTDIException {
    this.device.start();
    try {
      if(!this.device.writeAddress(this.address, false)) {
        return null;
      }
      if(!this.device.write(cmdData)) {
        return null;
      }
    }finally {
      this.device.stop();
      this.device.delay(10);
    }
    this.device.start();
    try {
      if(!this.device.writeAddress(this.address, true)) {
        return null;
      }
      return this.device.readWithAck();
    } finally {
      this.device.stop();
      this.device.delay(10);
    }
  }


  public void update() throws FTDIException {
    this.device.transactWrite(this.address, CMD_DATA_GPIOA, bufferDataGpioA);
    this.device.transactWrite(this.address, CMD_DATA_GPIOB, bufferDataGpioB);
  }

  public enum Pin {
    GPIO_A_0(true, (byte)0x01),
    GPIO_A_1(true, (byte)0x02),
    GPIO_A_2(true, (byte)0x04),
    GPIO_A_3(true, (byte)0x08),
    GPIO_A_4(true, (byte)0x10),
    GPIO_A_5(true, (byte)0x20),
    GPIO_A_6(true, (byte)0x40),
    GPIO_A_7(true, (byte)0x80),
    GPIO_B_0(false, (byte)0x01),
    GPIO_B_1(false, (byte)0x02),
    GPIO_B_2(false, (byte)0x04),
    GPIO_B_3(false, (byte)0x08),
    GPIO_B_4(false, (byte)0x10),
    GPIO_B_5(false, (byte)0x20),
    GPIO_B_6(false, (byte)0x40),
    GPIO_B_7(false, (byte)0x80);
    private final boolean a;
    private final byte mask;

    Pin(boolean a, byte mask) {

      this.a = a;
      this.mask = mask;
    }

    public boolean isA() {
      return a;
    }

    public byte getMask() {
      return mask;
    }
  }

}
