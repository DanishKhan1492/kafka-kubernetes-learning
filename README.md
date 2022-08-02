# kafka-kubernetes-learning
This project has 2 small services. 
	
	1. Producer Service
	2. Consumer Service

## Producer Service 
This service has endpoints which accepts data either to be saved or updated. 
The data is forwarded to producer which publishes it to Kafka topic.

## Consumer Service
This service listen to the topic where the data is pushed by producer. Once data is received, then it is forwarded to persistence server to either save or update in **MySQL** DB.



## Kubernetes

We have the following objects for the producer and consumer projects.

* namespace object
* configmap objects
* deployment objects
* service objects (Where Network configuration are given)
* Ingress (For Routing and Load Balancing)
* Secrets (For MySQL Configurations whether for projects pods or for mysql own pod.)
* PersistentVolumes and PersistentVolumeClaims
* Kafka Configuration (values.yaml file which is referenced while installing kafka)

**Kafka** is deployed using helm. The configurations are read from values.yaml file.

For Kafka UI **KafDrop** is used. The Kafdrop is also deployed using kube.
