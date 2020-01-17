# Solactive Code Challenge - Martin Percossi

## Requirements

- java 11 or higher (wouldn't be hard to reduce this to 1.8 or similar)
- maven (I used 3.8.1)

## Compiling

The following commands compiles and executes the tests.

```bash
~/solactivecodechallenge$ mvn clean package

...

Running com.solactive.codechallenge.SingleStockStatsCalculatorTest
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 22.108 sec

Results :

Tests run: 15, Failures: 0, Errors: 0, Skipped: 0

[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ solactivecodechallenge ---
[INFO] Building jar: /mnt/d/IdeaProjects/solactivecodechallenge/target/solactivecodechallenge-1.0-SNAPSHOT.jar
[INFO]
[INFO] --- maven-assembly-plugin:3.2.0:single (make-assembly) @ solactivecodechallenge ---
[INFO] Building jar: /mnt/d/IdeaProjects/solactivecodechallenge/target/solactivecodechallenge-1.0-SNAPSHOT-jar-with-dependencies.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:22 min
[INFO] Finished at: 2020-01-17T11:07:08Z
[INFO] ------------------------------------------------------------------------
~/solactivecodechallenge$ ls target/*-with-dependencies.jar
target/solactivecodechallenge-1.0-SNAPSHOT-jar-with-dependencies.jar
```

As you can see above, the resulting jar (with bundled dependencies) is stored in `target/solactivecodechallenge-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Usage

### Starting The REST Server

```bash
~/solactivecodechallenge$ java -cp target/solactivecodechallenge-1.0-SNAPSHOT-jar-with-dependencies.jar com.solactive.codechallenge.Main
Jan 17, 2020 11:10:40 AM org.glassfish.grizzly.http.server.NetworkListener start
INFO: Started listener bound to [localhost:8080]
Jan 17, 2020 11:10:40 AM org.glassfish.grizzly.http.server.HttpServer start
INFO: [HttpServer] Started.
Current time: 1579259440221
```

### Example Curl Commands

*Note: you need to use timestamps that are within the window, as per requirements.*

```bash
$ curl -H "Content-Type: application/json" -d '{"instrument":"IBM.N","price":100.0,"timestamp":2579258809302}' localhost:8080/ticks/
$ curl http://localhost:8080/statistics/IBM.N
{"avg":100.0,"max":100.0,"min":100.0,"count":1}
$ curl http://localhost:8080/statistics
{"avg":100.0,"max":100.0,"min":100.0,"count":1}

etc.
```

## Discussion

### Assumptions

- I process out-of-order messages, because this is how I read the spec initially. However,
I don't think this is the best approach, as I expressed in a WhatsApp message to Toralf 
Niehbuhr:
> Hi Toralf! I'm doing the coding challenge, and I'm a little unclear about the requirement 
> to deal with out of order messages. My inclination, and what we use in our systems, is 
> to ignore out- of-order events. Reasons: a) given a good data provider i.e. being colo'd, 
> such events are rare, b) you really want the freshest data, not stale data, at least for 
> our algos, c) assuming monotonically increasing times makes algorithms much more efficient, 
> e.g. avg becomes O(1) space, time (for updates and reads), min/max is O(1) time and 
> amortized O(1) space for updates. Obviously reads can be O(1) simply through use of 
> atomics in the case of if you do want to incorporate OOEs into the calculations, but, 
> unless I'm missing something, you're not going to get around some kind of sorting. So 
> you're going to lock around O(E(num msgs per minute)) in time... I'd get nervous about 
> that if there's a bust in mkt activity. My implementation assumes using OOEs, but I just 
> wanted to ping you about this since the language in the question isn't explicit on this point.

(**Note**: O(1) for avg is wrong even without out-of-order events, but if you used EWMA
instead of simple average then it is O(1).)

- Since I handle out-of-order messages, I need to sort the messages for a given stock,
which I do using a priority queue which internally uses a heap data structure.
- As mentioned above, a simple average is memory and CPU unfriendly, I would prefer to use
an EWMA - *especially* if you don't downsample to a grid. So for example a simple average
of the prices snapped at every second would be much easier to compute (same for min/max),
but for realtime data you can potentially have problems because events can come in 
pretty big bursts.
- I assume there are no more than 50K stock symbols. It's easy to increase this, since it's
just a constant.
- If I discarded out-of-order messages, I would be able to use ascending minima algorithm.
- So: assuming EWMA and no-of-order messages would have a drastic postive impact on
performance.
- As mentioned in the comments, I would want to do some kind of thread/CPU segregation:
one group of CPUs/threads would be for the web server to handle requests, the other
group to update the calculations. The link is the disruptor which funnels messages between
the two (for POST request - for GET we simply read the cached values).

### Improvements

- It's disappointing that when the disruptor is saturated, it drops the *newest* messages.
I used that since it's available and to try it out. But really, I would want something - 
again using a ring buffer - where if you saturate the *oldest* messages instead.
- As mentioned in the comments, there really should be logging and I only sketched out
the idea of a *health sentinel* which, on a minute-by-minute basis, leverages the stats 
calculation functionality to report on the number of messages processed, any dropped 
messages, etc.
- Change the tests to not use `extends TestCase`.

### Did I Like It

In all honesty, if I *didn't* I would be unlikely to say so, but I hope you can see from
the implementation that I had a blast!




