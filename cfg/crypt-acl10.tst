description egress ttl matching access list

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
access-list test4
 deny all any all any all ttl 110-120
 permit all any all any all
 exit
access-list test6
 deny all any all any all ttl 110-120
 permit all any all any all
 exit
vrf def v1
 rd 1:1
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234::1 ffff:ffff::
 ipv4 access-group-out test4
 ipv6 access-group-out test6
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
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234::2 ffff:ffff::
 exit
!

r2 tping 100 3 1.1.1.1 /vrf v1 /ttl 90
r2 tping 100 3 1234::1 /vrf v1 /ttl 90
r1 tping 100 3 1.1.1.2 /vrf v1 /ttl 90
r1 tping 100 3 1234::2 /vrf v1 /ttl 90

r2 tping 100 3 1.1.1.1 /vrf v1 /ttl 115
r2 tping 100 3 1234::1 /vrf v1 /ttl 115
r1 tping 0 3 1.1.1.2 /vrf v1 /ttl 115
r1 tping 0 3 1234::2 /vrf v1 /ttl 115

r2 tping 100 3 1.1.1.1 /vrf v1 /ttl 130
r2 tping 100 3 1234::1 /vrf v1 /ttl 130
r1 tping 100 3 1.1.1.2 /vrf v1 /ttl 130
r1 tping 100 3 1234::2 /vrf v1 /ttl 130
