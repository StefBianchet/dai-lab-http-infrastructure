DAI Lab - HTTP infrastructure
=============================

Objectives
----------

The main objective of this lab is to learn to build a complete Web infrastructure. This means, we will build a server infrastructure that serves a static Web site and a dynamic HTTP API. The diagram below shows the architecture of the infrastructure that we will build.

```mermaid
graph LR
    subgraph Client
        B((Browser))
    end
    subgraph Server
        RP(Reverse\nProxy)
        SS(Static\nWeb server)
        DS(Dynamic\nAPI server)
    end
    B -.-> RP
    RP --> SS
    RP --> DS
```

In addition to the basic requirement of service static and dynamic content, the infrastructure will have the following features:

- **Scalability**: both the static and the dynamic server will be deployed as a cluster of several instances. The reverse proxy will be configured to distribute the load among the instances.
- **Security**: the connection between the browser and the reverse proxy will be encrypted using HTTPS.
- **Management**: a Web application will be deployed to manage the infrastructure. This application will allow to start/stop instances of the servers and to monitor the state of the infrastructure.

General instructions
--------------------

- This is a **BIG** lab and you will need a lot of time to complete it. 
- You will work in **groups of 2 students** and use a Git workflow to collaborate.
- For certain steps you will need to do research in the documentation by yourself (we are here to help, but we will not give you step-by-step instructions!) or you will need to be creative (do not expect complete guidelines).
- Read carefully all the **acceptance criteria** of each step. They will tell you what you need to do to complete the step.
- After the lab, each group will perform a short **demo** of their infrastructure.
- **You have to write a report with a short descriptioin for each of the steps.** Please do that directly in the repo, in one or more markdown files. Start in the README.md file at the root of your directory.
- The report must contain the procedure that you have followed to prove that your configuration is correct (what you did do make the step work and what you would do if you were doing a demo).


Step 0: GitHub repository
-------------------------

Create a GitHub repository for your project. You will use this repository to collaborate with your team mate. You will also use it to submit your work. 

> [!IMPORTANT]
> Be careful to keep a clear structure of the repository such that the different components are clearly separated.

### Acceptance criteria

- [x] You have created a GitHub repository for your project.
- [x] The respository contains a Readme file that you will use to document your project.


Step 1: Static Web site
-----------------------

