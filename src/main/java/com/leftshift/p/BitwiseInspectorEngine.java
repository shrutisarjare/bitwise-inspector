package com.leftshift.p;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@RestController
public class BitwiseInspectorEngine {

    // Simple Web Endpoint that captures your console output and sends it to the browser
    @GetMapping("/")
    public String showInHtml(
            @RequestParam(defaultValue = "-2") int value,
            @RequestParam(defaultValue = "7") int shift) {

        // 1. Temporarily redirect System.out to capture what runLogic prints
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));

        // 2. Run your exact method
        runLogic(value, shift);

        // 3. Restore original System.out
        System.setOut(originalOut);

        // 4. Return simple HTML with an input form and the captured console text
        String consoleOutput = buffer.toString();

        return "<html>"
                + "<body style='background:#111; color:#00ff66; font-family:monospace; padding:20px;'>"
                + "  <h2>JVM Bitwise Left-Shift Inspector</h2>"
                + "  <form action='/' method='get'>"
                + "    <label>Byte Value: </label>"
                + "    <input type='number' name='value' value='" + value + "' style='width:60px;'> "
                + "    <label>Shift By: </label>"
                + "    <input type='number' name='shift' value='" + shift + "' style='width:60px;'> "
                + "    <button type='submit'>Run</button>"
                + "  </form>"
                + "  <hr style='border-color:#333; margin:20px 0;'>"
                + "  <pre style='font-size:15px;'>" + consoleOutput + "</pre>"
                + "</body>"
                + "</html>";
    }

    // --- YOUR EXACT METHODS REMAIN UNCHANGED BELOW ---

    public static void runLogic(int inputByte, int shiftBy) {
        System.out.println("..... STEP 1: ORIGINAL 8-BIT BYTE....");
        int[] originalByte = new int[8];
        fillBits(inputByte, originalByte);
        System.out.println("Value: " + inputByte);
        System.out.print("Bits : ");
        printArray(originalByte);
        System.out.println("\n");

        System.out.println("=== STEP 2: JVM NUMERIC PROMOTION (4 ARRAYS OF 8 BITS = 32 BITS) ===");
        System.out.println("Java cannot shift a byte. It creates 4 separate 8-bit blocks:");

        int promotedInt = (int) inputByte;
        int[] block1 = new int[8];
        int[] block2 = new int[8];
        int[] block3 = new int[8];
        int[] block4 = new int[8];

        fill32BitBlocks(promotedInt, block1, block2, block3, block4);

        printArray(block1);
        System.out.print(" | ");
        printArray(block2);
        System.out.print(" | ");
        printArray(block3);
        System.out.print(" | ");
        printArray(block4);
        System.out.println();
        System.out.println("Promoted Integer Value: " + promotedInt);
        System.out.println();

        System.out.println("=== STEP 3: PERFORM LEFT SHIFT (<< " + shiftBy + ") ===");
        int effectiveShift = shiftBy % 32;
        int shiftedInt = promotedInt << effectiveShift;

        fill32BitBlocks(shiftedInt, block1, block2, block3, block4);

        System.out.println("Shift amount: " + shiftBy + " (effective: " + effectiveShift + ")");
        System.out.print(" ");
        printArray(block1);
        System.out.print(" | ");
        printArray(block2);
        System.out.print(" | ");
        printArray(block3);
        System.out.print(" | ");
        printArray(block4);
        System.out.println();
        System.out.println("Shifted Integer Value: " + shiftedInt);
        System.out.println();

        System.out.println("=== STEP 4: TRUNCATE / EXPLICIT CAST BACK TO BYTE ===");
        System.out.println("Java throws away Block 1, Block 2, and Block 3.");
        System.out.println("It keeps only the LAST block (Block 4):");

        byte castAnswer = (byte) shiftedInt;
        System.out.print("Truncated 8 bits: ");
        printArray(block4);
        System.out.println();
        System.out.println("Final Byte Value: " + castAnswer);
        System.out.println();

        System.out.println("==========================================");
        System.out.println("RESULT: (byte)(" + inputByte + " << " + shiftBy + ") = " + castAnswer);
    }

    private static void fillBits(int number, int[] targetArray) {
        int temp = number;
        if (temp < 0) {
            temp = temp + 256;
            System.out.println("temp<0" + temp);
        }
        for (int i = 7; i >= 0; i--) {
            targetArray[i] = temp % 2;
            temp = temp / 2;
        }
    }

    private static void fill32BitBlocks(int number, int[] b1, int[] b2, int[] b3, int[] b4) {
        for (int i = 0; i < 8; i++) {
            b1[7 - i] = (number >> (24 + i)) & 1;
            b2[7 - i] = (number >> (16 + i)) & 1;
            b3[7 - i] = (number >> (8 + i)) & 1;
            b4[7 - i] = (number >> i) & 1;
        }
    }

    private static void printArray(int[] arr) {
        System.out.print("[ ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.print("]");
    }
}