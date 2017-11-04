description authoritative dns server

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 exit
server dns dns
 zone test.corp defttl 43200
 rr ip4a.test.corp ip4a 1.1.1.1
 rr ip6a.test.corp ip6a 1234::1
 rr ip4i.test.corp ip4i eth1
 rr ip6i.test.corp ip6i eth1
 vrf v1
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
proxy-profile p1
 vrf v1
 source ethernet1
 exit
client proxy p1
client name-server 1.1.1.1
!


r2 tping 100 3 1.1.1.1 /vrf v1
r2 tping 100 3 ip4a.test.corp /vrf v1
r2 tping 100 3 ip6a.test.corp /vrf v1
r2 tping 100 3 ip4i.test.corp /vrf v1
r2 tping 100 3 ip6i.test.corp /vrf v1
