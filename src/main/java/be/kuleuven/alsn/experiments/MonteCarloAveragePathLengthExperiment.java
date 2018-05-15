package be.kuleuven.alsn.experiments;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaSeparationFacade;
import be.kuleuven.alsn.facade.WikipediaSeparationFacade;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonteCarloAveragePathLengthExperiment {

    public static final List<WikiCommunityToken> LARGEST_COMMUNITIES = Stream.of(10, 9, 13, 11, 208, 37283, 645, 159, 28, 143,
            21686, 124, 66, 1386, 9163, 156, 350, 76958, 236, 173,
            204405, 15268, 260616, 270317, 133270, 383405, 4972, 335465, 259532, 260877)
            .map(WikiCommunityToken::new)
            .collect(Collectors.toList());
    public static final List<WikiCommunityToken> COMMUNITIES_OF_LARGEST_MEMBERS = Stream.of(7596, 236, 236, 6093, 6093, 6093,
            29, 86092, 7596, 308, 208, 308, 133270, 143, 6093, 11, 236, 236, 208, 308)
            .map(WikiCommunityToken::new)
            .collect(Collectors.toList());


    private final int sampleSize = 100;
    private final List<WikiCommunityToken> communities;

    private final IWikipediaSeparationFacade facade;

    public MonteCarloAveragePathLengthExperiment(IWikipediaSeparationFacade facade, List<WikiCommunityToken> communities) {
        this.facade = facade;
        this.communities = communities;

    }

    public static void main(String[] args) throws IOException {
        Neo4jConnectionDetails neo4jArguments = new Neo4jConnectionDetails();

        JCommander.newBuilder()
                .addObject(neo4jArguments)
                .build()
                .parse(args);

        IWikipediaSeparationFacade facade = new WikipediaSeparationFacade();
        try {
            facade.setNeo4jConnection(neo4jArguments);
        } catch (ServiceUnavailableException e) {
            System.out.println("WARNING: NO DATABASE CONNECTION ESTABLISHED: PLEASE START UP NEO4J!");
        }
        MonteCarloAveragePathLengthExperiment experiment = new MonteCarloAveragePathLengthExperiment(facade, LARGEST_COMMUNITIES);
        experiment.run();

    }

    private void run() throws IOException {
        PrintWriter totalOutputFile = new PrintWriter(new FileWriter("experiments/block-total.csv"));
        totalOutputFile.write("iteration\ttotal length\taverage length\tamount of no solution found\tamount of timeouts\n");
        totalOutputFile.flush();

        for (int i = 0; i <= communities.size(); i++) {
            if (i > 0) {
                facade.blockCommunity(communities.get(i - 1));
            }
            PrintWriter outputFile = new PrintWriter(new FileWriter("experiments/block" + i + ".csv"));

            int totalLength = 0;
            int noPathsFound = 0;
            int timeouts = 0;

            for (int j = 0; j < sampleSize; j++) {

                WikiPageCard page1 = facade.getRandomPage();
                WikiPageCard page2 = facade.getRandomPage();

                try {
                    Collection<WikiPath> path = facade.calculateShortestPath(page1.getPageName(), page2.getPageName());

                    if (path.isEmpty()) {
                        j -= 1;
                        noPathsFound += 1;
                    } else {
                        int length = path.iterator().next().getPathLength();
                        totalLength += length;
                        String output = page1.getPageId() + "\t" + page2.getPageId() + "\t" + length + "\n";

                        outputFile.write(output);
                        outputFile.flush();
                    }
                } catch (ClientException e) {
                    timeouts += 1;
                }
            }
            outputFile.close();

            String totalFileOutput =
                    i + "\t" +
                            totalLength + "\t" +
                            ((double) totalLength / (double) sampleSize) +
                            "\t" + noPathsFound + "\t" + timeouts+"\n";
            totalOutputFile.write(totalFileOutput);
            totalOutputFile.flush();

        }
        totalOutputFile.close();


    }


}
