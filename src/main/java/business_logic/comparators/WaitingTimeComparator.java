package business_logic.comparators;

import model.Server;

import java.util.Comparator;

public class WaitingTimeComparator implements Comparator<Server> {
    @Override
    public int compare(Server server1, Server server2) {
        return server1.getWaitingPeriod().intValue() - server2.getWaitingPeriod().intValue();
    }
}
