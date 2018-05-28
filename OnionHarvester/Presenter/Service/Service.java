package OnionHarvester.Presenter.Service;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public abstract class Service implements IService {
    @Override
    public void execute() {
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public void setParam(String paramName, Object paramValue) {

    }
}
