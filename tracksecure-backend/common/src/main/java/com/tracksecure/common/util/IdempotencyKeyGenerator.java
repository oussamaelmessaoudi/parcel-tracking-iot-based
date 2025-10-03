package com.tracksecure.common.util;

import java.util.UUID;

//Class is final and cannot be subclassed/ inhireted
public final class IdempotencyKeyGenerator {
    //Basically an idempotency key is a key that usually send along with the request to-
    // make sure the request is only processed once and avoid repeating actions
    private IdempotencyKeyGenerator() {
        // This is a utility class which means it only provide/ offer static helper methods for future use
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static String generateIdempotencyKey(String prefix){
        return prefix+"-"+ UUID.randomUUID();
    }

    //Another generator which generates the same idempotency key for the same seed
    // it will always be the same idemKey for the seed e.g: "payment123"
    public static String deterministic(String seed){
        return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
    }
}
