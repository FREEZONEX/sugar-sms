# sugar-sms
The sugar-sms, as an external alarm message service of supos, can promptly notify the industrial alarm information collected by supos to the customers.

The general architecture design is illustrated as follows.

Based on the design pattern of the Disruptor high-performance queue and message table, the sugar-sms can be implemented as a lightweight service to achieve high concurrency and ensure that each message is neither over-sent nor under-sent.

![dashboard](/assets/dashboard.png)
![system](/assets/system.png "Magic Gardens")
