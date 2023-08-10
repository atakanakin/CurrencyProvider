// IIPCExample.aidl
package com.atakan.mainserver;

// Declare any non-default types here with import statements

interface IIPCExample {
    /** Request the process ID of this service */
    int getPid();

    /** Count of received connection requests from clients */
    int getConnectionCount();

    /** Set displayed value of screen */
    void postVal(String packageName, int pid, String clientCurr1, String clientCurr2, String clientCurr3, double clientRate1, double clientRate2, double clientRate3, String time);

}