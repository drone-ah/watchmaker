package org.watchmaker.async.slsb.evaluator;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

@Stateless(name="AsyncFitnessEvaluator")
@Asynchronous
public class AsyncFitnessEvaluator {

    public <T> Future<EvaluatedCandidate<T>> getFitness(FitnessEvaluator<? super T> fitnessEvaluator,
                                            T candidate,
                                            List<T> unmodifiablePopulation) {
        double fitness = fitnessEvaluator.getFitness(candidate, unmodifiablePopulation);
        return new AsyncResult<EvaluatedCandidate<T>>(new EvaluatedCandidate<T>(candidate, fitness));
    }

}
