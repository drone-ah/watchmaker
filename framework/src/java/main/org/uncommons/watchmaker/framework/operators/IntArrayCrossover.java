// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * arrays of primitive ints.
 * @author Daniel Dyer
 */
public class IntArrayCrossover extends AbstractCrossover<int[]>
{
    /**
     * Default is single-point cross-over, applied to all parents.
     */
    public IntArrayCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    public IntArrayCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a fixed number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public IntArrayCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    public IntArrayCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Cross-over with a variable number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public IntArrayCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                             Probability crossoverProbability)
    {
        super(crossoverPointsVariable, crossoverProbability);
    }


    /**
     * {@inheritDoc}
     */
    protected List<int[]> mate(int[] parent1,
                               int[] parent2,
                               int numberOfCrossoverPoints,
                               Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        int[] offspring1 = new int[parent1.length];
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        int[] offspring2 = new int[parent2.length];
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<int[]> result = new ArrayList<int[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
