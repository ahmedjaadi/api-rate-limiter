-   The implementation is available on [GitHub](https://github.com/ahmedjaadi/api-rate-limiter).
    <br>

* [Introduction](#introduction)
* [Understanding the Architecture of an API Rate Limiter](#understanding-the-architecture-of-an-api-rate-limiter)
* [What is Throttling?](#what-is-throttling)
* [Client Responsibilities](#client-responsibilities)
* [Designing the Application](#designing-the-application)
* [Introduction](#conclusion)


## Introduction

APIs serve as the backbone of modern software systems, enabling communication between different applications. However, without proper control, APIs can be overwhelmed by excessive requests, potentially leading to degraded performance or even downtime. This is where API rate limiting comes into play.

API rate limiting is a mechanism that restricts the number of requests a client can make to an API within a specified time period. By doing so, it ensures fair usage among clients, protects system resources, and maintains service stability. In this article, we will first explore important concepts(components) relevant to API rate limiting, and then we see the design and implementation of an API rate limiter using the Spring framework and Redis. The full implementation code is available on [GitHub](https://github.com/ahmedjaadi/api-rate-limiter).

## Why Should You Rate Limit Your API?

APIs are integral to many businesses, and their reliability is critical. Implementing a rate limiter provides several benefits:

1.  **Enhancing Security**: Rate limiting prevents denial-of-service (DoS) attacks by limiting the number of requests a malicious user can make.

2.  **Mitigating Noisy Neighbors**: In multi-tenant systems, one client’s excessive use should not degrade the performance experienced by others.

3.  **Preventing Resource Starvation**: Rate limiting ensures that API consumers cannot monopolize resources, safeguarding the infrastructure from being overwhelmed.

## Understanding the Architecture of an API Rate Limiter

A robust API rate limiter consists of several key components working together to manage and enforce rate limits. Let’s delve into the architecture:

### Load Balancer

A load balancer is a standard component in distributed systems. It evenly distributes incoming network traffic across multiple service instances to ensure reliability and scalability. In the context of rate limiting, it ensures that no single instance of the rate limiter or the protected API server is overwhelmed.

### Rate Limiter

The rate limiter is the core component. It intercepts requests before they reach the protected API server (often called the Request Processor). Based on predefined rules, the rate limiter decides whether to allow or throttle requests. Throttling may involve rejecting requests or queuing them for later processing.

### Request Processor

This is the actual API server that processes valid requests. In our prototype, the rate limiter and the request processor are combined for simplicity. However, in real-world scenarios, these are often separate applications.

### Redis as a Distributed Cache

Redis is used to store and sync rate limit states across distributed instances of the rate limiter. It ensures that all instances share a consistent view of the rate limits, making the system scalable and fault-tolerant.

### Rules Engine Service and Database

The rules engine service defines the rate-limiting policies, such as the maximum number of requests allowed per time window and the actions to take when limits are exceeded. For brevity, our prototype uses hardcoded rules, but in a production-grade system, these rules would be dynamically retrieved from a database.

### Queue

A queue can serve multiple purposes, such as:

-   Temporarily holding requests during high traffic periods for later processing.

-   Implementing soft throttling by delaying requests instead of outright rejecting them.

<figure>
<img src="https://github.com/ahmedjaadi/api-rate-limiter/blob/c85ffd2ada37bb153ae23d7b4019bb83a5e22957/docs/high_level_architecture.png?raw=true" alt="high level architecture" />
<figcaption>High-Level Architecture</figcaption>
</figure>

## What is Scaling and How Is It Implemented?

Scaling refers to the ability of a system to handle increasing amounts of workload by adding resources. In the context of an API rate limiter, scaling ensures that the system can manage growing traffic without performance degradation. There are two types of scaling:

1.  **Vertical Scaling**: Adding more resources (e.g., CPU, memory) to a single instance. While straightforward, it has physical limits and can become costly.

2.  **Horizontal Scaling**: Adding more instances of a service to distribute the workload. This is the preferred approach for modern distributed systems as it offers better resilience and flexibility.

In our implementation, horizontal scaling is achieved by:

-   Deploying multiple instances of the rate limiter, each paired with a request processor.

-   Using Redis as a distributed cache to synchronize rate limit states across all instances.

<figure>
<img src="https://github.com/ahmedjaadi/api-rate-limiter/blob/c85ffd2ada37bb153ae23d7b4019bb83a5e22957/docs/scaling.png?raw=true" alt="scaling" />
<figcaption>Scaling</figcaption>
</figure>

## What is Throttling?

Throttling is the process of regulating the rate of incoming requests to an API. When a client exceeds its allowed quota, throttling mechanisms decide how to handle the excess requests. There are two main strategies:

### Hard Throttling

Hard throttling enforces strict limits. Once a client reaches its quota, any additional requests are immediately rejected. This approach is straightforward and is typically applied to general or untrusted clients.

### Soft Throttling

Soft throttling provides a more lenient approach, especially for premium or trusted clients. Instead of outright rejecting requests, soft throttling may:

-   Allow a certain percentage of excess requests to pass through.

-   Queue excess requests for processing once traffic subsides.

The choice between hard and soft throttling depends on business requirements. For instance, a subscription-based service might implement soft throttling for premium users to enhance their experience, while applying hard throttling for standard users.

## Client Responsibilities

To ensure the effectiveness of the rate limiter, clients should adopt best practices:

-   **Exponential Backoff**: Retry requests after progressively longer delays when encountering failures. This reduces server load and avoids immediate retries.

-   **Jitter**: Add random delays (Jitter) to retry intervals to prevent simultaneous retries from multiple clients, which can overwhelm the server.

## Designing the Application

### High-Level Architecture

Our application’s architecture is simplified for clarity and practicality, with some components removed or merged:

-   The **RateLimiter** (under the **api\_rate\_limiter** package) and **RequestProcessor** (under the **request\_processor** package) are combined into a single Spring Boot application. In real-world scenarios, these two components might exist as separate applications.

-   The application does not include a **Load Balancer** or a **Queue**.

-   **Rules** are hard-coded, eliminating the need for a **Rules Service** and a **Rules Engine** Database.

-   Distributed caching is supported using **Redis**. However, for scenarios where **Redis** is unavailable, an alternative approach is provided with a standalone runtime profile, where rules are stored in the application’s memory.

All applicable rules, including system-wide and tenant-specific rules, are applied dynamically. This ensures fair and precise enforcement of rate limits for every tenant.

### Domain Modeling

The application revolves around a few core domain entities:

-   **RateLimit**: Defines the properties of a rate limit, such as capacity and time period.

-   **TenantRateLimit**: Represents the rate limits applicable to a specific tenant. It includes properties for monthly and time-window limits.

-   **Bucket**: Tracks the live state of rate limits for each tenant. There is also a system-wide bucket for global limits.

<figure>
<img src="https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0l6p394yhuww6x41k94o.png" alt="domain model" />
<figcaption>Domain Model</figcaption>
</figure>

### Service Interactions

-   **TenantBucketProvider**: This is the actual service that the **Rate Limiter** relies on to make the decision whether to let a request through or not. Manages the limit states for tenants. It has two implementations:
    1.  **StandaloneBucketProvider**: Uses in-memory storage, suitable for standalone applications but not scalable.
    2.  **RedisBucketProvider**: Uses Redis for distributed environments, enabling horizontal scalability.

-   **ApiServerSender**: Forwards valid requests to the protected API server.

<figure>
<img src="https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qxl82zwkdfwr13cnlkvq.png" alt="services interaction" />
<figcaption>Services Interactions</figcaption>
</figure>

### Endpoints

The application includes a `RateLimitController`, which processes requests matching `/api/v1/**`. It checks the tenant’s bucket state using the `TenantBucketProvider`. If the request is within limits, it is forwarded; otherwise, it is throttled.

### Throttling

Again, for the sake of simplicity, we **Hard-Throttle** requests that exceed the tenant’s limits

### Profiles

The application supports two runtime profiles:

-   `standalone`: Uses the `StandaloneBucketProvider`.

-   `distributed`: Uses the `RedisBucketProvider`.

## Conclusion

API rate limiting is essential for maintaining the stability, security, and fairness of APIs. By understanding its architecture, scaling mechanisms, and implementation strategies, developers can design robust systems that handle diverse usage patterns effectively. For a hands-on demonstration, check out the implementation code on [GitHub](https://github.com/ahmedjaadi/api-rate-limiter).
