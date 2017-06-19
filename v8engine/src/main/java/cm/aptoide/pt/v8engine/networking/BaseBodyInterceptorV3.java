package cm.aptoide.pt.v8engine.networking;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.account.FacebookAccount;
import cm.aptoide.pt.v8engine.account.GoogleAccount;
import rx.Single;
import rx.schedulers.Schedulers;

public class BaseBodyInterceptorV3 implements BodyInterceptor<BaseBody> {

  public static final String RESPONSE_MODE_JSON = "json";

  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final IdsRepository idsRepository;
  private final AptoideAccountManager accountManager;
  private final QManager qManager;
  private final SharedPreferences sharedPreferences;
  private final String responseMode;

  public BaseBodyInterceptorV3(IdsRepository idsRepository, String aptoideMd5sum,
      String aptoidePackage, AptoideAccountManager accountManager, QManager qManager,
      SharedPreferences sharedPreferences, String responseMode) {
    this.idsRepository = idsRepository;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.accountManager = accountManager;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
    this.responseMode = responseMode;
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .map(account -> {
          body.setAptoideMd5sum(aptoideMd5sum);
          body.setAptoidePackage(aptoidePackage);
          body.setAptoideUid(idsRepository.getUniqueIdentifier());
          body.setQ(qManager.getFilters(ManagerPreferences.getHWSpecsFilter(sharedPreferences)));
          body.setResponseMode(responseMode);
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getAccessToken());
            body.setAuthMode(getAuthModeFromAccount(account));
          }
          return body;
        })
        .subscribeOn(Schedulers.computation());
  }

  private String getAuthModeFromAccount(Account account) {
    if (account instanceof GoogleAccount) {
      return "google";
    } else if (account instanceof FacebookAccount) {
      return "facebook";
    }
    return "aptoide";
  }
}
