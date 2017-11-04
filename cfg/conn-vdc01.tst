description vdc parent interface

exit

addrouter r1
port 61000 62000
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vdc def a
 int eth1
 exit
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 9.9.9.9 255.255.255.255
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
!
vrf def v1
 rd 1:1
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234::2 ffff::
 exit
!

r1 tping 100 3 9.9.9.9 /vrf v1

r1 send att vdc a
r1 send conf t
r1 send vrf def v1
r1 send  rd 1:1
r1 send  exit
r1 send int eth1
r1 send  vrf for v1
r1 send  ipv4 addr 1.1.1.1 255.255.255.0
r1 send  ipv6 addr 1234::1 ffff::
r1 send  exit
r1 send end

r1 tping 100 3 1.1.1.2 /vrf v1
r2 tping 100 3 1.1.1.1 /vrf v1
r1 tping 100 3 1234::2 /vrf v1
r2 tping 100 3 1234::1 /vrf v1
