#!/usr/bin/env bash

# Setup Mininet
sudo apt-get update
sudo apt-get install -y git
if [ ! -d /home/vagrant/mininet ]; then
    sudo -u vagrant git clone git://github.com/mininet/mininet
fi
/home/vagrant/mininet/util/install.sh -a

# Update Maven to 3.3.x
wget http://mirror.stjschools.org/public/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
cd /home/vagrant/bin
tar xvf ../apache-maven-3.3.9-bin.tar.gz
mv apache-maven apache-maven-old
mv apache-maven-3.3.9 apache-maven

# Setup Development Environment
if [ ! -d /home/vagrant/.m2 ]; then
    sudo -u vagrant mkdir -p /home/vagrant/.m2
fi
if [ -x /home/vagrant/.m2/settings.xml ]; then
    sudo -u vagrant cp -n /home/vagrant/.m2/settings.xml{,.orig}
fi
sudo -u vagrant wget -q -O /home/vagrant/.m2/settings.xml https://raw.githubusercontent.com/opendaylight/odlparent/master/settings.xml

# Build VXE Demo
cd /home/vagrant/vxe
MAVEN_HOME="/home/vagrant/bin/apache-maven" sudo -u vagrant /home/vagrant/bin/mvn clean install -DskipTests
sudo -u vagrant ln -s /home/vagrant/bin/vxe /home/vagrant/vxe/karaf/target/assemble/bin/karaf
