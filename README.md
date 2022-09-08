# hubspot-java
Java Wrapper for HubSpot API (<http://developers.hubspot.com/docs/overview>)
Application to register in Hubspot developer portal, so you can use it.
It uses OAuth2 to authenticate on Hubspot API.

#### Currently implemented
* Company
* Contact
* Deal
* LineItem
* Object
* Product

#### Maven Installation


```xml
<repositories>
	...
	<repository>
		<id>bintray</id>
		<url>http://dl.bintray.com/integrationagent/hubspot-java</url>
		<releases>
			<enabled>true</enabled>
		</releases>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</repository>
	...
</repositories>
<dependencies>
	...
	<dependency>
		<groupId>com.integrationagent</groupId>
		<artifactId>hubspot-java</artifactId>
		<version>0.1</version>
	</dependency>
	...
</dependencies>
```

#### Integration tests

For the integration tests to work, you must register your version of this project in Hubspot, then get the OAuth2 params to set them inside config.properties file for tests.

-----
Developed by DepositFix - Payment Integration for HubSpot
<https://www.depositfix.com/>

Updated by Slickteam
<https://slickteam.fr/>

_PS : the git history is short since we had an issue with our internal repository and it was restored from a saved zip file._

_The original repo is here : <https://github.com/integrationagent/hubspot-java>_