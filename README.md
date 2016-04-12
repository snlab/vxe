# VXE: Virtual eXecution Environment for SDN Applications

In this project we implement a demo implementation of *FAST*, a data-driven
runtime system aimed to solve the consistency problem in SDN control plane.

## Team Members

[Gao, Kai](https://github.com/emiapwil) (Tsinghua University),
[Zhang, Jingxuan Jensen](https://github.com/fno2010),
[Wang, Xin Tony](https://github.com/TonyWang123), and
[Chen, Shenshen](https://github.com/css9091) (Tongji University)

All developers are members of the [SNLab](http://snlab.github.io/), led by
[Yang, Yang Richard](https://github.com/yryang).

## Problem Statement

The abstraction of routing in the networking system, both traditional or in the
context of *Softwared-Defined Networking* (SDN), can be modelled as a simple
function *f*.  From a data-plane point of view, this function *f* takes the
packet attributes as variables where operations, like **match**, **foward**,
etc., can be conducted.  If we think of the control plane, which takes the
running state of the network and user configurations to calculate the routing,
as a function *F*, the function *N* of the networking system can be seen as:

![Network Function](https://raw.githubusercontent.com/snlab/vxe-onug-hackathon-2016/master/doc/figures/network-function.png)

> f(packet) = (F(state, configuration))(packet) = N(state, configuration, packet)

Almost every network applications, the shortest-path routing algorithm for
example, depend on certain network states and/or user configurations.  When the
dependent data change, the result of the applications become invalid.  Thus, it
is important to guarantee that the results of the applications are consistent
with the current network-related data.

Modern SDN controllers, such as Onix, OpenDaylight and ONOS, have come with the
abstraction of *datastores* which provides low-level interfaces for applications
to access the data and get notifications of changes.  Unfortunately, currently
these systems put on the programmers the burden of identifying dependent data
and monitoring their changes.  A more dangerous scenario is that applications
form close loops, which puts the network in an inconsistent state.

Meanwhile, as the networking system has a high demand on the performance,
techniques such as asynchronous I/O are widely used, which also introduces
complexities to the SDN programming.

## Our Long-term Goal

The long-term goal of our team, from a high-level perspective, is to solve the
problem of consistency in the control plane and to simplify the SDN programming
with an automatic, data-aware, and efficient runtime system.

## Challenges

1.  **Automatically identify dependent data**

    The first step to simplify the SDN programming, the runtime must be able to
    extract the dependent data automatically and release the burden from the
    programmers.  The runtime should also be able to identify fine-grained data
    dependencies to reduce false-positive data changes.

1.  **Automatically handle data changes**

    When a data change happens, the results of an application becomes invalid.
    The runtime must handle the data changes automatically and thus save the
    programmers from the extra complexity of cleaning up.

1.  **Handle asynchronous operations internally**

    The runtime must be able to take advantage of the asynchronous operations to
    achieve better performance and to make it transparent for programmers.

1.  **Efficiently execute the applications**

    Applications can depend on the results of some other applications.  The
    runtime system must be able to identify the *application dependencies* to
    avoid unnecessary executions.

## Our Solutions

1.  In the demo we use proxies to record the data that are accessed by
    applications.  To achieve fine-grained data dependencies, we plan to use
    bytecode instrumentation and conduct information flow analysis.

1.  In the demo we take advantage of the OpenDaylight datastore API.  However,
    its notification mechanism has certain constraints and the functionalities
    are compromised.  We plan to implement a specialized datastore for the
    framework in the future.

1.  In the demo we don't take advantage of the asynchronicity.  The future plan
    is to use bytecode instrumentation to generate callback functions
    automatically.

1.  Multiple applications, also referred to as *function instances* or
    *tasklets* in the demo, are not demonstrated in this demo.  We have
    introduced the concepts of *function instance store* to manage all the
    function instances, constructing the dependency graph between different
    applications and scheduling the execution order.


## Hackathon Project

### Preliminaries

We will provide a VM with all the dependencies.

### Demo Application

The source code for the application that will be executed can be found [here](https://github.com/snlab/vxe-onug-hackathon-2016/blob/master/demo/opendaylight-demo/impl/src/main/java/org/snlab/vxe/demo/opendaylight/impl/application/VxeDemoTasklet.java) and the steps required to launch the instance is demonstrated [here](https://github.com/snlab/vxe-onug-hackathon-2016/blob/master/demo/opendaylight-demo/impl/src/main/java/org/snlab/vxe/demo/opendaylight/impl/VxeOpenDaylightDemoProvider.java#110).

Basically the application sets up a path for two end hosts, which is quite
similar to the `HostToHostIntent` in ONOS.  For simplicity, we use only MAC
addresses to match the flows and the unit-cost shortest path algorithm to find
the path.

As we can see from the source code, the class is annotated with `\@VxeTasklet`
and the `findPath` method is annotated with `\@VxeEntryPoint`.  These indicate
that this class can be used in VXE as a function instance and the `findPath`
method will be invoked when the function instance is submitted.

### Network Topology

TBD

### The demonstration

1.  **Launch the OpenDaylight controller**

    Execute the following commands to start the OpenDaylight controller:

    ~~~
    pushd demo/opendaylight-demo
    karaf/target/assembly/bin/karaf
    popd
    ~~~

    Wait until `[VXE demo loaded]` appears in the karaf console, which indicates
    that the features required for the demo are loaded.

1.  **Start mininet**

    TBD

1.  **Create the request**

    TBD

    Errors will be dumped to the karaf console, indicating that the execution
    has failed.

1.  **Test the connectivities**

    Execute `hostn0 ping hostn1` in mininet CLI.  This command will send ICMP
    message from hostn0 to hostn1 and their MAC addresses will be learnt.  The
    topology will be updated and these data changes will trigger the function
    instance to be re-executed.  The path will be dumped in karaf console and
    we can see that the connection between hostn0 to hostn1 has be set up.

1.  **Simulate link failures**

    Execute `link n0 n1 down` in mininet CLI.  This command will change the
    status of the corresponding link to `DOWN`.  It will take a little while
    before OpenDaylight can update the topology and once it does, the new path
    will be dumped to the karaf console.

    Use `hostn0 ping hostn1` to test the connection between hostn0 to hostn1 is
    restored.

1.  **Simulate link restoration**

    Execute `link n0 n1 up` in mininet CLI which will bring up the link.

    The restored path will be dumped in the karaf console.
