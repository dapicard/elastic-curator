package dapicard.elasticsearch.curator.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dapicard.elasticsearch.curator.transport.CuratorClient;

public class CuratorService {
	private static final Logger LOGGER = LogManager.getLogger(CuratorService.class);
	
	private MatchingService matchingService;
	private CuratorClient transport;

	public CuratorService(MatchingService matchingService, CuratorClient transport) {
		super();
		this.matchingService = matchingService;
		this.transport = transport;
	}

	public void doCleanup() {
		LOGGER.info("Initializing indices cleanup operations...");
		for(String indice : transport.getOpenIndices()) {
			LOGGER.debug("For indice named {}", indice);
			if(matchingService.toClose(indice)) {
				LOGGER.info("Closing indice named {}", indice);
				transport.closeIndex(indice);
			}
		}
		for(String indice : transport.getAllIndices()) {
			LOGGER.debug("For indice named {}", indice);
			if(matchingService.toDelete(indice)) {
				LOGGER.info("Deleting indice named {}", indice);
				transport.deleteIndex(indice);
			}
		}
	}
}
