# hubspot-java
Java Wrapper for HubSpot API (v3)

## Overview
This project is a Java wrapper for the HubSpot API, using OAuth2 for authentication and Jackson for JSON serialization. It follows a layered architecture with specialized services for each HubSpot entity.

## Currently implemented services
* **Association** (`HSAssociationService`)
* **Company** (`HSCompanyService`)
* **Contact** (`HSContactService`)
* **Deal** (`HSDealService`)
* **LineItem** (`HSLineItemService`)
* **Pipeline** (`HSPipelineService`)
* **Product** (`HSProductService`)
* **Quote** (`HSQuoteService`)
* **Stage** (`HSStageService`)

## Installation

### Maven
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>fr.slickteam.hubspot.api</groupId>
    <artifactId>hubspot-java</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Gradle
Add the following to your `build.gradle` or `build.gradle.kts`:

```kotlin
implementation("fr.slickteam.hubspot.api:hubspot-java:3.0.0")
```

## Configuration

To use the library, you need to provide your HubSpot OAuth2 credentials. You can do this using the `HubSpotProperties` class.

```java
HubSpotProperties properties = new HubSpotProperties();
properties.setAccessToken("your_access_token");

// Optional: for automatic token refresh
properties.setClientId("your_client_id");
properties.setClientSecret("your_client_secret");
properties.setRefreshToken("your_refresh_token");

HubSpot hubSpot = new HubSpot(properties);
```

## Basic Usage

### Creating a contact
```java
HSContact contact = new HSContact("test@example.com", "John", "Doe", "123456789", "customer");
contact = hubSpot.contact().create(contact);
System.out.println("Created contact ID: " + contact.getId());
```

### Searching for companies
```java
List<HSCompany> companies = hubSpot.company().searchByCustomProperty("name", "Slickteam", Collections.singletonList("name"));
```

## Webhooks
The library provides utilities to handle HubSpot webhooks, including signature verification:

```java
boolean isValid = HubSpotWebHookSignatureUtils.isSignatureValid(
    signature,
    clientSecret,
    requestMethod,
    requestURI,
    payload,
    timestamp
);
```

## Integration tests
For the integration tests to work, you must register your version of this project in HubSpot, then get the OAuth2 params and set them inside `src/test/resources/config.properties`.

-----
Developed by DepositFix - Payment Integration for HubSpot
<https://www.depositfix.com/>

Updated by Slickteam
<https://slickteam.fr/>

_PS : the git history is short since we had an issue with our internal repository and it was restored from a saved zip file._

_The original repo is here : <https://github.com/integrationagent/hubspot-java>_
