package OnionHarvester.Presenter.Service.concurrent;

import OnionHarvester.Presenter.Service.IService;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public class Agent extends Thread {

    private IService service;

    public Agent(IService service) {
        this.service = service;
    }

    public void runService() {
        service.execute();
    }

    public void setThreadName(String Name) {
        setName(Name);
    }
}
