/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.downloadmanager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.preferences.Application;
import io.realm.Realm;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/4/16.
 */
public class DownloadServiceHelper {

	private final AptoideDownloadManager aptoideDownloadManager;
	private PermissionManager permissionManager;

	public DownloadServiceHelper(AptoideDownloadManager aptoideDownloadManager, PermissionManager permissionManager) {
		this.aptoideDownloadManager = aptoideDownloadManager;
		this.permissionManager = permissionManager;
	}

	/**
	 * Pause all the running downloads
	 */
	public void pauseAllDownloads() {
		Context context = Application.getContext();
		Intent intent = new Intent(context, DownloadService.class);
		intent.setAction(AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
		context.startService(intent);
	}

	/**
	 * Pause a download
	 *
	 * @param appId appId of the download to stop
	 */
	public void pauseDownload(long appId) {
		startDownloadService(appId, AptoideDownloadManager.DOWNLOADMANAGER_ACTION_PAUSE);
	}

	public static String TAG = DownloadServiceHelper.class.getSimpleName();

	/**
	 * Starts a download. If there is a download running it is added to queue
	 *
	 * @param permissionRequest
	 * @param download Download to provide info to be able to make the download
	 *
	 * @return An observable that reports the download state
	 */
	public Observable<Download> startDownload(PermissionRequest permissionRequest, Download download) {
		return permissionManager.requestExternalStoragePermission(permissionRequest).flatMap(success -> Observable.fromCallable(() -> {
			getDownloadAsync(download.getAppId()).first().subscribe(storedDownload -> {
				startDownloadService(download.getAppId(), AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
			}, throwable -> {
				if (throwable instanceof DownloadNotFoundException) {
					final Realm realm = Database.get();
					Database.save(download, realm);
					realm.close();
					startDownloadService(download.getAppId(), AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
				} else {
					throwable.printStackTrace();
				}
			});
			return download;
		}).flatMap(aDownload -> getDownloadAsync(download.getAppId())));
	}

	public void startDownload(@NonNull PermissionRequest permissionRequest, @NonNull List<Download> downloads, Action1<Long> action) {

		permissionManager.requestExternalStoragePermission(permissionRequest)
				.concatMap(success -> Database.DownloadQ.getDownloads())
				.first()
				.subscribe(downloadsFromRealm -> {
					for (int i = 0 ; i < downloads.size() ; i++) {
						final Download download = getDownloadFromList(downloadsFromRealm, downloads.get(i));
						if (download != null) {
							Database.DownloadQ.save(download);
							startDownloadService(download.getAppId(), AptoideDownloadManager.DOWNLOADMANAGER_ACTION_START_DOWNLOAD);
							if (action != null) {
								action.call(download.getAppId());
							}
						}
					}
				}, Throwable::printStackTrace);
	}

	private Download getDownloadFromList(List<Download> downloadsFromRealm, @NonNull Download download) {
		for (int i = 0 ; i < downloadsFromRealm.size() ; i++) {
			if (downloadsFromRealm.get(i).getAppId() == download.getAppId()) {
				return downloadsFromRealm.get(i);
			}
		}
		return download;
	}

	private void startDownloadService(long appId, String action) {
		Observable.fromCallable(() -> {
			Intent intent = new Intent(Application.getContext(), DownloadService.class);
			intent.putExtra(AptoideDownloadManager.APP_ID_EXTRA, appId);
			intent.setAction(action);
			Application.getContext().startService(intent);
			return null;
		}).subscribeOn(Schedulers.computation()).subscribe(o -> {
		}, Throwable::printStackTrace);
	}

	/**
	 * Finds the download that is currently running
	 *
	 * @return an observable that reports the current download state
	 */
	public Observable<Download> getCurrentDownlaod() {
		return aptoideDownloadManager.getCurrentDownload();
	}

	/**
	 * Gets all the recorded downloads
	 *
	 * @return an observable with all downloads in database
	 */
	public Observable<List<Download>> getAllDownloads() {
		return aptoideDownloadManager.getDownloads();
	}

	/**
	 * This method finds all the downloads that are in {@link Download#IN_QUEUE} and {@link Download#PAUSED} states.
	 *
	 * @return an observable with a download list
	 */
	public Observable<List<Download>> getRunningDownloads() {
		return aptoideDownloadManager.getCurrentDownloads();
	}

	/**
	 * This method finds the download with the appId
	 *
	 * @param appId appId to the app
	 *
	 * @return an observable with the download
	 */
	public Observable<Download> getDownloadAsync(long appId) {
		return aptoideDownloadManager.getDownloadAsync(appId);
	}


	public Observable<Download> getDownload(long appId) {
		return aptoideDownloadManager.getDownload(appId);
	}

	public void removeDownload(long appId) {
		aptoideDownloadManager.removeDownload(appId);
	}
}
