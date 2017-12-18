/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationsRequest;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationMapperV7 {

  private final AuthorizationFactory authorizationFactory;
  private final BillingIdManager billingIdManager;

  public AuthorizationMapperV7(AuthorizationFactory authorizationFactory,
      BillingIdManager billingIdManager) {
    this.authorizationFactory = authorizationFactory;
    this.billingIdManager = billingIdManager;
  }

  public Authorization map(GetAuthorizationsRequest.ResponseBody.Data.Authorization response,
      String transactionId) {

    Price price = null;
    if (response.getPrice() != null) {
      price = new Price(response.getPrice()
          .getAmount(), response.getPrice()
          .getCurrency(), response.getPrice()
          .getCurrencySymbol());
    }

    final GetAuthorizationsRequest.ResponseBody.Data.Authorization.Metadata metadata = response.getData();
    String description = null;
    String session = null;
    if (metadata != null) {
      description = metadata.getDescription();
      session = metadata.getSession();
    }

    return authorizationFactory.create(billingIdManager.generateAuthorizationId(response.getId()),
        String.valueOf(response.getUser()
            .getId()), response.getType(), Authorization.Status.valueOf(response.getStatus()), null,
        price, description, transactionId, session);
  }

  public List<Authorization> map(
      List<GetAuthorizationsRequest.ResponseBody.Data.Authorization> authorizations) {
    final List<Authorization> result = new ArrayList<>();

    for (GetAuthorizationsRequest.ResponseBody.Data.Authorization authorization : authorizations) {
      result.add(map(authorization, null));
    }
    return result;
  }
}