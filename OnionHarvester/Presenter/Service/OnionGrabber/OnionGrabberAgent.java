package OnionHarvester.Presenter.Service.OnionGrabber;

import OnionHarvester.Presenter.Service.IService;
import OnionHarvester.Presenter.Service.concurrent.Agent;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public class OnionGrabberAgent extends Agent {

    IService _service;

    public OnionGrabberAgent(IService service) {
        super(service);
        _service = service;
    }

    @Override
    public void run() {
        //super.run();
        _service.execute();
    }

    @Override
    public void runService() {
        //super.runService();
        this.start();
    }
}
