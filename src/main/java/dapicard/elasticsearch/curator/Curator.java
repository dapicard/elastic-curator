package dapicard.elasticsearch.curator;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import dapicard.elasticsearch.curator.configuration.Configuration;
import dapicard.elasticsearch.curator.service.CuratorService;
import dapicard.elasticsearch.curator.service.MatchingService;
import dapicard.elasticsearch.curator.transport.CuratorClient;
import dapicard.elasticsearch.curator.transport.impl.CuratorNodeClient;

public class Curator {
	private static final Logger LOGGER = LogManager.getLogger(Curator.class);
	public static final String TRANSPORT_SETTING = "transport.client.initial_nodes";
	public static final int TRANSPORT_DEFAULT_PORT = 9300;

	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		try {
			Configuration config = Configuration.getConfiguration();
			Builder settingsBuilder = ImmutableSettings.settingsBuilder();
			Settings settings = settingsBuilder.loadFromClasspath("elasticsearch.yml").build();
			
			String[] transportInitialNodes = settings.getAsArray(TRANSPORT_SETTING);
			
			Client client;
			if(transportInitialNodes != null && transportInitialNodes.length > 0) {
				//Transport client
				client = new TransportClient();
				//Specific configuration
				for(String initialNode : transportInitialNodes) {
					int port = TRANSPORT_DEFAULT_PORT;
					LOGGER.info("Adding remote node [" + initialNode + "] to Transport client");
					String[] splitHost = initialNode.split(":", 2);
					if (splitHost.length == 2) {
						initialNode = splitHost[0];
						try {
							port = Integer.parseInt(splitHost[1]);
						} catch (NumberFormatException nfe) {
							LOGGER.warn("The port number [" + splitHost[1] + "] is not a valid port number. Using port number " + TRANSPORT_DEFAULT_PORT);
						}
					}
					((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(initialNode, port));
				}
			} else {
				//Node client
				client = nodeBuilder().client(true).loadConfigSettings(true).node().client();
			}
			
			MatchingService matchingService = new MatchingService(config);
			CuratorClient transport = new CuratorNodeClient(client);
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
