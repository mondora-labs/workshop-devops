# Kubernetes 101

- [Kubernetes 101](#kubernetes-101)
  - [Prerequisite](#prerequisite)
  - [Start minikube](#start-minikube)
  - [Accesso kubectl](#accesso-kubectl)
  - [Accesso UI](#accesso-ui)
  - [Deploy Hello World](#deploy-hello-world)
  - [Scrivere YAML deployment](#scrivere-yaml-deployment)

## Prerequisite

- a running MiniKube

Kubernetes community provides [user manual for the installation](https://kubernetes.io/docs/setup/minikube/).
Remember that Minikube is not production ready, you have to use only for lab
env.

## Start minikube

Open a terminal and start minikube:

```bash
minikube start
```

As long as you see the message ‘Kubectl is now configured to use the cluster.’
you have successfully ran Minikube:

```bash
😄  minikube v1.0.0 on darwin (amd64)
🤹  Downloading Kubernetes v1.14.0 images in the background ...
🔥  Creating virtualbox VM (CPUs=2, Memory=2048MB, Disk=20000MB) ...
📶  "minikube" IP address is 192.168.99.101
🐳  Configuring Docker as the container runtime ...
🐳  Version of container runtime is 18.06.2-ce
⌛  Waiting for image downloads to complete ...
✨  Preparing Kubernetes environment ...
🚜  Pulling images required by Kubernetes v1.14.0 ...
🚀  Launching Kubernetes v1.14.0 using kubeadm ... 
⌛  Waiting for pods: apiserver proxy etcd scheduler controller dns
🔑  Configuring cluster permissions ...
🤔  Verifying component health .....
💗  kubectl is now configured to use "minikube"
🏄  Done! Thank you for using minikube!
```

Check the status of Minikube:

```bash
minikube status
```

Expected output:

```bash
host: Running
kubelet: Running
apiserver: Running
kubectl: Correctly Configured: pointing to minikube-vm at 172.31.36.43
```

## Accesso kubectl

Kubectl is a command line interface for running commands against Kubernetes
clusters. Authentication is made by certificate, configured under user's home
directory: /home/<user>/.kube

Open the config file:

```bash
/home/<user>/.kube/config  
```

The output show the connect information to accees our k8s "cluster"

```bash
apiVersion: v1
clusters:
- cluster:
    certificate-authority: /home/ubuntu/.minikube/ca.crt
    server: https://172.31.36.43:8443
  name: minikube
contexts:
- context:
    cluster: minikube
    user: minikube
  name: minikube
current-context: minikube
kind: Config
preferences: {}
users:
- name: minikube
  user:
    client-certificate: /home/ubuntu/.minikube/client.crt
    client-key: /home/ubuntu/.minikube/client.key
```

Let's use kubectl to query the cluster node status:

```bash
kubectl get nodes
```

This command show us the nodes composing our k8s cluster and their status:

```bash
NAME       STATUS   ROLES    AGE   VERSION
minikube   Ready    master   21m   v1.14.0
```

## Accesso UI

If you want to navigate the cluster throught the web UI, launch the command:

```bash
minikube dashboard --url
```

The output show the UI URL

```bash
🔌  Enabling dashboard ...
🤔  Verifying dashboard health ...
🚀  Launching proxy ...
🤔  Verifying proxy health ...
http://127.0.0.1:51433/api/v1/namespaces/kube-system/services/http:kubernetes-dashboard:/proxy/
```

Copy&Paste the UI in a web browser

## Deploy Hello World

Let us run our first container:

```bash
kubectl run hello-minikube --image=gcr.io/google_containers/echoserver:1.4 --port=8080
```

To list all applications and services on the cluster, run

```bash
kubectl get all
```

If deployment works, the output show us:

```bash
NAME                                  READY   STATUS    RESTARTS   AGE
pod/hello-minikube-597c997dd4-4d2t8   1/1     Running   0          114s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   26m

NAME                             READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/hello-minikube   1/1     1            1           114s

NAME                                        DESIRED   CURRENT   READY   AGE
replicaset.apps/hello-minikube-597c997dd4   1         1         1       114s
```

Our hello-world app is running, now we have to expose the container ports so
that we can access it from the web:

```bash
kubectl expose deployment hello-minikube --type=NodePort
```

Find where port 8080 in container exposed in your laptop port:

```bash
kubectl get services
```

kubectl get services command shows the list of services and their exposed ports.
The service post changes each time you expose a port, you may have been given a
different value than what I have.

```bash
NAME             TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
hello-minikube   NodePort    10.98.213.172   <none>        8080:32488/TCP   49s
kubernetes       ClusterIP   10.96.0.1       <none>        443/TCP          35m
```

Accessing the Application Using the Exposed NodePort, retrieve the minikube
cluster, virtual IP: Port 32488 is the TCP port where the Port 8080 of the
container is exposed.

```bash
minikube ip
```

Use the combination of minikube IP + service port to access the Hello World app:

```bash
curl -v 192.168.99.101:32488
```

Last, delete the hello-minikube app's deployment using:

```bash
kubectl delete deployment.apps/hello-minikube
```

Last delete the exposed service, list all app and services again:

```bash
kubectl get all
```

And delete it.

## Scrivere YAML deployment

Now it's time to deploy the SpringBoot microservice created in Lab #04 Open a
text editor and create mongodb.yml file.

We're using this file to deploy a **non** production ready mongodb inside
minikube. Add the declaration object to create a persistent storage:

```bash
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mongodb-storage-claim
  labels:
    tier: database
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

This block contain the max capacity of the persistent volume and a declarative
name, used to identify the object. Create a Deployment object in order to define
our mongo service:

```bash
---
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: mongodb-devops-workshop
  labels:
    tier: database
spec:
  revisionHistoryLimit: 2
  replicas: 1
  template:
    metadata:
      labels:
        tier: database
      annotations:
        kubernetes.io/change-cause: first-deploy
    spec:
      containers:
      - name: mongodb-devops-workshop
        image: mongo
        volumeMounts:
        - name: mongodb-persistent-storage
          mountPath: /data/db        
      volumes:
      - name: mongodb-persistent-storage
        persistentVolumeClaim:
          claimName: mongodb-storage-claim
```

We using the Docker Hub official mongo images and we mount the persistent storage
under /data/db [as documented](https://hub.docker.com/_/mongo). Last, add the
Service. A service tells the rest of the Kubernetes environment (including other
pods) what services your application provides. And other applications can find
your service through **Kurbenetes service discovery**.

```bash
---
apiVersion: v1
kind: Service
metadata:
  name: mongodb-devops-workshop
  labels:
    tier: database
spec:
  ports:
  - port: 27017
  selector:
    tier: database
---
```

Deploy the service using kubectl:

```bash
kubectl apply -f mongodb.yml
```

Monitor the status using kubectl & web UI.
If mongodb is running, deploy the SpringBoot microservices. Add microservice.yml:

```bash
---
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: springboot-devops-workshop
  labels:
    tier: webserver
spec:
  revisionHistoryLimit: 2
  replicas: 1
  template:
    metadata:
      labels:
        tier: webserver
      annotations:
        kubernetes.io/change-cause: first_deploy
    spec:
      containers:
      - name: springboot-devops-workshop
        image: mondoralabs/microservice-example-test
        imagePullPolicy: Always
        readinessProbe:
          httpGet:
            path: /
            scheme: HTTP
            port: 8080
        livenessProbe:
          httpGet:
            path: /
            scheme: HTTP
            port: 8080
          initialDelaySeconds: 25
          timeoutSeconds: 5
        env:
        - name: MONGO_URI
          value: mongodb://mongodb-devops-workshop:27017/test
---
apiVersion: v1
kind: Service
metadata:
  name: springboot-devops-workshop
  labels:
    tier: webserver
spec:
  type: NodePort
  ports:
  - name: http
    port: 8080
  selector:
    tier: webserver
---
```

The MONGO_URI environment variable contain the connection string to the mongo,
previously deployed, throught the K8s service discovery. Note: MONGO_URI
hostname will be valorized using the name of Service used in mongodb.yml.

And use kubectl to apply the configuration. And

```bash
kubectl get services
```

To retrieve the TCP port.

Test the microservice health:

```bash
curl 192.168.99.101:30357/actuator/health
```

Output show us the status:

```bash
{"status":"UP","details":{"diskSpace":{"status":"UP","details":{"total":18211606528,"free":14381268992,"threshold":10485760}},"mongo":{"status":"UP","details":{"version":"4.0.9"}}}}
```
