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

    controller_ip = sys.argv[1]

    topo = Topo()

    switches = [topo.addSwitch('s%d' % i, protocols='OpenFlow13') for i in range(4)]
    hosts = [topo.addHost('h%d' % i) for i in range(4)]

    for i in range(4):
        topo.addLink(hosts[i], switches[i])

    for i in [0, 1]:
        for j in [2, 3]:
            topo.addLink(switches[i], switches[j])

    net = Mininet(topo=topo, controller=RemoteController, link=TCLink, build=False)
    net.addController(ip=controller_ip)

    net.start()
    CLI(net)
    net.stop()

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print "Usage: ./demonetwork.py [CONTROLLER_ADDR]"
        sys.exit()

    main()
