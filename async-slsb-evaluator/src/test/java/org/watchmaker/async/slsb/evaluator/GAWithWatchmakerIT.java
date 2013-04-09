package org.watchmaker.async.slsb.evaluator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.StringCrossover;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.watchmaker.async.AsyncPopulationEvaluator;

@RunWith(Arquillian.class)
public class GAWithWatchmakerIT {

    @Deployment
    public static Archive<?> createDeployment() {

        MavenDependencyResolver resolver =  DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml")
                .goOffline();

        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                                .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
                                .addClass(AsyncFitnessEvaluator.class)
                                .addClass(AsyncPopulationEvaluator.class)
                                .addAsLibraries(resolver.artifact("org.uncommons.watchmaker:watchmaker-framework").resolveAsFiles());


        System.out.println(archive.toString(true));

        return archive;
    }

    @Inject
    private AsyncPopulationEvaluator<String> popEvaluator;

    @Test
    public void testSomething() {

     // Define the set of permitted characters (A-Z plus space).
        char[] chars = new char[27];
        for (char c = 'A'; c <= 'Z'; c++)
        {
            chars[c - 'A'] = c;
        }
        chars[26] = ' ';

        CandidateFactory<String> factory = new StringFactory(chars, 11);

        List<EvolutionaryOperator<String>> operators = new LinkedList<EvolutionaryOperator<String>>();
        operators.add(new StringCrossover());
        operators.add(new StringMutation(chars, new Probability(0.02)));

        EvolutionaryOperator<String> pipeline = new EvolutionPipeline<String>(operators);

        FitnessEvaluator<String> fitnessEvaluator = new StringEvaluator();
        SelectionStrategy<Object> selection = new TournamentSelection(new Probability(1));
        Random rng = new MersenneTwisterRNG();

        EvolutionEngine<String> engine =
                new GenerationalEvolutionEngine<String>(factory,
                                                    pipeline,
                                                    fitnessEvaluator,
                                                    selection,
                                                    rng);

        engine.setPopulationEvaluator(popEvaluator);
        engine.addEvolutionObserver(new EvolutionObserver<String>()
        {
            @Override
            public void populationUpdate(PopulationData<? extends String> data)
            {
                System.out.printf("Generation %d: %s\n",
                                  data.getGenerationNumber(),
                                  data.getBestCandidate());
            }
        });

        long start = System.currentTimeMillis();

        String result = engine.evolve(1000, 0, new GenerationCount(250));

        System.out.println("Time Taken: " + (System.currentTimeMillis() - start));
        System.out.println(result);


    }


    public static class StringEvaluator implements FitnessEvaluator<String>
    {
        private final String targetString = "HELLO WORLD";

        /**
         * Assigns one "fitness point" for every character in the
         * candidate String that matches the corresponding position in
         * the target string.
         */
        @Override
        public double getFitness(String candidate,
                                 List<? extends String> population)
        {
            int matches = 0;
            for (int i = 0; i < candidate.length(); i++)
            {
                if (candidate.charAt(i) == targetString.charAt(i))
                {
                    ++matches;
                }
            }
            return matches;
        }

        @Override
        public boolean isNatural()
        {
            return true;
        }
    }

}