The goal of this step is to build a Docker image that contains a static HTTP server Nginx. The server will serve a static Web site. The static Web site will be a single page with a nice looking template. You can use a free template for example from [Free-CSS](https://www.free-css.com/free-css-templates) or [Start Bootstrap](https://startbootstrap.com/themes).

### How we completed this step

To complete this step we needed to create an new folder, in our case `staticWebServer`, that will contain the website, the Dockerfile and the nginx.conf file.
Our nginx.conf file specifies the port that the server is listening on and the location of the index file.

Here are the commands that we used to build the image and run the container: 
```bash
docker build -t static_web_server .

docker run --name nginx_static -d -p 9080:9080 static_web_server
```

### Acceptance criteria

- [x] You have created a separate folder in your respository for your static Web server.
- [x] You have a Dockerfile based on the Nginx image. The Dockerfile copies the static site content into the image.
- [x] You have configured the `nginx.conf` configuration file to serve the static content on a port (normally 80).
- [x] You are able to explain the content of the `nginx.conf` file.
- [x] You can run the image and access the static content from a browser.
- [x] You have **documented** your configuration in your report.


Step 2: Docker compose
----------------------

The goal of this step is to use Docker compose to deploy a first version of the infrastructure with a single service: the static Web server.

In addition to the basic docker compose configuration, we want to be able to rebuild the docker image of the Web server. See the [Docker compose Build documentation](https://docs.docker.com/compose/compose-file/build/) for this part.

### How we completed this step

We created a pretty basic `docker-compose.yml`containing our image called static, this image builds the Dockerfile that we created in step 1.
Here's the contents of our `docker-compose.yml`:
```yml
services:
  static:
    image: dai/staticwebserver
    build: 
      context: ./staticwebserver
    ports: 
    - "9080:9080"
```

This file will be changed in future steps

### Acceptance criteria

- [x] You have added a docker compose configuration file to your GitHub repo.
- [x] You can start and stop an infrastructure with a single static Web server using docker compose.
- [x] You can access the Web server on your local machine on the respective port.
- [x] You can rebuild the docker image with `docker compose build`
- [x] You have **documented** your configuration in your report.


Step 3: HTTP API server
-----------------------

This step requires a more work. The goal is to build a HTTP API with Javalin. You can implement any API of your choice, such as:

- an API to manage a list of quotes of the day
- an API to manage a list of TODO items
- an API to manage a list of people

Use your imagination and be creative!

The only requirement is that the API supports at all CRUD operations, i.e.: Create, Read, Update, Delete. 

Use a API testing tool such as Insomnia, Hoppscotch or Bruno to test all these operations.

The server does not need to use a database. You can store the data in memory. But if you want to add a DB, feel free to do so.

Once you're finished with the implementation, create a Dockerfile for the API server. Then add it as a service to your docker compose configuration.

### How we completed this step

We decided to create an API about Formula 1 drivers. Our API supports all CRUD operations.

We used [Bruno](https://www.usebruno.com/) to test our API. API usage examples are in the `F1-driver-API-examples` folder
We add this environment variable to Bruno to make our API work correctly : `baseURL = http://localhost:7070`

We also created a new DockerFile dockerizing our API.

We added our new service API to our `docker-compose.yml` file. This new service builds the DockerFile created earlier.
Here is the updated content of our `docker-compose.yml` file :
```yml
services:
  static:
    image: dai/staticwebserver
    build:
      context: ./staticwebserver
    ports:
      - "9080:9080"
  api:
    image: dai/api
    build:
      context: ./api
    ports:
      - "7070:7070"
```

### Acceptance criteria

- [x] Your API supports all CRUD operations.
- [x] You are able to explain your implementation and walk us through the code.
- [x] You can start and stop the API server using docker compose.
- [x] You can access both the API and the static server from your browser.
- [x] You can rebuild the docker image with docker compose.
- [x] You can do demo where use an API testing tool to show that all CRUD operations work.
- [x] You have **documented** your implementation in your report.


Step 4: Reverse proxy with Traefik
----------------------------------

The goal of this step is to place a reverse proxy in front of the dynamic and static Web servers such that the reverse proxy receives all connections and relays them to the respective Web server. 

You will use [Traefik](https://traefik.io/traefik/) as a reverse proxy. Traefik interfaces directly with Docker to obtain the list of active backend servers. This means that it can dynamically adjust to the number of running server. Traefik has the particularity that it can be configured using labels in the docker compose file. This means that you do not need to write a configuration file for Traefik, but Traefik will read container configurations from the docker engine through the file `/var/run/docker.sock`.

The steps to follow for this section are thus:

- Add a new service "reverse_proxy" to your docker compose file using the Traefik docker image
- Read the [Traefik Quick Start](https://doc.traefik.io/traefik/getting-started/quick-start/) documentation to establish the basic configuration.
- Read the [Traefik & Docker](https://doc.traefik.io/traefik/routing/providers/docker/) documentation to learn how to configure Traefik to work with Docker.
- Then implement the reverse proxy:
  - relay the requests coming to "localhost" to the static HTTP server
  - relay the requests coming to "localhost/api" to the API server. See the [Traefik router documentation](https://doc.traefik.io/traefik/routing/routers/) for managing routes based on path prefixes. 
  - you will have to remove the `ports` configuration from the static and dynamic server in the docker compose file and replace them with `expose` configuration. Traefik will then be able to access the servers through the internal Docker network.
- You can use the [Traefik dashboard](https://doc.traefik.io/traefik/operations/dashboard/) to monitor the state of the reverse proxy.

### How we completed this step

Here's the *latest version* of our docker-compose.yml

```yml
services:
  static:
    image: dai/staticwebserver
    build: 
      context: ./staticwebserver
    labels:
      - traefik.http.routers.static.rule=Host(`localhost`)
      - traefik.http.services.static.loadbalancer.server.port=9080
  api:
    image: dai/api
    build:
      context: ./api
    labels:
      - traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)
      - traefik.http.services.api.loadbalancer.server.port=7070
  reverse_proxy:
    image: traefik:v2.10
    command: --api.insecure=true --providers.docker
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

We had to add two labels to each of our "services" to relay the requests as required.

The first label is to impose routing and path prefix conditions. The second label is used to define on which port the load-balancing server is listening on.

To access the Traefik dashboard all you need to do is run the `docker compose up -d` command and then go to http://localhost:8080/dashboard/

This dashboard allows us to see a list of HTTP routers, services and middlewares.

### Acceptance criteria

- [x] You can do a demo where you start from an "empty" Docker environment (no container running) and using docker compose you can start your infrastructure with 3 containers: static server, dynamic server and reverse proxy
- [x] In the demo you can access each server from the browser in the demo. You can prove that the routing is done correctly through the reverse proxy.
- [x] You are able to explain in the documentation how you have implemented the solution and walk us through the configuration and the code.
- [x] You are able to explain in the documentation why a reverse proxy is useful to improve the security of the infrastructure.
- [x] You are able to explain in the documentation how to access the dashboard of Traefik and how it works.
- [x] You have **documented** your configuration in your report.


Step 5: Scalability and load balancing
--------------------------------------

The goal of this section is to allow Traefik to dynamically detect several instances of the (dynamic/static) Web servers. You may have already done this in the previous step 3.

Modify your docker compose file such that several instances of each server are started. Check that the reverse proxy distributes the connections between the different instances. Then, find a way to *dynamically* update the number of instances of each service with docker compose, without having to stop and restart the topology.

### How we completed this step

Here's our docker-compose.yml for this step
```yml
services:
  static:
    image: dai/staticwebserver
    build: 
      context: ./staticwebserver
    labels:
      - traefik.http.routers.static.rule=Host(`localhost`)
      - traefik.http.services.static.loadbalancer.server.port=9080
    deploy:
      replicas: 5
  api:
    image: dai/api
    build:
      context: ./api
    labels:
      - traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api/`)
      - traefik.http.services.api.loadbalancer.server.port=7070
    deploy:
      replicas: 5
  reverse_proxy:
    image: traefik:v2.10
    command: --api.insecure=true --providers.docker
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

We added replicas to the file.

To dynamically change the number of instances you need to type this command into the terminal
```bash
docker compose up -d --scale api=7
```

You can replace `api=7` with `static=any_number` if you want to add more instances of the static web page container.


To tell which server is running all you need to do is run `docker compose logs`, the name of the container that is running will be on the left, i.e., dai-lab-http-infrastructure-api-4

### Acceptance criteria

- [x] You can use docker compose to start the infrastructure with several instances of each server (static and dynamic).
- [x] You can dynamically add and remove instances of each server.
- [x] You can do a demo to show that Traefik performs load balancing among the instances.
- [x] If you add or remove instances, you can show that the load balancer is dynamically updated to use the available instances.
- [x] You have **documented** your configuration in your report.


Step 6: Load balancing with round-robin and sticky sessions
-----------------------------------------------------------

By default, Traefik uses round-robin to distribute the load among all available instances. However, if a service is stateful, it would be better to send requests of the same session always to the same instance. This is called sticky sessions.

The goal of this step is to change the configuration such that:

- Traefik uses sticky session for the dynamic server instances (API service).
- Traefik continues to use round robin for the static servers (no change required).

### How we completed this step

Here's our docker-compose.yml for this step
```yml
services:
  static:
    image: dai/staticwebserver
    build: 
      context: ./staticwebserver
    labels:
      - traefik.http.routers.static.rule=Host(`localhost`)
      - traefik.http.services.static.loadbalancer.server.port=9080
    deploy:
      replicas: 5
  api:
    image: dai/api
    build:
      context: ./api
    labels:
      - traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api/`)
      - traefik.http.services.api.loadbalancer.server.port=7070
      - traefik.http.services.api.loadBalancer.sticky.cookie=true
      - traefik.http.services.api.loadBalancer.sticky.cookie.name=cookie_api
    deploy:
      replicas: 5
  reverse_proxy:
    image: traefik:v2.10
    command: --api.insecure=true --providers.docker
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

We added cookie labels to the API service.

After that, we checked if the cookie was present in the response from the API. Here is the response received by the client : 

```http
GET /api/drivers HTTP/1.1
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Accept-Language: en-GB,en;q=0.9,fr;q=0.8
Cache-Control: no-cache
Connection: keep-alive
Cookie: cookie_api=8ca106c699558f55
Host: localhost
Pragma: no-cache
Sec-Fetch-Dest: document
Sec-Fetch-Mode: navigate
Sec-Fetch-Site: none
Sec-Fetch-User: ?1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36
sec-ch-ua: "Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
```

In comparison, here is the response from the static web site. As you can see there is no cookie.

```http
GET / HTTP/1.1
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Accept-Language: en-GB,en;q=0.9,fr;q=0.8
Cache-Control: no-cache
Connection: keep-alive
Host: localhost
Pragma: no-cache
Sec-Fetch-Dest: document
Sec-Fetch-Mode: navigate
Sec-Fetch-Site: none
Sec-Fetch-User: ?1
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36
sec-ch-ua: "Not_A Brand";v="8", "Chromium";v="120", "Google Chrome";v="120"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"

```

### Acceptance criteria

- [x] You do a setup to demonstrate the notion of sticky session.
- [x] You prove that your load balancer can distribute HTTP requests in a round-robin fashion to the static server nodes (because there is no state).
- [x] You prove that your load balancer can handle sticky sessions when forwarding HTTP requests to the dynamic server nodes.
- [x] You have **documented** your configuration and your validation procedure in your report.


Step 7: Securing Traefik with HTTPS
-----------------------------------

Any real-world web infrastructure must be secured with HTTPS instead of clear-text HTTP. The goal of this step is to configure Traefik to use HTTPS with the clients. The schema below shows the architecture.

```mermaid

graph LR
    subgraph Client
        B((Browser))
    end
    subgraph Server
        RP(Reverse\nProxy)
        SS(Static\nWeb server)
        DS(Dynamic\nAPI server)
    end
    B -. HTTPS .-> RP
    RP -- HTTP --> SS
    RP -- HTTP --> DS
```

This means that HTTPS is used for connection with clients, over the Internet. Inside the infrastructure, the connections between the reverse proxy and the servers are still done in clear-text HTTP.

### Certificate

To do this, you will first need to generate an encryption certificate. Since the system is not exposed to the Internet, you cannot use a public certificate such as Let's encrypt, but have to generate a self-signed certificate. You can [do this using openssl](https://stackoverflow.com/questions/10175812/how-to-create-a-self-signed-certificate-with-openssl#10176685).

Once you got the two files (certificate and key), you can place them into a folder, which has to be [mounted as a volume in the Traefik container](https://docs.docker.com/compose/compose-file/compose-file-v3/#short-syntax-3). You can mount the volume at any path in the container, for example `/etc/traefik/certificates`.

### Traefik configuration file

Up to now, you've configured Traefik through labels directely in the docker compose file. However, it is not possible to specify the location of the certificates to Traefik with labels. You have to create a configuration file `traefik.yaml`. 

Again, you have to mount this file into the Traefik container as a volume, at the location `/etc/traefik/traefik.yaml`.

The configuration file has to contain several sections:

- The [providers](https://doc.traefik.io/traefik/providers/docker/#configuration-examples) section to configure Traefik to read the configuration from Docker.
- The [entrypoints](https://doc.traefik.io/traefik/routing/entrypoints/#configuration-examples) section to configure two endpoints:  `http` and `https`.
- The [tls](https://doc.traefik.io/traefik/https/tls/#user-defined) section to configure the TLS certificates. Specify the location of the certificates as the location where you mounted the directory into the container (such as `/etc/traefik/certificates`).
- In order to make the dashboard accessible, you have to configure the [api](https://doc.traefik.io/traefik/operations/dashboard/#insecure-mode) section. You can remove the respective labels from the docker compose file.

### Activating the HTTPS entrypoint for the servers

Finally, you have to activate HTTPS for the static and dynamic servers. This is done in the docker compose file. You have to add two labels to each server:

- to activate the HTTPS entrypoint,
- to set TLS to true.

See the [Traefik documentation for Docker](https://doc.traefik.io/traefik/routing/providers/docker/#routers) for these two labels.

### Testing

After these configurations it should be possible to access the static and the dynamic servers through HTTPS. The browser will complain that the sites are not secure, since the certificate is self-signed. But you can ignore this warning.

If it does not work, go to the Traefik dashboard and check the configuration of the routers and the entrypoints.

### How we completed this step

First, we needed to create a certificate and a key. We used openssl to do this. 

Here's the command that we used:
`openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365`

Then we put both files in the certificates folder [here](certificates/).

Here's the traefik.yaml file that we created:
```yaml
providers:
  docker:
    endpoint: "unix:///var/run/docker.sock"
    exposedByDefault: true

entryPoints:
  http:
    address: ":80"
    http:
      redirections:
        entryPoint:
          to: "https"
          scheme: "https"
  https:
    address: ":443"

api:
  dashboard: true
  insecure: true

tls:
  certificates:
    - certFile: /etc/traefik/certificates/cert.pem
      keyFile: /etc/traefik/certificates/key.pem
```

We added docker as a provider and then added two entrypoints, one for HTTP and the other for HTTPS.

The api section allows us to use the dashboard even though we configured TLS. 


After writing our traefik.yaml file we had to change our docker-compose.yml. Here it is:
```yml
services:
  static:
    image: dai/staticwebserver
    build: 
      context: ./staticwebserver
    labels:
      - traefik.http.routers.static.rule=Host(`localhost`)
      - traefik.http.services.static.loadbalancer.server.port=9080
      - traefik.http.routers.static.entrypoints=https
      - traefik.http.routers.static.tls=true
    deploy:
      replicas: 3
  api:
    image: dai/api
    build:
      context: ./api
    labels:
      - traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api/`)
      - traefik.http.services.api.loadbalancer.server.port=7070
      - traefik.http.services.api.loadBalancer.sticky.cookie=true
      - traefik.http.services.api.loadbalancer.sticky.cookie.secure=true
      - traefik.http.services.api.loadBalancer.sticky.cookie.name=cookie_api
      - traefik.http.routers.api.entrypoints=https
      - traefik.http.routers.api.tls=true
    deploy:
      replicas: 3
  reverse_proxy:
    image: traefik:v2.10
    command: --providers.docker
    ports:
      - "80:80"
      - "443:443"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./certificates:/etc/traefik/certificates
      - ./traefik.yaml:/etc/traefik/traefik.yaml
```

We had to add the entrypoints to both services and mount some files to configure traefik.

### Acceptance criteria

- [x] You can do a demo where you show that the static and dynamic servers are accessible through HTTPS.
- [x] You have **documented** your configuration in your report.



Optional steps
==============

If you sucessfully complete all the steps above, you can reach a grade of 5.0. If you want to reach a higher grade, you can do one or more of the following optional steps. 

Optional step 1: Management UI
------------------------------

The goal of this step is to deploy or develop a Web app that can be used to monitor and update your Web infrastructure dynamically. You should be able to list running containers, start/stop them and add/remove instances.

- you use an existing solution (search on Google)
- for extra points, develop your own Web app. In this case, you can use the Dockerode npm module (or another Docker client library, in any of the supported languages) to access the docker API.

### How we completed this step

We added a portainer service to our docker compose. This allows us to monitor our http infrastructure by using the Portainer Web app.

Here's the service we added to our docker-compose.yml file : 

```yml
  portainer:
    image: portainer/portainer-ce:latest
    ports:
      - "9443:9443"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    restart: unless-stopped
```

All you need to do is go to `localhost:9443` and log in. Click on the local environment, and then Stacks and choose the "dai-lab-http-infrastructure" stack. You can manage all of the containers here

### Acceptance criteria

- [x] You can do a demo to show the Management UI and manage the containers of your infrastructure.
- [x] You have **documented** how to use your solution.
- [x] You have **documented** your configuration in your report.


Optional step 2: Integration API - static Web site
--------------------------------------------------

This is a step into unknown territory. But you will figure it out.

The goal of this step is to change your static Web page to periodically make calls to your API server and show the results in the Web page. You will need JavaScript for this and this functionality is called AJAX.

Keep it simple! You can start by just making a GET request to the API server and display the result on the page. If you want, you can then you can add more features, but this is not obligatory.


The modern way to make such requests is to use the [JavaScript Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch). But you can also use JQuery if you prefer.

### How we completed this step

We slightly changed our website to make our driver list appear. We added some fetch code in our index.html file. Refresh the page to update the list.

Here's what we added:
```html
<h2 class="text-white mb-4">This list is provided by our API</h2>
<ul style="background-color: LightGrey;"></ul>
<script>
    fetch("/api/drivers", {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    }).then(res =>{
        return res.json();
    }).then(data => {
        const result = [];
        Object.keys(data).forEach(key => {
            result.push(data[key])
        })
        result.forEach(driver => {
            const markup = `<li>${driver.firstName} ${driver.lastName}</li>`;
            document.querySelector('ul').insertAdjacentHTML('beforeend', markup);
        });
    }).catch(error => console.log(error));
</script>
```
### Acceptance criteria

- [x] You have added JavaScript code to your static Web page to make at least a GET request to the API server.
- [x] You can do a demo where you show that the API is called and the result is displayed on the page.
- [x] You have **documented** your implementation in your report.

