# nRF-Visit-Card
PCB Visit Card designed around nRF 52832 Nordic chip

![image](https://github.com/user-attachments/assets/44cbd81e-2d5b-4657-8ca4-7464be555d9f)

![image](https://github.com/user-attachments/assets/8563e602-7cf3-4d23-bd46-ceaeef5357ea)

![image](https://github.com/user-attachments/assets/ddf66f56-fcfb-4f65-b5bc-f648f18b45ca)

![image](https://github.com/user-attachments/assets/43cd5d5b-b40b-415f-b020-4431bbd3a8c2)

# nRF Connect notes

## How to program the board

(nRF52 DK)[https://www.nordicsemi.com/Products/Development-hardware/nRF52-DK] can be used to programm the board. 

nRF Visit Card board has exposed pads that are used for programming.
![visitCard_programming_witg_nRF52_DK](https://github.com/user-attachments/assets/778b8e47-01ea-46b1-b14c-1322ab45e64c)

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
