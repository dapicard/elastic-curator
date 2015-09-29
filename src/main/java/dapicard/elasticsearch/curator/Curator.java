package dapicard.elasticsearch.curator;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dapicard.elasticsearch.curator.configuration.Configuration;
import dapicard.elasticsearch.curator.service.CuratorService;
import dapicard.elasticsearch.curator.service.MatchingService;
import dapicard.elasticsearch.curator.transport.CuratorClient;
import dapicard.elasticsearch.curator.transport.impl.CuratorNodeClient;

public class Curator {
	private static final Logger LOGGER = LogManager.getLogger(Curator.class);

	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		try {
			Configuration config = Configuration.getConfiguration();
			MatchingService matchingService = new MatchingService(config);
			CuratorClient transport = new CuratorNodeClient();
			final CuratorService curatorService = new CuratorService(matchingService, transport);
			
			Runnable cleanup = new Runnable() {
				@Override
				public void run() {
					curatorService.doCleanup();
				}
			};
			SCHEDULER.scheduleWithFixedDelay(cleanup, config.getInitialDelayDuration().getStandardSeconds(), config.getRepeatDelayDuration().getStandardSeconds(), TimeUnit.SECONDS);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage(), ioe);
		}

	}

}
