package OnionHarvester;

import OnionHarvester.Presenter.IPrenesnter;
import OnionHarvester.Presenter.Service.Presenter;
import OnionHarvester.View.IView;
import OnionHarvester.View.View;

/**
 * @author Mir Saman Tajbakhsh
 */
public class OH {

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--thread":
                    Variables.ThreadCount = Integer.parseInt(args[++i]);
                    break;
                case "--time-out":
                    Variables.TimeOut = Integer.parseInt(args[++i]);
                    break;
            }
            //TODO Implement new TOR options.
        }

        IView view = new View();

        view.printMessage(
                "Welcome to Onion Harvester (Version 2.0) project.\r\n" +
                        "This project will find all the onions in TOR network.\r\n" +
                        "Check more info:\r\n\t\thttp://onionharvester.com/\r\n" +
                        "\t\thttps://mstajbakhsh.ir/projects/onion-harvester/\r\n" +
                        "\t\tContact:\rsaman@mstajbakhsh.ir");

        IPrenesnter presenter = new Presenter(view);
        presenter.present();
    }
}