package com.github.dapicard.elasticsearch.curator.transport;

import java.util.Collection;

/**
 * Client Interface to retrieve informations and do actions on indices.
 * @author Damien Picard
 *
 */
public interface CuratorClient {

	/**
	 * Returns the list of opened indices on the cluster 
	 * @return opened indices name
	 */
	Collection<String> getOpenedIndices();

	/**
	 * Closes the given indice
	 * @param indiceName The name of the indice to close
	 */
	void closeIndex(String indiceName);

	/**
	 * Deletes the given indice
	 * @param indiceName The name of the indice to delete
	 */
	void deleteIndex(String indiceName);

	/**
	 * Returns the list of all indices on the cluster
	 * @return all indices name
	 */
	Collection<String> getAllIndices();

}
