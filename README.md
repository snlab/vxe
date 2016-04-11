# VXE: SDN Function Instance Store as a Mechanism to Automatically Implement Intents

Team members (Kai Gao, Jensen Zhang, Tony Wang, Shenshen Chen) of SNLAB implemented VXE, a tool to provide a Virtual eXecution Environment for building function-instance-based system like [FAST](), and a demo system by using VXE during the [ONUG Grand Challenge Hackathon](https://opennetworkingusergroup.com/onug-grand-challenge-hackathon/).

## Problem and Goal

Data-dependency is a basic property of many network control-plane functions. For example, basic routing such as shortest-path routing depends on network topology data. Going beyond basic routing, QoS routing depends on not only network topology data but also network resource allocation data. Going beyond routing, security functions (e.g., ACL, group based policy) depend heavily on configured security policy data. We refer to a generic data-dependent control-plane function as *f*, and a network control-plane may consist of a large number of such functions.

A key property of a data-dependent control function *f* is that if there is a change to its dependent data, the output of *f* using the old data may become invalid. Hence, it is essential that the output of *f* based on the new data be computed; otherwise, basic network services or security can be compromised. We refer to the problem of efficient, correct executions of data-dependent, generic network control functions in the presence of dependent data changes as the *efficient, correct control execution problem*.

Emerging network operating systems (e.g. OpenDaylight, ONOS) recognize this fundamental issue but their solutions using data stores are low level and complex. Furthermore, these systems burden the programmer with handling the programming complexity of identifying dependent data and monitoring their changes, a roadblock to simple and flexible control plane design.

Therefore, our goal is to develop a Virtual eXecution Environment (VXE) which automatically handles data dependency tracking and re-execution for the control-plane function. Through the APIs provided by VXE, programmers can write the control-plane function regardless of the complexity of identifying dependent data and monitoring their changes. Moreover, to show the benefits of VXE, we develop a host-host intent writen by using VXE APIs.

## Challenges

1. Automatic extraction of dependent data In order to release the burden of identifying dependent data and then monitoring their changes from programmers, the VXE system must have a method to extract dependent data from control-plane functions automatically. 
2. Automatic handling data changes The second challenge is to handle the data changes automatically. Since the output of a data-dependent control function would be invalid if the dependent data changes, the VXE system must handle the data change event through the function doesn't define the onDataChange function explicitly.

## Solution Overview

### Architecture

### Features

- Automatic extraction of dependent data <!--(why it is hard, what is clever; small amount of false positives)-->
- Automatic registration of listeners
- Automatic reexecution

## Hackathon Project

### Prerequisites

### VXE System

### VXE Library

## How to Run the Demo

Switch to the `demo` directory and execute:

~~~
mvn clean install exec:exec
~~~

> **Tips**
> 
> 1. Run `mvn checkstyle:check` at the root directory before pushing your changes.
