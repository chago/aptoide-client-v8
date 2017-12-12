package cm.aptoide.pt.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.ErrorsMapper;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.ImagePickerNavigator;
import cm.aptoide.pt.account.view.ImagePickerPresenter;
import cm.aptoide.pt.account.view.ImagePickerView;
import cm.aptoide.pt.account.view.ImageValidator;
import cm.aptoide.pt.account.view.MyAccountNavigator;
import cm.aptoide.pt.account.view.MyAccountPresenter;
import cm.aptoide.pt.account.view.MyAccountView;
import cm.aptoide.pt.account.view.PhotoFileGenerator;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.account.view.store.ManageStoreErrorMapper;
import cm.aptoide.pt.account.view.store.ManageStoreNavigator;
import cm.aptoide.pt.account.view.store.ManageStorePresenter;
import cm.aptoide.pt.account.view.store.ManageStoreView;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.account.view.user.CreateUserErrorMapper;
import cm.aptoide.pt.account.view.user.ManageUserNavigator;
import cm.aptoide.pt.account.view.user.ManageUserPresenter;
import cm.aptoide.pt.account.view.user.ManageUserView;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.permission.AccountPermissionProvider;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsPresenter;
import cm.aptoide.pt.presenter.LoginSignUpCredentialsView;
import com.facebook.appevents.AppEventsLogger;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import javax.inject.Named;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jose_messejana on 29-11-2017.
 */

@Module public class FragmentModuleTest{
  private final Fragment fragment;
  private final Bundle savedInstance;
  private final boolean dismissToNavigateToMainView;
  private final boolean navigateToHome;
  private final boolean goToHome;
  private final boolean isEditProfile;
  private final boolean isCreateStoreUserPrivacyEnabled;
  private final String packageName;


  public FragmentModuleTest(Fragment fragment, Bundle savedInstance, boolean dismissToNavigateToMainView, boolean navigateToHome, boolean goToHome,
      boolean isEditProfile, boolean isCreateStoreUserPrivacyEnabled, String packageName) {
    this.fragment = fragment;
    this.savedInstance = savedInstance;
    this.dismissToNavigateToMainView = dismissToNavigateToMainView;
    this.navigateToHome = navigateToHome;
    this.goToHome = goToHome;
    this.isEditProfile = isEditProfile;
    this.isCreateStoreUserPrivacyEnabled = isCreateStoreUserPrivacyEnabled;
    this.packageName = packageName;
  }

  @FragmentScopeTest @Provides LoginSignUpCredentialsPresenter provideLoginSignUpPresenter(AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountErrorMapper errorMapper, AccountAnalytics accountAnalytics){
    return new LoginSignUpCredentialsPresenter((LoginSignUpCredentialsView) fragment, accountManager, CrashReport.getInstance(),
        dismissToNavigateToMainView, navigateToHome, accountNavigator,
        Arrays.asList("email", "user_friends"), Arrays.asList("email"), errorMapper,
        accountAnalytics);
  }

  @FragmentScopeTest @Provides ImagePickerPresenter provideImagePickerPresenter(AccountPermissionProvider accountPermissionProvider,
      PhotoFileGenerator photoFileGenerator, ImageValidator imageValidator, UriToPathResolver uriToPathResolver, ImagePickerNavigator imagePickerNavigator){
    ImagePickerView view = (ImagePickerView) fragment;
    CrashReport crashReport = CrashReport.getInstance();
    return new ImagePickerPresenter(view, crashReport, accountPermissionProvider, photoFileGenerator,
        imageValidator, AndroidSchedulers.mainThread(), uriToPathResolver, imagePickerNavigator,
        fragment.getActivity().getContentResolver(), ImageLoader.with(fragment.getContext()));
  }

  @FragmentScopeTest @Provides
  ManageStorePresenter provideManageStorePresenter(StoreManager storeManager, UriToPathResolver uriToPathResolver,
      ManageStoreNavigator manageStoreNavigator, ManageStoreErrorMapper manageStoreErrorMapper){
    return new ManageStorePresenter((ManageStoreView) fragment, CrashReport.getInstance(), storeManager, uriToPathResolver,
        packageName, manageStoreNavigator, goToHome, manageStoreErrorMapper);
  }

  @FragmentScopeTest @Provides ManageUserPresenter provideManageUserPresenter(AptoideAccountManager accountManager, CreateUserErrorMapper errorMapper,
      ManageUserNavigator manageUserNavigator, UriToPathResolver uriToPathResolver){
    return new ManageUserPresenter((ManageUserView) fragment, CrashReport.getInstance(), accountManager, errorMapper, manageUserNavigator,
        isEditProfile, uriToPathResolver, isCreateStoreUserPrivacyEnabled, savedInstance == null);
  }

  @FragmentScopeTest @Provides MyAccountPresenter provideMyAccountPresenter(AptoideAccountManager accountManager,MyAccountNavigator myAccountNavigator,
      @Named("default")
          SharedPreferences defaultSharedPreferences, NavigationTracker navigationTracker, NotificationAnalytics notificationAnalytics){
    AptoideApplication aptoideApplication = (AptoideApplication) fragment.getContext().getApplicationContext();
    return new MyAccountPresenter((MyAccountView) fragment, accountManager, CrashReport.getInstance(),
        myAccountNavigator, aptoideApplication.getNotificationCenter(), defaultSharedPreferences,
        navigationTracker, notificationAnalytics,
        new PageViewsAnalytics(AppEventsLogger.newLogger(fragment.getContext().getApplicationContext()),
            Analytics.getInstance(), navigationTracker));
  }

  @FragmentScopeTest @Provides ImageValidator provideImageValidator(){
    return new ImageValidator(ImageLoader.with(fragment.getContext()), Schedulers.computation());
  }

  @FragmentScopeTest @Provides CreateUserErrorMapper provideCreateUserErrorMapper(AccountErrorMapper accountErrorMapper){
    return new CreateUserErrorMapper(fragment.getContext(), accountErrorMapper, fragment.getResources());
  }

  @FragmentScopeTest @Provides AccountErrorMapper provideAccountErrorMapper(){
    return new AccountErrorMapper(fragment.getContext(), new ErrorsMapper());
  }

  @FragmentScopeTest @Provides ManageStoreErrorMapper provideManageStoreErrorMapper(){
    return new ManageStoreErrorMapper(fragment.getResources(), new ErrorsMapper());
  }
}
