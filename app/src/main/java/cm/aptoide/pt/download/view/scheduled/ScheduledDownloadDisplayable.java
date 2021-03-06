/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.download.view.scheduled;

import cm.aptoide.pt.R;
import cm.aptoide.pt.database.accessors.ScheduledAccessor;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.recycler.displayable.SelectableDisplayablePojo;
import rx.Observable;

/**
 * Created
 */
public class ScheduledDownloadDisplayable extends SelectableDisplayablePojo<Scheduled> {

  private static final String TAG = ScheduledDownloadDisplayable.class.getSimpleName();
  private InstallManager installManager;

  public ScheduledDownloadDisplayable() {
  }

  public ScheduledDownloadDisplayable(Scheduled pojo, InstallManager installManager) {
    super(pojo);
    this.installManager = installManager;
  }

  public InstallManager getInstallManager() {
    return installManager;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_scheduled_download_row;
  }

  public void removeFromDatabase(ScheduledAccessor accessor) {
    accessor.delete(getPojo().getMd5());
  }

  public Observable<Boolean> isDownloading() {
    return installManager.getInstall(getPojo().getMd5(), getPojo().getPackageName(),
        getPojo().getVerCode())
        .map(installationProgress -> installationProgress.getState()
            == Install.InstallationStatus.INSTALLING
            || installationProgress.getState() == Install.InstallationStatus.IN_QUEUE);
  }
}
