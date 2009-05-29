package com.eternus.ratelimit;


public class TestFixedBucketWithCoarseMemoryStore extends FixedBucketTests {

    @Override
    protected TokenStore createTokenStore() {
        return new CoarseMemoryTokenStore();
    }

}
