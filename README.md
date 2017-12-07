# OnionHarvester
![Onion Harvester](https://mstajbakhsh.ir/wp-content/uploads/2017/11/Spy-Services.jpg)
A small TOR Onion Address harvester for checking if the address is available or not. The program uses the TOR local socks5 proxy for finding all the Onion Addresses which are alive and ports 80 and 443 are available.
Details about the project and TOR onion harvesting is published in my personal blog at: [Onion Harvester]

## Compile and Use
Just get the two java files and compile them inside your Java IDE. Java 8 is prefered (JDK 1.8). 

## Switches
* --ip: Sets the IP address for local TOR socks5 proxy. (Ex: 127.0.0.1)
* --port: Sets the Port for local TOR socks5 proxy. (Ex: 9150)
* --start: Sets the first Onion address to start. Can be used for resuming. (Ex: aaaaaaaaaaaaaaaa without **.onion** at the end of address)
* --thread: Sets the number of threads to harvest concurrently. (Ex: 20)
* --time-out: The timeout time for reaching each Onion address in milliseconds (Ex: 5000 means 5 seconds).

## Defaults
If you did not set the switched, you can use the Onion Harvester with its default settings. Default value for each switch is here:

| Switch | Default Value |
| ------ | ------ |
| --ip | 127.0.0.1 |
| --port | 9150 |
| --start | aaaaaaaaaaaaaaaa |
| --thread | 10 |
| --time-out | 5000 (5 seconds) |

If you want to help in harvesting, contact me at saman \[@\] mstajbakhsh \[.\] ir

[Onion Harvester]: <https://mstajbakhsh.ir/onion-harvester-first-step-tor-search-engines>

# Python Version
You may check the python version [readme file](https://github.com/mirsamantajbakhsh/OnionHarvester/blob/master/python_version_readme.md) to use the Python version based on [OWASP Nettacker](https://github.com/viraintel/OWASP-Nettacker) module.