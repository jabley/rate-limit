# Overview

Contains the primitives and utilities used to rate-limit / throttle Java 
applications, and a CircuitBreaker implementation.

# Summary
Inspired by reading Cal Henderson's "Building Scalable Web Sites" which talks 
briefly about this, and having been on the receiving end of a kicking from 
search engines, I wanted to have a simple way of determining whether to bother
processing requests and stop consuming server resources in a graceful way, 
rather than grinding to a halt.

## Background - types of throttling

### Next Service Slot

Each time a request comes in, we log the time. If it hasn't been a certain 
duration since the last request, then abort with a rate-limiting error.

    key = create_key(request)
    
    entry = gate.get_entry(key)

    if (entry)
        response.set_status(SERVICE_UNAVAILABLE)
        return
    end
    
    entry = create_entry(expires => '5s')
    
    gate.put_entry(key, entry)
    
    ...


### Fixed Bucket

We define a duration and an acceptable number of requests to be serviced in 
that time. Each time a request comes in, we look up the number of calls made 
in the current period. If it is at or above the limit, then abort with a 
rate-limiting error, otherwise increment the counter and service the request.

    key = create_key(request)
    
    entry = gate.get_entry(key)
    
    if (entry.count >= ALLOWED_PER_PERIOD)
        response.set_status(SERVICE_UNAVAILABLE)
        return
    end

    entry.count.increment()
    
    ...

From this description, it can be seen that Next Service Slot is essentially 
Fixed Bucket with a max size of 1 and an appropriate service period.

### Leaky Bucket

Similar to a Fixed Bucket, except that rather than aborting, we block until 
the end of the current time period upon which the bucket counter is 
decremented / completely emptied and then we service the request.

Hardest to implement, has the disadvantage that it will tie up a 
request-handling thread (which may cause upstream services to timeout / retry) 
but may be a good solution in other contexts.

    key = create_key(request)
    
    entry = gate.get_entry(key)
    
    if (entry.count >= ALLOWED_PER_PERIOD)
        entry.wait()
    end

    entry.count.increment()
    
    ...

## CircuitBreaker

There is some overlap in the intention of this library with the Circuit Breaker 
approach described by Michael Nygard in his excellent book "Release It!"; I've
done some work to add support for that as well. We've been running it in
production for a year and it works well for our purposes.

Please see the tests for details as to how to use it.
