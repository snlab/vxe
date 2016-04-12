# VXE: Virtual eXecution Environment for SDN Applications

In this project we implement a demo implementation of *FAST*, a data-driven
runtime system aimed to solve the consistency problem in SDN control plane.

## Team members

[Gao, Kai](https://github.com/emiapwil) (Tsinghua University),
[Zhang, Jingxuan Jensen](https://github.com/fno2010),
[Wang, Xin Tony](https://github.com/TonyWang123),
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
to access the data and get notifications of changes.  Unfortunately, current
systems put on the programmers the burden of identifying dependent data and
monitoring their changes.  A more dangerous scenario is that applications form
close loops, which puts the network in an inconsistent state.

Meanwhile, as the networking system has a high demand on the performance,
techniques such as asynchronous I/O are widely used, which also introduces
complexities to the SDN programming.

## Our Long-term Goal

The long-term goal of our team, from a high-level perspective, is to solve the
problem of consistency in the control plane and to simplify the SDN programming
with an automatic, data-aware, and efficient runtime system.

## Challenges

1.  *Automatically identify dependent data*

    The first step to simplify the SDN programming, the runtime must be able to
    extract the dependent data automatically and release the burden from the
    programmers.  The runtime should also be able to identify fine-grained data
    dependencies to reduce false-positive data changes.

1.  *Automatically handle data changes*

    When a data change happens, the results of an application becomes invalid.
    The runtime must handle the data changes automatically and thus save the
    programmers from the extra complexity of cleaning up.

1.  *Handle asynchronous operations internally*

    The runtime must be able to take advantage of the asynchronous operations to
    achieve better performance and to make it transparent for programmers.

1.  *Efficiently execute the applications*

    Applications can depend on the results of some other applications.  The
    runtime system must be able to identify the *application dependencies* to
    avoid unnecessary executions.

## Solutions

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

1.  Multiple applications, or as we call them, *function instances*, are not
    demonstrated in this demo.  We have introduced the concepts of *function
    instance store* to manage all the function instances, constructing the
    dependency graph between different applications and scheduling the execution
    order.


## Hackathon Project


