package OnionHarvester.Presenter.Service;

import OnionHarvester.Presenter.IPrenesnter;
import OnionHarvester.Presenter.Service.OnionGrabber.OnionGrabber;
import OnionHarvester.Presenter.Service.OnionGrabber.OnionGrabberAgent;
import OnionHarvester.Presenter.Service.concurrent.Agent;
import OnionHarvester.Variables;
import OnionHarvester.View.IView;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public class Presenter implements IPrenesnter {
    IView _view;

    public Presenter(IView view) {
        this._view = view;
    }

    @Override
    public void present() {
        start();
    }

    private void start() {
        for (int i = 0; i < Variables.ThreadCount; i++) {
            System.out.println("STARTING TOR #" + (i + 1));
            Agent oga = new OnionGrabberAgent(new OnionGrabber());
            oga.setName("Thread" + (i + 1));
            oga.runService();
        }
    }

    private IView getView() {
        return _view;
    }
}
