package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPageCommunityToken;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

public class WikiCommunityChecker implements AutoCloseable {

    private static final String GET_CLUSTER_OF_PAGE = "MATCH (page:Page{id: $id})-[:PART_OF_COM]->(c) RETURN c";
    private static final String GET_PAGES_OF_CLUSTER = "MATCH (com:Community{id:$id}), (n:Page), (o:Page),(n)-[:PART_OF_COM]->(com), s = (o)-[:REFERENCES_TO]->(n) \n" +
            "WITH n, COUNT(s) AS number \n" +
            "ORDER BY number DESC \n" +
            "RETURN n,number";
    private final Driver driver;

    private WikiCommunityChecker(Driver driver) {
        this.driver = driver;
    }

    public WikiCommunityChecker(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.createConnection());
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public WikiPageCommunityToken getCommunityOf(long pageId) {
        StatementResult statementResult =
                driver.session()
                        .writeTransaction(tx ->
                                tx.run(GET_CLUSTER_OF_PAGE, parameters("id", pageId)));
        if (statementResult.hasNext()) {
            return new WikiPageCommunityToken(statementResult.single().get(0).asLong());
        } else {
            throw new IllegalArgumentException("No page with page id " + pageId + " exists.");
        }
    }

    public List<WikiPageWithLinksCount> getCommunityPages(long communityId) {
        StatementResult statementResult =
                driver.session()
                        .writeTransaction(tx ->
                                tx.run(GET_PAGES_OF_CLUSTER, parameters("id", communityId)));
        List<WikiPageWithLinksCount> result = new ArrayList<>();
        while (statementResult.hasNext()) {
            Record record = statementResult.next();
            /* TODO
            WikipageCard card = record.get(0).asNode();
            WikiPageWithLinksCount wikiPageWithLinksCount = new WikiPageWithLinksCount()
            record.get(0).
            */
        }
        return result;
    }

    private static class WikiPageWithLinksCount {
        private final WikiPageCard card;
        private final int incomingLinks;

        public WikiPageWithLinksCount(WikiPageCard card, int incomingLinks) {
            this.card = card;
            this.incomingLinks = incomingLinks;
        }

        public WikiPageCard getCard() {
            return card;
        }

        public int getIncomingLinks() {
            return incomingLinks;
        }
    }


}
