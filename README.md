# coco-heart-center
the heart center for soa-server-cluster
## 1 
   soa-server-cluster's node start,connect the zookeeper cluster,and fetch the heart-center-cluster's host list
   
## 2 
   soa-server-cluster's node schedule send ping info to one host of heart-center-cluster
   
## 3 
   heart-center-cluster receive the ping info,then write info into redis-cluster,and set the key with expire time,and subscribe the key's expired event
   
## 4 
   if one of soa-server-cluster's node has oom or crashed or other fatal error ,cause the node can't send ping info to the heart-center-cluster, then the key of redis will be expired, publish the envent;
   
## 5 
   heart-center-cluster receive the expired event , all the nodes put the info into local memory's blacklist ,and retry to fetch the redis's distribute lock. the node which success
fetch the lock,it will update the soa-node into unavailable, the soa-server-cluster's consumers will not send request to the soa-server-node.

## 6 
   when the soa-server-node recover，it will schedule send ping info,the heart-center-cluster receive the info,it will remove the soa-node from blacklist, update the soa-node's state into avaiable on the zookeeper, send the info to brothers node ,remove the node from blackList

 ![image](https://github.com/netboynb/coco-heart-center/blob/master/heart-center-framework.jpeg)
