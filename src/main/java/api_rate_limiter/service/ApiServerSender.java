package api_rate_limiter.service;

/**
 * This interface is the specification for any service that would forward any allowed request to the protected
 * Api server
 * @author Ahmed Ali Rashid
 */
public interface ApiServerSender {
    /**
     *  Once a request is allowed to consume, this method forward to the protected server
     * @param endpoint the endpoint on the API server side to hit
     * @return return any object by the protected server
     */
    Object send(String endpoint, String method);
}
