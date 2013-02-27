package org.uncommons.watchmaker.framework.evaluators;

import java.util.ArrayList;
import java.util.List;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class SingleThreadedPopulationEvaluator<T> implements
        PopulationEvaluator<T> {

    @Override
    public List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population, FitnessEvaluator<? super T> fitnessEvaluator) {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());

        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate, population)));
        }
        return evaluatedPopulation;
    }

}
