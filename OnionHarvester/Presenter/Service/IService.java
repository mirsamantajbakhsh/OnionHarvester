package OnionHarvester.Presenter.Service;

/**
 * Created by Mir Saman on 22-Feb-18.
 */
public interface IService {
    void execute();

    void init();

    void setParam(String paramName, Object paramValue);
}
