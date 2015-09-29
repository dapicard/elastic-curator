package dapicard.elasticsearch.curator.transport;

import java.util.Collection;

public interface CuratorClient {

	Collection<String> getOpenIndices();

	void closeIndex(String indiceName);

	void deleteIndex(String indiceName);

	Collection<String> getAllIndices();

}
