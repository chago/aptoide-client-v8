package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.v7.store.ListStores;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 11/30/16.
 */

@Data @EqualsAndHashCode(callSuper = true) public class MyStore extends BaseV7Response {

  ListStores stores;
}
