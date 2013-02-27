package org.uncommons.watchmaker.framework.evaluators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluationWorker;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.FitnessEvalutationTask;

public class MultiThreadedPopulationEvaluator<T> implements
        PopulationEvaluator<T> {

    // A single multi-threaded worker is shared among multiple evolution engine instances.
    private static FitnessEvaluationWorker concurrentWorker = null;

    @Override
    public List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population, FitnessEvaluator<? super T> fitnessEvaluator) {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());

            // Divide the required number of fitness evaluations equally among the
            // available processors and coordinate the threads so that we do not
            // proceed until all threads have finished processing.
            try
            {
                List<T> unmodifiablePopulation = Collections.unmodifiableList(population);
                List<Future<EvaluatedCandidate<T>>> results = new ArrayList<Future<EvaluatedCandidate<T>>>(population.size());
                // Submit tasks for execution and wait until all threads have finished fitness evaluations.
                for (T candidate : population)
                {
                    results.add(getSharedWorker().submit(new FitnessEvalutationTask<T>(fitnessEvaluator,
                                                                                       candidate,
                                                                                       unmodifiablePopulation)));
                }
                for (Future<EvaluatedCandidate<T>> result : results)
                {
                    evaluatedPopulation.add(result.get());
                }
            }
            catch (ExecutionException ex)
            {
                throw new IllegalStateException("Fitness evaluation task execution failed.", ex);
            }
            catch (InterruptedException ex)
            {
                // Restore the interrupted status, allows methods further up the call-stack
                // to abort processing if appropriate.
                Thread.currentThread().interrupt();
            }
            return evaluatedPopulation;
    }

    /**
     * Lazily create the multi-threaded worker for fitness evaluations.
     */
    private static synchronized FitnessEvaluationWorker getSharedWorker()
    {
        if (concurrentWorker == null)
        {
            concurrentWorker = new FitnessEvaluationWorker();
        }
        return concurrentWorker;
    }

}
