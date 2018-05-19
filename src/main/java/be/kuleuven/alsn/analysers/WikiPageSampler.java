package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCard;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.neo4j.driver.v1.Values.parameters;

public class WikiPageSampler implements AutoCloseable {

    private static final String countingQuery = "MATCH (page:Page) RETURN count(page)";
    private static final String samplingQuery = "MATCH (page:Page) RETURN page.id,page.title SKIP $skip LIMIT $limit";
    private final Driver driver;
    private final long amountOfPages;
    private final RandomDataGenerator random = new RandomDataGenerator();

    private WikiPageSampler(Driver driver) {
        this.driver = driver;
        this.amountOfPages = getAmountOfPages();
    }

    public WikiPageSampler(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.createConnection());
    }

    public static void main(String[] args) {
        WikiPageSampler sampler = new WikiPageSampler(new Neo4jConnectionDetails());
        IntStream.range(0, 10)
                .forEach(x -> System.out.println(sampler.getRandomPage()));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public long getAmountOfPages() {
        return driver.session()
                .writeTransaction(tx ->
                        tx.run(countingQuery))
                .next()
                .get(0).asLong();
    }

    public WikiPageCard getRandomPage() {
        long randomIdx = random.nextLong(0,amountOfPages);
        StatementResult statementResult = driver.session()
                .writeTransaction(tx ->
                        tx.run(samplingQuery, parameters("skip", randomIdx, "limit", 1)));
        Record record = statementResult.single();
        return new WikiPageCard(record.get(0).asLong(), record.get(1).asString());
    }

    public void setRandomiserSeed(Long seed) {
        random.reSeed(seed);
    }
}
