package cm.aptoide.pt.shareapps.socket.message.messages;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.Message;
import lombok.Getter;

/**
 * Created by neuro on 14-02-2017.
 */

public class HostLeftMessage extends Message {

  @Getter private final Host hostThatLeft;

  public HostLeftMessage(Host localhost, Host hostThatLeft) {
    super(localhost);
    this.hostThatLeft = hostThatLeft;
  }
}