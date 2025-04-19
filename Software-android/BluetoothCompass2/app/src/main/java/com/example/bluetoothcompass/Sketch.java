package com.example.bluetoothcompass;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class Sketch extends PApplet {
    private ArrayList<BluetoothDevice> deviceList;
    private int direction = 0;
    BluetoothGattCharacteristic writeChar;
    BluetoothGatt bluetoothGatt;

    boolean ledState = false;
    boolean mouseOverRect = false;

    int rectX = 100;
    int rectY = 100;
    int rectW = 500;
    int rectH = 100;
    void setLedCharacteristic(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        this.writeChar = characteristic;
        this.bluetoothGatt = gatt;
    }

    void sendBooleanValue(boolean value) {
        if (writeChar != null && bluetoothGatt != null) {
            byte[] data = new byte[]{(byte) (value ? 1 : 0)};
            writeChar.setValue(data);
            bluetoothGatt.writeCharacteristic(writeChar);
        }
    }

    public void settings() {
        fullScreen();
//        size(600, 600);
        deviceList = new ArrayList<>();
    }

    public void updateDirection(int direction) {
        this.direction = direction;
    }
    public void addDevice(BluetoothDevice device) {
        // Check for duplicates based on device address
        boolean isDuplicate = false;
        for (BluetoothDevice existingDevice : deviceList) {
            if (existingDevice.getAddress().equals(device.getAddress())) {
                isDuplicate = true;
                break;
            }
        }

        // If it's not a duplicate, add the device
        if (!isDuplicate) {
            deviceList.add(device);
        }
    }

    PVector softBlue = new PVector(70, 130, 180);      // Steel Blue
    PVector brightBlue = new PVector(0, 191, 255);     // Deep Sky Blue

    PVector brightRed = new PVector(255, 60, 60);  // A vivid, clean red
    PVector deepRed = new PVector(178, 34, 34);    // Firebrick Red

//    color[] camoColors;

    public void setup() {
        rectX = width / 2 - rectW / 2;
        rectY = height / 2 + 600 - rectH / 2;
    }

   public  void drawShapeAtAngle(float angle) {
        pushMatrix();  // Save the current transformation matrix
        translate(width / 2, height / 2);  // Move to the center
        rotate(radians(angle));  // Rotate by the given angle

        // Set stroke weight and fill colors for the shapes
        strokeWeight(3);

        // Draw the triangles and circle with the correct colors
        fill(softBlue.x, softBlue.y, softBlue.z);
        triangle(0, 0, -25, 0, 0, -400);  // Left triangle

        fill(brightBlue.x, brightBlue.y, brightBlue.z);
        triangle(0, 0, 25, 0, 0, -400);   // Right triangle

        fill(brightRed.x, brightRed.y, brightRed.z);
        triangle(0, 0, -25, 0, 0, 400);   // Bottom-left triangle

        fill(255, 0, 0);
        triangle(0, 0, 25, 0, 0, 400);    // Bottom-right triangle

        fill(deepRed.x, deepRed.y, deepRed.z);
        circle(0, 0, 50);  // Circle in the center

        popMatrix();  // Restore the transformation matrix
    }

    public void draw() {
        background(250);

        // Display the list of devices
        textSize(100);
        fill(0);
        int yPos = 20; // Starting position for displaying the text
        text("Bluetooth compass\ndemonstration", width / 2, 250);
        // Loop through each device in the device list and print its name and address
//        for (BluetoothDevice device : deviceList) {
//            String deviceInfo = "Device: " + device.getName() + " [" + device.getAddress() + "]";
//            text(deviceInfo, 500, yPos);
//            yPos += 40; // Move down to print the next device
//        }

//        loadPixels();
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                float n = noise(x * bgNoiseScale, y * bgNoiseScale, bgNoiseDepth);
//
//                // Quantize to 4 levels for sharp camo edges
//                int level = (int)(n * camoColors.length);
//                level = constrain(level, 0, camoColors.length - 1);
//
//                pixels[x + y * width] = camoColors[level];
//            }
//        }
//        updatePixels();

        drawShapeAtAngle(direction);

        strokeWeight(6);
        noFill();
        circle(width / 2, height / 2, 800);

        textAlign(CENTER, CENTER);

        textSize(64);
        textAlign(CENTER, CENTER);
        fill(0);  // Black color for text
        text("N", width / 2, height / 2 - 340);  // North label
        text("E", width / 2 + 340, height / 2);  // East label
        text("S", width / 2, height / 2 + 340);  // South label
        text("W", width / 2 - 340, height / 2);  // West label

        textSize(50);

        float cx = width / 2;
        float cy = height / 2;
        float r1 = 360;  // inner radius
        float r2 = 400;  // outer radius
        float rt = 425;  // text radius (a little beyond outer line)
        for (int angle = 0; angle < 360; angle += 30) {
            float rad = radians(angle);  // convert to radians

            // Tick marks
            float x1 = cx + cos(rad) * r1;
            float y1 = cy + sin(rad) * r1;
            float x2 = cx + cos(rad) * r2;
            float y2 = cy + sin(rad) * r2;
            line(x1, y1, x2, y2);

            // Text position
            float xt = cx + cos(rad) * rt;
            float yt = cy + sin(rad) * rt;

            // Draw rotated text tangent to circle
            pushMatrix();
            translate(xt, yt);
            rotate(rad + HALF_PI);  // Tangent rotation (angle + 90°)
            fill(0);  // Ensure text is black
            text(str(angle) + "°", 0, 0);
            popMatrix();
        }

        mouseOverRect = mouseX >= rectX && mouseX <= rectX + rectW &&
                mouseY >= rectY && mouseY <= rectY + rectH;

        if (mouseOverRect) {
            fill(173, 216, 230); // light blue
        } else {
            fill(200); // gray if not hovered
        }

        rect(rectX, rectY, rectW, rectH);

        fill(0);
        text("Press to toggle LED", rectX + rectW / 2, rectY + rectH / 2);
    }

    public void keyPressed() {
        if (mouseOverRect) {
            ledState = !ledState;
            sendBooleanValue(ledState);

            println("LED state is now: " + ledState);
        }
    }
}

