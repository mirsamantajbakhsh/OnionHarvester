OnionHarvester
==============

This is the Python version of [OnionHarvester](https://github.com/mirsamantajbakhsh/OnionHarvester) (Mir Saman Tajbakhsh) based on [OWASP Nettacker](https://github.com/viraintel/OWASP-Nettacker) framework. ([license](https://github.com/mirsamantajbakhsh/OnionHarvester/blob/master/LICENSE))

#### usage
* make you sure `PySocks` in installed (`pip install PySocks`)
* `git clone https://github.com/viraintel/owasp-nettacker`
* goto OWASP Nettacker libraries directory `cd owasp-nettacker/lib/scan`
* create a directory and rename it to `onionharvester`
* go to directory you created and copy `engine.py` into it.
* create `__init__.py` file in the `onionharvester` directory and copy these contents and save it.
```python
#!/usr/bin/env python
# -*- coding: utf-8 -*-
pass
```
* now you are able to run it with OWASP Nettacker framework. `python nettacker.py -m onion_harvester_scan -i something.onion -v 5` (it starts from `aaaaaaaaaaaaaaaa.onion`)



### next steps
* improve multi-threading, speed and resources management
* define where to start
* define where to stop
* ...?


Please read OWASP Nettacker [wiki](https://github.com/viraintel/OWASP-Nettacker/wiki) for more information.