// ============================================================================
//   Copyright 2006 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package uk.co.dandyer.maths.random;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Utility to generate an input file for the DIEHARD suite of statistical
 * tests for random number generators.
 * @author Daniel Dyer
 */
public class DiehardInputGenerator
{
    private DiehardInputGenerator()
    {
        // Prevents instantiation.
    }

    // How many 32-bit values should be written to the output file.
    private static final int INT_COUNT = 3000000;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.out.println("Expected arguments:");
            System.out.println("\t<Fully-qualified RNG class name> <Output file>");
            System.exit(1);
        }
        Class<? extends Random> rngClass = (Class<? extends Random>) Class.forName(args[0]);
        File outputFile = new File(args[1]);
        generateOutputFile(rngClass.newInstance(), outputFile);
    }


    public static void generateOutputFile(Random rng,
                                          File outputFile) throws IOException
    {
        DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        for (int i = 0; i < INT_COUNT; i++)
        {
            dataOutput.writeInt(rng.nextInt());
        }
        dataOutput.flush();
        dataOutput.close();
    }
}
