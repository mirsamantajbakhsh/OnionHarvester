#!/usr/bin/env python
# -*- coding: utf-8 -*-

import socket
import socks
import time
import requests
import json
import threading
import string
import os
import itertools
from core.alert import *
from core.targets import target_type


def extra_requirements_dict():
    return {
        "onion_harvester_scan_tor_ip": ["127.0.0.1"],
        "onion_harvester_scan_tor_port": ["9050"],
        "onion_harvester_scan_ports": ["80"],
        "onion_harvester_scan_domain_chars": ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                                              'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5',
                                              '6', '7'],
        "onion_harvester_scan_stop": [""],
    }


def getaddrinfo(*args):
    return [(socket.AF_INET, socket.SOCK_STREAM, 6, '', (args[0], args[1]))]


def generate_domains(chars):
    chars = ''.join([str(x) for x in chars])
    for x in itertools.product(chars, repeat=16):
        yield 'http://' + ''.join([str(y) for y in x]) + '.onion'


def connect(host, port, timeout_sec, log_in_file, language, time_sleep, thread_tmp_filename, tor_ip, tor_port):
    _HOST = messages(language, 53)
    _USERNAME = messages(language, 54)
    _PASSWORD = messages(language, 55)
    _PORT = messages(language, 56)
    _TYPE = messages(language, 57)
    _DESCRIPTION = messages(language, 58)
    time.sleep(time_sleep)
    try:
        socks.set_default_proxy(socks.SOCKS5, str(tor_ip), int(tor_port))
        socket.socket = socks.socksocket
        socket.getaddrinfo = getaddrinfo
        user_agent = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, '
                                    'like Gecko) Chrome/62.0.3202.94 Safari/537.36'}

        r = requests.get(host, timeout=timeout_sec, headers=user_agent, verify=True)
        info(messages(language, 80).format(host, port))
        save = open(log_in_file, 'a')
        save.write(json.dumps({_HOST: host, _USERNAME: '', _PASSWORD: '', _PORT: port, _TYPE: 'onion_harvester_scan',
                               _DESCRIPTION: messages(language, 79)}) + '\n')
        save.close()
        thread_write = open(thread_tmp_filename, 'w')
        thread_write.write('0')
        thread_write.close()
        return True
    except:
        pass


def start(target, users, passwds, ports, timeout_sec, thread_number, num, total, log_in_file, time_sleep,
          language, verbose_level, show_version, check_update, proxies, retries, ping_flag,
          methods_args):  # Main function
    if target_type(target) == 'DOMAIN':
        # requirements check
        new_extra_requirements = extra_requirements_dict()
        if methods_args is not None:
            for extra_requirement in extra_requirements_dict():
                if extra_requirement in methods_args:
                    new_extra_requirements[extra_requirement] = methods_args[extra_requirement]
        extra_requirements = new_extra_requirements
        if ports is None:
            ports = extra_requirements["onion_harvester_scan_ports"]
        threads = []
        max = thread_number
        total_req = int(len(ports) * 1208925819614629174706176)
        thread_tmp_filename = 'tmp/thread_tmp_' + ''.join(
            random.choice(string.ascii_letters + string.digits) for _ in range(20))
        thread_write = open(thread_tmp_filename, 'w')
        thread_write.write('1')
        thread_write.close()
        trying = 0
        for target in generate_domains(extra_requirements["onion_harvester_scan_domain_chars"]):
            for port in ports:
                port = int(port)
                t = threading.Thread(target=connect,
                                     args=(target, int(port), timeout_sec, log_in_file, language, time_sleep,
                                           thread_tmp_filename, extra_requirements["onion_harvester_scan_tor_ip"][0],
                                           extra_requirements["onion_harvester_scan_tor_port"][0]))
                threads.append(t)
                t.start()
                trying += 1
                if verbose_level is not 0:
                    info(messages(language, 72).format(trying, total_req, num, total, target, port))
                while 1:
                    try:
                        n = 0
                        for thread in threads:
                            if thread.isAlive() is True:
                                n += 1
                            else:
                                threads.remove(thread)
                        if n >= max:
                            time.sleep(0.01)
                        else:
                            break
                    except KeyboardInterrupt:
                        break
                        break
                        break
        # wait for threads
        while 1:
            try:
                n = True
                for thread in threads:
                    if thread.isAlive() is True:
                        n = False
                time.sleep(0.01)
                if n is True:
                    break
            except KeyboardInterrupt:
                break
                break
        thread_write = int(open(thread_tmp_filename).read().rsplit()[0])
        if thread_write is 1 and verbose_level is not 0:
            _HOST = messages(language, 53)
            _USERNAME = messages(language, 54)
            _PASSWORD = messages(language, 55)
            _PORT = messages(language, 56)
            _TYPE = messages(language, 57)
            _DESCRIPTION = messages(language, 58)
            save = open(log_in_file, 'a')
            save.write(
                json.dumps({_HOST: target, _USERNAME: '', _PASSWORD: '', _PORT: '', _TYPE: 'onion_harvester_scan',
                            _DESCRIPTION: 'onion domain found!'}) + '\n')
            save.close()
        os.remove(thread_tmp_filename)

    else:
        warn('input must be .onion domain (example: aaaaaaaaaaaaaaaa.onion)')
