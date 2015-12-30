package com.github.dapicard.elasticsearch.curator.transport.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;

import com.github.dapicard.elasticsearch.curator.transport.CuratorClient;

public class CuratorNodeClient implements CuratorClient {
	private static final Logger LOGGER = LogManager.getLogger(CuratorNodeClient.class);
	
	private Client client;
	
	private ConcurrentLinkedQueue<String> indicesToClose = new ConcurrentLinkedQueue<>();
	private AtomicBoolean closeInProgress = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<String> indicesToDelete = new ConcurrentLinkedQueue<>();
	private AtomicBoolean deleteInProgress = new AtomicBoolean(false);
	
	public CuratorNodeClient(Client client) {
		this.client = client;
	}
	
	@Override
	public Collection<String> getOpenedIndices() {
		return Arrays.asList(client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllOpenIndices());
	}
	
	@Override
	public Collection<String> getAllIndices() {
		return Arrays.asList(client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().concreteAllIndices());
	}

	@Override
	public void closeIndex(final String indiceName) {
		if(!indicesToClose.contains(indiceName)) {
			indicesToClose.add(indiceName);
		}
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
			client.admin().indices().close(new CloseIndexRequest(indiceName), new ActionListener<CloseIndexResponse>() {
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
		if(!indicesToDelete.contains(indiceName)) {
			indicesToDelete.add(indiceName);
		}
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
			client.admin().indices().delete(new DeleteIndexRequest(indiceName), new ActionListener<DeleteIndexResponse>() {
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

}
