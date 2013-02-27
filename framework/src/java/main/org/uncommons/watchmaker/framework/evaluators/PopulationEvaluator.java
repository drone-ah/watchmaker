package org.uncommons.watchmaker.framework.evaluators;

import java.util.List;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Evaluates population for their fitness
 * <p>
 * Allows for varying concrete implementations including Single/MultiThreaded
 * implementation</p>
 *
 * @param <T> the generic type
 */
public interface PopulationEvaluator<T> {

    /**
     * Evaluate population.
     *
     * @param population The population to evaluate (each candidate is assigned
     * a fitness score).
     * @return The evaluated population (a list of candidates with attached fitness
     * scores).
     */
    List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population,
            FitnessEvaluator<? super T> fitnessEvaluator);

}
