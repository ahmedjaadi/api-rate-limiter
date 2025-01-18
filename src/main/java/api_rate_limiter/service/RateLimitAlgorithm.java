package api_rate_limiter.service;

/**
 * @author Ahmed Ali Rashid
 * This is the brain of this project, the idea is to allow different algorithms to be implemented which one to use
 * the specific requirement, that can be based on specific user or a request
 */
public interface RateLimitAlgorithm {
    /**
     * this method will be called for each incoming request
     * @param numTokens number of token the request is attempting to consume, typically a request would consume one token
     * @return returns true if the request can be forwarded to the API server or false if it is blocked
     */
    boolean tryConsume(int numTokens);
}
