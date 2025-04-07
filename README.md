# nRF-Visit-Card
This repository contains PCB design files for Visit Card designed around nRF 52832 Nordic chip as well as firmware running on it.

## Visit Card features
- User button
- Powered with CR2032 battery
- Bluetooth connectivity
- Magnetometer / compass with HMC5883L IC
- Array of1 12 circulary arranged LEDs to be used as compass
- Array of 6 linearly arranged LEDs to be used for custom needs
- Exposed programming pads
- Exposed UART pads

Visit Card has meandering line antenna for bluetooth connectivity.

To reduce cost, component count and layout space, microcontroller uses internal RC oscilator.
Board is designed as a 4 layer board with stackup:
- Top and bottom layer are signal layers
- Power plane and ground plane are the two middle layers

![image](https://github.com/user-attachments/assets/8589ff6a-9532-4c5c-9bc0-68de45dbeede)

This is how it started:

<img src="https://github.com/user-attachments/assets/11025395-a15e-4ba9-b2d4-a1033ff08f3d" width="300">

## Firmware functionalities
<img src="nRF_visitCard_firmware_functions.png" width="600">

## Schematics

![image](https://github.com/user-attachments/assets/44cbd81e-2d5b-4657-8ca4-7464be555d9f)

![image](https://github.com/user-attachments/assets/41059760-dbab-4f64-ac20-5e2e05677276)


![image](https://github.com/user-attachments/assets/6245c739-601c-4fdf-a974-a043f2d8678f)


![image](https://github.com/user-attachments/assets/9695e5d8-12e0-4b5b-81b7-b206917b640f)


![image](https://github.com/user-attachments/assets/cd7313c1-e0a2-4602-803e-3325d0341923)


![image](https://github.com/user-attachments/assets/94b7cabe-fce5-4caa-a3c1-a3271ef415c1)


![image](https://github.com/user-attachments/assets/8956ef3f-d99e-4088-82a9-a7bc84e0e17b)


![image](https://github.com/user-attachments/assets/9c81f184-08eb-4778-8a4b-53350d1dd8d7)


![image](https://github.com/user-attachments/assets/79bf2153-890b-4bf6-9e1f-79c108f14d09)


![image](https://github.com/user-attachments/assets/8a80f6cd-471d-46ce-a88c-4ed48157790a)


![image](https://github.com/user-attachments/assets/4b402ca1-72bf-458c-afe3-039e21d39bda)

## Layout - bottom copper layer

![image](https://github.com/user-attachments/assets/944f7d11-1d3f-49a6-a678-71cc5e9d8775)


# Rendered images of the board

![image](https://github.com/user-attachments/assets/ddf66f56-fcfb-4f65-b5bc-f648f18b45ca)



## Soldered board

![image](https://github.com/user-attachments/assets/8aa683d2-f650-419b-bce5-8ee3a23b7501)


# nRF Connect notes

## How to program the board

[nRF52 DK](https://www.nordicsemi.com/Products/Development-hardware/nRF52-DK) can be used to program the visit card. 

nRF Visit Card board has exposed pads that are used for programming.
![visitCard_programming_witg_nRF52_DK](https://github.com/user-attachments/assets/778b8e47-01ea-46b1-b14c-1322ab45e64c)

### Pinout

![image](https://github.com/user-attachments/assets/a7e94f38-904f-421a-b804-ea394c2b5d63)

## Internal oscilator enable

To enable internal oscilator for nRF52832 in the proj.conf file add:
````
CONFIG_CLOCK_CONTROL_NRF_K32SRC_RC=y
CONFIG_CLOCK_CONTROL_NRF_K32SRC_500PPM=y
CONFIG_CLOCK_CONTROL_NRF_K32SRC_RC_CALIBRATION=y
CONFIG_CLOCK_CONTROL_NRF_CALIBRATION_LF_ALWAYS_ON=y
````

## Enable rest pin to be input GPIO

In the proj.conf file add:
````
CONFIG_GPIO_AS_PINRESET=n
````

Save the project in VS code and save the VS code workspace. Once the workspace is saved, there should be a file named <workspace name>.code-workspace.
Modify this file to include softreset enable. Search for "settings" item in this json file.
````
"settings": {
	"nrf-connect.applicationOptions": {
		"${workspaceFolder}": {
			"flash": {
			"softreset": true
			}
		}
	}
}
````

Instead of modifying json file, you can also manually program the device by:
````
 nrfjprog --eraseuicr
 nrfjprog --program <path to hex file> --verify
````
 Example path: c:\Users\dprerad\Desktop\bt-fund\lesson3\blefund_less3_exer1\build\zephyr\zephyr.hex

 You might need to reset the board after this - unplug it and plug it back in.

## If _printk_ is not working
In the proj.conf file add:
````
CONFIG_CONSOLE=y
````
## Disabling NFC pins
Pins P0.09 and P0.10 are NFC pins and if you wan't to use them as GPIO pins you need to disable NFC.
[nrf52 enabling gpio on nfc pins](https://devzone.nordicsemi.com/f/nordic-q-a/35505/nrf52-enabling-gpio-on-nfc-pins)
```
CONFIG_NFCT_PINS_AS_GPIOS=y
```

 # Library of knowledge

 [nRF Connect SDK Fundamentals](https://academy.nordicsemi.com/courses/nrf-connect-sdk-fundamentals/)
 
 [Bluetooth Low Energy Fundamentals](https://academy.nordicsemi.com/courses/bluetooth-low-energy-fundamentals/)
