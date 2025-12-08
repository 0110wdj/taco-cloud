package tacos.messaging;

import tacos.TacoOrder;

public interface OrderReceiver {
    TacoOrder receiveOrder();
}
