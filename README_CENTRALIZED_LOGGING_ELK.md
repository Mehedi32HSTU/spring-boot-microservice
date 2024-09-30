# Centralized Logging with ELK Stack

This README provides step-by-step instructions on how to configure centralized logging for multiple microservices (`CUSTOMER-SERVICE`, `ORDER-SERVICE`, and `PRODUCT-SERVICE`) using the ELK stack.

## Prerequisites

- **Install Elasticsearch**: 
    1. Download Elasticsearch from the official site: https://www.elastic.co/downloads/elasticsearch
    2. Extract the downloaded archive.
    3. In the `elasticsearch.yml` file, locate the following setting and change it as following:
     ```yaml
        # --------------------------------------------------------------------------------
        # Enable security features
        xpack.security.enabled: false
        
        xpack.security.enrollment.enabled: false
        
        # Enable encryption for HTTP API client connections, such as Kibana, Logstash, and Agents
        xpack.security.http.ssl:
        enabled: false
        keystore.path: certs/http.p12
        
        # Enable encryption and mutual authentication between cluster nodes
        xpack.security.transport.ssl:
        enabled: false
        verification_mode: certificate
        keystore.path: certs/transport.p12
        truststore.path: certs/transport.p12
        # Create a new cluster with the current node only
        # Additional nodes can still join the cluster later
  
     ```
    4. Start Elasticsearch: `./bin/elasticsearch`
    5. Read details here: https://stackoverflow.com/questions/71492404/elasticsearch-showing-received-plaintext-http-traffic-on-an-https-channel-in-con
    6. By default, Elasticsearch runs on `http://localhost:9200`
    7. View attributes: `http://localhost:9200/_cat`


- **Install Kibana**:
    1. Download Kibana from: https://www.elastic.co/downloads/kibana
    2. Extract the archive.
    3. In the `kibana.yml` file, locate the following setting and uncomment it:

    ```yaml
      # Uncomment this line to point to your local Elasticsearch instance
      elasticsearch.hosts: ["http://localhost:9200"]
    ```
    4. Start Kibana: `./bin/kibana`
    5. Kibana will run on `http://localhost:5601`

- **Install Logstash**:
    1. Download Logstash from: https://www.elastic.co/downloads/logstash
    2. Extract the archive.
    3. You will configure Logstash to process logs from your microservices.

- **Java Microservices**: You should have logback configured in your Spring Boot applications.

---

## Step 1: Configuring Logback in Microservices

Each microservice will log to its own file in JSON format. Below is the `logback-spring.xml` configuration for each service.

### Logback Configuration for `CUSTOMER-SERVICE`

```xml
<configuration>
    <property name="serviceName" value="Microservice-CUSTOMER" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>C:/path/to/logs/CUSTOMER-SERVICE.log</file>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"serviceName": "${serviceName}"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

*** Repeat this configuration for ORDER-SERVICE and PRODUCT-SERVICE, adjusting the <property> and <file> paths as needed.

## Step 2: Configuring Logstash
Logstash will aggregate and forward the logs to Elasticsearch.
Create a `logstash.conf` file with the following content:

```conf
input {
  file {
    path => "C:/path/to/logs/CUSTOMER-SERVICE.log"
    start_position => "beginning"
    sincedb_path => "NUL"
    type => "customer-service"
  }

  file {
    path => "C:/path/to/logs/ORDER-SERVICE.log"
    start_position => "beginning"
    sincedb_path => "NUL"
    type => "order-service"
  }

  file {
    path => "C:/path/to/logs/PRODUCT-SERVICE.log"
    start_position => "beginning"
    sincedb_path => "NUL"
    type => "product-service"
  }
}

filter {
  if [type] == "customer-service" {
    json {
      source => "message"
    }
    mutate {
      add_field => { "service_name" => "CUSTOMER-SERVICE" }
    }
  }

  if [type] == "order-service" {
    json {
      source => "message"
    }
    mutate {
      add_field => { "service_name" => "ORDER-SERVICE" }
    }
  }

  if [type] == "product-service" {
    json {
      source => "message"
    }
    mutate {
      add_field => { "service_name" => "PRODUCT-SERVICE" }
    }
  }
}

output {
  elasticsearch {
    hosts => ["http://localhost:9200"]
    index => "%{type}-logs"
  }
  stdout { codec => rubydebug }
}

```
## Step 3: Start logstash:
* Put copy of `logstash.conf` file in `logstash` folder, `./bin` folder and `./config` folder
* Start logstash by running the command: `./bin/logstash -f logstash.conf`

## Step 4: Run All Microservice Application and view log
* Start all microservice application, It will create logs on the specified directory file.
* View Logs in Kibana
  1. Go to `http://localhost:5601` to open Kibana.
  2. In Kibana, create an index pattern for the logs. Go to `Management` > `Index Patterns`, and create a new index pattern matching your Logstash indices (e.g., `microservice-logs-*`).
  3. Once the index pattern is created, you can search, visualize, and analyze the logs using Kibana's Discover and Visualize tools.
