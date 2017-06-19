/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.networkclient.util.HashMapNotNull;

public class BaseBody extends HashMapNotNull<String, Object> {

  public void setAptoideMd5sum(String aptoideMd5sum) {
    put("aptoide_md5sum", aptoideMd5sum);
  }

  public void setAptoidePackage(String aptoidePackage) {
    put("aptoide_package", aptoidePackage);
  }

  public void setResponseMode(String mode) {
    put("mode", mode);
  }

  public void setAuthMode(String authMode) {
    put("authMode", authMode);
  }

  public void setAccessToken(String accessToken) {
    put("access_token", accessToken);
    put("oauthToken", accessToken);
  }

  public void setAptoideUid(String aptoideUid) {
    put("aptoide_uid", aptoideUid);
  }

  public void setQ(String q) {
    put("q", q);
  }
}
