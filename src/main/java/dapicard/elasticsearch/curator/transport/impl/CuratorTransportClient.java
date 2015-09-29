package dapicard.elasticsearch.curator.transport.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import dapicard.elasticsearch.curator.transport.CuratorClient;

/**
 * A TransportClient that reads its Transport Adresses from the custom directive
 * "network.transport.client.initial_nodes" in the settings
 * 
 * @author Damien Picard
 *
 */
public class CuratorTransportClient extends TransportClient implements CuratorClient {
	private static final Logger LOGGER = LogManager.getLogger(CuratorTransportClient.class);
	public static final String SETTING = "network.transport.client.initial_nodes";
	public static final int DEFAULT_PORT = 9300;

	private ConcurrentLinkedQueue<String> indicesToClose = new ConcurrentLinkedQueue<>();
	private AtomicBoolean closeInProgress = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<String> indicesToDelete = new ConcurrentLinkedQueue<>();
	private AtomicBoolean deleteInProgress = new AtomicBoolean(false);

	public CuratorTransportClient() throws ElasticsearchException {
		super();
		configure();
	}

	public CuratorTransportClient(Builder settings, boolean loadConfigSettings) throws ElasticsearchException {
		super(settings, loadConfigSettings);
		configure();
	}

	public CuratorTransportClient(Builder settings) {
		super(settings);
		configure();
	}

	public CuratorTransportClient(Settings pSettings, boolean loadConfigSettings) throws ElasticsearchException {
		super(pSettings, loadConfigSettings);
		configure();
	}

	public CuratorTransportClient(Settings settings) {
		super(settings);
		configure();
	}

	@Override
	public Collection<String> getOpenIndices() {
		return Arrays.asList(admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllOpenIndices());
	}

	@Override
	public void closeIndex(final String indiceName) {
		indicesToClose.add(indiceName);
		if (!closeInProgress.get()) {
			doClose();
		}
	}

	protected void doClose() {
		closeInProgress.set(true);
		final String indiceName = indicesToClose.poll();
		if (indiceName == null) {
			closeInProgress.set(false);
		} else {
			admin().indices().close(new CloseIndexRequest(indiceName), new ActionListener<CloseIndexResponse>() {
				@Override
				public void onResponse(CloseIndexResponse response) {
					LOGGER.info("Indice <<{}>> successfully closed", indiceName);
					doClose();
				}

				@Override
				public void onFailure(Throwable e) {
					LOGGER.error("Error while closing indice " + indiceName, e);
					doClose();
				}
			});
		}
	}

	@Override
	public void deleteIndex(final String indiceName) {
		indicesToDelete.add(indiceName);
		if (!deleteInProgress.get()) {
			doDelete();
		}
	}

	protected void doDelete() {
		deleteInProgress.set(true);
		final String indiceName = indicesToDelete.poll();
		if (indiceName == null) {
			deleteInProgress.set(false);
		} else {
			admin().indices().delete(new DeleteIndexRequest(indiceName), new ActionListener<DeleteIndexResponse>() {
				@Override
				public void onResponse(DeleteIndexResponse response) {
					LOGGER.info("Indice <<{}>> successfully deleted", indiceName);
					doDelete();
				}

				@Override
				public void onFailure(Throwable e) {
					LOGGER.error("Error while deleting indice " + indiceName, e);
					doDelete();
				}
			});
		}
	}

	private void configure() {
		for (String host : this.settings().getAsArray(SETTING)) {
			int port = DEFAULT_PORT;
			LOGGER.info("Adding remote node [" + host + "] to Transport client");
			String[] splitHost = host.split(":", 2);
			if (splitHost.length == 2) {
				host = splitHost[0];
				try {
					port = Integer.parseInt(splitHost[1]);
				} catch (NumberFormatException nfe) {
					LOGGER.warn("The port number [" + splitHost[1] + "] is not a valid port number. Using port number " + DEFAULT_PORT);
				}
			}
			this.addTransportAddress(new InetSocketTransportAddress(host, port));
		}
	}

	@Override
	public Collection<String> getAllIndices() {
		return Arrays.asList(admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllIndices());
	}
}
