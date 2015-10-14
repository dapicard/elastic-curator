package dapicard.elasticsearch.curator.transport;

import java.util.Collection;

public interface CuratorClient {

	Collection<String> getOpenedIndices();

	void closeIndex(String indiceName);

	void deleteIndex(String indiceName);

	Collection<String> getAllIndices();

}
