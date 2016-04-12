#!/usr/bin/env python2.7

import sys

from mininet.net import Mininet
from mininet.topo import Topo
from mininet.link import TCLink
from mininet.node import RemoteController
from mininet.cli import CLI
from mininet.log import MininetLogger

def main():
    logger = MininetLogger()
    logger.setLogLevel(levelname='info')

    controller_ip = sys.argv[2]

    topo = Topo()
    n = int(sys.argv[1])

    switches = [topo.addSwitch('s%d' % (i+1), protocols='OpenFlow13') for i in range(n)]
    host1 = topo.addHost('h%d' % 1, mac="12:34:56:78:00:01")
    host2 = topo.addHost('h%d' % 2, mac="12:34:56:78:00:02")
    hosts = [host1, host2]

    for i in [0, 1]:
        topo.addLink(hosts[i], switches[i])

    for i in range(n):
        topo.addLink(switches[i], switches[(i+1) % n])

    net = Mininet(topo=topo, controller=RemoteController, link=TCLink, build=False)
    net.addController(ip=controller_ip)

    net.start()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "Usage: ./demonetwork.py [NUMBER_OF_SWITCH] [CONTROLLER_ADDR]"
        sys.exit()

    main()
