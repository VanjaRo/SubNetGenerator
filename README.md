# Subnetwork Generator Test Project
### Prerequisites
You need to have `/data` directory with `in.txt` file in it.  
in.txt file should contain two rows: 
```text
10
1.2.3.4
```
- First row contains number of attempts to generate a network mask.
- Second row contains the IPv4 address you are using to find subnet.
### How to run?
#### Using Docker
Put this commands to your terminal:
```shell
 docker run -v `pwd`/data:/data ivanr43/subnetgenerator:latest
```
- The `/data` folder you are connecting to should satisfy prerequisites listed above.